package org.caleydo.rcp.command.handler.view;

import org.caleydo.rcp.dialog.sendview.SendViewDialog;
import org.caleydo.rcp.view.CaleydoViewPart;
import org.caleydo.rcp.view.opengl.AGLViewPart;
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

		IWorkbenchPart activePart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
		if (activePart instanceof CaleydoViewPart) {
			if (activePart instanceof AGLViewPart) {
				SendViewDialog dialog = new SendViewDialog(new Shell());
				AGLViewPart glViewPart = (AGLViewPart) activePart;
				dialog.setViewID(glViewPart.getGLEventListener().getID());
				dialog.open();
			} else {
				// TODO send non-GL views
				throw new RuntimeException("sending of non gl-views not supported yet");
			}
		} else {
			throw new IllegalStateException("SendViewHandler invoked for a non-sendable viewpart (" + activePart + ")");
		}
		
		return null;
	}

}
