package procurementBehaviours;

import interactors.OrderDataStore;
import interactors.RequestResult;
import jade.lang.acl.ACLMessage;

public class ProcurementRequestResult extends RequestResult {

    public ProcurementRequestResult(OrderDataStore dataStore) {
        super(dataStore);
    }
    
    @Override
    public ACLMessage execute(ACLMessage request) {
        ACLMessage response = request.createReply();
        response.setContent(request.getContent());
        if (RequestToBuy.buyCount == AskForAuction.partsCount) {
            response.setPerformative(ACLMessage.INFORM);
        } else {
            response.setPerformative(ACLMessage.FAILURE);
        }

        return response;
    }
}
