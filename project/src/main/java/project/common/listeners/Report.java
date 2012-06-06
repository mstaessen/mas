package project.common.listeners;

public class Report {
    private int deliveredPackages;
    private double avgPickupLateness;
    private double avgDeliveryLateness;
    private double avgCompletionTime;
    private double avgDistance;

    public int getDeliveredPackages() {
	return deliveredPackages;
    }

    public void setDeliveredPackages(int deliveredPackages) {
	this.deliveredPackages = deliveredPackages;
    }

    public double getAvgPickupLateness() {
	return avgPickupLateness;
    }

    public void setAvgPickupLateness(double avgPickupLateness) {
	this.avgPickupLateness = avgPickupLateness;
    }

    public double getAvgDeliveryLateness() {
	return avgDeliveryLateness;
    }

    public void setAvgDeliveryLateness(double avgDeliveryLateness) {
	this.avgDeliveryLateness = avgDeliveryLateness;
    }

    public double getAvgCompletionTime() {
	return avgCompletionTime;
    }

    public void setAvgCompletionTime(double avgCompletionTime) {
	this.avgCompletionTime = avgCompletionTime;
    }

    public double getAvgDistance() {
	return avgDistance;
    }

    public void setAvgDistance(double avgDistance) {
	this.avgDistance = avgDistance;
    }
}
