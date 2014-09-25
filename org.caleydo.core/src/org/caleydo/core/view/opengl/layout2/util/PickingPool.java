/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2.util;

import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.picking.IPickingListener;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.jogamp.common.util.IntIntHashMap;

/**
 * utility for creating a "type" picking listener
 *
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

	/**
	 * release the current object id if it is in use
	 *
	 * @param freed
	 */
	public boolean free(int objectId) {
		if (lookup.containsKey(objectId)) {
			context.unregisterPickingListener(lookup.remove(objectId));
			return true;
		}
		return false;
	}

	/**
	 * @return
	 */
	public boolean isEmpty() {
		return lookup.size() == 0;
	}

	/**
	 * returns a snapshot of the current keys
	 *
	 * @return
	 */
	public ImmutableSet<Integer> currentKeys() {
		Builder<Integer> builder = ImmutableSet.builder();
		for (IntIntHashMap.Entry entry : lookup) {
			builder.add(entry.key);
		}
		return builder.build();
	}

}
