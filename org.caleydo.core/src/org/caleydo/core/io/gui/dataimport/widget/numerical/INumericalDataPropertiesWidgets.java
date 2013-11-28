/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.io.gui.dataimport.widget.numerical;

import org.caleydo.core.io.NumericalProperties;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;

/**
 * A group of swt widgets that define a subset of attributes for {@link NumericalProperties}.
 *
 * @author Christian Partl
 *
 */
public interface INumericalDataPropertiesWidgets {

	/**
	 * Creates all widgets as a child of parent.
	 *
	 * @param parent
	 * @param listener
	 */
	public void create(Composite parent, Listener listener);

	/**
	 * Determines whether all widgets currently contain valid content.
	 *
	 * @return
	 */
	public boolean isContentValid();

	/**
	 * Updates the widgets according to the specified numerical properties.
	 *
	 * @param numericalProperties
	 */
	public void updateProperties(final NumericalProperties numericalProperties);

	/**
	 * Sets the attributes of the specified {@link NumericalProperties} according to the widgets.
	 *
	 * @param numericalProperties
	 */
	public void setProperties(NumericalProperties numericalProperties);

	/**
	 * Disposes all widgets.
	 */
	public void dispose();

}
