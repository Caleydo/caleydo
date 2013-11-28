/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.entourage;

import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.view.entourage.datamapping.DataMappingState;
import org.caleydo.view.tourguide.api.model.ADataDomainQuery;
import org.caleydo.view.tourguide.api.model.AScoreRow;
import org.caleydo.view.tourguide.api.model.ASingleIDDataDomainQuery;
import org.caleydo.view.tourguide.api.model.ITablePerspectiveScoreRow;
import org.caleydo.view.tourguide.api.model.InhomogenousDataDomainQuery;
import org.caleydo.view.tourguide.api.vis.ITourGuideView;
import org.caleydo.view.tourguide.entourage.model.CheckColumnModel;
import org.caleydo.view.tourguide.spi.adapter.ITourGuideDataMode;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.caleydo.vis.lineup.model.IRow;
import org.caleydo.vis.lineup.model.RankTableModel;

/**
 * @author Samuel Gratzl
 *
 */
public class EntourageNonGeneticAdapter extends AEntourageAdapter implements PropertyChangeListener {
	private final NonGeneticDataMode mode = new NonGeneticDataMode();

	private final CheckColumnModel check = new CheckColumnModel();

	public EntourageNonGeneticAdapter() {
		super();

		check.addPropertyChangeListener(CheckColumnModel.PROP_CHECKED, this);
	}

	@Override
	public String getSecondaryID() {
		return EntourageNonGeneticAdapterFactory.SECONDARY_ID;
	}

	@Override
	public String getPartName() {
		return "Non-Genetic";
	}

	@Override
	public void setup(ITourGuideView vis, GLElementContainer lineUp) {
		super.setup(vis, lineUp);

		if (isBound2View())
			loadViewState();
	}

	@Override
	public void addDefaultColumns(RankTableModel table) {
		asMode().addDefaultColumns(table);
		table.add(1, check);
		table.addPropertyChangeListener(RankTableModel.PROP_SELECTED_ROW, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				IRow new_ = (IRow) evt.getNewValue();
				if (new_ != null)
					check.set(new_, !check.is(new_));
			}
		});
	}

	@Override
	public ITourGuideDataMode asMode() {
		return mode;
	}

	@Override
	public boolean filterBoundView(ADataDomainQuery query) {
		if (entourage == null)
			return true;
		final IDCategory target = entourage.getDataMappingState().getExperimentalDataIDCategory();
		// filter all not experimental id categories
		if (query instanceof InhomogenousDataDomainQuery) {
			return ((ATableBasedDataDomain) query.getDataDomain()).getRecordIDCategory().equals(target);
		}
		if (query instanceof ASingleIDDataDomainQuery) {
			IDType having = ((ASingleIDDataDomainQuery) query).getSingleIDType();
			IDType opposite = ((ATableBasedDataDomain) query.getDataDomain()).getOppositeIDType(having);
			return target.isOfCategory(opposite);
		}
		return false;
	}

	@Override
	protected void loadViewState() {
		if (entourage == null || vis == null)
			return;
		List<TablePerspective> visible = entourage.getDataMappingState().getContextualTablePerspectives();
		Set<IDataDomain> visibleDataDomains = new HashSet<>();
		for(TablePerspective p : visible)
			visibleDataDomains.add(p.getDataDomain());

		check.removePropertyChangeListener(CheckColumnModel.PROP_CHECKED, this);
		check.set(false);
		boolean hasOne = false;
		for(ADataDomainQuery query : vis.getQueries()) {
			if (!query.isEnabled())
				continue;
			if (visibleDataDomains.contains(query.getDataDomain())) {
				query.setActive(true);
				hasOne = true;
				for (AScoreRow r : query.getOrCreate()) {
					for(TablePerspective p : visible)
						if (r.is(p)) {
							vis.setSelection(r);
							check.set(r, true);
							break;
						}
				}
			} else
				query.setActive(false);
		}
		if (!hasOne) {
			// just select the first one
			for (ADataDomainQuery query : vis.getQueries()) {
				if (!query.isEnabled())
					continue;
				query.setActive(true);
				break;
			}
		}
		check.addPropertyChangeListener(CheckColumnModel.PROP_CHECKED, this);
	}

	@Override
	public void cleanup() {
		super.cleanup();
	}

	@Override
	public boolean isPreviewing(AScoreRow row) {
		return false; // just a single selection
	}

	@Override
	public boolean isVisible(AScoreRow row) {
		return check.is(row);
	}

	@Override
	public void update(AScoreRow old, AScoreRow new_, Collection<IScore> visibleScores, IScore sortedByScore) {
		// nothing todo will be done via the checked stuff
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (entourage == null)
			return;
		assert evt.getPropertyName().equals(CheckColumnModel.PROP_CHECKED);
		final IRow new_ = check.getTable().getDataItem((((IndexedPropertyChangeEvent) evt)).getIndex());
		final boolean selected = (Boolean) evt.getNewValue();

		assert new_ == null || new_ instanceof ITablePerspectiveScoreRow;

		DataMappingState dmState = entourage.getDataMappingState();
		TablePerspective tp = ((ITablePerspectiveScoreRow) new_).asTablePerspective();
		ATableBasedDataDomain dataDomain = tp.getDataDomain();

		final IDCategory target = dmState.getExperimentalDataIDCategory();

		for(Perspective p : Arrays.asList(tp.getRecordPerspective(),tp.getDimensionPerspective())) {
			if (target.isOfCategory(p.getIdType()))
				continue;
			if (selected)
				dmState.addContextualTablePerspective(dataDomain, p);
			else
				dmState.removeContextualTablePerspective(dataDomain, p);
		}
	}

	@Override
	public boolean canShowPreviews() {
		return true;
	}
}
