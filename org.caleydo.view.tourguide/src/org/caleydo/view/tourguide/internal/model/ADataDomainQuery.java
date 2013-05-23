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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.BitSet;
import java.util.List;
import java.util.Objects;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.vis.rank.model.RankTableModel;

import com.google.common.base.Predicate;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class ADataDomainQuery implements Predicate<AScoreRow> {
	public static final String PROP_ACTIVE = "active";
	public static final String PROP_MASK = "mask";

	protected final PropertyChangeSupport propertySupport = new PropertyChangeSupport(this);

	protected final IDataDomain dataDomain;

	private int offset;
	private BitSet mask = null;
	private List<AScoreRow> data;
	private boolean active = false;

	public ADataDomainQuery(IDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	public abstract boolean hasFilter();

	/**
	 *
	 * @return pair with a list of the stratifications rows and one with the stratification, groups
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

	public final synchronized void init(int offset, List<AScoreRow> data) {
		this.data = data;
		if (this.offset != offset && this.mask != null) {
			this.offset = offset;
			this.mask = null;
		}
		this.offset = offset;
	}

	/**
	 * @return the data, see {@link #data}
	 */
	public final List<AScoreRow> getData() {
		return data;
	}

	public final synchronized List<AScoreRow> getOrCreate() {
		if (isInitialized())
			return getData();
		this.data = getAll();
		return data;
	}

	/**
	 * @return the offset, see {@link #offset}
	 */
	public final int getOffset() {
		return offset;
	}

	public final int getSize() {
		return data == null ? 0 : data.size();
	}

	protected final void updateFilter() {
		this.mask = null;
		if (!this.active)
			return;
		assert this.data != null;
		BitSet m = computeMask();
		refilter(m);
	}

	private BitSet computeMask() {
		BitSet m = new BitSet(offset + data.size());
		for (int i = 0; i < data.size(); ++i) {
			AScoreRow r = data.get(i);
			m.set(offset + i, apply(r));
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
		if (offset == 0)
			return shifted;
		BitSet r = new BitSet(data.size());
		for(int i = 0; i < data.size(); ++i) {
			r.set(i,shifted.get(offset+i));
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

	public abstract void createSpecificColumns(RankTableModel table);

	public abstract void removeSpecificColumns(RankTableModel table);

	/**
	 *
	 */
	public void onDataDomainUpdated() {
		if (!isInitialized()) // not yet used
			return;
		// we need to adapt stuff of our perspective rows -> mask exceptions
		// black list for removed stuff + white list for added staff
		// FIXME
	}
}
