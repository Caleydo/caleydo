/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.mappeddataview;

import java.util.List;
import java.util.Set;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2GL3;

import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.data.collection.column.container.CategoryProperty;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.data.DataSetSelectedEvent;
import org.caleydo.core.id.IIDTypeMapper;
import org.caleydo.core.io.DataDescription;
import org.caleydo.core.io.NumericalProperties;
import org.caleydo.core.util.collection.Algorithms;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.IPickingLabelProvider;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.enroute.EPickingType;
import org.caleydo.view.enroute.correlation.SimpleCategory;

/**
 * @author Christian
 *
 */
public abstract class AColumnBasedDataRenderer extends ADataRenderer {

	private static final Color MISSING_VALUE_COLOR = new Color(1, 1, 1, 0.3f);

	/**
	 * @param contentRenderer
	 */
	public AColumnBasedDataRenderer(ContentRenderer contentRenderer) {
		super(contentRenderer);
	}

	@Override
	public void render(GL2 gl, float x, float y, List<SelectionType> selectionTypes) {
		if (contentRenderer.resolvedRowID == null)
			return;
		VirtualArray va = contentRenderer.foreignColumnPerspective != null ? contentRenderer.foreignColumnPerspective
				.getVirtualArray() : contentRenderer.columnPerspective.getVirtualArray();
		float xIncrement = x / va.size();
		boolean useShading = true;
		if (xIncrement < contentRenderer.parentView.getPixelGLConverter().getGLWidthForPixelWidth(3)) {
			useShading = false;
		}

		gl.glPushMatrix();

		for (Integer id : va) {
			Integer columnID = id;

			if (contentRenderer.foreignColumnPerspective != null) {
				IIDTypeMapper<Integer, Integer> mapper = contentRenderer.columnIDMappingManager.getIDTypeMapper(
						contentRenderer.foreignColumnPerspective.getIdType(), contentRenderer.resolvedColumnIDType);
				Set<Integer> localVAIDS = mapper.apply(id);
				columnID = null;
				if (localVAIDS != null) {
					for (Integer localVAID : localVAIDS) {
						columnID = localVAID;
						break;
					}
				}
			}
			if (columnID == null) {
				renderColorColumn(gl, MISSING_VALUE_COLOR, xIncrement, y);
				renderMissingValue(gl, xIncrement, y);
			} else {
				renderColumnBar(gl, columnID, xIncrement, y, selectionTypes, useShading);

				if (contentRenderer.isShowDataClassification()) {
					Object rawValue = contentRenderer.dataDomain.getRaw(contentRenderer.resolvedRowIDType,
							contentRenderer.resolvedRowID, contentRenderer.resolvedColumnIDType, columnID);
					SimpleCategory category = contentRenderer.dataClassifier.apply(rawValue);
					if (category != null) {
						renderColorColumn(gl, new Color(category.color.r, category.color.g, category.color.b, 0.6f),
								xIncrement, y);
					}
				}
			}
			gl.glTranslatef(xIncrement, 0, 0);

		}

		gl.glPopMatrix();

	}

	protected void renderColorColumn(GL2 gl, Color color, float x, float y) {
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glBegin(GL2GL3.GL_QUADS);

		gl.glColor4fv(color.getRGBA(), 0);
		gl.glVertex3f(0, 0, z);
		gl.glVertex3f(x, 0, z);
		gl.glVertex3f(x, y, z);
		gl.glVertex3f(0, y, z);

		gl.glEnd();
	}

	protected void renderMissingValue(GL2 gl, float x, float y) {
		renderColorColumn(gl, MISSING_VALUE_COLOR, x, y);
	}

	protected abstract void renderColumnBar(GL2 gl, int columnID, float x, float y, List<SelectionType> selectionTypes,
			boolean useShading);

	protected void renderSingleBar(GL2 gl, float x, float y, float height, float width,
			List<SelectionType> selectionTypes, float[] baseColor, int columnID, boolean useShading) {
		float[] topBarColor = baseColor;
		float[] bottomBarColor = baseColor;

		List<SelectionType> experimentSelectionTypes = contentRenderer.parent.sampleSelectionManager.getSelectionTypes(
				contentRenderer.columnIDType, columnID);

		@SuppressWarnings("unchecked")
		List<SelectionType> sTypes = Algorithms.mergeListsToUniqueList(experimentSelectionTypes, selectionTypes);

		if (contentRenderer.isHighlightMode
				&& !(sTypes.contains(SelectionType.MOUSE_OVER) || sTypes.contains(SelectionType.SELECTION))) {
			return;
		}

		if (contentRenderer.isHighlightMode) {
			colorCalculator.setBaseColor(new Color(baseColor));

			colorCalculator.calculateColors(sTypes);

			topBarColor = colorCalculator.getPrimaryColor().getRGBA();
			bottomBarColor = colorCalculator.getSecondaryColor().getRGBA();
		}

		Integer resolvedSampleID = contentRenderer.columnIDMappingManager.getID(contentRenderer.resolvedColumnIDType,
				contentRenderer.parent.sampleIDType, columnID);

		gl.glPushName(contentRenderer.parentView.getPickingManager().getPickingID(contentRenderer.parentView.getID(),
				EPickingType.SAMPLE.name(), resolvedSampleID));
		gl.glPushName(contentRenderer.parentView.getPickingManager().getPickingID(contentRenderer.parentView.getID(),
				EPickingType.SAMPLE.name() + hashCode(), columnID));

		gl.glBegin(GL2GL3.GL_QUADS);

		gl.glColor4fv(bottomBarColor, 0);
		gl.glVertex3f(x, y, z);
		if (useShading) {
			gl.glColor3f(bottomBarColor[0] * 0.9f, bottomBarColor[1] * 0.9f, bottomBarColor[2] * 0.9f);

		}
		gl.glVertex3f(x + width, y, z);
		if (useShading) {
			gl.glColor3f(topBarColor[0] * 0.9f, topBarColor[1] * 0.9f, topBarColor[2] * 0.9f);
		} else {
			gl.glColor4fv(topBarColor, 0);
		}

		gl.glVertex3f(x + width, y + height, z);
		gl.glColor4fv(topBarColor, 0);

		gl.glVertex3f(x, y + height, z);

		gl.glEnd();

		gl.glPopName();
		gl.glPopName();
	}

