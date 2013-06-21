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
package org.caleydo.core.view.opengl.layout2.basic;


import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.fixedfunc.GLPointerFunc;

import org.caleydo.core.util.base.ICallback;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.core.view.opengl.picking.AdvancedPick;
import org.caleydo.core.view.opengl.picking.Pick;

import com.jogamp.common.nio.Buffers;

/**
 * renders waiting icon with optional cancel button and progress text
 *
 * @author Samuel Gratzl
 *
 */
public class WaitingElement extends AGLButton {
    private static final FloatBuffer vertices;
    private static final FloatBuffer colors;

    static {
    	final int numberOfPoints = 12;
		final int numberOfVisiblePoints = 10;
		final float[] color = Color.BLACK.getRGB();
		float[] verts = new float[numberOfVisiblePoints * 2];
		float[] cols = new float[numberOfVisiblePoints * 4];

		double deltaAngle = -2 * Math.PI / numberOfPoints;
		float deltaOpacity = 1.f / numberOfVisiblePoints;
		for (int i = 0; i < numberOfVisiblePoints; ++i) {
			cols[i * 4 + 0] = color[0];
			cols[i * 4 + 1] = color[1];
			cols[i * 4 + 2] = color[2];
			cols[i * 4 + 3] = (float) Math.pow(1 - deltaOpacity * i, 3);

			verts[i * 2 + 0] = (float) Math.cos(deltaAngle * i);
			verts[i * 2 + 1] = (float) Math.sin(deltaAngle * i);
		}

		vertices = Buffers.newDirectFloatBuffer(verts);
		colors = Buffers.newDirectFloatBuffer(cols);
    }

	private float acc = 0;
	private float completed = 0.f;
	private String text = "";
	private ICallback<Boolean> onCancel = null;

    @Override
    protected void renderImpl(GLGraphics g, float w, float h) {
        GL2 gl = g.gl;

        gl.glPushAttrib(GL2.GL_POINT_BIT);
        gl.glPushClientAttrib(GL2.GL_CLIENT_VERTEX_ARRAY_BIT);
		gl.glEnable(GL2ES1.GL_POINT_SMOOTH);

        g.save();
        gl.glTranslatef(w / 2, h / 2,g.z());
        gl.glRotatef(computeAngle(g.getDeltaTimeMs()), 0, 0, 1);

        final float r = Math.min(w, h) / 4;
		gl.glScalef(r, r, 1);
		gl.glPointSize(Math.round(r * 0.25f));

        gl.glEnableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GLPointerFunc.GL_COLOR_ARRAY);

        gl.glVertexPointer(2, GL.GL_FLOAT, 0, vertices);
        gl.glColorPointer(4, GL.GL_FLOAT, 0, colors);

        gl.glDrawArrays(GL.GL_POINTS, 0, vertices.capacity() / 2);

		g.restore();
        gl.glPopClientAttrib();
        gl.glPopAttrib();

		if (completed < 0)
			g.textColor(Color.RED);
		g.drawText(String.format("%.0f%%", Math.abs(completed) * 100), 0, h * 0.5f - 16, w, 16, VAlign.CENTER);
		if (text != null && !text.isEmpty()) {
			g.drawText(text, 0, h * 0.5f + 2, w, 10, VAlign.CENTER);
		}

		if (completed < 0)
			g.textColor(Color.BLACK);

		if (onCancel != null) {
			float wi = g.text.getTextWidth("Cancel", 12);
			g.drawText("Cancel", 0, h * 0.5f + r + 10, w, 12, VAlign.CENTER);
			if (hovered)
				g.withMove((w - wi - 10) * 0.5f, h * 0.5f + r + 10, hoverEffect, wi + 10, 16, this);
			if (armed)
				g.withMove((w - wi - 10) * 0.5f, h * 0.5f + r + 10, armedEffect, wi + 10, 16, this);
		}

		repaint(); // for the animation
    }

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		final float r = Math.min(w, h) / 4;
		float wi = g.text.getTextWidth("Cancel", 12);
		g.fillRect((w - wi) * 0.5f, h * 0.5f + r + 10, wi, 12);
	}

	/**
	 * reset this waiting element and set a new (optional) cancel callback
	 *
	 * @param onCancel
	 */
	public void reset(ICallback<Boolean> onCancel) {
		completed = 0;
		text = "";
		this.onCancel = onCancel;
		setVisibility(onCancel != null ? EVisibility.PICKABLE : EVisibility.VISIBLE);
	}

    private float computeAngle(int deltaTimeMs) {
		acc += deltaTimeMs * 0.2f;
        acc %= 360;
		return acc;
    }

	/**
	 * updates the progress completion state with an optional text
	 *
	 * @param completed
	 * @param text
	 */
	public void progress(float completed, String text) {
		this.completed = completed;
		this.text = text;
		repaint();
	}

	/**
	 * marks that there is no progress anymore and that an error occurred described using the parameter
	 *
	 * @param text
	 */
	public void error(String text) {
		this.completed *= -1;
		this.text = text;
		repaint();
	}

	@Override
	protected void onMouseOver(Pick pick) {
		if (pick.isAnyDragging())
			return;
		hovered = true;
		repaint();
	}

	@Override
	protected void onMouseOut(Pick pick) {
		if (!hovered)
			return;
		hovered = false;
		repaint();
	}

	@Override
	protected void onClicked(Pick pick) {
		if (pick.isAnyDragging())
			return;
		armed = true;
		if (!(pick instanceof AdvancedPick)) {
			if (onCancel != null) onCancel.on(Boolean.TRUE);
			armed = false;
		}
		repaint();
	}

	@Override
	protected void onMouseReleased(Pick pick) {
		if (!armed)
			return;
		armed = false;
		if (onCancel != null) onCancel.on(Boolean.TRUE);
		repaint();
	}

	public static void main(String[] args) {
		GLSandBox.main(args, new WaitingElement());
	}

}

