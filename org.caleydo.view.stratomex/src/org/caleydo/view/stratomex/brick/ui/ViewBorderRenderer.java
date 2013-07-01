/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.stratomex.brick.ui;

import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.util.APickableLayoutRenderer;

/**
 * Renders top and bottom border for a view.
 * 
 * @author Alexander Lex
 */
public class ViewBorderRenderer extends APickableLayoutRenderer {

	public final static Color DEFAULT_COLOR = Color.GRAY;

	private Color color = DEFAULT_COLOR;
	private boolean renderSides = false;

	public ViewBorderRenderer() {
		super();
	}

	public ViewBorderRenderer(AGLView view, String pickingType, int id) {
		super(view, pickingType, id);
	}

	public ViewBorderRenderer(AGLView view, List<Pair<String, Integer>> pickingIDs) {
		super(view, pickingIDs);
	}

	/**
	 * @param color
	 *            Color with rgba values.
	 */
	public void setColor(Color color) {
		this.color = color;
		setDisplayListDirty(true);
	}

	public Color getColor() {
		return color;
	}

	/**
	 * @param renderSides
	 *            setter, see {@link renderSides}
	 */
	public void setRenderSides(boolean renderSides) {
		this.renderSides = renderSides;
	}

	@Override
	protected void renderContent(GL2 gl) {
		gl.glColor3fv(color.darker().getRGB(), 0);
		pushNames(gl);

		gl.glPushAttrib(GL2.GL_LINE_BIT);
		gl.glLineWidth(1);

		// float tickWidth = layoutManager.getPixelGLConverter().getGLWidthForPixelWidth(10);
		if (!renderSides) {

			gl.glBegin(GL.GL_LINES);

			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(x, 0, 0);
			gl.glEnd();

			gl.glBegin(GL.GL_LINES);
			// gl.glVertex3f(0, 0, 0);
			// gl.glVertex3f(0, y, 0);
			gl.glVertex3f(0, y, 0);
			gl.glVertex3f(x, y, 0);
			// gl.glVertex3f(x, 0, 0);
			// gl.glVertex3f(x, y, 0);

			gl.glEnd();
		} else {
			gl.glBegin(GL.GL_LINES);

			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(x, 0, 0);

			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(0, y, 0);
			gl.glVertex3f(0, y, 0);
			gl.glVertex3f(x, y, 0);
			gl.glVertex3f(x, 0, 0);
			gl.glVertex3f(x, y, 0);

			gl.glEnd();
		}

		gl.glPopAttrib();
		popNames(gl);
	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return false;
	}

}
