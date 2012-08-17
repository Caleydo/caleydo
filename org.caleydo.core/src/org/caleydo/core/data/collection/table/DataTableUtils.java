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
package org.caleydo.core.data.collection.table;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.caleydo.core.data.collection.EDataTransformation;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.virtualarray.group.DimensionGroupList;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.RecordGroupList;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.parser.ascii.TabularDataParser;
import org.caleydo.core.manager.GeneralManager;

/**
 * Utility class that features creating, loading and saving sets and dimensions.
 * 
 * @author Werner Puff
 * @author Alexander Lex
 */
public class DataTableUtils {

	/** prefix for temporary set-file */
	public static final String DATA_FILE_PREFIX = "setfile";

	/** prefix for temporary gene-tree--file */
	public static final String RECORD_TREE_FILE_PREFIX = "recordtree";

	/** prefix for temporary experiment-tree-file */
	public static final String DIMENSION_TREE_FILE_PREFIX = "dimensiontree";

	/**
	 * Loads the set-file as specified in the {@link IDataDomain}'s
	 * {@link LoadDataParameters} and stores the raw-data in the useCase
	 * 
	 * @param useCase
	 */
	public static byte[] loadSetFile(DataSetDescription dataSetDescription) {
		String dataPath = dataSetDescription.getDataSourcePath();
		if (dataPath == null) {
			throw new RuntimeException("No set-file name specified in use case");
		}

		File file = new File(dataPath);
		byte[] buffer;
		try {
			FileInputStream is = new FileInputStream(file);
			if (file.length() > Integer.MAX_VALUE) {
				throw new RuntimeException(
						"set-file is larger than maximum internal file-dimension-size");
			}
			buffer = new byte[(int) file.length()];
			is.read(buffer, 0, buffer.length);
		} catch (IOException ex) {
			throw new RuntimeException("Could not read from specified set-file '"
					+ dataPath + "'", ex);
		}
		return buffer;
	}

	/**
	 * Saves the set-data contained in the useCase in a new created temp-file.
	 * The {@link LoadDataParameters} of the useCase are set according to the
	 * created set-file
	 * 
	 * @param parameters
	 *            set-load parameters to store the filename;
	 * @param data
	 *            set-data to save
	 */
	public static void saveTableFile(DataSetDescription parameters, byte[] data) {
		File homeDir = new File(GeneralManager.CALEYDO_HOME_PATH);
		File setFile;
		try {
			setFile = File.createTempFile(DATA_FILE_PREFIX, "csv", homeDir);
			parameters.setDataSourcePath(setFile.getCanonicalPath());
		} catch (IOException ex) {
			throw new RuntimeException(
					"Could not create temporary file to store the set file", ex);
		}
		saveFile(data, setFile);
	}

