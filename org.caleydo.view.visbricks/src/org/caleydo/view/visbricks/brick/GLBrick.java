package org.caleydo.view.visbricks.brick;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.InvalidAttributeValueException;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;

import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.selection.ElementConnectionInformation;
import org.caleydo.core.data.selection.RecordSelectionManager;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.events.SelectionUpdateListener;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.event.data.RelationsUpdatedEvent;
import org.caleydo.core.event.view.tablebased.SelectionUpdateEvent;
import org.caleydo.core.gui.util.ChangeNameDialog;
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
import org.caleydo.core.view.opengl.util.GLCoordinateUtils;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.caleydo.datadomain.pathway.data.PathwayDimensionGroupData;
import org.caleydo.view.visbricks.GLVisBricks;
import org.caleydo.view.visbricks.PickingType;
import org.caleydo.view.visbricks.brick.contextmenu.CreateKaplanMeierSmallMultiplesGroupItem;
import org.caleydo.view.visbricks.brick.contextmenu.CreatePathwayGroupFromDataItem;
import org.caleydo.view.visbricks.brick.contextmenu.CreatePathwaySmallMultiplesGroupItem;
import org.caleydo.view.visbricks.brick.contextmenu.RenameBrickItem;
import org.caleydo.view.visbricks.brick.layout.ABrickLayoutConfiguration;
import org.caleydo.view.visbricks.brick.layout.CompactBrickLayoutTemplate;
import org.caleydo.view.visbricks.brick.layout.CompactHeaderBrickLayoutTemplate;
import org.caleydo.view.visbricks.brick.layout.DefaultBrickLayoutTemplate;
import org.caleydo.view.visbricks.brick.layout.IBrickConfigurer;
import org.caleydo.view.visbricks.brick.layout.KaplanMeierDataConfigurer;
import org.caleydo.view.visbricks.brick.layout.PathwayDataConfigurer;
import org.caleydo.view.visbricks.brick.ui.RelationIndicatorRenderer;
import org.caleydo.view.visbricks.dialog.CreateKaplanMeierSmallMultiplesGroupDialog;
import org.caleydo.view.visbricks.dialog.CreatePathwayComparisonGroupDialog;
import org.caleydo.view.visbricks.dialog.CreatePathwaySmallMultiplesGroupDialog;
import org.caleydo.view.visbricks.dimensiongroup.DimensionGroup;
import org.caleydo.view.visbricks.event.AddGroupsToVisBricksEvent;
import org.caleydo.view.visbricks.event.OpenCreateKaplanMeierSmallMultiplesGroupDialogEvent;
import org.caleydo.view.visbricks.event.OpenCreatePathwayGroupDialogEvent;
import org.caleydo.view.visbricks.event.OpenCreatePathwaySmallMultiplesGroupDialogEvent;
import org.caleydo.view.visbricks.event.RenameEvent;
import org.caleydo.view.visbricks.listener.OpenCreateKaplanMeierSmallMultiplesGroupDialogListener;
import org.caleydo.view.visbricks.listener.OpenCreatePathwayGroupDialogListener;
import org.caleydo.view.visbricks.listener.OpenCreatePathwaySmallMultiplesGroupDialogListener;
import org.caleydo.view.visbricks.listener.RelationsUpdatedListener;
import org.caleydo.view.visbricks.listener.RenameListener;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * Individual Brick for VisBricks
 * 
 * @author Alexander Lex
 * 
 */
