package salesMarketBehaviours;

import basicAgents.SalesMarket;
import basicClasses.Order;
import interactors.AskBehaviour;
import interactors.OrderDataStore;

public class SalesMarketAskBehaviour extends AskBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = 974888961701937616L;

    public SalesMarketAskBehaviour(SalesMarketResponder interactionBehaviour, SalesMarketRequestResult interactor,
            OrderDataStore dataStore) {
        super(interactionBehaviour, interactor, dataStore);
    }

    @Override
    public void action() {
        if (!this.isStarted) {
            Order order = Order.gson.fromJson(interactionBehaviour.getRequest().getContent(), Order.class);
            if (!SalesMarket.orderQueue.contains(order)) {
                SalesMarket.orderQueue.add(order);

                // if agent agrees it starts executing request
                myAgent.addBehaviour(new AskForOrderBehaviour((SalesMarketResponder) interactionBehaviour, dataStore));
            }
            this.isStarted = true;
        }
    }
}
