package procurementMarketBehaviours;

import interactors.DecisionBehaviour;
import interactors.OrderDataStore;
import interactors.ResponderBehaviour;

public class ProcurementMarketDecisionBehaviour extends DecisionBehaviour {

    public ProcurementMarketDecisionBehaviour(ResponderBehaviour interactionBehaviour, OrderDataStore dataStore) {
        super(interactionBehaviour);
        this.interactor = new ProcurementMarketDecision(dataStore);
    }

    /**
     * 
     */
    private static final long serialVersionUID = -7653878670883427299L;

}
