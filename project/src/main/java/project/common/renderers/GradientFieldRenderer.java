package project.common.renderers;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.graphics.GC;

import rinde.sim.core.graph.Point;
import rinde.sim.core.model.RoadModel;
import rinde.sim.core.model.virtual.Field;
import rinde.sim.core.model.virtual.GradientFieldModel;

public class GradientFieldRenderer extends AbstractRenderer {

    private final RoadModel rModel;
    private final GradientFieldModel gfModel;

    private static final DecimalFormat df = new DecimalFormat("#.####");

    public GradientFieldRenderer(RoadModel rModel, GradientFieldModel gfModel) {
	this.rModel = rModel;
	this.gfModel = gfModel;
    }

    @Override
    public void render(GC gc, double xOrigin, double yOrigin, double minX, double minY, double scale) {
	super.render(gc, xOrigin, yOrigin, minX, minY, scale);

	Set<Point> nodes = new HashSet<Point>(rModel.getGraph().getNodes());
	for (Point node : nodes) {
	    final int x = (int) (xOrigin + (node.x - minX) * scale);
	    final int y = (int) (yOrigin + (node.y - minY) * scale);

	    gc.drawText(getFieldValue(node), x + 4, y + 1);
	}
    }

    private String getFieldValue(Point point) {
	double value = 0;
	for (Field field : gfModel.getFields(point)) {
	    value += field.getFieldData().getStrength() / (1 + field.getDistance());
	}
	return df.format(value);
    }
}
