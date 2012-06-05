package project.strategies.delegatemas;

import rinde.sim.event.pdp.StandardType;
import rinde.sim.scenario.Scenario;
import rinde.sim.scenario.ScenarioBuilder;
import rinde.sim.scenario.TimedEvent;

public class DelegateMasDemo {

    public static void main(String[] args) throws Exception {
	ScenarioBuilder builder = new ScenarioBuilder(StandardType.DO_TEST, StandardType.ADD_TRUCK,
		StandardType.ADD_PACKAGE);

	// Add 2 trucks at time 0
	builder.add(new ScenarioBuilder.MultipleEventGenerator<TimedEvent>(0, 2, new ScenarioBuilder.EventTypeFunction(
		StandardType.ADD_TRUCK)));

	// Add 14 packages at time 0
	builder.add(new ScenarioBuilder.MultipleEventGenerator<TimedEvent>(0, 14,
		new ScenarioBuilder.EventTypeFunction(StandardType.ADD_PACKAGE)));

	Scenario scenario = builder.build();

	final String MAP_DIR = "./files/maps/";

	DelegateMasController controller = new DelegateMasController(scenario, -1, MAP_DIR + "grid-10x10.dot");
	controller.startUi(123);

    }

}
