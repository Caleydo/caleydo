/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.impl.algorithm;

import java.util.List;
import java.util.Set;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.id.IDType;
import org.caleydo.view.tourguide.spi.algorithm.IComputeElement;
import org.caleydo.view.tourguide.spi.algorithm.IStratificationAlgorithm;
import org.eclipse.core.runtime.IProgressMonitor;

public abstract class AGSEAAlgorithm implements IStratificationAlgorithm {
	protected final Perspective perspective;
	protected final Group group;

	public AGSEAAlgorithm(Perspective perspective, Group group) {
		this.perspective = perspective;
		this.group = group;
	}

	/**
	 * @return the perspective, see {@link #perspective}
	 */
	public Perspective getPerspective() {
		return perspective;
	}

	/**
	 * @return the group, see {@link #group}
	 */
	public final Group getGroup() {
		return group;
	}

	@Override
	public final IDType getTargetType(IComputeElement a, IComputeElement b) {
		IDataDomain dataDomain = perspective.getDataDomain();
		return ((ATableBasedDataDomain) dataDomain).getDimensionIDType();
	}

	public final float compute(Set<Integer> geneSet, IProgressMonitor monitor) {
		if (geneSet.isEmpty())
			return Float.NaN;
		return computeImpl(geneSet, monitor);
	}

	protected abstract float computeImpl(Set<Integer> geneSet, IProgressMonitor monitor);

	protected abstract float computePValueImpl(Set<Integer> geneSet, IProgressMonitor monitor);

	final float computePValue(Set<Integer> geneSet, IProgressMonitor monitor) {
		if (geneSet.isEmpty())
			return Float.NaN;

		return computePValueImpl(geneSet, monitor);
	}

	@Override
	public final float compute(List<Set<Integer>> a, List<Set<Integer>> b, IProgressMonitor monitor) {
		return compute(a.iterator().next(), monitor);
	}

	public final IStratificationAlgorithm asPValue() {
		return new GSEAAlgorithmPValue(this);
	}

	public static class GSEAAlgorithmPValue implements IStratificationAlgorithm {
		private final AGSEAAlgorithm underlying;

		private GSEAAlgorithmPValue(AGSEAAlgorithm underlying) {
			this.underlying = underlying;
		}

		@Override
		public void init(IProgressMonitor monitor) {
			this.underlying.init(monitor);
		}

		@Override
		public IDType getTargetType(IComputeElement a, IComputeElement b) {
			return underlying.getTargetType(a, b);
		}

		/**
		 * @return the underlying, see {@link #underlying}
		 */
		public AGSEAAlgorithm getUnderlying() {
			return underlying;
		}

		@Override
		public String getAbbreviation() {
			return underlying.getAbbreviation();
		}

		@Override
		public String getDescription() {
			return underlying.getDescription().replace("score", "p-Value");
		}

		@Override
		public float compute(List<Set<Integer>> a, List<Set<Integer>> b, IProgressMonitor monitor) {
			return underlying.computePValue(a.iterator().next(), monitor);
		}
	}
}
