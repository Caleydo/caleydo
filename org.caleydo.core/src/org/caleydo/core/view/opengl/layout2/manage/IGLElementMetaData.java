/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.manage;

import java.net.URL;

import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.view.opengl.layout2.manage.IGLElementFactory2.EVisScaleType;

/**
 * description of a {@link IGLElementFactory} relevant meta data
 *
 * @author Samuel Gratzl
 *
 */
public interface IGLElementMetaData extends ILabeled {

	/**
	 * return the {@link EVisScaleType} of this element
	 *
	 * @return
	 */
	EVisScaleType getScaleType();

	/**
	 * return the unique id of this element factory
	 *
	 * @return
	 */
	String getId();

	/**
	 * return the url of a representative icon
	 *
	 * @return
	 */
	URL getIcon();
}
