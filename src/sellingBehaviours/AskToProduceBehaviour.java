package sellingBehaviours;

import basicClasses.Order;
import communication.Communication;
import interactors.OrderDataStore;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class AskToProduceBehaviour extends OneShotBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = -6365251601845699295L;
    private String orderToProceed;
    private String orderText;
    private OrderDataStore dataStore;
    private SellingResponder interactionBehaviour;

    public AskToProduceBehaviour(SellingResponder interactionBehaviour, ACLMessage msg, OrderDataStore dataStore) {
        super(interactionBehaviour.getAgent());
        orderToProceed = msg.getContent();
        this.interactionBehaviour = interactionBehaviour;
        this.dataStore = dataStore;
        this.dataStore.setRequestMessage(msg);
    }

    @Override
    public void action() {
        orderText = Order.gson.fromJson(orderToProceed, Order.class).getTextOfOrder();
        System.out.println("SellingAgent: " + orderText + " is in production");
        Communication.server.sendMessageToClient("SellingAgent", orderText + " is in production");
        System.out.println("4" + interactionBehaviour.getRequest());
        myAgent.addBehaviour(new AskToProduceInitiatorBehaviour(interactionBehaviour, dataStore));
    }
}