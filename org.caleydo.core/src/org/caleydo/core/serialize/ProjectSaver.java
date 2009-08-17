package org.caleydo.core.serialize;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.caleydo.core.data.collection.set.LoadDataParameters;
import org.caleydo.core.data.collection.set.SetUtils;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.graph.tree.TreePorter;
import org.caleydo.core.data.selection.VirtualArray;
import org.caleydo.core.manager.IUseCase;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.usecase.AUseCase;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.storagebased.EVAType;

/**
 * Serializes the current state of the application into a directory or file.
 * 
 * @author Alexander Lex
 * @author Werner Puff
 */
public class ProjectSaver {

	/** full path to directory to temporarily store the projects file before zipping */
	public static final String TEMP_PROJECT_DIR_NAME =
		GeneralManager.CALEYDO_HOME_PATH + "tempSave" + File.separator;

	/** full path to directory of the recently open project */
	public static final String RECENT_PROJECT_DIR_NAME =
		GeneralManager.CALEYDO_HOME_PATH + "recent_project" + File.separator;

	/** file name of the set-data-file in project-folders */
	public static final String SET_DATA_FILE_NAME = "data.csv";

	/** file name of the usecase-file in project-folders */
	public static final String USECASE_FILE_NAME = "usecase.xml";

	/** file name of the view-file in project-folders */
	public static final String VIEWS_FILE_NAME = "views.xml";

	/** file name of the gene-cluster-file in project-folders */
	public static final String GENE_TREE_FILE_NAME = "gene_cluster.xml";

	/** file name of the experiment-cluster-file in project-folders */
	public static final String EXP_TREE_FILE_NAME = "experiment_cluster.xml";

	/**
	 * Saves the project into a specified zip-archive.
	 * 
	 * @param fileName
	 *            name of the file to save the project in.
	 */
	public void save(String fileName) {
		ZipUtils zipUtils = new ZipUtils();
		saveProjectData(TEMP_PROJECT_DIR_NAME);
		saveViewData(TEMP_PROJECT_DIR_NAME);
		zipUtils.zipDirectory(TEMP_PROJECT_DIR_NAME, fileName);
		zipUtils.deleteDirectory(TEMP_PROJECT_DIR_NAME);
	}

	/**
	 * Saves the project to the directory for the recent project
	 */
	public void saveRecentProject() {
		ZipUtils zipUtils = new ZipUtils();
		if (!GeneralManager.get().getUseCase().getLoadDataParameters().getFileName().startsWith(
			RECENT_PROJECT_DIR_NAME)) {
			zipUtils.deleteDirectory(RECENT_PROJECT_DIR_NAME);
		}
		saveProjectData(RECENT_PROJECT_DIR_NAME);
	}

	/**
	 * Saves the project to the directory with the given name. The directory is created before saving.
	 * 
	 * @param dirName
	 *            directory to save the project-files into
	 */
	private void saveProjectData(String dirName) {
		if (dirName.charAt(dirName.length() - 1) != File.separatorChar) {
			dirName += File.separator;
		}

		File tempDirFile = new File(dirName);
		tempDirFile.mkdir();

		AUseCase useCase = (AUseCase) GeneralManager.get().getUseCase();
		LoadDataParameters parameters = useCase.getLoadDataParameters();
		byte[] data = SetUtils.loadSetFile(parameters);

		String setFileName = dirName + SET_DATA_FILE_NAME;
		File setFile = new File(setFileName);
		SetUtils.saveFile(data, setFile);

		SerializationManager serializationManager = GeneralManager.get().getSerializationManager();
		JAXBContext projectContext = serializationManager.getProjectContext();

		try {
			Marshaller marshaller = projectContext.createMarshaller();

			saveVirtualArray(marshaller, dirName, useCase, EVAType.CONTENT);
			saveVirtualArray(marshaller, dirName, useCase, EVAType.CONTENT_CONTEXT);
			saveVirtualArray(marshaller, dirName, useCase, EVAType.CONTENT_EMBEDDED_HM);
			saveVirtualArray(marshaller, dirName, useCase, EVAType.STORAGE);

			TreePorter treePorter = new TreePorter();
			Tree<ClusterNode> geneTree = useCase.getSet().getClusteredTreeGenes();
			if (geneTree != null) {
				treePorter.exportTree(dirName + GENE_TREE_FILE_NAME, geneTree);
			}

			treePorter = new TreePorter();
			Tree<ClusterNode> expTree = useCase.getSet().getClusteredTreeExps();
			if (expTree != null) {
				treePorter.exportTree(dirName + EXP_TREE_FILE_NAME, expTree);
			}

			File useCaseFile = new File(dirName + USECASE_FILE_NAME);
			marshaller.marshal(useCase, useCaseFile);
		} catch (JAXBException ex) {
			throw new RuntimeException("Error saving project files (xml serialization)", ex);
		} catch (IOException ex) {
			throw new RuntimeException("Error saving project files (file access)", ex);
		}
	}

