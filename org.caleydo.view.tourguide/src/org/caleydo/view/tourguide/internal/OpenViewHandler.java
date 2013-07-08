/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal;

import org.caleydo.core.gui.util.DisplayUtils;
import org.caleydo.core.util.execution.SafeCallable;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.internal.view.GLTourGuideView;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

public class OpenViewHandler extends AbstractHandler {

	@Override
	public final Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		for (EDataDomainQueryMode mode : EDataDomainQueryMode.values())
			showTourGuideImpl(window, mode);
		// show the first one again for having it the context
		showTourGuideImpl(window, EDataDomainQueryMode.STRATIFICATIONS);
		return null;
	}

	private static RcpGLTourGuideView showTourGuideImpl(IWorkbenchWindow window, EDataDomainQueryMode mode) {
		try {
			IWorkbenchPage activePage = window.getActivePage();
			RcpGLTourGuideView view = (RcpGLTourGuideView) activePage.showView(GLTourGuideView.VIEW_TYPE, mode.name(),
					IWorkbenchPage.VIEW_ACTIVATE);
			return view;
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * triggers to open tour guide
	 * 
	 * @param mode
	 * @return
	 */
	public static RcpGLTourGuideView showTourGuide(final EDataDomainQueryMode mode) {
		if (Display.getDefault().getThread() != Thread.currentThread()) { // not the right thread
			return DisplayUtils.syncExec(Display.getDefault(), new SafeCallable<RcpGLTourGuideView>() {
				@Override
				public RcpGLTourGuideView call() {
					return showTourGuideImpl(PlatformUI.getWorkbench().getActiveWorkbenchWindow(), mode);
				}
			});
		} else {
			return showTourGuideImpl(PlatformUI.getWorkbench().getActiveWorkbenchWindow(), mode);
		}
	}

}
