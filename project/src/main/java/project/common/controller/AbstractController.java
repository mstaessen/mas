package project.common.controller;

import org.apache.commons.math.random.MersenneTwister;

import project.common.listeners.PackageListener;
import project.common.packages.Package;
import project.common.renderers.AbstractRenderer;
import project.common.renderers.PackageRenderer;
import project.common.renderers.TruckRenderer;
import project.common.trucks.Truck;
import rinde.sim.core.Simulator;
import rinde.sim.core.graph.Graph;
import rinde.sim.core.graph.Graphs;
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

    String map;
    private RoadModel roadModel;
    private Graph<MultiAttributeEdgeData> graph;

    private PackageListener packageListener;

    private AbstractRenderer truckRenderer;
    private PackageRenderer packageRenderer;

    public AbstractController(Scenario scen, int numberOfTicks, String map) throws ConfigurationException {
	super(scen, numberOfTicks);
	this.map = map;

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

	MersenneTwister rand = new MersenneTwister(321);
	// Create a new simulator with a timestep in millis
	// Time step = 1 minute (= 60 * 1000 milliseconds)
	Simulator s = new Simulator(rand, 60 * 1000);
	s.register(roadModel);

	packageListener = new PackageListener(s);
	s.events.addListener(packageListener, Simulator.EventTypes.STOPPED);

	return s;
    }

    @Override
    protected boolean createUserInterface() {
	truckRenderer = new TruckRenderer(roadModel);
	packageRenderer = new PackageRenderer(roadModel);

	return true;
    }

    public void dispatch(int speed) {
	View.startGui(getSimulator(), speed, packageRenderer, truckRenderer);
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

	long pickupDeadline = (long) (getSimulator().getRandomGenerator().nextDouble() * 0.75
		* (Package.LOW_PRIO - Package.HIGH_PRIO) + Package.LOW_PRIO);

	long deliveryDeadline = (long) (pickupDeadline + (getSimulator().getRandomGenerator().nextDouble() + 1)
		* Graphs.pathLength(getRoadModel().getShortestPathTo(pl, dl)) / Truck.SPEED);

	Package pkg = new Package(pl, dl, pickupDeadline, deliveryDeadline);
	pkg.addListener(getPackageListener(), Package.EventType.values());
	pkg.events.dispatchEvent(new Event(Package.EventType.PACKAGE_CREATION, pkg));
	return pkg;
    }

    @Override
    protected abstract boolean handleAddTruck(Event e);

    @Override
    protected abstract boolean handleAddPackage(Event e);

    @Override
    protected boolean handleStopSimulation() {
	stop();
	return true;
    }

    @Override
    public void stop() {
	getSimulator().stop();
    }
}