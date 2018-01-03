package procurementBehaviours;

import basicClasses.Order;
import interactors.OrderDataStore;
import jade.core.behaviours.OneShotBehaviour;

public class AskForAuction extends OneShotBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = -4435715111290249737L;
    private String materialsToRequest;
    private String orderText;
    private OrderDataStore dataStore;
    private ProcurementResponder interactionBehaviour;

    public AskForAuction(ProcurementResponder interactionBehaviour, OrderDataStore dataStore) {
        super(interactionBehaviour.getAgent());
        this.interactionBehaviour = interactionBehaviour;
        this.dataStore = dataStore;
    }

    @Override
    public void action() {
        materialsToRequest = interactionBehaviour.getRequest().getContent();
        orderText = Order.gson.fromJson(materialsToRequest, Order.class).getTextOfOrder();
//        dataStore.setRequestMessage(interactionBehaviour.getRequest());
        System.out.println("ProductionAgent: Asking ProcurementAgent to get materials for " + orderText);
        myAgent.addBehaviour(new AskForAuctionInitiatorBehaviour(interactionBehaviour, dataStore));
    }
}
