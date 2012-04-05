package org.caleydo.core.parser.ascii;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.mapping.IDMappingManager;
import org.caleydo.core.data.mapping.IDMappingManagerRegistry;
import org.caleydo.core.data.perspective.PerspectiveInitializationData;
import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.Status;

/**
 * Parses a delimited text file which contains information on groupings of ids.
 * Creates {@link PerspectiveInitializationData} which then can be used to
 * create the {@link GroupList}s. The parser is based on the specification given
 * in {@link GroupingParseSpecification}.
 * 
 * @author Alexander Lex
 */
public class GroupingParser {

	public ArrayList<PerspectiveInitializationData> parseGrouping(
			GroupingParseSpecification groupingSpecifications,
			AStringConverter idConverter, IDType sourceIDType, IDType targetIDType) {

		if (!sourceIDType.getIDCategory().equals(targetIDType.getIDCategory()))
			throw new IllegalArgumentException("Can not map between specified IDTypes: "
					+ sourceIDType + ", " + targetIDType);

		IDMappingManager idMappingManager = IDMappingManagerRegistry.get()
				.getIDMappingManager(sourceIDType.getIDCategory());

		if (groupingSpecifications.getDataSourcePath() == null) {
			Logger.log(new Status(Status.INFO, this.toString(),
					"No path for grouping specified"));
			return null;
		}
		BufferedReader reader;
		try {

			String[] headerCells = null;

			// read header
			if (groupingSpecifications.isContainsColumnIDs()) {
				reader = new BufferedReader(new FileReader(
						groupingSpecifications.getDataSourcePath()));
				String headerLine = "";

				int rowOfColumnIDs = (groupingSpecifications.getRowOfColumnIDs() >= 0) ? groupingSpecifications
						.getRowOfColumnIDs() : groupingSpecifications
						.getNumberOfHeaderLines() - 1;
				for (int countToHeader = 0; countToHeader <= rowOfColumnIDs; countToHeader++) {
					headerLine = reader.readLine();
				}
				headerCells = headerLine.split(groupingSpecifications.getDelimiter(), -1);
			}

			reader = new BufferedReader(new FileReader(
					groupingSpecifications.getDataSourcePath()));

			for (int headerLineCounter = 0; headerLineCounter < groupingSpecifications
					.getNumberOfHeaderLines(); headerLineCounter++) {
				reader.readLine();
			}

			ArrayList<Integer> columnsToRead = groupingSpecifications.getColumns();
			// if this was not specified we read all columns, the row IDs are
			// guaranteed to be in the first column
			String firstDataLine = null;
			if (columnsToRead == null) {
				firstDataLine = reader.readLine();
				String[] data = firstDataLine
						.split(groupingSpecifications.getDelimiter());
				columnsToRead = new ArrayList<Integer>(data.length);
				if (headerCells == null) {
					headerCells = new String[data.length];
				}

				for (int columnCount = 1; columnCount < data.length; columnCount++) {
					columnsToRead.add(columnCount);
					headerCells[columnCount] = "Group " + columnCount;
				}
			}

			ArrayList<HashMap<String, ArrayList<Integer>>> listOfGroupLists = new ArrayList<HashMap<String, ArrayList<Integer>>>(
					columnsToRead.size());
			ArrayList<String> listOfGroupNames = new ArrayList<String>(
					columnsToRead.size());

			HashMap<String, ArrayList<Integer>> currentGroupList;
			// initialize for every column
			for (Integer columnCount : columnsToRead) {
				currentGroupList = new HashMap<String, ArrayList<Integer>>();
				listOfGroupLists.add(currentGroupList);
				listOfGroupNames.add(headerCells[columnCount]);
			}
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
				if (idConverter != null) {
					originalID = idConverter.convert(originalID);
				}

				Integer mappedID = idMappingManager.getID(sourceIDType, targetIDType,
						originalID);
				if (mappedID == null) {
					Logger.log(new Status(Status.WARNING, this.toString(),
							"Could not map id: " + originalID));
					continue;
				}

				// read data
				int groupListCounter = 0;
				for (Integer columnID : columnsToRead) {
					currentGroupList = listOfGroupLists.get(groupListCounter);

					ArrayList<Integer> group = currentGroupList.get(columns[columnID]);
					if (group == null) {
						group = new ArrayList<Integer>();
						currentGroupList.put(columns[columnID], group);
					}
					group.add(mappedID);
					groupListCounter++;
				}
				// lineCounter++;
			}

			// Create the initialization datas
			ArrayList<PerspectiveInitializationData> perspectiveInitializationDatas = new ArrayList<PerspectiveInitializationData>();

			for (int groupListCount = 0; groupListCount < listOfGroupLists.size(); groupListCount++) {
				HashMap<String, ArrayList<Integer>> groupList = listOfGroupLists
						.get(groupListCount);
				ArrayList<Integer> sortedIDs = new ArrayList<Integer>();
				ArrayList<Integer> clusterSizes = new ArrayList<Integer>(groupList.size());
				ArrayList<Integer> sampleElements = new ArrayList<Integer>(
						groupList.size());
				ArrayList<String> clusterNames = new ArrayList<String>(groupList.size());
				int sampleIndex = 0;
				for (String groupName : groupList.keySet()) {
					ArrayList<Integer> group = groupList.get(groupName);
					sortedIDs.addAll(group);
					clusterSizes.add(group.size());
					clusterNames.add(groupName);
					sampleElements.add(sampleIndex);
					sampleIndex += group.size();
				}

				PerspectiveInitializationData data = new PerspectiveInitializationData();
				data.setData(sortedIDs, clusterSizes, sampleElements, clusterNames);
				data.setLabel(listOfGroupNames.get(groupListCount));
				perspectiveInitializationDatas.add(data);
			}

			return perspectiveInitializationDatas;
		} catch (IOException ioException) {
			throw new IllegalStateException("Could not read file: "
					+ groupingSpecifications.getDataSourcePath());
		}
	}

}
