package project.classic.gradientfield;

import org.apache.commons.math.random.MersenneTwister;

import project.classic.gradientfield.listeners.PackageListener;
import project.classic.gradientfield.packages.Package;
import project.classic.gradientfield.packages.PackageAgent;
import project.classic.gradientfield.packages.Priority;
import project.classic.gradientfield.trucks.Truck;
import project.classic.gradientfield.trucks.TruckAgent;
import project.common.renderers.AbstractRenderer;
import project.common.renderers.PackageRenderer;
import project.common.renderers.TruckRenderer;
import rinde.sim.core.Simulator;
import rinde.sim.core.graph.Graph;
import rinde.sim.core.graph.MultiAttributeEdgeData;
import rinde.sim.core.graph.Point;
import rinde.sim.core.model.RoadModel;
import rinde.sim.core.model.virtual.GradientFieldModel;
import rinde.sim.event.Event;
import rinde.sim.scenario.ConfigurationException;
import rinde.sim.scenario.Scenario;
import rinde.sim.scenario.ScenarioController;
import rinde.sim.serializers.DotGraphSerializer;
import rinde.sim.serializers.SelfCycleFilter;
import rinde.sim.ui.View;

public class GradientFieldController extends ScenarioController {

	String map;
	private RoadModel roadModel;
	private GradientFieldModel gradientFieldModel;
	private Graph<MultiAttributeEdgeData> graph;

	private PackageListener pListener;

	private AbstractRenderer truckRenderer;
	private PackageRenderer packageRenderer;

	public GradientFieldController(Scenario scenario, int nbTicks, String map) throws ConfigurationException {
		super(scenario, nbTicks);
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
		gradientFieldModel = new GradientFieldModel(roadModel);

		MersenneTwister rand = new MersenneTwister(321);
		// Create a new simulator with a timestep in millis
		// Time step = 1 minute
		Simulator s = new Simulator(rand, 1 * 60 * 1000);
		s.register(roadModel);
		s.register(gradientFieldModel);

		pListener = new PackageListener(s);
		s.events.addListener(pListener, Simulator.EventTypes.STOPPED);

		return s;
	}

	@Override
	protected boolean createUserInterface() {
		truckRenderer = new TruckRenderer(roadModel);
		packageRenderer = new PackageRenderer(roadModel);

		return true;
	}

	@Override
	protected boolean handleAddTruck(Event e) {
		Truck truck = new Truck(graph.getRandomNode(getSimulator().getRandomGenerator()));
		getSimulator().register(truck);

		TruckAgent agent = new TruckAgent(truck);
		getSimulator().register(agent);

		return true;
	}

	@Override
	protected boolean handleAddPackage(Event e) {
		Point pl = graph.getRandomNode(getSimulator().getRandomGenerator());
		Point dl = graph.getRandomNode(getSimulator().getRandomGenerator());

		Package p = new Package(pl, dl);
		p.setPriority(Priority.random(getSimulator().getRandomGenerator()));
		p.addListener(pListener, Package.EventType.values());
		// TODO: this is kinda ugly
		p.events.dispatchEvent(new Event(Package.EventType.PACKAGE_CREATION, p));
		getSimulator().register(p);

		PackageAgent agent = new PackageAgent(p);
		getSimulator().register(agent);

		return true;
	}

	public void dispatch() {
		// The TruckAgentRenderer is reponsible for the
		// ConcurrentModificationException,
		// it is therefore disabled
		View.startGui(getSimulator(), 3, packageRenderer, truckRenderer);
	}
}
