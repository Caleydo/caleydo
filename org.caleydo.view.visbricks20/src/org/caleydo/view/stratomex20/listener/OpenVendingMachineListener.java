package org.caleydo.view.stratomex20.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.dvi.event.OpenVendingMachineEvent;
import org.caleydo.view.stratomex20.GLStratomex20;

/**
 * Listener for the event {@link OpenVendingMachineEvent}.
 * 
 * @author Marc Streit
 * 
 */
public class OpenVendingMachineListener
	extends AEventListener<GLStratomex20> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof OpenVendingMachineEvent) {
			OpenVendingMachineEvent openVendingMachineVent = (OpenVendingMachineEvent) event;
			handler.handleOpenVendingMachineEvent(openVendingMachineVent.getDataDomain());
		}
	}
}
