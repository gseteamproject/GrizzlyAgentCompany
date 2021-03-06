package productionBehaviours;

import basicAgents.SalesMarket;
import basicClasses.Order;
import interactors.AskBehaviour;
import interactors.OrderDataStore;

public class ProductionAskBehaviour extends AskBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = -4443443755165652310L;

    public ProductionAskBehaviour(ProductionResponder interactionBehaviour, ProductionRequestResult interactor,
            OrderDataStore dataStore) {
        super(interactionBehaviour, interactor, dataStore);
    }

    @Override
    public void action() {
        if (!this.isStarted) {
            Order order = Order.gson.fromJson(interactionBehaviour.getRequest().getContent(), Order.class);

            SalesMarket.orderQueue.get(order.searchInList(SalesMarket.orderQueue)).agent = interactionBehaviour.getAgent().getLocalName();
            
            myAgent.addBehaviour(new ProductionActivityBehaviour((ProductionResponder) interactionBehaviour, (ProductionRequestResult) interactor, dataStore));
//            myAgent.addBehaviour(new AskForMaterialsBehaviour((ProductionResponder) interactionBehaviour, dataStore));
            }
        this.isStarted = true;
    }
}
