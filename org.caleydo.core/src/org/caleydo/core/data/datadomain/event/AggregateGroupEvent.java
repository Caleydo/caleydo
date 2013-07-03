/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.datadomain.event;

import java.util.Set;

import org.caleydo.core.event.AEvent;

/**
 * Event signaling that the groups specified should be aggregated into one dimension
 * 
 * @author Alexander Lex
 */
public class AggregateGroupEvent
	extends AEvent {

	private Set<Integer> setGroupsToAggregate;

	public AggregateGroupEvent(Set<Integer> setGroupsToAggregate) {
		this.setGroupsToAggregate = setGroupsToAggregate;
	}

	@Override
	public boolean checkIntegrity() {
		return setGroupsToAggregate != null;
	}

	public Set<Integer> getGroups() {
		return setGroupsToAggregate;
	}

	public void setGroupsToDelete(Set<Integer> setGroupsToDelete) {
		this.setGroupsToAggregate = setGroupsToDelete;
	}

}
