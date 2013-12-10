/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.idbrowser.internal.ui;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.IIDTypeMapper;
import org.caleydo.view.idbrowser.internal.model.PrimaryIDRow;
import org.caleydo.vis.lineup.model.ARow;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;

/**
 * @author Samuel Gratzl
 *
 */
public class IDCategoryQuery extends ACategoryQuery {
	/**
	 *
	 */
	public IDCategoryQuery(IDCategory category) {
		super(category, category.getCategoryName(), category.getPrimaryMappingType());
	}

	@Override
	protected Set<IDType> findIDTypes(IDCategory category) {
		Set<IDType> idTypes = new HashSet<>();
		idTypes.addAll(category.getPublicIdTypes());
		idTypes.add(category.getHumanReadableIDType());
		idTypes.addAll(findReleveantDataIDTypes());
		return idTypes;
	}

	@Override
	protected Collection<ARow> createEntries(IDMappingManager mappingManager,
			final Map<IDType, IIDTypeMapper<Object, Object>> mappings) {
		Set<?> ids = mappingManager.getAllMappedIDs(primary);
		return Collections2.transform(ids, new Function<Object, ARow>() {
			@Override
			public ARow apply(Object input) {
				assert input != null;
				ImmutableMap.Builder<IDType, Set<Object>> b = ImmutableMap.builder();
				for (Map.Entry<IDType, IIDTypeMapper<Object, Object>> entry : mappings.entrySet()) {
					Set<Object> m = entry.getValue().apply(input);
					if (m != null)
						b.put(entry.getKey(), m);
				}
				return new PrimaryIDRow(category, input, b.build());
			}
		});
	}
}
