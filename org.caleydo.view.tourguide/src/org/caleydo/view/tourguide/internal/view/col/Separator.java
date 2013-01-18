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
package org.caleydo.view.tourguide.internal.view.col;

import java.util.Set;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.util.LineSeparatorRenderer;
import org.caleydo.core.view.opengl.layout.util.PickingRenderer;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.draganddrop.IDraggable;
import org.caleydo.core.view.opengl.util.draganddrop.IDropArea;
import org.caleydo.view.tourguide.internal.view.ScoreQueryUI;

/**
 * @author Samuel Gratzl
 *
 */
public class Separator extends ElementLayout implements IDropArea {

	private final ScoreQueryUI ui;
	private int id;

	public Separator(int id, AGLView view, ScoreQueryUI ui) {
		setRenderer(new LineSeparatorRenderer(true));
		addBackgroundRenderer(new PickingRenderer(ScoreQueryUI.DROP_SEPARATOR, id, view));
		setPixelSizeX(5);
		setGrabY(true);
		this.ui = ui;
		this.id = id;
	}

	@Override
	public void handleDragOver(GL2 gl, Set<IDraggable> draggables, float mouseCoordinateX, float mouseCoordinateY) {
		getRenderer().setLineWidth(3);
	}

	@Override
	public void handleDropAreaReplaced() {
		getRenderer().setLineWidth(1);
	}

	@Override
	public void handleDrop(GL2 gl, Set<IDraggable> draggables, float mouseCoordinateX, float mouseCoordinateY,
			DragAndDropController dragAndDropController) {
		ui.moveColumn((QueryColumn) draggables.iterator().next(), id);
	}

	@Override
	public LineSeparatorRenderer getRenderer() {
		return (LineSeparatorRenderer) super.getRenderer();
	}
}
