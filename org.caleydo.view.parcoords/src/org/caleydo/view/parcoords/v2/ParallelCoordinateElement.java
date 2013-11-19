/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.parcoords.v2;

import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;

import java.util.List;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.TablePerspectiveSelectionMixin;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.event.EventListenerManager.DeepScan;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.basic.ScrollingDecorator.IHasMinSize;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout2;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.view.parcoords.EPickingType;
import org.caleydo.view.parcoords.PCRenderStyle;
import org.caleydo.view.parcoords.PCRenderStyle.PolyLineState;
import org.caleydo.view.parcoords.preferences.MyPreferences;

/**
 * @author Samuel Gratzl
 *
 */
public class ParallelCoordinateElement extends GLElementContainer implements IGLLayout2,
		TablePerspectiveSelectionMixin.ITablePerspectiveMixinCallback, IHasMinSize {

	@DeepScan
	protected final TablePerspectiveSelectionMixin selections;
	private int numberOfRandomElements;
	private final EDetailLevel detailLevel;

	/**
	 *
	 */
	public ParallelCoordinateElement(TablePerspective tablePerspective, EDetailLevel detailLevel) {
		this.detailLevel = detailLevel;
		this.selections = new TablePerspectiveSelectionMixin(tablePerspective, this);
		setLayout(this);
		this.numberOfRandomElements = fromLevel(detailLevel);
	}

	/**
	 * @param level
	 * @return
	 */
	private int fromLevel(EDetailLevel level) {
		switch (level) {
		case LOW:
			return 50;
		case MEDIUM:
			return 100;
		case HIGH:
			return MyPreferences.getNumRandomSamplePoint();
		default:
			return 20;
		}
	}

	@Override
	public boolean doLayout(List<? extends IGLLayoutElement> children, float w, float h, IGLLayoutElement parent,
			int deltaTimeMs) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T> T getLayoutDataAs(Class<T> clazz, T default_) {
		if (clazz.isAssignableFrom(Vec2f.class))
			return clazz.cast(getMinSize());
		if (clazz.isInstance(getTablePerspective()))
			return clazz.cast(getTablePerspective());
		if (clazz.isInstance(getDataDomain()))
			return clazz.cast(getDataDomain());
		return super.getLayoutDataAs(clazz, default_);
	}

	public final TablePerspective getTablePerspective() {
		return selections.getTablePerspective();
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

	@Override
	public Vec2f getMinSize() {
		final int dims = getTablePerspective().getDimensionPerspective().getVirtualArray().size();
		switch (detailLevel) {
		case HIGH:
			return new Vec2f(dims * 10, 400);
		case MEDIUM:
			return new Vec2f(dims * 5, 200);
		default:
			return new Vec2f(dims * 5, 50);
		}
	}

	private static float getAngle(final Vec3f vecOne, final Vec3f vecTwo) {
		Vec3f vecNewOne = vecOne.copy();
		Vec3f vecNewTwo = vecTwo.copy();

		vecNewOne.normalize();
		vecNewTwo.normalize();
		float fTmp = vecNewOne.dot(vecNewTwo);
		return (float) Math.acos(fTmp);
	}

	private void renderPolylines(GLGraphics g, SelectionType selectionType) {
		TablePerspective t = getTablePerspective();
		SelectionManager recordSelectionManager = selections.getRecordSelectionManager();

		int nrRecords = t.getNrRecords();
		int nrVisibleLines = nrRecords - recordSelectionManager.getNumberOfElements(SelectionType.DESELECTED);

		int displayEveryNthPolyline = (nrRecords - recordSelectionManager.getNumberOfElements(SelectionType.DESELECTED))
				/ numberOfRandomElements;

		if (displayEveryNthPolyline <= 0) {
			displayEveryNthPolyline = 1;
		}

		PolyLineState renderState = renderStyle.getPolyLineState(selectionType, nrVisibleLines
				/ displayEveryNthPolyline);

		// this loop executes once per polyline
		VirtualArray va = t.getRecordPerspective().getVirtualArray();
		for (int i = 0; i < nrRecords; i += displayEveryNthPolyline) {
			int recordID = va.get(i);
			if (!recordSelectionManager.checkStatus(SelectionType.DESELECTED, recordID))
				renderSingleLine(g, recordID, selectionType, renderState, false);
		}
	}

	private void renderSingleLine(GLGraphics g, Integer recordID, SelectionType selectionType,
			PolyLineState renderState, boolean renderCaption) {
		g.color(renderState.color);
		g.lineWidth(renderState.lineWidth);

		float previousX = 0;
		float previousY = 0;
		float currentX = 0;
		float currentY = 0;

		if (selectionType != SelectionType.DESELECTED && g.isPickingPass()) {
			gl.glPushName(pickingManager.getPickingID(uniqueID, EPickingType.POLYLINE_SELECTION.name(), recordID));
		}

		if (!renderCaption) {
			gl.glBegin(GL.GL_LINE_STRIP);
		}

		VirtualArray dimensionVA = tablePerspective.getDimensionPerspective().getVirtualArray();
		Table table = dataDomain.getTable();

		// this loop executes once per axis
		for (int dimensionCount = 0; dimensionCount < tablePerspective.getNrDimensions(); dimensionCount++) {

			Integer dimensionID = dimensionVA.get(dimensionCount);

			currentX = axisSpacings.get(dimensionCount);
			currentY = table.getNormalizedValue(dimensionID, recordID);
			if (Float.isNaN(currentY)) {
				currentY = -pixelGLConverter.getGLHeightForPixelHeight(PCRenderStyle.NAN_Y_OFFSET);
			}
			if (dimensionCount != 0) {
				if (renderCaption) {
					gl.glBegin(GL.GL_LINES);
				}

				gl.glVertex3f(previousX, previousY * renderStyle.getAxisHeight(), renderState.zDepth);
				gl.glVertex3f(currentX, currentY * renderStyle.getAxisHeight(), renderState.zDepth);

				if (renderCaption) {
					gl.glEnd();
				}

			}

			if (renderCaption) {
				String rawValueString = table.getRawAsString(dimensionID, recordID);
				renderBoxedYValues(gl, currentX, currentY * renderStyle.getAxisHeight(), rawValueString, selectionType);
			}

			previousX = currentX;
			previousY = currentY;
		}

		if (!renderCaption) {
			gl.glEnd();
		}

		if (selectionType != SelectionType.DESELECTED) {
			gl.glPopName();
		}
	}
}
