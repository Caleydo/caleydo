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
package org.caleydo.vis.rank.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.caleydo.core.view.opengl.layout2.GLElement;

/**
 * simple property change listeners for gl elements
 * 
 * @author Samuel Gratzl
 * 
 */
public class GLPropertyChangeListeners {
	public static PropertyChangeListener repaintOnEvent(GLElement elem) {
		return new GLPropertyChangeListener(elem, false);
	}

	public static PropertyChangeListener relayoutOnEvent(GLElement elem) {
		return new GLPropertyChangeListener(elem, true);
	}

	private static class GLPropertyChangeListener implements PropertyChangeListener {
		private final GLElement elem;
		private final boolean relayout;

		public GLPropertyChangeListener(GLElement elem, boolean relayout) {
			super();
			this.elem = elem;
			this.relayout = relayout;
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (relayout)
				elem.relayout();
			else
				elem.repaint();
		}
	}
}

