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
package org.caleydo.core.id;

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
