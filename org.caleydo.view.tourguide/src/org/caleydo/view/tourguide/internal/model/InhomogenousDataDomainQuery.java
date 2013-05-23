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
package org.caleydo.view.tourguide.internal.model;

import static org.caleydo.vis.rank.model.StringRankColumnModel.starToRegex;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.vis.rank.model.RankTableModel;

/**
 * @author Samuel Gratzl
 *
 */
public class InhomogenousDataDomainQuery extends ADataDomainQuery {
	private String matches = null;

	public InhomogenousDataDomainQuery(ATableBasedDataDomain dataDomain) {
		super(dataDomain);
	}

	@Override
	public ATableBasedDataDomain getDataDomain() {
		return (ATableBasedDataDomain) super.getDataDomain();
	}

	@Override
	public boolean apply(AScoreRow row) {
		assert row.getDataDomain() == dataDomain;
		if (matches == null)
			return true;
		return Pattern.matches(starToRegex(matches), row.getLabel());
	}

	@Override
	protected List<AScoreRow> getAll() {
		ATableBasedDataDomain d = (ATableBasedDataDomain) dataDomain;

		List<AScoreRow> r = new ArrayList<>();

		for (String dimPerspectiveID : d.getDimensionPerspectiveIDs()) {
			Perspective p = d.getTable().getDimensionPerspective(dimPerspectiveID);
			if (p.isDefault() || p.isPrivate())
				continue;
			r.add(new InhomogenousPerspectiveRow(p, this));
		}
		return r;
	}

	/**
	 * @param dimensionPerspectiveID
	 * @param p
	 * @return
	 */
	public TablePerspective asTablePerspective(Perspective p) {
		ATableBasedDataDomain d = getDataDomain();

		String rowPerspectiveID = d.getTable().getDefaultRecordPerspective().getPerspectiveID();

		boolean existsAlready = d.hasTablePerspective(rowPerspectiveID, p.getPerspectiveID());

		TablePerspective per = d.getTablePerspective(rowPerspectiveID, p.getPerspectiveID());

		// We do not want to overwrite the state of already existing
		// public table perspectives.
		if (!existsAlready)
			per.setPrivate(true);

		return per;
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

	@Override
	public void createSpecificColumns(RankTableModel table) {

	}

	@Override
	public void removeSpecificColumns(RankTableModel table) {

	}
}
