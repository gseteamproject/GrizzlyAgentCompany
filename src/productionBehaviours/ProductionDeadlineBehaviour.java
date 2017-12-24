package productionBehaviours;

import interactors.DeadlineBehaviour;
import interactors.OrderDataStore;

public class ProductionDeadlineBehaviour extends DeadlineBehaviour {

    public ProductionDeadlineBehaviour(ProductionResponder interactionBehaviour, OrderDataStore dataStore) {
        super(interactionBehaviour, 6000);
        this.interactor = new ProductionRequestResult(dataStore);
    }

    private static final long serialVersionUID = 9050743659839198854L;
}