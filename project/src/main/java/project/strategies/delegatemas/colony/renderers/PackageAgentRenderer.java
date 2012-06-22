package project.strategies.delegatemas.colony.renderers;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;

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

    DecimalFormat df = new DecimalFormat("#.#");
    @Override
    public void render(GC gc, double xOrigin, double yOrigin, double minX, double minY, double m) {
	
	super.render(gc, xOrigin, yOrigin, minX, minY, m);

	if (!initialized)
	    super.initializeImages();

	final int radius = 4;

	Set<TickListener> objects = simulator.getTickListeners();

	synchronized (objects) {
	    
	    ArrayList<PackageAgent> list1 = new ArrayList<PackageAgent>();
	    for (TickListener entry : objects) {
		if (entry instanceof PackageAgent) {
		    list1.add((PackageAgent) entry);
		}
	    }

	    Collections.sort(list1);

	    synchronized (list1) {
		 int index = 0;
		    for (PackageAgent packageAgent : list1) {

			// draw table
			int w = 803 + (index % 6) * 100;
			int h = 13+(index / 6) * 180;
			gc.drawText(packageAgent.toString(7), w, h);
			gc.drawRectangle(new Rectangle(w - 3, h - 3, 94, 174));

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
			}

			if (packageAgent.getClaimer() != null) {
			    gc.drawText("~i: "+packageAgent.getClaimer().getId()+": "+df.format(packageAgent.getIntentionValue()),w,h+150);    
			} else {
			    gc.drawText("~i: no: "+df.format(packageAgent.getIntentionValue()),w,h+150);
			}
			

		    }

	    }
	 }
    }
}
