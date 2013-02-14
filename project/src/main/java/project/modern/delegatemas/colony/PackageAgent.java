package project.modern.delegatemas.colony;

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
	public void afterTick(long currentTime, long timeStep) {}

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
		return -1;
	}

	@Override
	public double getReliability() {
		return 1;
	}

	public PackageDestination getDestination() {
		return destination;
	}
	
	public int getId() {
		return this.id;
	}
	
	@Override
	public void receive(Message message) {
		if (message instanceof FeasibilityAnt ) {
			
			FeasibilityAnt fAnt = (FeasibilityAnt) message;

			// if we are not already in the path (loop!) ...
			if (!fAnt.getPath().contains(this)) {

				// add to table
				pathTable.addPath(fAnt.getPath());

				// send to the other agents, if there are hops left.

				if (fAnt.getHopsLeft() > 0) {
					communicationAPI.broadcast(new FeasibilityAnt(this, new Path(fAnt.getPath(), this), 
							fAnt.getHopsLeft() - 1), PackageDestination.class);
					
					System.out.println("forwarding");
				}
			}
		} else if (message instanceof ForwardExplorationAnt) {
			
		}
	}

	private void sendFeasibilityAnts() {
		
		// TODO waarom werkt dit niet?
		
		communicationAPI
				.broadcast(new FeasibilityAnt(this, Settings.MAX_HOPS_FEASIBILITY_ANT));
	}

}
