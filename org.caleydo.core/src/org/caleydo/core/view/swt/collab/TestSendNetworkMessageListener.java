package org.caleydo.core.view.swt.collab;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.net.Connection;
import org.caleydo.core.net.NetworkEventReceiver;
import org.caleydo.core.net.NetworkManager;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Text;

/**
 * Sends simulated network messages to a given {@link NetworkEventReceiver}.
 * 
 * @author Werner Puff
 */
public class TestSendNetworkMessageListener
	implements SelectionListener {

	/** Field to write a xml-serialized message into */
	Text messageField;

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		widgetSelected(e);
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		NetworkManager networkManager = GeneralManager.get().getNetworkManager();
		Connection connection = networkManager.getConnections().get(0);
		NetworkEventReceiver receiver = connection.getIncomingPublisher(); 
		if (receiver != null) {
			receiver.handleNetworkEvent(messageField.getText());
		}
		else {
			System.out.println("No NetworkEventReceiver specified, is the network framework stopped?");
		}
	}

	public Text getMessageField() {
		return messageField;
	}

	public void setMessageField(Text messageField) {
		this.messageField = messageField;
	}

}
