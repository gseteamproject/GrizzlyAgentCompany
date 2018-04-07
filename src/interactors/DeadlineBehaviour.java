package interactors;

import basicAgents.SalesMarket;
import basicClasses.Order;
import communication.Server;
import jade.core.behaviours.WakerBehaviour;

public class DeadlineBehaviour extends WakerBehaviour {
    /**
     * 
     */
    private static final long serialVersionUID = 3660229129526762982L;

    protected ResponderBehaviour interactionBehaviour;
    protected OrderDataStore dataStore;
    protected RequestResult interactor;

    public DeadlineBehaviour(ResponderBehaviour interactionBehaviour, OrderDataStore dataStore) {
        super(interactionBehaviour.getAgent(), dataStore.getDeadline() * Server.delaytime / 150);
        this.interactionBehaviour = interactionBehaviour;
        this.dataStore = dataStore;
        System.out.println(
                "interactionBehaviour.getAgent().getLocalName() " + interactionBehaviour.getAgent().getLocalName());
    }

    @Override
    protected void onWake() {
        System.out.println("zdes' " + interactionBehaviour.getAgent().getLocalName());
        System.out.println(dataStore.getDeadline() * Server.delaytime / 150);
        Order order = Order.gson.fromJson(dataStore.getRequestMessage().getContent(), Order.class);
        System.out.println(SalesMarket.orderQueue);
        System.out.println(dataStore.getRequestMessage().getContent());
        if (order.searchInList(SalesMarket.orderQueue) > -1) {
            System.out.println("Deadline of " + interactionBehaviour.getAgent().getLocalName());
            interactionBehaviour.setResult(interactor.execute(interactionBehaviour.getRequest()));
        }
    }
}
