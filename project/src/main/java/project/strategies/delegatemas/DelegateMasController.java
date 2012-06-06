package project.strategies.delegatemas;

import org.apache.commons.math.random.MersenneTwister;

import project.common.model.CommunicationModel;
import project.common.packages.Package;
import project.common.trucks.Truck;
import project.strategies.delegatemas.colony.PackageAgent;
import project.strategies.delegatemas.colony.PackageDestination;
import project.strategies.delegatemas.colony.TruckAgent;
import project.strategies.delegatemas.colony.renderers.PackageAgentRenderer;
import project.strategies.delegatemas.colony.renderers.TruckAgentRenderer;
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


public class DelegateMasController extends ScenarioController {

    String map;

    private RoadModel roadModel;
    private CommunicationModel communicationModel;

    private int truckID = 0;

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
	communicationModel = new CommunicationModel();
	Simulator s = new Simulator(rand, 1000); // timestep
	s.register(roadModel);
	s.register(communicationModel);
	return s;
    }

    @Override
    protected boolean createUserInterface() {
	View.startGui(getSimulator(), 3, new PackageAgentRenderer(getSimulator()), new TruckAgentRenderer(getSimulator()));
	return true;
    }

    @Override
    protected boolean handleAddTruck(Event e) {
	Truck truck = new Truck(graph.getRandomNode(getSimulator().getRandomGenerator()));
	truck.setSpeed(500);
	getSimulator().register(truck);
	TruckAgent agent = new TruckAgent(truck);
	getSimulator().register(agent);	
	
	
	if (tempAgent == null)
	    tempAgent = agent;
	
	return true;
    }

    @Override
    protected boolean handleAddPackage(Event e) {
	Point pl = graph.getRandomNode(getSimulator().getRandomGenerator());
	Point dl = graph.getRandomNode(getSimulator().getRandomGenerator());

	Package p = new Package(pl, dl);
	PackageAgent agent = new PackageAgent(p.getId(),p);
	PackageDestination destination = agent.getDestination();
	
	getSimulator().register(p);
	getSimulator().register(agent);
	getSimulator().register(destination);
	
	return true;
    }
    
    
    
    TruckAgent tempAgent;
    
    @Override
    protected boolean doTest(Event e) {
	
	System.out.println("Sent an exploration ant ...\n");
	tempAgent.sendExplorationAnts();
	
	return true;
    }
}
