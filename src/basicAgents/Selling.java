package basicAgents;

import java.util.Date;

import basicClasses.Product;
import basicClasses.Order;
import basicClasses.ProductStorage;
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

public class Selling extends Agent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7150875080288668056L;
	public ACLMessage starterMessage;
	public boolean isInWarehouse;

	// creating storage for products
	public static ProductStorage warehouse = new ProductStorage();

	@Override
	protected void setup() {
		MessageTemplate reqTemp = AchieveREResponder.createMessageTemplate(FIPANames.InteractionProtocol.FIPA_REQUEST);

		// adding behaviours
		addBehaviour(new WaitingForOrder(this, reqTemp));
	}

	// this class waits for receiving a message with certain template
	class WaitingForOrder extends AchieveREResponder {

		/**
		 * 
		 */
		private static final long serialVersionUID = 4671831774439180119L;
		private String orderText;

		public WaitingForOrder(Agent a, MessageTemplate mt) {
			super(a, mt);
		}

		@Override
		protected ACLMessage prepareResponse(ACLMessage request) throws NotUnderstoodException, RefuseException {
			// Selling reacts on SalesMarket's request

			orderText = Order.readOrder(request.getContent()).getTextOfOrder();

			// Agent should send agree or refuse
			// TODO: Add refuse answer (some conditions should be added)

			starterMessage = request;
			ACLMessage agree = request.createReply();
			agree.setContent(request.getContent());
			agree.setPerformative(ACLMessage.AGREE);

			if (request.getConversationId() == "Order") {
				System.out.println("SellingAgent: [request] SalesMarket orders a " + orderText);
				System.out.println("SellingAgent: [agree] I will check warehouse for " + orderText);
				addBehaviour(new CheckWarehouse(myAgent, agree));
			} else if (request.getConversationId() == "Take") {
				System.out.println("SellingAgent: [request] SalesMarket wants to get " + orderText + " from warehouse");
				System.out.println("SellingAgent: [agree] I will give you " + orderText + " from warehouse");
				addBehaviour(new GiveProductToMarket(myAgent, agree));
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
			if (isInWarehouse) {
				ACLMessage inform = request.createReply();
				inform.setContent(request.getContent());
				inform.setPerformative(ACLMessage.INFORM);
				return inform;
			} else {
				ACLMessage failure = request.createReply();
				failure.setContent(request.getContent());
				failure.setPerformative(ACLMessage.FAILURE);
				return failure;
			}
		}
	}

	class CheckWarehouse extends OneShotBehaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = 3856126876248315456L;
		private String requestedOrder;
		private String orderText;

		private ACLMessage agree;

		public CheckWarehouse(Agent a, ACLMessage msg) {
			super(a);
			requestedOrder = msg.getContent();
			agree = msg;
		}

		@Override
		public void action() {
			orderText = Order.readOrder(requestedOrder).getTextOfOrder();

			System.out.println("SellingAgent: Asking warehouse about " + orderText);

			int amountInWH = 0;

			// TODO: Refactoring is needed
			// TODO: Order may consist of several colors and sizes, so we need to send an
			// answer of each PRODUCT
			// TODO: we also have size parameter, but let's assume that we have only size 10
			// by now

			Order order = Order.readOrder(requestedOrder);

			// TODO: should be some iteration over list
			Product productToCheck = (Product) order.getProducts().get(0);

			//String color = productToCheck.getColor();

			int amount = order.getAmountByProduct(productToCheck);

			amountInWH = warehouse.getAmountOfProduct(productToCheck);

			if (amountInWH >= amount) {
				isInWarehouse = true;
				System.out.println("SellingAgent: I say that " + orderText + " is in warehouse");
			} else {
				isInWarehouse = false;
				System.out.println("SellingAgent: send info to Finances about product to produce " + orderText);
				// TODO: calculate needed PRODUCTS

				// check if this order is not in queue yet
				boolean isInQueue = false;
				for (Order orderInQueue : SalesMarket.orderQueue) {
					if (orderInQueue.getID() == order.getID()) {
						isInQueue = true;
					}
				}

				if (!isInQueue) {
					// add order to queue
					SalesMarket.orderQueue.add(order);
					addBehaviour(new SendInfo(myAgent, 2000, agree));
				}
			}
		}
	}

	class SendInfo extends TickerBehaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = -6365251601845699295L;
		private String orderToProceed;
		private String orderText;

		public SendInfo(Agent a, long period, ACLMessage msg) {
			super(a, period);
			orderToProceed = msg.getContent();
		}

		@Override
		protected void onTick() {
			orderText = Order.readOrder(orderToProceed).getTextOfOrder();
			System.out.println("SellingAgent: Sending an info to Finance Agent to produce " + orderText);

			String requestedAction = "Order";
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			msg.setConversationId(requestedAction);
			// there should be financesAgent, but we will ignore it by now
			msg.addReceiver(new AID(("AgentProduction"), AID.ISLOCALNAME));
			// msg.addReceiver(new AID(("AgentFinances"), AID.ISLOCALNAME));
			msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
			msg.setReplyByDate(new Date(System.currentTimeMillis() + 10000));
			msg.setContent(orderToProceed);

			addBehaviour(new RequestToFinance(myAgent, msg));
		}

		@Override
		public void stop() {
			orderText = Order.readOrder(orderToProceed).getTextOfOrder();
			System.out.println("SellingAgent: " + orderText + " is in production");
			super.stop();
		}

		class RequestToFinance extends AchieveREInitiator {

			/**
			 * 
			 */
			private static final long serialVersionUID = 994161564616428958L;

			public RequestToFinance(Agent a, ACLMessage msg) {
				super(a, msg);
			}

			@Override
			protected void handleInform(ACLMessage inform) {

				orderText = Order.readOrder(inform.getContent()).getTextOfOrder();
				System.out.println("SellingAgent: [inform] " + orderText);
				stop();
			}
		}
	}

	class GiveProductToMarket extends OneShotBehaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = -6498277261596869382L;
		private String orderToGive;
		private String orderText;

		public GiveProductToMarket(Agent a, ACLMessage msg) {
			super(a);
			orderToGive = msg.getContent();
		}

		@Override
		public void action() {
			Order order = Order.readOrder(SalesMarket.orderQueue.size(), orderToGive);
			orderText = order.getTextOfOrder();
			System.out.println("SellingAgent: Taking " + orderText + " from warehouse");

			// TODO: Refactoring is needed
			// TODO: Order may consist of several colors and sizes, so we need to send an
			// answer of each PRODUCT

			Product productToGive = (Product) order.getProducts().get(0);

			warehouse.remove(productToGive);
		}
	}
}
