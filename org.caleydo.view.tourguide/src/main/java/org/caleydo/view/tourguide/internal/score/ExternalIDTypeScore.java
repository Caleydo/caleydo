/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.score;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.view.tourguide.api.external.ScoreParseSpecification;
import org.caleydo.view.tourguide.api.score.ECombinedOperator;
import org.caleydo.view.tourguide.internal.serialize.DataDomainAdapter;
import org.caleydo.view.tourguide.internal.serialize.IDTypeAdapter;
import org.caleydo.view.tourguide.spi.algorithm.IComputeElement;
import org.caleydo.vis.lineup.model.mapping.PiecewiseMapping;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.primitives.Doubles;

/**
 * @author Samuel Gratzl
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class ExternalIDTypeScore extends AExternalScore {
	private static final Logger log = Logger.create(ExternalIDTypeScore.class);

	@XmlJavaTypeAdapter(IDTypeAdapter.class)
	private IDType idType;
	private ECombinedOperator operator;
	private boolean isRank;
	private Map<Integer, Double> scores = new HashMap<>();
	@XmlJavaTypeAdapter(DataDomainAdapter.class)
	private ATableBasedDataDomain dataDomain;

	// cache my id type mapping results
	@XmlTransient
	private final LoadingCache<Pair<IDType, Integer>, Optional<Integer>> mapping = CacheBuilder.newBuilder()
			.maximumSize(1000)
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
		super();
	}

	public ExternalIDTypeScore(String label, ScoreParseSpecification spec, IDType idType, boolean isRank,
			ATableBasedDataDomain dataDomain, Map<Integer, Double> scores) {
		super(label, spec);
		this.idType = idType;
		this.isRank = isRank;
		this.operator = spec.getOperator();
		this.scores.putAll(scores);
		this.dataDomain = dataDomain;
	}

	public boolean isCompatible(IDType type) {
		return type != null && this.idType.getIDCategory().equals(type.getIDCategory());
	}

	/**
	 * @return the idType, see {@link #idType}
	 */
	public IDType getIdType() {
		return idType;
	}

	@Override
	public PiecewiseMapping createMapping() {
		if (isRank)
			return new PiecewiseMapping(0, Double.NaN);
		return super.createMapping();
	}

	@Override
	public double apply(IComputeElement elem, Group g) {
		final IDType record = elem.getIdType();
		final IDType dim = elem.getDimensionIdType();
		if (dataDomain == null) { //backward case
			if (isCompatible(record)) {
				return addScores(record, elem.of(g));
			} else if (isCompatible(dim)) { // for all dimensions
				return addScores(dim, elem.getDimensionIDs());
			}
		} else {
			final boolean inDimension = dataDomain.getDimensionIDCategory().isOfCategory(this.idType);
			final boolean isCompatibleDataDomain = isCompatible(elem.getDataDomain(), inDimension);
			if (!isCompatibleDataDomain)
				return Double.NaN;
			if (!inDimension && isCompatible(record)) {
				return addScores(record, elem.of(g));
			} else if (inDimension && isCompatible(dim)) { // for all dimensions
				return addScores(dim, elem.getDimensionIDs());
			}
		}
		return Double.NaN;
	}

	private boolean isCompatible(IDataDomain current, boolean inDimension) {
		if (inDimension) {
			// current must be categorical datadomain: mutation, copy-number
			return DataSupportDefinitions.categoricalTables.apply(current);
			// return Objects.equals(current, dataDomain); //same data domain
		} else {
			return true;
		}
	}

	private double addScores(final IDType target, final Iterable<Integer> ids) {
		Collection<Double> scores = new ArrayList<>();
		for (Integer id : ids) {
			Optional<Integer> my = mapping.getUnchecked(Pair.make(target, id));
			if (!my.isPresent())
				continue;
			Double s = this.scores.get(my.get());
			if (s == null)
				continue;
			scores.add(s);
		}
		if (scores.isEmpty())
			return Double.NaN;
		if (scores.size() == 1)
			return scores.iterator().next().doubleValue();
		return operator.combine(Doubles.toArray(scores));
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
		if (!Objects.equal(idType, other.idType))
			return false;
		if (!Objects.equal(isRank, other.isRank))
			return false;
		if (!Objects.equal(operator, other.operator))
			return false;
		if (!Objects.equal(getLabel(), other.getLabel()))
			return false;
		if (!Objects.equal(dataDomain, other.dataDomain))
			return false;
		return true;
	}


}
