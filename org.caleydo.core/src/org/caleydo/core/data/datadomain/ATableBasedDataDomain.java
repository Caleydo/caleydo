/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.datadomain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.datadomain.event.AggregateGroupEvent;
import org.caleydo.core.data.datadomain.event.AggregateGroupListener;
import org.caleydo.core.data.datadomain.event.CreateClusteringEvent;
import org.caleydo.core.data.datadomain.event.LoadGroupingEvent;
import org.caleydo.core.data.datadomain.event.StartClusteringListener;
import org.caleydo.core.data.datadomain.listener.CreateClusteringEventListener;
import org.caleydo.core.data.datadomain.listener.LoadGroupingEventListener;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.delta.DeltaConverter;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.events.ISelectionHandler;
import org.caleydo.core.data.selection.events.SelectionCommandListener;
import org.caleydo.core.data.selection.events.SelectionUpdateListener;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.delta.VirtualArrayDelta;
import org.caleydo.core.data.virtualarray.events.DimensionVAUpdateEvent;
import org.caleydo.core.data.virtualarray.events.IVADeltaHandler;
import org.caleydo.core.data.virtualarray.events.PerspectiveUpdatedEvent;
import org.caleydo.core.data.virtualarray.events.RecordVAUpdateEvent;
import org.caleydo.core.data.virtualarray.events.ReplacePerspectiveEvent;
import org.caleydo.core.data.virtualarray.events.ReplacePerspectiveListener;
import org.caleydo.core.data.virtualarray.events.SortByDataEvent;
import org.caleydo.core.data.virtualarray.events.VADeltaEvent;
import org.caleydo.core.data.virtualarray.events.VADeltaListener;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.data.DataDomainUpdateEvent;
import org.caleydo.core.event.data.SelectionCommandEvent;
import org.caleydo.core.event.data.SelectionUpdateEvent;
import org.caleydo.core.event.data.StartClusteringEvent;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.IIDTypeMapper;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.clusterer.ClusterResult;
import org.caleydo.core.util.clusterer.Clusterers;
import org.caleydo.core.util.clusterer.initialization.ClusterConfiguration;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * <p>
 * Primary access point to a table data set. Holds the {@link Table}, the {@link TablePerspective} which hold the rules
 * on how to access the Table and a lot of meta-information such as human-readable labels.
 * </p>
 * <p>
 * Holds the information on the {@link IDCategory} and the {@link IDType}s of the rows and columns in the data set.
 * </p>
 * <p>
 * {@link ATableBasedDataDomain}s are initialized using a {@link DataDomainConfiguration} object, which can be either
 * specified or taken from a default initialization of an implementing sub-class.
 * </p>
 *
 * @author Alexander Lex
 * @author Marc Streit
 */
@XmlType
@XmlRootElement
public abstract class ATableBasedDataDomain extends ADataDomain implements IVADeltaHandler, ISelectionHandler {

	/** The raw data for this data domain. */
	protected Table table;

	/**
	 * <p>
	 * The {@link TablePerspective} registered for this data domain. A {@link TablePerspective} is defined by its
	 * combination of {@link Perspective} and {@link DimensionPerspective}.
	 * </p>
	 * <p>
	 * The key in this hasMap is created as a concatenation of the {@link Perspective#getPerspectiveID()} s using
	 * {@link #createKey(String, String)},
	 * </p>
	 */
	protected HashMap<String, TablePerspective> tablePerspectives = new HashMap<String, TablePerspective>();

	protected IDCategory recordIDCategory;
	protected IDCategory dimensionIDCategory;

	protected IDCategory recordGroupIDCategory;
	protected IDCategory dimensionGroupIDCategory;

	protected IDType recordIDType;
	protected IDType dimensionIDType;

	/** IDType used for {@link Group}s or records in this dataDomain */
	protected IDType recordGroupIDType;

	/** same as {@link #recordGroupIDType} for dimensions */
	protected IDType dimensionGroupIDType;

	protected SelectionManager recordSelectionManager;
	protected SelectionManager dimensionSelectionManager;
	protected SelectionManager recordGroupSelectionManager;

	/** central {@link EventPublisher} to receive and send events */
	protected EventPublisher eventPublisher = GeneralManager.get().getEventPublisher();

	/**
	 * All recordPerspectiveIDs registered with the Table. This variable is synchronous with the keys of the hashMap of
	 * the Table.
	 */
	@XmlElement
	private Set<String> recordPerspectiveIDs;

	/** Same as {@link #recordPerspectiveIDs} for dimensions */
	@XmlElement
	private Set<String> dimensionPerspectiveIDs;

