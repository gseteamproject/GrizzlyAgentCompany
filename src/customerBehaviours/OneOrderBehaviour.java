package customerBehaviours;

import basicAgents.Procurement;
import basicAgents.SalesMarket;
import basicAgents.Selling;
import basicClasses.Order;
import basicClasses.Paint;
import basicClasses.Product;
import basicClasses.Stone;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;

public class OneOrderBehaviour extends WakerBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = 3327849748177688933L;

    public OneOrderBehaviour(Agent a, long timeout) {
        super(a, timeout);
    }

    @Override
    public void onWake() {
        // THIS MESSAGE IS FOR TESTING
        ACLMessage testMsg = new ACLMessage(ACLMessage.REQUEST);
        testMsg.setConversationId("Order");
        testMsg.addReceiver(new AID(("AgentSalesMarket"), AID.ISLOCALNAME));
        testMsg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);

        // improvised customer
//         testMsg.setSender(new AID(("Customer"), AID.ISLOCALNAME));

        // it is an example of order
        Order order = new Order();

        order.id = SalesMarket.orderQueue.size() + 1;

        order.addProduct(new Product(10, "red"), 1);
        order.addProduct(new Product(10, "blue"), 2);
        order.addProduct(new Product(10, "green"), 3);
        
        order.deadline = 60000; // 60 seconds
        order.price = 100;
        order.agent = getAgent().getLocalName();

        String testGson = Order.gson.toJson(order);
        System.out.println(testGson);
        // {"id":1,"orderList":[{"product":{"stone":{"size":10.0,"price":0},"paint":{"color":"red","price":0},"price":0},"amount":1},{"product":{"stone":{"size":10.0,"price":0},"paint":{"color":"blue","price":0},"price":0},"amount":2},{"product":{"stone":{"size":10.0,"price":0},"paint":{"color":"green","price":0},"price":0},"amount":3}],"deadline":60000,"price":100}

        testMsg.setContent(testGson);
        myAgent.send(testMsg);

        /**
         * THIS IS REALLY ONLY FOR TESTING
         */
        // adding stone to warehouse and storage
        Paint paint = new Paint("red");
        Stone stone = new Stone(10);
        Product prdct = new Product(stone, paint);
        Selling.warehouse.add(prdct);

        paint = new Paint("blue");
        stone = new Stone(10);
        prdct = new Product(stone, paint);
        Selling.warehouse.add(prdct);
        // Procurement.materialStorage.add(paint);
        // Procurement.materialStorage.add(stone);

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
        // 3 green stones will be produced
    }
}