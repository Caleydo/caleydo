/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
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
