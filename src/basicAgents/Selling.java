package basicAgents;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import basicClasses.Product;
import basicClasses.Order;
import basicClasses.OrderPart;
import basicClasses.ProductStorage;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREInitiator;
import jade.proto.AchieveREResponder;

public class Selling extends Agent {

    /**
     * 
     */
    private static final long serialVersionUID = 7150875080288668056L;
    public ACLMessage starterMessage;
    public boolean isInWarehouse;
    public boolean isTaken;

    // queue for orders that in production
    public static List<Order> productionQueue = new ArrayList<Order>();

    // creating storage for products
    public static ProductStorage warehouse = new ProductStorage();

    @Override
    protected void setup() {
        MessageTemplate reqTemp = AchieveREResponder.createMessageTemplate(FIPANames.InteractionProtocol.FIPA_REQUEST);

        // adding behaviours
        addBehaviour(new WaitingForOrder(this, reqTemp));
    }

    // this class waits for receiving a message with certain template
    class WaitingForOrder extends AchieveREResponder {

        /**
         * 
         */
        private static final long serialVersionUID = 4671831774439180119L;
        private String orderText;

        public WaitingForOrder(Agent a, MessageTemplate mt) {
            super(a, mt);
        }

        @Override
        protected ACLMessage prepareResponse(ACLMessage request) throws NotUnderstoodException, RefuseException {
            // Selling reacts on SalesMarket's request

            // save this request message to reply on it later
            starterMessage = request;

            orderText = Order.gson.fromJson(request.getContent(), Order.class).getTextOfOrder();

            // Agent should send agree or refuse
            // TODO: Add refuse answer (some conditions should be added)

            ACLMessage response = request.createReply();
            response.setContent(request.getContent());
            response.setPerformative(ACLMessage.AGREE);

            // response.setPerformative(ACLMessage.REFUSE);

            if (request.getConversationId() == "Ask") {
                System.out.println("SellingAgent: [request] SalesMarket orders a " + orderText);
                System.out.println("SellingAgent: [agree] I will check warehouse for " + orderText);
                addBehaviour(new CheckWarehouse(myAgent, response));
            } else if (request.getConversationId() == "Take") {
                System.out
                        .println("SellingAgent: [request] SalesMarket wants to take " + orderText + " from warehouse");
                System.out.println("SellingAgent: [agree] I will give you " + orderText + " from warehouse");
                addBehaviour(new GiveProductToMarket(myAgent, response));
            }

            return response;
        }

