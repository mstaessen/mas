package project.modern.delegatemas.colony;


import rinde.sim.core.graph.Point;
import rinde.sim.core.model.communication.CommunicationAPI;
import rinde.sim.core.model.communication.CommunicationUser;
import rinde.sim.core.model.communication.Message;

public class PackageDestination implements CommunicationUser {

	private Point destination;
	private CommunicationAPI api;
	private PackageAgent agent;
	
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
		return Settings.RADIUS_PACKAGE_DESTINATION;
	}

	@Override
	public double getReliability() {
		return Settings.RELIABILITY_PACKAGE_DESTINATION;
	}

	@Override
	public void receive(Message message) {
		System.out.println("receive something");
		if (message instanceof FeasibilityAnt) {
			FeasibilityAnt fAnt = (FeasibilityAnt) message;
			api.send(agent, fAnt);
			System.out.println("receiving");
		}
		
	}

}
