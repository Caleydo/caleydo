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
package org.caleydo.core.view.opengl.layout2.util;

import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.picking.IPickingListener;

import com.jogamp.common.util.IntIntHashMap;

/**
 * utility for creating a "type" picking listener
 * 
 * @author Samuel Gratzl
 * 
 */
public class PickingPool {
	private final IGLElementContext context;
	private final IntIntHashMap lookup = new IntIntHashMap();
	private final IPickingListener pickingListener;

	public PickingPool(IGLElementContext context, IPickingListener pickingListener) {
		this.context = context;
		this.pickingListener = pickingListener;
	}

	/**
	 * creates a bunch of picking ids given by start and end
	 * 
	 * @param objectIdStart
	 * @param objectIdEnd
	 */
	public void ensure(int objectIdStart, int objectIdEnd) {
		for(int i = objectIdStart; i < objectIdEnd; ++i) {
			get(i);
		}
	}

	/**
	 * frees all used picking listeners
	 */
	public void clear() {
		for (IntIntHashMap.Entry entry : lookup) {
			context.unregisterPickingListener(entry.getValue());
		}
		lookup.clear();
	}

	/**
	 * returns the picking id for this picking type listener and the given object id
	 * 
	 * @param objectId
	 *            the object id to use
	 * @return
	 */
	public int get(int objectId) {
		if (lookup.containsKey(objectId))
			return lookup.get(objectId);
		int id = context.registerPickingListener(pickingListener, objectId);
		lookup.put(objectId, id);
		return id;
	}
}
