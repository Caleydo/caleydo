package org.caleydo.data.importer.tcga;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
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
import org.caleydo.data.importer.tcga.model.TCGADataSet;
import org.caleydo.data.importer.tcga.model.TCGADataSets;
import org.caleydo.data.importer.tcga.utils.IOUtils;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.google.common.io.Files;
import com.google.gson.JsonElement;

public abstract class ATCGATask extends RecursiveTask<JsonElement> {
	private static final long serialVersionUID = 6349085075502142673L;
	private static final Logger log = Logger.getLogger(ATCGATask.class.getSimpleName());

	protected static void generateJNLP(File jnlpFile, String projectRemoteOutputURL) {
		log.info("generating jnlp file: " + jnlpFile.getAbsolutePath());
		try {
			// Generate jnlp file from jnlp template
			String template = IOUtils.readAll(ATCGATask.class.getResourceAsStream("/resources/caleydo.jnlp"));
			template = template.replaceAll("CALEYDO_PROJECT_URL", projectRemoteOutputURL);
			template = template.replaceAll("JNLP_NAME", jnlpFile.getName());
			Files.write(template, jnlpFile, Charset.defaultCharset());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

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
				if (p.equals(record ? table.getDefaultRecordPerspective() : table.getDefaultRecordPerspective()))
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

	protected static boolean saveProject(Collection<ATableBasedDataDomain> dataDomains, String projectOutputPath) {
		try {
			ProjectManager.save(projectOutputPath, true, dataDomains).run(new NullProgressMonitor());
			return true;
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}

	protected static Collection<ATableBasedDataDomain> loadProject(TCGADataSets project) {
		Collection<ATableBasedDataDomain> dataDomains = new ArrayList<>();
		for (TCGADataSet desc : project) {
			ATableBasedDataDomain dataDomain = DataLoader.loadData(desc.getDescription());
			if (dataDomain == null)
				continue;
			dataDomains.add(dataDomain);
			desc.setDataDomain(dataDomain);
		}
		return dataDomains;
	}

	protected static void cleanUp(Collection<? extends IDataDomain> dataDomains) {
		log.info("cleanup " + dataDomains.size() + " data domains");
		for (IDataDomain dataDomain : dataDomains)
			DataDomainManager.get().unregister(dataDomain);
	}
}
