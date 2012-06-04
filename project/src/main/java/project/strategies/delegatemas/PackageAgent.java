package project.strategies.delegatemas;

import project.common.packages.AbstractPackageAgent;
import project.common.packages.Package;
import rinde.sim.core.SimulatorAPI;

public class PackageAgent extends AbstractPackageAgent {

    private SimulatorAPI simulator;
    private Package myPackage;

    private long sendFeasibilityAntsTime;

    public static final int TIMESTEPS_WAIT_BEFORE_SENDING_FEASIBILITY_ANTS = 50;

    public PackageAgent(Package pkg) {
	super(pkg);

	this.sendFeasibilityAntsTime = 0;
    }

    @Override
    public void setSimulator(SimulatorAPI api) {
	this.simulator = api;
    }

    @Override
    public void tick(long currentTime, long timeStep) {

	if (sendFeasibilityAntsTime < currentTime) {
	    System.out.println("send ants: " + sendFeasibilityAntsTime + " " + currentTime);
	    sendFeasibilityAnts();
	    sendFeasibilityAntsTime = currentTime + 1000 * timeStep;
	}
    }

    @Override
    public void afterTick(long currentTime, long timeStep) {

    }

    public void sendFeasibilityAnts() {
	//
	// Collection<Point> points = getPackage().getRoadModel().getGraph()
	// .getOutgoingConnections(getPackage().getPickupLocation());
	//
	// for (Point point : points) {
	// FeasibilityAnt ant = new
	// FeasibilityAnt(myPackage.getPickupLocation(), point, 10,
	// myPackage.getRoadModel());
	// ant.setSimulator(simulator);
	// }
    }
}
