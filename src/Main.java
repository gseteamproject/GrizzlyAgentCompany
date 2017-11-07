import jade.Boot;

public class
Main {


	public static void main(String[] args) {




		String[] parameters = new String[2];
		parameters[0] = "-gui";
		parameters[1] = "AgentSalesMarket:basicAgents.SalesMarket;" + "AgentSelling:basicAgents.Selling;"
				+ "AgentFinances:basicAgents.Finances;" + "AgentCapitalMarket:basicAgents.CapitalMarket;"
				+ "AgentProcurement:basicAgents.Procurement;" + "AgentProcurementMarket:basicAgents.ProcurementMarket;"
				+ "AgentProduction:basicAgents.Production;" + "sniffer:jade.tools.sniffer.Sniffer(Agent*);";
		// AgentSalesMarket, AgentSelling, AgentFinances, AgentCapitalMarket, AgentProcurement, AgentProcurementMarket, AgentProduction
		Boot.main(parameters);


	}
}
