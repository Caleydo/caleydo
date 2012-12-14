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
package org.caleydo.view.tourguide.data.compute;

import java.util.HashMap;
import java.util.Map;

import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.IIDTypeMapper;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

/**
 * @author Samuel Gratzl
 *
 */
public class CachedIDTypeMapper {

	private final Table<IDType, IDType, IIDTypeMapper<Integer, Integer>> mappers = HashBasedTable.create();
	private final Table<IDType, IDType, Predicate<Integer>> predicates = HashBasedTable.create();

	public Predicate<Integer> in(final IDType source, final IDType target) {
		if (source == target)
			return Predicates.alwaysTrue();
		// find way
		Predicate<Integer> mapper = predicates.get(source, target);
		if (mapper == null) {
			final Map<Integer, Boolean> cache = new HashMap<>();
			mapper = new Predicate<Integer>() {
				@Override
				public boolean apply(Integer sourceId) {
					if (cache.containsKey(sourceId)) {
						return cache.get(sourceId);
					}
					IIDTypeMapper<Integer, Integer> mapper = get(source, target);
					if (mapper == null)
						return false;
					boolean r = mapper.isMapAble(sourceId);
					cache.put(sourceId, r);
					return r;
				}
			};
			predicates.put(source, target, mapper);
		}
		return mapper;
	}

	public IIDTypeMapper<Integer, Integer> get(IDType source, IDType target) {
		// find way
		IIDTypeMapper<Integer, Integer> mapper = mappers.get(source, target);
		if (mapper == null) {
			mapper = IDMappingManagerRegistry.get().getIDMappingManager(target.getIDCategory())
					.getIDTypeMapper(source, target);
			mappers.put(source, target, mapper);
		}
		return mapper;
	}
}
