package project.strategies.delegatemas;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import rinde.sim.core.SimulatorAPI;
import rinde.sim.core.SimulatorUser;
import rinde.sim.core.TickListener;
import rinde.sim.core.graph.Point;
import rinde.sim.core.model.RoadModel;

public class FeasibilityAnt implements TickListener, SimulatorUser {

    private SimulatorAPI simulator;
    private Ant ant;
    private Queue<Point> path;

    public FeasibilityAnt(FeasibilityAnt feasibilityAnt, Point destination, int hopsLeft) {
	this.path = new LinkedList<Point>();
	this.path.add(destination);
	// this.path.add(feasibilityAnt.getAnt().getEndLocation());
	this.ant = new Ant(feasibilityAnt.getAnt(), destination, hopsLeft);
	this.ant.initRoadUser(feasibilityAnt.getAnt().getRoadModel());

    }

    public FeasibilityAnt(Point startPosition, Point destination, int maxHops, RoadModel model) {
	this.path = new LinkedList<Point>();
	this.path.add(destination);
	this.ant = new Ant(startPosition, destination, maxHops);
	this.ant.initRoadUser(model);
    }

    @Override
    public void setSimulator(SimulatorAPI api) {
	if (api == null)
	    throw new IllegalArgumentException();

	if (this.simulator != null) {
	    this.simulator.unregister(this);
	} else {
	    this.simulator = api;
	    this.simulator.register(this);
	}

    }

    @Override
    public void tick(long currentTime, long timeStep) {
	if (!path.isEmpty()) {
	    ant.drive(path, timeStep);
	} else {

	    ArrayList<Point> pastPath = ant.getPastPath();
	    Point destination = ant.getEndLocation();
	    pastPath.add(destination);
	    ant.setPastPath(pastPath);

	    if (ant.getHopsLeft() != 0) {
		Collection<Point> outgoings = ant.getRoadModel().getGraph().getOutgoingConnections(destination);
		for (Point outgoing : outgoings) {
		    if (!ant.getPastPath().contains(outgoing)) {
			FeasibilityAnt feasibilityAnt = new FeasibilityAnt(this, outgoing, ant.getHopsLeft() - 1);
			feasibilityAnt.setSimulator(simulator);
		    }
		}
	    }
	    terminate();
	}
    }

    @Override
    public void afterTick(long currentTime, long timeStep) {
	// DO nothing
    }

    public void terminate() {
	ant.terminate();
	if (this.simulator != null) {
	    this.simulator.unregister(this);
	}
    }

    public Ant getAnt() {
	return this.ant;
    }

    public List<Point> getPastPath() {
	return ant.getPastPath();
    }
}