	/**
	 * Saves all the view's serialized forms to the given directory. The directory must exist.
	 * @param dirName name of the directory to save the views to. 
	 */
	private void saveViewData(String dirName) {
		SerializationManager serializationManager = GeneralManager.get().getSerializationManager();
		JAXBContext projectContext = serializationManager.getProjectContext();

		try {
			Marshaller marshaller = projectContext.createMarshaller();
			ViewList storeViews = createStoreViewList();
			File viewFile = new File(dirName + VIEWS_FILE_NAME);
			marshaller.marshal(storeViews, viewFile);
		} catch (JAXBException ex) {
			throw new RuntimeException("Error saving view files (xml serialization)", ex);
		}
	}
	
	/**
	 * Creates a {@link ViewList} of all views registered in the central {@link IViewManager}.
	 * 
	 * @return {@link ViewList} to storing the view's state.
	 */
	private ViewList createStoreViewList() {
		ArrayList<ASerializedView> storeViews = new ArrayList<ASerializedView>();

		IViewManager viewManager = GeneralManager.get().getViewGLCanvasManager();

		Collection<AGLEventListener> glViews = viewManager.getAllGLEventListeners();
		for (AGLEventListener glView : glViews) {
			if (!glView.isRenderedRemote()) {
				ASerializedView serView = glView.getSerializableRepresentation();
				if (!(serView instanceof SerializedDummyView)) {
					storeViews.add(serView);
				}
			}
		}

		Collection<IView> swtViews = viewManager.getAllItems();
		for (IView swtView : swtViews) {
			ASerializedView serView = swtView.getSerializableRepresentation();
			if (!(serView instanceof SerializedDummyView)) {
				storeViews.add(serView);
			}
		}

		ViewList viewList = new ViewList();
		viewList.setViews(storeViews);

		return viewList;
	}

	/**
	 * Saves the {@link VirtualArray} of the given type. The filename is created from the type.
	 * 
	 * @param dir
	 *            directory to save the {@link VirtualArray} in.
	 * @param useCase
	 *            {@link IUseCase} to retrieve the {@link VirtualArray} from.
	 * @param type
	 *            type of the virtual array within the given {@link IUseCase}.
	 */
	private void saveVirtualArray(Marshaller marshaller, String dir, IUseCase useCase, EVAType type)
		throws JAXBException {
		String fileName = dir + "va_" + type.toString() + ".xml";
		VirtualArray va = (VirtualArray) useCase.getVA(type);
		marshaller.marshal(va, new File(fileName));
	}

}

// String geneTreePath = tempDirectory + "/bgene_tree.xml";

// ISet set = GeneralManager.get().getUseCase().getSet();

// SetExporter exporter = new SetExporter();
// exporter.export(set, exportedData, EWhichViewToExport.WHOLE_DATA);
//
// exporter.exportTrees(set, tempDirectory);
