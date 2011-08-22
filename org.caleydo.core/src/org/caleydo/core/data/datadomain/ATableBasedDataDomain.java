package org.caleydo.core.data.datadomain;

import java.util.HashMap;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.EDimensionType;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.collection.table.DimensionPerspective;
import org.caleydo.core.data.collection.table.RecordPerspective;
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
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.TableBasedDimensionGroupData;
import org.caleydo.core.data.virtualarray.delta.DimensionVADelta;
import org.caleydo.core.data.virtualarray.delta.RecordVADelta;
import org.caleydo.core.data.virtualarray.events.DimensionReplaceVAEvent;
import org.caleydo.core.data.virtualarray.events.DimensionReplaceVAListener;
import org.caleydo.core.data.virtualarray.events.DimensionVADeltaEvent;
import org.caleydo.core.data.virtualarray.events.DimensionVADeltaListener;
import org.caleydo.core.data.virtualarray.events.DimensionVAUpdateEvent;
import org.caleydo.core.data.virtualarray.events.IDimensionVADeltaHandler;
import org.caleydo.core.data.virtualarray.events.IRecordVADeltaHandler;
import org.caleydo.core.data.virtualarray.events.RecordReplaceVAEvent;
import org.caleydo.core.data.virtualarray.events.RecordReplaceVAListener;
import org.caleydo.core.data.virtualarray.events.RecordVADeltaEvent;
import org.caleydo.core.data.virtualarray.events.RecordVADeltaListener;
import org.caleydo.core.data.virtualarray.events.RecordVAUpdateEvent;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.similarity.RelationAnalyzer;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.EventPublisher;
import org.caleydo.core.manager.event.data.DimensionGroupsChangedEvent;
import org.caleydo.core.manager.event.data.StartClusteringEvent;
import org.caleydo.core.manager.event.view.SelectionCommandEvent;
import org.caleydo.core.manager.event.view.tablebased.SelectionUpdateEvent;
import org.caleydo.core.util.clusterer.ClusterHelper;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.util.clusterer.ClusterState;
import org.caleydo.core.util.clusterer.ClustererType;
import org.caleydo.core.view.opengl.canvas.listener.ForeignSelectionCommandListener;
import org.caleydo.core.view.opengl.canvas.listener.ForeignSelectionUpdateListener;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.SelectionCommandListener;
import org.caleydo.core.view.opengl.canvas.listener.SelectionUpdateListener;

