package org.caleydo.core.serialize;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.collection.table.LoadDataParameters;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.manager.BasicInformation;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.ADataDomain;
import org.caleydo.core.manager.datadomain.ATableBasedDataDomain;
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
 */
public class ProjectLoader {

	/** full path to directory to temporarily store the projects file before zipping */
	public static final String TEMP_PROJECT_DIR_NAME = GeneralManager.CALEYDO_HOME_PATH + "tempLoad"
		+ File.separator;

	/**
	 * Loads the project from a specified zip-archive.
	 * 
	 * @param fileName
	 *            name of the file to load the project from
	 * @return initialization data for the application from which it can restore itself
	 */
	public SerializationData load(String fileName) {

		FileOperations.deleteDirectory(TEMP_PROJECT_DIR_NAME);

		ZipUtils zipUtils = new ZipUtils();
		zipUtils.unzipToDirectory(fileName, TEMP_PROJECT_DIR_NAME);

		return loadDirectory(TEMP_PROJECT_DIR_NAME);
	}

	/**
	 * Loads the project from the recent-project saved automatically on exit
	 * 
	 * @return initialization data for the application from which it can restore itself
	 */
	public SerializationData loadRecent() {
		return loadDirectory(ProjectSaver.RECENT_PROJECT_DIR_NAME);
	}

	/**
	 * Loads the project from a directory
	 * 
	 * @param dirName
	 *            name of the directory to load the project from
	 * @return initialization data for the application from which it can restore itself
	 */
	public SerializationData loadDirectory(String dirName) {

		loadPlugins(dirName);

		SerializationData serializationData = null;

		SerializationManager serializationManager = GeneralManager.get().getSerializationManager();

		JAXBContext projectContext = serializationManager.getProjectContext();

		try {
			Unmarshaller unmarshaller = projectContext.createUnmarshaller();

			try {
				GeneralManager.get().setBasicInfo(
					(BasicInformation) unmarshaller.unmarshal(GeneralManager.get().getResourceLoader()
						.getResource(dirName + ProjectSaver.BASIC_INFORMATION_FILE_NAME)));
			}
			catch (FileNotFoundException e) {
				throw new IllegalStateException("Cannot load data domain list from project file");
			}

			DataDomainList dataDomainList;
			try {
				dataDomainList =
					(DataDomainList) unmarshaller.unmarshal(GeneralManager.get().getResourceLoader()
						.getResource(dirName + ProjectSaver.DATA_DOMAIN_FILE_NAME));
			}
			catch (FileNotFoundException e1) {
				throw new IllegalStateException("Cannot load data domain list from project file");
			}

			serializationData = new SerializationData();

			for (ADataDomain dataDomain : dataDomainList.getDataDomains()) {
				
				Thread thread = new Thread(dataDomain, dataDomain.getDataDomainID());
				thread.start();
				if (dataDomain instanceof ATableBasedDataDomain) {

					String extendedDirName = dirName + dataDomain.getDataDomainID() + "_";
					String setFileName = extendedDirName + ProjectSaver.DATA_TABLE_FILE_NAME;

					DataDomainSerializationData dataInitializationData = new DataDomainSerializationData();
					
					LoadDataParameters loadingParameters = dataDomain.getLoadDataParameters();
					loadingParameters.setFileName(setFileName);
					loadingParameters.setDataDomain((ATableBasedDataDomain) dataDomain);

					HashMap<String, ContentVirtualArray> contentVAMap =
						new HashMap<String, ContentVirtualArray>(6);
					String tmpType = DataTable.RECORD;
					contentVAMap.put(DataTable.RECORD, loadContentVirtualArray(unmarshaller, extendedDirName, tmpType));
					// tmpType = ContentVAType.CONTENT_CONTEXT;
					// contentVAMap.put(ContentVAType.CONTENT, loadContentVirtualArray(unmarshaller, dirName,
					// tmpType));
					// tmpType = ContentVAType.CONTENT_EMBEDDED_HM;
					// contentVAMap.put(ContentVAType.CONTENT, loadContentVirtualArray(unmarshaller, dirName,
					// tmpType));
					// FIXME: this should be done like this:
					// for (ContentVAType type : ContentVAType.getRegisteredVATypes()) {
					// contentVAMap.put(type, loadContentVirtualArray(unmarshaller, dirName, type));
					// }

					HashMap<String, DimensionVirtualArray> dimensionVAMap =
						new HashMap<String, DimensionVirtualArray>(2);

					String tempDimensionType = DataTable.DIMENSION;
					dimensionVAMap.put(tempDimensionType,
						loadDimensionVirtualArray(unmarshaller, extendedDirName, tempDimensionType));

					// FIXME: this should be done like this:
					// for (DimensionVAType type : DimensionVAType.getRegisteredVATypes()) {
					// dimensionVAMap.put(type, loadDimensionVirtualArray(unmarshaller, dirName, type));
					// }

					dataInitializationData.setDataDomain((ATableBasedDataDomain) dataDomain);
					dataInitializationData.setContentVAMap(contentVAMap);
					dataInitializationData.setDimensionVAMap(dimensionVAMap);

					dataDomain.getLoadDataParameters().setGeneTreeFileName(
						extendedDirName + ProjectSaver.GENE_TREE_FILE_NAME);
					dataDomain.getLoadDataParameters().setExperimentsFileName(
						extendedDirName + ProjectSaver.EXP_TREE_FILE_NAME);
					
					serializationData.addDataSerializationData(dataInitializationData);
				}
			}

			ViewList loadViews = null;
			try {
				loadViews =
					(ViewList) unmarshaller.unmarshal(GeneralManager.get().getResourceLoader()
						.getResource(dirName + ProjectSaver.VIEWS_FILE_NAME));
			}
			catch (FileNotFoundException e) {
				// do nothing - no view list available
			}

			if (loadViews != null) {
				serializationData.setViews(loadViews.getViews());
			}
		}
		catch (JAXBException ex) {
			throw new RuntimeException("Error while loading project", ex);
		}

		return serializationData;
	}

