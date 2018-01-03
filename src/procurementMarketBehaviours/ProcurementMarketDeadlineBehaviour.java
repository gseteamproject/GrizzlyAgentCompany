package procurementMarketBehaviours;

import interactors.DeadlineBehaviour;
import interactors.OrderDataStore;

public class ProcurementMarketDeadlineBehaviour extends DeadlineBehaviour {

    public ProcurementMarketDeadlineBehaviour(ProcurementMarketResponder interactionBehaviour,
            ProcurementMarketRequestResult interactor, OrderDataStore dataStore) {
        super(interactionBehaviour, 4000);
        this.interactor = interactor;
    }

    private static final long serialVersionUID = -736742834731478933L;
}
