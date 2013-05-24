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

import static org.caleydo.core.manager.GeneralManager.CALEYDO_HOME_PATH;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.caleydo.core.data.datadomain.ADataDomain;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.graph.tree.ClusterNode;
import org.caleydo.core.data.graph.tree.ClusterTree;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.graph.tree.TreePorter;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.IDTypeInitializer;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.manager.BasicInformation;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.util.system.FileOperations;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.XMLMemento;
import org.eclipse.ui.internal.ViewReference;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

/**
 * Serializes the current state of the application into a directory or file.
 *
 * @author Alexander Lex
 * @author Werner Puff
 * @author Marc Streit
 */
public final class ProjectManager {
	private static final String SEPARATOR = File.separator;

	private static final Logger log = Logger.create(ProjectManager.class);

	private static String file(String... elems) {
		StringBuilder b = new StringBuilder();
		b.append(CALEYDO_HOME_PATH);
		for (int i = 0; i < elems.length; ++i) {
			b.append(elems[i]);
			if (i != elems.length - 1)
				b.append(File.separator);
		}
		return b.toString();
	}

	private static String dir(String... elems) {
		return file(elems) + File.separator;
	}

	/** full path to directory of the recently open project */
	public static final String RECENT_PROJECT_FOLDER = dir("recent_project");

	/** full path to directory of the tmp copy of the recently open project */
	private static final String RECENT_PROJECT_FOLDER_TMP = dir("recent_project_tmp");

	private static final String WORKBENCH_XMI = "workbench.xmi";
	private static final String WORKBENCH_XMI_FILE = file(".metadata", ".plugins", "org.eclipse.e4.workbench",
			WORKBENCH_XMI);

	/** file name of the data table file in project-folders */
	private static final String DATA_TABLE_FILE = "data.csv";

	/** file name of the datadomain-file in project-folders */
	private static final String DATA_DOMAIN_FILE = "datadomain.xml";

	/** File name of file where list of plugins are to be stored */
	private static final String PLUG_IN_LIST_FILE = "plugins.xml";

	/** file name of the datadomain-file in project-folders */
	private static final String BASIC_INFORMATION_FILE = "basic_information.xml";

	/** meta data file name se {@link ProjectMetaData} */
	private static final String METADATA_FILE = "metadata.xml";

	/**
	 * full path to directory to temporarily store the projects file before zipping
	 */
	public static final String TEMP_PROJECT_ZIP_FOLDER = CALEYDO_HOME_PATH + "temp_load" + SEPARATOR;

	/**
	 * Loads the project from a specified zip-archive.
	 *
	 * @param fileName
	 *            name of the file to load the project from
	 * @return initialization data for the application from which it can restore itself
	 */
	public static void loadProjectFromZIP(String fileName) {
		FileOperations.deleteDirectory(TEMP_PROJECT_ZIP_FOLDER);
		ZipUtils.unzipToDirectory(fileName, TEMP_PROJECT_ZIP_FOLDER);
	}

	/**
	 * Loads the project from a directory
	 *
	 * @param dirName
	 *            name of the directory to load the project from
	 * @return initialization data for the application from which it can restore itself
	 */
	public static SerializationData loadProjectData(String dirName) {
		try {
			loadPluginData(dirName);
			return loadData(dirName);
		} catch (Exception e) {
			log.error("Failed to load project from\n" + dirName, e);
			return null;
		}

	}

	private static void loadPluginData(String dirName) throws JAXBException, BundleException {
		File pluginFile = new File(dirName + ProjectManager.PLUG_IN_LIST_FILE);
		if (!pluginFile.exists()) {
			log.info("Could not load plugin data from " + pluginFile);
			return;
		}

		final Unmarshaller unmarshaller = JAXBContext.newInstance(PlugInList.class).createUnmarshaller();
		PlugInList plugInList = (PlugInList) unmarshaller.unmarshal(pluginFile);

		for (String plugIn : plugInList.plugIns) {
			Bundle bundle = Platform.getBundle(plugIn);
			if (bundle == null) {
				log.warn("Could not load bundle: %s", plugIn);
				continue;
			}
			bundle.start();
		}
	}