	protected float[] getMappingColorForItem(int columnID) {
		DataDescription dataDescription = contentRenderer.dataDomain.getDataSetDescription().getDataDescription();
		// CategoricalClassDescription<?> categoryDescription = null;

		// inhomogeneous
		if (dataDescription == null) {
			Object dataClassDesc = null;
			if (contentRenderer.columnIDType.getIDCategory() == contentRenderer.dataDomain.getColumnIDCategory()) {
				dataClassDesc = contentRenderer.dataDomain.getTable().getDataClassSpecificDescription(columnID,
						contentRenderer.rowID);
			} else {
				dataClassDesc = contentRenderer.dataDomain.getTable().getDataClassSpecificDescription(
						contentRenderer.rowID, columnID);
			}

			if (dataClassDesc == null || dataClassDesc instanceof NumericalProperties) {
				return getBarColorFromNumericValue(columnID);
			} else {
				return getBarColorFromCategory((CategoricalClassDescription<?>) dataClassDesc, columnID);
			}
		} else if (dataDescription.getNumericalProperties() != null) {
			return getBarColorFromNumericValue(columnID);
		} else {
			return getBarColorFromCategory(dataDescription.getCategoricalClassDescription(), columnID);
		}

	}

	protected float[] getBarColorFromCategory(CategoricalClassDescription<?> categoryDescription, int columnID) {
		CategoryProperty<?> property = categoryDescription.getCategoryProperty(contentRenderer.dataDomain.getRaw(
				contentRenderer.resolvedColumnIDType, columnID, contentRenderer.resolvedRowIDType,
				contentRenderer.resolvedRowID));
		if (property == null)
			return new float[] { 1, 1, 1, 0.3f };
		return property.getColor().getRGBA();
	}

	protected float[] getBarColorFromNumericValue(int columnID) {
		Float value = contentRenderer.dataDomain.getNormalizedValue(contentRenderer.resolvedRowIDType,
				contentRenderer.resolvedRowID, contentRenderer.resolvedColumnIDType, columnID);

		float[] mappedColor = contentRenderer.dataDomain.getTable().getColorMapper().getColor(value);
		// if (mappedColor[0] < 0 || mappedColor[0] > 1 || mappedColor[1] < 0 || mappedColor[1] > 1 || mappedColor[2] <
		// 0
		// || mappedColor[2] > 1) {
		// int x = 5;
		// x++;
		// }
		return new float[] { mappedColor[0], mappedColor[1], mappedColor[2], 1f };
		// return new float[] { 0, 0, 0, 1f };
	}

	protected void registerPickingListeners() {
		contentRenderer.parent.pickingListenerManager.addTypePickingListener(new APickingListener() {
			@Override
			protected void clicked(Pick pick) {
				EventPublisher.trigger(new DataSetSelectedEvent(contentRenderer.dataDomain));
			}
		}, EPickingType.SAMPLE.name() + hashCode());

		contentRenderer.parent.pickingListenerManager.addTypePickingTooltipListener(new IPickingLabelProvider() {

			@Override
			public String getLabel(Pick pick) {
				DataDescription dataDescription = contentRenderer.dataDomain.getDataSetDescription()
						.getDataDescription();
				if (dataDescription != null && dataDescription.getCategoricalClassDescription() != null) {
					return getCategoryName(contentRenderer.dataDomain.getDataSetDescription().getDataDescription()
							.getCategoricalClassDescription(), pick.getObjectID());
				}
				return ""
						+ contentRenderer.dataDomain.getRawAsString(contentRenderer.resolvedRowIDType,
								contentRenderer.resolvedRowID, contentRenderer.resolvedColumnIDType, pick.getObjectID());
			}

			private String getCategoryName(CategoricalClassDescription<?> categoryDescription, int columnID) {
				return categoryDescription.getCategoryProperty(
						contentRenderer.dataDomain.getRaw(contentRenderer.resolvedRowIDType,
								contentRenderer.resolvedRowID, contentRenderer.resolvedColumnIDType, columnID))
						.getCategoryName();
			}

		}, EPickingType.SAMPLE.name() + hashCode());
	}
}
