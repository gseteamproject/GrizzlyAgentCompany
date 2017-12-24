package productionBehaviours;

import basicClasses.Order;
import jade.core.AID;
import jade.core.behaviours.DataStore;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

class GetFromStorageBehaviour extends OneShotBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = 6717167573013445327L;
    private String materialsToTake;
    private String orderText;
    private ACLMessage requestMessage;
    private DataStore dataStore;
    private ProductionResponder interactionBehaviour;

    public GetFromStorageBehaviour(ProductionResponder interactionBehaviour, DataStore dataStore) {
        super(interactionBehaviour.getAgent());
        this.dataStore = dataStore;
        this.interactionBehaviour = interactionBehaviour;
        requestMessage = interactionBehaviour.getRequest();
        materialsToTake = requestMessage.getContent();
    }

    @Override
    public void action() {

        orderText = Order.gson.fromJson(materialsToTake, Order.class).getTextOfOrder();

        System.out.println("ProductionAgent: Asking ProcurementAgent to take materials for " + orderText
                + " from materialStorage");

        String requestedAction = "Take";
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.setConversationId(requestedAction);
        msg.addReceiver(new AID(("AgentProcurement"), AID.ISLOCALNAME));
        msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
        // msg.setReplyByDate(new Date(System.currentTimeMillis() + 10000));
        msg.setContent(materialsToTake);

        System.out.println(interactionBehaviour.getRequest());
        myAgent.addBehaviour(new RequestToTakeInitiator(interactionBehaviour, msg, dataStore));
    }

    class RequestToTakeInitiator extends AchieveREInitiator {

        /**
         * 
         */
        private static final long serialVersionUID = 7996018163076712881L;
        private ProductionResponder interactionBehaviour;
        private DataStore dataStore;

        public RequestToTakeInitiator(ProductionResponder interactionBehaviour, ACLMessage msg, DataStore dataStore) {
            super(interactionBehaviour.getAgent(), msg);
            this.dataStore = dataStore;
            this.interactionBehaviour = interactionBehaviour;
        }

        @Override
        protected void handleInform(ACLMessage inform) {

            orderText = Order.gson.fromJson(inform.getContent(), Order.class).getTextOfOrder();

            System.out.println(
                    "ProductionAgent: received [inform] materials for " + orderText + " will be taken from storage");

            System.out.println("ProductionAgent: Now I have materials for " + orderText);
            myAgent.addBehaviour(new DeliverToSellingBehaviour(interactionBehaviour, dataStore));
        }

        @Override
        protected void handleFailure(ACLMessage failure) {

            orderText = Order.gson.fromJson(failure.getContent(), Order.class).getTextOfOrder();

            System.out.println("ProductionAgent: received [failure] materials for " + orderText
                    + " will not be taken from storage");
        }
    }
}