package project.strategies.delegatemas.colony;


import rinde.sim.core.model.communication.CommunicationUser;
import rinde.sim.core.model.communication.Message;

public class BackwardExplorationAnt extends Message {

    
    	private Path pathToDo;
    	private Path pathToEval;

	
	public BackwardExplorationAnt(CommunicationUser sender, Path pathToDo, Path pathToEval) {
	    super(sender);
	    
	    if (sender == null || !(sender instanceof TruckAgent)) {
		throw new IllegalArgumentException("Sender has to be a truck agent");
	    }
	    
	    if (pathToDo == null || pathToEval == null) {
		throw new IllegalArgumentException("pathToDo and pathToEval may not be null");
	    }
	    
	    this.pathToEval = pathToEval;
	    this.pathToDo = pathToDo;
	}
	
	
	public Path getPathToDo() {
	    return pathToDo;
	}
	
	public Path getPathToEval() {
	    return pathToEval;
	}
}

