package org.caleydo.testing.applications.caleydoplex;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Shell;

public class ConnectListener implements SelectionListener {

	private Shell shell;

	private DeskothequeManager deskothequeManager;

	public ConnectListener(Shell shell, DeskothequeManager deskothequeManager) {
		this.shell = shell;
		this.deskothequeManager = deskothequeManager;
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		// TODO Auto-generated method stub

		// get extents of shell window
		org.eclipse.swt.graphics.Rectangle shellRect = shell.getBounds();
		this.deskothequeManager.establishConnection(shellRect.x, shellRect.y,
				shellRect.width, shellRect.height);
	}

}
