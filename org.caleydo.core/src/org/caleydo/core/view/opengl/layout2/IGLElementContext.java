/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2;

import org.caleydo.core.view.opengl.picking.IPickingListener;

/**
 * basic interface for the context of a element hierarchy, e.g. one {@link IGLElementContext} per opengl canvas
 *
 * @author Samuel Gratzl
 *
 */
public interface IGLElementContext {
	/**
	 * register a picking listener
	 *
	 * @param l
	 * @return the pickingID to use for rendering
	 */
	int registerPickingListener(IPickingListener l);

	/**
	 * see {@link #registerPickingListener(IPickingListener)} with a dedicated objectId
	 *
	 * @param l
	 * @param objectId
	 * @return
	 */
	int registerPickingListener(IPickingListener l, int objectId);

	/**
	 * unregisters a picking listener, given by its pickingId
	 *
	 * @param pickingID
	 */
	void unregisterPickingListener(int pickingId);

	/**
	 * returns the {@link DisplayListPool} of this context
	 *
	 * @return
	 */
	DisplayListPool getDisplayListPool();

	/**
	 * returns a special kind of a {@link GLElementContainer} that will be positioned at the mouse position
	 *
	 * @return
	 */
	IMouseLayer getMouseLayer();

	/**
	 * returns a special kind of a {@link GLElementContainer} that will be renderer on top of the content
	 *
	 * @return
	 */
	IPopupLayer getPopupLayer();

	/**
	 * returns the corresponding {@link ISWTLayer}
	 * 
	 * @return
	 */
	ISWTLayer getSWTLayer();

	/**
	 * chance for a context to initialize an added element
	 *
	 * @param element
	 */
	void init(GLElement element);

	/**
	 * chance for a context to undo the initialization of added element, see {@link #init(GLElement)}
	 *
	 * @param element
	 */
	void takeDown(GLElement element);
}
