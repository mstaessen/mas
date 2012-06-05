package project.strategies.gradientfield;

import project.common.controller.AbstractController;
import project.common.packages.Package;
import project.common.renderers.GradientFieldRenderer;
import project.common.renderers.PackageRenderer;
import project.common.renderers.TruckRenderer;
import project.common.trucks.Truck;
import project.strategies.gradientfield.agents.PackageAgent;
import project.strategies.gradientfield.agents.TruckAgent;
import rinde.sim.core.Simulator;
import rinde.sim.core.model.virtual.GradientFieldModel;
import rinde.sim.event.Event;
import rinde.sim.scenario.ConfigurationException;
import rinde.sim.scenario.Scenario;
import rinde.sim.ui.View;

public class GradientFieldController extends AbstractController {

    private GradientFieldModel gradientFieldModel;
    private GradientFieldRenderer gradientFieldRenderer;

    public GradientFieldController(Scenario scen, int numberOfTicks, String map) throws ConfigurationException {
	super(scen, numberOfTicks, map);
    }

    @Override
    protected Simulator createSimulator() throws Exception {
	Simulator simulator = super.createSimulator();

	gradientFieldModel = new GradientFieldModel(getRoadModel());
	simulator.register(gradientFieldModel);

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
	Package pkg = createPackage();
	getSimulator().register(pkg);

	PackageAgent agent = new PackageAgent(pkg);
	getSimulator().register(agent);

	return true;
    }

    @Override
    protected boolean createUserInterface() {
	truckRenderer = new TruckRenderer(roadModel);
	packageRenderer = new PackageRenderer(roadModel);
	gradientFieldRenderer = new GradientFieldRenderer(roadModel, gradientFieldModel);

	return true;
    }

    @Override
    public void dispatch(int speed) {
	View.startGui(getSimulator(), speed, gradientFieldRenderer, packageRenderer, truckRenderer);
    }
}
