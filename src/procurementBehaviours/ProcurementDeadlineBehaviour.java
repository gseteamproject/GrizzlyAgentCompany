package procurementBehaviours;

import interactors.DeadlineBehaviour;
import interactors.OrderDataStore;

public class ProcurementDeadlineBehaviour extends DeadlineBehaviour {

    public ProcurementDeadlineBehaviour(ProcurementResponder interactionBehaviour, ProcurementRequestResult interactor,
            OrderDataStore dataStore) {
        super(interactionBehaviour, 6000);
        this.interactor = interactor;
    }

    private static final long serialVersionUID = 9050743659839198854L;
}