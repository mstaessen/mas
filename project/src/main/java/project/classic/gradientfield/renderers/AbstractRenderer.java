package project.classic.gradientfield.renderers;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

import rinde.sim.core.model.RoadModel;
import rinde.sim.ui.renderers.Renderer;

public abstract class AbstractRenderer implements Renderer {

	private static final String BASE_PATH = "src/resources/graphics/";
	protected static boolean initialized = false;
	protected static Image emptyTruckImage;
	protected static Image loadedTruckImage;
	protected static Image packageImage;
	protected static Image lowPriorityImage;
	protected static Image mediumPriorityImage;
	protected static Image highPriorityImage;
	protected static Image dropzoneImage;
	protected final RoadModel model;

	public AbstractRenderer(RoadModel roadModel) {
		this.model = roadModel;
	}

	protected void initializeImages() {
		lowPriorityImage = ImageDescriptor.createFromImageData(new ImageData(BASE_PATH + "flag_green.png")).createImage();
		mediumPriorityImage = ImageDescriptor.createFromImageData(new ImageData(BASE_PATH + "flag_yellow.png")).createImage();
		highPriorityImage = ImageDescriptor.createFromImageData(new ImageData(BASE_PATH + "flag_red.png")).createImage();
		
		emptyTruckImage = ImageDescriptor.createFromImageData(new ImageData(BASE_PATH + "lorry_flatbed.png")).createImage();
		loadedTruckImage = ImageDescriptor.createFromImageData(new ImageData(BASE_PATH + "lorry.png")).createImage();
		
		packageImage = ImageDescriptor.createFromImageData(new ImageData(BASE_PATH + "package.png")).createImage();
		dropzoneImage = ImageDescriptor.createFromImageData(new ImageData(BASE_PATH + "asterisk_yellow.png")).createImage();
		
		initialized = true;
	}

	@Override
	public void render(GC gc, double xOrigin, double yOrigin, double minX, double minY, double scale) {
		if (!initialized) {
			initializeImages();
		}
	}
}
