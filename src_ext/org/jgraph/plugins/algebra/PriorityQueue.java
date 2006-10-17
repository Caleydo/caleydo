/*
 * @(#)PriorityQueue.java 1.0 12-MAY-2004
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
public class PriorityQueue {

	/**
	 *
	 */
	protected Hashtable prio = new Hashtable();

	/**
	 *
	 */
	protected HashSet data = new HashSet();

	/**
	 *
	 */
	protected double minPrio = Double.MAX_VALUE;

	/**
	 *
	 */
	protected Object minElt = null;

	/**
	 *
	 */
	public boolean isEmpty() {
		return data.isEmpty();
	}

	/**
	 *
	 */
	public Object pop() {
		Object tmp = minElt;
		data.remove(tmp);
		update();
		return tmp;
	}

	/**
	 *
	 */
	public double getPrio() {
		return minPrio;
	}

	/**
	 *
	 */
	public double getPrio(Object obj) {
		if (obj != null) {
			Double d = (Double) prio.get(obj);
			if (d != null)
				return d.doubleValue();
		}
		return Double.MAX_VALUE;
	}

	/**
	 *
	 */
	protected void update() {
		Iterator it = data.iterator();
		minElt = null;
		minPrio = Double.MAX_VALUE;
		while (it.hasNext()) {
			Object tmp = it.next();
			double prio = getPrio(tmp);
			if (prio < minPrio) {
				minPrio = prio;
				minElt = tmp;
			}
		}
	}

	/**
	 *
	 */
	public void setPrio(Object obj, double prio) {
		Double d = new Double(prio);
		this.prio.put(obj, d);
		data.add(obj);
		if (prio < minPrio) {
			minPrio = prio;
			minElt = obj;
		}
	}

}
