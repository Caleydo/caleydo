package org.caleydo.core.event.view.grouper;

import java.util.Set;
import org.caleydo.core.event.AEvent;

public class CopyGroupsEvent
	extends AEvent {
	private Set<Integer> setGroupsToCopy;

	public CopyGroupsEvent(Set<Integer> setGroupsToCopy) {
		this.setGroupsToCopy = setGroupsToCopy;
	}

	@Override
	public boolean checkIntegrity() {
		return setGroupsToCopy != null;
	}

	public Set<Integer> getGroupsToCopy() {
		return setGroupsToCopy;
	}

	public void setGroupsToCopy(Set<Integer> setGroupsToCopy) {
		this.setGroupsToCopy = setGroupsToCopy;
	}

}
