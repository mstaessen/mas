package project.strategies.delegatemas.colony;

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
	this.pathTable = new PathTable();
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
	    pathTable.addPath(bAnt.getPathToEval());
	    pathTable.updatePheromones(bAnt.getPathToEval(), truck.getPosition(), truck.getRoadModel());
	}

    }

    @Override
    public void setSimulator(SimulatorAPI api) {
	this.simulatorAPI = api;
    }

    @Override
    public void tick(long currentTime, long timeStep) {

	pathTable.evaporate();
	
	sendExplorationAnts();

	checkForBestIntentions();

	if (driveRandom) {
	    /*
	     * Drive random
	     */
	    driveRandom(timeStep);
	} else {
	    /*
	     * Drive to packages
	     */
	    sendIntentionAnts();
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
		    System.out.println("Truck " + getId() + " delivered package " + targetedPackage.getId());
		    simulatorAPI.unregister(targetedPackage);
		    startOnNewPackage();
		} else {
		    planDirections(targetedPackage.getPackage().getDeliveryLocation());
		}
	    } else {
		if (targetedPackage == null) {
		    startOnNewPackage();
		} else {
		    if (truck.tryPickup()) {
			    System.out.println("Truck " + getId() + " picked up package " + targetedPackage.getId());
			    planDirections(targetedPackage.getPackage().getDeliveryLocation());
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
	    currentIntentions = currentIntentions.removeFirst();
	    System.out.println("targetting package " + targetedPackage.getId() + " intentions " + currentIntentions);
	}
    }

    public void planDirections(Point point) {
	directions = new LinkedList<Point>(truck.getRoadModel().getShortestPathTo(truck, point));
    }

    public void sendExplorationAnts() {
	ForwardExplorationAnt eAnt = new ForwardExplorationAnt(this, Settings.MAX_HOPS_EXPLORATION_ANT);
	communicationAPI.broadcast(eAnt, PackageAgent.class);
    }

    private void sendIntentionAnts() {

    }
    
    @Override
    public String toString() {
	String str = "Truck"+getId();
	if (targetedPackage != null) {
	    str += "\n\n "+targetedPackage.getId()+" -> "+currentIntentions+"\n";
	} else {
	    str += "\n\n null -> "+currentIntentions+"\n";
	}
	str += pathTable.toString();
	return str;
    }

}
