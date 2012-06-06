package project.strategies.delegatemas.colony.renderers;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;

import project.strategies.delegatemas.colony.PackageAgent;
import project.strategies.delegatemas.colony.PackageDestination;
import rinde.sim.core.Simulator;
import rinde.sim.core.TickListener;
import rinde.sim.core.graph.Point;

public class PackageAgentRenderer extends AbstractRenderer {

    protected Simulator simulator;

    public PackageAgentRenderer(Simulator simulator) {
	this.simulator = simulator;
    }

    @Override
    public void render(GC gc, double xOrigin, double yOrigin, double minX, double minY, double m) {

	super.render(gc, xOrigin, yOrigin, minX, minY, m);

	if (!initialized)
	    super.initializeImages();

	final int radius = 4;

	Set<TickListener> objects = simulator.getTickListeners();
	Set<TickListener> objects2 = new HashSet<TickListener>();
	objects2.addAll(objects);
	synchronized (objects2) {

	    int index = 0;
	    for (TickListener entry : objects2) {

		// Package Agent
		if (entry.getClass().equals(PackageAgent.class)) {

		    PackageAgent packageAgent = (PackageAgent) entry;

		    // draw table
		    gc.drawText(packageAgent.toString(), 800 + (index % 7) * 70, (index / 7) * 230);
		    index++;

		    int x, y, offsetX, offsetY;
		    Point p;
		    // draw the agent
		    if (!packageAgent.getPackage().isPickedUp()) {

			p = packageAgent.getPosition();

			x = (int) (xOrigin + (p.x - minX) * m) - radius;
			y = (int) (yOrigin + (p.y - minY) * m) - radius;
			offsetX = x - packageImage.getBounds().width / 2;
			offsetY = y - packageImage.getBounds().height / 2;

			gc.drawImage(packageImage, offsetX, offsetY);
			gc.drawText(packageAgent.getId() + "", offsetX - 10, offsetY - 10);

		    }

		    // draw the destination.
		    if (!packageAgent.getPackage().isDelivered()) {

			PackageDestination destination = packageAgent.getDestination();
			p = destination.getPosition();

			Image image = super.getFlagImage(packageAgent.getPackage());
			x = (int) (xOrigin + (p.x - minX) * m) - radius;
			y = (int) (yOrigin + (p.y - minY) * m) - radius;
			offsetX = x - image.getBounds().width / 2;
			offsetY = y - image.getBounds().height / 2;

			gc.drawImage(image, offsetX, offsetY);
			gc.drawText(packageAgent.getId() + "", offsetX - 10, offsetY + 10);
			gc.drawText(packageAgent.getPackage().getPriority() + "", offsetX + 10, offsetY + 10);
		    }
		}
	    }
	}
    }
}
