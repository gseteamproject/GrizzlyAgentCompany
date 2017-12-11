package basicAgents;

import java.util.Date;

import basicClasses.Product;
import basicClasses.Order;
import basicClasses.OrderPart;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.SimpleBehaviour;
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
    public boolean isProduced = false;

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
        
        public ACLMessage getRequest() {
    		return (ACLMessage) getDataStore().get(REQUEST_KEY);
    	}

        @Override
        protected ACLMessage prepareResponse(ACLMessage request) throws NotUnderstoodException, RefuseException {
            // ProductionAgent reacts on SellingAgent's request

            registerPrepareResultNotification(new ActivityBehaviour(myAgent, this));

            orderText = Order.gson.fromJson(request.getContent(), Order.class).getTextOfOrder();

            System.out.println("ProductionAgent: [request] SellingAgent asks to produce " + orderText);
            // Agent should send agree or refuse
            // TODO: Add refuse answer (some conditions should be added)
            ACLMessage agree = request.createReply();
            agree.setContent(request.getContent());
            agree.setPerformative(ACLMessage.AGREE);
            System.out.println("ProductionAgent: [agree] I will produce " + orderText);

            // if agent agrees it starts executing request
            // addBehaviour(new AskForMaterial(myAgent, 2000, agree));

            // registerPrepareResultNotification(new AskForMaterial(myAgent, 2000, agree));

            return agree;
        }

        @Override
        protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response)
                throws FailureException {

            orderText = Order.gson.fromJson(request.getContent(), Order.class).getTextOfOrder();

            // result of request to ProductionAgent
            // if agent agrees to request
            // after executing, it should send failure of inform
            ACLMessage reply = request.createReply();
            reply.setContent(request.getContent());

            if (isProduced) {
                reply.setPerformative(ACLMessage.INFORM);
            } else {
                reply.setPerformative(ACLMessage.FAILURE);
            }
            return reply;

        }
    }

    // TODO: REFACTOR THIS
    // TODO: Use DataStore

    public class ActivityBehaviour extends SequentialBehaviour {
        /**
         * 
         */
        private static final long serialVersionUID = 887352333041438646L;

        public ActivityBehaviour(Agent a, WaitingTaskMessage owner) {
            super(a);

            addSubBehaviour(new AskBehaviour(owner.getRequest()));
            addSubBehaviour(new WorkBehaviour(owner.getRequest()));
        }
    }

    public class Work {

        public Work(ACLMessage msg) {
            super();
        }

        public ACLMessage execute(ACLMessage request) {
            ACLMessage response = request.createReply();
            response.setPerformative(ACLMessage.INFORM);

            return response;
        }
    }

    public class WorkBehaviour extends SimpleBehaviour {
        Agent interactionBehaviour;
        Work interactor;
        ACLMessage request;

        public WorkBehaviour(ACLMessage msg) {
            this.interactor = new Work(msg);
            this.request = msg;
        }

        @Override
        public void action() {
            request = (interactor.execute(request));
        }

        @Override
        public boolean done() {
            return true;
        }

        private static final long serialVersionUID = -3500469822678572098L;
    }

    public class AskBehaviour extends SimpleBehaviour {
        ACLMessage request;

        public AskBehaviour(ACLMessage msg) {
            this.request = msg;
        }

        @Override
        public void action() {
            addBehaviour(new AskForMaterial(myAgent, 2000, request));
        }

        @Override
        public boolean done() {
            return true;
        }

        private static final long serialVersionUID = -3500469822678572098L;
    }

    // TODO: Use OneShot (?)
    class AskForMaterial extends TickerBehaviour {

        /**
         * 
         */
        private static final long serialVersionUID = 8495802171064457305L;
        private String materialsToRequest;
        private String orderText;
        private ACLMessage requestMessage;

        public AskForMaterial(Agent a, long period, ACLMessage msg) {
            super(a, period);
            requestMessage = msg;
        }

        @Override
        protected void onTick() {
            materialsToRequest = requestMessage.getContent();
            orderText = Order.gson.fromJson(materialsToRequest, Order.class).getTextOfOrder();

            System.out.println("ProductionAgent: Asking ProcurementAgent to get materials for " + orderText);

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

            orderText = Order.gson.fromJson(materialsToRequest, Order.class).getTextOfOrder();

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
                orderText = Order.gson.fromJson(inform.getContent(), Order.class).getTextOfOrder();

                System.out.println("ProductionAgent: received [inform] materials for " + orderText + " are in storage");
                stop();

                addBehaviour(new GetFromStorage(myAgent, 2000, inform, requestMessage));
            }

            @Override
            protected void handleFailure(ACLMessage failure) {
                orderText = Order.gson.fromJson(failure.getContent(), Order.class).getTextOfOrder();

                System.out.println(
                        "ProductionAgent: received [failure] materials for " + orderText + " are not in storage");
            }
        }
    }

    // TODO: Use OneShot (?)
    class GetFromStorage extends TickerBehaviour {

        /**
         * 
         */
        private static final long serialVersionUID = 6717167573013445327L;
        private String materialsToTake;
        private String orderText;
        private ACLMessage requestMessage;

        public GetFromStorage(Agent a, long period, ACLMessage msg, ACLMessage request) {
            super(a, period);
            materialsToTake = msg.getContent();
            requestMessage = request;
        }

        @Override
        protected void onTick() {

            orderText = Order.gson.fromJson(materialsToTake, Order.class).getTextOfOrder();

            System.out.println("ProductionAgent: Asking ProcurementAgent to take materials for " + orderText
                    + " from materialStorage");

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

            orderText = Order.gson.fromJson(materialsToTake, Order.class).getTextOfOrder();

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

                orderText = Order.gson.fromJson(inform.getContent(), Order.class).getTextOfOrder();

                System.out.println("ProductionAgent: received [inform] materials for " + orderText
                        + " will be taken from storage");
                stop();

                addBehaviour(new DeliverToSelling(myAgent, inform, requestMessage));
            }

            @Override
            protected void handleFailure(ACLMessage failure) {

                orderText = Order.gson.fromJson(failure.getContent(), Order.class).getTextOfOrder();

                System.out.println("ProductionAgent: received [failure] materials for " + orderText
                        + " will not be taken from storage");
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
        private ACLMessage reply, requestMessage;

        public DeliverToSelling(Agent a, ACLMessage msg, ACLMessage request) {
            super(a);
            orderToGive = msg.getContent();
            requestMessage = request;
        }

        @Override
        public void action() {
            Order order = Order.gson.fromJson(orderToGive, Order.class);
            orderText = order.getTextOfOrder();
            System.out.println("ProductionAgent: Delivering " + orderText + " to warehouse");

            for (OrderPart orderPart : order.orderList) {
                Product productToGive = orderPart.getProduct();
                for (int i = 0; i < orderPart.getAmount(); i++) {
                    Selling.warehouse.add(productToGive);
                }
            }
            isProduced = true;

            reply = requestMessage.createReply();
            reply.setPerformative(ACLMessage.INFORM);
            reply.setContent(orderToGive);
            send(reply);
        }
    }
}
