/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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

