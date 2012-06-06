package project.common.packages;

import org.apache.commons.math.random.RandomGenerator;

public enum Priority {
    LOW, MEDIUM, HIGH;

    public static Priority valueOf(double input) {
	if (input < 0 || input > 1) {
	    throw new IllegalArgumentException("Priority must be between 0 and 1.");
	}

	return values()[(int) Math.round(2 * input)];
    }

    public double getValue() {
	return ordinal() * 0.5;
    }

    public static Priority random(RandomGenerator random) {
	return valueOf(random.nextInt());
    }

    public static double minPriority() {
	return values()[0].getValue();
    }

    public static double maxPriority() {
	return values()[values().length - 1].getValue();
    }
}
