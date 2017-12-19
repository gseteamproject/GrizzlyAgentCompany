package productionBehaviours;

import jade.core.behaviours.DataStore;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.SequentialBehaviour;

public class ActivityBehaviour extends SequentialBehaviour {
    // public class ActivityBehaviour extends ParallelBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = -6703040253614653144L;

    public ActivityBehaviour(ProductionResponder interactionBehaviour, DataStore dataStore) {
        super(interactionBehaviour.getAgent());
        // super(interactionBehaviour.getAgent(), WHEN_ANY);

        // TODO: Remove Ask
        // addSubBehaviour(new AskBehaviour(interactionBehaviour, dataStore));
        addSubBehaviour(new AskForMaterialBehaviour(interactionBehaviour, dataStore));
        addSubBehaviour(new ProductionDeadlineBehaviour(interactionBehaviour, dataStore));
    }

}
