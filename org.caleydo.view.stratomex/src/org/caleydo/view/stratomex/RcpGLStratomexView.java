/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.stratomex;

import org.caleydo.core.gui.toolbar.action.OpenOnlineHelpAction;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.ARcpGLViewPart;
import org.caleydo.view.stratomex.toolbar.ConnectionsModeGUI;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;

/**
 * RCP view container for {@link GLStratomex}
 *
 * @author <Alexander Lex
 */
public class RcpGLStratomexView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLStratomexView() {
		super(SerializedStratomexView.class);
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		GLStratomex stratomex = new GLStratomex(glCanvas, serializedView.getViewFrustum());
		view = stratomex;
		initializeView();

		createPartControlGL();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.caleydo.core.view.CaleydoRCPViewPart#getView()
	 */
	@Override
	public GLStratomex getView() {
		return (GLStratomex) super.getView();
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedStratomexView();
		determineDataConfiguration(serializedView);
	}

	@Override
	public void addToolBarContent(IToolBarManager toolBarManager) {

		toolBarManager.add(new ConnectionsModeGUI());
		toolBarManager.add(new OpenOnlineHelpAction(
GeneralManager.HELP_URL + "/views/stratomex.md", true));

		toolBarManager.update(true);
	}
}
