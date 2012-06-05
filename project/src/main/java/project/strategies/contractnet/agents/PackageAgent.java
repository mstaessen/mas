package project.strategies.contractnet.agents;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import project.common.packages.AbstractPackageAgent;
import project.common.packages.Package;
import project.strategies.contractnet.messages.ContractNetMessage;
import project.strategies.contractnet.messages.ContractNetMessageType;
import rinde.sim.core.graph.Point;
import rinde.sim.core.model.communication.CommunicationAPI;
import rinde.sim.core.model.communication.CommunicationUser;
import rinde.sim.core.model.communication.Mailbox;
import rinde.sim.core.model.communication.Message;

public class PackageAgent extends AbstractPackageAgent implements CommunicationUser {

    private double radius;
    private double reliability;
    private Mailbox mailbox = new Mailbox();
    private CommunicationAPI communicationAPI;

    private boolean assigned = false;
    private boolean voting = false;
    private boolean response = false;

    private ContractNetMessage bestProposal;
    private Set<ContractNetMessage> lostProposals = new HashSet<ContractNetMessage>();

    private static final Logger LOGGER = LoggerFactory.getLogger("ContractNet");

    public PackageAgent(Package pkg, double radius, double reliability) {
	super(pkg);

	this.radius = radius;
	this.reliability = reliability;
	this.mailbox = new Mailbox();
    }

    @Override
    public void tick(long currentTime, long timeStep) {
	if (!getPackage().isPickedUp()) {
	    handleIncomingMessages();

	    if (!assigned) {
		if (voting) {
		    stopVoting();
		} else {
		    startVoting();
		}
	    }
	}
    }

    private void startVoting() {
	voting = true;
	broadcastCallForProposal();
    }

    private void handleIncomingMessages() {
	resetProposals();
	for (Message m : mailbox.getMessages()) {
	    ContractNetMessage message = (ContractNetMessage) m;
	    switch (message.getType()) {
		case PROPOSAL:
		    handleProposal(message);
		    break;
		case REFUSAL:
		    handleRefusal(message);
		    break;
		case FAILURE:
		    handleFailure(message);
		    break;
		case INFORM_DONE:
		    handleInformDone(message);
		    break;
		default:
		    break;
	    }
	}
    }

    private void stopVoting() {
	if (response) {
	    if (bestProposal != null) {
		// Send an accept message to the winner
		acceptProposal(bestProposal);
		assigned = true;
	    }

	    // Send a reject message to all the losers
	    for (ContractNetMessage proposal : lostProposals) {
		rejectProposal(proposal);
	    }
	    voting = false;
	    response = false;
	}
    }

    private void resetProposals() {
	bestProposal = null;
	lostProposals.clear();
    }

    private void handleInformDone(ContractNetMessage message) {
	// Do nothing, package is delivered. Redundant message.
    }

    private void handleRefusal(ContractNetMessage message) {
	response = true;
    }

    private void handleFailure(ContractNetMessage message) {
	assigned = false;
    }

    private void broadcastCallForProposal() {
	ContractNetMessage broadcastMessage = new ContractNetMessage(this, ContractNetMessageType.CALL_FOR_PROPOSAL);
	broadcastMessage.setPosition(getPosition());
	broadcastMessage.setPriority(getPackage().getPriority());
	communicationAPI.broadcast(broadcastMessage, TruckAgent.class);
	// LOGGER.info("{} -> CALL_FOR_PROPOSAL", this);
    }

    private void handleProposal(ContractNetMessage proposal) {
	response = true;
	if (assigned) {
	    lostProposals.add(proposal);
	} else {
	    if (bestProposal == null) {
		bestProposal = proposal;
	    } else if (bestProposal.getProposalValue() < proposal.getProposalValue()) {
		lostProposals.add(bestProposal);
		bestProposal = proposal;
	    } else {
		lostProposals.add(proposal);
	    }
	}
    }

    private void acceptProposal(ContractNetMessage proposal) {
	ContractNetMessage acceptProposal = new ContractNetMessage(this, ContractNetMessageType.ACCEPT_PROPOSAL,
		proposal);
	// update priority
	acceptProposal.setPriority(getPackage().getPriority());
	communicationAPI.send(proposal.getSender(), acceptProposal);
	// LOGGER.info("{} -> ACCEPT_PROPOSAL -> {}", this,
	// proposal.getSender());
    }

    private void rejectProposal(ContractNetMessage proposal) {
	ContractNetMessage rejectProposal = new ContractNetMessage(this, ContractNetMessageType.REJECT_PROPOSAL);
	communicationAPI.send(proposal.getSender(), rejectProposal);
	// LOGGER.info("{} -> REJECT_PROPOSAL -> {}", this,
	// proposal.getSender());
    }

    @Override
    public void setCommunicationAPI(CommunicationAPI api) {
	this.communicationAPI = api;
    }

    @Override
    public Point getPosition() {
	return getPackage().getPickupLocation();
    }

    @Override
    public double getRadius() {
	return radius;
    }

    @Override
    public double getReliability() {
	return reliability;
    }

    @Override
    public void receive(Message message) {
	this.mailbox.receive(message);
    }

    @Override
    public String toString() {
	return "PackageAgent-" + getPackage().getId();
    }
}
