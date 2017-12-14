package productionBehaviours;

import jade.core.behaviours.DataStore;
import jade.core.behaviours.WakerBehaviour;

//public class DeadlineBehaviour extends SimpleBehaviour {
//	ProductionResponderBehaviour interactionBehaviour;
//	private DataStore dataStore;
//    ACLMessage request;
//	Work interactor;
//
//    public DeadlineBehaviour(ProductionResponderBehaviour interactionBehaviour, DataStore dataStore) {
//    	this.interactionBehaviour = interactionBehaviour;
//		this.dataStore = dataStore;
//		this.interactor = new Work(dataStore);
//        this.request = interactionBehaviour.getRequest();
//    }
//
//	@Override
//	public void action() {
////		interactionBehaviour.setResult(interactor.execute(interactionBehaviour.getRequest()));
//		request = (interactor.execute(interactionBehaviour.getRequest()));
//	}
//
//	@Override
//	public boolean done() {
//		return interactor.done();
//	}
//
//    private static final long serialVersionUID = -3500469822678572098L;
//}

public class DeadlineBehaviour extends WakerBehaviour {

    ProductionResponder interactionBehaviour;

    Work interactor;

    public DeadlineBehaviour(ProductionResponder interactionBehaviour, DataStore dataStore) {
        super(interactionBehaviour.getAgent(), 10000);
        this.interactionBehaviour = interactionBehaviour;
        this.interactor = new Work(dataStore);
    }

    @Override
    protected void onWake() {
        interactionBehaviour.setResult(interactor.execute(interactionBehaviour.getRequest()));
    }

    private static final long serialVersionUID = 9050743659839198854L;
}