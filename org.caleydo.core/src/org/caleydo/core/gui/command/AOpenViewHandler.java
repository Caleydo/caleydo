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
package org.caleydo.core.gui.command;

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
	private final String view;

	protected AOpenViewHandler(String view) {
		this.view = view;
	}

	@Override
	public final Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			IWorkbenchPage activePage = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage();
			int secondary = getNextSecondaryId();
			if (secondary >= 0) { // multipe ones
				activePage.showView(view, Integer.toString(secondary), IWorkbenchPage.VIEW_ACTIVATE);
			} else { // single one
				activePage.showView(view);
			}

		} catch (PartInitException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * override and return a secondary id to use for each newly created view to support multiple view instances
	 * 
	 * @return
	 */
	protected int getNextSecondaryId() {
		return -1;
	}
}