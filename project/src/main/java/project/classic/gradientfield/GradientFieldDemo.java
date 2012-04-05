package project.classic.gradientfield;

import rinde.sim.event.pdp.StandardType;
import rinde.sim.scenario.Scenario;
import rinde.sim.scenario.ScenarioBuilder;
import rinde.sim.scenario.TimedEvent;

/**
 * 
 */
public class GradientFieldDemo {

	private static final String MAP_DIR = "../core/files/maps/";
	private static final String MAP = "leuven-simple.dot";
	private static final String MAP_URI = MAP_DIR + MAP;

	public static void main(String[] args) throws Exception {

		ScenarioBuilder builder = new ScenarioBuilder(StandardType.ADD_TRUCK, StandardType.ADD_PACKAGE);

		// Add 3 trucks at time 0
		builder.add(new ScenarioBuilder.MultipleEventGenerator<TimedEvent>(0, 3, 
				new ScenarioBuilder.EventTypeFunction(StandardType.ADD_TRUCK)));

		// Add 10 packages at time 0
		builder.add(new ScenarioBuilder.MultipleEventGenerator<TimedEvent>(0, 10,
				new ScenarioBuilder.EventTypeFunction(StandardType.ADD_PACKAGE)));

		Scenario scenario = builder.build();
		
		/**
		 * Trucks are blue,
		 * Packages are red,
		 * DeliveryLocations are green.
		 */
		//GradientFieldController controller = new GradientFieldController(scenario, -1, MAP_URI);
		GradientFieldController controller = new GradientFieldController(scenario, -1, "files/maps/grid-10x10.dot");
		controller.dispatch();
	}
}
