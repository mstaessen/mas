package project.common.trucks;

import java.util.LinkedList;
import java.util.Queue;

import rinde.sim.core.SimulatorAPI;
import rinde.sim.core.SimulatorUser;
import rinde.sim.core.TickListener;
import rinde.sim.core.graph.Point;

public abstract class AbstractTruckAgent implements SimulatorUser, TickListener {

	private final Truck truck;
	private SimulatorAPI simulator;
	private Queue<Point> path = new LinkedList<Point>();

	public AbstractTruckAgent(Truck truck) {
		this.truck = truck;
	}

	protected Truck getTruck() {
		return truck;
	}

	protected Queue<Point> getPath() {
		return path;
	}

	protected void setPath(Queue<Point> path) {
		if (path == null) {
			this.path.clear();
		} else {
			this.path = path;
		}
	}

	@Override
	public void setSimulator(SimulatorAPI simulator) {
		this.simulator = simulator;
	}

	protected SimulatorAPI getSimulator() {
		return simulator;
	}

	@Override
	public void tick(long currentTime, long timeStep) {}

	@Override
	public void afterTick(long currentTime, long timeStep) {}

}
