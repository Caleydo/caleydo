/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.gui.menu;


public class ReportBugHandler extends ABrowserContentsHandler {

	private final static String URL_REPORT_BUG = "https://github.com/Caleydo/caleydo-dev/issues/new";

	public ReportBugHandler() {
		super(URL_REPORT_BUG);
	}
}
