package productionBehaviours;

import jade.core.behaviours.DataStore;
import jade.core.behaviours.OneShotBehaviour;

public class DecisionBehaviour extends OneShotBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = -5138036682748995317L;

    ProductionResponder interactionBehaviour;

    Decision interactor;

    public DecisionBehaviour(ProductionResponder interactionBehaviour, DataStore dataStore) {
        this.interactionBehaviour = interactionBehaviour;
        this.interactor = new Decision(dataStore);
    }

    @Override
    public void action() {
        interactionBehaviour.setResponse(interactor.execute(interactionBehaviour.getRequest()));
    }

}
