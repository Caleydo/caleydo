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

		if (groupingSpecifications.getPath() == null) {
			Logger.log(new Status(Status.INFO, this.toString(),
					"No path for grouping specified"));
			return null;
		}
		BufferedReader reader;
		try {

			reader = new BufferedReader(new FileReader(groupingSpecifications.getPath()));
			String headerLine = reader.readLine();
			String[] headerCells = headerLine
					.split(groupingSpecifications.getDelimiter(), -1);

			ArrayList<Integer> columnsToRead = groupingSpecifications.getColumns();
			// if this was not specified we read all columns
			if (columnsToRead == null) {

				columnsToRead = new ArrayList<Integer>(headerCells.length);
				for (int columnCount = 1; columnCount < headerCells.length; columnCount++) {
					columnsToRead.add(columnCount);
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

				String line = reader.readLine();
				if (line == null)
					break;
				String[] columns = line.split(groupingSpecifications.getDelimiter());
				String originalID = columns[0];
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
					+ groupingSpecifications.getPath());
		}
	}

}
