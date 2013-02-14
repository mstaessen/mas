package project.modern.delegatemas;

import org.apache.commons.math.random.MersenneTwister;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;

import project.common.packages.DeliveryLocation;
import project.common.packages.Package;
import project.common.trucks.Truck;
import project.modern.delegatemas.colony.PackageAgent;
import project.modern.delegatemas.colony.TruckAgent;
import project.modern.delegatemas.colony.renderers.PackageAgentRenderer;
import rinde.sim.core.Simulator;
import rinde.sim.core.graph.Graph;
import rinde.sim.core.graph.MultiAttributeEdgeData;
import rinde.sim.core.graph.Point;
import rinde.sim.core.model.RoadModel;
import rinde.sim.core.model.communication.CommunicationModel;
import rinde.sim.event.Event;
import rinde.sim.scenario.ConfigurationException;
import rinde.sim.scenario.Scenario;
import rinde.sim.scenario.ScenarioController;
import rinde.sim.serializers.DotGraphSerializer;
import rinde.sim.serializers.SelfCycleFilter;
import rinde.sim.ui.View;
import rinde.sim.ui.renderers.ObjectRenderer;
import rinde.sim.ui.renderers.UiSchema;

public class DelegateMasController extends ScenarioController {

	String map;
	
	private RoadModel roadModel;
	private CommunicationModel communicationModel;
	
	private int truckID = 0;
	private int packageID = 0;
	private Graph<MultiAttributeEdgeData> graph;
	
	public DelegateMasController(Scenario scen, int numberOfTicks, String map) throws ConfigurationException {
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
		MersenneTwister rand = new MersenneTwister(123);
		communicationModel = new CommunicationModel(rand, true);
		Simulator s = new Simulator(rand, 1000000);
		s.register(roadModel);
		s.register(communicationModel);
		return s;	
	}
	
	@Override
	protected boolean createUserInterface() {
		UiSchema schema = new UiSchema();
		schema.add(Truck.class, new RGB(0,0,255));
//		schema.add(Package.class, ImageDescriptor.createFromImageData(new ImageData("src/resources/graphics/package.png")));
		schema.add(DeliveryLocation.class, new RGB(0,255,0));
//		View.startGui(getSimulator(), 3, new ObjectRenderer(roadModel, schema, false));		
		View.startGui(getSimulator(), 3, new PackageAgentRenderer(getSimulator()));		
		
		
//		View.startGui(getSimulator(), 3, );		

		
		
		return true;
	}

	@Override
	protected boolean handleAddTruck(Event e) {
		Truck truck = new Truck("Truck-"+truckID++, graph.getRandomNode(getSimulator().getRandomGenerator()), 7);
		getSimulator().register(truck);
		TruckAgent agent = new TruckAgent(truck);
		getSimulator().register(agent);
//		agent.setSimulator(getSimulator());
		return true;
	}	

	@Override
	protected boolean handleAddPackage(Event e){
		Point pl = graph.getRandomNode(getSimulator().getRandomGenerator());
		DeliveryLocation dl = new DeliveryLocation(graph.getRandomNode(getSimulator().getRandomGenerator()));
		getSimulator().register(pl);
		getSimulator().register(dl);
		
		Package p = new Package("Package-"+packageID++, pl, dl);
		getSimulator().register(p);
		
		PackageAgent agent = new PackageAgent(packageID,p);
		getSimulator().register(agent);
		agent.setSimulator(getSimulator());
		return true;
	}

	
	
}
