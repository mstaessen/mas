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

    protected static final int NB_TRUCKS = 3;
    protected static final int NB_PACKAGES = 3 * NB_TRUCKS;

    public GridExperiment(String reportFile) throws IOException {
	super(reportFile);
    }

    @Override
    public Scenario createScenario() {
	ScenarioBuilder builder = new ScenarioBuilder(StandardType.ADD_PACKAGE, StandardType.ADD_TRUCK,
		StandardType.STOP_SIMULATION);
	// Add 3 trucks at time 0
	builder.add(new ScenarioBuilder.MultipleEventGenerator<TimedEvent>(START, NB_TRUCKS,
		new ScenarioBuilder.EventTypeFunction(StandardType.ADD_TRUCK)));
	// Add 12 packages at time 0
	builder.add(new ScenarioBuilder.MultipleEventGenerator<TimedEvent>(START, NB_PACKAGES,
		new ScenarioBuilder.EventTypeFunction(StandardType.ADD_PACKAGE)));
	Scenario scenario = builder.build();
	scenario.add(new TimedEvent(StandardType.STOP_SIMULATION, END));
	return scenario;
    }

    public static void main(String[] args) {
	try {
	    String fileName = "./files/results/grid.csv";
	    Experiment gfExperiment = new GFExperiment(fileName);
	    gfExperiment.runMultiple(TIMES, true, false, false, "Gradient Field");

	    Experiment cnetExperiment = new CNetExperiment(fileName);
	    cnetExperiment.runMultiple(TIMES, true, false, true, "Contract Net");

	    Experiment dmasExperiment = new DMASExperiment(fileName);
	    dmasExperiment.runMultiple(TIMES, true, false, true, "Delegate MAS");
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }
}
