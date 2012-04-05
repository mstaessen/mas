package project.classic.gradientfield;

import org.apache.commons.math.random.MersenneTwister;

import project.classic.gradientfield.packages.DeliveryLocation;
import project.classic.gradientfield.packages.Package;
import project.classic.gradientfield.packages.PackageAgent;
import project.classic.gradientfield.trucks.Truck;
import project.classic.gradientfield.trucks.TruckAgent;
import project.common.renderers.AbstractRenderer;
import project.common.renderers.PackageRenderer;
import project.common.renderers.TruckRenderer;
import rinde.sim.core.Simulator;
import rinde.sim.core.graph.Connection;
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

	private int truckID = 0;
	private int packageID = 0;
	private Graph<MultiAttributeEdgeData> graph;

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
			for (Connection<MultiAttributeEdgeData> conn : graph.getConnections()) {
				System.out.println(conn);
			}
		} catch (Exception e) {
			throw new ConfigurationException("e:", e);
		}
		roadModel = new RoadModel(graph);
		GradientFieldModel gfModel = new GradientFieldModel(roadModel);

		MersenneTwister rand = new MersenneTwister(123);
		Simulator s = new Simulator(rand, 10000);
		s.register(roadModel);
		s.register(gfModel);
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
		Truck truck = new Truck("Truck-" + truckID++, graph.getRandomNode(getSimulator().getRandomGenerator()), 7);
		getSimulator().register(truck);
		TruckAgent agent = new TruckAgent(truck, 5);
		getSimulator().register(agent);
		return true;
	}

	@Override
	protected boolean handleAddPackage(Event e) {
		Point pl = graph.getRandomNode(getSimulator().getRandomGenerator());
		DeliveryLocation dl = new DeliveryLocation(graph.getRandomNode(getSimulator().getRandomGenerator()));
		getSimulator().register(pl);
		getSimulator().register(dl);

		Package p = new Package("Package-" + packageID++, pl, dl);
		getSimulator().register(p);
		PackageAgent agent = new PackageAgent(p);
		getSimulator().register(agent);
		return true;
	}

	public void dispatch() {
		View.startGui(getSimulator(), 3, packageRenderer, truckRenderer);
	}
}
