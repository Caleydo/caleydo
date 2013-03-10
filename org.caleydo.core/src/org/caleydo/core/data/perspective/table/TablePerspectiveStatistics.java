/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
/**
 *
 */
package org.caleydo.core.data.perspective.table;

import java.util.ArrayList;
import java.util.Collection;

import org.caleydo.core.data.collection.Histogram;
import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;

/**
 * <p>
 * {@link TablePerspectiveStatistics} provides access and calculates derivable meta-data for the data specified by a
 * {@link TablePerspective}, such as averages, histograms, etc.
 * </p>
 * <p>
 * Everything is calculated lazily.
 * </p>
 * <p>
 * TODO: There is currently no way to mark this dirty once the perspectives in the container or the container itself
 * changed.
 * </p>
 *
 * @author Alexander Lex
 */
public class TablePerspectiveStatistics {

	/** The table perspective to which we compare **/
	private TablePerspective referenceTablePerspective;

	/** The average of all cells in the container */
	private float averageValue = Float.NEGATIVE_INFINITY;

	/** The histogram for the data in this container along the dimensions */
	private Histogram histogram = null;

	/** The fold-change properties of this container with another container */
	private FoldChange foldChange;

	private TTest tTest;

	/**
	 * Optionally it is possible to specify the number of bins for the histogram manually. This should only be done if
	 * there really is a reason for it.
	 */
	private int numberOfBucketsForHistogram = Integer.MIN_VALUE;

	/**
	 * A list of averages across dimensions, one for every record in the data container. Sorted as the virtual array.
	 */
	private ArrayList<Average> averageRecords;

	/** Same as {@link #averageRecords} for dimensions */
	private ArrayList<Average> averageDimensions;

	public TablePerspectiveStatistics(TablePerspective referenceTablePerspective) {
		this.referenceTablePerspective = referenceTablePerspective;
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
		for (Integer recordID : referenceTablePerspective.getRecordPerspective().getVirtualArray()) {

			VirtualArray dimensionVA = referenceTablePerspective.getDimensionPerspective().getVirtualArray();

			if (dimensionVA == null) {
				averageValue = 0;
				return;
			}
			for (Integer dimensionID : dimensionVA) {
				Float value = referenceTablePerspective.getDataDomain().getTable()
						.getNormalizedValue(dimensionID, recordID);
				if (value != null && !Float.isNaN(value)) {
					averageValue += value;
					count++;
				}
			}
		}
		averageValue /= count;
	}

	/**
	 * This is optional! Read more: {@link #numberOfBucketsForHistogram}
	 *
	 * @param numberOfBucketsForHistogram
	 *            setter, see {@link #numberOfBucketsForHistogram}
	 */
	public void setNumberOfBucketsForHistogram(int numberOfBucketsForHistogram) {
		this.numberOfBucketsForHistogram = numberOfBucketsForHistogram;
	}

	/**
	 * @return the histogram, see {@link #histogram}
	 */
	public Histogram getHistogram() {
		if (histogram == null)
			histogram = calculateHistogram(referenceTablePerspective.getDataDomain().getTable(),
					referenceTablePerspective.getRecordPerspective().getVirtualArray(), referenceTablePerspective
							.getDimensionPerspective().getVirtualArray(), numberOfBucketsForHistogram);
		return histogram;
	}

	/**
	 * Calculates a histogram for a given set of virtual arrays. One of the two VA parameters has to be a dimensionVA,
	 * the other must be a recordVA. The order is irrelevant.
	 *
	 * @param table
	 * @param va1
	 * @param va2
	 * @param numberOfBucketsForHistogram
	 * @return
	 */
	public static Histogram calculateHistogram(Table table, VirtualArray va1, VirtualArray va2,
			int numberOfBucketsForHistogram) {

		VirtualArray recordVA, dimensionVA;

		if (va1.getIdType().equals(table.getDataDomain().getRecordIDType())
				&& va2.getIdType().equals(table.getDataDomain().getDimensionIDType())) {
			recordVA = va1;
			dimensionVA = va2;
		} else if (va1.getIdType().equals(table.getDataDomain().getDimensionIDType())
				&& va2.getIdType().equals(table.getDataDomain().getRecordIDType())) {
			recordVA = va2;
			dimensionVA = va1;
		} else {
			throw new IllegalArgumentException("Virtual arrays don't match table");
		}

		if (!table.isDataHomogeneous()) {
			throw new UnsupportedOperationException(
					"Tried to calcualte a set-wide histogram on a not homogeneous table. This makes no sense. Use dimension based histograms instead!");
		}

		int numberOfBuckets;

		if (numberOfBucketsForHistogram != Integer.MIN_VALUE)
			numberOfBuckets = numberOfBucketsForHistogram;
		else
			numberOfBuckets = (int) Math.sqrt(recordVA.size());
		Histogram histogram = new Histogram(numberOfBuckets);

		for (Integer dimensionID : dimensionVA) {
			{
				for (Integer recordID : recordVA) {
					float value = table.getNormalizedValue(dimensionID, recordID);

					if (Float.isNaN(value)) {
						histogram.addNAN(recordID);
					} else {

						// this works because the values in the container are
						// already noramlized
						int bucketIndex = (int) (value * numberOfBuckets);
						if (bucketIndex == numberOfBuckets)
							bucketIndex--;
						histogram.add(bucketIndex, recordID);
					}
				}
			}
		}

		return histogram;
	}

