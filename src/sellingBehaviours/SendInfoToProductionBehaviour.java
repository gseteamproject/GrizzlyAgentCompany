package sellingBehaviours;

import basicAgents.SalesMarket;
import basicAgents.Selling;
import basicClasses.Order;
import communication.Communication;
import communication.MessageObject;
import jade.core.AID;
import jade.core.behaviours.DataStore;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

public class SendInfoToProductionBehaviour extends OneShotBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = -6365251601845699295L;
    private String orderToProceed;
    private String orderText;
    private ACLMessage requestMessage;
    private DataStore dataStore;
    private SellingResponder interactionBehaviour;
    private MessageObject msgObj;

    public SendInfoToProductionBehaviour(SellingResponder interactionBehaviour, ACLMessage msg, DataStore dataStore) {
        super(interactionBehaviour.getAgent());
        orderToProceed = msg.getContent();
        requestMessage = interactionBehaviour.getRequest();
        this.interactionBehaviour = interactionBehaviour;
        this.dataStore = dataStore;
    }

    @Override
    public void action() {
        orderText = Order.gson.fromJson(orderToProceed, Order.class).getTextOfOrder();
        System.out.println("SellingAgent: " + orderText + " is in production");
        Communication.server.sendMessageToClient("SellingAgent", orderText + " is in production");

        String requestedAction = "Produce";
        ACLMessage requestToProduction = new ACLMessage(ACLMessage.REQUEST);
        requestToProduction.setConversationId(requestedAction);
        // there should be financesAgent, but we will ignore it by now
        requestToProduction.addReceiver(new AID(("AgentProduction"), AID.ISLOCALNAME));
        // requestToProduction.addReceiver(new AID(("AgentFinances"), AID.ISLOCALNAME));
        requestToProduction.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
        requestToProduction.setContent(orderToProceed);

        myAgent.addBehaviour(new RequestToFinanceInitiator(interactionBehaviour, requestToProduction, dataStore));
    }

    class RequestToFinanceInitiator extends AchieveREInitiator {

        /**
         * 
         */
        private static final long serialVersionUID = 994161564616428958L;
        private DataStore dataStore;
        private SellingResponder interactionBehaviour;
        SellingRequestResult interactor;
        private MessageObject msgObj;

        public RequestToFinanceInitiator(SellingResponder interactionBehaviour, ACLMessage msg, DataStore dataStore) {
            super(interactionBehaviour.getAgent(), msg);
            this.interactionBehaviour = interactionBehaviour;
            this.dataStore = dataStore;
            this.interactor = new SellingRequestResult(dataStore);
        }

        @Override
        protected void handleAgree(ACLMessage agree) {
            orderText = Order.gson.fromJson(agree.getContent(), Order.class).getTextOfOrder();
            System.out.println("SellingAgent: received [agree] Producing of " + orderText + " is initiated");
            Communication.server.sendMessageToClient("SellingAgent",
                    "received [agree] Producing of " + orderText + " is initiated");
        }

        @Override
        protected void handleInform(ACLMessage inform) {
            Order order = Order.gson.fromJson(inform.getContent(), Order.class);
            orderText = order.getTextOfOrder();
            System.out.println("SellingAgent: received [inform] " + orderText + " is delivered to warehouse");
            Communication.server.sendMessageToClient("SellingAgent",
                    "received [inform] " + orderText + " is delivered to warehouse");
            Selling.isInWarehouse = true;
            for (Order orderInQueue : SalesMarket.orderQueue) {
                if (orderInQueue.id == order.id) {
                    order = orderInQueue;
                }
            }

            // ACLMessage reply = requestMessage.createReply();
            // reply.setPerformative(ACLMessage.INFORM);
            // String testGson = Order.gson.toJson(order);
            // reply.setContent(testGson);
            // myAgent.send(reply);
        }

        @Override
        protected void handleFailure(ACLMessage failure) {
            orderText = Order.gson.fromJson(failure.getContent(), Order.class).getTextOfOrder();
            System.out.println("SellingAgent: received [failure] is not produced");
            Communication.server.sendMessageToClient("SellingAgent", "received [failure] is not produced");
        }
    }
}