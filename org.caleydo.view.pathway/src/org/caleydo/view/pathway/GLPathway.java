/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.pathway;

import gleem.linalg.Vec3f;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataSupportDefinition;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.selection.EventBasedSelectionManager;
import org.caleydo.core.data.selection.IEventBasedSelectionManagerUser;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.event.EventListenerManager;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventListenerManagers;
import org.caleydo.core.event.data.SelectionUpdateEvent;
import org.caleydo.core.event.view.TablePerspectivesChangedEvent;
import org.caleydo.core.id.IDType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.color.ColorManager;
import org.caleydo.core.util.color.mapping.IColorMappingUpdateListener;
import org.caleydo.core.util.color.mapping.UpdateColorMappingEvent;
import org.caleydo.core.util.color.mapping.UpdateColorMappingListener;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.IMultiTablePerspectiveBasedView;
import org.caleydo.core.view.listener.AddTablePerspectivesEvent;
import org.caleydo.core.view.listener.AddTablePerspectivesListener;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.GLMouseAdapter;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.canvas.IGLKeyListener;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.datadomain.genetic.EGeneIDTypes;
import org.caleydo.datadomain.genetic.GeneticDataSupportDefinition;
import org.caleydo.datadomain.pathway.IPathwayRepresentation;
import org.caleydo.datadomain.pathway.IVertexRepBasedEventFactory;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.datadomain.pathway.VertexRepBasedContextMenuItem;
import org.caleydo.datadomain.pathway.VertexRepBasedEventFactory;
import org.caleydo.datadomain.pathway.contextmenu.container.GeneMenuItemContainer;
import org.caleydo.datadomain.pathway.contextmenu.item.LoadPathwaysByPathwayItem;
import org.caleydo.datadomain.pathway.data.PathwayTablePerspective;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.PathwayPath;
import org.caleydo.datadomain.pathway.graph.item.vertex.EPathwayVertexType;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertex;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.listener.EnablePathSelectionEvent;
import org.caleydo.datadomain.pathway.listener.LoadPathwayEvent;
import org.caleydo.datadomain.pathway.listener.PathwayMappingEvent;
import org.caleydo.datadomain.pathway.listener.PathwayPathSelectionEvent;
import org.caleydo.datadomain.pathway.listener.ShowNodeContextEvent;
import org.caleydo.datadomain.pathway.manager.EPathwayDatabaseType;
import org.caleydo.datadomain.pathway.manager.PathwayItemManager;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.datadomain.pathway.toolbar.SelectPathAction;
import org.caleydo.view.pathway.event.SampleMappingModeEvent;
import org.caleydo.view.pathway.event.SampleMappingModeListener;
import org.caleydo.view.pathway.listener.EnRoutePathEventListener;
import org.caleydo.view.pathway.listener.EnableGeneMappingListener;
import org.caleydo.view.pathway.listener.SelectPathModeEventListener;
import org.caleydo.view.pathway.listener.ShowPortalNodesEventListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Composite;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.KShortestPaths;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.GraphPathImpl;

import setvis.bubbleset.BubbleSet;

/**
 * Single OpenGL2 pathway view
 *
 * @author Marc Streit
 * @author Alexander Lex
 */

