package productionBehaviours;

import basicClasses.Order;
import interactors.OrderDataStore;
import jade.core.behaviours.OneShotBehaviour;

public class TakeFromStorageBehaviour extends OneShotBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = 6717167573013445327L;
    private String materialsToTake;
    private String orderText;
    private OrderDataStore dataStore;
    private ProductionResponder interactionBehaviour;

    public TakeFromStorageBehaviour(ProductionResponder interactionBehaviour, OrderDataStore dataStore) {
        super(interactionBehaviour.getAgent());
        this.dataStore = dataStore;
        this.interactionBehaviour = interactionBehaviour;
    }

    @Override
    public void action() {
        materialsToTake = interactionBehaviour.getRequest().getContent();
        orderText = Order.gson.fromJson(materialsToTake, Order.class).getTextOfOrder();
        dataStore.setRequestMessage(interactionBehaviour.getRequest());
        System.out.println("ProductionAgent: Asking ProcurementAgent to take materials for " + orderText
                + " from materialStorage");
        myAgent.addBehaviour(new TakeFromStorageInitiatorBehaviour(interactionBehaviour, dataStore));
    }
}