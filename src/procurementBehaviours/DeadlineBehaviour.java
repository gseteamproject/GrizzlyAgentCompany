package procurementBehaviours;

import jade.core.behaviours.DataStore;
import jade.core.behaviours.WakerBehaviour;

public class DeadlineBehaviour extends WakerBehaviour {

    ProcurementResponder interactionBehaviour;

    Work interactor;

    public DeadlineBehaviour(ProcurementResponder interactionBehaviour, DataStore dataStore) {
        super(interactionBehaviour.getAgent(), 20000);
        this.interactionBehaviour = interactionBehaviour;
        this.interactor = new Work(dataStore);
    }

    @Override
    protected void onWake() {
        interactionBehaviour.setResult(interactor.execute(interactionBehaviour.getRequest()));
    }

    private static final long serialVersionUID = 9050743659839198854L;
}