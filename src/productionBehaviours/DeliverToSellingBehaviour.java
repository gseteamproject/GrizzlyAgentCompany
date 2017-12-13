package productionBehaviours;

import basicAgents.Selling;
import basicClasses.Order;
import basicClasses.OrderPart;
import basicClasses.Product;
import jade.core.behaviours.DataStore;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

class DeliverToSellingBehaviour extends OneShotBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = 313682933400751868L;
    private String orderToGive;
    private String orderText;
    private ACLMessage requestMessage;
    public static boolean isProduced = false;
    private ProductionResponder interactionBehaviour;
    private Work interactor;

    public DeliverToSellingBehaviour(ProductionResponder interactionBehaviour, DataStore dataStore) {
        super(interactionBehaviour.getAgent());
        this.interactor = new Work(dataStore);
        this.interactionBehaviour = interactionBehaviour;
        requestMessage = interactionBehaviour.getRequest();
        orderToGive = requestMessage.getContent();
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
        // System.out.println(requestMessage.getContent());
        // System.out.println(interactor.execute(requestMessage));
        // interactionBehaviour.setResult(interactor.execute(requestMessage));
    }
}