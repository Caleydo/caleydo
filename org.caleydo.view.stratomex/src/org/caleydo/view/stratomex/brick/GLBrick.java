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
package org.caleydo.view.stratomex.brick;

import java.awt.Point;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import org.caleydo.core.data.collection.table.TableUtils;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.selection.EventBasedSelectionManager;
import org.caleydo.core.data.selection.IEventBasedSelectionManagerUser;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.events.SelectionUpdateListener;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.event.data.RelationsUpdatedEvent;
import org.caleydo.core.event.data.SelectionUpdateEvent;
import org.caleydo.core.gui.util.RenameNameDialog;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.contextmenu.ContextMenuCreator;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.ATableBasedView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.canvas.listener.IMouseWheelHandler;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.ILayoutedElement;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.layout.util.multiform.IEmbeddedVisualizationInfo.EScalingEntity;
import org.caleydo.core.view.opengl.layout.util.multiform.IMultiFormChangeListener;
import org.caleydo.core.view.opengl.layout.util.multiform.MultiFormRenderer;
import org.caleydo.core.view.opengl.layout.util.multiform.MultiFormViewSwitchingBar;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.GLCoordinateUtils;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.draganddrop.IDraggable;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.caleydo.datadomain.pathway.data.PathwayDimensionGroupData;
import org.caleydo.datadomain.pathway.data.PathwayTablePerspective;
import org.caleydo.view.stratomex.EPickingType;
import org.caleydo.view.stratomex.GLStratomex;
import org.caleydo.view.stratomex.brick.configurer.ClinicalDataConfigurer;
import org.caleydo.view.stratomex.brick.configurer.IBrickConfigurer;
import org.caleydo.view.stratomex.brick.configurer.PathwayDataConfigurer;
import org.caleydo.view.stratomex.brick.contextmenu.CreateKaplanMeierSmallMultiplesGroupItem;
import org.caleydo.view.stratomex.brick.contextmenu.CreatePathwaySmallMultiplesGroupItem;
import org.caleydo.view.stratomex.brick.contextmenu.ExportBrickDataItem;
import org.caleydo.view.stratomex.brick.contextmenu.RemoveColumnItem;
import org.caleydo.view.stratomex.brick.contextmenu.RenameBrickItem;
import org.caleydo.view.stratomex.brick.layout.ABrickLayoutConfiguration;
import org.caleydo.view.stratomex.brick.layout.CollapsedBrickLayoutTemplate;
import org.caleydo.view.stratomex.brick.layout.CompactHeaderBrickLayoutTemplate;
import org.caleydo.view.stratomex.brick.layout.DefaultBrickLayoutTemplate;
import org.caleydo.view.stratomex.brick.layout.DetailBrickLayoutTemplate;
import org.caleydo.view.stratomex.brick.sorting.ExternallyProvidedSortingStrategy;
import org.caleydo.view.stratomex.brick.ui.RectangleCoordinates;
import org.caleydo.view.stratomex.brick.ui.RelationIndicatorRenderer;
import org.caleydo.view.stratomex.column.BrickColumn;
import org.caleydo.view.stratomex.dialog.CreateKaplanMeierSmallMultiplesGroupDialog;
import org.caleydo.view.stratomex.dialog.CreatePathwayComparisonGroupDialog;
import org.caleydo.view.stratomex.dialog.CreatePathwaySmallMultiplesGroupDialog;
import org.caleydo.view.stratomex.event.AddGroupsToStratomexEvent;
import org.caleydo.view.stratomex.event.ExportBrickDataEvent;
import org.caleydo.view.stratomex.event.OpenCreateKaplanMeierSmallMultiplesGroupDialogEvent;
import org.caleydo.view.stratomex.event.OpenCreatePathwayGroupDialogEvent;
import org.caleydo.view.stratomex.event.OpenCreatePathwaySmallMultiplesGroupDialogEvent;
import org.caleydo.view.stratomex.event.RenameEvent;
import org.caleydo.view.stratomex.listener.ExportBrickDataEventListener;
import org.caleydo.view.stratomex.listener.HighlightBrickEventListener;
import org.caleydo.view.stratomex.listener.OpenCreateKaplanMeierSmallMultiplesGroupDialogListener;
import org.caleydo.view.stratomex.listener.OpenCreatePathwayGroupDialogListener;
import org.caleydo.view.stratomex.listener.OpenCreatePathwaySmallMultiplesGroupDialogListener;
import org.caleydo.view.stratomex.listener.RelationsUpdatedListener;
import org.caleydo.view.stratomex.listener.RenameListener;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * Individual Brick for StratomeX
 *
 * @author Alexander Lex
 *
 */
