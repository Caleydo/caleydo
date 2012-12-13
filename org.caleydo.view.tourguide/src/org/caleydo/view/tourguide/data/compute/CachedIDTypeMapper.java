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
import org.caleydo.core.util.collection.Pair;

import com.google.common.base.Predicate;

/**
 * @author Samuel Gratzl
 *
 */
public class CachedIDTypeMapper {

	private final Map<Pair<IDType, IDType>, IIDTypeMapper<Integer, Integer>> mappers = new HashMap<>();

	public Predicate<Integer> in(final IDType source, final IDType target) {
		final Map<Integer, Boolean> cache = new HashMap<>();
		return new Predicate<Integer>() {
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
	}

	public IIDTypeMapper<Integer, Integer> get(IDType source, IDType target) {
		// find way
		IIDTypeMapper<Integer, Integer> mapper = mappers.get(Pair.make(source, target));
		if (mapper == null) {
			mapper = IDMappingManagerRegistry.get().getIDMappingManager(target.getIDCategory())
					.getIDTypeMapper(source, target);
			mappers.put(Pair.make(source, target), mapper);
		}
		return mapper;
	}
}
