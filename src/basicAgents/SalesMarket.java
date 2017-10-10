package basicAgents;

import java.util.Date;

import basicClasses.Material;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREInitiator;
import jade.proto.AchieveREResponder;

public class SalesMarket extends Agent {
	private static final long serialVersionUID = 4718901230605783759L;

	public ACLMessage starterMessage;

	@Override
	protected void setup() {

		// TODO: Should we use service here?

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

		addBehaviour(new WaitingCustomerMessage(this, reqTemp));

		addBehaviour(new SimpleAgentWakerBehaviour(this, 4000));
	}

	// class that sends test message with example of order. This simulates customer.
	class SimpleAgentWakerBehaviour extends WakerBehaviour {
		private static final long serialVersionUID = 2508808170658574583L;

		public SimpleAgentWakerBehaviour(Agent a, long timeout) {
			super(a, timeout);
		}

		@Override
		public void onWake() {
			// THIS MESSAGE IS FOR TESTING
			ACLMessage testMsg = new ACLMessage(ACLMessage.REQUEST);
			testMsg.addReceiver(new AID(("salesMarketAgent"), AID.ISLOCALNAME));
			testMsg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
			// there should be not just stone but on object with whole order
			testMsg.setContent("stone");
			send(testMsg);

			// adding stone to warehouse
			Material mat = new Material("blue", 10);
			Selling.warehouse.add(mat);
		}
	}

	// this class waits for receiving a message with certain template
	class WaitingCustomerMessage extends AchieveREResponder {
		private static final long serialVersionUID = 6130496380982287815L;

		public WaitingCustomerMessage(Agent a, MessageTemplate mt) {
			super(a, mt);
		}

		@Override
		protected ACLMessage prepareResponse(ACLMessage request) throws NotUnderstoodException, RefuseException {
			// Sales Market reacts on customer's request
			System.out.println("[request] Customer orders a " + request.getContent());
			// Agent should send agree or refuse
			// TODO: Add refuse answer (some conditions should be added)
			starterMessage = request;
			ACLMessage agree = request.createReply();
			agree.setContent(request.getContent());
			agree.setPerformative(ACLMessage.AGREE);
			System.out.println("[agree] I will make an order of " + agree.getContent());

			// if agent agrees it starts executing request
			addBehaviour(new SendAnOrder(myAgent, 2000, agree));

			return agree;
		}

		@Override
		protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response)
				throws FailureException {

			// result of request to sales market
			// if agent agrees to request
			// after executing, it should send failure of inform
			ACLMessage inform = request.createReply();
			inform.setContent("[inform] I ordered a " + response.getContent());
			inform.setPerformative(ACLMessage.INFORM);
			System.out.println("[inform] I ordered a " + response.getContent());

			return inform;
		}
	}

	class SendAnOrder extends TickerBehaviour {
		private static final long serialVersionUID = -1534610326024914625L;

		public String obj;

		public SendAnOrder(Agent a, long period, ACLMessage msg) {
			super(a, period);
			obj = msg.getContent();
		}

		@Override
		protected void onTick() {
			System.out.println("SalesMarketAgent: Sending an order to SellingAgent to get " + obj);

			String requestedAction = "Order";
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			msg.setConversationId(requestedAction);
			msg.addReceiver(new AID(("sellingAgent"), AID.ISLOCALNAME));
			msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
			msg.setReplyByDate(new Date(System.currentTimeMillis() + 10000));
			msg.setContent(obj);

			addBehaviour(new RequestToExecute(myAgent, msg));
		}

		@Override
		public void stop() {
			System.out.println("SalesMarketAgent: Now I know that " + obj + " is in warehouse");
			super.stop();
		}

		class RequestToExecute extends AchieveREInitiator {
			private static final long serialVersionUID = -8104498062148279796L;

			public RequestToExecute(Agent a, ACLMessage msg) {
				super(a, msg);
			}

			@Override
			protected void handleInform(ACLMessage inform) {
				System.out.println("SalesMarketAgent: received [inform] " + inform.getContent() + " is in warehouse");
				stop();

				// TODO: Is it nessessary to send something?
				// ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				// msg.addReceiver(starterMessage.getSender());
				// msg.setContent(starterMessage.getContent());
				// send(msg);

				addBehaviour(new GetFromWarehouse(myAgent, 2000, inform));
			}

			@Override
			protected void handleFailure(ACLMessage failure) {
				System.out.println(
						"SalesMarketAgent: received [failure] " + failure.getContent() + " is not in warehouse");
				stop();
			}
		}
	}

	class GetFromWarehouse extends TickerBehaviour {
		private static final long serialVersionUID = -1534610326024914625L;

		public String obj;

		public GetFromWarehouse(Agent a, long period, ACLMessage msg) {
			super(a, period);
			obj = msg.getContent();
		}

		@Override
		protected void onTick() {
			System.out.println("SalesMarketAgent: Asking SellingAgent to take " + obj + " from warehouse");

			String requestedAction = "Take";
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			msg.setConversationId(requestedAction);
			msg.addReceiver(new AID(("sellingAgent"), AID.ISLOCALNAME));
			msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
			msg.setReplyByDate(new Date(System.currentTimeMillis() + 10000));
			msg.setContent(obj);

			addBehaviour(new RequestToExecute(myAgent, msg));
		}

		@Override
		public void stop() {
			System.out.println("SalesMarketAgent: Now I have a " + obj);
			super.stop();
		}

		class RequestToExecute extends AchieveREInitiator {
			private static final long serialVersionUID = -8104498062148279796L;

			public RequestToExecute(Agent a, ACLMessage msg) {
				super(a, msg);
			}

			@Override
			protected void handleInform(ACLMessage inform) {
				System.out.println(
						"SalesMarketAgent: received [inform] " + inform.getContent() + " will be taken from warehouse");
				stop();
			}

			@Override
			protected void handleFailure(ACLMessage failure) {
				System.out.println("SalesMarketAgent: received [failure] " + failure.getContent()
						+ " will not be taken from warehouse");
				stop();
			}
		}
	}
}
