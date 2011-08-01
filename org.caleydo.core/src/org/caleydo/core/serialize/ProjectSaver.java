package org.caleydo.core.serialize;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.collection.table.LoadDataParameters;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.graph.tree.TreePorter;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.ADataDomain;
import org.caleydo.core.manager.datadomain.ATableBasedDataDomain;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.datadomain.IDataDomain;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.util.system.FileOperations;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
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

			ArrayList<ADataDomain> dataDomains = new ArrayList<ADataDomain>();
			for (IDataDomain dataDomain : DataDomainManager.get().getDataDomains()) {
				dataDomains.add((ADataDomain) dataDomain);
			}
			DataDomainList dataDomainList = new DataDomainList();
			dataDomainList.setDataDomains(dataDomains);

			marshaller.marshal(dataDomainList, dataDomainFile);

			for (IDataDomain dataDomain : DataDomainManager.get().getDataDomains()) {

				if (dataDomain instanceof ATableBasedDataDomain) {

					String extendedDirName = dirName + dataDomain.getDataDomainID() + "_";

					LoadDataParameters parameters = dataDomain.getLoadDataParameters();
					String sourceFileName = parameters.getFileName();

					if (sourceFileName.contains(RECENT_PROJECT_FOLDER))
						sourceFileName =
							sourceFileName.replace(RECENT_PROJECT_FOLDER, RECENT_PROJECT_FOLDER_TMP);

					try {
						FileOperations.writeInputStreamToFile(extendedDirName + DATA_TABLE_FILE,
							GeneralManager.get().getResourceLoader().getResource(sourceFileName));
					}
					catch (FileNotFoundException e) {
						throw new IllegalStateException("Error saving project file", e);
					}

					ATableBasedDataDomain setBasedDataDomain = (ATableBasedDataDomain) dataDomain;

					for (String type : setBasedDataDomain.getTable().getRegisteredRecordVATypes()) {
						saveRecordVA(marshaller, extendedDirName, setBasedDataDomain, type);
					}

					for (String type : setBasedDataDomain.getTable().getRegisteredDimensionVATypes()) {
						saveDimensionVA(marshaller, extendedDirName, setBasedDataDomain, type);
					}

					TreePorter treePorter = new TreePorter();
					Tree<ClusterNode> geneTree =
						setBasedDataDomain.getTable().getRecordData(DataTable.RECORD).getRecordTree();
					if (geneTree != null) {
						treePorter.exportTree(extendedDirName + GENE_TREE_FILE, geneTree);
					}

					treePorter = new TreePorter();
					Tree<ClusterNode> expTree =
						setBasedDataDomain.getTable().getDimensionData(DataTable.DIMENSION)
							.getDimensionTree();
					if (expTree != null) {
						treePorter.exportTree(extendedDirName + EXP_TREE_FILE, expTree);
					}
				}

				String fileName = dirName + BASIC_INFORMATION_FILE;
				marshaller.marshal(GeneralManager.get().getBasicInfo(), new File(fileName));
			}
		}
		catch (JAXBException ex) {
			throw new RuntimeException("Error saving project files (xml serialization)", ex);
		}
		catch (IOException ex) {
			throw new RuntimeException("Error saving project files (file access)", ex);
		}
	}

	/**
	 * Saves the {@link VirtualArray} of the given type. The filename is created from the type.
	 * 
	 * @param dir
	 *            directory to save the {@link VirtualArray} in.
	 * @param useCase
	 *            {@link IDataDomain} to retrieve the {@link VirtualArray} from.
	 * @param type
	 *            type of the virtual array within the given {@link IDataDomain}.
	 */
	private void saveRecordVA(Marshaller marshaller, String dir, ATableBasedDataDomain dataDomain, String type)
		throws JAXBException {

		String fileName = dir + "va_" + type.toString() + ".xml";
		RecordVirtualArray va = (RecordVirtualArray) dataDomain.getRecordVA(type);
		marshaller.marshal(va, new File(fileName));
	}

	private void saveDimensionVA(Marshaller marshaller, String dir, ATableBasedDataDomain dataDomain,
		String type) throws JAXBException {

		String fileName = dir + "va_" + type.toString() + ".xml";
		DimensionVirtualArray va = (DimensionVirtualArray) dataDomain.getDimensionVA(type);
		marshaller.marshal(va, new File(fileName));
	}
	
	/**
	 * Saves all the view's serialized forms to the given directory. The directory must exist.
	 * 
	 * @param dirName
	 *            name of the directory to save the views to.
	 */
	private void saveWorkbenchData(String dirName) {

//		SaveStateAction saveAction = new SaveStateAction();
//		saveAction.setSaveFolder(dirName);
//		saveAction.run(null);
		
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
