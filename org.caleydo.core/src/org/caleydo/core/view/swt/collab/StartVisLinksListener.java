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
package org.caleydo.core.view.swt.collab;

import org.caleydo.core.util.execution.DisplayLoopExecution;
import org.caleydo.core.view.vislink.VisLinkManager;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;

/**
 * SWT event listener for requesting busy mode
 * 
 * @author Werner Puff
 */
public class StartVisLinksListener
	implements Listener {

	Object requester;

	@Override
	public void handleEvent(Event event) {
		VisLinkManager visLinkManager = VisLinkManager.get();
		Display display = Display.getCurrent();

		// FIXME: how to get the correct shell?
		// for (Shell s : display.getShells()) {
		// System.out.println("shell: " + s.getBounds());
		// }
		Rectangle r = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getBounds();
		visLinkManager.register(r.x, r.y, r.width, r.height, display);
		DisplayLoopExecution dle = DisplayLoopExecution.get();
		dle.executeMultiple(visLinkManager);

	}

	public void setRequester(Object requester) {
		this.requester = requester;
	}

}
