package project.strategies.contractnet;

import rinde.sim.event.pdp.StandardType;
import rinde.sim.scenario.Scenario;
import rinde.sim.scenario.ScenarioBuilder;
import rinde.sim.scenario.TimedEvent;

/**
 * 
 */
public class ContractNetDemo {

    // Leuven
    private static final String LEUVEN_MAP_DIR = "../core/files/maps/";
    private static final String LEUVEN_MAP = "leuven-simple.dot";
    private static final String LEUVEN_MAP_URI = LEUVEN_MAP_DIR + LEUVEN_MAP;

    // Manhattan
    private static final String MAP_DIR = "files/maps/";
    private static final String MAP = "grid-10x10.dot";
    private static final String MAP_URI = MAP_DIR + MAP;

    private static final long START = 0;
    private static final long TIMESTEP = 24 * 60 * 60 * 1000;
    private static final long END = 20 * TIMESTEP;

    public static void main(String[] args) throws Exception {

	ScenarioBuilder builder = new ScenarioBuilder(StandardType.ADD_PACKAGE, StandardType.ADD_TRUCK,
		StandardType.STOP_SIMULATION);
	// Add 3 trucks at time 0
	builder.add(new ScenarioBuilder.MultipleEventGenerator<TimedEvent>(START, 3,
		new ScenarioBuilder.EventTypeFunction(StandardType.ADD_TRUCK)));
	// Add 6 packages at time 0
	builder.add(new ScenarioBuilder.MultipleEventGenerator<TimedEvent>(START, 6,
		new ScenarioBuilder.EventTypeFunction(StandardType.ADD_PACKAGE)));
	// Add 2 Packages every timeStep
//	for (int i = 0; i < 2; i++) {
//	    builder.add(new ScenarioBuilder.TimeSeries<TimedEvent>(START, END, TIMESTEP,
//		    new ScenarioBuilder.EventTypeFunction(StandardType.ADD_PACKAGE)));
//	}
	// End the simulation after some time
	builder.add(new ScenarioBuilder.MultipleEventGenerator<TimedEvent>(END, 1,
		new ScenarioBuilder.EventTypeFunction(StandardType.STOP_SIMULATION)));
	Scenario scenario = builder.build();

	// Create a controller and dispatch it with an initial speed of 3
	ContractNetController controller = new ContractNetController(scenario, -1, MAP_URI);
	controller.startUi(3);
    }
}
