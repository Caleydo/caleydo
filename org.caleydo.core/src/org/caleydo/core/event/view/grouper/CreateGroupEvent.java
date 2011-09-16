package org.caleydo.core.event.view.grouper;

import java.util.Set;

import org.caleydo.core.event.AEvent;

public class CreateGroupEvent
	extends AEvent {

	private Set<Integer> setContainedGroups;

	public CreateGroupEvent(Set<Integer> setContainedGroups) {
		this.setContainedGroups = setContainedGroups;
	}

	@Override
	public boolean checkIntegrity() {
		return setContainedGroups != null;
	}

	public Set<Integer> getContainedGroups() {
		return setContainedGroups;
	}

	public void setContainedGroups(Set<Integer> setContainedGroups) {
		this.setContainedGroups = setContainedGroups;
	}

}
