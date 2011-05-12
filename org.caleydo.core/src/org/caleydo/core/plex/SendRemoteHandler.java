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
