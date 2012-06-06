package project.strategies.delegatemas.colony;

import rinde.sim.core.model.communication.CommunicationUser;
import rinde.sim.core.model.communication.Message;

public class IntentionAnt extends Message {

    private Path pathDone;
    private Path pathAhead;

    public IntentionAnt(CommunicationUser sender, Path pathDone, Path pathAhead) {
	super(sender);

	if (sender == null || !(sender instanceof TruckAgent)) {
	    throw new IllegalArgumentException("Sender has to be a truck agent");
	}

	if (pathDone == null || pathAhead == null) {
	    throw new IllegalArgumentException("pathDone and pathAhead may not be null");
	}

	this.pathDone = pathDone;
	this.pathAhead = pathAhead;
    }

    public Path getPathDone() {
	return pathDone;
    }

    public Path getPathAhead() {
	return pathAhead;
    }

}
