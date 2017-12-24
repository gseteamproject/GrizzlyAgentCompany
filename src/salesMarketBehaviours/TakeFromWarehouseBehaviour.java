package salesMarketBehaviours;

import basicClasses.Order;
import communication.MessageObject;
import interactors.OrderDataStore;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class TakeFromWarehouseBehaviour extends OneShotBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = 4233055394916376580L;
    private String orderToTake;
    private String orderText;
    private OrderDataStore dataStore;
    private SalesMarketResponder interactionBehaviour;
    private MessageObject msgObj;

    public TakeFromWarehouseBehaviour(SalesMarketResponder interactionBehaviour, ACLMessage msg,
            OrderDataStore dataStore) {
        super(interactionBehaviour.getAgent());
        this.interactionBehaviour = interactionBehaviour;
        this.dataStore = dataStore;
        orderToTake = msg.getContent();
        orderText = Order.gson.fromJson(orderToTake, Order.class).getTextOfOrder();
        msgObj = new MessageObject(msg, orderText);
        this.dataStore.setRequestMessage(msg);
    }

    @Override
    public void action() {
        System.out.println(msgObj.getReceivedMessage());
        myAgent.addBehaviour(new TakeFromWarehouseInitiatorBehaviour(interactionBehaviour, dataStore));
    }
}
