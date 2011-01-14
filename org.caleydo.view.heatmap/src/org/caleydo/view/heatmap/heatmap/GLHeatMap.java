package org.caleydo.view.heatmap.heatmap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import javax.management.InvalidAttributeValueException;
import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.mapping.IDType;
import org.caleydo.core.data.selection.ContentSelectionManager;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.StorageSelectionManager;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.StorageVirtualArray;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.delta.ContentVADelta;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.EDataFilterLevel;
import org.caleydo.core.manager.event.view.storagebased.HideHeatMapElementsEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.manager.view.ConnectedElementRepresentationManager;
import org.caleydo.core.manager.view.StandardTransformer;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.clusterer.ClusterManager;
import org.caleydo.core.util.clusterer.ClusterResult;
import org.caleydo.core.util.clusterer.ClusterState;
import org.caleydo.core.util.clusterer.EClustererAlgo;
import org.caleydo.core.util.clusterer.EClustererType;
import org.caleydo.core.util.clusterer.EDistanceMeasure;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.AStorageBasedView;
import org.caleydo.core.view.opengl.canvas.DetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.container.ContentContextMenuItemContainer;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.container.StorageContextMenuItemContainer;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.heatmap.HeatMapRenderStyle;
import org.caleydo.view.heatmap.heatmap.layout.ATemplate;
import org.caleydo.view.heatmap.heatmap.template.AHeatMapTemplate;
import org.caleydo.view.heatmap.heatmap.template.DefaultTemplate;
import org.caleydo.view.heatmap.heatmap.template.TemplateRenderer;
import org.caleydo.view.heatmap.hierarchical.GLHierarchicalHeatMap;
import org.caleydo.view.heatmap.listener.GLHeatMapKeyListener;

