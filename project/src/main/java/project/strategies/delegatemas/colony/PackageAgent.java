package project.strategies.delegatemas.colony;

import java.util.ArrayList;
import java.util.List;

import project.common.packages.Package;
import rinde.sim.core.SimulatorAPI;
import rinde.sim.core.SimulatorUser;
import rinde.sim.core.TickListener;
import rinde.sim.core.graph.Point;
import rinde.sim.core.model.communication.CommunicationAPI;
import rinde.sim.core.model.communication.CommunicationUser;
import rinde.sim.core.model.communication.Message;

public class PackageAgent implements TickListener, SimulatorUser, CommunicationUser, Comparable<PackageAgent> {

    private Package myPackage;
    private PackageDestination destination;
    private PathTable pathTable;

    private SimulatorAPI simulatorAPI;
    private CommunicationAPI communicationAPI;

    private static final double PHEROMONE_BONUS_FEASIBILITY = Settings.START_PACKAGE_PHEROMONE_PATH
	    * Settings.EVAPORATION_RATE;

    public PackageAgent(Package myPackage) {
	this.myPackage = myPackage;
	this.pathTable = new PathTable(Settings.MAX_PACKAGE_PHEROMONE_PATH, Settings.MIN_PACKAGE_PHEROMONE_PATH,
		Settings.START_PACKAGE_PHEROMONE_PATH);
	this.destination = new PackageDestination(this, myPackage.getDeliveryLocation());
    }

    @Override
    public void setSimulator(SimulatorAPI api) {
	this.simulatorAPI = api;
    }

    @Override
    public void tick(long currentTime, long timeStep) {

	// Send feasibility Ants
	if (intentionValue > Math.random()) {
	    sendFeasibilityAnts();
	} else {
	    tSinceClaim++;
	    if (tSinceClaim > Settings.DURATION_CLAIM) {
		intentionValue = 1;
		claimer = null;
	    }
	}

	// Evoperate pheromones.
	pathTable.evaporate();

    }

    @Override
    public void afterTick(long currentTime, long timeStep) {
	if (!getPackage().isDelivered()) {
	    getPackage().decreaseDeadline(timeStep);
	}
    }

    @Override
    public void setCommunicationAPI(CommunicationAPI api) {
	this.communicationAPI = api;
	this.destination.setCommunicationAPI(api);
    }

    @Override
    public Point getPosition() {
	return myPackage.getPickupLocation();
    }

    @Override
    public double getRadius() {
	return Settings.BROADCAST_RANGE;
    }

    @Override
    public double getReliability() {
	return 1;
    }

    public PackageDestination getDestination() {
	return destination;
    }

    public Package getPackage() {
	return myPackage;
    }

    public int getId() {
	return this.getPackage().getId();
    }

    @Override
    public void receive(Message message) {

	if (myPackage.isDelivered()) {
	    return;
	}

	if (message instanceof FeasibilityAnt) {

	    FeasibilityAnt fAnt = (FeasibilityAnt) message;
	    receiveFeasibilityAnt(fAnt);

	} else if (message instanceof ForwardExplorationAnt) {

	    ForwardExplorationAnt eAnt = (ForwardExplorationAnt) message;
	    receiveForwardExplorationAnt(eAnt);

	} else if (message instanceof BackwardExplorationAnt) {

	    BackwardExplorationAnt bAnt = (BackwardExplorationAnt) message;
	    receiveBackwardEplorationAnt(bAnt);

	} else if (message instanceof IntentionAnt) {

	    IntentionAnt iAnt = (IntentionAnt) message;
	    receiveIntentionAnt(iAnt);

	}
    }

    private void receiveIntentionAnt(IntentionAnt iAnt) {
	if (iAnt.getPathAhead().length() == 0) {
	    throw new IllegalArgumentException("Should not be receiving this ant");
	} else {

	    if (!iAnt.getPathAhead().getFirst().equals(this)) {
		throw new IllegalArgumentException("Should not be receiving this ant " + getId() + "  "
			+ iAnt.getPathAhead());
	    }

	    if (iAnt.getPathAhead().length() > 0) {

		double iValue = ((double) iAnt.getPathDone().length())
			/ ((double) (iAnt.getPathAhead().length() + iAnt.getPathDone().length()))-Settings.INTENTION_PENALTY;

		if (claimer != null) {
		    if (iAnt.getSender().equals(claimer)) {
			tSinceClaim = 0;
			intentionValue = iValue;
		    } else {
			if (iValue < intentionValue) {
			    claimer = (TruckAgent) iAnt.getSender();
			    tSinceClaim = 0;
			    intentionValue = iValue;
			}
		    }
		} else {
		    claimer = (TruckAgent) iAnt.getSender();
		    tSinceClaim = 0;
		    intentionValue = iValue;
		}

		if (iAnt.getPathAhead().length() > 1) {
		    // Forward it to next agent.
		    Path newPathDone = new Path(iAnt.getPathDone(), this);
		    Path newPathAhead = iAnt.getPathAhead().getPathWithoutFirst();
		    PackageAgent receiver = newPathAhead.getFirst();
		    communicationAPI.send(receiver, new IntentionAnt(iAnt.getSender(), newPathDone, newPathAhead));
		}

	    }
	}

    }

