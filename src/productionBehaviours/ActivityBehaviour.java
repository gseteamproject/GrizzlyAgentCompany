package productionBehaviours;

import jade.core.behaviours.DataStore;
import jade.core.behaviours.SequentialBehaviour;

public class ActivityBehaviour extends SequentialBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = -6703040253614653144L;

    public ActivityBehaviour(ProductionResponder interactionBehaviour, DataStore dataStore) {
        super(interactionBehaviour.getAgent());

        addSubBehaviour(new AskBehaviour(interactionBehaviour, dataStore));
        addSubBehaviour(new DeadlineBehaviour(interactionBehaviour, dataStore));
    }

}