public class GLPathway extends AGLView implements IMultiTablePerspectiveBasedView, IViewCommandHandler,
		IEventBasedSelectionManagerUser, IColorMappingUpdateListener, IPathwayRepresentation {

	public static String VIEW_TYPE = "org.caleydo.view.pathway";

	public static String VIEW_NAME = "Pathway";

	public static final String DEFAULT_PATHWAY_PATH_EVENT_SPACE = "pathwayPath";

	private PathwayDataDomain pathwayDataDomain;

	private PathwayGraph pathway;

	private PathwayManager pathwayManager;
	private PathwayItemManager pathwayItemManager;

	/**
	 * Determines whether vertex highlighting via selection (click/mouse over) is enabled.
	 */
	private boolean highlightVertices = true;

	/**
	 * The maximal number of paths in the pathway that are looked up. The user specifies from which source to which
	 * destination node the search will be triggered.
	 */
	private final static int MAX_PATHS = 10;

	private boolean enablePathwayTexture = true;

	private boolean isPathwayDataDirty = false;

	private boolean isDynamicDetail = false;

	private GLPathwayAugmentationRenderer augmentationRenderer;

	private EventBasedSelectionManager vertexSelectionManager;
	private EventBasedSelectionManager sampleSelectionManager;

	private List<TablePerspective> tablePerspectives = new ArrayList<>();

	/** The mode determing which samples (all or a selection)s should be mapped */
	private ESampleMappingMode sampleMappingMode = ESampleMappingMode.ALL;

	/**
	 * Selection manager for metabolites (compounds). Uses the hash value of compound names as id.
	 */
	private EventBasedSelectionManager metaboliteSelectionManager;

	// /**
	// * Selection manager for pathways. Used for embedded pathway nodes.
	// */
	// private EventBasedSelectionManager pathwaySelectionManager;

	/**
	 * Own texture manager is needed for each GL2 context, because textures cannot be bound to multiple GL2 contexts.
	 */
	// private HashMap<GL, GLPathwayTextureManager> hashGLcontext2TextureManager;
	private GLPathwayTextureManager pathwayTextureManager;

	private Vec3f vecScaling;
	private Vec3f vecTranslation;

	private EnableGeneMappingListener enableGeneMappingListener;
	private EnRoutePathEventListener enRoutePathEventListener;
	private SelectPathModeEventListener selectPathModeEventListener;
	private AddTablePerspectivesListener<GLPathway> addTablePerspectivesListener;
	private SampleMappingModeListener sampleMappingModeListener;
	private UpdateColorMappingListener updateColorMappingListener;
	private ShowPortalNodesEventListener showPortalNodesEventListener;

	private IPickingListener pathwayElementPickingListener;

	private Set<PathwayVertexRep> portalVertexReps = new HashSet<>();
	private List<PathwayPath> pathSegments = new ArrayList<PathwayPath>();
	/**
	 * The currently selected path as selected by the user from allPaths.
	 */
	private GraphPath<PathwayVertexRep, DefaultEdge> selectedPath;
	private GraphPath<PathwayVertexRep, DefaultEdge> previousSelectedPath;
	private PathwayVertexRep pathStartVertexRep = null;

	/**
	 * All paths which are available between two user selected nodes.
	 */
	private List<GraphPath<PathwayVertexRep, DefaultEdge>> allPaths = null;
	private List<Pair<List<GraphPath<PathwayVertexRep, DefaultEdge>>, Integer>> allPathsList = new ArrayList<Pair<List<GraphPath<PathwayVertexRep, DefaultEdge>>, Integer>>();
	private boolean isBubbleTextureDirty;
	private boolean isPathStartSelected = false;
	private int selectedPathID;
	private PathwayBubbleSet bubbleSet = new PathwayBubbleSet();
	private PathwayBubbleSet alternativeBubbleSet = new PathwayBubbleSet();
	private PathwayBubbleSet contextPathBubbleSet = new PathwayBubbleSet();
	private boolean isControlKeyDown = false;
	private boolean isShiftKeyDown = false;

	/**
	 * Determines whether the paths should be selectable via mouse click.
	 */
	private boolean isPathSelectionMode = false;
	private SelectPathAction selectPathAction = null;
	private boolean showPortals = false;

	private int minHeightPixels = -1;
	private int minWidthPixels = -1;

	private List<List<PathwayVertexRep>> contextPaths = new ArrayList<>();
	private boolean areContextPathsDirty = false;

	// /**
	// * The nodes that are currently considered as portals and need to be highlighted.
	// */
	// private Set<PathwayVertexRep> portalHighlightNodes = new HashSet<>();

	/**
	 * Event space for events that synchronize a pathway path.
	 */
	private String pathwayPathEventSpace = DEFAULT_PATHWAY_PATH_EVENT_SPACE;

	/**
	 * Context menu items that shall be displayed on right click on a {@link PathwayVertexRep}. Added via
	 * {@link #addVertexRepBasedContextMenuItem(VertexRepBasedContextMenuItem)}.
	 */
	private List<VertexRepBasedContextMenuItem> addedContextMenuItems = new ArrayList<>();

	/**
	 * Events that should be triggered when selecting a node. Added via
	 * {@link #addVertexRepBasedSelectionEvent(VertexRepBasedEventFactory, PickingMode)}.
	 */
	protected Map<PickingMode, List<IVertexRepBasedEventFactory>> nodeEvents = new HashMap<>();

	private EventListenerManager listeners = EventListenerManagers.wrap(this);

	private IDType geneIDType;
	private IDType sampleIDType;

	/**
	 * Constructor.
	 */
	public GLPathway(IGLCanvas glCanvas, Composite parentComposite, ViewFrustum viewFrustum) {
		super(glCanvas, parentComposite, viewFrustum, VIEW_TYPE, VIEW_NAME);

		pathwayManager = PathwayManager.get();
		pathwayItemManager = PathwayItemManager.get();

		metaboliteSelectionManager = new EventBasedSelectionManager(this, IDType.getIDType("METABOLITE"));
		metaboliteSelectionManager.registerEventListeners();

		pathwayDataDomain = (PathwayDataDomain) DataDomainManager.get().getDataDomainByType(
				"org.caleydo.datadomain.pathway");
		geneIDType = pathwayDataDomain.getDavidIDType();

		// pathwaySelectionManager = new EventBasedSelectionManager(this, IDType.getIDType("PATHWAY"));
		// pathwaySelectionManager.registerEventListeners();

		// hashGLcontext2TextureManager = new HashMap<GL, GLPathwayTextureManager>();

		vertexSelectionManager = new EventBasedSelectionManager(this, IDType.getIDType(EGeneIDTypes.PATHWAY_VERTEX_REP
				.name()));

		vecScaling = new Vec3f(1, 1, 1);
		vecTranslation = new Vec3f(0, 0, 0);

		registerMouseListeners();
		registerKeyListeners();

		// ///////////////////////////////////////////////////
		// / bubble sets
		isBubbleTextureDirty = true;
		selectedPathID = 0;

	}

	public Set<PathwayVertexRep> getCurrentPortalVertexReps() {
		return portalVertexReps;
	}

	private void selectNextPath(boolean inc) {
		// System.out.println("selectNextPath()");
		// if (this.allPathsList == null)
		// return;
		if (this.allPathsList.size() < 1)
			return;
		List<GraphPath<PathwayVertexRep, DefaultEdge>> paths = this.allPathsList.get(this.allPathsList.size() - 1)
				.getFirst();
		if (paths.size() > 1) {
			// System.out.println("allPaths.size() > 1");

			if (inc)
				selectedPathID++;
			else
				selectedPathID--;

			if (selectedPathID < 0)
				selectedPathID = 0;
			if (selectedPathID > paths.size() - 1)
				selectedPathID = paths.size() - 1;

			if (allPaths.size() > 0) {
				selectedPath = paths.get(selectedPathID);
				// System.out.println("selectedPathID"+selectedPathID);
				if (selectedPath.getEdgeList().size() > 0 && !isShiftKeyDown) {
					PathwayVertexRep startPrevVertex = selectedPath.getStartVertex();
					PathwayVertexRep endPrevVertex = selectedPath.getEndVertex();
					List<DefaultEdge> edgePrevList = selectedPath.getEdgeList();
					previousSelectedPath = new GraphPathImpl<PathwayVertexRep, DefaultEdge>(pathway, startPrevVertex,
							endPrevVertex, edgePrevList, 0);
				}
			}
		} else {
			selectedPathID = 0;
		}

		// System.out.println("selectedPathID="+selectedPathID);
		this.allPathsList.get(this.allPathsList.size() - 1).setSecond(selectedPathID);
		isBubbleTextureDirty = true;
		setDisplayListDirty();
		triggerPathUpdate();
	}

	public void setPathway(final PathwayGraph pathway) {
		// Unregister former pathway in visibility list
		if (pathway != null) {
			pathwayManager.setPathwayVisibilityState(pathway, false);
		}

		this.pathway = pathway;
		isPathwayDataDirty = true;
	}

	public void setPathway(final int iPathwayID) {

		setPathway(pathwayManager.getItem(iPathwayID));
	}

	@Override
	public PathwayGraph getPathway() {

		return pathway;
	}

	@Override
	public void initialize() {
		super.initialize();
		registerPickingListeners();
		augmentationRenderer = new GLPathwayAugmentationRenderer(viewFrustum, this);

		augmentationRenderer.setMappingPerspective(null);
	}

	@Override
	public void initLocal(final GL2 gl) {
		init(gl);
	}

	@Override
	public void initRemote(final GL2 gl, final AGLView glParentView, final GLMouseListener glMouseListener) {
		this.glMouseListener = glMouseListener;
		init(gl);
	}

	@Override
	public void init(final GL2 gl) {

		displayListIndex = gl.glGenLists(1);

		bubbleSet.getBubbleSetGLRenderer().init(gl);
		contextPathBubbleSet.getBubbleSetGLRenderer().init(gl);
		alternativeBubbleSet.getBubbleSetGLRenderer().init(gl);
		// Check if pathway exists or if it's already loaded
		if (pathway == null || !pathwayManager.hasItem(pathway.getID()))
			return;

		initPathwayData(gl);
	}

	protected void registerMouseListeners() {

		parentGLCanvas.addMouseListener(new GLMouseAdapter() {
			@Override
			public void mouseWheelMoved(IMouseEvent e) {
				// selectNextPath();
			}
		});
	}

	public void setSelectPathAction(SelectPathAction aSelectPathAction) {
		this.selectPathAction = aSelectPathAction;
	}

	protected void registerKeyListeners() {

		parentGLCanvas.addKeyListener(new IGLKeyListener() {
			@Override
			public void keyPressed(IKeyEvent e) {
				// //comment_1/2:
				if (e.isControlDown() && (e.isKey('o'))) { // ctrl +o
					enablePathSelection(!isPathSelectionMode);
					getParentComposite().getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							if (selectPathAction != null) {
								selectPathAction.setChecked(isPathSelectionMode);
							}
						}
					});
				}// if (e.isControlDown() && (e.getKeyCode() == 79))
				isControlKeyDown = e.isControlDown();
				isShiftKeyDown = e.isShiftDown();

				if (e.isDownDown()) {
					// System.out.println("isDownDown");
					// selectedPathID--;
					selectNextPath(false);
				}

				if (e.isUpDown()) {
					// System.out.println("isUpDown");
					// selectedPathID++;
					selectNextPath(true);
				}

			}

			@Override
			public void keyReleased(IKeyEvent e) {
				isControlKeyDown = e.isControlDown();
				isShiftKeyDown = e.isShiftDown();
			}
		});
	}

	protected void registerPickingListeners() {

		if (!highlightVertices)
			return;

		addTypePickingListener(new APickingListener() {

			@Override
			public void mouseOver(Pick pick) {

				if (detailLevel == EDetailLevel.VERY_LOW || !highlightVertices) {
					return;
				}

				handlePathwayElementSelection(SelectionType.MOUSE_OVER, pick.getObjectID());
				triggerNodeEvents(pick.getPickingMode(), pathwayItemManager.getPathwayVertexRep(pick.getObjectID()));
			}

			@Override
			protected void mouseOut(Pick pick) {
				PathwayVertexRep vertexRep = pathwayItemManager.getPathwayVertexRep(pick.getObjectID());
				vertexSelectionManager.removeFromType(SelectionType.MOUSE_OVER, vertexRep.getID());

				SelectionDelta selectionDelta = vertexSelectionManager.getDelta();
				SelectionUpdateEvent event = new SelectionUpdateEvent();
				event.setSender(this);
				event.setSelectionDelta(selectionDelta);
				eventPublisher.triggerEvent(event);
				triggerNodeEvents(pick.getPickingMode(), pathwayItemManager.getPathwayVertexRep(pick.getObjectID()));
			}

			@Override
			public void clicked(Pick pick) {

				if (detailLevel == EDetailLevel.VERY_LOW || !highlightVertices) {
					return;
				}

				// We do not handle picking events in pathways for StratomeX
				if (glRemoteRenderingView != null
						&& glRemoteRenderingView.getViewType().equals("org.caleydo.view.brick"))
					return;

				handlePathwayElementSelection(SelectionType.SELECTION, pick.getObjectID());
				triggerNodeEvents(pick.getPickingMode(), pathwayItemManager.getPathwayVertexRep(pick.getObjectID()));
				// triggerNodeEvents(PickingMode.MOUSE_OVER,
				// pathwayItemManager.getPathwayVertexRep(pick.getObjectID()));
			}

			@Override
			public void doubleClicked(Pick pick) {

				if (detailLevel == EDetailLevel.VERY_LOW || !highlightVertices) {
					return;
				}

				PathwayVertexRep vertexRep = pathwayItemManager.getPathwayVertexRep(pick.getObjectID());

				// Load embedded pathway
				if (vertexRep.getType() == EPathwayVertexType.map) {
					PathwayGraph pathway = PathwayManager.get().getPathwayByTitle(vertexRep.getName(),
							EPathwayDatabaseType.KEGG);

					if (pathway != null) {
						LoadPathwayEvent event = new LoadPathwayEvent();
						event.setSender(this);
						event.setPathwayID(pathway.getID());
						GeneralManager.get().getEventPublisher().triggerEvent(event);
					}
				}

				handlePathwayElementSelection(SelectionType.SELECTION, pick.getObjectID());

				triggerNodeEvents(pick.getPickingMode(), vertexRep);
			}

			@Override
			public void rightClicked(Pick pick) {

				if (detailLevel == EDetailLevel.VERY_LOW || !highlightVertices) {
					return;
				}

				PathwayVertexRep vertexRep = pathwayItemManager.getPathwayVertexRep(pick.getObjectID());

				if (vertexRep.getType() == EPathwayVertexType.map) {

					LoadPathwaysByPathwayItem menuItem = new LoadPathwaysByPathwayItem(PathwayManager.get()
							.getPathwayByTitle(vertexRep.getName(), EPathwayDatabaseType.KEGG));
					contextMenuCreator.addContextMenuItem(menuItem);

				} else if (vertexRep.getType() == EPathwayVertexType.gene) {
					if (!isRenderedRemote() && !tablePerspectives.isEmpty()) {
						ATableBasedDataDomain datadomain = tablePerspectives.get(0).getDataDomain();
						for (PathwayVertex pathwayVertex : vertexRep.getPathwayVertices()) {
							for (Integer davidID : pathwayItemManager.getDavidIdByPathwayVertex(pathwayVertex)) {
								GeneMenuItemContainer contexMenuItemContainer = new GeneMenuItemContainer();
								contexMenuItemContainer.setDataDomain(datadomain);
								contexMenuItemContainer.setData(pathwayDataDomain.getDavidIDType(), davidID);
								contextMenuCreator.addContextMenuItemContainer(contexMenuItemContainer);
							}
						}
					}
					for (VertexRepBasedContextMenuItem item : addedContextMenuItems) {
						item.setVertexRep(vertexRep);
						contextMenuCreator.addContextMenuItem(item);
					}
				}

				triggerNodeEvents(pick.getPickingMode(), vertexRep);

				// handlePathwayElementSelection(SelectionType.SELECTION, pick.getObjectID());
			}
		}, EPickingType.PATHWAY_ELEMENT_SELECTION.name());

		addTypePickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {

				if (allPaths == null || allPaths.size() <= pick.getObjectID())
					return;

				// selectedPath = allPaths.get(pick.getObjectID());
				if (allPaths.size() <= selectedPathID)
					selectedPathID = 0;

				selectedPath = allPaths.get(selectedPathID);

				setDisplayListDirty();
				triggerPathUpdate();
			}

		}, EPickingType.PATHWAY_PATH_SELECTION.name());

		addTypePickingListener(new APickingListener() {
			@Override
			public void clicked(Pick pick) {
				// handlePathwayElementSelection(SelectionType.SELECTION, -1);
				handlePathwayTextureSelection(SelectionType.SELECTION);

				if (allPaths == null || allPaths.isEmpty())
					return;

				int pickX = (int) pick.getPickedPoint().getX();
				int pickY = (int) pick.getPickedPoint().getY();

				float pathwayTextureScaling = 1;

				int iImageWidth = pathway.getWidth();
				int iImageHeight = pathway.getHeight();

				if (iImageWidth == -1 || iImageHeight == -1) {
					Logger.log(new Status(IStatus.ERROR, this.toString(),
							"Problem because pathway texture width or height is invalid!"));
				}

				pathwayTextureScaling = pathway.getHeight()
						/ (float) pixelGLConverter.getPixelHeightForGLHeight(viewFrustum.getHeight());

				pickX = (int) ((pickX - pixelGLConverter.getPixelWidthForGLWidth(vecTranslation.x())) * pathwayTextureScaling);
				pickY = (int) ((pickY - pixelGLConverter.getPixelHeightForGLHeight(vecTranslation.y())) * pathwayTextureScaling);

				// code adapted from documentation at
				// http://docs.oracle.com/javase/6/docs/api/java/awt/image/PixelGrabber.html
				int[] pixels = bubbleSet.getBubbleSetGLRenderer().getPxl(pickX, pickX);
				int alpha = (pixels[0] >> 24) & 0xff;
				int red = (pixels[0] >> 16) & 0xff;
				int green = (pixels[0] >> 8) & 0xff;
				int blue = (pixels[0]) & 0xff;
				// System.out.println("DENIS_DEBUG:: pickedRed:" + red +
				// " pickedGreen:" + green + " pickedBlue:" + blue
				// + " pickedAlpha:" + alpha);
				// look up color
				List<org.caleydo.core.util.color.Color> colorTable = (ColorManager.get())
						.getColorList("qualitativeColors");
				float[] cComponents = new float[4];
				for (int i = 0; i < colorTable.size() - 2; i++) {
					org.caleydo.core.util.color.Color c = colorTable.get(i);
					//
					int threshold = 10;
					cComponents = c.getRGB();
					if (red > (int) (cComponents[0] * 255f) - threshold
							&& red < (int) (cComponents[0] * 255f) + threshold) {
						// System.out.println("DENIS_DEBUG:: found usedColor id=" + i);
						// select
						selectedPathID = i;
						if (selectedPathID > allPaths.size() - 1)
							selectedPathID = allPaths.size() - 1;
						selectedPath = allPaths.get(selectedPathID);
						isBubbleTextureDirty = true;
						setDisplayListDirty();
						triggerPathUpdate();
						i = colorTable.size();
					}
				}
			}

			// @Override
			// protected void mouseOver(Pick pick) {
			// PathwayTextureSelectionEvent event = new PathwayTextureSelectionEvent(pathway);
			// event.setEventSpace(pathwayPathEventSpace);
			// EventPublisher.INSTANCE.triggerEvent(event);
			// }

		}, EPickingType.PATHWAY_TEXTURE_SELECTION.name());
	}

	@Override
	public void displayLocal(final GL2 gl) {

		// Check if pathway exists or if it's already loaded
		if (pathway == null || !pathwayManager.hasItem(pathway.getID())) {
			if (isDisplayListDirty) {
				gl.glNewList(displayListIndex, GL2.GL_COMPILE);
				renderEmptyViewText(gl, new String[] {
						"Please select a pathway map from the pathway dropdown box in the toolbar.",
						"To map experimental data select one of the available datasets from the dataset dropdown box.",
						"Refer to http://help.caleydo.org for more information." });
				gl.glEndList();
				isDisplayListDirty = false;
			}
			gl.glCallList(displayListIndex);
			return;
		}

		if (isPathwayDataDirty)
			initPathwayData(gl);

		pickingManager.handlePicking(this, gl);
		display(gl);
	}

	@Override
	public void displayRemote(final GL2 gl) {
		processEvents();
		display(gl);
	}

	@Override
	public void display(final GL2 gl) {
		if (isDynamicDetail) {
			setHighlightVertices(true);
			if (viewFrustum.getHeight() <= pixelGLConverter.getGLHeightForPixelHeight(120)
					|| viewFrustum.getWidth() <= pixelGLConverter.getGLWidthForPixelWidth(120)) {
				setHighlightVertices(false);
			}
		}

		checkForHits(gl);

		if (isDisplayListDirty) {
			calculatePathwayScaling(gl, pathway);
			rebuildPathwayDisplayList(gl);
			isDisplayListDirty = false;
		}

		if (pathway != null) {
			// TODO: also put this in global DL
			renderPathway(gl, pathway);
		}
		areContextPathsDirty = false;
		// There is obviously some blending issue, when pathways are involved, therefore do this...
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
	}

	protected void initPathwayData(final GL2 gl) {

		isPathwayDataDirty = false;
		isDisplayListDirty = true;

		selectedPath = null;
		allPaths = null;

		augmentationRenderer.init(gl, vertexSelectionManager);
		vertexSelectionManager.clearSelections();

		// Create new pathway manager for GL2 context
		pathwayTextureManager = new GLPathwayTextureManager();

		calculatePathwayScaling(gl, pathway);
		pathwayManager.setPathwayVisibilityState(pathway, true);

		// gLPathwayAugmentationRenderer.buildPathwayDisplayList(gl, this,
		// iPathwayID);
	}

	public static String readFromStream(InputStream ins) throws IOException {
		if (ins == null) {
			throw new IOException("Could not read from stream.");
		}
		StringBuffer buffer = new StringBuffer();
		Scanner scanner = new Scanner(ins);
		try {
			while (scanner.hasNextLine()) {
				buffer.append(scanner.nextLine() + "\n");
			}
		} finally {
			scanner.close();
		}
		return buffer.toString();
	}

	protected boolean initShader = false;
	public int shaderProgramTextOverlay;

	public void initShaders(GL2 gl) throws IOException {
		int vs = gl.glCreateShader(GL2.GL_VERTEX_SHADER);
		int fs = gl.glCreateShader(GL2.GL_FRAGMENT_SHADER);
		String vsrc = readFromStream(this.getClass().getResourceAsStream(
				"/src/org/caleydo/view/pathway/vsTextOverlay.glsl"));
		gl.glShaderSource(vs, 1, new String[] { vsrc }, (int[]) null, 0);
		gl.glCompileShader(vs);

		String fsrc = readFromStream(this.getClass().getResourceAsStream(
				"/src/org/caleydo/view/pathway/fsTextOverlay.glsl"));
		gl.glShaderSource(fs, 1, new String[] { fsrc }, (int[]) null, 0);
		gl.glCompileShader(fs);

		shaderProgramTextOverlay = gl.glCreateProgram();
		gl.glAttachShader(shaderProgramTextOverlay, vs);
		gl.glAttachShader(shaderProgramTextOverlay, fs);
		gl.glLinkProgram(shaderProgramTextOverlay);
		gl.glValidateProgram(shaderProgramTextOverlay);

		// gl.glUseProgram(shaderprogramTextOutline);
		initShader = true;

	}

	private void renderPathway(final GL2 gl, final PathwayGraph pathway) {

		// //////////////////////////START 1/2 HIER NEU CHRISITIAN
		if (!initShader) {
			try {
				initShaders(gl);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// //////////////////////////START 1/2 HIER NEU CHRISITIAN

		gl.glPushMatrix();
		gl.glTranslatef(vecTranslation.x(), vecTranslation.y(), vecTranslation.z());
		gl.glScalef(vecScaling.x(), vecScaling.y(), vecScaling.z());
		float textureOffset = 0.0f;// to avoid z fighting
		if (enablePathwayTexture) {
			float fPathwayTransparency = 1.0f;

			pathwayTextureManager.renderPathway(gl, this, pathway, fPathwayTransparency, false);
		}

		float pathwayHeight = pixelGLConverter.getGLHeightForPixelHeight(pathway.getHeight());

		// Pathway texture height is subtracted from Y to align pathways to
		// front level
		gl.glEnable(GL.GL_STENCIL_TEST);
		gl.glClearStencil(0);
		gl.glClear(GL.GL_STENCIL_BUFFER_BIT);
		textureOffset += PathwayRenderStyle.Z_OFFSET;
		gl.glTranslatef(0, pathwayHeight, textureOffset);
		// if (!this.highlightVertices)
		// augmentationRenderer.setVisible(false);
		augmentationRenderer.renderPathway(gl, pathway, false);
		gl.glTranslatef(0, -pathwayHeight, -textureOffset);

		if (enablePathwayTexture) {
			float fPathwayTransparency = 1.0f;
			textureOffset += PathwayRenderStyle.Z_OFFSET;
			gl.glTranslatef(0.0f, 0.0f, textureOffset);

			gl.glEnable(GL.GL_STENCIL_TEST);
			gl.glDisable(GL.GL_DEPTH_TEST);
			// gl.glStencilFunc(GL2.GL_EQUAL, 0, 1);
			gl.glStencilFunc(GL.GL_GREATER, 2, 0xff);
			gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_KEEP);
			gl.glPushName(generalManager.getViewManager().getPickingManager()
					.getPickingID(uniqueID, EPickingType.PATHWAY_TEXTURE_SELECTION.name(), 0));
			// //////////////////////////START 2/2 HIER NEU CHRISITIAN
			// enable shader
			// gl.glUseProgram(shaderProgramTextOverlay);
			// int pathwayTex = gl.glGetUniformLocation(shaderProgramTextOverlay, "pathwayTex");
			// gl.glUniform1i(pathwayTex, 0);
			pathwayTextureManager.renderPathway(gl, this, pathway, fPathwayTransparency, false);
			// gl.glUseProgram(0);
			// disable shader
			// //////////////////////////END 2/2 HIER NEU CHRISITIAN
			// pathwayTextureManager.renderPathway(gl, this, pathway, fPathwayTransparency, false);
			gl.glPopName();

			gl.glStencilFunc(GL.GL_GREATER, 1, 0xff);
			gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_KEEP);
			textureOffset -= 2f * PathwayRenderStyle.Z_OFFSET;
			gl.glTranslatef(0.0f, 0.0f, textureOffset);

			overlayContextBubbleSets(gl);
			overlayBubbleSets(gl);

			gl.glEnable(GL.GL_DEPTH_TEST);
			gl.glDisable(GL.GL_STENCIL_TEST);
		}
		// //
		gl.glScalef(1 / vecScaling.x(), 1 / vecScaling.y(), 1 / vecScaling.z());
		gl.glTranslatef(-vecTranslation.x(), -vecTranslation.y(), -vecTranslation.z());

		gl.glDisable(GL.GL_STENCIL_TEST);
		gl.glPopMatrix();
	}

	private void overlayContextBubbleSets(GL2 gl) {
		if (contextPaths.size() < 1)
			return;
		if (areContextPathsDirty) {
			this.contextPathBubbleSet.clear();
			this.contextPathBubbleSet.getBubbleSetGLRenderer().setSize(pathway.getWidth(), pathway.getHeight());
			this.contextPathBubbleSet.setPathwayGraph(pathway);

			// System.out.println("contextPaths"+contextPaths.size());
			this.contextPathBubbleSet.addContextPathSegements(contextPaths);

			// ((BubbleSet)(contextPathBubbleSet.getBubbleSetGLRenderer().setOutline)).setParameter(100, 20, 3, 10.0,
			// 7.0, 0.5, 2.5, 15.0, 5);
			// BubbleSet(routingIterations, marchingIterations,pixelGroup,edgeR0,edgeR1, nodeR0, nodeR1,
			// morphBuffer,skip)
			((BubbleSet) (contextPathBubbleSet.getBubbleSetGLRenderer().setOutline)).setParameter(10, 10, 3, 10.0,
					20.0, 20.5, 5.5, 5.0, 5);
			((BubbleSet) (contextPathBubbleSet.getBubbleSetGLRenderer().setOutline)).useVirtualEdges(false);

			this.contextPathBubbleSet.getBubbleSetGLRenderer().update(gl, null, selectedPathID);
			areContextPathsDirty = false;
		}

		this.contextPathBubbleSet.getBubbleSetGLRenderer().render(gl,
				pixelGLConverter.getGLWidthForPixelWidth(pathway.getWidth()),
				pixelGLConverter.getGLHeightForPixelHeight(pathway.getHeight()), 1.0f);
	}

	private void overlayBubbleSets(GL2 gl) {
		// if (allPaths == null)
		// return;overlayBubbleSets

		if (isBubbleTextureDirty) {
			// //allPaths
			// System.out.println("overlayBubbleSets");
			this.bubbleSet.clear();
			this.alternativeBubbleSet.clear();
			this.bubbleSet.setPathwayGraph(pathway);
			this.alternativeBubbleSet.setPathwayGraph(pathway);

			// if(selectedPathID>=0)
			// allPathsList.get(allPathsList.size()-1).setSecond(this.selectedPathID);
			// else
			// allPathsList.get(allPathsList.size()-1).setSecond(0);
			// if(allPathsList.size()>0)
			// {

			boolean renderAlternatives = false;
			for (PathwayPath pathSegment : pathSegments) {
				if (pathSegment.getPathway() == pathway) {
					renderAlternatives = true;
					continue;
				}
			}
			if (!renderAlternatives) {
				allPathsList.clear();
			}
			for (Pair<List<GraphPath<PathwayVertexRep, DefaultEdge>>, Integer> pathsPair : allPathsList) {
				// this.bubbleSet.addAllPaths(paths);
				List<GraphPath<PathwayVertexRep, DefaultEdge>> paths = pathsPair.getFirst();
				this.alternativeBubbleSet.addAllPaths(paths, pathsPair.getSecond());
			}
			// }
			//
			// System.out.println("overlayBubbleSets pathSegments.size"+pathSegments.size());
			this.bubbleSet.addPathSegements(pathSegments);
			this.bubbleSet.addPathSegements(pathSegments);

			if (this.highlightVertices) {
				((BubbleSet) (bubbleSet.getBubbleSetGLRenderer().setOutline)).setParameter(100, 20, 3, 10.0, 7.0, 0.5,
						2.5, 15.0, 8);
			} else {
				// this.bubbleSet.addPathSegements(pathSegments);
				((BubbleSet) (bubbleSet.getBubbleSetGLRenderer().setOutline)).setParameter(10, 10, 3, 10.0, 20.0, 20.5,
						15.5, 5.0, 5);
			}

			// this.bubbleSet.addPortals(portalVertexReps);

			// update texture
			this.bubbleSet.getBubbleSetGLRenderer().setSize(pathway.getWidth(), pathway.getHeight());
			this.bubbleSet.getBubbleSetGLRenderer().update(gl, null, 0);

			alternativeBubbleSet.getBubbleSetGLRenderer().setSize(pathway.getWidth(), pathway.getHeight());
			// this.bubbleSet.getBubbleSetGLRenderer().update(gl, SelectionType.SELECTION.getColor(), selectedPathID);
			alternativeBubbleSet.getBubbleSetGLRenderer().update(gl, null, 0);

			isBubbleTextureDirty = false;
		}

		gl.glPushName(generalManager.getViewManager().getPickingManager()
				.getPickingID(uniqueID, EPickingType.PATHWAY_TEXTURE_SELECTION.name(), 0));

		this.alternativeBubbleSet.getBubbleSetGLRenderer().render(gl,
				pixelGLConverter.getGLWidthForPixelWidth(pathway.getWidth()),

				pixelGLConverter.getGLHeightForPixelHeight(pathway.getHeight()), 0.25f);

		if (this.highlightVertices) {
			this.bubbleSet.getBubbleSetGLRenderer().render(gl,
					pixelGLConverter.getGLWidthForPixelWidth(pathway.getWidth()),
					pixelGLConverter.getGLHeightForPixelHeight(pathway.getHeight()));
		} else {

			this.bubbleSet.getBubbleSetGLRenderer().render(gl,
					pixelGLConverter.getGLWidthForPixelWidth(pathway.getWidth()),
					pixelGLConverter.getGLHeightForPixelHeight(pathway.getHeight()), 1.0f);
		}

		gl.glPopName();

	}

	private void rebuildPathwayDisplayList(final GL2 gl) {
		augmentationRenderer.buildPathwayDisplayList(gl, pathway);
	}

	private void calculatePathwayScaling(final GL2 gl, final PathwayGraph pathway) {

		if (pathwayTextureManager == null)
			return;

		int pathwayPixelWidth = pathway.getWidth();
		int pathwayPixelHeight = pathway.getHeight();

		if (pathwayPixelWidth == -1 || pathwayPixelHeight == -1) {
			Logger.log(new Status(IStatus.ERROR, this.toString(),
					"Problem because pathway texture width or height is invalid!"));
		}

		float pathwayWidth = pixelGLConverter.getGLWidthForPixelWidth(pathwayPixelWidth);
		float pathwayHeight = pixelGLConverter.getGLHeightForPixelHeight(pathwayPixelHeight);
		float viewFrustumWidth = viewFrustum.getRight() - viewFrustum.getLeft();
		float viewFrustumHeight = viewFrustum.getTop() - viewFrustum.getBottom();

		float pathwayAspectRatio = pathwayWidth / pathwayHeight;
		float viewFrustumAspectRatio = viewFrustumWidth / viewFrustumHeight;
		// boolean pathwayFitsViewFrustum = true;

		// float rendererAspectRatio = x / y;
		// float imageAspectRatio = (float) baseImage.getWidth() / (float) baseImage.getHeight();

		if (isRenderedRemote()) {
			float renderWidth;
			float renderHeight;

			if (pathwayWidth <= viewFrustumWidth && pathwayHeight <= viewFrustumHeight) {
				vecScaling.set(1, 1, 1f);
				renderWidth = pathwayWidth;
				renderHeight = pathwayHeight;
			} else {
				if (viewFrustumAspectRatio > pathwayAspectRatio) {
					renderWidth = (viewFrustumHeight / pathwayHeight) * pathwayWidth;
					renderHeight = viewFrustumHeight;
				} else {
					renderWidth = viewFrustumWidth;
					renderHeight = (viewFrustumWidth / pathwayWidth) * pathwayHeight;
				}
				vecScaling.set(renderWidth / pathwayWidth, renderHeight / pathwayHeight, 1);
			}
			vecTranslation.set((viewFrustumWidth - renderWidth) / 2.0f, (viewFrustumHeight - renderHeight) / 2.0f, 0);
			return;

		}

		// Center pathway in x direction
		if (pathwayWidth < viewFrustumWidth) {
			vecTranslation.setX((viewFrustumWidth - pathwayWidth) / 2.0f);
		}

		// Center pathway in y direction
		if (pathwayHeight < viewFrustumWidth) {
			vecTranslation.setY((viewFrustumHeight - pathwayHeight) / 2.0f);
		}
	}

	@ListenTo
	public void onMapTablePerspective(PathwayMappingEvent event) {
		if (event.getReceiver() != this)
			return;
		augmentationRenderer.setMappingPerspective(event.getTablePerspective());
		setDisplayListDirty();
	}

	public void enablePathwayTextures(final boolean enablePathwayTexture) {
		this.enablePathwayTexture = enablePathwayTexture;
		setDisplayListDirty();
	}

	@Override
	public String getViewName() {
		if (pathway == null)
			return VIEW_NAME;
		return VIEW_NAME + ": " + pathway.getName();
	}

	@Override
	public void initData() {
		super.initData();
	}

	@Override
	public void destroyViewSpecificContent(GL2 gl) {
		pathwayManager.setPathwayVisibilityState(pathway, false);
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();

		// enableGeneMappingListener = new EnableGeneMappingListener();
		// enableGeneMappingListener.setHandler(this);
		// eventPublisher.addListener(PathwayMappingEvent.class, enableGeneMappingListener);

		enRoutePathEventListener = new EnRoutePathEventListener();
		enRoutePathEventListener.setExclusiveEventSpace(pathwayPathEventSpace);
		enRoutePathEventListener.setHandler(this);
		eventPublisher.addListener(PathwayPathSelectionEvent.class, enRoutePathEventListener);

		selectPathModeEventListener = new SelectPathModeEventListener();
		selectPathModeEventListener.setExclusiveEventSpace(pathwayPathEventSpace);
		selectPathModeEventListener.setHandler(this);
		eventPublisher.addListener(EnablePathSelectionEvent.class, selectPathModeEventListener);

		addTablePerspectivesListener = new AddTablePerspectivesListener<>();
		addTablePerspectivesListener.setHandler(this);
		eventPublisher.addListener(AddTablePerspectivesEvent.class, addTablePerspectivesListener);

		sampleMappingModeListener = new SampleMappingModeListener();
		sampleMappingModeListener.setHandler(this);
		eventPublisher.addListener(SampleMappingModeEvent.class, sampleMappingModeListener);

		updateColorMappingListener = new UpdateColorMappingListener();
		updateColorMappingListener.setHandler(this);
		eventPublisher.addListener(UpdateColorMappingEvent.class, updateColorMappingListener);

		showPortalNodesEventListener = new ShowPortalNodesEventListener();
		showPortalNodesEventListener.setHandler(this);
		showPortalNodesEventListener.setEventSpace(pathwayPathEventSpace);
		listeners.register(ShowNodeContextEvent.class, showPortalNodesEventListener);

		listeners.register(this);
	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();

		listeners.unregisterAll();

		if (enableGeneMappingListener != null) {
			eventPublisher.removeListener(PathwayMappingEvent.class, enableGeneMappingListener);
			enableGeneMappingListener = null;
		}

		if (enRoutePathEventListener != null) {
			eventPublisher.removeListener(enRoutePathEventListener);
			enRoutePathEventListener = null;
		}

		if (selectPathModeEventListener != null) {
			eventPublisher.removeListener(selectPathModeEventListener);
			selectPathModeEventListener = null;
		}

		if (addTablePerspectivesListener != null) {
			eventPublisher.removeListener(addTablePerspectivesListener);
			addTablePerspectivesListener = null;
		}

		if (sampleMappingModeListener != null) {
			eventPublisher.removeListener(sampleMappingModeListener);
			sampleMappingModeListener = null;
		}

		if (updateColorMappingListener != null) {
			eventPublisher.removeListener(updateColorMappingListener);
			updateColorMappingListener = null;
		}

		metaboliteSelectionManager.unregisterEventListeners();
		// pathwaySelectionManager.unregisterEventListeners();
		vertexSelectionManager.unregisterEventListeners();

	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedPathwayView serializedForm = new SerializedPathwayView(pathwayDataDomain.getDataDomainID(), this);
		// FIXME this needs to be reviewed - what is the unique, serializable ID
		// of the pathway here?
		if (pathway != null)
			serializedForm.setPathwayID(pathway.getID());

		serializedForm.setPathSelectionMode(isPathSelectionMode);
		serializedForm.setMappingMode(sampleMappingMode);

		System.out.println("Serializing Pathway: review me!");

		return serializedForm;
	}

	public PathwayDataDomain getPathwayDataDomain() {
		return pathwayDataDomain;
	}

	@Override
	public int getMinPixelHeight() {
		if (minHeightPixels != -1)
			return minHeightPixels;
		if (pathway == null)
			return 120;
		return (int) (pathway.getHeight() * 0.8f);
	}

	@Override
	public int getMinPixelWidth() {
		if (minWidthPixels != -1)
			return minWidthPixels;
		if (pathway == null)
			return 120;
		return (int) (pathway.getWidth() * 0.8f);
	}

	@Override
	public boolean isDataView() {
		return true;
	}

	// @Override
	// public void setDataDomain(ATableBasedDataDomain dataDomain) {
	// if (dataDomain == null) {
	// if (gLPathwayAugmentationRenderer != null) {
	// gLPathwayAugmentationRenderer.enableGeneMapping(false);
	// }
	// return;
	// }
	// if (!(dataDomain instanceof GeneticDataDomain))
	// throw new IllegalArgumentException("Pathway view can handle only genetic data domain, tried to set: "
	// + dataDomain);
	//
	// this.dataDomain = (GeneticDataDomain) dataDomain;
	//
	// if (gLPathwayAugmentationRenderer != null) {
	// gLPathwayAugmentationRenderer.enableGeneMapping(true);
	// }
	// // only make a new sample selection manager if necessary due to
	// // different id category or because it wasn't initalized so far
	// if (sampleSelectionManager == null
	// || !sampleSelectionManager.getIDType().getIDCategory()
	// .equals(this.dataDomain.getSampleIDType().getIDCategory())) {
	// sampleSelectionManager = new EventBasedSelectionManager(this,
	// ((GeneticDataDomain) dataDomain).getSampleIDType());
	// }
	// setDisplayListDirty();
	//
	// }

	@Override
	public void addTablePerspective(TablePerspective tablePerspective) {
		if (tablePerspective == null || !tablePerspective.getDataDomain().hasIDCategory(geneIDType)) {
			throw new IllegalStateException("Perspective null or illegal for this view: " + tablePerspective);
		}
		if (tablePerspectives.contains(tablePerspective))
			return;
		tablePerspectives.add(tablePerspective);
		if (tablePerspective instanceof PathwayTablePerspective)
			pathway = ((PathwayTablePerspective) tablePerspective).getPathway();

		if (sampleSelectionManager == null) {
			sampleIDType = tablePerspective.getDataDomain().getOppositeIDType(geneIDType).getIDCategory()
					.getPrimaryMappingType();
			sampleSelectionManager = new EventBasedSelectionManager(this, sampleIDType);
		}

		setDisplayListDirty();

		// DataDomainUpdateEvent event = new DataDomainUpdateEvent(tablePerspective.getDataDomain());
		// eventPublisher.triggerEvent(event);

		TablePerspectivesChangedEvent tbEvent = new TablePerspectivesChangedEvent(this);
		eventPublisher.triggerEvent(tbEvent);

	}

	/**
	 * @return the vertexSelectionManager, see {@link #vertexSelectionManager}
	 */
	public SelectionManager getGeneSelectionManager() {
		return vertexSelectionManager;
	}

	/**
	 * @return the sampleSelectionManager, see {@link #sampleSelectionManager}
	 */
	public SelectionManager getSampleSelectionManager() {
		return sampleSelectionManager;
	}

	@Override
	public int getMinPixelWidth(EDetailLevel detailLevel) {
		return 200;
	}

	public void handlePathwayTextureSelection(SelectionType selectionType) {
		setDisplayListDirty();
		if (selectionType == SelectionType.SELECTION) {
			handlePathwayTextureMouseClick();
		}
	}

	private void handlePathwayTextureMouseClick() {
		if (selectedPath != null) {
			isPathStartSelected = false;
			vertexSelectionManager.clearSelection(SelectionType.SELECTION);
			metaboliteSelectionManager.clearSelection(SelectionType.SELECTION);
			metaboliteSelectionManager.triggerSelectionUpdateEvent();
			// Add new vertex to internal selection manager
			vertexSelectionManager.addToType(SelectionType.SELECTION, selectedPath.getEndVertex().getID());
			SelectionDelta selectionDelta = vertexSelectionManager.getDelta();

			SelectionUpdateEvent event = new SelectionUpdateEvent();
			event.setSender(this);
			event.setSelectionDelta(selectionDelta);
			eventPublisher.triggerEvent(event);

			if (/* isControlKeyDown || */this.isShiftKeyDown) {
				previousSelectedPath = selectedPath;
			}
		}
	}

	private GraphPath<PathwayVertexRep, DefaultEdge> copyPath(GraphPath<PathwayVertexRep, DefaultEdge> path) {
		if (path == null)
			return null;

		List<DefaultEdge> edgeList = path.getEdgeList();
		List<DefaultEdge> edgeListNew = new ArrayList<DefaultEdge>();

		PathwayVertexRep startVertex = path.getStartVertex();
		PathwayVertexRep endVertex = path.getEndVertex();

		for (int i = 0; i < edgeList.size(); i++) {
			DefaultEdge edge = edgeList.get(i);
			endVertex = pathway.getEdgeTarget(edge);
			edgeListNew.add(edge);
		}

		GraphPath<PathwayVertexRep, DefaultEdge> newPath = new GraphPathImpl<PathwayVertexRep, DefaultEdge>(pathway,
				startVertex, endVertex, edgeListNew, 0);

		return newPath;
	}

	private void shrinkSelectedPath(PathwayVertexRep vertexRep) {
		if (previousSelectedPath == null)
			return;
		List<DefaultEdge> edgeListPrev = previousSelectedPath.getEdgeList();
		List<DefaultEdge> edgeListNew = new ArrayList<DefaultEdge>();
		PathwayVertexRep startVertex = previousSelectedPath.getStartVertex();
		PathwayVertexRep endVertex = previousSelectedPath.getEndVertex();
		if (vertexRep == startVertex) {
			generateSingleNodePath(vertexRep);
		} else {
			for (int i = 0; i < edgeListPrev.size(); i++) {
				DefaultEdge edge = edgeListPrev.get(i);
				endVertex = pathway.getEdgeTarget(edge);
				edgeListNew.add(edge);
				if (vertexRep == endVertex)
					break;
			}
			GraphPath<PathwayVertexRep, DefaultEdge> tmpSelectedPath = new GraphPathImpl<PathwayVertexRep, DefaultEdge>(
					pathway, startVertex, endVertex, edgeListNew, 0);
			//
			if (allPaths == null) {
				allPaths = new ArrayList<GraphPath<PathwayVertexRep, DefaultEdge>>();
			} else
				allPaths.clear();
			allPaths.add(tmpSelectedPath);
			selectedPathID = 0;
			selectedPath = tmpSelectedPath;
		}
	}

	private void extendSelectedPath(PathwayVertexRep vertexRep) {
		if (previousSelectedPath == null)
			return;
		PathwayVertexRep endVertex = previousSelectedPath.getEndVertex();
		KShortestPaths<PathwayVertexRep, DefaultEdge> pathAlgo = new KShortestPaths<PathwayVertexRep, DefaultEdge>(
				pathway, endVertex, MAX_PATHS);
		if (vertexRep != endVertex) {
			allPaths = pathAlgo.getPaths(vertexRep);
		} else {
			allPaths = null;
		}
		if (allPaths == null) {
			allPaths = new ArrayList<GraphPath<PathwayVertexRep, DefaultEdge>>();
			allPaths.add(previousSelectedPath);
			selectedPathID = 0;
			selectedPath = previousSelectedPath;
		} else {
			List<DefaultEdge> edgeListPrev = previousSelectedPath.getEdgeList();
			PathwayVertexRep startExtVertex = previousSelectedPath.getStartVertex();
			int idx = 0;
			for (GraphPath<PathwayVertexRep, DefaultEdge> path : allPaths) {
				List<DefaultEdge> edgeListExt = new ArrayList<DefaultEdge>();
				edgeListExt.addAll(edgeListPrev);
				PathwayVertexRep endExtVertex = path.getEndVertex();
				List<DefaultEdge> edgeListNew = path.getEdgeList();
				edgeListExt.addAll(edgeListNew);
				GraphPath<PathwayVertexRep, DefaultEdge> extendedPath = new GraphPathImpl<PathwayVertexRep, DefaultEdge>(
						pathway, startExtVertex, endExtVertex, edgeListExt, 0);
				allPaths.set(idx, extendedPath);
				idx++;
			}
			if (allPaths.size() <= selectedPathID)
				selectedPathID = 0;
			selectedPath = allPaths.get(selectedPathID);
		}

	}

	private void generateSingleNodePath(PathwayVertexRep vertexRep) {
		// System.out.println("generateSingleNodePath");
		GraphPath<PathwayVertexRep, DefaultEdge> path = new GraphPathImpl<PathwayVertexRep, DefaultEdge>(pathway,
				vertexRep, vertexRep, new ArrayList<DefaultEdge>(), 0);
		if (allPaths == null) {
			allPaths = new ArrayList<GraphPath<PathwayVertexRep, DefaultEdge>>();
		} else
			allPaths.clear();
		allPaths.add(path);
		allPathsList.add(new Pair<List<GraphPath<PathwayVertexRep, DefaultEdge>>, Integer>(allPaths, 0));
		selectedPath = path;
		selectedPathID = 0;
	}

	private void selectPath(PathwayVertexRep vertexRep, SelectionType selectionType) {
		if (vertexRep == null || isControlKeyDown)
			return;
		boolean triggerPathUpdate = false;
		if (!isPathStartSelected) {// ////////////////////////////////
			// if (isControlKeyDown) {// shrink previous selected path
			// shrinkSelectedPath(vertexRep);
			// if (selectionType == SelectionType.SELECTION) {// click on
			// previousSelectedPath = selectedPath;
			// }
			// triggerPathUpdate = true;
			// }
			// //////////////////////////////
			if (isShiftKeyDown) {// extend previous selected path
				extendSelectedPath(vertexRep);
				if (selectionType == SelectionType.SELECTION) {// click on
					previousSelectedPath = selectedPath;
				}
				triggerPathUpdate = true;
			}
			if (!isShiftKeyDown && /* !isControlKeyDown && */vertexRep != null) {
				// no interaction with the previous selected path
				// select vertexRep as startPoint and switch to
				// end_point_selection_mode

				if (selectionType == SelectionType.SELECTION) {

					boolean isPortalNode = false;
					// if (previousSelectedPath != null) {
					// portalVertexReps = PathwayManager.get().getEquivalentVertexRepsInPathway(
					// previousSelectedPath.getEndVertex(), pathway);
					if (pathSegments != null && pathSegments.size() > 0) {
						portalVertexReps = PathwayManager.get().getEquivalentVertexRepsInPathway(
								pathSegments.get(pathSegments.size() - 1).getPath().getEndVertex(), pathway);
						for (PathwayVertexRep portal : portalVertexReps) {
							if (vertexRep == portal) {
								isPortalNode = true;
							}
						}
					}
					if (!isPortalNode) {
						pathSegments.clear();
						allPathsList.clear();
					}

					generateSingleNodePath(vertexRep);
					pathSegments.add(new PathwayPath(selectedPath));
					isPathStartSelected = true;
					triggerPathUpdate = true;
				}
			}
		} else {// //////// select end node /////////////////////////
			if (pathStartVertexRep == null)
				return;
			if (!pathway.containsVertex(pathStartVertexRep)) {
				isPathStartSelected = false;
				return;
			}
			KShortestPaths<PathwayVertexRep, DefaultEdge> pathAlgo = new KShortestPaths<PathwayVertexRep, DefaultEdge>(
					pathway, pathStartVertexRep, MAX_PATHS);
			List<GraphPath<PathwayVertexRep, DefaultEdge>> allPathsTmp = null;
			if (vertexRep != null) {
				if (vertexRep != pathStartVertexRep) {
					allPathsTmp = pathAlgo.getPaths(vertexRep);
					// if at least one path exist update the selected path
					if (allPathsTmp != null && allPathsTmp.size() > 0) {
						allPaths = allPathsTmp;
						// allPathsList.set(allPathsList.size()-1,new Pair<List<GraphPath<PathwayVertexRep,
						// DefaultEdge>>, Integer>(allPaths,0);
						allPathsList.get(allPathsList.size() - 1).setFirst(allPaths);
						if (allPaths.size() <= selectedPathID)
							selectedPathID = 0;
						selectedPath = allPaths.get(selectedPathID);
						triggerPathUpdate = true;
					}
				} else {
					generateSingleNodePath(vertexRep);
				}
			}
			// update
			if (pathSegments.size() > 0)
				pathSegments.set(pathSegments.size() - 1, new PathwayPath(selectedPath));
			if (selectionType == SelectionType.SELECTION) {// click on
															// end node
				isPathStartSelected = false;
				previousSelectedPath = selectedPath;
				//
			}
		}

		if (triggerPathUpdate) {
			triggerPathUpdate();
			isBubbleTextureDirty = true;
		}
	}

	public void handlePathwayElementSelection(SelectionType selectionType, int externalID) {
		setDisplayListDirty();
		PathwayVertexRep vertexRep = pathwayItemManager.getPathwayVertexRep(externalID);

		if (isPathSelectionMode && !isControlKeyDown) {

			if (!isPathStartSelected && selectionType == SelectionType.SELECTION) {
				pathStartVertexRep = vertexRep;
				// pathwayItemManager.getPathwayVertexRep((Integer) vertexSelectionManager
				// .getElements(SelectionType.SELECTION).toArray()[0]);
			}
			selectPath(vertexRep, selectionType);
		}

		vertexSelectionManager.clearSelection(selectionType);
		if (metaboliteSelectionManager.getNumberOfElements(selectionType) > 0) {
			metaboliteSelectionManager.clearSelection(selectionType);
			metaboliteSelectionManager.triggerSelectionUpdateEvent();
		}

		if (vertexRep.getType() == EPathwayVertexType.compound) {
			metaboliteSelectionManager.addToType(selectionType, vertexRep.getName().hashCode());
			metaboliteSelectionManager.triggerSelectionUpdateEvent();
		}

		// Add new vertex to internal selection manager
		vertexSelectionManager.addToType(selectionType, vertexRep.getID());

		SelectionDelta selectionDelta = vertexSelectionManager.getDelta();
		SelectionUpdateEvent event = new SelectionUpdateEvent();
		event.setSender(this);
		event.setSelectionDelta(selectionDelta);
		eventPublisher.triggerEvent(event);

		// if (vertexRep.getType() == EPathwayVertexType.map) {
		// PathwayGraph pathway = PathwayManager.get().getPathwayByTitle(vertexRep.getName(),
		// EPathwayDatabaseType.KEGG);
		// if (pathway != null) {
		// pathwaySelectionManager.clearSelections();
		// if (selectionType == SelectionType.SELECTION || selectionType == SelectionType.MOUSE_OVER) {
		// pathwaySelectionManager.addToType(selectionType, pathway.getID());
		// }
		// pathwaySelectionManager.triggerSelectionUpdateEvent();
		// }
		// }

		// TODO: make sure that this is the last vertex of the last path segment
		// if (selectedPath != null && vertexRep == selectedPath.getEndVertex()
		// && selectedPath.getEdgeList().size() > 0) {
		// if(showPortals){
		// ShowPortalNodesEvent e = new ShowPortalNodesEvent(vertexRep);
		// e.setSender(this);
		// e.setEventSpace(pathwayPathEventSpace);
		// eventPublisher.triggerEvent(e);
		// // the event will not be sent back to this pathway object, so highlight must be triggered here
		// //updatePortalVertexReps(vertexRep);
		// portalVertexReps = PathwayManager.get().getEquivalentVertexRepsInPathway(vertexRep, pathway);

		// }

		// }
	}

	private void triggerPathUpdate() {
		// List<PathwayPath> pathSegments = new ArrayList<>(1);
		PathwayPathSelectionEvent pathEvent = new PathwayPathSelectionEvent();

		// for (PathwayPath pathSegment : pathSegmentList) {
		// pathSegments.add(pathSegment);
		// }
		// if (selectedPath != null && pathSegments!=null && pathSegments.size()>0) {
		// //pathSegments.get(pathSegments.size()-1).setPathway(selectedPath);
		// //pathSegments.set(pathSeg, element)
		// }
		if (selectedPath != null) {
			if (pathSegments.size() > 0)
				pathSegments.set(pathSegments.size() - 1, new PathwayPath(selectedPath));
			else
				pathSegments.add(new PathwayPath(selectedPath));
		}
		pathEvent.setPathSegments(pathSegments);
		pathEvent.setSender(this);
		pathEvent.setEventSpace(pathwayPathEventSpace);
		eventPublisher.triggerEvent(pathEvent);
	}

	/**
	 * @param selectedPath
	 *            setter, see {@link #selectedPath}
	 */
	public void setSelectedPathSegments(List<PathwayPath> pathSegmentsBroadcasted) {
		// System.out.println("pathSegmentsBroadcasted");
		if (pathSegmentsBroadcasted == null) {
			// System.out.println("(pathSegmentsBroadcasted == null");
			return;
		}
		// System.out.println("(pathSegmentsBroadcasted.size()="+pathSegmentsBroadcasted.size());
		pathSegments = pathSegmentsBroadcasted;
		boolean wasPathSelected = this.selectedPath != null;
		this.selectedPath = null;
		for (PathwayPath path : pathSegments) {
			// TODO: Handle multiple path segments
			if (path.getPathway() == pathway) {
				this.selectedPath = path.getPath();
				break;
			}
		}

		allPaths = new ArrayList<GraphPath<PathwayVertexRep, DefaultEdge>>();
		if (selectedPath != null)
			allPaths.add(selectedPath);

		if ((wasPathSelected && this.selectedPath == null) || this.selectedPath != null) {
			isBubbleTextureDirty = true;
			setDisplayListDirty();
		}
		isBubbleTextureDirty = true;
	}

	/**
	 * @return
	 */
	public IPickingListener getPathwayElementPickingListener() {

		return pathwayElementPickingListener;
	}

	@Override
	public void notifyOfSelectionChange(EventBasedSelectionManager selectionManager) {
		if (highlightVertices)
			setDisplayListDirty();
	}

	/**
	 * @return the metaboliteSelectionManager, see {@link #metaboliteSelectionManager}
	 */
	public EventBasedSelectionManager getMetaboliteSelectionManager() {
		return metaboliteSelectionManager;
	}

	/**
	 * @return the isPathSelectionMode, see {@link #isPathSelectionMode}
	 */
	public boolean isPathSelectionMode() {
		return isPathSelectionMode;
	}

	@Override
	public IDataSupportDefinition getDataSupportDefinition() {
		return new GeneticDataSupportDefinition();
	}

	@Override
	public void handleRedrawView() {
		setDisplayListDirty();
	}

	// @Override
	// public void handleClearSelections() {
	// vertexSelectionManager.clearSelections();
	// sampleSelectionManager.clearSelections();
	// metaboliteSelectionManager.clearSelections();
	// }

	@Override
	public List<TablePerspective> getTablePerspectives() {

		return tablePerspectives;
	}

	/**
	 * @param sampleMappingMode
	 *            setter, see {@link #sampleMappingMode}
	 */
	public void setSampleMappingMode(ESampleMappingMode sampleMappingMode) {
		if (this.sampleMappingMode != sampleMappingMode)
			setDisplayListDirty();
		this.sampleMappingMode = sampleMappingMode;

	}

	/**
	 * @return the sampleMappingMode, see {@link #sampleMappingMode}
	 */
	public ESampleMappingMode getSampleMappingMode() {
		return sampleMappingMode;
	}

	@Override
	public void addTablePerspectives(List<TablePerspective> newTablePerspectives) {
		for (TablePerspective tp : newTablePerspectives)
			addTablePerspective(tp);

	}

	@Override
	public void removeTablePerspective(TablePerspective tablePerspective) {
		Iterator<TablePerspective> i = tablePerspectives.iterator();
		while (i.hasNext()) {
			if (i.next().equals(tablePerspective))
				i.remove();
		}

	}

	// @Override
	// public Set<IDataDomain> getDataDomains() {
	// Set<IDataDomain> dataDomains = new HashSet<IDataDomain>(1);
	// dataDomains.add(dataDomain);
	// return dataDomains;
	// }

	@Override
	public void updateColorMapping() {
		setDisplayListDirty();
	}

	/**
	 * @param pathwayPathEventSpace
	 *            setter, see {@link pathwayPathEventSpace}
	 */
	public void setPathwayPathEventSpace(String pathwayPathEventSpace) {
		this.pathwayPathEventSpace = pathwayPathEventSpace;
	}

	/**
	 * @return the pathwayPathEventSpace, see {@link #pathwayPathEventSpace}
	 */
	public String getPathwayPathEventSpace() {
		return pathwayPathEventSpace;
	}

	/**
	 * Highlights all nodes equivalent to the specified {@link PathwayVertexRep}.
	 *
	 * @param vertexRep
	 */
	public void highlightPortalNodes(PathwayVertexRep vertexRep) {
		portalVertexReps = PathwayManager.get().getEquivalentVertexRepsInPathway(vertexRep, pathway);
	}

	@Override
	public List<PathwayGraph> getPathways() {
		List<PathwayGraph> pathways = new ArrayList<>(1);
		PathwayGraph pathway = getPathway();
		if (pathway != null)
			pathways.add(pathway);
		return pathways;
	}

	@Override
	public Rectangle2D getVertexRepBounds(PathwayVertexRep vertexRep) {
		if (vecTranslation == null || vecScaling == null || vertexRep.getPathway() != getPathway())
			return null;
		int x = pixelGLConverter.getPixelWidthForGLWidth(vecTranslation.x() + vecScaling.x()
				* (pixelGLConverter.getGLWidthForPixelWidth(vertexRep.getCoords().get(0).getFirst())));
		float pathwayHeight = pixelGLConverter.getGLHeightForPixelHeight(pathway.getHeight());
		float pwPositionY = pixelGLConverter.getGLHeightForPixelHeight(vertexRep.getCoords().get(0).getSecond());
		int y = pixelGLConverter.getPixelHeightForGLHeight(viewFrustum.getHeight() - vecTranslation.y()
				+ vecScaling.y() * (pwPositionY - pathwayHeight));
		int width = pixelGLConverter.getPixelWidthForGLWidth(vecScaling.x()
				* pixelGLConverter.getGLWidthForPixelWidth(vertexRep.getWidth()));
		int height = pixelGLConverter.getPixelHeightForGLHeight(vecScaling.y()
				* pixelGLConverter.getGLHeightForPixelHeight(vertexRep.getHeight()));
		return new Rectangle2D.Float(x, y, width, height);
	}

	@Override
	public List<Rectangle2D> getVertexRepsBounds(PathwayVertexRep vertexRep) {
		List<Rectangle2D> pathways = new ArrayList<>(1);
		Rectangle2D location = getVertexRepBounds(vertexRep);
		if (location != null)
			pathways.add(location);
		return null;
	}

	@Override
	public synchronized void addVertexRepBasedContextMenuItem(VertexRepBasedContextMenuItem item) {
		addedContextMenuItems.add(item);
	}

	public void enablePathSelection(boolean isPathSelection) {
		this.isPathSelectionMode = isPathSelection;
		isPathStartSelected = false;
	}

	/**
	 * @param minHeightPixels
	 *            setter, see {@link minHeightPixels}
	 */
	public void setMinHeightPixels(int minHeightPixels) {
		this.minHeightPixels = minHeightPixels;
	}

	/**
	 * @param minWidthPixels
	 *            setter, see {@link minWidthPixels}
	 */
	public void setMinWidthPixels(int minWidthPixels) {
		this.minWidthPixels = minWidthPixels;
	}

	/**
	 * @param highlightVertices
	 *            setter, see {@link highlightVertices}
	 */
	public void setHighlightVertices(boolean highlightVertices) {
		this.highlightVertices = highlightVertices;
	}

	/**
	 * @return the highlightVertices, see {@link #highlightVertices}
	 */
	public boolean isHighlightVertices() {
		return highlightVertices;
	}

	@Override
	public void addVertexRepBasedSelectionEvent(IVertexRepBasedEventFactory eventFactory, PickingMode pickingMode) {
		List<IVertexRepBasedEventFactory> factories = nodeEvents.get(pickingMode);
		if (factories == null) {
			factories = new ArrayList<>();
			nodeEvents.put(pickingMode, factories);
		}
		factories.add(eventFactory);
	}

	private void triggerNodeEvents(PickingMode pickingMode, PathwayVertexRep vertexRep) {
		List<IVertexRepBasedEventFactory> factories = nodeEvents.get(pickingMode);
		if (factories != null) {
			for (IVertexRepBasedEventFactory factory : factories) {
				factory.triggerEvent(vertexRep);
			}
		}
	}

	/**
	 * @param contextPaths
	 *            setter, see {@link contextPaths}
	 */
	public void setContextPaths(List<List<PathwayVertexRep>> contextPaths) {
		this.contextPaths = contextPaths;
		areContextPathsDirty = true;
		setDisplayListDirty();
	}

	/**
	 * @param isDynamicDetail
	 *            setter, see {@link isDynamicDetail}
	 */
	public void setDynamicDetail(boolean isDynamicDetail) {
		this.isDynamicDetail = isDynamicDetail;
	}

	// /**
	// * @return the portalNodes, see {@link #portalNodes}
	// */
	// public Set<PathwayVertexRep> getPortalHighlightNodes() {
	// return portalHighlightNodes;
	// }

	// @ListenTo(restrictExclusiveToEventSpace = true)
	// public void onHighlightPortalNodes(HighlightPortalsEvent event) {
	// if (pathway == null)
	// return;
	// Set<PathwayVertexRep> allPortals = event.getPortals();
	//
	// boolean isPortalsLarger = allPortals.size() > pathway.vertexSet().size();
	// Set<PathwayVertexRep> cloneSet = new HashSet<PathwayVertexRep>(isPortalsLarger ? pathway.vertexSet()
	// : allPortals);
	// cloneSet.retainAll(isPortalsLarger ? allPortals : pathway.vertexSet());
	//
	// if (portalHighlightNodes.size() != cloneSet.size() || !portalHighlightNodes.containsAll(cloneSet)) {
	// portalHighlightNodes = cloneSet;
	// setDisplayListDirty();
	// }
	// }

}
