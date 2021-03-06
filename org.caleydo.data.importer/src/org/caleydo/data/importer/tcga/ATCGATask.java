/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.data.importer.tcga;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.RecursiveTask;
import java.util.logging.Logger;

import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.io.DataLoader;
import org.caleydo.core.serialize.ProjectManager;
import org.caleydo.core.serialize.ProjectMetaData;
import org.caleydo.data.importer.tcga.model.TCGADataSet;
import org.caleydo.data.importer.tcga.model.TCGADataSets;
import org.caleydo.view.tourguide.api.score.ISerializeableScore;
import org.caleydo.view.tourguide.api.score.Scores;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;

public abstract class ATCGATask extends RecursiveTask<JsonElement> {
	private static final long serialVersionUID = 6349085075502142673L;
	private static final Logger log = Logger.getLogger(ATCGATask.class.getName());

	public static class AdditionalInfo {
		private AdditionalPerspectiveInfo gene;
		private AdditionalPerspectiveInfo sample;

		public AdditionalInfo(ATableBasedDataDomain d) {
			gene = new AdditionalPerspectiveInfo(d.getTable(), false);
			sample = new AdditionalPerspectiveInfo(d.getTable(), true);
		}

		/**
		 * @return the gene, see {@link #gene}
		 */
		public AdditionalPerspectiveInfo getGene() {
			return gene;
		}

		/**
		 * @return the sample, see {@link #sample}
		 */
		public AdditionalPerspectiveInfo getSample() {
			return sample;
		}
	}

	public static class AdditionalPerspectiveInfo {
		private int count;
		private List<String> groupings = new ArrayList<>();

		public AdditionalPerspectiveInfo(Table table, boolean record) {
			this.count = record ? table.depth() : table.size();
			Iterable<String> list = record ? table.getRecordPerspectiveIDs() : table.getDimensionPerspectiveIDs();

			for (String id : list) {
				Perspective p = record ? table.getRecordPerspective(id) : table.getDimensionPerspective(id);
				if (p.isPrivate())
					continue;
				if (p.equals(record ? table.getDefaultRecordPerspective(false) : table
						.getDefaultRecordPerspective(false)))
					continue;
				if (p.getLabel().equalsIgnoreCase("ungrouped"))
					continue;
				groupings.add(p.getLabel());
			}
		}

		/**
		 * @return the count, see {@link #count}
		 */
		public int getCount() {
			return count;
		}

		/**
		 * @return the groupings, see {@link #groupings}
		 */
		public List<String> getGroupings() {
			return groupings;
		}
	}

	protected static boolean saveProject(Collection<ATableBasedDataDomain> dataDomains, String projectOutputPath,
			ProjectMetaData metaData) {
		try {
			ProjectManager.save(projectOutputPath, true, dataDomains, metaData).run(new NullProgressMonitor());
			return true;
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}

	protected static Collection<ATableBasedDataDomain> loadProject(TCGADataSets project) {
		Collection<ATableBasedDataDomain> dataDomains = new ArrayList<>();
		for (TCGADataSet desc : project) {
			try {
			ATableBasedDataDomain dataDomain = DataLoader.loadData(desc.getDescription(), new NullProgressMonitor());
			if (dataDomain == null)
				continue;
			dataDomains.add(dataDomain);
			desc.setDataDomain(dataDomain);
			} catch (Exception e) {
				continue;
			}
		}
		return dataDomains;
	}

	protected static void cleanUp(Collection<? extends IDataDomain> dataDomains) {
		log.info("cleanup " + dataDomains.size() + " data domains");
		for (IDataDomain dataDomain : dataDomains)
			DataDomainManager.get().unregister(dataDomain);
		// remove external scores
		final Scores scores = Scores.get();
		for (ISerializeableScore s : Lists.newArrayList(scores.getPersistentScores()))
			scores.removePersistentScore(s);
	}
}
