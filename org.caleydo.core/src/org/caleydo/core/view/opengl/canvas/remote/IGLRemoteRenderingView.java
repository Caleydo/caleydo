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
package org.caleydo.core.view.opengl.canvas.remote;

import java.util.List;

import org.caleydo.core.view.IView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.eclipse.swt.widgets.Composite;

/**
 * Interface for accessing views that remotely render other views.
 *
 * @author Marc Streit
 */
public interface IGLRemoteRenderingView extends IView {

	public IGLCanvas getParentGLCanvas();

	public Composite getParentComposite();

	/**
	 * Retrieves all the contained views from a given view. The correct
	 * implementation of this method is essential for the destruction of remote
	 * rendered views.
	 *
	 * @return list of views contained in the given view.
	 */
	public List<AGLView> getRemoteRenderedViews();

	public ViewFrustum getViewFrustum();

	/**
	 * Get the {@link PixelGLConverter} from the parent view, which needs the
	 * top-level's view frustum.
	 *
	 * @return
	 */
	public PixelGLConverter getPixelGLConverter();
}
