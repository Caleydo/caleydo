package org.caleydo.core.view.swt.collab;

import org.caleydo.core.manager.execution.DisplayLoopExecution;
import org.caleydo.core.manager.vislink.VisLinkManager;
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
