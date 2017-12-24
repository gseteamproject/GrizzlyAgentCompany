package productionBehaviours;

import interactors.OrderDataStore;
import interactors.RequestResult;
import jade.lang.acl.ACLMessage;

public class ProductionRequestResult extends RequestResult {

    public ProductionRequestResult(OrderDataStore dataStore) {
        super(dataStore);
    }

    @Override
    public ACLMessage execute(ACLMessage request) {
        ACLMessage response = request.createReply();
        response.setContent(request.getContent());

        if (DeliverToSellingBehaviour.isProduced) {
            response.setPerformative(ACLMessage.INFORM);
        } else {
            response.setPerformative(ACLMessage.FAILURE);
        }
        
        return response;
    }
}