/**
 * Rendering the GLHeatMap
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class GLHeatMap extends AStorageBasedView {

	public final static String VIEW_ID = "org.caleydo.view.heatmap";
	public static final SelectionType SELECTION_HIDDEN = new SelectionType("Hidden",
			new float[] { 0f, 0f, 0f, 1f }, 1, false, false, 0.2f);

	HeatMapRenderStyle renderStyle;

	// private EIDType eldDataType = EIDType.EXPRESSION_INDEX;
	// private EIDType eStorageDataType = EIDType.EXPERIMENT_INDEX;

	boolean bUseDetailLevel = true;

	private boolean sendClearSelectionsEvent = false;

	int iCurrentMouseOverElement = -1;

	int numSentClearSelectionEvents = 0;

	private TemplateRenderer templateRenderer;
	private AHeatMapTemplate template;
	/** hide elements with the state {@link #SELECTION_HIDDEN} if this is true */
	private boolean hideElements = true;
	/** try to show captions, if spacing allows it */
	private boolean showCaptions = false;

	/**
	 * Determines whether a bigger space between heat map and caption is needed
	 * or not. If false no cluster info is available and therefore no additional
	 * space is needed. Set by remote rendering view (HHM).
	 */
	boolean bClusterVisualizationGenesActive = false;

	boolean bClusterVisualizationExperimentsActive = false;

	/** Utility object for coordinate transformation and projection */
	protected StandardTransformer selectionTransformer;

	/** Signals that the heat map is currently active */
	private boolean isActive = false;

	/** ID for va used when heat map is embedded */
	public static final String CONTENT_EMBEDDED_VA = "CONTENT_EMBEDDED";

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param viewFrustum
	 */
	public GLHeatMap(GLCaleydoCanvas glCanvas, ViewFrustum viewFrustum) {

		super(glCanvas, viewFrustum);
		viewType = GLHeatMap.VIEW_ID;

		glKeyListener = new GLHeatMapKeyListener(this);
		renderStyle = new HeatMapRenderStyle(this, viewFrustum);
	}

	@Override
	public void init(GL2 gl) {
		super.renderStyle = renderStyle;

		templateRenderer = new TemplateRenderer(this.viewFrustum);
		if (template == null)
			template = new DefaultTemplate(this);

		templateRenderer.setTemplate(template);
	}

	@Override
	public void initLocal(GL2 gl) {
		// Register keyboard listener to GL2 canvas
		GeneralManager.get().getGUIBridge().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				parentGLCanvas.getParentComposite().addKeyListener(glKeyListener);
			}
		});

		iGLDisplayListIndexLocal = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		selectionTransformer = new StandardTransformer(iUniqueID);
		init(gl);
	}

	@Override
	public void initRemote(final GL2 gl, final AGLView glParentView,
			final GLMouseListener glMouseListener, GLInfoAreaManager infoAreaManager) {

		if (glRemoteRenderingView != null
				&& glRemoteRenderingView.getViewType().equals("org.caleydo.view.bucket"))
			renderStyle.setUseFishEye(false);

		// Register keyboard listener to GL2 canvas
		glParentView.getParentGLCanvas().getParentComposite().getDisplay()
				.asyncExec(new Runnable() {
					@Override
					public void run() {
						glParentView.getParentGLCanvas().getParentComposite()
								.addKeyListener(glKeyListener);
					}
				});

		this.glMouseListener = glMouseListener;

		iGLDisplayListIndexRemote = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexRemote;

		selectionTransformer = new StandardTransformer(iUniqueID);

		init(gl);
	}

	@Override
	public void setDetailLevel(DetailLevel detailLevel) {
		if (bUseDetailLevel) {
			super.setDetailLevel(detailLevel);
			if (detailLevel == DetailLevel.HIGH)
				showCaptions = true;
			else
				showCaptions = false;

			template.recalculateSpacings();

		}
	}

	@Override
	public void displayLocal(GL2 gl) {

		if (set == null)
			return;

		pickingManager.handlePicking(this, gl);

		if (bIsDisplayListDirtyLocal) {
			buildDisplayList(gl, iGLDisplayListIndexLocal);
			bIsDisplayListDirtyLocal = false;
		}
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		display(gl);
		numSentClearSelectionEvents = 0;
		checkForHits(gl);

		ConnectedElementRepresentationManager cerm = GeneralManager.get()
				.getViewGLCanvasManager().getConnectedElementRepresentationManager();
		cerm.doViewRelatedTransformation(gl, selectionTransformer);

		if (eBusyModeState != EBusyModeState.OFF) {
			renderBusyMode(gl);
		}
	}

	@Override
	public void displayRemote(GL2 gl) {
		if (set == null)
			return;

		if (bIsDisplayListDirtyRemote) {
			buildDisplayList(gl, iGLDisplayListIndexRemote);
			bIsDisplayListDirtyRemote = false;
			generalManager.getViewGLCanvasManager()
					.getConnectedElementRepresentationManager()
					.clearTransformedConnections();
		}
		iGLDisplayListToCall = iGLDisplayListIndexRemote;

		display(gl);
		numSentClearSelectionEvents = 0;
		checkForHits(gl);

		// glMouseListener.resetEvents();
	}

	@Override
	public void display(GL2 gl) {

		gl.glCallList(iGLDisplayListToCall);

		// buildDisplayList(gl, iGLDisplayListIndexRemote);

		if (!isRenderedRemote())
			contextMenu.render(gl, this);
	}

	private void buildDisplayList(final GL2 gl, int iGLDisplayListIndex) {

		if (bHasFrustumChanged) {
			bHasFrustumChanged = false;
		}
		gl.glNewList(iGLDisplayListIndex, GL2.GL_COMPILE);

		if (contentVA.size() == 0) {
			renderSymbol(gl, EIconTextures.HEAT_MAP_SYMBOL, 2);
		} else {
			templateRenderer.render(gl);
		}
		gl.glEndList();
	}

	public ContentSelectionManager getContentSelectionManager() {
		return contentSelectionManager;
	}

	public StorageSelectionManager getStorageSelectionManager() {
		return storageSelectionManager;
	}

	@Override
	protected void initLists() {
		// todo this is not nice here, we may need a more intelligent way to
		// determine which to use

		if (contentVAType.equals(CONTENT_EMBEDDED_VA)) {
			set.setContentVA(contentVAType, new ContentVirtualArray(contentVAType));
		} else {
			if (bRenderOnlyContext)
				contentVAType = ISet.CONTENT_CONTEXT;
			else
				contentVAType = ISet.CONTENT;
		}

		contentVA = set.getContentData(contentVAType).getContentVA();
		storageVA = set.getStorageData(storageVAType).getStorageVA();

		contentSelectionManager.setVA(contentVA);
		storageSelectionManager.setVA(storageVA);

		// FIXME: do we need to do this here?
		renderStyle = new HeatMapRenderStyle(this, viewFrustum);
		if (getRemoteRenderingGLCanvas() instanceof GLHierarchicalHeatMap)
			renderStyle.setUseFishEye(false);
	}

	@Override
	public String getShortInfo() {

		if (contentVA == null)
			return "Heat Map - 0 " + dataDomain.getContentName(false, true)
					+ " / 0 experiments";

		return "Heat Map - " + contentVA.size() + " "
				+ dataDomain.getContentName(false, true) + " / " + storageVA.size()
				+ " experiments";
	}

	@Override
	public String getDetailedInfo() {

		StringBuffer sInfoText = new StringBuffer();
		sInfoText.append("<b>Type:</b> Heat Map\n");

		sInfoText.append(contentVA.size() + " " + dataDomain.getContentName(true, true)
				+ " in rows and " + storageVA.size() + " experiments in columns.\n");

		if (bRenderOnlyContext) {
			sInfoText.append("Showing only " + " "
					+ dataDomain.getContentName(false, true)
					+ " which occur in one of the other views in focus\n");
		} else {
			if (bUseRandomSampling) {
				sInfoText.append("Random sampling active, sample size: "
						+ iNumberOfRandomElements + "\n");
			} else {
				sInfoText.append("Random sampling inactive\n");
			}

			if (dataFilterLevel == EDataFilterLevel.COMPLETE) {
				sInfoText.append("Showing all genes in the dataset\n");
			} else if (dataFilterLevel == EDataFilterLevel.ONLY_MAPPING) {
				sInfoText
						.append("Showing all genes that have a known DAVID ID mapping\n");
			} else if (dataFilterLevel == EDataFilterLevel.ONLY_CONTEXT) {
				sInfoText
						.append("Showing all genes that are contained in any of the KEGG or Biocarta pathways\n");
			}
		}

		return sInfoText.toString();
	}

	@Override
	protected void handlePickingEvents(EPickingType pickingType,
			EPickingMode pickingMode, int iExternalID, Pick pick) {
		if (detailLevel == DetailLevel.VERY_LOW) {
			return;
		}

		SelectionType selectionType;
		switch (pickingType) {
		case HEAT_MAP_LINE_SELECTION:
			iCurrentMouseOverElement = iExternalID;
			switch (pickingMode) {

			case CLICKED:
				selectionType = SelectionType.SELECTION;
				setActive(true);
				break;

			case MOUSE_OVER:
				selectionType = SelectionType.MOUSE_OVER;
				break;

			case RIGHT_CLICKED:
				selectionType = SelectionType.SELECTION;

				if (!isRenderedRemote()) {
					contextMenu.setLocation(pick.getPickedPoint(), getParentGLCanvas()
							.getWidth(), getParentGLCanvas().getHeight());
					contextMenu.setMasterGLView(this);
				}

				ContentContextMenuItemContainer contentContextMenuItemContainer = new ContentContextMenuItemContainer();
				contentContextMenuItemContainer.setDataDomain(dataDomain);
				contentContextMenuItemContainer.setID(contentIDType, iExternalID);
				contextMenu.addItemContanier(contentContextMenuItemContainer);
				break;

			default:
				return;

			}

			createContentSelection(selectionType, iExternalID);

			break;

		case HEAT_MAP_STORAGE_SELECTION:

			switch (pickingMode) {
			case CLICKED:
				selectionType = SelectionType.SELECTION;
				break;
			case MOUSE_OVER:
				selectionType = SelectionType.MOUSE_OVER;
				break;
			case RIGHT_CLICKED:
				if (!isRenderedRemote()) {
					contextMenu.setLocation(pick.getPickedPoint(), getParentGLCanvas()
							.getWidth(), getParentGLCanvas().getHeight());
					contextMenu.setMasterGLView(this);
				}
				StorageContextMenuItemContainer experimentContextMenuItemContainer = new StorageContextMenuItemContainer();
				experimentContextMenuItemContainer.setDataDomain(dataDomain);
				experimentContextMenuItemContainer.setID(storageIDType, iExternalID);
				contextMenu.addItemContanier(experimentContextMenuItemContainer);
			default:
				return;
			}

			createStorageSelection(selectionType, iExternalID);

			break;

		case HEAT_MAP_HIDE_HIDDEN_ELEMENTS:
			if (pickingMode == EPickingMode.CLICKED)
				if (hideElements)
					hideElements = false;
				else
					hideElements = true;

			HideHeatMapElementsEvent event = new HideHeatMapElementsEvent(hideElements);
			event.setSender(this);
			event.setDataDomainType(dataDomain.getDataDomainType());
			eventPublisher.triggerEvent(event);

			setDisplayListDirty();

			break;
		case HEAT_MAP_SHOW_CAPTIONS:

			if (pickingMode == EPickingMode.CLICKED)
				if (showCaptions)
					showCaptions = false;
				else {
					showCaptions = true;
				}

			template.recalculateSpacings();
			setDisplayListDirty();
			break;
		}
	}

	private void createContentSelection(SelectionType selectionType, int contentID) {
		if (contentSelectionManager.checkStatus(selectionType, contentID))
			return;

		// check if the mouse-overed element is already selected, and if it is,
		// whether mouse over is clear.
		// If that all is true we don't need to do anything
		if (selectionType == SelectionType.MOUSE_OVER
				&& contentSelectionManager
						.checkStatus(SelectionType.SELECTION, contentID)
				&& contentSelectionManager.getElements(SelectionType.MOUSE_OVER).size() == 0)
			return;

		connectedElementRepresentationManager.clear(contentIDType, selectionType);

		contentSelectionManager.clearSelection(selectionType);

		// TODO: Integrate multi spotting support again

		Integer iMappingID = generalManager.getIDManager().createID(
				EManagedObjectType.CONNECTION);
		contentSelectionManager.addToType(selectionType, contentID);
		contentSelectionManager.addConnectionID(iMappingID, contentID);

		SelectionDelta selectionDelta = contentSelectionManager.getDelta();

		handleConnectedElementReps(selectionDelta);
		SelectionUpdateEvent event = new SelectionUpdateEvent();
		event.setSender(this);
		event.setDataDomainType(dataDomain.getDataDomainType());
		event.setSelectionDelta(selectionDelta);
		event.setInfo(getShortInfoLocal());
		eventPublisher.triggerEvent(event);

		setDisplayListDirty();
	}

	private void createStorageSelection(SelectionType selectionType, int storageID) {
		if (storageSelectionManager.checkStatus(selectionType, storageID))
			return;

		// check if the mouse-overed element is already selected, and if it is,
		// whether mouse over is clear.
		// If that all is true we don't need to do anything
		if (selectionType == SelectionType.MOUSE_OVER
				&& storageSelectionManager
						.checkStatus(SelectionType.SELECTION, storageID)
				&& storageSelectionManager.getElements(SelectionType.MOUSE_OVER).size() == 0)
			return;

		storageSelectionManager.clearSelection(selectionType);
		storageSelectionManager.addToType(selectionType, storageID);

		SelectionDelta selectionDelta = storageSelectionManager.getDelta();
		SelectionUpdateEvent event = new SelectionUpdateEvent();
		event.setSender(this);
		event.setDataDomainType(dataDomain.getDataDomainType());
		event.setSelectionDelta(selectionDelta);
		eventPublisher.triggerEvent(event);

		setDisplayListDirty();
	}

	public void upDownSelect(boolean isUp) {
		ContentVirtualArray virtualArray = contentVA;
		if (virtualArray == null)
			throw new IllegalStateException(
					"Virtual Array is required for selectNext Operation");
		int selectedElement = cursorSelect(virtualArray, contentSelectionManager, isUp);
		if (selectedElement < 0)
			return;
		createContentSelection(SelectionType.MOUSE_OVER, selectedElement);
	}

	public void leftRightSelect(boolean isLeft) {
		StorageVirtualArray virtualArray = storageVA;
		if (virtualArray == null)
			throw new IllegalStateException(
					"Virtual Array is required for selectNext Operation");
		int selectedElement = cursorSelect(virtualArray, storageSelectionManager, isLeft);
		if (selectedElement < 0)
			return;
		createStorageSelection(SelectionType.MOUSE_OVER, selectedElement);
	}

	public void enterPressedSelect() {
		StorageVirtualArray virtualArray = storageVA;
		if (virtualArray == null)
			throw new IllegalStateException(
					"Virtual Array is required for enterPressed Operation");

		java.util.Set<Integer> elements = storageSelectionManager
				.getElements(SelectionType.MOUSE_OVER);
		Integer selectedElement = -1;
		if (elements.size() == 1) {
			selectedElement = (Integer) elements.toArray()[0];
			createStorageSelection(SelectionType.SELECTION, selectedElement);
		}

		ContentVirtualArray contentVirtualArray = contentVA;
		if (contentVirtualArray == null)
			throw new IllegalStateException(
					"Virtual Array is required for enterPressed Operation");
		elements = contentSelectionManager.getElements(SelectionType.MOUSE_OVER);
		selectedElement = -1;
		if (elements.size() == 1) {
			selectedElement = (Integer) elements.toArray()[0];
			createContentSelection(SelectionType.SELECTION, selectedElement);
		}
	}

	private <VAType extends VirtualArray<?, ?, ?>, SelectionManagerType extends SelectionManager> int cursorSelect(
			VAType virtualArray, SelectionManagerType selectionManager, boolean isUp) {

		java.util.Set<Integer> elements = selectionManager
				.getElements(SelectionType.MOUSE_OVER);
		if (elements.size() == 0) {
			elements = selectionManager.getElements(SelectionType.SELECTION);
			if (elements.size() == 0)
				return -1;
		}

		if (elements.size() == 1) {
			Integer element = elements.iterator().next();
			int index = virtualArray.indexOf(element);
			int newIndex;
			if (isUp) {
				newIndex = index - 1;
				if (newIndex < 0)
					return -1;
			} else {
				newIndex = index + 1;
				if (newIndex == virtualArray.size())
					return -1;

			}
			return virtualArray.get(newIndex);

		}
		return -1;
	}

	@Override
	protected ArrayList<SelectedElementRep> createElementRep(IDType idType, int id)
			throws InvalidAttributeValueException {
		SelectedElementRep elementRep;
		ArrayList<SelectedElementRep> alElementReps = new ArrayList<SelectedElementRep>(4);

		for (int contentIndex : contentVA.indicesOf(id)) {
			if (contentIndex == -1) {
				continue;
			}

			float xValue = renderStyle.getXCenter();

			float yValue = 0;

			yValue = getYCoordinateByContentIndex(contentIndex);
			yValue = viewFrustum.getHeight() - yValue;
			elementRep = new SelectedElementRep(contentIDType, iUniqueID, xValue, yValue,
					0);// - fAnimationTranslation,
			// 0);

			alElementReps.add(elementRep);
		}

		return alElementReps;
	}

	/**
	 * Returns the y coordinate of the element rendered at contentIndex, or null
	 * if the current element is hidden
	 * 
	 * @param contentIndex
	 * @return
	 */
	public Float getYCoordinateByContentIndex(int contentIndex) {

		if (isHideElements()) {
			Integer contentID = contentVA.get(contentIndex);
			if (contentSelectionManager.checkStatus(SELECTION_HIDDEN, contentID))
				return null;
		}
		return template.getYCoordinateByContentIndex(contentIndex);

	}

	/**
	 * Returns the x coordinate of the element rendered at storageIndex
	 * 
	 * @param storageIndex
	 * @return
	 */
	public Float getXCoordinateByStorageIndex(int storageIndex) {
		return template.getXCoordinateByStorageIndex(storageIndex);
	}

	@Override
	public void renderContext(boolean bRenderOnlyContext) {

		this.bRenderOnlyContext = bRenderOnlyContext;

		if (this.bRenderOnlyContext) {
			contentVA = dataDomain.getContentVA(ISet.CONTENT_CONTEXT);
		} else {
			contentVA = dataDomain.getContentVA(ISet.CONTENT);
		}

		contentSelectionManager.setVA(contentVA);
		// renderStyle.setActiveVirtualArray(iContentVAID);

		setDisplayListDirty();

	}

	@Override
	public void handleVAUpdate(ContentVADelta delta, String info) {

		super.handleVAUpdate(delta, info);

		if (delta.getVAType().equals(ISet.CONTENT_CONTEXT)
				&& contentVAType.equals(ISet.CONTENT_CONTEXT)) {
			ClusterState state = new ClusterState(EClustererAlgo.AFFINITY_PROPAGATION,
					EClustererType.CONTENT_CLUSTERING,
					EDistanceMeasure.EUCLIDEAN_DISTANCE);
			int contentVAID = contentVA.getID();

			if (contentVA.size() == 0 || storageVA.size() == 0)
				return;

			state.setContentVA(contentVA);
			state.setStorageVA(storageVA);
			state.setAffinityPropClusterFactorGenes(4.0f);

			ClusterManager clusterManger = new ClusterManager(set);
			ClusterResult result = clusterManger.cluster(state);

			contentVA = result.getContentResult().getContentVA();
			contentSelectionManager.setVA(contentVA);
			contentVA.setID(contentVAID);
		}
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedHeatMapView serializedForm = new SerializedHeatMapView(
				dataDomain.getDataDomainType());
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public void handleUpdateView() {
		setDisplayListDirty();
	}

	public void setClusterVisualizationGenesActiveFlag(boolean bClusterVisualizationActive) {
		this.bClusterVisualizationGenesActive = bClusterVisualizationActive;
	}

	public void setClusterVisualizationExperimentsActiveFlag(
			boolean bClusterVisualizationExperimentsActive) {
		this.bClusterVisualizationExperimentsActive = bClusterVisualizationExperimentsActive;
	}

	@Override
	public String toString() {
		return "Standalone heat map, rendered remote: " + isRenderedRemote()
				+ ", contentSize: " + contentVA.size() + ", storageSize: "
				+ storageVA.size() + ", contentVAType: " + contentVAType
				+ ", remoteRenderer:" + getRemoteRenderingGLCanvas();
	}

	@Override
	public void destroy() {
		selectionTransformer.destroy();
		super.destroy();
	}

	public void useFishEye(boolean useFishEye) {
		renderStyle.setUseFishEye(useFishEye);
	}

	public void setContentVA(ContentVirtualArray contentVA) {
		contentSelectionManager.setVA(contentVA);
		this.contentVA = contentVA;
		setDisplayListDirty();
	}

	public boolean isSendClearSelectionsEvent() {
		return sendClearSelectionsEvent;
	}

	public void setSendClearSelectionsEvent(boolean sendClearSelectionsEvent) {
		this.sendClearSelectionsEvent = sendClearSelectionsEvent;
	}

	public void setSet(ISet set) {
		this.set = set;
	}

	public PickingManager getPickingManager() {
		return pickingManager;
	}

	public void setRenderTemplate(AHeatMapTemplate template) {
		this.template = template;
	}

	/**
	 * Check whether we should hide elements
	 * 
	 * @return
	 */
	public boolean isHideElements() {
		return hideElements;
	}

	/**
	 * Check whether we should try to show captions
	 * 
	 * @return
	 */
	public boolean isShowCaptions() {
		return showCaptions;
	}

	/**
	 * returns the number of elements currently visible in the heat map
	 * 
	 * @return
	 */
	public int getNumberOfVisibleElements() {
		if (isHideElements())
			return contentVA.size()
					- contentSelectionManager.getNumberOfElements(SELECTION_HIDDEN);
		else
			return contentVA.size();
	}

	/**
	 * returns the overhead which the heat map needs additionally to the
	 * elements
	 * 
	 * @return
	 */
	public float getRequiredOverheadSpacing() {
		// return template.getYOverhead();
		if (isActive)
			return 0.1f;
		else
			return 0;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
		template.setActive(isActive);
		setDisplayListDirty();
	}

	public boolean isActive() {
		return isActive;
	}

	/**
	 * Returns true if a minimum spacing per element is required. This is
	 * typically the case when captions are rendered.
	 * 
	 * @return
	 */
	public boolean isForceMinSpacing() {
		if (isShowCaptions() || isActive)
			return true;
		return false;
	}

	/**
	 * Returns the height of a particular element
	 * 
	 * @param contentID
	 *            the id of the element - since they can be of different height
	 *            due to the fish eye
	 * @return the height of the element
	 */
	public float getFieldHeight(int contentID) {
		return template.getElementHeight(contentID);
	}

	public float getFieldWidth(int storageID) {
		return template.getElementWidth(storageID);
	}

	/**
	 * Returns the minimal spacing required when {@link #isForceMinSpacing()} is
	 * true. This is typically the case when captions are rendered.
	 * 
	 * @return
	 */
	// public float getMinSpacing() {
	// return HeatMapRenderStyle.MIN_SELECTED_FIELD_HEIGHT;
	// }

	public void recalculateLayout() {
		processEvents();
		template.recalculateSpacings();
	}

	@Override
	public void handleSelectionUpdate(ISelectionDelta selectionDelta,
			boolean scrollToSelection, String info) {
		super.handleSelectionUpdate(selectionDelta, scrollToSelection, info);

		if (getZoomedElements().size() > 0)
			this.setActive(true);
		else
			this.setActive(false);
	}

	public java.util.Set<Integer> getZoomedElements() {
		java.util.Set<Integer> zoomedElements = new HashSet<Integer>(
				contentSelectionManager.getElements(SelectionType.SELECTION));
		// zoomedElements.addAll(contentSelectionManager
		// .getElements(SelectionType.MOUSE_OVER));
		Iterator<Integer> elementIterator = zoomedElements.iterator();
		while (elementIterator.hasNext()) {
			int contentID = elementIterator.next();
			if (contentVA.containsElement(contentID) == 0)
				elementIterator.remove();
			else if (contentSelectionManager.checkStatus(SELECTION_HIDDEN, contentID))
				elementIterator.remove();
		}
		return zoomedElements;
	}

	public ISet getSet() {
		return set;
	}
}
