/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.mappeddataview;

import java.util.HashSet;
import java.util.Set;

import org.caleydo.core.util.base.ILabelProvider;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.picking.IPickingLabelProvider;
import org.caleydo.core.view.opengl.picking.IPickingListener;

/**
 * @author Christian
 *
 */
public class PickingListenerManager {

	private static class Listener {
		final IPickingListener pickingListener;
		final String pickingType;

		public Listener(IPickingListener listener, String pickingType) {
			this.pickingListener = listener;
			this.pickingType = pickingType;
		}

		public void remove(AGLView view) {
			view.removeTypePickingListener(pickingListener, pickingType);
		}
	}

	private static class IDListener extends Listener {
		final int id;

		/**
		 * @param listener
		 * @param pickingType
		 */
		public IDListener(IPickingListener listener, String pickingType, int id) {
			super(listener, pickingType);
			this.id = id;
		}

		@Override
		public void remove(AGLView view) {
			view.removeIDPickingListener(pickingListener, pickingType, id);
		}

	}

	private Set<Listener> listeners;
	private AGLView view;

	public PickingListenerManager(AGLView view) {
		this.view = view;
		listeners = new HashSet<>();
	}

	/**
	 * Removes all picking listeners that were registered using this class.
	 */
	public void removePickingListeners() {
		for (Listener listener : listeners) {
			listener.remove(view);
		}
		listeners.clear();
	}

	public void addIDPickingListener(IPickingListener pickingListener, String pickingType, int pickedObjectID) {
		view.addIDPickingListener(pickingListener, pickingType, pickedObjectID);
		listeners.add(new IDListener(pickingListener, pickingType, pickedObjectID));
	}

	public final IPickingListener addIDPickingTooltipListener(String tooltip, String pickingType, int pickedObjectID) {
		IPickingListener pickingListener = view.addIDPickingTooltipListener(tooltip, pickingType, pickedObjectID);
		listeners.add(new IDListener(pickingListener, pickingType, pickedObjectID));
		return pickingListener;
	}

	public final IPickingListener addIDPickingTooltipListener(ILabelProvider tooltip, String pickingType,
			int pickedObjectID) {
		IPickingListener pickingListener = view.addIDPickingTooltipListener(tooltip, pickingType, pickedObjectID);
		listeners.add(new IDListener(pickingListener, pickingType, pickedObjectID));
		return pickingListener;
	}

	public final IPickingListener addTypePickingTooltipListener(String tooltip, String pickingType) {
		IPickingListener pickingListener = view.addTypePickingTooltipListener(tooltip, pickingType);
		listeners.add(new Listener(pickingListener, pickingType));
		return pickingListener;
	}

	public final IPickingListener addTypePickingTooltipListener(IPickingLabelProvider labelProvider, String pickingType) {
		IPickingListener pickingListener = view.addTypePickingTooltipListener(labelProvider, pickingType);
		listeners.add(new Listener(pickingListener, pickingType));
		return pickingListener;
	}

	public void addTypePickingListener(IPickingListener pickingListener, String pickingType) {
		view.addTypePickingListener(pickingListener, pickingType);
		listeners.add(new Listener(pickingListener, pickingType));
	}

}
