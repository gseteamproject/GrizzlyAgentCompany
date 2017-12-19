package sellingBehaviours;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import basicAgents.Selling;
import basicClasses.Order;
import basicClasses.OrderPart;
import basicClasses.Product;
import communication.Communication;
import communication.MessageObject;
import jade.core.Agent;
import jade.core.behaviours.DataStore;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class GiveProductToMarketBehaviour extends OneShotBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = -6498277261596869382L;
    private String orderToGive;
    private DataStore dataStore;
    private SellingResponder interactionBehaviour;
    SellingRequestResult interactor;
    private MessageObject msgObj;

    public GiveProductToMarketBehaviour(SellingResponder interactionBehaviour, DataStore dataStore) {
        super(interactionBehaviour.getAgent());
        orderToGive = interactionBehaviour.getRequest().getContent();
        this.interactionBehaviour = interactionBehaviour;
        this.dataStore = dataStore;
        this.interactor = new SellingRequestResult(dataStore);
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