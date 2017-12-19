package basicAgents;

import java.util.ArrayList;
import java.util.List;

import basicClasses.Order;
import jade.core.Agent;
import jade.core.behaviours.DataStore;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import salesMarketBehaviours.CustomerSimulatorBehaviour;
import salesMarketBehaviours.SalesMarketResponder;

public class SalesMarket extends Agent {

    /**
     * 
     */
    private static final long serialVersionUID = 2003110338808844985L;

    // creating list of orders
    public static List<Order> orderQueue = new ArrayList<Order>();
    protected DataStore dataStore;

    @Override
    protected void setup() {
        MessageTemplate temp = MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
        MessageTemplate reqTemp = MessageTemplate.and(temp, MessageTemplate.MatchPerformative(ACLMessage.REQUEST));

        dataStore = new DataStore();

        // adding behaviours
        addBehaviour(new SalesMarketResponder(this, reqTemp, dataStore));

        // addBehaviour(new GenerateOrders(this, 15000));

        addBehaviour(new CustomerSimulatorBehaviour(this, 4000));
    }
}
