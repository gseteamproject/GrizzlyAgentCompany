package salesMarketBehaviours;

import basicClasses.Order;
import jade.core.AID;
import jade.core.behaviours.DataStore;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREInitiator;

public class AskForOrderBehaviour extends OneShotBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = 8296971392230921846L;
    private String orderToRequest;
    private String orderText;
    private DataStore dataStore;
    private SalesMarketResponder interactionBehaviour;

    public AskForOrderBehaviour(SalesMarketResponder interactionBehaviour, DataStore dataStore) {
        super(interactionBehaviour.getAgent());
        this.interactionBehaviour = interactionBehaviour;
        this.dataStore = dataStore;
        orderToRequest = interactionBehaviour.getRequest().getContent();
        // orderToRequest = dataStore.getContent();
    }

    @Override
    public void action() {
        orderText = Order.gson.fromJson(orderToRequest, Order.class).getTextOfOrder();
        System.out.println("SalesMarketAgent: Asking SellingAgent to get " + orderText);

        String requestedAction = "Ask";
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.setConversationId(requestedAction);
        msg.addReceiver(new AID(("AgentSelling"), AID.ISLOCALNAME));
        msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
        msg.setContent(orderToRequest);

        myAgent.addBehaviour(new RequestToOrderInitiator(interactionBehaviour, msg, dataStore));
    }

    class RequestToOrderInitiator extends AchieveREInitiator {

        /**
         * 
         */
        private static final long serialVersionUID = -6945741747877024833L;

        public RequestToOrderInitiator(SalesMarketResponder interactionBehaviour, ACLMessage msg,
                DataStore dataStore) {
            super(interactionBehaviour.getAgent(), msg, dataStore);
        }

        @Override
        protected void handleInform(ACLMessage inform) {
            orderText = Order.gson.fromJson(inform.getContent(), Order.class).getTextOfOrder();

            System.out.println("SalesMarketAgent: received [inform] " + orderText + " is in warehouse");

            myAgent.addBehaviour(new TakeFromWarehouseBehaviour(interactionBehaviour, inform, dataStore));
        }

        @Override
        protected void handleFailure(ACLMessage failure) {
            Order order = Order.gson.fromJson(failure.getContent(), Order.class);
            orderText = order.getTextOfOrder();

            System.out.println("SalesMarketAgent: received [failure] " + orderText + " is not in warehouse");

            MessageTemplate temp = MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
            MessageTemplate infTemp = MessageTemplate.and(temp, MessageTemplate.MatchPerformative(ACLMessage.INFORM));
            infTemp = MessageTemplate.and(infTemp, MessageTemplate.MatchConversationId("Ask"));

            // SalesMarket will wait
            myAgent.addBehaviour(new WaitingSellingMessageResponder(interactionBehaviour, infTemp, dataStore));
        }
    }
}