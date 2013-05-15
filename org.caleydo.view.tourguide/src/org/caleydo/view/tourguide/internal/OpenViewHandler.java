/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
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
		showTourGuide(event);
		return null;
	}

	private static void showTourGuide(ExecutionEvent event) {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		for (EDataDomainQueryMode mode : EDataDomainQueryMode.values())
			showTourGuideImpl(window, mode);
		// show the first one again for having it the context
		showTourGuideImpl(window, EDataDomainQueryMode.STRATIFICATIONS);
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

	public static RcpGLTourGuideView showTourGuide(final EDataDomainQueryMode mode) {
		if (Display.getDefault().getThread() != Thread.currentThread()) {
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
