package project.strategies.contractnet;

import project.common.controller.AbstractController;
import project.common.packages.Package;
import project.common.trucks.Truck;
import project.experiments.Experiment;
import project.strategies.contractnet.agents.PackageAgent;
import project.strategies.contractnet.agents.TruckAgent;
import rinde.sim.core.Simulator;
import rinde.sim.core.graph.Point;
import rinde.sim.core.model.communication.CommunicationModel;
import rinde.sim.event.Event;
import rinde.sim.scenario.ConfigurationException;
import rinde.sim.scenario.Scenario;

public class ContractNetController extends AbstractController {

    private CommunicationModel communicationModel;

    public ContractNetController(Scenario scen, int numberOfTicks, String map) throws ConfigurationException {
	this(null, scen, numberOfTicks, map);
    }

    public ContractNetController(Experiment experiment, Scenario scen, int numberOfTicks, String map)
	    throws ConfigurationException {
	super(experiment, scen, numberOfTicks, map);
    }

    @Override
    protected Simulator createSimulator() throws Exception {
	Simulator simulator = super.createSimulator();

	communicationModel = new CommunicationModel(simulator.getRandomGenerator(), true);
	simulator.register(communicationModel);

	return simulator;
    }

    @Override
    protected boolean handleAddTruck(Event e) {
	Truck truck = new Truck(getGraph().getRandomNode(getSimulator().getRandomGenerator()));
	getSimulator().register(truck);

	TruckAgent agent = new TruckAgent(truck, -1, 1);
	agent.setCommunicationAPI(communicationModel);
	getSimulator().register(agent);

	return true;
    }

    @Override
    protected boolean handleAddPackage(Event e) {
	Point pl = getGraph().getRandomNode(getSimulator().getRandomGenerator());
	Point dl = getGraph().getRandomNode(getSimulator().getRandomGenerator());
	getSimulator().register(pl);
	getSimulator().register(dl);

	Package p = createPackage();
	getSimulator().register(p);

	PackageAgent agent = new PackageAgent(p, -1, 1);
	agent.setCommunicationAPI(communicationModel);
	getSimulator().register(agent);

	return true;
    }
}
