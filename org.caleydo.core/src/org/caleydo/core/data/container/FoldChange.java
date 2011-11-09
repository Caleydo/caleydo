/**
 * 
 */
package org.caleydo.core.data.container;

import java.util.HashMap;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.container.FoldChangeSettings.FoldChangeEvaluator;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.conversion.ConversionTools;

/**
 * @author alexsb
 */
public class FoldChange {

	HashMap<DataContainer, Pair<double[], FoldChangeSettings>> containerToFoldChangeResult =
		new HashMap<DataContainer, Pair<double[], FoldChangeSettings>>();

	HashMap<DataContainer, double[]> setToFoldChangeUncertainty = new HashMap<DataContainer, double[]>();;

	public void setResult(DataContainer container, double[] resultVector) {

		containerToFoldChangeResult
			.put(container, new Pair<double[], FoldChangeSettings>(resultVector, null));
	}

	public HashMap<DataContainer, Pair<double[], FoldChangeSettings>> getAllFoldChangeResults() {
		return containerToFoldChangeResult;
	}

	public Pair<double[], FoldChangeSettings> getResult(DataContainer container) {

		return containerToFoldChangeResult.get(container);
	}

	public void setFoldChangeSettings(DataContainer container, FoldChangeSettings foldChangeSettings) {
		containerToFoldChangeResult.get(container).setSecond(foldChangeSettings);

		// Recalculate normalized uncertainty for fold change
		boolean calculateAbsolute = false;
		if (foldChangeSettings.getEvaluator() == FoldChangeEvaluator.BOTH)
			calculateAbsolute = true;

		setToFoldChangeUncertainty.put(container, ConversionTools.normalize(
			containerToFoldChangeResult.get(container).getFirst(), foldChangeSettings.getRatioUncertainty(),
			foldChangeSettings.getRatio(), calculateAbsolute));
	}

	public double[] getFoldChangeUncertainty(DataTable set) {

		return setToFoldChangeUncertainty.get(set);
	}

}
