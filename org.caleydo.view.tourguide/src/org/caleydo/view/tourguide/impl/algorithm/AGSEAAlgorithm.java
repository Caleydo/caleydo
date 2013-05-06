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

	protected abstract void init();

	@Override
	public final IDType getTargetType(IComputeElement a, IComputeElement b) {
		IDataDomain dataDomain = perspective.getDataDomain();
		return ((ATableBasedDataDomain) dataDomain).getDimensionIDType();
	}

	public final float compute(Set<Integer> geneSet) {
		if (geneSet.isEmpty())
			return Float.NaN;
		init();
		return computeImpl(geneSet);
	}

	protected abstract float computeImpl(Set<Integer> geneSet);

	protected abstract float computePValueImpl(Set<Integer> geneSet);

	final float computePValue(Set<Integer> geneSet) {
		if (geneSet.isEmpty())
			return Float.NaN;
		init();

		return computePValueImpl(geneSet);
	}

	@Override
	public final float compute(List<Set<Integer>> a, List<Set<Integer>> b) {
		return compute(a.iterator().next());
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
		public float compute(List<Set<Integer>> a, List<Set<Integer>> b) {
			return underlying.computePValue(a.iterator().next());
		}
	}
}
