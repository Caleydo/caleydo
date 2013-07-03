/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.treemap.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.treemap.GLHierarchicalTreeMap;

/**
 * Listener for zoom out function.
 * 
 * @author Michael Lafer
 * 
 */

public class ZoomOutListener extends AEventListener<GLHierarchicalTreeMap> {

	@Override
	public void handleEvent(AEvent event) {
		handler.zoomOut();
	}

}
