package org.caleydo.core.manager.event.view.dimensionbased;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.virtualarray.delta.VirtualArrayDelta;
import org.caleydo.core.manager.event.AEvent;

/**
 * Event to signal that the virtual array has changed. It carries a {@link VirtualArrayDelta} as payload which
 * adapts the recipients virtual array for example by removing items.
 * 
 * @author Alexander Lex
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public abstract class VirtualArrayUpdateEvent<T extends VirtualArrayDelta<?>>
	extends AEvent {

	/** delta between old and new selection */
	private T virtualArrayDelta;

	/** additional information about the selection, e.g. to display in the info-box */
	private String info;

	public T getVirtualArrayDelta() {
		return virtualArrayDelta;
	}

	public void setVirtualArrayDelta(T virtualArrayDelta) {
		this.virtualArrayDelta = virtualArrayDelta;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	@Override
	public boolean checkIntegrity() {
		if (virtualArrayDelta == null) {
			throw new IllegalStateException("Integrity check in " + this
				+ "failed - virtualArrayDelta was null");
		}
		return true;
	}

}
