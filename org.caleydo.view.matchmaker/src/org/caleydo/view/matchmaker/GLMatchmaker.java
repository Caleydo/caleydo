package org.caleydo.view.matchmaker;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.awt.GLCanvas;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.IDataDomainBasedView;
import org.caleydo.core.data.id.IDCategory;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.SelectionTypeEvent;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.virtualarray.EVAOperation;
import org.caleydo.core.data.virtualarray.events.IRecordVAUpdateHandler;
import org.caleydo.core.data.virtualarray.events.ReplaceRecordPerspectiveListener;
import org.caleydo.core.data.virtualarray.group.RecordGroupList;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.view.ClearSelectionsEvent;
import org.caleydo.core.manager.event.view.SelectionCommandEvent;
import org.caleydo.core.manager.event.view.grouper.CompareGroupsEvent;
import org.caleydo.core.manager.event.view.matchmaker.AdjustPValueEvent;
import org.caleydo.core.manager.event.view.matchmaker.CreateSelectionTypesEvent;
import org.caleydo.core.manager.event.view.matchmaker.DuplicateTableBarItemEvent;
import org.caleydo.core.manager.event.view.tablebased.HideHeatMapElementsEvent;
import org.caleydo.core.manager.event.view.tablebased.NewRecordGroupInfoEvent;
import org.caleydo.core.manager.event.view.tablebased.SelectionUpdateEvent;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.DetailLevel;
import org.caleydo.core.view.opengl.canvas.listener.ClearSelectionsListener;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.SelectionCommandListener;
import org.caleydo.core.view.opengl.canvas.listener.SelectionUpdateListener;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.matchmaker.event.UseBandBundlingEvent;
import org.caleydo.view.matchmaker.event.UseSortingEvent;
import org.caleydo.view.matchmaker.event.UseZoomEvent;
import org.caleydo.view.matchmaker.listener.AdjustPValueOfSetEventListener;
import org.caleydo.view.matchmaker.listener.CompareGroupsEventListener;
import org.caleydo.view.matchmaker.listener.CreateSelectionTypesListener;
import org.caleydo.view.matchmaker.listener.DuplicateTableBarItemEventListener;
import org.caleydo.view.matchmaker.listener.HideHeatMapElementsEventListener;
import org.caleydo.view.matchmaker.listener.NewContentGroupInfoEventListener;
import org.caleydo.view.matchmaker.listener.UseBandBundlingListener;
import org.caleydo.view.matchmaker.listener.UseSortingListener;
import org.caleydo.view.matchmaker.listener.UseZoomListener;
import org.caleydo.view.matchmaker.state.ACompareViewState;
import org.caleydo.view.matchmaker.state.CompareViewStateController;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;

/**
 * The matchmaker view for comparing clustered data sets.
 * 
 * @author Christian Partl
 * @author Alexander Lex
 * @author Marc Streit
 */
