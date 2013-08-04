/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.kaplanmeier.v2;

import static org.caleydo.view.kaplanmeier.GLKaplanMeier.AXIS_LABEL_TEXT_HEIGHT_PIXELS;
import static org.caleydo.view.kaplanmeier.GLKaplanMeier.AXIS_LABEL_TEXT_SIDE_SPACING_PIXELS;
import static org.caleydo.view.kaplanmeier.GLKaplanMeier.AXIS_TICK_LABEL_SPACING_PIXELS;
import static org.caleydo.view.kaplanmeier.GLKaplanMeier.BOTTOM_AXIS_SPACING_PIXELS;
import static org.caleydo.view.kaplanmeier.GLKaplanMeier.LEFT_AXIS_SPACING_PIXELS;
import static org.caleydo.view.kaplanmeier.GLKaplanMeier.RIGHT_AXIS_SPACING_PIXELS;
import static org.caleydo.view.kaplanmeier.GLKaplanMeier.TOP_AXIS_SPACING_PIXELS;
import gleem.linalg.Vec2f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.view.ASingleTablePerspectiveElement;
import org.caleydo.core.view.opengl.picking.IPickingLabelProvider;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingListenerComposite;
import org.caleydo.core.view.opengl.util.spline.TesselatedPolygons;
import org.caleydo.view.kaplanmeier.GLKaplanMeier;

/**
 * kaplan meier plot implementation as a {@link GLElement}
 *
 * @author Samuel Gratzl
 *
 */
public class KaplanMeierElement extends ASingleTablePerspectiveElement {
	/**
	 * for listening to my group
	 */
	private final SelectionManager recordGroupSelections;

	private int[] groupPickingIds;

	private final float maxAxisTime;

	private final EDetailLevel detailLevel;

