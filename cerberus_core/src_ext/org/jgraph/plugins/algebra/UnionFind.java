/*
 * @(#)UnionFind.java 1.0 12-MAY-2004
 * 
 * Copyright (c) 2001-2005, Gaudenz Alder
 * All rights reserved. 
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package org.jgraph.plugins.algebra;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;


/**
 * @author Gaudenz Alder
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class UnionFind {

	/**
	 *
	 */
	protected Hashtable sets = new Hashtable(), cells = new Hashtable();

	/**
	 *
	 */
	public int getSetCount() {
		return sets.size();
	}

	/**
	 *
	 */
	public Object find(Object cell) {
		Object set = null;
		if (cell != null) {
			set = cells.get(cell);
			if (set == null) {
				set = cell;
				cells.put(cell, set);
				HashSet contents = new HashSet();
				contents.add(cell);
				sets.put(set, contents);
			}
		}
		return set;
	}

	/**
	 *
	 */
	public Object union(Object set1, Object set2) {
		if (set1 != null && set2 != null && set1 != set2) {
			HashSet tmp1 = (HashSet) sets.get(set1);
			HashSet tmp2 = (HashSet) sets.get(set2);
			if (tmp1 != null && tmp2 != null) {
				if (tmp1.size() < tmp2.size()) {
					Object tmp = tmp1;
					tmp1 = tmp2;
					tmp2 = (HashSet) tmp;
					tmp = set1;
					set1 = set2;
					set2 = tmp;
				}
				tmp1.addAll(tmp2);
				sets.remove(set2);
				Iterator it = tmp2.iterator();
				while (it.hasNext())
					cells.put(it.next(), set1);
			}
		}
		return set1;
	}

}
