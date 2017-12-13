package procurementBehaviours;

import basicClasses.Order;
import jade.core.behaviours.DataStore;
import jade.lang.acl.ACLMessage;

public class Decision {
    protected DataStore dataStore;
    private String orderText;

    public Decision(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public ACLMessage execute(ACLMessage request) {

        orderText = Order.gson.fromJson(request.getContent(), Order.class).getTextOfOrder();

        System.out.println("ProductionAgent: [request] SellingAgent asks to produce " + orderText);
        // Agent should send agree or refuse
        // TODO: Add refuse answer (some conditions should be added)
        ACLMessage response = request.createReply();
        response.setContent(request.getContent());
        response.setPerformative(ACLMessage.AGREE);
        System.out.println("ProductionAgent: [agree] I will produce " + orderText);

//        if (request.getConversationId() == "Materials") {
//            System.out.println("ProcurementAgent: [request] ProductionAgent asks for materials for " + orderText);
//            System.out.println("ProcurementAgent: [agree] I will check materialStorage for materials for " + orderText);
//            myAgent.addBehaviour(new CheckMaterialStorage(myAgent, agree));
//        } else if (request.getConversationId() == "Take") {
//            System.out.println("ProcurementAgent: [request] ProductionAgent wants to get materials for " + orderText
//                    + " from materialStorage");
//            System.out.println(
//                    "ProcurementAgent: [agree] I will give you materials for " + orderText + " from materialStorage");
//            myAgent.addBehaviour(new GiveMaterialToProduction(myAgent, agree));
//        }

        // if agent agrees it starts executing request
        // addBehaviour(new AskForMaterial(myAgent, 2000, agree));

        // registerPrepareResultNotification(new AskForMaterial(myAgent, 2000, agree));

        // content = operation name
        // String operationName = request.getContent();
        //
        // ACLMessage response = request.createReply();
        // if (dataStore.getMachine().willExecute(operationName)) {
        // response.setPerformative(ACLMessage.AGREE);
        //
        // } else {
        // response.setPerformative(ACLMessage.REFUSE);
        // }
        return response;
    }

}