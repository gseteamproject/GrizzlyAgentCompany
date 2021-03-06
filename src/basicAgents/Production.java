package basicAgents;

import interactors.OrderDataStore;
import jade.core.Agent;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import productionBehaviours.ProductionResponder;

public class Production extends Agent {

    /**
     * 
     */
    private static final long serialVersionUID = 9064413910591040008L;
    public static boolean isProduced = false;
    protected OrderDataStore dataStore;

    @Override
    protected void setup() {
        // TODO: Need services for employees/robots
        MessageTemplate reqTemp = MessageTemplate.and(
                MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
                MessageTemplate.MatchPerformative(ACLMessage.REQUEST));

        dataStore = new OrderDataStore();

        addBehaviour(new ProductionResponder(this, reqTemp, dataStore));
    }
}
