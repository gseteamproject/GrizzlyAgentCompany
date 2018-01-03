package procurementBehaviours;

import interactors.OrderDataStore;
import jade.core.behaviours.ParallelBehaviour;

public class ProcurementActivityBehaviour extends ParallelBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = -6703040253614653144L;

    public static ProcurementRequestResult interactor;

    public ProcurementActivityBehaviour(ProcurementResponder interactionBehaviour, OrderDataStore dataStore) {
        super(interactionBehaviour.getAgent(), WHEN_ANY);
        interactor = new ProcurementRequestResult(dataStore);

        addSubBehaviour(new ProcurementAskBehaviour(interactionBehaviour, interactor, dataStore));
        addSubBehaviour(new ProcurementDeadlineBehaviour(interactionBehaviour, interactor, dataStore));
    }

}
