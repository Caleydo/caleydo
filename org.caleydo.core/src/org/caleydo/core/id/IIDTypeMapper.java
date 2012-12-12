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
package org.caleydo.core.id;

import java.util.Collection;
import java.util.Set;

import com.google.common.base.Function;

/**
 * subobject of an {@link IDMappingManager}, with a specific source and target id type
 *
 * @author Samuel Gratzl
 *
 */
public interface IIDTypeMapper<K, V> extends Function<K, Set<V>> {
	public IDType getSource();

	public IDType getTarget();

	/**
	 * maps the given set of ids at once
	 *
	 * @param sourceIds
	 * @return a set with all mapped id, that were able to be mapped, e.g. if empty nothing were able to be mapped
	 */
	public Set<V> apply(Collection<K> sourceIds);

	/**
	 * predicate whether the current id can be mapped to the target type
	 *
	 * @param sourceId
	 * @return
	 */
	public boolean isMapAble(K sourceId);
}
