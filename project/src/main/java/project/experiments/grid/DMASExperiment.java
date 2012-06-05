package project.experiments.grid;

import java.io.IOException;

import project.common.controller.AbstractController;
import project.strategies.delegatemas.DelegateMasController;
import rinde.sim.scenario.ConfigurationException;
import rinde.sim.scenario.Scenario;

public class DMASExperiment extends GridExperiment {

    public DMASExperiment(String reportFile) throws IOException {
	super(reportFile);
    }

    @Override
    protected AbstractController createController(Scenario scenario) {
	try {
	    return new DelegateMasController(this, scenario, -1, GridExperiment.MAP_URI);
	} catch (ConfigurationException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return null;
    }

}
