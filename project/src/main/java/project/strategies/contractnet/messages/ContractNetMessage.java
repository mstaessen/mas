package project.strategies.contractnet.messages;

import project.common.packages.Priority;
import rinde.sim.core.graph.Point;
import rinde.sim.core.model.communication.CommunicationUser;
import rinde.sim.core.model.communication.Message;

public class ContractNetMessage extends Message {

    private ContractNetMessageType type;
    private Point position;
    private double distance;
    private double priority;

    public ContractNetMessage(CommunicationUser sender) {
	super(sender);
    }

    public ContractNetMessage(CommunicationUser sender, ContractNetMessageType type) {
	this(sender);
	setType(type);
    }

    public ContractNetMessage(CommunicationUser sender, ContractNetMessageType type, ContractNetMessage original) {
	this(sender, type);

	this.position = original.position;
	this.distance = original.distance;
	this.priority = original.priority;
    }

    public Point getPosition() {
	return position;
    }

    public void setPosition(Point position) {
	this.position = position;
    }

    public ContractNetMessageType getType() {
	return type;
    }

    public void setType(ContractNetMessageType type) {
	this.type = type;
    }

    public void setDistance(double distance) {
	this.distance = distance;
    }

    public double getProposalValue() {
	return priority / distance;
    }

    public void setPriority(double priority) {
	this.priority = priority;
    }

    public void setPriority(Priority priority) {
	setPriority(priority.getValue());
    }
}