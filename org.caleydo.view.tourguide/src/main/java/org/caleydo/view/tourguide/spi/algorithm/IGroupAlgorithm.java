/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.spi.algorithm;

import java.util.Set;

import org.caleydo.core.data.virtualarray.group.Group;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * a group algorithm is an abstract definition of an algorithm that computes of two Sets a score
 *
 * @author Samuel Gratzl
 *
 */
public interface IGroupAlgorithm extends IAlgorithm {
	/**
	 * computes the score between the two sets identified by a set of integer noted in the same id type
	 *
	 * @param a
	 * @param ag
	 * @param b
	 * @param bg
	 * @return
	 */
	double compute(Set<Integer> a, Group ag, Set<Integer> b, Group bg, IProgressMonitor monitor);
}
