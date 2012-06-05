package project.common.packages;

import project.common.exceptions.AlreadyPickedUpException;
import rinde.sim.core.SimulatorAPI;
import rinde.sim.core.SimulatorUser;
import rinde.sim.core.graph.Point;
import rinde.sim.core.model.RoadModel;
import rinde.sim.core.model.RoadUser;
import rinde.sim.event.Event;
import rinde.sim.event.EventDispatcher;
import rinde.sim.event.Events;
import rinde.sim.event.Listener;

public class Package implements SimulatorUser, RoadUser, Events {
    private static int counter = 0;

    private final int id;
    public final EventDispatcher events = new EventDispatcher(Package.EventType.values());

    private Point pickupLocation;
    private Point deliveryLocation;
    private boolean pickedUp = false;
    private boolean delivered = false;
    private SimulatorAPI simulator;
    private RoadModel roadModel;

    private long deadline = 2 * 24 * 60 * 60 * 1000;
    public static final long OFFSET = 3 * 24 * 60 * 60 * 1000;

    public enum EventType {
	PACKAGE_CREATION, PACKAGE_PICKUP, PACKAGE_DELIVERY;

	public static EventType valueOf(int ordinal) {
	    return values()[ordinal];
	}
    }

    public Package(Point pickupLocation, Point deliveryLocation) {
	this.id = counter++;
	this.pickupLocation = pickupLocation;
	this.deliveryLocation = deliveryLocation;
    }

    public Package(Point pickupLocation, Point deliveryLocation, long deadline) {
	this(pickupLocation, deliveryLocation);

	this.deadline = deadline;
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
     * 
     * @throws AlreadyPickedUpException
     *             if a package is already picked up
     */
    public void pickup() throws AlreadyPickedUpException {
	if (isPickedUp()) {
	    throw new AlreadyPickedUpException();
	}

	setPickedUp();
	events.dispatchEvent(new Event(Package.EventType.PACKAGE_PICKUP, this));
    }

    private void setPickedUp() {
	this.pickedUp = true;
    }

    public void deliver() {
	setDelivered();
	events.dispatchEvent(new Event(Package.EventType.PACKAGE_DELIVERY, this));
	this.simulator.unregister(this);
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
    }

    @Override
    public void initRoadUser(RoadModel model) {
	model.addObjectAt(this, pickupLocation);
	this.roadModel = model;
    }
    
    public RoadModel getRoadModel() {
	return roadModel;
    }

    public long getDeadline() {
	return deadline;
    }

    protected SimulatorAPI getSimulator() {
	return simulator;
    }

    @Override
    public void addListener(Listener l, Enum<?>... eventTypes) {
	events.addListener(l, eventTypes);
    }

    @Override
    public void removeListener(Listener l, Enum<?>... eventTypes) {
	events.removeListener(l, eventTypes);
    }

    @Override
    public boolean containsListener(Listener l, Enum<?> eventType) {
	return events.containsListener(l, eventType);
    }

    void decreaseDeadline(long timeStep) {
	this.deadline -= timeStep;
    }

    public double getPriority() {
	// Less then half a day!
	if (getDeadline() <= 0) {
	    return Priority.maxPriority();
	} else if (getDeadline() >= OFFSET ) {
	    return Priority.minPriority();
	} else {
	    return Math.pow(getDeadline() - OFFSET, 2) / Math.pow(259200000, 2);
	}
    }
}