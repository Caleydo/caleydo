package org.caleydo.core.manager.datadomain;

import java.util.HashMap;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.DimensionType;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.collection.table.RecordData;
import org.caleydo.core.data.filter.DimensionFilterManager;
import org.caleydo.core.data.filter.RecordFilterManager;
import org.caleydo.core.data.graph.tree.ClusterTree;
import org.caleydo.core.data.graph.tree.ESortingStrategy;
import org.caleydo.core.data.id.IDCategory;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.mapping.IDMappingManager;
import org.caleydo.core.data.selection.DimensionSelectionManager;
import org.caleydo.core.data.selection.RecordSelectionManager;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.delta.DeltaConverter;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.TableBasedDimensionGroupData;
import org.caleydo.core.data.virtualarray.delta.DimensionVADelta;
import org.caleydo.core.data.virtualarray.delta.RecordVADelta;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.similarity.RelationAnalyzer;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.EventPublisher;
import org.caleydo.core.manager.event.data.DimensionGroupsChangedEvent;
import org.caleydo.core.manager.event.data.ReplaceDimensionVAEvent;
import org.caleydo.core.manager.event.data.ReplaceDimensionVAInUseCaseEvent;
import org.caleydo.core.manager.event.data.ReplaceRecordVAEvent;
import org.caleydo.core.manager.event.data.ReplaceRecordVAInUseCaseEvent;
import org.caleydo.core.manager.event.data.StartClusteringEvent;
import org.caleydo.core.manager.event.view.SelectionCommandEvent;
import org.caleydo.core.manager.event.view.tablebased.DimensionVAUpdateEvent;
import org.caleydo.core.manager.event.view.tablebased.RecordVAUpdateEvent;
import org.caleydo.core.manager.event.view.tablebased.SelectionUpdateEvent;
import org.caleydo.core.util.clusterer.ClusterHelper;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.util.clusterer.ClusterState;
import org.caleydo.core.util.clusterer.ClustererType;
import org.caleydo.core.view.opengl.canvas.listener.DimensionVAUpdateListener;
import org.caleydo.core.view.opengl.canvas.listener.ForeignSelectionCommandListener;
import org.caleydo.core.view.opengl.canvas.listener.ForeignSelectionUpdateListener;
import org.caleydo.core.view.opengl.canvas.listener.IDimensionVAUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.IRecordVAUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.RecordVAUpdateListener;
import org.caleydo.core.view.opengl.canvas.listener.SelectionCommandListener;
import org.caleydo.core.view.opengl.canvas.listener.SelectionUpdateListener;

