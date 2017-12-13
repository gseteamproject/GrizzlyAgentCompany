package basicAgents;

import java.util.ArrayList;
import java.util.List;

import basicClasses.Order;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.DataStore;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREInitiator;
import jade.proto.AchieveREResponder;
import salesMarketBehaviours.CustomerSimulatorBehaviour;
import salesMarketBehaviours.SalesMarketResponder;

public class SalesMarket extends Agent {

    /**
     * 
     */
    private static final long serialVersionUID = 2003110338808844985L;

    // creating list of orders
    public static List<Order> orderQueue = new ArrayList<Order>();
    protected DataStore dataStore;

    @Override
    protected void setup() {
        dataStore = new DataStore();
        MessageTemplate temp = MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
        MessageTemplate reqTemp = MessageTemplate.and(temp, MessageTemplate.MatchPerformative(ACLMessage.REQUEST));

        // adding behaviours
        addBehaviour(new SalesMarketResponder(this, reqTemp, dataStore));
        // addBehaviour(new WaitingSellingMessage(this, infTemp));

        // addBehaviour(new GenerateOrders(this, 15000));

        addBehaviour(new CustomerSimulatorBehaviour(this, 4000));
    }

    // class for generating random orders and adding them to the randomOrders-List
    // TODO delete not profitable orders and maybe delete orders after a specific
    // time
    // class GenerateOrdersBehaviour extends TickerBehaviour {
    //
    // /**
    // *
    // */
    // private static final long serialVersionUID = -7549190406155306008L;
    //
    // public GenerateOrdersBehaviour(Agent a, long period) {
    // super(a, period);
    // }
    //
    // @Override
    // protected void onTick() {
    // ACLMessage orderMsg = new ACLMessage(ACLMessage.REQUEST);
    // orderMsg.addReceiver(new AID(("AgentSalesMarket"), AID.ISLOCALNAME));
    // orderMsg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
    //
    // // improvised customer
    // orderMsg.setSender(new AID(("Customer"), AID.ISLOCALNAME));
    //
    // Order order = new Order();
    // order.id = orderQueue.size() + 1;
    //
    // Random rand = new Random();
    // int randSize, randAmount, randColI;
    // String randColS = "";
    // for (int i = 0; i < 3; i++) {
    // randColI = rand.nextInt(3);
    // switch (randColI) {
    // case 0:
    // randColS = "red";
    // break;
    // case 1:
    // randColS = "blue";
    // break;
    // case 2:
    // randColS = "green";
    // break;
    // default:
    // randColS = "other";
    // break;
    // }
    //
    // randSize = rand.nextInt(10) + 1;
    // randAmount = rand.nextInt(100) + 1;
    //
    // order.addProduct(new Product(randSize, randColS), randAmount);
    // }
    //
    // String testGson = Order.gson.toJson(order);
    // //
    // {"id":1,"orderList":[{"product":{"stone":{"size":10.0,"price":0},"paint":{"color":"blue","price":0},"price":0},"amount":2},{"product":{"stone":{"size":10.0,"price":0},"paint":{"color":"red","price":0},"price":0},"amount":2}]}
    //
    // orderMsg.setContent(testGson);
    // send(orderMsg);
    // }
    //
    // @Override
    // public void stop() {
    // super.stop();
    // }
    // }

    // class that sends test message with example of order. This simulates customer.
    // class CustomerSimulatorBehaviour extends WakerBehaviour {
    //
    // /**
    // *
    // */
    // private static final long serialVersionUID = 3327849748177688933L;
    //
    // public CustomerSimulatorBehaviour(Agent a, long timeout) {
    // super(a, timeout);
    // }
    //
    // @Override
    // public void onWake() {
    // // THIS MESSAGE IS FOR TESTING
    // ACLMessage testMsg = new ACLMessage(ACLMessage.REQUEST);
    // testMsg.addReceiver(new AID(("AgentSalesMarket"), AID.ISLOCALNAME));
    // testMsg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
    //
    // // improvised customer
    // testMsg.setSender(new AID(("Customer"), AID.ISLOCALNAME));
    //
    // // it is an example of order
    // Order order = new Order();
    // order.id = orderQueue.size() + 1;
    //
    // order.addProduct(new Product(10, "red"), 1);
    // order.addProduct(new Product(10, "blue"), 2);
    // order.addProduct(new Product(10, "green"), 6);
    //
    //
    // String testGson = Order.gson.toJson(order);
    // //
    // {"id":1,"orderList":[{"product":{"stone":{"size":10.0,"price":0},"paint":{"color":"blue","price":0},"price":0},"amount":2},{"product":{"stone":{"size":10.0,"price":0},"paint":{"color":"red","price":0},"price":0},"amount":2}]}
    //
    // testMsg.setContent(testGson);
    // send(testMsg);
    //
    // /**
    // * THIS IS REALLY ONLY FOR TESTING
    // */
    // // adding stone to warehouse and storage
    // Paint paint = new Paint("red");
    // Stone stone = new Stone(10);
    // Product prdct = new Product(stone, paint);
    // Selling.warehouse.add(prdct);
    //
    // paint = new Paint("blue");
    // stone = new Stone(10);
    // prdct = new Product(stone, paint);
    // Selling.warehouse.add(prdct);
    // Procurement.materialStorage.add(paint);
    // Procurement.materialStorage.add(stone);
    //
    // paint = new Paint("green");
    // stone = new Stone(10);
    // prdct = new Product(stone, paint);
    // Procurement.materialStorage.add(paint);
    // Procurement.materialStorage.add(stone);
    // Procurement.materialStorage.add(paint);
    // Procurement.materialStorage.add(stone);
    //
    // // That means:
    // // 1 red stone will be taken from warehouse
    // // 1 blue stone will be taken from warehouse
    // // 1 blue stone will be produced
    // // 2 green stone will be produced
    // }
    // }

