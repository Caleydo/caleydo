package org.caleydo.core.serialize;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.caleydo.core.data.datadomain.ADataDomain;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.graph.tree.ClusterTree;
import org.caleydo.core.data.graph.tree.TreePorter;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.perspective.RecordPerspective;
import org.caleydo.core.manager.BasicInformation;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.util.system.FileOperations;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

/**
 * Restores the state of the application from a given file.
 * 
 * @author Werner Puff
 * @author Alexander Lex
 * @author Marc Streit
 */
public class ProjectLoader {

	/** full path to directory to temporarily store the projects file before zipping */
	public static final String TEMP_PROJECT_ZIP_FOLDER = GeneralManager.CALEYDO_HOME_PATH + "temp_load"
		+ File.separator;

	/**
	 * Loads the project from a specified zip-archive.
	 * 
	 * @param fileName
	 *            name of the file to load the project from
	 * @return initialization data for the application from which it can restore itself
	 */
	public void loadProjectFromZIP(String fileName) {
		FileOperations.deleteDirectory(TEMP_PROJECT_ZIP_FOLDER);

		ZipUtils zipUtils = new ZipUtils();
		zipUtils.unzipToDirectory(fileName, TEMP_PROJECT_ZIP_FOLDER);
	}

	/**
	 * Loads the project from a directory
	 * 
	 * @param dirName
	 *            name of the directory to load the project from
	 * @return initialization data for the application from which it can restore itself
	 */
	public SerializationData loadProjectData(String dirName) {
		try {
			loadPluginData(dirName);
			SerializationData serializationData = loadData(dirName);
			return serializationData;
		}
		catch (Exception e) {
			String message = "Failed to load project from\n" + dirName;
			Logger.log(new Status(IStatus.ERROR, this.toString(), message, e));
			// MessageBox messageBox =
			// new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.OK);
			// messageBox.setText("Project Loading");
			// messageBox.setMessage(message);
			// messageBox.open();
			return null;
		}

	}

	private void loadPluginData(String dirName) throws JAXBException, BundleException {
		JAXBContext context;
		PlugInList plugInList = null;

		context = JAXBContext.newInstance(PlugInList.class);

		Unmarshaller unmarshaller = context.createUnmarshaller();

		File pluginFile = new File(dirName + ProjectSaver.PLUG_IN_LIST_FILE);

		if (!pluginFile.exists()) {
			Logger.log(new Status(Status.INFO, this.toString(), "Could not load plugin data from "
				+ pluginFile));

			return;
		}

		plugInList = (PlugInList) unmarshaller.unmarshal(pluginFile);

		ArrayList<String> plugIns = plugInList.plugIns;
		for (String plugIn : plugIns) {
			Bundle bundle = Platform.getBundle(plugIn);
			if (bundle == null) {
				Logger.log(new Status(IStatus.WARNING, toString(), "Could not load bundle: " + bundle));
				continue;
			}
			bundle.start();
		}
	}

