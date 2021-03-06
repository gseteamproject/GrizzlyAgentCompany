package salesMarketBehaviours;

import basicClasses.Order;
import communication.Communication;
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

    public TakeFromWarehouseBehaviour(SalesMarketResponder interactionBehaviour, OrderDataStore dataStore) {
        super(interactionBehaviour.getAgent());
        this.dataStore = dataStore;
        this.interactionBehaviour = interactionBehaviour;
    }

    @Override
    public void action() {

        orderToTake = dataStore.getRequestMessage().getContent();
//        orderToTake = interactionBehaviour.getRequest().getContent();
        orderText = Order.gson.fromJson(orderToTake, Order.class).getTextOfOrder();
        dataStore.setRequestMessage(dataStore.getRequestMessage());
//        dataStore.setRequestMessage(interactionBehaviour.getRequest());

        msgObj = new MessageObject("AgentSalesMarket", orderText);
        Communication.server.sendMessageToClient(msgObj);
        /*
         * System.out.println(msgObj.getReceivedMessage());
         */
        myAgent.addBehaviour(new TakeFromWarehouseInitiatorBehaviour(interactionBehaviour, dataStore));
    }
}
