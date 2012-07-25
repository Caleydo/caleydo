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

import org.caleydo.core.event.view.CreateGUIViewEvent;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.net.IGroupwareManager;
import org.caleydo.core.view.ARcpGLViewPart;
import org.caleydo.core.view.CaleydoRCPViewPart;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

/**
 * RCP command handler to show the {@link SendViewDialog}.
 * 
 * @author Werner Puff
 */
public class PublishViewHandler
	extends AbstractHandler
	implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IWorkbenchPart activePart = activePage.getActivePart();
		if (activePart instanceof CaleydoRCPViewPart) {
			if (activePart instanceof ARcpGLViewPart) {
				ARcpGLViewPart glViewPart = (ARcpGLViewPart) activePart;

				IGroupwareManager groupwareManager = GeneralManager.get().getGroupwareManager();
				String targetApplicationID = groupwareManager.getPublicGroupwareClient();

				CreateGUIViewEvent viewEvent = new CreateGUIViewEvent();
				viewEvent.setSerializedView(glViewPart.getGLView().getSerializableRepresentation());
				viewEvent.setTargetApplicationID(targetApplicationID);
				viewEvent.setSender(this);

				groupwareManager.getNetworkManager().getGlobalOutgoingPublisher().triggerEvent(viewEvent);

				IViewReference[] views = activePage.getViewReferences();
				for (int i = 0; i < views.length; i++) {
					if (glViewPart.getViewGUIID().equals(views[i].getId())) {
						activePage.hideView(views[i]);
						break;
					}
				}
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
