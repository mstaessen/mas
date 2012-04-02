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

	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception {

		ScenarioBuilder builder = new ScenarioBuilder(StandardType.ADD_TRUCK, StandardType.ADD_PACKAGE);

		// Add 3 trucks at time 0
		builder.add(new ScenarioBuilder.MultipleEventGenerator<TimedEvent>(0, 3, 
				new ScenarioBuilder.EventTypeFunction(StandardType.ADD_TRUCK)));

		// Add 10 packages at time 0
		builder.add(new ScenarioBuilder.MultipleEventGenerator<TimedEvent>(0, 10,
				new ScenarioBuilder.EventTypeFunction(StandardType.ADD_PACKAGE)));

		// int timeStep = 50000000;
		//
		// builder.add(
		// new ScenarioBuilder.TimeSeries<TimedEvent>(
		// 0, // start time
		// 20*timeStep, // end time
		// timeStep, // step
		// new ScenarioBuilder.EventTypeFunction(
		// StandardType.ADD_PACKAGE
		// )
		// )
		// );

		Scenario scenario = builder.build();
		new GradientFieldController(scenario, -1, MAP_URI);
	}
}
