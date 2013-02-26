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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.BitSet;
import java.util.List;
import java.util.Objects;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.internal.view.PerspectiveRow;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class ADataDomainQuery {
	public static final String PROP_ACTIVE = "active";
	public static final String PROP_MASK = "mask";

	protected final PropertyChangeSupport propertySupport = new PropertyChangeSupport(this);

	protected final IDataDomain dataDomain;
	protected final EDataDomainQueryMode mode;

	private int offset;
	private BitSet mask = null;
	private List<PerspectiveRow> data;
	private boolean active = false;

	public ADataDomainQuery(EDataDomainQueryMode mode, IDataDomain dataDomain) {
		this.dataDomain = dataDomain;
		this.mode = mode;
	}

	public void cloneFrom(ADataDomainQuery clone) {
		this.mask = clone.mask != null ? (BitSet) clone.mask.clone() : null;
		this.active = clone.active;
		if (clone.data != null) {
			// TODO
		}
	}

	public abstract boolean hasFilter();
	protected abstract boolean include(Perspective perspective, Group group);

	public abstract List<PerspectiveRow> getAll();

	/**
	 * @return the dataDomain, see {@link #dataDomain}
	 */
	public IDataDomain getDataDomain() {
		return dataDomain;
	}

	/**
	 * @return the mode, see {@link #mode}
	 */
	public EDataDomainQueryMode getMode() {
		return mode;
	}

	public boolean isInitialized() {
		return data != null;
	}

	public void setActive(boolean active) {
		if (this.active == active)
			return;
		propertySupport.firePropertyChange(PROP_ACTIVE, this.active, this.active = active);
	}

	public void setJustActive(boolean active) {
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

	public void init(int offset, List<PerspectiveRow> data) {
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
	public List<PerspectiveRow> getData() {
		return data;
	}

	public List<PerspectiveRow> getOrCreate() {
		if (isInitialized())
			return getData();
		data = getAll();
		return data;
	}

	/**
	 * @return the offset, see {@link #offset}
	 */
	public int getOffset() {
		return offset;
	}

	public int getSize() {
		return data == null ? 0 : data.size();
	}

	protected void updateFilter() {
		this.mask = null;
		if (!this.active)
			return;
		assert this.data != null;
		BitSet m = computeMask();
		refilter(m);
	}

	private BitSet computeMask() {
		BitSet m = new BitSet(offset + data.size());
		int i = 0;
		for (PerspectiveRow r : data) {
			m.set(offset + i++, include(r.getStratification(), r.getGroup()));
		}
		return m;
	}

	/**
	 * @return the mask, see {@link #mask}
	 */
	public BitSet getMask() {
		if (mask == null)
			mask = computeMask();
		return mask;
	}

	protected void refilter(BitSet mask) {
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
}