@XmlType
@XmlRootElement
public abstract class ATableBasedDataDomain
	extends ADataDomain
	implements IRecordVAUpdateHandler, IDimensionVAUpdateHandler, ISelectionUpdateHandler,
	ISelectionCommandHandler {

	private SelectionUpdateListener selectionUpdateListener;
	private SelectionCommandListener selectionCommandListener;
	private StartClusteringListener startClusteringListener;
	private ReplaceRecordVAInUseCaseListener replaceRecordVirtualArrayInUseCaseListener;
	private ReplaceDimensionVAInUseCaseListener replaceDimensionVirtualArrayInUseCaseListener;
	private RecordVAUpdateListener recordVAUpdateListener;
	private DimensionVAUpdateListener dimensionVAUpdateListener;
	private AggregateGroupListener aggregateGroupListener;

	/** The set which is currently loaded and used inside the views for this use case. */
	protected DataTable table;

	protected IDType humanReadableRecordIDType;
	protected IDType humanReadableDimensionIDType;

	protected IDType primaryRecordMappingType;

	protected IDCategory recordIDCategory;
	protected IDCategory dimensionIDCategory;

	protected IDType recordIDType;
	protected IDType dimensionIDType;

	/** IDType used for {@link Group}s in this dataDomain */
	protected IDType recordGroupIDType;

	protected RecordSelectionManager recordSelectionManager;
	protected DimensionSelectionManager dimensionSelectionManager;
	protected SelectionManager recordGroupSelectionManager;

	/** central {@link EventPublisher} to receive and send events */
	protected EventPublisher eventPublisher = GeneralManager.get().getEventPublisher();

	protected RecordFilterManager recordFilterManager;
	protected DimensionFilterManager dimensionFilterManager;

	// private RelationAnalyzer contentRelationAnalyzer;

	@XmlTransient
	private HashMap<Integer, DataTable> otherSubDataTables = new HashMap<Integer, DataTable>();

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

		assignIDCategories();
		if (recordIDCategory == null || dimensionIDCategory == null) {
			throw new IllegalStateException("A ID category in " + toString()
				+ " was null, recordIDCategory: " + recordIDCategory + ", dimensionIDCategory: "
				+ dimensionIDCategory);
		}
		recordIDType =
			IDType.registerType("record_" + dataDomainID + "_" + hashCode(), recordIDCategory,
				DimensionType.INT);
		dimensionIDType =
			IDType.registerType("dimension_" + dataDomainID + "_" + hashCode(), dimensionIDCategory,
				DimensionType.INT);

		recordGroupIDType =
			IDType.registerType("group_record_" + dataDomainID + "_" + hashCode(), recordIDCategory,
				DimensionType.INT);
	}

	/**
	 * Assign {@link #recordIDCategory} and {@link #dimensionIDCategory} in the concrete implementing classes.
	 * ID Categories should typically be already existing through the data mapping. Assign the correct types
	 * using {@link IDCategory#getIDCategory(String)}.
	 */
	protected abstract void assignIDCategories();

	/**
	 * Sets the set which is currently loaded and used inside the views for this use case.
	 * 
	 * @param table
	 *            The new set which replaced the currently loaded one.
	 */
	public void setTable(DataTable table) {
		assert (table != null);

		// table.setDataDomain(this);

		DataTable oldSet = this.table;
		this.table = table;
		if (oldSet != null) {
			oldSet.destroy();
			oldSet = null;
		}
	}

	public void addSubDataTable(DataTable table) {
		otherSubDataTables.put(table.getID(), table);
	}

	/**
	 * Returns the root set which is currently loaded and used inside the views for this use case.
	 * 
	 * @return a data set
	 */
	@XmlTransient
	public DataTable getTable() {
		return table;
	}

	public DataTable getTable(int tableID) {
		if (table.getID() == tableID)
			return table;

		ClusterNode root = table.getDimensionData(DataTable.DIMENSION).getDimensionTreeRoot();
		DataTable set = root.getSubDataTableFromSubTree(tableID);

		if (set == null)
			set = otherSubDataTables.get(tableID);
		return set;

	}

	public void registerSubDataTable() {

	}

	public IDType getRecordIDType() {
		return recordIDType;
	}

	public IDType getDimensionIDType() {
		return dimensionIDType;
	}

	/**
	 * Returns the ID type used for {@link Group}s in this dataDomain.
	 * 
	 * @return
	 */
	public IDType getRecordGroupIDType() {
		return recordGroupIDType;
	}

	public IDCategory getRecordIDCategory() {
		return recordIDCategory;
	}

	public IDCategory getDimensionIDCategory() {
		return dimensionIDCategory;
	}

	/**
	 * Update the data set in the view of this use case.
	 */
	public void updateSetInViews() {

		initFullVA();
		initSelectionManagers();

		recordFilterManager = new RecordFilterManager(this);
		dimensionFilterManager = new DimensionFilterManager(this);

		// GLRemoteRendering glRemoteRenderingView = null;
		//
		// // Update set in the views
		// for (IView view : alView) {
		// view.setTable(set);
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
		recordSelectionManager = new RecordSelectionManager(recordIDType);
		recordSelectionManager.setVA(table.getRecordData(DataTable.RECORD).getRecordVA());
		dimensionSelectionManager = new DimensionSelectionManager(dimensionIDType);
		dimensionSelectionManager.setVA(table.getDimensionData(DataTable.DIMENSION).getDimensionVA());
		recordGroupSelectionManager = new SelectionManager(recordGroupIDType);
	}

	/**
	 * Returns a clone of the record selection manager. You have to set your virtual array manually. This is
	 * the preferred way to initialize SelectionManagers.
	 * 
	 * @return a clone of the record selection manager
	 */
	public RecordSelectionManager getRecordSelectionManager() {
		return recordSelectionManager.clone();
	}

	/**
	 * Returns a clone of the dimension selection manager. You have to set your virtual array manually. This
	 * is the preferred way to initialize SelectionManagers.
	 * 
	 * @return a clone of the dimension selection manager
	 */
	public DimensionSelectionManager getDimensionSelectionManager() {
		return dimensionSelectionManager.clone();
	}

	public SelectionManager getRecordGroupSelectionManager() {
		return recordGroupSelectionManager.clone();
	}

	/**
	 * Returns the virtual array for the type
	 * 
	 * @param vaType
	 *            the type of VA requested
	 * @return
	 */
	public RecordVirtualArray getRecordVA(String vaType) {
		RecordVirtualArray va = table.getRecordData(vaType).getRecordVA();
		RecordVirtualArray vaCopy = va.clone();
		return vaCopy;
	}

	/**
	 * Returns the virtual array for the type
	 * 
	 * @param vaType
	 *            the type of VA requested
	 * @return
	 */
	public DimensionVirtualArray getDimensionVA(String vaType) {
		DimensionVirtualArray va = table.getDimensionData(vaType).getDimensionVA();
		DimensionVirtualArray vaCopy = va.clone();
		return vaCopy;
	}

	/**
	 * Initiates clustering based on the parameters passed. Sends out an event to all affected views upon
	 * positive completion to replace their VA.
	 * 
	 * @param tableID
	 *            ID of the set to cluster
	 * @param clusterState
	 */
	public void startClustering(int tableID, ClusterState clusterState) {

		DataTable table = null;
		if (this.table.getID() == tableID)
			table = this.table;
		else
			table =
				this.table.getDimensionData(DataTable.DIMENSION).getDimensionTreeRoot()
					.getSubDataTableFromSubTree(tableID);

		// TODO: warning
		if (table == null)
			return;

		table.cluster(clusterState);

		RecordData recordData = table.getRecordData(DataTable.RECORD);
		if (table.containsUncertaintyData()) {
			ClusterHelper.calculateAggregatedUncertainties(recordData.getRecordTree(), table);
			ClusterHelper.calculateClusterAverages(recordData.getRecordTree(),
				ClustererType.RECORD_CLUSTERING, table);
			recordData.getRecordTree().setSortingStrategy(ESortingStrategy.CERTAINTY);
			recordData.updateVABasedOnSortingStrategy();
		}

		// This should be done to avoid problems with group info in HHM

		eventPublisher.triggerEvent(new ReplaceRecordVAEvent(table, dataDomainID, clusterState
			.getRecordVAType()));
		eventPublisher.triggerEvent(new ReplaceDimensionVAEvent(table, dataDomainID, DataTable.DIMENSION));

		if (clusterState.getClustererType() == ClustererType.DIMENSION_CLUSTERING
			|| clusterState.getClustererType() == ClustererType.BI_CLUSTERING) {
			((DataTable) table).createSubDataTable();
		}
	}

	/**
	 * Resets the context VA to it's initial state
	 */
	public void resetContextVA() {
		table.setRecordVA(DataTable.RECORD_CONTEXT, new RecordVirtualArray(DataTable.RECORD_CONTEXT));
	}

	/**
	 * This is the method which is used to synchronize the views with the Virtual Array, which is initiated
	 * from this class. Therefore it should not be called any time!
	 */
	@Override
	public void replaceRecordVA(int tableID, String dataDomainType, String vaType) {
		throw new IllegalStateException("UseCases shouldn't react to this");

	}

	/**
	 * Replace content VA for the default table.
	 */

	public void replaceRecordVA(String dataDomainType, String vaType, RecordVirtualArray virtualArray) {

		replaceRecordVA(table.getID(), dataDomainType, vaType, virtualArray);

		// Tree<ClusterNode> dimensionTree = table.getDimensionData(Set.STORAGE).getDimensionTree();
		// if (dimensionTree == null)
		// return;
		// else {
		// // TODO check whether we need this for the meat sets, it fires a lot of unnecessar events in other
		// // cases
		// // for (DataTable tmpSet : dimensionTree.getRoot().getAllSubDataTablesFromSubTree()) {
		// // tmpSet.setRecordVA(vaType, virtualArray.clone());
		// // eventPublisher.triggerEvent(new ReplaceRecordVAEvent(tmpSet, dataDomainType, vaType));
		// // }
		// }
	}

	/**
	 * Replace record VA for a specific table.
	 * 
	 * @param tableID
	 * @param dataDomainType
	 * @param vaType
	 * @param virtualArray
	 */
	public void replaceRecordVA(int tableID, String dataDomainType, String vaType,
		RecordVirtualArray virtualArray) {

		if (dataDomainType != this.dataDomainID) {
			handleForeignRecordVAUpdate(tableID, dataDomainType, vaType, virtualArray);
			return;
		}
		DataTable table;
		if (tableID == this.table.getID()) {
			table = this.table;
		}
		else {
			table =
				this.table.getDimensionData(DataTable.DIMENSION).getDimensionTreeRoot()
					.getSubDataTableFromSubTree(tableID);
		}
		if (table == null)
			table = otherSubDataTables.get(tableID);

		table.setRecordVA(vaType, virtualArray.clone());
		recordSelectionManager.setVA(table.getRecordData(DataTable.RECORD).getRecordVA());

		virtualArray.setGroupList(null);
		eventPublisher.triggerEvent(new ReplaceRecordVAEvent(table, dataDomainType, vaType));
	}

	/**
	 * Replaces the dimension virtual array with the virtual array specified, if the dataDomain matches. If
	 * the dataDomain doesn't match, the method
	 * {@link #handleForeignRecordVAUpdate(int, String, RecordVAType, RecordVirtualArray)} is called.
	 * 
	 * @param idCategory
	 *            the type of id
	 * @param the
	 *            type of the virtual array
	 * @param virtualArray
	 *            the new virtual array
	 */
	public void replaceDimensionVA(String dataDomainType, String vaType) {
		throw new IllegalStateException("UseCases shouldn't react to this");
	}

	public void replaceDimensionVA(String dataDomainType, String vaType, DimensionVirtualArray virtualArray) {

		table.setDimensionVA(vaType, virtualArray);
		dimensionSelectionManager.setVA(virtualArray);

		// if (table.getDimensionData(DimensionVAType.STORAGE).getDimensionTree() != null) {
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
		ReplaceDimensionVAEvent event = new ReplaceDimensionVAEvent(table, dataDomainType, vaType);
		event.setSender(this);
		eventPublisher.triggerEvent(event);

	}

	public void setRecordVirtualArray(String vaType, RecordVirtualArray virtualArray) {
		table.setRecordVA(vaType, virtualArray);
	}

	public void setDimensionVirtualArray(String vaType, DimensionVirtualArray virtualArray) {
		table.setDimensionVA(vaType, virtualArray);
	}

	protected void initFullVA() {
		if (table.getRecordData(DataTable.RECORD) == null)
			table.restoreOriginalRecordVA();
	}

	/**
	 * Restore the original data. All applied filters are undone.
	 */
	public void restoreOriginalRecordVA() {
		initFullVA();

		ReplaceRecordVAEvent event = new ReplaceRecordVAEvent(table, dataDomainID, DataTable.RECORD);

		event.setSender(this);
		eventPublisher.triggerEvent(event);
	}

	@Override
	public void handleVAUpdate(RecordVADelta vaDelta, String info) {
		IDCategory targetCategory = vaDelta.getIDType().getIDCategory();
		if (targetCategory != recordIDCategory)
			return;

		if (targetCategory == recordIDCategory && vaDelta.getIDType() != recordIDType)
			vaDelta = DeltaConverter.convertDelta(recordIDType, vaDelta);
		RecordData recordData = table.getRecordData(vaDelta.getVAType());
		recordData.reset();
		recordData.setVADelta(vaDelta);

	}

	@Override
	public void handleVAUpdate(DimensionVADelta vaDelta, String info) {
		IDCategory targetCategory = vaDelta.getIDType().getIDCategory();
		if (targetCategory != dimensionIDCategory)
			return;

		DimensionVirtualArray va = table.getDimensionData(vaDelta.getVAType()).getDimensionVA();

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

		replaceRecordVirtualArrayInUseCaseListener = new ReplaceRecordVAInUseCaseListener();
		replaceRecordVirtualArrayInUseCaseListener.setHandler(this);
		replaceRecordVirtualArrayInUseCaseListener.setDataDomainType(dataDomainID);
		eventPublisher.addListener(ReplaceRecordVAInUseCaseEvent.class,
			replaceRecordVirtualArrayInUseCaseListener);

		replaceDimensionVirtualArrayInUseCaseListener = new ReplaceDimensionVAInUseCaseListener();
		replaceDimensionVirtualArrayInUseCaseListener.setHandler(this);
		replaceDimensionVirtualArrayInUseCaseListener.setDataDomainType(dataDomainID);
		eventPublisher.addListener(ReplaceDimensionVAInUseCaseEvent.class,
			replaceDimensionVirtualArrayInUseCaseListener);

		recordVAUpdateListener = new RecordVAUpdateListener();
		recordVAUpdateListener.setHandler(this);
		recordVAUpdateListener.setDataDomainType(dataDomainID);
		eventPublisher.addListener(RecordVAUpdateEvent.class, recordVAUpdateListener);

		dimensionVAUpdateListener = new DimensionVAUpdateListener();
		dimensionVAUpdateListener.setHandler(this);
		dimensionVAUpdateListener.setDataDomainType(dataDomainID);
		eventPublisher.addListener(DimensionVAUpdateEvent.class, dimensionVAUpdateListener);

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

		if (replaceRecordVirtualArrayInUseCaseListener != null) {
			eventPublisher.removeListener(replaceRecordVirtualArrayInUseCaseListener);
			replaceRecordVirtualArrayInUseCaseListener = null;
		}

		if (replaceDimensionVirtualArrayInUseCaseListener != null) {
			eventPublisher.removeListener(replaceDimensionVirtualArrayInUseCaseListener);
			replaceDimensionVirtualArrayInUseCaseListener = null;
		}

		if (recordVAUpdateListener != null) {
			eventPublisher.removeListener(recordVAUpdateListener);
			recordVAUpdateListener = null;
		}

		if (dimensionVAUpdateListener != null) {
			eventPublisher.removeListener(dimensionVAUpdateListener);
			dimensionVAUpdateListener = null;
		}

		if (aggregateGroupListener != null) {
			eventPublisher.removeListener(aggregateGroupListener);
			aggregateGroupListener = null;
		}
	}

	/**
	 * Returns the label for the record. E.g. gene for genome use case, entity for generic use case
	 * 
	 * @param bUpperCase
	 *            TRUE makes the label upper case
	 * @param bPlural
	 *            TRUE label = plural, FALSE label = singular
	 * @return label valid for the specific use case
	 */
	public String getRecordName(boolean bCapitalized, boolean bPlural) {

		String recordLabel = "";

		if (bPlural)
			recordLabel = recordLabelPlural;
		else
			recordLabel = recordLabelSingular;

		if (bCapitalized) {

			// Make first char capitalized
			recordLabel =
				recordLabel.substring(0, 1).toUpperCase() + recordLabel.substring(1, recordLabel.length());
		}

		return recordLabel;
	}

	@Override
	public void handleSelectionUpdate(ISelectionDelta selectionDelta, boolean scrollToSelection, String info) {

		if (recordSelectionManager == null)
			return;

		IDMappingManager mappingManager = GeneralManager.get().getIDMappingManager();
		if (mappingManager.hasMapping(selectionDelta.getIDType(), recordSelectionManager.getIDType())) {
			recordSelectionManager.setDelta(selectionDelta);
		}
		else if (mappingManager.hasMapping(selectionDelta.getIDType(), dimensionSelectionManager.getIDType())) {
			dimensionSelectionManager.setDelta(selectionDelta);
		}

		if (selectionDelta.getIDType() == recordGroupSelectionManager.getIDType()) {
			recordGroupSelectionManager.setDelta(selectionDelta);
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
	 * This method is called if a record VA Update was requested, but the dataDomainType specified was not
	 * this dataDomains type. Concrete handling can only be done in concrete dataDomains.
	 * 
	 * @param tableID
	 * @param dataDomainType
	 * @param vaType
	 * @param virtualArray
	 */
	public abstract void handleForeignRecordVAUpdate(int tableID, String dataDomainType, String vaType,
		RecordVirtualArray virtualArray);

	/**
	 * Returns the id type that should be used if an entity of this data domain should be printed human
	 * readable
	 * 
	 * @return
	 */
	public IDType getHumanReadableRecordIDType() {
		return humanReadableRecordIDType;
	}

	/**
	 * Get the human readable content label for a specific id. The id has to be of the recordIDType of the
	 * dataDomain.
	 * 
	 * @param id
	 *            the id to convert to a human readable label
	 * @return the readable label
	 */
	public String getRecordLabel(Object id) {
		return getRecordLabel(recordIDType, id);
	}

	public abstract String getRecordLabel(IDType idType, Object id);

	/**
	 * Get the human readable dimension label for a specific id. The id has to be of the dimensionIDType of
	 * the dataDomain.
	 * 
	 * @param id
	 *            the id to convert to a human readable label
	 * @return the readable label
	 */
	public String getDimensionLabel(Object id) {
		return getDimensionLabel(dimensionIDType, id);
	}

	/**
	 * Get the human readable dimension label for a specific id.
	 * 
	 * @param idType
	 *            specify of which id type the id is
	 * @param id
	 *            the id to convert to a human readable label
	 * @return the readable label
	 */
	public String getDimensionLabel(IDType idType, Object id) {
		String label = table.get((Integer) id).getLabel();
		if (label == null)
			label = "";
		return label;
	}

	// FIXME CONTEXT MENU
	// /**
	// * A dataDomain may contribute to the context menu. This function returns the recordItemContainer of the
	// * context menu if one was specified. This should be overridden by subclasses if needed.
	// *
	// * @return a context menu item container related to record items
	// */
	// public AItemContainer getRecordItemContainer(IDType idType, int id) {
	// return null;
	// }

	// FIXME CONTEXT MENU
	// /**
	// * A dataDomain may contribute to the context menu. This function returns dataDomain specific
	// * implementations of a context menu for content groups. * @param idType
	// *
	// * @param ids
	// * @return
	// */
	// public AItemContainer getRecordGroupItemContainer(IDType idType, ArrayList<Integer> ids) {
	// return null;
	// }

	/**
	 * Returns the primary mapping type of the record. This type is not determined at run-time but something
	 * permanent like an official gene mapping type like DAVID.
	 * 
	 * @return
	 */
	public IDType getPrimaryRecordMappingType() {
		return primaryRecordMappingType;
	}

	public IDType getPrimaryDimensionMappingType() {
		return dimensionIDType;
	}

	/**
	 * Filter manager holds all filter applied in the content dimension.
	 * 
	 * @return
	 */
	public RecordFilterManager getRecordFilterManager() {
		return recordFilterManager;
	}

	/**
	 * Filter manager holds all filter applied in the dimension dimension.
	 * 
	 * @return
	 */
	public DimensionFilterManager getDimensionFilterManager() {
		return dimensionFilterManager;
	}

	/**
	 * Create a new {@link RelationAnalyzer} for recordVAs of this DataDomain. The contentRelationAnalyzer
	 * runs in a separate thread and listens to {@link ReplaceRecordVAEvent}s to do its business.
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

	public void createDimensionGroupsFromDimensionTree(ClusterTree tree) {
		dimensionGroups.clear();
		if (tree == null)
			return;
		ClusterNode rootNode = tree.getRoot();
		if (rootNode != null && rootNode.hasChildren())
			createDimensionGroupsFromDimensionTree(rootNode);
	}

	private void createDimensionGroupsFromDimensionTree(ClusterNode parent) {

		for (ClusterNode child : parent.getChildren()) {
			if (child.hasChildren()) {
				dimensionGroups.add(new TableBasedDimensionGroupData(this, child.getSubDataTable()));
				createDimensionGroupsFromDimensionTree(child);
			}
		}

		DimensionGroupsChangedEvent event = new DimensionGroupsChangedEvent(this);
		event.setSender(this);
		GeneralManager.get().getEventPublisher().triggerEvent(event);
	}

	public void aggregateGroups(java.util.Set<Integer> groups) {
		System.out.println("Received command to aggregate experiments, not implemented yet");
	}
}
