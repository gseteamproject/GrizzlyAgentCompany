package productionBehaviours;

import jade.core.Agent;
import jade.core.behaviours.DataStore;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;

public class ProductionResponder extends AchieveREResponder {

    /**
     * 
     */
    private static final long serialVersionUID = -5695904570705958678L;

    public ProductionResponder(Agent a, MessageTemplate mt, DataStore dataStore) {
        super(a, mt, dataStore);

        registerHandleRequest(new DecisionBehaviour(this, dataStore));
        registerPrepareResultNotification(new ActivityBehaviour(this, dataStore));
    }

    public ACLMessage getRequest() {
        return (ACLMessage) getDataStore().get(REQUEST_KEY);
    }

    public void setResponse(ACLMessage response) {
        getDataStore().put(RESPONSE_KEY, response);
    }

    public void setResult(ACLMessage result) {
        getDataStore().put(RESULT_NOTIFICATION_KEY, result);
    }
}
