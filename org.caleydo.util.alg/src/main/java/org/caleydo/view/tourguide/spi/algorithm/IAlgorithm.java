/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.spi.algorithm;

import org.caleydo.core.id.IDType;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * basic interface for {@link IGroupAlgorithm} and {@link IStratificationAlgorithm}
 * 
 * @author Samuel Gratzl
 * 
 */
public interface IAlgorithm {
	/**
	 * triggers to initialize this algorithm
	 * 
	 * @param monitor
	 */
	void init(IProgressMonitor monitor);

	/**
	 * returns the target {@link IDType} that should be used for converting the ids of the two given
	 * {@link IComputeElement}s
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	IDType getTargetType(IComputeElement a, IComputeElement b);

	/**
	 * returns the abbreviation of this algorithm
	 * 
	 * @return
	 */
	String getAbbreviation();

	/**
	 * returns a small description what this algorithm does
	 * 
	 * @return
	 */
	String getDescription();
}
