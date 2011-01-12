package org.caleydo.rcp.command.handler.view;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.view.CreateGUIViewEvent;
import org.caleydo.core.net.IGroupwareManager;
import org.caleydo.rcp.dialog.sendview.SendViewDialog;
import org.caleydo.rcp.view.rcp.ARcpGLViewPart;
import org.caleydo.rcp.view.rcp.CaleydoRCPViewPart;
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
