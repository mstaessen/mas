package project.common.packages;

import rinde.sim.core.SimulatorAPI;
import rinde.sim.core.SimulatorUser;
import rinde.sim.core.TickListener;

public abstract class AbstractPackageAgent implements SimulatorUser, TickListener {

    private SimulatorAPI simulator;
    private final Package pkg;

    public AbstractPackageAgent(Package pkg) {
	this.pkg = pkg;
    }

    @Override
    public void setSimulator(SimulatorAPI simulator) {
	this.simulator = simulator;
    }

    protected Package getPackage() {
	return pkg;
    }

    protected SimulatorAPI getSimulator() {
	return simulator;
    }

    @Override
    public void tick(long currentTime, long timeStep) {
    }

    @Override
    public void afterTick(long currentTime, long timeStep) {
    }
}
