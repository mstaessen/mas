package project.strategies.delegatemas.colony.renderers;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

import project.common.packages.DeliveryLocation;
import project.common.packages.Package;
import project.common.packages.Priority;
import project.strategies.delegatemas.colony.PackageDestination;

import rinde.sim.ui.renderers.Renderer;

public abstract class AbstractRenderer implements Renderer {

	private static final String BASE_PATH = "src/resources/graphics/";
	protected static boolean initialized = false;
	protected static Image emptyTruckImage;
	protected static Image loadedTruckImage;
	protected static Image packageImage;
	protected static Image greenFlagImage;
	protected static Image yellowFlagImage;
	protected static Image redFlagImage;
	protected static Image dropOffImage;

	protected void initializeImages() {
		greenFlagImage = ImageDescriptor.createFromImageData(new ImageData(BASE_PATH + "flag_green.png")).createImage();
		yellowFlagImage = ImageDescriptor.createFromImageData(new ImageData(BASE_PATH + "flag_yellow.png"))
				.createImage();
		redFlagImage = ImageDescriptor.createFromImageData(new ImageData(BASE_PATH + "flag_red.png")).createImage();

		emptyTruckImage = ImageDescriptor.createFromImageData(new ImageData(BASE_PATH + "lorry_flatbed.png"))
				.createImage();
		loadedTruckImage = ImageDescriptor.createFromImageData(new ImageData(BASE_PATH + "lorry.png")).createImage();

		packageImage = ImageDescriptor.createFromImageData(new ImageData(BASE_PATH + "package.png")).createImage();
		dropOffImage = ImageDescriptor.createFromImageData(new ImageData(BASE_PATH + "asterisk_yellow.png"))
				.createImage();

		initialized = true;
	}

	@Override
	public void render(GC gc, double xOrigin, double yOrigin, double minX, double minY, double scale) {
		if (!initialized) {
			initializeImages();
		}
	}
	
	
	protected Image getFlagImage(Package p) {
		switch (Priority.valueOf(p.getPriority())) {
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
