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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.data.collection.column.container.CategoryProperty;
import org.caleydo.core.data.collection.table.CategoricalTable;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainOracle;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.internal.view.PerspectiveRow;

/**
 * @author Samuel Gratzl
 *
 */
public class CategoricalDataDomainQuery extends ADataDomainQuery {
	private Set<CategoryProperty<?>> selected = new HashSet<>();

	public CategoricalDataDomainQuery(EDataDomainQueryMode mode, ATableBasedDataDomain dataDomain) {
		super(mode, dataDomain);
		assert (DataDomainOracle.isCategoricalDataDomain(dataDomain));
		this.selected.addAll(getCategories());
	}

	@Override
	public ATableBasedDataDomain getDataDomain() {
		return (ATableBasedDataDomain) super.getDataDomain();
	}

	@Override
	public void cloneFrom(ADataDomainQuery clone) {
		super.cloneFrom(clone);
		this.selected.clear();
		this.selected.addAll(((CategoricalDataDomainQuery) clone).selected);
	}

	@SuppressWarnings("unchecked")
	public List<CategoryProperty<?>> getCategories() {
		final CategoricalTable<?> table = (CategoricalTable<?>) getDataDomain().getTable();

		CategoricalClassDescription<?> cats = table.getCategoryDescriptions();
		List<?> tmp = cats.getCategoryProperties();
		return (List<CategoryProperty<?>>) tmp;
	}

	@Override
	public boolean include(Perspective perspective, Group group) {
		assert perspective.getDataDomain() == dataDomain;
		if (group == null)
			return true;
		for (CategoryProperty<?> s : selected) {
			if (Objects.equals(s.getCategoryName(), group.getLabel()))
				return true;
		}
		return false;
	}

	@Override
	public List<PerspectiveRow> getAll() {
		ATableBasedDataDomain d = (ATableBasedDataDomain) dataDomain;
		DataDomainOracle.initDataDomain(d);
		List<PerspectiveRow> r = new ArrayList<>();
		for (TablePerspective per : d.getAllTablePerspectives()) {
			Perspective p = per.getRecordPerspective();
			for (Group g : p.getVirtualArray().getGroupList()) {
				r.add(new PerspectiveRow(p, g, per));
			}
		}
		return r;
	}


	public void setSelection(Set<CategoryProperty<?>> selected) {
		if (Objects.equals(selected, this.selected))
			return;
		this.selected.clear();
		this.selected.addAll(selected);
		updateFilter();
	}

	/**
	 * @return the selected, see {@link #selected}
	 */
	public Set<CategoryProperty<?>> getSelected() {
		return selected;
	}

	@Override
	public boolean hasFilter() {
		return selected.size() < getCategories().size();
	}
}
