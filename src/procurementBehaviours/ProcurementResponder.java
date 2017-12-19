package procurementBehaviours;

import interactors.ResponderBehaviour;
import jade.core.Agent;
import jade.core.behaviours.DataStore;
import jade.lang.acl.MessageTemplate;

public class ProcurementResponder extends ResponderBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = -5804509731381843266L;

    public ProcurementResponder(Agent a, MessageTemplate mt, DataStore dataStore) {
        super(a, mt, dataStore);

        registerHandleRequest(new ProcurementDecisionBehaviour(this, dataStore));
        registerPrepareResultNotification(new ActivityBehaviour(this, dataStore));
    }
}