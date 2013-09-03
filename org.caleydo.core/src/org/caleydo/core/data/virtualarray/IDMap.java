/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.virtualarray;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.jogamp.common.util.IntObjectHashMap;

/**
 * Package private class used as hash-backing of virtual arrays to increase indexof performance to linear
 * time.
 *
 * @author Alexander Lex
 */
class IDMap {
	private final static Integer INVALID = Integer.valueOf(-1);

	// use a specialized class for less memory
	private IntObjectHashMap id2indices;
	private final List<Integer> data;

	IDMap(List<Integer> virtualArrayList) {
		this.data = virtualArrayList;
	}

	void setDirty() {
		id2indices = null;
	}

	private void checkIDMap() {
		if (id2indices != null)
			return;

		// Logger.log(new Status(IStatus.INFO, "core", "Rebuilding index map for VA:" + virtualArray));

		id2indices = new IntObjectHashMap(data.size());
		for(int i = 0; i < data.size(); ++i) {
			Integer id = data.get(i);
			int id_i = id.intValue();
			Object r = id2indices.get(id_i);
			if (r == null) // first use a single Integer value
				r = Integer.valueOf(i);
			else if (r instanceof Integer) { // second wrap as list
				List<Integer> r_l = new ArrayList<>(2);
				r_l.add((Integer) r);
				r_l.add(i);
				r = r_l;
			} else if (r instanceof List<?>) { // add another value to the list
				@SuppressWarnings("unchecked")
				List<Integer> r_l = (List<Integer>) r;
				r_l.add(i);
			}
			id2indices.put(id_i, r);
		}
	}

	@SuppressWarnings("unchecked")
	Integer indexOf(Integer id) {
		checkIDMap();

		Object r = id2indices.get(id);
		if (r == null)
			return INVALID;
		if (r instanceof Integer)
			return (Integer) r;
		if (r instanceof List<?>) {
			return ((List<Integer>) r).get(0);
		}
		throw new IllegalStateException();
	}

	@SuppressWarnings("unchecked")
	List<Integer> indicesOf(Integer id) {
		checkIDMap();

		Object r = id2indices.get(id);
		if (r == null)
			return ImmutableList.of(INVALID);
		if (r instanceof Integer)
			return ImmutableList.of((Integer) r);
		if (r instanceof List<?>) {
			return ((List<Integer>) r);
		}
		throw new IllegalStateException();
	}

	boolean contains(Integer id) {
		checkIDMap();
		return id2indices.containsKey(id.intValue());
	}

	int occurencesOf(Integer id) {
		checkIDMap();
		Object r = id2indices.get(id);
		if (r == null)
			return 0;
		if (r instanceof Integer)
			return 1;
		if (r instanceof List<?>) {
			return ((List<?>) r).size();
		}
		throw new IllegalStateException();
	}

}
