/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.compute;

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
 * id type mapper cache that avoid looking up all {@link IIDTypeMapper} again and again
 * 
 * @author Samuel Gratzl
 * 
 */
public class CachedIDTypeMapper {

	private final Table<IDType, IDType, IIDTypeMapper<Integer, Integer>> mappers = HashBasedTable.create();
	private final Table<IDType, IDType, Predicate<Integer>> predicates = HashBasedTable.create();

	/**
	 * computes a predicate that check if a given id is available in the target {@link IDType}
	 * 
	 * @param source
	 * @param target
	 * @return
	 */
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

	/**
	 * returns the {@link IIDTypeMapper} for a given pair
	 * 
	 * @param source
	 * @param target
	 * @return
	 */
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
