package project.strategies.delegatemas;

import rinde.sim.core.graph.Point;
import java.util.ArrayList;
import java.util.Queue;

import rinde.sim.core.model.MovingRoadUser;
import rinde.sim.core.model.RoadModel;
import rinde.sim.core.model.RoadModel.PathProgress;

public class Ant implements MovingRoadUser {

    public static double DEFAULT_ANT_SPEED = 2;

    private RoadModel rm;
    private Point startLocation;
    private Point endLocation;
    private ArrayList<Point> pastPath;
    private int hopsLeft;

    @SuppressWarnings("unchecked")
    public Ant(Ant ant, Point endLocation, int hopsLeft) {
	this.hopsLeft = hopsLeft;
	this.startLocation = ant.getEndLocation();
	this.endLocation = endLocation;
	this.pastPath = (ArrayList<Point>) ant.getPastPath().clone();

    }

    public Ant(Point origin, Point endLocation, int maxHops) {
	this.hopsLeft = maxHops;
	this.startLocation = origin;
	this.endLocation = endLocation;
	pastPath = new ArrayList<Point>();
	pastPath.add(origin);

    }

    public Point getEndLocation() {
	return this.endLocation;
    }

    public ArrayList<Point> getPastPath() {
	return pastPath;
    }

    @Override
    public void initRoadUser(RoadModel model) {
	this.rm = model;
	this.rm.addObjectAt(this, startLocation);
    }

    public PathProgress drive(Queue<Point> path, long time) {
	return this.rm.followPath(this, path, time);
    }

    public RoadModel getRoadModel() {
	return rm;
    }

    public void setPastPath(ArrayList<Point> path) {
	this.pastPath = path;
    }

    public int getHopsLeft() {
	return hopsLeft;
    }

    public void setHopsLeft(int hopsLeft) {
	this.hopsLeft = hopsLeft;
    }

    public void terminate() {
	if (rm != null)
	    this.rm.removeObject(this);
    }

    @Override
    public double getSpeed() {
	return DEFAULT_ANT_SPEED;
    }

}
