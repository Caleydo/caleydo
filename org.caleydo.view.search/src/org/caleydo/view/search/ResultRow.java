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
package org.caleydo.view.search;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.caleydo.core.id.IDType;

/**
 * one row within one category specific table
 *
 * @author Samuel Gratzl
 *
 */
public final class ResultRow {

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

	public Object getPrimaryId() {
		return pid;
	}


	public boolean has(IDType idType) {
		return values.containsKey(idType);
	}

	/**
	 * @return the primary, see {@link #primary}
	 */
	public IDType getPrimary() {
		return primary;
	}

	public Object get(IDType idType) {
		return values.get(idType);
	}

	public boolean wasFound(IDType type) {
		return found.contains(type);
	}

}