	private SerializationData loadData(String dirName) throws IOException, JAXBException {
		SerializationData serializationData = null;
		SerializationManager serializationManager = GeneralManager.get().getSerializationManager();

		JAXBContext projectContext = serializationManager.getProjectContext();

		Unmarshaller unmarshaller = projectContext.createUnmarshaller();

		GeneralManager.get().setBasicInfo(
			(BasicInformation) unmarshaller.unmarshal(GeneralManager.get().getResourceLoader()
				.getResource(dirName + ProjectSaver.BASIC_INFORMATION_FILE)));

		DataDomainList dataDomainList;

		dataDomainList =
			(DataDomainList) unmarshaller.unmarshal(GeneralManager.get().getResourceLoader()
				.getResource(dirName + ProjectSaver.DATA_DOMAIN_FILE));

		serializationData = new SerializationData();

		for (ADataDomain dataDomain : dataDomainList.getDataDomains()) {

			dataDomain.init();
			// Register data domain by hand because it restored from the serialization and not created via the
			// DataDomainManager
			DataDomainManager.get().register(dataDomain);
			// DataDomainManager usually takes care of that, we need to do it manually for serialization
			

			Thread thread = new Thread(dataDomain, dataDomain.getDataDomainID());
			thread.start();
			if (dataDomain instanceof ATableBasedDataDomain) {

				String extendedDirName = dirName + dataDomain.getDataDomainID() + "_";

				// Overwrite filename with new one in caleydo project (data.csv)
				dataDomain.setFileName(extendedDirName + ProjectSaver.DATA_TABLE_FILE);
				dataDomain.getLoadDataParameters()
					.setFileName(extendedDirName + ProjectSaver.DATA_TABLE_FILE);

				DataDomainSerializationData dataInitializationData = new DataDomainSerializationData();
				dataInitializationData.setDataDomain((ATableBasedDataDomain) dataDomain);

				HashMap<String, RecordPerspective> recordPerspectives =
					new HashMap<String, RecordPerspective>();

				for (String recordPerspectiveID : ((ATableBasedDataDomain) dataDomain)
					.getRecordPerspectiveIDs()) {

					RecordPerspective recordPerspective =
						(RecordPerspective) unmarshaller.unmarshal(GeneralManager.get().getResourceLoader()
							.getResource(extendedDirName + recordPerspectiveID + ".xml"));
					recordPerspective.setDataDomain((ATableBasedDataDomain)dataDomain);
					recordPerspective.setIDType(((ATableBasedDataDomain) dataDomain).getRecordIDType());
					recordPerspectives.put(recordPerspectiveID, recordPerspective);

					if (!recordPerspective.isTreeDefaultTree()) {
						ClusterTree tree =
							loadTree(extendedDirName + recordPerspectiveID + "_tree.xml",
								((ATableBasedDataDomain) dataDomain).getRecordIDType());
						recordPerspective.setTree(tree);
					}

				}

				dataInitializationData.setRecordPerspectiveMap(recordPerspectives);

				HashMap<String, DimensionPerspective> dimensionPerspectives =
					new HashMap<String, DimensionPerspective>();

				for (String dimensionPerspectiveID : ((ATableBasedDataDomain) dataDomain)
					.getDimensionPerspectiveIDs()) {

					DimensionPerspective dimensionPerspective =
						(DimensionPerspective) unmarshaller.unmarshal(GeneralManager.get()
							.getResourceLoader()
							.getResource(extendedDirName + dimensionPerspectiveID + ".xml"));
					dimensionPerspective.setDataDomain((ATableBasedDataDomain) dataDomain);
					dimensionPerspective.setIDType(((ATableBasedDataDomain) dataDomain).getDimensionIDType());
					dimensionPerspectives.put(dimensionPerspectiveID, dimensionPerspective);

					if (!dimensionPerspective.isTreeDefaultTree()) {
						ClusterTree tree =
							loadTree(extendedDirName + dimensionPerspectiveID + "_tree.xml",
								((ATableBasedDataDomain) dataDomain).getDimensionIDType());
						dimensionPerspective.setTree(tree);
					}

				}

				dataInitializationData.setDimensionPerspectiveMap(dimensionPerspectives);

				serializationData.addDataDomainSerializationData(dataInitializationData);
			}
		}

		return serializationData;
	}

	/**
	 * Load trees as specified in loadDataParameters and write them to the table. FIXME: this is not aware of
	 * possibly alternative {@link RecordVAType}s or {@link DimensionVAType}s
	 * 
	 * @param loadDataParameters
	 * @param set
	 */
	private ClusterTree loadTree(String path, IDType idType) throws JAXBException, IOException {
		TreePorter treePorter = new TreePorter();
		ClusterTree tree;
		tree = treePorter.importTree(path, idType);
		return tree;

	}

	public void loadWorkbenchData(String dirName) {
		try {
			File workbenchFile = new File(dirName + ProjectSaver.WORKBENCH_MEMENTO_FILE);

			if (!workbenchFile.exists()) {
				Logger.log(new Status(Status.INFO, this.toString(), "Could not load workbench data from "
					+ workbenchFile));

				return;
			}

			FileOperations.copyFolder(new File(dirName + ProjectSaver.WORKBENCH_MEMENTO_FILE), new File(
				GeneralManager.CALEYDO_HOME_PATH + ProjectSaver.WORKBENCH_MEMENTO_FOLDER
					+ ProjectSaver.WORKBENCH_MEMENTO_FILE));
		}
		catch (IOException e) {
			// throw new IllegalStateException("Could not load workbench data from " + dirName, e);
			Logger.log(new Status(Status.INFO, this.toString(), "Could not load workbench data from "
				+ dirName, e));
		}
	}
}