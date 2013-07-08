/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.picking;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * composite pattern for {@link IPickingListener}s
 *
 * @author Samuel Gratzl
 *
 */
public final class PickingListenerComposite extends ArrayList<IPickingListener> implements IPickingListener {
	private static final long serialVersionUID = 7373988297616381165L;

	public PickingListenerComposite() {
	}

	public PickingListenerComposite(int initialCapacity) {
		super(initialCapacity);
	}

	public static IPickingListener concat(IPickingListener... listeners) {
		if (listeners.length == 1)
			return listeners[0];
		PickingListenerComposite c = new PickingListenerComposite();
		c.addAll(Arrays.asList(listeners));
		return c;
	}

	@Override
	public void pick(Pick pick) {
		for (IPickingListener p : this)
			p.pick(pick);
	}
}

