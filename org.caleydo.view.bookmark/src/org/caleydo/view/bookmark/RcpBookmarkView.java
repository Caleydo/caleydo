/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.bookmark;

import org.caleydo.core.view.ARcpGLViewPart;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.view.bookmark.toolbar.ExportDataAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;

public class RcpBookmarkView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpBookmarkView() {
		super(SerializedBookmarkView.class);
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		view = new GLBookmarkView(glCanvas, ViewFrustum.createDefault());
		initializeView();
		createPartControlGL();
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedBookmarkView();
		determineDataConfiguration(serializedView);
	}

	@Override
	public void addToolBarContent(IToolBarManager toolBarManager) {
		toolBarManager.add(new ExportDataAction());
	}

}
