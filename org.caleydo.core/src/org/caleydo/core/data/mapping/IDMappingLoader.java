package org.caleydo.core.data.mapping;

import java.util.ArrayList;

import org.caleydo.core.manager.GeneralManager;

/**
 * Class responsible for loaded ID mapping files. If the loading a specific mapping file is requested multiple
 * times, it is ignored.
 * 
 * @author Marc Streit
 */
public class IDMappingLoader {

	private volatile static IDMappingLoader instance;

	private ArrayList<String> loadedMappingFiles;

	private IDMappingLoader() {
		loadedMappingFiles = new ArrayList<String>();
	}

	public static IDMappingLoader get() {

		if (instance == null) {
			synchronized (IDMappingLoader.class) {

				if (instance == null)
					instance = new IDMappingLoader();
			}
		}
		return instance;
	}

	public void loadMappingFile(String fileName) {

		if (fileName == null || loadedMappingFiles.contains(fileName))
			return;

		// GeneralManager.get().getXmlParserManager().parseXmlFileByName("data/bootstrap/bootstrap.xml");
		GeneralManager.get().getXmlParserManager().parseXmlFileByName(fileName);
		loadedMappingFiles.add(fileName);
	}
}