	protected IDMappingManager recordIDMappingManager;
	protected IDMappingManager dimensionIDMappingManager;

	/**
	 * Constructor that should be used only for serialization
	 */
	public ATableBasedDataDomain() {
		super();
	}

	/**
	 * @return the tablePerspectives, see {@link #tablePerspectives}
	 */
	public HashMap<String, TablePerspective> getTablePerspectives() {
		return tablePerspectives;
	}

	/**
	 * @param tablePerspectives
	 *            setter, see {@link #tablePerspectives}
	 */
	public void setTablePerspectives(HashMap<String, TablePerspective> tablePerspectives) {
		this.tablePerspectives = tablePerspectives;
	}

	public ATableBasedDataDomain(String dataDomainType, String dataDomainID) {
		super(dataDomainType, dataDomainID);
	}

	{
		defaultStartViewType = "org.caleydo.view.heatmap.hierarchical";
	}

	/**
	 * @return the isColumnDimension, see {@link #isColumnDimension}
	 */
	public boolean isColumnDimension() {
		return !dataSetDescription.isTransposeMatrix();
	}

	@Override
	public void init() {

		if (dataSetDescription.isTransposeMatrix()) {
			recordIDCategory = IDCategory.getIDCategory(dataSetDescription.getColumnIDSpecification().getIdCategory());
			dimensionIDCategory = IDCategory.getIDCategory(dataSetDescription.getRowIDSpecification().getIdCategory());
		} else {
			recordIDCategory = IDCategory.getIDCategory(dataSetDescription.getRowIDSpecification().getIdCategory());
			dimensionIDCategory = IDCategory.getIDCategory(dataSetDescription.getColumnIDSpecification()
					.getIdCategory());

		}

		final String seed = dataDomainID + "_" + hashCode();
		{
			recordIDType = IDType.registerInternalType("record_" + seed, recordIDCategory, EDataType.INTEGER);
			recordGroupIDCategory = IDCategory.registerInternalCategory(recordIDCategory.getCategoryName() + "_GROUP");
			recordGroupIDType = IDType.registerInternalType("group_record_" + seed, recordGroupIDCategory,
					EDataType.INTEGER);
			recordIDMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(recordIDCategory);
			recordSelectionManager = new SelectionManager(recordIDType);
			addIDCategory(recordIDCategory);
		}

		{
			dimensionIDType = IDType.registerInternalType("dimension_" + seed, dimensionIDCategory, EDataType.INTEGER);
			dimensionGroupIDCategory = IDCategory.registerInternalCategory(dimensionIDCategory.getCategoryName()
					+ "_GROUP");
			dimensionGroupIDType = IDType.registerInternalType("group_dimension_" + seed, dimensionGroupIDCategory,
					EDataType.INTEGER);

			dimensionIDMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(dimensionIDCategory);

			dimensionSelectionManager = new SelectionManager(dimensionIDType);
			recordGroupSelectionManager = new SelectionManager(recordGroupIDType);

			addIDCategory(dimensionIDCategory);
		}

		super.init();

	}

	/**
	 * Sets the {@link #table} of this dataDomain. The table may not be null. Initializes {@link #recordPerspectiveIDs}
	 * and {@link #dimensionPerspectiveIDs}.
	 *
	 * @param table
	 *            The new set which replaced the currently loaded one.
	 */
	public void setTable(Table table) {
		if (table == null)
			throw new IllegalArgumentException("Table was null");
		this.table = table;

		recordPerspectiveIDs = table.getRecordPerspectiveIDs();
		dimensionPerspectiveIDs = table.getDimensionPerspectiveIDs();
	}

	/**
	 * @return the table, see {@link #table}
	 */
	@XmlTransient
	public Table getTable() {
		return table;
	}

	/**
	 * Returns the {@link TablePerspective} for the {@link Perspective} and the {@link DimensionPerspective} specified.
	 * </p>
	 * <p>
	 * If such a container exists already, the existing container is returned. If not, a new container is created and
	 * the datadomain will be notified.
	 *
	 * </p>
	 *
	 * @param recordPerspectiveID
	 * @param dimensionPerspectiveID
	 * @return
	 */
	public TablePerspective getTablePerspective(String recordPerspectiveID, String dimensionPerspectiveID) {
		return getTablePerspective(recordPerspectiveID, dimensionPerspectiveID, true);
	}

