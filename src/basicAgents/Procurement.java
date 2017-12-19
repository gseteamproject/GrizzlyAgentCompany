package basicAgents;

import java.util.ArrayList;
import java.util.List;

import basicClasses.MaterialStorage;
import basicClasses.Order;
import jade.core.Agent;
import jade.core.behaviours.DataStore;
import jade.domain.FIPANames;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;
import procurementBehaviours.ProcurementResponder;

public class Procurement extends Agent {
    /**
     * 
     */
    private static final long serialVersionUID = 2923962894395399488L;
    public static boolean isInMaterialStorage;
    protected DataStore dataStore;

    // queue for procurement orders
    public static List<Order> procurementQueue = new ArrayList<Order>();

    // creating storage for raw materials
    public static MaterialStorage materialStorage = new MaterialStorage();

    @Override
    protected void setup() {
        MessageTemplate reqTemp = AchieveREResponder.createMessageTemplate(FIPANames.InteractionProtocol.FIPA_REQUEST);

        dataStore = new DataStore();

        // adding behaviours
        addBehaviour(new ProcurementResponder(this, reqTemp, dataStore));
    }
}
