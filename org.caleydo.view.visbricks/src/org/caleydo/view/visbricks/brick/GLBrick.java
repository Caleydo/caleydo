package org.caleydo.view.visbricks.brick;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.InvalidAttributeValueException;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;

import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.selection.RecordSelectionManager;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.events.SelectionUpdateListener;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.event.data.RelationsUpdatedEvent;
import org.caleydo.core.event.view.tablebased.SelectionUpdateEvent;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.ATableBasedView;
import org.caleydo.core.view.opengl.canvas.listener.IMouseWheelHandler;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.ILayoutedElement;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.core.view.opengl.util.GLCoordinateUtils;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.datadomain.pathway.data.PathwayDimensionGroupData;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.view.visbricks.GLVisBricks;
import org.caleydo.view.visbricks.brick.contextmenu.CreatePathwayGroupFromDataItem;
import org.caleydo.view.visbricks.brick.layout.ABrickLayoutTemplate;
import org.caleydo.view.visbricks.brick.layout.CompactBrickLayoutTemplate;
import org.caleydo.view.visbricks.brick.layout.CompactCentralBrickLayoutTemplate;
import org.caleydo.view.visbricks.brick.layout.DefaultBrickLayoutTemplate;
import org.caleydo.view.visbricks.brick.layout.IBrickConfigurer;
import org.caleydo.view.visbricks.brick.ui.RelationIndicatorRenderer;
import org.caleydo.view.visbricks.dialog.CreatePathwayComparisonGroupDialog;
import org.caleydo.view.visbricks.dimensiongroup.DimensionGroup;
import org.caleydo.view.visbricks.event.AddGroupsToVisBricksEvent;
import org.caleydo.view.visbricks.event.OpenCreatePathwayGroupDialogEvent;
import org.caleydo.view.visbricks.listener.OpenCreatePathwayGroupDialogListener;
import org.caleydo.view.visbricks.listener.RelationsUpdatedListener;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**
 * Individual Brick for VisBricks
 * 
 * @author Alexander Lex
 * 
 */
