package org.caleydo.core.data.collection.table;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.bind.JAXBException;

import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.data.CmdDataCreateDimension;
import org.caleydo.core.command.data.CmdDataCreateTable;
import org.caleydo.core.command.data.parser.CmdLoadFileLookupTable;
import org.caleydo.core.command.data.parser.CmdLoadFileNDimensions;
import org.caleydo.core.data.collection.DimensionType;
import org.caleydo.core.data.collection.ExternalDataRepresentation;
import org.caleydo.core.data.collection.dimension.NominalDimension;
import org.caleydo.core.data.collection.dimension.NumericalDimension;
import org.caleydo.core.data.graph.tree.ClusterTree;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.graph.tree.TreePorter;
import org.caleydo.core.data.id.ManagedObjectType;
import org.caleydo.core.data.virtualarray.group.ContentGroupList;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.DimensionGroupList;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.ATableBasedDataDomain;
import org.caleydo.core.manager.datadomain.IDataDomain;
import org.caleydo.core.parser.ascii.TabularAsciiDataReader;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

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
	public static final String CONTENT_TREE_FILE_PREFIX = "contenttree";

	/** prefix for temporary experiment-tree-file */
	public static final String STORAGE_TREE_FILE_PREFIX = "dimensiontree";

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
	public static void saveSetFile(LoadDataParameters parameters, byte[] data) {
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
	public static boolean createDimensions(LoadDataParameters loadDataParameters) {

		ArrayList<Integer> dimensionIds = null;
		boolean createDimensionsFromExistingIDs = false;
		
		if (loadDataParameters.getDimensionIds() == null)
			dimensionIds = new ArrayList<Integer>();
		else
		{
			dimensionIds = loadDataParameters.getDimensionIds();
			createDimensionsFromExistingIDs = true;
		}

		TabularAsciiDataReader reader = new TabularAsciiDataReader(null, loadDataParameters.getDataDomain());
		reader.setTokenPattern(loadDataParameters.getInputPattern());
		ArrayList<DimensionType> dataTypes = reader.getColumnDataTypes();

		boolean abort = false;
		Iterator<String> dimensionLabelIterator = loadDataParameters.getDimensionLabels().iterator();
		CmdDataCreateDimension cmdCreateDimension;
		String dimensionLabel;

		for (int dataTableIndex = 0; dataTableIndex < dataTypes.size(); dataTableIndex++) {
			DimensionType dataType = dataTypes.get(dataTableIndex);
			switch (dataType) {
				case FLOAT:
					cmdCreateDimension =
						(CmdDataCreateDimension) GeneralManager.get().getCommandManager()
							.createCommandByType(CommandType.CREATE_DIMENSION);

					if (createDimensionsFromExistingIDs)
						cmdCreateDimension.setAttributes(ManagedObjectType.DIMENSION_NUMERICAL,
							dimensionIds.get(dataTableIndex));
					else
						cmdCreateDimension.setAttributes(ManagedObjectType.DIMENSION_NUMERICAL);

					cmdCreateDimension.doCommand();
					dimensionLabel = dimensionLabelIterator.next();
					NumericalDimension dimension = (NumericalDimension) cmdCreateDimension.getCreatedObject();
					dimension.setLabel(dimensionLabel);
					
					if (!createDimensionsFromExistingIDs)
						dimensionIds.add(dimension.getID());
						
					break;
				case STRING:
					cmdCreateDimension =
						(CmdDataCreateDimension) GeneralManager.get().getCommandManager()
							.createCommandByType(CommandType.CREATE_DIMENSION);

					if (createDimensionsFromExistingIDs)
						cmdCreateDimension.setAttributes(ManagedObjectType.DIMENSION_NOMINAL,
							dimensionIds.get(dataTableIndex));
					else
					{
						cmdCreateDimension.setAttributes(ManagedObjectType.DIMENSION_NOMINAL);
					}

					cmdCreateDimension.doCommand();

					dimensionLabel = dimensionLabelIterator.next();
					NominalDimension<?> nominalDimension =
						(NominalDimension<?>) cmdCreateDimension.getCreatedObject();
					nominalDimension.setLabel(dimensionLabel);
					
					if (!createDimensionsFromExistingIDs)
						dimensionIds.add(nominalDimension.getID());

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

		loadDataParameters.setDimensionIds(dimensionIds);

		return true;
	}

	/**
	 * Creates the set from a previously prepared dimension definition.
	 */
	public static DataTable createData(ATableBasedDataDomain dataDomain) {

		LoadDataParameters loadDataParameters = dataDomain.getLoadDataParameters();
		ArrayList<Integer> dimensionIDs = loadDataParameters.getDimensionIds();

		// Create SET
		CmdDataCreateTable cmdCreateSet =
			(CmdDataCreateTable) GeneralManager.get().getCommandManager()
				.createCommandByType(CommandType.CREATE_DATA_TABLE);

		cmdCreateSet.setAttributes(dimensionIDs, dataDomain);
		cmdCreateSet.doCommand();

		// Load dynamic mapping
		CmdLoadFileLookupTable cmdLoadLookupTableFile =
			(CmdLoadFileLookupTable) GeneralManager.get().getCommandManager()
				.createCommandByType(CommandType.LOAD_LOOKUP_TABLE_FILE);

		if (dataDomain.getDataDomainType().equals("org.caleydo.datadomain.genetic")) {
			String lookupTableInfo =
				loadDataParameters.getFileIDTypeName() + "_2_" + dataDomain.getContentIDType().getTypeName()
					+ " REVERSE";

			cmdLoadLookupTableFile.setAttributes(loadDataParameters.getFileName(),
				loadDataParameters.getStartParseFileAtLine(), -1, lookupTableInfo,
				loadDataParameters.getDelimiter(), "");

			cmdLoadLookupTableFile.doCommand();
		}
		// FIXME: general data loading (non genetic)
		// else if (dataDomain.getDataDomainType().equals("org.caleydo.datadomain.generic")) {
		// String lookupTableInfo =
		// loadDataParameters.getFileIDTypeName() + "_2_" + dataDomain.getContentIDType().getTypeName()
		// + " REVERSE";
		//
		// cmdLoadLookupTableFile.setAttributes(loadDataParameters.getFileName(),
		// loadDataParameters.getStartParseFileAtLine(), -1, lookupTableInfo,
		// loadDataParameters.getDelimiter(), "");
		// }
		// else {
		// throw new IllegalStateException("Not implemented for " + dataDomain);
		// }

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
		DataTable dataTable = (DataTable) dataDomain.getDataTable();

		// loadTrees(loadDataParameters, set);

		if (loadDataParameters.isMinDefined()) {
			dataTable.getMetaData().setMin(loadDataParameters.getMin());
		}
		if (loadDataParameters.isMaxDefined()) {
			dataTable.getMetaData().setMax(loadDataParameters.getMax());
		}

		boolean isSetHomogeneous = loadDataParameters.isDataHomogeneous();

		if (loadDataParameters.getMathFilterMode().equals("Normal")) {
			dataTable.setExternalDataRepresentation(ExternalDataRepresentation.NORMAL, isSetHomogeneous);
		}
		else if (loadDataParameters.getMathFilterMode().equals("Log10")) {
			dataTable.setExternalDataRepresentation(ExternalDataRepresentation.LOG10, isSetHomogeneous);
		}
		else if (loadDataParameters.getMathFilterMode().equals("Log2")) {
			dataTable.setExternalDataRepresentation(ExternalDataRepresentation.LOG2, isSetHomogeneous);
		}
		else
			throw new IllegalStateException("Unknown data representation type");

		return dataTable;
	}

	public static void setDataTables(DataTable dataTable, ArrayList<Integer> dimensionIDs) {
		for (int iDimensionID : dimensionIDs) {
			dataTable.addDimension(iDimensionID);
		}

		dataTable.finalizeAddedDimensions();
	}

	/**
	 * Creates the gene-cluster information of the given {@link DataTable} as xml-String
	 * 
	 * @param set
	 *            {@link DataTable} to create the gene-cluster information of
	 * @return xml-document representing the gene-cluster information
	 */
	public static String getGeneClusterXml(DataTable set) {
		String xml = null;

		try {
			xml = getTreeClusterXml(set.getContentData(DataTable.RECORD).getContentTree());
		}
		catch (IOException ex) {
			throw new RuntimeException("error while writing experiment-cluster-XML to String", ex);
		}
		catch (JAXBException ex) {
			throw new RuntimeException("error while creating experiment-cluster-XML", ex);
		}

		return xml;
	}

	/**
	 * Creates the experiment-cluster information of the given {@link DataTable} as XML-String
	 * 
	 * @param set
	 *            {@link DataTable} to create the experiment-cluster information of
	 * @return XML-document representing the experiment-cluster information
	 */
	public static String getExperimentClusterXml(DataTable set) {
		String xml = null;

		try {
			xml = getTreeClusterXml(set.getDimensionData(DataTable.DIMENSION).getDimensionTree());
		}
		catch (IOException ex) {
			throw new RuntimeException("error while writing experiment-cluster-XML to String", ex);
		}
		catch (JAXBException ex) {
			throw new RuntimeException("error while creating experiment-cluster-XML", ex);
		}

		return xml;
	}

	/**
	 * Creates the tree-cluster information of the given {@link Tree} as XML-String
	 * 
	 * @param tree
	 *            {@link Tree} to create the XML-String of
	 * @return XML-String of the given {@link Tree}
	 * @throws IOException
	 *             if a error occurs while writing the XML-String
	 * @throws JAXBException
	 *             if a XML-serialization error occurs
	 */
	public static String getTreeClusterXml(Tree<ClusterNode> tree) throws IOException, JAXBException {
		String xml = null;

		if (tree != null) {
			StringWriter writer = new StringWriter();
			TreePorter treePorter = new TreePorter();
			treePorter.exportTree(writer, tree);
			xml = writer.getBuffer().toString();
		}

		return xml;
	}

	/**
	 * Saves the gene-tree-xml in a new created temp-file.
	 * 
	 * @param parameters
	 *            set-load parameters to store the filename;
	 * @param data
	 *            set-data to save
	 */
	public static void saveGeneTreeFile(LoadDataParameters parameters, String data) {
		File homeDir = new File(GeneralManager.CALEYDO_HOME_PATH);
		File geneFile;
		try {
			geneFile = File.createTempFile(CONTENT_TREE_FILE_PREFIX, "xml", homeDir);
			parameters.setGeneTreeFileName(geneFile.getCanonicalPath());
		}
		catch (IOException ex) {
			throw new RuntimeException("Could not create temporary file to store the set file", ex);
		}
		saveFile(data.getBytes(), geneFile);
	}

	/**
	 * Saves the experiments-tree-xml in a new created temp-file.
	 * 
	 * @param parameters
	 *            set-load parameters to store the filename;
	 * @param data
	 *            set-data to save
	 */
	public static void saveExperimentsTreeFile(LoadDataParameters parameters, String data) {
		File homeDir = new File(GeneralManager.CALEYDO_HOME_PATH);
		File expFile;
		try {
			expFile = File.createTempFile(STORAGE_TREE_FILE_PREFIX, "xml", homeDir);
			parameters.setExperimentsFileName(expFile.getCanonicalPath());
		}
		catch (IOException ex) {
			throw new RuntimeException("Could not create temporary file to store the set file", ex);
		}
		saveFile(data.getBytes(), expFile);
	}

	/**
	 * Load trees as specified in loadDataParameters and write them to the set. FIXME: this is not aware of
	 * possibly alternative {@link ContentVAType}s or {@link DimensionVAType}s
	 * 
	 * @param loadDataParameters
	 * @param set
	 */
	public static void loadTrees(LoadDataParameters loadDataParameters, DataTable set) {
		// import gene tree
		String geneTreeFileName = loadDataParameters.getGeneTreeFileName();
		if (geneTreeFileName != null) {
			if (geneTreeFileName.equals("") == false) {
				Logger.log(new Status(IStatus.INFO, "SetUtils", "Loading gene tree from file "
					+ geneTreeFileName));

				TreePorter treePorter = new TreePorter();
				treePorter.setDataDomain(set.getDataDomain());
				ClusterTree tree;
				try {

					tree = treePorter.importTree(geneTreeFileName, set.getDataDomain().getContentIDType());
					// tree.setSortingStrategy(ESortingStrategy.AVERAGE_VALUE);
					set.getContentData(DataTable.RECORD).setContentTree(tree);
				}
				catch (JAXBException e) {
					e.printStackTrace();
				}
				catch (FileNotFoundException e) {
					// do nothing - no gene tree is available
				}
			}
		}

		// import experiment tree
		String experimentsTreeFileName = loadDataParameters.getExperimentsFileName();
		if (experimentsTreeFileName != null) {
			if (experimentsTreeFileName.equals("") == false) {
				Logger.log(new Status(IStatus.INFO, "SetUtils", "Loading experiments tree from file "
					+ experimentsTreeFileName));

				TreePorter treePorter = new TreePorter();
				treePorter.setDataDomain(set.getDataDomain());
				ClusterTree tree;
				try {
					tree = treePorter.importDimensionTree(experimentsTreeFileName);
					set.getDimensionData(DataTable.DIMENSION).setDimensionTree(tree);
					set.getDataDomain().createDimensionGroupsFromDimensionTree(tree);
				}
				catch (JAXBException e) {
					e.printStackTrace();
				}
				catch (FileNotFoundException e) {
					// do nothing - no experiment tree is available
				}
			}
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
	 *            maximum and minimum, meaning that all dimensions in the set contain equal data. If false, each
	 *            dimension is treated separately, has it's own min and max etc. Sets that contain nominal data
	 *            MUST be inhomogeneous.
	 */
	public static void setExternalDataRepresentation(DataTable set,
		ExternalDataRepresentation externalDataRep, boolean isSetHomogeneous) {
		set.setExternalDataRepresentation(externalDataRep, isSetHomogeneous);
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
	public static void setContentGroupList(DataTable set, String vaType, int[] groupInfo) {

		int cluster = 0, cnt = 0;

		ContentGroupList contentGroupList = set.getContentData(vaType).getContentVA().getGroupList();
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
	public static void setDimensionGroupList(DataTable set, String vaType, int[] groupInfo) {
		int cluster = 0, cnt = 0;

		DimensionGroupList dimensionGroupList = set.getDimensionData(vaType).getDimensionVA().getGroupList();
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
	 * @param vaType
	 * @param groupReps
	 */
	public static void setContentGroupRepresentatives(DataTable set, String vaType, int[] groupReps) {

		int group = 0;

		ContentGroupList contentGroupList = set.getContentData(vaType).getContentVA().getGroupList();

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
	 * @param vaType
	 * @param groupReps
	 */
	public static void setDimensionGroupRepresentatives(DataTable set, String vaType, int[] groupReps) {

		int group = 0;

		DimensionGroupList dimensionGroupList = set.getDimensionData(vaType).getDimensionVA().getGroupList();

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
