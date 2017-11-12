package basicAgents;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import basicClasses.Paint;
import basicClasses.Product;
import basicClasses.Order;
import basicClasses.Stone;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREInitiator;
import jade.proto.AchieveREResponder;

public class SalesMarket extends Agent {

    /**
     * 
     */
    private static final long serialVersionUID = 2003110338808844985L;
    public ACLMessage starterMessage;

    // creating list of orders
    public static List<Order> orderQueue = new ArrayList<Order>();

    @Override
    protected void setup() {
        MessageTemplate reqTemp = MessageTemplate.and(
                MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
                MessageTemplate.MatchPerformative(ACLMessage.REQUEST));

        // adding behaviours
        addBehaviour(new WaitingCustomerMessage(this, reqTemp));

        addBehaviour(new SimpleAgentWakerBehaviour(this, 4000));
    }

    // class that sends test message with example of order. This simulates customer.
    class SimpleAgentWakerBehaviour extends WakerBehaviour {

        /**
         * 
         */
        private static final long serialVersionUID = 3327849748177688933L;

        public SimpleAgentWakerBehaviour(Agent a, long timeout) {
            super(a, timeout);
        }

        @Override
        public void onWake() {
            // THIS MESSAGE IS FOR TESTING
            ACLMessage testMsg = new ACLMessage(ACLMessage.REQUEST);
            testMsg.addReceiver(new AID(("AgentSalesMarket"), AID.ISLOCALNAME));
            testMsg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);

            // improvised customer
            testMsg.setSender(new AID(("Customer"), AID.ISLOCALNAME));

            // it is an example of order
            Order order = new Order();
            order.id = orderQueue.size() + 1;

            // TODO: ���� ����� ���������� ������ 1 ���� ������, ���� ���������� ��
            // ProcurementAgent: I say that materials for ... are in materialStorage
            order.addProduct(new Product(10, "red"), 1);
            order.addProduct(new Product(10, "blue"), 2);
            order.addProduct(new Product(10, "green"), 2);

            String testGson = Order.gson.toJson(order);
            // {"id":1,"orderList":[{"product":{"stone":{"size":10.0,"price":0},"paint":{"color":"blue","price":0},"price":0},"amount":2},{"product":{"stone":{"size":10.0,"price":0},"paint":{"color":"red","price":0},"price":0},"amount":2}]}

            testMsg.setContent(testGson);
            send(testMsg);

            // adding stone to warehouse and storage
            Paint paint = new Paint("red");
            Stone stone = new Stone(10);
            Product prdct = new Product(stone, paint);
            Selling.warehouse.add(prdct);

            paint = new Paint("blue");
            stone = new Stone(10);
            prdct = new Product(stone, paint);
            Selling.warehouse.add(prdct);
            Procurement.materialStorage.add(paint);
            Procurement.materialStorage.add(stone);

            paint = new Paint("green");
            stone = new Stone(10);
            prdct = new Product(stone, paint);
            Procurement.materialStorage.add(paint);
            Procurement.materialStorage.add(stone);
            Procurement.materialStorage.add(paint);
            Procurement.materialStorage.add(stone);

