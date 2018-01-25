package salesMarketBehaviours;

import basicAgents.SalesMarket;
import basicClasses.Order;
import communication.Communication;
import communication.MessageObject;
import interactors.Decision;
import interactors.OrderDataStore;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class SalesMarketDecision extends Decision {

    public SalesMarketDecision(OrderDataStore dataStore) {
        super(dataStore);
    }

    @Override
    public ACLMessage execute(ACLMessage request) {
        System.out.println("request " + request.getContent());
        Order order = Order.gson.fromJson(request.getContent(), Order.class);
        String orderText = order.getTextOfOrder();

        MessageObject msgObj = new MessageObject(request, orderText);
        Communication.server.sendMessageToClient(msgObj);
        Communication.server.sendJson(request, "lol", "Procurement", "Selling");
        Communication.server.sendJson(request, "halz maul ohren", "Selling", "Sales Market");

        System.out.println(msgObj.getReceivedMessage());

        // Agent should send agree or refuse
        ACLMessage response = request.createReply();
        response.setContent(request.getContent());
        response.setSender(new AID(("AgentSalesMarket"), AID.ISLOCALNAME));
        if (!SalesMarket.orderQueue.contains(order)) {
            response.setPerformative(ACLMessage.AGREE);
            msgObj = new MessageObject(response, orderText);
            System.out.println(msgObj.getReceivedMessage());
        } else {
            response.setPerformative(ACLMessage.REFUSE);
            msgObj = new MessageObject(response, orderText);
            System.out.println(msgObj.getReceivedMessage());
        }
        return response;
    }
}
