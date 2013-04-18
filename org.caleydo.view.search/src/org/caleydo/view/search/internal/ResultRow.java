/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
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
