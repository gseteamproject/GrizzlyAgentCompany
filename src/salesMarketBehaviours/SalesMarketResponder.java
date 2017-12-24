package salesMarketBehaviours;

import interactors.OrderDataStore;
import interactors.ResponderBehaviour;
import jade.core.Agent;
import jade.lang.acl.MessageTemplate;

public class SalesMarketResponder extends ResponderBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = 7386418031416044376L;
    protected OrderDataStore dataStore;

    public SalesMarketResponder(Agent a, MessageTemplate mt, OrderDataStore dataStore) {
        super(a, mt, dataStore);

        registerHandleRequest(new SalesMarketDecisionBehaviour(this, dataStore));
        registerPrepareResultNotification(new SalesMarketActivityBehaviour(this, dataStore));
    }
}