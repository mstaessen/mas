package project.strategies.delegatemas.colony;

import rinde.sim.core.model.communication.CommunicationUser;
import rinde.sim.core.model.communication.Message;

public class FeasibilityAnt extends Message {

	private Path path;
	private int hopsLeft;
	
	public FeasibilityAnt(CommunicationUser sender, Path path, int hopsLeft) {
		super(sender);
		if (!(sender instanceof PackageAgent)) {
			throw new IllegalArgumentException("Only PackageAgents can send A FeasibiltyAnt");
		} else {
			this.path = path;
		}
		this.hopsLeft = hopsLeft;
	}
	
	public FeasibilityAnt(CommunicationUser sender, int hopsLeft) {
		super(sender);
		if (!(sender instanceof PackageAgent)) {
			throw new IllegalArgumentException("Only PackageAgents can send A FeasibiltyAnt");
		} else {
			this.path = new Path((PackageAgent) sender);
		}
		this.hopsLeft = hopsLeft;
	}

	public Path getPath() {
		return path;
	}
	
	public int getHopsLeft() {
		return hopsLeft;
	}
	
}
