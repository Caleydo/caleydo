/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.serialize;

import static org.caleydo.core.manager.GeneralManager.CALEYDO_HOME_PATH;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXB;
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
import org.caleydo.core.id.IDMappingDescription;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.IDTypeInitializer;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.FileUtil;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.util.system.FileOperations;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

import com.google.common.io.Files;

/**
 * Serializes the current state of the application into a directory or file.
 *
 * @author Alexander Lex
 * @author Werner Puff
 * @author Marc Streit
 */
public final class ProjectManager {
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

	/** meta data file name se {@link ProjectMetaData} */
	private static final String METADATA_FILE = "metadata.xml";

	/** File name of file where list of user defined id mappings are to be stored */
	private static final String ID_MAPPING_FILE = "idmapping.xml";

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
			log.error("Error Loading Project from\n" + dirName, e);
			return null;
		}

	}

	private static void loadPluginData(String dirName) throws JAXBException, BundleException {
		File pluginFile = new File(dirName + ProjectManager.PLUG_IN_LIST_FILE);
		if (!pluginFile.exists()) {
			log.info("Error Loading Plugin Data  " + pluginFile);
			return;
		}

		final Unmarshaller unmarshaller = JAXBContext.newInstance(PlugInList.class).createUnmarshaller();
		PlugInList plugInList = (PlugInList) unmarshaller.unmarshal(pluginFile);

		for (String plugIn : plugInList.plugIns) {
			Bundle bundle = Platform.getBundle(plugIn);
			if (bundle == null) {
				log.warn("Error Loading Bundle : %s", plugIn);
				continue;
			}
			bundle.start();
		}
	}

	/**
	 * checks whether the unpacked project at the given location is compatible with this version using the project
	 * metadata
	 *
	 * @param unpackedProjectLocation
	 * @return true if it can be loaded
	 */
	public static boolean checkCompatibility(String dirName) {
		File metaData = new File(dirName, METADATA_FILE);
		if (!metaData.exists())
			return false;
		ProjectMetaData data = JAXB.unmarshal(metaData, ProjectMetaData.class);
		if (data == null)
			return false;
		return GeneralManager.canLoadDataCreatedFor(data.getVersion());
	}

	private static SerializationData loadData(String dirName) throws IOException, JAXBException {
		SerializationManager serializationManager = GeneralManager.get().getSerializationManager();

		Unmarshaller unmarshaller = serializationManager.getProjectContext().createUnmarshaller();

		File metaData = new File(dirName, METADATA_FILE);
		if (metaData.exists()) {
			ProjectMetaData m = (ProjectMetaData) unmarshaller.unmarshal(metaData);
			GeneralManager.get().setMetaData(m);
		}

		DataDomainList dataDomainList;

		for (ISerializationAddon addon : serializationManager.getAddons()) {
			addon.deserialize(dirName, unmarshaller);
		}

		dataDomainList = (DataDomainList) unmarshaller.unmarshal(GeneralManager.get().getResourceLoader()
				.getResource(dirName + ProjectManager.DATA_DOMAIN_FILE));

		SerializationData serializationData = new SerializationData();
		SubMonitor monitor = GeneralManager.get().createSubProgressMonitor();
		monitor.beginTask("Loading Data", dataDomainList.getDataDomains().size() * 10);

		for (ADataDomain dataDomain : dataDomainList.getDataDomains()) {
			DataSetDescription dataSetDescription = dataDomain.getDataSetDescription();

			// FIXME hack
			if (dataDomain.getDataDomainType().equals("org.caleydo.datadomain.genetic"))
				DataDomainManager.get().initalizeDataDomain("org.caleydo.datadomain.genetic");

			// Not every data domain has got a dataSetDescription
			if (dataSetDescription != null)
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

				Set<String> recordPerspectiveIDs = ((ATableBasedDataDomain) dataDomain).getRecordPerspectiveIDs();
				Set<String> dimensionPerspectiveIDs = ((ATableBasedDataDomain) dataDomain).getDimensionPerspectiveIDs();

				int nrPerspectives = recordPerspectiveIDs.size() + dimensionPerspectiveIDs.size();
				SubMonitor dataDomainMonitor = monitor.newChild(10, SubMonitor.SUPPRESS_SUBTASK);
				dataDomainMonitor.beginTask("Loading Groupings for: " + dataDomain.getLabel(), nrPerspectives);

				for (String recordPerspectiveID : recordPerspectiveIDs) {

					Perspective recordPerspective = (Perspective) unmarshaller.unmarshal(GeneralManager.get()
							.getResourceLoader().getResource(extendedDirName + recordPerspectiveID + ".xml"));
					recordPerspective.setDataDomain((ATableBasedDataDomain) dataDomain);
					recordPerspective.setIDType(((ATableBasedDataDomain) dataDomain).getRecordIDType());
					recordPerspectives.put(recordPerspectiveID, recordPerspective);

					ClusterTree tree = loadTree(extendedDirName + recordPerspectiveID + "_tree.xml",
							((ATableBasedDataDomain) dataDomain).getRecordIDType());
					if (tree != null)
						recordPerspective.setTree(tree);

					dataDomainMonitor.worked(1);
				}

				dataInitializationData.setRecordPerspectiveMap(recordPerspectives);

				HashMap<String, Perspective> dimensionPerspectives = new HashMap<String, Perspective>();

				for (String dimensionPerspectiveID : dimensionPerspectiveIDs) {

					Perspective dimensionPerspective = (Perspective) unmarshaller.unmarshal(GeneralManager.get()
							.getResourceLoader().getResource(extendedDirName + dimensionPerspectiveID + ".xml"));
					dimensionPerspective.setDataDomain((ATableBasedDataDomain) dataDomain);
					dimensionPerspective.setIDType(((ATableBasedDataDomain) dataDomain).getDimensionIDType());
					dimensionPerspectives.put(dimensionPerspectiveID, dimensionPerspective);

					ClusterTree tree = loadTree(extendedDirName + dimensionPerspectiveID + "_tree.xml",
							((ATableBasedDataDomain) dataDomain).getDimensionIDType());
					dimensionPerspective.setTree(tree);

					dataDomainMonitor.worked(1);
				}

				dataInitializationData.setDimensionPerspectiveMap(dimensionPerspectives);

				serializationData.addDataDomainSerializationData(dataInitializationData);

				dataDomainMonitor.done();

			} else {
				monitor.worked(10);
			}
		}

		loadIDMappings(dirName);

		for (ISerializationAddon addon : serializationManager.getAddons()) {
			addon.deserialize(dirName, unmarshaller, serializationData);
		}

		return serializationData;
	}

	private static void loadIDMappings(String dirName) throws JAXBException {

		SerializationManager serializationManager = GeneralManager.get().getSerializationManager();
		Unmarshaller unmarshaller = serializationManager.getProjectContext().createUnmarshaller();

		File idMapping = new File(dirName, ID_MAPPING_FILE);

		if (!idMapping.exists())
			return;
		IDMappingList idMappingList = (IDMappingList) unmarshaller.unmarshal(idMapping);
		if (idMappingList == null || idMappingList.getIDMappingDescriptions() == null)
			return;
		for (IDMappingDescription desc : idMappingList.getIDMappingDescriptions()) {

			String fileNameOnly = FileUtil.exctractFileName(desc.getFileName());
			// Update to correct file name
			desc.setFileName(dirName + fileNameOnly);

			desc.addMapping();
			IDMappingManager.addIDMappingDescription(desc);
		}
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
		return save(fileName, false);
	}

	public static IRunnableWithProgress save(String fileName, boolean onlyData) {
		return save(fileName, onlyData, DataDomainManager.get().getDataDomains(), GeneralManager.get().getMetaData()
				.cloneForSaving());
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
				log.info("Saving Project to " + fileName);
				String tempDir = dir("temp_project" + System.currentTimeMillis());
				FileOperations.createDirectory(tempDir);
				try {
					if (!onlyData) {
						monitor.subTask("Saving Workbench Data");
						saveWorkbenchData(tempDir);
						monitor.worked(1);
					}
					log.info("Storing Plugin Data");
					monitor.subTask("Saving Plugin Data");
					savePluginData(tempDir);
					monitor.worked(1);
					log.info("Stored Plugin Data");
					log.info("Storing Data");
					saveData(tempDir, dataDomains, monitor, metaData);
					log.info("Stored Data");

					log.info("Storing Mappings");
					saveIDMappings(tempDir, monitor);
					log.info("Stored Mappings");

					monitor.subTask("Packing Project Data");
					ZipUtils.zipDirectory(tempDir, fileName);
					monitor.worked(1);

					monitor.subTask("Cleanup Temporary Data");
					FileOperations.deleteDirectory(tempDir);
					monitor.worked(1);
					String message = "Successfully Wrote Project to\n" + fileName;
					log.info(message);
					monitor.done();
					showMessageBox("Project Save", message);
				} catch (Exception e) {
					String failureMessage = "Error Saving Project to " + fileName + ".";
					log.error(failureMessage, e);
					showMessageBox("Project Save", failureMessage);
				}
			}
		};

	}

	private static void saveIDMappings(final String dirName, IProgressMonitor monitor) throws JAXBException {
		List<IDMappingDescription> descriptions = IDMappingManager.getIdMappingDescriptions();
		IDMappingList mappingList = new IDMappingList();
		mappingList.setMappingDescriptions(descriptions);

		SerializationManager serializationManager = GeneralManager.get().getSerializationManager();
		JAXBContext projectContext = serializationManager.getProjectContext();

		Marshaller marshaller = projectContext.createMarshaller();

		File idMappingFile = new File(dirName + ID_MAPPING_FILE);
		marshaller.marshal(mappingList, idMappingFile);

		for (IDMappingDescription desc : descriptions) {
			String fileName = FileUtil.exctractFileName(desc.getFileName());
			monitor.subTask("Persisting ID Mapping File: " + desc.getFileName());

			String extendedDirName = dirName + fileName;

			try {
				FileOperations.writeInputStreamToFile(extendedDirName, GeneralManager.get().getResourceLoader()
						.getResource(desc.getFileName()));
			} catch (IllegalStateException e) {
				e.printStackTrace();
				throw new IllegalStateException("Error Saving Project File", e);
			}

			monitor.worked(1);
		}

		monitor.worked(1);

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
				monitor.subTask("Preparing Data");
				if (new File(RECENT_PROJECT_FOLDER).exists())
					FileOperations.renameDirectory(RECENT_PROJECT_FOLDER, RECENT_PROJECT_FOLDER_TMP);

				FileOperations.createDirectory(RECENT_PROJECT_FOLDER);
				monitor.worked(1);
				try {
					log.info("Saving Plugin Data");
					monitor.subTask("Saving Plugin Data");
					savePluginData(RECENT_PROJECT_FOLDER);
					monitor.worked(1);
					log.info("Saving Data");
					saveData(RECENT_PROJECT_FOLDER, dataDomains, monitor, GeneralManager.get().getMetaData());
					log.info("Saving Workbench");
					monitor.subTask("Packing Project Data");
					saveWorkbenchData(RECENT_PROJECT_FOLDER);
					monitor.worked(1);
					log.info("saved");
				} catch (Exception e) {
					log.error("Error Saving Auto-Save Project", e);
				}
				monitor.subTask("Cleanup temporary data");
				FileOperations.deleteDirectory(RECENT_PROJECT_FOLDER_TMP);
				log.info("Saved Recent Project");
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
			log.error("Error Serializing Plugin-Ins: " + plugInList.toString(), ex);
			throw ex;
		}

	}

	private static void saveData(String dirName, Collection<? extends IDataDomain> toSave, IProgressMonitor monitor,
			ProjectMetaData metaData) throws JAXBException, IOException {

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
				monitor.worked(1);
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
					throw new IllegalStateException("Error Saving Project File", e);
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
			monitor.worked(1);
		}

		DataDomainList dataDomainList = new DataDomainList();
		dataDomainList.setDataDomains(dataDomains);

		marshaller.marshal(dataDomainList, dataDomainFile);

		File metaDataFile = new File(dirName, METADATA_FILE);
		marshaller.marshal(metaData, metaDataFile);

		monitor.worked(1);

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
	private static void saveWorkbenchData(String dirName) {
		log.info("Storing Workbench Data");

		IJobManager jobManager = Job.getJobManager();
		// find auto saver
		Job[] build = jobManager.find(Workbench.WORKBENCH_AUTO_SAVE_JOB);

		if (build.length == 1) {
			Job j = build[0];
			j.wakeUp(); // call it now
			try {
				j.join(); // wait for it
				Job[] jobs = jobManager.find(null); // find the resource saver and wait for it, too
				for (Job job : jobs)
					if ("Workbench Auto-Save Background Job".equals(job.getName()))
						job.join();

				// clear old workbench file
				File source = new File(WORKBENCH_XMI_FILE);
				if (!source.exists()) {
					log.warn("Error Saving Workbench Data from " + source);
					return;
				}
				File target = new File(dirName + WORKBENCH_XMI);
				Files.copy(source, target);
			} catch (InterruptedException | IOException e) {
				log.error("Error Saving Workbench Data", e);
			}

		}
	}

	public static void loadWorkbenchData(String dirName) {
		try {
			// clear old workbench file
			File target = new File(WORKBENCH_XMI_FILE);
			FileOperations.deleteDirectory(target);

			File workbenchFile = new File(dirName + WORKBENCH_XMI);
			if (!workbenchFile.exists()) {
				log.info("Error Loading Workbench Data  from " + workbenchFile);
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
			log.warn("Error Loading Workbench Data  from " + dirName, e);
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
