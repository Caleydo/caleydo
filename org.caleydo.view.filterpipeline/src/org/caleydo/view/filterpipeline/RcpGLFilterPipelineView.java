/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.filterpipeline;

import org.caleydo.core.view.ARcpGLViewPart;
import org.caleydo.view.filterpipeline.toolbar.SelectFilterTypeWidget;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO: DOCUMENT ME!
 *
 * @author <INSERT_YOUR_NAME>
 */
public class RcpGLFilterPipelineView extends ARcpGLViewPart {
	public static String VIEW_TYPE = "org.caleydo.view.filterpipeline";

	/**
	 * Constructor.
	 */
	public RcpGLFilterPipelineView() {
		super(SerializedFilterPipelineView.class);
	}

	@Override
	public boolean isSupportView() {
		return true;
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		view = new GLFilterPipeline(glCanvas, serializedView.getViewFrustum());
		initializeView();
		createPartControlGL();
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedFilterPipelineView();
		determineDataConfiguration(serializedView);
	}

	@Override
	public void addToolBarContent(IToolBarManager toolBarManager) {
		toolBarManager.add(new SelectFilterTypeWidget((GLFilterPipeline) view));
	}
}
