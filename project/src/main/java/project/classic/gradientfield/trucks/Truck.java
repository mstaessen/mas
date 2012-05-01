package project.classic.gradientfield.trucks;

import java.util.Queue;
import java.util.Set;

import project.classic.gradientfield.exceptions.AlreadyPickedUpException;
import project.classic.gradientfield.packages.Package;
import rinde.sim.core.graph.Point;
import rinde.sim.core.model.MovingRoadUser;
import rinde.sim.core.model.RoadModel;
import rinde.sim.core.model.RoadModel.PathProgress;

public class Truck implements MovingRoadUser {
	private static int counter = 0;

	private int id;
	private Point startLocation;
	/**
	 * 30km/h = 30000/3600 ~ 7
	 */
	private double speed = 7d;
	private Package load;

	private RoadModel rm;

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

	protected RoadModel getRoadModel() {
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
