package project.modern.delegatemas;

import java.util.Collection;

import project.common.packages.Package;
import rinde.sim.core.SimulatorAPI;
import rinde.sim.core.SimulatorUser;
import rinde.sim.core.TickListener;
import rinde.sim.core.graph.Point;

public class PackageAgent implements TickListener, SimulatorUser {

	private SimulatorAPI simulator;
	private Package myPackage;

	private long sendFeasibilityAntsTime;

	public static final int TIMESTEPS_WAIT_BEFORE_SENDING_FEASIBILITY_ANTS = 50;

	public PackageAgent(Package myPackage) {
		this.myPackage = myPackage;
		this.sendFeasibilityAntsTime = 0;
	}

	@Override
	public void setSimulator(SimulatorAPI api) {
		this.simulator = api;
	}

	@Override
	public void tick(long currentTime, long timeStep) {
			
		if (sendFeasibilityAntsTime < currentTime) {
			System.out.println("send ants: "+sendFeasibilityAntsTime+" "+currentTime);
			sendFeasibilityAnts();
			sendFeasibilityAntsTime = currentTime + 1000*timeStep;
		}
	}

	@Override
	public void afterTick(long currentTime, long timeStep) {

	}

	public void sendFeasibilityAnts() {
//
//		Collection<Point> points = myPackage.getRoadModel().getGraph()
//				.getOutgoingConnections(myPackage.getPickupLocation());
//
//		for (Point point : points) {
//			FeasibilityAnt ant = new FeasibilityAnt(myPackage.getPickupLocation(), point, 10, myPackage.getRoadModel());
//			ant.setSimulator(simulator);
//		}
	}
}
