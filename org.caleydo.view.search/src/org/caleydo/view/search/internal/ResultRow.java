/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.search.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.view.search.api.IResultRow;

/**
 * one row within one category specific table
 *
 * @author Samuel Gratzl
 *
 */
public final class ResultRow implements IResultRow {

	/**
	 * the id of the primary {@link IDType}
	 */
	private final Object pid;

	private final IDType primary;

	private final Map<IDType, Object> values = new HashMap<>();
	/**
	 * marker that the query matches the given {@link IDType}
	 */
	private final Set<IDType> found = new HashSet<>();

	public ResultRow(IDType primary, Object pid) {
		this.pid = pid;
		this.primary = primary;
		set(primary, pid, false);
	}

	@Override
	public Object getPrimaryId() {
		return pid;
	}

	@Override
	public IDCategory getIDCategory() {
		return primary.getIDCategory();
	}

	/**
	 * @return the primary, see {@link #primary}
	 */
	public IDType getPrimary() {
		return primary;
	}

	public void set(IDType idType, Object id, boolean found) {
		if (id == null)
			return;
		values.put(idType, id);
		if (found)
			this.found.add(idType);
	}

	public void set(IDType idType, Set<Object> ids) {
		if (ids == null || ids.isEmpty())
			return;
		if (ids.size() == 1)
			values.put(idType, ids.iterator().next());
		else
			values.put(idType, ids);
	}

	@Override
	public boolean has(IDType idType) {
		return values.containsKey(idType);
	}

	@Override
	public Object get(IDType idType) {
		return values.get(idType);
	}

	public boolean wasFound(IDType type) {
		return found.contains(type);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pid == null) ? 0 : pid.hashCode());
		result = prime * result + ((primary == null) ? 0 : primary.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResultRow other = (ResultRow) obj;
		return Objects.equals(pid, other.pid) && Objects.equals(primary, other.primary);
	}

}