	private final GLPadding padding;
	/**
	 * @param tablePerspective
	 */
	public KaplanMeierElement(TablePerspective tablePerspective, EDetailLevel detailLevel,
			boolean useParentMaxTimeIfPossible) {
		super(tablePerspective);

		// listen for group selections
		recordGroupSelections = new SelectionManager(getDataDomain().getRecordGroupIDType().getIDCategory()
				.getPrimaryMappingType());
		selections.add(recordGroupSelections); // add to event manager

		this.maxAxisTime = computeMaxtime(tablePerspective, useParentMaxTimeIfPossible);
		this.detailLevel = detailLevel;

		if (detailLevel.compareTo(EDetailLevel.MEDIUM) < 0)
			setVisibility(EVisibility.VISIBLE);

		if (detailLevel == EDetailLevel.HIGH)
			padding = new GLPadding(LEFT_AXIS_SPACING_PIXELS, TOP_AXIS_SPACING_PIXELS, RIGHT_AXIS_SPACING_PIXELS,
					BOTTOM_AXIS_SPACING_PIXELS);
		else
			padding = GLPadding.ZERO;
		setPicker(null);
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
	protected void renderImpl(GLGraphics g, float w, float h) {
		render(g, w, h);
		super.renderImpl(g, w, h);
	}

	private void render(GLGraphics g, float w, float h) {
		final TablePerspective tablePerspective = getTablePerspective();

		// resolve data
		Integer groupID = null;
		VirtualArray recordVA;
		if (tablePerspective.getParentTablePerspective() != null) {
			recordVA = tablePerspective.getParentTablePerspective().getRecordPerspective().getVirtualArray();
			groupID = tablePerspective.getRecordGroup().getID();
		} else {
			recordVA = tablePerspective.getRecordPerspective().getVirtualArray();
		}

		g.gl.glPushAttrib(GL2.GL_LINE_BIT);
		{
			g.save().move(padding.left, padding.top);

			float wp = w - padding.hor();
			float hp = h - padding.vert();

			if (groupID != null) {
				// render this first to consider z-order
				renderCurve(g, tablePerspective.getRecordGroup(), recordVA, true, true, wp, hp);
			}
			for (Group group : recordVA.getGroupList()) {
				if (groupID != null && group.getID() == groupID.intValue())
					continue;
				renderCurve(g, group, recordVA, false, groupID != null, wp, hp);
			}
			g.restore();
		}

		if (detailLevel == EDetailLevel.HIGH && !g.isPickingPass())
			renderAxes(g, w, h);

		g.gl.glPopAttrib();
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		if (getVisibility() == EVisibility.PICKABLE) {
			render(g, w, h);
		}
		super.renderPickImpl(g, w, h);
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

	private void renderAxes(GLGraphics g, float w, float h) {
		assert detailLevel == EDetailLevel.HIGH;
		final TablePerspective tablePerspective = getTablePerspective();

		g.color(Color.BLACK);
		final String xAxisLabel = tablePerspective.getDimensionPerspective().getLabel();
		final String yAxisLabel = "Percentage of "
				+ tablePerspective.getRecordPerspective().getIdType().getIDCategory().getCategoryName();

		g.drawText(xAxisLabel, 0, h - AXIS_LABEL_TEXT_SIDE_SPACING_PIXELS - AXIS_LABEL_TEXT_HEIGHT_PIXELS, w,
				AXIS_LABEL_TEXT_HEIGHT_PIXELS, VAlign.CENTER);
		{// draw rotated text
			g.save();
			g.gl.glRotatef(-90, 0, 0, 1);
			g.move(-h, 0);
			g.drawText(yAxisLabel, 0, AXIS_LABEL_TEXT_SIDE_SPACING_PIXELS, h, AXIS_LABEL_TEXT_HEIGHT_PIXELS,
					VAlign.CENTER);
			g.restore();
		}

		g.move(padding.left, padding.top);
		float wp = w - padding.hor();
		float hp = h - padding.vert();
		g.lineWidth(2);
		renderSingleAxis(g, true, 6, maxAxisTime, wp, hp);
		renderSingleAxis(g, false, 6, 100, wp, hp);
		g.move(-padding.left, -padding.top);
	}

	private void renderSingleAxis(GLGraphics g, boolean isXAxis, int numTicks, float maxTickValue, float w, float h) {
		Vec2f start = new Vec2f(0, h);
		Vec2f end = isXAxis ? new Vec2f(w, h) : new Vec2f(0, 0);

		g.drawLine(start.x(), start.y(), end.x(), end.y());

		float factor = (isXAxis ? w : h) / (numTicks - 1);
		float step = maxTickValue / (numTicks - 1);

		// render ticks
		for (int i = 0; i < numTicks; i++) {
			float v = factor * i;
			String text = String.valueOf((int) (i * step));
			if (isXAxis) {
				g.drawLine(v, h - 7, v, h + 7);
				g.drawText(text, v - 100, h + AXIS_TICK_LABEL_SPACING_PIXELS, 200, 13, VAlign.CENTER);
			} else {
				g.drawLine(-7, v, 7, v);
				g.drawText(text, -100, v - 7, 100 - AXIS_TICK_LABEL_SPACING_PIXELS, 13, VAlign.RIGHT);
			}
		}
	}

	private void renderSingleKaplanMeierCurve(GLGraphics g, List<Integer> recordIDs, boolean fillCurve, Group group,
			Color color, float w, float h) {
		final TablePerspective tablePerspective = getTablePerspective();
		VirtualArray dimensionVA = tablePerspective.getDimensionPerspective().getVirtualArray();

		final Table table = tablePerspective.getDataDomain().getTable();
		final Integer dimensionID = dimensionVA.get(0);

		// prepare data
		float[] data = readData(recordIDs, table, dimensionID);

		g.pushName(groupPickingIds[group.getGroupIndex()]);

		drawCurve(g, data, color, group, w, h, fillCurve);

		g.popName();
	}

	private static float[] readData(List<Integer> recordIDs, final Table table, final Integer dimensionID) {
		float[] data = new float[recordIDs.size()];
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
		return data;
	}

	private void drawCurve(GLGraphics g, float[] data, Color color, Group group, float w, float h, boolean fillCurve) {
		List<Vec2f> linePoints = createCurve(data, w, h);

		if (fillCurve) {
			if (color.isGray())
				g.color(Color.MEDIUM_DARK_GRAY);
			else
				g.color(color.lessSaturated());
			// add addition points to get a closed shape
			linePoints.add(new Vec2f(w, h));
			linePoints.add(new Vec2f(0, h)); // origin
			g.fillPolygon(TesselatedPolygons.polygon2(linePoints));
			linePoints.remove(linePoints.size() - 1); // remove again not part of the curve
			linePoints.remove(linePoints.size() - 1); // remove again not part of the curve
		}

		float translationZ = 0;
		if (detailLevel == EDetailLevel.HIGH && !g.isPickingPass()) {
			Color bgColor = new Color(1, 1, 1, 0.5f);
			Color textColor = Color.BLACK;
			SelectionType selectionType = recordGroupSelections.getHighestSelectionType(group.getID());
			if (selectionType == SelectionType.SELECTION) {
				bgColor = Color.WHITE;
				textColor = color;
				translationZ = 0.1f;
			} else if (selectionType == SelectionType.MOUSE_OVER) {
				bgColor = Color.WHITE;
				textColor = color;
				translationZ = 0.2f;
			}
			// add a label to the end of the curve
			Vec2f pos = linePoints.get(linePoints.size() - 1).copy();
			float textWidth = g.text.getTextWidth(group.getLabel(), 13);
			g.save();
			g.move(pos.x(), pos.y() - 13);
			g.incZ(translationZ);
			g.color(bgColor).fillRect(0, 0, textWidth + 2, 13 + 2);
			g.textColor(textColor).drawText(group, 1, 1, textWidth, 13).textColor(Color.BLACK);
			g.restore();
		}
		g.color(color);
		g.incZ(translationZ);
		g.drawPath(linePoints, false);
		g.incZ(-translationZ);
	}

	/**
	 * create a curve out of the data by minimizing the point by not sampling the data but compute the curve
	 *
	 * @param data
	 * @param w
	 * @param h
	 * @return
	 */
	private List<Vec2f> createCurve(float[] data, float w, float h) {
		assert data.length != 0;

		List<Vec2f> linePoints = new ArrayList<>();

		float timeFactor = w; // / maxAxisTime; //as normalized in 0...maxtime
		float valueFactor = h / data.length;

		float last = data[0];
		float lastTime = 0;
		int lastIndex = 0;

		for (int i = 1; i < data.length; ++i) {
			float v = data[i];
			if (v == last)
				continue;
			assert v > last; // as sorted
			float time = v * timeFactor;
			linePoints.add(new Vec2f(lastTime, lastIndex * valueFactor));
			linePoints.add(new Vec2f(time, lastIndex * valueFactor)); // as we have a sharp dropoff
			lastTime = time;
			lastIndex = i;
			last = v;
		}
		// final one
		linePoints.add(new Vec2f(lastTime, lastIndex * valueFactor));
		linePoints.add(new Vec2f(w, lastIndex * valueFactor));
		return linePoints;
	}

	@Override
	public Vec2f getMinSize() {
		switch (detailLevel) {
		case HIGH:
			return new Vec2f(400, 400);
		default:
			return new Vec2f(100, 100);
		}
	}

	@Override
	public String toString() {
		return "Kaplan Maier";
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
