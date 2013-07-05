/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.bookmark;

import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.id.IDType;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.ILayoutedElement;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;

/**
 * Abstract base class for a single bookmark
 *
 * @author Alexander Lex
 */
public abstract class ABookmark extends ALayoutRenderer implements ILayoutedElement {

	protected IDType idType;
	protected int id;

	protected CaleydoTextRenderer textRenderer;

	protected GLBookmarkView manager;

	protected ABookmarkContainer parentContainer;

	protected final static int Y_SPACING_PIXEL = 4;
	protected final static int X_SPACING_PIXEL = 5;

	/** spacing for text in y direction */
	protected float ySpacing;

	/** spacing for text in x direction */
	protected float xSpacing;

	protected PixelGLConverter pixelGLConverter;

	/**
	 * The constructor takes a TextRenderer which is used to render all text
	 *
	 * @param textRenderer
	 */
	public ABookmark(GLBookmarkView manager, ABookmarkContainer parentContainer,
			IDType idType, CaleydoTextRenderer textRenderer) {
		this.textRenderer = textRenderer;
		this.manager = manager;
		this.idType = idType;
		this.parentContainer = parentContainer;

		pixelGLConverter = manager.getPixelGLConverter();

	}

	@Override
	public void renderContent(GL2 gl) {

		// this needs to be done only when the frustum has changed, but that's
		// difficult to determine here
		ySpacing = pixelGLConverter.getGLHeightForPixelHeight(Y_SPACING_PIXEL);
		xSpacing = pixelGLConverter.getGLWidthForPixelWidth(X_SPACING_PIXEL);
		float[] highlightColor = null;

		List<SelectionType> selectionTypes = parentContainer.selectionManager
				.getSelectionTypes(this.id);

		if (selectionTypes == null)
			return;

		SelectionType topLevelType = null;
		for (SelectionType selectionType : selectionTypes) {
			if (!selectionType.isVisible())
				continue;
			if (selectionType == SelectionType.NORMAL)
				continue;
			if (topLevelType == null)
				topLevelType = selectionType;
			else if (topLevelType.getPriority() < selectionType.getPriority())
				topLevelType = selectionType;
		}
		if (topLevelType == null)
			return;

		highlightColor = topLevelType.getColor().getRGBA();

		ElementLayout layout = getLayout();
		if (highlightColor != null) {

			float xOrigin = 0;
			float yOrigin = 0;
			// float width =
			float width = layout.getSizeScaledX();
			float height = layout.getSizeScaledY();
			gl.glLineWidth(1);
			gl.glColor3fv(highlightColor, 0);
			gl.glBegin(GL.GL_LINE_LOOP);
			gl.glVertex3f(xOrigin, yOrigin, 0);
			gl.glVertex3f(xOrigin + width, yOrigin, 0);
			gl.glVertex3f(xOrigin + width, yOrigin + height, 0);
			gl.glVertex3f(xOrigin, yOrigin + height, 0);
			gl.glEnd();
			// GLHelperFunctions.drawPointAt(gl, width, height, 0);
		}

		// gl.glPopName();
	}

	public int getID() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj.hashCode() == hashCode())
			return true;

		return false;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public String toString() {
		return Integer.toString(id);
	}

}