public class GLBrick extends ATableBasedView implements IGLRemoteRenderingView,
		ILayoutedElement {

	public final static String VIEW_TYPE = "org.caleydo.view.brick";

	private LayoutManager layoutManager;
	private ElementLayout wrappingLayout;
	private AGLView currentRemoteView;
	private Map<EContainedViewType, AGLView> views;
	private Map<EContainedViewType, LayoutRenderer> containedViewRenderers;

	private int baseDisplayListIndex;
	private boolean isBaseDisplayListDirty = true;
	private EContainedViewType currentViewType;

	/** Enum listing the options of how the height of a brick is set */
	public enum EBrickHeightMode {
		/** The height of the brick is set manually */
		STATIC,
		/** The height of the brick is determined by the view rendered */
		VIEW_DEPENDENT,
		/** The height of the brick is determined by how many records it shows */
		PROPORTIONAL;
	}

	/**
	 * State telling how the height of the brick is determined. See
	 * {@link EBrickHeightMode} for options.
	 */
	private EBrickHeightMode brickHeigthMode = EBrickHeightMode.VIEW_DEPENDENT;

	/**
	 * The height of the brick used if the {@link #brickHeigthMode} is set to
	 * {@link EBrickHeightMode#STATIC}
	 */
	private int staticBrickHeight;

	public enum EBrickWidthMode {
		/** The width of the brick is set manually */
		STATIC,
		/** The width of the brick is determined by the view rendered */
		VIEW_DEPENDENT;
	}

	/**
	 * State telling how the width of the brick is determined. See
	 * {@link EBrickWidthMode} for options.
	 */
	private EBrickWidthMode brickWidthMode = EBrickWidthMode.VIEW_DEPENDENT;

	/**
	 * The width of the brick used if the {@link #brickWidthMode} is set to
	 * {@link EBrickWidthMode#STATIC}
	 */
	private int staticBrickWidth;

	/**
	 * Renders indication of group relations to the neighboring dimension group.
	 * May be null for certain brick types
	 */
	private RelationIndicatorRenderer leftRelationIndicatorRenderer;
	/** same as {@link #leftRelationIndicatorRenderer} for the right side */
	private RelationIndicatorRenderer rightRelationIndicatorRenderer;

	private OpenCreatePathwaySmallMultiplesGroupDialogListener openCreatePathwaySmallMultiplesGroupDialogListener;
	private OpenCreateKaplanMeierSmallMultiplesGroupDialogListener openCreateKaplanMeierSmallMultiplesGroupDialogListener;

	private RelationsUpdatedListener relationsUpdateListener;
	private OpenCreatePathwayGroupDialogListener openCreatePathwayGroupDialogListener;
	private RenameListener renameListener;

	private BrickState expandedBrickState;

	private DimensionGroup dimensionGroup;

	private SelectionManager dataContainerSelectionManager;
	private boolean isInOverviewMode = false;
	private float previousXCoordinate = Float.NaN;
	private float previousYCoordinate = Float.NaN;
	private boolean isBrickResizeActive = false;

	private boolean isInitialized = false;

	private GLVisBricks visBricks;

	private ABrickLayoutConfiguration brickLayout;
	private IBrickConfigurer brickConfigurer;

	public GLBrick(GLCanvas glCanvas, Composite parentComposite, ViewFrustum viewFrustum) {

		super(glCanvas, parentComposite, viewFrustum);
		viewType = GLBrick.VIEW_TYPE;
		viewLabel = "Brick";

		views = new HashMap<EContainedViewType, AGLView>();
		containedViewRenderers = new HashMap<EContainedViewType, LayoutRenderer>();

	}

	@Override
	public void initialize() {
		super.initialize();
		dataContainerSelectionManager = new SelectionManager(
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

		layoutManager = new LayoutManager(viewFrustum, pixelGLConverter);

		if (brickLayout == null) {
			brickLayout = new DefaultBrickLayoutTemplate(this, visBricks, dimensionGroup,
					brickConfigurer);
		}

		brickConfigurer.setBrickViews(this, gl, glMouseListener, brickLayout);

		currentViewType = brickLayout.getDefaultViewType();

		setBrickLayoutTemplate(brickLayout, currentViewType);

		brickLayout.setViewRenderer(containedViewRenderers.get(currentViewType));
		currentRemoteView = views.get(currentViewType);
		if (brickLayout.getViewRenderer() instanceof IMouseWheelHandler) {
			visBricks.registerMouseWheelListener((IMouseWheelHandler) brickLayout
					.getViewRenderer());
		}

		layoutManager.setStaticLayoutConfiguration(brickLayout);

		addIDPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {

				SelectionType currentSelectionType = dataContainerSelectionManager.getSelectionType();
				dataContainerSelectionManager.clearSelection(currentSelectionType);

				dataContainerSelectionManager.addToType(currentSelectionType,
						dataContainer.getID());

				SelectionUpdateEvent event = new SelectionUpdateEvent();
				event.setDataDomainID(getDataDomain().getDataDomainID());
				event.setSender(this);
				SelectionDelta delta = dataContainerSelectionManager.getDelta();
				event.setSelectionDelta(delta);
				GeneralManager.get().getEventPublisher().triggerEvent(event);

				selectElementsByGroup();
			}

			@Override
			public void rightClicked(Pick pick) {

				// Check if the data domain is genetic and the brick is the
				// center brick
				if (!(dataDomain instanceof GeneticDataDomain))
					return;

				if (dimensionGroup.getDataContainer() == dataContainer) {
					contextMenuCreator
							.addContextMenuItem(new CreatePathwaySmallMultiplesGroupItem(
									dimensionGroup.getDataContainer(), dimensionGroup
											.getDataContainer().getDimensionPerspective()));

					contextMenuCreator
							.addContextMenuItem(new CreateKaplanMeierSmallMultiplesGroupItem(
									dimensionGroup.getDataContainer(), dimensionGroup
											.getDataContainer().getDimensionPerspective()));
				} else
					contextMenuCreator
							.addContextMenuItem(new CreatePathwayGroupFromDataItem(
									dataDomain, dataContainer.getRecordPerspective()
											.getVirtualArray(), dimensionGroup
											.getDataContainer().getDimensionPerspective()));

				contextMenuCreator.addContextMenuItem(new RenameBrickItem(getID()));
			}

		}, PickingType.BRICK.name(), getID());
		dimensionGroup.updateLayout();

		isInitialized = true;

	}

	/**
	 * Triggers a dialog to rename the specified group.
	 * 
	 * @param groupID
	 *            ID of the group that shall be renamed.
	 */
	public void rename(int id) {

		if (id != getID())
			return;

		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				ChangeNameDialog dialog = new ChangeNameDialog();
				dialog.run(PlatformUI.getWorkbench().getDisplay(), getLabel());
				label = dialog.getResultingName();
				dataContainer.setLabel(label, false);
				setDisplayListDirty();

				if (brickLayout instanceof DefaultBrickLayoutTemplate)
					((DefaultBrickLayoutTemplate) brickLayout).setHideCaption(false);

			}
		});

	}

	/**
	 * @return the label, see {@link #label}
	 */
	@Override
	public String getLabel() {
		return dataContainer.getLabel();
	}

	private void selectElementsByGroup() {

		// Select all elements in group with special type

		RecordSelectionManager recordSelectionManager = visBricks
				.getRecordSelectionManager();
		SelectionType selectedByGroupSelectionType = recordSelectionManager
				.getSelectionType();

		if (!visBricks.getKeyListener().isCtrlDown()) {
			recordSelectionManager.clearSelection(selectedByGroupSelectionType);

		}

		// Prevent selection for center brick as this would select all elements
		if (dimensionGroup.getHeaderBrick() == this)
			return;

		RecordVirtualArray va = dataContainer.getRecordPerspective().getVirtualArray();

		for (Integer recordID : va) {
			recordSelectionManager.addToType(selectedByGroupSelectionType,
					va.getIdType(), recordID);// va.getIdType(), recordID);
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

		if (isBaseDisplayListDirty)
			buildBaseDisplayList(gl);

		GLVisBricks visBricks = getDimensionGroup().getVisBricksView();
		gl.glPushName(visBricks.getPickingManager().getPickingID(visBricks.getID(),
				PickingType.BRICK.name(), getID()));
		gl.glPushName(getPickingManager().getPickingID(getID(), PickingType.BRICK.name(),
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
		gl.glPopName();

		layoutManager.render(gl);

		gl.glCallList(baseDisplayListIndex);

	}

	@Override
	protected void displayLocal(GL2 gl) {
		pickingManager.handlePicking(this, gl);
		checkForHits(gl);
		display(gl);

	}

	@Override
	public void displayRemote(GL2 gl) {
		checkForHits(gl);
		display(gl);

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
		if (layoutManager != null)
			layoutManager.updateLayout();

		if (brickHeigthMode == EBrickHeightMode.VIEW_DEPENDENT) {
			wrappingLayout.setPixelSizeY(brickLayout.getDefaultHeightPixels());
		}
		if (brickWidthMode == EBrickWidthMode.VIEW_DEPENDENT) {
			wrappingLayout.setPixelSizeX(brickLayout.getDefaultWidthPixels());
		}
	}

	/** resize of a brick */
	private void handleBrickResize(GL2 gl) {

		if (!isBrickResizeActive)
			return;

		brickHeigthMode = EBrickHeightMode.STATIC;
		brickWidthMode = EBrickWidthMode.STATIC;
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

	@Override
	public List<AGLView> getRemoteRenderedViews() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFrustum(ViewFrustum viewFrustum) {
		super.setFrustum(viewFrustum);
		if (layoutManager != null)
			layoutManager.updateLayout();
	}

	/**
	 * Sets the type of view that should be rendered in the brick. The view type
	 * is not set, if it is not valid for the current brick layout.
	 * 
	 * @param viewType
	 */
	public void setContainedView(EContainedViewType viewType) {
		currentViewType = viewType;
		LayoutRenderer viewRenderer = containedViewRenderers.get(viewType);

		if (viewRenderer == null)
			return;

		if (!brickLayout.isViewTypeValid(viewType))
			return;

		currentRemoteView = views.get(viewType);

		brickLayout.setViewRenderer(viewRenderer);

		brickLayout.viewTypeChanged(viewType);

		if (viewType.isUseProportionalHeight())
			brickHeigthMode = EBrickHeightMode.PROPORTIONAL;
		switch (brickHeigthMode) {
		case STATIC:
			wrappingLayout.setPixelSizeY(staticBrickHeight);
			break;
		case VIEW_DEPENDENT:
			int defaultHeightPixels = brickLayout.getDefaultHeightPixels();
			wrappingLayout.setPixelSizeY(defaultHeightPixels);
			break;

		case PROPORTIONAL:
			double proportionalHeight = dimensionGroup.getProportionalHeightPerRecord()
					* dataContainer.getNrRecords();
			wrappingLayout.setPixelSizeY((int) proportionalHeight);
			break;

		}

		switch (brickWidthMode) {
		case STATIC:
			wrappingLayout.setPixelSizeX(staticBrickWidth);
			break;
		case VIEW_DEPENDENT:
			int defaultWidthPixels = brickLayout.getDefaultWidthPixels();
			wrappingLayout.setPixelSizeX(defaultWidthPixels);
			break;
		}

		layoutManager.setStaticLayoutConfiguration(brickLayout);
		layoutManager.updateLayout();

		visBricks.updateLayout();
		visBricks.updateConnectionLinesBetweenDimensionGroups();

	}

	public TextureManager getTextureManager() {
		return textureManager;
	}

	/**
	 * Sets the {@link ABrickLayoutConfiguration} for this brick, specifying its
	 * appearance. If the specified view type is valid, it will be set,
	 * otherwise the default view type will be set.
	 * 
	 * @param brickLayoutTemplate
	 * @param viewType
	 */
	public void setBrickLayoutTemplate(ABrickLayoutConfiguration brickLayoutTemplate,
			EContainedViewType viewType) {
		if (brickLayout != null)
			brickLayout.destroy();
		brickLayout = brickLayoutTemplate;
		if ((brickLayout instanceof CompactBrickLayoutTemplate)
				|| (brickLayout instanceof CompactHeaderBrickLayoutTemplate))
			isInOverviewMode = true;
		else
			isInOverviewMode = false;

		if (layoutManager != null) {
			layoutManager.setStaticLayoutConfiguration(brickLayout);
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
		eventPublisher.addListener(RelationsUpdatedEvent.class, relationsUpdateListener);

		selectionUpdateListener = new SelectionUpdateListener();
		selectionUpdateListener.setHandler(this);
		eventPublisher.addListener(SelectionUpdateEvent.class, selectionUpdateListener);

		openCreatePathwayGroupDialogListener = new OpenCreatePathwayGroupDialogListener();
		openCreatePathwayGroupDialogListener.setHandler(this);
		eventPublisher.addListener(OpenCreatePathwayGroupDialogEvent.class,
				openCreatePathwayGroupDialogListener);

		openCreatePathwaySmallMultiplesGroupDialogListener = new OpenCreatePathwaySmallMultiplesGroupDialogListener();
		openCreatePathwaySmallMultiplesGroupDialogListener.setHandler(this);
		eventPublisher.addListener(OpenCreatePathwaySmallMultiplesGroupDialogEvent.class,
				openCreatePathwaySmallMultiplesGroupDialogListener);

		renameListener = new RenameListener();
		renameListener.setHandler(this);
		eventPublisher.addListener(RenameEvent.class, renameListener);

		openCreateKaplanMeierSmallMultiplesGroupDialogListener = new OpenCreateKaplanMeierSmallMultiplesGroupDialogListener();
		openCreateKaplanMeierSmallMultiplesGroupDialogListener.setHandler(this);
		eventPublisher.addListener(
				OpenCreateKaplanMeierSmallMultiplesGroupDialogEvent.class,
				openCreateKaplanMeierSmallMultiplesGroupDialogListener);

	}

	@Override
	public void unregisterEventListeners() {
		renameListener = new RenameListener();
		renameListener.setHandler(this);
		eventPublisher.addListener(RenameEvent.class, renameListener);
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
		if (openCreatePathwaySmallMultiplesGroupDialogListener != null) {
			eventPublisher
					.removeListener(openCreatePathwaySmallMultiplesGroupDialogListener);
			openCreatePathwaySmallMultiplesGroupDialogListener = null;
		}

		if (renameListener != null) {
			eventPublisher.removeListener(renameListener);
			renameListener = null;
		}

		// if (brickLayout.getViewRenderer() instanceof IMouseWheelHandler) {
		// visBricks
		// .unregisterRemoteViewMouseWheelListener((IMouseWheelHandler)
		// brickLayout
		// .getViewRenderer());
		// }

		if (openCreateKaplanMeierSmallMultiplesGroupDialogListener != null) {
			eventPublisher
					.removeListener(openCreateKaplanMeierSmallMultiplesGroupDialogListener);
			openCreateKaplanMeierSmallMultiplesGroupDialogListener = null;
		}

	}

	private void registerPickingListeners() {
		addIDPickingListener(new APickingListener() {
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
		return dataContainerSelectionManager;
	}

	@Override
	public void handleSelectionUpdate(SelectionDelta selectionDelta,
			boolean scrollToSelection, String info) {
		if (selectionDelta.getIDType() == dataContainerSelectionManager.getIDType()) {
			dataContainerSelectionManager.setDelta(selectionDelta);

			if (dataContainerSelectionManager.checkStatus(
					dataContainerSelectionManager.getSelectionType(),
					dataContainer.getID())) {
				// brickLayout.setShowHandles(true);
				brickLayout.setSelected(true);
				visBricks.updateConnectionLinesBetweenDimensionGroups();
			} else {
				brickLayout.setSelected(false);
				// brickLayout.setShowHandles(false);
			}
			// }
			layoutManager.updateLayout();
		}
	}

	/**
	 * @return true, if the brick us currently selected, false otherwise
	 */
	public boolean isActive() {
		return dataContainerSelectionManager.checkStatus(SelectionType.SELECTION,
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

		ABrickLayoutConfiguration layoutTemplate = brickLayout
				.getCollapsedLayoutTemplate();
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
		ABrickLayoutConfiguration layoutTemplate = brickLayout
				.getExpandedLayoutTemplate();
		if (expandedBrickState != null) {
			setBrickLayoutTemplate(layoutTemplate, expandedBrickState.getViewType());
			// wrappingLayout.setAbsoluteSizeX(expandedBrickState.getWidth());
			// wrappingLayout.setAbsoluteSizeY(expandedBrickState.getHeight());
		} else {
			setBrickLayoutTemplate(layoutTemplate, currentViewType);
			// float defaultHeight = pixelGLConverter
			// .getGLHeightForPixelHeight(layoutTemplate.getDefaultHeightPixels());
			// float defaultWidth =
			// pixelGLConverter.getGLWidthForPixelWidth(layoutTemplate
			// .getDefaultWidthPixels());
			// wrappingLayout.setAbsoluteSizeY(defaultHeight);
			// wrappingLayout.setAbsoluteSizeX(defaultWidth);
		}
		isInOverviewMode = false;
		brickLayout.setLockResizing(true);
		// dimensionGroup.updateLayout();
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

	/**
	 * @param brickHeigthMode
	 *            setter, see {@link #brickHeigthMode}
	 */
	public void setBrickHeigthMode(EBrickHeightMode brickHeigthMode) {
		this.brickHeigthMode = brickHeigthMode;
	}

	/**
	 * @return the brickHeigthMode, see {@link #brickHeigthMode}
	 */
	public EBrickHeightMode getBrickHeigthMode() {
		return brickHeigthMode;
	}

	/**
	 * @param staticBrickHeight
	 *            setter, see {@link #staticBrickHeight}
	 */
	public void setStaticBrickHeight(int staticBrickHeight) {
		this.staticBrickHeight = staticBrickHeight;
	}

	/**
	 * @param brickWidthMode
	 *            setter, see {@link #brickWidthMode}
	 */
	public void setBrickWidthMode(EBrickWidthMode brickWidthMode) {
		this.brickWidthMode = brickWidthMode;
	}

	/**
	 * @return the brickWidthMode, see {@link #brickWidthMode}
	 */
	public EBrickWidthMode getBrickWidthMode() {
		return brickWidthMode;
	}

	/**
	 * @param staticBrickWidth
	 *            setter, see {@link #staticBrickWidth}
	 */
	public void setStaticBrickWidth(int staticBrickWidth) {
		this.staticBrickWidth = staticBrickWidth;
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

	/**
	 * FIXME this should not be here but somewhere specific to genes
	 * 
	 * @param sourceDataDomain
	 * @param sourceRecordVA
	 */
	public void openCreatePathwaySmallMultiplesGroupDialog(
			final DataContainer dimensionGroupDataContainer,
			final DimensionPerspective dimensionPerspective) {
		getParentComposite().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				Shell shell = new Shell();
				// shell.setSize(500, 800);

				CreatePathwaySmallMultiplesGroupDialog dialog = new CreatePathwaySmallMultiplesGroupDialog(
						shell, dimensionGroupDataContainer, dimensionPerspective);
				dialog.create();
				dialog.setBlockOnOpen(true);

				if (dialog.open() == Status.OK) {
					AddGroupsToVisBricksEvent event = new AddGroupsToVisBricksEvent();
					ArrayList<DataContainer> dataContainers = new ArrayList<DataContainer>();

					List<PathwayDimensionGroupData> pathwayDimensionGroupDataList = dialog
							.getPathwayDimensionGroupDataList();

					for (PathwayDimensionGroupData pathwayDimensionGroupData : pathwayDimensionGroupDataList) {
						dataContainers.add(pathwayDimensionGroupData);
						event.setDataContainers(dataContainers);
						event.setDataConfigurer(new PathwayDataConfigurer());
						event.setSender(this);
						event.setReceiver(visBricks);
						eventPublisher.triggerEvent(event);
					}
				}
			}
		});
	}

	/**
	 * FIXME this should not be here but somewhere specific to genes
	 * 
	 * @param sourceDataDomain
	 * @param sourceRecordVA
	 */
	public void openCreateKaplanMeierSmallMultiplesGroupDialog(
			final DataContainer dimensionGroupDataContainer,
			final DimensionPerspective dimensionPerspective) {

		getParentComposite().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				Shell shell = new Shell();
				CreateKaplanMeierSmallMultiplesGroupDialog dialog = new CreateKaplanMeierSmallMultiplesGroupDialog(
						shell, dimensionGroupDataContainer, dimensionPerspective);
				dialog.create();
				dialog.setBlockOnOpen(true);

				if (dialog.open() == Status.OK) {
					AddGroupsToVisBricksEvent event = new AddGroupsToVisBricksEvent();
					ArrayList<DataContainer> dataContainers = new ArrayList<DataContainer>();

					List<DataContainer> kaplanMeierDimensionGroupDataList = dialog
							.getKaplanMeierDimensionGroupDataList();
					for (DataContainer kaplanMeierDimensionGroupData : kaplanMeierDimensionGroupDataList) {
						dataContainers.add(kaplanMeierDimensionGroupData);
						event.setDataContainers(dataContainers);
						event.setDataConfigurer(new KaplanMeierDataConfigurer());
						event.setSender(this);
						event.setReceiver(visBricks);
						eventPublisher.triggerEvent(event);
					}
				}
			}
		});
	}

	/**
	 * FIXME this should not be here but somewhere specific to genes
	 * 
	 * @param sourceDataDomain
	 * @param sourceRecordVA
	 */
	public void openCreatePathwayGroupDialog(
			final ATableBasedDataDomain sourceDataDomain,
			final RecordVirtualArray sourceRecordVA) {
		getParentComposite().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				Shell shell = new Shell();
				// shell.setSize(500, 800);

				CreatePathwayComparisonGroupDialog dialog = new CreatePathwayComparisonGroupDialog(
						shell, dataContainer);
				dialog.create();
				dialog.setSourceDataDomain(sourceDataDomain);
				dialog.setSourceVA(sourceRecordVA);
				dialog.setDimensionPerspective(dataContainer.getDimensionPerspective());
				dialog.setRecordPerspective(dataContainer.getRecordPerspective());

				dialog.setBlockOnOpen(true);

				if (dialog.open() == Status.OK) {
					AddGroupsToVisBricksEvent event = new AddGroupsToVisBricksEvent();
					ArrayList<DataContainer> dataContainers = new ArrayList<DataContainer>();

					PathwayDimensionGroupData pathwayDimensionGroupData = dialog
							.getPathwayDimensionGroupData();

					// IDataDomain pathwayDataDomain = DataDomainManager.get()
					// .getDataDomainByType(PathwayDataDomain.DATA_DOMAIN_TYPE);
					// pathwayDataDomain.addDimensionGroup(pathwayDimensionGroupData);
					dataContainers.add(pathwayDimensionGroupData);
					event.setDataContainers(dataContainers);
					event.setSender(visBricks);
					eventPublisher.triggerEvent(event);
				}
			}
		});
	}

	@Override
	protected ArrayList<ElementConnectionInformation> createElementConnectionInformation(
			IDType idType, int id) throws InvalidAttributeValueException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return the isDefaultLabel, see {@link #isDefaultLabel}
	 */
	public boolean isDefaultLabel() {
		return dataContainer.isDefaultLabel();
	}

	/**
	 * @param rightRelationIndicatorRenderer
	 *            setter, see {@link #rightRelationIndicatorRenderer}
	 */
	public void setRightRelationIndicatorRenderer(
			RelationIndicatorRenderer rightRelationIndicatorRenderer) {
		this.rightRelationIndicatorRenderer = rightRelationIndicatorRenderer;
	}

	/**
	 * @param leftRelationIndicatorRenderer
	 *            setter, see {@link #leftRelationIndicatorRenderer}
	 */
	public void setLeftRelationIndicatorRenderer(
			RelationIndicatorRenderer leftRelationIndicatorRenderer) {
		this.leftRelationIndicatorRenderer = leftRelationIndicatorRenderer;
	}
}
