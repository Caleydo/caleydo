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
package org.caleydo.core.plex;

import org.caleydo.core.view.ARcpGLViewPart;
import org.caleydo.core.view.CaleydoRCPViewPart;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

/**
 * RCP command handler to show the {@link SendViewDialog}.
 * 
 * @author Werner Puff
 */
public class SendRemoteHandler
	extends AbstractHandler
	implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IWorkbenchPart activePart =
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
		if (activePart instanceof CaleydoRCPViewPart) {
			if (activePart instanceof ARcpGLViewPart) {
				SendViewDialog dialog = new SendViewDialog(new Shell());
				ARcpGLViewPart glViewPart = (ARcpGLViewPart) activePart;
				dialog.setViewID(glViewPart.getGLView().getID());
				dialog.open();
			}
			else {
				// TODO send non-GL2 views
				throw new RuntimeException("sending of non gl-views not supported yet");
			}
		}
		else {
			throw new IllegalStateException("SendViewHandler invoked for a non-sendable viewpart ("
				+ activePart + ")");
		}

		return null;
	}

}
