package org.caleydo.core.manager.datadomain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.EStorageType;
import org.caleydo.core.data.collection.table.ContentData;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.filter.ContentFilterManager;
import org.caleydo.core.data.filter.StorageFilterManager;
import org.caleydo.core.data.graph.tree.ClusterTree;
import org.caleydo.core.data.graph.tree.ESortingStrategy;
import org.caleydo.core.data.id.IDCategory;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.mapping.IDMappingManager;
import org.caleydo.core.data.selection.ContentSelectionManager;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.StorageSelectionManager;
import org.caleydo.core.data.selection.delta.DeltaConverter;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.virtualarray.ADimensionGroupData;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.SetBasedDimensionGroupData;
import org.caleydo.core.data.virtualarray.StorageVirtualArray;
import org.caleydo.core.data.virtualarray.delta.ContentVADelta;
import org.caleydo.core.data.virtualarray.delta.StorageVADelta;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.similarity.RelationAnalyzer;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.EventPublisher;
import org.caleydo.core.manager.event.data.DimensionGroupsChangedEvent;
import org.caleydo.core.manager.event.data.ReplaceContentVAEvent;
import org.caleydo.core.manager.event.data.ReplaceContentVAInUseCaseEvent;
import org.caleydo.core.manager.event.data.ReplaceStorageVAEvent;
import org.caleydo.core.manager.event.data.ReplaceStorageVAInUseCaseEvent;
import org.caleydo.core.manager.event.data.StartClusteringEvent;
import org.caleydo.core.manager.event.view.SelectionCommandEvent;
import org.caleydo.core.manager.event.view.storagebased.ContentVAUpdateEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.event.view.storagebased.StorageVAUpdateEvent;
import org.caleydo.core.util.clusterer.ClusterHelper;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.util.clusterer.ClusterState;
import org.caleydo.core.util.clusterer.EClustererType;
import org.caleydo.core.view.opengl.canvas.listener.ContentVAUpdateListener;
import org.caleydo.core.view.opengl.canvas.listener.ForeignSelectionCommandListener;
import org.caleydo.core.view.opengl.canvas.listener.ForeignSelectionUpdateListener;
import org.caleydo.core.view.opengl.canvas.listener.IContentVAUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.IStorageVAUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.SelectionCommandListener;
import org.caleydo.core.view.opengl.canvas.listener.SelectionUpdateListener;
import org.caleydo.core.view.opengl.canvas.listener.StorageVAUpdateListener;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AItemContainer;