	private void loadPlugins(String dirName) {
		JAXBContext context;
		PlugInList plugInList = null;
		try {
			context = JAXBContext.newInstance(PlugInList.class);

			Unmarshaller unmarshaller = context.createUnmarshaller();

			plugInList =
				(PlugInList) unmarshaller.unmarshal(new File(dirName + ProjectSaver.PLUG_IN_LIST_FILE_NAME));
		}
		catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ArrayList<String> plugIns = plugInList.plugIns;
		for (String plugIn : plugIns) {
			Bundle bundle = Platform.getBundle(plugIn);
			if (bundle == null) {
				Logger.log(new Status(IStatus.WARNING, toString(), "Could not load bundle: " + bundle));
				continue;
			}
			try {
				bundle.start();
			}
			catch (BundleException e) {
				Logger.log(new Status(IStatus.ERROR, this.toString(), "Problem starting bundle " + plugIn +" at deserialization.", e));
			}
		}

	}

	/**
	 * Loads a {@link VirtualArray} from the file system. the filename is created by the type of
	 * {@link VirtualArray}.
	 * 
	 * @param unmarshaller
	 *            JAXB-unmarshaller to convert the xml-file to a {@link VirtualArray}-instance
	 * @param dir
	 *            directory-name in the file system to load the {@link VirtualArray} from
	 * @param type
	 *            type of VirtualArray in the {@link UseCase}
	 * @return loaded {@link VirtualArray}
	 * @throws JAXBException
	 *             in case of a {@link JAXBException} while unmarshalling the xml file
	 */
	private ContentVirtualArray loadContentVirtualArray(Unmarshaller unmarshaller, String dir, String type)
		throws JAXBException {
		String fileName = dir + "va_" + type.toString() + ".xml";
		ContentVirtualArray va = (ContentVirtualArray) unmarshaller.unmarshal(new File(fileName));
		return va;
	}

	private DimensionVirtualArray loadDimensionVirtualArray(Unmarshaller unmarshaller, String dir, String type)
		throws JAXBException {
		String fileName = dir + "va_" + type.toString() + ".xml";
		DimensionVirtualArray va = (DimensionVirtualArray) unmarshaller.unmarshal(new File(fileName));
		return va;
	}
}