@XmlType
@XmlRootElement
public abstract class ATableBasedDataDomain
	extends ADataDomain
	implements IRecordVADeltaHandler, IDimensionVADeltaHandler, ISelectionUpdateHandler,
	ISelectionCommandHandler {

	private SelectionUpdateListener selectionUpdateListener;
	private SelectionCommandListener selectionCommandListener;
	private StartClusteringListener startClusteringListener;

	private DimensionReplaceVAListener dimensionReplaceVAListener;
	private DimensionVADeltaListener dimensionVADeltaListener;
	private RecordReplaceVAListener recordReplaceVAListener;
	private RecordVADeltaListener recordVADeltaListener;

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

	@XmlElement
	private Set<String> recordPerspectiveIDs;
	@XmlElement
	private Set<String> dimensionPerspectiveIDs;

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
				EDimensionType.INT);
		dimensionIDType =
			IDType.registerType("dimension_" + dataDomainID + "_" + hashCode(), dimensionIDCategory,
				EDimensionType.INT);

		recordGroupIDType =
			IDType.registerType("group_record_" + dataDomainID + "_" + hashCode(), recordIDCategory,
				EDimensionType.INT);
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
		
		recordPerspectiveIDs = table.getRecordPerspectiveIDs();
		dimensionPerspectiveIDs = table.getDimensionPerspectiveIDs();
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

	// public DataTable getTable(int tableID) {
	// if (table.getID() == tableID)
	// return table;
	//
	// ClusterNode root = table.getDimensionData(DataTable.DIMENSION).getDimensionTreeRoot();
	// DataTable set = root.getSubDataTableFromSubTree(tableID);
	//
	// if (set == null)
	// set = otherSubDataTables.get(tableID);
	// return set;
	//
	// }

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

		// initFullVA();
		initSelectionManagers();

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
		dimensionSelectionManager = new DimensionSelectionManager(dimensionIDType);
		recordGroupSelectionManager = new SelectionManager(recordGroupIDType);
	}

	/**
	 * Returns a clone of the record selection manager. You have to set your virtual array manually. This is
	 * the preferred way to initialize SelectionManagers.
	 * 
	 * @return a clone of the record selection manager
	 */
	public RecordSelectionManager getRecordSelectionManager() {
		return (RecordSelectionManager) recordSelectionManager.clone();
	}

	/**
	 * Returns a clone of the dimension selection manager. You have to set your virtual array manually. This
	 * is the preferred way to initialize SelectionManagers.
	 * 
	 * @return a clone of the dimension selection manager
	 */
	public DimensionSelectionManager getDimensionSelectionManager() {
		return (DimensionSelectionManager) dimensionSelectionManager.clone();
	}

	public SelectionManager getRecordGroupSelectionManager() {
		return recordGroupSelectionManager.clone();
	}

	/**
	 * Returns the virtual array for the type
	 * 
	 * @param recordPerspectiveID
	 *            the type of VA requested
	 * @return
	 */
	public RecordVirtualArray getRecordVA(String recordPerspectiveID) {
		RecordVirtualArray va = table.getRecordPerspective(recordPerspectiveID).getVirtualArray();
		return va;
	}

	/**
	 * Returns the virtual array for the type
	 * 
	 * @param dimensionPerspectiveID
	 *            the type of VA requested
	 * @return
	 */
	public DimensionVirtualArray getDimensionVA(String dimensionPerspectiveID) {
		DimensionVirtualArray va = table.getDimensionPerspective(dimensionPerspectiveID).getVirtualArray();
		return va;
	}
	
	public Set<String> getDimensionPerspectiveIDs() {
		return dimensionPerspectiveIDs;
	}
	
	public Set<String> getRecordPerspectiveIDs() {
		return recordPerspectiveIDs;
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
				clusterState.getDimensionPerspective().getTree().getRoot()
					.getSubDataTableFromSubTree(tableID);

		// TODO: warning
		if (table == null)
			return;

		table.cluster(clusterState);

		RecordPerspective recordData = clusterState.getRecordPerspective();
		DimensionPerspective dimensionData = clusterState.getDimensionPerspective();
		if (table.containsUncertaintyData()) {
			ClusterHelper.calculateAggregatedUncertainties(recordData, table);
			ClusterHelper.calculateClusterAverages(dimensionData, recordData,
				ClustererType.RECORD_CLUSTERING, table);
			recordData.getTree().setSortingStrategy(ESortingStrategy.CERTAINTY);
			recordData.createVABasedOnTree();
		}

		// This should be done to avoid problems with group info in HHM

		if (clusterState.getClustererType() == ClustererType.DIMENSION_CLUSTERING
			|| clusterState.getClustererType() == ClustererType.BI_CLUSTERING) {
			eventPublisher.triggerEvent(new DimensionReplaceVAEvent(table, dataDomainID, clusterState
				.getDimensionPerspective().getPerspectiveID()));
			((DataTable) table).createSubDataTable(clusterState.getDimensionPerspective());
		}

		if (clusterState.getClustererType() == ClustererType.RECORD_CLUSTERING
			|| clusterState.getClustererType() == ClustererType.BI_CLUSTERING) {
			eventPublisher.triggerEvent(new RecordReplaceVAEvent(table, dataDomainID, clusterState
				.getRecordPerspective().getPerspectiveID()));

		}
	}

	/**
	 * Resets the context VA to it's initial state
	 */
	public void resetRecordVA(String recordPerspectiveID) {
		table.getRecordPerspective(recordPerspectiveID).setVirtualArray(table.getBaseRecordVA(recordPerspectiveID));
	}

	// /**
	// * Replace content VA for the default table.
	// */
	//
	// public void replaceRecordVA(String dataDomainType, String vaType, RecordVirtualArray virtualArray) {
	//
	// replaceRecordVA(table.getID(), dataDomainType, vaType, virtualArray);
	//
	// // Tree<ClusterNode> dimensionTree = table.getDimensionData(Set.STORAGE).getDimensionTree();
	// // if (dimensionTree == null)
	// // return;
	// // else {
	// // // TODO check whether we need this for the meat sets, it fires a lot of unnecessar events in other
	// // // cases
	// // // for (DataTable tmpSet : dimensionTree.getRoot().getAllSubDataTablesFromSubTree()) {
	// // // tmpSet.setRecordVA(vaType, virtualArray.clone());
	// // // eventPublisher.triggerEvent(new ReplaceRecordVAEvent(tmpSet, dataDomainType, vaType));
	// // // }
	// // }
	// }

	/**
	 * Replace record VA for a specific table.
	 * 
	 * @param tableID
	 * @param dataDomainType
	 * @param recordPerspectiveID
	 * @param virtualArray
	 */
	@Override
	public void replaceRecordVA(int tableID, String dataDomainType, String recordPerspectiveID,
		RecordVirtualArray virtualArray) {

		if (dataDomainType != this.dataDomainID) {
			handleForeignRecordVAUpdate(tableID, dataDomainType, recordPerspectiveID, virtualArray);
			return;
		}
		DataTable table;
		if (tableID == this.table.getID()) {
			table = this.table;
		}
		else {
			table =
				this.table.getDimensionPerspective(recordPerspectiveID).getTreeRoot()
					.getSubDataTableFromSubTree(tableID);
		}
		if (table == null)
			table = otherSubDataTables.get(tableID);

		table.getRecordPerspective(recordPerspectiveID).setVirtualArray(virtualArray);

		RecordVAUpdateEvent event = new RecordVAUpdateEvent();
		event.setSender(this);
		event.setDataDomainID(dataDomainID);
		event.setDataTableID(tableID);
		eventPublisher.triggerEvent(event);
	}

	@Override
	public void handleRecordVADelta(RecordVADelta vaDelta, String info) {
		IDCategory targetCategory = vaDelta.getIDType().getIDCategory();
		if (targetCategory != recordIDCategory)
			return;

		if (targetCategory == recordIDCategory && vaDelta.getIDType() != recordIDType)
			vaDelta = DeltaConverter.convertDelta(recordIDType, vaDelta);
		RecordPerspective recordData = table.getRecordPerspective(vaDelta.getVAType());
		recordData.setVADelta(vaDelta);

		RecordVAUpdateEvent event = new RecordVAUpdateEvent();
		event.setSender(this);
		event.setDataDomainID(dataDomainID);
		event.setDataTableID(table.getID());
		eventPublisher.triggerEvent(event);

	}

	@Override
	public void replaceDimensionVA(int dataTableID, String dataDomainType, String dimensionPerspectiveID,
		DimensionVirtualArray virtualArray) {
		if (dataTableID != table.getID())
			return;

		table.getDimensionPerspective(dimensionPerspectiveID).setVirtualArray(virtualArray);

		DimensionVAUpdateEvent event = new DimensionVAUpdateEvent();
		event.setDataDomainID(dataDomainID);
		event.setSender(this);
		event.setDataTableID(dataTableID);
		eventPublisher.triggerEvent(event);

	}

	// FIXME: do we need those methods or can we just use the replaceDimensionVA?
	public void setRecordVirtualArray(String recordPerspectiveID, RecordVirtualArray virtualArray) {
		table.getRecordPerspective(recordPerspectiveID).setVirtualArray(virtualArray);
	}

	public void setDimensionVirtualArray(String dimensionPerspectiveID, DimensionVirtualArray virtualArray) {
		table.getDimensionPerspective(dimensionPerspectiveID).setVirtualArray(virtualArray);
	}

	// protected void initFullVA(String recordPerspectiveID) {
	// if (table.getRecordData(recordPerspectiveID) == null)
	// resetRecordVA(recordPerspectiveID);
	// }

	/**
	 * Restore the original data. All applied filters are undone.
	 */
	// public void restoreOriginalRecordVA(String recordPerspectiveID) {
	// initFullVA(recordPerspectiveID);
	//
	// RecordReplaceVAEvent event = new RecordReplaceVAEvent(table, dataDomainID, recordPerspectiveID);
	// event.setSender(this);
	// eventPublisher.triggerEvent(event);
	// }

	@Override
	public void handleDimensionVADelta(DimensionVADelta vaDelta, String info) {
		// TODO Auto-generated method stub

	}

	// @Override
	// public void handleVAUpdate(DimensionVADelta vaDelta, String info) {
	// IDCategory targetCategory = vaDelta.getIDType().getIDCategory();
	// if (targetCategory != dimensionIDCategory)
	// return;
	//
	// DimensionVirtualArray va = table.getDimensionData(vaDelta.getVAType()).getDimensionVA();
	// va.setDelta(vaDelta);
	// }

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
		selectionUpdateListener.setExclusiveDataDomainID(dataDomainID);
		eventPublisher.addListener(SelectionUpdateEvent.class, selectionUpdateListener);

		selectionCommandListener = new SelectionCommandListener();
		selectionCommandListener.setHandler(this);
		selectionCommandListener.setDataDomainID(dataDomainID);
		eventPublisher.addListener(SelectionCommandEvent.class, selectionCommandListener);

		startClusteringListener = new StartClusteringListener();
		startClusteringListener.setHandler(this);
		startClusteringListener.setDataDomainID(dataDomainID);
		eventPublisher.addListener(StartClusteringEvent.class, startClusteringListener);

		recordVADeltaListener = new RecordVADeltaListener();
		recordVADeltaListener.setHandler(this);
		recordVADeltaListener.setDataDomainID(dataDomainID);
		eventPublisher.addListener(RecordVADeltaEvent.class, recordVADeltaListener);

		recordReplaceVAListener = new RecordReplaceVAListener();
		recordReplaceVAListener.setHandler(this);
		recordReplaceVAListener.setDataDomainID(dataDomainID);
		eventPublisher.addListener(RecordReplaceVAEvent.class, recordReplaceVAListener);

		dimensionVADeltaListener = new DimensionVADeltaListener();
		dimensionVADeltaListener.setHandler(this);
		dimensionVADeltaListener.setDataDomainID(dataDomainID);
		eventPublisher.addListener(DimensionVADeltaEvent.class, dimensionVADeltaListener);

		dimensionReplaceVAListener = new DimensionReplaceVAListener();
		dimensionReplaceVAListener.setHandler(this);
		dimensionReplaceVAListener.setDataDomainID(dataDomainID);
		eventPublisher.addListener(DimensionReplaceVAEvent.class, dimensionReplaceVAListener);

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

		if (recordReplaceVAListener != null) {
			eventPublisher.removeListener(recordReplaceVAListener);
			recordReplaceVAListener = null;
		}

		if (dimensionReplaceVAListener != null) {
			eventPublisher.removeListener(dimensionReplaceVAListener);
			dimensionReplaceVAListener = null;
		}

		if (recordVADeltaListener != null) {
			eventPublisher.removeListener(recordVADeltaListener);
			recordVADeltaListener = null;
		}

		if (dimensionVADeltaListener != null) {
			eventPublisher.removeListener(dimensionVADeltaListener);
			dimensionVADeltaListener = null;
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
	public void handleSelectionUpdate(SelectionDelta selectionDelta, boolean scrollToSelection, String info) {

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
	public void handleForeignSelectionUpdate(String dataDomainType, SelectionDelta delta,
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
