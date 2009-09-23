package org.caleydo.core.view.swt.collab;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.net.event.ConnectToServerEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * Listener for the "connect to" button that triggers a connection to a running caleydo server
 * 
 * @author Werner Puff
 */
public class ConnectToServerListener
	implements Listener {

	/** text field for the ip address to connect to */
	private Text addressField;

	@Override
	public void handleEvent(Event event) {
		System.out.println("connect to " + addressField.getText());
		ConnectToServerEvent connectToServerEvent = new ConnectToServerEvent();
		connectToServerEvent.setAddress(addressField.getText());
		GeneralManager.get().getEventPublisher().triggerEvent(connectToServerEvent);
	}

	public Text getAddressField() {
		return addressField;
	}

	public void setAddressField(Text addressField) {
		this.addressField = addressField;
	}

}
