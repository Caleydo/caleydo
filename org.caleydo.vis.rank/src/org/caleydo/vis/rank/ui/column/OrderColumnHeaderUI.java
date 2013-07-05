/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.rank.ui.column;

import org.caleydo.vis.rank.config.IRankTableUIConfig;
import org.caleydo.vis.rank.model.ARankColumnModel;

/**
 * @author Samuel Gratzl
 *
 */
public class OrderColumnHeaderUI extends AColumnHeaderUI {
	public OrderColumnHeaderUI(ARankColumnModel model, IRankTableUIConfig config) {
		super(model, config, true, true);
	}
}
