package productionBehaviours;

import basicClasses.Order;
import communication.Communication;
import communication.MessageObject;
import interactors.Decision;
import interactors.OrderDataStore;
import jade.core.AID;
import jade.lang.acl.ACLMessage;



public class ProductionDecision extends Decision {

    private MessageObject msgObj;

    public ProductionDecision(OrderDataStore dataStore) {
        super(dataStore);


    }

    @Override
    public ACLMessage execute(ACLMessage request) {
        String orderText = Order.gson.fromJson(request.getContent(), Order.class).getTextOfOrder();

        msgObj = new MessageObject(request, orderText);
        Communication.server.sendMessageToClient(msgObj);

     /*   System.out.println("ProductionAgent: [request] SellingAgent asks to produce " + orderText);*/
        // Agent should send agree or refuse
        // TODO: Add refuse answer (some conditions should be added)

        ACLMessage response = request.createReply();
        response.setContent(request.getContent());
        response.setPerformative(ACLMessage.AGREE);
        response.setSender(new AID(("AgentProduction"), AID.ISLOCALNAME));

        msgObj = new MessageObject("AgentProduction", orderText + " will be produced");
        Communication.server.sendMessageToClient(msgObj);
/*
        System.out.println("ProductionAgent: [agree] I will produce " + orderText);
*/

        return response;
    }

}
