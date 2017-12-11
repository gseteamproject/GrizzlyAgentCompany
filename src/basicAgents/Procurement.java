package basicAgents;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicClasses.*;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREInitiator;
import jade.proto.AchieveREResponder;

public class Procurement extends Agent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2923962894395399488L;
	public boolean isInMaterialStorage;

	// queue for procurement orders
	public static List<Order> procurementQueue = new ArrayList<Order>();

	// creating storage for raw materials
	public static MaterialStorage materialStorage = new MaterialStorage();

	@Override
	protected void setup() {
		MessageTemplate reqTemp = AchieveREResponder.createMessageTemplate(FIPANames.InteractionProtocol.FIPA_REQUEST);

		// adding behaviours
		addBehaviour(new WaitingForMaterialOrder(this, reqTemp));
	}

	// this class waits for receiving a message with certain template
	class WaitingForMaterialOrder extends AchieveREResponder {

		/**
		 * 
		 */
		private static final long serialVersionUID = -5804509731381843266L;
		private String orderText;

		public WaitingForMaterialOrder(Agent a, MessageTemplate mt) {
			super(a, mt);
		}

		@Override
		protected ACLMessage prepareResponse(ACLMessage request) throws NotUnderstoodException, RefuseException {
			// Selling reacts on SalesMarket's request

			orderText = Order.gson.fromJson(request.getContent(), Order.class).getTextOfOrder();

			// Agent should send agree or refuse
			// TODO: Add refuse answer (some conditions should be added)
			ACLMessage agree = request.createReply();
			agree.setContent(request.getContent());
			agree.setPerformative(ACLMessage.AGREE);

			if (request.getConversationId() == "Materials") {
				System.out.println("ProcurementAgent: [request] ProductionAgent asks for materials for " + orderText);
				System.out.println(
						"ProcurementAgent: [agree] I will check materialStorage for materials for " + orderText);
				addBehaviour(new CheckMaterialStorage(myAgent, agree));
			} else if (request.getConversationId() == "Take") {
				System.out.println("ProcurementAgent: [request] ProductionAgent wants to get materials for " + orderText
						+ " from materialStorage");
				System.out.println("ProcurementAgent: [agree] I will give you materials for " + orderText
						+ " from materialStorage");
				addBehaviour(new GiveMaterialToProduction(myAgent, agree));
			}

			return agree;
		}

		@Override
		protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response)
				throws FailureException {
			// if agent agrees to request
			// after executing, it should send failure of inform

			// in case of inform product will be taken from warehouse
			// in case of failure product will be produced
			ACLMessage reply = request.createReply();
			reply.setContent(request.getContent());

			if (isInMaterialStorage) {
				reply.setPerformative(ACLMessage.INFORM);
			} else {
				reply.setPerformative(ACLMessage.FAILURE);
			}
			return reply;
		}
	}

	class CheckMaterialStorage extends OneShotBehaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = -4869963544017982955L;
		private String requestedMaterial;

		private ACLMessage agree;

		public CheckMaterialStorage(Agent a, ACLMessage msg) {
			super(a);
			requestedMaterial = msg.getContent();
			agree = msg;
		}

		@Override
		public void action() {
			Order order = Order.gson.fromJson(requestedMaterial, Order.class);

			isInMaterialStorage = true;
			boolean isInQueue = false;

			// check if this order is not in queue yet
			isInQueue = procurementQueue.contains(order);

			// part of order, that needs to be produced
			Order orderToBuy = new Order();
			orderToBuy.id = order.id;
			


			for (OrderPart orderPart : order.orderList) {
				Product productToCheck = orderPart.getProduct();

				String color = productToCheck.getColor();
				Double size = productToCheck.getSize();

				int amount = orderPart.getAmount();

				System.out.println("ProcurementAgent: Asking materialStorage about " + orderPart.getTextOfOrderPart());

				
				int paintAmountInMS = materialStorage.getAmountOfPaint(color);
				int stoneAmountInMS = materialStorage.getAmountOfStones(size);

				if (paintAmountInMS >= amount && stoneAmountInMS >= amount) {
					isInMaterialStorage = true;
					System.out.println("ProcurementAgent: I say that materials for " + orderPart.getTextOfOrderPart()
							+ " are in materialStorage");
				} else {
					// need to describe multiple statements to check every material
					isInMaterialStorage = false;

					// creating new instance of OrderPart to change its amount
					OrderPart paintOrderPart = new OrderPart(orderPart.getProduct().getPaint());
					OrderPart stoneOrderPart = new OrderPart(orderPart.getProduct().getStone());

					paintOrderPart.setAmount(amount - paintAmountInMS);
					stoneOrderPart.setAmount(amount - stoneAmountInMS);
					
					System.out.println("paintOrderPart.getAmount() " + paintOrderPart.getAmount());


					if (paintOrderPart.getAmount() > 0) {
						orderToBuy.orderList.add(paintOrderPart);
					}
					if (stoneOrderPart.getAmount() > 0) {
						orderToBuy.orderList.add(stoneOrderPart);
					}
				}
			}

			if (!isInQueue && orderToBuy.orderList.size() > 0) {
				String testGson = Order.gson.toJson(orderToBuy);
				agree.setContent(testGson);

				// add order to queue
				procurementQueue.add(order);

				System.out.println("ProcurementAgent: send info to ProcurementMarket to buy materials for "
						+ orderToBuy.getTextOfOrder());
				addBehaviour(new AskForAuction(myAgent, 2000, agree));
			}
		}
	}

	// TODO: Use OneShot (?)
	class AskForAuction extends TickerBehaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = -6100676860519799721L;
		private String materialToBuy;
		private String orderText;

		public AskForAuction(Agent a, long period, ACLMessage msg) {
			super(a, period);
			materialToBuy = msg.getContent();
		}

		@Override
		protected void onTick() {
			orderText = Order.gson.fromJson(materialToBuy, Order.class).getTextOfOrder();
			System.out.println(
					"ProcurementAgent: Sending an info to ProcurementMarket to buy materials for " + orderText);

			String requestedAction = "Order";
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			msg.setConversationId(requestedAction);
			msg.addReceiver(new AID(("AgentProcurementMarket"), AID.ISLOCALNAME));
			msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
			msg.setReplyByDate(new Date(System.currentTimeMillis() + 10000));
			msg.setContent(materialToBuy);

			addBehaviour(new RequestToBuy(myAgent, msg));
		}

		@Override
		public void stop() {
			orderText = Order.gson.fromJson(materialToBuy, Order.class).getTextOfOrder();
			System.out.println("ProcurementAgent: materials for " + orderText + " are in auction");
			super.stop();
		}

		class RequestToBuy extends AchieveREInitiator {

			/**
			 * 
			 */
			private static final long serialVersionUID = -1322936877118129497L;

			public RequestToBuy(Agent a, ACLMessage msg) {
				super(a, msg);
			}

			@Override
			protected void handleInform(ACLMessage inform) {

				orderText = Order.gson.fromJson(inform.getContent(), Order.class).getTextOfOrder();
				stop();
			}
		}
	}

	class GiveMaterialToProduction extends OneShotBehaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = -1386982676634257780L;
		private String materialsToGive;
		private String orderText;

		public GiveMaterialToProduction(Agent a, ACLMessage msg) {
			super(a);
			materialsToGive = msg.getContent();
		}

		@Override
		public void action() {
			Order order = Order.gson.fromJson(materialsToGive, Order.class);
			orderText = order.getTextOfOrder();
			System.out.println("ProcurementAgent: Taking " + orderText + " from materialStorage");

			for (Product product : order.getProducts()) {
				materialStorage.remove(new Paint(product.getColor()));
				materialStorage.remove(new Stone(product.getSize()));
			}
		}
	}
}
