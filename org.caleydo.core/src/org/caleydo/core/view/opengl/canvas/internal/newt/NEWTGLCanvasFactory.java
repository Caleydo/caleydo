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
package org.caleydo.core.view.opengl.canvas.internal.newt;

import javax.media.opengl.GLCapabilitiesImmutable;

import org.caleydo.core.view.opengl.canvas.internal.swt.ASWTBasedCanvasFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.newt.swt.NewtCanvasSWT;

/**
 * @author Samuel Gratzl
 *
 */
public class NEWTGLCanvasFactory extends ASWTBasedCanvasFactory {

	@Override
	public NEWTGLCanvas create(GLCapabilitiesImmutable caps, Composite parent) {
		GLWindow canvas = GLWindow.create(caps);
		NewtCanvasSWT composite = NewtCanvasSWT.create(parent, SWT.NO_BACKGROUND, canvas);
		return new NEWTGLCanvas(canvas, composite);
	}

}