    // this class waits for receiving a message with certain template
    // class WaitingCustomerMessageResponder extends AchieveREResponder {
    //
    // /**
    // *
    // */
    // private static final long serialVersionUID = 7386418031416044376L;
    // private String orderText;
    //
    // public WaitingCustomerMessageResponder(Agent a, MessageTemplate mt) {
    // super(a, mt);
    // }
    //
    // @Override
    // protected ACLMessage handleRequest(ACLMessage request) throws
    // NotUnderstoodException, RefuseException {
    // // Sales Market reacts on customer's request
    // System.out.println("request " + request.getContent());
    //
    // Order order = Order.gson.fromJson(request.getContent(), Order.class);
    // orderText = order.getTextOfOrder();
    //
    // System.out.println("SalesMarketAgent: [request] Customer orders a " +
    // orderText);
    //
    // // Agent should send agree or refuse
    // ACLMessage response;
    // response = request.createReply();
    // response.setContent(request.getContent());
    // if (!orderQueue.contains(order)) {
    // orderQueue.add(order);
    // response.setPerformative(ACLMessage.AGREE);
    // System.out.println("SalesMarketAgent: [agree] I will make an order of " +
    // orderText);
    //
    // // if agent agrees it starts executing request
    // addBehaviour(new AskForOrderBehaviour(myAgent, response));
    // } else {
    // response.setPerformative(ACLMessage.REFUSE);
    // System.out.println("SalesMarketAgent: [refuse] Following order is already in
    // queue: " + orderText);
    // }
    // return response;
    // }
    //
    // @Override
    // protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage
    // response)
    // throws FailureException {
    // orderText = Order.gson.fromJson(request.getContent(),
    // Order.class).getTextOfOrder();
    //
    // // result of request to sales market
    // // if agent agrees to request
    // // after executing, it should send failure of inform
    // ACLMessage inform = request.createReply();
    // inform.setContent(request.getContent());
    // inform.setPerformative(ACLMessage.INFORM);
    // System.out.println("SalesMarketAgent: [inform] I ordered a " + orderText);
    //
    // return inform;
    // }
    // }

    // class AskForOrderBehaviour extends OneShotBehaviour {
    //
    // /**
    // *
    // */
    // private static final long serialVersionUID = 8296971392230921846L;
    // private String orderToRequest;
    // private String orderText;
    //
    // public AskForOrderBehaviour(Agent a, ACLMessage msg) {
    // super(a);
    // orderToRequest = msg.getContent();
    // }
    //
    // @Override
    // public void action() {
    // orderText = Order.gson.fromJson(orderToRequest,
    // Order.class).getTextOfOrder();
    // System.out.println("SalesMarketAgent: Asking SellingAgent to get " +
    // orderText);
    //
    // String requestedAction = "Ask";
    // ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
    // msg.setConversationId(requestedAction);
    // msg.addReceiver(new AID(("AgentSelling"), AID.ISLOCALNAME));
    // msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
    // msg.setContent(orderToRequest);
    //
    // addBehaviour(new RequestToOrder(myAgent, msg));
    // }
    //
    // class RequestToOrder extends AchieveREInitiator {
    //
    // /**
    // *
    // */
    // private static final long serialVersionUID = -6945741747877024833L;
    //
    // public RequestToOrder(Agent a, ACLMessage msg) {
    // super(a, msg);
    // }
    //
    // @Override
    // protected void handleInform(ACLMessage inform) {
    // orderText = Order.gson.fromJson(inform.getContent(),
    // Order.class).getTextOfOrder();
    //
    // System.out.println("SalesMarketAgent: received [inform] " + orderText + " is
    // in warehouse");
    //
    // addBehaviour(new TakeFromWarehouseBehaviour(myAgent, inform));
    // }
    //
    // @Override
    // protected void handleFailure(ACLMessage failure) {
    // Order order = Order.gson.fromJson(failure.getContent(), Order.class);
    // orderText = order.getTextOfOrder();
    //
    // System.out.println("SalesMarketAgent: received [failure] " + orderText + " is
    // not in warehouse");
    //
    // // SalesMarket will wait
    // addBehaviour(new WaitingSellingMessageResponder(myAgent, infTemp));
    // }
    // }
    // }

