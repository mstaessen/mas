package project.strategies.delegatemas.colony;

import rinde.sim.core.model.communication.CommunicationUser;
import rinde.sim.core.model.communication.Message;

public class ForwardExplorationAnt extends Message {

	public Path path;
	public int hopsLeft;
	
	public ForwardExplorationAnt(CommunicationUser sender, Path path, int hopsLeft) {
		super(sender);
		if (!(sender instanceof TruckAgent)) {
			throw new IllegalArgumentException("Only PackageAgents can send A FeasibiltyAnt");
		} else {
			this.path = path;
			this.hopsLeft = hopsLeft;
		}
	}
	
	
	public ForwardExplorationAnt(CommunicationUser sender, int hopsLeft) {
		super(sender);
		if (!(sender instanceof PackageAgent)) {
			throw new IllegalArgumentException("Only PackageAgents can send A FeasibiltyAnt");
		} else {
			this.path = new Path();
			this.hopsLeft = hopsLeft;
		}
		
	}
	
	public Path getPath() {
		return path;
	}
	
	public int getHopsLeft() {
		return hopsLeft;
	}

}
