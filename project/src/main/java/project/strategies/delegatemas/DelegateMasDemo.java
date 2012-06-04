package project.strategies.delegatemas;

import rinde.sim.event.pdp.StandardType;
import rinde.sim.scenario.Scenario;
import rinde.sim.scenario.ScenarioBuilder;
import rinde.sim.scenario.TimedEvent;

public class DelegateMasDemo {

    public static void main(String[] args) throws Exception {
	ScenarioBuilder builder = new ScenarioBuilder(StandardType.ADD_TRUCK, StandardType.ADD_PACKAGE);

	builder.add(new ScenarioBuilder.MultipleEventGenerator<TimedEvent>(0, // at
									      // time
									      // 0
		1, // amount of trucks to be added
		new ScenarioBuilder.EventTypeFunction(StandardType.ADD_TRUCK)));

	builder.add(new ScenarioBuilder.MultipleEventGenerator<TimedEvent>(0, // at
									      // time
									      // 0
		14, // amount of packages to be added
		new ScenarioBuilder.EventTypeFunction(StandardType.ADD_PACKAGE)));


	Scenario scenario = builder.build();

	final String MAP_DIR = "./files/maps/";

	new DelegateMasController(scenario, -1, MAP_DIR + "grid-10x10.dot");
    }

}
