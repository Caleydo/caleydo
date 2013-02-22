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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.internal.view.PerspectiveRow;
import org.caleydo.view.tourguide.v3.model.RankTableModel;

/**
 * @author Samuel Gratzl
 *
 */
public class DataDomainQueries {
	private final RankTableModel table;
	private final BitSet mask = new BitSet();
	private final List<ADataDomainQuery> queries = new ArrayList<>();

	private final PropertyChangeListener listener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			switch (evt.getPropertyName()) {
			case ADataDomainQuery.PROP_ACTIVE:
				onActiveChanged((ADataDomainQuery) evt.getSource(), (boolean) evt.getNewValue());
				break;
			case ADataDomainQuery.PROP_MASK:
				updateMask();
			}
		}
	};

	public DataDomainQueries(RankTableModel table) {
		this.table = table;

		for (EDataDomainQueryMode mode : EDataDomainQueryMode.values()) {
			for (IDataDomain dd : mode.getAllDataDomains()) {
				final ADataDomainQuery q = createFor(dd);
				q.addPropertyChangeListener(ADataDomainQuery.PROP_ACTIVE, listener);
				q.addPropertyChangeListener(ADataDomainQuery.PROP_MASK, listener);
				queries.add(q);
			}
		}
	}

	/**
	 * @return the queries, see {@link #queries}
	 */
	public List<ADataDomainQuery> getQueries() {
		return Collections.unmodifiableList(queries);
	}

	private ADataDomainQuery createFor(IDataDomain dd) {
		if (DataSupportDefinitions.categoricalTables.apply(dd))
			return new CategoricalDataDomainQuery((ATableBasedDataDomain) dd);
		if (dd instanceof PathwayDataDomain)
			return new PathwayDataDomainQuery((PathwayDataDomain) dd);
		return new TableDataDomainQuery((ATableBasedDataDomain) dd);
	}

	// @ListenTo
	// private void onAddDataDomain(final NewDataDomainEvent event) {
	// IDataDomain dd = event.getDataDomain();
	//
	// int i = 0;
	// for (EDataDomainQueryMode mode : EDataDomainQueryMode.values()) {
	// if (mode.isCompatible(dd)) {
	// GLElementContainer c = (GLElementContainer) get(i);
	// c.add(createFor(dd).setCallback(callback));
	// c.setSize(-1, c.size() * 20);
	// break;
	// }
	// i++;
	// }
	// pack(false, true);
	// }
	//
	// @ListenTo
	// private void onRemoveDataDomain(final RemoveDataDomainEvent event) {
	// final String id = event.getEventSpace();
	// for (GLElement elem : this) {
	// GLElementContainer c = (GLElementContainer) elem;
	// for (GLElement b : c) {
	// if (Objects.equals(b.getLayoutDataAs(IDataDomain.class, null).getDataDomainID(), id)) {
	// c.remove(b);
	// break;
	// }
	// }
	// c.setSize(-1, c.size() * 20);
	// }
	// pack(false, true);
	// relayoutParent();
	// }

	@SuppressWarnings("unchecked")
	protected void onActiveChanged(ADataDomainQuery q, boolean active) {
		if (q.isInitialized()) {
			updateMask();
			return;
		}
		// init
		int offset = table.getDataSize();
		table.addData(q.getAll());
		// use sublists to save memory
		List<?> m = table.getData();
		q.setData(new CustomSubList<PerspectiveRow>((List<PerspectiveRow>) m, offset, m.size() - offset));
		updateMask();
		// FIXME compute data
	}

	private void updateMask() {
		this.mask.clear();
		for (ADataDomainQuery q : this.queries) {
			if (!q.isInitialized())
				continue;
			int offset = q.getOffset();
			int size = q.getSize();
			if (!q.isActive())
				this.mask.set(offset, offset + size, false);
			else {
				BitSet m = q.getMask();
				for (int i = 0; i < size; ++i)
					this.mask.set(i + offset, m.get(i));
			}
		}
		table.setDataMask(this.mask);
	}

}
