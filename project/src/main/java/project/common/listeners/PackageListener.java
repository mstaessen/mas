package project.common.listeners;

import java.util.HashMap;

import project.common.packages.Package;
import rinde.sim.core.Simulator;
import rinde.sim.event.Event;
import rinde.sim.event.Listener;

public class PackageListener implements Listener {

	private final Simulator simulator;
	private HashMap<Integer, Long> creationTimes = new HashMap<Integer, Long>();
	private HashMap<Integer, Long> pickupTimes = new HashMap<Integer, Long>();
	private HashMap<Integer, Long> deliveryTimes = new HashMap<Integer, Long>();

	public PackageListener(Simulator simulator) {
		this.simulator = simulator;
	}

	@Override
	public void handleEvent(Event e) {
		if (e.getIssuer() == getSimulator()) {
			handleSimulatorEvent(e);
		} else {
			handlePackageEvent(e);
		}
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

	private void handleSimulatorEvent(Event e) {
		generateReport();
	}

	public HashMap<Integer, Long> getCreationTimes() {
		return creationTimes;
	}

	public void setCreationTimes(HashMap<Integer, Long> creationTimes) {
		this.creationTimes = creationTimes;
	}

	public HashMap<Integer, Long> getPickupTimes() {
		return pickupTimes;
	}

	public void setPickupTimes(HashMap<Integer, Long> pickupTimes) {
		this.pickupTimes = pickupTimes;
	}

	public HashMap<Integer, Long> getDeliveryTimes() {
		return deliveryTimes;
	}

	public void setDeliveryTimes(HashMap<Integer, Long> deliveryTimes) {
		this.deliveryTimes = deliveryTimes;
	}

	public Simulator getSimulator() {
		return simulator;
	}

	private void handleDeliveryEvent(Event e) {
		getDeliveryTimes().put(((Package) e.getIssuer()).getId(), getSimulator().getCurrentTime());
	}

	private void handlePickupEvent(Event e) {
		getPickupTimes().put(((Package) e.getIssuer()).getId(), getSimulator().getCurrentTime());
	}

	private void handleCreationEvent(Event e) {
		getCreationTimes().put(((Package) e.getIssuer()).getId(), getSimulator().getCurrentTime());
	}

	// TODO: Work out
	public void generateReport() {
		System.out.print("PID");
		System.out.print("\t");
		System.out.print("created");
		System.out.print("\t");
		System.out.print("pickup");
		System.out.print("\t");
		System.out.print("delivery");
		System.out.print("\t");
		System.out.print("pickup lateness");
		System.out.print("\t");
		System.out.print("delivery lateness");
		System.out.print("\t");
		System.out.println("completion time");

		for (int pid : getCreationTimes().keySet()) {
			long creationTime = getCreationTimes().get(pid);
			long pickupTime = getPickupTimes().get(pid);
			long deliveryTime = getDeliveryTimes().get(pid);

			System.out.print(pid);
			System.out.print("\t");
			System.out.print(creationTime);
			System.out.print("\t");
			System.out.print(pickupTime);
			System.out.print("\t");
			System.out.print(deliveryTime);
			System.out.print("\t");
			System.out.print(pickupTime - creationTime);
			System.out.print("\t");
			System.out.print("TBD");
			System.out.print("\t");
			System.out.println(deliveryTime - pickupTime);
		}
	}
}
