/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.picking;

/**
 * Listener for several mouse events. The listener has to be registered with a view in one of two
 * combinations:
 * <ol>
 * <li>either both, a pickedObjectID (an Integer) and a pickingType (a String) - listeners are notified only
 * when the correct combination of id and type are picked, or</li>
 * <li>only a picking type - listeneres are notified when any object of the type are picked</li>
 * </ol>
 *
 * @author Christian Partl
 * @author Alexander Lex
 */
public interface IPickingListener {
	/**
	 * generic callback for a pick use {@link APickingListener} for distinction
	 *
	 * @param pick
	 */
	void pick(Pick pick);
}
