package org.caleydo.core.view.opengl.canvas;

import java.util.ArrayList;

import javax.management.InvalidAttributeValueException;
import javax.media.opengl.awt.GLCanvas;

import org.caleydo.core.data.collection.dimension.DataRepresentation;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.id.IDCategory;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.selection.DimensionSelectionManager;
import org.caleydo.core.data.selection.RecordSelectionManager;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.DeltaConverter;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDeltaItem;
import org.caleydo.core.data.virtualarray.EVAOperation;
import org.caleydo.core.data.virtualarray.delta.DimensionVADelta;
import org.caleydo.core.data.virtualarray.delta.RecordVADelta;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.ATableBasedDataDomain;
import org.caleydo.core.manager.datadomain.EDataFilterLevel;
import org.caleydo.core.manager.datadomain.IDataDomain;
import org.caleydo.core.manager.event.data.ReplaceDimensionVAEvent;
import org.caleydo.core.manager.event.data.ReplaceRecordVAEvent;
import org.caleydo.core.manager.event.view.ClearSelectionsEvent;
import org.caleydo.core.manager.event.view.DataDomainsChangedEvent;
import org.caleydo.core.manager.event.view.SelectionCommandEvent;
import org.caleydo.core.manager.event.view.SwitchDataRepresentationEvent;
import org.caleydo.core.manager.event.view.tablebased.DimensionVAUpdateEvent;
import org.caleydo.core.manager.event.view.tablebased.RecordVAUpdateEvent;
import org.caleydo.core.manager.event.view.tablebased.RedrawViewEvent;
import org.caleydo.core.manager.event.view.tablebased.SelectionUpdateEvent;
import org.caleydo.core.manager.view.ConnectedElementRepresentationManager;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.IDataDomainSetBasedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.listener.ClearSelectionsListener;
import org.caleydo.core.view.opengl.canvas.listener.DimensionVAUpdateListener;
import org.caleydo.core.view.opengl.canvas.listener.IDimensionVAUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.IRecordVAUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.RecordVAUpdateListener;
import org.caleydo.core.view.opengl.canvas.listener.RedrawViewListener;
import org.caleydo.core.view.opengl.canvas.listener.ReplaceDimensionVAListener;
import org.caleydo.core.view.opengl.canvas.listener.ReplaceRecordVAListener;
import org.caleydo.core.view.opengl.canvas.listener.SelectionCommandListener;
import org.caleydo.core.view.opengl.canvas.listener.SelectionUpdateListener;
import org.caleydo.core.view.opengl.canvas.listener.SwitchDataRepresentationListener;
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
	implements IDataDomainSetBasedView, ISelectionUpdateHandler, IRecordVAUpdateHandler,
	IDimensionVAUpdateHandler, ISelectionCommandHandler, IViewCommandHandler {

	protected DataTable table;

	protected ATableBasedDataDomain dataDomain;

	// protected ArrayList<Boolean> alUseInRandomSampling;

	protected ConnectedElementRepresentationManager connectedElementRepresentationManager;

	/**
	 * This manager is responsible for the content in the dimensions (the indices). The contentSelectionManager
	 * is initialized when the useCase is set ({@link #setDataDomain(IDataDomain)}).
	 */
	protected RecordSelectionManager recordSelectionManager;

	/**
	 * This manager is responsible for the management of the dimensions in the table. The dimensionSelectionManager
	 * is initialized when the useCase is set ( {@link #setDataDomain(IDataDomain)}).
	 */
	protected DimensionSelectionManager dimensionSelectionManager;

	/**
	 * flag whether the whole data or the selection should be rendered
	 */
	protected boolean bRenderOnlyContext;

	/**
	 * Define what level of filtering on the data should be applied
	 */
	protected EDataFilterLevel dataFilterLevel = EDataFilterLevel.ONLY_CONTEXT;

	protected boolean bUseRandomSampling = true;

	protected int iNumberOfRandomElements = 100;

	protected int iNumberOfSamplesPerTexture = 100;

	protected int iNumberOfSamplesPerHeatmap = 100;

	protected SelectionUpdateListener selectionUpdateListener;
	protected SelectionCommandListener selectionCommandListener;

	protected RedrawViewListener redrawViewListener;
	protected ClearSelectionsListener clearSelectionsListener;

	protected RecordVAUpdateListener recordVAUpdateListener;
	protected DimensionVAUpdateListener dimensionVAUpdateListener;
	protected ReplaceRecordVAListener replaceRecordVAListener;
	protected ReplaceDimensionVAListener replaceDimensionVAListener;

	protected SwitchDataRepresentationListener switchDataRepresentationListener;

	protected IDType recordIDType;
	protected IDType dimensionIDType;

	protected DataRepresentation renderingRepresentation = DataRepresentation.NORMALIZED;

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
		this.dataDomain = (ATableBasedDataDomain) dataDomain;

		recordSelectionManager = this.dataDomain.getRecordSelectionManager();
		dimensionSelectionManager = this.dataDomain.getDimensionSelectionManager();

		recordIDType = dataDomain.getRecordIDType();
		dimensionIDType = dataDomain.getDimensionIDType();

		initData();

		DataDomainsChangedEvent event = new DataDomainsChangedEvent(this);
		event.setSender(this);
		GeneralManager.get().getEventPublisher().triggerEvent(event);
	}

	@Override
	public ATableBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	/**
	 * Toggle whether to render the complete dataset (with regards to the filters though) or only contextual
	 * data This effectively means switching between the {@link VAType#RECORD} and
	 * {@link VAType#RECORD_CONTEXT}
	 */
	public abstract void renderContext(boolean bRenderContext);

	/**
	 * Check whether only context is beeing rendered
	 * 
	 * @return
	 */
	public boolean isRenderingOnlyContext() {
		return bRenderOnlyContext;
	}

	@Override
	public void initData() {
		if (table == null)
			table = dataDomain.getTable();

		super.initData();

		bRenderOnlyContext =
			(glRemoteRenderingView != null && glRemoteRenderingView.getViewType().equals(
				"org.caleydo.view.bucket"));

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
	public void handleSelectionUpdate(ISelectionDelta selectionDelta, boolean scrollToSelection, String info) {

		if (selectionDelta.getIDType().getIDCategory().equals(recordIDType.getIDCategory())) {
			// Check for type that can be handled
			if (selectionDelta.getIDType() != recordIDType) {
				selectionDelta = DeltaConverter.convertDelta(recordIDType, selectionDelta);
			}

			recordSelectionManager.setDelta(selectionDelta);
			// ISelectionDelta internalDelta = contentSelectionManager.getCompleteDelta();
			initForAddedElements();
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

		// else if (selectionDelta.getIDType() == EIDType.EXPERIMENT_INDEX
		// && dataDomain.getDataDomainType().equals("org.caleydo.datadomain.clinical")) {
		//
		// contentSelectionManager.setDelta(selectionDelta);
		// //
		// handleConnectedElementRep(selectionDelta);
		// reactOnExternalSelection(scrollToSelection);
		// setDisplayListDirty();
		// }
		//
		// // FIXME: this is not nice since we use expression index for unspecified
		// // data
		// else if (selectionDelta.getIDType() == EIDType.EXPRESSION_INDEX
		// && dataDomain.getDataDomainType().equals("org.caleydo.datadomain.generic")) {
		//
		// contentSelectionManager.setDelta(selectionDelta);
		// // handleConnectedElementRep(contentSelectionManager.getCompleteDelta());
		// handleConnectedElementRep(selectionDelta);
		// reactOnExternalSelection(scrollToSelection);
		// setDisplayListDirty();
		// }
		//
		// else if (selectionDelta.getIDType() == EIDType.EXPERIMENT_INDEX
		// && dataDomain.getDataDomainType().equals("org.caleydo.datadomain.generic")) {
		//
		// dimensionSelectionManager.setDelta(selectionDelta);
		// // handleConnectedElementRep(contentSelectionManager.getCompleteDelta());
		// handleConnectedElementRep(selectionDelta);
		// reactOnExternalSelection(scrollToSelection);
		// setDisplayListDirty();
		// }

	}

	@Override
	public void handleVAUpdate(RecordVADelta delta, String info) {
		if (!delta.getVAType().equals(recordVAType))
			return;

		recordVA.setGroupList(null);
		recordSelectionManager.setVADelta(delta);

		reactOnRecordVAChanges(delta);

		// reactOnExternalSelection();
		setDisplayListDirty();
	}

	@Override
	public void handleVAUpdate(DimensionVADelta delta, String info) {
		dimensionVA.setGroupList(null);
		// reactOnDimensionVAChanges(delta);
		dimensionSelectionManager.setVADelta(delta);
		setDisplayListDirty();
	}

	/**
	 * Is called any time a update is triggered externally. Should be implemented by inheriting views.
	 */
	protected void reactOnExternalSelection(ISelectionDelta selectionDelta, boolean scrollToSelection) {

	}

	/**
	 * Is called any time a virtual array is changed. Can be implemented by inheriting views if some action is
	 * necessary
	 * 
	 * @param delta
	 */
	protected void reactOnRecordVAChanges(RecordVADelta delta) {

	}

	/**
	 * This method is called when new elements are added from external - if you need to react to it do it
	 * here, if not don't do anything.
	 */
	protected void initForAddedElements() {
	}

	@Override
	public void clearAllSelections() {
		connectedElementRepresentationManager.clear(recordIDType);
		connectedElementRepresentationManager.clear(dimensionIDType);
		recordSelectionManager.clearSelections();
		dimensionSelectionManager.clearSelections();

		setDisplayListDirty();
	}

	// @Override
	// public void triggerEvent(EMediatorType eMediatorType, IEventContainer
	// eventContainer) {
	// generalManager.getEventPublisher().triggerEvent(eMediatorType, this,
	// eventContainer);
	// }

	// @Override
	// public void handleExternalEvent(IMediatorSender eventTrigger,
	// IEventContainer eventContainer,
	// EMediatorType eMediatorType) {
	// switch (eventContainer.getEventType()) {
	// case VIEW_COMMAND:
	// ViewCommandEventContainer viewCommandEventContainer =
	// (ViewCommandEventContainer) eventContainer;
	//
	// if (viewCommandEventContainer.getViewCommand() == EViewCommand.REDRAW) {
	// setDisplayListDirty();
	// }
	// else if (viewCommandEventContainer.getViewCommand() ==
	// EViewCommand.CLEAR_SELECTIONS) {
	// clearAllSelections();
	// setDisplayListDirty();
	// }
	// break;
	// }
	// }

	@Override
	public void handleRedrawView() {
		setDisplayListDirty();
	}

	@Override
	public void handleClearSelections() {
		clearAllSelections();
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
	protected void handleConnectedElementReps(ISelectionDelta selectionDelta) {
		try {
			int id = -1;

			int iID = -1;
			IDType idType;

			if (selectionDelta.size() > 0) {
				for (SelectionDeltaItem item : selectionDelta) {
					if (!connectedElementRepresentationManager.isSelectionTypeRenderedWithVisuaLinks(item
						.getSelectionType()) || item.isRemove())
						continue;
					if (selectionDelta.getIDType() == recordIDType) {
						id = item.getPrimaryID();

						iID = item.getSecondaryID();
						idType = recordIDType;

					}
					else if (selectionDelta.getIDType() == dimensionIDType) {
						iID = item.getPrimaryID();
						id = iID;
						idType = dimensionIDType;
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
	 * Broadcast all elements independent of their type.
	 */
	// public abstract void broadcastElements();
	@Override
	public void broadcastElements(EVAOperation type) {
		// nothing to do
	}

	/**
	 * Set whether to use random sampling or not, synchronized
	 * 
	 * @param bUseRandomSampling
	 */
	public final void useRandomSampling(boolean bUseRandomSampling) {
		if (this.bUseRandomSampling != bUseRandomSampling) {
			this.bUseRandomSampling = bUseRandomSampling;
		}
		// TODO, probably do this with initCompleteList, take care of selection
		// manager though
		initData();
	}

	/**
	 * Set the number of samples which are shown in the view. The distribution is purely random
	 * 
	 * @param iNumberOfRandomElements
	 *            the number
	 */
	public final void setNumberOfSamplesToShow(int iNumberOfRandomElements) {
		if (iNumberOfRandomElements != this.iNumberOfRandomElements && bUseRandomSampling) {
			this.iNumberOfRandomElements = iNumberOfRandomElements;
			initData();
			return;
		}
		// TODO, probably do this with initCompleteList, take care of selection
		// manager though
		this.iNumberOfRandomElements = iNumberOfRandomElements;
	}

	// public abstract void resetSelections();

	@Override
	public int getNumberOfSelections(SelectionType SelectionType) {
		return recordSelectionManager.getElements(SelectionType).size();
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();

		selectionUpdateListener = new SelectionUpdateListener();
		selectionUpdateListener.setHandler(this);
		selectionUpdateListener.setExclusiveDataDomainType(dataDomain.getDataDomainID());
		eventPublisher.addListener(SelectionUpdateEvent.class, selectionUpdateListener);

		recordVAUpdateListener = new RecordVAUpdateListener();
		recordVAUpdateListener.setHandler(this);
		recordVAUpdateListener.setExclusiveDataDomainType(dataDomain.getDataDomainID());
		eventPublisher.addListener(RecordVAUpdateEvent.class, recordVAUpdateListener);

		replaceRecordVAListener = new ReplaceRecordVAListener();
		replaceRecordVAListener.setHandler(this);
		replaceRecordVAListener.setExclusiveDataDomainType(dataDomain.getDataDomainID());
		eventPublisher.addListener(ReplaceRecordVAEvent.class, replaceRecordVAListener);

		dimensionVAUpdateListener = new DimensionVAUpdateListener();
		dimensionVAUpdateListener.setHandler(this);
		dimensionVAUpdateListener.setExclusiveDataDomainType(dataDomain.getDataDomainID());
		eventPublisher.addListener(DimensionVAUpdateEvent.class, dimensionVAUpdateListener);

		replaceDimensionVAListener = new ReplaceDimensionVAListener();
		replaceDimensionVAListener.setHandler(this);
		replaceDimensionVAListener.setExclusiveDataDomainType(dataDomain.getDataDomainID());
		eventPublisher.addListener(ReplaceDimensionVAEvent.class, replaceDimensionVAListener);

		selectionCommandListener = new SelectionCommandListener();
		selectionCommandListener.setHandler(this);
		selectionCommandListener.setExclusiveDataDomainType(dataDomain.getDataDomainID());
		eventPublisher.addListener(SelectionCommandEvent.class, selectionCommandListener);

		redrawViewListener = new RedrawViewListener();
		redrawViewListener.setHandler(this);
		redrawViewListener.setDataDomainType(dataDomain.getDataDomainID());
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
		if (replaceRecordVAListener != null) {
			eventPublisher.removeListener(replaceRecordVAListener);
			replaceRecordVAListener = null;
		}

		if (replaceDimensionVAListener != null) {
			eventPublisher.removeListener(replaceDimensionVAListener);
			replaceDimensionVAListener = null;
		}

		if (switchDataRepresentationListener != null) {
			eventPublisher.removeListener(switchDataRepresentationListener);
			switchDataRepresentationListener = null;
		}

	}

	@Override
	public void replaceRecordVA(int tableID, String dataDomainType, String vaType) {

		if (table.getID() != tableID || this.recordVAType != vaType)
			return;

		recordVA = table.getRecordData(vaType).getRecordVA();
		recordSelectionManager.setVA(recordVA);

		initData();
	}

	@Override
	public void replaceDimensionVA(String dataDomain, String vaType) {
		if (vaType != dimensionVAType)
			return;

		dimensionVA = table.getDimensionData(vaType).getDimensionVA();

		initData();
	}

	/**
	 * Manually set the vaType if you want to override the automatic setting triggeret in
	 * {@link #init(javax.media.opengl.GL)}
	 * 
	 * @param vaType
	 */
	public void setRecordVAType(String vaType) {
		this.recordVAType = vaType;
	}

	/**
	 * Manually set the vaType if you want to override the automatic setting triggeret in
	 * {@link #init(javax.media.opengl.GL)}
	 * 
	 * @param vaType
	 */
	public void setDimensionVAType(String vaType) {
		this.dimensionVAType = vaType;
	}

	@Override
	public boolean isDataView() {
		return true;
	}

	public void switchDataRepresentation() {
		if (renderingRepresentation.equals(DataRepresentation.NORMALIZED)) {
			if (!table.containsFoldChangeRepresentation())
				table.createFoldChangeRepresentation();
			renderingRepresentation = DataRepresentation.FOLD_CHANGE_NORMALIZED;
		}
		else
			renderingRepresentation = DataRepresentation.NORMALIZED;
	}

	public DataRepresentation getRenderingRepresentation() {
		return renderingRepresentation;
	}

}
