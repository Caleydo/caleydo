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
package org.caleydo.core.data.collection.table;

import org.caleydo.core.data.collection.column.AColumn;
import org.caleydo.core.data.collection.column.DataRepresentation;
import org.caleydo.core.data.collection.column.NumericalColumn;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;

/**
 * This class encapsulates all uncertainty-related functionality of the data table
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class Uncertainty {

	private DataTable table;
	private MetaData metaData;

	/**
	 * the uncertainties for the whole dimension aggregated across the dimensionVA based on the normalized
	 * uncertainty values
	 */
	private float[] aggregatedNormalizedUncertainties;
	/**
	 * the uncertainties for the whole dimension aggregated across the dimensionVA based on the raw
	 * uncertainty values
	 */
	private float[] aggregatedRawUncertainties;

	public Uncertainty(DataTable table) {
		this.table = table;
		this.metaData = table.getMetaData();
	}

	public float getNormalizedUncertainty(int recordIndex) {

		if (aggregatedRawUncertainties == null) {
			// calculateRawAverageUncertainty();
			// calculateNormalizedAverageUncertainty(2, 3);
			throw new IllegalStateException("Certainty has not been calculated yet.");

		}

		return aggregatedNormalizedUncertainties[recordIndex];
	}

	public float[] getNormalizedUncertainty() {

		// if (aggregatedRawUncertainties == null) {
		// calculateRawAverageUncertainty();
		// // throw new IllegalStateException("Certainty has not been calculated yet.");
		// }

		return aggregatedNormalizedUncertainties;
	}

	public float[] getRawUncertainty() {

		if (aggregatedRawUncertainties == null)
			throw new IllegalStateException("Certainty has not been calculated yet.");

		return aggregatedRawUncertainties;
	}

	public void calculateNormalizedAverageUncertainty(float invalidThreshold, float validThreshold,
		String dimensionPerspectiveID) {

		for (AColumn dimension : table.hashColumns.values()) {

			if (dimension instanceof NumericalColumn)
				((NumericalColumn) dimension).normalizeUncertainty(invalidThreshold, validThreshold);
		}

		aggregatedNormalizedUncertainties = new float[metaData.depth()];
		for (int recordIndex = 0; recordIndex < metaData.depth(); recordIndex++) {
			// float aggregatedUncertainty = calculateMaxUncertainty(recordIndex);
			float aggregatedUncertainty =
				calcualteAverageUncertainty(recordIndex, DataRepresentation.UNCERTAINTY_NORMALIZED,
					dimensionPerspectiveID);
			aggregatedNormalizedUncertainties[recordIndex] = aggregatedUncertainty;
		}
	}

	public void calculateRawAverageUncertainty(String dimensionPerspectiveID) {
		aggregatedRawUncertainties = new float[metaData.depth()];
		for (int recordIndex = 0; recordIndex < metaData.depth(); recordIndex++) {
			float aggregatedUncertainty;

			aggregatedUncertainty =
				calcualteAverageUncertainty(recordIndex, DataRepresentation.UNCERTAINTY_RAW,
					dimensionPerspectiveID);

			// aggregatedUncertainty =
			// calculateMaxUncertainty(recordIndex, EDataRepresentation.UNCERTAINTY_RAW);

			aggregatedRawUncertainties[recordIndex] = aggregatedUncertainty;
		}
	}

	private float calcualteAverageUncertainty(int recordIndex, DataRepresentation dataRepresentation,
		String dimensionPerspectiveID) {
		float uncertaintySum = 0;
		DimensionVirtualArray dimensionVA =
			table.getDimensionPerspective(dimensionPerspectiveID).getVirtualArray();
		for (Integer dimensionID : dimensionVA) {
			try {
				uncertaintySum +=
					table.hashColumns.get(dimensionID).getFloat(dataRepresentation, recordIndex);
			}
			catch (Exception e) {
				System.out.println("dimensionID: " + dimensionID);
			}
		}
		return uncertaintySum / dimensionVA.size();
	}

	@SuppressWarnings("unused")
	private float calculateMaxUncertainty(int recordIndex, DataRepresentation dataRepresentation,
		String dimensionPerspectiveID) {
		float maxUncertainty = Float.MAX_VALUE;
		for (Integer dimensionID : table.getDimensionPerspective(dimensionPerspectiveID).getVirtualArray()) {
			float cellUncertainty = 0;
			try {
				cellUncertainty =
					table.hashColumns.get(dimensionID).getFloat(dataRepresentation, recordIndex);
			}
			catch (Exception e) {
				System.out.println("dimensionID: " + dimensionID);

			}
			if (cellUncertainty < maxUncertainty) {
				maxUncertainty = cellUncertainty;
			}
		}
		return maxUncertainty;
	}

}
