package org.caleydo.view.tourguide.internal.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.datadomain.pathway.manager.EPathwayDatabaseType;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.view.tourguide.api.adapter.ATourGuideDataMode;
import org.caleydo.view.tourguide.api.model.ADataDomainQuery;
import org.caleydo.view.tourguide.api.model.AScoreRow;
import org.caleydo.view.tourguide.api.model.PathwayDataDomainQuery;
import org.caleydo.view.tourguide.api.model.PathwayPerspectiveRow;
import org.caleydo.view.tourguide.internal.view.col.PathwayLinkRankColumnModel;
import org.caleydo.view.tourguide.internal.view.col.SizeRankColumnModel;
import org.caleydo.vis.lineup.model.CategoricalRankColumnModel;
import org.caleydo.vis.lineup.model.GroupRankColumnModel;
import org.caleydo.vis.lineup.model.IRow;
import org.caleydo.vis.lineup.model.RankTableModel;
import org.caleydo.vis.lineup.model.StringRankColumnModel;

import com.google.common.base.Function;

/**
 * @author Samuel Gratzl
 *
 */
public class PathwayDataMode extends ATourGuideDataMode {

	@Override
	public Iterable<? extends ADataDomainQuery> createDataDomainQueries() {
		PathwayDataDomain dd = DataDomainManager.get().getDataDomainsByType(PathwayDataDomain.class).get(0);
		Collection<ADataDomainQuery> r = new ArrayList<ADataDomainQuery>();
		for (EPathwayDatabaseType type : EPathwayDatabaseType.values()) {
			if (PathwayManager.get().hasPathways(type))
				r.add(new PathwayDataDomainQuery(dd, type));
		}
		return r;
	}

	@Override
	public Collection<? extends IDataDomain> getAllDataDomains() {
		return DataDomainManager.get().getDataDomainsByType(PathwayDataDomain.class);
	}

	@Override
	public boolean apply(IDataDomain input) {
		return input instanceof PathwayDataDomain;
	}


	@Override
	public void addDefaultColumns(RankTableModel table) {
		final StringRankColumnModel base = new StringRankColumnModel(GLRenderers.drawText("Pathway"),
				StringRankColumnModel.DEFAULT);
		table.add(base);
		base.setWidth(150);
		base.orderByMe();

		Map<EPathwayDatabaseType, String> metaData = new EnumMap<>(EPathwayDatabaseType.class);
		for(EPathwayDatabaseType type : EPathwayDatabaseType.values()) {
			metaData.put(type, type.getName());
		}

		GroupRankColumnModel group = new GroupRankColumnModel("Metrics", Color.GRAY, new Color(0.95f, .95f, .95f));
		table.add(group);
		group.add(new CategoricalRankColumnModel<>(GLRenderers.drawText("Database"),
				new Function<IRow, EPathwayDatabaseType>() {
			@Override
			public EPathwayDatabaseType apply(IRow in) {
				PathwayPerspectiveRow r = (PathwayPerspectiveRow)in;
				return r.getType();
			}
				}, metaData));

		// table.add(new StringRankColumnModel(GLRenderers.drawText("Description"), new Function<IRow, String>() {
		// @Override
		// public String apply(IRow in) {
		// PathwayPerspectiveRow r = (PathwayPerspectiveRow) in;
		// return r.getPathway().getTitle();
		// }
		// }).setCollapsed(true));

		group.add(new SizeRankColumnModel("#Genes", new Function<IRow, Integer>() {
			@Override
			public Integer apply(IRow in) {
				int s = ((AScoreRow) in).size();

				return s;
			}
		}).setWidth(75));

		group.add(new PathwayLinkRankColumnModel(GLRenderers.drawText("Pathway ID", VAlign.CENTER), Color.GRAY,
				new Color(.95f, .95f, .95f)));
	}

	@Override
	public Iterable<? extends ADataDomainQuery> createDataDomainQuery(IDataDomain dd) {
		if (dd instanceof PathwayDataDomain)
			return createDataDomainQueries();
		else
			return Collections.emptyList();
	}
}
