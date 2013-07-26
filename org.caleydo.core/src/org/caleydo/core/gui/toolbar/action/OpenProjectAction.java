/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.gui.toolbar.action;

import org.caleydo.core.gui.SimpleAction;
import org.caleydo.core.internal.startup.OpenProjectCommand;
import org.eclipse.swt.widgets.Shell;

/**
 * Button to open a project
 *
 * @author Christian Partl
 *
 */
public class OpenProjectAction extends SimpleAction {
	public static final String LABEL = "Open Project";
	public static final String ICON = "resources/icons/general/open.png";

	/**
	 * Constructor.
	 */
	public OpenProjectAction() {
		super(LABEL, ICON);
	}

	@Override
	public void run() {
		OpenProjectCommand.openProject(new Shell());
	}
}
