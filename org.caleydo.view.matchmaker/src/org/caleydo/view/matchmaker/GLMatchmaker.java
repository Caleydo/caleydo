package org.caleydo.view.matchmaker;

import java.awt.Font;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.selection.ContentGroupList;
import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.SelectionTypeEvent;
import org.caleydo.core.data.selection.delta.ContentVADelta;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.manager.event.data.ReplaceContentVAEvent;
import org.caleydo.core.manager.event.view.ClearSelectionsEvent;
import org.caleydo.core.manager.event.view.SelectionCommandEvent;
import org.caleydo.core.manager.event.view.grouper.CompareGroupsEvent;
import org.caleydo.core.manager.event.view.matchmaker.AdjustPValueEvent;
import org.caleydo.core.manager.event.view.matchmaker.CreateSelectionTypesEvent;
import org.caleydo.core.manager.event.view.matchmaker.DuplicateSetBarItemEvent;
import org.caleydo.core.manager.event.view.storagebased.HideHeatMapElementsEvent;
import org.caleydo.core.manager.event.view.storagebased.NewContentGroupInfoEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.listener.ClearSelectionsListener;
import org.caleydo.core.view.opengl.canvas.listener.IContentVAUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.ReplaceContentVAListener;
import org.caleydo.core.view.opengl.canvas.listener.SelectionCommandListener;
import org.caleydo.core.view.opengl.canvas.listener.SelectionUpdateListener;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.matchmaker.event.UseBandBundlingEvent;
import org.caleydo.view.matchmaker.event.UseSortingEvent;
import org.caleydo.view.matchmaker.event.UseZoomEvent;
import org.caleydo.view.matchmaker.listener.AdjustPValueOfSetEventListener;
import org.caleydo.view.matchmaker.listener.CompareGroupsEventListener;
import org.caleydo.view.matchmaker.listener.CreateSelectionTypesListener;
import org.caleydo.view.matchmaker.listener.DuplicateSetBarItemEventListener;
import org.caleydo.view.matchmaker.listener.HideHeatMapElementsEventListener;
import org.caleydo.view.matchmaker.listener.NewContentGroupInfoEventListener;
import org.caleydo.view.matchmaker.listener.UseBandBundlingListener;
import org.caleydo.view.matchmaker.listener.UseSortingListener;
import org.caleydo.view.matchmaker.listener.UseZoomListener;
import org.caleydo.view.matchmaker.state.ACompareViewState;
import org.caleydo.view.matchmaker.state.CompareViewStateController;

import com.sun.opengl.util.j2d.TextRenderer;

/**
 * The matchmaker view for comparing clustered data sets.
 * 
 * @author Christian Partl
 * @author Alexander Lex
 * @author Marc Streit
 */
