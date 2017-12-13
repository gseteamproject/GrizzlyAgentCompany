package salesMarketBehaviours;

import basicAgents.SalesMarket;
import basicClasses.Order;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.DataStore;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

public class TakeFromWarehouseBehaviour extends OneShotBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = 4233055394916376580L;
    private String orderToTake;
    private String orderText;
    private DataStore dataStore;
    private SalesMarketResponder interactionBehaviour;

    public TakeFromWarehouseBehaviour(SalesMarketResponder interactionBehaviour, ACLMessage msg,
            DataStore dataStore) {
        super(interactionBehaviour.getAgent());
        this.interactionBehaviour = interactionBehaviour;
        this.dataStore = dataStore;
        // TODO: PROBLEM IS HERE
        orderToTake = msg.getContent();
    }

    @Override
    public void action() {
        orderText = Order.gson.fromJson(orderToTake, Order.class).getTextOfOrder();

        System.out.println("SalesMarketAgent: Asking SellingAgent to take " + orderText + " from warehouse");

        String requestedAction = "Take";
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.setConversationId(requestedAction);
        msg.addReceiver(new AID(("AgentSelling"), AID.ISLOCALNAME));
        msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
        msg.setContent(orderToTake);

        myAgent.addBehaviour(new RequestToTake(myAgent, msg));
    }

    class RequestToTake extends AchieveREInitiator {

        /**
         * 
         */
        private static final long serialVersionUID = -2624609588724924573L;

        public RequestToTake(Agent a, ACLMessage msg) {
            super(a, msg);
        }

        @Override
        protected void handleInform(ACLMessage inform) {
            Order order = Order.gson.fromJson(inform.getContent(), Order.class);
            orderText = order.getTextOfOrder();

            System.out.println("SalesMarketAgent: received [inform] " + orderText + " will be taken from warehouse");
            System.out.println("SalesMarketAgent: Now I have a " + orderText);
            if (SalesMarket.orderQueue.remove(order)) {
                System.out.println("SalesMarketAgent: " + orderText + " is removed from Orderqueue.");
            }
        }

        @Override
        protected void handleFailure(ACLMessage failure) {
            orderText = Order.gson.fromJson(failure.getContent(), Order.class).getTextOfOrder();

            System.out
                    .println("SalesMarketAgent: received [failure] " + orderText + " will not be taken from warehouse");
        }
    }
}