	/**
	 * Returns the {@link TablePerspective} for the {@link Perspective} and the {@link DimensionPerspective} specified.
	 * </p>
	 * <p>
	 * If such a container exists already, the existing container is returned. If not, a new container is created.
	 * </p>
	 *
	 * @param recordPerspectiveID
	 * @param dimensionPerspectiveID
	 * @param flag
	 *            that determines whether a datadomain update event will be sent or not
	 * @return
	 */
	public TablePerspective getTablePerspective(String recordPerspectiveID, String dimensionPerspectiveID,
			boolean notifyDataDomain) {

		TablePerspective container = tablePerspectives.get(TablePerspective.createKey(recordPerspectiveID,
				dimensionPerspectiveID));
		if (container == null) {
			Perspective recordPerspective = table.getRecordPerspective(recordPerspectiveID);
			if (recordPerspective == null)
				throw new IllegalArgumentException("No record perspective registered with this datadomain for "
						+ recordPerspectiveID);

			Perspective dimensionPerspective = table.getDimensionPerspective(dimensionPerspectiveID);
			if (dimensionPerspective == null)
				throw new IllegalArgumentException("No dimension perspective registered with this datadomain for "
						+ dimensionPerspectiveID);

			container = new TablePerspective(this, recordPerspective, dimensionPerspective);

			tablePerspectives.put(TablePerspective.createKey(recordPerspectiveID, dimensionPerspectiveID), container);

			if (notifyDataDomain) {
				DataDomainUpdateEvent event = new DataDomainUpdateEvent(this);
				event.setSender(this);
				GeneralManager.get().getEventPublisher().triggerEvent(event);
			}
		}

		return container;
	}

	/**
	 * Returns a data container based on its key
	 *
	 * @param tablePerspectiveKey
	 * @return
	 */
	public TablePerspective getTablePerspective(String tablePerspectiveKey) {
		return tablePerspectives.get(tablePerspectiveKey);
	}

	/** Returns the data container made up of the default perspectives */
	public TablePerspective getDefaultTablePerspective() {
		return getTablePerspective(table.getDefaultRecordPerspective(false).getPerspectiveID(), table
				.getDefaultDimensionPerspective(false).getPerspectiveID());
	}

	/**
	 * Returns whether a {@link TablePerspective} Object exists in this datadomain for the given perspectiveIDs.
	 *
	 * @param recordPerspectiveID
	 * @param dimensionPerspectiveID
	 * @return
	 */
	public boolean hasTablePerspective(String recordPerspectiveID, String dimensionPerspectiveID) {
		return tablePerspectives.containsKey(TablePerspective.createKey(recordPerspectiveID, dimensionPerspectiveID));
	}

	/**
	 * @return All {@link TablePerspective}s of this datadomain.
	 */
	public Collection<TablePerspective> getAllTablePerspectives() {
		return tablePerspectives.values();
	}

	/**
	 * @return the recordIDMappingManager, see {@link #recordIDMappingManager}
	 */
	public IDMappingManager getRecordIDMappingManager() {
		return recordIDMappingManager;
	}

	/**
	 * @return the dimensionIDMappingManager, see {@link #dimensionIDMappingManager}
	 */
	public IDMappingManager getDimensionIDMappingManager() {
		return dimensionIDMappingManager;
	}

	/**
	 * @return the recordIDType, see {@link #recordIDType}
	 */
	public IDType getRecordIDType() {
		return recordIDType;
	}

	/**
	 * @return the dimensionIDType, see {@link #dimensionIDType}
	 */
	public IDType getDimensionIDType() {
		return dimensionIDType;
	}

	/**
	 * @return the recordIDCategory, see {@link #recordIDCategory}
	 */
	public IDCategory getRecordIDCategory() {
		return recordIDCategory;
	}

	/**
	 * @return the dimensionIDCategory, see {@link #dimensionIDCategory}
	 */
	public IDCategory getDimensionIDCategory() {
		return dimensionIDCategory;
	}

	/**
	 * @return the recordGroupIDType, see {@link #recordGroupIDType}
	 */
	public IDType getRecordGroupIDType() {
		return recordGroupIDType;
	}

	/**
	 * @return the dimensionGroupIDType, see {@link #dimensionGroupIDType}
	 */
	public IDType getDimensionGroupIDType() {
		return dimensionGroupIDType;
	}

	/**
	 * Returns a clone of the record selection manager. This is the preferred way to initialize SelectionManagers.
	 *
	 * @return a clone of the record selection manager
	 */
	public SelectionManager cloneRecordSelectionManager() {
		return recordSelectionManager.clone();
	}

	/**
	 * Returns a clone of the dimension selection manager. This is the preferred way to initialize SelectionManagers.
	 *
	 * @return a clone of the dimension selection manager
	 */
	public SelectionManager cloneDimensionSelectionManager() {
		return dimensionSelectionManager.clone();
	}

