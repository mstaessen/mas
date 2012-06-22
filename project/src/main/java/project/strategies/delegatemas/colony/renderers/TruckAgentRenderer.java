package project.strategies.delegatemas.colony.renderers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;

import project.strategies.delegatemas.colony.PackageAgent;
import project.strategies.delegatemas.colony.TruckAgent;
import rinde.sim.core.Simulator;
import rinde.sim.core.TickListener;
import rinde.sim.core.graph.Point;

public class TruckAgentRenderer extends AbstractRenderer {

    protected Simulator simulator;

    public TruckAgentRenderer(Simulator simulator) {
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

	    ArrayList<TruckAgent> list1 = new ArrayList<TruckAgent>();
	    for (TickListener entry : objects2) {
		if (entry instanceof TruckAgent) {
		    list1.add((TruckAgent) entry);
		}
	    }

	    Collections.sort(list1);
	    
	    int index = 0;
	    for (TruckAgent agent : list1) {

		Point p = agent.getPosition();
		Image image;
		if (agent.getTruck().hasLoad()) {
		    image = loadedTruckImage;
		} else {
		    image = emptyTruckImage;
		}

		int x = (int) (xOrigin + (p.x - minX) * m) - radius;
		int y = (int) (yOrigin + (p.y - minY) * m) - radius;
		int offsetX = x - image.getBounds().width / 2;
		int offsetY = y - image.getBounds().height / 2;

		gc.drawImage(image, offsetX, offsetY);
		gc.drawText(agent.getId() + "", offsetX + 10, offsetY - 10);
		
		int w = 800 + (index % 7) * 200;
		int h = 400 + (index / 7) * 230;
		gc.drawText(agent.toString(15), w, h);
		
		gc.drawRectangle(new Rectangle(w-3, h-3, 194, 374));
		
		index++;
	    }

	}

    }

}
