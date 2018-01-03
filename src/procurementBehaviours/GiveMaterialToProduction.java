package procurementBehaviours;

import basicAgents.Procurement;
import basicClasses.Order;
import basicClasses.Paint;
import basicClasses.Product;
import basicClasses.Stone;
import interactors.OrderDataStore;
import jade.core.behaviours.OneShotBehaviour;

public class GiveMaterialToProduction extends OneShotBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = -1386982676634257780L;
    private ProcurementResponder interactionBehaviour;
    private ProcurementRequestResult interactor;
    private OrderDataStore dataStore;
    private String materialsToGive;
    private String orderText;

    public GiveMaterialToProduction(ProcurementResponder interactionBehaviour, OrderDataStore dataStore) {
        super(interactionBehaviour.getAgent());
        this.interactionBehaviour = interactionBehaviour;
        this.interactor = ProcurementActivityBehaviour.interactor;
        this.dataStore = dataStore;
        materialsToGive = interactionBehaviour.getRequest().getContent();
    }

    @Override
    public void action() {
        Order order = Order.gson.fromJson(materialsToGive, Order.class);
        orderText = order.getTextOfOrder();
        System.out.println("ProcurementAgent: Taking " + orderText + " from materialStorage");

        Procurement.isGiven = false;

        // TODO: get materials objects from message
        for (Product product : order.getProducts()) {
            Procurement.materialStorage.remove(new Paint(product.getColor()));
            Procurement.materialStorage.remove(new Stone(product.getSize()));
        }

        Procurement.isGiven = true;
        interactor.execute(interactionBehaviour.getRequest());
    }
}