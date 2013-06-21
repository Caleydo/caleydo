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
package org.caleydo.view.stratomex.tourguide.internal;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.stratomex.EPickingType;
import org.caleydo.view.stratomex.column.BrickColumn;
import org.caleydo.view.stratomex.tourguide.TourguideAdapter;

/**
 * @author Samuel Gratzl
 *
 */
public class AddAttachedLayoutRenderer extends ALayoutRenderer implements IPickingListener {
	private final BrickColumn view;
	private final int id;
	private final TourguideAdapter tourguide;
	private final boolean left;
	private boolean show = false;

	public AddAttachedLayoutRenderer(BrickColumn view, TourguideAdapter tourguide, boolean left) {
		this.view = view;
		this.id = view.getID();
		this.tourguide = tourguide;
		this.left = left;
		view.getStratomexView().addTypePickingListener(this, EPickingType.BRICK.name());
	}

	@Override
	protected void renderContent(GL2 gl) {
		if (!show)
			return;
		float w1px = view.getPixelGLConverter().getGLWidthForPixelWidth(1);
		float h1px = view.getPixelGLConverter().getGLHeightForPixelHeight(1);
		float hi = h1px * 24;
		float wi = w1px * 24;
		tourguide.renderAddDependentButton(gl, left ? (-wi - w1px * 2) : (x + w1px * 2), y - hi * 2, wi, hi, id * 2
				+ (left ? 1 : 0));
	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return false;
	}

	@Override
	public void pick(Pick pick) {
		switch(pick.getPickingMode()) {
		case MOUSE_OVER:
			show = view.getHeaderBrick() != null && pick.getObjectID() == view.getHeaderBrick().getID()
					&& !view.getStratomexView().isDetailMode();
			setDisplayListDirty(true);
			break;
		default:
			break;
		}

	}

	@Override
	public void destroy(GL2 gl) {
		view.getStratomexView().removeTypePickingListener(this, EPickingType.BRICK.name());
		super.destroy(gl);
	}

}
