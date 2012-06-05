package project.strategies.delegatemas.colony.renderers;

import java.util.Set;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;

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
		synchronized (objects) {
			
			for (TickListener entry : objects) {
				
				// Package Agent
				if (entry.getClass().equals(TruckAgent.class)) {
					
					// draw the agent
					TruckAgent agent = (TruckAgent) entry;
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
					gc.drawText(agent.getId()+"", offsetX+10, offsetY-10);					
				}
				
			}
		}

	}
    
}
