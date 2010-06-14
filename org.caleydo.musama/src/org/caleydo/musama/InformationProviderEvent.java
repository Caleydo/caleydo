/**
 * 
 */
package org.caleydo.musama;


import de.mmis.core.base.event.AbstractTypedEvent;

public class InformationProviderEvent extends
		AbstractTypedEvent<InformationProviderEvent.Type> {
	public static enum Type {
		ROLE_CHANGED, VIEWADDED
	}

	private String id;
	
	@Deprecated
	public InformationProviderEvent() {
		super(null);
	}

	public InformationProviderEvent(InformationProviderEvent.Type type, String id) {
		super(type);
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "InformationProviderEvent [type="+getEventType()+", id=" + id + "]";
	}
}