package productionBehaviours;

import java.util.Date;

import basicClasses.Order;
import jade.core.AID;
import jade.core.behaviours.DataStore;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

class AskForMaterialBehaviour extends OneShotBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = 8495802171064457305L;
    private String materialsToRequest;
    private String orderText;
    private DataStore dataStore;
    private ProductionResponder interactionBehaviour;

    public AskForMaterialBehaviour(ProductionResponder interactionBehaviour, DataStore dataStore) {
        super(interactionBehaviour.getAgent());
        this.interactionBehaviour = interactionBehaviour;
        this.dataStore = dataStore;
    }

    @Override
    public void action() {
        System.out.println("bom bom                   " + materialsToRequest);
        materialsToRequest = interactionBehaviour.getRequest().getContent();
        orderText = Order.gson.fromJson(materialsToRequest, Order.class).getTextOfOrder();

        System.out.println("ProductionAgent: Asking ProcurementAgent to get materials for " + orderText);

        String requestedAction = "Materials";
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.setConversationId(requestedAction);
        msg.addReceiver(new AID(("AgentProcurement"), AID.ISLOCALNAME));
        msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
        msg.setReplyByDate(new Date(System.currentTimeMillis() + 10000));
        msg.setContent(materialsToRequest);

        myAgent.addBehaviour(new RequestToGetInitiator(interactionBehaviour, msg, dataStore));
    }

//    @Override
//    public void stop() {
//
//        orderText = Order.gson.fromJson(materialsToRequest, Order.class).getTextOfOrder();
//
//        System.out.println("ProductionAgent: Now I know that materials for " + orderText + " are in storage");
//        super.stop();
//    }

    class RequestToGetInitiator extends AchieveREInitiator {

        /**
         * 
         */
        private static final long serialVersionUID = 1618638159227094879L;
        private DataStore dataStore;

        public RequestToGetInitiator(ProductionResponder interactionBehaviour, ACLMessage msg, DataStore dataStore) {
            super(interactionBehaviour.getAgent(), msg);
            this.dataStore = dataStore;
        }

        @Override
        protected void handleInform(ACLMessage inform) {
            orderText = Order.gson.fromJson(inform.getContent(), Order.class).getTextOfOrder();

            System.out.println("ProductionAgent: received [inform] materials for " + orderText + " are in storage");
//            stop();

            myAgent.addBehaviour(new GetFromStorageBehaviour(interactionBehaviour, 2000, dataStore));
        }

        @Override
        protected void handleFailure(ACLMessage failure) {
            orderText = Order.gson.fromJson(failure.getContent(), Order.class).getTextOfOrder();

            System.out
                    .println("ProductionAgent: received [failure] materials for " + orderText + " are not in storage");
        }
    }
}
