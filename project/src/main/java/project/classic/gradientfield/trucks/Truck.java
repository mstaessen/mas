package project.classic.gradientfield.trucks;

import java.util.Queue;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import project.classic.gradientfield.exceptions.AlreadyPickedUpException;
import project.classic.gradientfield.packages.Package;
import rinde.sim.core.graph.Point;
import rinde.sim.core.model.MovingRoadUser;
import rinde.sim.core.model.RoadModel;
import rinde.sim.core.model.RoadModel.PathProgress;

public class Truck implements MovingRoadUser {

	protected static final Logger LOGGER = LoggerFactory.getLogger(Truck.class);
	private RoadModel rm;
	private Point startLocation;
	private int id;
	private double speed;
	private Package load;

	public Truck(int id, Point startLocation, double speed) {
		this.id = id;
		this.startLocation = startLocation;
		this.speed = speed;
	}

	public int getId() {
		return id;
	}

	@Override
	public void initRoadUser(RoadModel model) {
		this.rm = model;
		this.rm.addObjectAt(this, startLocation);
	}

	@Override
	public double getSpeed() {
		return speed;
	}

	public RoadModel getRoadModel() {
		return rm;
	}

	public PathProgress drive(Queue<Point> path, long time) {
		return this.rm.followPath(this, path, time);
	}

	public Point getPosition() {
		return rm.getPosition(this);
	}

	public Point getLastCrossRoad() {
		return rm.getLastCrossRoad(this);
	}

	public boolean hasLoad() {
		return load != null;
	}

	public Package getLoad() {
		return this.load;
	}

	public boolean tryPickup() {
		if (!hasLoad()) {
			Set<Package> packages = rm.getObjectsAt(this, Package.class);
			if (!packages.isEmpty()) {
				Package p = (Package) packages.toArray()[0];
				try {
					p.pickup();
					load = p;
					LOGGER.info(this + " picked up " + p);
					return true;
				} catch (AlreadyPickedUpException e) {
					LOGGER.info("Package " + p.getId() + " is already picked up");
				}
			}
		}
		return false;
	}

	public boolean tryDelivery() {
		if (load != null) {
			if (load.getDeliveryLocation().equals(this.getPosition())) {
				LOGGER.info(this + " delivered " + load);
				load.deliver();
				load = null;
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "truck-" + getId();
	}
}
