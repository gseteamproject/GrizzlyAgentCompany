package basicAgents;

import java.util.ArrayList;
import java.util.Date;
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

            orderText = Order.gson.fromJson(request.getContent(), Order.class).getTextOfOrder();

            // Agent should send agree or refuse
            // TODO: Add refuse answer (some conditions should be added)

            starterMessage = request;
            ACLMessage agree = request.createReply();
            agree.setContent(request.getContent());
            agree.setPerformative(ACLMessage.AGREE);

            ACLMessage refuse = request.createReply();
            refuse.setContent(request.getContent());
            refuse.setPerformative(ACLMessage.REFUSE);

            if (request.getConversationId() == "Order") {
                System.out.println("SellingAgent: [request] SalesMarket orders a " + orderText);
                System.out.println("SellingAgent: [agree] I will check warehouse for " + orderText);
                addBehaviour(new CheckWarehouse(myAgent, agree));
            } else if (request.getConversationId() == "Take") {
                System.out.println("SellingAgent: [request] SalesMarket wants to get " + orderText + " from warehouse");
                System.out.println("SellingAgent: [agree] I will give you " + orderText + " from warehouse");
                addBehaviour(new GiveProductToMarket(myAgent, agree));
            }

            return agree;
        }

        @Override
        protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response)
                throws FailureException {
            // Order order = Order.gson.fromJson(request.getContent(), Order.class);

            // if agent agrees to request
            // after executing, it should send failure of inform

            // in case of inform product will be taken from warehouse
            // in case of failure product will be produced
            if (isInWarehouse) {
                ACLMessage inform = request.createReply();
                inform.setContent(request.getContent());
                inform.setPerformative(ACLMessage.INFORM);
                return inform;
            } else {
                ACLMessage failure = request.createReply();
                failure.setContent(request.getContent());
                failure.setPerformative(ACLMessage.FAILURE);
                // //PUT INTO PRODUCTION QUEUE
                // productionQueue.add(order);
                // System.out.println("SellingAgent: " + orderText + " is added to the
                // ProductionQueue.");
                return failure;
            }
        }
    }

    class CheckWarehouse extends OneShotBehaviour {

        /**
         * 
         */
        private static final long serialVersionUID = 3856126876248315456L;
        private String requestedOrder;

        private ACLMessage agree;

        public CheckWarehouse(Agent a, ACLMessage msg) {
            super(a);
            requestedOrder = msg.getContent();
            agree = msg;
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
                agree.setContent(testGson);

                // add order to queue
                productionQueue.add(order);

                System.out.println(
                        "SellingAgent: Sending an info to Finance Agent to produce " + orderToProduce.getTextOfOrder());
                addBehaviour(new SendInfoToProduction(myAgent, agree));
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

            String requestedAction = "Order";
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.setConversationId(requestedAction);
            // there should be financesAgent, but we will ignore it by now
            msg.addReceiver(new AID(("AgentProduction"), AID.ISLOCALNAME));
            // msg.addReceiver(new AID(("AgentFinances"), AID.ISLOCALNAME));
            msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
            msg.setReplyByDate(new Date(System.currentTimeMillis() + 10000));
            msg.setContent(orderToProceed);

            addBehaviour(new RequestToFinance(myAgent, msg));
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
            protected void handleInform(ACLMessage inform) {

                orderText = Order.gson.fromJson(inform.getContent(), Order.class).getTextOfOrder();
                System.out.println("SellingAgent: [inform] Producing of " + orderText + " is initiated");
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
            for (OrderPart orderPart : order.orderList) {
                Product productToGive = orderPart.product;
                System.out.println("SellingAgent: Taking " + orderPart.getTextOfOrderPart() + " from warehouse");
                warehouse.remove(productToGive);
            }
        }
    }
}
