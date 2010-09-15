package org.caleydo.core.serialize;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.set.LoadDataParameters;
import org.caleydo.core.data.collection.set.Set;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.StorageVirtualArray;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.ADataDomain;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
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
	public DataInitializationData load(String fileName) {
		
		FileOperations.deleteDirectory(TEMP_PROJECT_DIR_NAME);
		
		ZipUtils zipUtils = new ZipUtils();
		zipUtils.unzipToDirectory(fileName, TEMP_PROJECT_DIR_NAME);
		DataInitializationData initData = loadDirectory(TEMP_PROJECT_DIR_NAME);
		return initData;
	}

	/**
	 * Loads the project from the recent-project saved automatically on exit
	 * 
	 * @return initialization data for the application from which it can restore itself
	 */
	public DataInitializationData loadRecent() {
		return loadDirectory(ProjectSaver.RECENT_PROJECT_DIR_NAME);
	}

	/**
	 * Loads the project from a directory
	 * 
	 * @param dirName
	 *            name of the directory to load the project from
	 * @return initialization data for the application from which it can restore itself
	 */
	public DataInitializationData loadDirectory(String dirName) {

		loadPlugins(dirName);

		DataInitializationData initData = null;

		SerializationManager serializationManager = GeneralManager.get().getSerializationManager();

		JAXBContext projectContext = serializationManager.getProjectContext();

		try {
			Unmarshaller unmarshaller = projectContext.createUnmarshaller();
			DataDomainList dataDomainList;
			try {
				dataDomainList =
					(DataDomainList) unmarshaller.unmarshal(GeneralManager.get().getResourceLoader()
						.getResource(dirName + ProjectSaver.DATA_DOMAIN_FILE_NAME));
			}
			catch (FileNotFoundException e1) {
				throw new IllegalStateException("Cannot load data domain list from project file");
			}

			initData = new DataInitializationData();

			for (ADataDomain dataDomain : dataDomainList.getDataDomains()) {

				if (dataDomain instanceof ASetBasedDataDomain) {

					String setFileName = dirName + ProjectSaver.SET_DATA_FILE_NAME;

					LoadDataParameters loadingParameters = dataDomain.getLoadDataParameters();
					loadingParameters.setFileName(setFileName);
					loadingParameters.setDataDomain((ASetBasedDataDomain) dataDomain);

					HashMap<String, ContentVirtualArray> contentVAMap =
						new HashMap<String, ContentVirtualArray>(6);
					String tmpType = ISet.CONTENT;
					contentVAMap.put(ISet.CONTENT, loadContentVirtualArray(unmarshaller, dirName, tmpType));
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

					HashMap<String, StorageVirtualArray> storageVAMap =
						new HashMap<String, StorageVirtualArray>(2);

					String tempStorageType = Set.STORAGE;
					storageVAMap.put(tempStorageType,
						loadStorageVirtualArray(unmarshaller, dirName, tempStorageType));

					// FIXME: this should be done like this:
					// for (StorageVAType type : StorageVAType.getRegisteredVATypes()) {
					// storageVAMap.put(type, loadStorageVirtualArray(unmarshaller, dirName, type));
					// }

					// TODO: now only the last set data domain is handled
					initData.setDataDomain((ASetBasedDataDomain) dataDomain);
					initData.setContentVAMap(contentVAMap);
					initData.setStorageVAMap(storageVAMap);

					dataDomain.getLoadDataParameters().setGeneTreeFileName(
						dirName + ProjectSaver.GENE_TREE_FILE_NAME);
					dataDomain.getLoadDataParameters().setExperimentsFileName(
						dirName + ProjectSaver.EXP_TREE_FILE_NAME);
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
				initData.setViews(loadViews.getViews());
			}
		}
		catch (JAXBException ex) {
			throw new RuntimeException("Error while loading project", ex);
		}

		return initData;
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
				// TODO Auto-generated catch block
				e.printStackTrace();
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

	private StorageVirtualArray loadStorageVirtualArray(Unmarshaller unmarshaller, String dir, String type)
		throws JAXBException {
		String fileName = dir + "va_" + type.toString() + ".xml";
		StorageVirtualArray va = (StorageVirtualArray) unmarshaller.unmarshal(new File(fileName));
		return va;
	}

}