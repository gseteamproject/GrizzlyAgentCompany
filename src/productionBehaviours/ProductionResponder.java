package productionBehaviours;

import interactors.ResponderBehaviour;
import jade.core.Agent;
import jade.core.behaviours.DataStore;
import jade.lang.acl.MessageTemplate;

public class ProductionResponder extends ResponderBehaviour {

    public ProductionResponder(Agent a, MessageTemplate mt, DataStore dataStore) {
        super(a, mt, dataStore);

        registerHandleRequest(new ProductionDecisionBehaviour(this, dataStore));
        registerPrepareResultNotification(new ActivityBehaviour(this, dataStore));
    }

    private static final long serialVersionUID = -5695904570705958678L;
}
