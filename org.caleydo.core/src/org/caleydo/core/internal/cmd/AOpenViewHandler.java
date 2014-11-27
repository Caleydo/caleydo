/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.internal.cmd;

import java.util.UUID;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class AOpenViewHandler extends AbstractHandler implements IHandler {
	protected static final boolean MULTIPLE = true;
	protected static final boolean SINGLE = false;

	private final String view;
	private final boolean allowMultiple;

	protected AOpenViewHandler(String view) {
		this(view, false);
	}

	protected AOpenViewHandler(String view, boolean allowMultiple) {
		this.view = view;
		this.allowMultiple = allowMultiple;
	}

	@Override
	public final Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			IWorkbenchPage activePage = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage();
			if (allowMultiple) { // multipe ones
				activePage.showView(view, createSecondaryID(),
						IWorkbenchPage.VIEW_ACTIVATE);
			} else { // single one
				activePage.showView(view);
			}

		} catch (PartInitException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static String createSecondaryID() {
		return Integer.toString(UUID.randomUUID().hashCode(), Character.MAX_RADIX);
	}
}
