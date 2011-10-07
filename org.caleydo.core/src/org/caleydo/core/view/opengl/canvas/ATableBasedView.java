package org.caleydo.core.view.opengl.canvas;

import java.util.ArrayList;

import javax.management.InvalidAttributeValueException;
import javax.media.opengl.awt.GLCanvas;

import org.caleydo.core.data.collection.dimension.DataRepresentation;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.EDataFilterLevel;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.id.IDCategory;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.selection.DimensionSelectionManager;
import org.caleydo.core.data.selection.RecordSelectionManager;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.DeltaConverter;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDeltaItem;
import org.caleydo.core.data.selection.events.ClearSelectionsListener;
import org.caleydo.core.data.selection.events.ISelectionCommandHandler;
import org.caleydo.core.data.selection.events.ISelectionUpdateHandler;
import org.caleydo.core.data.selection.events.SelectionCommandListener;
import org.caleydo.core.data.selection.events.SelectionUpdateListener;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.events.DimensionVAUpdateEvent;
import org.caleydo.core.data.virtualarray.events.DimensionVAUpdateListener;
import org.caleydo.core.data.virtualarray.events.IDimensionVAUpdateHandler;
import org.caleydo.core.data.virtualarray.events.IRecordVAUpdateHandler;
import org.caleydo.core.data.virtualarray.events.RecordVAUpdateEvent;
import org.caleydo.core.data.virtualarray.events.RecordVAUpdateListener;
import org.caleydo.core.event.view.ClearSelectionsEvent;
import org.caleydo.core.event.view.DataDomainsChangedEvent;
import org.caleydo.core.event.view.SelectionCommandEvent;
import org.caleydo.core.event.view.SwitchDataRepresentationEvent;
import org.caleydo.core.event.view.tablebased.RedrawViewEvent;
import org.caleydo.core.event.view.tablebased.SelectionUpdateEvent;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.ITableBasedDataDomainView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.RedrawViewListener;
import org.caleydo.core.view.opengl.canvas.listener.SwitchDataRepresentationListener;
import org.caleydo.core.view.vislink.ConnectedElementRepresentationManager;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Composite;

