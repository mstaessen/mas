package project.strategies.delegatemas.colony;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.Queue;

import project.common.trucks.Truck;
import rinde.sim.core.SimulatorAPI;
import rinde.sim.core.SimulatorUser;
import rinde.sim.core.TickListener;
import rinde.sim.core.graph.Point;
import rinde.sim.core.model.communication.CommunicationAPI;
import rinde.sim.core.model.communication.CommunicationUser;
import rinde.sim.core.model.communication.Message;

public class TruckAgent implements TickListener, SimulatorUser, CommunicationUser, Comparable<TruckAgent> {

    private Truck truck;

    private SimulatorAPI simulatorAPI;
    private CommunicationAPI communicationAPI;
    private PathTable pathTable;

    private Path currentIntentions = null;
    private boolean driveRandom = true;

    private Queue<Point> directions;

    public TruckAgent(Truck truck) {
	this.truck = truck;
	this.pathTable = new PathTable(Settings.MAX_TRUCK_PHEROMONE_PATH, Settings.MIN_TRUCK_PHEROMONE_PATH,
		Settings.START_TRUCK_PHEROMONE_PATH);
	this.directions = new LinkedList<Point>();
    }

    public Truck getTruck() {
	return truck;
    }

    public int getId() {
	return truck.getId();
    }

    @Override
    public void setCommunicationAPI(CommunicationAPI api) {
	this.communicationAPI = api;
    }

    @Override
    public Point getPosition() {
	return truck.getPosition();
    }

    @Override
    public double getRadius() {
	return Settings.BROADCAST_RANGE;
    }

    @Override
    public double getReliability() {
	return 1;
    }

    @Override
    public void receive(Message message) {

	if (message instanceof BackwardExplorationAnt) {
	    BackwardExplorationAnt bAnt = (BackwardExplorationAnt) message;

	    if (currentIntentions != null) {
		if (bAnt.getPathToEval().getFirst().equals(currentIntentions.getFirst())) {
		    pathTable.addPath(bAnt.getPathToEval());
		    pathTable.addPheromoneBonus(bAnt.getPathToEval(), truck.getPosition(), 
			    bAnt.getIntentionValues(), truck.getRoadModel());
		}
	    } else {
		pathTable.addPath(bAnt.getPathToEval());
		pathTable.addPheromoneBonus(bAnt.getPathToEval(), truck.getPosition(), 
			bAnt.getIntentionValues(), truck.getRoadModel());
	    }

	}
    }

    @Override
    public void setSimulator(SimulatorAPI api) {
	this.simulatorAPI = api;
    }

    @Override
    public void tick(long currentTime, long timeStep) {

	checkForBestIntentions();
	
	pathTable.evaporate();

	sendExplorationAnts();

	if (driveRandom) {
	    /*
	     * Drive random
	     */
	    driveRandom(timeStep);
	} else {
	    /*
	     * Drive to packages
	     */
	    sendIntentionAnt();
	    driveToTargetedPackage(timeStep);
	}

    }

    @Override
    public void afterTick(long currentTime, long timeStep) {
    }

    private void checkForBestIntentions() {
	 
	Path bestPath = pathTable.getBestPath();
	
	if (currentIntentions == null && bestPath != null) {
	    currentIntentions = new Path(null,bestPath);
	    driveRandom = false;
	    startOnNewPackage();
	} else if (bestPath != null) {
	    currentIntentions = bestPath;
	}
    }

    private void driveRandom(long timeStep) {
	if (!directions.isEmpty()) {
	    truck.drive(directions, timeStep);
	} else {
	    Point destination = truck.getRoadModel().getGraph().getRandomNode(simulatorAPI.getRandomGenerator());
	    planDirections(destination);
	}

    }

