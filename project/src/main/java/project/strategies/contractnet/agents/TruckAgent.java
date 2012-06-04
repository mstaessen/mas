package project.strategies.contractnet.agents;

import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import project.common.packages.Package;
import project.common.trucks.AbstractTruckAgent;
import project.common.trucks.Truck;
import project.strategies.contractnet.messages.ContractNetMessage;
import project.strategies.contractnet.messages.ContractNetMessageType;
import rinde.sim.core.graph.Graphs;
import rinde.sim.core.graph.Point;
import rinde.sim.core.model.communication.CommunicationAPI;
import rinde.sim.core.model.communication.CommunicationUser;
import rinde.sim.core.model.communication.Mailbox;
import rinde.sim.core.model.communication.Message;

public class TruckAgent extends AbstractTruckAgent implements CommunicationUser {

    private double reliability;
    private double radius;
    private Mailbox mailbox = new Mailbox();
    private CommunicationAPI communicationAPI;

    private ContractNetMessage assignedProposal = null;

    private static final Logger LOGGER = LoggerFactory.getLogger("CONTRACTNET");

    public TruckAgent(Truck truck, double radius, double reliability) {
	super(truck);

	this.radius = radius;
	this.reliability = reliability;
    }

    @Override
    public void tick(long currentTime, long timeStep) {
	handleIncomingMessages();

	// Drive when possible
	if (!getPath().isEmpty()) {
	    getTruck().drive(getPath(), timeStep);
	} else {
	    if (getTruck().hasLoad()) {
		tryDelivery();
	    } else {
		if (assignedProposal != null) {
		    tryPickup();
		}
	    }
	}
    }

    private void handleIncomingMessages() {
	for (Message m : mailbox.getMessages()) {
	    ContractNetMessage message = (ContractNetMessage) m;
	    switch (message.getType()) {
		case CALL_FOR_PROPOSAL:
		    handleCallForProposal(message);
		    break;
		case ACCEPT_PROPOSAL:
		    handleAcceptedProposal(message);
		    break;
		case REJECT_PROPOSAL:
		    handleRejectedProposal(message);
		    break;
		default:
		    break;
	    }
	}
    }

    private void handleRejectedProposal(ContractNetMessage message) {
	// do nothing
    }

    private void handleCallForProposal(ContractNetMessage callForProposal) {
	// if (getTruck().hasLoad()) {
	// sendRefusal(callForProposal);
	// } else {
	sendProposal(callForProposal);
	// }
    }

    // private void sendRefusal(ContractNetMessage callForProposal) {
    // ContractNetMessage refusal = new ContractNetMessage(this,
    // ContractNetMessageType.REFUSAL);
    // communicationAPI.send(callForProposal.getSender(), refusal);
    // LOGGER.info(callForProposal.getSender() + " <- REFUSAL <- " + this);
    // }

    private void sendProposal(ContractNetMessage callForProposal) {
	ContractNetMessage proposal = new ContractNetMessage(this, ContractNetMessageType.PROPOSAL, callForProposal);
	double distance = Graphs.pathLength(getTruck().getRoadModel().getShortestPathTo(getTruck(),
		callForProposal.getSender().getPosition()));
	proposal.setDistance(distance);
	communicationAPI.send(callForProposal.getSender(), proposal);
	LOGGER.info(callForProposal.getSender() + " <- PROPOSAL <- " + this);
    }

    private void handleAcceptedProposal(ContractNetMessage acceptedProposal) {
	if (!getTruck().hasLoad()) {
	    if (assignedProposal == null) {
		assignedProposal = acceptedProposal;
	    } else {
		// update distance before reconsidering
		acceptedProposal.setDistance(getDistanceTo(acceptedProposal.getPosition()));
		if (acceptedProposal.getProposalValue() > assignedProposal.getProposalValue()) {
		    sendFailure(assignedProposal);
		    assignedProposal = acceptedProposal;
		} else {
		    sendFailure(acceptedProposal);
		}
	    }

	    setPath(new LinkedList<Point>(getTruck().getRoadModel().getShortestPathTo(getTruck(),
		    assignedProposal.getPosition())));
	    LOGGER.info(this + " : starting path to " + assignedProposal.getSender());
	} else {
	    sendFailure(acceptedProposal);
	}
    }

    private void sendFailure(ContractNetMessage acceptedProposal) {
	ContractNetMessage failure = new ContractNetMessage(this, ContractNetMessageType.FAILURE);
	communicationAPI.send(acceptedProposal.getSender(), failure);
	LOGGER.info(acceptedProposal.getSender() + " <- FAILURE <- " + this);
    }

    private void tryPickup() {
	if (getTruck().tryPickup()) {
	    LOGGER.info(this + " picked up " + getTruck().getLoad());
	    setPath(new LinkedList<Point>(getTruck().getRoadModel().getShortestPathTo(getTruck(),
		    getTruck().getLoad().getDeliveryLocation())));
	}
    }

    private void tryDelivery() {
	Package pkg = getTruck().getLoad();
	if (getTruck().tryDelivery()) {
	    assignedProposal = null;
	    LOGGER.info(this + " delivered " + pkg);
	}
    }

    private double getDistanceTo(Point position) {
	return Graphs.pathLength(getTruck().getRoadModel().getShortestPathTo(getTruck(), position));
    }

    @Override
    public void setCommunicationAPI(CommunicationAPI model) {
	this.communicationAPI = model;
    }

    @Override
    public Point getPosition() {
	return getTruck().getPosition();
    }

    @Override
    public double getRadius() {
	return this.radius;
    }

    @Override
    public double getReliability() {
	return this.reliability;
    }

    @Override
    public void receive(Message message) {
	mailbox.receive(message);
    }

    @Override
    public String toString() {
	return "TruckAgent-" + getTruck().getId();
    }
}