/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.idbrowser.internal.model;

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.caleydo.core.id.IDType;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.manager.EPathwayDatabaseType;
import org.caleydo.vis.lineup.model.ARow;
import org.caleydo.vis.lineup.model.IRow;

/**
 * a {@link IRow} for an idtype entry
 *
 * @author Samuel Gratzl
 *
 */
public class PathwayRow extends ARow implements IIDRow {
	private final PathwayGraph pathway;
	private final Map<IDType, Set<Object>> mappings;

	public PathwayRow(PathwayGraph pathway, Map<IDType, Set<Object>> mappings) {
		this.pathway = pathway;
		this.mappings = mappings;
	}

	/**
	 * @return the pathway, see {@link #pathway}
	 */
	public PathwayGraph getPathway() {
		return pathway;
	}

	/**
	 * @return the label, see {@link #label}
	 */
	@Override
	public String getLabel() {
		return pathway.getName();
	}

	public EPathwayDatabaseType getDatabase() {
		return pathway.getType();
	}

	public int size() {
		return pathway.vertexSet().size();
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
