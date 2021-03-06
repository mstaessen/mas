package project.classic.gradientfield.packages;

import rinde.sim.core.SimulatorAPI;
import rinde.sim.core.SimulatorUser;
import rinde.sim.core.TickListener;
import rinde.sim.core.graph.Point;
import rinde.sim.core.model.virtual.FieldData;
import rinde.sim.core.model.virtual.GradientFieldAPI;
import rinde.sim.core.model.virtual.VirtualEntity;

public class PackageAgent implements TickListener, SimulatorUser, VirtualEntity {

	private SimulatorAPI simulator;
	private GradientFieldAPI gfApi;
	Package myPackage;

	public PackageAgent(Package myPackage) {
		this.myPackage = myPackage;
	}

	@Override
	public void setSimulator(SimulatorAPI api) {
		this.simulator = api;
	}

	@Override
	public void tick(long currentTime, long timeStep) {
		if (this.myPackage.isDelivered())
			this.simulator.unregister(this);

	}

	@Override
	public void afterTick(long currentTime, long timeStep) {
		// unused?
	}

	@Override
	public void init(GradientFieldAPI api) {
		this.gfApi = api;
	}

	@Override
	public boolean isEmitting() {
		return !myPackage.isPickedUp();
	}

	@Override
	public Point getPosition() {
		return myPackage.getPickupLocation();
	}

	@Override
	public FieldData getFieldData() {
		return new FieldData() {
			@Override
			public double getStrength() {
				return 1000 * myPackage.getPriority().getValue();
			}
		};
	}

}
