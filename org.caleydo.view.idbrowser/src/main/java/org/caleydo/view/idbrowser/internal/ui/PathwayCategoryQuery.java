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

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.IIDTypeMapper;
import org.caleydo.core.util.base.Labels;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.datadomain.genetic.EGeneIDTypes;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.manager.EPathwayDatabaseType;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.view.idbrowser.internal.model.PathwayRow;
import org.caleydo.vis.lineup.model.ARow;
import org.caleydo.vis.lineup.model.CategoricalRankColumnModel;
import org.caleydo.vis.lineup.model.IRow;
import org.caleydo.vis.lineup.model.RankTableModel;
import org.caleydo.vis.lineup.model.StringRankColumnModel;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;

/**
 * @author Samuel Gratzl
 *
 */
public class PathwayCategoryQuery extends ACategoryQuery {
	private final static IDType vertex = IDType.getIDType(EGeneIDTypes.PATHWAY_VERTEX_REP.name());

	public PathwayCategoryQuery() {
		super(vertex.getIDCategory(), "Pathways", vertex);
	}

	@Override
	protected Set<IDType> findIDTypes(IDCategory category) {
		Set<IDType> idTypes = new HashSet<>();
		idTypes.addAll(findReleveantDataIDTypes());
		return idTypes;
	}

	@Override
	protected Collection<ARow> createEntries(final IDMappingManager mappingManager,
			final Map<IDType, IIDTypeMapper<Object, Object>> mappings) {
		// for all pathways
		return Collections2.transform(PathwayManager.get().getAllItems(), new Function<PathwayGraph, ARow>() {
			@Override
			public PathwayRow apply(PathwayGraph input) {
				assert input != null;
				ImmutableMap.Builder<IDType, Set<Object>> b = ImmutableMap.builder();
				Set<Object> vs = new HashSet<>();
				// for all vertex reps
				for (PathwayVertexRep r : input.vertexSet()) {
					vs.add(r.getID());
				}
				// map to others
				for (Map.Entry<IDType, IIDTypeMapper<Object, Object>> entry : mappings.entrySet()) {
					Set<Object> m = entry.getValue().apply(vs);
					if (m != null)
						b.put(entry.getKey(), m);
				}
				return new PathwayRow(input, b.build());
			}
		});
	}

	@Override
	public void addColumns(RankTableModel table) {
		table.add(new StringRankColumnModel(GLRenderers.drawText("Name", VAlign.CENTER),new Function<IRow, String>() {
			@Override
			public String apply(IRow input) {
				return ((PathwayRow)input).getName();
			}
		}));
		table.add(CategoricalRankColumnModel.createSimple(GLRenderers.drawText("Database", VAlign.CENTER),
				new Function<IRow, String>() {
					@Override
					public String apply(IRow input) {
						EPathwayDatabaseType database = ((PathwayRow) input).getDatabase();
						return database.getName();
					}
				}, Collections2.transform(EPathwayDatabaseType.values(), Labels.TO_LABEL)));
		super.addColumns(table);
	}

	@Override
	protected boolean expectMultiMapping(ATableBasedDataDomain d) {
		return true; // each gene will be mapped multiple times
	}
}
