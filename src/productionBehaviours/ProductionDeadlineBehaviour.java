package productionBehaviours;

import interactors.DeadlineBehaviour;
import jade.core.behaviours.DataStore;

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

public class ProductionDeadlineBehaviour extends DeadlineBehaviour {

    public ProductionDeadlineBehaviour(ProductionResponder interactionBehaviour, DataStore dataStore) {
        super(interactionBehaviour, 6000);
        this.interactor = new ProductionRequestResult(dataStore);
    }

    private static final long serialVersionUID = 9050743659839198854L;
}