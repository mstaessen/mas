package project.strategies.contractnet;

import project.common.controller.AbstractController;
import project.common.packages.Package;
import project.common.trucks.Truck;
import project.strategies.contractnet.agents.PackageAgent;
import project.strategies.contractnet.agents.TruckAgent;
import rinde.sim.core.graph.Point;
import rinde.sim.event.Event;
import rinde.sim.scenario.ConfigurationException;
import rinde.sim.scenario.Scenario;

public class ContractNetController extends AbstractController {

	public ContractNetController(Scenario scen, int numberOfTicks, String map) throws ConfigurationException {
		super(scen, numberOfTicks, map);
	}

	@Override
	protected boolean handleAddTruck(Event e) {
		Truck truck = new Truck(getGraph().getRandomNode(getSimulator().getRandomGenerator()));
		getSimulator().register(truck);
		TruckAgent agent = new TruckAgent(truck, -1, 1);
		getSimulator().register(agent);
		return true;
	}

	@Override
	protected boolean handleAddPackage(Event e) {
		Point pl = getGraph().getRandomNode(getSimulator().getRandomGenerator());
		Point dl = getGraph().getRandomNode(getSimulator().getRandomGenerator());
		getSimulator().register(pl);
		getSimulator().register(dl);

		Package p = new Package(pl, dl);
		getSimulator().register(p);
		PackageAgent agent = new PackageAgent(p, -1, 1);
		getSimulator().register(agent);
		return true;
	}
}
