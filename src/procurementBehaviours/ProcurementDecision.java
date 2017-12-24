package procurementBehaviours;

import basicClasses.Order;
import communication.MessageObject;
import interactors.Decision;
import interactors.OrderDataStore;
import jade.lang.acl.ACLMessage;

public class ProcurementDecision extends Decision {
    private String orderText;
    private MessageObject msgObj;

    public ProcurementDecision(OrderDataStore dataStore) {
        super(dataStore);
    }

    public ACLMessage execute(ACLMessage request) {
        orderText = Order.gson.fromJson(request.getContent(), Order.class).getTextOfOrder();

        // Agent should send agree or refuse
        // TODO: Add refuse answer (some conditions should be added)
        ACLMessage response = request.createReply();
        response.setContent(request.getContent());
        response.setPerformative(ACLMessage.AGREE);

        msgObj = new MessageObject(request, orderText);
        System.out.println(msgObj.getReceivedMessage());

        if (request.getConversationId() == "Materials") {
            System.out.println("ProcurementAgent: [request] ProductionAgent asks for materials for " + orderText);
            System.out.println("ProcurementAgent: [agree] I will check materialStorage for materials for " + orderText);
        } else if (request.getConversationId() == "Take") {
            System.out.println("ProcurementAgent: [request] ProductionAgent wants to get materials for " + orderText
                    + " from materialStorage");
            System.out.println(
                    "ProcurementAgent: [agree] I will give you materials for " + orderText + " from materialStorage");
        }

        return response;
    }

}