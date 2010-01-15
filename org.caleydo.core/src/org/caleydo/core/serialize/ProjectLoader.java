package org.caleydo.core.serialize;

import java.io.File;
import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.caleydo.core.data.selection.EVAType;
import org.caleydo.core.data.selection.VirtualArray;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.usecase.AUseCase;

/**
 * Restores the state of the application from a given file.
 * 
 * @author Werner Puff
 */
public class ProjectLoader {

	/** full path to directory to temporarily store the projects file before zipping */
	public static final String TEMP_PROJECT_DIR_NAME =
		IGeneralManager.CALEYDO_HOME_PATH + "tempLoad" + File.separator;

	/**
	 * Loads the project from a specified zip-archive.
	 * 
	 * @param fileName
	 *            name of the file to load the project from
	 * @return initialization data for the application from which it can restore itself
	 */
	public ApplicationInitData load(String fileName) {
		ZipUtils zipUtils = new ZipUtils();
		zipUtils.unzipToDirectory(fileName, TEMP_PROJECT_DIR_NAME);
		ApplicationInitData initData = loadDirectory(TEMP_PROJECT_DIR_NAME);
		return initData;
	}

	/**
	 * Loads the project from the recent-project saved automatically on exit
	 * 
	 * @return initialization data for the application from which it can restore itself
	 */
	public ApplicationInitData loadRecent() {
		return loadDirectory(ProjectSaver.RECENT_PROJECT_DIR_NAME);
	}

	/**
	 * Loads the project from a directory
	 * 
	 * @param dirName
	 *            name of the directory to load the project from
	 * @return initialization data for the application from which it can restore itself
	 */
	public ApplicationInitData loadDirectory(String dirName) {
		ApplicationInitData initData = null;

		SerializationManager serializationManager = GeneralManager.get().getSerializationManager();
		JAXBContext projectContext = serializationManager.getProjectContext();

		try {
			Unmarshaller unmarshaller = projectContext.createUnmarshaller();
			File useCaseFile = new File(dirName + ProjectSaver.USECASE_FILE_NAME);
			AUseCase useCase = (AUseCase) unmarshaller.unmarshal(useCaseFile);

			String setFileName = dirName + ProjectSaver.SET_DATA_FILE_NAME;
			useCase.getLoadDataParameters().setFileName(setFileName);

			HashMap<EVAType, VirtualArray> virtualArrayMap = new HashMap<EVAType, VirtualArray>();
			virtualArrayMap.put(EVAType.CONTENT, loadVirtualArray(unmarshaller, dirName, EVAType.CONTENT));
			virtualArrayMap.put(EVAType.CONTENT_CONTEXT, loadVirtualArray(unmarshaller, dirName,
				EVAType.CONTENT_CONTEXT));
			virtualArrayMap.put(EVAType.CONTENT_EMBEDDED_HM, loadVirtualArray(unmarshaller, dirName,
				EVAType.CONTENT_EMBEDDED_HM));
			virtualArrayMap.put(EVAType.STORAGE, loadVirtualArray(unmarshaller, dirName, EVAType.STORAGE));

			File viewFile = new File(dirName + ProjectSaver.VIEWS_FILE_NAME);
			ViewList loadViews = null;
			if (viewFile.exists()) {
				loadViews = (ViewList) unmarshaller.unmarshal(viewFile);
			}

			initData = new ApplicationInitData();
			initData.setUseCase(useCase);
			initData.setVirtualArrayMap(virtualArrayMap);
			if (loadViews != null) {
				initData.setViews(loadViews.getViews());
			}

			File geneClusterFile = new File(dirName + ProjectSaver.GENE_TREE_FILE_NAME);
			if (geneClusterFile.exists()) {
				useCase.getLoadDataParameters().setGeneTreeFileName(geneClusterFile.getAbsolutePath());
			}
			File expClusterFile = new File(dirName + ProjectSaver.EXP_TREE_FILE_NAME);
			if (expClusterFile.exists()) {
				useCase.getLoadDataParameters().setExperimentsFileName(expClusterFile.getAbsolutePath());
			}

		}
		catch (JAXBException ex) {
			throw new RuntimeException("Error while loading project", ex);
		}

		return initData;
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
	private VirtualArray loadVirtualArray(Unmarshaller unmarshaller, String dir, EVAType type)
		throws JAXBException {
		String fileName = dir + "va_" + type.toString() + ".xml";
		VirtualArray va = (VirtualArray) unmarshaller.unmarshal(new File(fileName));
		return va;
	}

}