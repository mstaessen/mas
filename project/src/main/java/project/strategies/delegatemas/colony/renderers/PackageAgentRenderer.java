package project.strategies.delegatemas.colony.renderers;

import java.util.Set;

import org.eclipse.swt.graphics.GC;

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
		synchronized (objects) {
			
		    int index = 0;
		    
			for (TickListener entry : objects) {
				
				// Package Agent
				if (entry.getClass().equals(PackageAgent.class)) {
					
					// draw the agent
					PackageAgent packageAgent = (PackageAgent) entry;
					Point p = packageAgent.getPosition();
					
					int x = (int) (xOrigin + (p.x - minX) * m) - radius;
					int y = (int) (yOrigin + (p.y - minY) * m) - radius;
					int offsetX = x - packageImage.getBounds().width / 2;
					int offsetY = y - packageImage.getBounds().height / 2;
					
					gc.drawImage(packageImage, offsetX, offsetY);
					gc.drawText(packageAgent.getId()+"", offsetX-10, offsetY-10);
					gc.drawText(packageAgent.toString(),800+(index%7)*55, (index/7)*230);
					index++;
					
					// draw the destination.
					PackageDestination destination = packageAgent.getDestination();
					p = destination.getPosition();
					
					x = (int) (xOrigin + (p.x - minX) * m) - radius;
					y = (int) (yOrigin + (p.y - minY) * m) - radius;
					offsetX = x - greenFlagImage.getBounds().width / 2;
					offsetY = y - greenFlagImage.getBounds().height / 2;
					
					gc.drawImage(greenFlagImage, offsetX, offsetY);
					gc.drawText(packageAgent.getId()+"", offsetX-10, offsetY+10);
					
				}
				
			}
		}

	}

}


