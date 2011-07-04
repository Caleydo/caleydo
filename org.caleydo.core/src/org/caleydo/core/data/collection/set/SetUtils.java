package org.caleydo.core.data.collection.set;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.bind.JAXBException;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.data.CmdDataCreateSet;
import org.caleydo.core.command.data.CmdDataCreateStorage;
import org.caleydo.core.command.data.parser.CmdLoadFileLookupTable;
import org.caleydo.core.command.data.parser.CmdLoadFileNStorages;
import org.caleydo.core.data.collection.EExternalDataRepresentation;
import org.caleydo.core.data.collection.EStorageType;
import org.caleydo.core.data.collection.INumericalStorage;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.storage.NominalStorage;
import org.caleydo.core.data.graph.tree.ClusterTree;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.graph.tree.TreePorter;
import org.caleydo.core.data.virtualarray.group.ContentGroupList;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.StorageGroupList;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.manager.datadomain.IDataDomain;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.parser.ascii.TabularAsciiDataReader;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Utility class that features creating, loading and saving sets and storages.
 * 
 * @author Werner Puff
 * @author Alexander Lex
 */
public class SetUtils {

	/** prefix for temporary set-file */
	public static final String DATA_FILE_PREFIX = "setfile";

	/** prefix for temporary gene-tree--file */
	public static final String CONTENT_TREE_FILE_PREFIX = "contenttree";

