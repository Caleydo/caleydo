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
package org.caleydo.view.pathway;

import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.IRemoteRendererCreator;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;

/**
 * @author Christian
 *
 */
public class PathwayTextureCreator implements IRemoteRendererCreator {

	@Override
	public ALayoutRenderer createRemoteView(AGLView remoteRenderingView, List<TablePerspective> tablePerspectives,
			String embeddingEventSpace) {
		// TODO: create real pathway texture
		return new ALayoutRenderer() {

			@Override
			protected void renderContent(GL2 gl) {
				gl.glColor4f(1, 0, 0, 1);
				gl.glBegin(GL2.GL_QUADS);
				gl.glVertex2f(0, 0);
				gl.glVertex2f(x, 0);
				gl.glVertex2f(x, y);
				gl.glVertex2f(0, y);
				gl.glEnd();
			}

			@Override
			protected boolean permitsWrappingDisplayLists() {
				return true;
			}

			@Override
			public int getMinHeightPixels() {
				return 100;
			}

			@Override
			public int getMinWidthPixels() {
				return 100;
			}
		};
	}
}
