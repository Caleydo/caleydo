/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.rank.model.mapping;

import org.caleydo.core.util.function.FloatStatistics;
import org.caleydo.core.util.function.IFloatFunction;

/**
 * @author Samuel Gratzl
 *
 */
public interface IMappingFunction extends IFloatFunction {
	String toJavaScript();

	void fromJavaScript(String code);

	float[] getMappedMin();

	float[] getMappedMax();

	boolean hasDefinedMappingBounds();

	boolean isMinDefined();

	boolean isMaxDefined();

	boolean isMappingDefault();

	float getActMin();

	float getActMax();

	/**
	 * @return
	 */
	IMappingFunction clone();

	void reset();


	float getMaxTo();

	float getMinTo();

	void setActStatistics(FloatStatistics stats);

	boolean isComplexMapping();
}
