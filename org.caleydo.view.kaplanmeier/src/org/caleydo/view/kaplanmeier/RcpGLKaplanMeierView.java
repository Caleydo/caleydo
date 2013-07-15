/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.kaplanmeier;

import org.caleydo.core.view.ARcpGLViewPart;
import org.eclipse.swt.widgets.Composite;

/**
 * RCP Kaplan Meier view.
 *
 * @author Marc Streit
 */
public class RcpGLKaplanMeierView extends ARcpGLViewPart {

	public RcpGLKaplanMeierView() {
		super(SerializedKaplanMeierView.class);
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		view = new GLKaplanMeier(glCanvas, serializedView.getViewFrustum());
		initializeView();
		createPartControlGL();
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedKaplanMeierView();
		determineDataConfiguration(serializedView);
	}

	@Override
	public String getViewGUIID() {
		return GLKaplanMeier.VIEW_TYPE;
	}

}
