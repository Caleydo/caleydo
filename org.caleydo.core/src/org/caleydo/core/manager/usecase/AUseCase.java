package org.caleydo.core.manager.usecase;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.set.LoadDataParameters;
import org.caleydo.core.data.collection.set.Set;
import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.ContentSelectionManager;
import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.StorageSelectionManager;
import org.caleydo.core.data.selection.StorageVAType;
import org.caleydo.core.data.selection.StorageVirtualArray;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.IIDMappingManager;
import org.caleydo.core.manager.IUseCase;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.data.ReplaceContentVAEvent;
import org.caleydo.core.manager.event.data.ReplaceContentVAInUseCaseEvent;
import org.caleydo.core.manager.event.data.ReplaceStorageVAEvent;
import org.caleydo.core.manager.event.data.StartClusteringEvent;
import org.caleydo.core.manager.event.view.NewSetEvent;
import org.caleydo.core.manager.event.view.SelectionCommandEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.event.view.storagebased.VirtualArrayUpdateEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.specialized.clinical.ClinicalUseCase;
import org.caleydo.core.manager.specialized.genetic.GeneticUseCase;
import org.caleydo.core.util.clusterer.ClusterState;
import org.caleydo.core.util.clusterer.EClustererType;
import org.caleydo.core.view.opengl.canvas.listener.ContentVAUpdateListener;
import org.caleydo.core.view.opengl.canvas.listener.IContentVAUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.IStorageVAUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.SelectionCommandListener;
import org.caleydo.core.view.opengl.canvas.listener.SelectionUpdateListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * Abstract use case class that implements data and view management.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
@XmlType
@XmlRootElement
@XmlSeeAlso( { GeneticUseCase.class, ClinicalUseCase.class, UnspecifiedUseCase.class })
public abstract class AUseCase
	implements IContentVAUpdateHandler, IStorageVAUpdateHandler, ISelectionUpdateHandler,
	ISelectionCommandHandler, IUseCase, IListenerOwner {

	protected String contentLabelSingular = "<not specified>";
	protected String contentLabelPlural = "<not specified>";

	protected EDataFilterLevel dataFilterLevel = EDataFilterLevel.ONLY_CONTEXT;

	/**
	 * This mode determines whether the user can load and work with gene expression data or otherwise if an
	 * not further specified data set is loaded. In the case of the unspecified data set some specialized gene
	 * expression features are not available.
	 */
	protected EDataDomain useCaseMode = EDataDomain.UNSPECIFIED;

	/** The set which is currently loaded and used inside the views for this use case. */
	protected ISet set;

	/** parameters for loading the the data-{@link set} */
	protected LoadDataParameters loadDataParameters;

	/** bootstrap filename this application was started with */
	protected String bootsTrapFileName;

	/** central {@link IEventPublisher} to receive and send events */
	private IEventPublisher eventPublisher;

	protected SelectionUpdateListener selectionUpdateListener;
	protected SelectionCommandListener selectionCommandListener;
	private StartClusteringListener startClusteringListener;
	private ReplaceContentVAInUseCaseListener replaceVirtualArrayInUseCaseListener;
	private ContentVAUpdateListener virtualArrayUpdateListener;

	/** Every use case needs to state all views that can visualize its data */
	protected ArrayList<String> possibleViews;

	/**
	 * Every use case needs to state all ID Categories it can handle. The string must specify which primary
	 * VAType ({@link VAType#getPrimaryVAType()} is associated for the ID Category
	 */
	protected HashMap<EIDCategory, String> possibleIDCategories;

	protected EIDType contentIDType;
	protected EIDType storageIDType;

	protected ContentSelectionManager contentSelectionManager;
	protected StorageSelectionManager storageSelectionManager;

	public AUseCase() {
		eventPublisher = GeneralManager.get().getEventPublisher();
		registerEventListeners();
	}

	@Override
	public EDataDomain getDataDomain() {
		return useCaseMode;
	}

	@Override
	public String getVATypeForIDCategory(EIDCategory idCategory) {
		return possibleIDCategories.get(idCategory);
	}

	// public void setUseCaseMode(EDataDomain useCaseMode) {
	// this.useCaseMode = useCaseMode;
	// }

	@Override
	public ArrayList<String> getPossibleViews() {
		return possibleViews;
	}

	@XmlTransient
	@Override
	public ISet getSet() {
		return set;
	}

	@Override
	public void setSet(ISet set) {
		assert (set != null);

		if ((set.getSetType() == ESetType.GENE_EXPRESSION_DATA && useCaseMode == EDataDomain.GENETIC_DATA)
			|| (set.getSetType() == ESetType.CLINICAL_DATA && useCaseMode == EDataDomain.CLINICAL_DATA)
			|| (set.getSetType() == ESetType.UNSPECIFIED && useCaseMode == EDataDomain.UNSPECIFIED)
			|| (set.getSetType() == ESetType.GENE_EXPRESSION_DATA && useCaseMode == EDataDomain.PATHWAY_DATA)) {

			ISet oldSet = this.set;
			this.set = set;
			if (oldSet != null) {
				oldSet.destroy();
				oldSet = null;
			}

		}
		else {
			throw new IllegalStateException("The Set " + set + " specified is not suited for the use case "
				+ this);
		}

	}

	@Override
	public void updateSetInViews() {

		initFullVA();
		initSelectionManagers();
		NewSetEvent newSetEvent = new NewSetEvent();
		newSetEvent.setSet((Set) set);
		GeneralManager.get().getEventPublisher().triggerEvent(newSetEvent);

		// GLRemoteRendering glRemoteRenderingView = null;
		//
		// // Update set in the views
		// for (IView view : alView) {
		// view.setSet(set);
		//			
		//
		// if (view instanceof GLRemoteRendering) {
		// glRemoteRenderingView = (GLRemoteRendering) view;
		// }
		// }

		// TODO check
		// oldSet.destroy();
		// oldSet = null;
		// When new data is set, the bucket will be cleared because the internal heatmap and parcoords cannot
		// be updated in the context mode.
		// if (glRemoteRenderingView != null)
		// glRemoteRenderingView.clearAll();
	}

	@Override
	public String getContentLabel(boolean bCapitalized, boolean bPlural) {

		String sContentLabel = "";

		if (bPlural)
			sContentLabel = contentLabelPlural;
		else
			sContentLabel = contentLabelSingular;

		if (bCapitalized) {

			// Make first char capitalized
			sContentLabel =
				sContentLabel.substring(0, 1).toUpperCase()
					+ sContentLabel.substring(1, sContentLabel.length());
		}

		return sContentLabel;
	}

	protected void initSelectionManagers() {
		contentSelectionManager = new ContentSelectionManager(contentIDType);
		storageSelectionManager = new StorageSelectionManager(storageIDType);
	}

	@Override
	public ContentVirtualArray getContentVA(ContentVAType vaType) {
		ContentVirtualArray va = set.getContentVA(vaType);
		ContentVirtualArray vaCopy = va.clone();
		return vaCopy;
	}

	@Override
	public StorageVirtualArray getStorageVA(StorageVAType vaType) {
		StorageVirtualArray va = set.getStorageVA(vaType);
		StorageVirtualArray vaCopy = va.clone();
		return vaCopy;
	}

	@Override
	public void startClustering(int setID, ClusterState clusterState) {

		ISet set = null;
		if (this.set.getID() == setID)
			set = this.set;
		else
			set = this.set.getStorageTreeRoot().getMetaSetFromSubTree(setID);

		// TODO: warning
		if (set == null)
			return;

		set.cluster(clusterState);

		// This should be done to avoid problems with group info in HHM
		set.setGeneClusterInfoFlag(false);
		set.setExperimentClusterInfoFlag(false);

		eventPublisher.triggerEvent(new ReplaceContentVAEvent(set, EIDCategory.GENE, clusterState
			.getContentVAType()));
		eventPublisher.triggerEvent(new ReplaceStorageVAEvent(set, EIDCategory.EXPERIMENT,
			StorageVAType.STORAGE));

		if (clusterState.getClustererType() == EClustererType.EXPERIMENTS_CLUSTERING
			|| clusterState.getClustererType() == EClustererType.BI_CLUSTERING) {
			((Set) set).createMetaSets();
		}
	}

	/**
	 * This is the method which is used to synchronize the views with the Virtual Array, which is initiated
	 * from this class. Therefore it should not be called any time!
	 */
	@Override
	public void replaceContentVA(int setID, EIDCategory idCategory, ContentVAType vaType) {
		throw new IllegalStateException("UseCases shouldn't react to this");

	}

	public void replaceContentVA(int setID, EIDCategory idCategory, ContentVAType vaType,
		ContentVirtualArray virtualArray) {
		// String idCategoryAsscoatedVAType = possibleIDCategories.get(idCategory);
		// if (idCategoryAsscoatedVAType == null)
		// return;

		// if (!idCategoryAsscoatedVAType.equals(vaType.getPrimaryVAType()))
		// vaType = VAType.getVATypeForPrimaryVAType(idCategoryAsscoatedVAType);

		ISet set;
		if (setID == this.set.getID()) {
			set = this.set;
		}
		else {
			set = this.set.getStorageTree().getRoot().getMetaSetFromSubTree(setID);
		}

		set.setContentVA(vaType, virtualArray.clone());

		if (set.getContentTree() != null) {
			// GeneralManager.get().getGUIBridge().getDisplay().asyncExec(new Runnable() {
			// public void run() {
			// Shell shell = new Shell();
			// MessageBox messageBox = new MessageBox(shell, SWT.CANCEL);
			// messageBox.setText("Warning");
			// messageBox
			// .setMessage("Modifications break tree structure, therefore dendrogram will be closed!");
			// messageBox.open();
			// }
			// });

			set.setContentTree(null);

		}

		virtualArray.setGroupList(null);

		eventPublisher.triggerEvent(new ReplaceContentVAEvent(set, idCategory, vaType));
	}

	public void replaceStorageVA(EIDCategory idCategory, StorageVAType vaType) {
		throw new IllegalStateException("UseCases shouldn't react to this");
	}

	public void replaceContentVA(EIDCategory idCategory, ContentVAType vaType,
		ContentVirtualArray virtualArray) {

		replaceContentVA(set.getID(), idCategory, vaType, virtualArray);

		for (ISet tmpSet : set.getStorageTree().getRoot().getAllMetaSetsFromSubTree()) {
			tmpSet.setContentVA(vaType, virtualArray.clone());
			eventPublisher.triggerEvent(new ReplaceContentVAEvent(tmpSet, idCategory, vaType));
		}
	}

	public void replaceStorageVA(EIDCategory idCategory, StorageVAType vaType,
		StorageVirtualArray virtualArray) {

		set.setStorageVA(vaType, virtualArray);
		set.setStorageTree(null);

		if (set.getStorageTree() != null) {
			GeneralManager.get().getGUIBridge().getDisplay().asyncExec(new Runnable() {
				public void run() {
					Shell shell = new Shell();
					MessageBox messageBox = new MessageBox(shell, SWT.CANCEL);
					messageBox.setText("Warning");
					messageBox
						.setMessage("Modifications break tree structure, therefore dendrogram will be closed!");
					messageBox.open();
				}
			});

			set.setStorageTree(null);
		}

	}

	public void setContentVirtualArray(ContentVAType vaType, ContentVirtualArray virtualArray) {
		set.setContentVA(vaType, virtualArray);
	}

	public void setStorageVirtualArray(StorageVAType vaType, StorageVirtualArray virtualArray) {
		set.setStorageVA(vaType, virtualArray);
	}

	public void registerEventListeners() {

		// groupMergingActionListener = new GroupMergingActionListener();
		// groupMergingActionListener.setHandler(this);
		// eventPublisher.addListener(MergeGroupsEvent.class, groupMergingActionListener);
		//
		// groupInterChangingActionListener = new GroupInterChangingActionListener();
		// groupInterChangingActionListener.setHandler(this);
		// eventPublisher.addListener(InterchangeGroupsEvent.class, groupInterChangingActionListener);

		selectionUpdateListener = new SelectionUpdateListener();
		selectionUpdateListener.setHandler(this);
		eventPublisher.addListener(SelectionUpdateEvent.class, selectionUpdateListener);

		selectionCommandListener = new SelectionCommandListener();
		selectionCommandListener.setHandler(this);
		eventPublisher.addListener(SelectionCommandEvent.class, selectionCommandListener);

		startClusteringListener = new StartClusteringListener();
		startClusteringListener.setHandler(this);
		eventPublisher.addListener(StartClusteringEvent.class, startClusteringListener);

		replaceVirtualArrayInUseCaseListener = new ReplaceContentVAInUseCaseListener();
		replaceVirtualArrayInUseCaseListener.setHandler(this);
		eventPublisher
			.addListener(ReplaceContentVAInUseCaseEvent.class, replaceVirtualArrayInUseCaseListener);

		virtualArrayUpdateListener = new ContentVAUpdateListener();
		virtualArrayUpdateListener.setHandler(this);
		eventPublisher.addListener(VirtualArrayUpdateEvent.class, virtualArrayUpdateListener);
	}

	// TODO this is never called!
	public void unregisterEventListeners() {

		if (selectionUpdateListener != null) {
			eventPublisher.removeListener(selectionUpdateListener);
			selectionUpdateListener = null;
		}

		if (selectionCommandListener != null) {
			eventPublisher.removeListener(selectionCommandListener);
			selectionCommandListener = null;
		}

		if (startClusteringListener != null) {
			eventPublisher.removeListener(startClusteringListener);
			startClusteringListener = null;
		}

		if (replaceVirtualArrayInUseCaseListener != null) {
			eventPublisher.removeListener(replaceVirtualArrayInUseCaseListener);
			replaceVirtualArrayInUseCaseListener = null;
		}

		if (virtualArrayUpdateListener != null) {
			eventPublisher.removeListener(virtualArrayUpdateListener);
			virtualArrayUpdateListener = null;
		}
	}

	@Override
	public synchronized void queueEvent(AEventListener<? extends IListenerOwner> listener, AEvent event) {

		// FIXME: concurrency issues?
		listener.handleEvent(event);

	}

	@Override
	public void resetContextVA() {

		set.setContentVA(ContentVAType.CONTENT_CONTEXT,
			new ContentVirtualArray(ContentVAType.CONTENT_CONTEXT));
	}

	public String getContentLabelSingular() {
		return contentLabelSingular;
	}

	public void setContentLabelSingular(String contentLabelSingular) {
		this.contentLabelSingular = contentLabelSingular;
	}

	public String getContentLabelPlural() {
		return contentLabelPlural;
	}

	public void setContentLabelPlural(String contentLabelPlural) {
		this.contentLabelPlural = contentLabelPlural;
	}

	public EDataFilterLevel getDataFilterLevel() {
		return dataFilterLevel;
	}

	public void setDataFilterLevel(EDataFilterLevel dataFilterLevel) {
		this.dataFilterLevel = dataFilterLevel;
	}

	@Override
	public LoadDataParameters getLoadDataParameters() {
		return loadDataParameters;
	}

	@Override
	public void setLoadDataParameters(LoadDataParameters loadDataParameters) {
		this.loadDataParameters = loadDataParameters;
	}

	public String getBootstrapFileName() {
		return bootsTrapFileName;
	}

	public void setBootstrapFileName(String bootsTrapFileName) {
		this.bootsTrapFileName = bootsTrapFileName;
	}

	@Override
	public ContentSelectionManager getContentSelectionManager() {
		return contentSelectionManager.clone();
	}

	@Override
	public StorageSelectionManager getStorageSelectionManager() {
		return storageSelectionManager.clone();
	}

	@Override
	public void handleSelectionUpdate(ISelectionDelta selectionDelta, boolean scrollToSelection, String info) {
		IIDMappingManager mappingManager = GeneralManager.get().getIDMappingManager();
		if (mappingManager.hasMapping(selectionDelta.getIDType(), contentSelectionManager.getIDType())) {
			contentSelectionManager.setDelta(selectionDelta);
		}
		else if (mappingManager.hasMapping(selectionDelta.getIDType(), storageSelectionManager.getIDType())) {
			storageSelectionManager.setDelta(selectionDelta);
		}
	}

	@Override
	public void handleSelectionCommand(EIDCategory category, SelectionCommand selectionCommand) {
		// TODO Auto-generated method stub

	}

	protected void initFullVA() {
		set.restoreOriginalContentVA();
	}

	@Override
	public void restoreOriginalContentVA() {
		initFullVA();

		ReplaceContentVAEvent event =
			new ReplaceContentVAEvent(set, contentSelectionManager.getIDType().getCategory(),
				ContentVAType.CONTENT);

		event.setSender(this);
		eventPublisher.triggerEvent(event);
	}

}