public class GLBrick extends ATableBasedView implements IGLRemoteRenderingView,
		ILayoutedElement {

	public final static String VIEW_TYPE = "org.caleydo.view.brick";

	private LayoutManager templateRenderer;
	private ABrickLayoutTemplate brickLayout;
	private IBrickConfigurer brickConfigurer;
	private ElementLayout wrappingLayout;

	private AGLView currentRemoteView;

	private Map<EContainedViewType, AGLView> views;
	private Map<EContainedViewType, LayoutRenderer> containedViewRenderers;

	private int baseDisplayListIndex;
	private boolean isBaseDisplayListDirty = true;
	private EContainedViewType currentViewType;

	// /**
	// * Was the mouse over the brick area in the last frame.
	// */
	// private boolean wasMouseOverBrickArea = false;

	// private DataTable set;
	// private GLHeatMap heatMap;

	private RelationIndicatorRenderer leftRelationIndicatorRenderer;
	private RelationIndicatorRenderer rightRelationIndicatorRenderer;

	private RelationsUpdatedListener relationsUpdateListener;
	private OpenCreatePathwayGroupDialogListener openCreatePathwayGroupDialogListener;

	private BrickState expandedBrickState;

	/** The id of the group in the recordVA this brick is rendering. */
	// private int groupID = -1;
	/** The group on which the recordVA of this brick is based on */
	// private Group group;

	private GLVisBricks visBricks;
	private DimensionGroup dimensionGroup;

	private SelectionManager recordGroupSelectionManager;

	/** The average value of the data of this brick */
	// private double averageValue = Double.NaN;

	private boolean isInOverviewMode = false;

	// private boolean isDraggingActive = false;
	private float previousXCoordinate = Float.NaN;
	private float previousYCoordinate = Float.NaN;
	private boolean isBrickResizeActive = false;
	private boolean isSizeFixed = false;
	private boolean isInitialized = false;

	public GLBrick(GLCanvas glCanvas, Composite parentComposite, ViewFrustum viewFrustum) {

		super(glCanvas, parentComposite, viewFrustum);
		viewType = GLBrick.VIEW_TYPE;
		label = "Brick";

		views = new HashMap<EContainedViewType, AGLView>();
		containedViewRenderers = new HashMap<EContainedViewType, LayoutRenderer>();

	}

	@Override
	public void initialize() {
		super.initialize();
		recordGroupSelectionManager = new SelectionManager(
				DataContainer.DATA_CONTAINER_IDTYPE);
		registerPickingListeners();
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init(GL2 gl) {
		textRenderer = new CaleydoTextRenderer(24);
		baseDisplayListIndex = gl.glGenLists(1);

		templateRenderer = new LayoutManager(viewFrustum);

		if (brickLayout == null) {

			brickLayout = new DefaultBrickLayoutTemplate(this, visBricks, dimensionGroup,
					brickConfigurer);

		}

		brickConfigurer.setBrickViews(this, gl, glMouseListener, brickLayout);

		currentViewType = brickLayout.getDefaultViewType();
		brickLayout.setViewRenderer(containedViewRenderers.get(currentViewType));
		currentRemoteView = views.get(currentViewType);
		if (brickLayout.getViewRenderer() instanceof IMouseWheelHandler) {
			visBricks.registerMouseWheelListener((IMouseWheelHandler) brickLayout
					.getViewRenderer());
		}

		templateRenderer.setTemplate(brickLayout);
		float defaultHeight = pixelGLConverter.getGLHeightForPixelHeight(brickLayout
				.getDefaultHeightPixels());
		float defaultWidth = pixelGLConverter.getGLWidthForPixelWidth(brickLayout
				.getDefaultWidthPixels());
		wrappingLayout.setAbsoluteSizeY(defaultHeight);
		wrappingLayout.setAbsoluteSizeX(defaultWidth);
		templateRenderer.updateLayout();

		addSingleIDPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {

				SelectionType currentSelectionType = recordGroupSelectionManager.getSelectionType();
				recordGroupSelectionManager.clearSelection(currentSelectionType);

				recordGroupSelectionManager.addToType(currentSelectionType,
						dataContainer.getID());

				SelectionUpdateEvent event = new SelectionUpdateEvent();
				event.setDataDomainID(getDataDomain().getDataDomainID());
				event.setSender(this);
				SelectionDelta delta = recordGroupSelectionManager.getDelta();
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

				contextMenuCreator.addContextMenuItem(new CreatePathwayGroupFromDataItem(
						dataDomain, dataContainer.getRecordPerspective()
								.getVirtualArray(), dimensionGroup.getDataContainer()
								.getDimensionPerspective()));

				// HashMap<PathwayGraph, Integer> hashPathwaysToOccurences = new
				// HashMap<PathwayGraph, Integer>();
				//
				// Set<Integer> davids = null;
				//
				// if (dataDomain.isColumnDimension()) {
				// for (Integer gene : dataContainer.getRecordPerspective()
				// .getVirtualArray()) {
				// davids = dataDomain.getRecordIDMappingManager().getIDAsSet(
				// dataDomain.getRecordIDType(),
				// dataDomain.getPrimaryRecordMappingType(), gene);
				// }
				// } else {
				// for (Integer gene : dataContainer.getRecordPerspective()
				// .getVirtualArray()) {
				// davids =
				// dataDomain.getDimensionIDMappingManager().getIDAsSet(
				// dataDomain.getDimensionIDType(),
				// dataDomain.getPrimaryDimensionMappingType(), gene);
				// }
				// }
				//
				// for (Integer david : davids) {
				// PathwayDataDomain pathwayDataDomain = (PathwayDataDomain)
				// DataDomainManager
				// .get()
				// .getDataDomainByType(PathwayDataDomain.DATA_DOMAIN_TYPE);
				// Set<PathwayGraph> pathwayGraphs = pathwayDataDomain
				// .getMappingHelper().getPathwayGraphsByGeneID(
				// pathwayDataDomain.getDavidIDType(), david);
				//
				// if (pathwayGraphs != null) {
				//
				// for (PathwayGraph pathwayGraph : pathwayGraphs) {
				//
				// if (!hashPathwaysToOccurences.containsKey(pathwayGraph))
				// hashPathwaysToOccurences.put(pathwayGraph, 1);
				// else {
				// int occurences = hashPathwaysToOccurences
				// .get(pathwayGraph);
				// occurences++;
				// hashPathwaysToOccurences.put(pathwayGraph, occurences);
				// }
				//
				// }
				// }
				// }
				//
				// final ArrayList<PathwayGraph> pathways = new
				// ArrayList<PathwayGraph>();
				//
				// for (PathwayGraph pathway :
				// hashPathwaysToOccurences.keySet()) {
				// if (hashPathwaysToOccurences.get(pathway) >= 2)
				// pathways.add(pathway);
				// }

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

		}, PickingType.BRICK.name(), getID());

		dimensionGroup.updateLayout();

		isInitialized = true;

	}

	private void selectElementsByGroup() {

		// Select all elements in group with special type

		RecordSelectionManager recordSelectionManager = visBricks
				.getRecordSelectionManager();
		SelectionType selectedByGroupSelectionType = recordSelectionManager
				.getSelectionType();

		if (!visBricks.getKeyListener().isCtrlDown()) {
			recordSelectionManager.clearSelection(selectedByGroupSelectionType);

			// ClearSelectionsEvent cse = new ClearSelectionsEvent();
			// cse.setDataDomainType(getDataDomain().getDataDomainType());
			// cse.setSender(this);
			// eventPublisher.triggerEvent(cse);
		}

		// Prevent selection for center brick as this would select all elements
		if (dimensionGroup.getCenterBrick() == this)
			return;

		for (Integer recordID : dataContainer.getRecordPerspective().getVirtualArray()) {
			recordSelectionManager.addToType(selectedByGroupSelectionType, recordID);
		}

		SelectionUpdateEvent event = new SelectionUpdateEvent();
		event.setDataDomainID(getDataDomain().getDataDomainID());
		event.setSender(this);
		SelectionDelta delta = recordSelectionManager.getDelta();
		event.setSelectionDelta(delta);
		GeneralManager.get().getEventPublisher().triggerEvent(event);
	}

	@Override
	protected void initLocal(GL2 gl) {
		init(gl);

	}

	@Override
	public void initRemote(GL2 gl, AGLView glParentView, GLMouseListener glMouseListener) {
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
		
		gl.glPushName(getPickingManager().getPickingID(getID(), PickingType.BRICK,
				getID()));
		gl.glColor4f(1.0f, 0.0f, 0.0f, 0.5f);
		gl.glTranslatef(0, 0, 0.1f);
		gl.glBegin(GL2.GL_QUADS);
		
		float zpos = 0f;
		
		gl.glVertex3f(0, 0, zpos);
		gl.glVertex3f(wrappingLayout.getSizeScaledX(), 0, zpos);
		gl.glVertex3f(wrappingLayout.getSizeScaledX(), wrappingLayout.getSizeScaledY(),
				zpos);
		gl.glVertex3f(0, wrappingLayout.getSizeScaledY(), zpos);
		gl.glEnd();
		gl.glPopName();

		templateRenderer.render(gl);

		

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
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

		super.reshape(drawable, x, y, width, height);
		if (templateRenderer != null)
			templateRenderer.updateLayout();

		if (!isSizeFixed) {
			wrappingLayout.setAbsoluteSizeX(brickLayout.getDefaultWidthPixels());
			wrappingLayout.setAbsoluteSizeY(brickLayout.getDefaultWidthPixels());
		}
	}

	@Override
	protected void handlePickingEvents(PickingType pickingType, PickingMode pickingMode,
			int pickingID, Pick pick) {

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
		// float changePercentage = changeX / width;

		float newWidth = width + changeX;
		float newHeight = height + changeY;

		float minWidth = pixelGLConverter.getGLWidthForPixelWidth(brickLayout
				.getMinWidthPixels());
		float minHeight = pixelGLConverter.getGLHeightForPixelHeight(brickLayout
				.getMinHeightPixels());
		// float minWidth = pixelGLConverter
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

	// /**
	// * Set the recordVA this brick should render plus the groupID that is
	// * associated with this recordVA.
	// *
	// * @param groupID
	// * @param recordVA
	// */
	// public void setRecordVA(Group group, RecordVirtualArray recordVA) {
	// this.group = group;
	// if (group != null)
	// this.groupID = group.getGroupID();
	// this.recordVA = recordVA;
	// }

	// @Override
	// public void setDataContainer(DataContainer dataContainer) {
	// super.setDataContainer(dataContainer);
	//
	// }

	// /**
	// * Set the group of this brick.
	// *
	// * @param group
	// */
	// public void setGroup(Group group) {
	// this.group = group;
	// this.groupID = group.getGroupID();
	// }

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
	// public int getGroupID() {
	// return groupID;
	// }

	/**
	 * Returns the group on which the recordVA of this brick is based on.
	 * 
	 * @return
	 */
	// public Group getGroup() {
	// return return group;
	//
	// }

	@Override
	public List<AGLView> getRemoteRenderedViews() {
		// TODO Auto-generated method stub
		return null;
	}

	// private void calculateAverageValueForBrick() {
	// averageValue = 0;
	// int count = 0;
	// if (recordVA == null)
	// throw new IllegalStateException("recordVA was null");
	// for (Integer contenID : recordVA) {
	// DimensionData dimensionData = table.getDimensionData(Set.STORAGE);
	// if (dimensionData == null) {
	// averageValue = 0;
	// return;
	// }
	//
	// DimensionVirtualArray dimensionVA = dimensionData.getDimensionVA();
	//
	// if (dimensionVA == null) {
	// averageValue = 0;
	// return;
	// }
	// for (Integer dimensionID : dimensionVA) {
	// float value =
	// table.get(dimensionID).getFloat(EDataRepresentation.NORMALIZED,
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

	// public DataTable getTable() {
	// return set;
	// }

	/**
	 * Sets the type of view that should be rendered in the brick. The view type
	 * is not set, if it is not valid for the current brick layout.
	 * 
	 * @param viewType
	 */
	public void setContainedView(EContainedViewType viewType) {

		LayoutRenderer viewRenderer = containedViewRenderers.get(viewType);

		if (viewRenderer == null)
			return;

		if (!brickLayout.isViewTypeValid(viewType))
			return;

		currentRemoteView = views.get(viewType);
		// if (brickLayout.getViewRenderer() instanceof IMouseWheelHandler) {
		// visBricks
		// .unregisterRemoteViewMouseWheelListener((IMouseWheelHandler)
		// brickLayout
		// .getViewRenderer());
		// }
		brickLayout.setViewRenderer(viewRenderer);

		// if (brickLayout.getViewRenderer() instanceof IMouseWheelHandler) {
		// visBricks
		// .registerMouseWheelListener((IMouseWheelHandler) brickLayout
		// .getViewRenderer());
		// }

		brickLayout.viewTypeChanged(viewType);
		int defaultHeightPixels = brickLayout.getDefaultHeightPixels();
		int defaultWidthPixels = brickLayout.getDefaultWidthPixels();
		float defaultHeight = pixelGLConverter
				.getGLHeightForPixelHeight(defaultHeightPixels);
		float defaultWidth = pixelGLConverter.getGLWidthForPixelWidth(defaultWidthPixels);

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
	 * otherwise the default view type will be table.
	 * 
	 * @param brickLayoutTemplate
	 * @param viewType
	 */
	public void setBrickLayoutTemplate(ABrickLayoutTemplate brickLayoutTemplate,
			EContainedViewType viewType) {
		if (brickLayout != null)
			brickLayout.destroy();
		brickLayout = brickLayoutTemplate;
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

		relationsUpdateListener.setExclusiveDataDomainID(dataDomain.getDataDomainID());
		eventPublisher.addListener(RelationsUpdatedEvent.class, relationsUpdateListener);

		selectionUpdateListener = new SelectionUpdateListener();
		selectionUpdateListener.setHandler(this);

		selectionUpdateListener.setExclusiveDataDomainID(dataDomain.getDataDomainID());
		eventPublisher.addListener(SelectionUpdateEvent.class, selectionUpdateListener);

		openCreatePathwayGroupDialogListener = new OpenCreatePathwayGroupDialogListener();
		openCreatePathwayGroupDialogListener.setHandler(this);
		eventPublisher.addListener(OpenCreatePathwayGroupDialogEvent.class,
				openCreatePathwayGroupDialogListener);

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

		if (openCreatePathwayGroupDialogListener != null) {
			eventPublisher.removeListener(openCreatePathwayGroupDialogListener);
			openCreatePathwayGroupDialogListener = null;
		}

		// if (brickLayout.getViewRenderer() instanceof IMouseWheelHandler) {
		// visBricks
		// .unregisterRemoteViewMouseWheelListener((IMouseWheelHandler)
		// brickLayout
		// .getViewRenderer());
		// }
	}

	private void registerPickingListeners() {
		addSingleIDPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				isBrickResizeActive = true;

			}
		}, PickingType.RESIZE_HANDLE_LOWER_RIGHT.name(), 1);
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
		return "Brick: " + dataContainer;// + table.getLabel();

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
	public SelectionManager getRecordGroupSelectionManager() {
		return recordGroupSelectionManager;
	}

	@Override
	public void handleSelectionUpdate(SelectionDelta selectionDelta,
			boolean scrollToSelection, String info) {
		if (selectionDelta.getIDType() == recordGroupSelectionManager.getIDType()) {
			recordGroupSelectionManager.setDelta(selectionDelta);

			if (recordGroupSelectionManager
					.checkStatus(recordGroupSelectionManager.getSelectionType(),
							dataContainer.getID())) {
				brickLayout.setShowHandles(true);
				brickLayout.setSelected(true);
				visBricks.updateConnectionLinesBetweenDimensionGroups();
			} else {
				brickLayout.setSelected(false);
				brickLayout.setShowHandles(false);
			}
			// }
			templateRenderer.updateLayout();
		}
	}

	/**
	 * @return true, if the brick us currently selected, false otherwise
	 */
	public boolean isActive() {
		return recordGroupSelectionManager.checkStatus(SelectionType.SELECTION,
				dataContainer.getID());
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
					wrappingLayout.getSizeScaledY(), wrappingLayout.getSizeScaledX());
		}

		ABrickLayoutTemplate layoutTemplate = brickLayout.getCollapsedLayoutTemplate();
		// isSizeFixed = false;

		setBrickLayoutTemplate(layoutTemplate, layoutTemplate.getDefaultViewType());

		float minHeight = pixelGLConverter.getGLHeightForPixelHeight(layoutTemplate
				.getMinHeightPixels());
		float minWidth = pixelGLConverter.getGLHeightForPixelHeight(layoutTemplate
				.getMinWidthPixels());
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

		ABrickLayoutTemplate layoutTemplate = brickLayout.getExpandedLayoutTemplate();

		if (expandedBrickState != null) {
			setBrickLayoutTemplate(layoutTemplate, expandedBrickState.getViewType());
			wrappingLayout.setAbsoluteSizeX(expandedBrickState.getWidth());
			wrappingLayout.setAbsoluteSizeY(expandedBrickState.getHeight());
		} else {
			setBrickLayoutTemplate(layoutTemplate, currentViewType);
			float defaultHeight = pixelGLConverter
					.getGLHeightForPixelHeight(layoutTemplate.getDefaultHeightPixels());
			float defaultWidth = pixelGLConverter.getGLWidthForPixelWidth(layoutTemplate
					.getDefaultWidthPixels());
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
			Map<EContainedViewType, LayoutRenderer> containedViewRenderers) {
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

	public IBrickConfigurer getBrickConfigurer() {
		return brickConfigurer;
	}

	public void setBrickConfigurer(IBrickConfigurer brickConfigurer) {
		this.brickConfigurer = brickConfigurer;
	}

	public void openCreatePathwayGroupDialog(
			final ATableBasedDataDomain sourceDataDomain,
			final RecordVirtualArray sourceRecordVA) {
		getParentComposite().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				Shell shell = new Shell();
				// shell.setSize(500, 800);
				CreatePathwayComparisonGroupDialog dialog = new CreatePathwayComparisonGroupDialog(
						shell);
				dialog.create();
				dialog.setSourceDataDomain(sourceDataDomain);
				dialog.setSourceVA(sourceRecordVA);
				dialog.setDimensionPerspective(dataContainer.getDimensionPerspective());
				dialog.setRecordPerspective(dataContainer.getRecordPerspective());

				dialog.setBlockOnOpen(true);

				if (dialog.open() == Status.OK) {
					AddGroupsToVisBricksEvent event = new AddGroupsToVisBricksEvent();
					ArrayList<DataContainer> dataContainers = new ArrayList<DataContainer>();

//					IDataDomain pathwayDataDomain = DataDomainManager.get()
//							.getDataDomainByType(PathwayDataDomain.DATA_DOMAIN_TYPE);
					PathwayDimensionGroupData pathwayDimensionGroupData = dialog
							.getPathwayDimensionGroupData();

					// pathwayDataDomain.addDimensionGroup(pathwayDimensionGroupData);
					dataContainers.add(pathwayDimensionGroupData);
					event.setDataContainers(dataContainers);
					event.setSender(this);
					eventPublisher.triggerEvent(event);
				}
			}
		});
	}

	@Override
	protected ArrayList<SelectedElementRep> createElementRep(IDType idType, int id)
			throws InvalidAttributeValueException {
		// TODO Auto-generated method stub
		return null;
	}

}
