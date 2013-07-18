/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.impl.algorithm;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math.distribution.TDistributionImpl;
import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.eclipse.core.runtime.IProgressMonitor;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Parametric Gene Set Expression Analysis based on <a href="http://www.biomedcentral.com/1471-2105/6/144">PAGE:
 * Parametric Analysis of Gene Set Enrichment</a> and R package <a
 * href="http://bioconductor.org/packages/2.4/bioc/html/PGSEA.html">PGSEA</a>
 *
 * @author Samuel Gratzl
 *
 */
public class PGSEAAlgorithm extends AGSEAAlgorithm {
	private Map<Integer, Float> foldChanges = Maps.newHashMap();
	private float foldChangesMean;
	private float foldChangesSD;


	public PGSEAAlgorithm(Perspective perspective, Group group) {
		super(perspective, group);
	}

	@Override
	public void init(IProgressMonitor monitor) {
		if (!foldChanges.isEmpty())
			return;

		final Set<Integer> inA = new HashSet<>(perspective.getVirtualArray()
				.getIDsOfGroup(group.getGroupIndex()));
		ATableBasedDataDomain dataDomain = (ATableBasedDataDomain) perspective.getDataDomain();
		Table table = dataDomain.getTable();

		List<Integer> rows = perspective.getVirtualArray().getIDs();
		List<Integer> cols = table.getDefaultDimensionPerspective().getVirtualArray().getIDs();

		Stopwatch w = new Stopwatch().start();

		float sum = 0;
		float squaredSum = 0;

		for (Integer col : cols) {
			// mean of the expressions level of the samples for the given gen.
			float asum = 0;
			int acount = 0;
			float bsum = 0;
			int bcount = 0;
			for (Integer row : rows) {
				Float v = table.getRaw(col, row);
				if (v == null || v.isNaN() || v.isInfinite())
					continue;
				if (inA.contains(row)) {
					asum += v;
					acount++;
				} else {
					bsum += v;
					bcount++;
				}

			}
			if (monitor.isCanceled()) {
				foldChanges.clear(); // undo init
				return;
			}

			// now some kind of correlation between the two
			float foldChange = Statistics.foldChange((asum / acount), (bsum / bcount));
			Set<Integer> davids = dim2primary.apply(col);
			if (davids == null)
				continue;
			sum += foldChange * davids.size();
			squaredSum += (foldChange * foldChange) * davids.size();
			for (Integer david : davids)
				foldChanges.put(david, foldChange);
		}
		foldChangesMean = sum / foldChanges.size();
		foldChangesSD = (float) Math.sqrt(squaredSum / foldChanges.size() - foldChangesMean * foldChangesMean);
		System.out.println(w);
	}

	@Override
	protected float computeImpl(Set<Integer> geneSet, IProgressMonitor monitor) {
		float sum = 0;
		int m = 0;
		for (Integer gene : geneSet) {
			Float f = foldChanges.get(gene);
			if (f == null)
				continue;
			m++;
			sum += f;
		}
		if (m == 0)
			return Float.NaN;
		float Sm = sum / m;

		float z = (float) ((Sm - foldChangesMean) * Math.sqrt(m) / foldChangesSD);
		return z;
	}

	@Override
	protected float computePValueImpl(Set<Integer> geneSet, IProgressMonitor monitor) {
		float z = compute(geneSet, monitor);
		if (Float.isNaN(z))
			return Float.NaN;
		int m = Sets.intersection(foldChanges.keySet(), geneSet).size();
		if (m == 0)
			return Float.NaN;
		TDistributionImpl t = new TDistributionImpl(m);
		float pValue = (float) t.density(z);
		return pValue;
	}

	@Override
	public String getAbbreviation() {
		return "PGSEA";
	}

	@Override
	public String getDescription() {
		return "PGSEA score against ";
	}
}