	/** prefix for temporary experiment-tree-file */
	public static final String STORAGE_TREE_FILE_PREFIX = "storagetree";

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
				throw new RuntimeException("set-file is larger than maximum internal file-storage-size");
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
	 * Creates the storages from a previously prepared storage defintion.
	 * 
	 * @param loadDataParameters
	 *            definition how to create the storages
	 * @return <code>true</code>if the creation was successful, <code>false</code> otherwise
	 */
	public static boolean createStorages(LoadDataParameters loadDataParameters) {
		ArrayList<Integer> storageIds = new ArrayList<Integer>();

		TabularAsciiDataReader reader = new TabularAsciiDataReader(null, loadDataParameters.getDataDomain());
		reader.setTokenPattern(loadDataParameters.getInputPattern());
		ArrayList<EStorageType> dataTypes = reader.getColumnDataTypes();

		boolean abort = false;
		Iterator<String> storageLabelIterator = loadDataParameters.getStorageLabels().iterator();
		CmdDataCreateStorage cmdCreateStorage;
		String storageLabel;
		for (EStorageType dataType : dataTypes) {
			switch (dataType) {
				case FLOAT:
					cmdCreateStorage =
						(CmdDataCreateStorage) GeneralManager.get().getCommandManager()
							.createCommandByType(ECommandType.CREATE_STORAGE);
					cmdCreateStorage.setAttributes(EManagedObjectType.STORAGE_NUMERICAL);
					cmdCreateStorage.doCommand();

					storageLabel = storageLabelIterator.next();
					INumericalStorage storage = (INumericalStorage) cmdCreateStorage.getCreatedObject();
					storage.setLabel(storageLabel);
					storageIds.add(storage.getID());
					break;
				case STRING:
					cmdCreateStorage =
						(CmdDataCreateStorage) GeneralManager.get().getCommandManager()
							.createCommandByType(ECommandType.CREATE_STORAGE);
					cmdCreateStorage.setAttributes(EManagedObjectType.STORAGE_NOMINAL);
					cmdCreateStorage.doCommand();

					storageLabel = storageLabelIterator.next();
					NominalStorage<?> nominalStorage =
						(NominalStorage<?>) cmdCreateStorage.getCreatedObject();
					nominalStorage.setLabel(storageLabel);
					storageIds.add(nominalStorage.getID());
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

		loadDataParameters.setStorageIds(storageIds);

		return true;
	}

	/**
	 * Creates the set from a previously prepared storage definition.
	 */
	public static Set createData(ASetBasedDataDomain dataDomain) {

		LoadDataParameters loadDataParameters = dataDomain.getLoadDataParameters();
		ArrayList<Integer> storageIDs = loadDataParameters.getStorageIds();

		// Create SET
		CmdDataCreateSet cmdCreateSet =
			(CmdDataCreateSet) GeneralManager.get().getCommandManager()
				.createCommandByType(ECommandType.CREATE_SET_DATA);

		cmdCreateSet.setAttributes(storageIDs, dataDomain);
		cmdCreateSet.doCommand();

		// ----------------- load dynamic mapping ---------------------

		CmdLoadFileLookupTable cmdLoadLookupTableFile =
			(CmdLoadFileLookupTable) GeneralManager.get().getCommandManager()
				.createCommandByType(ECommandType.LOAD_LOOKUP_TABLE_FILE);

		if (dataDomain.getDataDomainType().equals("org.caleydo.datadomain.genetic")) {
			String lookupTableInfo =
				loadDataParameters.getFileIDTypeName() + "_2_" + dataDomain.getContentIDType().getTypeName()
					+ " REVERSE";

			cmdLoadLookupTableFile.setAttributes(loadDataParameters.getFileName(),
				loadDataParameters.getStartParseFileAtLine(), -1, lookupTableInfo,
				loadDataParameters.getDelimiter(), "");

			cmdLoadLookupTableFile.doCommand();
		}
//		else if (dataDomain.getDataDomainType().equals("org.caleydo.datadomain.generic")) {
//			String lookupTableInfo =
//				loadDataParameters.getFileIDTypeName() + "_2_" + dataDomain.getContentIDType().getTypeName()
//					+ " REVERSE";
//
//			cmdLoadLookupTableFile.setAttributes(loadDataParameters.getFileName(),
//				loadDataParameters.getStartParseFileAtLine(), -1, lookupTableInfo,
//				loadDataParameters.getDelimiter(), "");
//		}
		// else {
		// throw new IllegalStateException("Not implemented for " + dataDomain);
		// }

		// --------- data loading ---------------

		// Trigger file loading command
		CmdLoadFileNStorages cmdLoadCSV =
			(CmdLoadFileNStorages) GeneralManager.get().getCommandManager()
				.createCommandByType(ECommandType.LOAD_DATA_FILE);

		cmdLoadCSV.setAttributes(storageIDs, loadDataParameters);
		cmdLoadCSV.doCommand();

		if (!cmdLoadCSV.isParsingOK()) {
			// TODO: Clear created set and storages which are empty
			return null;
		}

		// ----------------------------------------

		Set set = (Set) dataDomain.getSet();

		// loadTrees(loadDataParameters, set);

		if (loadDataParameters.isMinDefined()) {
			set.setMin(loadDataParameters.getMin());
		}
		if (loadDataParameters.isMaxDefined()) {
			set.setMax(loadDataParameters.getMax());
		}

		boolean isSetHomogeneous = loadDataParameters.isDataHomogeneous();
		
		if (loadDataParameters.getMathFilterMode().equals("Normal")) {
			set.setExternalDataRepresentation(EExternalDataRepresentation.NORMAL, isSetHomogeneous);
		}
		else if (loadDataParameters.getMathFilterMode().equals("Log10")) {
			set.setExternalDataRepresentation(EExternalDataRepresentation.LOG10, isSetHomogeneous);
		}
		else if (loadDataParameters.getMathFilterMode().equals("Log2")) {
			set.setExternalDataRepresentation(EExternalDataRepresentation.LOG2, isSetHomogeneous);
		}
		else
			throw new IllegalStateException("Unknown data representation type");

		return set;
	}

	public static void setStorages(Set set, ArrayList<Integer> storageIDs) {
		for (int iStorageID : storageIDs) {
			set.addStorage(iStorageID);
		}

		set.finalizeAddedStorages();

	}

	/**
	 * Creates the gene-cluster information of the given {@link ISet} as xml-String
	 * 
	 * @param set
	 *            {@link ISet} to create the gene-cluster information of
	 * @return xml-document representing the gene-cluster information
	 */
	public static String getGeneClusterXml(ISet set) {
		String xml = null;

		try {
			xml = getTreeClusterXml(set.getContentData(ISet.CONTENT).getContentTree());
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
	 * Creates the experiment-cluster information of the given {@link ISet} as XML-String
	 * 
	 * @param set
	 *            {@link ISet} to create the experiment-cluster information of
	 * @return XML-document representing the experiment-cluster information
	 */
	public static String getExperimentClusterXml(ISet set) {
		String xml = null;

		try {
			xml = getTreeClusterXml(set.getStorageData(Set.STORAGE).getStorageTree());
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
	 * possibly alternative {@link ContentVAType}s or {@link StorageVAType}s
	 * 
	 * @param loadDataParameters
	 * @param set
	 */
	public static void loadTrees(LoadDataParameters loadDataParameters, ISet set) {
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
					tree.setUseDefaultComparator(false);
					set.getContentData(ISet.CONTENT).setContentTree(tree);
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
					tree = treePorter.importStorageTree(experimentsTreeFileName);
					set.getStorageData(Set.STORAGE).setStorageTree(tree);
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
	 *            Determines how the data is visualized. For options see {@link EExternalDataRepresentation}
	 * @param bIsSetHomogeneous
	 *            Determines whether a set is homogeneous or not. Homogeneous means that the sat has a global
	 *            maximum and minimum, meaning that all storages in the set contain equal data. If false, each
	 *            storage is treated separately, has it's own min and max etc. Sets that contain nominal data
	 *            MUST be inhomogeneous.
	 */
	public static void setExternalDataRepresentation(Set set, EExternalDataRepresentation externalDataRep,
		boolean isSetHomogeneous) {
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
	public static void setContentGroupList(Set set, String vaType, int[] groupInfo) {

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
	 * Creates a storageGroupList from the group information read from a stored file
	 * 
	 * @param set
	 * @param vaType
	 *            specify for which va type this is valid
	 * @param groupInfo
	 *            the array list extracted from the file
	 */
	public static void setStorageGroupList(Set set, String vaType, int[] groupInfo) {
		int cluster = 0, cnt = 0;

		StorageGroupList storageGroupList = set.getStorageData(vaType).getStorageVA().getGroupList();
		storageGroupList.clear();

		for (int i = 0; i < groupInfo.length; i++) {
			Group group = null;
			if (cluster != groupInfo[i]) {
				group = new Group(cnt, 0);
				storageGroupList.append(group);
				cluster++;
				cnt = 0;
			}
			cnt++;
			if (i == groupInfo.length - 1) {
				group = new Group(cnt, 0);
				storageGroupList.append(group);
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
	public static void setContentGroupRepresentatives(Set set, String vaType, int[] groupReps) {

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
	 * Set representative elements for storageGroupLists read from file
	 * 
	 * @param set
	 * @param vaType
	 * @param groupReps
	 */
	public static void setStorageGroupRepresentatives(Set set, String vaType, int[] groupReps) {

		int group = 0;

		StorageGroupList storageGroupList = set.getStorageData(vaType).getStorageVA().getGroupList();

		storageGroupList.get(group).setRepresentativeElementIndex(0);
		group++;

		for (int i = 1; i < groupReps.length; i++) {
			if (groupReps[i] != groupReps[i - 1]) {
				storageGroupList.get(group).setRepresentativeElementIndex(i);
				group++;
			}
		}
	}

}
