package productionBehaviours;

import basicAgents.Selling;
import basicClasses.Order;
import basicClasses.OrderPart;
import basicClasses.Product;
import interactors.OrderDataStore;
import jade.core.behaviours.OneShotBehaviour;

class DeliverToSellingBehaviour extends OneShotBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = 313682933400751868L;
    private String orderToGive;
    private String orderText;
    private OrderDataStore dataStore;
    private ProductionResponder interactionBehaviour;
    public static boolean isProduced = false;

    public DeliverToSellingBehaviour(ProductionResponder interactionBehaviour, OrderDataStore dataStore) {
        super(interactionBehaviour.getAgent());
        this.dataStore = dataStore;
        this.interactionBehaviour = interactionBehaviour;
    }

    @Override
    public void action() {
        orderToGive = interactionBehaviour.getRequest().getContent();
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
    }
}