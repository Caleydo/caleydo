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

