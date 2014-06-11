/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.radial;

import org.caleydo.core.view.ARcpGLViewPart;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.view.radial.actions.ChangeColorModeAction;
import org.caleydo.view.radial.actions.GoBackInHistoryAction;
import org.caleydo.view.radial.actions.GoForthInHistoryAction;
import org.caleydo.view.radial.toolbar.DepthSlider;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;

public class RcpGLRadialHierarchyView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLRadialHierarchyView() {
		super(SerializedRadialHierarchyView.class);
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		// minSizeComposite.setView(view);

		view = new GLRadialHierarchy(glCanvas, ViewFrustum.createDefault());
		initializeView();
		createPartControlGL();
	}


	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedRadialHierarchyView();
		determineDataConfiguration(serializedView);
	}


	@Override
	public void addToolBarContent(IToolBarManager toolBarManager) {
		super.addToolBarContent(toolBarManager);
		toolBarManager.add(new GoBackInHistoryAction());
		toolBarManager.add(new GoForthInHistoryAction());
		toolBarManager.add(new ChangeColorModeAction());
		toolBarManager.add(new DepthSlider(((SerializedRadialHierarchyView) serializedView)
				.getMaxDisplayedHierarchyDepth()));
	}
}
