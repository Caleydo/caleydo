/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 * 
 */
package org.caleydo.core.data.perspective.table;

import java.util.HashMap;

import org.caleydo.core.data.perspective.table.FoldChangeSettings.FoldChangeEvaluator;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.conversion.ConversionTools;

/**
 * @author alexsb
 */
public class FoldChange {

	HashMap<TablePerspective, Pair<double[], FoldChangeSettings>> containerToFoldChangeResult =
		new HashMap<TablePerspective, Pair<double[], FoldChangeSettings>>();

	HashMap<TablePerspective, double[]> containerToFoldChangeUncertainty =
		new HashMap<TablePerspective, double[]>();

	public void setResult(TablePerspective container, double[] resultVector) {

		containerToFoldChangeResult
			.put(container, new Pair<double[], FoldChangeSettings>(resultVector, null));
	}

	public HashMap<TablePerspective, Pair<double[], FoldChangeSettings>> getAllFoldChangeResults() {
		return containerToFoldChangeResult;
	}

	public Pair<double[], FoldChangeSettings> getResult(TablePerspective container) {

		return containerToFoldChangeResult.get(container);
	}

	public void setFoldChangeSettings(TablePerspective container, FoldChangeSettings foldChangeSettings) {
		containerToFoldChangeResult.get(container).setSecond(foldChangeSettings);

		// Recalculate normalized uncertainty for fold change
		boolean calculateAbsolute = false;
		if (foldChangeSettings.getEvaluator() == FoldChangeEvaluator.BOTH)
			calculateAbsolute = true;

		containerToFoldChangeUncertainty.put(container, ConversionTools.normalize(containerToFoldChangeResult
			.get(container).getFirst(), foldChangeSettings.getRatioUncertainty(), foldChangeSettings
			.getRatio(), calculateAbsolute));
	}

	public double[] getFoldChangeUncertainty(TablePerspective tablePerspective) {

		return containerToFoldChangeUncertainty.get(tablePerspective);
	}

}
