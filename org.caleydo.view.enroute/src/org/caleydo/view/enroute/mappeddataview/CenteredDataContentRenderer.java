/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.enroute.mappeddataview;

import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2GL3;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspectiveStatistics;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.collection.Algorithms;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.picking.IPickingLabelProvider;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.enroute.EPickingType;

public class CenteredDataContentRenderer extends ACategoricalRowContentRenderer {

	protected float dataCenter = 0;
	protected float minValue = 0;
	protected float maxValue = 0;

	protected boolean showCenterLineAtRowCenter = false;

	public CenteredDataContentRenderer(IDType rowIDType, Integer rowID, IDType resolvedRowIDType,
			Integer resolvedRowID, ATableBasedDataDomain dataDomain, Perspective columnPerspective, AGLView parentView,
			MappedDataRenderer parent, Group group, boolean isHighlightMode, float dataCenter, float minValue,
			float maxValue) {
		super(rowIDType, rowID, resolvedRowIDType, resolvedRowID, dataDomain, columnPerspective, parentView, parent,
				group, isHighlightMode);
		this.dataCenter = dataCenter;
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	@Override
	public void init() {
		if (resolvedRowID == null)
			return;

		VirtualArray dimensionVirtualArray = new VirtualArray(resolvedRowIDType);
		dimensionVirtualArray.append(resolvedRowID);
		histogram = TablePerspectiveStatistics.calculateHistogram(dataDomain.getTable(),
				columnPerspective.getVirtualArray(), dimensionVirtualArray);

		registerPickingListeners();

	}

	protected float getNormalizedValue(float rawValue) {
		float value = (rawValue - minValue) / (maxValue - minValue);
		if (value > 1)
			return 1;
		if (value < 0)
			return 0;
		return value;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void renderAllBars(GL2 gl, List<SelectionType> geneSelectionTypes) {
		if (resolvedRowID == null)
			return;
		if (x / columnPerspective.getVirtualArray().size() < parentView.getPixelGLConverter()
				.getGLWidthForPixelWidth(3)) {
			useShading = false;
		}
		float xIncrement = x / columnPerspective.getVirtualArray().size();
		int experimentCount = 0;

		// float[] tempTopBarColor = topBarColor;
		// float[] tempBottomBarColor = bottomBarColor;
		float spacing = 0;
		float range = y;
		// dataCenter = 1;
		// showCenterLineAtRowCenter = true;
		if (showCenterLineAtRowCenter) {
			float diffMax = maxValue - dataCenter;
			float diffMin = dataCenter - minValue;
			float totalValueRange = 0;
			if (diffMax >= diffMin) {
				totalValueRange = diffMax * 2.0f;
			} else {
				totalValueRange = diffMin * 2.0f;
			}
			float numberSpacing = diffMax - diffMin;
			if (totalValueRange != 0) {
				spacing = numberSpacing / totalValueRange * y;
			}
			range = y - Math.abs(spacing);
			if (spacing < 0)
				spacing = 0;
		}

		float center = getNormalizedValue(dataCenter);
		// float center = 0.5f;

		gl.glColor3f(0, 0, 0);
		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(0, (center * range) + spacing, z);
		gl.glVertex3f(x, (center * range) + spacing, z);
		gl.glEnd();

		for (Integer columnID : columnPerspective.getVirtualArray()) {

			float value;
			if (rowID != null) {
				// value = dataDomain.getNormalizedValue(resolvedRowIDType, resolvedRowID, resolvedColumnIDType,
				// columnID);
				Object rawValue = dataDomain.getRaw(resolvedRowIDType, resolvedRowID, resolvedColumnIDType, columnID);
				if (rawValue instanceof Integer) {
					value = getNormalizedValue(((Integer) rawValue).floatValue());
				} else if (rawValue instanceof Float) {
					value = getNormalizedValue(((Float) rawValue).floatValue());
				} else if (rawValue instanceof Double) {
					value = getNormalizedValue(((Double) rawValue).floatValue());
				} else {
					throw new IllegalStateException("The value " + rawValue + " is not supported.");
				}
				if (value < center + 0.0001 && value > center - 0.0001) {
					experimentCount++;
					continue;
				}

				List<SelectionType> experimentSelectionTypes = parent.sampleSelectionManager.getSelectionTypes(
						columnIDType, columnID);

				float[] baseColor = dataDomain.getTable().getColorMapper().getColor(value);
				float[] topBarColor = baseColor;
				float[] bottomBarColor = baseColor;

				colorCalculator.calculateColors(Algorithms.mergeListsToUniqueList(experimentSelectionTypes,
						geneSelectionTypes));

				List<SelectionType> selectionTypes = Algorithms.mergeListsToUniqueList(experimentSelectionTypes,
						geneSelectionTypes);

				if (isHighlightMode
						&& !(selectionTypes.contains(SelectionType.MOUSE_OVER) || selectionTypes
								.contains(SelectionType.SELECTION))) {
					experimentCount++;
					continue;
				}

				if (isHighlightMode) {
					colorCalculator.setBaseColor(new Color(baseColor[0], baseColor[1], baseColor[2]));

					colorCalculator.calculateColors(selectionTypes);

					topBarColor = colorCalculator.getPrimaryColor().getRGB();
					bottomBarColor = colorCalculator.getSecondaryColor().getRGB();
				}

				float leftEdge = xIncrement * experimentCount;

				float upperEdge = (value * range) + spacing;

				// gl.glPushName(parentView.getPickingManager().getPickingID(
				// parentView.getID(), PickingType.ROW_PRIMARY.name(), rowID));

				Integer resolvedColumnID = columnIDMappingManager.getID(dataDomain.getPrimaryIDType(columnIDType),
						parent.sampleIDType, columnID);
				gl.glPushName(parentView.getPickingManager().getPickingID(parentView.getID(),
						EPickingType.SAMPLE.name(), resolvedColumnID));
				gl.glPushName(parentView.getPickingManager().getPickingID(parentView.getID(),
						EPickingType.SAMPLE.name() + hashCode(), columnID));

				gl.glColor3fv(bottomBarColor, 0);
				gl.glBegin(GL2GL3.GL_QUADS);

				gl.glVertex3f(leftEdge, (center * range) + spacing, z);
				if (useShading) {
					gl.glColor3f(bottomBarColor[0] * 0.9f, bottomBarColor[1] * 0.9f, bottomBarColor[2] * 0.9f);
				}
				gl.glVertex3f(leftEdge + xIncrement, (center * range) + spacing, z);

				if (useShading) {
					gl.glColor3f(topBarColor[0] * 0.9f, topBarColor[1] * 0.9f, topBarColor[2] * 0.9f);
				} else {
					gl.glColor3fv(topBarColor, 0);
				}
				gl.glVertex3f(leftEdge + xIncrement, upperEdge, z);
				gl.glColor3fv(topBarColor, 0);

				gl.glVertex3f(leftEdge, upperEdge, z);

				gl.glEnd();

				gl.glPopName();
				gl.glPopName();
				// gl.glPopName();
				experimentCount++;
				// topBarColor = tempTopBarColor;
				// bottomBarColor = tempBottomBarColor;
			}

		}
	}

	@Override
	protected void registerPickingListeners() {
		super.registerPickingListeners();
		parentView.addTypePickingTooltipListener(new IPickingLabelProvider() {

			@Override
			public String getLabel(Pick pick) {
				if (dataDomain.getDataSetDescription().getDataDescription().getCategoricalClassDescription() != null) {
					return dataDomain
							.getDataSetDescription()
							.getDataDescription()
							.getCategoricalClassDescription()
							.getCategoryProperty(
									dataDomain.getRaw(resolvedRowIDType, resolvedRowID, resolvedColumnIDType,
											pick.getObjectID())).getCategoryName();
				}

				return ""
						+ dataDomain.getRawAsString(resolvedRowIDType, resolvedRowID, resolvedColumnIDType,
								pick.getObjectID());
			}
		}, EPickingType.SAMPLE.name() + hashCode());
	}

	/**
	 * @param showCenterLineAtRowCenter
	 *            setter, see {@link showCenterLineAtRowCenter}
	 */
	public void setShowCenterLineAtRowCenter(boolean showCenterLineAtRowCenter) {
		this.showCenterLineAtRowCenter = showCenterLineAtRowCenter;
	}
}
