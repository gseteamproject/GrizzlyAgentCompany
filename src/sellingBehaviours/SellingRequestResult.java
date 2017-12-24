package sellingBehaviours;

import basicAgents.Selling;
import interactors.OrderDataStore;
import interactors.RequestResult;
import jade.lang.acl.ACLMessage;

public class SellingRequestResult extends RequestResult {

    public SellingRequestResult(OrderDataStore dataStore) {
        super(dataStore);
    }

    @Override
    public ACLMessage execute(ACLMessage request) {
        ACLMessage response = request.createReply();
        response.setContent(request.getContent());
        // TODO: Need to check if in warehouse here?
        if (request.getConversationId() == "Ask") {
            if (Selling.isInWarehouse) {
                response.setPerformative(ACLMessage.INFORM);
            } else {
                response.setPerformative(ACLMessage.FAILURE);
            }
        } else if (request.getConversationId() == "Take") {
            if (Selling.isTaken) {
                response.setPerformative(ACLMessage.INFORM);
            } else {
                response.setPerformative(ACLMessage.FAILURE);
            }
        }

        return response;
    }
}
