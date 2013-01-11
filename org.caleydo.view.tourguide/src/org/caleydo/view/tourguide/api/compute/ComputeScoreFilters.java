package org.caleydo.view.tourguide.api.compute;

import java.util.Objects;

import org.caleydo.core.data.datadomain.DataDomainOracle;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.variable.ARecordPerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.view.tourguide.spi.compute.IComputeScoreFilter;

public final class ComputeScoreFilters {
	public static IComputeScoreFilter ALL = new IComputeScoreFilter() {
		@Override
		public boolean doCompute(ARecordPerspective a, Group ag, ARecordPerspective b, Group bg) {
			return true;
		}
	};

	public static IComputeScoreFilter SELF = new IComputeScoreFilter() {
		@Override
		public boolean doCompute(ARecordPerspective a, Group ag, ARecordPerspective b, Group bg) {
			if (!Objects.equals(ag, bg)) // not the same group
				return true;
			if (ag == null && !Objects.equals(a, b)) // no groups and not the same stratification
				return true;
			return false;
		}
	};
	/**
	 * if true, add another filter that will produce NaN if the referred groups are equal in the the same categorical
	 * datadomain
	 *
	 * checks whether we the given group refers to the same group within different stratifications within the same
	 * categorical datadomain. e.g. mutated and mutated in two genes
	 *
	 * @param perspective
	 * @param elem
	 * @return
	 */
	public static IComputeScoreFilter MUTUAL_EXCLUSIVE = new IComputeScoreFilter() {
		@Override
		public boolean doCompute(ARecordPerspective a, Group ag, ARecordPerspective b, Group bg) {
			if (ag == null || !Objects.equals(ag.getGroupIndex(), bg.getGroupIndex()))
				return true;
			IDataDomain dataDomain = b.getDataDomain();
			if (!DataDomainOracle.isCategoricalDataDomain(dataDomain))
				return true;
			IDataDomain dataDomain2 = a.getDataDomain();
			if (!dataDomain.equals(dataDomain2))
				return true;
			return false;
		}
	};

	public static IComputeScoreFilter and(final IComputeScoreFilter... elems) {
		if (elems.length == 1)
			return elems[0];
		return new IComputeScoreFilter() {
			@Override
			public boolean doCompute(ARecordPerspective a, Group ag, ARecordPerspective b, Group bg) {
				for (IComputeScoreFilter elem : elems)
					if (!elem.doCompute(a, ag, b, bg))
						return false;
				return true;
			}
		};
	}

	public static IComputeScoreFilter or(final IComputeScoreFilter... elems) {
		if (elems.length == 1)
			return elems[0];
		return new IComputeScoreFilter() {
			@Override
			public boolean doCompute(ARecordPerspective a, Group ag, ARecordPerspective b, Group bg) {
				for (IComputeScoreFilter elem : elems)
					if (elem.doCompute(a, ag, b, bg))
						return true;
				return false;
			}
		};
	}
}