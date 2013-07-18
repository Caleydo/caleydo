/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.color;

import javax.media.opengl.GL2;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * a color with advanced options, e.g. line options
 *
 * @author Samuel Gratzl
 *
 */
@XmlType
public class StyledColor extends Color {
	@XmlAttribute
	private int lineWidth = -1;
	@XmlAttribute
	private short dashing = 0;
	@XmlAttribute
	private int dashFactor = 0;


	public StyledColor() {
		super();
	}

	public StyledColor(float r, float g, float b) {
		super(r, g, b);
	}

	public StyledColor(int r, int g, int b) {
		super(r, g, b);
	}

	public StyledColor(Color base) {
		super(base.r, base.g, base.b, base.r);
	}

	public void set(GL2 gl) {
		set(gl, false);
	}

	public void setClearManually(GL2 gl) {
		set(gl, true);
	}

	private void set(GL2 gl, boolean externalClearing) {
		gl.glColor4f(r, g, b, a);
		if (!externalClearing && (lineWidth > 0 || dashFactor > 0))
			gl.glPushAttrib(GL2.GL_LINE_BIT);
		if (lineWidth > 0)
			gl.glLineWidth(lineWidth);
		if (dashFactor > 0) {
			gl.glLineStipple(dashFactor, dashing);
			gl.glEnable(GL2.GL_LINE_STIPPLE);
		}
	}

	public void clear(GL2 gl) {
		if (lineWidth > 0 || dashFactor > 0)
			gl.glPopAttrib();
	}

	public StyledColor setLineWidth(int lineWidth) {
		this.lineWidth = lineWidth;
		return this;
	}

	public StyledColor setDashing(int dashFactor, int dashing) {
		this.dashFactor = dashFactor;
		this.dashing = (short) dashing;
		return this;
	}
}