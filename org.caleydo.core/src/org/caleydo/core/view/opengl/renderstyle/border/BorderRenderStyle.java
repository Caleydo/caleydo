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
package org.caleydo.core.view.opengl.renderstyle.border;

import gleem.linalg.Vec4f;

import javax.media.opengl.GL2;

public class BorderRenderStyle
	implements IBorderRenderStyle {

	public static final class BORDER {
		public static final int FULL = 0xf;
		public static final int LEFT = 0x1;
		public static final int TOP = 0x2;
		public static final int RIGHT = 0x4;
		public static final int BOTTOM = 0x8;
	}

	protected boolean bBorderLeft = true;
	protected boolean bBorderTop = true;
	protected boolean bBorderRight = true;
	protected boolean bBorderBottom = true;
	protected int iBorderWidth = 1;

	protected Vec4f vBorderColor = new Vec4f(0.0f, 0.0f, 0.0f, 1.0f);

	protected int glList = -1;

	@Override
	public void setBorderWidth(final int width) {
		iBorderWidth = width;
	}

	@Override
	public void setBorder(int borderpart, boolean onoff) {
		if ((borderpart & BORDER.LEFT) == BORDER.LEFT) {
			bBorderLeft = onoff;
		}

		if ((borderpart & BORDER.TOP) == BORDER.TOP) {
			bBorderTop = onoff;
		}

		if ((borderpart & BORDER.RIGHT) == BORDER.RIGHT) {
			bBorderRight = onoff;
		}

		if ((borderpart & BORDER.BOTTOM) == BORDER.BOTTOM) {
			bBorderBottom = onoff;
		}

	}

	@Override
	public void setBorderColor(Vec4f color) {
		vBorderColor = color;
	}

	@Override
	public void init(GL2 gl) {

	}

	@Override
	public void display(GL2 gl) {
		if (glList < 0)
			return;

		gl.glCallList(glList);
	}

}
