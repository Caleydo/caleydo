package org.caleydo.core.data.collection.table;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.data.CmdDataCreateColumn;
import org.caleydo.core.command.data.CmdDataCreateTable;
import org.caleydo.core.command.data.parser.CmdLoadFileLookupTable;
import org.caleydo.core.command.data.parser.CmdLoadFileNDimensions;
import org.caleydo.core.data.collection.EDimensionType;
import org.caleydo.core.data.collection.ExternalDataRepresentation;
import org.caleydo.core.data.collection.dimension.NominalColumn;
import org.caleydo.core.data.collection.dimension.NumericalColumn;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.id.ManagedObjectType;
import org.caleydo.core.data.mapping.IDMappingManager;
import org.caleydo.core.data.mapping.MappingType;
import org.caleydo.core.data.virtualarray.group.DimensionGroupList;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.RecordGroupList;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.parser.ascii.TabularAsciiDataReader;

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
	 * Loads the set-file as specified in the {@link IDataDomain}'s {@link LoadDataParameters} and stores the
	 * raw-data in the useCase
	 * 
	 * @param useCase
	 */
	public static byte[] loadSetFile(LoadDataParameters parameters) {
		String setFileName = parameters.getFileName();
		if (setFileName == null) {
			throw new RuntimeException("No set-file name specified in use case");
		}

		File setFile = new File(setFileName);
		byte[] buffer;
		try {
			FileInputStream is = new FileInputStream(setFile);
			if (setFile.length() > Integer.MAX_VALUE) {
				throw new RuntimeException("set-file is larger than maximum internal file-dimension-size");
			}
			buffer = new byte[(int) setFile.length()];
			is.read(buffer, 0, buffer.length);
		}
		catch (IOException ex) {
			throw new RuntimeException("Could not read from specified set-file '" + setFileName + "'", ex);
		}
		return buffer;
	}

	/**
	 * Saves the set-data contained in the useCase in a new created temp-file. The {@link LoadDataParameters}
	 * of the useCase are set according to the created set-file
	 * 
	 * @param parameters
	 *            set-load parameters to store the filename;
	 * @param data
	 *            set-data to save
	 */
	public static void saveTableFile(LoadDataParameters parameters, byte[] data) {
		File homeDir = new File(GeneralManager.CALEYDO_HOME_PATH);
		File setFile;
		try {
			setFile = File.createTempFile(DATA_FILE_PREFIX, "csv", homeDir);
			parameters.setFileName(setFile.getCanonicalPath());
		}
		catch (IOException ex) {
			throw new RuntimeException("Could not create temporary file to store the set file", ex);
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
		}
		catch (FileNotFoundException ex) {
			throw new RuntimeException("Could not create temporary file to store the set file", ex);
		}
		catch (IOException ex) {
			throw new RuntimeException("Could not write to temportary set file", ex);
		}
		finally {
			if (os != null) {
				try {
					os.close();
				}
				catch (IOException ex) {
					// nothing to do here, assuming output stream is already closed
				}
			}
		}
	}

	/**
	 * Creates the dimensions from a previously prepared dimension definition.
	 * 
	 * @param loadDataParameters
	 *            definition how to create the dimensions
	 * @return <code>true</code>if the creation was successful, <code>false</code> otherwise
	 */
	public static boolean createColumns(LoadDataParameters loadDataParameters) {

		ArrayList<Integer> columnIds = null;
		boolean createColumnsFromExistingIDs = false;

		if (loadDataParameters.getColumnIds() == null)
			columnIds = new ArrayList<Integer>();
		else {
			columnIds = loadDataParameters.getColumnIds();
			createColumnsFromExistingIDs = true;
		}

		TabularAsciiDataReader reader = new TabularAsciiDataReader(null, loadDataParameters.getDataDomain());
		reader.setTokenPattern(loadDataParameters.getInputPattern());
		ArrayList<EDimensionType> dataTypes = reader.getColumnDataTypes();

		boolean abort = false;
		Iterator<String> columnLabelIterator = loadDataParameters.getColumnLabels().iterator();
		CmdDataCreateColumn cmdCreateColumn;
		String columnLabel;

		ATableBasedDataDomain dataDomain = loadDataParameters.getDataDomain();

		IDMappingManager columnIDMappingManager;
		IDType columnIDType;
		IDType hrColumnIDType;
		if (dataDomain.isColumnDimension()) {
			columnIDMappingManager = dataDomain.getDimensionIDMappingManager();
			columnIDType = dataDomain.getDimensionIDType();
			hrColumnIDType = dataDomain.getHumanReadableDimensionIDType();
		}
		else {
			columnIDMappingManager = dataDomain.getRecordIDMappingManager();
			columnIDType = dataDomain.getRecordIDType();
			hrColumnIDType = dataDomain.getHumanReadableRecordIDType();

		}

		MappingType mappingType = columnIDMappingManager.createMap(columnIDType, hrColumnIDType, false);
		Map<Integer, String> dimensionIDMap = columnIDMappingManager.getMap(mappingType);

		int columnCount = 0;

		for (EDimensionType dataType : dataTypes) {
			switch (dataType) {
				case FLOAT:
					cmdCreateColumn =
						(CmdDataCreateColumn) GeneralManager.get().getCommandManager()
							.createCommandByType(CommandType.CREATE_COLUMN);

					if (createColumnsFromExistingIDs)
						cmdCreateColumn.setAttributes(ManagedObjectType.COLUMN_NUMERICAL,
							columnIds.get(columnCount++));
					else
						cmdCreateColumn.setAttributes(ManagedObjectType.COLUMN_NUMERICAL);

					cmdCreateColumn.doCommand();
					columnLabel = columnLabelIterator.next();
					NumericalColumn column = (NumericalColumn) cmdCreateColumn.getCreatedObject();
					column.setLabel(columnLabel);
					dimensionIDMap.put(column.getID(), columnLabel);

					if (!createColumnsFromExistingIDs)
						columnIds.add(column.getID());

					break;
				case STRING:
					cmdCreateColumn =
						(CmdDataCreateColumn) GeneralManager.get().getCommandManager()
							.createCommandByType(CommandType.CREATE_COLUMN);

					if (createColumnsFromExistingIDs)
						cmdCreateColumn.setAttributes(ManagedObjectType.COLUMN_NOMINAL,
							columnIds.get(columnCount++));
					else
						cmdCreateColumn.setAttributes(ManagedObjectType.COLUMN_NOMINAL);

					cmdCreateColumn.doCommand();

					columnLabel = columnLabelIterator.next();
					NominalColumn<?> nominalColumn = (NominalColumn<?>) cmdCreateColumn.getCreatedObject();
					nominalColumn.setLabel(columnLabel);

					if (!createColumnsFromExistingIDs)
						columnIds.add(nominalColumn.getID());

				case SKIP:
					// nothing to do, just skip
					break;
				case ABORT:
					abort = true;
					break;
				default:
					// nothing to do
					break;
			}
			if (abort) {
				break;
			}
		}
		columnIDMappingManager.createReverseMap(mappingType);
		loadDataParameters.setDimensionIds(columnIds);

		return true;
	}

	/**
	 * Creates the set from a previously prepared dimension definition.
	 */
	public static DataTable createData(ATableBasedDataDomain dataDomain, boolean createDefaultPerspectives) {

		LoadDataParameters loadDataParameters = dataDomain.getLoadDataParameters();
		ArrayList<Integer> dimensionIDs = loadDataParameters.getColumnIds();

		// Create table
		CmdDataCreateTable cmdCreateSet =
			(CmdDataCreateTable) GeneralManager.get().getCommandManager()
				.createCommandByType(CommandType.CREATE_DATA_TABLE);

		cmdCreateSet.setAttributes(dimensionIDs, dataDomain);
		cmdCreateSet.doCommand();

		// Load dynamic mapping
		CmdLoadFileLookupTable cmdLoadLookupTableFile =
			(CmdLoadFileLookupTable) GeneralManager.get().getCommandManager()
				.createCommandByType(CommandType.LOAD_LOOKUP_TABLE_FILE);

		String lookupTableInfo =
			loadDataParameters.getFileIDTypeName() + "_2_" + dataDomain.getRecordIDType().getTypeName()
				+ " REVERSE";

		cmdLoadLookupTableFile.setAttributes(loadDataParameters.getFileName(),
			loadDataParameters.getStartParseFileAtLine(), -1, lookupTableInfo,
			loadDataParameters.getDelimiter(), "", dataDomain.getRecordIDCategory());

		cmdLoadLookupTableFile.doCommand();

		// --------- data loading ---------------

		// Trigger file loading command
		CmdLoadFileNDimensions cmdLoadCSV =
			(CmdLoadFileNDimensions) GeneralManager.get().getCommandManager()
				.createCommandByType(CommandType.LOAD_DATA_FILE);

		cmdLoadCSV.setAttributes(dimensionIDs, loadDataParameters);
		cmdLoadCSV.doCommand();

		if (!cmdLoadCSV.isParsingOK()) {
			// TODO: Clear created set and dimensions which are empty
			return null;
		}

		// ----------------------------------------
		DataTable table = dataDomain.getTable();

		if (createDefaultPerspectives) {
			table.createDefaultRecordPerspective();
			table.createDefaultDimensionPerspective();
		}

		// loadTrees(loadDataParameters, set);

		if (loadDataParameters.isMinDefined()) {
			table.getMetaData().setMin(loadDataParameters.getMin());
		}
		if (loadDataParameters.isMaxDefined()) {
			table.getMetaData().setMax(loadDataParameters.getMax());
		}

		boolean isSetHomogeneous = loadDataParameters.isDataHomogeneous();

		if (loadDataParameters.getMathFilterMode().equals("Normal")) {
			table.setExternalDataRepresentation(ExternalDataRepresentation.NORMAL, isSetHomogeneous);
		}
		else if (loadDataParameters.getMathFilterMode().equals("Log10")) {
			table.setExternalDataRepresentation(ExternalDataRepresentation.LOG10, isSetHomogeneous);
		}
		else if (loadDataParameters.getMathFilterMode().equals("Log2")) {
			table.setExternalDataRepresentation(ExternalDataRepresentation.LOG2, isSetHomogeneous);
		}
		else
			throw new IllegalStateException("Unknown data representation type");

		return table;
	}

	public static void setTables(DataTable table, ArrayList<Integer> dimensionIDs) {
		for (int dimensionID : dimensionIDs) {
			table.addColumn(dimensionID);
		}
	}

	/**
	 * Switch the representation of the data. When this is called the data in normalized is replaced with data
	 * calculated from the mode specified.
	 * 
	 * @param externalDataRep
	 *            Determines how the data is visualized. For options see {@link ExternalDataRepresentation}
	 * @param bIsSetHomogeneous
	 *            Determines whether a set is homogeneous or not. Homogeneous means that the sat has a global
	 *            maximum and minimum, meaning that all dimensions in the set contain equal data. If false,
	 *            each dimension is treated separately, has it's own min and max etc. Sets that contain
	 *            nominal data MUST be inhomogeneous.
	 */
	public static void setExternalDataRepresentation(DataTable table,
		ExternalDataRepresentation externalDataRep, boolean isSetHomogeneous) {
		table.setExternalDataRepresentation(externalDataRep, isSetHomogeneous);
	}

	/**
	 * Creates a contentGroupList from the group information read from a stored file
	 * 
	 * @param set
	 * @param vaType
	 *            specify for which va type this is valid
	 * @param groupInfo
	 *            the array list extracted from the file
	 */
	public static void setContentGroupList(DataTable table, String vaType, int[] groupInfo) {

		int cluster = 0, cnt = 0;

		RecordGroupList contentGroupList =
			table.getRecordPerspective(vaType).getVirtualArray().getGroupList();
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
	 * Creates a dimensionGroupList from the group information read from a stored file
	 * 
	 * @param set
	 * @param vaType
	 *            specify for which va type this is valid
	 * @param groupInfo
	 *            the array list extracted from the file
	 */
	public static void setDimensionGroupList(DataTable table, String vaType, int[] groupInfo) {
		int cluster = 0, cnt = 0;

		DimensionGroupList dimensionGroupList =
			table.getDimensionPerspective(vaType).getVirtualArray().getGroupList();
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
	public static void setRecordGroupRepresentatives(DataTable table, String recordPerspectiveID,
		int[] groupReps) {

		int group = 0;

		RecordGroupList contentGroupList =
			table.getRecordPerspective(recordPerspectiveID).getVirtualArray().getGroupList();

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
	public static void setDimensionGroupRepresentatives(DataTable table, String dimensionPerspectiveID,
		int[] groupReps) {

		int group = 0;

		DimensionGroupList dimensionGroupList =
			table.getDimensionPerspective(dimensionPerspectiveID).getVirtualArray().getGroupList();

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
