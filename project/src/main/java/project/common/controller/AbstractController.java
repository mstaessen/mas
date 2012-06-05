package project.common.controller;

import org.apache.commons.math.random.MersenneTwister;
import org.apache.commons.math.random.RandomGenerator;

import project.common.listeners.PackageListener;
import project.common.listeners.Report;
import project.common.packages.Package;
import project.common.renderers.AbstractRenderer;
import project.common.renderers.PackageRenderer;
import project.common.renderers.TruckRenderer;
import project.experiments.Experiment;
import rinde.sim.core.Simulator;
import rinde.sim.core.graph.Graph;
import rinde.sim.core.graph.MultiAttributeEdgeData;
import rinde.sim.core.graph.Point;
import rinde.sim.core.model.RoadModel;
import rinde.sim.event.Event;
import rinde.sim.scenario.ConfigurationException;
import rinde.sim.scenario.Scenario;
import rinde.sim.scenario.ScenarioController;
import rinde.sim.serializers.DotGraphSerializer;
import rinde.sim.serializers.SelfCycleFilter;
import rinde.sim.ui.View;

public abstract class AbstractController extends ScenarioController {

    protected String map;
    protected RoadModel roadModel;
    protected Graph<MultiAttributeEdgeData> graph;

    protected PackageListener packageListener;

    protected AbstractRenderer truckRenderer;
    protected PackageRenderer packageRenderer;

    protected Experiment experiment;

    protected static final long DAYLENGTH = 24 * 60 * 60 * 1000;
    protected static final int SPEED = 3;

    protected final RandomGenerator random = new MersenneTwister();

    public AbstractController(Scenario scen, int numberOfTicks, String map) throws ConfigurationException {
	this(null, scen, numberOfTicks, map);
    }

    public AbstractController(Experiment runner, Scenario scen, int nbTicks, String map) throws ConfigurationException {
	super(scen, nbTicks);
	this.map = map;
	this.experiment = runner;

	initialize();
    }

    @Override
    protected Simulator createSimulator() throws Exception {
	try {
	    graph = DotGraphSerializer.getMultiAttributeGraphSerializer(new SelfCycleFilter()).read(map);
	} catch (Exception e) {
	    throw new ConfigurationException("e:", e);
	}
	roadModel = new RoadModel(graph);

	// Create a new simulator with a timestep in millis
	// Time step = 1 minute (= 60 * 1000 milliseconds)
	Simulator s = new Simulator(random, 60 * 1000);
	s.register(roadModel);

	packageListener = new PackageListener(s);

	return s;
    }

    @Override
    protected boolean createUserInterface() {
	if (experiment == null) {
	    truckRenderer = new TruckRenderer(roadModel);
	    packageRenderer = new PackageRenderer(roadModel);
	    return true;
	}

	return false;
    }

    public void startUi(int seed) {
	random.setSeed(seed);
	View.startGui(getSimulator(), SPEED, packageRenderer, truckRenderer);
    }

    protected RoadModel getRoadModel() {
	return roadModel;
    }

    protected Graph<MultiAttributeEdgeData> getGraph() {
	return graph;
    }

    protected PackageListener getPackageListener() {
	return packageListener;
    }

    protected Package createPackage() {
	Point pl = getGraph().getRandomNode(getSimulator().getRandomGenerator());
	Point dl = getGraph().getRandomNode(getSimulator().getRandomGenerator());

	long deadline = (long) (DAYLENGTH + getSimulator().getRandomGenerator().nextDouble() * 2 * DAYLENGTH);

	Package pkg = new Package(pl, dl, deadline);
	pkg.addListener(getPackageListener(), Package.EventType.values());
	pkg.events.dispatchEvent(new Event(Package.EventType.PACKAGE_CREATION, pkg));
	return pkg;
    }

    @Override
    protected boolean handleStopSimulation(Event e) {
	stop();
	return true;
    }

    @Override
    public void stop() {
	super.stop();
	Report report = getPackageListener().generateReport();

	if (experiment != null) {
	    experiment.receiveReport(report);
	}
    }

    public void start(int seed) throws ConfigurationException {
	random.setSeed(seed);
	super.start();
    }

    @Override
    protected abstract boolean handleAddTruck(Event e);

    @Override
    protected abstract boolean handleAddPackage(Event e);
}