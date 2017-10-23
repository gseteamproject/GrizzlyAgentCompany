package basicAgents;

import java.util.Date;

import basicClasses.Material;
import basicClasses.Order;
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

public class Production extends Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9064413910591040008L;
	public ACLMessage starterMessage;

	@Override
	protected void setup() {
		// TODO: Need services for employees/robots

		MessageTemplate reqTemp = MessageTemplate.and(
				MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
				MessageTemplate.MatchPerformative(ACLMessage.REQUEST));

		// adding behaviours
		addBehaviour(new WaitingTaskMessage(this, reqTemp));
	}

	// this class waits for receiving a message with certain template
	class WaitingTaskMessage extends AchieveREResponder {

		/**
		 * 
		 */
		private static final long serialVersionUID = -4173474368073887844L;
		private String orderText;

		public WaitingTaskMessage(Agent a, MessageTemplate mt) {
			super(a, mt);
		}

		@Override
		protected ACLMessage prepareResponse(ACLMessage request) throws NotUnderstoodException, RefuseException {
			// ProductionAgent reacts on SellingAgent's request

			orderText = Order.readOrder(request.getContent()).getTextOfOrder();

			System.out.println("ProductionAgent: [request] SellingAgent asks to produce " + orderText);
			// Agent should send agree or refuse
			// TODO: Add refuse answer (some conditions should be added)
			starterMessage = request;
			ACLMessage agree = request.createReply();
			agree.setContent(request.getContent());
			agree.setPerformative(ACLMessage.AGREE);
			System.out.println("ProductionAgent: [agree] I will produce " + orderText);

			// if agent agrees it starts executing request
			addBehaviour(new AskForMaterial(myAgent, 2000, agree));

			return agree;
		}

		@Override
		protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response)
				throws FailureException {

			orderText = Order.readOrder(request.getContent()).getTextOfOrder();

			// result of request to ProductionAgent
			// if agent agrees to request
			// after executing, it should send failure of inform
			ACLMessage inform = request.createReply();
			inform.setContent(request.getContent());
			inform.setPerformative(ACLMessage.INFORM);
			System.out.println("ProductionAgent: [inform] I initiated producing " + orderText);

			return inform;
		}
	}

	class AskForMaterial extends TickerBehaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = 8495802171064457305L;
		private String materialsToRequest;
		private String orderText;

		public AskForMaterial(Agent a, long period, ACLMessage msg) {
			super(a, period);
			materialsToRequest = msg.getContent();
		}

		@Override
		protected void onTick() {
			orderText = Order.readOrder(materialsToRequest).getTextOfOrder();

			// TODO: ask for all materials at once

			System.out.println("ProductionAgent: Asking ProcurementAgent to get " + orderText);

			String requestedAction = "Materials";
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			msg.setConversationId(requestedAction);
			msg.addReceiver(new AID(("AgentProcurement"), AID.ISLOCALNAME));
			msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
			msg.setReplyByDate(new Date(System.currentTimeMillis() + 10000));
			msg.setContent(materialsToRequest);

			addBehaviour(new RequestToGet(myAgent, msg));
		}

		@Override
		public void stop() {

			orderText = Order.readOrder(materialsToRequest).getTextOfOrder();

			System.out.println("ProductionAgent: Now I know that materials for " + orderText + " are in storage");
			super.stop();
		}

		class RequestToGet extends AchieveREInitiator {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1618638159227094879L;

			public RequestToGet(Agent a, ACLMessage msg) {
				super(a, msg);
			}

			@Override
			protected void handleInform(ACLMessage inform) {

				orderText = Order.readOrder(inform.getContent()).getTextOfOrder();

				System.out.println("ProductionAgent: received [inform] materials for " + orderText + " are in storage");
				stop();

				addBehaviour(new GetFromStorage(myAgent, 2000, inform));
			}

			@Override
			protected void handleFailure(ACLMessage failure) {

				orderText = Order.readOrder(failure.getContent()).getTextOfOrder();

				System.out.println(
						"ProductionAgent: received [failure] materials for " + orderText + " are not in storage");
				// TODO: may cause infinite loop
				// stop();
			}
		}
	}

	class GetFromStorage extends TickerBehaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = 6717167573013445327L;
		private String materialsToTake;
		private String orderText;

		public GetFromStorage(Agent a, long period, ACLMessage msg) {
			super(a, period);
			materialsToTake = msg.getContent();
		}

		@Override
		protected void onTick() {

			orderText = Order.readOrder(materialsToTake).getTextOfOrder();

			System.out.println("ProductionAgent: Asking SellingAgent to take " + orderText + " from warehouse");

			String requestedAction = "Take";
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			msg.setConversationId(requestedAction);
			msg.addReceiver(new AID(("AgentProcurement"), AID.ISLOCALNAME));
			msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
			msg.setReplyByDate(new Date(System.currentTimeMillis() + 10000));
			msg.setContent(materialsToTake);

			addBehaviour(new RequestToTake(myAgent, msg));
		}

		@Override
		public void stop() {

			orderText = Order.readOrder(materialsToTake).getTextOfOrder();

			System.out.println("ProductionAgent: Now I have materials for " + orderText);
			super.stop();
		}

		class RequestToTake extends AchieveREInitiator {

			/**
			 * 
			 */
			private static final long serialVersionUID = 7996018163076712881L;

			public RequestToTake(Agent a, ACLMessage msg) {
				super(a, msg);
			}

			@Override
			protected void handleInform(ACLMessage inform) {

				orderText = Order.readOrder(inform.getContent()).getTextOfOrder();

				System.out.println("ProductionAgent: received [inform] materials for " + orderText
						+ " will be taken from storage");
				stop();

				addBehaviour(new DeliverToSelling(myAgent, inform));
			}

			@Override
			protected void handleFailure(ACLMessage failure) {

				orderText = Order.readOrder(failure.getContent()).getTextOfOrder();

				System.out.println("ProductionAgent: received [failure] materials for " + orderText
						+ " will not be taken from storage");
				// TODO: may cause infinite loop
				// stop();
			}
		}
	}

	class DeliverToSelling extends OneShotBehaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = 313682933400751868L;
		private String orderToGive;
		private String orderText;

		public DeliverToSelling(Agent a, ACLMessage msg) {
			super(a);
			orderToGive = msg.getContent();
		}

		@Override
		public void action() {
			Order order = Order.readOrder(SalesMarket.orderQueue.size(), orderToGive);
			orderText = order.getTextOfOrder();
			System.out.println("ProductionAgent: Delivering " + orderText + " to warehouse");

			// TODO: Refactoring is needed

			Material productToGive = (Material) order.getMaterials().get(0);
			Selling.warehouse.add(productToGive);
		}
	}
}
