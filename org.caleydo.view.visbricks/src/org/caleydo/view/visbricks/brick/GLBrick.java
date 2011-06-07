package org.caleydo.view.visbricks.brick;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import org.caleydo.core.data.selection.ContentSelectionManager;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.EVAOperation;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.event.data.RelationsUpdatedEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.picking.APickingListener;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.IPickingListener;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.IDataDomainSetBasedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.SelectionUpdateListener;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.ILayoutedElement;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.GLCoordinateUtils;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.manager.GeneticIDMappingHelper;
import org.caleydo.view.visbricks.GLVisBricks;
import org.caleydo.view.visbricks.brick.data.IBrickData;
import org.caleydo.view.visbricks.brick.data.IDimensionGroupData;
import org.caleydo.view.visbricks.brick.data.PathwayDimensionGroupData;
import org.caleydo.view.visbricks.brick.layout.ABrickLayoutTemplate;
import org.caleydo.view.visbricks.brick.layout.CompactBrickLayoutTemplate;
import org.caleydo.view.visbricks.brick.layout.CompactCentralBrickLayoutTemplate;
import org.caleydo.view.visbricks.brick.layout.DefaultBrickLayoutTemplate;
import org.caleydo.view.visbricks.brick.layout.IBrickConfigurer;
import org.caleydo.view.visbricks.brick.ui.AContainedViewRenderer;
import org.caleydo.view.visbricks.brick.ui.RelationIndicatorRenderer;
import org.caleydo.view.visbricks.dimensiongroup.DimensionGroup;
import org.caleydo.view.visbricks.event.AddGroupsToVisBricksEvent;
import org.caleydo.view.visbricks.listener.RelationsUpdatedListener;

/**
 * Individual Brick for VisBricks
 * 
 * @author Alexander Lex
 * 
 */