	/**
	 * Returns a clone of the record group selection manager. This is the preferred way to initialize SelectionManagers.
	 * *
	 *
	 * @return a clone of the dimension selection manager
	 */
	public SelectionManager cloneRecordGroupSelectionManager() {
		return recordGroupSelectionManager.clone();
	}

	/**
	 * Returns the virtual array for the type
	 *
	 * @param recordPerspectiveID
	 *            the type of VA requested
	 * @return
	 */
	public VirtualArray getRecordVA(String recordPerspectiveID) {
		VirtualArray va = table.getRecordPerspective(recordPerspectiveID).getVirtualArray();
		return va;
	}

	/**
	 * Returns the virtual array for the type
	 *
	 * @param dimensionPerspectiveID
	 *            the type of VA requested
	 * @return
	 */
	public VirtualArray getDimensionVA(String dimensionPerspectiveID) {
		VirtualArray va = table.getDimensionPerspective(dimensionPerspectiveID).getVirtualArray();
		return va;
	}

	/**
	 * @return the recordPerspectiveIDs, see {@link #recordPerspectiveIDs}
	 */
	public Set<String> getRecordPerspectiveIDs() {
		return recordPerspectiveIDs;
	}

	/**
	 * @return the dimensionPerspectiveIDs, see {@link #dimensionPerspectiveIDs}
	 */
	public Set<String> getDimensionPerspectiveIDs() {
		return dimensionPerspectiveIDs;
	}

	/**
	 * Initiates clustering based on the parameters passed. Sends out an event to all affected views upon positive
	 * completion to replace their VA.
	 *
	 * @param tableID
	 *            ID of the set to cluster
	 * @param clusterState
	 */
	public ClusterResult startClustering(ClusterConfiguration config) {
		// FIXME this should be re-designed so that the clustering is a separate
		// thread and communicates via events
		ClusterResult result = Clusterers.cluster(config);

		// check if clustering failed. If so, we just ignore it.
		if (result == null || (result.getDimensionResult() == null && result.getRecordResult() == null)) {
			Logger.log(new Status(IStatus.ERROR, this.toString(), "Custering failed. Result: " + result));
			return null;

		}
		boolean registerLater = false;

		switch (config.getClusterTarget()) {
		case DIMENSION_CLUSTERING:
			if (result.getDimensionResult() == null)
				return null;
			PerspectiveInitializationData dimensionResult = result.getDimensionResult();
			Perspective targetDimensionPerspective;
			if (config.isModifyExistingPerspective()) {
				targetDimensionPerspective = config.getSourceDimensionPerspective();
			} else {
				targetDimensionPerspective = config.getOptionalTargetDimensionPerspective();
				if (targetDimensionPerspective == null) {
					registerLater = true;
					targetDimensionPerspective = new Perspective(this, dimensionIDType);
				}
			}
			targetDimensionPerspective.init(dimensionResult);
			targetDimensionPerspective.setLabel(config.getClusterAlgorithmConfiguration().getLabel() + " "
					+ targetDimensionPerspective.getVirtualArray().getGroupList().size(), false);
			if (registerLater) {
				table.registerDimensionPerspective(targetDimensionPerspective);
			}
			EventPublisher.trigger(new DimensionVAUpdateEvent(dataDomainID, targetDimensionPerspective
					.getPerspectiveID(), this));
			break;
		case RECORD_CLUSTERING:
			if (result.getRecordResult() == null)
				return null;
			PerspectiveInitializationData recordResult = result.getRecordResult();
			Perspective targetRecordPerspective;
			if (config.isModifyExistingPerspective()) {
				targetRecordPerspective = config.getSourceRecordPerspective();
			} else {
				targetRecordPerspective = config.getOptionalTargetRecordPerspective();
				if (targetRecordPerspective == null) {
					registerLater = true;
					targetRecordPerspective = new Perspective(this, recordIDType);

				}
			}
			targetRecordPerspective.init(recordResult);
			targetRecordPerspective.setLabel(config.getClusterAlgorithmConfiguration().getLabel() + " "
					+ targetRecordPerspective.getVirtualArray().getGroupList().size(), false);
			if (registerLater) {
				table.registerRecordPerspective(targetRecordPerspective);
			}
			EventPublisher.trigger(new RecordVAUpdateEvent(dataDomainID, targetRecordPerspective.getPerspectiveID(),
					this));
		}

		EventPublisher.trigger(new DataDomainUpdateEvent(this));
		return result;
	}

