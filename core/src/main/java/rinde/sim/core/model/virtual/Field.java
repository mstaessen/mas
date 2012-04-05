package rinde.sim.core.model.virtual;

public class Field {

	private FieldData fieldData;
	private double distance;
	private static final double AMPLIFIER = 1000;

	public Field(FieldData fieldData, double distance) {
		this.fieldData = fieldData;
		this.distance = distance;
	}

	public FieldData getFieldData() {
		return fieldData;
	}

	public double getDistance() {
		return distance;
	}

	public double getHeuristicValue() {
		return fieldData.getStrength() / (distance) * AMPLIFIER;
	}
}
