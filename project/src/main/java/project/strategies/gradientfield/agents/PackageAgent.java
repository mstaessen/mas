package project.strategies.gradientfield.agents;

import project.common.packages.AbstractPackageAgent;
import project.common.packages.Package;
import rinde.sim.core.graph.Point;
import rinde.sim.core.model.virtual.FieldData;
import rinde.sim.core.model.virtual.GradientFieldAPI;
import rinde.sim.core.model.virtual.VirtualEntity;

public class PackageAgent extends AbstractPackageAgent implements VirtualEntity {

    @SuppressWarnings("unused")
    private GradientFieldAPI gradientFieldModel;

    public PackageAgent(Package pkg) {
	super(pkg);
    }

    @Override
    public void tick(long currentTime, long timeStep) {
	if (getPackage().isDelivered())
	    getSimulator().unregister(this);

    }

    @Override
    public void init(GradientFieldAPI model) {
	this.gradientFieldModel = model;
    }

    @Override
    public boolean isEmitting() {
	return !getPackage().isPickedUp();
    }

    @Override
    public Point getPosition() {
	return getPackage().getPickupLocation();
    }

    @Override
    public FieldData getFieldData() {
	return new FieldData() {
	    @Override
	    public double getStrength() {
		return 1000 * (getPackage().getPriority());
	    }
	};
    }

}
