package basicAgents;

import java.util.Date;

import basicClasses.Material;
import basicClasses.Storage;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
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

public class Selling extends Agent {
	private static final long serialVersionUID = 3662790430798172624L;
	public ACLMessage starterMessage;
	public boolean isInWarehouse;

	public static Storage warehouse = new Storage();

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

		MessageTemplate reqTemp = AchieveREResponder.createMessageTemplate(FIPANames.InteractionProtocol.FIPA_REQUEST);

		addBehaviour(new WaitingForOrder(this, reqTemp));
	}

	// this class waits for receiving a message with certain template
	class WaitingForOrder extends AchieveREResponder {
		private static final long serialVersionUID = 6130496380982287815L;

		public WaitingForOrder(Agent a, MessageTemplate mt) {
			super(a, mt);
		}

		@Override
		protected ACLMessage prepareResponse(ACLMessage request) throws NotUnderstoodException, RefuseException {

			// this shows subject of interaction, request of SalesMarket
			System.out.println("ID " + request.getConversationId());

			// Selling reacts on SalesMarket's request
			// Agent should send agree or refuse
			// TODO: Add refuse answer (some conditions should be added)

			starterMessage = request;
			ACLMessage agree = request.createReply();
			agree.setContent(request.getContent());
			agree.setPerformative(ACLMessage.AGREE);

			if (request.getConversationId() == "Order") {
				System.out.println("[request] SalesMarket orders a " + request.getContent());
				System.out.println("[agree] I will check warehouse for " + agree.getContent());
				addBehaviour(new CheckWarehouse(myAgent, agree));
			} else if (request.getConversationId() == "Take") {
				System.out.println("[request] SalesMarket wants to get " + request.getContent() + " from warehouse");
				System.out.println("[agree] I will give you " + agree.getContent() + " from warehouse");
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
				inform.setContent(response.getContent());
				inform.setPerformative(ACLMessage.INFORM);
				return inform;
			} else {
				ACLMessage failure = request.createReply();
				failure.setContent(response.getContent());
				failure.setPerformative(ACLMessage.FAILURE);
				return failure;
			}
		}
	}

	class CheckWarehouse extends OneShotBehaviour {
		private static final long serialVersionUID = -1534610326024914625L;

		public String obj;
		public ACLMessage agree;

		public CheckWarehouse(Agent a, ACLMessage msg) {
			super(a);
			obj = msg.getContent();
			agree = msg;
		}

		@Override
		public void action() {
			System.out.println("SellingAgent: Asking warehouse about " + obj);

			// TODO: Get color and size from message
			int amountInWH = warehouse.getAmountByColor("blue");

			if (amountInWH >= 1) {
				isInWarehouse = true;
				System.out.println("SellingAgent: I say that " + obj + " is in warehouse");
			} else {
				isInWarehouse = false;
				System.out.println("send info to Finances about product to produce " + obj);
				addBehaviour(new SendInfo(myAgent, 2000, agree));
			}
		}
	}

	class SendInfo extends TickerBehaviour {
		private static final long serialVersionUID = -1534610326024914625L;

		public String obj;

		public SendInfo(Agent a, long period, ACLMessage msg) {
			super(a, period);
			obj = msg.getContent();
		}

		@Override
		protected void onTick() {
			System.out.println("SellingAgent: Sending an info to Finance Agent to produce " + obj);

			String requestedAction = "Order";
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			msg.setConversationId(requestedAction);
			msg.addReceiver(new AID(("financesAgent"), AID.ISLOCALNAME));
			msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
			msg.setReplyByDate(new Date(System.currentTimeMillis() + 10000));
			msg.setContent(obj);

			addBehaviour(new RequestToFinance(myAgent, msg));
		}

		@Override
		public void stop() {
			System.out.println("SellingAgent: " + obj + " is in production");
			super.stop();
		}

		class RequestToFinance extends AchieveREInitiator {
			private static final long serialVersionUID = -8104498062148279796L;

			public RequestToFinance(Agent a, ACLMessage msg) {
				super(a, msg);
			}

			@Override
			protected void handleInform(ACLMessage inform) {
				System.out.println(inform.getContent());
				stop();

				// TODO: Is it nessessary to send something?
				// ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				// msg.addReceiver(starterMessage.getSender());
				// msg.setContent(starterMessage.getContent());
				// send(msg);
			}
		}
	}

	class GiveProductToMarket extends OneShotBehaviour {
		private static final long serialVersionUID = -1534610326024914625L;

		public String obj;

		public GiveProductToMarket(Agent a, ACLMessage msg) {
			super(a);
			obj = msg.getContent();
		}

		@Override
		public void action() {
			System.out.println("SellingAgent: Taking " + obj + " from warehouse");

			// TODO: Get color and size from message
			Material requiredMat = new Material("blue", 10);
			warehouse.remove(requiredMat);
		}
	}
}
