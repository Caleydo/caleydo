/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.correlation.wilcoxon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.math3.stat.inference.MannWhitneyUTest;
import org.apache.commons.math3.stat.ranking.NaNStrategy;
import org.apache.commons.math3.stat.ranking.TiesStrategy;
import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.data.collection.column.container.CategoricalClassDescription.ECategoryType;
import org.caleydo.core.data.collection.column.container.CategoryProperty;
import org.caleydo.core.io.NumericalProperties;
import org.caleydo.view.enroute.correlation.CategoricalDataClassifier;
import org.caleydo.view.enroute.correlation.DataCellInfo;
import org.caleydo.view.enroute.correlation.IDataClassifier;
import org.caleydo.view.enroute.correlation.NumericalDataClassifier;
import org.caleydo.view.enroute.correlation.SimpleCategory;
import org.caleydo.view.enroute.correlation.SimpleIDClassifier;

import com.google.common.collect.Sets;

/**
 * @author Christian
 *
 */
public final class WilcoxonUtil {

	public static class WilcoxonResult {
		public double p;
		public double u;
		public IDataClassifier classifier;
		public SimpleIDClassifier derivedClassifier;

		public WilcoxonResult() {

		}

		/**
		 * @param p
		 * @param u
		 * @param classifier
		 * @param derivedClassifier
		 */
		public WilcoxonResult(double p, double u, IDataClassifier classifier, SimpleIDClassifier derivedClassifier) {
			super();
			this.p = p;
			this.u = u;
			this.classifier = classifier;
			this.derivedClassifier = derivedClassifier;
		}

	}

	private WilcoxonUtil() {
	}

	public static List<WilcoxonResult> calcAllWilcoxonCombinations(DataCellInfo sourceInfo, DataCellInfo targetInfo) {
		List<WilcoxonResult> results = new ArrayList<>();

		Object description = sourceInfo.dataDomain.getDataClassSpecificDescription(sourceInfo.rowIDType,
				sourceInfo.rowID, sourceInfo.columnPerspective.getIdType(), sourceInfo.columnPerspective
						.getVirtualArray().get(0));

		MannWhitneyUTest test = new MannWhitneyUTest(NaNStrategy.REMOVED, TiesStrategy.AVERAGE);

		if (description == null || description instanceof NumericalProperties) {
			List<Double> values = getSampleValues(sourceInfo, new HashSet<Object>(sourceInfo.columnPerspective
					.getVirtualArray().getIDs()));
			Collections.sort(values);

			for (Double threshold : values) {
				NumericalDataClassifier classifier = new NumericalDataClassifier(threshold.floatValue(),
						WilcoxonRankSumTestWizard.CLASSIFICATION_COLORS_1.getFirst(),
						WilcoxonRankSumTestWizard.CLASSIFICATION_COLORS_1.getSecond(),
						Character.toString((char) 0x2264) + " " + threshold, "> " + threshold);

				SimpleIDClassifier derivedClassifier = createDerivedClassifier(classifier, sourceInfo);
				double[] values1 = getSampleValuesArray(targetInfo, derivedClassifier.getClass1IDs());
				double[] values2 = getSampleValuesArray(targetInfo, derivedClassifier.getClass2IDs());

				if (values1.length > 0 && values2.length > 0) {
					double u = test.mannWhitneyU(values1, values2);
					double p = test.mannWhitneyUTest(values1, values2);
					results.add(new WilcoxonResult(p, u, classifier, derivedClassifier));
				}
			}

		} else {

			CategoricalClassDescription<?> classDesc = (CategoricalClassDescription<?>) description;
			Set<Object> allCategories = new LinkedHashSet<>(classDesc.size());
			for (CategoryProperty<?> property : classDesc.getCategoryProperties()) {
				allCategories.add(property.getCategory());
			}
			Set<Set<Object>> combinations = new HashSet<>();
			calcCategoryCombinations(combinations, new HashSet<>(), allCategories);

			Set<Set<Object>> usedClass1s = new HashSet<>();

			for (Set<Object> class1 : combinations) {
				Set<Object> class2 = Sets.symmetricDifference(class1, allCategories);
				if (!class2.isEmpty() && !containsSet(usedClass1s, class2)) {
					usedClass1s.add(class1);
					CategoricalDataClassifier classifier = new CategoricalDataClassifier(class1, class2,
							WilcoxonRankSumTestWizard.CLASSIFICATION_COLORS_1.getFirst(),
							WilcoxonRankSumTestWizard.CLASSIFICATION_COLORS_1.getSecond(), getCategoryName(class1,
									classDesc), getCategoryName(class2, classDesc), classDesc);

					SimpleIDClassifier derivedClassifier = createDerivedClassifier(classifier, sourceInfo);
					double[] values1 = getSampleValuesArray(targetInfo, derivedClassifier.getClass1IDs());
					double[] values2 = getSampleValuesArray(targetInfo, derivedClassifier.getClass2IDs());

					if (values1.length > 0 && values2.length > 0) {
						double u = test.mannWhitneyU(values1, values2);
						double p = test.mannWhitneyUTest(values1, values2);
						results.add(new WilcoxonResult(p, u, classifier, derivedClassifier));
					}
				}
			}

		}

		return results;
	}

