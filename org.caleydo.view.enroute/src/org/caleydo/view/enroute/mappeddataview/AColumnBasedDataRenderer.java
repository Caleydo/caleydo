/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.mappeddataview;

import java.util.List;
import java.util.Set;

import javax.media.opengl.GL2;
import javax.media.opengl.GL2GL3;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.id.IIDTypeMapper;

/**
 * @author Christian
 *
 */
public abstract class AColumnBasedDataRenderer extends ADataRenderer {

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
				renderMissingValue(gl, xIncrement, y);
			} else {
				renderColumnBar(gl, columnID, xIncrement, y, selectionTypes, useShading);
			}
			gl.glTranslatef(xIncrement, 0, 0);

		}

		gl.glPopMatrix();

	}

	protected void renderMissingValue(GL2 gl, float x, float y) {
		gl.glBegin(GL2GL3.GL_QUADS);

		gl.glColor4f(1f, 1f, 1f, 1f);
		gl.glVertex3f(0, 0, z);
		gl.glVertex3f(x, 0, z);
		gl.glVertex3f(x, y, z);
		gl.glVertex3f(0, y, z);

		gl.glEnd();
	}

	protected abstract void renderColumnBar(GL2 gl, int columnID, float x, float y, List<SelectionType> selectionTypes,
			boolean useShading);

}
