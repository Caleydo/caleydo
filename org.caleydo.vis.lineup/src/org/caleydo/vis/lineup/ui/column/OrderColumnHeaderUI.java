/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.ui.column;

import org.caleydo.vis.lineup.config.IRankTableUIConfig;
import org.caleydo.vis.lineup.model.ARankColumnModel;

/**
 * @author Samuel Gratzl
 *
 */
public class OrderColumnHeaderUI extends AColumnHeaderUI {
	public OrderColumnHeaderUI(ARankColumnModel model, IRankTableUIConfig config) {
		super(model, config, true, true);
	}
}
