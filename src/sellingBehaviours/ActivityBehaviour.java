package sellingBehaviours;

import jade.core.behaviours.DataStore;
import jade.core.behaviours.SequentialBehaviour;

public class ActivityBehaviour extends SequentialBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = 5504974627813962693L;

    public ActivityBehaviour(SellingResponder interactionBehaviour, DataStore dataStore) {
        super(interactionBehaviour.getAgent());

        addSubBehaviour(new AskBehaviour(interactionBehaviour, dataStore));
        addSubBehaviour(new SellingDeadlineBehaviour(interactionBehaviour, dataStore));
    }

}
