package org.caleydo.core.manager.event.view;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.view.serialize.ASerializedView;

/**
 * Event to create SWT-views from its serialized form. 
 * Especially used to transmit a view to a remote caleydo application or load views from disk. 
 * 
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class CreateGUIViewEvent
	extends AEvent {

	/** serialized form of the view to create */
	private ASerializedView serializedView;
	
	/** application id of caleydo application where the id should be created */
	private String targetApplicationID;
	
	@Override
	public boolean checkIntegrity() {
		if (serializedView == null) {
			throw new IllegalStateException("the serialized-view has not been set");
		}
		if (targetApplicationID == null) {
			throw new IllegalStateException("the targetApplicationID has not been set");
		}
		
		return true;
	}

	public ASerializedView getSerializedView() {
		return serializedView;
	}

	public void setSerializedView(ASerializedView serializedView) {
		this.serializedView = serializedView;
	}

	public void setTargetApplicationID(String targetApplicationID) {
		this.targetApplicationID = targetApplicationID;
	}

	public String getTargetApplicationID() {
		return targetApplicationID;
	}

}
