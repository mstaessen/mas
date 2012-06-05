package project.strategies.delegatemas.colony;


import project.common.packages.Package;
import rinde.sim.core.SimulatorAPI;
import rinde.sim.core.SimulatorUser;
import rinde.sim.core.TickListener;
import rinde.sim.core.graph.Point;
import rinde.sim.core.model.communication.CommunicationAPI;
import rinde.sim.core.model.communication.CommunicationUser;
import rinde.sim.core.model.communication.Message;

public class PackageAgent implements TickListener, SimulatorUser, CommunicationUser {

    private final int id;
    private Package myPackage;
    private PackageDestination destination;
    private PathTable pathTable;

    private SimulatorAPI simulatorAPI;
    private CommunicationAPI communicationAPI;
    private long lastFeasibilityCheck;
    
    private boolean packagePickedUp = false;
    
    public PackageAgent(int id, Package myPackage) {
	this.id = id;
	this.myPackage = myPackage;
	this.lastFeasibilityCheck = Integer.MAX_VALUE;
	this.pathTable = new PathTable();
	this.destination = new PackageDestination(this, myPackage.getDeliveryLocation());
    }

    @Override
    public void setSimulator(SimulatorAPI api) {
	this.simulatorAPI = api;
    }

    @Override
    public void tick(long currentTime, long timeStep) {

	// Evoperate pheromones.
	pathTable.evaporate();

	// Check for feasibibility
	if (lastFeasibilityCheck > Settings.TIMESTEPS_WAIT_BEFORE_SENDING_FEASIBILITY_ANTS) {
	    sendFeasibilityAnts();
	    lastFeasibilityCheck = 0;
	} else {
	    lastFeasibilityCheck++;
	}
    }

    @Override
    public void afterTick(long currentTime, long timeStep) {
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
	return this.id;
    }

    @Override
    public void receive(Message message) {
	
	if (!packagePickedUp) {    
	    if (message instanceof FeasibilityAnt) {
		    FeasibilityAnt fAnt = (FeasibilityAnt) message;
		    receiveFeasibilityAnt(fAnt);
		} else if (message instanceof ForwardExplorationAnt) {
		    ForwardExplorationAnt eAnt = (ForwardExplorationAnt) message;
		    receiveForwardExplorationAnt(eAnt);
		} else if (message instanceof BackwardExplorationAnt) {
		    BackwardExplorationAnt bAnt = (BackwardExplorationAnt) message;
		    receiveBackwardEplorationAnt(bAnt);
		}
	}
    }

    private void receiveBackwardEplorationAnt(BackwardExplorationAnt bAnt) {
	System.out.println(getId() + ": Received backward Exploration Ant");

	if (!bAnt.getPathToDo().contains(this)) {
	    // if we are not in the path of the ant, do nothing
	    return;
	}

	// Evaluate pheromones
	double pheromone = bAnt.getPathToEval().getPheromoneBonusForPath(
		myPackage.getDeliveryLocation(),myPackage.getRoadModel());
//	pathTable.addPheromone(bAnt.getPathToEval(), pheromone);

	CommunicationUser receiver;
	if (bAnt.getPathToDo().length() == 0) {
	    receiver = bAnt.getSender();
	} else {
	    receiver = bAnt.getPathToDo().getLast();
	}
	BackwardExplorationAnt newAnt = new BackwardExplorationAnt(bAnt.getSender(), bAnt.getPathToDo().removeLast(),
		new Path(bAnt.getPathToEval(), this));
	communicationAPI.send(receiver, newAnt);

    }

    private void receiveForwardExplorationAnt(ForwardExplorationAnt eAnt) {

	System.out.println(getId() + ": Received forward Exploration Ant");

	if (eAnt.getHopsLeft() - 1 > 0) {

	    // Forward the ant.
	    Path pathToGo = pathTable.chosePath();
	    if (pathToGo == null) {
		// No paths available yet.
		System.out.println(getId() + ": No paths available yet");
		return;
	    }
	    PackageAgent agent = pathToGo.getListPackageAgents().get(0);
	    communicationAPI.send(agent, new ForwardExplorationAnt(eAnt.getSender(), new Path(eAnt.getPath(), this),
		    eAnt.getHopsLeft() - 1));

	    System.out.println(getId() + ": sent to " + agent.getId());

	} else {
	    // transform to backward exploration ant.
	    BackwardExplorationAnt bAnt = new BackwardExplorationAnt(eAnt.getSender(), new Path(eAnt.getPath()),
		    new Path(this));

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

    }

    public void receiveFeasibilityAnt(FeasibilityAnt fAnt) {

	// if we are not already in the path (loop!) ...
	if (!fAnt.getPath().contains(this)) {

	    // add to table
	    pathTable.addPath(fAnt.getPath());

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
    
    
    public void setPackagePickedUp() {
	destination.setPackagePickedUp();
    }
}
