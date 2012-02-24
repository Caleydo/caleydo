package org.caleydo.core.parser.ascii;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.mapping.IDMappingManager;
import org.caleydo.core.data.mapping.IDMappingManagerRegistry;
import org.caleydo.core.data.perspective.PerspectiveInitializationData;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.Status;

/**
 * @author Alexander Lex
 */
public class ExternalGroupingParser {

	public ArrayList<PerspectiveInitializationData> loadExternalGrouping(String clusterFile,
		ArrayList<Integer> groupingColumns, AStringConverter idConverter, IDType sourceIDType,
		IDType targetIDType) {

		if (!sourceIDType.getIDCategory().equals(targetIDType.getIDCategory()))
			throw new IllegalArgumentException("Can not map between specified IDTypes: " + sourceIDType
				+ ", " + targetIDType);

		IDMappingManager idMappingManager =
			IDMappingManagerRegistry.get().getIDMappingManager(sourceIDType.getIDCategory());

		String delimiter = "\t";

		if (clusterFile == null) {
			Logger.log(new Status(Status.INFO, this.toString(), "No Cluster Information specified"));
			return null;
		}
		// open file to read second line to determine number of rows and columns
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(clusterFile));

			// skip header ("#1.2")
			// TODO: check if file is indeed a gct file
			reader.readLine();

			ArrayList<HashMap<String, ArrayList<Integer>>> listOfGroupLists =
				new ArrayList<HashMap<String, ArrayList<Integer>>>();

			HashMap<String, ArrayList<Integer>> currentGroupList;
			// initialize for every column
			for (Integer columnCount : groupingColumns) {
				currentGroupList = new HashMap<String, ArrayList<Integer>>();
				listOfGroupLists.add(currentGroupList);
			}

			int lineCounter = 0;
			while (true) {

				String line = reader.readLine();
				if (line == null)
					break;
				String[] columns = line.split(delimiter);
				String originalID = columns[0];
				if (idConverter != null) {
					originalID = idConverter.convert(originalID);
				}
				// String originalID = columns[0];

				Integer mappedID = idMappingManager.getID(sourceIDType, targetIDType, originalID);
				if (mappedID == null) {
					Logger
						.log(new Status(Status.WARNING, this.toString(), "Could not map id: " + originalID));
					continue;
				}
				int groupListCounter = 0;
				for (Integer columnCount : groupingColumns) {
					currentGroupList = listOfGroupLists.get(groupListCounter);

					ArrayList<Integer> group = currentGroupList.get(columns[columnCount]);
					if (group == null) {
						group = new ArrayList<Integer>();
						currentGroupList.put(columns[columnCount], group);
					}
					group.add(mappedID);
					groupListCounter++;
				}
				lineCounter++;
			}
			ArrayList<PerspectiveInitializationData> perspectiveInitializationDatas =
				new ArrayList<PerspectiveInitializationData>();
			for (HashMap<String, ArrayList<Integer>> groupList : listOfGroupLists) {

				ArrayList<Integer> sortedIDs = new ArrayList<Integer>();
				ArrayList<Integer> clusterSizes = new ArrayList<Integer>(groupList.size());
				ArrayList<Integer> sampleElements = new ArrayList<Integer>(groupList.size());
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
				perspectiveInitializationDatas.add(data);
			}

			return perspectiveInitializationDatas;
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}

		return null;

	}

}
