package project.common.trucks;

import java.util.Queue;
import java.util.Set;

import project.common.exceptions.AlreadyPickedUpException;
import project.common.packages.Package;
import rinde.sim.core.graph.Point;
import rinde.sim.core.model.MovingRoadUser;
import rinde.sim.core.model.RoadModel;
import rinde.sim.core.model.RoadModel.PathProgress;

public class Truck implements MovingRoadUser {

    /**
     * Average speed of 30km/h = 30000m / 3600s ~ 7
     */
    public static final double SPEED = 7d;
    private double speed = SPEED;
    private int id;
    private Point startLocation;
    private Package load = null;
    private RoadModel rm;
    private static int counter = 0;

    public Truck(Point startLocation) {
	this.id = counter++;
	this.startLocation = startLocation;
    }

    public Truck(Point startLocation, double speed) {
	this(startLocation);
	setSpeed(speed);
    }

    @Override
    public void initRoadUser(RoadModel model) {
	this.rm = model;
	this.rm.addObjectAt(this, startLocation);
    }

    public int getId() {
	return id;
    }

    @Override
    public double getSpeed() {
	return speed;
    }

    public void setSpeed(double speed) {
	if (speed >= 0) {
	    this.speed = speed;
	}
    }

    public RoadModel getRoadModel() {
	return rm;
    }

    public PathProgress drive(Queue<Point> path, long time) {
	return getRoadModel().followPath(this, path, time);
    }

    public Point getPosition() {
	return getRoadModel().getPosition(this);
    }

    public Point getLastCrossRoad() {
	return getRoadModel().getLastCrossRoad(this);
    }

    public boolean hasLoad() {
	return load != null;
    }

    public Package getLoad() {
	return this.load;
    }

    @Override
    public String toString() {
	return "truck-" + getId();
    }

    public boolean tryPickup() {
	if (!hasLoad()) {
	    Set<Package> packages = rm.getObjectsAt(this, Package.class);
	    if (!packages.isEmpty()) {
		Package p = (Package) packages.toArray()[0];
		try {
		    p.pickup();
		    load = p;
		    return true;
		} catch (AlreadyPickedUpException e) {
		    // No package for you!
		}
	    }
	}
	return false;
    }

    public boolean tryPickup(Package pkg) {
	if (!hasLoad()) {
	    Set<Package> packages = rm.getObjectsAt(this, Package.class);
	    if (packages.contains(pkg)) {
		try {
		    pkg.pickup();
		    load = pkg;
		    return true;
		} catch (AlreadyPickedUpException e) {
		    // No package for you!
		}
	    }
	}
	return false;
    }

    public boolean tryDelivery() {
	if (load != null) {
	    if (load.getDeliveryLocation().equals(this.getPosition())) {
		load.deliver();
		load = null;
		return true;
	    }
	}
	return false;
    }
}
