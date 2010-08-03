package org.caleydo.core.manager.datadomain;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.set.Set;
import org.caleydo.core.data.graph.tree.Tree;
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
import org.caleydo.core.manager.ISetBasedDataDomain;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.data.ReplaceContentVAEvent;
import org.caleydo.core.manager.event.data.ReplaceContentVAInUseCaseEvent;
import org.caleydo.core.manager.event.data.ReplaceStorageVAEvent;
import org.caleydo.core.manager.event.data.ReplaceStorageVAInUseCaseEvent;
import org.caleydo.core.manager.event.data.StartClusteringEvent;
import org.caleydo.core.manager.event.view.NewSetEvent;
import org.caleydo.core.manager.event.view.SelectionCommandEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.event.view.storagebased.VirtualArrayUpdateEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.util.clusterer.ClusterState;
import org.caleydo.core.util.clusterer.EClustererType;
import org.caleydo.core.view.opengl.canvas.listener.ContentVAUpdateListener;
import org.caleydo.core.view.opengl.canvas.listener.ForeignSelectionCommandListener;
import org.caleydo.core.view.opengl.canvas.listener.ForeignSelectionUpdateListener;
import org.caleydo.core.view.opengl.canvas.listener.SelectionCommandListener;
import org.caleydo.core.view.opengl.canvas.listener.SelectionUpdateListener;

