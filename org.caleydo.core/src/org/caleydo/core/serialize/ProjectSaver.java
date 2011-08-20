package org.caleydo.core.serialize;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.caleydo.core.data.collection.table.DataPerspective;
import org.caleydo.core.data.collection.table.LoadDataParameters;
import org.caleydo.core.data.datadomain.ADataDomain;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.graph.tree.TreePorter;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.util.system.FileOperations;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.XMLMemento;
import org.eclipse.ui.internal.IWorkbenchConstants;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.WorkbenchMessages;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.osgi.framework.Bundle;

/**
 * Serializes the current state of the application into a directory or file.
 * 
 * @author Alexander Lex
 * @author Werner Puff
 * @author Marc Streit
 */
@SuppressWarnings("restriction")
public class ProjectSaver {

	/** full path to directory to temporarily store the projects file before zipping */
	public static final String TEMP_PROJECT_FOLDER = GeneralManager.CALEYDO_HOME_PATH + "temp_project"
		+ File.separator;

	/** full path to directory of the recently open project */
	public static final String RECENT_PROJECT_FOLDER = GeneralManager.CALEYDO_HOME_PATH + "recent_project"
		+ File.separator;

	/** full path to directory of the tmp copy of the recently open project */
	public static final String RECENT_PROJECT_FOLDER_TMP = GeneralManager.CALEYDO_HOME_PATH
		+ "recent_project_tmp" + File.separator;

	public static final String WORKBENCH_MEMENTO_FOLDER = ".metadata" + File.separator + ".plugins"
		+ File.separator + "org.eclipse.ui.workbench" + File.separator;

	public static final String WORKBENCH_MEMENTO_FILE = "workbench.xml";

	/** file name of the data table file in project-folders */
	public static final String DATA_TABLE_FILE = "data.csv";

	/** file name of the datadomain-file in project-folders */
	public static final String DATA_DOMAIN_FILE = "datadomain.xml";

	/** File name of file where list of plugins are to be stored */
	public static final String PLUG_IN_LIST_FILE = "plugins.xml";

	/** file name of the gene-cluster-file in project-folders */
	public static final String GENE_TREE_FILE = "gene_cluster.xml";

	/** file name of the experiment-cluster-file in project-folders */
	public static final String EXP_TREE_FILE = "experiment_cluster.xml";

	/** file name of the datadomain-file in project-folders */
	public static final String BASIC_INFORMATION_FILE = "basic_information.xml";

