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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.DataDomainOracle;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.ARecordPerspective;
import org.caleydo.core.data.perspective.variable.DimensionPerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.execution.SafeCallable;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.view.tourguide.api.query.filter.CompareDomainFilter;
import org.caleydo.view.tourguide.api.query.filter.CompositeDataDomainFilter;
import org.caleydo.view.tourguide.api.query.filter.DataDomainFilters;
import org.caleydo.view.tourguide.api.query.filter.EStringCompareOperator;
import org.caleydo.view.tourguide.api.query.filter.SpecificDataDomainFilter;
import org.caleydo.view.tourguide.api.util.MappedPropertyChangeEvent;
import org.caleydo.view.tourguide.spi.query.filter.IDataDomainFilter;

import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/**
 * @author Samuel Gratzl
 *
 */
public class DataDomainQuery implements SafeCallable<Collection<Pair<ARecordPerspective, TablePerspective>>>,
		Function<Collection<ARecordPerspective>, Multimap<ARecordPerspective, Group>>, Cloneable {
	public static final String PROP_FILTER = "filter";
	public static final String PROP_SELECTION = "selection";
	public static final String PROP_DIMENSION_SELECTION = "dimensionSelection";
	public static final String PROP_MODE = "mode";

	private PropertyChangeSupport listeners = new PropertyChangeSupport(this);

	/**
	 * the currently selected data domains
	 */
	private Set<IDataDomain> selection = new HashSet<>();
	/**
	 * filters that select the stratification and groups
	 */
	private CompositeDataDomainFilter filter = new CompositeDataDomainFilter();

	/**
	 * the current mode
	 */
	private EDataDomainQueryMode mode = EDataDomainQueryMode.TABLE_BASED;

	// cache
	private WeakReference<Collection<Pair<ARecordPerspective, TablePerspective>>> cache = null;

	/**
	 * contains the selected dimension perspective to use for a given datadomain
	 */
	private final Map<IDataDomain, DimensionPerspective> dimensionSelection = new HashMap<>();

	public DataDomainQuery() {
		this(true);
	}

	private DataDomainQuery(boolean initFilter) {
		if (initFilter) {
			filter.add(DataDomainFilters.EMPTY_GROUP);
			filter.add(DataDomainFilters.DEFAULT_GROUP);
			// create an intelligent default filter
			filter.addAll(createDefaultFilters());
		}
	}

	@Override
	protected DataDomainQuery clone() {
		DataDomainQuery clone = new DataDomainQuery(false);
		clone.selection.addAll(this.selection);
		clone.filter = this.filter.clone();
		clone.mode = this.mode;
		clone.dimensionSelection.putAll(this.dimensionSelection);

		return clone;
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

	public Collection<IDataDomain> getSelection() {
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

	/**
	 * @return the mode, see {@link #mode}
	 */
	public EDataDomainQueryMode getMode() {
		return mode;
	}

	/**
	 * @param mode
	 *            setter, see {@link mode}
	 */
	public void setMode(EDataDomainQueryMode mode) {
		if (this.mode == mode)
			return;
		for (IDataDomain sel : Lists.newArrayList(this.selection))
			if (!mode.isCompatible(sel))
				removeSelection(sel);
		cache = null;
		listeners.firePropertyChange(PROP_MODE, this.mode, this.mode = mode);
	}

	@Override
	public Multimap<ARecordPerspective, Group> apply(Collection<ARecordPerspective> stratifications) {
		Multimap<ARecordPerspective, Group> r = ArrayListMultimap.create();
		for (ARecordPerspective strat : stratifications) {
			for (Group group : strat.getVirtualArray().getGroupList()) {
				if (group == null || !filter.apply(Pair.make(strat, group)))
					continue;
				r.put(strat, group);
			}
		}
		return r;
	}

	@Override
	public Collection<Pair<ARecordPerspective, TablePerspective>> call() {
		Collection<Pair<ARecordPerspective, TablePerspective>> c = cache != null ? cache.get() : null;
		if (c != null)
			return c;
		Collection<Pair<ARecordPerspective, TablePerspective>> stratifications = new ArrayList<>();
		for (IDataDomain dataDomain : selection) {
			stratifications.addAll(getStratifications(dataDomain));
		}
		cache = new WeakReference<>(stratifications);
		return stratifications;
	}

	public Collection<Pair<ARecordPerspective, TablePerspective>> getStratifications(IDataDomain dataDomain) {
		if (EDataDomainQueryMode.TABLE_BASED.isCompatible(dataDomain))
			return Collections2.transform(getTableBasedStratifications((ATableBasedDataDomain) dataDomain), toPair);
		else if (EDataDomainQueryMode.GENE_SET.isCompatible(dataDomain))
			return Collections2.transform(getGeneSetStratifications(dataDomain), toDummyPair);
		else
			return Collections.emptyList();
	}

	public Collection<ARecordPerspective> getJustStratifications(IDataDomain dataDomain) {
		if (EDataDomainQueryMode.TABLE_BASED.isCompatible(dataDomain))
			return Collections2.transform(getTableBasedStratifications((ATableBasedDataDomain) dataDomain), toRecord);
		else if (EDataDomainQueryMode.GENE_SET.isCompatible(dataDomain))
			return getGeneSetStratifications(dataDomain);
		else
			return Collections.emptyList();
	}

	public Collection<TablePerspective> getPerspectives(IDataDomain dataDomain) {
		if (EDataDomainQueryMode.TABLE_BASED.isCompatible(dataDomain))
			return getTableBasedStratifications((ATableBasedDataDomain) dataDomain);
		return Collections.emptyList();
	}

	private static final Function<TablePerspective, Pair<ARecordPerspective, TablePerspective>> toPair = new Function<TablePerspective, Pair<ARecordPerspective, TablePerspective>>() {
		@Override
		public Pair<ARecordPerspective, TablePerspective> apply(TablePerspective in) {
			return Pair.make((ARecordPerspective) in.getRecordPerspective(), in);
		}
	};
	private static final Function<TablePerspective, ARecordPerspective> toRecord = new Function<TablePerspective, ARecordPerspective>() {
		@Override
		public ARecordPerspective apply(TablePerspective in) {
			return in.getRecordPerspective();
		}
	};
	private static final Function<ARecordPerspective, Pair<ARecordPerspective, TablePerspective>> toDummyPair = new Function<ARecordPerspective, Pair<ARecordPerspective, TablePerspective>>() {
		@Override
		public Pair<ARecordPerspective, TablePerspective> apply(ARecordPerspective in) {
			return Pair.make(in, null);
		}
	};

	private Collection<ARecordPerspective> getGeneSetStratifications(IDataDomain dataDomain) {
		if (dataDomain instanceof PathwayDataDomain) {
			PathwayDataDomain p = (PathwayDataDomain)dataDomain;
			List<ARecordPerspective> result = Lists.newArrayList();
			for (ARecordPerspective per : p.getPathwayRecordPerspectives()) {
				if (filter.apply(Pair.make(per, (Group) null)))
					result.add(per);
			}
			return result;
		}
		return Collections.emptyList();
	}

	private Collection<TablePerspective> getTableBasedStratifications(
			ATableBasedDataDomain dataDomain) {
		if (DataDomainOracle.isCategoricalDataDomain(dataDomain)) {
			List<TablePerspective> result = Lists.newArrayList();
			for (TablePerspective per : dataDomain.getAllTablePerspectives()) {
				if (filter.apply(Pair.make(per.getRecordPerspective(), (Group) null)))
					result.add(per);
			}
			return result;
		} else {
			String dimensionPerspectiveID = null;
			if (dimensionSelection.containsKey(dataDomain))
				dimensionPerspectiveID = dimensionSelection.get(dataDomain).getPerspectiveID();
			else
				dimensionPerspectiveID = getDefaultDimensionPerspective(dataDomain);

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

				if (!filter.apply(Pair.make(newTablePerspective.getRecordPerspective(), (Group) null)))
					continue;

				stratifications.add(newTablePerspective);
			}
			return stratifications;
		}
	}

	private String getDefaultDimensionPerspective(ATableBasedDataDomain dataDomain) {
		// Take the first non ungrouped dimension perspective
		return getPossibleDimensionPerspectives(dataDomain).iterator().next().getPerspectiveID();
	}

	/**
	 * returns a list of possible dimension perspectives that the user can choose from
	 *
	 * @param dataDomain
	 * @return
	 */
	public static Collection<DimensionPerspective> getPossibleDimensionPerspectives(IDataDomain dataDomain) {
		if (!(dataDomain instanceof ATableBasedDataDomain))
			return Collections.emptySet();
		if (DataDomainOracle.isCategoricalDataDomain(dataDomain))
			return Collections.emptySet();

		ATableBasedDataDomain dd = (ATableBasedDataDomain) dataDomain;

		List<DimensionPerspective> r = Lists.newArrayList();
		for (String tmpDimensionPerspectiveID : dd.getDimensionPerspectiveIDs()) {
			DimensionPerspective per = dd.getTable().getDimensionPerspective(tmpDimensionPerspectiveID);
			if (isUngrouped(per))
				continue;
			r.add(per);
		}
		return r;
	}

	private static boolean isUngrouped(DimensionPerspective per) {
		return per.getLabel().contains("Ungrouped");
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

	public void addSelection(IDataDomain dataDomain) {
		if (!selection.add(dataDomain))
			return;
		cache = null;
		initDataDomain(dataDomain);
		if (!mode.isCompatible(dataDomain)) { // auto switch mode
			for (EDataDomainQueryMode m : EDataDomainQueryMode.values()) {
				if (m.isCompatible(dataDomain)) {
					setMode(m);
				}
			}
		}
		listeners.fireIndexedPropertyChange(PROP_SELECTION, selection.size() - 1, null, dataDomain);
	}

	private void initDataDomain(IDataDomain dataDomain) {
		if (dataDomain instanceof ATableBasedDataDomain)
			DataDomainOracle.initDataDomain((ATableBasedDataDomain) dataDomain);
		else if (dataDomain instanceof PathwayDataDomain) {
			// nothing todo
		}
	}

	public void removeSelection(IDataDomain dataDomain) {
		cache = null;
		if (!selection.remove(dataDomain))
			return;
		listeners.fireIndexedPropertyChange(PROP_SELECTION, selection.size(), dataDomain, null);
	}


	public DimensionPerspective getDimensionSelection(IDataDomain dataDomain) {
		return dimensionSelection.get(dataDomain);
	}

	public void setDimensionSelection(IDataDomain dataDomain, DimensionPerspective d) {
		if (Objects.equals(dimensionSelection.get(dataDomain), d))
			return;
		listeners.firePropertyChange(new MappedPropertyChangeEvent(this, PROP_DIMENSION_SELECTION, dataDomain,
				this.dimensionSelection.put(dataDomain, d), d));
	}

}
