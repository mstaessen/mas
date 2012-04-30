package project.classic.gradientfield.trucks;

import java.util.LinkedList;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rinde.sim.core.SimulatorAPI;
import rinde.sim.core.SimulatorUser;
import rinde.sim.core.TickListener;
import rinde.sim.core.graph.Point;
import rinde.sim.core.model.virtual.Field;
import rinde.sim.core.model.virtual.FieldData;
import rinde.sim.core.model.virtual.GradientFieldAPI;
import rinde.sim.core.model.virtual.VirtualEntity;

public class TruckAgent implements TickListener, SimulatorUser, VirtualEntity {

	private static final Logger LOGGER = LoggerFactory.getLogger("TruckAgent");
	private final Truck truck;
	private GradientFieldAPI gfApi;
	private SimulatorAPI simulator;

	private Queue<Point> path = new LinkedList<Point>();

	private boolean emitting = true;

	public TruckAgent(Truck truck, int timerInterval) {
		this.truck = truck;
	}

	@Override
	public void setSimulator(SimulatorAPI api) {
		this.simulator = api;
	}

	@Override
	public void init(GradientFieldAPI api) {
		this.gfApi = api;
	}

	/**
	 * Very dumb agent, that chooses paths randomly and tries to pickup stuff
	 * and deliver stuff at the end of his paths
	 */
	@Override
	public void tick(long currentTime, long timeStep) {
		if (!path.isEmpty()) {
			followPath(timeStep);
		} else {
			if (truck.hasLoad()) {
				tryDelivery();
			} else {
				tryPickup();
			}
		}
	}

	private void tryPickup() {
		if (truck.tryPickup()) {
			setEmitting(false);
			setPathToDeliveryLocation();
		} else {
			// Try next node
			setPathToBestNode();
		}
	}

	private void tryDelivery() {
		if (truck.tryDelivery()) {
			setEmitting(true);
			setPathToBestNode();
		} else {
			setPathToDeliveryLocation();
		}
	}

	private void followPath(long timeStep) {
		truck.drive(path, timeStep);
	}

	private void setPathToBestNode() {
		double maxStrength = Double.NEGATIVE_INFINITY;
		Point highestStrengthNode = null;
		for (Point node : truck.getRoadModel().getGraph().getOutgoingConnections(truck.getPosition())) {
			double strength = calculateFieldStrength(node);
			LOGGER.info(truck.getId() + ": Strength(" + node + ") = " + strength);
			if (strength > maxStrength) {
				maxStrength = strength;
				highestStrengthNode = node;
			}
		}
		if (highestStrengthNode != null) {
			path = new LinkedList<Point>(truck.getRoadModel().getShortestPathTo(truck, highestStrengthNode));
		}
	}

	private void setPathToDeliveryLocation() {
		this.path = new LinkedList<Point>(truck.getRoadModel().getShortestPathTo(truck, truck.getLoad()
				.getDeliveryLocation()));
	}

	private void setEmitting(boolean b) {
		this.emitting = b;
	}

	protected double calculateFieldStrength(Point node) {
		double value = 0;
		for (Field field : gfApi.getFields(node)) {
			value += field.getFieldData().getStrength() / field.getDistance();
		}
		return value;
	}

	public double getFieldStrength() {
		return calculateFieldStrength(getPosition());
	}

	protected void pickNewPickupLocation() {
		setPathToBestNode();
	}

	@Override
	public void afterTick(long currentTime, long timeStep) {
		// unused
	}

	@Override
	public boolean isEmitting() {
		return emitting;
	}

	@Override
	public Point getPosition() {
		return truck.getPosition();
	}

	@Override
	public FieldData getFieldData() {
		return new FieldData() {
			@Override
			public double getStrength() {
				return -1d;
			}
		};
	}

	@Override
	public String toString() {
		return "truckAgent-" + truck.getId();
	}
}