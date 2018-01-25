package sellingBehaviours;

import basicClasses.Order;
import communication.Communication;
import communication.MessageObject;
import interactors.Decision;
import interactors.OrderDataStore;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class SellingDecision extends Decision {

    public SellingDecision(OrderDataStore dataStore) {
        super(dataStore);
    }

    @Override
    public ACLMessage execute(ACLMessage request) {
        // Selling reacts on SalesMarket's request

        String orderText = Order.gson.fromJson(request.getContent(), Order.class).getTextOfOrder();
        MessageObject msgObj = new MessageObject(request, orderText);
        // Agent should send agree or refuse
        // TODO: Add refuse answer (some conditions should be added)

        ACLMessage response = request.createReply();
        response.setContent(request.getContent());
        response.setPerformative(ACLMessage.AGREE);
        response.setSender(new AID(("AgentSelling"), AID.ISLOCALNAME));

        // response.setPerformative(ACLMessage.REFUSE);

        /* System.out.println(msgObj.getReceivedMessage()); */

        if (request.getConversationId() == "Ask") {

            msgObj = new MessageObject(response, orderText);
            Communication.server.sendMessageToClient(msgObj);

            /*System.out.println(msgObj.getReceivedMessage());
            Communication.server.sendMessageToClient("SellingAgent", "[agree] I will check warehouse for " + orderText);*/
        } else if (request.getConversationId() == "Take") {

            msgObj = new MessageObject(response, orderText);
            Communication.server.sendMessageToClient(msgObj);

            /*System.out.println(msgObj.getReceivedMessage());
            Communication.server.sendMessageToClient("SellingAgent",
                    "[agree] I will give you " + orderText + " from warehouse");*/
        }
        
        return response;
    }
}
