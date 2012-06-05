package project.common.model;

import java.util.HashSet;
import java.util.Set;

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
public class CommunicationModel implements Model<CommunicationUser>, CommunicationAPI {

    Set<CommunicationUser> users = new HashSet<CommunicationUser>();

    @Override
    public void send(CommunicationUser recipient, Message message) {
	recipient.receive(message);
    }

    @Override
    public void broadcast(Message message) {
	for (CommunicationUser user : users) {
	    if (user != message.getSender() && canCommunicate(message.getSender(), user)) {
		send(user, message);
	    }
	}
    }

    @Override
    public void broadcast(Message message, Class<? extends CommunicationUser> type) {
	for (CommunicationUser user : users) {
	    if (user != message.getSender() && user.getClass().equals(type)
		    && canCommunicate(message.getSender(), user)) {
		send(user, message);
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

	return Point.distance(sender.getPosition(), receiver.getPosition()) < Math.max(sender.getRadius(),
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

}
