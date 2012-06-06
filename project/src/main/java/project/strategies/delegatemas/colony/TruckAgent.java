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

public class TruckAgent implements TickListener, SimulatorUser, CommunicationUser {

    private Truck truck;

    private SimulatorAPI simulatorAPI;
    private CommunicationAPI communicationAPI;
    private PathTable pathTable;

    private Path currentIntentions = null;
    private PackageAgent targetedPackage = null;
    private boolean driveRandom = true;

    private Queue<Point> directions;

    public TruckAgent(Truck truck) {
	this.truck = truck;
	this.pathTable = new PathTable(Settings.MAX_TRUCK_PHEROMONE_PATH);
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
	    
	    if (bAnt.getPathToEval().length() != 0) {
		
		Path newPath = bAnt.getPathToEval();
		if (targetedPackage != null && newPath.getFirst().equals(targetedPackage)) {
		    newPath = newPath.getPathWithoutFirst();
		}
		if (targetedPackage != null && newPath.contains(targetedPackage)) {
		    // Not adding
		} else {
		    pathTable.addPath(newPath);
		    pathTable.updatePheromones(newPath, truck.getPosition(), truck.getRoadModel()); 
		}
		
		
		
	    }
	    
	    
	} else if (message instanceof IntentionAnt) {
	    IntentionAnt iAnt = (IntentionAnt) message;
	    pathTable.updatePheromones(iAnt.getPathDone(), truck.getPosition(), truck.getRoadModel());
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
	currentIntentions = pathTable.getBestPath();
		
	if (targetedPackage == null && currentIntentions != null) {
	    driveRandom = false;
	    startOnNewPackage();
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
		    simulatorAPI.unregister(targetedPackage);
		    startOnNewPackage();
		} else {
		    planDirections(getTruck().getLoad().getDeliveryLocation());
		}
	    } else {
		if (targetedPackage == null) {
		    startOnNewPackage();
		} else {
		    if (truck.tryPickup(targetedPackage.getPackage())) {
			// System.out.println("Truck " + getId() +
			// " picked up package " + targetedPackage.getId());
			planDirections(getTruck().getLoad().getDeliveryLocation());
			targetedPackage.isPickedUpBy(this);
		    } else {
			startOnNewPackage();
		    }

		}
	    }
	}

    }

    private void startOnNewPackage() {

	if (currentIntentions == null || currentIntentions.length() == 0) {
	    driveRandom = true;
	    currentIntentions = null;
	    targetedPackage = null;
	     System.out.println("DRiving RANDOM AGAIN");
	} else {
	    targetedPackage = currentIntentions.getFirst();
	    planDirections(targetedPackage.getPackage().getPickupLocation());
	    currentIntentions = currentIntentions.getPathWithoutFirst();
	    if (currentIntentions.length() == 0)
		currentIntentions = null;
	    pathTable.purgeFromTable(targetedPackage);
	    // System.out.println("targetting package " +
	    // targetedPackage.getId() + " intentions " + currentIntentions);
	}
    }

    public void planDirections(Point point) {
	directions = new LinkedList<Point>(truck.getRoadModel().getShortestPathTo(truck, point));
    }

    public void sendExplorationAnts() {
	
	ForwardExplorationAnt eAnt;
	
	if (targetedPackage != null) {
	    eAnt = new ForwardExplorationAnt(this, Settings.MAX_HOPS_EXPLORATION_ANT+1);
	    communicationAPI.send(targetedPackage, eAnt);
	} else {
	    eAnt = new ForwardExplorationAnt(this, Settings.MAX_HOPS_EXPLORATION_ANT);
	    communicationAPI.broadcast(eAnt, PackageAgent.class);
	}
	
    }

    private void sendIntentionAnt() {
	if (currentIntentions != null && currentIntentions.length() != 0) {
	    IntentionAnt iAnt = new IntentionAnt(this, new Path(), new Path(currentIntentions));
	    communicationAPI.send(currentIntentions.getFirst(), iAnt);
	}
    }

    @Override
    public String toString() {
	String str = "Truck" + getId();
	DecimalFormat df = new DecimalFormat("#.##");
	if (targetedPackage != null) {
	    Double pheromone = pathTable.getPheromone(currentIntentions);
	    if (pheromone == null)
		pheromone = new Double(0);
	    str += "\n\n " + targetedPackage.getId() + " -> " + currentIntentions + "::"+df.format(pheromone)+"\n";
	} else {
	    str += "\n\n null -> " + currentIntentions + "\n";
	}
	str += pathTable.toString();
	return str;
    }

}