	/**
	 * Saves the given data in the given file.
	 * 
	 * @param data
	 *            data to save.
	 * @param target
	 *            file to store the data.
	 */
	public static void saveFile(byte[] data, File setFile) {
		FileOutputStream os = null;
		try {
			os = new FileOutputStream(setFile);
			os.write(data);
		} catch (FileNotFoundException ex) {
			throw new RuntimeException(
					"Could not create temporary file to store the set file", ex);
		} catch (IOException ex) {
			throw new RuntimeException("Could not write to temportary set file", ex);
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException ex) {
					// nothing to do here, assuming output stream is already
					// closed
				}
			}
		}
	}

	/**
	 * Creates the {@link DataTable} from a previously prepared dimension
	 * definition.
	 * 
	 * @param dataDomain
	 * @param createDefaultDimensionPerspectives
	 * @param createDefaultRecordPerspective
	 * @return
	 */
	public static void loadData(ATableBasedDataDomain dataDomain,
			DataSetDescription dataSetDescription,
			boolean createDefaultDimensionPerspectives,
			boolean createDefaultRecordPerspective) {

		// --------- load dynamic mapping ---------------

		// IDType rowTargetIDType;

		// IDMappingCreator idMappingCreator = new IDMappingCreator();
		// idMappingCreator.setIdSpecification(dataSetDescription.getRowIDSpecification());
		// idMappingCreator.createMapping(dataSetDescription.getDataSourcePath(),
		// dataSetDescription.getNumberOfHeaderLines(), -1,
		// IDType.getIDType(dataSetDescription.getRowIDSpecification().getIdType()),
		// rowTargetIDType, "\t", rowTargetIDType.getIDCategory(), false, true,
		// false,
		// null, null);

		// --------- data loading ---------------

		TabularDataParser parser = new TabularDataParser(dataDomain, dataSetDescription);
		parser.loadData();
		DataTable table = dataDomain.getTable();

		// TODO re-enable this
		if (createDefaultDimensionPerspectives)
			table.createDefaultDimensionPerspective();

		if (createDefaultRecordPerspective)
			table.createDefaultRecordPerspective();
		// TODO re-enable this
		// loadTrees(loadDataParameters, set);

		table.getMetaData().setDataCenteredAtZero(
				dataSetDescription.isDataCenteredAtZero());

		if (dataSetDescription.getMin() != null) {
			table.getMetaData().setMin(dataSetDescription.getMin());
		}
		if (dataSetDescription.getMax() != null) {
			table.getMetaData().setMax(dataSetDescription.getMax());
		}

		boolean isSetHomogeneous = dataSetDescription.isDataHomogeneous();

		if (dataSetDescription.getMathFilterMode().equalsIgnoreCase("None")) {
			table.setExternalDataRepresentation(EDataTransformation.NONE,
					isSetHomogeneous);
		} else if (dataSetDescription.getMathFilterMode().equalsIgnoreCase("Log10")) {
			table.setExternalDataRepresentation(EDataTransformation.LOG10,
					isSetHomogeneous);
		} else if (dataSetDescription.getMathFilterMode().equalsIgnoreCase("Log2")) {
			table.setExternalDataRepresentation(EDataTransformation.LOG2,
					isSetHomogeneous);
		} else
			throw new IllegalStateException("Unknown data representation type");
	}

	/**
	 * Switch the representation of the data. When this is called the data in
	 * normalized is replaced with data calculated from the mode specified.
	 * 
	 * @param externalDataRep
	 *            Determines how the data is visualized. For options see
	 *            {@link EDataTransformation}
	 * @param bIsSetHomogeneous
	 *            Determines whether a set is homogeneous or not. Homogeneous
	 *            means that the sat has a global maximum and minimum, meaning
	 *            that all dimensions in the set contain equal data. If false,
	 *            each dimension is treated separately, has it's own min and max
	 *            etc. Sets that contain nominal data MUST be inhomogeneous.
	 */
	public static void setExternalDataRepresentation(DataTable table,
			EDataTransformation externalDataRep, boolean isSetHomogeneous) {
		table.setExternalDataRepresentation(externalDataRep, isSetHomogeneous);
	}

	/**
	 * Creates a contentGroupList from the group information read from a stored
	 * file
	 * 
	 * @param set
	 * @param vaType
	 *            specify for which va type this is valid
	 * @param groupInfo
	 *            the array list extracted from the file
	 */
	public static void setContentGroupList(DataTable table, String vaType, int[] groupInfo) {

		int cluster = 0, cnt = 0;

		RecordGroupList contentGroupList = table.getRecordPerspective(vaType)
				.getVirtualArray().getGroupList();
		contentGroupList.clear();

		for (int i = 0; i < groupInfo.length; i++) {
			Group group = null;
			if (cluster != groupInfo[i]) {
				group = new Group(cnt);
				contentGroupList.append(group);
				cluster++;
				cnt = 0;
			}
			cnt++;
			if (i == groupInfo.length - 1) {
				group = new Group(cnt);
				contentGroupList.append(group);
			}
		}
	}

	/**
	 * Creates a dimensionGroupList from the group information read from a
	 * stored file
	 * 
	 * @param set
	 * @param vaType
	 *            specify for which va type this is valid
	 * @param groupInfo
	 *            the array list extracted from the file
	 */
	public static void setDimensionGroupList(DataTable table, String vaType,
			int[] groupInfo) {
		int cluster = 0, cnt = 0;

		DimensionGroupList dimensionGroupList = table.getDimensionPerspective(vaType)
				.getVirtualArray().getGroupList();
		dimensionGroupList.clear();

		for (int i = 0; i < groupInfo.length; i++) {
			Group group = null;
			if (cluster != groupInfo[i]) {
				group = new Group(cnt, 0);
				dimensionGroupList.append(group);
				cluster++;
				cnt = 0;
			}
			cnt++;
			if (i == groupInfo.length - 1) {
				group = new Group(cnt, 0);
				dimensionGroupList.append(group);
			}
		}
	}

	/**
	 * Set representative elements for contentGroupLists read from file
	 * 
	 * @param set
	 * @param recordPerspectiveID
	 * @param groupReps
	 */
	public static void setRecordGroupRepresentatives(DataTable table,
			String recordPerspectiveID, int[] groupReps) {

		int group = 0;

		RecordGroupList contentGroupList = table
				.getRecordPerspective(recordPerspectiveID).getVirtualArray()
				.getGroupList();

		contentGroupList.get(group).setRepresentativeElementIndex(0);
		group++;

		for (int i = 1; i < groupReps.length; i++) {
			if (groupReps[i] != groupReps[i - 1]) {
				contentGroupList.get(group).setRepresentativeElementIndex(i);
				group++;
			}
		}
	}

	/**
	 * Set representative elements for dimensionGroupLists read from file
	 * 
	 * @param set
	 * @param dimensionPerspectiveID
	 * @param groupReps
	 */
	public static void setDimensionGroupRepresentatives(DataTable table,
			String dimensionPerspectiveID, int[] groupReps) {

		int group = 0;

		DimensionGroupList dimensionGroupList = table
				.getDimensionPerspective(dimensionPerspectiveID).getVirtualArray()
				.getGroupList();

		dimensionGroupList.get(group).setRepresentativeElementIndex(0);
		group++;

		for (int i = 1; i < groupReps.length; i++) {
			if (groupReps[i] != groupReps[i - 1]) {
				dimensionGroupList.get(group).setRepresentativeElementIndex(i);
				group++;
			}
		}
	}

}
