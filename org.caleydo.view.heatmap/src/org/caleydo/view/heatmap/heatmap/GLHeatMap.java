package org.caleydo.view.heatmap.heatmap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import javax.management.InvalidAttributeValueException;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.id.ManagedObjectType;
import org.caleydo.core.data.selection.DimensionSelectionManager;
import org.caleydo.core.data.selection.RecordSelectionManager;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.delta.RecordVADelta;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.EDataFilterLevel;
import org.caleydo.core.manager.event.view.tablebased.HideHeatMapElementsEvent;
import org.caleydo.core.manager.event.view.tablebased.SelectionUpdateEvent;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.manager.picking.PickingMode;
import org.caleydo.core.manager.picking.PickingType;
import org.caleydo.core.manager.view.ConnectedElementRepresentationManager;
import org.caleydo.core.manager.view.StandardTransformer;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.clusterer.ClusterManager;
import org.caleydo.core.util.clusterer.ClusterResult;
import org.caleydo.core.util.clusterer.ClusterState;
import org.caleydo.core.util.clusterer.ClustererType;
import org.caleydo.core.util.clusterer.EClustererAlgo;
import org.caleydo.core.util.clusterer.EDistanceMeasure;
import org.caleydo.core.view.contextmenu.ContextMenuItem;
import org.caleydo.core.view.contextmenu.item.BookmarkMenuItem;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.ATableBasedView;
import org.caleydo.core.view.opengl.canvas.DetailLevel;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.heatmap.HeatMapRenderStyle;
import org.caleydo.view.heatmap.heatmap.template.AHeatMapTemplate;
import org.caleydo.view.heatmap.heatmap.template.DefaultTemplate;
import org.caleydo.view.heatmap.hierarchical.GLHierarchicalHeatMap;
import org.caleydo.view.heatmap.listener.GLHeatMapKeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * Rendering the GLHeatMap
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class GLHeatMap extends ATableBasedView {

	public final static String VIEW_TYPE = "org.caleydo.view.heatmap";
	public static final SelectionType SELECTION_HIDDEN = new SelectionType("Hidden",
			new float[] { 0f, 0f, 0f, 1f }, 1, false, false, 0.2f);

	HeatMapRenderStyle renderStyle;

	boolean bUseDetailLevel = true;

	private boolean sendClearSelectionsEvent = false;

	int iCurrentMouseOverElement = -1;

	int numSentClearSelectionEvents = 0;

	private LayoutManager templateRenderer;
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
	 */
	public GLHeatMap(GLCanvas glCanvas, Composite parentComposite, ViewFrustum viewFrustum) {

		super(glCanvas, parentComposite, viewFrustum);
		viewType = GLHeatMap.VIEW_TYPE;

		glKeyListener = new GLHeatMapKeyListener(this);
		renderStyle = new HeatMapRenderStyle(this, viewFrustum);
	}

	@Override
	public void init(GL2 gl) {
		super.renderStyle = renderStyle;

		textRenderer = new CaleydoTextRenderer(24);

		templateRenderer = new LayoutManager(this.viewFrustum);
		if (template == null)
			template = new DefaultTemplate(this);

		templateRenderer.setTemplate(template);
		templateRenderer.updateLayout();
	}

	@Override
	public void initLocal(GL2 gl) {
		// Register keyboard listener to GL2 canvas
		Display.getCurrent().asyncExec(new Runnable() {
			@Override
			public void run() {
				parentComposite.addKeyListener(glKeyListener);
			}
		});

		iGLDisplayListIndexLocal = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		selectionTransformer = new StandardTransformer(uniqueID);
		init(gl);
	}

	@Override
	public void initRemote(final GL2 gl, final AGLView glParentView,
			final GLMouseListener glMouseListener) {

		if (glRemoteRenderingView != null
				&& glRemoteRenderingView.getViewType().equals("org.caleydo.view.bucket"))
			renderStyle.setUseFishEye(false);

		// Register keyboard listener to GL2 canvas
		parentComposite.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				glParentView.getParentComposite().addKeyListener(glKeyListener);
			}
		});

		this.glMouseListener = glMouseListener;

		iGLDisplayListIndexRemote = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexRemote;

		selectionTransformer = new StandardTransformer(uniqueID);

		init(gl);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		super.reshape(drawable, x, y, width, height);
		templateRenderer.updateLayout();
	}

	@Override
	public void setDetailLevel(DetailLevel detailLevel) {
		if (bUseDetailLevel) {
			super.setDetailLevel(detailLevel);
			if (detailLevel == DetailLevel.HIGH)
				showCaptions = true;
			else if (detailLevel == DetailLevel.MEDIUM)
				showCaptions = true;
			else
				showCaptions = false;

			template.setStaticLayouts();

		}
	}

	@Override
	public void displayLocal(GL2 gl) {

		if (table == null)
			return;

		if (!lazyMode)
			pickingManager.handlePicking(this, gl);

		if (bIsDisplayListDirtyLocal) {
			buildDisplayList(gl, iGLDisplayListIndexLocal);
			bIsDisplayListDirtyLocal = false;
		}
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		display(gl);
		numSentClearSelectionEvents = 0;

		if (!lazyMode)
			checkForHits(gl);

		ConnectedElementRepresentationManager cerm = GeneralManager.get()
				.getViewManager().getConnectedElementRepresentationManager();
		cerm.doViewRelatedTransformation(gl, selectionTransformer);

		if (eBusyModeState != EBusyModeState.OFF) {
			renderBusyMode(gl);
		}
	}

	@Override
	public void displayRemote(GL2 gl) {
		if (table == null)
			return;

		if (bIsDisplayListDirtyRemote) {
			templateRenderer.updateLayout();
			buildDisplayList(gl, iGLDisplayListIndexRemote);
			bIsDisplayListDirtyRemote = false;
			generalManager.getViewManager()
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
	}

	private void buildDisplayList(final GL2 gl, int iGLDisplayListIndex) {

		if (bHasFrustumChanged) {
			bHasFrustumChanged = false;
		}
		gl.glNewList(iGLDisplayListIndex, GL2.GL_COMPILE);

		if (recordVA.size() == 0) {
			renderSymbol(gl, EIconTextures.HEAT_MAP_SYMBOL, 2);
		} else {
			templateRenderer.render(gl);
		}
		gl.glEndList();
	}

	public RecordSelectionManager getContentSelectionManager() {
		return recordSelectionManager;
	}

	public DimensionSelectionManager getDimensionSelectionManager() {
		return dimensionSelectionManager;
	}

	@Override
	protected void initLists() {
		// todo this is not nice here, we may need a more intelligent way to
		// determine which to use

		if (recordVAType.equals(CONTENT_EMBEDDED_VA)) {
			table.setRecordVA(recordVAType, new RecordVirtualArray(recordVAType));
		} else {
			if (bRenderOnlyContext)
				recordVAType = DataTable.RECORD_CONTEXT;
			else
				recordVAType = DataTable.RECORD;
		}

		if (recordVA == null)
			recordVA = table.getRecordData(recordVAType).getRecordVA();
		if (dimensionVA == null)
			dimensionVA = table.getDimensionData(dimensionVAType).getDimensionVA();

		recordSelectionManager.setVA(recordVA);
		dimensionSelectionManager.setVA(dimensionVA);

		// FIXME: do we need to do this here?
		renderStyle = new HeatMapRenderStyle(this, viewFrustum);
		if (getRemoteRenderingGLCanvas() instanceof GLHierarchicalHeatMap)
			renderStyle.setUseFishEye(false);
	}

	@Override
	public String getShortInfo() {

		if (recordVA == null)
			return "Heat Map - 0 " + dataDomain.getRecordName(false, true)
					+ " / 0 experiments";

		return "Heat Map - " + recordVA.size() + " "
				+ dataDomain.getRecordName(false, true) + " / " + dimensionVA.size()
				+ " experiments";
	}

	@Override
	public String getDetailedInfo() {

		StringBuffer sInfoText = new StringBuffer();
		sInfoText.append("<b>Type:</b> Heat Map\n");

		sInfoText.append(recordVA.size() + " " + dataDomain.getRecordName(true, true)
				+ " in rows and " + dimensionVA.size() + " experiments in columns.\n");

		if (bRenderOnlyContext) {
			sInfoText.append("Showing only " + " "
					+ dataDomain.getRecordName(false, true)
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
	protected void handlePickingEvents(PickingType pickingType,
			PickingMode pickingMode, int externalID, Pick pick) {
		if (detailLevel == DetailLevel.VERY_LOW) {
			return;
		}

		SelectionType selectionType;
		
		switch (pickingType) {
		case HEAT_MAP_LINE_SELECTION:
			iCurrentMouseOverElement = externalID;
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
				
				ContextMenuItem menuItem = new BookmarkMenuItem("Bookmark "
						+ dataDomain.getRecordLabel(recordIDType, externalID),
						recordIDType, externalID);
				contextMenuCreator.addContextMenuItem(menuItem);
				
				break;

			default:
				return;

			}

			createContentSelection(selectionType, externalID);

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
				
				ContextMenuItem menuItem = new BookmarkMenuItem("Bookmark " + dataDomain.getDimensionLabel(dimensionIDType, externalID), dimensionIDType,
						externalID);
				contextMenuCreator.addContextMenuItem(menuItem);
				
			default:
				return;
			}

			createDimensionSelection(selectionType, externalID);

			break;

		case HEAT_MAP_HIDE_HIDDEN_ELEMENTS:
			if (pickingMode == PickingMode.CLICKED)
				if (hideElements)
					hideElements = false;
				else
					hideElements = true;

			HideHeatMapElementsEvent event = new HideHeatMapElementsEvent(hideElements);
			event.setSender(this);
			event.setDataDomainID(dataDomain.getDataDomainID());
			eventPublisher.triggerEvent(event);

			setDisplayListDirty();

			break;
		case HEAT_MAP_SHOW_CAPTIONS:

			if (pickingMode == PickingMode.CLICKED)
				if (showCaptions)
					showCaptions = false;
				else {
					showCaptions = true;
				}

			template.setStaticLayouts();
			setDisplayListDirty();
			break;
		}
	}

	private void createContentSelection(SelectionType selectionType, int recordID) {
		if (recordSelectionManager.checkStatus(selectionType, recordID))
			return;

		// check if the mouse-overed element is already selected, and if it is,
		// whether mouse over is clear.
		// If that all is true we don't need to do anything
		if (selectionType == SelectionType.MOUSE_OVER
				&& recordSelectionManager
						.checkStatus(SelectionType.SELECTION, recordID)
				&& recordSelectionManager.getElements(SelectionType.MOUSE_OVER).size() == 0)
			return;

		connectedElementRepresentationManager.clear(recordIDType, selectionType);

		recordSelectionManager.clearSelection(selectionType);

		// TODO: Integrate multi spotting support again

		Integer iMappingID = generalManager.getIDCreator().createID(
				ManagedObjectType.CONNECTION);
		recordSelectionManager.addToType(selectionType, recordID);
		recordSelectionManager.addConnectionID(iMappingID, recordID);

		SelectionDelta selectionDelta = recordSelectionManager.getDelta();

		handleConnectedElementReps(selectionDelta);
		SelectionUpdateEvent event = new SelectionUpdateEvent();
		event.setSender(this);
		event.setDataDomainID(dataDomain.getDataDomainID());
		event.setSelectionDelta(selectionDelta);
		event.setInfo(getShortInfoLocal());
		eventPublisher.triggerEvent(event);

		setDisplayListDirty();
	}

	private void createDimensionSelection(SelectionType selectionType, int dimensionID) {
		if (dimensionSelectionManager.checkStatus(selectionType, dimensionID))
			return;

		// check if the mouse-overed element is already selected, and if it is,
		// whether mouse over is clear.
		// If that all is true we don't need to do anything
		if (selectionType == SelectionType.MOUSE_OVER
				&& dimensionSelectionManager
						.checkStatus(SelectionType.SELECTION, dimensionID)
				&& dimensionSelectionManager.getElements(SelectionType.MOUSE_OVER).size() == 0)
			return;

		dimensionSelectionManager.clearSelection(selectionType);
		dimensionSelectionManager.addToType(selectionType, dimensionID);

		SelectionDelta selectionDelta = dimensionSelectionManager.getDelta();
		SelectionUpdateEvent event = new SelectionUpdateEvent();
		event.setSender(this);
		event.setDataDomainID(dataDomain.getDataDomainID());
		event.setSelectionDelta(selectionDelta);
		eventPublisher.triggerEvent(event);

	}

	public void upDownSelect(boolean isUp) {
		RecordVirtualArray virtualArray = recordVA;
		if (virtualArray == null)
			throw new IllegalStateException(
					"Virtual Array is required for selectNext Operation");
		int selectedElement = cursorSelect(virtualArray, recordSelectionManager, isUp);
		if (selectedElement < 0)
			return;
		createContentSelection(SelectionType.MOUSE_OVER, selectedElement);
	}

	public void leftRightSelect(boolean isLeft) {
		DimensionVirtualArray virtualArray = dimensionVA;
		if (virtualArray == null)
			throw new IllegalStateException(
					"Virtual Array is required for selectNext Operation");
		int selectedElement = cursorSelect(virtualArray, dimensionSelectionManager, isLeft);
		if (selectedElement < 0)
			return;
		createDimensionSelection(SelectionType.MOUSE_OVER, selectedElement);
	}

	public void enterPressedSelect() {
		DimensionVirtualArray virtualArray = dimensionVA;
		if (virtualArray == null)
			throw new IllegalStateException(
					"Virtual Array is required for enterPressed Operation");

		java.util.Set<Integer> elements = dimensionSelectionManager
				.getElements(SelectionType.MOUSE_OVER);
		Integer selectedElement = -1;
		if (elements.size() == 1) {
			selectedElement = (Integer) elements.toArray()[0];
			createDimensionSelection(SelectionType.SELECTION, selectedElement);
		}

		RecordVirtualArray contentVirtualArray = recordVA;
		if (contentVirtualArray == null)
			throw new IllegalStateException(
					"Virtual Array is required for enterPressed Operation");
		elements = recordSelectionManager.getElements(SelectionType.MOUSE_OVER);
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

		for (int recordIndex : recordVA.indicesOf(id)) {
			if (recordIndex == -1) {
				continue;
			}

			float xValue = renderStyle.getXCenter();

			float yValue = 0;

			yValue = getYCoordinateByContentIndex(recordIndex);
			yValue = viewFrustum.getHeight() - yValue;
			elementRep = new SelectedElementRep(recordIDType, uniqueID, xValue, yValue,
					0);

			alElementReps.add(elementRep);
		}

		return alElementReps;
	}

	/**
	 * Returns the y coordinate of the element rendered at recordIndex, or null
	 * if the current element is hidden
	 * 
	 * @param recordIndex
	 * @return
	 */
	public Float getYCoordinateByContentIndex(int recordIndex) {

		if (isHideElements()) {
			Integer recordID = recordVA.get(recordIndex);
			if (recordSelectionManager.checkStatus(SELECTION_HIDDEN, recordID))
				return null;
		}
		return template.getYCoordinateByContentIndex(recordIndex);

	}

	/**
	 * Returns the x coordinate of the element rendered at dimensionIndex
	 * 
	 * @param dimensionIndex
	 * @return
	 */
	public Float getXCoordinateByDimensionIndex(int dimensionIndex) {
		return template.getXCoordinateByDimensionIndex(dimensionIndex);
	}

	@Override
	public void renderContext(boolean bRenderOnlyContext) {

		this.bRenderOnlyContext = bRenderOnlyContext;

		if (this.bRenderOnlyContext) {
			recordVA = dataDomain.getRecordVA(DataTable.RECORD_CONTEXT);
		} else {
			recordVA = dataDomain.getRecordVA(DataTable.RECORD);
		}

		recordSelectionManager.setVA(recordVA);
		// renderStyle.setActiveVirtualArray(iRecordVAID);

		setDisplayListDirty();

	}

	@Override
	public void handleVAUpdate(RecordVADelta delta, String info) {

		super.handleVAUpdate(delta, info);

		if (delta.getVAType().equals(DataTable.RECORD_CONTEXT)
				&& recordVAType.equals(DataTable.RECORD_CONTEXT)) {
			ClusterState state = new ClusterState(EClustererAlgo.AFFINITY_PROPAGATION,
					ClustererType.RECORD_CLUSTERING,
					EDistanceMeasure.EUCLIDEAN_DISTANCE);
			int recordVAID = recordVA.getID();

			if (recordVA.size() == 0 || dimensionVA.size() == 0)
				return;

			state.setRecordVA(recordVA);
			state.setDimensionVA(dimensionVA);
			state.setAffinityPropClusterFactorGenes(4.0f);

			ClusterManager clusterManger = new ClusterManager(table);
			ClusterResult result = clusterManger.cluster(state);

			recordVA = result.getRecordResult().getRecordVA();
			recordSelectionManager.setVA(recordVA);
			recordVA.tableID(recordVAID);
		}
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedHeatMapView serializedForm = new SerializedHeatMapView(
				dataDomain.getDataDomainID());
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
				+ ", contentSize: " + recordVA.size() + ", dimensionSize: "
				+ dimensionVA.size() + ", recordVAType: " + recordVAType
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

	public void setRecordVA(RecordVirtualArray recordVA) {
		recordSelectionManager.setVA(recordVA);
		this.recordVA = recordVA;
		setDisplayListDirty();
	}

	public boolean isSendClearSelectionsEvent() {
		return sendClearSelectionsEvent;
	}

	public void setSendClearSelectionsEvent(boolean sendClearSelectionsEvent) {
		this.sendClearSelectionsEvent = sendClearSelectionsEvent;
	}

	public void setTable(DataTable set) {
		this.table = set;
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
			return recordVA.size()
					- recordSelectionManager.getNumberOfElements(SELECTION_HIDDEN);
		else
			return recordVA.size();
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
	 * @param recordID
	 *            the id of the element - since they can be of different height
	 *            due to the fish eye
	 * @return the height of the element
	 */
	public float getFieldHeight(int recordID) {
		return template.getElementHeight(recordID);
	}

	public float getFieldWidth(int dimensionID) {
		return template.getElementWidth(dimensionID);
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
		template.setStaticLayouts();
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
				recordSelectionManager.getElements(SelectionType.SELECTION));
		// zoomedElements.addAll(contentSelectionManager
		// .getElements(SelectionType.MOUSE_OVER));
		Iterator<Integer> elementIterator = zoomedElements.iterator();
		while (elementIterator.hasNext()) {
			int recordID = elementIterator.next();
			if (!recordVA.contains(recordID))
				elementIterator.remove();
			else if (recordSelectionManager.checkStatus(SELECTION_HIDDEN, recordID))
				elementIterator.remove();
		}
		return zoomedElements;
	}

	public DataTable getTable() {
		return table;
	}

	public AHeatMapTemplate getTemplate() {
		return template;
	}

	@Override
	public int getMinPixelHeight() {
		// TODO: Calculate depending on content
		// int pixelHeight = 10;
		// if (recordVA.size() > 1) {
		//
		// pixelHeight += (int) ((double) recordVA.size() / Math
		// .log(recordVA.size()));
		// }

		RecordVirtualArray setRecordVA = table.getRecordData(DataTable.RECORD)
				.getRecordVA();
		int numBricks = 1;
		if (setRecordVA.getGroupList() != null) {
			numBricks += setRecordVA.getGroupList().size();
		}

		int windowHeight = parentGLCanvas.getHeight();
		int pixelHeight = (int) (((float) (windowHeight - numBricks * 80) / (float) setRecordVA
				.size()) * recordVA.size());
		return Math.max(16, pixelHeight);
	}

	@Override
	public int getMinPixelHeight(DetailLevel detailLevel) {
		switch (detailLevel) {
		case HIGH:
			return getMinPixelHeight();
		case MEDIUM:
			return getMinPixelHeight();
		case LOW:
			return getMinPixelHeight();
		default:
			return 50;
		}
	}

	@Override
	public int getMinPixelWidth(DetailLevel detailLevel) {
		switch (detailLevel) {
		case HIGH:
			return Math.max(150, 16 * table.getMetaData().size());
		case MEDIUM:
			return Math.max(150, 16 * table.getMetaData().size());
		case LOW:
			return Math.max(150, 16 * table.getMetaData().size());
		default:
			return 100;
		}
	}

	@Override
	public void setFrustum(ViewFrustum viewFrustum) {
		super.setFrustum(viewFrustum);
		renderStyle = new HeatMapRenderStyle(this, viewFrustum);
		templateRenderer.setViewFrustum(viewFrustum);
	}

}
