/**
 * 
 */
package org.caleydo.core.data.container;

import org.caleydo.core.data.collection.Histogram;
import org.caleydo.core.data.collection.dimension.DataRepresentation;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;

/**
 * This class is intended to calculate and hold all kins of derived meta-data for a container, like averages,
 * histograms, etc.
 * 
 * @author Alexander Lex
 */
public class ContainerStatistics {
	private ADataContainer container;

	/** The average of all cells in the container */
	private float averageValue = Float.NEGATIVE_INFINITY;

	private Histogram histogram = null;

	/**
	 * 
	 */
	public ContainerStatistics(ADataContainer container) {
		this.container = container;
	}

	/**
	 * @return the averageValue, see {@link #averageValue}
	 */
	public double getAverageValue() {
		if (Float.isInfinite(averageValue))
			calculateAverageValue();
		return averageValue;
	}

	private void calculateAverageValue() {
		int count = 0;
		for (Integer contenID : container.getRecordPerspective().getVirtualArray()) {

			DimensionVirtualArray dimensionVA = container.getDimensionPerspective().getVirtualArray();

			if (dimensionVA == null) {
				averageValue = 0;
				return;
			}
			for (Integer dimensionID : dimensionVA) {
				float value =
					container.getDataDomain().getTable().get(dimensionID)
						.getFloat(DataRepresentation.NORMALIZED, contenID);
				if (!Float.isNaN(value)) {
					averageValue += value;
					count++;
				}
			}
		}
		averageValue /= count;
	}

	public Histogram getHistogram() {
		if (histogram == null)
			calculateHistogram();
		return histogram;
	}

	private void calculateHistogram() {
		if (!container.getDataDomain().getTable().isSetHomogeneous()) {
			throw new UnsupportedOperationException(
				"Tried to calcualte a set-wide histogram on a not homogeneous table. This makes no sense. Use dimension based histograms instead!");
		}

		int numberOfBuckets = (int) Math.sqrt(container.getRecordPerspective().getVirtualArray().size());
		histogram = new Histogram(numberOfBuckets);
		for (int iCount = 0; iCount < numberOfBuckets; iCount++) {
			histogram.add(0);
		}

		// FloatCContainerIterator iterator =
		// ((FloatCContainer) hashCContainers.get(DataRepresentation.NORMALIZED)).iterator(recordVA);
		for (Integer dimensionID : container.getDimensionPerspective().getVirtualArray()) {
			{
				for (Integer recordID : container.getRecordPerspective().getVirtualArray()) {
					float value =
						container.getDataDomain().getTable().get(dimensionID)
							.getFloat(DataRepresentation.NORMALIZED, recordID);

					// this works because the values in the container are already noramlized
					int iIndex = (int) (value * numberOfBuckets);
					if (iIndex == numberOfBuckets)
						iIndex--;
					Integer iNumOccurences = histogram.get(iIndex);
					histogram.set(iIndex, ++iNumOccurences);
				}
			}
		}
	}
}
