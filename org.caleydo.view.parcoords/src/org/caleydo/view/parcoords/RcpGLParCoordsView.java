/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.parcoords;

import org.caleydo.core.view.ARcpGLViewPart;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.view.parcoords.toolbar.AngularBrushingAction;
import org.caleydo.view.parcoords.toolbar.ResetAxisSpacingAction;
import org.caleydo.view.parcoords.toolbar.SaveSelectionsAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;

public class RcpGLParCoordsView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLParCoordsView() {
		super(SerializedParallelCoordinatesView.class);
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		view = new GLParallelCoordinates(glCanvas, ViewFrustum.createDefault());
		initializeView();
		createPartControlGL();
	}

	@Override
	public void createDefaultSerializedView() {

		serializedView = new SerializedParallelCoordinatesView();
		determineDataConfiguration(serializedView);
	}

	@Override
	public void addToolBarContent(IToolBarManager toolBarManager) {
		super.addToolBarContent(toolBarManager);

		toolBarManager.add(new AngularBrushingAction());
		toolBarManager.add(new ResetAxisSpacingAction());
		toolBarManager.add(new SaveSelectionsAction());

	}
}
