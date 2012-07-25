/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.view.swt.collab;

import org.caleydo.core.manager.GeneralManager;
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
		NetworkManager networkManager = GeneralManager.get().getGroupwareManager().getNetworkManager();
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
