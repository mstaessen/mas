package project.experiments.grid;

import java.io.IOException;

import project.experiments.Experiment;
import rinde.sim.event.pdp.StandardType;
import rinde.sim.scenario.Scenario;
import rinde.sim.scenario.ScenarioBuilder;
import rinde.sim.scenario.TimedEvent;

public abstract class GridExperiment extends Experiment {

    private static final int TIMES = 10;

    protected static final String MAP_DIR = "files/maps/";
    protected static final String MAP = "grid-10x10.dot";
    protected static final String MAP_URI = MAP_DIR + MAP;

    protected static final long START = 0;
    protected static final long TIMESTEP = 24 * 60 * 60 * 1000;
    protected static final long END = 20 * TIMESTEP;

    public GridExperiment(String reportFile) throws IOException {
	super(reportFile);
    }

    @Override
    public Scenario createScenario() {
	ScenarioBuilder builder = new ScenarioBuilder(StandardType.ADD_PACKAGE, StandardType.ADD_TRUCK,
		StandardType.STOP_SIMULATION);
	// Add 3 trucks at time 0
	builder.add(new ScenarioBuilder.MultipleEventGenerator<TimedEvent>(START, 3,
		new ScenarioBuilder.EventTypeFunction(StandardType.ADD_TRUCK)));
	// Add 2 packages at time 0
	builder.add(new ScenarioBuilder.MultipleEventGenerator<TimedEvent>(START, 2,
		new ScenarioBuilder.EventTypeFunction(StandardType.ADD_PACKAGE)));
	// Add 2 Packages every timeStep
	for (int i = 0; i < 2; i++) {
	    builder.add(new ScenarioBuilder.TimeSeries<TimedEvent>(START, END, TIMESTEP,
		    new ScenarioBuilder.EventTypeFunction(StandardType.ADD_PACKAGE)));
	}
	// End the simulation after 10 * timeStep
	builder.add(new ScenarioBuilder.MultipleEventGenerator<TimedEvent>(END, 1,
		new ScenarioBuilder.EventTypeFunction(StandardType.STOP_SIMULATION)));
	return builder.build();
    }

    public static void main(String[] args) {
	try {
	    Experiment gfExperiment = new GFExperiment("./files/results/grid.gradient");
	    gfExperiment.runMultiple(TIMES);
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }
}
