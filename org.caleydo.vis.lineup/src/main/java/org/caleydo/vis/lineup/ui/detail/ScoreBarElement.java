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
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.vis.lineup.model.IRow;
import org.caleydo.vis.lineup.model.mixin.IDoubleRankableColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IMappedColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IRankColumnModel;
import org.caleydo.vis.lineup.ui.IColumnRenderInfo;
import org.caleydo.vis.lineup.ui.RenderStyle;

import com.jogamp.opengl.util.texture.Texture;

/**
 * a simple {@link IGLRenderer} for rendering a score bar
 *
 * @author Samuel Gratzl
 *
 */
public class ScoreBarElement extends ValueElement {
	protected final IDoubleRankableColumnMixin model;

	public ScoreBarElement(IDoubleRankableColumnMixin model) {
		this.model = model;
		setVisibility(EVisibility.VISIBLE);
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h, IRow row) {
		double v = model.applyPrimitive(row);
		boolean inferred = model.isValueInferred(row);
		renderValue(g, w, h, row, v, inferred, false, model.getColor(), null);
	}

	@Override
	public String getTooltip() {
		final IRow r = getRow();
		double v = model.applyPrimitive(r);
		if (Double.isNaN(v) || v < 0)
			return null;
		boolean inferred = model.isValueInferred(r);
		return getText(r, model, v, inferred);
	}

	protected void renderValue(GLGraphics g, float w, float h, final IRow r, double v, boolean inferred, boolean align,
			Color color, Color collapseColor) {
		if (Double.isNaN(v) || v <= 0)
			return;
		float v_f = (float) v;
		if (getRenderInfo().isCollapsed()) {
			// if collapsed use a brightness encoding
			if (collapseColor == null)
				g.color(1 - v_f, 1 - v_f, 1 - v_f, 1);
			else {
				float[] rgb = collapseColor.getRGB();
				g.color(rgb[0], rgb[1], rgb[2], v_f);
			}
			g.fillRect(0, 1, w - 2, h - 2);
			if (inferred) {
				g.gl.glLineStipple(4, (short) 0xAAAA);
				g.gl.glEnable(GL2.GL_LINE_STIPPLE);
				g.color(0, 0, 0, .5f).drawRect(1, 2, w - 4, h - 4);
				g.gl.glDisable(GL2.GL_LINE_STIPPLE);
			}
		} else {
			// score bar
			g.color(color);
			if (useHatching(model)) {
				renderHatchedValue(g, 0, 1, w * v_f, h - 2);
			} else
				g.fillRect(0, 1, w * v_f, h - 2);
			if (inferred) {
				g.gl.glLineStipple(1, (short) 0xAAAA);
				g.gl.glEnable(GL2.GL_LINE_STIPPLE);
				g.color(0, 0, 0, .5f).drawRect(0, 1, w * v_f, h - 2);
				g.gl.glDisable(GL2.GL_LINE_STIPPLE);
			} else if (getRenderInfo().getBarOutlineColor() != null) {
				// outline
				g.color(getRenderInfo().getBarOutlineColor()).drawRect(0, 1, w * v_f, h - 2);
			}

			if (model.getTable().getSelectedRow() == r) { // is selected, render the value
				renderText(g, w, h, r, v, inferred);
			}
		}
	}

	private void renderHatchedValue(GLGraphics g, float x, float y, float w, float h) {
		Texture tex;
		GL2 gl = g.gl;
		float z = g.z();

		tex = g.getTexture(RenderStyle.ICON_COMPLEX_MAPPING);
		tex.setTexParameteri(gl, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);

		tex.enable(gl);
		tex.bind(gl);

		float widthPerRepeat = 16;
		float repeated = w / widthPerRepeat;

		gl.glBegin(GL2GL3.GL_QUADS);
		gl.glTexCoord2f(0, 0);
		gl.glVertex3f(x, y, z);
		gl.glTexCoord2f(repeated, 0);
		gl.glVertex3f(x + w, y, z);
		gl.glTexCoord2f(repeated, 1);
		gl.glVertex3f(x + w, y + h, z);
		gl.glTexCoord2f(0, 1);
		gl.glVertex3f(x, y + h, z);
		gl.glEnd();

		tex.disable(gl);
	}

	private boolean useHatching(IDoubleRankableColumnMixin model) {
		return (model instanceof IMappedColumnMixin) && ((IMappedColumnMixin) model).isComplexMapping();
	}

	protected void renderText(GLGraphics g, float w, float h, final IRow r, double v, boolean inferred) {
		String text = getText(r, model, v, inferred);
		float hi = getTextHeight(h);
		renderLabel(g, (h - hi) * 0.5f, w, hi, text, v);
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

	protected static String getText(final IRow r, IRankColumnModel model, double v, boolean inferred) {
		String text;
		if (model instanceof IMappedColumnMixin) {
			text = ((IMappedColumnMixin) model).getRawValue(r);
			text += (inferred ? "*" : "");
			text += " (" + Formatter.formatNumber(v);
			text += (inferred ? "*)" : ")");
		} else {
			text = Formatter.formatNumber(v);
			text += (inferred ? "*" : "");
		}
		return text;
	}

	static float getTextHeight(float h) {
		float hi = Math.min(h * 0.6f, 14);
		return hi;
	}

	protected void renderLabel(GLGraphics g, float y, float w, float h, String text, double v) {
		if (h < 5)
			return;
		float tw = g.text.getTextWidth(text, h);
		boolean hasFreeSpace = getRenderInfo().hasFreeSpace();

		VAlign alignment = getRenderInfo().getAlignment();
		float space = (hasFreeSpace && alignment == VAlign.LEFT) ? w : ((float) v * w) - 2;
		if (tw < space)
			g.drawText(text, 2, y, space, h, VAlign.LEFT);
	}
}
