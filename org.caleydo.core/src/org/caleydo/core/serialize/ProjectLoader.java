/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 * 
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.serialize;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.caleydo.core.data.datadomain.ADataDomain;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.graph.tree.ClusterTree;
import org.caleydo.core.data.graph.tree.TreePorter;
import org.caleydo.core.data.perspective.variable.DimensionPerspective;
import org.caleydo.core.data.perspective.variable.RecordPerspective;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.IDTypeInitializer;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.manager.BasicInformation;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.util.system.FileOperations;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.internal.WorkbenchPlugin;
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

	/**
	 * full path to directory to temporarily store the projects file before
	 * zipping
	 */
	public static final String TEMP_PROJECT_ZIP_FOLDER = GeneralManager.CALEYDO_HOME_PATH
			+ "temp_load" + File.separator;

	/**
	 * Loads the project from a specified zip-archive.
	 * 
	 * @param fileName name of the file to load the project from
	 * @return initialization data for the application from which it can restore
	 *         itself
	 */
	public void loadProjectFromZIP(String fileName) {
		FileOperations.deleteDirectory(TEMP_PROJECT_ZIP_FOLDER);
		// FileOperations.deleteDirectory(GeneralManager.CALEYDO_HOME_PATH);

		ZipUtils zipUtils = new ZipUtils();
		zipUtils.unzipToDirectory(fileName, TEMP_PROJECT_ZIP_FOLDER);
	}

	/**
	 * Loads the project from a directory
	 * 
	 * @param dirName name of the directory to load the project from
	 * @return initialization data for the application from which it can restore
	 *         itself
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
			e.printStackTrace();
			// MessageBox messageBox =
			// new
			// MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
			// SWT.OK);
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
			Logger.log(new Status(Status.INFO, this.toString(),
					"Could not load plugin data from " + pluginFile));

			return;
		}

		plugInList = (PlugInList) unmarshaller.unmarshal(pluginFile);

		ArrayList<String> plugIns = plugInList.plugIns;
		for (String plugIn : plugIns) {
			Bundle bundle = Platform.getBundle(plugIn);
			if (bundle == null) {
				Logger.log(new Status(IStatus.WARNING, toString(), "Could not load bundle: "
						+ plugIn));
				continue;
			}
			bundle.start();
		}
	}

	private SerializationData loadData(String dirName) throws IOException, JAXBException {
		SerializationData serializationData = null;
		SerializationManager serializationManager = GeneralManager.get()
				.getSerializationManager();

		JAXBContext projectContext = serializationManager.getProjectContext();

		Unmarshaller unmarshaller = projectContext.createUnmarshaller();

		GeneralManager.get().setBasicInfo(
				(BasicInformation) unmarshaller.unmarshal(GeneralManager.get()
						.getResourceLoader()
						.getResource(dirName + ProjectSaver.BASIC_INFORMATION_FILE)));

		DataDomainList dataDomainList;

		dataDomainList = (DataDomainList) unmarshaller.unmarshal(GeneralManager.get()
				.getResourceLoader().getResource(dirName + ProjectSaver.DATA_DOMAIN_FILE));

		serializationData = new SerializationData();

		for (ADataDomain dataDomain : dataDomainList.getDataDomains()) {
			DataSetDescription dataSetDescription = dataDomain.getDataSetDescription();

			if (dataDomain.getDataDomainType().equals("org.caleydo.datadomain.genetic"))
				DataDomainManager.get().initalizeDataDomain("org.caleydo.datadomain.genetic");

			IDTypeInitializer.initIDs(dataSetDescription);
			dataDomain.init();
			// Register data domain by hand because it restored from the
			// serialization and not created via the
			// DataDomainManager
			DataDomainManager.get().register(dataDomain);
			// DataDomainManager usually takes care of that, we need to do it
			// manually for serialization

			Thread thread = new Thread(dataDomain, dataDomain.getDataDomainID());
			thread.start();
			if (dataDomain instanceof ATableBasedDataDomain) {

				String extendedDirName = dirName + dataDomain.getDataDomainID() + "_";

				// Overwrite filename with new one in caleydo project (data.csv)
				dataDomain.getDataSetDescription().setDataSourcePath(
						extendedDirName + ProjectSaver.DATA_TABLE_FILE);

				DataDomainSerializationData dataInitializationData = new DataDomainSerializationData();
				dataInitializationData.setDataDomain((ATableBasedDataDomain) dataDomain);

				HashMap<String, RecordPerspective> recordPerspectives = new HashMap<String, RecordPerspective>();

				GeneralManager.get().getSWTGUIManager()
						.setProgressBarText("Loading groupings for: " + dataDomain.getLabel());

				Set<String> recordPerspectiveIDs = ((ATableBasedDataDomain) dataDomain)
						.getRecordPerspectiveIDs();
				Set<String> dimensionPerspectiveIDs = ((ATableBasedDataDomain) dataDomain)
						.getDimensionPerspectiveIDs();

				int nrPerspectives = recordPerspectiveIDs.size()
						+ dimensionPerspectiveIDs.size();
				float progressBarFactor = 100f / nrPerspectives;
				int perspectiveCount = 0;
				for (String recordPerspectiveID : recordPerspectiveIDs) {

					RecordPerspective recordPerspective = (RecordPerspective) unmarshaller
							.unmarshal(GeneralManager
									.get()
									.getResourceLoader()
									.getResource(
											extendedDirName + recordPerspectiveID + ".xml"));
					recordPerspective.setDataDomain((ATableBasedDataDomain) dataDomain);
					recordPerspective.setIDType(((ATableBasedDataDomain) dataDomain)
							.getRecordIDType());
					recordPerspectives.put(recordPerspectiveID, recordPerspective);

					ClusterTree tree = loadTree(extendedDirName + recordPerspectiveID
							+ "_tree.xml",
							((ATableBasedDataDomain) dataDomain).getRecordIDType());
					if (tree != null)
						recordPerspective.setTree(tree);

					GeneralManager
							.get()
							.getSWTGUIManager()
							.setProgressBarPercentage(
									(int) (progressBarFactor * perspectiveCount));
					perspectiveCount++;
				}

				dataInitializationData.setRecordPerspectiveMap(recordPerspectives);

				HashMap<String, DimensionPerspective> dimensionPerspectives = new HashMap<String, DimensionPerspective>();

				for (String dimensionPerspectiveID : dimensionPerspectiveIDs) {

					DimensionPerspective dimensionPerspective = (DimensionPerspective) unmarshaller
							.unmarshal(GeneralManager
									.get()
									.getResourceLoader()
									.getResource(
											extendedDirName + dimensionPerspectiveID + ".xml"));
					dimensionPerspective.setDataDomain((ATableBasedDataDomain) dataDomain);
					dimensionPerspective.setIDType(((ATableBasedDataDomain) dataDomain)
							.getDimensionIDType());
					dimensionPerspectives.put(dimensionPerspectiveID, dimensionPerspective);

					ClusterTree tree = loadTree(extendedDirName + dimensionPerspectiveID
							+ "_tree.xml",
							((ATableBasedDataDomain) dataDomain).getDimensionIDType());
					dimensionPerspective.setTree(tree);
					GeneralManager
							.get()
							.getSWTGUIManager()
							.setProgressBarPercentage(
									(int) (progressBarFactor * perspectiveCount));
					perspectiveCount++;

				}

				dataInitializationData.setDimensionPerspectiveMap(dimensionPerspectives);

				serializationData.addDataDomainSerializationData(dataInitializationData);

			}
		}

		return serializationData;
	}

	/**
	 * Load trees as specified in loadDataParameters and write them to the
	 * table. FIXME: this is not aware of possibly alternative
	 * {@link RecordVAType}s or {@link DimensionVAType}s
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
			// clear old workbench file

			IPath path = WorkbenchPlugin.getDefault().getDataLocation();
			path = path.append(ProjectSaver.WORKBENCH_MEMENTO_FILE);
			FileOperations.deleteDirectory(path.toOSString());

			File workbenchFile = new File(dirName + ProjectSaver.WORKBENCH_MEMENTO_FILE);

			if (!workbenchFile.exists()) {
				Logger.log(new Status(Status.INFO, this.toString(),
						"Could not load workbench data from " + workbenchFile));
				return;
			}

			// Create .metadata folder if it does not exist yet. This is the
			// case when Caleydo is started the first time.
			File f = new File(ProjectSaver.WORKBENCH_MEMENTO_FOLDER);
			if (f.exists() == false) {
				f.mkdirs();
			}

			FileOperations.copyFolder(new File(dirName + ProjectSaver.WORKBENCH_MEMENTO_FILE),
					new File(path.toOSString()));
		}
		catch (IOException e) {
			// throw new
			// IllegalStateException("Could not load workbench data from " +
			// dirName, e);
			Logger.log(new Status(Status.INFO, this.toString(),
					"Could not load workbench data from " + dirName, e));
		}
	}
}