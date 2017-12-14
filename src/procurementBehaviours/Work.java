package procurementBehaviours;

import java.net.Authenticator.RequestorType;

import org.eclipse.jetty.http.MetaData.Request;

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
        if (RequestToBuy.buyCount == AskForAuction.partsCount) {
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
