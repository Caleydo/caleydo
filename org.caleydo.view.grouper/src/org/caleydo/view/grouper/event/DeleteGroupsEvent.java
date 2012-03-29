package org.caleydo.view.grouper.event;

import java.util.Set;
import org.caleydo.core.event.AEvent;

public class DeleteGroupsEvent extends AEvent {

	private Set<Integer> setGroupsToDelete;

	public DeleteGroupsEvent(Set<Integer> setGroupsToDelete) {
		this.setGroupsToDelete = setGroupsToDelete;
	}

	@Override
	public boolean checkIntegrity() {
		return setGroupsToDelete != null;
	}

	public Set<Integer> getGroupsToDelete() {
		return setGroupsToDelete;
	}

	public void setGroupsToDelete(Set<Integer> setGroupsToDelete) {
		this.setGroupsToDelete = setGroupsToDelete;
	}

}
