package project.strategies.gradientfield;

import project.common.controller.AbstractController;
import rinde.sim.event.pdp.StandardType;
import rinde.sim.scenario.Scenario;
import rinde.sim.scenario.ScenarioBuilder;
import rinde.sim.scenario.TimedEvent;

/**
 * 
 */
public class GradientFieldDemo {

    // Leuven
    // private static final String MAP_DIR = "../core/files/maps/";
    // private static final String MAP = "leuven-simple.dot";

    // Manhattan
    private static final String MAP_DIR = "files/maps/";
    private static final String MAP = "grid-10x10.dot";

    private static final String MAP_URI = MAP_DIR + MAP;

    public static void main(String[] args) throws Exception {

	int timeStep = 24 * 60 * 60 * 1000;
	ScenarioBuilder builder = new ScenarioBuilder(StandardType.ADD_PACKAGE, StandardType.ADD_TRUCK,
		StandardType.STOP_SIMULATION);
	// Add 3 trucks at time 0
	builder.add(new ScenarioBuilder.MultipleEventGenerator<TimedEvent>(0, 3, new ScenarioBuilder.EventTypeFunction(
		StandardType.ADD_TRUCK)));
	// Add 2 packages at time 0
	builder.add(new ScenarioBuilder.MultipleEventGenerator<TimedEvent>(0, 2, new ScenarioBuilder.EventTypeFunction(
		StandardType.ADD_PACKAGE)));
	// Add 2 Packages every timeStep
	for (int i = 0; i < 2; i++) {
	    builder.add(new ScenarioBuilder.TimeSeries<TimedEvent>(0, 10 * timeStep, timeStep,
		    new ScenarioBuilder.EventTypeFunction(StandardType.ADD_PACKAGE)));
	}
	// End the simulation after 10 * timeStep
	builder.add(new ScenarioBuilder.MultipleEventGenerator<TimedEvent>(10 * timeStep, 1,
		new ScenarioBuilder.EventTypeFunction(StandardType.STOP_SIMULATION)));
	Scenario scenario = builder.build();

	AbstractController controller = new GradientFieldController(scenario, -1, MAP_URI);

	// dispatch the controller with an initial speed of 3
	controller.dispatch(3);
    }
}
