package basicAgents;

import java.util.Date;

import basicClasses.Order;
import jade.core.AID;
import jade.core.Agent;
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

		// TODO: Should we use service here?
		// TODO: Or service should be used for employees/robots?

		// // description
		// ServiceDescription serviceDescription = new ServiceDescription();
		// serviceDescription.setName("Stone");
		// // agent
		// DFAgentDescription agentDescription = new DFAgentDescription();
		// agentDescription.setName(getAID());
		// agentDescription.addServices(serviceDescription);
		// try {
		// // register DF
		// DFService.register(this, agentDescription);
		// } catch (FIPAException exception) {
		// exception.printStackTrace();
		// }

		// adding behaviours

		// MessageTemplate reqTemp =
		// AchieveREResponder.createMessageTemplate(FIPANames.InteractionProtocol.FIPA_REQUEST);
		MessageTemplate reqTemp = MessageTemplate.and(
				MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
				MessageTemplate.MatchPerformative(ACLMessage.REQUEST));

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

			System.out.println("[request] SellingAgent asks to produce " + orderText);
			// Agent should send agree or refuse
			// TODO: Add refuse answer (some conditions should be added)
			starterMessage = request;
			ACLMessage agree = request.createReply();
			agree.setContent(request.getContent());
			agree.setPerformative(ACLMessage.AGREE);
			System.out.println("[agree] I will produce " + orderText);

			// if agent agrees it starts executing request
			addBehaviour(new AskForMaterial(myAgent, 2000, agree));

			return agree;
		}

		@Override
		protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response)
				throws FailureException {

			orderText = Order.readOrder(request.getContent()).getTextOfOrder();

			// some testing
			System.out.println("\nProductionAgent: response.getContent()" + response.getContent());
			System.out.println("ProductionAgent: response.getSender()" + request.getSender());
			System.out.println("ProductionAgent: response.getPerformative()" + response.getPerformative() + "\n");

			// result of request to ProductionAgent
			// if agent agrees to request
			// after executing, it should send failure of inform
			ACLMessage inform = request.createReply();
			inform.setContent(request.getContent());
			inform.setPerformative(ACLMessage.INFORM);
			System.out.println("[inform] I initiated producing " + orderText);

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
			msg.addReceiver(new AID(("procurementAgent"), AID.ISLOCALNAME));
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

				// TODO: Is it necessary to send something?
				// ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				// msg.addReceiver(starterMessage.getSender());
				// msg.setContent(starterMessage.getContent());
				// send(msg);

				addBehaviour(new GetFromStorage(myAgent, 2000, inform));
			}

			@Override
			protected void handleFailure(ACLMessage failure) {

				orderText = Order.readOrder(failure.getContent()).getTextOfOrder();

				System.out.println(
						"ProductionAgent: received [failure] materials for " + orderText + " are not in storage");
				stop();
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
			msg.addReceiver(new AID(("procurementAgent"), AID.ISLOCALNAME));
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
						+ " will not be taken from storage");
				stop();
			}

			@Override
			protected void handleFailure(ACLMessage failure) {

				orderText = Order.readOrder(failure.getContent()).getTextOfOrder();

				System.out.println("ProductionAgent: received [failure] materials for " + orderText
						+ " will not be taken from storage");
				stop();
			}
		}
	}
}
