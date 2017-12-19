package sellingBehaviours;

import interactors.DecisionBehaviour;
import jade.core.behaviours.DataStore;

public class SellingDecisionBehaviour extends DecisionBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = 1860338194487186607L;

    public SellingDecisionBehaviour(SellingResponder interactionBehaviour, DataStore dataStore) {
        super(interactionBehaviour);
        this.interactor = new SellingDecision(dataStore);
    }
}