	@Override
	public void handleVADelta(VirtualArrayDelta vaDelta, String info) {

		IDCategory targetCategory = vaDelta.getIDType().getIDCategory();
		if (targetCategory == recordIDCategory) {
			if (vaDelta.getIDType() != recordIDType)
				vaDelta = DeltaConverter.convertDelta(recordIDMappingManager, recordIDType, vaDelta);
			Perspective recordPerspective = table.getRecordPerspective(vaDelta.getPerspectiveID());
			recordPerspective.setVADelta(vaDelta);

			RecordVAUpdateEvent event = new RecordVAUpdateEvent(dataDomainID, recordPerspective.getPerspectiveID(),
					this);
			eventPublisher.triggerEvent(event);
		} else if (targetCategory == dimensionIDCategory) {
			if (vaDelta.getIDType() != dimensionIDType)
				vaDelta = DeltaConverter.convertDelta(dimensionIDMappingManager, dimensionIDType, vaDelta);
			Perspective dimensionPerspective = table.getDimensionPerspective(vaDelta.getPerspectiveID());
			dimensionPerspective.setVADelta(vaDelta);

			RecordVAUpdateEvent event = new RecordVAUpdateEvent(dataDomainID, dimensionPerspective.getPerspectiveID(),
					this);
			eventPublisher.triggerEvent(event);
		}

	}

	@Override
	public void replacePerspective(String dataDomainID, String perspectiveID, PerspectiveInitializationData data) {

		if (dataDomainID != this.dataDomainID) {
			handleForeignRecordVAUpdate(dataDomainID, perspectiveID, data);
			return;
		}

		if (table.getRecordPerspective(perspectiveID) != null) {
			table.getRecordPerspective(perspectiveID).init(data);

			RecordVAUpdateEvent event = new RecordVAUpdateEvent();
			event.setSender(this);
			event.setEventSpace(dataDomainID);
			event.setPerspectiveID(perspectiveID);
			eventPublisher.triggerEvent(event);
		} else if (table.getDimensionPerspective(perspectiveID) != null) {
			table.getDimensionPerspective(perspectiveID).init(data);

			DimensionVAUpdateEvent event = new DimensionVAUpdateEvent();
			event.setEventSpace(dataDomainID);
			event.setSender(this);
			event.setPerspectiveID(perspectiveID);
			eventPublisher.triggerEvent(event);
		}
	}

