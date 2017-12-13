package productionBehaviours;

import jade.core.behaviours.DataStore;
import jade.lang.acl.ACLMessage;

public class Work {
    protected DataStore dataStore;

    public Work(DataStore dataStore) {
        super();
        this.dataStore = dataStore;
    }

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

    public boolean done() {
        return true;
    }
}
