package org.caleydo.view.tourguide.data.compute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.RecordGroupList;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.IIDTypeMapper;
import org.caleydo.core.util.collection.Pair;
import org.eclipse.core.runtime.jobs.Job;

import com.google.common.base.Predicate;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public abstract class AScoreJob extends Job {
	private final CachedIDTypeMapper mapper = new CachedIDTypeMapper();
	private final Table<TablePerspective, Pair<IDType, IDType>, Set<Integer>> stratCache = HashBasedTable.create();
	private final Table<Group, Pair<IDType, IDType>, Set<Integer>> groupCache = HashBasedTable.create();

	public AScoreJob(String label) {
		super(label);
	}

	protected final void clear(TablePerspective strat) {
		stratCache.row(strat).clear();
	}

	protected final void clear(Group g) {
		groupCache.row(g).clear();
	}

	protected final Set<Integer> get(TablePerspective strat, Group group, IDType target, IDType occurIn) {
		Pair<IDType, IDType> check = Pair.make(target, occurIn);
		if (groupCache.contains(group, check))
			return groupCache.get(group, check);

		IDType source = strat.getRecordPerspective().getIdType();

		IIDTypeMapper<Integer, Integer> mapper = this.mapper.get(source, target);
		Set<Integer> r = null;
		if (mapper == null)
			r = Collections.emptySet();
		else {
			RecordVirtualArray va = strat.getRecordPerspective().getVirtualArray();
			r = mapper.apply(va.getIDsOfGroup(group.getGroupIndex()));
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

	protected final List<Set<Integer>> getAll(TablePerspective strat, IDType target, IDType occurIn) {
		RecordGroupList groups = strat.getRecordPerspective().getVirtualArray().getGroupList();
		List<Set<Integer>> r = new ArrayList<>(groups.size());
		for(Group g : groups)
			r.add(get(strat, g, target, occurIn));
		return r;
	}

	protected final Set<Integer> get(TablePerspective strat, IDType target, IDType occurIn) {
		Pair<IDType, IDType> check = Pair.make(target, occurIn);
		if (stratCache.contains(strat, check))
			return stratCache.get(strat, check);

		IDType source = strat.getRecordPerspective().getIdType();

		IIDTypeMapper<Integer, Integer> mapper = this.mapper.get(source, target);
		Set<Integer> r = null;
		if (mapper == null)
			r = Collections.emptySet();
		else {
			RecordVirtualArray va = strat.getRecordPerspective().getVirtualArray();
			r = mapper.apply(va);
			if (!target.equals(occurIn) && !source.equals(occurIn)) { // check against third party
				Predicate<Integer> in = this.mapper.in(target, occurIn);
				// filter the wrong out
				for (Iterator<Integer> it = r.iterator(); it.hasNext();)
					if (!in.apply(it.next()))
						it.remove();
			}
		}
		stratCache.put(strat, check, r);
		return r;
	}
}