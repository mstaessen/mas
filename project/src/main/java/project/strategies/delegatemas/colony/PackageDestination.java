package project.strategies.delegatemas.colony;

import rinde.sim.core.SimulatorAPI;
import rinde.sim.core.SimulatorUser;
import rinde.sim.core.graph.Point;
import rinde.sim.core.model.communication.CommunicationAPI;
import rinde.sim.core.model.communication.CommunicationUser;
import rinde.sim.core.model.communication.Message;

public class PackageDestination implements CommunicationUser, SimulatorUser {

	private Point destination;
	private CommunicationAPI api;
	private PackageAgent agent;
	private SimulatorAPI simulatorAPI;
	
	private boolean packagePickedUp = false;
	
	public PackageDestination(PackageAgent agent, Point destination) {
		this.destination = destination;
		this.agent = agent;
	}
	
	@Override
	public void setCommunicationAPI(CommunicationAPI api) {
		this.api = api;
	}

	@Override
	public Point getPosition() {
		return destination;
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
	    if (!packagePickedUp) {
		if (message instanceof FeasibilityAnt) {
			FeasibilityAnt fAnt = (FeasibilityAnt) message;
			if(!fAnt.getSender().equals(agent)) {
			    api.send(agent, fAnt);
//			    agent.receiveFeasibilityAnt(fAnt);
			}
		}
	    }
	}

	@Override
	public void setSimulator(SimulatorAPI api) {
	    this.simulatorAPI = api;
	}
	
	
	public void setPackagePickedUp() {
	    packagePickedUp = true;
	}

}