public class GLMatchmaker extends AGLView implements IViewCommandHandler,
		IGLRemoteRenderingView, ISelectionUpdateHandler, ISelectionCommandHandler,
		IRecordVAUpdateHandler, IDataDomainBasedView<ATableBasedDataDomain> {

	public final static String VIEW_TYPE = "org.caleydo.view.matchmaker";

	private CompareViewStateController compareViewStateController;

	private CompareGroupsEventListener compareGroupsEventListener;
	private DuplicateTableBarItemEventListener duplicateSetBarItemEventListener;
	private SelectionUpdateListener selectionUpdateListener;
	private AdjustPValueOfSetEventListener adjustPValueOfSetEventListener;
	private SelectionCommandListener selectionCommandListener;
	private CompareMouseWheelListener compareMouseWheelListener;
	private ReplaceRecordPerspectiveListener replaceRecordVAListener;
	private UseSortingListener useSortingListener;
	private UseZoomListener useZoomListener;
	private UseBandBundlingListener useBandBundlingListener;
	private NewContentGroupInfoEventListener newContentGroupInfoEventListener;
	private CreateSelectionTypesListener createSelectionTypesListener;
	private ClearSelectionsListener clearSelectionsListener;
	private HideHeatMapElementsEventListener hideHeatMapElementsEventListener;

	private boolean isControlPressed;
	private boolean wasMouseWheeled;

	private int wheelAmount;
	private Point wheelPoint;

	private ArrayList<DataTable> setsToCompare;
	private ArrayList<Integer> clusteredSets;

	protected ATableBasedDataDomain dataDomain;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param label
	 * @param viewFrustum
	 */
	public GLMatchmaker(GLCanvas glCanvas, Composite parentComposite,
			ViewFrustum viewFrustum) {

		super(glCanvas, parentComposite, viewFrustum);

		viewType = VIEW_TYPE;

		glKeyListener = new GLMatchmakerKeyListener(this);
		isControlPressed = false;
		compareMouseWheelListener = new CompareMouseWheelListener(this);

		// Unregister standard mouse wheel listener
		parentGLCanvas.removeMouseWheelListener(glMouseListener);
		// Register specialized compare mouse wheel listener
		parentGLCanvas.addMouseWheelListener(compareMouseWheelListener);

		SelectionTypeEvent event = new SelectionTypeEvent(GLHeatMap.SELECTION_HIDDEN);
		GeneralManager.get().getEventPublisher().triggerEvent(event);

		SelectionTypeEvent selectionTypeEvent = new SelectionTypeEvent(
				ACompareViewState.ACTIVE_HEATMAP_SELECTION_TYPE);
		eventPublisher.triggerEvent(selectionTypeEvent);

		clusteredSets = new ArrayList<Integer>();
		setsToCompare = new ArrayList<DataTable>();

	}

	@Override
	public void init(GL2 gl) {
		// recordVA = useCase.getRecordVA(RecordVAType.CONTENT);
		// dimensionVA = useCase.getDimensionVA(DimensionVAType.STORAGE);

		textRenderer = new CaleydoTextRenderer(24);

		compareViewStateController = new CompareViewStateController(this, uniqueID,
				textRenderer, textureManager, pickingManager, glMouseListener, dataDomain);

		compareViewStateController.init(gl);

		parentComposite.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				KeyListener listener = new org.eclipse.swt.events.KeyAdapter() {

					@Override
					public void keyPressed(KeyEvent e) {
						if (e.keyCode == SWT.CTRL) {
							eventPublisher.triggerEvent(new ClearSelectionsEvent());
						}
					}
				};
				parentComposite.addKeyListener(listener);
			}
		});

	}

	@Override
	public void initLocal(GL2 gl) {

		iGLDisplayListIndexLocal = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		// Register keyboard listener to GL2 canvas
		parentComposite.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				parentComposite.addKeyListener(glKeyListener);
			}
		});

		init(gl);
	}

	@Override
	public void initRemote(final GL2 gl, final AGLView glParentView,
			final GLMouseListener glMouseListener) {

		// Register keyboard listener to GL2 canvas
		glParentView.getParentComposite().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				glParentView.getParentComposite().addKeyListener(glKeyListener);
			}
		});

		this.glMouseListener = glMouseListener;

		iGLDisplayListIndexRemote = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexRemote;
		init(gl);
	}

	@Override
	public void initData() {
		super.initData();
	}

	@Override
	public void setDetailLevel(DetailLevel detailLevel) {

	}

	@Override
	public void displayLocal(GL2 gl) {

		if (wasMouseWheeled) {
			wasMouseWheeled = false;
			compareViewStateController.handleMouseWheel(gl, wheelAmount, wheelPoint);
		}

		compareViewStateController.executeDrawingPreprocessing(gl,
				bIsDisplayListDirtyLocal);

		pickingManager.handlePicking(this, gl);

		// if (bIsDisplayListDirtyLocal) {
		// bIsDisplayListDirtyLocal = false;
		// buildDisplayList(gl, iGLDisplayListIndexLocal);
		// }
		// iGLDisplayListToCall = iGLDisplayListIndexLocal;

		display(gl);

		// checkForHits(gl);
	}

	@Override
	public void displayRemote(GL2 gl) {
		// if (bIsDisplayListDirtyRemote) {
		// bIsDisplayListDirtyRemote = false;
		// buildDisplayList(gl, iGLDisplayListIndexRemote);
		// }
		// iGLDisplayListToCall = iGLDisplayListIndexRemote;

		throw new IllegalStateException("not in use");
		// display(gl);

	}

	@Override
	public void display(GL2 gl) {
		// processEvents();

		compareViewStateController.drawActiveElements(gl);

		// if (bIsDisplayListDirtyLocal) {
		bIsDisplayListDirtyLocal = false;
		buildDisplayList(gl, iGLDisplayListIndexLocal);
		iGLDisplayListToCall = iGLDisplayListIndexLocal;
		// }

		gl.glCallList(iGLDisplayListToCall);

		compareViewStateController.handleDragging(gl);

		checkForHits(gl);
	}

	private void buildDisplayList(final GL2 gl, int iGLDisplayListIndex) {
		// gl.glNewList(iGLDisplayListIndex, GL2.GL_COMPILE);

		compareViewStateController.drawDisplayListElements(gl);

		// gl.glEndList();
	}

	@Override
	public String getDetailedInfo() {
		return new String("");
	}

	@Override
	protected void handlePickingEvents(PickingType pickingType, PickingMode pickingMode,
			int externalID, Pick pick) {
		if (detailLevel == DetailLevel.VERY_LOW) {
			return;
		}
		compareViewStateController.handlePickingEvents(pickingType, pickingMode,
				externalID, pick, isControlPressed);
	}

	@Override
	public void broadcastElements(EVAOperation type) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getNumberOfSelections(SelectionType selectionType) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getShortInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearAllSelections() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleRedrawView() {
		setDisplayListDirty();
	}

	@Override
	public void handleUpdateView() {
		setDisplayListDirty();
	}

	@Override
	public void handleClearSelections() {

		compareViewStateController.handleClearSelections();
		setDisplayListDirty();
	}

	@Override
	public ASerializedView getSerializableRepresentation() {

		SerializedMatchmakerView serializedForm = new SerializedMatchmakerView(
				dataDomain.getDataDomainID());

		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();
		compareGroupsEventListener = new CompareGroupsEventListener();
		compareGroupsEventListener.setHandler(this);
		eventPublisher.addListener(CompareGroupsEvent.class, compareGroupsEventListener);

		duplicateSetBarItemEventListener = new DuplicateTableBarItemEventListener();
		duplicateSetBarItemEventListener.setHandler(this);
		eventPublisher.addListener(DuplicateTableBarItemEvent.class,
				duplicateSetBarItemEventListener);

		selectionUpdateListener = new SelectionUpdateListener();
		selectionUpdateListener.setHandler(this);
		eventPublisher.addListener(SelectionUpdateEvent.class, selectionUpdateListener);

		adjustPValueOfSetEventListener = new AdjustPValueOfSetEventListener();
		adjustPValueOfSetEventListener.setHandler(this);
		eventPublisher.addListener(AdjustPValueEvent.class,
				adjustPValueOfSetEventListener);

		selectionCommandListener = new SelectionCommandListener();
		selectionCommandListener.setHandler(this);
		eventPublisher.addListener(SelectionCommandEvent.class, selectionCommandListener);

		useSortingListener = new UseSortingListener();
		useSortingListener.setHandler(this);
		eventPublisher.addListener(UseSortingEvent.class, useSortingListener);

		useZoomListener = new UseZoomListener();
		useZoomListener.setHandler(this);
		eventPublisher.addListener(UseZoomEvent.class, useZoomListener);

		useBandBundlingListener = new UseBandBundlingListener();
		useBandBundlingListener.setHandler(this);
		eventPublisher.addListener(UseBandBundlingEvent.class, useBandBundlingListener);

		newContentGroupInfoEventListener = new NewContentGroupInfoEventListener();
		newContentGroupInfoEventListener.setHandler(this);
		eventPublisher.addListener(NewRecordGroupInfoEvent.class,
				newContentGroupInfoEventListener);

		createSelectionTypesListener = new CreateSelectionTypesListener();
		createSelectionTypesListener.setHandler(this);
		eventPublisher.addListener(CreateSelectionTypesEvent.class,
				createSelectionTypesListener);

		clearSelectionsListener = new ClearSelectionsListener();
		clearSelectionsListener.setHandler(this);
		eventPublisher.addListener(ClearSelectionsEvent.class, clearSelectionsListener);

		hideHeatMapElementsEventListener = new HideHeatMapElementsEventListener();
		hideHeatMapElementsEventListener.setHandler(this);
		eventPublisher.addListener(HideHeatMapElementsEvent.class,
				hideHeatMapElementsEventListener);

	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();
		if (compareGroupsEventListener != null) {
			eventPublisher.removeListener(compareGroupsEventListener);
			compareGroupsEventListener = null;
		}
		if (duplicateSetBarItemEventListener != null) {
			eventPublisher.removeListener(duplicateSetBarItemEventListener);
			duplicateSetBarItemEventListener = null;
		}
		if (selectionUpdateListener != null) {
			eventPublisher.removeListener(selectionUpdateListener);
			selectionUpdateListener = null;
		}
		if (adjustPValueOfSetEventListener != null) {
			eventPublisher.removeListener(adjustPValueOfSetEventListener);
			adjustPValueOfSetEventListener = null;
		}

		if (selectionCommandListener != null) {
			eventPublisher.removeListener(selectionCommandListener);
			selectionCommandListener = null;
		}

		if (replaceRecordVAListener != null) {
			eventPublisher.removeListener(replaceRecordVAListener);
			replaceRecordVAListener = null;
		}

		if (useSortingListener != null) {
			eventPublisher.removeListener(useSortingListener);
			useSortingListener = null;
		}

		if (useZoomListener != null) {
			eventPublisher.removeListener(useZoomListener);
			useZoomListener = null;
		}

		if (useBandBundlingListener != null) {
			eventPublisher.removeListener(useBandBundlingListener);
			useBandBundlingListener = null;
		}

		if (newContentGroupInfoEventListener != null) {
			eventPublisher.removeListener(newContentGroupInfoEventListener);
			newContentGroupInfoEventListener = null;
		}

		if (createSelectionTypesListener != null) {
			eventPublisher.removeListener(createSelectionTypesListener);
			createSelectionTypesListener = null;
		}

		if (clearSelectionsListener != null) {
			eventPublisher.removeListener(clearSelectionsListener);
			clearSelectionsListener = null;
		}

		if (hideHeatMapElementsEventListener != null) {
			eventPublisher.removeListener(hideHeatMapElementsEventListener);
			hideHeatMapElementsEventListener = null;
		}
	}

	@Override
	public List<AGLView> getRemoteRenderedViews() {
		// TODO: Do it differently?
		return new ArrayList<AGLView>();
	}

	public void setTablesToCompare(final ArrayList<DataTable> sets) {

		this.setsToCompare = sets;
	}

	public boolean isControlPressed() {
		return isControlPressed;
	}

	public void setControlPressed(boolean isControlPressed) {
		this.isControlPressed = isControlPressed;
	}

	public void handleDuplicateSetBarItem(int itemID) {
		compareViewStateController.duplicateSetBarItem(itemID);
		// setBar.handleDuplicateSetBarItem(itemID);
	}

	public void handleAdjustPValue() {

		compareViewStateController.handleAdjustPValue();
	}

	@Override
	public void handleSelectionUpdate(SelectionDelta selectionDelta,
			boolean scrollToSelection, String info) {
		compareViewStateController.handleSelectionUpdate(selectionDelta,
				scrollToSelection, info);

	}

	@Override
	public void handleSelectionCommand(IDCategory category,
			SelectionCommand selectionCommand) {
		compareViewStateController.handleSelectionCommand(category, selectionCommand);
	}

	public void handleMouseWheel(int wheelAmount, Point wheelPosition) {
		this.wheelAmount = wheelAmount;
		this.wheelPoint = wheelPosition;
		wasMouseWheeled = true;
	}

	// @Override
	// public void handleRecordVADelta(RecordVADelta vaDelta, String info) {
	// System.out.println("COMPARER IGNORES CONTENT VA UPDATE");
	// }

	@Override
	public void handleRecordVAUpdate(int dataTableID, String info) {
		clusteredSets.add(dataTableID);

		// Check if all sets are properly clustered
		// boolean allSetsClustered = true;
		// for (DataTable set : setsToCompare) {
		// if (!clusteredSets.contains(table.getID())) {
		// allSetsClustered = false;
		// break;
		// }
		// }
		// if (!allSetsClustered)
		// return;

		compareViewStateController.setTablesToCompare(setsToCompare);
		compareViewStateController.handleReplaceRecordVA(dataTableID, dataDomain.getDataDomainType(), recordPerspectiveID);
		clusteredSets.clear();

	}

	public void setUseSorting(boolean useSorting) {
		compareViewStateController.setUseSorting(useSorting);
	}

	public void setUseFishEye(boolean useFishEye) {
		compareViewStateController.setUseFishEye(useFishEye);
	}

	public void setUseZoom(boolean useZoom) {
		compareViewStateController.setUseZoom(useZoom);
	}

	public void handleContentGroupListUpdate(String recordVAType, int tableID,
			RecordGroupList contentGroupList) {
		if (this.recordPerspectiveID.equals(recordVAType))
			compareViewStateController.handleContentGroupListUpdate(tableID,
					contentGroupList);
	}

	public void setBandBundling(boolean bandBundlingActive) {
		compareViewStateController.setBandBundling(bandBundlingActive);
	}

	public void setCreateSelctionTypes(boolean createSelectionTypes) {
		compareViewStateController.setCreateSelectionTypes(createSelectionTypes);
	}

	public void setHideHeatMapElements(boolean hideElements) {
		compareViewStateController.setHideHeatMapElements(hideElements);
	}

	@Override
	public ATableBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	@Override
	public void setDataDomain(ATableBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	@Override
	public boolean isDataView() {
		return true;
	}
}
