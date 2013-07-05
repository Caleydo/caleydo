/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.vis.rank.ui.detail;


import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.format.Formatter;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.model.StarsRankColumnModel;
import org.caleydo.vis.rank.ui.IColumnRenderInfo;
import org.caleydo.vis.rank.ui.RenderStyle;

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
		float v = model.applyPrimitive(r);

		if (Float.isNaN(v) || v <= 0)
			return;

		IColumnRenderInfo info = getRenderInfo();
		if (info.isCollapsed()) {
			// if collapsed use a brightness encoding
			g.color(1 - v, 1 - v, 1 - v, 1);
			g.fillRect(0, 1, w - 2, h - 2);
		} else {
			if (h >= 10) { // is selected, render the value
				float hi = Math.min(16, h);
				float yi = (h - hi) * 0.5f;
				g.move(0, yi);
				renderStars(g, v, w, hi, (info.hasFreeSpace() && info.getAlignment() == VAlign.LEFT));
				g.move(0, -yi);
			} else {
				g.color(model.getColor()).fillRect(0, 1, w * v, h - 2);
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

			gl.glBegin(GL2.GL_QUADS);
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

		gl.glBegin(GL2.GL_QUADS);
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
		float v = model.getRaw(r);
		if (Float.isNaN(v) || v < 0)
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
			float v = model.applyPrimitive(r);
			if (!Float.isNaN(v) && v > 0)
				g.fillRect(0, 0, w * v, h);
		}
	}
}
