package project.strategies.contractnet.agents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.LoggerFactory;

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
	private PackageAgent targetedPackage;

	private List<CommunicationUser> lostProposers = new ArrayList<CommunicationUser>();
	private CommunicationUser bestProposer;
	private double bestDistance;

	public TruckAgent(Truck truck, double radius, double reliability) {
		super(truck);

		this.radius = radius;
		this.reliability = reliability;
	}

	@Override
	public void tick(long currentTime, long timeStep) {

		handleIncomingMessages(mailbox.getMessages());

		// Drive when possible
		if (targetedPackage != null) {
			if (!getPath().isEmpty()) {
				getTruck().drive(getPath(), timeStep);
			} else {
				if (targetedPackage.needsPickUp())
					pickUpAndGo();
				else
					deliver();
			}
		}
	}

	@SuppressWarnings("incomplete-switch")
	private void handleIncomingMessages(Collection<Message> messages) {
		bestProposer = null;
		lostProposers.clear();

		for (Message m : messages) {
			ContractNetMessage message = (ContractNetMessage) m;
			switch (message.getType()) {
			case CALL_FOR_PROPOSAL:
				calledForProposal(message.getSender());
				break;
			case ACCEPT_PROPOSAL:
				acceptedProposal(message.getSender());
				break;
			case REJECT_PROPOSAL:
				// Do Nothing;
				break;
			}
		}

		if (bestProposer != null && targetedPackage == null)
			sendProposal();
	}

	private void calledForProposal(CommunicationUser sender) {

		if (targetedPackage != null) {
			lostProposers.add(sender);
		} else if (bestProposer == null) {
			bestProposer = sender;
			bestDistance = Graphs.pathLength(getTruck().getRoadModel().getShortestPathTo(getTruck(), sender
					.getPosition()));
		} else {
			double distance = Graphs.pathLength(getTruck().getRoadModel().getShortestPathTo(getTruck(), sender
					.getPosition()));
			if (distance < bestDistance) {
				lostProposers.add(bestProposer);
				bestProposer = sender;
				bestDistance = distance;
			} else {
				lostProposers.add(sender);
			}
		}
	}

	private void sendProposal() {

		for (CommunicationUser proposer : lostProposers) {
			ContractNetMessage reply = new ContractNetMessage(this);
			reply.setType(ContractNetMessageType.REFUSE);
			communicationAPI.send(proposer, reply);
			LoggerFactory.getLogger("CONTRACTNET").info(proposer.hashCode() + " <- REFUSE <- " + this.hashCode());
		}

		ContractNetMessage reply = new ContractNetMessage(this);
		Point dest = bestProposer.getPosition();
		double distance = Graphs.pathLength(getTruck().getRoadModel().getShortestPathTo(getTruck(), dest));
		reply.setType(ContractNetMessageType.PROPOSE);
		reply.setProposalValue(1 / distance);
		communicationAPI.send(bestProposer, reply);
		LoggerFactory.getLogger("CONTRACTNET").info(bestProposer.hashCode() + " <- PROPOSE <- " + this.hashCode());
	}

	private void acceptedProposal(CommunicationUser sender) {

		if (targetedPackage == null) {
			Point dest = sender.getPosition();

			setPath(new LinkedList<Point>(getTruck().getRoadModel().getShortestPathTo(getTruck(), dest)));
			targetedPackage = (PackageAgent) sender;

			LoggerFactory.getLogger("CONTRACTNET").info(this.hashCode() + " : starting path");
		} else {
			ContractNetMessage message = new ContractNetMessage(this);
			message.setType(ContractNetMessageType.FAILURE);
			communicationAPI.send(sender, message);
			LoggerFactory.getLogger("CONTRACTNET").info(sender.hashCode() + " <- FAILURE <- " + this.hashCode());
		}

	}

	private void pickUpAndGo() {
		targetedPackage.pickUp();
		Point dest = targetedPackage.getDeliveryLocation();
		setPath(new LinkedList<Point>(getTruck().getRoadModel().getShortestPathTo(getTruck(), dest)));
		LoggerFactory.getLogger("CONTRACTNET").info(toString() + " : picked up : " + targetedPackage.hashCode());
	}

	private void deliver() {
		ContractNetMessage m = new ContractNetMessage(this);
		m.setType(ContractNetMessageType.INFORM_DONE);
		communicationAPI.send(targetedPackage, m);
		getTruck().tryDelivery();
		LoggerFactory.getLogger("CONTRACTNET").info(toString() + " : delivered : " + targetedPackage.hashCode());
		targetedPackage = null;
	}

	@Override
	public void setCommunicationAPI(CommunicationAPI api) {
		this.communicationAPI = api;
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
		this.mailbox.receive(message);
	}

	@Override
	public String toString() {
		return "PackageAgent-" + getTruck().getId();
	}

}
