package org.caleydo.rcp.dialog.sendview;

import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.event.view.CreateGUIViewEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.net.Connection;
import org.caleydo.core.net.NetworkManager;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
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
	private Connection[] connections;
	
	/** List to let the user choose the connections to send the view to */
	private List clientList;
	
	/** central {@link NetworkManager} to retrieve network related information */
	NetworkManager networkManager;
	
	/** central {@link IViewManager} to retrieve view information */
	IViewManager viewManager;	
	
	/**
	 * Constructor.
	 */
	public SendViewDialog(Shell parentShell) {
		super(parentShell);

		viewManager = GeneralManager.get().getViewGLCanvasManager();
		networkManager = GeneralManager.get().getNetworkManager();

		clientList = null;
		connections = null;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);

		newShell.setText("Send Client to View");
//		newShell.setImage(GeneralManager.get().getResourceLoader().getImage(newShell.getDisplay(),
//			StartClusteringAction.ICON));
	}

	@Override
	protected Control createDialogArea(Composite p) {

		Composite parent = new Composite(p, SWT.NONE);
		parent.setLayout(new GridLayout(1, false));

		java.util.List<Connection> connectionList = networkManager.getConnections();
		if (connectionList.size() == 0) {
			Label label = new Label(parent, SWT.CENTER);
			label.setText("No Clients connected");
		} else {
			Label label = new Label(parent, SWT.NULL);
			label.setText("Choose client(s) to send the view to:");

			clientList = new List(parent, SWT.BORDER | SWT.MULTI);
			connections = connectionList.toArray(new Connection[connectionList.size()]); 
			for (Connection connection : connections) {
				clientList.add(connection.getRemoteNetworkName());
			}
		}
		
		return parent;
	}

	@Override
	protected void okPressed() {
		if (clientList != null) {
			int[] selections = clientList.getSelectionIndices();
			for (int selection : selections) {
				System.out.println("sending view " + viewID + " to " + connections[selection].getRemoteNetworkName());

				CreateGUIViewEvent event = new CreateGUIViewEvent();
				AGLEventListener view = viewManager.getGLEventListener(viewID);
				event.setSerializedView(view.getSerializableRepresentation());
				event.setTargetApplicationID(connections[selection].getRemoteNetworkName());
				event.setSender(this);
				
				networkManager.getGlobalOutgoingPublisher().triggerEvent(event);
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
	 * @return viewID of view to send
	 */
	public int getViewID() {
		return viewID;
	}

	/**
	 * Sets the viewID of the view that should be send. 
	 * This viewID must be specified by the invoker of the dialog. 
	 * @param viewID viewID of view to send.
	 */
	public void setViewID(int viewID) {
		this.viewID = viewID;
	}

}
