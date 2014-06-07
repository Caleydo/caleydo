/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.gui.command;

import org.apache.commons.lang.StringUtils;
import org.caleydo.core.internal.MyPreferences;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.preference.IPreferenceStore;

public class ZoomHandler extends AbstractHandler implements IHandler {
	private static final int CHANGE = 20; // 20 %

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String action = event.getParameter("action");
		final boolean reset = StringUtils.equalsIgnoreCase(action, "reset");
		final boolean increaseZoomFactor = StringUtils.containsIgnoreCase(action, "in");

		final IPreferenceStore prefs = MyPreferences.prefs();
		int current = prefs.getInt(MyPreferences.VIEW_ZOOM_FACTOR);

		int next;
		if (reset)
			next = prefs.getDefaultInt(MyPreferences.VIEW_ZOOM_FACTOR);
		else if (increaseZoomFactor)
			next = current + CHANGE;
		else
			next = Math.max(10, current - CHANGE);

		MyPreferences.prefs().setValue(MyPreferences.VIEW_ZOOM_FACTOR, next);

		return null;
	}
}
