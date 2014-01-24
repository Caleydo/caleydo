/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.kaplanmeier.v2;

import gleem.linalg.Vec2f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.TablePerspectiveSelectionMixin;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.core.event.EventListenerManager.DeepScan;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.function.ArrayDoubleList;
import org.caleydo.core.util.function.IDoubleList;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.manage.GLLocation;
import org.caleydo.core.view.opengl.picking.IPickingLabelProvider;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingListenerComposite;
import org.caleydo.view.kaplanmeier.GLKaplanMeier;

import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * kaplan meier plot implementation as a {@link GLElement}
 *
 * @author Samuel Gratzl
 *
 */
public class KaplanMeierElement extends AKaplanMeierElement implements
		TablePerspectiveSelectionMixin.ITablePerspectiveMixinCallback {
	@DeepScan
	protected final TablePerspectiveSelectionMixin selections;

	/**
	 * for listening to my group
	 */
	private final SelectionManager recordGroupSelections;

	private int[] groupPickingIds;

	private final float maxTime;
	/**
	 * @param tablePerspective
	 */
	public KaplanMeierElement(TablePerspective tablePerspective, EDetailLevel detailLevel,
			boolean useParentMaxTimeIfPossible) {
		super(detailLevel);
		this.selections = new TablePerspectiveSelectionMixin(tablePerspective, this);
		this.maxTime = computeMaxtime(tablePerspective, useParentMaxTimeIfPossible);

		// listen for group selections
		recordGroupSelections = new SelectionManager(getDataDomain().getRecordGroupIDType().getIDCategory()
				.getPrimaryMappingType());
		selections.add(recordGroupSelections); // add to event manager
	}

	private static float computeMaxtime(TablePerspective tablePerspective, boolean useParentMaxTimeIfPossible) {
		float maxTimeValue = 0;
		if (tablePerspective.getParentTablePerspective() != null && useParentMaxTimeIfPossible) {
			maxTimeValue = GLKaplanMeier.calculateMaxAxisTime(tablePerspective.getParentTablePerspective());
		} else {
			maxTimeValue = GLKaplanMeier.calculateMaxAxisTime(tablePerspective);
		}
		return Math.abs(maxTimeValue);
	}

	public final TablePerspective getTablePerspective() {
		return selections.getTablePerspective();
	}

	@Override
	public <T> T getLayoutDataAs(Class<T> clazz, Supplier<? extends T> default_) {
		if (clazz.isAssignableFrom(Vec2f.class))
			return clazz.cast(getMinSize());
		if (clazz.isInstance(getTablePerspective()))
			return clazz.cast(getTablePerspective());
		if (clazz.isInstance(getDataDomain()))
			return clazz.cast(getDataDomain());
		return super.getLayoutDataAs(clazz, default_);
	}

	public final ATableBasedDataDomain getDataDomain() {
		return getTablePerspective().getDataDomain();
	}

	@Override
	public void onSelectionUpdate(SelectionManager manager) {
		repaintAll();
	}

	@Override
	public void onVAUpdate(TablePerspective tablePerspective) {
		repaintAll();
	}

	private GroupList getGroupList() {
		TablePerspective table = getTablePerspective();
		if (table.getParentTablePerspective() != null) // has a parent use its group list
			table = table.getParentTablePerspective();
		return table.getRecordPerspective().getVirtualArray().getGroupList();
	}

	@Override
	protected void init(IGLElementContext context) {
		super.init(context);

		// picking id per group
		GroupList groupList = getGroupList();
		groupPickingIds = new int[groupList.size()];
		GroupPicker g = new GroupPicker();
		IPickingListener p = PickingListenerComposite.concat(g, context.getSWTLayer().createTooltip(g));
		for (int i = 0; i < groupPickingIds.length; ++i) {
			Group group = groupList.get(i);
			groupPickingIds[i] = context.registerPickingListener(p, group.getID());
		}
	}

	@Override
	protected void takeDown() {
		for (int pickingId : groupPickingIds)
			context.unregisterPickingListener(pickingId);
		groupPickingIds = null;
		super.takeDown();
	}

	@Override
	protected void renderCurve(GLGraphics g, float w, float h) {
		// resolve data
		TablePerspective tablePerspective = getTablePerspective();
		Integer groupID = null;
		VirtualArray recordVA;
		if (tablePerspective.getParentTablePerspective() != null) {
			recordVA = tablePerspective.getParentTablePerspective().getRecordPerspective().getVirtualArray();
			groupID = tablePerspective.getRecordGroup().getID();
		} else {
			recordVA = tablePerspective.getRecordPerspective().getVirtualArray();
		}
		if (groupID != null) {
			// render this first to consider z-order
			renderCurve(g, tablePerspective.getRecordGroup(), recordVA, true, true, w, h);
		}
		for (Group group : recordVA.getGroupList()) {
			if (groupID != null && group.getID() == groupID.intValue())
				continue;
			renderCurve(g, group, recordVA, false, groupID != null, w, h);
		}
	}

	@Override
	public List<GLLocation> getLocations(EDimension dim, Iterable<Integer> dataIndizes) {
		final TablePerspective tablePerspective = getTablePerspective();
		VirtualArray dimensionVA = tablePerspective.getDimensionPerspective().getVirtualArray();

		final Table table = tablePerspective.getDataDomain().getTable();
		final Integer dimensionID = dimensionVA.get(0);
		final Vec2f wh = getSize();
		final float factor = dim.select(wh);
		List<GLLocation> r = new ArrayList<>();

		final VirtualArray va = tablePerspective.getRecordPerspective().getVirtualArray();
		if (tablePerspective.getParentTablePerspective() != null
				|| tablePerspective.getRecordPerspective().getVirtualArray().getGroupList().size() <= 1) {
			// single group
			IDoubleList data = readData(va.getIDs(), table, dimensionID);

			List<Vec2f> curve = createCurve(data);

			for (Integer dataIndex : dataIndizes) {
				double v = table.getNormalizedValue(dimensionID, va.get(dataIndex));
				v = Double.isNaN(v) ? 1 : v;
				Pair<Vec2f, Vec2f> loc = getLocation(curve, v);

				float offset = dim.select(loc.getFirst()) * factor;
				float size = dim.select(loc.getSecond()) * factor - offset;
				r.add(new GLLocation(offset, size));
			}
			return r;
		} else {
			LoadingCache<Group, List<Vec2f>> curveLoader = CacheBuilder.newBuilder().build(
					new CacheLoader<Group, List<Vec2f>>() {
						@Override
						public List<Vec2f> load(Group key) throws Exception {
							final List<Integer> iDs = tablePerspective.getRecordPerspective().getVirtualArray()
									.getIDsOfGroup(key.getGroupIndex());
							IDoubleList data = readData(iDs, table, dimensionID);

							List<Vec2f> curve = createCurve(data);
							return curve;
						}
					});
			for (Integer dataIndex : dataIndizes) {
				double v = table.getNormalizedValue(dimensionID, va.get(dataIndex));
				v = Double.isNaN(v) ? 1 : v;

				Group group = va.getGroupList().getGroupOfVAIndex(dataIndex);
				Pair<Vec2f, Vec2f> loc = getLocation(curveLoader.getUnchecked(group), v);

				float offset = dim.select(loc.getFirst()) * factor;
				float size = dim.select(loc.getSecond()) * factor - offset;
				r.add(new GLLocation(offset, size));
			}
			return r;
		}
	}

	private void renderCurve(GLGraphics g, Group group, VirtualArray recordVA, boolean fillCurve,
			boolean hasPrimaryCurve, float w, float h) {

		List<Integer> recordIDs = recordVA.getIDsOfGroup(group.getGroupIndex());

		int lineWidth = 1;
		Color color = Color.DARK_GRAY;
		if (hasPrimaryCurve && !fillCurve) {
			color = Color.LIGHT_GRAY;
		}
		SelectionType selectionType = recordGroupSelections.getHighestSelectionType(group.getID());
		if (selectionType != null) {
			// || (group.getID() == mouseOverGroupID)) {
			lineWidth += 1;
			color = selectionType.getColor();
		}
		lineWidth *= 2;
		g.lineWidth(lineWidth);

		renderSingleKaplanMeierCurve(g, recordIDs, fillCurve, group, color, w, h);
	}

	@Override
	protected Axis getAxis(EDimension dim) {
		final TablePerspective tablePerspective = getTablePerspective();
		if (dim.isHorizontal()) {
			return new Axis(tablePerspective.getDimensionPerspective().getLabel(), 6, maxTime);
		} else
			return new Axis("Percentage of "
					+ tablePerspective.getRecordPerspective().getIdType().getIDCategory().getCategoryName(), 6, 100);
	}


	private void renderSingleKaplanMeierCurve(GLGraphics g, List<Integer> recordIDs, boolean fillCurve, Group group,
			Color color, float w, float h) {
		final TablePerspective tablePerspective = getTablePerspective();
		VirtualArray dimensionVA = tablePerspective.getDimensionPerspective().getVirtualArray();

		final Table table = tablePerspective.getDataDomain().getTable();
		final Integer dimensionID = dimensionVA.get(0);

		// prepare data
		IDoubleList data = readData(recordIDs, table, dimensionID);

		g.pushName(groupPickingIds[group.getGroupIndex()]);

		String label = detailLevel == EDetailLevel.HIGH ? group.getLabel() : null;

		final List<Vec2f> curve = createCurve(data);
		drawCurve(g, color, w, h, fillCurve, recordGroupSelections.getHighestSelectionType(group.getID()), label,
				curve);

		g.popName();
	}

	private static IDoubleList readData(List<Integer> recordIDs, final Table table, final Integer dimensionID) {
		double[] data = new double[recordIDs.size()];
		for (int i = 0; i < data.length; ++i) {
			Integer recordID = recordIDs.get(i);
			float normalizedValue = table.getNormalizedValue(dimensionID, recordID);
			if (Float.isNaN(normalizedValue)) {
				// we assume that those who don't have an entry are still alive.
				normalizedValue = 1;
			}
			data[i] = normalizedValue;
		}
		Arrays.sort(data);
		return new ArrayDoubleList(data);
	}


	private class GroupPicker implements IPickingLabelProvider,IPickingListener {
		@Override
		public void pick(Pick pick) {
			switch(pick.getPickingMode()) {
			case MOUSE_OVER:
				recordGroupSelections.clearSelection(SelectionType.MOUSE_OVER);
				recordGroupSelections.addToType(SelectionType.MOUSE_OVER, pick.getObjectID());
				selections.fireSelectionDelta(recordGroupSelections);
				repaint();
				break;
			case MOUSE_OUT:
				recordGroupSelections.removeFromType(SelectionType.MOUSE_OVER, pick.getObjectID());
				selections.fireSelectionDelta(recordGroupSelections);
				repaint();
				break;
			case CLICKED:
				recordGroupSelections.clearSelection(SelectionType.SELECTION);
				recordGroupSelections.addToType(SelectionType.SELECTION, pick.getObjectID());
				selections.fireSelectionDelta(recordGroupSelections);
				repaint();
				break;
			default:
				break;
			}
		}

		@Override
		public String getLabel(Pick pick) {
			int id = pick.getObjectID();
			for (Group g : getGroupList())
				if (g.getID() == id)
					return g.getLabel();
			return null;
		}
	}
}
