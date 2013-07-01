/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.id;

import java.util.Set;

import com.google.common.base.Function;

/**
 * subobject of an {@link IDMappingManager}, with a specific source and target id type
 *
 * @author Samuel Gratzl
 *
 */
public interface IIDTypeMapper<K, V> extends Function<K, Set<V>> {
	IDType getSource();

	IDType getTarget();

	/**
	 * maps the given set of ids at once
	 *
	 * @param sourceIds
	 * @return a set with all mapped id, that were able to be mapped, e.g. if empty nothing were able to be mapped
	 */
	Set<V> apply(Iterable<K> sourceIds);

	/**
	 * predicate whether the current id can be mapped to the target type
	 *
	 * @param sourceId
	 * @return
	 */
	boolean isMapAble(K sourceId);
}
