package project.classic.gradientfield.packages;

public enum Priority {
	LOW, MEDIUM, HIGH;

	public static Priority valueOf(int input) {
		return values()[Math.abs(input) % values().length];
	}
	
	public double getValue() {
		return ordinal() + 1;
	}
}
