/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.idbrowser.internal.model;

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.vis.lineup.model.ARow;
import org.caleydo.vis.lineup.model.IRow;

/**
 * a {@link IRow} for an idtype entry
 *
 * @author Samuel Gratzl
 *
 */
public class PrimaryIDRow extends ARow implements IIDRow {
	private final IDCategory category;
	private final Object primary;
	private final Map<IDType, Set<Object>> mappings;

	public PrimaryIDRow(IDCategory category, Object primary, Map<IDType,Set<Object>> mappings) {
		this.category = category;
		this.primary = primary;
		this.mappings = mappings;
	}

	/**
	 * @return the label, see {@link #label}
	 */
	@Override
	public String getLabel() {
		return getAsString(category.getHumanReadableIDType(), primary.toString());
	}

	/**
	 * @return the category, see {@link #category}
	 */
	public IDCategory getCategory() {
		return category;
	}


	/**
	 * @return the primary, see {@link #primary}
	 */
	public Object getPrimary() {
		return primary;
	}

	public IDType getPrimaryIDType() {
		return category.getPrimaryMappingType();
	}

	/**
	 * @param idType
	 * @return
	 */
	@Override
	public Set<Object> get(IDType idType) {
		return mappings.get(idType);
	}

	/**
	 * @param idType
	 * @return
	 */
	@Override
	public String getAsString(IDType idType, String default_) {
		Set<Object> r = mappings.get(idType);
		if (r == null || r.isEmpty())
			return default_;
		if (r.size() == 1)
			return r.iterator().next().toString();
		return StringUtils.join(r, ", ");
	}
}
