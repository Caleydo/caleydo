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
package org.caleydo.view.tourguide.internal.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.base.ILabelProvider;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.view.tourguide.internal.compute.CachedIDTypeMapper;
import org.caleydo.view.tourguide.spi.score.IGroupScore;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.caleydo.view.tourguide.spi.score.IStratificationScore;
import org.caleydo.view.tourguide.v3.model.ARow;

/**
 * @author Samuel Gratzl
 *
 */
public final class PerspectiveRow extends ARow implements ILabelProvider, Cloneable {
	private final TablePerspective perspective;
	private final Perspective stratification;
	private final Group group;

	public PerspectiveRow(Perspective stratification, TablePerspective perspective) {
		this(stratification, null, perspective);
	}

	public PerspectiveRow(Perspective stratification, Group group, TablePerspective perspective) {
		this.stratification = stratification;
		this.perspective = perspective;
		this.group = group;
	}

	@Override
	public String getLabel() {
		String label = stratification.getLabel();
		if (group != null)
			label += ": " + group.getLabel();
		return label;
	}

	@Override
	public String getProviderName() {
		return stratification.getProviderName();
	}

	public IDataDomain getDataDomain() {
		return stratification.getDataDomain();
	}

	public Perspective getStratification() {
		return stratification;
	}

	public TablePerspective getPerspective() {
		return perspective;
	}

	private VirtualArray getVirtualArray() {
		return stratification.getVirtualArray();
	}

	private IDType getIdType() {
		return stratification.getIdType();
	}


	public Group getGroup() {
		return group;
	}

	/**
	 * returns the list of row ids that intersects all the relevant visible columns based on this stratifaction and
	 * group
	 *
	 * @param pair
	 *            containing the ids and the type in which the ids are
	 * @return
	 */
	public Pair<Collection<Integer>, IDType> getIntersection(Collection<IScore> visibleColumns) {

		// select nearest score
		Collection<IStratificationScore> relevant = filterRelevantColumns(visibleColumns);

		IDType target = getIdType();
		for (IStratificationScore elem : relevant) {
			IDType type = elem.getStratification().getIdType();
			if (!target.getIDCategory().equals(type.getIDCategory()))
				continue;
			if (!target.equals(type))
				target = target.getIDCategory().getPrimaryMappingType();
		}

		CachedIDTypeMapper mapper = new CachedIDTypeMapper();

		// compute the intersection of all
		IDType source = getIdType();

		VirtualArray va = getVirtualArray();
		Collection<Integer> ids = (group == null) ? va.getIDs() : va.getIDsOfGroup(group.getGroupIndex());

		if (!relevant.isEmpty()) {
			Collection<Integer> intersection = new ArrayList<>(mapper.get(source, target).apply(ids));
			for (IStratificationScore score : relevant) {
				va = score.getStratification().getVirtualArray();
				Group g = (score instanceof IGroupScore) ? ((IGroupScore) score).getGroup() : null;
				ids = (g == null) ? va.getIDs() : va.getIDsOfGroup(g.getGroupIndex());
				Set<Integer> mapped = mapper.get(score.getStratification().getIdType(), target).apply(ids);
				for (Iterator<Integer> it = intersection.iterator(); it.hasNext();) {
					if (!mapped.contains(it.next())) // not part of
						it.remove();
				}
			}
			ids = intersection;
		}
		return Pair.make(ids, target);
	}


	private Set<IStratificationScore> filterRelevantColumns(Collection<IScore> columns) {
		Set<IStratificationScore> relevant = new HashSet<>();
		for (IScore score : columns) {
			if (score instanceof IStratificationScore)
				relevant.add((IStratificationScore) score);
		}
		return relevant;
	}

	/**
	 * @return
	 */
	public int size() {
		if (getGroup() != null)
			return getGroup().getSize();
		if (getVirtualArray() != null)
			return getVirtualArray().size();
		return 0;
	}

	@Override
	public PerspectiveRow clone() {
		try {
			return (PerspectiveRow) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException(e);
		}
	}
}
