import communication.Communication;
import communication.Server;
import jade.Boot;

public class
Main {
    public static void main(String[] args) {
        Communication communication = new Communication();

        // todo: maybe start the program, when the client is connected first?

        String[] parameters = new String[2];
        parameters[0] = "-gui";
        parameters[1] = "AgentCustomer:basicAgents.Customer;" + "AgentSalesMarket:basicAgents.SalesMarket;" + "AgentSelling:basicAgents.Selling;"
                + "AgentFinances:basicAgents.Finances;" + "AgentCapitalMarket:basicAgents.CapitalMarket;"
                + "AgentProcurement:basicAgents.Procurement;" + "AgentPaintSelling:basicAgents.PaintSeller;"
                + "AgentStoneSelling:basicAgents.StoneSeller;" + "AgentProcurementMarket:basicAgents.ProcurementMarket;"
                + "AgentProduction:basicAgents.Production;" + "sniffer:jade.tools.sniffer.Sniffer(Agent*);";
        // AgentSalesMarket, AgentSelling, AgentFinances, AgentCapitalMarket,
        // AgentProcurement, AgentProcurementMarket, AgentProduction
        Boot.main(parameters);
    }
}
