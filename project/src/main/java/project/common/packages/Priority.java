package project.common.packages;

import org.apache.commons.math.random.RandomGenerator;

public enum Priority {
    LOW, MEDIUM, HIGH;

    public static Priority valueOf(double input) {
	return values()[(int) Math.log10(input)];
    }

    public double getValue() {
	return Math.pow(10, ordinal());
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
