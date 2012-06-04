package project.strategies.gradientfield;

import project.common.controller.AbstractController;
import project.common.packages.Package;
import project.common.packages.Priority;
import project.common.trucks.Truck;
import project.strategies.gradientfield.agents.PackageAgent;
import project.strategies.gradientfield.agents.TruckAgent;
import rinde.sim.core.Simulator;
import rinde.sim.core.graph.Point;
import rinde.sim.core.model.virtual.GradientFieldModel;
import rinde.sim.event.Event;
import rinde.sim.scenario.ConfigurationException;
import rinde.sim.scenario.Scenario;

public class GradientFieldController extends AbstractController {

    public GradientFieldController(Scenario scen, int numberOfTicks, String map) throws ConfigurationException {
	super(scen, numberOfTicks, map);
    }

    @Override
    protected Simulator createSimulator() throws Exception {
	Simulator simulator = super.createSimulator();

	GradientFieldModel model = new GradientFieldModel(getRoadModel());
	simulator.register(model);

	return simulator;
    }

    @Override
    protected boolean handleAddTruck(Event e) {
	Truck truck = new Truck(getGraph().getRandomNode(getSimulator().getRandomGenerator()));
	getSimulator().register(truck);

	TruckAgent agent = new TruckAgent(truck);
	getSimulator().register(agent);

	return true;
    }

    @Override
    protected boolean handleAddPackage(Event e) {
	Point pl = getGraph().getRandomNode(getSimulator().getRandomGenerator());
	Point dl = getGraph().getRandomNode(getSimulator().getRandomGenerator());

	Package p = new Package(pl, dl);
	p.setPriority(Priority.random(getSimulator().getRandomGenerator()));
	p.addListener(getPackageListener(), Package.EventType.values());
	// TODO: this is kinda ugly
	p.events.dispatchEvent(new Event(Package.EventType.PACKAGE_CREATION, p));
	getSimulator().register(p);

	PackageAgent agent = new PackageAgent(p);
	getSimulator().register(agent);

	return true;
    }
}
