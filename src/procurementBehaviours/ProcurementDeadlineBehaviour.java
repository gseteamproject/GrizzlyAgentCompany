package procurementBehaviours;

import interactors.DeadlineBehaviour;
import interactors.OrderDataStore;

public class ProcurementDeadlineBehaviour extends DeadlineBehaviour {

    public ProcurementDeadlineBehaviour(ProcurementResponder interactionBehaviour, OrderDataStore dataStore) {
        super(interactionBehaviour, 2000);
        this.interactor = new ProcurementRequestResult(dataStore);
    }

    private static final long serialVersionUID = 9050743659839198854L;
}