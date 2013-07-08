/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 *
 */
package org.caleydo.view.filterpipeline.representation;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.data.filter.RecordMetaOrFilter;
import org.caleydo.core.view.opengl.picking.PickingManager;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.core.view.opengl.util.AGLGUIElement;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.view.filterpipeline.FilterItem;
import org.caleydo.view.filterpipeline.renderstyle.FilterPipelineRenderStyle;

/**
 * @author Thomas Geymayer
 *
 */
public class FilterMenu extends AGLGUIElement implements IRenderable {
	private FilterItem filter = null;
	private int mouseOverItem = -1;

	private FilterPipelineRenderStyle renderStyle;
	private PickingManager pickingManager;
	private int viewId;

	/**
	 *
	 */
	public FilterMenu(FilterPipelineRenderStyle renderStyle,
			PickingManager pickingManager, int viewId) {
		super();
		minSize = 10;

		this.renderStyle = renderStyle;
		this.pickingManager = pickingManager;
		this.viewId = viewId;
	}

	/**
	 *
	 * @param filter
	 */
	public void setFilter(FilterItem filter) {
		this.filter = filter;
	}

	@Override
	public void render(GL2 gl, CaleydoTextRenderer textRenderer) {
		if (filter == null)
			return;

		int numFilters = 1;

		if (filter.getFilter() instanceof RecordMetaOrFilter) {
			numFilters = ((RecordMetaOrFilter) filter.getFilter()).getFilterList().size();
		}

		float x = filter.getRepresentation().getPosition().x(), y = filter
				.getRepresentation().getPosition().y(), width = 0.3f, height = (numFilters + 1) * 0.3f;

		beginGUIElement(gl, new Vec3f());
		gl.glPushName(filter.getPickingID());
		gl.glBegin(GL2.GL_QUADS);
		{
			gl.glColor3f(0.7f, 0.8f, 0.9f);
			gl.glVertex3d(x - width, y, 0.85);
			gl.glVertex3d(x - width, y + height, 0.85);
			gl.glVertex3d(x, y + height, 0.85);
			gl.glVertex3d(x, y, 0.85);
		}
		gl.glEnd();
		gl.glPopName();

		// render filter buttons
		for (int i = 0; i < numFilters; ++i) {
			gl.glPushName(pickingManager.getPickingID(viewId,
					PickingType.FILTERPIPE_SUB_FILTER, i));
			gl.glBegin(GL2.GL_QUADS);
			{
				gl.glColor4fv(renderStyle.getFilterColor(i).getRGBA(), 0);

				gl.glVertex3d(x - 0.9f * width, y + (i + 1.1f) * 0.3f, 0.9);
				gl.glVertex3d(x - 0.9f * width, y + (i + 1.9f) * 0.3f, 0.9);
				gl.glVertex3d(x - 0.1f * width, y + (i + 1.9f) * 0.3f, 0.9);
				gl.glVertex3d(x - 0.1f * width, y + (i + 1.1f) * 0.3f, 0.9);
			}
			gl.glEnd();
			gl.glPopName();
		}

		if (mouseOverItem >= 0) {
			gl.glBegin(GL.GL_LINE_LOOP);
			{
				gl.glColor3f(0.5f, 0.2f, 0.9f);
				gl.glLineWidth(2);

				gl.glVertex3d(x - 0.9f * width, y + (mouseOverItem + 1.1f) * 0.3f, 0.95);
				gl.glVertex3d(x - 0.9f * width, y + (mouseOverItem + 1.9f) * 0.3f, 0.95);
				gl.glVertex3d(x - 0.1f * width, y + (mouseOverItem + 1.9f) * 0.3f, 0.95);
				gl.glVertex3d(x - 0.1f * width, y + (mouseOverItem + 1.1f) * 0.3f, 0.95);
			}
			gl.glEnd();
		}

		endGUIElement(gl);
	}

	public void handleIconMouseOver(int externalID) {
		mouseOverItem = externalID;

		if (filter != null)
			filter.handleIconMouseOver(externalID);
	}

	public void handleClearMouseOver() {
		mouseOverItem = -1;

		if (filter != null)
			filter.handleClearMouseOver();
	}

}