/**
 * Base class for OpenGL2 views that visualize {@link DataTable}s.
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public abstract class ATableBasedView
	extends AGLView
	implements ITableBasedDataDomainView, ISelectionUpdateHandler, IRecordVAUpdateHandler,
	IDimensionVAUpdateHandler, ISelectionCommandHandler, IViewCommandHandler {

	protected DataTable table;

	protected ATableBasedDataDomain dataDomain;

	protected ConnectedElementRepresentationManager connectedElementRepresentationManager;

	/**
	 * This manager is responsible for the content in the dimensions (the indices). The
	 * contentSelectionManager is initialized when the useCase is set ({@link #setDataDomain(IDataDomain)}).
	 */
	protected RecordSelectionManager recordSelectionManager;

	/**
	 * This manager is responsible for the management of the dimensions in the table. The
	 * dimensionSelectionManager is initialized when the useCase is set ( {@link #setDataDomain(IDataDomain)}
	 * ).
	 */
	protected DimensionSelectionManager dimensionSelectionManager;

	/**
	 * Define what level of filtering on the data should be applied
	 */
	protected EDataFilterLevel dataFilterLevel = EDataFilterLevel.ONLY_CONTEXT;

	protected boolean useRandomSampling = true;

	protected int numberOfRandomElements = 100;

	protected SelectionUpdateListener selectionUpdateListener;
	protected SelectionCommandListener selectionCommandListener;

	protected RedrawViewListener redrawViewListener;
	protected ClearSelectionsListener clearSelectionsListener;

	protected RecordVAUpdateListener recordVAUpdateListener;
	protected DimensionVAUpdateListener dimensionVAUpdateListener;

	protected SwitchDataRepresentationListener switchDataRepresentationListener;

	protected IDType recordIDType;
	protected IDType dimensionIDType;

	protected DataRepresentation dimensionDataRepresentation = DataRepresentation.NORMALIZED;

	/**
	 * Constructor for dimension based views
	 * 
	 * @param glCanvas
	 * @param label
	 * @param viewFrustum
	 */
	protected ATableBasedView(GLCanvas glCanvas, Composite parentComposite, final ViewFrustum viewFrustum) {
		super(glCanvas, parentComposite, viewFrustum);

		connectedElementRepresentationManager =
			generalManager.getViewManager().getConnectedElementRepresentationManager();
	}

	@Override
	public void setDataDomain(ATableBasedDataDomain dataDomain) {

		if (this.dataDomain == dataDomain)
			return;

		this.dataDomain = (ATableBasedDataDomain) dataDomain;

		recordSelectionManager = this.dataDomain.getRecordSelectionManager();
		dimensionSelectionManager = this.dataDomain.getDimensionSelectionManager();

		recordIDType = dataDomain.getRecordIDType();
		dimensionIDType = dataDomain.getDimensionIDType();

		initData();

		DataDomainsChangedEvent event = new DataDomainsChangedEvent(this);
		event.setSender(this);
		GeneralManager.get().getEventPublisher().triggerEvent(event);

		setDisplayListDirty();
	}

	@Override
	public ATableBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	@Override
	public void initData() {
		if (table == null)
			table = dataDomain.getTable();

		super.initData();

		initLists();
	}

	/**
	 * View specific data initialization
	 */
	protected abstract void initLists();

	/**
	 * Create 0:n {@link SelectedElementRep} for the selectionDelta
	 * 
	 * @param iDType
	 *            TODO
	 * @param selectionDelta
	 *            the selection delta which should be represented
	 * @throws InvalidAttributeValueException
	 *             when the selectionDelta does not contain a valid type for this view
	 */
	protected abstract ArrayList<SelectedElementRep> createElementRep(IDType idType, int id)
		throws InvalidAttributeValueException;

	@Override
	public void handleSelectionUpdate(SelectionDelta selectionDelta, boolean scrollToSelection, String info) {

		if (selectionDelta.getIDType().getIDCategory().equals(recordIDType.getIDCategory())) {
			// Check for type that can be handled
			if (selectionDelta.getIDType() != recordIDType) {
				selectionDelta =
					DeltaConverter.convertDelta(dataDomain.getRecordIDMappingManager(), recordIDType,
						selectionDelta);
			}

			recordSelectionManager.setDelta(selectionDelta);
			handleConnectedElementReps(selectionDelta);
			reactOnExternalSelection(selectionDelta, scrollToSelection);
			setDisplayListDirty();
		}
		else if (selectionDelta.getIDType() == dimensionIDType) {

			dimensionSelectionManager.setDelta(selectionDelta);
			handleConnectedElementReps(selectionDelta);
			reactOnExternalSelection(selectionDelta, scrollToSelection);
			setDisplayListDirty();
		}

	}

	@Override
	public void handleRecordVAUpdate(String recordPerspectiveID) {
		if (this.recordPerspectiveID == null || !this.recordPerspectiveID.equals(recordPerspectiveID))
			return;
		recordVA = dataDomain.getRecordVA(recordPerspectiveID);

		reactOnRecordVAChanges();

		// reactOnExternalSelection();
		setDisplayListDirty();
	}

	@Override
	public void handleDimensionVAUpdate(String dimensionPerspectiveID) {
		if (!this.dimensionPerspectiveID.equals(dimensionPerspectiveID))
			return;

		dimensionVA = dataDomain.getDimensionVA(dimensionPerspectiveID);
		setDisplayListDirty();
	}

	/**
	 * Is called any time a update is triggered externally. Should be implemented by inheriting views.
	 */
	protected void reactOnExternalSelection(SelectionDelta selectionDelta, boolean scrollToSelection) {

	}

	/**
	 * Is called any time a virtual array is changed. Can be implemented by inheriting views if some action is
	 * necessary
	 * 
	 * @param delta
	 */
	protected void reactOnRecordVAChanges() {

	}

	@Override
	public void handleRedrawView() {
		setDisplayListDirty();
	}

	@Override
	public void handleClearSelections() {
		connectedElementRepresentationManager.clear(recordIDType);
		connectedElementRepresentationManager.clear(dimensionIDType);
		recordSelectionManager.clearSelections();
		dimensionSelectionManager.clearSelections();

		setDisplayListDirty();
		setDisplayListDirty();
	}

	@Override
	public void handleSelectionCommand(IDCategory idCategory, SelectionCommand selectionCommand) {
		if (idCategory == dataDomain.getRecordIDCategory())
			recordSelectionManager.executeSelectionCommand(selectionCommand);
		else if (idCategory == dataDomain.getDimensionIDCategory())
			dimensionSelectionManager.executeSelectionCommand(selectionCommand);
		else
			return;
		setDisplayListDirty();
	}

	/**
	 * Handles the creation of {@link SelectedElementRep} according to the data in a selectionDelta
	 * 
	 * @param selectionDelta
	 *            the selection data that should be handled
	 */
	protected void handleConnectedElementReps(SelectionDelta selectionDelta) {
		try {
			int id = -1;

			IDType idType;

			if (selectionDelta.size() > 0) {
				for (SelectionDeltaItem item : selectionDelta) {
					if (!connectedElementRepresentationManager.isSelectionTypeRenderedWithVisuaLinks(item
						.getSelectionType()) || item.isRemove())
						continue;
					if (selectionDelta.getIDType() == recordIDType) {
						id = item.getID();
						idType = recordIDType;
						if (!recordVA.contains(id))
							return;

					}
					else if (selectionDelta.getIDType() == dimensionIDType) {
						id = item.getID();
						idType = dimensionIDType;
						if (!dimensionVA.contains(id))
							return;
					}
					else
						throw new InvalidAttributeValueException("Can not handle data type: "
							+ selectionDelta.getIDType());

					if (id == -1)
						throw new IllegalArgumentException("No internal ID in selection delta");

					ArrayList<SelectedElementRep> alRep = createElementRep(idType, id);
					if (alRep == null) {
						continue;
					}
					for (SelectedElementRep rep : alRep) {
						if (rep == null) {
							continue;
						}

						for (Integer iConnectionID : item.getConnectionIDs()) {
							connectedElementRepresentationManager.addSelection(iConnectionID, rep,
								item.getSelectionType());
						}
					}
				}
			}
		}
		catch (InvalidAttributeValueException e) {
			Logger.log(new Status(IStatus.WARNING, this.toString(),
				"Can not handle data type of update in selectionDelta", e));
		}
	}

	/**
	 * Set whether to use random sampling or not, synchronized
	 * 
	 * @param useRandomSampling
	 */
	public final void useRandomSampling(boolean useRandomSampling) {
		if (this.useRandomSampling != useRandomSampling) {
			this.useRandomSampling = useRandomSampling;
		}
		initData();
	}

	/**
	 * Set the number of samples which are shown in the view. The distribution is purely random
	 * 
	 * @param numberOfRandomElements
	 *            the number
	 */
	public final void setNumberOfSamplesToShow(int numberOfRandomElements) {
		if (numberOfRandomElements != this.numberOfRandomElements && useRandomSampling) {
			this.numberOfRandomElements = numberOfRandomElements;
			initData();
			return;
		}

		this.numberOfRandomElements = numberOfRandomElements;
	}

	@Override
	public int getNumberOfSelections(SelectionType SelectionType) {
		return recordSelectionManager.getElements(SelectionType).size();
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();

		selectionUpdateListener = new SelectionUpdateListener();
		selectionUpdateListener.setHandler(this);
		eventPublisher.addListener(SelectionUpdateEvent.class, selectionUpdateListener);

		recordVAUpdateListener = new RecordVAUpdateListener();
		recordVAUpdateListener.setHandler(this);
		recordVAUpdateListener.setExclusiveDataDomainID(dataDomain.getDataDomainID());
		eventPublisher.addListener(RecordVAUpdateEvent.class, recordVAUpdateListener);

		dimensionVAUpdateListener = new DimensionVAUpdateListener();
		dimensionVAUpdateListener.setHandler(this);
		dimensionVAUpdateListener.setExclusiveDataDomainID(dataDomain.getDataDomainID());
		eventPublisher.addListener(DimensionVAUpdateEvent.class, dimensionVAUpdateListener);

		selectionCommandListener = new SelectionCommandListener();
		selectionCommandListener.setHandler(this);
		selectionCommandListener.setDataDomainID(dataDomain.getDataDomainID());
		eventPublisher.addListener(SelectionCommandEvent.class, selectionCommandListener);

		redrawViewListener = new RedrawViewListener();
		redrawViewListener.setHandler(this);
		redrawViewListener.setDataDomainID(dataDomain.getDataDomainID());
		eventPublisher.addListener(RedrawViewEvent.class, redrawViewListener);

		clearSelectionsListener = new ClearSelectionsListener();
		clearSelectionsListener.setHandler(this);
		eventPublisher.addListener(ClearSelectionsEvent.class, clearSelectionsListener);

		switchDataRepresentationListener = new SwitchDataRepresentationListener();
		switchDataRepresentationListener.setHandler(this);
		eventPublisher.addListener(SwitchDataRepresentationEvent.class, switchDataRepresentationListener);

	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();

		if (selectionUpdateListener != null) {
			eventPublisher.removeListener(selectionUpdateListener);
			selectionUpdateListener = null;
		}
		if (selectionCommandListener != null) {
			eventPublisher.removeListener(selectionCommandListener);
			selectionCommandListener = null;
		}

		if (redrawViewListener != null) {
			eventPublisher.removeListener(redrawViewListener);
			redrawViewListener = null;
		}
		if (clearSelectionsListener != null) {
			eventPublisher.removeListener(clearSelectionsListener);
			clearSelectionsListener = null;
		}

		if (recordVAUpdateListener != null) {
			eventPublisher.removeListener(recordVAUpdateListener);
			recordVAUpdateListener = null;
		}

		if (dimensionVAUpdateListener != null) {
			eventPublisher.removeListener(dimensionVAUpdateListener);
			dimensionVAUpdateListener = null;
		}

		if (switchDataRepresentationListener != null) {
			eventPublisher.removeListener(switchDataRepresentationListener);
			switchDataRepresentationListener = null;
		}

	}

	@Override
	public void setRecordPerspectiveID(String recordPerspectiveID) {
		this.recordPerspectiveID = recordPerspectiveID;
	}

	@Override
	public void setDimensionPerspectiveID(String dimensionPerspectiveID) {
		this.dimensionPerspectiveID = dimensionPerspectiveID;
	}

	@Override
	public boolean isDataView() {
		return true;
	}

	public void switchDataRepresentation() {
		if (dimensionDataRepresentation.equals(DataRepresentation.NORMALIZED)) {
			if (!table.containsFoldChangeRepresentation())
				table.createFoldChangeRepresentation();
			dimensionDataRepresentation = DataRepresentation.FOLD_CHANGE_NORMALIZED;
		}
		else
			dimensionDataRepresentation = DataRepresentation.NORMALIZED;

		setDisplayListDirty();
	}

	public DataRepresentation getRenderingRepresentation() {
		return dimensionDataRepresentation;
	}

	public void setRecordVA(RecordVirtualArray recordVA) {
		this.recordVA = recordVA;
	}

	public void setDimensionVA(DimensionVirtualArray dimensionVA) {
		this.dimensionVA = dimensionVA;
	}

	public RecordSelectionManager getRecordSelectionManager() {
		return recordSelectionManager;
	}

	public DimensionSelectionManager getDimensionSelectionManager() {
		return dimensionSelectionManager;
	}

	public DataTable getTable() {
		return table;
	}
}
