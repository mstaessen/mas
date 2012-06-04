package project.modern.delegatemas;

import rinde.sim.event.pdp.StandardType;
import rinde.sim.scenario.Scenario;
import rinde.sim.scenario.ScenarioBuilder;
import rinde.sim.scenario.TimedEvent;

public class DelegateMasDemo {

    public static void main(String[] args) throws Exception {
	ScenarioBuilder builder = new ScenarioBuilder(StandardType.ADD_TRUCK, StandardType.ADD_PACKAGE);

	// Add a truck at time 0
	builder.add(new ScenarioBuilder.MultipleEventGenerator<TimedEvent>(0, 1, new ScenarioBuilder.EventTypeFunction(
		StandardType.ADD_TRUCK)));

	// Add a package at time 0
	builder.add(new ScenarioBuilder.MultipleEventGenerator<TimedEvent>(0, 1, new ScenarioBuilder.EventTypeFunction(
		StandardType.ADD_PACKAGE)));

	Scenario scenario = builder.build();

	final String MAP_DIR = "../core/files/maps/";

	new DelegateMasController(scenario, -1, MAP_DIR + "leuven-simple.dot");
    }

}
