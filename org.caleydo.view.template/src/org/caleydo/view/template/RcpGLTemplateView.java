/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.template;

import org.caleydo.core.view.ARcpGLViewPart;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO: DOCUMENT ME!
 *
 * @author <INSERT_YOUR_NAME>
 */
public class RcpGLTemplateView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLTemplateView() {
		super(SerializedTemplateView.class);
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		view = new GLTemplateView(glCanvas);
		initializeView();
		createPartControlGL();
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedTemplateView();
		determineDataConfiguration(serializedView);
	}

	@Override
	public String getViewGUIID() {
		return GLTemplateView.VIEW_TYPE;
	}

}
