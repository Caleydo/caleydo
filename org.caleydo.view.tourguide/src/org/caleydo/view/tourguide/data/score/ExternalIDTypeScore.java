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
package org.caleydo.view.tourguide.data.score;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.AVariablePerspective;
import org.caleydo.core.data.perspective.variable.DimensionPerspective;
import org.caleydo.core.data.perspective.variable.RecordPerspective;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.base.DefaultLabelProvider;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.view.tourguide.data.ScoringElement;
import org.caleydo.view.tourguide.data.serialize.IDTypeAdapter;
import org.caleydo.view.tourguide.data.serialize.ISerializeableScore;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.primitives.Floats;

/**
 * @author Samuel Gratzl
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class ExternalIDTypeScore extends DefaultLabelProvider implements ISerializeableScore {
	private static final Logger log = Logger.create(ExternalIDTypeScore.class);

	@XmlJavaTypeAdapter(IDTypeAdapter.class)
	private IDType idType;
	private ECombinedOperator operator;
	private boolean isRank;
	private Map<Integer, Float> scores = new HashMap<>();

	// cache my id type mapping results
	@XmlTransient
	private final Cache<Pair<IDType, Integer>, Optional<Integer>> mapping = CacheBuilder.newBuilder().maximumSize(1000)
			.build(new CacheLoader<Pair<IDType, Integer>, Optional<Integer>>() {
				@Override
				public Optional<Integer> load(Pair<IDType, Integer> arg0) {
					IDMappingManager m = IDMappingManagerRegistry.get().getIDMappingManager(idType);
					Set<Integer> s = m.getIDAsSet(idType, arg0.getFirst(), arg0.getSecond());
					if (s == null || s.isEmpty())
						return Optional.absent();
					return Optional.of(s.iterator().next());
				}
			});

	public ExternalIDTypeScore() {
		super("");
	}

	public ExternalIDTypeScore(String label, IDType idType, ECombinedOperator operator, boolean isRank,
			Map<Integer, Float> scores) {
		super(label);
		this.idType = idType;
		this.isRank = isRank;
		this.operator = operator;
		this.scores.putAll(scores);
	}

	@Override
	public final EScoreType getScoreType() {
		return isRank ? EScoreType.RANK : EScoreType.SCORE;
	}

	@Override
	public String getProviderName() {
		return "External";
	}

	private boolean isCompatible(IDType type) {
		return this.idType.getIDCategory().equals(type.getIDCategory());
	}

	@Override
	public float getScore(ScoringElement elem) {
		TablePerspective strat = elem.getStratification();
		Iterator<Integer> it;
		IDType target;

		final RecordPerspective recordPerspective = strat.getRecordPerspective();
		final DimensionPerspective dimensionPerspective = strat.getDimensionPerspective();


		if (isCompatible(dimensionPerspective.getIdType())) {
			target = dimensionPerspective.getIdType();
			DimensionVirtualArray va = dimensionPerspective.getVirtualArray();
			//if we have a group and the group reduces my virtual array use it
			if (isMyGroup(elem.getGroup(), dimensionPerspective))
				it = va.getIDsOfGroup(elem.getGroup().getGroupIndex()).iterator();
			else
				it = va.iterator();
		} else if (isCompatible(recordPerspective.getIdType())) {
			target = recordPerspective.getIdType();
			RecordVirtualArray va = recordPerspective.getVirtualArray();
			// if we have a group and the group reduces my virtual array use it
			if (isMyGroup(elem.getGroup(), recordPerspective))
				it = va.getIDsOfGroup(elem.getGroup().getGroupIndex()).iterator();
			else
				it = va.iterator();
		} else {
			// can't map to either dimension
			return Float.NaN;
		}

		Collection<Float> scores = new ArrayList<>();

		try {
			while (it.hasNext()) {
				Optional<Integer> my = mapping.get(Pair.make(target, it.next()));
				if (!my.isPresent())
					continue;
				Float s = this.scores.get(my.get());
				if (s == null)
					continue;
				scores.add(s);
			}
		} catch (ExecutionException e) {
			log.warn("can't get mapping value", e);
		}
		if (scores.isEmpty())
			return Float.NaN;
		if (scores.size() == 1)
			return scores.iterator().next().floatValue();
		return operator.combine(Floats.toArray(scores));
	}

	/**
	 * @param group
	 * @param dimensionPerspective
	 * @return
	 */
	private static boolean isMyGroup(Group group, AVariablePerspective<?, ?, ?, ?> perspective) {
		if (group == null)
			return false;
		for (Group g : perspective.getVirtualArray().getGroupList())
			if (g.equals(group))
				return true;
		return false;
		// return group.getPerspectiveID().equals(perspective.getPerspectiveID());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getLabel(), idType, isRank, operator);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExternalIDTypeScore other = (ExternalIDTypeScore) obj;
		if (Objects.equal(idType, other.idType))
			return false;
		if (Objects.equal(isRank, other.isRank))
			return false;
		if (Objects.equal(operator, other.operator))
			return false;
		if (Objects.equal(getLabel(), other.getLabel()))
			return false;
		return true;
	}


}
