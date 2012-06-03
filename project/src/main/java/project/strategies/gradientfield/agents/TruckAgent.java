package project.strategies.gradientfield.agents;

import java.util.LinkedList;

import project.common.trucks.AbstractTruckAgent;
import project.common.trucks.Truck;
import rinde.sim.core.graph.Point;
import rinde.sim.core.model.virtual.Field;
import rinde.sim.core.model.virtual.FieldData;
import rinde.sim.core.model.virtual.GradientFieldAPI;
import rinde.sim.core.model.virtual.VirtualEntity;

public class TruckAgent extends AbstractTruckAgent implements VirtualEntity {

	private GradientFieldAPI gradientFieldModel;
	private boolean emitting = true;

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
		if (!getPath().isEmpty()) {
			followPath(timeStep);
		} else {
			if (getTruck().hasLoad()) {
				tryDelivery();
			} else {
				tryPickup();
			}
		}
	}

	private void tryPickup() {
		if (getTruck().tryPickup()) {
			setEmitting(false);
			setPathToDeliveryLocation();
		} else {
			// Try next node
			setPathToBestNode();
		}
	}

	private void tryDelivery() {
		if (getTruck().tryDelivery()) {
			setEmitting(true);
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
		for (Point node : getTruck().getRoadModel().getGraph().getOutgoingConnections(getTruck().getPosition())) {
			double strength = calculateFieldStrength(node);
			if (strength > maxStrength) {
				maxStrength = strength;
				highestStrengthNode = node;
			}
		}
		if (highestStrengthNode != null) {
			setPath(new LinkedList<Point>(getTruck().getRoadModel().getShortestPathTo(getTruck(), highestStrengthNode)));
		}
	}

	private void setPathToDeliveryLocation() {
		setPath(new LinkedList<Point>(getTruck().getRoadModel().getShortestPathTo(getTruck(), getTruck().getLoad()
				.getDeliveryLocation())));
	}

	private void setEmitting(boolean b) {
		this.emitting = b;
	}

	protected double calculateFieldStrength(Point node) {
		double value = 0;
		for (Field field : gradientFieldModel.getFields(node)) {
			value += field.getFieldData().getStrength() / field.getDistance();
		}
		return value;
	}

	public double getFieldStrength() {
		return calculateFieldStrength(getPosition());
	}

	protected void pickNewPickupLocation() {
		setPathToBestNode();
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