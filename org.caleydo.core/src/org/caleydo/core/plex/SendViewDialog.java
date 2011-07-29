package org.caleydo.core.plex;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.view.CreateGUIViewEvent;
import org.caleydo.core.manager.view.ViewManager;
import org.caleydo.core.net.IGroupwareManager;
import org.caleydo.core.net.NetworkManager;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

/**
 * Dialog to ask for the clients to send a view to.
 * 
 * @author Werner Puff
 */
public class SendViewDialog
	extends Dialog {

	/** viewID of the the view to send */
	private int viewID;

	/** connections to remote applications in the moment of the dialog creation */
	private String[] clientNames;

	/** List to let the user choose the connections to send the view to */
	private List clientList;

	/** central {@link NetworkManager} to retrieve network related information */
	IGroupwareManager groupwareManager;

	/** central {@link ViewManager} to retrieve view information */
	ViewManager viewManager;

	/**
	 * Constructor.
	 */
	public SendViewDialog(Shell parentShell) {
		super(parentShell);

		viewManager = GeneralManager.get().getViewManager();
		groupwareManager = GeneralManager.get().getGroupwareManager();

		clientList = null;
		clientNames = null;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);

		newShell.setText("Send Client to View");
		// newShell.setImage(GeneralManager.get().getResourceLoader().getImage(newShell.getDisplay(),
		// StartClusteringDialogAction.ICON));
	}

	@Override
	protected Control createDialogArea(Composite p) {

		Composite parent = new Composite(p, SWT.NONE);
		parent.setLayout(new GridLayout(1, false));

		if (groupwareManager == null) {
			Label label = new Label(parent, SWT.CENTER);
			label.setText("This application is not in collaboration mode.");
		}
		else {
			clientNames = groupwareManager.getAvailableGroupwareClients();
			if (clientNames.length == 0) {
				Label label = new Label(parent, SWT.CENTER);
				label.setText("No Clients connected");
			}
			else {
				Label label = new Label(parent, SWT.NULL);
				label.setText("Choose client(s) to send the view to:");

				clientList = new List(parent, SWT.BORDER | SWT.MULTI);
				for (String clientName : clientNames) {
					clientList.add(clientName);
				}
			}
		}

		return parent;
	}

	@Override
	protected void okPressed() {
		if (clientList != null) {
			int[] selections = clientList.getSelectionIndices();
			for (int selection : selections) {
				System.out.println("sending view " + viewID + " to " + clientNames[selection]);

				CreateGUIViewEvent event = new CreateGUIViewEvent();
				AGLView view = viewManager.getGLView(viewID);
				event.setSerializedView(view.getSerializableRepresentation());
				event.setTargetApplicationID(clientNames[selection]);
				event.setSender(this);

				groupwareManager.getNetworkManager().getGlobalOutgoingPublisher().triggerEvent(event);
			}
		}
		super.okPressed();
	}

	@Override
	protected void cancelPressed() {
		super.cancelPressed();
	}

	/**
	 * Returns the viewID of the view that should be send.
	 * 
	 * @return viewID of view to send
	 */
	public int getViewID() {
		return viewID;
	}

	/**
	 * Sets the viewID of the view that should be send. This viewID must be specified by the invoker of the
	 * dialog.
	 * 
	 * @param viewID
	 *            viewID of view to send.
	 */
	public void setViewID(int viewID) {
		this.viewID = viewID;
	}

}
