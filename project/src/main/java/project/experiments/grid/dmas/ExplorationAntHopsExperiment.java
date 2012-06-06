package project.experiments.grid.dmas;

import java.io.IOException;

import project.experiments.Experiment;
import project.experiments.grid.DMASExperiment;
import project.strategies.delegatemas.colony.Settings;

public class ExplorationAntHopsExperiment extends DMASExperiment {

    public ExplorationAntHopsExperiment(String reportFile) throws IOException {
	super(reportFile);
    }

    public static void main(String[] args) {
	try {
	    Experiment hopsExperiment = new ExplorationAntHopsExperiment(
		    "./files/results/grid.dmas_explorationHops.csv");

	    Settings.BROADCAST_RANGE = 60;
	    Settings.MAX_HOPS_FEASIBILITY_ANT = 1;

	    for (Settings.MAX_HOPS_EXPLORATION_ANT = 1; Settings.MAX_HOPS_EXPLORATION_ANT <= 5; Settings.MAX_HOPS_EXPLORATION_ANT++) {
		hopsExperiment.runMultiple(10, true, false, "Max Hops Exploration Ants = "
			+ Settings.MAX_HOPS_EXPLORATION_ANT);
	    }

	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

}
