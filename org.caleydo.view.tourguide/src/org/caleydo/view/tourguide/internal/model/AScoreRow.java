/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.view.tourguide.internal.compute.CachedIDTypeMapper;
import org.caleydo.view.tourguide.spi.algorithm.IComputeElement;
import org.caleydo.view.tourguide.spi.score.IGroupScore;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.caleydo.view.tourguide.spi.score.IStratificationScore;
import org.caleydo.vis.rank.model.ARow;
import org.caleydo.vis.rank.model.IRow;

import com.google.common.base.Function;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class AScoreRow extends ARow implements ILabeled, Cloneable, IComputeElement {
	public static final Function<IRow, String> TO_DATADOMAIN = new Function<IRow, String>() {
		@Override
		public String apply(IRow in) {
			if (in == null || !(in instanceof AScoreRow))
				return null;
			AScoreRow r = ((AScoreRow) in);
			return r.getDataDomain().getLabel() + " " + r.getLabel();
		}
	};

	public abstract boolean is(TablePerspective tablePerspective);

	public abstract Collection<GroupInfo> getGroupInfos();

	/**
	 * returns the list of row ids that intersects all the relevant visible columns based on this stratifaction and
	 * group
	 *
	 * @param pair
	 *            containing the ids and the type in which the ids are
	 * @return
	 */
	public final Pair<Collection<Integer>, IDType> getIntersection(Collection<IScore> visibleColumns, Group group) {

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

		Collection<Integer> ids = of(group);

		if (!relevant.isEmpty()) {
			Collection<Integer> intersection = new ArrayList<>(mapper.get(source, target).apply(ids));
			for (IStratificationScore score : relevant) {
				VirtualArray va = score.getStratification().getVirtualArray();
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


	private final Set<IStratificationScore> filterRelevantColumns(Collection<IScore> columns) {
		Set<IStratificationScore> relevant = new HashSet<>();
		for (IScore score : columns) {
			if (score instanceof IStratificationScore)
				relevant.add((IStratificationScore) score);
		}
		return relevant;
	}

	@Override
	public AScoreRow clone() {
		try {
			return (AScoreRow) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException(e);
		}
	}

}
