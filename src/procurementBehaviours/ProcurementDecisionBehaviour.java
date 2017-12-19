package procurementBehaviours;

import interactors.DecisionBehaviour;
import jade.core.behaviours.DataStore;

public class ProcurementDecisionBehaviour extends DecisionBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = -9182852661127360232L;

    public ProcurementDecisionBehaviour(ProcurementResponder interactionBehaviour, DataStore dataStore) {
        super(interactionBehaviour);
        this.interactor = new ProcurementDecision(dataStore);
    }
}