/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.gui.menu;

import org.caleydo.core.util.system.BrowserUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;

public abstract class ABrowserContentsHandler extends AbstractHandler implements IHandler {
	private final String url;

	public ABrowserContentsHandler(String url) {
		this.url = url;
	}

	@Override
	public final Object execute(ExecutionEvent event) throws ExecutionException {

		BrowserUtils.openURL(url);

		return null;
	}
}
