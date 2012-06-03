package project.common.renderers;

import java.util.Collection;

import org.eclipse.swt.graphics.GC;

import project.strategies.gradientfield.agents.TruckAgent;
import rinde.sim.core.model.virtual.GradientFieldModel;

public class TruckAgentRenderer extends AbstractRenderer {

	protected final GradientFieldModel model;

	public TruckAgentRenderer(GradientFieldModel model) {
		this.model = model;
	}

	@Override
	public void render(GC gc, double xOrigin, double yOrigin, double minX, double minY, double scale) {
		super.render(gc, xOrigin, yOrigin, minX, minY, scale);

		Collection<TruckAgent> agents = model.getObjectsOfType(TruckAgent.class);
		synchronized (agents) {
			for (TruckAgent agent : agents) {
				renderAgent(agent, gc, xOrigin, yOrigin, minX, minY, scale);
			}
		}
	}

	private void renderAgent(TruckAgent ta, GC gc, double xOrigin, double yOrigin, double minX, double minY,
			double scale) {
		final int x = (int) (xOrigin + (ta.getPosition().x - minX) * scale) - 8;
		final int y = (int) (yOrigin + (ta.getPosition().y - minY) * scale) - 8;

		if (ta.isEmitting()) {
			gc.drawText(String.valueOf(ta.getFieldStrength()), x + 18, y);
		}
	}
}
