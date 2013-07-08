/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.spi.score;

import org.caleydo.core.util.color.Color;

import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.spi.algorithm.IComputeElement;
import org.caleydo.vis.rank.model.mapping.PiecewiseMapping;

/**
 * basic abstraction of a score
 *
 * @author Samuel Gratzl
 *
 */
public interface IScore extends ILabeled {
	/**
	 * determines whether the current score support the given {@link EDataDomainQueryMode} mode
	 *
	 * @param mode
	 * @return
	 */
	boolean supports(EDataDomainQueryMode mode);

	/**
	 * @return
	 */
	String getAbbreviation();

	String getDescription();

	Color getColor();

	Color getBGColor();

	/**
	 * factory method for creating a {@link PiecewiseMapping} which is used by the mapping editor of the column
	 * 
	 * @return
	 */
	PiecewiseMapping createMapping();

	/**
	 * computes the score of the given {@link IComputeElement}
	 * 
	 * @param elem
	 * @param g
	 *            optional depending whether this kind of score is based on groups or not
	 * @return the score or {@link Float#NaN} otherwise
	 */
	float apply(IComputeElement elem, Group g);
}
