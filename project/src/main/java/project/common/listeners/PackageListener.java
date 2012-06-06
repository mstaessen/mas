package project.common.listeners;

import java.util.HashMap;
import java.util.Map;

import project.common.packages.Package;
import rinde.sim.core.Simulator;
import rinde.sim.event.Event;
import rinde.sim.event.Listener;

public class PackageListener implements Listener {

    private final Simulator simulator;
    private Map<Integer, Long> creationTimes = new HashMap<Integer, Long>();
    private Map<Integer, Long> pickupTimes = new HashMap<Integer, Long>();
    private Map<Integer, Long> deliveryTimes = new HashMap<Integer, Long>();
    private Map<Integer, Long> deliveryLatenesses = new HashMap<Integer, Long>();

    public PackageListener(Simulator simulator) {
	this.simulator = simulator;
    }

    @Override
    public void handleEvent(Event e) {
	handlePackageEvent(e);
    }

    private void handlePackageEvent(Event e) {
	switch (Package.EventType.valueOf(e.getEventType().ordinal())) {
	    case PACKAGE_CREATION:
		handleCreationEvent(e);
		break;
	    case PACKAGE_PICKUP:
		handlePickupEvent(e);
		break;
	    case PACKAGE_DELIVERY:
		handleDeliveryEvent(e);
		break;
	}
    }

    public Simulator getSimulator() {
	return simulator;
    }

    private void handleDeliveryEvent(Event e) {
	deliveryTimes.put(((Package) e.getIssuer()).getId(), getSimulator().getCurrentTime()
		/ getSimulator().getTimeStep());
	deliveryLatenesses.put(((Package) e.getIssuer()).getId(), ((Package) e.getIssuer()).getDeadline()
		/ getSimulator().getTimeStep());
    }

    private void handlePickupEvent(Event e) {
	pickupTimes.put(((Package) e.getIssuer()).getId(), getSimulator().getCurrentTime()
		/ getSimulator().getTimeStep());
    }

    private void handleCreationEvent(Event e) {
	creationTimes.put(((Package) e.getIssuer()).getId(), getSimulator().getCurrentTime()
		/ getSimulator().getTimeStep());
    }

    public Report generateReport() {
	Report report = new Report();
	report.setDeliveredPackages(deliveryTimes.size());
	double avgPickupLateness = 0;
	for (int pid : pickupTimes.keySet()) {
	    avgPickupLateness += pickupTimes.get(pid) - creationTimes.get(pid);
	}
	report.setAvgPickupLateness(avgPickupLateness / pickupTimes.size());

	double avgDeliveryLateness = 0;
	for (int pid : deliveryLatenesses.keySet()) {
	    avgDeliveryLateness += deliveryLatenesses.get(pid);
	}
	report.setAvgDeliveryLateness(avgDeliveryLateness / deliveryLatenesses.size());

	double avgCompletionTime = 0;
	for (int pid : deliveryTimes.keySet()) {
	    avgCompletionTime += deliveryTimes.get(pid) - creationTimes.get(pid);
	}
	report.setAvgCompletionTime(avgCompletionTime / deliveryTimes.size());
	return report;
    }
}
