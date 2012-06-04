package project.common.packages;

import org.apache.commons.math.random.RandomGenerator;

public enum Priority {
    LOW, MEDIUM, HIGH;

    public static Priority valueOf(int input) {
	return values()[Math.abs(input) % values().length];
    }

    public double getValue() {
	return Math.pow(10, ordinal());
    }

    public static Priority random(RandomGenerator random) {
	return valueOf(random.nextInt());
    }
}
