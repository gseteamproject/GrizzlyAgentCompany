package sellingBehaviours;

import basicClasses.Order;
import communication.Communication;
import communication.MessageObject;
import interactors.AskBehaviour;
import interactors.OrderDataStore;
import jade.lang.acl.ACLMessage;

public class SellingAskBehaviour extends AskBehaviour {

    private static final long serialVersionUID = -4443443755165652310L;
    private MessageObject msgObj;

    public SellingAskBehaviour(SellingResponder interactionBehaviour, SellingRequestResult interactor,
            OrderDataStore dataStore) {
        super(interactionBehaviour, interactor, dataStore);
    }

    @Override
    public void action() {
        ACLMessage request = interactionBehaviour.getRequest();
        String orderText = Order.gson.fromJson(request.getContent(), Order.class).getTextOfOrder();
        if (request.getConversationId() == "Ask") {
            if (!this.isStarted) {
                this.interactor.isDone = false;
                msgObj = new MessageObject(request, orderText);
                Communication.server.sendMessageToClient(msgObj);

                /*System.out.println(msgObj.getReceivedMessage());
                Communication.server.sendMessageToClient("SellingAgent",
                        "[agree] I will check warehouse for " + orderText);*/
                myAgent.addBehaviour(new CheckWarehouseBehaviour((SellingResponder) interactionBehaviour, dataStore));
            }
            this.isStarted = true;
        } else if (request.getConversationId() == "Take") {

            if (this.isStarted) {
                this.interactor.isDone = false;
                msgObj = new MessageObject(request, orderText);
                Communication.server.sendMessageToClient(msgObj);

                /*System.out.println(msgObj.getReceivedMessage());
                Communication.server.sendMessageToClient("SellingAgent",
                        "[agree] I will give you " + orderText + " from warehouse");*/
                myAgent.addBehaviour(
                        new GiveProductToMarketBehaviour((SellingResponder) interactionBehaviour, dataStore));
            }
            this.isStarted = false;
        }
    }
}
