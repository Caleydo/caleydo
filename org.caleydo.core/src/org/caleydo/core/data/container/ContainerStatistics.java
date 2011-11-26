/**
 * 
 */
package org.caleydo.core.data.container;

import java.util.ArrayList;

import org.caleydo.core.data.collection.Histogram;
import org.caleydo.core.data.collection.dimension.DataRepresentation;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;

/**
 * <p>
 * {@link ContainerStatistics} provides access and calculates derivable meta-data for the data specified by a
 * {@link DataContainer}, such as averages, histograms, etc.
 * </p>
 * <p>
 * Everything is calculated lazily.
 * </p>
 * <p>
 * TODO: There is currently no way to mark this dirty once the perspectives in the container or the container
 * itself changed.
 * </p>
 * 
 * @author Alexander Lex
 */
public class ContainerStatistics {
	private DataContainer container;

	/** The average of all cells in the container */
	private float averageValue = Float.NEGATIVE_INFINITY;

	/** The histogram for the data in this container along the dimensions */
	private Histogram histogram = null;

	/** The fold-change properties of this container with another container */
	private FoldChange foldChange;

	private TTest tTest;

	/**
	 * A list of averages across dimensions, one for every record in the data container. Sorted as the virtual
	 * array.
	 */
	private ArrayList<Average> averageRecords;
	/** Same as {@link #averageRecords} for dimensionse */
	private ArrayList<Average> averageDimensions;

	public ContainerStatistics(DataContainer container) {
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
		averageValue = 0;
		int count = 0;
		for (Integer contenID : container.getRecordPerspective().getVirtualArray()) {

			DimensionVirtualArray dimensionVA = container.getDimensionPerspective().getVirtualArray();

			if (dimensionVA == null) {
				averageValue = 0;
				return;
			}
			for (Integer dimensionID : dimensionVA) {
				float value =
					container.getDataDomain().getTable()
						.getFloat(DataRepresentation.NORMALIZED, contenID, dimensionID);
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
		if (!container.getDataDomain().getTable().isDataHomogeneous()) {
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
						container.getDataDomain().getTable()
							.getFloat(DataRepresentation.NORMALIZED, recordID, dimensionID);

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

	/**
	 * Access anything related to fold-changes between this container and others
	 */
	public FoldChange foldChange() {
		if (foldChange == null)
			foldChange = new FoldChange();
		return foldChange;
	}

	/**
	 * Access anything related to t-tests
	 * 
	 * @return
	 */
	public TTest tTest() {
		if (tTest == null)
			tTest = new TTest();
		return tTest;
	}

	/**
	 * @return the averageRecords, see {@link #averageRecords}
	 */
	public ArrayList<Average> getAverageRecords() {
		if (averageRecords == null)
			calculateAverageRecords();
		return averageRecords;
	}

	/**
	 * Calculates the arithmetic mean and the standard deviation from the arithmetic mean of the records
	 */
	private void calculateAverageRecords() {
		averageRecords = new ArrayList<Average>();

		DimensionVirtualArray dimensionVA = container.getDimensionPerspective().getVirtualArray();
		RecordVirtualArray recordVA = container.getRecordPerspective().getVirtualArray();
		DataTable table = container.getDataDomain().getTable();

		for (Integer recordID : recordVA) {
			Average averageRecord = new Average();
			double sumOfValues = 0;
			double sumDeviation = 0;

			int nrValidValues = 0;
			for (Integer dimensionID : dimensionVA) {
				Float value = table.getFloat(DataRepresentation.NORMALIZED, recordID, dimensionID);
				if (!value.isNaN()) {
					sumOfValues += value;
					nrValidValues++;
				}
			}
			averageRecord.arithmeticMean = sumOfValues / nrValidValues;

			for (Integer dimensionID : dimensionVA) {
				Float value = table.getFloat(DataRepresentation.NORMALIZED, recordID, dimensionID);
				if (!value.isNaN()) {
					sumDeviation = Math.pow(-averageRecord.arithmeticMean, 2);
				}
			}
			averageRecord.standardDeviation = Math.sqrt(sumDeviation / nrValidValues);
			averageRecords.add(averageRecord);
		}
	}

	/**
	 * @return the averageRecords, see {@link #averageRecords}
	 */
	public ArrayList<Average> getAverageDimensions() {
		if (averageDimensions == null)
			calculateAverageDimensions();
		return averageDimensions;
	}

	/**
	 * Calculates the arithmetic mean and the standard deviation from the arithmetic mean of the dimensions
	 */
	private void calculateAverageDimensions() {
		averageDimensions = new ArrayList<Average>();

		DimensionVirtualArray dimensionVA = container.getDimensionPerspective().getVirtualArray();
		RecordVirtualArray recordVA = container.getRecordPerspective().getVirtualArray();
		DataTable table = container.getDataDomain().getTable();

		for (Integer dimensionID : dimensionVA) {
			Average averageDimension = new Average();
			double sumOfValues = 0;
			double sumDeviation = 0;

			int nrValidValues = 0;
			for (Integer recordID : recordVA) {
				Float value = table.getFloat(DataRepresentation.NORMALIZED, recordID, dimensionID);
				if (!value.isNaN()) {
					sumOfValues += value;
					nrValidValues++;
				}
			}
			averageDimension.arithmeticMean = sumOfValues / nrValidValues;

			for (Integer recordID : recordVA) {
				Float value = table.getFloat(DataRepresentation.NORMALIZED, recordID, dimensionID);
				if (!value.isNaN()) {
					sumDeviation = Math.pow(-averageDimension.arithmeticMean, 2);
				}
			}
			averageDimension.standardDeviation = Math.sqrt(sumDeviation / nrValidValues);
			averageDimensions.add(averageDimension);
		}
	}
}