        @Override
        protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response)
                throws FailureException {

            // if agent agrees to request
            // after executing, it should send failure of inform

            // in case of inform product will be taken from warehouse
            // in case of failure product will be produced
            ACLMessage reply = request.createReply();
            reply.setContent(request.getContent());

            if (request.getConversationId() == "Ask") {
                if (isInWarehouse) {
                    reply.setPerformative(ACLMessage.INFORM);
                } else {
                    reply.setPerformative(ACLMessage.FAILURE);
                }
            } else if (request.getConversationId() == "Take") {
                if (isTaken) {
                    reply.setPerformative(ACLMessage.INFORM);
                } else {
                    reply.setPerformative(ACLMessage.FAILURE);
                }
            }

            return reply;
        }
    }

    class CheckWarehouse extends OneShotBehaviour {

        /**
         * 
         */
        private static final long serialVersionUID = 3856126876248315456L;
        private String requestedOrder;
        private ACLMessage msgToProduction;

        public CheckWarehouse(Agent a, ACLMessage msg) {
            super(a);
            requestedOrder = msg.getContent();
            msgToProduction = msg;
        }

        @Override
        public void action() {
            Order order = Order.gson.fromJson(requestedOrder, Order.class);

            isInWarehouse = true;
            boolean isInQueue = false;

            // check if this order is not in queue yet
            isInQueue = productionQueue.contains(order);

            // part of order, that needs to be produced
            Order orderToProduce = new Order();
            orderToProduce.id = order.id;

            for (OrderPart orderPart : order.orderList) {
                Product productToCheck = orderPart.product;
                int amount = orderPart.amount;

                System.out.println("SellingAgent: Asking warehouse about " + orderPart.getTextOfOrderPart());

                int amountInWH = warehouse.getAmountOfProduct(productToCheck);

                if (amountInWH >= amount) {
                    if (isInWarehouse) {
                        isInWarehouse = true;
                    }
                    System.out
                            .println("SellingAgent: I say that " + orderPart.getTextOfOrderPart() + " is in warehouse");
                } else {
                    isInWarehouse = false;

                    // creating new instance of OrderPart to change its amount
                    OrderPart newOrderPart = new OrderPart();
                    newOrderPart.product = orderPart.product;
                    newOrderPart.amount = orderPart.amount - amountInWH;
                    if (newOrderPart.amount > 0) {
                        orderToProduce.orderList.add(newOrderPart);
                    }
                }
            }

            // productToCheck needs to be produced
            if (!isInQueue && (orderToProduce.orderList.size() > 0)) {
                String testGson = Order.gson.toJson(orderToProduce);
                msgToProduction.setContent(testGson);

                // add order to queue
                productionQueue.add(order);

                System.out.println(
                        "SellingAgent: Sending an info to Finance Agent to produce " + orderToProduce.getTextOfOrder());
                addBehaviour(new SendInfoToProduction(myAgent, msgToProduction));
            }
        }
    }

    class SendInfoToProduction extends OneShotBehaviour {

        /**
         * 
         */
        private static final long serialVersionUID = -6365251601845699295L;
        private String orderToProceed;
        private String orderText;

        public SendInfoToProduction(Agent a, ACLMessage msg) {
            super(a);
            orderToProceed = msg.getContent();
        }

        @Override
        public void action() {
            orderText = Order.gson.fromJson(orderToProceed, Order.class).getTextOfOrder();
            System.out.println("SellingAgent: " + orderText + " is in production");

            String requestedAction = "Produce";
            ACLMessage requestToProduction = new ACLMessage(ACLMessage.REQUEST);
            requestToProduction.setConversationId(requestedAction);
            // there should be financesAgent, but we will ignore it by now
            requestToProduction.addReceiver(new AID(("AgentProduction"), AID.ISLOCALNAME));
            // requestToProduction.addReceiver(new AID(("AgentFinances"), AID.ISLOCALNAME));
            requestToProduction.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
            requestToProduction.setContent(orderToProceed);

            addBehaviour(new RequestToFinance(myAgent, requestToProduction));
        }

        class RequestToFinance extends AchieveREInitiator {

            /**
             * 
             */
            private static final long serialVersionUID = 994161564616428958L;

            public RequestToFinance(Agent a, ACLMessage msg) {
                super(a, msg);
            }

            @Override
            protected void handleAgree(ACLMessage agree) {
                orderText = Order.gson.fromJson(agree.getContent(), Order.class).getTextOfOrder();
                System.out.println("SellingAgent: received [agree] Producing of " + orderText + " is initiated");
            }

            @Override
            protected void handleInform(ACLMessage inform) {
                orderText = Order.gson.fromJson(inform.getContent(), Order.class).getTextOfOrder();
                System.out.println("SellingAgent: received [inform] " + orderText + " is delivered to warehouse");

                ACLMessage reply = starterMessage.createReply();
                reply.setPerformative(ACLMessage.INFORM);
                reply.setContent(starterMessage.getContent());
                send(reply);
            }

            @Override
            protected void handleFailure(ACLMessage failure) {
                orderText = Order.gson.fromJson(failure.getContent(), Order.class).getTextOfOrder();
                System.out.println("SellingAgent: received [failure] is not produced");
            }
        }
    }

    class GiveProductToMarket extends OneShotBehaviour {

        /**
         * 
         */
        private static final long serialVersionUID = -6498277261596869382L;
        private String orderToGive;

        public GiveProductToMarket(Agent a, ACLMessage msg) {
            super(a);
            orderToGive = msg.getContent();
        }

        @Override
        public void action() {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            Order order = gson.fromJson(orderToGive, Order.class);

            isTaken = false;

            int takeCount = 0;
            for (OrderPart orderPart : order.orderList) {
                Product productToGive = orderPart.product;
                System.out.println("SellingAgent: Taking " + orderPart.getTextOfOrderPart() + " from warehouse");
                warehouse.remove(productToGive);
                takeCount += 1;
            }
            if (takeCount == order.orderList.size()) {
                isTaken = true;
            }
        }
    }
}
