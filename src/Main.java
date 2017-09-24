import jade.Boot;

public class Main {
	public static void main(String[] args) {

		String[] parameters = new String[2];
		parameters[0] = "-gui";
		parameters[1] = "sellingAgent:basicAgents.Selling;" 
						+ "financesAgent:basicAgents.Finances;"
						+ "procurementAgent:basicAgents.Procurement;" 
						+ "productionAgent:basicAgents.Production;";
		Boot.main(parameters);

	}
}