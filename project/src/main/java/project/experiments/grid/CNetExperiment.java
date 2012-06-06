package project.experiments.grid;

import java.io.IOException;

import project.common.controller.AbstractController;
import project.strategies.contractnet.ContractNetController;
import rinde.sim.scenario.ConfigurationException;
import rinde.sim.scenario.Scenario;

public class CNetExperiment extends GridExperiment {

    public CNetExperiment(String reportFile) throws IOException {
	super(reportFile);
    }

    @Override
    protected AbstractController createController(Scenario scenario) {
	try {
	    return new ContractNetController(this, scenario, -1, GridExperiment.MAP_URI);
	} catch (ConfigurationException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return null;
    }

}
