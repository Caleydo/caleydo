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
