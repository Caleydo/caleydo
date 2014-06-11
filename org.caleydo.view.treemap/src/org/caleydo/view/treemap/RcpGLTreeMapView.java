/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.treemap;

import org.caleydo.core.view.ARcpGLViewPart;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.view.treemap.actions.LevelHighlightingAction;
import org.caleydo.view.treemap.actions.ToggleColoringModeAction;
import org.caleydo.view.treemap.actions.ToggleLabelAction;
import org.caleydo.view.treemap.actions.ZoomInAction;
import org.caleydo.view.treemap.actions.ZoomOutAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;

public class RcpGLTreeMapView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLTreeMapView() {
		super(SerializedHierarchicalTreeMapView.class);
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		view = new GLHierarchicalTreeMap(glCanvas, ViewFrustum.createDefault());
		initializeView();
		createPartControlGL();
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedHierarchicalTreeMapView();
		determineDataConfiguration(serializedView);
	}

	@Override
	public void addToolBarContent(IToolBarManager toolBarManager) {
		super.addToolBarContent(toolBarManager);
		toolBarManager.add(new LevelHighlightingAction());
		toolBarManager.add(new ToggleColoringModeAction());
		toolBarManager.add(new ToggleLabelAction());
		toolBarManager.add(new ZoomInAction());
		toolBarManager.add(new ZoomOutAction());
	}
}
