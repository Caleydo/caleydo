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
package org.caleydo.view.tourguide.internal.view.model;

import static org.caleydo.vis.rank.model.StringRankColumnModel.starToRegex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.internal.view.PerspectiveRow;

/**
 * @author Samuel Gratzl
 *
 */
public class TableDataDomainQuery extends ADataDomainQuery {
	public static final String PROP_DIMENSION_SELECTION = "dimensionSelection";

	private String matches = null;
	private Perspective dimensionSelection = null;

	public TableDataDomainQuery(EDataDomainQueryMode mode, ATableBasedDataDomain dataDomain) {
		super(mode, dataDomain);
	}

	@Override
	public ATableBasedDataDomain getDataDomain() {
		return (ATableBasedDataDomain) super.getDataDomain();
	}

	@Override
	public void cloneFrom(ADataDomainQuery clone, List<PerspectiveRow> allData) {
		super.cloneFrom(clone, allData);
		this.matches = ((TableDataDomainQuery) clone).matches;
		this.dimensionSelection = ((TableDataDomainQuery) clone).dimensionSelection;
	}

	@Override
	protected boolean include(Perspective perspective, Group group) {
		assert perspective.getDataDomain() == dataDomain;
		if (matches == null)
			return true;
		return Pattern.matches(starToRegex(matches), perspective.getLabel());
	}

	@Override
	protected Pair<List<PerspectiveRow>, List<PerspectiveRow>> getAll() {
		ATableBasedDataDomain d = (ATableBasedDataDomain) dataDomain;

		final String dimensionPerspectiveID = getDimensionPerspectiveID();

		List<PerspectiveRow> r = new ArrayList<>();
		List<PerspectiveRow> rg = new ArrayList<>();

		for (String rowPerspectiveID : d.getRecordPerspectiveIDs()) {
			Perspective p = d.getTable().getRecordPerspective(rowPerspectiveID);
			if (p.isDefault() || p.isPrivate())
				continue;

			TablePerspective per = asTablePerspective(dimensionPerspectiveID, p);

			r.add(new PerspectiveRow(p, null, per));
			for (Group g : p.getVirtualArray().getGroupList()) {
				rg.add(new PerspectiveRow(p, g, per));
			}
		}
		return Pair.make(r, rg);
	}

	private String getDimensionPerspectiveID() {
		String dimensionPerspectiveID = null;
		if (dimensionSelection != null)
			dimensionPerspectiveID = dimensionSelection.getPerspectiveID();
		else
			dimensionPerspectiveID = getDimensionPerspectives().iterator().next().getPerspectiveID();
		return dimensionPerspectiveID;
	}

	/**
	 * @param dimensionPerspectiveID
	 * @param p
	 * @return
	 */
	private TablePerspective asTablePerspective(String dimensionPerspectiveID, Perspective p) {
		ATableBasedDataDomain d = getDataDomain();

		boolean existsAlready = d.hasTablePerspective(p.getPerspectiveID(), dimensionPerspectiveID);

		TablePerspective per = d.getTablePerspective(p.getPerspectiveID(), dimensionPerspectiveID);

		// We do not want to overwrite the state of already existing
		// public table perspectives.
		if (!existsAlready)
			per.setPrivate(true);

		return per;
	}

	/**
	 * @return
	 */
	public Collection<Perspective> getDimensionPerspectives() {
		Collection<Perspective> r = new ArrayList<>();
		Table table = getDataDomain().getTable();
		for (String id : table.getDimensionPerspectiveIDs()) {
			r.add(table.getDimensionPerspective(id));
		}
		return r;
	}

	/**
	 * @return the dimensionSelection, see {@link #dimensionSelection}
	 */
	public Perspective getDimensionSelection() {
		return dimensionSelection;
	}

	public void setMatches(String matches) {
		if (Objects.equals(matches, this.matches))
			return;
		this.matches = matches;
		updateFilter();
	}

	/**
	 * @return the matches, see {@link #matches}
	 */
	public String getMatches() {
		return matches;
	}

	@Override
	public boolean hasFilter() {
		return this.matches != null;
	}

	/**
	 * @param d
	 */
	public void setDimensionSelection(Perspective d) {
		if (Objects.equals(dimensionSelection, d))
			d = null;
		if (Objects.equals(dimensionSelection, d))
			return;
		propertySupport.firePropertyChange(PROP_DIMENSION_SELECTION, dimensionSelection, dimensionSelection = d);
		updatePerspectiveRows();
	}

	private void updatePerspectiveRows() {
		if (!this.isInitialized())
			return;
		final String dimensionPerspectiveID = getDimensionPerspectiveID();
		for (PerspectiveRow row : this.getData()) {
			row.setPerspective(asTablePerspective(dimensionPerspectiveID, row.getStratification()));
		}
		// TODO update Stratomex Data
	}
}
