package salesMarketBehaviours;

import basicAgents.SalesMarket;
import basicClasses.Order;
import interactors.OrderDataStore;
import jade.core.behaviours.SimpleBehaviour;

public class SalesMarketAskBehaviour extends SimpleBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = 974888961701937616L;

    private OrderDataStore dataStore;
    private SalesMarketResponder interactionBehaviour;
    SalesMarketRequestResult interactor;

    public SalesMarketAskBehaviour(SalesMarketResponder interactionBehaviour, OrderDataStore dataStore) {
        this.interactionBehaviour = interactionBehaviour;
        this.dataStore = dataStore;
        this.interactor = new SalesMarketRequestResult(dataStore);
    }

    @Override
    public void action() {
        Order order = Order.gson.fromJson(interactionBehaviour.getRequest().getContent(), Order.class);
        if (!SalesMarket.orderQueue.contains(order)) {
            SalesMarket.orderQueue.add(order);

            // if agent agrees it starts executing request
            myAgent.addBehaviour(new AskForOrderBehaviour(interactionBehaviour, dataStore));
        }
    }

    @Override
    public boolean done() {
        return true;
    }

}
