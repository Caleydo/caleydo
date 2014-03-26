/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.stratomex.brick.configurer;

import org.caleydo.core.view.contextmenu.ContextMenuCreator;
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

	protected void configureBrick(MultiFormRenderer multiFormRenderer, final GLBrick brick, int compactRendererID) {
		MultiFormViewSwitchingBar viewSwitchingBar = new MultiFormViewSwitchingBar(multiFormRenderer,
				brick.getStratomex());

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

	@Override
	public void addDataSpecificContextMenuEntries(ContextMenuCreator creator, GLBrick brick) {
		// do nothing by default
	}

}
