/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.gui.menu;


public class KnownBugsHandler extends ABrowserContentsHandler {

	private final static String URL_KNOWN_BUGS = "https://github.com/Caleydo/caleydo-dev/issues";

	public KnownBugsHandler() {
		super(URL_KNOWN_BUGS);
	}
}