public class GLBrick extends ATableBasedView implements IGLRemoteRenderingView, ILayoutedElement, IDraggable,
		IMultiFormChangeListener, IEventBasedSelectionManagerUser {

	public static String VIEW_TYPE = "org.caleydo.view.brick";
	public static String VIEW_NAME = "Brick";

	private LayoutManager layoutManager;
	private ElementLayout wrappingLayout;

	private int baseDisplayListIndex;
	private boolean isBaseDisplayListDirty = true;

	/**
	 * Renderer that handles the display of all views and renderers that have been associated with this brick by the
	 * {@link #brickConfigurer}.
	 */
	private MultiFormRenderer multiFormRenderer;

	/**
	 * Bar of buttons that can be used to switch the views of {@link #multiFormRenderer}.
	 */
	private MultiFormViewSwitchingBar viewSwitchingBar;

	/**
	 * The ID used in {@link #multiFormRenderer} for the renderer that represents the most compact visualization.
	 */
	private int compactRendererID = -1;

	/**
	 * Maps global, i.e., brick-column wide IDs to identify similar renderers for bricks of the same type (segment vs.
	 * header), to the local renderer IDs used in {@link #multiFormRenderer}.
	 */
	private Map<Integer, Integer> globalRendererIDToLocalRendererID = new HashMap<>();

	/**
	 * Same as {@link #globalRendererIDToLocalRendererID}, just vice versa.
	 */
	private Map<Integer, Integer> localRendererIDToGlobalRendererID = new HashMap<>();

	/**
	 * <p>
	 * Flag telling whether this brick is a header brick (true), and contains all the records of the dimension group, or
	 * if it is a cluster brick (false) which shows only a part.
	 * </p>
	 * <p>
	 * Defaults to false (not a header-brick).
	 * </p>
	 * <p>
	 * For header bricks {@link TablePerspective#getRecordGroup()} is null, for cluster brick the recordGroup must be
	 * defined.
	 * </p>
	 */
	private boolean isHeaderBrick;

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
	 * State telling how the height of the brick is determined. See {@link EBrickHeightMode} for options.
	 */
	private EBrickHeightMode brickHeigthMode = null;

	/**
	 * The height of the brick used if the {@link #brickHeigthMode} is set to {@link EBrickHeightMode#STATIC}
	 */
	private int staticBrickHeight;

	public enum EBrickWidthMode {

		/** The width of the brick is set manually */
		STATIC,

		/**
		 * The width of the brick is determined by the width of the sides of the arch
		 */
		CONTEXT_MODE,

		/**
		 * The width of the brick should be taken from {@link IBrickConfigurer#getDefaultWidth()}
		 */
		DATA_TYPE_DEFAULT,

		/** The width of the brick is determined by the view rendered */
		VIEW_DEPENDENT;
	}

	/**
	 * State telling how the width of the brick is determined. See {@link EBrickWidthMode} for options.
	 */
	private EBrickWidthMode brickWidthMode = null;

	/**
	 * The width of the brick used if the {@link #brickWidthMode} is set to {@link EBrickWidthMode#STATIC}
	 */
	private int staticBrickWidth;

	/**
	 * Renders indication of group relations to the neighboring dimension group. May be null for certain brick types
	 */
	private RelationIndicatorRenderer leftRelationIndicatorRenderer;
	/** same as {@link #leftRelationIndicatorRenderer} for the right side */
	private RelationIndicatorRenderer rightRelationIndicatorRenderer;

	private OpenCreatePathwaySmallMultiplesGroupDialogListener openCreatePathwaySmallMultiplesGroupDialogListener;
	private OpenCreateKaplanMeierSmallMultiplesGroupDialogListener openCreateKaplanMeierSmallMultiplesGroupDialogListener;

	private RelationsUpdatedListener relationsUpdateListener;
	private OpenCreatePathwayGroupDialogListener openCreatePathwayGroupDialogListener;
	private RenameListener renameListener;
	private ExportBrickDataEventListener exportBrickDataEventListener;

	private BrickState expandedBrickState;

	private BrickColumn brickColumn;

	private EventBasedSelectionManager tablePerspectiveSelectionManager;
	private EventBasedSelectionManager recordGroupSelectionManager;

	private boolean isInOverviewMode = false;
	private float previousXCoordinate = Float.NaN;
	private float previousYCoordinate = Float.NaN;
	private boolean isBrickResizeActive = false;

	protected float draggingMousePositionDeltaX;
	protected float draggingMousePositionDeltaY;

	private boolean isInitialized = false;

	private GLStratomex stratomex;

	private ABrickLayoutConfiguration brickLayoutConfiguration;
	private IBrickConfigurer brickConfigurer;

	private final Collection<IContextMenuBrickFactory> contextMenuFactories;
	private HighlightBrickEventListener highlightListener;

	public GLBrick(IGLCanvas glCanvas, Composite parentComposite, ViewFrustum viewFrustum) {
		super(glCanvas, parentComposite, viewFrustum, VIEW_TYPE, VIEW_NAME);

		contextMenuFactories = createContextMenuFactories();
	}

	/**
	 * @return
	 */
	private static Collection<IContextMenuBrickFactory> createContextMenuFactories() {
		Collection<IContextMenuBrickFactory> factories = new ArrayList<>();
		try {
			for (IConfigurationElement elem : RegistryFactory.getRegistry().getConfigurationElementsFor(
					IContextMenuBrickFactory.EXTENSION_ID)) {
				final Object o = elem.createExecutableExtension("class");
				if (o instanceof IContextMenuBrickFactory)
					factories.add((IContextMenuBrickFactory) o);
			}
		} catch (CoreException ex) {
			System.err.println(ex.getMessage());
		}
		return factories;
	}

	@Override
	public void initialize() {
		super.initialize();
		tablePerspectiveSelectionManager = new EventBasedSelectionManager(this, TablePerspective.DATA_CONTAINER_IDTYPE);
		tablePerspectiveSelectionManager.registerEventListeners();
		recordGroupSelectionManager = new EventBasedSelectionManager(this, dataDomain.getRecordGroupIDType());
		recordGroupSelectionManager.registerEventListeners();
		// dataDomain.cloneRecordGroupSelectionManager().clone();

		if (brickLayoutConfiguration == null) {
			brickLayoutConfiguration = new DefaultBrickLayoutTemplate(this, brickColumn, stratomex);
		}

		brickConfigurer.setBrickViews(this, brickLayoutConfiguration);
		brickLayoutConfiguration.configure(brickConfigurer);

		// brickLayoutConfiguration.setViewRenderer(containedViewRenderers.get(currentViewType));
		if (brickLayoutConfiguration.getViewRenderer() instanceof IMouseWheelHandler) {
			stratomex.registerMouseWheelListener((IMouseWheelHandler) brickLayoutConfiguration.getViewRenderer());
		}

		// layoutManager.setStaticLayoutConfiguration(brickLayoutConfiguration);

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
		layoutManager.setUseDisplayLists(true);

		setBrickLayoutTemplate(brickLayoutConfiguration);

		int rendererID = multiFormRenderer.getActiveRendererID();
		if (rendererID == -1) {
			multiFormRenderer.setActive(multiFormRenderer.getDefaultRendererID());
		}

		updateBrickSizeAccordingToRenderer(multiFormRenderer.getActiveRendererID());

		brickColumn.updateLayout();

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
				String r = RenameNameDialog.show(getParentComposite().getShell(), "Rename '" + getLabel() + "' to",
						getLabel());
				if (r != null) {
					label = r;
					tablePerspective.setLabel(label, false);
					setDisplayListDirty();

					if (brickLayoutConfiguration instanceof DefaultBrickLayoutTemplate)
						((DefaultBrickLayoutTemplate) brickLayoutConfiguration).setHideCaption(false);
				}
			}
		});

	}

	private void selectElementsByGroup() {

		// Select all elements in group with special type

		SelectionManager recordSelectionManager = stratomex.getRecordSelectionManager();
		SelectionType selectedByGroupSelectionType = recordSelectionManager.getSelectionType();

		if (!stratomex.getKeyListener().isCtrlDown()) {
			recordSelectionManager.clearSelection(selectedByGroupSelectionType);

		}

		// Prevent selection for center brick as this would select all elements
		if (brickColumn.getHeaderBrick() == this)
			return;

		VirtualArray va = tablePerspective.getRecordPerspective().getVirtualArray();

		for (Integer recordID : va) {
			recordSelectionManager.addToType(selectedByGroupSelectionType, va.getIdType(), recordID);// va.getIdType(),
																										// recordID);
		}

		SelectionUpdateEvent event = new SelectionUpdateEvent();
		event.setEventSpace(getDataDomain().getDataDomainID());
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
		checkForHits(gl);
		processEvents();
		handleBrickResize(gl);

		if (isBaseDisplayListDirty)
			buildBaseDisplayList(gl);

		GLStratomex stratomex = getBrickColumn().getStratomexView();
		gl.glPushName(stratomex.getPickingManager().getPickingID(stratomex.getID(), EPickingType.BRICK.name(), getID()));
		gl.glPushName(getPickingManager().getPickingID(getID(), EPickingType.BRICK.name(), getID()));
		gl.glColor4f(1.0f, 0.0f, 0.0f, 0.5f);
		gl.glTranslatef(0, 0, 0.1f);
		gl.glBegin(GL2.GL_QUADS);

		float zpos = 0f;

		gl.glVertex3f(0, 0, zpos);
		gl.glVertex3f(wrappingLayout.getSizeScaledX(), 0, zpos);
		gl.glVertex3f(wrappingLayout.getSizeScaledX(), wrappingLayout.getSizeScaledY(), zpos);
		gl.glVertex3f(0, wrappingLayout.getSizeScaledY(), zpos);
		gl.glEnd();
		gl.glPopName();
		gl.glPopName();

		// The full brick content will not be rendered with DetailLevel.LOW
		if (brickColumn.getDetailLevel() != EDetailLevel.LOW || isHeaderBrick)
			layoutManager.render(gl);

		gl.glCallList(baseDisplayListIndex);

	}

	@Override
	protected void displayLocal(GL2 gl) {
		pickingManager.handlePicking(this, gl);
		display(gl);
	}

	@Override
	public void displayRemote(GL2 gl) {
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
			wrappingLayout.setPixelSizeY(brickLayoutConfiguration.getDefaultHeightPixels());
		}
		if (brickWidthMode == EBrickWidthMode.VIEW_DEPENDENT) {
			wrappingLayout.setPixelSizeX(brickLayoutConfiguration.getDefaultWidthPixels());
		}
	}

	/** resize of a brick */
	private void handleBrickResize(GL2 gl) {

		if (!isBrickResizeActive)
			return;

		brickHeigthMode = EBrickHeightMode.STATIC;
		brickWidthMode = EBrickWidthMode.STATIC;
		brickLayoutConfiguration.setLockResizing(true);

		if (glMouseListener.wasMouseReleased()) {
			isBrickResizeActive = false;
			previousXCoordinate = Float.NaN;
			previousYCoordinate = Float.NaN;
			return;
		}

		Point currentPoint = glMouseListener.getPickedPoint();

		float[] pointCordinates = GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x,
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

		float minWidth = pixelGLConverter.getGLWidthForPixelWidth(brickLayoutConfiguration.getMinWidthPixels());
		float minHeight = pixelGLConverter.getGLHeightForPixelHeight(brickLayoutConfiguration.getMinHeightPixels());
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
		// brickColumn.updateLayout();
		// groupColumn.setAbsoluteSizeX(width + changeX);

		// float height = wrappingLayout.getSizeScaledY();
		// wrappingLayout.setAbsoluteSizeY(height * (1 + changePercentage));

		// centerBrick.getLayout().updateSubLayout();

		stratomex.setLastResizeDirectionWasToLeft(false);
		stratomex.setLayoutDirty();
		stratomex.updateConnectionLinesBetweenColumns();

	}

	/**
	 * Set the {@link GLStratomex} view managing this brick, which is needed for environment information.
	 *
	 * @param stratomex
	 */
	public void setStratomex(GLStratomex stratomex) {
		this.stratomex = stratomex;
	}

	/**
	 * Set the {@link BrickColumn} this brick belongs to.
	 *
	 * @param brickColumn
	 */
	public void setBrickColumn(BrickColumn brickColumn) {
		this.brickColumn = brickColumn;
	}

	/**
	 * Returns the {@link BrickColumn} this brick belongs to.
	 *
	 * @return
	 */
	public BrickColumn getBrickColumn() {
		return brickColumn;
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
	 * Updates the width and height of the brick according to the specified renderer.
	 *
	 * @param viewType
	 *            ID of the renderer in {@link #multiFormRenderer}.
	 */
	private void updateBrickSizeAccordingToRenderer(int rendererID) {
		if (brickLayoutConfiguration instanceof CompactHeaderBrickLayoutTemplate) {
			brickHeigthMode = EBrickHeightMode.VIEW_DEPENDENT;
			brickWidthMode = EBrickWidthMode.CONTEXT_MODE;
			staticBrickWidth = stratomex.getSideArchWidthPixels();
		} else {
			if (brickHeigthMode != null && brickHeigthMode != EBrickHeightMode.STATIC)
				brickHeigthMode = null;
			if (brickWidthMode != null && brickWidthMode != EBrickWidthMode.STATIC)
				brickWidthMode = null;
		}
		// currentViewType = viewType;
		// // ALayoutRenderer viewRenderer = containedViewRenderers.get(viewType);
		//
		// if (viewRenderer == null)
		// return;
		//
		// if (!brickLayoutConfiguration.isViewTypeValid(viewType))
		// return;
		//
		// brickLayoutConfiguration.setViewRenderer(viewRenderer);
		//
		// brickLayoutConfiguration.viewTypeChanged(viewType);

		// if no height mode was set we use proportional if available, else
		// view-dependent
		if (brickHeigthMode == null) {
			if (multiFormRenderer.getPrimaryHeightScalingEntity() == EScalingEntity.RECORD)
				brickHeigthMode = EBrickHeightMode.PROPORTIONAL;
			else
				brickHeigthMode = EBrickHeightMode.VIEW_DEPENDENT;
		}

		switch (brickHeigthMode) {
		case STATIC:
			wrappingLayout.setPixelSizeY(staticBrickHeight);
			break;
		case VIEW_DEPENDENT:
			int defaultHeightPixels = brickLayoutConfiguration.getDefaultHeightPixels();
			wrappingLayout.setPixelSizeY(defaultHeightPixels);
			break;
		case PROPORTIONAL:
			double proportionalHeight = brickColumn.getProportionalHeightPerRecord() * tablePerspective.getNrRecords()
					+ getHeightOverheadOfProportioanlBrick();

			wrappingLayout.setPixelSizeY((int) proportionalHeight);
			break;

		}

		if (brickWidthMode == null) {
			if (brickConfigurer.useDefaultWidth())
				brickWidthMode = EBrickWidthMode.DATA_TYPE_DEFAULT;
			else
				brickWidthMode = EBrickWidthMode.VIEW_DEPENDENT;

		}
		switch (brickWidthMode) {
		case CONTEXT_MODE:
		case STATIC:
			wrappingLayout.setPixelSizeX(staticBrickWidth);
			break;
		case DATA_TYPE_DEFAULT:
			wrappingLayout.setPixelSizeX(brickConfigurer.getDefaultWidth());
			break;
		case VIEW_DEPENDENT:
			int defaultWidthPixels = brickLayoutConfiguration.getDefaultWidthPixels();
			wrappingLayout.setPixelSizeX(defaultWidthPixels);
			break;
		}

		layoutManager.setStaticLayoutConfiguration(brickLayoutConfiguration);
		layoutManager.updateLayout();

		stratomex.setLayoutDirty();
		stratomex.updateConnectionLinesBetweenColumns();

	}

	@Override
	public TextureManager getTextureManager() {
		return textureManager;
	}

	/**
	 * Sets the {@link ABrickLayoutConfiguration} for this brick, specifying its appearance. If the specified view type
	 * is valid, it will be set, otherwise the default view type will be set.
	 *
	 * @param newBrickLayout
	 * @param viewType
	 */
	public void setBrickLayoutTemplate(ABrickLayoutConfiguration newBrickLayout) {
		if (brickLayoutConfiguration != null && brickLayoutConfiguration != newBrickLayout)
			brickLayoutConfiguration.destroy();
		brickLayoutConfiguration = newBrickLayout;
		brickLayoutConfiguration.setViewRenderer(multiFormRenderer);
		if ((brickLayoutConfiguration instanceof CollapsedBrickLayoutTemplate)
				|| (brickLayoutConfiguration instanceof CompactHeaderBrickLayoutTemplate))
			isInOverviewMode = true;
		else
			isInOverviewMode = false;

		if (layoutManager != null) {
			layoutManager.setStaticLayoutConfiguration(brickLayoutConfiguration);
		}
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
		eventPublisher.addListener(OpenCreatePathwayGroupDialogEvent.class, openCreatePathwayGroupDialogListener);

		openCreatePathwaySmallMultiplesGroupDialogListener = new OpenCreatePathwaySmallMultiplesGroupDialogListener();
		openCreatePathwaySmallMultiplesGroupDialogListener.setHandler(this);
		eventPublisher.addListener(OpenCreatePathwaySmallMultiplesGroupDialogEvent.class,
				openCreatePathwaySmallMultiplesGroupDialogListener);

		renameListener = new RenameListener();
		renameListener.setHandler(this);
		eventPublisher.addListener(RenameEvent.class, renameListener);

		openCreateKaplanMeierSmallMultiplesGroupDialogListener = new OpenCreateKaplanMeierSmallMultiplesGroupDialogListener();
		openCreateKaplanMeierSmallMultiplesGroupDialogListener.setHandler(this);
		eventPublisher.addListener(OpenCreateKaplanMeierSmallMultiplesGroupDialogEvent.class,
				openCreateKaplanMeierSmallMultiplesGroupDialogListener);

		exportBrickDataEventListener = new ExportBrickDataEventListener();
		exportBrickDataEventListener.setHandler(this);
		eventPublisher.addListener(ExportBrickDataEvent.class, exportBrickDataEventListener);

	}

	@Override
	public void unregisterEventListeners() {

		if (renameListener != null) {
			eventPublisher.removeListener(renameListener);
			renameListener = null;
		}

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
			eventPublisher.removeListener(openCreatePathwaySmallMultiplesGroupDialogListener);
			openCreatePathwaySmallMultiplesGroupDialogListener = null;
		}

		if (renameListener != null) {
			eventPublisher.removeListener(renameListener);
			renameListener = null;
		}

		tablePerspectiveSelectionManager.unregisterEventListeners();
		recordGroupSelectionManager.unregisterEventListeners();
		// if (brickLayout.getViewRenderer() instanceof IMouseWheelHandler) {
		// visBricks
		// .unregisterRemoteViewMouseWheelListener((IMouseWheelHandler)
		// brickLayout
		// .getViewRenderer());
		// }

		if (openCreateKaplanMeierSmallMultiplesGroupDialogListener != null) {
			eventPublisher.removeListener(openCreateKaplanMeierSmallMultiplesGroupDialogListener);
			openCreateKaplanMeierSmallMultiplesGroupDialogListener = null;
		}

		if (exportBrickDataEventListener != null) {
			eventPublisher.removeListener(exportBrickDataEventListener);
			exportBrickDataEventListener = null;
		}

		if (highlightListener != null) {
			eventPublisher.removeListener(highlightListener);
			highlightListener = null;
		}

	}

	private void registerPickingListeners() {

		APickingListener pickingListener = new APickingListener() {

			@Override
			public void clicked(Pick pick) {

				SelectionType currentSelectionType = tablePerspectiveSelectionManager.getSelectionType();
				if (!stratomex.getKeyListener().isCtrlDown()) {
					tablePerspectiveSelectionManager.clearSelection(currentSelectionType);
					// System.out.println("clear");
				}

				tablePerspectiveSelectionManager.addToType(currentSelectionType, tablePerspective.getID());
				tablePerspectiveSelectionManager.triggerSelectionUpdateEvent();

				brickLayoutConfiguration.setSelected(true);
				layoutManager.updateLayout();

				// SelectionUpdateEvent event = new SelectionUpdateEvent();
				// event.setEventSpace(getDataDomain().getDataDomainID());
				// event.setSender(this);
				// SelectionDelta delta = tablePerspectiveSelectionManager.getDelta();
				// event.setSelectionDelta(delta);
				// GeneralManager.get().getEventPublisher().triggerEvent(event);

				if (tablePerspective.getRecordGroup() != null) {
					SelectionType currentRecordGroupSelectionType = recordGroupSelectionManager.getSelectionType();
					if (!stratomex.getKeyListener().isCtrlDown())
						recordGroupSelectionManager.clearSelection(currentRecordGroupSelectionType);

					recordGroupSelectionManager.addToType(currentRecordGroupSelectionType, tablePerspective
							.getRecordGroup().getID());
					recordGroupSelectionManager.triggerSelectionUpdateEvent();

					// event = new SelectionUpdateEvent();
					// event.setEventSpace(getDataDomain().getDataDomainID());
					// event.setSender(this);
					// delta = recordGroupSelectionManager.getDelta();
					// event.setSelectionDelta(delta);
					// GeneralManager.get().getEventPublisher().triggerEvent(event);
				}

				selectElementsByGroup();

				if (!isHeaderBrick && !(brickLayoutConfiguration instanceof DetailBrickLayoutTemplate)) {
					Point point = pick.getPickedPoint();
					DragAndDropController dragAndDropController = stratomex.getDragAndDropController();

					dragAndDropController.clearDraggables();
					dragAndDropController.setDraggingStartPosition(new Point(point.x, point.y));
					dragAndDropController.addDraggable(GLBrick.this);
					dragAndDropController.setDraggingMode("BrickDrag" + brickColumn.getID());
					stratomex.setDisplayListDirty();
				}
			}

			@Override
			public void rightClicked(Pick pick) {

				// Differentiate between cases where user selects header brick
				// or a brick
				ContextMenuCreator contextMenuCreator = stratomex.getContextMenuCreator();

				contextMenuCreator.addContextMenuItem(new RenameBrickItem(getID()));
				contextMenuCreator.addContextMenuItem(new RemoveColumnItem(stratomex, getBrickColumn()
						.getTablePerspective()));
				contextMenuCreator.addContextMenuItem(new ExportBrickDataItem(GLBrick.this, false));
				contextMenuCreator.addContextMenuItem(new ExportBrickDataItem(GLBrick.this, true));

				if (brickColumn.getTablePerspective() == tablePerspective) {
					// header brick
					if (dataDomain instanceof GeneticDataDomain && !dataDomain.isColumnDimension()) {
						contextMenuCreator.addContextMenuItem(new CreatePathwaySmallMultiplesGroupItem(brickColumn
								.getTablePerspective(), brickColumn.getTablePerspective().getDimensionPerspective()));
					}
					contextMenuCreator.addContextMenuItem(new CreateKaplanMeierSmallMultiplesGroupItem(brickColumn
							.getTablePerspective(), brickColumn.getTablePerspective().getDimensionPerspective()));

					for (IContextMenuBrickFactory factory : contextMenuFactories)
						for (AContextMenuItem item : factory.createStratification(brickColumn))
							contextMenuCreator.addContextMenuItem(item);
				} else {

					for (IContextMenuBrickFactory factory : contextMenuFactories)
						for (AContextMenuItem item : factory.createGroupEntries(brickColumn, tablePerspective))
							contextMenuCreator.addContextMenuItem(item);

					// FIXME: if added, this line causes the context menu on the bricks to not appear
					// selectElementsByGroup();
				}
			}

		};

		stratomex.addIDPickingListener(pickingListener, EPickingType.BRICK.name(), getID());
		if (isHeaderBrick) {
			stratomex.addIDPickingListener(pickingListener, EPickingType.DIMENSION_GROUP.name(), brickColumn.getID());
		}

		stratomex.addIDPickingTooltipListener(this, EPickingType.BRICK_TITLE.name(), getID());

		addIDPickingListener(new APickingListener() {
			@Override
			public void clicked(Pick pick) {
				isBrickResizeActive = true;
			}
		}, EPickingType.RESIZE_HANDLE_LOWER_RIGHT.name(), 1);
	}

	/**
	 * Only to be called via a {@link RelationsUpdatedListener} upon a {@link RelationsUpdatedEvent}.
	 *
	 * TODO: add parameters to check whether this brick needs to be updated
	 */
	public void relationsUpdated() {
		if (rightRelationIndicatorRenderer != null && leftRelationIndicatorRenderer != null) {
			rightRelationIndicatorRenderer.updateRelations();
			leftRelationIndicatorRenderer.updateRelations();
		}
	}

	@Override
	public String toString() {
		return "Brick: " + tablePerspective;// + table.getLabel();
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
	 * Returns the layout that this view is wrapped in, which is created by the same instance that creates the view.
	 *
	 * @return
	 */
	@Override
	public ElementLayout getLayout() {
		return wrappingLayout;
	}

	public RectangleCoordinates getLayoutForConnections() {
		ElementLayout brickLayout = brickLayoutConfiguration.getViewLayout();
		RectangleCoordinates coordinates = new RectangleCoordinates();
		coordinates.setLeft(wrappingLayout.getTranslateX());
		coordinates.setWidth(wrappingLayout.getSizeScaledX());

		coordinates.setBottom(wrappingLayout.getTranslateY() + brickLayout.getTranslateY());
		coordinates.setHeight(brickLayout.getSizeScaledY());
		return coordinates;

	}

	/**
	 * Returns the selection manager responsible for managing selections of data containers.
	 *
	 * @return
	 */
	public SelectionManager getTablePerspectiveSelectionManager() {
		return tablePerspectiveSelectionManager;
	}

	@Override
	public void handleSelectionUpdate(SelectionDelta selectionDelta) {
		// if (selectionDelta.getIDType() == tablePerspectiveSelectionManager.getIDType()) {
		// tablePerspectiveSelectionManager.setDelta(selectionDelta);
		// // System.out.println(selectionDelta);
		// if (tablePerspectiveSelectionManager.checkStatus(tablePerspectiveSelectionManager.getSelectionType(),
		// tablePerspective.getID())) {
		// // brickLayout.setShowHandles(true);
		// // System.out.println("SELECTED " + getLabel());
		// brickLayoutConfiguration.setSelected(true);
		// stratomex.updateConnectionLinesBetweenColumns();
		// } else {
		// // System.out.println("DESELECTED " + getLabel());
		// brickLayoutConfiguration.setSelected(false);
		// // brickLayout.setShowHandles(false);
		// }
		// // }
		// layoutManager.updateLayout();
		// } else if (selectionDelta.getIDType() == recordGroupSelectionManager.getIDType()) {
		// recordGroupSelectionManager.setDelta(selectionDelta);
		// }
	}

	/**
	 * @return true, if the brick us currently selected, false otherwise
	 */
	public boolean isActive() {
		return tablePerspectiveSelectionManager.checkStatus(SelectionType.SELECTION, tablePerspective.getID());
	}

	/**
	 * Sets this brick collapsed
	 *
	 * @return how much this has affected the height of the brick.
	 */
	public void collapse() {

		if (!isInOverviewMode && isInitialized) {
			expandedBrickState = new BrickState(multiFormRenderer.getActiveRendererID(),
					wrappingLayout.getSizeScaledY(), wrappingLayout.getSizeScaledX());
		}

		ABrickLayoutConfiguration layoutTemplate = brickLayoutConfiguration.getCollapsedLayoutTemplate();
		layoutTemplate.configure(brickConfigurer);

		setBrickLayoutTemplate(layoutTemplate);
		multiFormRenderer.setActive(compactRendererID);

		stratomex.setLayoutDirty();
		stratomex.updateConnectionLinesBetweenColumns();

	}

	public void expand() {
		ABrickLayoutConfiguration layoutTemplate = brickLayoutConfiguration.getExpandedLayoutTemplate();
		layoutTemplate.configure(brickConfigurer);
		if (expandedBrickState != null) {
			setBrickLayoutTemplate(layoutTemplate);
			multiFormRenderer.setActive(expandedBrickState.getRendererID());
		} else {
			setBrickLayoutTemplate(layoutTemplate);
			multiFormRenderer.setActive(multiFormRenderer.getDefaultRendererID());
		}
		isInOverviewMode = false;
		brickLayoutConfiguration.setLockResizing(true);
		brickColumn.updateLayout();
		stratomex.setLayoutDirty();
		stratomex.updateConnectionLinesBetweenColumns();
	}

	public boolean isInOverviewMode() {
		return isInOverviewMode;
	}

	/**
	 * Sets, whether view switching by this brick should affect other bricks in the dimension group.
	 *
	 * @param isGlobalViewSwitching
	 */
	public void setGlobalViewSwitching(boolean isGlobalViewSwitching) {
		brickLayoutConfiguration.setGlobalViewSwitching(isGlobalViewSwitching);
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
	public void openCreatePathwaySmallMultiplesGroupDialog(final TablePerspective dimensionGroupTablePerspective,
			final Perspective dimensionPerspective) {
		getParentComposite().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				Shell shell = new Shell();
				// shell.setSize(500, 800);

				CreatePathwaySmallMultiplesGroupDialog dialog = new CreatePathwaySmallMultiplesGroupDialog(shell,
						dimensionGroupTablePerspective, dimensionPerspective);
				dialog.create();
				dialog.setBlockOnOpen(true);

				if (dialog.open() == IStatus.OK) {

					List<PathwayTablePerspective> pathwayTablePerspectives = dialog.getPathwayTablePerspective();

					for (PathwayTablePerspective pathwayTablePerspective : pathwayTablePerspectives) {

						AddGroupsToStratomexEvent event = new AddGroupsToStratomexEvent(pathwayTablePerspective);
						event.setSourceColumn(getBrickColumn());
						event.setDataConfigurer(new PathwayDataConfigurer());
						event.setSender(this);
						event.setReceiver(stratomex);
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
	public void openCreateKaplanMeierSmallMultiplesGroupDialog(final TablePerspective dimensionGroupTablePerspective,
			final Perspective dimensionPerspective) {

		getParentComposite().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				Shell shell = new Shell();
				CreateKaplanMeierSmallMultiplesGroupDialog dialog = new CreateKaplanMeierSmallMultiplesGroupDialog(
						shell, dimensionGroupTablePerspective);
				dialog.create();
				dialog.setBlockOnOpen(true);

				if (dialog.open() == IStatus.OK) {

					List<TablePerspective> kaplanMeierDimensionGroupDataList = dialog
							.getKaplanMeierDimensionGroupDataList();

					for (TablePerspective kaplanMeierDimensionGroupData : kaplanMeierDimensionGroupDataList) {

						AddGroupsToStratomexEvent event = new AddGroupsToStratomexEvent(kaplanMeierDimensionGroupData);

						ClinicalDataConfigurer dataConfigurer = new ClinicalDataConfigurer();
						ExternallyProvidedSortingStrategy sortingStrategy = new ExternallyProvidedSortingStrategy();
						sortingStrategy.setExternalBricks(brickColumn.getBricks());
						sortingStrategy.setHashConvertedRecordPerspectiveToOrginalRecordPerspective(dialog
								.getHashConvertedRecordPerspectiveToOrginalRecordPerspective());
						dataConfigurer.setSortingStrategy(sortingStrategy);
						event.setSourceColumn(getBrickColumn());
						event.setDataConfigurer(dataConfigurer);
						event.setSender(this);
						event.setReceiver(stratomex);
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
	public void openCreatePathwayGroupDialog(final ATableBasedDataDomain sourceDataDomain,
			final VirtualArray sourceRecordVA) {
		getParentComposite().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				Shell shell = new Shell();
				// shell.setSize(500, 800);

				CreatePathwayComparisonGroupDialog dialog = new CreatePathwayComparisonGroupDialog(shell,
						tablePerspective);
				dialog.create();
				dialog.setSourceDataDomain(sourceDataDomain);
				dialog.setSourceVA(sourceRecordVA);
				dialog.setDimensionPerspective(tablePerspective.getDimensionPerspective());
				dialog.setRecordPerspective(tablePerspective.getRecordPerspective());

				dialog.setBlockOnOpen(true);

				if (dialog.open() == IStatus.OK) {

					PathwayDimensionGroupData pathwayDimensionGroupData = dialog.getPathwayDimensionGroupData();

					AddGroupsToStratomexEvent event = new AddGroupsToStratomexEvent(pathwayDimensionGroupData);
					event.setSourceColumn(getBrickColumn());
					event.setSender(stratomex);
					eventPublisher.triggerEvent(event);
				}
			}
		});
	}

	/**
	 * @return
	 */
	@Override
	public boolean isLabelDefault() {
		return tablePerspective.isLabelDefault();
	}

	/**
	 * @param rightRelationIndicatorRenderer
	 *            setter, see {@link #rightRelationIndicatorRenderer}
	 */
	public void setRightRelationIndicatorRenderer(RelationIndicatorRenderer rightRelationIndicatorRenderer) {
		this.rightRelationIndicatorRenderer = rightRelationIndicatorRenderer;
	}

	/**
	 * @param leftRelationIndicatorRenderer
	 *            setter, see {@link #leftRelationIndicatorRenderer}
	 */
	public void setLeftRelationIndicatorRenderer(RelationIndicatorRenderer leftRelationIndicatorRenderer) {
		this.leftRelationIndicatorRenderer = leftRelationIndicatorRenderer;
	}

	/**
	 * @param isHeaderBrick
	 *            setter, see {@link #isHeaderBrick}
	 */
	public void setHeaderBrick(boolean isHeaderBrick) {
		this.isHeaderBrick = isHeaderBrick;
	}

	/**
	 * @return the isHeaderBrick, see {@link #isHeaderBrick}
	 */
	public boolean isHeaderBrick() {
		return isHeaderBrick;
	}

	public int getHeightOverheadOfProportioanlBrick() {
		int proportionalHeight = 0;

		// if (brickHeigthMode != null
		// && brickHeigthMode.equals(EBrickHeightMode.PROPORTIONAL)
		if (brickLayoutConfiguration instanceof DefaultBrickLayoutTemplate) {
			DefaultBrickLayoutTemplate layoutConfig = (DefaultBrickLayoutTemplate) brickLayoutConfiguration;
			proportionalHeight = layoutConfig.getOverheadHeight();
		}
		return proportionalHeight;
	}

	@Override
	public void setDraggingStartPoint(float mouseCoordinateX, float mouseCoordinateY) {

		draggingMousePositionDeltaX = wrappingLayout.getSizeScaledX() / 2.0f;
		draggingMousePositionDeltaY = wrappingLayout.getSizeScaledY() / 2.0f;

	}

	@Override
	public void handleDragging(GL2 gl, float mouseCoordinateX, float mouseCoordinateY) {
		gl.glColor4f(0.5f, 0.5f, 0.5f, 0.5f);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(mouseCoordinateX - draggingMousePositionDeltaX, mouseCoordinateY - draggingMousePositionDeltaY, 2);
		gl.glVertex3f(mouseCoordinateX - draggingMousePositionDeltaX + wrappingLayout.getSizeScaledX(),
				mouseCoordinateY - draggingMousePositionDeltaY, 2);
		gl.glVertex3f(mouseCoordinateX - draggingMousePositionDeltaX + wrappingLayout.getSizeScaledX(),
				mouseCoordinateY - draggingMousePositionDeltaY + wrappingLayout.getSizeScaledY(), 2);
		gl.glVertex3f(mouseCoordinateX - draggingMousePositionDeltaX, mouseCoordinateY - draggingMousePositionDeltaY
				+ wrappingLayout.getSizeScaledY(), 2);
		gl.glEnd();

		stratomex.setDisplayListDirty();

	}

	@Override
	public void handleDrop(GL2 gl, float mouseCoordinateX, float mouseCoordinateY) {
		// TODO Auto-generated method stub

	}

	public void updateLayout() {
		if (brickHeigthMode == EBrickHeightMode.PROPORTIONAL) {

			double proportionalHeight = brickColumn.getProportionalHeightPerRecord() * tablePerspective.getNrRecords()
					+ getHeightOverheadOfProportioanlBrick();

			wrappingLayout.setPixelSizeY((int) proportionalHeight);
		}

		wrappingLayout.updateSubLayout();
	}

	@Override
	public String getLabel() {
		return tablePerspective.getLabel();
	}

	@Override
	protected void destroyViewSpecificContent(GL2 gl) {
		if (layoutManager != null)
			layoutManager.destroy(gl);
	}

	/**
	 * @return the stratomex, see {@link #stratomex}
	 */
	public GLStratomex getStratomex() {
		return stratomex;
	}

	@Override
	public void setTablePerspective(TablePerspective tablePerspective) {
		super.setTablePerspective(tablePerspective);
		label = tablePerspective.getLabel();
	}

	public void exportData(final boolean exportIdentifiersOnly) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				FileDialog fileDialog = new FileDialog(new Shell(), SWT.SAVE);
				fileDialog.setText("Save");
				String[] filterExt = { "*.csv", "*.txt", "*.*" };
				fileDialog.setFilterExtensions(filterExt);

				fileDialog.setFileName("caleydo_export_" + new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date())
						+ ".csv");
				String fileName = fileDialog.open();

				if (fileName != null) {
					if (exportIdentifiersOnly) {
						Perspective dimensionPerspective = new Perspective();
						dimensionPerspective.setVirtualArray(new VirtualArray(dataDomain.getDimensionIDType()));
						TableUtils.export(dataDomain, fileName, tablePerspective.getRecordPerspective(),
								dimensionPerspective, null, null, false);
					} else {
						TableUtils.export(dataDomain, fileName, tablePerspective.getRecordPerspective(),
								tablePerspective.getDimensionPerspective(), null, null, false);
					}
				}
			}
		});

	}

	/**
	 * @param multiFormRenderer
	 *            setter, see {@link multiFormRenderer}
	 */
	public void setMultiFormRenderer(MultiFormRenderer multiFormRenderer) {
		this.multiFormRenderer = multiFormRenderer;
	}

	/**
	 * @param viewSwitchingBar
	 *            setter, see {@link viewSwitchingBar}
	 */
	public void setViewSwitchingBar(MultiFormViewSwitchingBar viewSwitchingBar) {
		this.viewSwitchingBar = viewSwitchingBar;
	}

	/**
	 * @return the multiFormRenderer, see {@link #multiFormRenderer}
	 */
	public MultiFormRenderer getMultiFormRenderer() {
		return multiFormRenderer;
	}

	/**
	 * @return the viewSwitchingBar, see {@link #viewSwitchingBar}
	 */
	public MultiFormViewSwitchingBar getViewSwitchingBar() {
		return viewSwitchingBar;
	}

	/**
	 * @param compactRendererID
	 *            setter, see {@link compactRendererID}
	 */
	public void setCompactRendererID(int compactRendererID) {
		this.compactRendererID = compactRendererID;
	}

	/**
	 * Sets the specified renderer to be displayed.
	 *
	 * @param rendererID
	 *            ID of the renderer in {@link #multiFormRenderer}, i.e., the local renderer ID.
	 */
	public void setRenderer(int rendererID) {
		multiFormRenderer.setActive(rendererID);
	}

	@Override
	public void activeRendererChanged(MultiFormRenderer multiFormRenderer, int rendererID, int previousRendererID,
			boolean wasTriggeredByUser) {
		if (isInitialized)
			updateBrickSizeAccordingToRenderer(rendererID);
		// if (brickColumn.isGlobalViewSwitching()) {
		// brickColumn.switchBrickViews(getGlobalRendererID(rendererID));
		// }
	}

	@Override
	public void rendererAdded(MultiFormRenderer multiFormRenderer, int rendererID) {
		// Nothing to do
	}

	@Override
	public void rendererRemoved(MultiFormRenderer multiFormRenderer, int rendererID) {
		// Invalidate id of compact renderer
		if (rendererID == compactRendererID)
			compactRendererID = -1;
	}

	@Override
	public void destroyed(MultiFormRenderer multiFormRenderer) {
		this.multiFormRenderer = null;
	}

	/**
	 * Associates global, i.e., brick-column wide IDs to identify similar renderers for bricks of the same type (segment
	 * vs. header), to the local renderer IDs used in {@link #multiFormRenderer}.
	 *
	 * @param globalRendererID
	 *            Global renderer ID.
	 * @param localRendererID
	 *            Local renderer ID.
	 */
	public void associateIDs(int globalRendererID, int localRendererID) {
		localRendererIDToGlobalRendererID.put(localRendererID, globalRendererID);
		globalRendererIDToLocalRendererID.put(globalRendererID, localRendererID);
	}

	/**
	 * Gets the local renderer ID used by {@link #multiFormRenderer} for a global ID.
	 *
	 * @param globalRendererID
	 *            Global renderer ID.
	 * @return The local renderer ID, -1 if no local ID could be found for the specified global ID.
	 */
	public int getLocalRendererID(int globalRendererID) {
		if (!globalRendererIDToLocalRendererID.containsKey(globalRendererID))
			return -1;
		return globalRendererIDToLocalRendererID.get(globalRendererID);
	}

	/**
	 * Gets the global renderer ID for a local ID.
	 *
	 * @param localRendererID
	 *            Local renderer ID.
	 * @return The global renderer ID, -1 if no global ID could be found for the specified local ID.
	 */
	public int getGlobalRendererID(int localRendererID) {
		if (!localRendererIDToGlobalRendererID.containsKey(localRendererID))
			return -1;
		return localRendererIDToGlobalRendererID.get(localRendererID);
	}

	/**
	 * @return The local renderer ID of the renderer that is currently displayed.
	 */
	public int getActiveRendererID() {
		return multiFormRenderer.getActiveRendererID();
	}

	@Override
	public void notifyOfSelectionChange(EventBasedSelectionManager selectionManager) {
		if (selectionManager == tablePerspectiveSelectionManager) {
			// tablePerspectiveSelectionManager.setDelta(selectionDelta);
			// System.out.println(selectionDelta);

			// if (tablePerspectiveSelectionManager.checkStatus(tablePerspectiveSelectionManager.getSelectionType(),
			// tablePerspective.getID())) {
			if (tablePerspectiveSelectionManager.getElements(tablePerspectiveSelectionManager.getSelectionType())
					.contains(tablePerspective.getID())) {
				// brickLayout.setShowHandles(true);
				// System.out.println("SELECTED " + getLabel());
				brickLayoutConfiguration.setSelected(true);
				stratomex.updateConnectionLinesBetweenColumns();
			} else {
				// System.out.println("DESELECTED " + getLabel());
				brickLayoutConfiguration.setSelected(false);
				// brickLayout.setShowHandles(false);
			}
			// }
			layoutManager.updateLayout();
		}
	}
}
