package org.caleydo.view.tourguide.internal.compute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.IIDTypeMapper;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.view.tourguide.spi.algorithm.IComputeElement;
import org.caleydo.view.tourguide.spi.algorithm.IStratificationAlgorithm;
import org.caleydo.view.tourguide.spi.compute.IComputedReferenceStratificationScore;
import org.caleydo.view.tourguide.spi.compute.IComputedStratificationScore;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;

public abstract class AScoreJob {
	private final CachedIDTypeMapper mapper = new CachedIDTypeMapper();
	private final Table<IComputeElement, Pair<IDType, IDType>, Set<Integer>> stratCache = HashBasedTable.create();
	private final Table<Group, Pair<IDType, IDType>, Set<Integer>> groupCache = HashBasedTable.create();

	public abstract IStatus run(IProgressMonitor monitor);

	protected final void clear(IComputeElement va) {
		stratCache.row(va).clear();
	}

	protected final void clear(Group g) {
		groupCache.row(g).clear();
	}

	protected final Set<Integer> get(IComputeElement va, Group group, IDType target, IDType occurIn) {
		Pair<IDType, IDType> check = Pair.make(target, occurIn);
		if (groupCache.contains(group, check))
			return groupCache.get(group, check);

		IDType source = va.getIdType();

		IIDTypeMapper<Integer, Integer> mapper = this.mapper.get(source, target);
		Set<Integer> r = null;
		if (mapper == null)
			r = Collections.emptySet();
		else {
			r = mapper.apply(va.of(group));
			if (!target.equals(occurIn) && !source.equals(occurIn)) { // check against third party
				Predicate<Integer> in = this.mapper.in(target, occurIn);
				// filter the wrong out
				for (Iterator<Integer> it = r.iterator(); it.hasNext();)
					if (!in.apply(it.next()))
						it.remove();
			}
		}
		groupCache.put(group, check, r);
		return r;
	}

	protected final List<Set<Integer>> getAll(IComputeElement va, IDType target, IDType occurIn) {
		Collection<Group> groups = va.getGroups();
		List<Set<Integer>> r = new ArrayList<>(groups.size());
		for(Group g : groups)
			r.add(get(va, g, target, occurIn));
		return r;
	}

	protected final Set<Integer> get(IComputeElement va, IDType target, IDType occurIn) {
		Pair<IDType, IDType> check = Pair.make(target, occurIn);
		if (stratCache.contains(va, check))
			return stratCache.get(va, check);

		IDType source = va.getIdType();

		IIDTypeMapper<Integer, Integer> mapper = this.mapper.get(source, target);
		Set<Integer> r = null;
		if (mapper == null)
			r = Collections.emptySet();
		else {
			r = mapper.apply(va);
			if (!target.equals(occurIn) && !source.equals(occurIn)) { // check against third party
				Predicate<Integer> in = this.mapper.in(target, occurIn);
				// filter the wrong out
				for (Iterator<Integer> it = r.iterator(); it.hasNext();)
					if (!in.apply(it.next()))
						it.remove();
			}
		}
		stratCache.put(va, check, r);
		return r;
	}

	protected final static <A, B extends A> Pair<Collection<A>, Collection<B>> partition(Collection<A> in,
			final Class<B> with) {
		Collection<B> bs = Lists.newArrayList(Iterables.transform(Iterables.filter(in, Predicates.instanceOf(with)),
				new Function<A, B>() {
					@Override
					public B apply(A a) {
						return with.cast(a);
					}
				}));
		Collection<A> as = Collections2.filter(in, Predicates.not(Predicates.instanceOf(with)));
		return Pair.make(as, bs);
	}

	protected final IStatus computeStratificationScores(IProgressMonitor monitor, IComputeElement va,
			Collection<IComputedStratificationScore> stratMetrics,
			Collection<IComputedReferenceStratificationScore> stratScores) {
		for (IComputedStratificationScore score : stratMetrics) {
			IStratificationAlgorithm algorithm = score.getAlgorithm();
			IDType target = algorithm.getTargetType(va, va);
			if (score.contains(va) || !score.getFilter().doCompute(va, null, va, null)) {
				continue;
			}
			List<Set<Integer>> compute = getAll(va, target, target);

			if (Thread.interrupted() || monitor.isCanceled())
				return Status.CANCEL_STATUS;

			float v = algorithm.compute(compute, compute);
			score.put(va, v);
		}

		// all stratification scores
		for (IComputedReferenceStratificationScore score : stratScores) {
			IStratificationAlgorithm algorithm = score.getAlgorithm();
			final IComputeElement rs = score.asComputeElement();
			IDType target = algorithm.getTargetType(va, rs);
			if (score.contains(va) || !score.getFilter().doCompute(va, null, rs, null)) {
				continue;
			}
			List<Set<Integer>> compute = getAll(va, target, target);
			List<Set<Integer>> reference = getAll(rs, target, target);

			if (Thread.interrupted() || monitor.isCanceled())
				return Status.CANCEL_STATUS;

			float v = algorithm.compute(compute, reference);
			score.put(va, v);
		}
		return null;
	}
}