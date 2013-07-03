/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.treemap.layout;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.util.color.mapping.ColorMapper;

/**
 * Shared data for a tree of ClusterNodes.
 * 
 * @author Michael Lafer
 * 
 */
public class ClusterReferenzData {
	float sizeReferenzValue = 1;
	float colorReferenzSpace = 1;
	float colorMin;
	float colorMax;
	ColorMapper colorMapper;

	boolean bUseExpressionValues = false;
	ATableBasedDataDomain dataDomain;
}
