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
package org.caleydo.core.view.opengl.canvas;

import java.util.ArrayList;
import java.util.List;
import javax.management.InvalidAttributeValueException;
import javax.media.opengl.awt.GLCanvas;
import org.caleydo.core.data.collection.dimension.DataRepresentation;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DefaultDataSupportDefinition;
import org.caleydo.core.data.datadomain.EDataFilterLevel;
import org.caleydo.core.data.datadomain.IDataSupportDefinition;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.selection.DimensionSelectionManager;
import org.caleydo.core.data.selection.ElementConnectionInformation;
import org.caleydo.core.data.selection.RecordSelectionManager;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.delta.DeltaConverter;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDeltaItem;
import org.caleydo.core.data.selection.events.ClearSelectionsListener;
import org.caleydo.core.data.selection.events.ISelectionCommandHandler;
import org.caleydo.core.data.selection.events.ISelectionUpdateHandler;
import org.caleydo.core.data.selection.events.SelectionCommandListener;
import org.caleydo.core.data.selection.events.SelectionUpdateListener;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.events.DimensionVAUpdateEvent;
import org.caleydo.core.data.virtualarray.events.DimensionVAUpdateListener;
import org.caleydo.core.data.virtualarray.events.IDimensionVAUpdateHandler;
import org.caleydo.core.data.virtualarray.events.IRecordVAUpdateHandler;
import org.caleydo.core.data.virtualarray.events.RecordVAUpdateEvent;
import org.caleydo.core.data.virtualarray.events.RecordVAUpdateListener;
import org.caleydo.core.event.data.ClearSelectionsEvent;
import org.caleydo.core.event.data.SelectionCommandEvent;
import org.caleydo.core.event.data.SelectionUpdateEvent;
import org.caleydo.core.event.view.RedrawViewEvent;
import org.caleydo.core.event.view.SwitchDataRepresentationEvent;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.core.serialize.ASerializedSingleTablePerspectiveBasedView;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.ISingleTablePerspectiveBasedView;
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
public abstract class ATableBasedView extends AGLView implements
		ISingleTablePerspectiveBasedView, ISelectionUpdateHandler,
		IRecordVAUpdateHandler, IDimensionVAUpdateHandler, ISelectionCommandHandler,
		IViewCommandHandler {

	protected ATableBasedDataDomain dataDomain;

	protected TablePerspective tablePerspective;

	protected ConnectedElementRepresentationManager connectedElementRepresentationManager;

	/**
	 * This manager is responsible for the selection states of the records.
	 */
	protected RecordSelectionManager recordSelectionManager;

	/**
	 * This manager is responsible for the selection states of the dimensions.
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
	 * Flag that tells the view whether visual linking is used for it's element
	 * so it must create {@link ElementConnectionInformation}
	 */
	private boolean isVisualLinkingActive = false;

	/**
	 * Constructor for dimension based views
	 * 
	 * @param glCanvas
	 * @param viewFrustum
	 * @param viewType
	 *            TODO
	 * @param viewName
	 *            TODO
	 * @param label
	 */
	protected ATableBasedView(GLCanvas glCanvas, Composite parentComposite,
			final ViewFrustum viewFrustum, String viewType, String viewName) {
		super(glCanvas, parentComposite, viewFrustum, viewType, viewName);

		connectedElementRepresentationManager = generalManager.getViewManager()
				.getConnectedElementRepresentationManager();
	}

	@Override
	public void setDataDomain(ATableBasedDataDomain dataDomain) {

		if (this.dataDomain == dataDomain)
			return;

		this.dataDomain = dataDomain;

		setDisplayListDirty();
	}

	@Override
	public ATableBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	/**
	 * @return the tablePerspective, see {@link #tablePerspective}
	 */
	public TablePerspective getTablePerspective() {
		return tablePerspective;
	}

	/**
	 * @param tablePerspective
	 *            setter, see {@link #tablePerspective}
	 */
	@Override
	public void setTablePerspective(TablePerspective tablePerspective) {
		this.tablePerspective = tablePerspective;
	}

	@Override
	public void initialize() {
		super.initialize();
		recordSelectionManager = this.dataDomain.getRecordSelectionManager();
		dimensionSelectionManager = this.dataDomain.getDimensionSelectionManager();

		recordIDType = dataDomain.getRecordIDType();
		dimensionIDType = dataDomain.getDimensionIDType();

		initData();
	}

	@Override
	public void initData() {
		super.initData();
	}

	@Override
	public void initFromSerializableRepresentation(ASerializedView serialzedView) {
		if (serialzedView instanceof ASerializedSingleTablePerspectiveBasedView) {
			ASerializedSingleTablePerspectiveBasedView topSerializedView = (ASerializedSingleTablePerspectiveBasedView) serialzedView;
			tablePerspective = dataDomain.getTablePerspective(topSerializedView
					.getTablePerspectiveKey());
		}
	}

	/**
	 * @param isVisualLinkingActive
	 *            setter, see {@link #isVisualLinkingActive}
	 */
	public void setVisualLinkingActive(boolean isVisualLinkingActive) {
		this.isVisualLinkingActive = isVisualLinkingActive;
	}

	/**
	 * Create {@link ElementConnectionInformation}, which basically describes
	 * anchor points for visual links for the element specified through the id
	 * 
	 * @param iDType
	 *            the type of the id - for possible conversion
	 * @param the
	 *            id of the element
	 * @throws InvalidAttributeValueException
	 *             when the selectionDelta does not contain a valid type for
	 *             this view
	 */
	protected abstract ArrayList<ElementConnectionInformation> createElementConnectionInformation(
			IDType idType, int id) throws InvalidAttributeValueException;

	@Override
	public void handleSelectionUpdate(SelectionDelta selectionDelta) {

		if (selectionDelta.getIDType().getIDCategory()
				.equals(recordIDType.getIDCategory())) {
			// Check for type that can be handled
			if (selectionDelta.getIDType() != recordIDType) {
				selectionDelta = DeltaConverter.convertDelta(
						dataDomain.getRecordIDMappingManager(), recordIDType,
						selectionDelta);
			}

			recordSelectionManager.setDelta(selectionDelta);
			if (isVisualLinkingActive)
				prepareVisualLinkingInformation(selectionDelta);
			reactOnExternalSelection(selectionDelta);
			setDisplayListDirty();
		} else if (selectionDelta.getIDType() == dimensionIDType) {

			dimensionSelectionManager.setDelta(selectionDelta);
			if (isVisualLinkingActive)
				prepareVisualLinkingInformation(selectionDelta);
			reactOnExternalSelection(selectionDelta);
			setDisplayListDirty();
		}

	}

	@Override
	public void handleRecordVAUpdate(String recordPerspectiveID) {
		if (tablePerspective != null
				&& tablePerspective.hasRecordPerspective(recordPerspectiveID)) {

			reactOnRecordVAChanges();
			// reactOnExternalSelection();
			setDisplayListDirty();
		}
	}

	@Override
	public void handleDimensionVAUpdate(String dimensionPerspectiveID) {
		if (tablePerspective.hasDimensionPerspective(dimensionPerspectiveID)) {
			setDisplayListDirty();
		}
	}

	/**
	 * Is called any time a update is triggered externally. Should be
	 * implemented by inheriting views.
	 */
	protected void reactOnExternalSelection(SelectionDelta selectionDelta) {

	}

	/**
	 * Is called any time a virtual array is changed. Can be implemented by
	 * inheriting views if some action is necessary
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
	public void handleSelectionCommand(IDCategory idCategory,
			SelectionCommand selectionCommand) {
		if (idCategory == dataDomain.getRecordIDCategory())
			recordSelectionManager.executeSelectionCommand(selectionCommand);
		else if (idCategory == dataDomain.getDimensionIDCategory())
			dimensionSelectionManager.executeSelectionCommand(selectionCommand);
		else
			return;
		setDisplayListDirty();
	}

	/**
	 * Handles the creation of {@link ElementConnectionInformation} according to
	 * the data in a selectionDelta
	 * 
	 * @param selectionDelta
	 *            the selection data that should be handled
	 */
	protected void prepareVisualLinkingInformation(SelectionDelta selectionDelta) {
		try {
			int id = -1;

			IDType idType;

			if (selectionDelta.size() > 0) {
				for (SelectionDeltaItem item : selectionDelta) {
					if (!connectedElementRepresentationManager
							.isSelectionTypeRenderedWithVisuaLinks(item
									.getSelectionType())
							|| item.isRemove())
						continue;
					if (selectionDelta.getIDType() == recordIDType) {
						id = item.getID();
						idType = recordIDType;
						if (tablePerspective == null) {
							Logger.log(new Status(Status.ERROR, this.toString(),
									"tablePerspective was null"));
							return;
						}
						if (!tablePerspective.getRecordPerspective().getVirtualArray()
								.contains(id))
							return;

					} else if (selectionDelta.getIDType() == dimensionIDType) {
						id = item.getID();
						idType = dimensionIDType;
						if (!tablePerspective.getDimensionPerspective().getVirtualArray()
								.contains(id))
							return;
					} else
						throw new InvalidAttributeValueException(
								"Can not handle data type: " + selectionDelta.getIDType());

					if (id == -1)
						throw new IllegalArgumentException(
								"No internal ID in selection delta");

					ArrayList<ElementConnectionInformation> alRep = createElementConnectionInformation(
							idType, id);
					if (alRep == null) {
						continue;
					}
					for (ElementConnectionInformation rep : alRep) {
						if (rep == null) {
							continue;
						}

						for (Integer iConnectionID : item.getConnectionIDs()) {
							connectedElementRepresentationManager.addSelection(
									iConnectionID, rep, item.getSelectionType());
						}
					}
				}
			}
		} catch (InvalidAttributeValueException e) {
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
	 * Set the number of samples which are shown in the view. The distribution
	 * is purely random
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
		eventPublisher.addListener(DimensionVAUpdateEvent.class,
				dimensionVAUpdateListener);

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
		eventPublisher.addListener(SwitchDataRepresentationEvent.class,
				switchDataRepresentationListener);

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
	public boolean isDataView() {
		return true;
	}

	public void switchDataRepresentation() {
		if (dimensionDataRepresentation.equals(DataRepresentation.NORMALIZED)) {
			if (!dataDomain.getTable().containsFoldChangeRepresentation())
				dataDomain.getTable().createFoldChangeRepresentation();
			dimensionDataRepresentation = DataRepresentation.FOLD_CHANGE_NORMALIZED;
		} else
			dimensionDataRepresentation = DataRepresentation.NORMALIZED;

		setDisplayListDirty();
	}

	public DataRepresentation getRenderingRepresentation() {
		return dimensionDataRepresentation;
	}

	public RecordSelectionManager getRecordSelectionManager() {
		return recordSelectionManager;
	}

	public DimensionSelectionManager getDimensionSelectionManager() {
		return dimensionSelectionManager;
	}

	@Override
	public List<TablePerspective> getTablePerspectives() {
		ArrayList<TablePerspective> tablePerspectives = new ArrayList<TablePerspective>();
		tablePerspectives.add(tablePerspective);
		return tablePerspectives;
	}

	/**
	 * Calculates how many pixels are required per element for a given detail
	 * level.
	 * 
	 * @param forRecord
	 *            flag telling whether this is for the records (true) or for
	 *            dimensions (false)
	 * @param detailLevel
	 *            the detail level for which this should be calcualted
	 * @param pixelPerElementMedium
	 *            the number of pixels this view requires in medium mode for a
	 *            single element
	 * @param pixelPerElementHigh
	 *            same as previous for high mode
	 * @return the number of pixels for the detail level
	 */
	protected int getPixelPerElement(boolean forRecord, EDetailLevel detailLevel,
			int pixelPerElementMedium, int pixelPerElementHigh) {
		int pixelPerElement = 0;
		switch (detailLevel) {
		case HIGH:
			pixelPerElement = pixelPerElementMedium;
			break;
		case MEDIUM:
			pixelPerElement = pixelPerElementHigh;
			break;
		case LOW:
		default:
			return 1;
		}
		VirtualArray<?, ?, ?> va;
		if (forRecord)
			va = tablePerspective.getRecordPerspective().getVirtualArray();
		else
			va = tablePerspective.getDimensionPerspective().getVirtualArray();

		int pixelHeight = va.size() * pixelPerElement;
		return pixelHeight;
	}

	@Override
	public IDataSupportDefinition getDataSupportDefinition() {
		return new DefaultDataSupportDefinition();
	}

}
