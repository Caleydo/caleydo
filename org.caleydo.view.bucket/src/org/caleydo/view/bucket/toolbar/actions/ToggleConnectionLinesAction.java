package org.caleydo.view.bucket.toolbar.actions;

import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.view.bucket.toolbar.RemoteRenderingToolBarMediator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class ToggleConnectionLinesAction extends Action implements IToolBarItem {

	public static final String TEXT = "Turn on/off connection lines";
	public static final String ICON = "resources/icons/view/remote/connection_lines.png";

	/** mediator to handle actions triggered by instances of this class */
	RemoteRenderingToolBarMediator remoteRenderingToolBarMediator;

	/** status of this toggle-button */
	private boolean connectionLinesEnabled = true;

	/**
	 * Constructor.
	 */
	public ToggleConnectionLinesAction(RemoteRenderingToolBarMediator mediator) {
		remoteRenderingToolBarMediator = mediator;

		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(
				PlatformUI.getWorkbench().getDisplay(), ICON)));
		setChecked(true);
	}

	@Override
	public void run() {
		super.run();

		connectionLinesEnabled = !connectionLinesEnabled;
		if (connectionLinesEnabled) {
			remoteRenderingToolBarMediator.enableConnectionLines();
		} else {
			remoteRenderingToolBarMediator.disableConnectionLines();
		}
	}

	public boolean isConnectionLinesEnabled() {
		return connectionLinesEnabled;
	}

	public void setConnectionLinesEnabled(boolean connectionLinesEnabled) {
		this.connectionLinesEnabled = connectionLinesEnabled;
		setChecked(connectionLinesEnabled);
	};

}
