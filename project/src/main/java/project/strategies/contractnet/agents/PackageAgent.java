package project.strategies.contractnet.agents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.LoggerFactory;

import project.common.exceptions.AlreadyPickedUpException;
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

	private TruckAgent targetedTruck;
	private ContractNetMessage bestProposal;
	private List<CommunicationUser> lostProposals = new ArrayList<CommunicationUser>();

	public PackageAgent(Package pkg, double radius, double reliability) {
		super(pkg);

		this.radius = radius;
		this.reliability = reliability;
		this.mailbox = new Mailbox();
	}

	@Override
	public void tick(long currentTime, long timeStep) {
		if (!getPackage().isDelivered()) {
			handleIncomingMessages(mailbox.getMessages());
			if (targetedTruck == null) {
				broadcastCallForProposal();
			}
		}
	}

	@SuppressWarnings("incomplete-switch")
	private void handleIncomingMessages(Collection<Message> messages) {
		bestProposal = null;
		lostProposals.clear();

		for (Message m : messages) {
			ContractNetMessage message = (ContractNetMessage) m;
			switch (message.getType()) {
			case PROPOSE:
				proposed(message);
				break;
			case REFUSE:
				// Do nothing;
				break;
			case FAILURE:
				failed();
				break;
			case INFORM_DONE:
				// Do nothing (TruckAgent does all the actions);
				break;
			}
		}

		if (bestProposal != null)
			sendAcceptProposal(bestProposal, lostProposals);

	}

	private void broadcastCallForProposal() {

		ContractNetMessage broadcastMessage = new ContractNetMessage(this);
		broadcastMessage.setType(ContractNetMessageType.CALL_FOR_PROPOSAL);
		communicationAPI.broadcast(broadcastMessage);

		LoggerFactory.getLogger("CONTRACTNET").info(this.hashCode() + " -> CALL_FOR_PROPOSAL");

	}

	private void failed() {
		targetedTruck = null;
	}

	private void proposed(ContractNetMessage proposal) {

		if (targetedTruck != null) {
			lostProposals.add(proposal.getSender());
		} else if (bestProposal == null) {
			bestProposal = proposal;
		} else if (bestProposal.getProposalValue() < proposal.getProposalValue()) {
			lostProposals.add(bestProposal.getSender());
			bestProposal = proposal;
		} else {
			lostProposals.add(proposal.getSender());
		}
	}

	private void sendAcceptProposal(ContractNetMessage bestProposal, List<CommunicationUser> lostProposals) {

		// Send the response to the winner
		targetedTruck = (TruckAgent) bestProposal.getSender();
		ContractNetMessage acceptMessage = new ContractNetMessage(this);
		acceptMessage.setType(ContractNetMessageType.ACCEPT_PROPOSAL);
		communicationAPI.send(targetedTruck, acceptMessage);
		LoggerFactory.getLogger("CONTRACTNET").info(this.hashCode() + " -> ACCEPT_PROPOSAL -> "
				+ targetedTruck.hashCode());

		// Send responses to losers
		for (CommunicationUser loser : lostProposals) {
			ContractNetMessage rejectProposal = new ContractNetMessage(this);
			rejectProposal.setType(ContractNetMessageType.REJECT_PROPOSAL);

			LoggerFactory.getLogger("CONTRACTNET").info(this.hashCode() + " -> REJECT_PROPOSAL -> " + loser.hashCode());
			communicationAPI.send(loser, rejectProposal);
		}
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

	public void pickUp() {
		try {
			getPackage().pickup();
		} catch (AlreadyPickedUpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void deliver() {
		getPackage().deliver();
	}

	public boolean needsPickUp() {
		return !getPackage().isPickedUp();
	}

	public Point getDeliveryLocation() {
		return getPackage().getDeliveryLocation();
	}
}
