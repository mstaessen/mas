package project.strategies.gradientfield.agents;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import project.common.trucks.AbstractTruckAgent;
import project.common.trucks.Truck;
import rinde.sim.core.graph.Point;
import rinde.sim.core.model.virtual.Field;
import rinde.sim.core.model.virtual.FieldData;
import rinde.sim.core.model.virtual.GradientFieldAPI;
import rinde.sim.core.model.virtual.VirtualEntity;

public class TruckAgent extends AbstractTruckAgent implements VirtualEntity {

    private GradientFieldAPI gradientFieldModel;

    public TruckAgent(Truck truck) {
	super(truck);
    }

    @Override
    public void init(GradientFieldAPI model) {
	this.gradientFieldModel = model;
    }

    /**
     * Very dumb agent, that chooses paths randomly and tries to pickup stuff
     * and deliver stuff at the end of his paths
     */
    @Override
    public void tick(long currentTime, long timeStep) {
	// Drive the remaining path
	if (!getPath().isEmpty()) {
	    followPath(timeStep);
	}

	if (getPath().isEmpty()) {
	    // If the truck is loaded, unload
	    if (getTruck().hasLoad()) {
		tryDelivery();
	    }
	    // Don't use "else"!
	    // Like his, you can pickup and deliver in the same tick
	    if (!getTruck().hasLoad()) {
		tryPickup();
	    }
	}
    }

    private void tryPickup() {
	if (getTruck().tryPickup()) {
	    setPathToDeliveryLocation();
	} else {
	    // Try next node
	    setPathToBestNode();
	}
    }

    private void tryDelivery() {
	if (getTruck().tryDelivery()) {
	    setPathToBestNode();
	} else {
	    setPathToDeliveryLocation();
	}
    }

    private void followPath(long timeStep) {
	getTruck().drive(getPath(), timeStep);
    }

    private void setPathToBestNode() {
	double maxStrength = Double.NEGATIVE_INFINITY;
	Point highestStrengthNode = null;
	Map<Point, Double> options = getNodes();
	for (Point node : options.keySet()) {
	    if (options.get(node) > maxStrength) {
		maxStrength = options.get(node);
		highestStrengthNode = node;
	    }
	}
	if (highestStrengthNode != null) {
	    setPath(new LinkedList<Point>(getTruck().getRoadModel().getShortestPathTo(getTruck(), highestStrengthNode)));
	}
    }

    private void setPathToDeliveryLocation() {
	setPath(new LinkedList<Point>(getTruck().getRoadModel().getShortestPathTo(getTruck(),
		getTruck().getLoad().getDeliveryLocation())));
    }

    protected double calculateFieldStrength(Point node) {
	double value = 0;
	for (Field field : gradientFieldModel.getFields(node)) {
	    value += field.getFieldData().getStrength() / (1 + field.getDistance());
	}
	return value;
    }

    public double getFieldStrength() {
	return calculateFieldStrength(getPosition());
    }

    protected void pickNewPickupLocation() {
	setPathToBestNode();
    }

    public Map<Point, Double> getNodes() {
	Collection<Point> outgoingConnections = getTruck().getRoadModel().getGraph()
		.getOutgoingConnections(getPosition());
	Map<Point, Double> result = new HashMap<Point, Double>(outgoingConnections.size());
	for (Point node : outgoingConnections) {
	    result.put(node, calculateFieldStrength(node));
	}
	return result;
    }

    @Override
    public void afterTick(long currentTime, long timeStep) {
	// unused
    }

    @Override
    public boolean isEmitting() {
	return !getTruck().hasLoad();
    }

    @Override
    public Point getPosition() {
	return getTruck().getPosition();
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
	return "truckAgent-" + getTruck().getId();
    }
}