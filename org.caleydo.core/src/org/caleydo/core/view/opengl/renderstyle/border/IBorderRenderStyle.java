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

public interface IBorderRenderStyle {

	/*
	 * (non-Javadoc)
	 * @seeorg.caleydo.core.view.opengl.miniview.slider.iBorderRenderStyle# setBorderWidth(int)
	 */
	public abstract void setBorderWidth(final int width);

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.miniview.slider.iBorderRenderStyle#setBorder
	 * (org.caleydo.core.view.opengl.miniview.slider.BorderRenderStyle.BORDER, boolean)
	 */
	public abstract void setBorder(int borderpart, boolean onoff);

	/*
	 * (non-Javadoc)
	 * @seeorg.caleydo.core.view.opengl.miniview.slider.iBorderRenderStyle# setBorderColor(gleem.linalg.Vec4f)
	 */
	public abstract void setBorderColor(Vec4f color);

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.miniview.slider.iBorderRenderStyle#init( javax.media.opengl.GL)
	 */
	public abstract void init(GL2 gl);

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.miniview.slider.iBorderRenderStyle#display (javax.media.opengl.GL)
	 */
	public abstract void display(GL2 gl);

}