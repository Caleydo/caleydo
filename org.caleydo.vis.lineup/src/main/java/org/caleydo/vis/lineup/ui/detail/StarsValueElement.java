/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.ui.detail;


import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2GL3;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.format.Formatter;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.vis.lineup.model.IRow;
import org.caleydo.vis.lineup.model.StarsRankColumnModel;
import org.caleydo.vis.lineup.ui.IColumnRenderInfo;
import org.caleydo.vis.lineup.ui.RenderStyle;

import com.jogamp.opengl.util.texture.Texture;

/**
 * @author Samuel Gratzl
 *
 */
public class StarsValueElement extends ValueElement {
	protected final StarsRankColumnModel model;

	public StarsValueElement(StarsRankColumnModel model) {
		this.model = model;
		setVisibility(EVisibility.VISIBLE);
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		final IRow r = getLayoutDataAs(IRow.class, null); // current row
		double v = model.applyPrimitive(r);

		if (Double.isNaN(v) || v <= 0)
			return;

		float v_f = (float) v;

		IColumnRenderInfo info = getRenderInfo();
		if (info.isCollapsed()) {
			// if collapsed use a brightness encoding
			g.color(1 - v_f, 1 - v_f, 1 - v_f, 1);
			g.fillRect(0, 1, w - 2, h - 2);
		} else {
			if (h >= 10) { // is selected, render the value
				float hi = Math.min(16, h);
				float yi = (h - hi) * 0.5f;
				g.move(0, yi);
				renderStars(g, v_f, w, hi, (info.hasFreeSpace() && info.getAlignment() == VAlign.LEFT));
				g.move(0, -yi);
			} else {
				g.color(model.getColor()).fillRect(0, 1, w * v_f, h - 2);
			}
		}
	}

	private void renderStars(GLGraphics g, float v, float w, float h, boolean fullStars) {
		GL2 gl = g.gl;
		float z = g.z();

		g.color(Color.WHITE);

		Texture tex;

		if (fullStars && v < 1) {
			tex = g.getTexture(RenderStyle.ICON_STAR_DISABLED);
			tex.setTexParameteri(gl, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);

			tex.enable(gl);
			tex.bind(gl);

			gl.glBegin(GL2GL3.GL_QUADS);
			gl.glTexCoord2f(-(1 - v) * model.getStars(), 0);
			gl.glVertex3f(w * v, 0, z);
			gl.glTexCoord2f(0, 0);
			gl.glVertex3f(w, 0, z);
			gl.glTexCoord2f(0, 1);
			gl.glVertex3f(w, h, z);
			gl.glTexCoord2f(-(1 - v) * model.getStars(), 1);
			gl.glVertex3f(w * v, h, z);
			gl.glEnd();

			tex.disable(gl);
		}

		tex = g.getTexture(RenderStyle.ICON_STAR);
		tex.setTexParameteri(gl, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);

		tex.enable(gl);
		tex.bind(gl);

		gl.glBegin(GL2GL3.GL_QUADS);
		gl.glTexCoord2f(0, 0);
		gl.glVertex3f(0, 0, z);
		gl.glTexCoord2f(v * model.getStars(), 0);
		gl.glVertex3f(w * v, 0, z);
		gl.glTexCoord2f(v * model.getStars(), 1);
		gl.glVertex3f(w * v, h, z);
		gl.glTexCoord2f(0, 1);
		gl.glVertex3f(0, h, z);
		gl.glEnd();

		tex.disable(gl);
	}

	@Override
	public String getTooltip() {
		final IRow r = getLayoutDataAs(IRow.class, null); // current row
		double v = model.getRaw(r);
		if (Double.isNaN(v) || v < 0)
			return null;
		boolean inferred = model.isValueInferred(r);
		return Formatter.formatNumber(v) + " stars" + (inferred ? "*" : "");
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		if (getVisibility() != EVisibility.PICKABLE)
			return;
		IColumnRenderInfo renderInfo = getRenderInfo();
		if ((renderInfo.hasFreeSpace() && renderInfo.getAlignment() == VAlign.LEFT)) {
			g.fillRect(0, 0, w, h);
		} else {
			final IRow r = getLayoutDataAs(IRow.class, null); // current row
			double v = model.applyPrimitive(r);
			if (!Double.isNaN(v) && v > 0)
				g.fillRect(0, 0, w * (float) v, h);
		}
	}
}
