package project.strategies.delegatemas;

import rinde.sim.event.pdp.StandardType;
import rinde.sim.scenario.Scenario;
import rinde.sim.scenario.ScenarioBuilder;
import rinde.sim.scenario.TimedEvent;

public class DelegateMasDemo {

    public static void main(String[] args) throws Exception {
	ScenarioBuilder builder = new ScenarioBuilder(StandardType.DO_TEST, StandardType.ADD_TRUCK, StandardType.ADD_PACKAGE);

	builder.add(new ScenarioBuilder.MultipleEventGenerator<TimedEvent>(0, // at
									      // time
									      // 0
		2, // amount of trucks to be added
		new ScenarioBuilder.EventTypeFunction(StandardType.ADD_TRUCK)));

	builder.add(new ScenarioBuilder.MultipleEventGenerator<TimedEvent>(0, // at
									      // time
									      // 0
		14, // amount of packages to be added
		new ScenarioBuilder.EventTypeFunction(StandardType.ADD_PACKAGE)));

	
//	builder.add(new ScenarioBuilder.MultipleEventGenerator<TimedEvent>(3000,1
//		,new ScenarioBuilder.EventTypeFunction(StandardType.DO_TEST)));
	

	Scenario scenario = builder.build();

//	final String MAP_DIR = "./files/maps/";
//	new DelegateMasController(scenario, -1, MAP_DIR + "grid-10x10.dot");
	
	final String MAP_DIR = "../core/files/maps/";
	new DelegateMasController(scenario, -1, MAP_DIR + "leuven-simple.dot");
    }

}
