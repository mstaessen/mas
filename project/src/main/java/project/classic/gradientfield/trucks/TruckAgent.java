package project.classic.gradientfield.trucks;

import java.util.LinkedList;
import java.util.Queue;

import project.classic.gradientfield.packages.Package;
import rinde.sim.core.SimulatorAPI;
import rinde.sim.core.SimulatorUser;
import rinde.sim.core.TickListener;
import rinde.sim.core.graph.Graphs;
import rinde.sim.core.graph.Point;
import rinde.sim.core.model.virtual.Field;
import rinde.sim.core.model.virtual.FieldData;
import rinde.sim.core.model.virtual.GradientFieldAPI;
import rinde.sim.core.model.virtual.VirtualEntity;

public class TruckAgent implements TickListener, SimulatorUser, VirtualEntity {

	private final Truck truck;
	private GradientFieldAPI gfApi;
	private SimulatorAPI simulator;

	private double currentFieldsValue = 0;
	private double lastKnownFieldValue = 0;
	private Queue<Point> path = new LinkedList<Point>();

	private boolean emitting = true;

	public TruckAgent(Truck truck, int timerInterval) {
		this.truck = truck;
		truck.setAgent(this);
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
		// We have a load, we are delivering
		if (truck.hasLoad()) {
			if (path == null || path.isEmpty()) {
				// We have arrived. Drop the load and find a new one.
				truck.tryDelivery();
			} else {
				truck.drive(path, timeStep);
			}
		}
		// We don't have a load. We are driving towards one.
		else {
			currentFieldsValue = calculateFieldsValue();

			// Somebody else is coming too close (too much negativity)
			if (currentFieldsValue < lastKnownFieldValue) {
				pickNewPickupLocation();
			} else {
				if (path == null || path.isEmpty()) {
					// Maybe there is something to pickup here?
					if (truck.tryPickup()) {
						navigateToDeliveryLocation();
					}
					// There is not. Find me a load to pick up!
					else {
						// Drive somewhere new...
						pickNewPickupLocation();
					}
				} else {
					truck.drive(path, timeStep);
				}
			}

			// Replace old value with current.
			lastKnownFieldValue = currentFieldsValue;
		}
	}

	private void navigateToDeliveryLocation() {
		setEmitting(false);
		this.path = new LinkedList<Point>(truck.getRoadModel().getShortestPathTo(truck, truck.getLoad()
				.getDeliveryLocation()));
	}

	private void setEmitting(boolean b) {
		this.emitting = false;
	}

	private void setEmitting() {
		this.emitting = true;
	}

	protected double calculateFieldsValue() {
		double value = 0;
		for (Field field : gfApi.getFields(truck.getPosition())) {
			value += field.getHeuristicValue();
		}
		return value;
	}

	// TODO: Trucks should drive towards the packages with the highest
	// attraction, not the packages closest by
	protected void pickNewPickupLocation() {
		Package p = Graphs.findClosestObject(truck.getPosition(), truck.getRoadModel(), Package.class);
		if (p != null) {
			this.path = new LinkedList<Point>(truck.getRoadModel().getShortestPathTo(truck, p));
			setEmitting();
		}
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
				return -truck.getSpeed();
			}
		};
	}

	public double getCurrentFieldsValue() {
		return currentFieldsValue;
	}
}