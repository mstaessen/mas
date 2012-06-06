package project.experiments.grid.dmas;

import java.io.IOException;

import project.experiments.Experiment;
import project.experiments.grid.DMASExperiment;
import project.strategies.delegatemas.colony.Settings;

public class FeasibilityAntHopsExperiment extends DMASExperiment {

    public FeasibilityAntHopsExperiment(String reportFile) throws IOException {
	super(reportFile);
    }

    public static void main(String[] args) {
	try {
	    Experiment hopsExperiment = new FeasibilityAntHopsExperiment(
		    "./files/results/grid.dmas_feasibilityHops.csv");

	    Settings.MAX_HOPS_FEASIBILITY_ANT = 1;
	    hopsExperiment.runMultiple(10, true, false, false, "Max Hops Feasibility Ants = "
		    + Settings.MAX_HOPS_FEASIBILITY_ANT);

	    for (int i = 0; i < 4; i++) {
		Settings.MAX_HOPS_FEASIBILITY_ANT++;
		hopsExperiment.runMultiple(10, true, false, true, "Max Hops Feasibility Ants = "
			+ Settings.MAX_HOPS_FEASIBILITY_ANT);
	    }

	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

}
