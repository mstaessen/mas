package project.common.renderers;

import java.util.Set;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;

import project.classic.gradientfield.packages.Package;
import rinde.sim.core.model.RoadModel;

public class PackageRenderer extends AbstractRenderer {

	private final RoadModel model;

	public PackageRenderer(RoadModel roadModel) {
		this.model = roadModel;
	}

	@Override
	public void render(GC gc, double xOrigin, double yOrigin, double minX, double minY, double scale) {
		super.render(gc, xOrigin, yOrigin, minX, minY, scale);

		Set<Package> packages = model.getObjectsOfType(Package.class);
		synchronized (packages) {
			for (Package p : packages) {
				if (!p.isPickedUp()) {
					renderPickupLocation(p, gc, xOrigin, yOrigin, minX, minY, scale);
				} else {

					if (!p.isDelivered()) {
						renderDeliveryLocation(p, gc, xOrigin, yOrigin, minX, minY, scale);
					}
				}
			}
		}
	}

	private void renderDeliveryLocation(Package p, GC gc, double xOrigin, double yOrigin, double minX, double minY,
			double scale) {
		final int x = (int) (xOrigin + (p.getDeliveryLocation().x - minX) * scale) - 8;
		final int y = (int) (yOrigin + (p.getDeliveryLocation().y - minY) * scale) - 8;

		gc.drawImage(dropOffImage, x, y);
		gc.drawText(String.valueOf(p.getId()), x + 18, y);
	}

	private void renderPickupLocation(Package p, GC gc, double xOrigin, double yOrigin, double minX, double minY,
			double scale) {
		final int x = (int) (xOrigin + (p.getPickupLocation().x - minX) * scale) - 8;
		final int y = (int) (yOrigin + (p.getPickupLocation().y - minY) * scale) - 8;

		gc.drawImage(getImage(p), x, y);
		gc.drawText(String.valueOf(p.getId()), x + 18, y);
	}

	private Image getImage(Package p) {
		switch (p.getPriority()) {
		case LOW:
			return greenFlagImage;
		case MEDIUM:
			return yellowFlagImage;
		case HIGH:
			return redFlagImage;
		}
		return null;
	}
}
