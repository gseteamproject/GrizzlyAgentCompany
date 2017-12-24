package sellingBehaviours;

import interactors.OrderDataStore;
import jade.core.behaviours.SequentialBehaviour;

public class SellingActivityBehaviour extends SequentialBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = 5504974627813962693L;

    public SellingActivityBehaviour(SellingResponder interactionBehaviour, OrderDataStore dataStore) {
        super(interactionBehaviour.getAgent());

        addSubBehaviour(new SellingAskBehaviour(interactionBehaviour, dataStore));
        addSubBehaviour(new SellingDeadlineBehaviour(interactionBehaviour, dataStore));
    }

}
