package project.classic.gradientfield.packages;

import java.util.Random;

import rinde.sim.core.SimulatorAPI;
import rinde.sim.core.SimulatorUser;
import rinde.sim.core.graph.Point;
import rinde.sim.core.model.RoadModel;
import rinde.sim.core.model.RoadUser;

public class Package implements SimulatorUser, RoadUser {
	private static final Random random = new Random(1);
	public final String packageID;
	private Point pickupLocation;
	private DeliveryLocation deliveryLocation;
	private boolean pickedUp = false;
	private boolean delivered = false;
	private SimulatorAPI simulator;
	private double priority = random.nextInt(3);

	public Package(String packageID, Point pickupLocation, DeliveryLocation deliveryLocation) {
		this.packageID = packageID;
		this.pickupLocation = pickupLocation;
		this.deliveryLocation = deliveryLocation;
	}

	public boolean needsPickUp() {
		return !pickedUp;
	}

	public boolean delivered() {
		return delivered;
	}

	public void pickup() {
		this.pickedUp = true;
		this.simulator.unregister(this);
	}

	public void deliver() {
		this.delivered = true;
		this.simulator.unregister(deliveryLocation);
	}

	public String getPackageID() {
		return packageID;
	}

	@Override
	public String toString() {
		return packageID;
	}

	public Point getPickupLocation() {
		return pickupLocation;
	}

	public Point getDeliveryLocation() {
		return deliveryLocation.getPosition();
	}

	@Override
	public void setSimulator(SimulatorAPI api) {
		this.simulator = api;
	}

	@Override
	public void initRoadUser(RoadModel model) {
		model.addObjectAt(this, pickupLocation);
	}

	public double getPriority() {
		return priority;
	}

}
