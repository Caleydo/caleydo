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
package org.caleydo.core.gui.menu;

import org.caleydo.core.event.view.browser.ChangeURLEvent;
import org.caleydo.core.manager.GeneralManager;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

public class ReportBugHandler
	extends AbstractHandler
	implements IHandler {

	private final static String URL_REPORT_BUG = "https://trac.icg.tugraz.at/projects/org.caleydo/newticket";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		try {
			HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().showView("org.caleydo.view.browser");
		}
		catch (PartInitException e) {
			e.printStackTrace();
		}

		ChangeURLEvent changeURLEvent = new ChangeURLEvent();
		changeURLEvent.setSender(this);
		changeURLEvent.setUrl(URL_REPORT_BUG);
		GeneralManager.get().getEventPublisher().triggerEvent(changeURLEvent);

		return null;
	}
}
