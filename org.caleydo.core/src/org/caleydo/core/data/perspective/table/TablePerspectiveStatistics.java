/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 *
 */
package org.caleydo.core.data.perspective.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.caleydo.core.data.collection.CategoricalHistogram;
import org.caleydo.core.data.collection.EDataClass;
import org.caleydo.core.data.collection.Histogram;
import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.data.collection.table.CategoricalTable;
import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.IIDTypeMapper;

import com.google.common.collect.ImmutableSet;

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

	private Map<IDType, Map<Set<Integer>, Average>> mapIDTypeToIDToAverage = new HashMap<>();

	/**
	 * The optional number of buckets f or the histogram.
	 */
	private Integer numberOfBucketsForHistogram = null;

	/**
	 * A list of averages across dimensions, one for every record in the data container. Sorted as the virtual array.
	 */
	private ArrayList<Average> averageRecords;

	/** Same as {@link #averageRecords} for dimensions */
	private ArrayList<Average> averageDimensions;

	/**
	 * A list of statistics computed over the data records using all the dimensions
	 */
	private StatContainer statsRecordsFull;

	/**
	 * A list of statistics computed over the data dimensions using all the records
	 */
	private StatContainer statsDimensionsFull;

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
	 * <p>
	 * Calculates a histogram for a given set of virtual arrays. One of the two VA parameters has to be a dimensionVA,
	 * the other must be a recordVA. The order is irrelevant.
	 * </p>
	 * <p>
	 * Automatically calculates the number of buckets in the histogram, which is square root of the size of the record
	 * VA for for numerical, and the number of categories for categorical data.
	 * </p>
	 *
	 * @param table
	 * @param va1
	 * @param va2
	 * @return
	 */
	public static Histogram calculateHistogram(Table table, VirtualArray va1, VirtualArray va2) {
		return calculateHistogram(table, va1, va2, null);
	}

	/**
	 * Wrapper for {@link #calculateHistogram(Table, VirtualArray, VirtualArray)} which lets you manually specify the
	 * number of buckets.
	 */
	public static Histogram calculateHistogram(Table table, VirtualArray va1, VirtualArray va2, Integer numberOfBuckets) {

		if (va1 == null || va2 == null)
			throw new IllegalArgumentException("One of the vas was null " + va1 + ", " + va2);

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

		if (!table.isDataHomogeneous() && dimensionVA.size() > 1) {
			throw new UnsupportedOperationException(
					"Tried to calcualte a multi-set-wide histogram on a not homogeneous table. This makes no sense. Use dimension based histograms instead!");
		}

		if (table instanceof CategoricalTable<?>
				|| (!table.isDataHomogeneous() && table.getDataClass(dimensionVA.get(0), recordVA.get(0)) == EDataClass.CATEGORICAL)) {

			CategoricalClassDescription<?> classDescription = (CategoricalClassDescription<?>) table
					.getDataClassSpecificDescription(dimensionVA.get(0), recordVA.get(0));

			CategoricalHistogram cHistogram = new CategoricalHistogram(classDescription);
			for (Integer dimensionID : dimensionVA) {
				for (Integer recordID : recordVA) {
					cHistogram.add(table.getRaw(dimensionID, recordID), recordID);
				}
			}
			return cHistogram;

		} else {
			if (numberOfBuckets == null) {
				numberOfBuckets = (int) Math.sqrt(recordVA.size());
			}

			Histogram histogram = new Histogram(numberOfBuckets);

			for (Integer dimensionID : dimensionVA) {

				for (Integer recordID : recordVA) {
					float value = table.getNormalizedValue(dimensionID, recordID);

					if (Float.isNaN(value)) {
						histogram.addNAN(recordID);
					} else {
						assert (value <= 1 && value >= 0) || Float.isNaN(value) : "Normalization failed for "
								+ table.toString() + ". Should produce value between 0 and 1 or NAN but was " + value;
						// this works because the values in the container are
						// already normalized
						int bucketIndex = (int) (value * numberOfBuckets);
						if (bucketIndex == numberOfBuckets)
							bucketIndex--;
						histogram.add(bucketIndex, recordID);
					}
				}
			}
			return histogram.optimize();
		}

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

	public Average getAverage(IDType idType, Integer id) {
		return getAverage(idType, Collections.singleton(id));
	}

	public Average getAverage(IDType idType, Collection<Integer> ids) {
		IDType targetIDtype;

		if (referenceTablePerspective.getRecordPerspective().getIdType().getIDCategory().isOfCategory(idType)) {
			targetIDtype = referenceTablePerspective.getRecordPerspective().getIdType();

		} else if (referenceTablePerspective.getDimensionPerspective().getIdType().getIDCategory().isOfCategory(idType)) {
			targetIDtype = referenceTablePerspective.getDimensionPerspective().getIdType();
		} else {
			throw new IllegalArgumentException("IDType specified (" + idType + ") invalid for table perspective "
					+ referenceTablePerspective);
		}
		IDMappingManager mapper = IDMappingManagerRegistry.get().getIDMappingManager(idType);
		IIDTypeMapper<Integer, Integer> idTypeMapper = mapper.getIDTypeMapper(idType, targetIDtype);

		Set<Integer> targetIDs = idTypeMapper.apply(ids);
		if (targetIDs == null || targetIDs.isEmpty())
			return null;
		targetIDs = ImmutableSet.copyOf(targetIDs);
		Map<Set<Integer>, Average> idToAverage = mapIDTypeToIDToAverage.get(targetIDtype);
		if (idToAverage == null) {
			// I assume we're working with approximately 500 values at a time
			idToAverage = new HashMap<>(500);
			mapIDTypeToIDToAverage.put(targetIDtype, idToAverage);
		}
		Average average = idToAverage.get(targetIDs);
		if (average == null) {
			average = calculateAverage(targetIDtype, targetIDs);
			idToAverage.put(targetIDs, average);
		}
		return average;
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

			Average averageRecord = calculateAverage(dimensionVA, referenceTablePerspective.getDataDomain(),
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
			Average averageDimension = calculateAverage(recordVA, referenceTablePerspective.getDataDomain(),
					referenceTablePerspective.getDimensionPerspective().getIdType(), dimensionID);
			averageDimensions.add(averageDimension);
		}
	}

	private Average calculateAverage(IDType idType, Collection<Integer> ids) {
		return calculateAverage(referenceTablePerspective.getOppositePerspective(idType).getVirtualArray(),
				referenceTablePerspective.getDataDomain(), idType, ids);
	}

	public static Average calculateAverage(VirtualArray virtualArray, ATableBasedDataDomain dataDomain,
			IDType objectIDType, Integer objectID) {
		return calculateAverage(virtualArray, dataDomain, objectIDType, Collections.singleton(objectID));
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
	public static Average calculateAverage(VirtualArray virtualArray, ATableBasedDataDomain dataDomain,
			IDType objectIDType, Collection<Integer> objectIDs) {
		if (objectIDs == null || objectIDs.isEmpty() || virtualArray.size() == 0)
			return null;
		Average averageDimension = new Average();
		double sumOfValues = 0;
		// sum of squares
		double sqrSum = 0;

		int nrValidValues = 0;

		IDType resolvedVAIDType = virtualArray.getIdType();
		if (!dataDomain.hasIDType(resolvedVAIDType)) {

			IDType virtualArrayIDType = virtualArray.getIdType();
			resolvedVAIDType = dataDomain.getPrimaryIDType(virtualArrayIDType);

			if (!resolvedVAIDType.equals(virtualArrayIDType)) {
				PerspectiveInitializationData data = new PerspectiveInitializationData();
				data.setData(virtualArray);
				Perspective tempPerspective = new Perspective(dataDomain, virtualArrayIDType);
				tempPerspective.init(data);
				virtualArray = dataDomain.convertForeignPerspective(tempPerspective).getVirtualArray();
			}
		}
		IDType resolvedObjectIDType = objectIDType;
		Collection<Integer> ids;
		if (!dataDomain.hasIDType(resolvedObjectIDType)) {
			resolvedObjectIDType = dataDomain.getPrimaryIDType(objectIDType);

			IIDTypeMapper<Integer, Integer> idTypeMapper = IDMappingManagerRegistry.get()
					.getIDMappingManager(objectIDType).getIDTypeMapper(objectIDType, resolvedObjectIDType);
			ids = idTypeMapper.apply(objectIDs);

			if (ids == null)
				return null;
		} else {
			ids = objectIDs;
		}
		// IDMappingManager idMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(virtualArrayIDType);

		for (Integer virtualArrayID : virtualArray) {
			Float value;

			for (Integer id : ids) {
				value = dataDomain.getNormalizedValue(resolvedObjectIDType, id, resolvedVAIDType, virtualArrayID);

				if (value != null && !value.isNaN()) {
					sumOfValues += value;
					sqrSum += value * value; // Math.pow(value, 2);
					nrValidValues++;
				}
			}
		}
		averageDimension.arithmeticMean = sumOfValues / nrValidValues;
		averageDimension.standardDeviation = Math.sqrt(sqrSum / nrValidValues
				- ((averageDimension.arithmeticMean * averageDimension.arithmeticMean)));// Math.pow(averageDimension.arithmeticMean,
																							// 2)));

		return averageDimension;
	}

	public static StatContainer computeStats(boolean isFull) {
		StatContainer resultStatContainer = new StatContainer();
		return resultStatContainer;
	}

	/**
	 * @return the statsRecordsFull
	 */
	public StatContainer getStatsRecordsFull() {
		if (statsRecordsFull == null) {
			statsRecordsFull = StatisticsUtils.computeFullStatContainer();
		}
		return statsRecordsFull;
	}

	/**
	 * @param statsRecordsFull
	 *            the statsRecordsFull to set
	 */
	public void setStatsRecordsFull(StatContainer statsRecordsFull) {
		this.statsRecordsFull = statsRecordsFull;
	}

	/**
	 * @return the statsDimensionsFull
	 */
	public StatContainer getStatsDimensionsFull() {
		if (statsDimensionsFull == null) {
			statsDimensionsFull = StatisticsUtils.computeFullStatContainer();
		}
		return statsDimensionsFull;
	}

	/**
	 * @param statsDimensionsFull
	 *            the statsDimensionsFull to set
	 */
	public void setStatsDimensionsFull(StatContainer statsDimensionsFull) {
		this.statsDimensionsFull = statsDimensionsFull;
	}

}
