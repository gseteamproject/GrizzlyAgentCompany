package salesMarketBehaviours;

import interactors.DeadlineBehaviour;
import interactors.OrderDataStore;

public class SalesMarketDeadlineBehaviour extends DeadlineBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = -7011771949303737555L;

    public SalesMarketDeadlineBehaviour(SalesMarketResponder interactionBehaviour, OrderDataStore dataStore) {
        super(interactionBehaviour, 18000);
        this.interactor = new SalesMarketRequestResult(dataStore);
    }
}
