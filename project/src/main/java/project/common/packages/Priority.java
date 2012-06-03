package project.common.packages;

import org.apache.commons.math.random.RandomGenerator;

public enum Priority {
	LOW, MEDIUM, HIGH;

	public static Priority valueOf(int input) {
		return values()[Math.abs(input) % values().length];
	}

	public double getValue() {
		return ordinal() + 1;
	}

	public static Priority random(RandomGenerator random) {
		return valueOf(random.nextInt());
	}
}