    private void driveToTargetedPackage(long timeStep) {
	if (!directions.isEmpty()) {
	    truck.drive(directions, timeStep);
	} else {
	    if (truck.hasLoad()) {
		if (truck.tryDelivery()) {
		    // System.out.println("Truck " + getId() +
		    // " delivered package " + targetedPackage.getId());
		    simulatorAPI.unregister(currentIntentions.getFirst());
		    startOnNewPackage();
		} else {
		    planDirections(getTruck().getLoad().getDeliveryLocation());
		}
	    } else {
		    if (truck.tryPickup(currentIntentions.getFirst().getPackage())) {
			// System.out.println("Truck " + getId() +
			// " picked up package " + targetedPackage.getId());
			planDirections(getTruck().getLoad().getDeliveryLocation());
		    } else {
			startOnNewPackage();
		    }
	    }
	}

    }

    private void startOnNewPackage() {

	if (currentIntentions == null || currentIntentions.length() <= 1) {
	    driveRandom = true;
	    currentIntentions = null;
	    System.out.println("DRiving RANDOM AGAIN");
	} else {
	    PackageAgent oldA = currentIntentions.getFirst();
	    currentIntentions = currentIntentions.getPathWithoutFirst();
	    planDirections(currentIntentions.getFirst().getPackage().getPickupLocation());
	    pathTable.purgeAndCleanUpFor(oldA,currentIntentions.getFirst());
	}
    }

    public void planDirections(Point point) {
	directions = new LinkedList<Point>(truck.getRoadModel().getShortestPathTo(truck, point));
    }

    public void sendExplorationAnts() {

	ForwardExplorationAnt fAnt = new ForwardExplorationAnt(this, new Path(), Settings.MAX_HOPS_EXPLORATION_ANT);

	if (currentIntentions != null) {
	    communicationAPI.send(currentIntentions.getFirst(), fAnt);
	    communicationAPI.send(currentIntentions.getFirst(), fAnt);
	    communicationAPI.send(currentIntentions.getFirst(), fAnt);
	    communicationAPI.send(currentIntentions.getFirst(), fAnt);
	    communicationAPI.send(currentIntentions.getFirst(), fAnt);
	    communicationAPI.send(currentIntentions.getFirst(), fAnt);
	    communicationAPI.send(currentIntentions.getFirst(), fAnt);
	    communicationAPI.send(currentIntentions.getFirst(), fAnt);
	    communicationAPI.send(currentIntentions.getFirst(), fAnt);
	    communicationAPI.send(currentIntentions.getFirst(), fAnt);
	} else {
	    communicationAPI.broadcast(fAnt, PackageAgent.class);
	}

    }

    private void sendIntentionAnt() {
	if (currentIntentions != null) {
	    communicationAPI.send(currentIntentions.getFirst(), new IntentionAnt(this, new Path(), new Path(currentIntentions)));
	}
    }

    @Override
    public String toString() {
	String str = "Truck" + getId();
	DecimalFormat df = new DecimalFormat("#.##");
	if (currentIntentions != null) {
	    Double pheromone = pathTable.getPheromone(currentIntentions);
	    if (pheromone == null)
		pheromone = new Double(0);
	    str += "\n\n " + currentIntentions.getFirst().getId() + " -> " + currentIntentions + "::" + df.format(pheromone) + "\n";
	} else {
	    str += "\n\n null -> " + currentIntentions + "\n";
	}
	str += pathTable.toString();
	return str;
    }
    
    
    public String toString(int maxEntries) {
	String str = "Truck" + getId();
	DecimalFormat df = new DecimalFormat("#.##");
	if (currentIntentions != null) {
	    Double pheromone = pathTable.getPheromone(currentIntentions);
	    if (pheromone == null)
		pheromone = new Double(0);
	    str += "\n\n " + currentIntentions.getFirst().getId() + " -> " + currentIntentions + "::" + df.format(pheromone) + "\n";
	} else {
	    str += "\n\n null -> " + currentIntentions + "\n";
	}
	str += pathTable.toString(maxEntries);
	return str;
    }

    @Override
    public int compareTo(TruckAgent o) {
	if (o.getId() > getId()) {
	    return -1;
	} else if (o.getId() < getId()) {
	    return 1;
	} else {
	    return 0;
	}
    }

}
