package basicAgents;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import basicClasses.*;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.DataStore;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREInitiator;
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

        // adding behaviours
        addBehaviour(new ProcurementResponder(this, reqTemp, dataStore));
    }
}
