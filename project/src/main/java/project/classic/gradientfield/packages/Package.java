package project.classic.gradientfield.packages;

import project.classic.gradientfield.exceptions.AlreadyPickedUpException;
import rinde.sim.core.SimulatorAPI;
import rinde.sim.core.SimulatorUser;
import rinde.sim.core.graph.Point;
import rinde.sim.core.model.RoadModel;
import rinde.sim.core.model.RoadUser;

public class Package implements SimulatorUser, RoadUser {
	public final int id;
	private Point pickupLocation;
	private Point deliveryLocation;
	private boolean pickedUp = false;
	private boolean delivered = false;
	private SimulatorAPI simulator;
	// TODO: use the random generator of the simulator
	private Priority priority = Priority.LOW;

	public Package(int id, Point pickupLocation, Point deliveryLocation) {
		this.id = id;
		this.pickupLocation = pickupLocation;
		this.deliveryLocation = deliveryLocation;
	}

	public boolean isPickedUp() {
		return pickedUp;
	}

	public boolean isDelivered() {
		return delivered;
	}

	/**
	 * The object is removed after afterTick(). You have to check whether a
	 * package is already picked up because two agents can pickup the same
	 * package otherwise.
	 * @throws AlreadyPickedUpException if a package is already picked up
	 */
	public void pickup() throws AlreadyPickedUpException {
		if (isPickedUp()) {
			throw new AlreadyPickedUpException();
		}

		setPickedUp();
		//this.simulator.unregister(this);
	}

	private void setPickedUp() {
		this.pickedUp = true;
	}

	public void deliver() {
		setDelivered();
	}

	private void setDelivered() {
		this.delivered = true;
	}

	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return "package-" + id;
	}

	public Point getPickupLocation() {
		return pickupLocation;
	}

	public Point getDeliveryLocation() {
		return deliveryLocation;
	}

	@Override
	public void setSimulator(SimulatorAPI api) {
		this.simulator = api;

		if (simulator != null) {
			setPriority(Priority.valueOf(simulator.getRandomGenerator().nextInt()));
		}
	}

	private void setPriority(Priority priority) {
		this.priority = priority;
	}

	@Override
	public void initRoadUser(RoadModel model) {
		model.addObjectAt(this, pickupLocation);
	}

	public Priority getPriority() {
		return priority;
	}
}