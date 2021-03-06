package financesBehaviours;

import interactors.OrderDataStore;
import interactors.RequestResult;
import jade.lang.acl.ACLMessage;

public class FinancesRequestResult extends RequestResult {

    public FinancesRequestResult(OrderDataStore dataStore) {
        super(dataStore);
    }

    @Override
    public ACLMessage execute(ACLMessage request) {
        ACLMessage response = request.createReply();
        response.setContent(request.getContent());

        if (request.getConversationId() == "Order") {
//            if (Procurement.isInMaterialStorage) {
                response.setPerformative(ACLMessage.INFORM);
                this.isDone = true;
//            } else {
//                response.setPerformative(ACLMessage.FAILURE);
//                this.isDone = false;
//            }
        } else if (request.getConversationId() == "Materials") {
//            if (Procurement.isGiven) {
                response.setPerformative(ACLMessage.INFORM);
                this.isDone = true;
//            } else {
//                response.setPerformative(ACLMessage.FAILURE);
//                this.isDone = false;
//            }
        }

        return response;
    }
}