	@Override
	public void handleSelectionUpdate(SelectionDelta selectionDelta) {

		if (recordSelectionManager == null)
			return;

		if (recordIDMappingManager.hasMapping(selectionDelta.getIDType(), recordSelectionManager.getIDType())) {
			recordSelectionManager.setDelta(selectionDelta);
		} else if (dimensionIDMappingManager.hasMapping(selectionDelta.getIDType(),
				dimensionSelectionManager.getIDType())) {
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
	 * This method is called by the {@link ForeignSelectionUpdateListener}, signaling that a selection form another
	 * dataDomain is available. If possible, it is converted to be compatible with the local dataDomain and then sent
	 * out via a {@link SelectionUpdateEvent}.
	 *
	 * @param dataDomainType
	 *            the type of the dataDomain for which this selectionUpdate is intended
	 * @param delta
	 * @param scrollToSelection
	 * @param info
	 */
	public void handleForeignSelectionUpdate(String dataDomainType, SelectionDelta delta) {
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
	 * This method is called if a record VA Update was requested, but the dataDomainType specified was not this
	 * dataDomains type. Concrete handling can only be done in concrete dataDomains.
	 *
	 * @param tableID
	 * @param dataDomainType
	 * @param vaType
	 * @param data
	 */
	public void handleForeignRecordVAUpdate(String dataDomainType, String vaType, PerspectiveInitializationData data) {
		// may be interesting to implement in sub-class
	}

	/**
	 * Returns the denomination for the records. For genetic data for example this would be "Gene"
	 *
	 * @param capitalized
	 *            if true, the label is returned capitalized, e.g., "Gene", if false it would be "gene"
	 * @param plural
	 *            if true, the label is returned in the plural form of the word, e.g., "genes" instead of the singular
	 *            form, e.g., "gene"
	 * @return the denomination formatted according to the parameters passed
	 */
	public String getRecordDenomination(boolean capitalized, boolean plural) {
		String recordDenomination;
		if (plural)
			recordDenomination = recordIDCategory.getDenominationPlural();
		else
			recordDenomination = recordIDCategory.getDenomination();

		if (capitalized) {
			// Make first char capitalized
			recordDenomination = recordDenomination.substring(0, 1).toUpperCase()
					+ recordDenomination.substring(1, recordDenomination.length());
		}
		return recordDenomination;
	}

	/** Same as {@link #getRecordDenomination(boolean, boolean)} for dimensions. */
	public String getDimensionDenomination(boolean capitalized, boolean plural) {
		String dimensionDenomination;

		if (plural)
			dimensionDenomination = dimensionIDCategory.getDenominationPlural();
		else
			dimensionDenomination = dimensionIDCategory.getDenomination();

		if (capitalized) {
			// Make first char capitalized
			dimensionDenomination = dimensionDenomination.substring(0, 1).toUpperCase()
					+ dimensionDenomination.substring(1, dimensionDenomination.length());
		}
		return dimensionDenomination;
	}

	/**
	 * Get the human readable record label for the id, which is of the {@link #recordIDType}.
	 *
	 * @param id
	 *            the id to convert to a human readable label
	 * @return the readable label
	 */
	public String getRecordLabel(Object id) {
		return getRecordLabel(recordIDType, id);
	}

	/**
	 * Get the human readable dimension label for the id, which is of the {@link #dimensionIDType}.
	 *
	 * @param id
	 *            the id to convert to a human readable label
	 * @return the readable label
	 */
	public String getDimensionLabel(Object id) {
		return getDimensionLabel(dimensionIDType, id);
	}

	/**
	 * Get the human readable record label for the id, which is of the type specified.
	 *
	 * @param idType
	 *            the IDType of the id passed
	 * @param id
	 * @return the readable label
	 */
	public String getRecordLabel(IDType idType, Object id) {
		Set<String> ids = recordIDMappingManager
				.getIDAsSet(idType, idType.getIDCategory().getHumanReadableIDType(), id);
		String label = "No Mapping";
		if (ids != null && ids.size() > 0) {
			label = ids.iterator().next();
		}
		return label;
	}

	/** Same as {@link #getRecordLabel(IDType, Object)} for dimensions */
	public String getDimensionLabel(IDType idType, Object id) {
		Set<String> ids = dimensionIDMappingManager.getIDAsSet(idType, idType.getIDCategory().getHumanReadableIDType(),
				id);
		String label = "No Mapping";
		if (ids != null && ids.size() > 0) {
			label = ids.iterator().next();
		}
		return label;
	}

	public void aggregateGroups(java.util.Set<Integer> groups) {
		System.out.println("Received command to aggregate experiments, not implemented yet");
	}

	// FIXME CONTEXT MENU
	// /**
	// * A dataDomain may contribute to the context menu. This function returns
	// the recordItemContainer of the
	// * context menu if one was specified. This should be overridden by
	// subclasses if needed.
	// *
	// * @return a context menu item container related to record items
	// */
	// public AItemContainer getRecordItemContainer(IDType idType, int id) {
	// return null;
	// }

	// FIXME CONTEXT MENU
	// /**
	// * A dataDomain may contribute to the context menu. This function returns
	// dataDomain specific
	// * implementations of a context menu for content groups. * @param idType
	// *
	// * @param ids
	// * @return
	// */
	// public AItemContainer getRecordGroupItemContainer(IDType idType,
	// ArrayList<Integer> ids) {
	// return null;
	// }

	@Override
	public int getDataAmount() {
		if (table == null)
			return 0;
		try {
			return table.size() * table.depth();
		} catch (NoSuchElementException e) {
			return 0;
		}
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();
		SelectionUpdateListener selectionUpdateListener = new SelectionUpdateListener();
		selectionUpdateListener.setHandler(this);
		selectionUpdateListener.setExclusiveEventSpace(dataDomainID);
		listeners.register(SelectionUpdateEvent.class, selectionUpdateListener);

		SelectionCommandListener selectionCommandListener = new SelectionCommandListener();
		selectionCommandListener.setHandler(this);
		selectionCommandListener.setEventSpace(dataDomainID);
		listeners.register(SelectionCommandEvent.class, selectionCommandListener);

		StartClusteringListener startClusteringListener = new StartClusteringListener();
		startClusteringListener.setHandler(this);
		startClusteringListener.setEventSpace(dataDomainID);
		listeners.register(StartClusteringEvent.class, startClusteringListener);

		VADeltaListener vADeltaListener = new VADeltaListener();
		vADeltaListener.setHandler(this);
		vADeltaListener.setEventSpace(dataDomainID);
		listeners.register(VADeltaEvent.class, vADeltaListener);

		ReplacePerspectiveListener replacePerspectiveListener = new ReplacePerspectiveListener();
		replacePerspectiveListener.setHandler(this);
		replacePerspectiveListener.setEventSpace(dataDomainID);
		listeners.register(ReplacePerspectiveEvent.class, replacePerspectiveListener);

		AggregateGroupListener aggregateGroupListener = new AggregateGroupListener();
		aggregateGroupListener.setHandler(this);
		listeners.register(AggregateGroupEvent.class, aggregateGroupListener);

		listeners.register(CreateClusteringEvent.class, new CreateClusteringEventListener(this));
		listeners.register(LoadGroupingEvent.class, new LoadGroupingEventListener(this));

		listeners.register(this);
	}

	// TODO this is never called!

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();

		listeners.unregisterAll();
	}

	/**
	 * <p>
	 * Converts a {@link Perspective} with an IDType that is not the {@link #recordIDType} or the
	 * {@link #dimensionIDType}, but is of the same {@link IDCategory} as one of the two to a new Perspective with the
	 * local {@link IDType}.
	 * </p>
	 * <p>
	 * Grouping, and naming is preserved, sample elements and trees are not.
	 * </p>
	 */
	public Perspective convertForeignPerspective(Perspective foreignPerspective) {

		foreignPerspective.setCrossDatasetID(foreignPerspective.getPerspectiveID());

		IDType localIDType = getPrimaryIDType(foreignPerspective.getIdType());
		IDMappingManager idMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(localIDType);

		if (foreignPerspective.getIdType() == localIDType)
			return foreignPerspective;

		VirtualArray foreignVA = foreignPerspective.getVirtualArray();

		GroupList groupList = foreignVA.getGroupList();

		PerspectiveInitializationData data = new PerspectiveInitializationData();
		ArrayList<Integer> indices = new ArrayList<Integer>(foreignVA.size());
		ArrayList<Integer> groupSizes = new ArrayList<Integer>(groupList.size());
		ArrayList<Integer> sampleElements = new ArrayList<Integer>(groupList.size());
		ArrayList<String> groupNames = new ArrayList<String>(groupList.size());

		for (Group foreignGroup : groupList) {
			// initialize number of groups with 0
			groupSizes.add(0);
			sampleElements.add(0);
			groupNames.add(foreignGroup.getLabel());
		}

		int count = 0;
		int unmapped = 0;
		IIDTypeMapper<Integer, Integer> mapper = idMappingManager.getIDTypeMapper(foreignVA.getIdType(), localIDType);
		for (Integer foreignVAID : foreignVA) {
			Set<Integer> localVAIDS = mapper.apply(foreignVAID);
			if (localVAIDS == null) {
				unmapped++;
				continue;
			}
			for (Integer localVAID : localVAIDS) {
				if (localVAID == null) {
					unmapped++;
					continue;
				}
				indices.add(localVAID);
				int groupIndex = groupList.getGroupOfVAIndex(foreignVA.indexOf(foreignVAID)).getGroupIndex();
				groupSizes.set(groupIndex, groupSizes.get(groupIndex) + 1);
				sampleElements.set(groupIndex, count);
				count++;
			}

		}

		if (unmapped > 0) {
			Logger.log(new Status(IStatus.INFO, this.toString(), "Failed to convert " + unmapped
					+ " elements when converting " + foreignPerspective + " with size "
					+ foreignPerspective.getVirtualArray().size() + " for this data domain."));

		}

		data.setData(indices, groupSizes, sampleElements, groupNames);

		Perspective localPerspective = new Perspective(this, localIDType);
		localPerspective.setUnmappedElements(unmapped);
		localPerspective.setCrossDatasetID(foreignPerspective.getCrossDatasetID());
		localPerspective.setIDType(localIDType);
		localPerspective.init(data);
		localPerspective.setLabel(foreignPerspective.getLabel(), foreignPerspective.isLabelDefault());
		return localPerspective;

	}

	@ListenTo
	public void handleVASorting(SortByDataEvent event) {
		if (!event.getDataDomainID().equals(dataDomainID))
			return;
		TablePerspective tPerspective = event.getTablePerspective();
		if (tPerspective == null) {
			tPerspective = getTablePerspective(event.getTablePerspectiveKey());
		}

		Perspective perspective = null;
		IDType pIDType = event.getPerspectiveIDType();
		IDType sortByIDType = getOppositeIDType(pIDType);

		Set<Integer> sortByIDs = IDMappingManagerRegistry.get().getIDMappingManager(event.getSortByIDType())
				.getIDAsSet(event.getSortByIDType(), sortByIDType, event.getSortByID());
		if (sortByIDs == null || sortByIDs.size() < 1)
			return;
		Integer sortByID = sortByIDs.iterator().next();

		assert (tPerspective != null) : "no tPerspective for " + event.getTablePerspectiveKey();

		VirtualArray virtualArray;
		ArrayList<Float> valueColumn = null;
		if (tPerspective == null)
			return;

		perspective = tPerspective.getPerspective(pIDType);
		virtualArray = perspective.getVirtualArray();

		valueColumn = new ArrayList<>(virtualArray.size());
		for (Integer index : virtualArray) {
			valueColumn.add(getNormalizedValue(getPrimaryIDType(pIDType), index, sortByIDType, sortByID));
		}

		perspective.sort(valueColumn);
		PerspectiveUpdatedEvent pUpdateEvent = new PerspectiveUpdatedEvent(perspective);
		pUpdateEvent.setSender(this);
		eventPublisher.triggerEvent(pUpdateEvent);

	}

	// ================ New ID based interface ======================

	/** Returns true if the specified id type is one of the two primary categories registered for this table. */
	public boolean hasIDCategory(IDType idType) {
		if (recordIDCategory.isOfCategory(idType))
			return true;
		if (dimensionIDCategory.isOfCategory(idType))
			return true;
		return false;
	}

	/** Returns true if one of the two primary ID types <b>exactly</b> matches the id type */
	public boolean hasIDType(IDType idType) {
		if (recordIDType.equals(idType))
			return true;
		if (dimensionIDType.equals(idType))
			return true;

		return false;
	}

	/**
	 * Returns the normalized value of the table by using the IDTypes to identify record/dimension. Wrapper around
	 * {@link Table#getNormalizedValue(Integer, Integer)}.
	 *
	 * @param idType1
	 * @param id1
	 * @param idType2
	 * @param id2
	 * @return
	 */
	public float getNormalizedValue(IDType idType1, Integer id1, IDType idType2, Integer id2) {
		if (idType1.equals(recordIDType) && idType2.equals(dimensionIDType)) {
			return table.getNormalizedValue(id2, id1);
		} else if (idType2.equals(recordIDType) && idType1.equals(dimensionIDType)) {
			return table.getNormalizedValue(id1, id2);
		}
		throw new IllegalStateException("At least one of the ID types " + idType1 + " " + idType2
				+ " not registered with this datadomain " + this.toString());
	}

	/**
	 * Resolves IDTypes to record/dimension and calls {@link Table#getRawAsString(Integer, Integer)}
	 *
	 * @param idType1
	 * @param id1
	 * @param idType2
	 * @param id2
	 * @return
	 */
	public String getRawAsString(IDType idType1, Integer id1, IDType idType2, Integer id2) {
		if (idType1.equals(recordIDType) && idType2.equals(dimensionIDType)) {
			return table.getRawAsString(id2, id1);
		} else if (idType2.equals(recordIDType) && idType1.equals(dimensionIDType)) {
			return table.getRawAsString(id1, id2);
		}
		throw new IllegalStateException("At least one of the ID types " + idType1 + " " + idType2
				+ " not registered with this datadomain " + this.toString());
	}

	/**
	 * Returns true if the type is identical with a primary IDType, i.e., either recordIDType or dimensionIDType.
	 *
	 * @param candidateType
	 * @return
	 */

	public boolean isPrimaryIDType(IDType candidateType) {
		if (candidateType.equals(recordIDType))
			return true;
		else if (candidateType.equals(dimensionIDType))
			return true;
		return false;
	}

	/**
	 * Returns the primary type of this datadomain that is of the same category as the provided IDType.
	 *
	 * @param candidateType
	 * @return
	 */
	public IDType getPrimaryIDType(IDType candidateType) {
		if (recordIDType.getIDCategory().isOfCategory(candidateType)) {
			return recordIDType;
		} else if (dimensionIDType.getIDCategory().isOfCategory(candidateType)) {
			return dimensionIDType;
		}
		throw new IllegalStateException("ID type " + candidateType + " not registered with this datadomain "
				+ this.toString());
	}

	/**
	 * Convenience wrapper for {@link #getOppositeIDType(IDCategory)}
	 *
	 * @param idType
	 * @return
	 */
	public IDType getOppositeIDType(IDType idType) {
		return getOppositeIDType(idType.getIDCategory());
	}

	/**
	 * Returns the ID type "opposite" to the given id category. If the passed id category matches the record id category
	 * the id type for dimensions is returned and vice versa.
	 *
	 * @param idCategory
	 * @return
	 */
	public IDType getOppositeIDType(IDCategory idCategory) {
		if (idCategory.equals(recordIDCategory)) {
			return dimensionIDType;
		} else if (idCategory.equals(dimensionIDCategory)) {
			return recordIDType;
		}
		throw new IllegalStateException("ID Category " + idCategory + " not registered with this datadomain "
				+ this.toString());
	}
}