	/**
	 * @return the foldChange, see {@link #foldChange}
	 */
	public FoldChange getFoldChange() {
		if (foldChange == null)
			foldChange = new FoldChange();
		return foldChange;
	}

	/**
	 * @return the tTest, see {@link #tTest}
	 */
	public TTest getTTest() {
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

		VirtualArray dimensionVA = referenceTablePerspective.getDimensionPerspective().getVirtualArray();
		VirtualArray recordVA = referenceTablePerspective.getRecordPerspective().getVirtualArray();

		for (Integer recordID : recordVA) {

			Average averageRecord = calculateAverage(dimensionVA, referenceTablePerspective.getDataDomain().getTable(),
					referenceTablePerspective.getRecordPerspective().getIdType(), recordID);

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

		VirtualArray dimensionVA = referenceTablePerspective.getDimensionPerspective().getVirtualArray();
		VirtualArray recordVA = referenceTablePerspective.getRecordPerspective().getVirtualArray();

		for (Integer dimensionID : dimensionVA) {
			Average averageDimension = calculateAverage(recordVA, referenceTablePerspective.getDataDomain().getTable(),
					referenceTablePerspective.getDimensionPerspective().getIdType(), dimensionID);
			averageDimensions.add(averageDimension);
		}
	}

	/**
	 * <p>
	 * Calculates the average and the standard deviation for the values of one dimension or record in the data table.
	 * Whether the average is calculated for the column or row is determined by the type of the {@link VirtualArray}.
	 * </p>
	 * <p>
	 * The objectID has to be of the "opposing" type, i.e., if the virtualArray is of type {@link VirtualArray}, the id
	 * has to be a dimension id.
	 * </p>
	 * <p>
	 * The std-dev is calculated in the same loop as the average, according to <a
	 * href="http://www.strchr.com/standard_deviation_in_one_pass">this blog.</a>. The problems of this method discussed
	 * there doesn not apply here since we use only values between 0 and 1.}
	 * </p>
	 *
	 *
	 * @param virtualArray
	 * @param table
	 * @param objectID
	 * @return
	 */
	public static Average calculateAverage(VirtualArray virtualArray, Table table, IDType objectIDType, Integer objectID) {
		if (objectID == null || virtualArray.size() == 0)
			return null;
		Average averageDimension = new Average();
		double sumOfValues = 0;
		// sum of squares
		double sqrSum = 0;

		int nrValidValues = 0;

		ATableBasedDataDomain dataDomain = table.getDataDomain();

		IDType virtualArrayIDType = virtualArray.getIdType();
		IDType resolvedVAIDType = dataDomain.getPrimaryIDType(virtualArrayIDType);
		IDType resolvedObjectIDType = dataDomain.getPrimaryIDType(objectIDType);

		if (!resolvedVAIDType.equals(virtualArrayIDType)) {
			PerspectiveInitializationData data = new PerspectiveInitializationData();
			data.setData(virtualArray);
			Perspective tempPerspective = new Perspective(dataDomain, virtualArrayIDType);
			tempPerspective.init(data);
			virtualArray = dataDomain.convertForeignPerspective(tempPerspective).getVirtualArray();
		}

		Collection<Integer> ids;
		if (!resolvedObjectIDType.equals(objectIDType)) {
			ids = IDMappingManagerRegistry.get().getIDMappingManager(objectIDType)
					.getIDAsSet(objectIDType, resolvedObjectIDType, objectID);
		} else {
			ids = new ArrayList<Integer>(1);
			ids.add(objectID);
		}

		if (ids == null)
			return null;
		// IDMappingManager idMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(virtualArrayIDType);

		for (Integer virtualArrayID : virtualArray) {
			Float value;

			for (Integer id : ids) {
				value = table.getDataDomain().getNormalizedValue(resolvedObjectIDType, id, resolvedVAIDType,
						virtualArrayID);

				if (value != null && !value.isNaN()) {
					sumOfValues += value;
					sqrSum += Math.pow(value, 2);
					nrValidValues++;
				}
			}
		}
		averageDimension.arithmeticMean = sumOfValues / nrValidValues;
		averageDimension.standardDeviation = Math.sqrt(sqrSum / nrValidValues
				- (Math.pow(averageDimension.arithmeticMean, 2)));

		return averageDimension;
	}
}
