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
package org.caleydo.view.tourguide.api.query;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.datadomain.ADataDomain;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.DataDomainOracle;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.DimensionPerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.execution.SafeCallable;
import org.caleydo.view.tourguide.api.query.filter.CompareDomainFilter;
import org.caleydo.view.tourguide.api.query.filter.CompositeDataDomainFilter;
import org.caleydo.view.tourguide.api.query.filter.DefaultStratificationDomainFilter;
import org.caleydo.view.tourguide.api.query.filter.EStringCompareOperator;
import org.caleydo.view.tourguide.api.query.filter.EmptyGroupFilter;
import org.caleydo.view.tourguide.api.query.filter.SpecificDataDomainFilter;
import org.caleydo.view.tourguide.spi.query.filter.IDataDomainFilter;

import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/**
 * @author Samuel Gratzl
 *
 */
public class DataDomainQuery implements SafeCallable<Collection<TablePerspective>>,
		Function<Collection<TablePerspective>, Multimap<TablePerspective, Group>> {
	public static final String PROP_FILTER = "filter";
	public static final String PROP_SELECTION = "selection";

	private PropertyChangeSupport listeners = new PropertyChangeSupport(this);

	private Set<ATableBasedDataDomain> selection = new HashSet<>();
	private CompositeDataDomainFilter filter = new CompositeDataDomainFilter();

	private WeakReference<Collection<TablePerspective>> cache = null;

	public DataDomainQuery() {
		filter.add(new EmptyGroupFilter());
		filter.add(new DefaultStratificationDomainFilter());
		// create an intelligent default filter
		filter.addAll(createDefaultFilters());
	}

	/**
	 * intelligent guess of good default filters
	 *
	 * @return
	 */
	private static Collection<IDataDomainFilter> createDefaultFilters() {
		Collection<IDataDomainFilter> filter = new ArrayList<>();
		for (ATableBasedDataDomain dataDomain : DataDomainManager.get().getDataDomainsByType(
				ATableBasedDataDomain.class)) {
			SpecificDataDomainFilter f = new SpecificDataDomainFilter(dataDomain);
			if (dataDomain.getLabel().equalsIgnoreCase("Mutations"))
				f.add(new CompareDomainFilter(EStringCompareOperator.EQUAL_IGNORECASE, "Mutated", false));
			else if (dataDomain.getLabel().contains("Copy"))
				f.add(new CompareDomainFilter(EStringCompareOperator.NOT_EQUAL_IGNORECASE, "Normal", false));
			filter.add(f);
		}
		return filter;
	}

	public Collection<ATableBasedDataDomain> getSelection() {
		return Collections.unmodifiableCollection(selection);
	}

	public void addFilter(IDataDomainFilter filter) {
		this.filter.add(filter);
		listeners.fireIndexedPropertyChange(PROP_FILTER, this.filter.size() - 1, null, filter);
	}

	public void updateFilter(IDataDomainFilter filter) {
		if (!this.filter.contains(filter))
			return;
		this.cache = null;
		listeners.fireIndexedPropertyChange(PROP_FILTER, this.filter.size() - 1, null, filter);
	}

	public void removeFilter(IDataDomainFilter filter) {
		if (!this.filter.contains(filter))
			return;

		this.filter.remove(filter);
		this.cache = null;
		listeners.fireIndexedPropertyChange(PROP_FILTER, this.filter.size(), filter, null);
	}

	public Collection<IDataDomainFilter> getFilter() {
		return Collections.unmodifiableSet(filter);
	}

	@Override
	public Multimap<TablePerspective, Group> apply(Collection<TablePerspective> stratifications) {
		Multimap<TablePerspective, Group> r = ArrayListMultimap.create();
		for (TablePerspective strat : stratifications) {
			for (Group group : strat.getRecordPerspective().getVirtualArray().getGroupList()) {
				if (group == null || !filter.apply(Pair.make(strat, group)))
					continue;
				r.put(strat, group);
			}
		}
		return r;
	}

	@Override
	public Collection<TablePerspective> call() {
		Collection<TablePerspective> c = cache != null ? cache.get() : null;
		if (c != null)
			return c;
		Collection<TablePerspective> stratifications = new ArrayList<TablePerspective>();
		for (ATableBasedDataDomain dataDomain : selection) {
			stratifications.addAll(getStratifications(dataDomain));
		}
		cache = new WeakReference<>(stratifications);
		return stratifications;
	}

	public Collection<TablePerspective> getStratifications(ATableBasedDataDomain dataDomain) {
		if (DataDomainOracle.isCategoricalDataDomain(dataDomain)) {
			List<TablePerspective> result = Lists.newArrayList();
			for (TablePerspective per : dataDomain.getAllTablePerspectives()) {
				if (filter.apply(Pair.make(per, (Group) null)))
					result.add(per);
			}
			return result;
		} else {
			// Take the first non ungrouped dimension perspective
			String dimensionPerspectiveID = null;
			for (String tmpDimensionPerspectiveID : dataDomain.getDimensionPerspectiveIDs()) {
				DimensionPerspective per = dataDomain.getTable().getDimensionPerspective(tmpDimensionPerspectiveID);
				if (isUngrouped(per))
					continue;
				dimensionPerspectiveID = tmpDimensionPerspectiveID;
			}

			Set<String> rowPerspectiveIDs = dataDomain.getRecordPerspectiveIDs();

			// we ignore stratifications with only one group, which is the ungrouped default
			if (rowPerspectiveIDs.size() == 1)
				return Collections.emptyList();

			Collection<TablePerspective> stratifications = new ArrayList<>(rowPerspectiveIDs.size());
			for (String rowPerspectiveID : rowPerspectiveIDs) {
				boolean existsAlready = dataDomain.hasTablePerspective(rowPerspectiveID, dimensionPerspectiveID);

				TablePerspective newTablePerspective = dataDomain.getTablePerspective(rowPerspectiveID,
						dimensionPerspectiveID);

				// We do not want to overwrite the state of already existing
				// public table perspectives.
				if (!existsAlready)
					newTablePerspective.setPrivate(true);

				if (!filter.apply(Pair.make(newTablePerspective, (Group) null)))
					continue;

				stratifications.add(newTablePerspective);
			}
			return stratifications;
		}
	}

	private static boolean isUngrouped(DimensionPerspective per) {
		return per.getLabel().contains("Ungrouped");
	}

	public static List<ATableBasedDataDomain> allDataDomains() {
		List<ATableBasedDataDomain> dataDomains = new ArrayList<>(DataDomainManager.get().getDataDomainsByType(
				ATableBasedDataDomain.class));

		for (Iterator<ATableBasedDataDomain> it = dataDomains.iterator(); it.hasNext();)
			if (DataDomainOracle.isClinical(it.next()))
				it.remove();

		// Sort data domains alphabetically
		Collections.sort(dataDomains, new Comparator<ADataDomain>() {
			@Override
			public int compare(ADataDomain dd1, ADataDomain dd2) {
				return dd1.getLabel().compareTo(dd2.getLabel());
			}
		});
		return dataDomains;
	}

	/**
	 * @param listener
	 * @see java.beans.PropertyChangeSupport#addPropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(listener);
	}

	/**
	 * @param listener
	 * @see java.beans.PropertyChangeSupport#removePropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		listeners.removePropertyChangeListener(listener);
	}

	/**
	 * @param propertyName
	 * @param listener
	 * @see java.beans.PropertyChangeSupport#addPropertyChangeListener(java.lang.String,
	 *      java.beans.PropertyChangeListener)
	 */
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(propertyName, listener);
	}

	/**
	 * @param propertyName
	 * @param listener
	 * @see java.beans.PropertyChangeSupport#removePropertyChangeListener(java.lang.String,
	 *      java.beans.PropertyChangeListener)
	 */
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		listeners.removePropertyChangeListener(propertyName, listener);
	}

	public void addSelection(ATableBasedDataDomain dataDomain) {
		if (!selection.add(dataDomain))
			return;
		cache = null;
		DataDomainOracle.initDataDomain(dataDomain);
		listeners.fireIndexedPropertyChange(PROP_SELECTION, selection.size() - 1, null, dataDomain);
	}

	public void removeSelection(ATableBasedDataDomain dataDomain) {
		cache = null;
		if (!selection.remove(dataDomain))
			return;
		listeners.fireIndexedPropertyChange(PROP_SELECTION, selection.size(), dataDomain, null);
	}


}