@XmlType
@XmlRootElement
public abstract class ASetBasedDataDomain
	extends ADataDomain
	implements ISetBasedDataDomain {

	protected SelectionUpdateListener selectionUpdateListener;
	protected SelectionCommandListener selectionCommandListener;
	private StartClusteringListener startClusteringListener;
	private ReplaceContentVAInUseCaseListener replaceContentVirtualArrayInUseCaseListener;
	private ReplaceStorageVAInUseCaseListener replaceStorageVirtualArrayInUseCaseListener;
	private ContentVAUpdateListener virtualArrayUpdateListener;;

	/** The set which is currently loaded and used inside the views for this use case. */
	protected ISet set;

	protected EIDType contentIDType;
	protected EIDType storageIDType;

	protected ContentSelectionManager contentSelectionManager;
	protected StorageSelectionManager storageSelectionManager;

	/** central {@link IEventPublisher} to receive and send events */
	protected IEventPublisher eventPublisher;

	public ASetBasedDataDomain() {
		eventPublisher = GeneralManager.get().getEventPublisher();
		registerEventListeners();
	}

	public ASetBasedDataDomain(String dataDomainType) {
		this.dataDomainType = dataDomainType;
		eventPublisher = GeneralManager.get().getEventPublisher();
		registerEventListeners();
	}

	@Override
	public void setSet(ISet set) {
		assert (set != null);

		set.setDataDomain(this);

		ISet oldSet = this.set;
		this.set = set;
		if (oldSet != null) {
			oldSet.destroy();
			oldSet = null;
		}
	}

	@XmlTransient
	@Override
	public ISet getSet() {
		return set;
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

		// TODO check
		// oldSet.destroy();
		// oldSet = null;
		// When new data is set, the bucket will be cleared because the internal heatmap and parcoords cannot
		// be updated in the context mode.
		// if (glRemoteRenderingView != null)
		// glRemoteRenderingView.clearAll();
	}

	protected void initSelectionManagers() {
		contentSelectionManager = new ContentSelectionManager(contentIDType);
		storageSelectionManager = new StorageSelectionManager(storageIDType);
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
	public ContentVirtualArray getContentVA(ContentVAType vaType) {
		ContentVirtualArray va = set.getContentData(vaType).getContentVA();
		ContentVirtualArray vaCopy = va.clone();
		return vaCopy;
	}

	@Override
	public StorageVirtualArray getStorageVA(StorageVAType vaType) {
		StorageVirtualArray va = set.getStorageData(vaType).getStorageVA();
		StorageVirtualArray vaCopy = va.clone();
		return vaCopy;
	}

	@Override
	public void startClustering(int setID, ClusterState clusterState) {

		ISet set = null;
		if (this.set.getID() == setID)
			set = this.set;
		else
			set =
				this.set.getStorageData(StorageVAType.STORAGE).getStorageTreeRoot()
					.getMetaSetFromSubTree(setID);

		// TODO: warning
		if (set == null)
			return;

		set.cluster(clusterState);

		// This should be done to avoid problems with group info in HHM

		eventPublisher.triggerEvent(new ReplaceContentVAEvent(set, dataDomainType, clusterState
			.getContentVAType()));
		eventPublisher.triggerEvent(new ReplaceStorageVAEvent(set, dataDomainType, StorageVAType.STORAGE));

		if (clusterState.getClustererType() == EClustererType.EXPERIMENTS_CLUSTERING
			|| clusterState.getClustererType() == EClustererType.BI_CLUSTERING) {
			((Set) set).createMetaSets();
		}
	}

	@Override
	public void resetContextVA() {

		set.setContentVA(ContentVAType.CONTENT_CONTEXT,
			new ContentVirtualArray(ContentVAType.CONTENT_CONTEXT));
	}

	/**
	 * This is the method which is used to synchronize the views with the Virtual Array, which is initiated
	 * from this class. Therefore it should not be called any time!
	 */
	@Override
	public void replaceContentVA(int setID, String dataDomainType, ContentVAType vaType) {
		throw new IllegalStateException("UseCases shouldn't react to this");

	}

	/**
	 * Replace content VA for the default set.
	 */
	@Override
	public void replaceContentVA(String dataDomainType, ContentVAType vaType, ContentVirtualArray virtualArray) {

		replaceContentVA(set.getID(), dataDomainType, vaType, virtualArray);

		Tree<ClusterNode> storageTree = set.getStorageData(StorageVAType.STORAGE).getStorageTree();
		if (storageTree == null)
			return;
		else {

			for (ISet tmpSet : storageTree.getRoot().getAllMetaSetsFromSubTree()) {
				tmpSet.setContentVA(vaType, virtualArray.clone());
				eventPublisher.triggerEvent(new ReplaceContentVAEvent(tmpSet, dataDomainType, vaType));
			}
		}
	}

	/**
	 * Replace content VA for a specific set.
	 * 
	 * @param setID
	 * @param dataDomainType
	 * @param vaType
	 * @param virtualArray
	 */
	public void replaceContentVA(int setID, String dataDomainType, ContentVAType vaType,
		ContentVirtualArray virtualArray) {

		if (dataDomainType != this.dataDomainType) {
			handleForeignContentVAUpdate(setID, dataDomainType, vaType, virtualArray);
			return;
		}
		ISet set;
		if (setID == this.set.getID()) {
			set = this.set;
		}
		else {
			set =
				this.set.getStorageData(StorageVAType.STORAGE).getStorageTreeRoot()
					.getMetaSetFromSubTree(setID);
		}

		set.setContentVA(vaType, virtualArray.clone());

		virtualArray.setGroupList(null);

		eventPublisher.triggerEvent(new ReplaceContentVAEvent(set, dataDomainType, vaType));
	}

	public void replaceStorageVA(String dataDomainType, StorageVAType vaType) {
		throw new IllegalStateException("UseCases shouldn't react to this");
	}

	@Override
	public void replaceStorageVA(String dataDomainType, StorageVAType vaType, StorageVirtualArray virtualArray) {

		set.setStorageVA(vaType, virtualArray);

		// if (set.getStorageData(StorageVAType.STORAGE).getStorageTree() != null) {
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
		// }
		eventPublisher.triggerEvent(new ReplaceStorageVAEvent(set, dataDomainType, vaType));

	}

	@Override
	public void setContentVirtualArray(ContentVAType vaType, ContentVirtualArray virtualArray) {
		set.setContentVA(vaType, virtualArray);
	}

	@Override
	public void setStorageVirtualArray(StorageVAType vaType, StorageVirtualArray virtualArray) {
		set.setStorageVA(vaType, virtualArray);
	}

	protected void initFullVA() {
		if (set.getContentData(ContentVAType.CONTENT) == null)
			set.restoreOriginalContentVA();
	}

	@Override
	public void restoreOriginalContentVA() {
		initFullVA();

		ReplaceContentVAEvent event = new ReplaceContentVAEvent(set, dataDomainType, ContentVAType.CONTENT);

		event.setSender(this);
		eventPublisher.triggerEvent(event);
	}

	@Override
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
		selectionUpdateListener.setExclusiveDataDomainType(dataDomainType);
		eventPublisher.addListener(SelectionUpdateEvent.class, selectionUpdateListener);

		selectionCommandListener = new SelectionCommandListener();
		selectionCommandListener.setHandler(this);
		selectionCommandListener.setDataDomainType(dataDomainType);
		eventPublisher.addListener(SelectionCommandEvent.class, selectionCommandListener);

		startClusteringListener = new StartClusteringListener();
		startClusteringListener.setHandler(this);
		startClusteringListener.setDataDomainType(dataDomainType);
		eventPublisher.addListener(StartClusteringEvent.class, startClusteringListener);

		replaceContentVirtualArrayInUseCaseListener = new ReplaceContentVAInUseCaseListener();
		replaceContentVirtualArrayInUseCaseListener.setHandler(this);
		replaceContentVirtualArrayInUseCaseListener.setDataDomainType(dataDomainType);
		eventPublisher.addListener(ReplaceContentVAInUseCaseEvent.class,
			replaceContentVirtualArrayInUseCaseListener);

		replaceStorageVirtualArrayInUseCaseListener = new ReplaceStorageVAInUseCaseListener();
		replaceStorageVirtualArrayInUseCaseListener.setHandler(this);
		replaceStorageVirtualArrayInUseCaseListener.setDataDomainType(dataDomainType);
		eventPublisher.addListener(ReplaceStorageVAInUseCaseEvent.class,
			replaceStorageVirtualArrayInUseCaseListener);

		virtualArrayUpdateListener = new ContentVAUpdateListener();
		virtualArrayUpdateListener.setHandler(this);
		virtualArrayUpdateListener.setDataDomainType(dataDomainType);
		eventPublisher.addListener(VirtualArrayUpdateEvent.class, virtualArrayUpdateListener);
	}

	// TODO this is never called!
	@Override
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

		if (replaceContentVirtualArrayInUseCaseListener != null) {
			eventPublisher.removeListener(replaceContentVirtualArrayInUseCaseListener);
			replaceContentVirtualArrayInUseCaseListener = null;
		}

		if (replaceStorageVirtualArrayInUseCaseListener != null) {
			eventPublisher.removeListener(replaceStorageVirtualArrayInUseCaseListener);
			replaceStorageVirtualArrayInUseCaseListener = null;
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
	public String getVATypeForIDCategory(EIDCategory idCategory) {
		return possibleIDCategories.get(idCategory);
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

	@Override
	public void handleSelectionUpdate(ISelectionDelta selectionDelta, boolean scrollToSelection, String info) {

		if (contentSelectionManager == null)
			return;

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

	/**
	 * Interface used by {@link ForeignSelectionUpdateListener} to signal foreign selection updates. Can be
	 * implemented in concrete classes, has no functionality in base class.
	 */
	@Override
	public void handleForeignSelectionUpdate(String dataDomainType, ISelectionDelta delta,
		boolean scrollToSelection, String info) {
		// may be interesting to implement in sub-class

	}

	/**
	 * Interface used by {@link ForeignSelectionCommandListener} to signal foreign selection commands. Can be
	 * implemented in concrete classes, has no functionality in base class.
	 */
	@Override
	public void handleForeignSelectionCommand(String dataDomainType, EIDCategory category,
		SelectionCommand selectionCommand) {
		// may be interesting to implement in sub-class
	}

}
