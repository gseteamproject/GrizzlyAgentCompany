package basicAgents;

import java.util.ArrayList;
import java.util.List;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;


public class ProcurementMarket extends Agent {

	private static final long serialVersionUID = -7418692714860762106L;

    public static List<AID> findAgents(Agent a, String serviceName) {
        /* prepare service-search template */
        ServiceDescription requiredService = new ServiceDescription();
        requiredService.setName(serviceName);
        /*
         * prepare agent-search template. agent-search template can have several
         * service-search templates
         */
        DFAgentDescription agentDescriptionTemplate = new DFAgentDescription();
        agentDescriptionTemplate.addServices(requiredService);

        List<AID> foundAgents = new ArrayList<AID>();
        try {
            /* perform request to DF-Agent */
            DFAgentDescription[] agentDescriptions = DFService.search(a, agentDescriptionTemplate);
            for (DFAgentDescription agentDescription : agentDescriptions) {
                /* store all found agents in an array for further processing */
                foundAgents.add(agentDescription.getName());
            }
        } catch (FIPAException exception) {
            exception.printStackTrace();
        }

        return foundAgents;
    }
}