@XmlType
@XmlRootElement
public abstract class ATableBasedDataDomain
	extends ADataDomain
	implements IContentVAUpdateHandler, IStorageVAUpdateHandler, ISelectionUpdateHandler,
	ISelectionCommandHandler {

	private SelectionUpdateListener selectionUpdateListener;
	private SelectionCommandListener selectionCommandListener;
	private StartClusteringListener startClusteringListener;
	private ReplaceContentVAInUseCaseListener replaceContentVirtualArrayInUseCaseListener;
	private ReplaceStorageVAInUseCaseListener replaceStorageVirtualArrayInUseCaseListener;
	private ContentVAUpdateListener contentVAUpdateListener;
	private StorageVAUpdateListener storageVAUpdateListener;
	private AggregateGroupListener aggregateGroupListener;

	protected List<ADimensionGroupData> dimensionGroups;

	/** The set which is currently loaded and used inside the views for this use case. */
	protected DataTable table;

	protected IDType humanReadableContentIDType;
	protected IDType humanReadableStorageIDType;

	protected IDType primaryContentMappingType;

	protected IDCategory contentIDCategory;
	protected IDCategory storageIDCategory;

	protected IDType contentIDType;
	protected IDType storageIDType;

	/** IDType used for {@link Group}s in this dataDomain */
	protected IDType contentGroupIDType;

	protected ContentSelectionManager contentSelectionManager;
	protected StorageSelectionManager storageSelectionManager;
	protected SelectionManager contentGroupSelectionManager;

	/** central {@link EventPublisher} to receive and send events */
	protected EventPublisher eventPublisher = GeneralManager.get().getEventPublisher();

	protected ContentFilterManager contentFilterManager;
	protected StorageFilterManager storageFilterManager;

	// private RelationAnalyzer contentRelationAnalyzer;

	@XmlTransient
	private HashMap<Integer, DataTable> otherMetaSets = new HashMap<Integer, DataTable>();

	/**
	 * DO NOT CALL THIS CONSTRUCTOR! ONLY USED FOR DESERIALIZATION.
	 */
	public ATableBasedDataDomain() {
		super();
		init();
	}

	public ATableBasedDataDomain(String dataDomainType, String dataDomainID) {
		super(dataDomainType, dataDomainID);
		init();
	}

	private void init() {

		dimensionGroups = new ArrayList<ADimensionGroupData>();

		assignIDCategories();
		if (contentIDCategory == null || storageIDCategory == null) {
			throw new IllegalStateException("A ID category in " + toString()
				+ " was null, contentIDCategory: " + contentIDCategory + ", storageIDCategory: "
				+ storageIDCategory);
		}
		contentIDType =
			IDType.registerType("content_" + dataDomainID + "_" + hashCode(), contentIDCategory,
				EStorageType.INT);
		storageIDType =
			IDType.registerType("storage_" + dataDomainID + "_" + hashCode(), storageIDCategory,
				EStorageType.INT);

		contentGroupIDType =
			IDType.registerType("group_content_" + dataDomainID + "_" + hashCode(), contentIDCategory,
				EStorageType.INT);
	}

	/**
	 * Assign {@link #contentIDCategory} and {@link #storageIDCategory} in the concrete implementing classes.
	 * ID Categories should typically be already existing through the data mapping. Assign the correct types
	 * using {@link IDCategory#getIDCategory(String)}.
	 */
	protected abstract void assignIDCategories();

	/**
	 * Sets the set which is currently loaded and used inside the views for this use case.
	 * 
	 * @param set
	 *            The new set which replaced the currently loaded one.
	 */
	public void setSet(DataTable set) {
		assert (set != null);

		// set.setDataDomain(this);

		DataTable oldSet = this.table;
		this.table = set;
		if (oldSet != null) {
			oldSet.destroy();
			oldSet = null;
		}

	}

	public void addMetaSet(DataTable set) {
		otherMetaSets.put(set.getID(), set);
	}

	/**
	 * Returns the root set which is currently loaded and used inside the views for this use case.
	 * 
	 * @return a data set
	 */
	@XmlTransient
	public DataTable getDataTable() {
		return table;
	}

	public DataTable getSet(int setID) {
		if (table.getID() == setID)
			return table;

		ClusterNode root = table.getStorageData(DataTable.STORAGE).getStorageTreeRoot();
		DataTable set = root.getMetaSetFromSubTree(setID);

		if (set == null)
			set = otherMetaSets.get(setID);
		return set;

	}

	public void registerMetaSet() {

	}

	public IDType getContentIDType() {
		return contentIDType;
	}

	public IDType getStorageIDType() {
		return storageIDType;
	}

	/**
	 * Returns the ID type used for {@link Group}s in this dataDomain.
	 * 
	 * @return
	 */
	public IDType getContentGroupIDType() {
		return contentGroupIDType;
	}

	public IDCategory getContentIDCategory() {
		return contentIDCategory;
	}

	public IDCategory getStorageIDCategory() {
		return storageIDCategory;
	}

	/**
	 * Update the data set in the view of this use case.
	 */
	public void updateSetInViews() {

		initFullVA();
		initSelectionManagers();


		contentFilterManager = new ContentFilterManager(this);
		storageFilterManager = new StorageFilterManager(this);

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
		contentSelectionManager.setVA(table.getContentData(DataTable.CONTENT).getContentVA());
		storageSelectionManager = new StorageSelectionManager(storageIDType);
		storageSelectionManager.setVA(table.getStorageData(DataTable.STORAGE).getStorageVA());
		contentGroupSelectionManager = new SelectionManager(contentGroupIDType);
	}

	/**
	 * Returns a clone of the content selection manager. You have to set your virtual array manually. This is
	 * the preferred way to initialize SelectionManagers.
	 * 
	 * @return a clone of the content selection manager
	 */
	public ContentSelectionManager getContentSelectionManager() {
		return contentSelectionManager.clone();
	}

	/**
	 * Returns a clone of the storage selection manager. You have to set your virtual array manually. This is
	 * the preferred way to initialize SelectionManagers.
	 * 
	 * @return a clone of the storage selection manager
	 */
	public StorageSelectionManager getStorageSelectionManager() {
		return storageSelectionManager.clone();
	}

	public SelectionManager getContentGroupSelectionManager() {
		return contentGroupSelectionManager.clone();
	}

	/**
	 * Returns the virtual array for the type
	 * 
	 * @param vaType
	 *            the type of VA requested
	 * @return
	 */
	public ContentVirtualArray getContentVA(String vaType) {
		ContentVirtualArray va = table.getContentData(vaType).getContentVA();
		ContentVirtualArray vaCopy = va.clone();
		return vaCopy;
	}

	/**
	 * Returns the virtual array for the type
	 * 
	 * @param vaType
	 *            the type of VA requested
	 * @return
	 */
	public StorageVirtualArray getStorageVA(String vaType) {
		StorageVirtualArray va = table.getStorageData(vaType).getStorageVA();
		StorageVirtualArray vaCopy = va.clone();
		return vaCopy;
	}

	/**
	 * Initiates clustering based on the parameters passed. Sends out an event to all affected views upon
	 * positive completion to replace their VA.
	 * 
	 * @param setID
	 *            ID of the set to cluster
	 * @param clusterState
	 */
	public void startClustering(int setID, ClusterState clusterState) {

		DataTable set = null;
		if (this.table.getID() == setID)
			set = this.table;
		else
			set = this.table.getStorageData(DataTable.STORAGE).getStorageTreeRoot().getMetaSetFromSubTree(setID);

		// TODO: warning
		if (set == null)
			return;

		set.cluster(clusterState);

		ContentData contentData = set.getContentData(DataTable.CONTENT);
		ClusterHelper.calculateAggregatedUncertainties(contentData.getContentTree(), set);
		ClusterHelper.calculateClusterAverages(contentData.getContentTree(),
			EClustererType.CONTENT_CLUSTERING, set);
		contentData.getContentTree().setSortingStrategy(ESortingStrategy.CERTAINTY);
		contentData.updateVABasedOnSortingStrategy();

		// This should be done to avoid problems with group info in HHM

		eventPublisher.triggerEvent(new ReplaceContentVAEvent(set, dataDomainID, clusterState
			.getContentVAType()));
		eventPublisher.triggerEvent(new ReplaceStorageVAEvent(set, dataDomainID, DataTable.STORAGE));

		if (clusterState.getClustererType() == EClustererType.STORAGE_CLUSTERING
			|| clusterState.getClustererType() == EClustererType.BI_CLUSTERING) {
			((DataTable) set).createMetaSets();
		}
	}

	/**
	 * Resets the context VA to it's initial state
	 */
	public void resetContextVA() {
		table.setContentVA(DataTable.CONTENT_CONTEXT, new ContentVirtualArray(DataTable.CONTENT_CONTEXT));
	}

	/**
	 * This is the method which is used to synchronize the views with the Virtual Array, which is initiated
	 * from this class. Therefore it should not be called any time!
	 */
	@Override
	public void replaceContentVA(int setID, String dataDomainType, String vaType) {
		throw new IllegalStateException("UseCases shouldn't react to this");

	}

	/**
	 * Replace content VA for the default set.
	 */

	public void replaceContentVA(String dataDomainType, String vaType, ContentVirtualArray virtualArray) {

		replaceContentVA(table.getID(), dataDomainType, vaType, virtualArray);

		// Tree<ClusterNode> storageTree = set.getStorageData(Set.STORAGE).getStorageTree();
		// if (storageTree == null)
		// return;
		// else {
		// // TODO check whether we need this for the meat sets, it fires a lot of unnecessar events in other
		// // cases
		// // for (DataTable tmpSet : storageTree.getRoot().getAllMetaSetsFromSubTree()) {
		// // tmpSet.setContentVA(vaType, virtualArray.clone());
		// // eventPublisher.triggerEvent(new ReplaceContentVAEvent(tmpSet, dataDomainType, vaType));
		// // }
		// }
	}

	/**
	 * Replace content VA for a specific set.
	 * 
	 * @param setID
	 * @param dataDomainType
	 * @param vaType
	 * @param virtualArray
	 */
	public void replaceContentVA(int setID, String dataDomainType, String vaType,
		ContentVirtualArray virtualArray) {

		if (dataDomainType != this.dataDomainID) {
			handleForeignContentVAUpdate(setID, dataDomainType, vaType, virtualArray);
			return;
		}
		DataTable set;
		if (setID == this.table.getID()) {
			set = this.table;
		}
		else {
			set = this.table.getStorageData(DataTable.STORAGE).getStorageTreeRoot().getMetaSetFromSubTree(setID);
		}
		if (set == null)
			set = otherMetaSets.get(setID);

		set.setContentVA(vaType, virtualArray.clone());
		contentSelectionManager.setVA(set.getContentData(DataTable.CONTENT).getContentVA());

		virtualArray.setGroupList(null);
		eventPublisher.triggerEvent(new ReplaceContentVAEvent(set, dataDomainType, vaType));
	}

	/**
	 * Replaces the storage virtual array with the virtual array specified, if the dataDomain matches. If the
	 * dataDomain doesn't match, the method
	 * {@link #handleForeignContentVAUpdate(int, String, ContentVAType, ContentVirtualArray)} is called.
	 * 
	 * @param idCategory
	 *            the type of id
	 * @param the
	 *            type of the virtual array
	 * @param virtualArray
	 *            the new virtual array
	 */
	public void replaceStorageVA(String dataDomainType, String vaType) {
		throw new IllegalStateException("UseCases shouldn't react to this");
	}

	public void replaceStorageVA(String dataDomainType, String vaType, StorageVirtualArray virtualArray) {

		table.setStorageVA(vaType, virtualArray);
		storageSelectionManager.setVA(virtualArray);

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
		ReplaceStorageVAEvent event = new ReplaceStorageVAEvent(table, dataDomainType, vaType);
		event.setSender(this);
		eventPublisher.triggerEvent(event);

	}

	public void setContentVirtualArray(String vaType, ContentVirtualArray virtualArray) {
		table.setContentVA(vaType, virtualArray);
	}

	public void setStorageVirtualArray(String vaType, StorageVirtualArray virtualArray) {
		table.setStorageVA(vaType, virtualArray);
	}

	protected void initFullVA() {
		if (table.getContentData(DataTable.CONTENT) == null)
			table.restoreOriginalContentVA();
	}

	/**
	 * Restore the original data. All applied filters are undone.
	 */
	public void restoreOriginalContentVA() {
		initFullVA();

		ReplaceContentVAEvent event = new ReplaceContentVAEvent(table, dataDomainID, DataTable.CONTENT);

		event.setSender(this);
		eventPublisher.triggerEvent(event);
	}

	@Override
	public void handleVAUpdate(ContentVADelta vaDelta, String info) {
		IDCategory targetCategory = vaDelta.getIDType().getIDCategory();
		if (targetCategory != contentIDCategory)
			return;

		if (targetCategory == contentIDCategory && vaDelta.getIDType() != contentIDType)
			vaDelta = DeltaConverter.convertDelta(contentIDType, vaDelta);
		ContentData contentData = table.getContentData(vaDelta.getVAType());
		contentData.reset();
		contentData.setVADelta(vaDelta);

	}

	@Override
	public void handleVAUpdate(StorageVADelta vaDelta, String info) {
		IDCategory targetCategory = vaDelta.getIDType().getIDCategory();
		if (targetCategory != storageIDCategory)
			return;

		StorageVirtualArray va = table.getStorageData(vaDelta.getVAType()).getStorageVA();

		va.setDelta(vaDelta);
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
		selectionUpdateListener.setExclusiveDataDomainType(dataDomainID);
		eventPublisher.addListener(SelectionUpdateEvent.class, selectionUpdateListener);

		selectionCommandListener = new SelectionCommandListener();
		selectionCommandListener.setHandler(this);
		selectionCommandListener.setDataDomainType(dataDomainID);
		eventPublisher.addListener(SelectionCommandEvent.class, selectionCommandListener);

		startClusteringListener = new StartClusteringListener();
		startClusteringListener.setHandler(this);
		startClusteringListener.setDataDomainType(dataDomainID);
		eventPublisher.addListener(StartClusteringEvent.class, startClusteringListener);

		replaceContentVirtualArrayInUseCaseListener = new ReplaceContentVAInUseCaseListener();
		replaceContentVirtualArrayInUseCaseListener.setHandler(this);
		replaceContentVirtualArrayInUseCaseListener.setDataDomainType(dataDomainID);
		eventPublisher.addListener(ReplaceContentVAInUseCaseEvent.class,
			replaceContentVirtualArrayInUseCaseListener);

		replaceStorageVirtualArrayInUseCaseListener = new ReplaceStorageVAInUseCaseListener();
		replaceStorageVirtualArrayInUseCaseListener.setHandler(this);
		replaceStorageVirtualArrayInUseCaseListener.setDataDomainType(dataDomainID);
		eventPublisher.addListener(ReplaceStorageVAInUseCaseEvent.class,
			replaceStorageVirtualArrayInUseCaseListener);

		contentVAUpdateListener = new ContentVAUpdateListener();
		contentVAUpdateListener.setHandler(this);
		contentVAUpdateListener.setDataDomainType(dataDomainID);
		eventPublisher.addListener(ContentVAUpdateEvent.class, contentVAUpdateListener);

		storageVAUpdateListener = new StorageVAUpdateListener();
		storageVAUpdateListener.setHandler(this);
		storageVAUpdateListener.setDataDomainType(dataDomainID);
		eventPublisher.addListener(StorageVAUpdateEvent.class, storageVAUpdateListener);

		aggregateGroupListener = new AggregateGroupListener();
		aggregateGroupListener.setHandler(this);
		eventPublisher.addListener(AggregateGroupEvent.class, aggregateGroupListener);
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

		if (contentVAUpdateListener != null) {
			eventPublisher.removeListener(contentVAUpdateListener);
			contentVAUpdateListener = null;
		}

		if (storageVAUpdateListener != null) {
			eventPublisher.removeListener(storageVAUpdateListener);
			storageVAUpdateListener = null;
		}

		if (aggregateGroupListener != null) {
			eventPublisher.removeListener(aggregateGroupListener);
			aggregateGroupListener = null;
		}
	}

	/**
	 * Returns the label for the content. E.g. gene for genome use case, entity for generic use case
	 * 
	 * @param bUpperCase
	 *            TRUE makes the label upper case
	 * @param bPlural
	 *            TRUE label = plural, FALSE label = singular
	 * @return label valid for the specific use case
	 */
	public String getContentName(boolean bCapitalized, boolean bPlural) {

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

		IDMappingManager mappingManager = GeneralManager.get().getIDMappingManager();
		if (mappingManager.hasMapping(selectionDelta.getIDType(), contentSelectionManager.getIDType())) {
			contentSelectionManager.setDelta(selectionDelta);
		}
		else if (mappingManager.hasMapping(selectionDelta.getIDType(), storageSelectionManager.getIDType())) {
			storageSelectionManager.setDelta(selectionDelta);
		}

		if (selectionDelta.getIDType() == contentGroupSelectionManager.getIDType()) {
			contentGroupSelectionManager.setDelta(selectionDelta);
		}
	}

	@Override
	public void handleSelectionCommand(IDCategory idCategory, SelectionCommand selectionCommand) {
		// TODO Auto-generated method stub

	}

	/**
	 * This method is called by the {@link ForeignSelectionUpdateListener}, signaling that a selection form
	 * another dataDomain is available. If possible, it is converted to be compatible with the local
	 * dataDomain and then sent out via a {@link SelectionUpdateEvent}.
	 * 
	 * @param dataDomainType
	 *            the type of the dataDomain for which this selectionUpdate is intended
	 * @param delta
	 * @param scrollToSelection
	 * @param info
	 */
	public void handleForeignSelectionUpdate(String dataDomainType, ISelectionDelta delta,
		boolean scrollToSelection, String info) {
		// may be interesting to implement in sub-class

	}

	/**
	 * Interface used by {@link ForeignSelectionCommandListener} to signal foreign selection commands. Can be
	 * implemented in concrete classes, has no functionality in base class.
	 */
	public void handleForeignSelectionCommand(String dataDomainType, IDCategory idCategory,
		SelectionCommand selectionCommand) {
		// may be interesting to implement in sub-class
	}

	/**
	 * This method is called if a content VA Update was requested, but the dataDomainType specified was not
	 * this dataDomains type. Concrete handling can only be done in concrete dataDomains.
	 * 
	 * @param setID
	 * @param dataDomainType
	 * @param vaType
	 * @param virtualArray
	 */
	public abstract void handleForeignContentVAUpdate(int setID, String dataDomainType, String vaType,
		ContentVirtualArray virtualArray);

	/**
	 * Returns the id type that should be used if an entity of this data domain should be printed human
	 * readable
	 * 
	 * @return
	 */
	public IDType getHumanReadableContentIDType() {
		return humanReadableContentIDType;
	}

	/**
	 * Get the human readable content label for a specific id. The id has to be of the contentIDType of the
	 * dataDomain.
	 * 
	 * @param id
	 *            the id to convert to a human readable label
	 * @return the readable label
	 */
	public String getContentLabel(Object id) {
		return getContentLabel(contentIDType, id);
	}

	public abstract String getContentLabel(IDType idType, Object id);

	/**
	 * Get the human readable storage label for a specific id. The id has to be of the storageIDType of the
	 * dataDomain.
	 * 
	 * @param id
	 *            the id to convert to a human readable label
	 * @return the readable label
	 */
	public String getStorageLabel(Object id) {
		return getStorageLabel(storageIDType, id);
	}

	/**
	 * Get the human readable storage label for a specific id.
	 * 
	 * @param idType
	 *            specify of which id type the id is
	 * @param id
	 *            the id to convert to a human readable label
	 * @return the readable label
	 */
	public String getStorageLabel(IDType idType, Object id) {
		String label = table.get((Integer) id).getLabel();
		if (label == null)
			label = "";
		return label;
	}

	/**
	 * A dataDomain may contribute to the context menu. This function returns the contentItemContainer of the
	 * context menu if one was specified. This should be overridden by subclasses if needed.
	 * 
	 * @return a context menu item container related to content items
	 */
	public AItemContainer getContentItemContainer(IDType idType, int id) {
		return null;
	}

	/**
	 * A dataDomain may contribute to the context menu. This function returns dataDomain specific
	 * implementations of a context menu for content groups. * @param idType
	 * 
	 * @param ids
	 * @return
	 */
	public AItemContainer getContentGroupItemContainer(IDType idType, ArrayList<Integer> ids) {
		return null;
	}

	/**
	 * Returns the primary mapping type of the content. This type is not determined at run-time but something
	 * permanent like an official gene mapping type like david.
	 * 
	 * @return
	 */
	public IDType getPrimaryContentMappingType() {
		return primaryContentMappingType;
	}

	public IDType getPrimaryStorageMappingType() {
		return storageIDType;
	}

	/**
	 * Filter manager holds all filter applied in the content dimension.
	 * 
	 * @return
	 */
	public ContentFilterManager getContentFilterManager() {
		return contentFilterManager;
	}

	/**
	 * Filter manager holds all filter applied in the storage dimension.
	 * 
	 * @return
	 */
	public StorageFilterManager getStorageFilterManager() {
		return storageFilterManager;
	}

	/**
	 * Create a new {@link RelationAnalyzer} for contentVAs of this DataDomain. The contentRelationAnalyzer
	 * runs in a separate thread and listens to {@link ReplaceContentVAEvent}s to do its business.
	 */
	// public void createContentRelationAnalyzer() {
	// if (contentRelationAnalyzer != null)
	// return;
	// contentRelationAnalyzer = new RelationAnalyzer(this);
	//
	// Thread thread = new Thread(contentRelationAnalyzer, "Relation Analyzer");
	// thread.start();
	// }

	/**
	 * Returns the {@link RelationAnalyzer} of this dataDomain, or null if it has not been created (via
	 * {@link #createContentRelationAnalyzer()}).
	 * 
	 * @return
	 */

	// public RelationAnalyzer getContentRelationAnalyzer() {
	// return contentRelationAnalyzer;
	// }

	public void createDimensionGroupsFromStorageTree(ClusterTree tree) {
		dimensionGroups.clear();
		if (tree == null)
			return;
		ClusterNode rootNode = tree.getRoot();
		if (rootNode != null && rootNode.hasChildren())
			createDimensionGroupsFromStorageTree(rootNode);
	}

	private void createDimensionGroupsFromStorageTree(ClusterNode parent) {

		for (ClusterNode child : parent.getChildren()) {
			if (child.hasChildren()) {
				dimensionGroups.add(new SetBasedDimensionGroupData(this, child.getMetaSet()));
				createDimensionGroupsFromStorageTree(child);
			}
		}
		
		DimensionGroupsChangedEvent event = new DimensionGroupsChangedEvent(this);
		event.setSender(this);
		GeneralManager.get().getEventPublisher().triggerEvent(event);
	}

	@Override
	public List<ADimensionGroupData> getDimensionGroups() {
		return dimensionGroups;
	}

	@Override
	public void setDimensionGroups(List<ADimensionGroupData> dimensionGroups) {
		this.dimensionGroups = dimensionGroups;
		DimensionGroupsChangedEvent event = new DimensionGroupsChangedEvent(this);
		event.setSender(this);
		GeneralManager.get().getEventPublisher().triggerEvent(event);
	}

	@Override
	public void addDimensionGroup(ADimensionGroupData dimensionGroup) {
		dimensionGroups.add(dimensionGroup);
		DimensionGroupsChangedEvent event = new DimensionGroupsChangedEvent(this);
		event.setSender(this);
		GeneralManager.get().getEventPublisher().triggerEvent(event);
	}

	public void aggregateGroups(java.util.Set<Integer> groups) {
		System.out.println("Received command to aggregate experiments, not implemented yet");

	}
}