    // this class waits for message that order is ready for collection
    // class WaitingSellingMessageResponder extends AchieveREResponder {
    //
    // /**
    // *
    // */
    // private static final long serialVersionUID = 7386418031416044376L;
    // private String orderText;
    //
    // public WaitingSellingMessageResponder(Agent a, MessageTemplate mt) {
    // super(a, mt);
    // }
    //
    // @Override
    // protected ACLMessage handleRequest(ACLMessage request) throws
    // NotUnderstoodException, RefuseException {
    // // Sales Market reacts on Selling' information
    // ACLMessage response;
    // Order order = Order.gson.fromJson(request.getContent(), Order.class);
    // orderText = order.getTextOfOrder();
    //
    // System.out.println("SalesMarketAgent: received [inform] order " + orderText
    // + " is ready for collection from warehouse");
    //
    // response = request.createReply();
    // response.setContent(request.getContent());
    // if (orderQueue.contains(order)) {
    // response.setPerformative(ACLMessage.AGREE);
    // System.out.println("SalesMarketAgent: [agree] I will take " + orderText);
    //
    // addBehaviour(new TakeFromWarehouseBehaviour(myAgent, request));
    //
    // } else {
    // response.setPerformative(ACLMessage.REFUSE);
    // System.out.println("SalesMarketAgent: [refuse] I will not take " +
    // orderText);
    // }
    // return response;
    // }
    //
    // @Override
    // protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage
    // response)
    // throws FailureException {
    //
    // orderText = Order.gson.fromJson(request.getContent(),
    // Order.class).getTextOfOrder();
    //
    // ACLMessage inform = request.createReply();
    // inform.setContent(request.getContent());
    // inform.setPerformative(ACLMessage.INFORM);
    // System.out.println("SalesMarketAgent: [inform] I asked to take " +
    // orderText);
    //
    // return inform;
    // }
    // }

    // class TakeFromWarehouseBehaviour extends OneShotBehaviour {
    //
    // /**
    // *
    // */
    // private static final long serialVersionUID = 4233055394916376580L;
    // private String orderToTake;
    // private String orderText;
    //
    // public TakeFromWarehouseBehaviour(Agent a, ACLMessage msg) {
    // super(a);
    // orderToTake = msg.getContent();
    // }
    //
    // @Override
    // public void action() {
    // orderText = Order.gson.fromJson(orderToTake, Order.class).getTextOfOrder();
    //
    // System.out.println("SalesMarketAgent: Asking SellingAgent to take " +
    // orderText + " from warehouse");
    //
    // String requestedAction = "Take";
    // ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
    // msg.setConversationId(requestedAction);
    // msg.addReceiver(new AID(("AgentSelling"), AID.ISLOCALNAME));
    // msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
    // msg.setContent(orderToTake);
    //
    // addBehaviour(new RequestToTake(myAgent, msg));
    // }
    //
    // class RequestToTake extends AchieveREInitiator {
    //
    // /**
    // *
    // */
    // private static final long serialVersionUID = -2624609588724924573L;
    //
    // public RequestToTake(Agent a, ACLMessage msg) {
    // super(a, msg);
    // }
    //
    // @Override
    // protected void handleInform(ACLMessage inform) {
    // Order order = Order.gson.fromJson(inform.getContent(), Order.class);
    // orderText = order.getTextOfOrder();
    //
    // System.out
    // .println("SalesMarketAgent: received [inform] " + orderText + " will be taken
    // from warehouse");
    // System.out.println("SalesMarketAgent: Now I have a " + orderText);
    // if (orderQueue.remove(order)) {
    // System.out.println("SalesMarketAgent: " + orderText + " is removed from
    // Orderqueue.");
    // }
    // }
    //
    // @Override
    // protected void handleFailure(ACLMessage failure) {
    // orderText = Order.gson.fromJson(failure.getContent(),
    // Order.class).getTextOfOrder();
    //
    // System.out.println(
    // "SalesMarketAgent: received [failure] " + orderText + " will not be taken
    // from warehouse");
    // }
    // }
    // }
}