	private static SerializationData loadData(String dirName) throws IOException, JAXBException {
		SerializationManager serializationManager = GeneralManager.get().getSerializationManager();

		Unmarshaller unmarshaller = serializationManager.getProjectContext().createUnmarshaller();

		File metaData = new File(dirName, METADATA_FILE);
		if (metaData.exists()) {
			ProjectMetaData m = (ProjectMetaData) unmarshaller.unmarshal(metaData);
			GeneralManager.get().setMetaData(m);
		}

		GeneralManager.get().setBasicInfo(
				(BasicInformation) unmarshaller.unmarshal(GeneralManager.get().getResourceLoader()
						.getResource(dirName + ProjectManager.BASIC_INFORMATION_FILE)));

		DataDomainList dataDomainList;

		dataDomainList = (DataDomainList) unmarshaller.unmarshal(GeneralManager.get().getResourceLoader()
				.getResource(dirName + ProjectManager.DATA_DOMAIN_FILE));

		SerializationData serializationData = new SerializationData();

		for (ADataDomain dataDomain : dataDomainList.getDataDomains()) {
			DataSetDescription dataSetDescription = dataDomain.getDataSetDescription();

			if (dataDomain.getDataDomainType().equals("org.caleydo.datadomain.genetic"))
				DataDomainManager.get().initalizeDataDomain("org.caleydo.datadomain.genetic");

			IDTypeInitializer.initIDs(dataSetDescription);
			dataDomain.init();
			// Register data domain by hand because it restored from the
			// serialization and not created via the
			// DataDomainManager
			DataDomainManager.get().register(dataDomain);
			// DataDomainManager usually takes care of that, we need to do it
			// manually for serialization

			Thread thread = new Thread(dataDomain, dataDomain.getDataDomainID());
			thread.start();
			if (dataDomain instanceof ATableBasedDataDomain) {

				String extendedDirName = dirName + dataDomain.getDataDomainID() + "_";

				// Overwrite filename with new one in caleydo project (data.csv)
				dataDomain.getDataSetDescription().setDataSourcePath(extendedDirName + ProjectManager.DATA_TABLE_FILE);

				DataDomainSerializationData dataInitializationData = new DataDomainSerializationData();
				dataInitializationData.setDataDomain((ATableBasedDataDomain) dataDomain);

				HashMap<String, Perspective> recordPerspectives = new HashMap<String, Perspective>();

				GeneralManager.get().getSWTGUIManager()
						.setProgressBarText("Loading groupings for: " + dataDomain.getLabel());

				Set<String> recordPerspectiveIDs = ((ATableBasedDataDomain) dataDomain).getRecordPerspectiveIDs();
				Set<String> dimensionPerspectiveIDs = ((ATableBasedDataDomain) dataDomain).getDimensionPerspectiveIDs();

				int nrPerspectives = recordPerspectiveIDs.size() + dimensionPerspectiveIDs.size();
				float progressBarFactor = 100f / nrPerspectives;
				int perspectiveCount = 0;
				for (String recordPerspectiveID : recordPerspectiveIDs) {

					Perspective recordPerspective = (Perspective) unmarshaller
							.unmarshal(GeneralManager
							.get().getResourceLoader().getResource(extendedDirName + recordPerspectiveID + ".xml"));
					recordPerspective.setDataDomain((ATableBasedDataDomain) dataDomain);
					recordPerspective.setIDType(((ATableBasedDataDomain) dataDomain).getRecordIDType());
					recordPerspectives.put(recordPerspectiveID, recordPerspective);

					ClusterTree tree = loadTree(extendedDirName + recordPerspectiveID + "_tree.xml",
							((ATableBasedDataDomain) dataDomain).getRecordIDType());
					if (tree != null)
						recordPerspective.setTree(tree);

					GeneralManager.get().getSWTGUIManager()
							.setProgressBarPercentage((int) (progressBarFactor * perspectiveCount));
					perspectiveCount++;
				}

				dataInitializationData.setRecordPerspectiveMap(recordPerspectives);

				HashMap<String, Perspective> dimensionPerspectives = new HashMap<String, Perspective>();

				for (String dimensionPerspectiveID : dimensionPerspectiveIDs) {

					Perspective dimensionPerspective = (Perspective) unmarshaller
							.unmarshal(GeneralManager.get().getResourceLoader()
									.getResource(extendedDirName + dimensionPerspectiveID + ".xml"));
					dimensionPerspective.setDataDomain((ATableBasedDataDomain) dataDomain);
					dimensionPerspective.setIDType(((ATableBasedDataDomain) dataDomain).getDimensionIDType());
					dimensionPerspectives.put(dimensionPerspectiveID, dimensionPerspective);

					ClusterTree tree = loadTree(extendedDirName + dimensionPerspectiveID + "_tree.xml",
							((ATableBasedDataDomain) dataDomain).getDimensionIDType());
					dimensionPerspective.setTree(tree);
					GeneralManager.get().getSWTGUIManager()
							.setProgressBarPercentage((int) (progressBarFactor * perspectiveCount));
					perspectiveCount++;

				}

				dataInitializationData.setDimensionPerspectiveMap(dimensionPerspectives);

				serializationData.addDataDomainSerializationData(dataInitializationData);

			}
		}

		for (ISerializationAddon addon : serializationManager.getAddons()) {
			addon.deserialize(dirName, unmarshaller, serializationData);
		}

		return serializationData;
	}

