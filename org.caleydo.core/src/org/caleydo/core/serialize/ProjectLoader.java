package org.caleydo.core.serialize;

import java.io.File;
import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.caleydo.core.data.selection.VirtualArray;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.usecase.AUseCase;
import org.caleydo.core.view.opengl.canvas.storagebased.EVAType;

/**
 * Restores the state of the application from a given file. 
 * 
 * @author Werner Puff
 */
public class ProjectLoader {

	/** full path to directory to temporarily store the projects file before zipping */
	public static final String TEMP_PROJECT_DIR_NAME = GeneralManager.CALEYDO_HOME_PATH + "tempLoad" + File.separator;

	public ApplicationInitData load(String fileName) {
		ZipUtils zipUtils = new ZipUtils();
		zipUtils.unzipToDirectory(fileName, TEMP_PROJECT_DIR_NAME);
		ApplicationInitData initData = loadDirectory(TEMP_PROJECT_DIR_NAME);
		return initData;
	}
	
	public ApplicationInitData loadRecent() {
		return loadDirectory(ProjectSaver.RECENT_PROJECT_DIR_NAME);
	}

	public ApplicationInitData loadDirectory(String dirName) {
		ApplicationInitData initData;

		
		SerializationManager serializationManager = GeneralManager.get().getSerializationManager();
		JAXBContext projectContext = serializationManager.getProjectContext();
		
		try {
			Unmarshaller unmarshaller = projectContext.createUnmarshaller();
			File useCaseFile = new File(dirName + "usecase.xml");
			AUseCase useCase = (AUseCase) unmarshaller.unmarshal(useCaseFile);

			String setFileName = dirName + "data.csv";
			useCase.getLoadDataParameters().setFileName(setFileName);
			
			HashMap<EVAType, VirtualArray> virtualArrayMap = new HashMap<EVAType, VirtualArray>();
			virtualArrayMap.put(EVAType.CONTENT, loadVirtualArray(unmarshaller, dirName, EVAType.CONTENT));
			virtualArrayMap.put(EVAType.CONTENT_CONTEXT, loadVirtualArray(unmarshaller, dirName, EVAType.CONTENT_CONTEXT));
			virtualArrayMap.put(EVAType.CONTENT_EMBEDDED_HM, loadVirtualArray(unmarshaller, dirName, EVAType.CONTENT_EMBEDDED_HM));
			virtualArrayMap.put(EVAType.STORAGE, loadVirtualArray(unmarshaller, dirName, EVAType.STORAGE));
			
			initData = new ApplicationInitData();
			initData.setUseCase(useCase);
			initData.setVirtualArrayMap(virtualArrayMap);

		} catch (JAXBException ex) {
			throw new RuntimeException("Error while loading project", ex);
		}
		
		return initData;
	}

	/**
	 * Loads a {@link VirtualArray} from the file system. the filename is created by the 
	 * type of {@link VirtualArray}.
	 * @param unmarshaller JAXB-unmarshaller to convert the xml-file to a {@link VirtualArray}-instance
	 * @param dir directory-name in the file system to load the {@link VirtualArray} from 
	 * @param type type of VirtualArray in the {@link UseCase}
	 * @return loaded {@link VirtualArray}
	 * @throws JAXBException in case of a {@link JAXBException} while unmarshalling the xml file
	 */
	private VirtualArray loadVirtualArray(Unmarshaller unmarshaller, String dir, EVAType type) 
	throws JAXBException {
		String fileName = dir + "va_" + type.toString() + ".xml";
		VirtualArray va = (VirtualArray) unmarshaller.unmarshal(new File(fileName));
		return va;
	}
	
}