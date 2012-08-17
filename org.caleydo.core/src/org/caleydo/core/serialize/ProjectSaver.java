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
package org.caleydo.core.serialize;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.caleydo.core.data.datadomain.ADataDomain;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.graph.tree.ClusterNode;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.graph.tree.TreePorter;
import org.caleydo.core.data.perspective.variable.AVariablePerspective;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.manager.GeneralManager;
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

	/**
	 * full path to directory to temporarily store the projects file before
	 * zipping
	 */
	public static final String TEMP_PROJECT_FOLDER = GeneralManager.CALEYDO_HOME_PATH
			+ "temp_project" + File.separator;

	/** full path to directory of the recently open project */
	public static final String RECENT_PROJECT_FOLDER = GeneralManager.CALEYDO_HOME_PATH
			+ "recent_project" + File.separator;

	/** full path to directory of the tmp copy of the recently open project */
	public static final String RECENT_PROJECT_FOLDER_TMP = GeneralManager.CALEYDO_HOME_PATH
			+ "recent_project_tmp" + File.separator;

	public static final String WORKBENCH_MEMENTO_FOLDER = GeneralManager.CALEYDO_HOME_PATH
			+ ".metadata" + File.separator + ".plugins" + File.separator
			+ "org.eclipse.ui.workbench" + File.separator;

	public static final String WORKBENCH_MEMENTO_FILE = "workbench.xml";

	/** file name of the data table file in project-folders */
	public static final String DATA_TABLE_FILE = "data.csv";

	/** file name of the datadomain-file in project-folders */
	public static final String DATA_DOMAIN_FILE = "datadomain.xml";

	/** File name of file where list of plugins are to be stored */
	public static final String PLUG_IN_LIST_FILE = "plugins.xml";

	/** file name of the datadomain-file in project-folders */
	public static final String BASIC_INFORMATION_FILE = "basic_information.xml";

	/**
	 * Saves the project into a specified zip-archive.
	 * 
	 * @param fileName name of the file to save the project in.
	 */
	public void save(String fileName) {
		save(fileName, false);
	}

	/**
	 * Saves the data and optionally also the workbench (i.e., view states,
	 * etc.)
	 * 
	 * @param fileName
	 * @param onlyData if true, only the data is saved, else also the workbench
	 *            is saved
	 */
	public void save(String fileName, boolean onlyData) {

		FileOperations.createDirectory(TEMP_PROJECT_FOLDER);

		try {
			if (!onlyData) {
				saveWorkbenchData(TEMP_PROJECT_FOLDER);
			}
			savePluginData(TEMP_PROJECT_FOLDER);
			saveData(TEMP_PROJECT_FOLDER);

			ZipUtils zipUtils = new ZipUtils();
			zipUtils.zipDirectory(TEMP_PROJECT_FOLDER, fileName);

			FileOperations.deleteDirectory(TEMP_PROJECT_FOLDER);

			String message = "Caleydo project successfully written to\n" + fileName;
			Logger.log(new Status(IStatus.INFO, this.toString(), message));

			if (PlatformUI.isWorkbenchRunning()) {
				MessageBox messageBox = new MessageBox(PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getShell(), SWT.OK);
				messageBox.setText("Project Save");
				messageBox.setMessage(message);
				messageBox.open();
			}

		}
		catch (Exception savingException) {
			String failureMessage = "Faild to save project to " + fileName + ".";
			Logger.log(new Status(Status.ERROR, "org.caleydo.core", failureMessage,
					savingException));

			if (PlatformUI.isWorkbenchRunning()) {
				MessageBox messageBox = new MessageBox(PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getShell(), SWT.OK);
				messageBox.setText("Project Save");
				messageBox.setMessage(failureMessage);
				messageBox.open();
				return;
			}
		}

	}

	/**
	 * Saves the project to the directory for the recent project
	 */
	public void saveRecentProject() {

		if (new File(RECENT_PROJECT_FOLDER).exists())
			FileOperations.renameDirectory(RECENT_PROJECT_FOLDER, RECENT_PROJECT_FOLDER_TMP);

		FileOperations.createDirectory(RECENT_PROJECT_FOLDER);

		try {
			savePluginData(RECENT_PROJECT_FOLDER);
			saveData(RECENT_PROJECT_FOLDER);
			saveWorkbenchData(RECENT_PROJECT_FOLDER);
		}
		catch (Exception savingException) {
			Logger.log(new Status(Status.ERROR, "org.caleydo.core",
					"Faild to auto-save project.", savingException));
		}

		FileOperations.deleteDirectory(RECENT_PROJECT_FOLDER_TMP);
	}

	/**
	 * Save which plug-ins were loaded
	 * 
	 * @param dirName
	 */
	private void savePluginData(String dirName) throws JAXBException {
		PlugInList plugInList = new PlugInList();

		for (Bundle bundle : Platform.getBundle("org.caleydo.core").getBundleContext()
				.getBundles()) {
			if (bundle.getSymbolicName().contains("org.caleydo")
					&& bundle.getState() == Bundle.ACTIVE)
				plugInList.plugIns.add(bundle.getSymbolicName());
		}

		File pluginFile = new File(dirName + PLUG_IN_LIST_FILE);
		try {
			JAXBContext context = JAXBContext.newInstance(PlugInList.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.marshal(plugInList, pluginFile);
		}
		catch (JAXBException ex) {
			Logger.log(new Status(Status.ERROR, this.toString(),
					"Could not serialize plug-in names: " + plugInList.toString(), ex));
			throw ex;
		}

	}

	/**
	 * Saves the project to the directory with the given name. The directory is
	 * created before saving.
	 * 
	 * @param dirName directory to save the project-files into
	 */
	// private void saveProjectData(String dirName) throws JAXBException,
	// IOException {
	//
	// }

	private void saveData(String dirName) throws JAXBException, IOException {

		SerializationManager serializationManager = GeneralManager.get()
				.getSerializationManager();
		JAXBContext projectContext = serializationManager.getProjectContext();

		Marshaller marshaller = projectContext.createMarshaller();

		File dataDomainFile = new File(dirName + DATA_DOMAIN_FILE);
		List<ADataDomain> dataDomains = new ArrayList<ADataDomain>();

		for (IDataDomain dataDomain : DataDomainManager.get().getDataDomains()) {

			if (!dataDomain.isSerializeable())
				continue;

			dataDomains.add((ADataDomain) dataDomain);

			if (dataDomain instanceof ATableBasedDataDomain) {

				String extendedDirName = dirName + dataDomain.getDataDomainID() + "_";
				String dataDomainFileName = extendedDirName + DATA_TABLE_FILE;

				DataSetDescription dataSetDescription = dataDomain.getDataSetDescription();
				String sourceFileName = dataSetDescription.getDataSourcePath();

				if (sourceFileName.contains(RECENT_PROJECT_FOLDER))
					sourceFileName = sourceFileName.replace(RECENT_PROJECT_FOLDER,
							RECENT_PROJECT_FOLDER_TMP);

				try {
					FileOperations.writeInputStreamToFile(dataDomainFileName, GeneralManager
							.get().getResourceLoader().getResource(sourceFileName));
				}
				catch (FileNotFoundException e) {
					e.printStackTrace();
					throw new IllegalStateException("Error saving project file", e);
				}

				ATableBasedDataDomain tableBasedDataDomain = (ATableBasedDataDomain) dataDomain;

				for (String recordPerspectiveID : tableBasedDataDomain.getTable()
						.getRecordPerspectiveIDs()) {
					saveDataPerspective(
							marshaller,
							extendedDirName,
							recordPerspectiveID,
							tableBasedDataDomain.getTable().getRecordPerspective(
									recordPerspectiveID));
				}

				for (String dimensionPerspectiveID : tableBasedDataDomain.getTable()
						.getDimensionPerspectiveIDs()) {
					saveDataPerspective(
							marshaller,
							extendedDirName,
							dimensionPerspectiveID,
							tableBasedDataDomain.getTable().getDimensionPerspective(
									dimensionPerspectiveID));
				}

			}

			String fileName = dirName + BASIC_INFORMATION_FILE;
			marshaller.marshal(GeneralManager.get().getBasicInfo(), new File(fileName));
		}

		DataDomainList dataDomainList = new DataDomainList();
		dataDomainList.setDataDomains(dataDomains);

		marshaller.marshal(dataDomainList, dataDomainFile);

	}

	/**
	 * Saves the {@link VirtualArray} of the given type. The filename is created
	 * from the type.
	 * 
	 * @param dir directory to save the {@link VirtualArray} in.
	 * @param useCase {@link IDataDomain} to retrieve the {@link VirtualArray}
	 *            from.
	 * @param perspectiveID type of the virtual array within the given
	 *            {@link IDataDomain} .
	 */
	private void saveDataPerspective(Marshaller marshaller, String dir, String perspectiveID,
			AVariablePerspective<?, ?, ?, ?> perspective) throws JAXBException, IOException {

		String fileName = dir + perspectiveID + ".xml";
		marshaller.marshal(perspective, new File(fileName));
		if (perspective.getTree() != null) {
			TreePorter treePorter = new TreePorter();
			Tree<ClusterNode> tree = perspective.getTree();
			treePorter.exportTree(dir + perspectiveID + "_tree.xml", tree);
		}

	}

	// /**
	// * Creates the tree-cluster information of the given {@link Tree} as
	// XML-String
	// *
	// * @param tree
	// * {@link Tree} to create the XML-String of
	// * @return XML-String of the given {@link Tree}
	// * @throws IOException
	// * if a error occurs while writing the XML-String
	// * @throws JAXBException
	// * if a XML-serialization error occurs
	// */
	// public static String getTreeClusterXml(Tree<ClusterNode> tree) throws
	// IOException, JAXBException {
	// String xml = null;
	//
	// if (tree != null) {
	// StringWriter writer = new StringWriter();
	// TreePorter treePorter = new TreePorter();
	// treePorter.exportTree(writer, tree);
	// xml = writer.getBuffer().toString();
	// }
	// return xml;
	// }

	/**
	 * Saves all the view's serialized forms to the given directory. The
	 * directory must exist.
	 * 
	 * @param dirName name of the directory to save the views to.
	 */
	private void saveWorkbenchData(String dirName) {

		// Activator.trace( "Saving state." );
		XMLMemento memento = XMLMemento.createWriteRoot(IWorkbenchConstants.TAG_WORKBENCH);
		saveState(memento);
		saveMementoToFile(memento);

		try {
			FileOperations.copyFolder(getWorkbenchStateFile(), new File(dirName
					+ ProjectSaver.WORKBENCH_MEMENTO_FILE));
		}
		catch (Exception e) {
			throw new RuntimeException("Error saving workbench data (file access)", e);
		}
	}

	/**
	 * Method from
	 * http://eclipsenuggets.blogspot.com/2007/09/how-to-save-eclipse
	 * -ui-workbench-state_6644.html
	 */
	private IStatus saveState(final IMemento memento) {
		MultiStatus result = new MultiStatus(PlatformUI.PLUGIN_ID, IStatus.OK,
				WorkbenchMessages.Workbench_problemsSaving, null);
		// Save the version number.
		memento.putString(IWorkbenchConstants.TAG_VERSION, "2.0");
		// Save how many plug-ins were loaded while restoring the workbench
		memento.putInteger(IWorkbenchConstants.TAG_PROGRESS_COUNT, 10); // we
																		// guesstimate
																		// this
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
	 * Method from
	 * http://eclipsenuggets.blogspot.com/2007/09/how-to-save-eclipse
	 * -ui-workbench-state_6644.html
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
	 * Method from
	 * http://eclipsenuggets.blogspot.com/2007/09/how-to-save-eclipse
	 * -ui-workbench-state_6644.html
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