	/**
	 * Load trees as specified in loadDataParameters and write them to the table. FIXME: this is not aware of possibly
	 * alternative {@link RecordVAType}s or {@link DimensionVAType}s
	 *
	 * @param loadDataParameters
	 * @param set
	 */
	private static ClusterTree loadTree(String path, IDType idType) throws JAXBException, IOException {
		TreePorter treePorter = new TreePorter();
		return treePorter.importTree(path, idType);

	}

	/**
	 * Saves the project into a specified zip-archive.
	 *
	 * @param fileName
	 *            name of the file to save the project in.
	 */
	public static IRunnableWithProgress save(String fileName) {
		return save(fileName, false, DataDomainManager.get().getDataDomains(), ProjectMetaData.createDefault());
	}

	/**
	 * Saves the data and optionally also the workbench (i.e., view states, etc.)
	 *
	 * @param fileName
	 * @param onlyData
	 *            if true, only the data is saved, else also the workbench is saved
	 */
	public static IRunnableWithProgress save(final String fileName, final boolean onlyData,
			final Collection<? extends IDataDomain> dataDomains, final ProjectMetaData metaData) {
		return new IRunnableWithProgress() {
			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				monitor.beginTask("Saving Project to " + fileName, (onlyData ? 0 : 1) + 1 + dataDomains.size() + 1 + 1
						+ 1);
				log.info("saving to " + fileName);
				String tempDir = dir("temp_project" + System.currentTimeMillis());
				FileOperations.createDirectory(tempDir);
				int w = 0;
				try {
					if (!onlyData) {
						monitor.subTask("Saving Workbench Data");
						saveWorkbenchData(tempDir);
						monitor.worked(w++);
					}
					log.info("storing plugin data");
					monitor.subTask("Saving Plugin Data");
					savePluginData(tempDir);
					monitor.worked(w++);
					log.info("stored plugin data");
					log.info("storing data");
					saveData(tempDir, dataDomains, monitor, w, metaData);
					w += dataDomains.size() + 1;
					log.info("stored data");

					monitor.subTask("packing Project Data");
					ZipUtils.zipDirectory(tempDir, fileName);
					monitor.worked(w++);

					monitor.subTask("cleanup temporary data");
					FileOperations.deleteDirectory(tempDir);
					monitor.worked(w++);
					String message = "Caleydo project successfully written to\n" + fileName;
					log.info(message);
					monitor.done();
					showMessageBox("Project Save", message);
				} catch (Exception e) {
					String failureMessage = "Faild to save project to " + fileName + ".";
					log.error(failureMessage, e);
					showMessageBox("Project Save", failureMessage);
				}
			}
		};

	}

	private static void showMessageBox(final String title, final String message) {
		if (!PlatformUI.isWorkbenchRunning())
			return;
		final IWorkbench workbench = PlatformUI.getWorkbench();
		final Display d = workbench.getDisplay();
		d.asyncExec(new Runnable() {
			@Override
			public void run() {
				MessageBox messageBox = new MessageBox(workbench.getActiveWorkbenchWindow().getShell(), SWT.OK);
				messageBox.setText(title);
				messageBox.setMessage(message);
				messageBox.open();
			}
		});

	}

	/**
	 * Saves the project to the directory for the recent project
	 */
	public static IRunnableWithProgress saveRecentProject() {
		return new IRunnableWithProgress() {
			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				final Collection<IDataDomain> dataDomains = DataDomainManager.get().getDataDomains();

				monitor.beginTask("Saving Recent Project", 1 + 1 + dataDomains.size() + 1 + 1 + 1);
				int w = 0;
				monitor.subTask("Preparing Data");
				if (new File(RECENT_PROJECT_FOLDER).exists())
					FileOperations.renameDirectory(RECENT_PROJECT_FOLDER, RECENT_PROJECT_FOLDER_TMP);

				FileOperations.createDirectory(RECENT_PROJECT_FOLDER);
				monitor.worked(w++);
				try {
					log.info("saving plugin data");
					monitor.subTask("Saving Plugin Data");
					savePluginData(RECENT_PROJECT_FOLDER);
					monitor.worked(w++);
					log.info("saving data");
					saveData(RECENT_PROJECT_FOLDER, dataDomains, monitor, w, GeneralManager.get().getMetaData());
					w += dataDomains.size() + 1;
					log.info("saving workbench");
					monitor.subTask("packing Project Data");
					saveWorkbenchData(RECENT_PROJECT_FOLDER);
					monitor.worked(w++);
					log.info("saved");
				} catch (Exception e) {
					log.error("Faild to auto-save project.", e);
				}
				monitor.subTask("Cleanup temporary data");
				FileOperations.deleteDirectory(RECENT_PROJECT_FOLDER_TMP);
				log.info("saved recent project");
				monitor.done();
			}
		};

	}

	/**
	 * Save which plug-ins were loaded
	 *
	 * @param dirName
	 */
	private static void savePluginData(String dirName) throws JAXBException {
		PlugInList plugInList = new PlugInList();

		// find all bundles that are part of caleydo
		for (Bundle bundle : Platform.getBundle("org.caleydo.core").getBundleContext().getBundles()) {
			if (bundle.getSymbolicName().contains("org.caleydo") && bundle.getState() == Bundle.ACTIVE)
				plugInList.plugIns.add(bundle.getSymbolicName());
		}

		File pluginFile = new File(dirName + PLUG_IN_LIST_FILE);
		try {
			JAXBContext context = JAXBContext.newInstance(PlugInList.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.marshal(plugInList, pluginFile);
		} catch (JAXBException ex) {
			log.error("Could not serialize plug-in names: " + plugInList.toString(), ex);
			throw ex;
		}

	}

	private static void saveData(String dirName, Collection<? extends IDataDomain> toSave, IProgressMonitor monitor,
			int w, ProjectMetaData metaData) throws JAXBException,
			IOException {

		SerializationManager serializationManager = GeneralManager.get().getSerializationManager();
		JAXBContext projectContext = serializationManager.getProjectContext();

		Marshaller marshaller = projectContext.createMarshaller();

		for (ISerializationAddon addon : serializationManager.getAddons()) {
			addon.serialize(toSave, marshaller, dirName);
		}

		File dataDomainFile = new File(dirName + DATA_DOMAIN_FILE);
		List<ADataDomain> dataDomains = new ArrayList<ADataDomain>();

		for (IDataDomain dataDomain : toSave) {
			monitor.subTask("Persisting Datadomain: " + dataDomain.getLabel());
			if (!dataDomain.isSerializeable()) {
				monitor.worked(w++);
				continue;
			}

			dataDomains.add((ADataDomain) dataDomain);

			if (dataDomain instanceof ATableBasedDataDomain) {

				String extendedDirName = dirName + dataDomain.getDataDomainID() + "_";
				String dataDomainFileName = extendedDirName + DATA_TABLE_FILE;

				DataSetDescription dataSetDescription = dataDomain.getDataSetDescription();
				String sourceFileName = dataSetDescription.getDataSourcePath();

				if (sourceFileName.contains(RECENT_PROJECT_FOLDER))
					sourceFileName = sourceFileName.replace(RECENT_PROJECT_FOLDER, RECENT_PROJECT_FOLDER_TMP);

				try {
					FileOperations.writeInputStreamToFile(dataDomainFileName, GeneralManager.get().getResourceLoader()
							.getResource(sourceFileName));
				} catch (IllegalStateException e) {
					e.printStackTrace();
					throw new IllegalStateException("Error saving project file", e);
				}

				ATableBasedDataDomain tableBasedDataDomain = (ATableBasedDataDomain) dataDomain;

				for (String recordPerspectiveID : tableBasedDataDomain.getTable().getRecordPerspectiveIDs()) {
					saveDataPerspective(marshaller, extendedDirName, recordPerspectiveID, tableBasedDataDomain
							.getTable().getRecordPerspective(recordPerspectiveID));
				}

				for (String dimensionPerspectiveID : tableBasedDataDomain.getTable().getDimensionPerspectiveIDs()) {
					saveDataPerspective(marshaller, extendedDirName, dimensionPerspectiveID, tableBasedDataDomain
							.getTable().getDimensionPerspective(dimensionPerspectiveID));
				}
			}
			monitor.worked(w++);
		}

		monitor.subTask("Persisting General Information");
		String fileName = dirName + BASIC_INFORMATION_FILE;
		marshaller.marshal(GeneralManager.get().getBasicInfo(), new File(fileName));

		DataDomainList dataDomainList = new DataDomainList();
		dataDomainList.setDataDomains(dataDomains);

		marshaller.marshal(dataDomainList, dataDomainFile);

		File metaDataFile = new File(dirName, METADATA_FILE);
		marshaller.marshal(metaData, metaDataFile);

		monitor.worked(w++);

	}

	/**
	 * Saves the {@link VirtualArray} of the given type. The filename is created from the type.
	 *
	 * @param dir
	 *            directory to save the {@link VirtualArray} in.
	 * @param useCase
	 *            {@link IDataDomain} to retrieve the {@link VirtualArray} from.
	 * @param perspectiveID
	 *            type of the virtual array within the given {@link IDataDomain} .
	 */
	private static void saveDataPerspective(Marshaller marshaller, String dir, String perspectiveID,
			Perspective perspective) throws JAXBException, IOException {

		String fileName = dir + perspectiveID + ".xml";
		marshaller.marshal(perspective, new File(fileName));
		if (perspective.getTree() != null) {
			TreePorter treePorter = new TreePorter();
			Tree<ClusterNode> tree = perspective.getTree();
			treePorter.exportTree(dir + perspectiveID + "_tree.xml", tree);
		}

	}

	/**
	 * Saves all the view's serialized forms to the given directory. The directory must exist.
	 *
	 * @param dirName
	 *            name of the directory to save the views to.
	 */
	@SuppressWarnings("restriction")
	private static void saveWorkbenchData(String dirName) {
		log.info("storing workbench data");
		// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=2369
		// -> if this is implemented than a much cleaner solution can be used to persist the application model

		IWorkbench workbench = PlatformUI.getWorkbench();
		MApplication app = (MApplication) workbench.getService(MApplication.class);

		// persist the views in their models
		IWorkbenchWindow windows[] = workbench.getWorkbenchWindows();
		for (int i = 0; i < windows.length; i++) {
			IWorkbenchPage pages[] = windows[i].getPages();
			for (int j = 0; j < pages.length; j++) {
				IViewReference[] references = pages[j].getViewReferences();
				for (int k = 0; k < references.length; k++) {
					if (references[k].getView(false) != null) {
						try {
							persistView(((ViewReference) references[k]));
						} catch (IOException e) {
							log.warn("cant persist view: " + references[k].getId());
						}
					}
				}
			}
		}

		EObject local = EcoreUtil.copy((EObject) app); // create a local copy
		MApplication localapp = (MApplication) local;
		localapp.getMenuContributions().clear(); // manipulate like in the original
		localapp.getSelectedElement().setMainMenu(null);

		// dump the model
		ResourceSet resSet = new ResourceSetImpl();
		Resource resource = resSet.createResource(URI.createFileURI(dirName + WORKBENCH_XMI));

		resource.getContents().add(local);
		try {
			resource.save(Collections.EMPTY_MAP);
			log.info("stored workbench data");
		} catch (IOException e) {
			log.error("can't persist application.xmi", e);
		}
	}

	/**
	 * persist the given view see {@link ViewReference#persist}
	 *
	 * @param viewReference
	 * @throws IOException
	 */
	private static void persistView(ViewReference viewReference) throws IOException {
		IViewPart view = viewReference.getView(false);
		if (view != null) {
			XMLMemento root = XMLMemento.createWriteRoot("view"); //$NON-NLS-1$
			view.saveState(root);
			StringWriter writer = new StringWriter();
			root.save(writer);
			viewReference.getModel().getPersistedState().put("memento", writer.toString());
		}
	}

	public static void loadWorkbenchData(String dirName) {
		try {
			// clear old workbench file
			File target = new File(WORKBENCH_XMI_FILE);
			FileOperations.deleteDirectory(target);

			File workbenchFile = new File(dirName + WORKBENCH_XMI);
			if (!workbenchFile.exists()) {
				log.info("Could not load workbench data from " + workbenchFile);
				return;
			}
			// Create .metadata folder if it does not exist yet. This is the case when Caleydo is started the first
			// time.
			File f = target.getParentFile();
			if (!f.exists()) {
				f.mkdirs();
			}
			FileOperations.copyFolder(workbenchFile, target);
		} catch (IOException e) {
			log.warn("Could not load workbench data from " + dirName, e);
		}
	}

	/**
	 * returns the modification date of the recent project or null if no recent project exists
	 *
	 * @return
	 */
	public static Date getRecentProjectLastModified() {
		File f = new File(ProjectManager.RECENT_PROJECT_FOLDER + ProjectManager.DATA_DOMAIN_FILE);
		return f.exists() ? new Date(f.lastModified()) : null;
	}

	public static void deleteWorkbenchSettings() {
		// clear old workbench file
		File target = new File(WORKBENCH_XMI_FILE);
		FileOperations.deleteDirectory(target);
	}
}
