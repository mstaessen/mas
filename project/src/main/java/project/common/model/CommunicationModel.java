package project.common.model;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import rinde.sim.core.TickListener;
import rinde.sim.core.graph.Point;
import rinde.sim.core.model.Model;
import rinde.sim.core.model.communication.CommunicationAPI;
import rinde.sim.core.model.communication.CommunicationUser;
import rinde.sim.core.model.communication.Message;

/**
 * This model ignores distances for direct communication but allows broadcasts
 * to be limited to a certain range.
 * 
 * @author Michiel Staessen
 * 
 */
public class CommunicationModel implements Model<CommunicationUser>, CommunicationAPI, TickListener {

    Set<CommunicationUser> users = new HashSet<CommunicationUser>();
    List<Entry<CommunicationUser, Message>> sendQueue = new LinkedList<Entry<CommunicationUser, Message>>();

    @Override
    public void send(CommunicationUser recipient, Message message) {
	sendQueue.add(new AbstractMap.SimpleEntry<CommunicationUser, Message>(recipient, message));
    }

    @Override
    public void broadcast(Message message) {
	for (CommunicationUser recipient : users) {
	    if (recipient != message.getSender() && canCommunicate(message.getSender(), recipient)) {
		send(recipient, message);
	    }
	}
    }

    @Override
    public void broadcast(Message message, Class<? extends CommunicationUser> type) {
	for (CommunicationUser recipient : users) {
	    if (recipient != message.getSender() && recipient.getClass().equals(type)
		    && canCommunicate(message.getSender(), recipient)) {
		send(recipient, message);
	    }
	}
    }

    public static boolean canCommunicate(CommunicationUser sender, CommunicationUser receiver) {
	if (sender == null || receiver == null) {
	    return false;
	}

	if (sender == receiver) {
	    return true;
	}

	if (sender.getPosition() == null || receiver.getPosition() == null) {
	    return false;
	}

	return Point.distance(sender.getPosition(), receiver.getPosition()) <= Math.max(sender.getRadius(),
		receiver.getRadius());
    }

    @Override
    public boolean register(CommunicationUser user) {
	users.add(user);
	try {
	    user.setCommunicationAPI(this);
	} catch (Exception e) {
	    users.remove(user);
	    return false;
	}
	return true;
    }

    @Override
    public boolean unregister(CommunicationUser user) {
	return users.remove(user);
    }

    @Override
    public Class<CommunicationUser> getSupportedType() {
	return CommunicationUser.class;
    }

    @Override
    public void tick(long currentTime, long timeStep) {
	// Don't send in the tick
    }

    @Override
    public void afterTick(long currentTime, long timeStep) {
	List<Entry<CommunicationUser, Message>> cache = sendQueue;
	sendQueue = new LinkedList<Entry<CommunicationUser, Message>>();
	for (Entry<CommunicationUser, Message> entry : cache) {
	    entry.getKey().receive(entry.getValue());
	}
	sendQueue.clear();
    }
}
