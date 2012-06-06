package project.experiments.grid.dmas;

import java.io.IOException;

import project.experiments.Experiment;
import project.experiments.grid.DMASExperiment;
import project.strategies.delegatemas.colony.Settings;

public class BroadcastRangeExperiment extends DMASExperiment {

    public BroadcastRangeExperiment(String reportFile) throws IOException {
	super(reportFile);
    }

    public static void main(String[] args) {
	try {
	    Experiment bcrExperiment = new BroadcastRangeExperiment("./files/results/grid.dmas_bcrExperiment.csv");

	    Settings.MAX_HOPS_FEASIBILITY_ANT = 1;
	    Settings.MAX_HOPS_EXPLORATION_ANT = 3;

	    for (Settings.BROADCAST_RANGE = 20; Settings.BROADCAST_RANGE <= 180; Settings.BROADCAST_RANGE += 20) {
		bcrExperiment.runMultiple(10, true, false, "" + Settings.BROADCAST_RANGE);
	    }

	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

}
