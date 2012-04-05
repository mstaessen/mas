package project.classic.contractnet.messages;


import rinde.sim.core.graph.Point;
import rinde.sim.core.model.communication.CommunicationUser;
import rinde.sim.core.model.communication.Message;

public class ContractNetMessage extends Message {
	
	private ContractNetMessageType type;
	private double proposalValue;
	private Point resultPoint;
	
	
	public Point getResultPoint() {
		return resultPoint;
	}

	public void setResultPoint(Point resultPoint) {
		this.resultPoint = resultPoint;
	}

	public ContractNetMessage(CommunicationUser sender) {
		super(sender);
	}

	public ContractNetMessageType getType() {
		return type;
	}

	public void setType(ContractNetMessageType type) {
		this.type = type;
	}

	public void setProposalValue(double proposalValue) {
		this.proposalValue = proposalValue;
	}
	
	public double getProposalValue() {
		return proposalValue;
	}
}