	/**
	 * Saves the project into a specified zip-archive.
	 * 
	 * @param fileName
	 *            name of the file to save the project in.
	 */
	public void save(String fileName) {

		FileOperations.createDirectory(TEMP_PROJECT_FOLDER);

		savePluginData(TEMP_PROJECT_FOLDER);
		saveProjectData(TEMP_PROJECT_FOLDER);

		ZipUtils zipUtils = new ZipUtils();
		zipUtils.zipDirectory(TEMP_PROJECT_FOLDER, fileName);

		FileOperations.deleteDirectory(TEMP_PROJECT_FOLDER);

		String message = "Caleydo project successfully written to\n" + TEMP_PROJECT_FOLDER;

		Logger.log(new Status(IStatus.INFO, this.toString(), message));

		MessageBox messageBox =
			new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.OK);
		messageBox.setText("Project Save");
		messageBox.setMessage(message);
		messageBox.open();
	}

	/**
	 * Saves the project to the directory for the recent project
	 */
	public void saveRecentProject() {

		if (new File(RECENT_PROJECT_FOLDER).exists())
			FileOperations.renameDirectory(RECENT_PROJECT_FOLDER, RECENT_PROJECT_FOLDER_TMP);

		FileOperations.createDirectory(RECENT_PROJECT_FOLDER);

		savePluginData(RECENT_PROJECT_FOLDER);
		saveProjectData(RECENT_PROJECT_FOLDER);

		FileOperations.deleteDirectory(RECENT_PROJECT_FOLDER_TMP);
	}

	/**
	 * Save which plug-ins were loaded
	 * 
	 * @param dirName
	 */
	private void savePluginData(String dirName) {
		PlugInList plugInList = new PlugInList();

		for (Bundle bundle : Platform.getBundle("org.caleydo.core").getBundleContext().getBundles()) {
			if (bundle.getSymbolicName().contains("org.caleydo") && bundle.getState() == Bundle.ACTIVE)
				plugInList.plugIns.add(bundle.getSymbolicName());
		}

		File pluginFile = new File(dirName + PLUG_IN_LIST_FILE);
		try {
			JAXBContext context = JAXBContext.newInstance(PlugInList.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.marshal(plugInList, pluginFile);
		}
		catch (JAXBException ex) {
			Logger.log(new Status(Status.ERROR, this.toString(), "Could not serialize plug-in names: "
				+ plugInList.toString(), ex));
			ex.printStackTrace();
		}

	}

	/**
	 * Saves the project to the directory with the given name. The directory is created before saving.
	 * 
	 * @param dirName
	 *            directory to save the project-files into
	 */
	private void saveProjectData(String dirName) {

		saveData(dirName);
		saveWorkbenchData(dirName);
	}

	private void saveData(String dirName) {

		SerializationManager serializationManager = GeneralManager.get().getSerializationManager();
		JAXBContext projectContext = serializationManager.getProjectContext();

		try {
			Marshaller marshaller = projectContext.createMarshaller();

			File dataDomainFile = new File(dirName + DATA_DOMAIN_FILE);
			List<ADataDomain> dataDomains = new ArrayList<ADataDomain>();

			for (IDataDomain dataDomain : DataDomainManager.get().getDataDomains()) {

				dataDomains.add((ADataDomain) dataDomain);

				if (dataDomain instanceof ATableBasedDataDomain) {

					String extendedDirName = dirName + dataDomain.getDataDomainID() + "_";
					String dataDomainFileName = extendedDirName + DATA_TABLE_FILE;

					LoadDataParameters parameters = dataDomain.getLoadDataParameters();
					String sourceFileName = parameters.getFileName();

					if (sourceFileName.contains(RECENT_PROJECT_FOLDER))
						sourceFileName =
							sourceFileName.replace(RECENT_PROJECT_FOLDER, RECENT_PROJECT_FOLDER_TMP);

					try {
						FileOperations.writeInputStreamToFile(dataDomainFileName, GeneralManager.get()
							.getResourceLoader().getResource(sourceFileName));
					}
					catch (FileNotFoundException e) {
						throw new IllegalStateException("Error saving project file", e);
					}

					ATableBasedDataDomain tableBasedDataDomain = (ATableBasedDataDomain) dataDomain;

					for (String recordPerspectiveID : tableBasedDataDomain.getTable()
						.getRegisteredRecordPerspectives()) {
						saveDataPerspective(marshaller, extendedDirName, tableBasedDataDomain,
							recordPerspectiveID,
							tableBasedDataDomain.getTable().getRecordPerspective(recordPerspectiveID));
					}

					for (String dimensionPerspectiveID : tableBasedDataDomain.getTable()
						.getRegisteredDimensionPerspectives()) {
						saveDataPerspective(marshaller, extendedDirName, tableBasedDataDomain,
							dimensionPerspectiveID,
							tableBasedDataDomain.getTable().getDimensionPerspective(dimensionPerspectiveID));
					}

				}

				String fileName = dirName + BASIC_INFORMATION_FILE;
				marshaller.marshal(GeneralManager.get().getBasicInfo(), new File(fileName));
			}

			DataDomainList dataDomainList = new DataDomainList();
			dataDomainList.setDataDomains(dataDomains);

			marshaller.marshal(dataDomainList, dataDomainFile);
		}
		catch (JAXBException ex) {
			throw new RuntimeException("Error saving project files (xml serialization)", ex);
		}

	}

	/**
	 * Saves the {@link VirtualArray} of the given type. The filename is created from the type.
	 * 
	 * @param dir
	 *            directory to save the {@link VirtualArray} in.
	 * @param useCase
	 *            {@link IDataDomain} to retrieve the {@link VirtualArray} from.
	 * @param perspectiveID
	 *            type of the virtual array within the given {@link IDataDomain}.
	 */
	private void saveDataPerspective(Marshaller marshaller, String dir, ATableBasedDataDomain dataDomain,
		String perspectiveID, DataPerspective<?, ?, ?, ?> perspective) throws JAXBException {

		String fileName = dir + perspectiveID + ".xml";
		marshaller.marshal(perspective, new File(fileName));
		if (!perspective.isTreeDefaultTree()) {
			TreePorter treePorter = new TreePorter();
			Tree<ClusterNode> tree = perspective.getTree();
			if (tree != null) {
				try {
					treePorter.exportTree(dir + perspectiveID + "_tree.xml", tree);
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Saves the gene-tree-xml in a new created temp-file.
	 * 
	 * @param parameters
	 *            set-load parameters to store the filename;
	 * @param data
	 *            set-data to save
	 */
	// public static void saveGeneTreeFile(LoadDataParameters parameters, String data) {
	// File homeDir = new File(GeneralManager.CALEYDO_HOME_PATH);
	// File geneFile;
	// try {
	// geneFile = File.createTempFile(RECORD_TREE_FILE_PREFIX, "xml", homeDir);
	// parameters.setGeneTreeFileName(geneFile.getCanonicalPath());
	// }
	// catch (IOException ex) {
	// throw new RuntimeException("Could not create temporary file to store the set file", ex);
	// }
	// saveFile(data.getBytes(), geneFile);
	// }

	private void saveDimensionPerspective(Marshaller marshaller, String dir,
		ATableBasedDataDomain dataDomain, String type) throws JAXBException {

		String fileName = dir + "va_" + type.toString() + ".xml";
		DimensionVirtualArray va = (DimensionVirtualArray) dataDomain.getDimensionVA(type);
		marshaller.marshal(va, new File(fileName));

		// treePorter = new TreePorter();
		// Tree<ClusterNode> expTree =
		// tableBasedDataDomain.getTable().getDimensionData(DataTable.DIMENSION)
		// .getDimensionTree();
		// if (expTree != null) {
		// treePorter.exportTree(extendedDirName + EXP_TREE_FILE, expTree);
	}

	/**
	 * Creates the record-cluster information of the given {@link DataTable} as xml-String
	 * 
	 * @param set
	 *            {@link DataTable} to create the gene-cluster information of
	 * @return xml-document representing the gene-cluster information
	 */
	// public static String getRecordClusterXml(DataTable table) {
	// String xml = null;
	//
	// try {
	// xml = getTreeClusterXml(table.getRecordData(DataTable.RECORD).getTree());
	// }
	// catch (IOException ex) {
	// throw new RuntimeException("error while writing experiment-cluster-XML to String", ex);
	// }
	// catch (JAXBException ex) {
	// throw new RuntimeException("error while creating experiment-cluster-XML", ex);
	// }
	//
	// return xml;
	// }

	/**
	 * Creates the dimension-cluster information of the given {@link DataTable} as XML-String
	 * 
	 * @param set
	 *            {@link DataTable} to create the experiment-cluster information of
	 * @return XML-document representing the experiment-cluster information
	 */
	// public static String getDimensionClusterXml(DataTable table) {
	// String xml = null;
	//
	// try {
	// xml = getTreeClusterXml(table.getDimensionData(DataTable.DIMENSION).getDimensionTree());
	// }
	// catch (IOException ex) {
	// throw new RuntimeException("error while writing experiment-cluster-XML to String", ex);
	// }
	// catch (JAXBException ex) {
	// throw new RuntimeException("error while creating experiment-cluster-XML", ex);
	// }
	//
	// return xml;
	// }

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
	 * Saves the experiments-tree-xml in a new created temp-file.
	 * 
	 * @param parameters
	 *            set-load parameters to store the filename;
	 * @param data
	 *            set-data to save
	 */
	// public static void saveExperimentsTreeFile(LoadDataParameters parameters, String data) {
	// File homeDir = new File(GeneralManager.CALEYDO_HOME_PATH);
	// File expFile;
	// try {
	// expFile = File.createTempFile(DIMENSION_TREE_FILE_PREFIX, "xml", homeDir);
	// parameters.setExperimentsFileName(expFile.getCanonicalPath());
	// }
	// catch (IOException ex) {
	// throw new RuntimeException("Could not create temporary file to store the set file", ex);
	// }
	// saveFile(data.getBytes(), expFile);
	// }

	/**
	 * Saves all the view's serialized forms to the given directory. The directory must exist.
	 * 
	 * @param dirName
	 *            name of the directory to save the views to.
	 */
	private void saveWorkbenchData(String dirName) {

		// Activator.trace( "Saving state." );
		XMLMemento memento = XMLMemento.createWriteRoot(IWorkbenchConstants.TAG_WORKBENCH);
		saveState(memento);
		saveMementoToFile(memento);

		try {
			FileOperations.copyFolder(new File(GeneralManager.CALEYDO_HOME_PATH
				+ ProjectSaver.WORKBENCH_MEMENTO_FOLDER + ProjectSaver.WORKBENCH_MEMENTO_FILE), new File(
				dirName + ProjectSaver.WORKBENCH_MEMENTO_FILE));
		}
		catch (Exception e) {
			throw new RuntimeException("Error saving workbench data (file access)", e);
		}
	}

	/**
	 * Method from http://eclipsenuggets.blogspot.com/2007/09/how-to-save-eclipse-ui-workbench-state_6644.html
	 */
	private IStatus saveState(final IMemento memento) {
		MultiStatus result =
			new MultiStatus(PlatformUI.PLUGIN_ID, IStatus.OK, WorkbenchMessages.Workbench_problemsSaving,
				null);
		// Save the version number.
		memento.putString(IWorkbenchConstants.TAG_VERSION, "2.0");
		// Save how many plug-ins were loaded while restoring the workbench
		memento.putInteger(IWorkbenchConstants.TAG_PROGRESS_COUNT, 10); // we guesstimate this
		// Save the advisor state.
		result.add(Status.OK_STATUS);
		// Save the workbench windows.
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
		for (int nX = 0; nX < windows.length; nX++) {
			WorkbenchWindow window = (WorkbenchWindow) windows[nX];
			IMemento childMem = memento.createChild(IWorkbenchConstants.TAG_WINDOW);
			result.merge(window.saveState(childMem));
		}
		result.add(((Workbench) workbench).getEditorHistory().saveState(
			memento.createChild(IWorkbenchConstants.TAG_MRU_LIST)));
		return result;
	}

	/**
	 * Method from http://eclipsenuggets.blogspot.com/2007/09/how-to-save-eclipse-ui-workbench-state_6644.html
	 */
	private void saveMementoToFile(XMLMemento memento) {
		File stateFile = getWorkbenchStateFile();
		if (stateFile != null) {
			try {
				FileOutputStream stream = new FileOutputStream(stateFile);
				OutputStreamWriter writer = new OutputStreamWriter(stream, "utf-8"); //$NON-NLS-1$
				memento.save(writer);
				writer.close();
			}
			catch (IOException ioe) {
				stateFile.delete();
				// Activator.log( ioe );
			}
		}
	}

	/**
	 * Method from http://eclipsenuggets.blogspot.com/2007/09/how-to-save-eclipse-ui-workbench-state_6644.html
	 */
	private File getWorkbenchStateFile() {
		IPath path = WorkbenchPlugin.getDefault().getDataLocation();
		if (path == null) {
			return null;
		}
		path = path.append(WORKBENCH_MEMENTO_FILE);
		return path.toFile();
	}
}
