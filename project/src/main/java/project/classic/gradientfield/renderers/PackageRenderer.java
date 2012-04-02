package project.classic.gradientfield.renderers;

import java.util.Set;

import org.eclipse.swt.graphics.GC;

import project.classic.gradientfield.packages.Package;
import rinde.sim.core.model.RoadModel;

public class PackageRenderer extends AbstractRenderer {

	public PackageRenderer(RoadModel roadModel) {
		super(roadModel);
	}

	@Override
	public void render(GC gc, double xOrigin, double yOrigin, double minX, double minY, double scale) {
		super.render(gc, xOrigin, yOrigin, minX, minY, scale);

		Set<Package> packages = model.getObjectsOfType(Package.class);
		synchronized (packages) {
			for (Package p : packages) {

				if (p.needsPickUp()) {
					renderPickupLocation(p, gc, xOrigin, yOrigin, minX, minY, scale);
				}

				if (!p.delivered()) {
					renderDeliveryLocation(p, gc, xOrigin, yOrigin, minX, minY, scale);
				}
			}
		}
	}

	private void renderDeliveryLocation(Package p, GC gc, double xOrigin, double yOrigin, double minX, double minY,
			double scale) {
		final int x = (int) (xOrigin + (p.getDeliveryLocation().x - minX) * scale) - 4;
		final int y = (int) (yOrigin + (p.getDeliveryLocation().y - minY) * scale) - 4;

		int offsetX = x - dropzoneImage.getBounds().width / 2;
		int offsetY = y - dropzoneImage.getBounds().height / 2;
		gc.drawImage(dropzoneImage, offsetX, offsetY);
	}

	private void renderPickupLocation(Package p, GC gc, double xOrigin, double yOrigin, double minX, double minY,
			double scale) {
		final int x = (int) (xOrigin + (p.getPickupLocation().x - minX) * scale) - 4;
		final int y = (int) (yOrigin + (p.getPickupLocation().y - minY) * scale) - 4;

		int offsetX = x - lowPriorityImage.getBounds().width / 2;
		int offsetY = y - lowPriorityImage.getBounds().height / 2;

		switch ((int) p.getPriority()) {
		case 0:
			gc.drawImage(lowPriorityImage, offsetX, offsetY);
			break;

		case 1:
			gc.drawImage(mediumPriorityImage, offsetX, offsetY);
			break;

		case 2:
			gc.drawImage(highPriorityImage, offsetX, offsetY);
			break;
		}
	}
}