public class GLMatchmaker extends AGLView implements IViewCommandHandler,
		IGLRemoteRenderingView, ISelectionUpdateHandler, ISelectionCommandHandler,
		IContentVAUpdateHandler {

	public final static String VIEW_ID = "org.caleydo.view.matchmaker";

	private TextRenderer textRenderer;
	private CompareViewStateController compareViewStateController;

	private CompareGroupsEventListener compareGroupsEventListener;
	private DuplicateSetBarItemEventListener duplicateSetBarItemEventListener;
	private SelectionUpdateListener selectionUpdateListener;
	private AdjustPValueOfSetEventListener adjustPValueOfSetEventListener;
	private SelectionCommandListener selectionCommandListener;
	private CompareMouseWheelListener compareMouseWheelListener;
	private ReplaceContentVAListener replaceContentVAListener;
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

	private ArrayList<ISet> setsToCompare;
	private ArrayList<Integer> clusteredSets;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param sLabel
	 * @param viewFrustum
	 */
	public GLMatchmaker(GLCaleydoCanvas glCanvas, final String sLabel,
			final IViewFrustum viewFrustum) {
		super(glCanvas, sLabel, viewFrustum, true);

		viewType = VIEW_ID;
		glKeyListener = new GLMatchmakerKeyListener(this);
		isControlPressed = false;
		textRenderer = new TextRenderer(new Font("Arial", Font.PLAIN, 32), true, true);
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
		setsToCompare = new ArrayList<ISet>();
	}

	@Override
	public void init(GL gl) {
		// contentVA = useCase.getContentVA(ContentVAType.CONTENT);
		// storageVA = useCase.getStorageVA(StorageVAType.STORAGE);
		compareViewStateController = new CompareViewStateController(this, iUniqueID,
				textRenderer, textureManager, pickingManager, glMouseListener,
				contextMenu, dataDomain, useCase);

		compareViewStateController.init(gl);
	}

	@Override
	public void initLocal(GL gl) {

		iGLDisplayListIndexLocal = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		// Register keyboard listener to GL canvas
		parentGLCanvas.getParentComposite().getDisplay().asyncExec(new Runnable() {
			public void run() {
				parentGLCanvas.getParentComposite().addKeyListener(glKeyListener);
			}
		});

		init(gl);
	}

	@Override
	public void initRemote(final GL gl, final AGLView glParentView,
			final GLMouseListener glMouseListener, GLInfoAreaManager infoAreaManager) {

		// Register keyboard listener to GL canvas
		glParentView.getParentGLCanvas().getParentComposite().getDisplay().asyncExec(
				new Runnable() {
					public void run() {
						glParentView.getParentGLCanvas().getParentComposite()
								.addKeyListener(glKeyListener);
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
	public void setDetailLevel(EDetailLevel detailLevel) {

	}

	@Override
	public void displayLocal(GL gl) {

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
	public void displayRemote(GL gl) {
		// if (bIsDisplayListDirtyRemote) {
		// bIsDisplayListDirtyRemote = false;
		// buildDisplayList(gl, iGLDisplayListIndexRemote);
		// }
		// iGLDisplayListToCall = iGLDisplayListIndexRemote;

		throw new IllegalStateException("not in use");
		// display(gl);

	}

	@Override
	public void display(GL gl) {
		// processEvents();

		compareViewStateController.drawActiveElements(gl);

		// if (bIsDisplayListDirtyLocal) {
		bIsDisplayListDirtyLocal = false;
		buildDisplayList(gl, iGLDisplayListIndexLocal);
		iGLDisplayListToCall = iGLDisplayListIndexLocal;
		// }

		gl.glCallList(iGLDisplayListToCall);

		compareViewStateController.handleDragging(gl);

		if (!isRenderedRemote())
			contextMenu.render(gl, this);

		checkForHits(gl);
	}

	private void buildDisplayList(final GL gl, int iGLDisplayListIndex) {
		// gl.glNewList(iGLDisplayListIndex, GL.GL_COMPILE);

		compareViewStateController.drawDisplayListElements(gl);

		// gl.glEndList();
	}

	@Override
	public String getDetailedInfo() {
		return new String("");
	}

	@Override
	protected void handlePickingEvents(EPickingType ePickingType,
			EPickingMode pickingMode, int iExternalID, Pick pick) {
		if (detailLevel == EDetailLevel.VERY_LOW) {
			return;
		}
		contextMenu.setLocation(pick.getPickedPoint(), getParentGLCanvas().getWidth(),
				getParentGLCanvas().getHeight());
		contextMenu.setMasterGLView(this);
		compareViewStateController.handlePickingEvents(ePickingType, pickingMode,
				iExternalID, pick, isControlPressed);
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
		SerializedCompareView serializedForm = new SerializedCompareView(dataDomain);
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();
		compareGroupsEventListener = new CompareGroupsEventListener();
		compareGroupsEventListener.setHandler(this);
		eventPublisher.addListener(CompareGroupsEvent.class, compareGroupsEventListener);

		duplicateSetBarItemEventListener = new DuplicateSetBarItemEventListener();
		duplicateSetBarItemEventListener.setHandler(this);
		eventPublisher.addListener(DuplicateSetBarItemEvent.class,
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

		replaceContentVAListener = new ReplaceContentVAListener();
		replaceContentVAListener.setHandler(this);
		eventPublisher.addListener(ReplaceContentVAEvent.class, replaceContentVAListener);

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
		eventPublisher.addListener(NewContentGroupInfoEvent.class,
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

		if (replaceContentVAListener != null) {
			eventPublisher.removeListener(replaceContentVAListener);
			replaceContentVAListener = null;
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

	public void setSetsToCompare(final ArrayList<ISet> sets) {

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
	public void handleSelectionUpdate(ISelectionDelta selectionDelta,
			boolean scrollToSelection, String info) {
		compareViewStateController.handleSelectionUpdate(selectionDelta,
				scrollToSelection, info);

	}

	@Override
	public void handleSelectionCommand(EIDCategory category,
			SelectionCommand selectionCommand) {
		compareViewStateController.handleSelectionCommand(category, selectionCommand);
	}

	public void handleMouseWheel(int wheelAmount, Point wheelPosition) {
		this.wheelAmount = wheelAmount;
		this.wheelPoint = wheelPosition;
		wasMouseWheeled = true;
	}

	@Override
	public void handleContentVAUpdate(ContentVADelta vaDelta, String info) {
		System.out.println("COMPARER IGNORES CONTENT VA UPDATE");
	}

	@Override
	public void replaceContentVA(int setID, EIDCategory idCategory, ContentVAType vaType) {

		clusteredSets.add(setID);

		// Check if all sets are properly clustered
		boolean allSetsClustered = true;
		for (ISet set : setsToCompare) {
			if (!clusteredSets.contains(set.getID())) {
				allSetsClustered = false;
				break;
			}
		}

		if (!allSetsClustered)
			return;
	
		compareViewStateController.setSetsToCompare(setsToCompare);
		compareViewStateController.handleReplaceContentVA(setID, idCategory, vaType);
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

	public void handleContentGroupListUpdate(int setID, ContentGroupList contentGroupList) {
		compareViewStateController.handleContentGroupListUpdate(setID, contentGroupList);
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
}
