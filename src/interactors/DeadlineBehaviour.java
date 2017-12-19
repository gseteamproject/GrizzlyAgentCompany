package interactors;

import jade.core.behaviours.WakerBehaviour;

public class DeadlineBehaviour extends WakerBehaviour {
    /**
     * 
     */
    private static final long serialVersionUID = 3660229129526762982L;

    protected ResponderBehaviour interactionBehaviour;

    protected RequestResult interactor;

    public DeadlineBehaviour(ResponderBehaviour interactionBehaviour, long period) {
        super(interactionBehaviour.getAgent(), period);
        this.interactionBehaviour = interactionBehaviour;
    }

    @Override
    protected void onWake() {
        interactionBehaviour.setResult(interactor.execute(interactionBehaviour.getRequest()));
    }
}