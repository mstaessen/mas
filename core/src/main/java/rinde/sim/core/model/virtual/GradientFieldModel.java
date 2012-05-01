package rinde.sim.core.model.virtual;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import rinde.sim.core.graph.Graphs;
import rinde.sim.core.graph.Point;
import rinde.sim.core.model.Model;
import rinde.sim.core.model.RoadModel;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;

public class GradientFieldModel implements Model<VirtualEntity>, GradientFieldAPI {
	private RoadModel rm;
	protected Collection<VirtualEntity> entities = new HashSet<VirtualEntity>();

	public GradientFieldModel(RoadModel roadModel) {
		this.rm = roadModel;
	}

	@Override
	public Collection<Field> getFields(Point point) {
		Collection<Field> fields = new HashSet<Field>(entities.size());

		for (VirtualEntity entity : entities) {
			if (entity.isEmitting()) {
				fields.add(new Field(entity.getFieldData(), Graphs.pathLength(rm.getShortestPathTo(point, entity
						.getPosition()))));
			}
		}

		return fields;
	}

	@Override
	public Collection<Field> getSimpleFields(Point point) {
		Collection<Field> fields = new HashSet<Field>(entities.size());

		for (VirtualEntity entity : entities) {
			if (entity.isEmitting()) {
				double distance = Point.distance(point, entity.getPosition());
				fields.add(new Field(entity.getFieldData(), distance));
			}
		}

		return fields;
	}

	@Override
	public boolean register(VirtualEntity entity) {
		entity.init(this);
		return entities.add(entity);
	}

	@Override
	public boolean unregister(VirtualEntity entity) {
		if (entities.contains(entity)) {
			entities.remove(entity);
			return true;
		}
		return false;
	}

	@Override
	public Class<VirtualEntity> getSupportedType() {
		return VirtualEntity.class;
	}

	public Collection<VirtualEntity> getEntities() {
		return Collections.unmodifiableCollection(entities);
	}

	/**
	 * This method returns a set of {@link VirtualEntity} objects which exist in
	 * this model and are instances of the specified {@link Class}. The returned
	 * set is not a live view on the set, but a new created copy.
	 * @param type The type of returned objects.
	 * @return A set of {@link VirtualEntity} objects.
	 */
	@SuppressWarnings("unchecked")
	public <Y extends VirtualEntity> Set<Y> getObjectsOfType(final Class<Y> type) {
		return (Set<Y>) getObjects(new Predicate<VirtualEntity>() {
			@Override
			public boolean apply(VirtualEntity input) {
				return type.isInstance(input);
			}
		});
	}

	/**
	 * This method returns the set of {@link VirtualEntity} objects which exist
	 * in this model. The returned set is not a live view on the set, but a new
	 * created copy.
	 * @return The set of {@link VirtualEntity} objects.
	 */
	public Set<VirtualEntity> getObjects() {
		synchronized (entities) {
			Set<VirtualEntity> copy = new LinkedHashSet<VirtualEntity>();
			copy.addAll(entities);
			return copy;
		}
	}

	/**
	 * This method returns a set of {@link VirtualEntity} objects which exist in
	 * this model and satisfy the given {@link Predicate}. The returned set is
	 * not a live view on this model, but a new created copy.
	 * @param predicate The predicate that decides which objects to return.
	 * @return A set of {@link VirtualEntity} objects.
	 */
	public Set<VirtualEntity> getObjects(Predicate<VirtualEntity> predicate) {
		return Sets.filter(getObjects(), predicate);
	}
}