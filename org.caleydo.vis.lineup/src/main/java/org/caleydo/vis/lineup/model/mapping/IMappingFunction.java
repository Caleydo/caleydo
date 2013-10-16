/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.model.mapping;

import org.caleydo.core.util.function.DoubleStatistics;
import org.caleydo.core.util.function.IDoubleFunction;

/**
 * @author Samuel Gratzl
 *
 */
public interface IMappingFunction extends IDoubleFunction {
	String toJavaScript();

	void fromJavaScript(String code);

	double[] getMappedMin();

	double[] getMappedMax();

	boolean hasDefinedMappingBounds();

	boolean isMinDefined();

	boolean isMaxDefined();

	boolean isMappingDefault();

	double getActMin();

	double getActMax();

	/**
	 * @return
	 */
	IMappingFunction clone();

	void reset();


	double getMaxTo();

	double getMinTo();

	void setActStatistics(DoubleStatistics stats);

	boolean isComplexMapping();
}
