package procurementBehaviours;

import interactors.OrderDataStore;
import jade.core.behaviours.SequentialBehaviour;

public class ProcurementActivityBehaviour extends SequentialBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = -6703040253614653144L;

    public ProcurementActivityBehaviour(ProcurementResponder interactionBehaviour, OrderDataStore dataStore) {
        super(interactionBehaviour.getAgent());

        addSubBehaviour(new ProcurementAskBehaviour(interactionBehaviour, dataStore));
        addSubBehaviour(new ProcurementDeadlineBehaviour(interactionBehaviour, dataStore));
    }

}
