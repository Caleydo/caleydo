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

import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.internal.view.GLTourGuideView;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

public class OpenViewHandler extends AbstractHandler {

	@Override
	public final Object execute(ExecutionEvent event) throws ExecutionException {
		showTourGuide(event);
		return null;
	}

	private void showTourGuide(ExecutionEvent event) {
		for (EDataDomainQueryMode mode : EDataDomainQueryMode.values())
			showTourGuide(event, mode);
		// show the first one again for having it the context
		showTourGuide(event, EDataDomainQueryMode.STRATIFICATIONS);
	}

	private void showTourGuide(ExecutionEvent event, EDataDomainQueryMode mode) {
		try {
			IWorkbenchPage activePage = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage();
			RcpGLTourGuideView v = (RcpGLTourGuideView) activePage.showView(GLTourGuideView.VIEW_TYPE, mode.name(),
						IWorkbenchPage.VIEW_ACTIVATE);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
}
