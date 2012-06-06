package project.experiments.leuven;

import java.io.IOException;

import project.common.controller.AbstractController;
import project.strategies.gradientfield.GradientFieldController;
import rinde.sim.scenario.ConfigurationException;
import rinde.sim.scenario.Scenario;

public class GFExperiment extends LeuvenExperiment {

    public GFExperiment(String reportFile) throws IOException {
	super(reportFile);
    }

    @Override
    protected AbstractController createController(Scenario scenario) {
	try {
	    return new GradientFieldController(this, scenario, -1, LeuvenExperiment.MAP_URI);
	} catch (ConfigurationException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return null;
    }
}
