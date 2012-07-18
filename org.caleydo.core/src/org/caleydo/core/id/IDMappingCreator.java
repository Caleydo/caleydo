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

import org.caleydo.core.io.IDSpecification;
import org.caleydo.core.io.parser.ascii.IDMappingParser;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.specialized.Organism;

/**
 * Command loads lookup table from file using one delimiter and a target
 * Collection.
 * 
 * @author Marc Streit
 */
public class IDMappingCreator {

	protected String fileName;

	/**
	 * Creates an ID mapping for the id types specified by parsing the supplied
	 * file.
	 * 
	 * @param fileName
	 * @param startParsingAtLine
	 * @param stopParsingAtLine
	 * @param codeResolvingLUTMappingType
	 * @param delimiter
	 * @param idCategory
	 * @param isMultiMap
	 * @param createReverseMap
	 * @param resolveCodeMappingUsingCodeToId_LUT
	 *            Boolean indicates if one column of the mapping needs to be
	 *            resolved. Resolving means replacing codes by internal IDs.
	 */
	public void createMapping(String fileName, int startParsingAtLine,
			int stopParsingAtLine, IDType fromIDType, IDType toIDType, String delimiter,
			IDCategory idCategory, boolean isMultiMap, boolean createReverseMap,
			boolean resolveCodeMappingUsingCodeToId_LUT, IDType codeResolvedFromIDType,
			IDType codeResolvedToIDType) {

		IDMappingParser idMappingParser = null;

		if (fileName.contains("ORGANISM")) {
			Organism eOrganism = GeneralManager.get().getBasicInfo().getOrganism();
			this.fileName = fileName.replace("ORGANISM", eOrganism.toString());
		}

		// FIXME: Currently we do not have the ensembl mapping table for home
		// sapiens
		if (fileName.contains("HOMO_SAPIENS") && fileName.contains("ENSEMBL"))
			return;

		if (idCategory == null)
			throw new IllegalStateException("ID Category was null");
		IDMappingManager idMappingManager = IDMappingManagerRegistry.get()
				.getIDMappingManager(idCategory);
		MappingType mappingType = idMappingManager.createMap(fromIDType, toIDType,
				isMultiMap);

		if (resolveCodeMappingUsingCodeToId_LUT) {

			idMappingManager.createCodeResolvedMap(mappingType, codeResolvedFromIDType,
					codeResolvedToIDType);
		}

		if (!fileName.equals("already_loaded")) {
			idMappingParser = new IDMappingParser(idCategory, fileName, mappingType);
			idMappingParser.setTokenSeperator(delimiter);
			idMappingParser.setStartParsingStopParsingAtLine(startParsingAtLine,
					stopParsingAtLine);
			idMappingParser.loadData();
		}

		if (createReverseMap) {
			idMappingManager.createReverseMap(mappingType);
		}
	}
}
