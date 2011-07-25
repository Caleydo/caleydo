package org.caleydo.core.data.collection.table;

import org.caleydo.core.data.collection.dimension.ADimension;
import org.caleydo.core.data.collection.dimension.DataRepresentation;
import org.caleydo.core.data.collection.dimension.NumericalDimension;
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
	 * the uncertainties for the whole dimension aggregated across the dimensionVA based on the raw uncertainty
	 * values
	 */
	private float[] aggregatedRawUncertainties;

	public Uncertainty(DataTable table) {
		this.table = table;
		this.metaData = table.getMetaData();
	}

	public float getNormalizedUncertainty(int contentIndex) {

		if (aggregatedRawUncertainties == null) {
			// calculateRawAverageUncertainty();
			// calculateNormalizedAverageUncertainty(2, 3);
			throw new IllegalStateException("Certainty has not been calculated yet.");

		}

		return aggregatedNormalizedUncertainties[contentIndex];
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

	public void calculateNormalizedAverageUncertainty(float invalidThreshold, float validThreshold) {

		for (ADimension dimension : table.hashDimensions.values()) {

			if (dimension instanceof NumericalDimension)
				((NumericalDimension) dimension).normalizeUncertainty(invalidThreshold, validThreshold);
		}

		aggregatedNormalizedUncertainties = new float[metaData.depth()];
		for (int contentIndex = 0; contentIndex < metaData.depth(); contentIndex++) {
			// float aggregatedUncertainty = calculateMaxUncertainty(contentIndex);
			float aggregatedUncertainty =
				calcualteAverageUncertainty(contentIndex, DataRepresentation.UNCERTAINTY_NORMALIZED);
			aggregatedNormalizedUncertainties[contentIndex] = aggregatedUncertainty;
		}
	}

	public void calculateRawAverageUncertainty() {
		aggregatedRawUncertainties = new float[metaData.depth()];
		for (int contentIndex = 0; contentIndex < metaData.depth(); contentIndex++) {
			float aggregatedUncertainty;

			aggregatedUncertainty =
				calcualteAverageUncertainty(contentIndex, DataRepresentation.UNCERTAINTY_RAW);

			// aggregatedUncertainty =
			// calculateMaxUncertainty(contentIndex, EDataRepresentation.UNCERTAINTY_RAW);

			aggregatedRawUncertainties[contentIndex] = aggregatedUncertainty;
		}
	}

	private float calcualteAverageUncertainty(int contentIndex, DataRepresentation dataRepresentation) {
		float uncertaintySum = 0;
		DimensionVirtualArray dimensionVA = table.hashDimensionData.get(DataTable.DIMENSION).getDimensionVA();
		for (Integer dimensionID : dimensionVA) {
			try {
				uncertaintySum +=
					table.hashDimensions.get(dimensionID).getFloat(dataRepresentation, contentIndex);
			}
			catch (Exception e) {
				System.out.println("dimensionID: " + dimensionID);
			}
		}
		return uncertaintySum / dimensionVA.size();
	}

	@SuppressWarnings("unused")
	private float calculateMaxUncertainty(int contentIndex, DataRepresentation dataRepresentation) {
		float maxUncertainty = Float.MAX_VALUE;
		for (Integer dimensionID : table.hashDimensionData.get(DataTable.DIMENSION).getDimensionVA()) {
			float cellUncertainty = 0;
			try {
				cellUncertainty =
					table.hashDimensions.get(dimensionID).getFloat(dataRepresentation, contentIndex);
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
