package org.caleydo.core.serialize;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.caleydo.core.data.collection.set.LoadDataParameters;
import org.caleydo.core.data.collection.set.SetUtils;
import org.caleydo.core.data.selection.VirtualArray;
import org.caleydo.core.manager.IUseCase;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.usecase.AUseCase;
import org.caleydo.core.view.opengl.canvas.storagebased.EVAType;

/**
 * Serializes the current state of the application into a directory or file.
 * 
 * @author Alexander Lex
 * @author Werner Puff
 */
public class ProjectSaver {

	/** full path to directory to temporarily store the projects file before zipping */
	public static final String TEMP_PROJECT_DIR_NAME = GeneralManager.CALEYDO_HOME_PATH + "tempSave" + File.separator;

	/** full path to directory of the recently open project */
	public static final String RECENT_PROJECT_DIR_NAME = GeneralManager.CALEYDO_HOME_PATH + "recent_project" + File.separator;

	/** 
	 * Saves the project into a specified zip-archive.
	 * @param fileName name of the file to save the project in.
	 */
	public void save(String fileName) {
		ZipUtils zipUtils = new ZipUtils();
		saveToDirectory(TEMP_PROJECT_DIR_NAME);
		zipUtils.zipDirectory(TEMP_PROJECT_DIR_NAME, fileName);
		zipUtils.deleteDirectory(TEMP_PROJECT_DIR_NAME);
	}

	/**
	 * Saves the project to the directory for the recent project 
	 */
	public void saveRecentProject() {
		ZipUtils zipUtils = new ZipUtils();
		if (!GeneralManager.get().getUseCase().getLoadDataParameters().getFileName().startsWith(RECENT_PROJECT_DIR_NAME)) {
			zipUtils.deleteDirectory(RECENT_PROJECT_DIR_NAME);
		}
		saveToDirectory(RECENT_PROJECT_DIR_NAME);
	}

	/**
	 * Saves the project to the directory with the given name. The directory is created before saving.
	 * @param dirName directory to save the project-files into
	 */
	private void saveToDirectory(String dirName) {
		if (dirName.charAt(dirName.length() - 1) != File.separatorChar) {
			dirName += File.separator;
		}
		
		File tempDirFile = new File(dirName);
		tempDirFile.mkdir();
		
		AUseCase useCase = (AUseCase) GeneralManager.get().getUseCase();
		LoadDataParameters parameters = useCase.getLoadDataParameters(); 
		byte[] data = SetUtils.loadSetFile(parameters);
		
		String setFileName = dirName + "data.csv";
		File setFile = new File(setFileName);
		SetUtils.saveSetFile(parameters, data, setFile);
		
		SerializationManager serializationManager = GeneralManager.get().getSerializationManager();
		JAXBContext projectContext = serializationManager.getProjectContext();
		
		try {
			Marshaller marshaller = projectContext.createMarshaller();
			File useCaseFile = new File(dirName + "usecase.xml");
			marshaller.marshal(useCase, useCaseFile);

			saveVirtualArray(marshaller, dirName, useCase, EVAType.CONTENT);
			saveVirtualArray(marshaller, dirName, useCase, EVAType.CONTENT_CONTEXT);
			saveVirtualArray(marshaller, dirName, useCase, EVAType.CONTENT_EMBEDDED_HM);
			saveVirtualArray(marshaller, dirName, useCase, EVAType.STORAGE);
		} catch (JAXBException ex) {
			throw new RuntimeException("Error saving project files (xml serialization)", ex);
		}
	}
	
	/**
	 * Saves the {@link VirtualArray} of the given type. The filename is created
	 * from the type.
	 * @param dir directory to save the {@link VirtualArray} in.
	 * @param useCase {@link IUseCase} to retrieve the {@link VirtualArray} from.
	 * @param type type of the virtual array within the given {@link IUseCase}.
	 */
	private void saveVirtualArray(Marshaller marshaller, String dir, IUseCase useCase, EVAType type) 
	throws JAXBException {
		String fileName = dir + "va_" + type.toString() + ".xml";
		VirtualArray va = (VirtualArray) useCase.getVA(type);
		marshaller.marshal(va, new File(fileName));
	}
	
}

//String geneTreePath = tempDirectory + "/bgene_tree.xml";

//ISet set = GeneralManager.get().getUseCase().getSet();

//SetExporter exporter = new SetExporter();
//exporter.export(set, exportedData, EWhichViewToExport.WHOLE_DATA);
//
//exporter.exportTrees(set, tempDirectory);
