/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.BitSet;
import java.util.List;
import java.util.Objects;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.contextmenu.ContextMenuCreator;
import org.caleydo.view.tourguide.internal.view.ui.ADataDomainElement;
import org.caleydo.vis.lineup.model.RankTableModel;

import com.google.common.base.Predicate;

/**
 * base model cass for data set selection on the left side
 *
 * @author Samuel Gratzl
 *
 */
public abstract class ADataDomainQuery implements Predicate<AScoreRow> {
	public static final String PROP_ACTIVE = "active";
	public static final String PROP_MASK = "mask";
	protected final PropertyChangeSupport propertySupport = new PropertyChangeSupport(this);

	protected final IDataDomain dataDomain;

	/**
	 * filter mask of the entries
	 */
	private BitSet mask = null;
	/**
	 * the data list
	 */
	protected OffsetList<AScoreRow> data = null;
	/**
	 * is the list currently visible
	 */
	private boolean active = false;

	public ADataDomainQuery(IDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	public abstract boolean hasFilter();

	/**
	 * whether filtering is possible
	 *
	 * @return
	 */
	public abstract boolean isFilteringPossible();

	/**
	 *
	 * @return a list of all filtered {@link AScoreRow} of this query
	 */
	protected abstract List<AScoreRow> getAll();

	/**
	 * @return the dataDomain, see {@link #dataDomain}
	 */
	public IDataDomain getDataDomain() {
		return dataDomain;
	}

	public final boolean isInitialized() {
		return data != null;
	}

	public final void setActive(boolean active) {
		if (this.active == active)
			return;
		propertySupport.firePropertyChange(PROP_ACTIVE, this.active, this.active = active);
	}

	public final void setJustActive(boolean active) {
		if (this.active == active)
			return;
		this.active = active;
	}

	/**
	 * @return the active, see {@link #active}
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * initialized this query using the given data
	 *
	 * @param offset
	 * @param data
	 */
	public final void init(int offset, List<AScoreRow> data) {
		this.data = new OffsetList<>(offset,data);
		this.mask = null;
	}

	public final void addData(int offset, List<AScoreRow> data) {
		if (this.data == null)
			init(offset, data);
		else {
			this.mask = null;
			this.data.addSubList(offset,data);
		}
	}

	/**
	 * optionally creates and returns the data
	 *
	 * @return
	 */
	public final synchronized List<AScoreRow> getOrCreate() {
		if (isInitialized()) {
			return this.data;
		}
		List<AScoreRow> all = getAll();
		this.data = new OffsetList<>(0, all);
		return all;
	}

	/**
	 * update the filter mask
	 */
	protected final void updateFilter() {
		this.mask = null;
		if (!this.active)
			return;
		assert this.data != null;
		BitSet m = computeMask();
		refilter(m);
	}

	private BitSet computeMask() {
		BitSet m = new BitSet(this.data.getMaxIndex());
		for(Pair<Integer,List<AScoreRow>> elem : this.data.subLists()) {
			int offset = elem.getFirst();
			List<AScoreRow> data = elem.getSecond();
			for (int i = 0; i < data.size(); ++i) {
				AScoreRow r = data.get(i);
				m.set(offset + i, r != null && apply(r));
			}
		}
		return m;
	}

	/**
	 * @return the mask, see {@link #mask}
	 */
	public final BitSet getMask() {
		if (mask == null)
			mask = computeMask();
		return mask;
	}

	/**
	 * returns the unshifted mask
	 *
	 * @return
	 */
	public BitSet getRawMask() {
		BitSet shifted = getMask();
		if (this.data.isDummy())
			return shifted;
		BitSet r = new BitSet(this.data.size());
		int j = 0;
		for (Pair<Integer, List<AScoreRow>> elem : this.data.subLists()) {
			int offset = elem.getFirst();
			List<AScoreRow> data = elem.getSecond();
			for (int i = 0; i < data.size(); ++i, ++j) {
				r.set(j, shifted.get(offset + i));
			}
		}
		return r;
	}

	protected final void refilter(BitSet mask) {
		if (Objects.equals(mask, this.mask))
			return;
		propertySupport.firePropertyChange(PROP_MASK, this.mask, this.mask = mask);
	}

	public final void addPropertyChangeListener(PropertyChangeListener listener) {
		propertySupport.addPropertyChangeListener(listener);
	}

	public final void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertySupport.addPropertyChangeListener(propertyName, listener);
	}

	public final void removePropertyChangeListener(PropertyChangeListener listener) {
		propertySupport.removePropertyChangeListener(listener);
	}

	public final void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertySupport.removePropertyChangeListener(propertyName, listener);
	}

	/**
	 * factory method for adding query specific metric columns
	 *
	 * @param table
	 */
	public abstract void createSpecificColumns(RankTableModel table);

	/**
	 * reverse of {@link #createSpecificColumns(RankTableModel)}
	 *
	 * @param table
	 */
	public abstract void removeSpecificColumns(RankTableModel table);

	public abstract void updateSpecificColumns(RankTableModel table);
	/**
	 *
	 * @return null if nothing changed, or else if the data were updated
	 */
	public abstract List<AScoreRow> onDataDomainUpdated();

	/**
	 * deletes all data
	 */
	public void cleanup() {
		if (!this.isInitialized()) {
			return;
		}
		// set every item in the data list to null for cleaning up the data
		for (int i = 0; i < this.data.size(); ++i)
			this.data.set(i, null);
	}

	public boolean addCustomDomainActions(ContextMenuCreator creator, ADataDomainElement ui) {
		return false;
	}
}
