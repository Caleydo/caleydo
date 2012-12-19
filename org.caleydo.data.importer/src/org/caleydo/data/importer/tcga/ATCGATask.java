package org.caleydo.data.importer.tcga;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.RecursiveTask;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.data.importer.tcga.utils.GroupingListCreator;
import org.caleydo.data.importer.tcga.utils.IOUtils;

public abstract class ATCGATask extends RecursiveTask<String> {
	private static final long serialVersionUID = 6349085075502142673L;

	protected static void generateJNLP(File jnlpFile, String projectRemoteOutputURL) {
		try {
			// Generate jnlp file from jnlp template
			String template = IOUtils.readAll(ATCGATask.class.getResourceAsStream("/resources/caleydo.jnlp"));
			template = template.replaceAll("CALEYDO_PROJECT_URL", projectRemoteOutputURL);
			template = template.replaceAll("JNLP_NAME", jnlpFile.getName());
			IOUtils.dumpToFile(template, jnlpFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected static String getAdditionalInfo(ATableBasedDataDomain dataDomain) {
		return "{\"gene\":{\"count\":\"" + dataDomain.getTable().getMetaData().size() + "\",\"groupings\":["
				+ GroupingListCreator.getDimensionGroupingList(dataDomain) + "]},\"sample\":{\"count\":\""
				+ dataDomain.getTable().getMetaData().depth() + "\",\"groupings\":["
				+ GroupingListCreator.getRecordGroupingList(dataDomain) + "]}}";
	}

	protected static void cleanUp(Collection<? extends IDataDomain> dataDomains) {
		for (IDataDomain dataDomain : dataDomains)
			DataDomainManager.get().unregister(dataDomain);
	}

}
