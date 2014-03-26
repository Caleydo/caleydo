/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.entourage.toolbar;

import org.caleydo.core.gui.SimpleAction;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.view.entourage.Activator;
import org.caleydo.view.entourage.datamapping.DataMappers;

/**
 * @author Christian
 *
 */
public class ShowDataMapperAction extends SimpleAction {
	public static final String LABEL = "Show data assignment view";
	public static final String ICON = "resources/icons/data_mapping.png";

	/**
	 * Constructor.
	 */
	public ShowDataMapperAction() {
		super(LABEL, ICON, new ResourceLoader(Activator.getResourceLocator()));
		setChecked(false);
	}

	@Override
	public void run() {
		super.run();
		setChecked(false);
		DataMappers.getDataMapper().show();
	}
}
