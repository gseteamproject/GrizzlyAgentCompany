package sellingBehaviours;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import basicAgents.Selling;
import basicClasses.Order;
import basicClasses.OrderPart;
import basicClasses.Product;
import communication.Communication;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class GiveProductToMarketBehaviour extends OneShotBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = -6498277261596869382L;
    private String orderToGive;

    public GiveProductToMarketBehaviour(Agent a, ACLMessage msg) {
        super(a);
        orderToGive = msg.getContent();
    }

    @Override
    public void action() {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Order order = gson.fromJson(orderToGive, Order.class);

        Selling.isTaken = false;

        int takeCount = 0;
        for (OrderPart orderPart : order.orderList) {
            Product productToGive = orderPart.getProduct();
            System.out.println("SellingAgent: Taking " + orderPart.getTextOfOrderPart() + " from warehouse");
            Communication.server.sendMessageToClient("SellingAgent",
                    "Taking " + orderPart.getTextOfOrderPart() + " from warehouse");
            Selling.warehouse.remove(productToGive);
            takeCount += 1;
        }
        if (takeCount == order.orderList.size()) {
            Selling.isTaken = true;
        }
    }
}