public class GLBrick extends AGLView implements IDataDomainSetBasedView,
		IGLRemoteRenderingView, ISelectionUpdateHandler, ILayoutedElement {

	public final static String VIEW_ID = "org.caleydo.view.brick";

	private LayoutManager templateRenderer;
	private ABrickLayoutTemplate brickLayout;
	private IBrickConfigurer brickConfigurer;
	private ElementLayout wrappingLayout;

	private AGLView currentRemoteView;

	private Map<EContainedViewType, AGLView> views;
	private Map<EContainedViewType, AContainedViewRenderer> containedViewRenderers;

	private int baseDisplayListIndex;
	private boolean isBaseDisplayListDirty = true;
	private EContainedViewType currentViewType;

	// /**
	// * Was the mouse over the brick area in the last frame.
	// */
	// private boolean wasMouseOverBrickArea = false;

	// private ISet set;
	// private GLHeatMap heatMap;
	private ASetBasedDataDomain dataDomain;

	private RelationIndicatorRenderer leftRelationIndicatorRenderer;
	private RelationIndicatorRenderer rightRelationIndicatorRenderer;

	private RelationsUpdatedListener relationsUpdateListener;
	private SelectionUpdateListener selectionUpdateListener;

	private BrickState expandedBrickState;

	/** The id of the group in the contentVA this brick is rendering. */
	private int groupID = -1;
	/** The group on which the contentVA of this brick is based on */
	private Group group;

	

	private GLVisBricks visBricks;
	private DimensionGroup dimensionGroup;
	private IBrickData brickData;

	private SelectionManager contentGroupSelectionManager;

	/** The average value of the data of this brick */
	// private double averageValue = Double.NaN;

	private boolean isInOverviewMode = false;

	// private boolean isDraggingActive = false;
	private float previousXCoordinate = Float.NaN;
	private float previousYCoordinate = Float.NaN;
	private boolean isBrickResizeActive = false;
	private boolean isSizeFixed = false;
	private boolean isInitialized = false;

	public GLBrick(GLCaleydoCanvas glCanvas, ViewFrustum viewFrustum) {
		super(glCanvas, viewFrustum, true);
		viewType = GLBrick.VIEW_ID;

		views = new HashMap<EContainedViewType, AGLView>();
		containedViewRenderers = new HashMap<EContainedViewType, AContainedViewRenderer>();

		

	}

	@Override
	public void initialize() {
		super.initialize();
		contentGroupSelectionManager = dataDomain
				.getContentGroupSelectionManager();
		registerPickingListeners();
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearAllSelections() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(GL2 gl) {
		textRenderer = new CaleydoTextRenderer(24);
		baseDisplayListIndex = gl.glGenLists(1);

		// if (set == null)
		// set = dataDomain.getSet();
		//
		// if (contentVA == null)
		// contentVA = set.getContentData(Set.CONTENT).getContentVA();
		//
		// if (storageVA == null) {
		// if (set.getStorageData(Set.STORAGE) != null)
		// storageVA = set.getStorageData(Set.STORAGE).getStorageVA();
		// }

		templateRenderer = new LayoutManager(viewFrustum);

		// if (set.getSetType().equals(ESetDataType.NUMERIC)) {
		// brickConfigurer = new NumericalDataConfigurer();
		// } else {
		// brickConfigurer = new NominalDataConfigurer();
		// }

		// TODO: Just for testing
		// brickConfigurer = new PathwayDataConfigurer();

		if (brickLayout == null) {

			brickLayout = new DefaultBrickLayoutTemplate(this, visBricks,
					dimensionGroup, brickConfigurer);

		}

		brickConfigurer.setBrickViews(this, gl, glMouseListener, brickLayout);

		currentViewType = brickLayout.getDefaultViewType();
		brickLayout
				.setViewRenderer(containedViewRenderers.get(currentViewType));
		currentRemoteView = views.get(currentRemoteView);
		visBricks.registerRemoteViewMouseWheelListener(brickLayout
				.getViewRenderer());

		templateRenderer.setTemplate(brickLayout);
		float defaultHeight = getParentGLCanvas()
				.getPixelGLConverter()
				.getGLHeightForPixelHeight(brickLayout.getDefaultHeightPixels());
		float defaultWidth = getParentGLCanvas().getPixelGLConverter()
				.getGLHeightForPixelHeight(brickLayout.getDefaultWidthPixels());
		wrappingLayout.setAbsoluteSizeY(defaultHeight);
		wrappingLayout.setAbsoluteSizeX(defaultWidth);
		templateRenderer.updateLayout();

		addPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {

				SelectionType currentSelectionType = contentGroupSelectionManager.getSelectionType();
				contentGroupSelectionManager
						.clearSelection(currentSelectionType);
				contentGroupSelectionManager.addToType(currentSelectionType,
						group.getID());

				SelectionUpdateEvent event = new SelectionUpdateEvent();
				event.setDataDomainType(getDataDomain().getDataDomainType());
				event.setSender(this);
				SelectionDelta delta = contentGroupSelectionManager.getDelta();
				event.setSelectionDelta(delta);
				GeneralManager.get().getEventPublisher().triggerEvent(event);

				showHandles();

				selectElementsByGroup();
			}

			@Override
			public void mouseOver(Pick pick) {
				showHandles();
				// updateSelection();
			}

			@Override
			public void rightClicked(Pick pick) {

				HashMap<PathwayGraph, Integer> hashPathwaysToOccurences = new HashMap<PathwayGraph, Integer>();

				for (Integer gene : contentVA) {
					java.util.Set<Integer> davids = GeneralManager
							.get()
							.getIDMappingManager()
							.getIDAsSet(dataDomain.getContentIDType(),
									dataDomain.getPrimaryContentMappingType(),
									gene);
					if (davids == null || davids.size() == 0)
						continue;
					for (Integer david : davids) {
						java.util.Set<PathwayGraph> pathwayGraphs = GeneticIDMappingHelper
								.get()
								.getPathwayGraphsByGeneID(
										dataDomain
												.getPrimaryContentMappingType(),
										david);

						// int iPathwayCount = 0;
						if (pathwayGraphs != null) {
							// iPathwayCount = pathwayGraphs.size();

							for (PathwayGraph pathwayGraph : pathwayGraphs) {

								if (!hashPathwaysToOccurences
										.containsKey(pathwayGraph))
									hashPathwaysToOccurences.put(pathwayGraph,
											1);
								else {
									int occurences = hashPathwaysToOccurences
											.get(pathwayGraph);
									occurences++;
									hashPathwaysToOccurences.put(pathwayGraph,
											occurences);
								}

							}
						}
					}
				}

				ArrayList<PathwayGraph> pathways = new ArrayList<PathwayGraph>();

				for (PathwayGraph pathway : hashPathwaysToOccurences.keySet()) {
					if (hashPathwaysToOccurences.get(pathway) >= 10)
						pathways.add(pathway);
				}

				AddGroupsToVisBricksEvent event = new AddGroupsToVisBricksEvent();
				ArrayList<IDimensionGroupData> dimensionGroupData = new ArrayList<IDimensionGroupData>();
				PathwayDimensionGroupData pathwayDimensionGroupData = new PathwayDimensionGroupData(
						DataDomainManager.get().getDataDomain(
								"org.caleydo.datadomain.pathway"), dataDomain,
						pathways);
				dimensionGroupData.add(pathwayDimensionGroupData);
				event.setDimensionGroupData(dimensionGroupData);
				event.setSender(this);
				eventPublisher.triggerEvent(event);

			}

			public void showHandles() {
				// System.out.println("picked brick");
				if (brickLayout.isShowHandles())
					return;

				ArrayList<DimensionGroup> dimensionGroups = dimensionGroup
						.getVisBricksView().getDimensionGroupManager()
						.getDimensionGroups();

				for (DimensionGroup dimensionGroup : dimensionGroups) {
					dimensionGroup.hideHandles();
				}
				if (!brickLayout.isShowHandles()) {
					brickLayout.setShowHandles(true);
					templateRenderer.updateLayout();
				}

			}

		}, EPickingType.BRICK, getID());

		dimensionGroup.updateLayout();

		isInitialized = true;

	}

	private void selectElementsByGroup() {

		// Select all elements in group with special type

		ContentSelectionManager contentSelectionManager = visBricks
				.getContentSelectionManager();
		SelectionType selectedByGroupSelectionType = contentSelectionManager
				.getSelectionType();

		if (!visBricks.getKeyListener().isCtrlDown()) {
			contentSelectionManager
					.clearSelection(selectedByGroupSelectionType);

			// ClearSelectionsEvent cse = new ClearSelectionsEvent();
			// cse.setSender(this);
			// eventPublisher.triggerEvent(cse);
		}

		// Prevent selection for center brick as this would select all elements
		if (dimensionGroup.getCenterBrick() == this)
			return;

		for (Integer contentID : contentVA) {
			contentSelectionManager.addToType(selectedByGroupSelectionType,
					contentID);
		}

		SelectionUpdateEvent event = new SelectionUpdateEvent();
		event.setDataDomainType(getDataDomain().getDataDomainType());
		event.setSender(this);
		SelectionDelta delta = contentSelectionManager.getDelta();
		event.setSelectionDelta(delta);
		GeneralManager.get().getEventPublisher().triggerEvent(event);
	}

	@Override
	protected void initLocal(GL2 gl) {
		init(gl);

	}

	@Override
	public void initRemote(GL2 gl, AGLView glParentView,
			GLMouseListener glMouseListener) {
		init(gl);

	}

	@Override
	public void display(GL2 gl) {
		if (currentRemoteView != null)
			currentRemoteView.processEvents();
		processEvents();
		handleBrickResize(gl);
		// GLHelperFunctions.drawViewFrustum(gl, viewFrustum);

		if (isBaseDisplayListDirty)
			buildBaseDisplayList(gl);

		// if (!isMouseOverBrickArea && brickLayout.isShowHandles()) {
		// brickLayout.setShowHandles(false);
		// templateRenderer.updateLayout();
		// }

		// if(!isMouseOverBrickArea && wasMouseOverBrickArea) {
		// brickLayout.setShowHandles(false);
		// templateRenderer.updateLayout();
		// }

		templateRenderer.render(gl);

		gl.glPushName(getPickingManager().getPickingID(getID(),
				EPickingType.BRICK, getID()));
		gl.glColor4f(1.0f, 0.0f, 0.0f, 0f);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(wrappingLayout.getSizeScaledX(), 0, 0);
		gl.glVertex3f(wrappingLayout.getSizeScaledX(),
				wrappingLayout.getSizeScaledY(), 0);
		gl.glVertex3f(0, wrappingLayout.getSizeScaledY(), 0);
		gl.glEnd();
		gl.glPopName();

		gl.glCallList(baseDisplayListIndex);

		// textRenderer.renderText(gl, ""+groupID, 0.5f, 0, 0);

		// isMouseOverBrickArea = false;
	}

	@Override
	protected void displayLocal(GL2 gl) {
		pickingManager.handlePicking(this, gl);
		checkForHits(gl);
		display(gl);

	}

	@Override
	public void displayRemote(GL2 gl) {

		// float brickGLBoundingBoxStartX = wrappingLayout.getTranslateX();
		// float brickGLBoundingBoxStartY = wrappingLayout.getTranslateY();
		// float brickGLBoundingBoxEndX = wrappingLayout.getTranslateX()
		// + wrappingLayout.getSizeScaledX();
		// float brickGLBoundingBoxEndY = wrappingLayout.getTranslateY()
		// + wrappingLayout.getSizeScaledY();
		//
		//
		//
		// PixelGLConverter pixelGLConverter = getParentGLCanvas()
		// .getPixelGLConverter();
		// int brickPixelBoundingBoxStartX = (int) pixelGLConverter
		// .getPixelWidthForGLWidth(brickGLBoundingBoxStartX);
		// int brickPixelBoundingBoxStartY = (int) pixelGLConverter
		// .getPixelHeightForGLHeight(brickGLBoundingBoxStartY);
		// int brickPixelBoundingBoxEndX = (int) pixelGLConverter
		// .getPixelWidthForGLWidth(brickGLBoundingBoxEndX);
		// int brickPixelBoundingBoxEndY = (int) pixelGLConverter
		// .getPixelHeightForGLHeight(brickGLBoundingBoxEndY);
		//
		// Point mousePosition = glMouseListener.getPickedPoint();

		// if ((mousePosition.x >= brickPixelBoundingBoxStartX)
		// && (mousePosition.x <= brickPixelBoundingBoxEndX)) {
		// contentGroupSelectionManager
		// .clearSelection(SelectionType.SELECTION);
		// contentGroupSelectionManager.addToType(SelectionType.SELECTION,
		// group.getID());
		//
		// SelectionUpdateEvent event = new SelectionUpdateEvent();
		// event.setDataDomainType(getDataDomain().getDataDomainType());
		// event.setSender(this);
		// SelectionDelta delta = contentGroupSelectionManager.getDelta();
		// event.setSelectionDelta(delta);
		// GeneralManager.get().getEventPublisher().triggerEvent(event);
		//
		// if (!brickLayout.isShowHandles()) {
		// brickLayout.setShowHandles(true);
		// templateRenderer.updateLayout();
		// }
		// }

		checkForHits(gl);
		display(gl);

		// gl.glColor4f(1,0,0,0.5f);
		// gl.glBegin(GL2.GL_QUADS);
		// gl.glVertex3f(-brickGLBoundingBoxStartX, brickGLBoundingBoxStartY,
		// 0);
		// gl.glVertex3f(
		// -brickGLBoundingBoxStartX + wrappingLayout.getSizeScaledX(),
		// brickGLBoundingBoxStartY, 0);
		// gl.glVertex3f(
		// -brickGLBoundingBoxStartX + wrappingLayout.getSizeScaledX(),
		// brickGLBoundingBoxStartY + wrappingLayout.getSizeScaledY(), 0);
		// gl.glVertex3f(-brickGLBoundingBoxStartX, brickGLBoundingBoxStartY
		// + wrappingLayout.getSizeScaledY(), 0);
		// gl.glEnd();

	}

	private void buildBaseDisplayList(GL2 gl) {
		gl.glNewList(baseDisplayListIndex, GL2.GL_COMPILE);
		// templateRenderer.updateLayout();

		gl.glEndList();
		isBaseDisplayListDirty = false;
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {

		super.reshape(drawable, x, y, width, height);
		if (templateRenderer != null)
			templateRenderer.updateLayout();

		if (!isSizeFixed) {
			wrappingLayout
					.setAbsoluteSizeX(brickLayout.getDefaultWidthPixels());
			wrappingLayout
					.setAbsoluteSizeY(brickLayout.getDefaultWidthPixels());
		}
	}

	@Override
	protected void handlePickingEvents(EPickingType pickingType,
			EPickingMode pickingMode, int pickingID, Pick pick) {

//		HashMap<Integer, IPickingListener> map = pickingListeners
//				.get(pickingType);
//		if (map == null)
//			return;
//
//		IPickingListener pickingListener = map.get(pickingID);
//
//		if (pickingListener == null)
//			return;
//
//		switch (pickingMode) {
//		case CLICKED:
//			pickingListener.clicked(pick);
//			break;
//		case DOUBLE_CLICKED:
//			pickingListener.doubleClicked(pick);
//			break;
//		case RIGHT_CLICKED:
//			pickingListener.rightClicked(pick);
//			break;
//		case MOUSE_OVER:
//			pickingListener.mouseOver(pick);
//			break;
//		case DRAGGED:
//			pickingListener.dragged(pick);
//			break;
//		}

		// switch (pickingType) {
		// case BRICK_CLUSTER:
		// switch (pickingMode) {
		// case CLICKED:
		// // set.cluster(clusterState);
		// System.out.println("cluster");
		//
		// getParentGLCanvas().getParentComposite().getDisplay()
		// .asyncExec(new Runnable() {
		// @Override
		// public void run() {
		// StartClusteringDialog dialog = new StartClusteringDialog(
		// new Shell(), dataDomain);
		// dialog.open();
		// ClusterState clusterState = dialog
		// .getClusterState();
		//
		// StartClusteringEvent event = null;
		// // if (clusterState != null && set != null)
		//
		// event = new StartClusteringEvent(clusterState,
		// set.getID());
		// event.setDataDomainType(dataDomain
		// .getDataDomainType());
		// GeneralManager.get().getEventPublisher()
		// .triggerEvent(event);
		// }
		// });
		//
		// }
		// }

	}

	/** resize of a brick */
	private void handleBrickResize(GL2 gl) {

		if (!isBrickResizeActive)
			return;

		// TODO: resizing in all layouts?
		isSizeFixed = true;
		brickLayout.setLockResizing(true);

		if (glMouseListener.wasMouseReleased()) {
			isBrickResizeActive = false;
			previousXCoordinate = Float.NaN;
			previousYCoordinate = Float.NaN;
			return;
		}

		Point currentPoint = glMouseListener.getPickedPoint();

		float[] pointCordinates = GLCoordinateUtils
				.convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x,
						currentPoint.y);

		if (Float.isNaN(previousXCoordinate)) {
			previousXCoordinate = pointCordinates[0];
			previousYCoordinate = pointCordinates[1];
			return;
		}

		float changeX = pointCordinates[0] - previousXCoordinate;
		float changeY = -(pointCordinates[1] - previousYCoordinate);

		float width = wrappingLayout.getSizeScaledX();
		float height = wrappingLayout.getSizeScaledY();
		float changePercentage = changeX / width;

		float newWidth = width + changeX;
		float newHeight = height + changeY;

		float minWidth = parentGLCanvas.getPixelGLConverter()
				.getGLWidthForPixelWidth(brickLayout.getMinWidthPixels());
		float minHeight = parentGLCanvas.getPixelGLConverter()
				.getGLHeightForPixelHeight(brickLayout.getMinHeightPixels());
		// float minWidth = parentGLCanvas.getPixelGLConverter()
		// .getGLWidthForPixelWidth(brickLayout.getMinWidthPixels());
		if (newWidth < minWidth - 0.001f) {
			newWidth = minWidth;
		}

		if (newHeight < minHeight - 0.001f) {
			newHeight = minHeight;
		}

		previousXCoordinate = pointCordinates[0];
		previousYCoordinate = pointCordinates[1];

		wrappingLayout.setAbsoluteSizeX(newWidth);
		wrappingLayout.setAbsoluteSizeY(newHeight);

		// templateRenderer.updateLayout();
		// dimensionGroup.updateLayout();
		// groupColumn.setAbsoluteSizeX(width + changeX);

		// float height = wrappingLayout.getSizeScaledY();
		// wrappingLayout.setAbsoluteSizeY(height * (1 + changePercentage));

		// centerBrick.getLayout().updateSubLayout();

		visBricks.setLastResizeDirectionWasToLeft(false);
		visBricks.updateLayout();
		visBricks.updateConnectionLinesBetweenDimensionGroups();

	}

	@Override
	public String getShortInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDetailedInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void broadcastElements(EVAOperation type) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getNumberOfSelections(SelectionType SelectionType) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setDataDomain(ASetBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	@Override
	public ASetBasedDataDomain getDataDomain() {
		// TODO Auto-generated method stub
		return dataDomain;
	}

	/**
	 * Set the Set this brick's data corresponds to.
	 * 
	 * @param set
	 */
	// public void setSet(ISet set) {
	// this.set = set;
	// if (set.getSetType().equals(ESetDataType.NUMERIC)) {
	// brickConfigurer = new NumericalDataConfigurer();
	// } else {
	// brickConfigurer = new NominalDataConfigurer();
	// }
	// }

	/**
	 * Set the contentVA this brick should render plus the groupID that is
	 * associated with this contentVA.
	 * 
	 * @param groupID
	 * @param contentVA
	 */
	public void setContentVA(Group group, ContentVirtualArray contentVA) {
		this.group = group;
		if (group != null)
			this.groupID = group.getGroupID();
		this.contentVA = contentVA;
	}

	/**
	 * Set the group of this brick.
	 * 
	 * @param group
	 */
	public void setGroup(Group group) {
		this.group = group;
		this.groupID = group.getGroupID();
	}

	/**
	 * Set the {@link GLVisBricks} view managing this brick, which is needed for
	 * environment information.
	 * 
	 * @param visBricks
	 */
	public void setVisBricks(GLVisBricks visBricks) {
		this.visBricks = visBricks;
	}

	/**
	 * Set the {@link DimensionGroup} this brick belongs to.
	 * 
	 * @param dimensionGroup
	 */
	public void setDimensionGroup(DimensionGroup dimensionGroup) {
		this.dimensionGroup = dimensionGroup;
	}

	/**
	 * Returns the {@link DimensionGroup} this brick belongs to.
	 * 
	 * @return
	 */
	public DimensionGroup getDimensionGroup() {
		return dimensionGroup;
	}

	/**
	 * Returns the group ID of the data this brick is currently rendering
	 * 
	 * @return
	 */
	public int getGroupID() {
		return groupID;
	}

	/**
	 * Returns the group on which the contentVA of this brick is based on.
	 * 
	 * @return
	 */
	public Group getGroup() {
		return group;
	}

	@Override
	public List<AGLView> getRemoteRenderedViews() {
		// TODO Auto-generated method stub
		return null;
	}

	// private void calculateAverageValueForBrick() {
	// averageValue = 0;
	// int count = 0;
	// if (contentVA == null)
	// throw new IllegalStateException("contentVA was null");
	// for (Integer contenID : contentVA) {
	// StorageData storageData = set.getStorageData(Set.STORAGE);
	// if (storageData == null) {
	// averageValue = 0;
	// return;
	// }
	//
	// StorageVirtualArray storageVA = storageData.getStorageVA();
	//
	// if (storageVA == null) {
	// averageValue = 0;
	// return;
	// }
	// for (Integer storageID : storageVA) {
	// float value = set.get(storageID).getFloat(EDataRepresentation.NORMALIZED,
	// contenID);
	// if (!Float.isNaN(value)) {
	// averageValue += value;
	// count++;
	// }
	// }
	// }
	// averageValue /= count;
	//
	// }

	// public double getAverageValue() {
	// if (Double.isNaN(averageValue))
	// calculateAverageValueForBrick();
	// return averageValue;
	// }

	@Override
	public void setFrustum(ViewFrustum viewFrustum) {
		super.setFrustum(viewFrustum);
		if (templateRenderer != null)
			templateRenderer.updateLayout();
	}

	

	// public ISet getSet() {
	// return set;
	// }

	/**
	 * Sets the type of view that should be rendered in the brick. The view type
	 * is not set, if it is not valid for the current brick layout.
	 * 
	 * @param viewType
	 */
	public void setContainedView(EContainedViewType viewType) {

		AContainedViewRenderer viewRenderer = containedViewRenderers
				.get(viewType);

		if (viewRenderer == null)
			return;

		if (!brickLayout.isViewTypeValid(viewType))
			return;

		currentRemoteView = views.get(viewType);
		visBricks.unregisterRemoteViewMouseWheelListener(brickLayout
				.getViewRenderer());
		brickLayout.setViewRenderer(viewRenderer);
		visBricks.registerRemoteViewMouseWheelListener(brickLayout
				.getViewRenderer());
		brickLayout.viewTypeChanged(viewType);
		int defaultHeightPixels = brickLayout.getDefaultHeightPixels();
		int defaultWidthPixels = brickLayout.getDefaultWidthPixels();
		float defaultHeight = getParentGLCanvas().getPixelGLConverter()
				.getGLHeightForPixelHeight(defaultHeightPixels);
		float defaultWidth = getParentGLCanvas().getPixelGLConverter()
				.getGLWidthForPixelWidth(defaultWidthPixels);

		if (isSizeFixed) {

			float currentHeight = wrappingLayout.getSizeScaledY();
			float currentWidth = wrappingLayout.getSizeScaledX();

			if (currentHeight < defaultHeight) {
				// float width = (wrappingLayout.getSizeScaledX() /
				// currentHeight)
				// * minHeight;
				// wrappingLayout.setAbsoluteSizeX(width);
				wrappingLayout.setAbsoluteSizeY(defaultHeight);
			}
			if (currentWidth < defaultWidth) {
				wrappingLayout.setAbsoluteSizeX(defaultWidth);
			}
		} else {
			wrappingLayout.setAbsoluteSizeY(defaultHeight);
			wrappingLayout.setAbsoluteSizeX(defaultWidth);
		}

		templateRenderer.updateLayout();

		visBricks.updateLayout();
		visBricks.updateConnectionLinesBetweenDimensionGroups();

		currentViewType = viewType;
	}

	public TextureManager getTextureManager() {
		return textureManager;
	}

	/**
	 * Sets the {@link ABrickLayoutTemplate} for this brick, specifying its
	 * appearance. If the specified view type is valid, it will be set,
	 * otherwise the default view type will be set.
	 * 
	 * @param brickLayoutTemplate
	 * @param viewType
	 */
	public void setBrickLayoutTemplate(
			ABrickLayoutTemplate brickLayoutTemplate,
			EContainedViewType viewType) {
		this.brickLayout = brickLayoutTemplate;
		if ((brickLayout instanceof CompactBrickLayoutTemplate)
				|| (brickLayout instanceof CompactCentralBrickLayoutTemplate))
			isInOverviewMode = true;
		else
			isInOverviewMode = false;

		if (templateRenderer != null) {
			templateRenderer.setTemplate(brickLayout);
			if (brickLayout.isViewTypeValid(viewType)) {
				setContainedView(viewType);
			} else {
				setContainedView(brickLayout.getDefaultViewType());
			}
		}
	}

	/**
	 * @return Type of view that is currently displayed by the brick.
	 */
	public EContainedViewType getCurrentViewType() {
		return currentViewType;
	}

	@Override
	public void registerEventListeners() {

		relationsUpdateListener = new RelationsUpdatedListener();
		relationsUpdateListener.setHandler(this);
		relationsUpdateListener.setExclusiveDataDomainType(dataDomain
				.getDataDomainType());
		eventPublisher.addListener(RelationsUpdatedEvent.class,
				relationsUpdateListener);

		selectionUpdateListener = new SelectionUpdateListener();
		selectionUpdateListener.setHandler(this);
		selectionUpdateListener.setExclusiveDataDomainType(dataDomain
				.getDataDomainType());
		eventPublisher.addListener(SelectionUpdateEvent.class,
				selectionUpdateListener);
	}

	@Override
	public void unregisterEventListeners() {

		if (relationsUpdateListener != null) {
			eventPublisher.removeListener(relationsUpdateListener);
			relationsUpdateListener = null;
		}

		if (selectionUpdateListener != null) {
			eventPublisher.removeListener(selectionUpdateListener);
			selectionUpdateListener = null;
		}
		
		visBricks.unregisterRemoteViewMouseWheelListener(brickLayout
				.getViewRenderer());
	}

	private void registerPickingListeners() {
		addPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				isBrickResizeActive = true;

			}
		}, EPickingType.RESIZE_HANDLE_LOWER_RIGHT, 1);
	}

	/**
	 * Only to be called via a {@link RelationsUpdatedListener} upon a
	 * {@link RelationsUpdatedEvent}.
	 * 
	 * TODO: add parameters to check whether this brick needs to be updated
	 */
	public void relationsUpdated() {
		if (rightRelationIndicatorRenderer != null
				&& leftRelationIndicatorRenderer != null) {
			rightRelationIndicatorRenderer.updateRelations();
			leftRelationIndicatorRenderer.updateRelations();
		}
	}

	@Override
	public String toString() {
		return "Brick: " + groupID + " in ";// + set.getLabel();

	}

	/**
	 * Set the layout that this view is embedded in
	 * 
	 * @param wrappingLayout
	 */
	public void setLayout(ElementLayout wrappingLayout) {
		this.wrappingLayout = wrappingLayout;
	}

	/**
	 * Returns the layout that this view is wrapped in, which is created by the
	 * same instance that creates the view.
	 * 
	 * @return
	 */
	@Override
	public ElementLayout getLayout() {
		return wrappingLayout;
	}

	/**
	 * Returns the selection manager responsible for managing selections of
	 * groups.
	 * 
	 * @return
	 */
	public SelectionManager getContentGroupSelectionManager() {
		return contentGroupSelectionManager;
	}

	@Override
	public void handleSelectionUpdate(ISelectionDelta selectionDelta,
			boolean scrollToSelection, String info) {
		if (selectionDelta.getIDType() == contentGroupSelectionManager
				.getIDType()) {
			contentGroupSelectionManager.setDelta(selectionDelta);
			if (group == null)
				return;
			if (contentGroupSelectionManager.checkStatus(
					contentGroupSelectionManager.getSelectionType(), getGroup()
							.getID())) {
				brickLayout.setShowHandles(true);
				visBricks.updateConnectionLinesBetweenDimensionGroups();
			} else {
				brickLayout.setShowHandles(false);
			}
			templateRenderer.updateLayout();
		}
	}

	/**
	 * @return true, if the brick us currently selected, false otherwise
	 */
	public boolean isActive() {
		return contentGroupSelectionManager.checkStatus(
				SelectionType.SELECTION, getGroup().getID());
	}

	/**
	 * Sets this brick collapsed
	 * 
	 * @return how much this has affected the height of the brick.
	 */
	public float collapse() {
		// if (isInOverviewMode)
		// return 0;

		if (!isInOverviewMode && isInitialized) {
			expandedBrickState = new BrickState(currentViewType,
					wrappingLayout.getSizeScaledY(),
					wrappingLayout.getSizeScaledX());
		}

		ABrickLayoutTemplate layoutTemplate = brickLayout
				.getCollapsedLayoutTemplate();
		// isSizeFixed = false;

		setBrickLayoutTemplate(layoutTemplate,
				layoutTemplate.getDefaultViewType());

		float minHeight = getParentGLCanvas().getPixelGLConverter()
				.getGLHeightForPixelHeight(layoutTemplate.getMinHeightPixels());
		float minWidth = getParentGLCanvas().getPixelGLConverter()
				.getGLHeightForPixelHeight(layoutTemplate.getMinWidthPixels());
		float currentSize = wrappingLayout.getSizeScaledY();
		wrappingLayout.setAbsoluteSizeY(minHeight);
		wrappingLayout.setAbsoluteSizeX(minWidth);

		visBricks.updateLayout();
		visBricks.updateConnectionLinesBetweenDimensionGroups();

		return currentSize - minHeight;
	}

	public void expand() {
		// if (!isInOverviewMode)
		// return;

		ABrickLayoutTemplate layoutTemplate = brickLayout
				.getExpandedLayoutTemplate();

		if (expandedBrickState != null) {
			setBrickLayoutTemplate(layoutTemplate,
					expandedBrickState.getViewType());
			wrappingLayout.setAbsoluteSizeX(expandedBrickState.getWidth());
			wrappingLayout.setAbsoluteSizeY(expandedBrickState.getHeight());
		} else {
			setBrickLayoutTemplate(layoutTemplate, currentViewType);
			float defaultHeight = getParentGLCanvas().getPixelGLConverter()
					.getGLHeightForPixelHeight(
							layoutTemplate.getDefaultHeightPixels());
			float defaultWidth = getParentGLCanvas().getPixelGLConverter()
					.getGLWidthForPixelWidth(
							layoutTemplate.getDefaultWidthPixels());
			wrappingLayout.setAbsoluteSizeY(defaultHeight);
			wrappingLayout.setAbsoluteSizeX(defaultWidth);
		}
		isInOverviewMode = false;
		isSizeFixed = true;
		brickLayout.setLockResizing(true);

		visBricks.updateLayout();
		visBricks.updateConnectionLinesBetweenDimensionGroups();
	}

	public boolean isInOverviewMode() {
		return isInOverviewMode;
	}

	/**
	 * Sets, whether view switching by this brick should affect other bricks in
	 * the dimension group.
	 * 
	 * @param isGlobalViewSwitching
	 */
	public void setGlobalViewSwitching(boolean isGlobalViewSwitching) {
		brickLayout.setGlobalViewSwitching(isGlobalViewSwitching);
	}

	public void setCurrentRemoteView(AGLView currentRemoteView) {
		this.currentRemoteView = currentRemoteView;
	}

	public void setViews(Map<EContainedViewType, AGLView> views) {
		this.views = views;
	}

	public void setContainedViewRenderers(
			Map<EContainedViewType, AContainedViewRenderer> containedViewRenderers) {
		this.containedViewRenderers = containedViewRenderers;
	}

	public void setCurrentViewType(EContainedViewType currentViewType) {
		this.currentViewType = currentViewType;
	}

	public boolean isSizeFixed() {
		return isSizeFixed;
	}

	public void setSizeFixed(boolean isSizeFixed) {
		this.isSizeFixed = isSizeFixed;
	}

	/**
	 * Hides the handles of the brick.
	 */
	public void hideHandles() {
		brickLayout.setShowHandles(false);
		templateRenderer.updateLayout();
	}

	public ElementLayout getWrappingLayout() {
		return wrappingLayout;
	}

	public void setBrickData(IBrickData brickData) {
		this.brickData = brickData;
		brickData.setBrickData(this);
	}

	public IBrickData getBrickData() {
		return brickData;
	}

	public IBrickConfigurer getBrickConfigurer() {
		return brickConfigurer;
	}

	public void setBrickConfigurer(IBrickConfigurer brickConfigurer) {
		this.brickConfigurer = brickConfigurer;
	}

}
