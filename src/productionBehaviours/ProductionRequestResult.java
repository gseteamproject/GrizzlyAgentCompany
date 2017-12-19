package productionBehaviours;

import interactors.RequestResult;
import jade.core.behaviours.DataStore;
import jade.lang.acl.ACLMessage;

public class ProductionRequestResult extends RequestResult {

    public ProductionRequestResult(DataStore dataStore) {
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