	private static String getCategoryName(Set<Object> categories, CategoricalClassDescription<?> classDesc) {
		StringBuilder b = new StringBuilder();
		int i = 0;
		for (Object category : categories) {
			String categoryName = classDesc.getCategoryProperty(category).getCategoryName();
			b.append(categoryName);
			if (i < categories.size() - 1)
				b.append(", ");
			i++;
		}

		return b.toString();
	}

	private static boolean containsSet(Set<Set<Object>> pool, Set<Object> set) {
		for (Set<Object> s : pool) {
			if (Sets.symmetricDifference(s, set).isEmpty()) {
				return true;
			}
		}
		return false;
	}

	private static void calcCategoryCombinations(Set<Set<Object>> combinations, Set<Object> previousSet,
			Set<Object> categoryPool) {

		Set<Object> currentPool = new LinkedHashSet<>(categoryPool);

		for (Object category : categoryPool) {
			Set<Object> currentSet = new LinkedHashSet<>(previousSet);
			currentSet.add(category);
			combinations.add(currentSet);
			currentPool.remove(category);
			if (!currentPool.isEmpty()) {
				calcCategoryCombinations(combinations, currentSet, new LinkedHashSet<>(currentPool));
			}
		}
	}

	public static List<Double> getSampleValues(DataCellInfo info, Iterable<Object> columnIDs) {
		Object description = info.dataDomain.getDataClassSpecificDescription(info.rowIDType, info.rowID,
				info.columnPerspective.getIdType(), info.columnPerspective.getVirtualArray().get(0));

		List<Double> values = new ArrayList<>();

		if (description == null || description instanceof NumericalProperties) {

			for (Object columnID : columnIDs) {
				Number value = (Number) info.dataDomain.getRaw(info.columnPerspective.getIdType(), (Integer) columnID,
						info.rowIDType, info.rowID);
				if (value != null && !Double.isNaN(value.doubleValue())) {
					values.add(value.doubleValue());
				}
			}
			return values;

		} else if (description instanceof CategoricalClassDescription) {

			CategoricalClassDescription<?> desc = (CategoricalClassDescription<?>) description;
			if (desc.getCategoryType() == ECategoryType.NOMINAL) {
				throw new UnsupportedOperationException("Wilcoxon rank-sum test cannot be applied to nominal data");
			} else {
				for (Object columnID : columnIDs) {
					Object category = info.dataDomain.getRaw(info.columnPerspective.getIdType(), (Integer) columnID,
							info.rowIDType, info.rowID);
					if (category != null) {
						values.add((new Integer(desc.indexOf(category))).doubleValue());
					}
				}
				return values;
			}
		}

		return values;
	}

	public static double[] getSampleValuesArray(DataCellInfo info, Iterable<Object> columnIDs) {
		return asArray(getSampleValues(info, columnIDs));
	}

	private static double[] asArray(List<Double> values) {
		double[] array = new double[values.size()];
		for (int i = 0; i < values.size(); i++) {
			array[i] = values.get(i);
		}
		return array;
	}

	public static SimpleIDClassifier createDerivedClassifier(IDataClassifier classifier, DataCellInfo info) {
		List<SimpleCategory> classes = classifier.getDataClasses();
		List<Set<Object>> idSets = new ArrayList<>(classes.size());
		// There will be no more than 2
		idSets.add(new HashSet<>());
		idSets.add(new HashSet<>());
		for (int columnID : info.columnPerspective.getVirtualArray()) {
			Object value = info.dataDomain.getRaw(info.columnPerspective.getIdType(), columnID, info.rowIDType,
					info.rowID);
			SimpleCategory c = classifier.apply(value);
			if (c != null) {
				Set<Object> idSet = idSets.get(classes.indexOf(c));
				idSet.add(columnID);
			}
		}

		SimpleCategory class1 = new SimpleCategory(classes.get(0).name + " in first data block",
				WilcoxonRankSumTestWizard.CLASSIFICATION_COLORS_2.getFirst());
		SimpleCategory class2 = new SimpleCategory(classes.get(1).name + " in first data block",
				WilcoxonRankSumTestWizard.CLASSIFICATION_COLORS_2.getSecond());

		return new SimpleIDClassifier(idSets.get(0), idSets.get(1), info.columnPerspective.getIdType(), class1, class2);
	}

}
