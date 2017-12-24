package productionBehaviours;

import basicClasses.Order;
import interactors.OrderDataStore;
import jade.core.behaviours.OneShotBehaviour;

public class AskForMaterialsBehaviour extends OneShotBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = 8495802171064457305L;
    private String materialsToRequest;
    private String orderText;
    private OrderDataStore dataStore;
    private ProductionResponder interactionBehaviour;

    public AskForMaterialsBehaviour(ProductionResponder interactionBehaviour, OrderDataStore dataStore) {
        super(interactionBehaviour.getAgent());
        this.interactionBehaviour = interactionBehaviour;
        this.dataStore = dataStore;
    }

    @Override
    public void action() {
        materialsToRequest = interactionBehaviour.getRequest().getContent();
        orderText = Order.gson.fromJson(materialsToRequest, Order.class).getTextOfOrder();
        dataStore.setRequestMessage(interactionBehaviour.getRequest());
        System.out.println("ProductionAgent: Asking ProcurementAgent to get materials for " + orderText);
        myAgent.addBehaviour(new AskForMaterialsInitiatorBehaviour(interactionBehaviour, dataStore));
    }
}
