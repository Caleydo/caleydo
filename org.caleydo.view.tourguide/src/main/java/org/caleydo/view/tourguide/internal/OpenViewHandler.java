/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal;

import org.caleydo.core.gui.util.DisplayUtils;
import org.caleydo.core.util.execution.SafeCallable;
import org.caleydo.view.tourguide.internal.view.GLTourGuideView;
import org.caleydo.view.tourguide.spi.adapter.ITourGuideAdapterFactory;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

public class OpenViewHandler extends AbstractHandler {

	@Override
	public final Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		ITourGuideAdapterFactory first = null;
		for (ITourGuideAdapterFactory adapter : TourGuideAdapters.get()) {
			showTourGuideImpl(window, adapter.getSecondaryID());
			if (first == null)
				first = adapter;
		}
		// show the first one again for having it the context
		if (first != null)
			showTourGuideImpl(window, first.getSecondaryID());
		return null;
	}

	private static RcpGLTourGuideView showTourGuideImpl(IWorkbenchWindow window, String secondaryID) {
		try {
			IWorkbenchPage activePage = window.getActivePage();
			RcpGLTourGuideView view = (RcpGLTourGuideView) activePage.showView(GLTourGuideView.VIEW_TYPE, secondaryID,
					IWorkbenchPage.VIEW_ACTIVATE);
			return view;
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static RcpGLTourGuideView showTourGuide(final ITourGuideAdapterFactory adapter) {
		return showTourGuide(adapter.getSecondaryID());
	}
	/**
	 * triggers to open tour guide
	 *
	 * @param mode
	 * @return
	 */
	public static RcpGLTourGuideView showTourGuide(final String secondaryID) {
		if (Display.getDefault().getThread() != Thread.currentThread()) { // not the right thread
			return DisplayUtils.syncExec(Display.getDefault(), new SafeCallable<RcpGLTourGuideView>() {
				@Override
				public RcpGLTourGuideView call() {
					return showTourGuideImpl(PlatformUI.getWorkbench().getActiveWorkbenchWindow(), secondaryID);
				}
			});
		} else {
			return showTourGuideImpl(PlatformUI.getWorkbench().getActiveWorkbenchWindow(), secondaryID);
		}
	}

	public static void hideTourGuide(final ITourGuideAdapterFactory adapter) {
		hideTourGuide(adapter.getSecondaryID());
	}

	public static void hideTourGuide(final String secondaryID) {
		if (Display.getDefault().getThread() != Thread.currentThread()) { // not the right thread
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					hideTourGuideImpl(PlatformUI.getWorkbench().getActiveWorkbenchWindow(), secondaryID);
				}
			});
		} else {
			hideTourGuideImpl(PlatformUI.getWorkbench().getActiveWorkbenchWindow(), secondaryID);
		}
	}

	private static void hideTourGuideImpl(IWorkbenchWindow window, final String secondaryID) {
		IWorkbenchPage activePage = window.getActivePage();
		IViewReference viewReference = activePage.findViewReference(GLTourGuideView.VIEW_TYPE, secondaryID);
		if (viewReference != null)
			activePage.hideView(viewReference);
	}

}
