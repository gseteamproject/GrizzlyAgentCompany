package sellingBehaviours;

import interactors.DeadlineBehaviour;
import jade.core.behaviours.DataStore;

public class SellingDeadlineBehaviour extends DeadlineBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = 3277018524589680071L;

    public SellingDeadlineBehaviour(SellingResponder interactionBehaviour, DataStore dataStore) {
        super(interactionBehaviour, 8000);
        this.interactionBehaviour = interactionBehaviour;
        this.interactor = new SellingRequestResult(dataStore);
    }
}
