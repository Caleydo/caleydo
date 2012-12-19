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
package org.caleydo.core.io.parser.ascii;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.io.GroupingParseSpecification;
import org.caleydo.core.io.IDSpecification;
import org.caleydo.core.io.IDTypeParsingRules;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Parses a delimited text file which contains information on groupings of ids.
 * Creates {@link PerspectiveInitializationData} which then can be used to
 * create the {@link GroupList}s. The parser is based on the specification given
 * in {@link GroupingParseSpecification}.
 * 
 * @author Alexander Lex
 */
public class GroupingParser extends ATextParser {

	private GroupingParseSpecification groupingSpecifications;
	private static final String DEFAULT_GROUP_NAME = "DEFAULT_GROUP_NAME";
	private IDType targetIDType;
	/** Where the data is stored during parsing */
	private ArrayList<PerspectiveInitializationData> perspectiveInitializationDatas;

	public GroupingParser(GroupingParseSpecification groupingSpecifications,
			IDType targetIDType) {
		super(groupingSpecifications.getDataSourcePath());
		this.groupingSpecifications = groupingSpecifications;
		this.targetIDType = targetIDType;
	}

	@Override
	protected void parseFile(BufferedReader reader) throws IOException {

		swtGuiManager.setProgressBarText("Loading groupings for " + targetIDType);
		float progressBarFactor = 100f / numberOfLinesInFile;

		IDSpecification idSpecification = groupingSpecifications.getRowIDSpecification();
		IDType sourceIDType = IDType.getIDType(idSpecification.getIdType());

		IDTypeParsingRules parsingRules = null;
		if (idSpecification.getIdTypeParsingRules() != null)
			parsingRules = idSpecification.getIdTypeParsingRules();
		else if (sourceIDType.getIdTypeParsingRules() != null)
			parsingRules = sourceIDType.getIdTypeParsingRules();

		if (!sourceIDType.getIDCategory().equals(targetIDType.getIDCategory()))
			throw new IllegalArgumentException("Can not map between specified IDTypes: "
					+ sourceIDType + ", " + targetIDType);

		IDMappingManager idMappingManager = IDMappingManagerRegistry.get()
				.getIDMappingManager(sourceIDType.getIDCategory());

		if (groupingSpecifications.getDataSourcePath() == null) {
			Logger.log(new Status(IStatus.INFO, this.toString(),
					"No path for grouping specified"));
			return;
		}
		try {

			String[] headerCells = null;

			// read header
			if (groupingSpecifications.isContainsColumnIDs()) {

				reader = GeneralManager.get().getResourceLoader()
						.getResource(groupingSpecifications.getDataSourcePath());

				String headerLine = "";

				int rowOfColumnIDs = (groupingSpecifications.getRowOfColumnIDs() != null) ? groupingSpecifications
						.getRowOfColumnIDs() : groupingSpecifications
						.getNumberOfHeaderLines() - 1;
				for (int countToHeader = 0; countToHeader <= rowOfColumnIDs; countToHeader++) {
					headerLine = reader.readLine();
				}
				headerCells = headerLine.split(groupingSpecifications.getDelimiter(), -1);
				reader.close();
			}

			reader = GeneralManager.get().getResourceLoader()
					.getResource(groupingSpecifications.getDataSourcePath());

			for (int headerLineCounter = 0; headerLineCounter < groupingSpecifications
					.getNumberOfHeaderLines(); headerLineCounter++) {
				reader.readLine();
			}

			ArrayList<Integer> columnsToRead = groupingSpecifications.getColumns();
			// if this was not specified we read all columns, the row IDs
			// are
			// guaranteed to be in the first column
			String firstDataLine = null;
			if (columnsToRead == null || headerCells == null) {
				firstDataLine = reader.readLine();
				String[] data = firstDataLine
						.split(groupingSpecifications.getDelimiter());
				if (columnsToRead == null) {
					columnsToRead = new ArrayList<Integer>(data.length);

					for (int columnCount = 1; columnCount < data.length; columnCount++) {
						columnsToRead.add(columnCount);
					}
				}
				// Assigning default group names
				if (headerCells == null) {
					headerCells = new String[data.length];
					for (int columnCount = 0; columnCount < data.length; columnCount++) {
						headerCells[columnCount] = DEFAULT_GROUP_NAME;
					}
				}
			}

			ArrayList<ArrayList<Pair<String, ArrayList<Integer>>>> listOfGroupLists = new ArrayList<ArrayList<Pair<String, ArrayList<Integer>>>>(
					columnsToRead.size());
			ArrayList<String> listOfGroupNames = new ArrayList<String>(
					columnsToRead.size());

			ArrayList<Pair<String, ArrayList<Integer>>> currentGroupList;
			// initialize for every column
			for (Integer columnNumber : columnsToRead) {
				currentGroupList = new ArrayList<Pair<String, ArrayList<Integer>>>();
				listOfGroupLists.add(currentGroupList);
				listOfGroupNames.add(headerCells[columnNumber]);
			}
			int lineCounter = 0;
			// the actual parsing
			while (true) {
				String line = null;
				if (firstDataLine == null) {
					line = reader.readLine();
				} else {
					// the reader already read the first line so we need to
					// re-use it
					line = firstDataLine;
					firstDataLine = null;
				}
				if (line == null)
					break;

				// read ID
				String[] columns = line.split(groupingSpecifications.getDelimiter());
				String originalID = columns[groupingSpecifications.getColumnOfRowIds()];

				originalID = convertID(originalID, parsingRules);

				Integer mappedID = idMappingManager.getID(sourceIDType, targetIDType,
						originalID);
				if (mappedID == null) {
					Logger.log(new Status(IStatus.WARNING, this.toString(),
							"Could not map id: " + originalID));
					continue;
				}

				// read data
				int groupListCounter = 0;
				for (Integer columnID : columnsToRead) {
					currentGroupList = listOfGroupLists.get(groupListCounter);

					ArrayList<Integer> group = null;
					for (Pair<String, ArrayList<Integer>> groupPair : currentGroupList) {
						if (groupPair.getFirst().equals(columns[columnID]))
							group = groupPair.getSecond();
					}
					// ArrayList<Integer> group = currentGroupList.get();
					if (group == null) {
						group = new ArrayList<Integer>();
						currentGroupList.add(new Pair<String, ArrayList<Integer>>(
								columns[columnID], group));
					}
					group.add(mappedID);
					groupListCounter++;
				}
				lineCounter++;
				if (lineCounter % 100 == 0) {
					swtGuiManager
							.setProgressBarPercentage((int) (progressBarFactor * lineCounter));
				}
			}

			// Create the initialization data
			perspectiveInitializationDatas = new ArrayList<PerspectiveInitializationData>();

			for (int groupListCount = 0; groupListCount < listOfGroupLists.size(); groupListCount++) {
				ArrayList<Pair<String, ArrayList<Integer>>> groupList = listOfGroupLists
						.get(groupListCount);
				ArrayList<Integer> sortedIDs = new ArrayList<Integer>();
				ArrayList<Integer> clusterSizes = new ArrayList<Integer>(groupList.size());
				ArrayList<Integer> sampleElements = new ArrayList<Integer>(
						groupList.size());
				ArrayList<String> clusterNames = new ArrayList<String>(groupList.size());
				int sampleIndex = 0;
				for (Pair<String, ArrayList<Integer>> groupPair : groupList) {
					ArrayList<Integer> group = groupPair.getSecond();
					sortedIDs.addAll(group);
					clusterSizes.add(group.size());
					clusterNames.add(groupPair.getFirst());
					sampleElements.add(sampleIndex);
					sampleIndex += group.size();
				}

				PerspectiveInitializationData data = new PerspectiveInitializationData();
				data.setData(sortedIDs, clusterSizes, sampleElements, clusterNames);
				String groupLabel = listOfGroupNames.get(groupListCount);
				if (groupLabel.equals(DEFAULT_GROUP_NAME)) {
					if (groupingSpecifications.getGroupingName() != null) {
						groupLabel = clusterSizes.size() + " "
								+ groupingSpecifications.getGroupingName();
					} else {
						groupLabel = clusterSizes.size() + " Clusters";
					}
				}
				data.setLabel(groupLabel);
				perspectiveInitializationDatas.add(data);
			}

		} catch (IOException ioException) {
			throw new IllegalStateException("Could not read file: "
					+ groupingSpecifications.getDataSourcePath());
		}
	}

	/**
	 * @return the perspectiveInitializationDatas, see
	 *         {@link #perspectiveInitializationDatas}
	 */
	public ArrayList<PerspectiveInitializationData> getPerspectiveInitializationDatas() {
		return perspectiveInitializationDatas;
	}

}
