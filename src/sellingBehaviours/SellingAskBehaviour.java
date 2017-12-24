package sellingBehaviours;

import basicClasses.Order;
import communication.Communication;
import communication.MessageObject;
import interactors.OrderDataStore;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

public class SellingAskBehaviour extends SimpleBehaviour {

    private static final long serialVersionUID = -4443443755165652310L;
    private OrderDataStore dataStore;
    private SellingResponder interactionBehaviour;
    SellingRequestResult interactor;
    private MessageObject msgObj;

    public SellingAskBehaviour(SellingResponder interactionBehaviour, OrderDataStore dataStore) {
        this.interactionBehaviour = interactionBehaviour;
        this.dataStore = dataStore;
        this.interactor = new SellingRequestResult(dataStore);
    }

    @Override
    public void action() {
        ACLMessage request = interactionBehaviour.getRequest();
        String orderText = Order.gson.fromJson(request.getContent(), Order.class).getTextOfOrder();
        if (request.getConversationId() == "Ask") {

            System.out.println("1" + interactionBehaviour.getRequest());
            msgObj = new MessageObject(request, orderText);
            System.out.println(msgObj.getReceivedMessage());
            Communication.server.sendMessageToClient("SellingAgent", "[agree] I will check warehouse for " + orderText);
            myAgent.addBehaviour(new CheckWarehouseBehaviour(interactionBehaviour, dataStore));
        } else if (request.getConversationId() == "Take") {

            System.out.println("2" + interactionBehaviour.getRequest());
            msgObj = new MessageObject(request, orderText);
            System.out.println(msgObj.getReceivedMessage());
            Communication.server.sendMessageToClient("SellingAgent",
                    "[agree] I will give you " + orderText + " from warehouse");
            myAgent.addBehaviour(new GiveProductToMarketBehaviour(interactionBehaviour, dataStore));
        }
    }

    @Override
    public boolean done() {
        return true;
    }

}
