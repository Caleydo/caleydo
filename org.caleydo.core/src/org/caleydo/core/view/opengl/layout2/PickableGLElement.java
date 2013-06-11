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

import org.caleydo.core.util.base.ILabeled;
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
	/**
	 * the tooltip of this element
	 */
	private String tooltip = null;
	/**
	 * the object id to use
	 */
	private int objectId = 0;

	public PickableGLElement() {
		this(0);
	}

	public PickableGLElement(int objectId) {
		this.objectId = objectId;
		this.setVisibility(EVisibility.PICKABLE);
		this.onPick(new IPickingListener() {
			@Override
			public void pick(Pick pick) {
				onPicked(pick);
			}
		});
	}

	@Override
	protected void init(IGLElementContext context) {
		super.init(context);
		// create a tooltip listener to render the tooltip of this element
		this.onPick(context.getSWTLayer().createTooltip(new ILabeled() {
			@Override
			public String getLabel() {
				return getTooltip();
			}
		}));
	}

	/**
	 * @return the tooltip of this element
	 */
	public String getTooltip() {
		return tooltip;
	}

	/**
	 * sets the picking object id to use
	 * 
	 * @param objectId
	 *            setter, see {@link objectId}
	 */
	public PickableGLElement setPickingObjectId(int objectId) {
		if (this.objectId == objectId)
			return this;
		this.objectId = objectId;
		if (context != null) { // already initialized
			// temporary set the visible state to not pickable, to enforce that we register a new picking listener with
			// the right id
			EVisibility bak = getVisibility();
			if (bak == EVisibility.PICKABLE) {
				setVisibility(EVisibility.VISIBLE).setVisibility(EVisibility.PICKABLE);
			}
		}
		return this;
	}

	/**
	 * @return the objectId, see {@link #objectId}
	 */
	@Override
	public int getPickingObjectId() {
		return objectId;
	}

	/**
	 * @param tooltip
	 *            setter, see {@link tooltip}
	 */
	public void setTooltip(String tooltip) {
		if (tooltip != null)
			tooltip = tooltip.trim();
		if (tooltip != null && tooltip.length() == 0)
			tooltip = null;
		this.tooltip = tooltip;
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
