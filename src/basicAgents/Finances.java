package basicAgents;

import basicClasses.Order;
import basicClasses.ProductStorage;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;

public class Finances extends Agent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4773016963292343207L;
	public ACLMessage starterMessage;
	public boolean isInWarehouse;

	// creating storage for products
	public static ProductStorage warehouse = new ProductStorage();

	@Override
	protected void setup() {
		MessageTemplate reqTemp = AchieveREResponder.createMessageTemplate(FIPANames.InteractionProtocol.FIPA_REQUEST);

		// adding behaviours
		addBehaviour(new WaitingForTransaction(this, reqTemp));
	}

	// this class waits for receiving a message with certain template
	class WaitingForTransaction extends AchieveREResponder {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7237232183102411323L;
		private String orderText;

		public WaitingForTransaction(Agent a, MessageTemplate mt) {
			super(a, mt);
		}

		@Override
		protected ACLMessage prepareResponse(ACLMessage request) throws NotUnderstoodException, RefuseException {
			// Finances reacts on Selling's or Procurement's request

			orderText = Order.gson.fromJson(request.getContent(), Order.class).getTextOfProductOrder();

			// Agent should send agree or refuse
			// TODO: Add refuse answer (some conditions should be added)

			starterMessage = request;
			ACLMessage agree = request.createReply();
			agree.setContent(request.getContent());
			agree.setPerformative(ACLMessage.AGREE);

			if (request.getConversationId() == "Sell") {
				System.out.println("FinancesAgent: [request] Sellings wants to sell " + orderText);
				System.out.println("FinancesAgent: [agree] I accept to sell " + orderText);
				addBehaviour(new TransferMoneyToBank(myAgent, agree));
			} else if (request.getConversationId() == "Buy") {
				System.out.println("FinancesAgent: [request] Procurement wants to buy " + orderText);
				System.out.println("FinancesAgent: [agree] I accept to buy " + orderText);
				addBehaviour(new TransferMoneyFromBank(myAgent, agree));
			}

			return agree;
		}

		@Override
		protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response)
				throws FailureException {
			// if agent agrees to request
			// after executing, it should send failure of inform

			// in case of inform money will be transfered
			ACLMessage inform = request.createReply();
			inform.setContent(request.getContent());
			inform.setPerformative(ACLMessage.INFORM);
			return inform;
		}
	}

	class TransferMoneyToBank extends OneShotBehaviour {
		/**
		 * 
		 */
		private static final long serialVersionUID = 8632047471324433248L;
		private String orderToSell;
		private String orderText;

		public TransferMoneyToBank(Agent a, ACLMessage msg) {
			super(a);
			orderToSell = msg.getContent();
		}

		@Override
		public void action() {
			orderText = Order.gson.fromJson(orderToSell, Order.class).getTextOfProductOrder();
			System.out.println("FinancesAgent: Transfering money for selling " + orderText + " to Bank");

			// TODO: Do something here
		}
	}

	class TransferMoneyFromBank extends OneShotBehaviour {
		/**
		 * 
		 */
		private static final long serialVersionUID = -1431593557266453540L;
		private String orderToBuy;
		private String orderText;

		public TransferMoneyFromBank(Agent a, ACLMessage msg) {
			super(a);
			orderToBuy = msg.getContent();
		}

		@Override
		public void action() {
			orderText = Order.gson.fromJson(orderToBuy, Order.class).getTextOfProductOrder();
			System.out.println("FinancesAgent: Transfering money to buy " + orderText + " from Bank");

			// TODO: Do something here
		}
	}
}
