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
package org.caleydo.core.view.opengl.layout2;

import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;

/**
 * a special Element that is pickable by default and provides similar methods as {@link APickingListener}
 *
 * @author Samuel Gratzl
 *
 */
public class PickableGLElement extends GLElement {

	public PickableGLElement() {
		this.setVisibility(EVisibility.PICKABLE);
		this.onPick(new IPickingListener() {
			@Override
			public void pick(Pick pick) {
				onPicked(pick);
			}
		});
	}

	protected void onPicked(Pick pick) {
		switch (pick.getPickingMode()) {
		case CLICKED:
			onClicked(pick);
			break;
		case DOUBLE_CLICKED:
			onDoubleClicked(pick);
			break;
		case DRAGGED:
			onDragged(pick);
			break;
		case MOUSE_OUT:
			onMouseOut(pick);
			break;
		case MOUSE_OVER:
			onMouseOver(pick);
			break;
		case RIGHT_CLICKED:
			onRightClicked(pick);
			break;
		case MOUSE_MOVED:
			onMouseMoved(pick);
			break;
		case MOUSE_RELEASED:
			onMouseReleased(pick);
		}

	}

	protected void onMouseReleased(Pick pick) {

	}

	protected void onMouseMoved(Pick pick) {

	}

	protected void onRightClicked(Pick pick) {

	}

	protected void onMouseOver(Pick pick) {

	}

	protected void onMouseOut(Pick pick) {

	}

	protected void onDragged(Pick pick) {

	}

	protected void onDoubleClicked(Pick pick) {

	}

	protected void onClicked(Pick pick) {

	}
}
