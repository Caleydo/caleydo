package org.caleydo.core.manager.event.view.storagebased;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.event.AEvent;

/**
 * Event that changes the trend highlight mode for VisBricks.
 * 
 * @author Marc Streit
 */
@XmlRootElement
@XmlType
public class ConnectionsModeEvent
	extends AEvent {

	private boolean connectionsOn;
	private boolean connectionsHighlightDynamic;
	
	/**
	 * Determines the focus highlight dynamically in a range between 0 and 1
	 */
	private float connectionsFocusFactor;
	
	public ConnectionsModeEvent(boolean connectionsOn, boolean connectionsHighlighDynamic, float focusFactor) {
		this.connectionsOn = connectionsOn;
		this.connectionsHighlightDynamic = connectionsHighlighDynamic;
		this.connectionsFocusFactor = focusFactor;
	}
	
	@Override
	public boolean checkIntegrity() {
		return true;
	}
	
	public boolean isConnectionsHighlightDynamic() {
		return connectionsHighlightDynamic;
	}
	
	public boolean isConnectionsOn() {
		return connectionsOn;
	}
	
	public float getFocusFactor() {
		return connectionsFocusFactor;
	}
}