            // That means:
            // 1 red stone will be taken from warehouse
            // 1 blue stone will be taken from warehouse
            // 1 blue stone will be produced
            // 2 green stone will be produced
        }
    }

    // this class waits for receiving a message with certain template
    class WaitingCustomerMessage extends AchieveREResponder {

        /**
         * 
         */
        private static final long serialVersionUID = 7386418031416044376L;
        private String orderText;

        public WaitingCustomerMessage(Agent a, MessageTemplate mt) {
            super(a, mt);
        }

        @Override
        protected ACLMessage prepareResponse(ACLMessage request) throws NotUnderstoodException, RefuseException {
            // Sales Market reacts on customer's request
            System.out.println("request" + request.getContent());

            Order order = Order.gson.fromJson(request.getContent(), Order.class);
            orderText = order.getTextOfOrder();

            System.out.println("SalesMarketAgent: [request] Customer orders a " + orderText);
            // Agent should send agree or refuse
            // TODO: Add refuse answer (some conditions should be added)
            starterMessage = request;
            ACLMessage agree = request.createReply();
            agree.setContent(request.getContent());
            agree.setPerformative(ACLMessage.AGREE);
            System.out.println("SalesMarketAgent: [agree] I will make an order of " + orderText);

            // if agent agrees it starts executing request
            addBehaviour(new SendAnOrder(myAgent, 2000, agree));

            return agree;
        }

        @Override
        protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response)
                throws FailureException {

            orderText = Order.gson.fromJson(request.getContent(), Order.class).getTextOfOrder();

            // result of request to sales market
            // if agent agrees to request
            // after executing, it should send failure of inform
            ACLMessage inform = request.createReply();
            inform.setContent(request.getContent());
            inform.setPerformative(ACLMessage.INFORM);
            System.out.println("SalesMarketAgent: [inform] I ordered a " + orderText);

            return inform;
        }
    }

    class SendAnOrder extends TickerBehaviour {

        /**
         * 
         */
        private static final long serialVersionUID = 8296971392230921846L;
        private String orderToRequest;
        private String orderText;

        public SendAnOrder(Agent a, long period, ACLMessage msg) {
            super(a, period);
            orderToRequest = msg.getContent();
        }

        @Override
        protected void onTick() {
            orderText = Order.gson.fromJson(orderToRequest, Order.class).getTextOfOrder();
            System.out.println("SalesMarketAgent: Sending an order to SellingAgent to get " + orderText);

            String requestedAction = "Order";
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.setConversationId(requestedAction);
            msg.addReceiver(new AID(("AgentSelling"), AID.ISLOCALNAME));
            msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
            msg.setReplyByDate(new Date(System.currentTimeMillis() + 10000));
            msg.setContent(orderToRequest);

            addBehaviour(new RequestToOrder(myAgent, msg));
        }

        @Override
        public void stop() {

            orderText = Order.gson.fromJson(orderToRequest, Order.class).getTextOfOrder();

            System.out.println("SalesMarketAgent: Now I know that " + orderText + " is in warehouse");
            super.stop();
        }

        class RequestToOrder extends AchieveREInitiator {

            /**
             * 
             */
            private static final long serialVersionUID = -6945741747877024833L;

            public RequestToOrder(Agent a, ACLMessage msg) {
                super(a, msg);
            }

            @Override
            protected void handleInform(ACLMessage inform) {

                orderText = Order.gson.fromJson(inform.getContent(), Order.class).getTextOfOrder();

                System.out.println("SalesMarketAgent: received [inform] " + orderText + " is in warehouse");
                stop();

                addBehaviour(new GetFromWarehouse(myAgent, 2000, inform));
            }

            @Override
            protected void handleFailure(ACLMessage failure) {

                orderText = Order.gson.fromJson(failure.getContent(), Order.class).getTextOfOrder();

                System.out.println("SalesMarketAgent: received [failure] " + orderText + " is not in warehouse");
                // TODO: may cause infinite loop
                // stop();
            }
        }
    }

    class GetFromWarehouse extends TickerBehaviour {

        /**
         * 
         */
        private static final long serialVersionUID = 4233055394916376580L;
        private String orderToTake;
        private String orderText;

        public GetFromWarehouse(Agent a, long period, ACLMessage msg) {
            super(a, period);
            orderToTake = msg.getContent();
        }

        @Override
        protected void onTick() {

            orderText = Order.gson.fromJson(orderToTake, Order.class).getTextOfOrder();

            System.out.println("SalesMarketAgent: Asking SellingAgent to take " + orderText + " from warehouse");

            String requestedAction = "Take";
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.setConversationId(requestedAction);
            msg.addReceiver(new AID(("AgentSelling"), AID.ISLOCALNAME));
            msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
            msg.setReplyByDate(new Date(System.currentTimeMillis() + 10000));
            msg.setContent(orderToTake);

            addBehaviour(new RequestToTake(myAgent, msg));
        }

        @Override
        public void stop() {

            orderText = Order.gson.fromJson(orderToTake, Order.class).getTextOfOrder();

            System.out.println("SalesMarketAgent: Now I have a " + orderText);
            super.stop();
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

                orderText = Order.gson.fromJson(inform.getContent(), Order.class).getTextOfOrder();

                System.out
                        .println("SalesMarketAgent: received [inform] " + orderText + " will be taken from warehouse");
                stop();
            }

            @Override
            protected void handleFailure(ACLMessage failure) {

                orderText = Order.gson.fromJson(failure.getContent(), Order.class).getTextOfOrder();

                System.out.println(
                        "SalesMarketAgent: received [failure] " + orderText + " will not be taken from warehouse");
                // TODO: may cause infinite loop
                // stop();
            }
        }
    }
}
