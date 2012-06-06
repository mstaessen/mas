package project.experiments.leuven.dmas;

import java.io.IOException;

import project.experiments.Experiment;
import project.experiments.leuven.DMASExperiment;
import project.strategies.delegatemas.colony.Settings;

public class BroadcastRangeExperiment extends DMASExperiment {

    public BroadcastRangeExperiment(String reportFile) throws IOException {
	super(reportFile);
    }

    public static void main(String[] args) {
	try {
	    Experiment bcrExperiment = new BroadcastRangeExperiment("./files/results/leuven.dmas_bcrExperiment.csv");

	    Settings.MAX_HOPS_FEASIBILITY_ANT = 1;
	    Settings.MAX_HOPS_EXPLORATION_ANT = 3;

	    for (Settings.BROADCAST_RANGE = 2000; Settings.BROADCAST_RANGE <= 18000; Settings.BROADCAST_RANGE += 1000) {
		bcrExperiment.runMultiple(10, true, false, "" + Settings.BROADCAST_RANGE);
	    }

	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

}
