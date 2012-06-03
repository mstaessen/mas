package project.common.renderers;

import java.util.Set;

import org.eclipse.swt.graphics.GC;

import project.common.trucks.Truck;
import rinde.sim.core.model.RoadModel;

public class TruckRenderer extends AbstractRenderer {

	private final RoadModel model;

	public TruckRenderer(RoadModel model) {
		this.model = model;
	}

	@Override
	public void render(GC gc, double xOrigin, double yOrigin, double minX, double minY, double scale) {
		super.render(gc, xOrigin, yOrigin, minX, minY, scale);

		Set<Truck> trucks = model.getObjectsOfType(Truck.class);
		synchronized (trucks) {
			for (Truck truck : trucks) {
				renderTruck(truck, gc, xOrigin, yOrigin, minX, minY, scale);
			}
		}
	}

	private void renderTruck(Truck t, GC gc, double xOrigin, double yOrigin, double minX, double minY, double scale) {
		final int x = (int) (xOrigin + (t.getPosition().x - minX) * scale) - 8;
		final int y = (int) (yOrigin + (t.getPosition().y - minY) * scale) - 8;

		if (t.hasLoad())
			gc.drawImage(loadedTruckImage, x, y);
		else
			gc.drawImage(emptyTruckImage, x, y);
		gc.drawText(String.valueOf(t.getId()), x - 10, y);
	}
}
