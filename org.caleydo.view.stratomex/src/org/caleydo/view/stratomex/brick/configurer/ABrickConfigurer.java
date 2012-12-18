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
package org.caleydo.view.stratomex.brick.configurer;

import org.caleydo.core.view.opengl.layout.util.multiform.MultiFormRenderer;
import org.caleydo.core.view.opengl.layout.util.multiform.MultiFormViewSwitchingBar;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.stratomex.brick.GLBrick;

/**
 * @author Christian Partl
 *
 */
public abstract class ABrickConfigurer implements IBrickConfigurer {

	protected void configureBrick(MultiFormRenderer multiFormRenderer, final GLBrick brick,
			int compactRendererID) {
		MultiFormViewSwitchingBar viewSwitchingBar = new MultiFormViewSwitchingBar(multiFormRenderer, brick);

		// There should be no view switching button for the visualization that is used in compact mode, as there is a
		// dedicated button to switch to this mode.
		viewSwitchingBar.removeButton(compactRendererID);

		APickingListener pickingListener = new APickingListener() {
			@Override
			public void clicked(Pick pick) {
				if (brick.getBrickColumn().isGlobalViewSwitching()) {
					brick.getBrickColumn().switchBrickViews(brick.getGlobalRendererID(pick.getObjectID()));
				}
			}
		};

		for (Integer rendererID : multiFormRenderer.getRendererIDs()) {
			viewSwitchingBar.addButtonPickingListener(pickingListener, rendererID);
		}

		brick.setMultiFormRenderer(multiFormRenderer);
		brick.setViewSwitchingBar(viewSwitchingBar);
		brick.setCompactRendererID(compactRendererID);
		multiFormRenderer.addChangeListener(brick);
	}

}
