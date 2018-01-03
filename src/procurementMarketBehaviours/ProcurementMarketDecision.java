package procurementMarketBehaviours;

import basicClasses.Order;
import communication.MessageObject;
import interactors.Decision;
import interactors.OrderDataStore;
import jade.lang.acl.ACLMessage;

public class ProcurementMarketDecision extends Decision {
    private String orderText;
    private MessageObject msgObj;

    public ProcurementMarketDecision(OrderDataStore dataStore) {
        super(dataStore);
        // TODO Auto-generated constructor stub
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
        
        return response;
    }
}
