/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 *
 */
package org.caleydo.core.util.base;

/**
 * Interface for classes that provide a label. The {@link #getLabel()} method is used as callback to keep the text
 * up-to-date for classes using this text.
 * 
 * @author Christian
 * 
 */
public interface ILabelProvider extends ILabeled {
	/**
	 * @return A human-readable name of the concrete label provider.
	 */
	public String getProviderName();

}
