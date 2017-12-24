package sellingBehaviours;

import interactors.DeadlineBehaviour;
import interactors.OrderDataStore;

public class SellingDeadlineBehaviour extends DeadlineBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = 3277018524589680071L;

    public SellingDeadlineBehaviour(SellingResponder interactionBehaviour, OrderDataStore dataStore) {
        super(interactionBehaviour, 8000);
        this.interactor = new SellingRequestResult(dataStore);
    }
}