    private void receiveBackwardEplorationAnt(BackwardExplorationAnt bAnt) {

	if (myPackage.isPickedUp() && claimer != null && !bAnt.getSender().equals(claimer)) {
	    return;
	}

	if (!bAnt.getPathToDo().contains(this)) {
	    // if we are not in the path of the ant, do nothing
	    return;
	}

	pathTable.addPheromoneBonus(bAnt.getPathToEval(), myPackage.getDeliveryLocation(), null,
		myPackage.getRoadModel());

	CommunicationUser receiver;
	Path newToDo = bAnt.getPathToDo().getPathWithoutLast();
	if (newToDo.length() == 0) {
	    receiver = bAnt.getSender();
	} else {
	    receiver = newToDo.getLast();
	}

	List<Double> intentionList = bAnt.getIntentionValues();
	if (claimer == null || !bAnt.getSender().equals(claimer))
	    intentionList.add(intentionValue);
	else {
	    intentionList.add(1d);
	}

	BackwardExplorationAnt newAnt = new BackwardExplorationAnt(bAnt.getSender(), newToDo, new Path(this,
		bAnt.getPathToEval()), intentionList);

	communicationAPI.send(receiver, newAnt);
    }

    private void receiveForwardExplorationAnt(ForwardExplorationAnt eAnt) {

	if (myPackage.isPickedUp() && claimer != null && !eAnt.getSender().equals(claimer)) {
	    return;
	}

	if (eAnt.getHopsLeft() - 1 > 0) {

	    // Forward the ant.
	    Path pathToGo = pathTable.chosePath();
	    if (pathToGo == null || pathToGo.length() == 0) {
		// No paths available yet.
		sendExplorationAntBack(eAnt);
		return;
	    }
	    PackageAgent agent = pathToGo.getListPackageAgents().get(0);


	    if (Math.random() < Settings.EARLY_RETURN_RATE_EXPLORATION_ANT) {
		sendExplorationAntBack(eAnt);
		return;
	    }
//	     forward the ant	    
	    if (!eAnt.getPath().contains(agent)) {
		communicationAPI.send(agent, new ForwardExplorationAnt(eAnt.getSender(),
			new Path(eAnt.getPath(), this), eAnt.getHopsLeft() - 1));
		return;
	    } else {
		sendExplorationAntBack(eAnt);
		return;
	    }
	} else {
	    sendExplorationAntBack(eAnt);
	}
    }

    private void sendExplorationAntBack(ForwardExplorationAnt eAnt) {

	// transform to backward exploration ant.
	List<Double> intentionList = new ArrayList<Double>();

	if (claimer == null || !eAnt.getSender().equals(claimer))
	    intentionList.add(intentionValue);
	else {
	    intentionList.add(1d);
	}

	intentionList.add(intentionValue);

	BackwardExplorationAnt bAnt = new BackwardExplorationAnt(eAnt.getSender(), new Path(eAnt.getPath()), new Path(
		this), intentionList);
	CommunicationUser receiver = null;
	if (eAnt.getPath().length() == 0) {
	    // return to sender
	    receiver = bAnt.getSender();
	} else {
	    // return to last packageAgent.
	    receiver = eAnt.getPath().getLast();
	}
	communicationAPI.send(receiver, bAnt);
    }

    public void receiveFeasibilityAnt(FeasibilityAnt fAnt) {

	// if we are not already in the path (loop!) ...
	if (!fAnt.getPath().contains(this)) {

	    // add to table
	    pathTable.addPath(fAnt.getPath());

	    // Increase the value ...
	    pathTable.increasePheromones(fAnt.getPath(), PHEROMONE_BONUS_FEASIBILITY);

	    // send to the other agents, if there are hops left.
	    if (fAnt.getHopsLeft() - 1 > 0) {
		communicationAPI.broadcast(new FeasibilityAnt(this, new Path(fAnt.getPath(), this),
			fAnt.getHopsLeft() - 1), PackageDestination.class);
	    }
	}

    }

    public void sendFeasibilityAnts() {
	communicationAPI.broadcast(new FeasibilityAnt(this, Settings.MAX_HOPS_FEASIBILITY_ANT),
		PackageDestination.class);
    }

    @Override
    public String toString() {
	String string = "ID: " + getId();
	string += pathTable.toString();

	return string;
    }

    public String toString(int maxEntries) {
	String string = "ID: " + getId();
	string += pathTable.toString(maxEntries);

	return string;
    }

    private double intentionValue = 1;
    private int tSinceClaim = 0;
    private TruckAgent claimer = null;

    public double getIntentionValue() {
	return intentionValue;
    }

    public TruckAgent getClaimer() {
	return claimer;
    }

    @Override
    public int compareTo(PackageAgent o) {
	if (o.getId() > getId()) {
	    return -1;
	} else if (o.getId() < getId()) {
	    return 1;
	} else {
	    return 0;
	}
    }
}
