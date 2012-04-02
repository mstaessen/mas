package project.classic.gradientfield.renderers;

import java.util.Set;

import org.eclipse.swt.graphics.GC;

import project.classic.gradientfield.trucks.Truck;
import rinde.sim.core.model.RoadModel;

public class TruckRenderer extends AbstractRenderer {

	public TruckRenderer(RoadModel roadModel) {
		super(roadModel);
	}

	@Override
	public void render(GC gc, double xOrigin, double yOrigin, double minX, double minY, double scale) {
		super.render(gc, xOrigin, yOrigin, minX, minY, scale);

		Set<Truck> trucks = model.getObjectsOfType(Truck.class);
		synchronized (trucks) {
			for (Truck truck : trucks) {
				if (truck.hasLoad()) {
					renderLoadedTruck(truck, gc, xOrigin, yOrigin, minX, minY, scale);
				} else {
					renderAvailableTruck(truck, gc, xOrigin, yOrigin, minX, minY, scale);
				}
			}
		}
	}

	private void renderAvailableTruck(Truck t, GC gc, double xOrigin, double yOrigin, double minX, double minY,
			double scale) {
		final int x = (int) (xOrigin + (t.getPosition().x - minX) * scale) - 4;
		final int y = (int) (yOrigin + (t.getPosition().y - minY) * scale) - 4;

		int offsetX = x - emptyTruckImage.getBounds().width / 2;
		int offsetY = y - emptyTruckImage.getBounds().height / 2;
		gc.drawImage(emptyTruckImage, offsetX, offsetY);
		gc.drawText(String.valueOf(t.getAttraction()), offsetX + 16, offsetY);

	}

	private void renderLoadedTruck(Truck t, GC gc, double xOrigin, double yOrigin, double minX, double minY,
			double scale) {
		final int x = (int) (xOrigin + (t.getPosition().x - minX) * scale) - 4;
		final int y = (int) (yOrigin + (t.getPosition().y - minY) * scale) - 4;

		int offsetX = x - loadedTruckImage.getBounds().width / 2;
		int offsetY = y - loadedTruckImage.getBounds().height / 2;
		gc.drawImage(loadedTruckImage, offsetX, offsetY);
	}
}
