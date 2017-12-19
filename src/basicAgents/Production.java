package basicAgents;

import jade.core.Agent;
import jade.core.behaviours.DataStore;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import productionBehaviours.ProductionResponder;

public class Production extends Agent {

    /**
     * 
     */
    private static final long serialVersionUID = 9064413910591040008L;
    // public boolean isProduced = false;
    protected DataStore dataStore;

    @Override
    protected void setup() {
        // TODO: Need services for employees/robots
        MessageTemplate reqTemp = MessageTemplate.and(
                MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
                MessageTemplate.MatchPerformative(ACLMessage.REQUEST));

        dataStore = new DataStore();

        addBehaviour(new ProductionResponder(this, reqTemp, dataStore));
    }
}
