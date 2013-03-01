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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.datadomain.IDataSupportDefinition;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.selection.EventBasedSelectionManager;
import org.caleydo.core.data.selection.IEventBasedSelectionManagerUser;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.virtualarray.EVAOperation;
import org.caleydo.core.data.virtualarray.delta.VADeltaItem;
import org.caleydo.core.data.virtualarray.delta.VirtualArrayDelta;
import org.caleydo.core.data.virtualarray.events.VADeltaEvent;
import org.caleydo.core.event.EventListenerManager;
import org.caleydo.core.event.EventListenerManagers;
import org.caleydo.core.event.data.DataDomainUpdateEvent;
import org.caleydo.core.event.data.SelectionUpdateEvent;
import org.caleydo.core.event.view.TablePerspectivesChangedEvent;
import org.caleydo.core.id.IDType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.color.ColorManager;
import org.caleydo.core.util.color.mapping.IColorMappingUpdateListener;
import org.caleydo.core.util.color.mapping.UpdateColorMappingEvent;
import org.caleydo.core.util.color.mapping.UpdateColorMappingListener;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.ISingleTablePerspectiveBasedView;
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
import org.caleydo.datadomain.genetic.EGeneIDTypes;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.caleydo.datadomain.genetic.GeneticDataSupportDefinition;
import org.caleydo.datadomain.pathway.IPathwayRepresentation;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.datadomain.pathway.VertexRepBasedContextMenuItem;
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
import org.caleydo.datadomain.pathway.listener.PathwayPathSelectionEvent;
import org.caleydo.datadomain.pathway.listener.ShowPortalNodesEvent;
import org.caleydo.datadomain.pathway.manager.EPathwayDatabaseType;
import org.caleydo.datadomain.pathway.manager.PathwayItemManager;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.datadomain.pathway.toolbar.SelectPathAction;
import org.caleydo.view.pathway.event.ClearMappingEvent;
import org.caleydo.view.pathway.event.EnableGeneMappingEvent;
import org.caleydo.view.pathway.event.SampleMappingModeEvent;
import org.caleydo.view.pathway.event.SampleMappingModeListener;
import org.caleydo.view.pathway.listener.ClearMappingListener;
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


/**
 * Single OpenGL2 pathway view
 *
 * @author Marc Streit
 * @author Alexander Lex
 */

public class GLPathway extends AGLView implements ISingleTablePerspectiveBasedView, IViewCommandHandler,
		IEventBasedSelectionManagerUser, IColorMappingUpdateListener, IPathwayRepresentation {

	public static String VIEW_TYPE = "org.caleydo.view.pathway";

	public static String VIEW_NAME = "Pathway";

	public static final String DEFAULT_PATHWAY_PATH_EVENT_SPACE = "pathwayPath";

	private GeneticDataDomain dataDomain;

	private TablePerspective tablePerspective;

	private PathwayDataDomain pathwayDataDomain;

	private PathwayGraph pathway;

	private PathwayManager pathwayManager;
	private PathwayItemManager pathwayItemManager;

	/**
	 * The maximal number of paths in the pathway that are looked up. The user specifies from which source to which
	 * destination node the search will be triggered.
	 */
	private final static int MAX_PATHS = 10;

	private boolean enablePathwayTexture = true;

	private boolean isPathwayDataDirty = false;

	private GLPathwayAugmentationRenderer gLPathwayAugmentationRenderer;

	private EventBasedSelectionManager vertexSelectionManager;
	private EventBasedSelectionManager sampleSelectionManager;

	/** The mode determing which samples (all or a selection)s should be mapped */
	private ESampleMappingMode sampleMappingMode = ESampleMappingMode.ALL;

	/**
	 * Selection manager for metabolites (compounds). Uses the hash value of compound names as id.
	 */
	private EventBasedSelectionManager metaboliteSelectionManager;

	/**
	 * Own texture manager is needed for each GL2 context, because textures cannot be bound to multiple GL2 contexts.
	 */
	private HashMap<GL, GLPathwayTextureManager> hashGLcontext2TextureManager;

	private Vec3f vecScaling;
	private Vec3f vecTranslation;

	private EnableGeneMappingListener enableGeneMappingListener;
	private EnRoutePathEventListener enRoutePathEventListener;
	private SelectPathModeEventListener selectPathModeEventListener;
	private AddTablePerspectivesListener addTablePerspectivesListener;
	private SampleMappingModeListener sampleMappingModeListener;
	private UpdateColorMappingListener updateColorMappingListener;
	private ClearMappingListener clearMappingListener;
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
	private boolean isBubbleTextureDirty;
	private boolean isPathStartSelected = false;
	private int selectedPathID;
	private PathwayBubbleSet bubbleSet = new PathwayBubbleSet();
	private boolean isControlKeyDown = false;
	private boolean isShiftKeyDown = false;


	/**
	 * Determines whether the paths should be selectable via mouse click.
	 */
	private boolean isPathSelectionMode = false;
	private SelectPathAction selectPathAction = null;

	/**
	 * Event space for events that synchronize a pathway path.
	 */
	private String pathwayPathEventSpace = DEFAULT_PATHWAY_PATH_EVENT_SPACE;

	/**
	 * Context menu items that shall be displayed on right click on a {@link PathwayVertexRep}. Added via
	 * {@link #addVertexRepBasedContextMenuItem(VertexRepBasedContextMenuItem)}.
	 */
	List<VertexRepBasedContextMenuItem> addedContextMenuItems = new ArrayList<>();

	EventListenerManager listeners = EventListenerManagers.wrap(this);

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

		hashGLcontext2TextureManager = new HashMap<GL, GLPathwayTextureManager>();

		vertexSelectionManager = new EventBasedSelectionManager(this, IDType.getIDType(EGeneIDTypes.PATHWAY_VERTEX_REP
				.name()));

		vecScaling = new Vec3f(1, 1, 1);
		vecTranslation = new Vec3f(0, 0, 0);

		registerPickingListeners();
		registerMouseListeners();
		registeKeyListeners();

		// ///////////////////////////////////////////////////
		// / bubble sets
		isBubbleTextureDirty = true;
		selectedPathID = 0;

	}

	private void selectNextPath() {
		if (allPaths == null)
			return;

		if (allPaths.size() == 1)
			selectedPathID = 0;
		else
		{
			selectedPathID++;

			if (selectedPathID > allPaths.size() - 1)
				selectedPathID = 0;

			if (allPaths.size() > 0) {
				selectedPath = allPaths.get(selectedPathID);

				if (selectedPath.getEdgeList().size() > 0 && !isShiftKeyDown) {
					PathwayVertexRep startPrevVertex = selectedPath.getStartVertex();
					PathwayVertexRep endPrevVertex = selectedPath.getEndVertex();
					List<DefaultEdge> edgePrevList = selectedPath.getEdgeList();
					previousSelectedPath = new GraphPathImpl<PathwayVertexRep, DefaultEdge>(pathway, startPrevVertex,
							endPrevVertex, edgePrevList, 0);
				}
			}
		}
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
		gLPathwayAugmentationRenderer = new GLPathwayAugmentationRenderer(viewFrustum, this);
		if (dataDomain != null)
			gLPathwayAugmentationRenderer.enableGeneMapping(true);
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
		// Check if pathway exists or if it's already loaded
		if (pathway == null || !pathwayManager.hasItem(pathway.getID()))
			return;

		initPathwayData(gl);
	}

	protected void registerMouseListeners() {

		parentGLCanvas.addMouseListener(new GLMouseAdapter() {
			@Override
			public void mouseWheelMoved(IMouseEvent e) {
				selectNextPath();
			}
		});
	}

	public void setSelectPathAction(SelectPathAction aSelectPathAction) {
		this.selectPathAction = aSelectPathAction;
	}

	protected void registeKeyListeners() {

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
			}

			@Override
			public void keyReleased(IKeyEvent e) {
				isControlKeyDown = e.isControlDown();
				isShiftKeyDown = e.isShiftDown();
			}
		});
	}

	protected void registerPickingListeners() {

		addTypePickingListener(new APickingListener() {

			@Override
			public void mouseOver(Pick pick) {

				if (detailLevel == EDetailLevel.VERY_LOW) {
					return;
				}

				handlePathwayElementSelection(SelectionType.MOUSE_OVER, pick.getObjectID());
			}

			@Override
			public void clicked(Pick pick) {

				if (detailLevel == EDetailLevel.VERY_LOW) {
					return;
				}

				// We do not handle picking events in pathways for StratomeX
				if (glRemoteRenderingView != null
						&& glRemoteRenderingView.getViewType().equals("org.caleydo.view.brick"))
					return;

				handlePathwayElementSelection(SelectionType.SELECTION, pick.getObjectID());
			}

			@Override
			public void doubleClicked(Pick pick) {

				if (detailLevel == EDetailLevel.VERY_LOW) {
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
						event.setEventSpace(dataDomain.getDataDomainID());
						GeneralManager.get().getEventPublisher().triggerEvent(event);
					}
				} else {

					// // Load pathways
					// for (IGraphItem pathwayVertexGraphItem :
					// tmpVertexGraphItemRep
					// .getAllItemsByProp(EGraphItemProperty.ALIAS_CHILD))
					// {
					//
					// LoadPathwaysByGeneEvent
					// loadPathwaysByGeneEvent =
					// new LoadPathwaysByGeneEvent();
					// loadPathwaysByGeneEvent.setSender(this);
					// loadPathwaysByGeneEvent.setGeneID(pathwayVertexGraphItem.getId());
					// loadPathwaysByGeneEvent.setIdType(EIDType.PATHWAY_VERTEX);
					// generalManager.getEventPublisher().triggerEvent(loadPathwaysByGeneEvent);
					//
					// }
				}

				// same behavior as for single click except that
				// pathways are also loaded
				handlePathwayElementSelection(SelectionType.SELECTION, pick.getObjectID());
			}

			@Override
			public void rightClicked(Pick pick) {

				if (detailLevel == EDetailLevel.VERY_LOW) {
					return;
				}

				PathwayVertexRep vertexRep = pathwayItemManager.getPathwayVertexRep(pick.getObjectID());

				if (vertexRep.getType() == EPathwayVertexType.map) {

					LoadPathwaysByPathwayItem menuItem = new LoadPathwaysByPathwayItem(PathwayManager.get()
							.getPathwayByTitle(vertexRep.getName(), EPathwayDatabaseType.KEGG),
							dataDomain.getDataDomainID());
					contextMenuCreator.addContextMenuItem(menuItem);

				} else if (vertexRep.getType() == EPathwayVertexType.gene) {
					for (PathwayVertex pathwayVertex : vertexRep.getPathwayVertices()) {
						for (Integer davidID : pathwayItemManager.getDavidIdByPathwayVertex(pathwayVertex)) {
							GeneMenuItemContainer contexMenuItemContainer = new GeneMenuItemContainer();
							contexMenuItemContainer.setDataDomain(dataDomain);
							contexMenuItemContainer.setData(pathwayDataDomain.getDavidIDType(), davidID);
							contextMenuCreator.addContextMenuItemContainer(contexMenuItemContainer);
						}
					}
					for (VertexRepBasedContextMenuItem item : addedContextMenuItems) {
						item.setVertexRep(vertexRep);
						contextMenuCreator.addContextMenuItem(item);
					}
				}

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
//				 System.out.println("DENIS_DEBUG:: pickedRed:" + red +
//				 " pickedGreen:" + green + " pickedBlue:" + blue
//				 + " pickedAlpha:" + alpha);
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
						 System.out.println("DENIS_DEBUG:: found usedColor id="+ i);
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
		checkForHits(gl);

		// gl.glColor3f(1, 0, 0);
		// gl.glBegin(GL2.GL_POLYGON);
		// gl.glVertex3f(0, 0, 0);
		// gl.glVertex3f(0, 1, 0);
		// gl.glVertex3f(1, 1, 0);
		// gl.glVertex3f(1, 0, 0);
		// gl.glEnd();

		if (true) {
			calculatePathwayScaling(gl, pathway);
			rebuildPathwayDisplayList(gl, displayListIndex);
			isDisplayListDirty = false;
		}

		if (pathway != null) {
			// TODO: also put this in global DL
			renderPathway(gl, pathway);

		}
	}

	protected void initPathwayData(final GL2 gl) {

		isPathwayDataDirty = false;
		isDisplayListDirty = true;

		selectedPath = null;
		allPaths = null;

		gLPathwayAugmentationRenderer.init(gl, vertexSelectionManager);
		vertexSelectionManager.clearSelections();

		if (dataDomain != null) {
			sampleSelectionManager.clearSelections();
		}

		// Create new pathway manager for GL2 context
		if (!hashGLcontext2TextureManager.containsKey(gl)) {
			hashGLcontext2TextureManager.put(gl, new GLPathwayTextureManager());
		}

		calculatePathwayScaling(gl, pathway);
		pathwayManager.setPathwayVisibilityState(pathway, true);

		// gLPathwayAugmentationRenderer.buildPathwayDisplayList(gl, this,
		// iPathwayID);
	}

	private void renderPathway(final GL2 gl, final PathwayGraph pathway) {

		gl.glPushMatrix();
		gl.glTranslatef(vecTranslation.x(), vecTranslation.y(), vecTranslation.z());
		gl.glScalef(vecScaling.x(), vecScaling.y(), vecScaling.z());
		float textureOffset = 0.0f;// to avoid z fighting
		if (enablePathwayTexture) {
			float fPathwayTransparency = 1.0f;
			hashGLcontext2TextureManager.get(gl).renderPathway(gl, this, pathway, fPathwayTransparency, false);
		}

		float pathwayHeight = pixelGLConverter.getGLHeightForPixelHeight(pathway.getHeight());

		// Pathway texture height is subtracted from Y to align pathways to
		// front level
		gl.glEnable(GL.GL_STENCIL_TEST);
		gl.glClearStencil(0);
		gl.glClear(GL.GL_STENCIL_BUFFER_BIT);
		textureOffset += PathwayRenderStyle.Z_OFFSET;
		gl.glTranslatef(0, pathwayHeight, textureOffset);
		gLPathwayAugmentationRenderer.renderPathway(gl, pathway, false);
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
			hashGLcontext2TextureManager.get(gl).renderPathway(gl, this, pathway, fPathwayTransparency, false);
			gl.glPopName();

			gl.glStencilFunc(GL.GL_GREATER, 1, 0xff);
			gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_KEEP);
			textureOffset -= 2f * PathwayRenderStyle.Z_OFFSET;
			gl.glTranslatef(0.0f, 0.0f, textureOffset);
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


	private void overlayBubbleSets(GL2 gl) {
		if (allPaths == null)
			return;

		if (isBubbleTextureDirty) {
//			//allPaths
			this.bubbleSet.clear();
			this.bubbleSet.setPathwayGraph(pathway);

			//this.bubbleSet.addAllPaths(allPaths);

			this.bubbleSet.addPathSegements(pathSegments);
			this.bubbleSet.addPortals(portalVertexReps);

			//update texture
			this.bubbleSet.getBubbleSetGLRenderer().setSize(pathway.getWidth(), pathway.getHeight());
			this.bubbleSet.getBubbleSetGLRenderer().update(gl,SelectionType.SELECTION.getColor(),selectedPathID);
			isBubbleTextureDirty = false;
		}

		gl.glPushName(generalManager.getViewManager().getPickingManager()
				.getPickingID(uniqueID, EPickingType.PATHWAY_TEXTURE_SELECTION.name(), 0));
		this.bubbleSet.getBubbleSetGLRenderer().render(gl,
					pixelGLConverter.getGLWidthForPixelWidth(pathway.getWidth()),
					pixelGLConverter.getGLHeightForPixelHeight(pathway.getHeight())
					);
		gl.glPopName();
	}

	private void rebuildPathwayDisplayList(final GL2 gl, int displayListIndex) {
		gLPathwayAugmentationRenderer.buildPathwayDisplayList(gl, pathway);
	}

	private void calculatePathwayScaling(final GL2 gl, final PathwayGraph pathway) {

		if (hashGLcontext2TextureManager.get(gl) == null)
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
		boolean pathwayFitsViewFrustum = true;

		if (isRenderedRemote()) {

			if (viewFrustumAspectRatio < pathwayAspectRatio && pathwayWidth > viewFrustumWidth) {

				vecScaling.setX((viewFrustum.getRight() - viewFrustum.getLeft()) / pathwayWidth);
				vecScaling.setY(vecScaling.x());

				vecTranslation.set(
						(viewFrustum.getRight() - viewFrustum.getLeft() - pathwayWidth * vecScaling.x()) / 2.0f,
						(viewFrustum.getTop() - viewFrustum.getBottom() - pathwayHeight * vecScaling.y()) / 2.0f, 0);
				pathwayFitsViewFrustum = false;
			}

			if (viewFrustumAspectRatio >= pathwayAspectRatio && pathwayHeight > viewFrustumHeight) {

				vecScaling.setY((viewFrustum.getTop() - viewFrustum.getBottom()) / pathwayHeight);
				vecScaling.setX(vecScaling.y());

				vecTranslation.set(
						(viewFrustum.getRight() - viewFrustum.getLeft() - pathwayWidth * vecScaling.x()) / 2.0f,
						(viewFrustum.getTop() - viewFrustum.getBottom() - pathwayHeight * vecScaling.y()) / 2.0f, 0);
				pathwayFitsViewFrustum = false;
			}

			if (pathwayFitsViewFrustum) {
				vecScaling.set(1, 1, 1f);

				vecTranslation.set((viewFrustum.getRight() - viewFrustum.getLeft()) / 2.0f - pathwayWidth / 2.0f,
						(viewFrustum.getTop() - viewFrustum.getBottom()) / 2.0f - pathwayHeight / 2.0f, 0);
			}
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

	public void enableGeneMapping(final boolean enableGeneMapping) {
		gLPathwayAugmentationRenderer.enableGeneMapping(enableGeneMapping);
		setDisplayListDirty();
	}

	public void enablePathwayTextures(final boolean bEnablePathwayTexture) {
		gLPathwayAugmentationRenderer.enableEdgeRendering(!bEnablePathwayTexture);
		setDisplayListDirty();

		this.enablePathwayTexture = bEnablePathwayTexture;
	}

	public void enableNeighborhood(final boolean bEnableNeighborhood) {
		setDisplayListDirty();

		gLPathwayAugmentationRenderer.enableNeighborhood(bEnableNeighborhood);
	}

	@Override
	public void broadcastElements(EVAOperation type) {

		if (pathway == null)
			return;

		VirtualArrayDelta delta = new VirtualArrayDelta(tablePerspective.getRecordPerspective().getPerspectiveID(),
				pathwayDataDomain.getDavidIDType());

		for (PathwayVertexRep vertexRep : pathway.vertexSet()) {
			for (Integer davidID : vertexRep.getDavidIDs()) {
				delta.add(VADeltaItem.create(type, davidID));
			}
		}

		VADeltaEvent virtualArrayDeltaEvent = new VADeltaEvent();
		virtualArrayDeltaEvent.setSender(this);
		virtualArrayDeltaEvent.setEventSpace(dataDomain.getDataDomainID());
		virtualArrayDeltaEvent.setVirtualArrayDelta(delta);
		virtualArrayDeltaEvent.setInfo(VIEW_NAME);
		eventPublisher.triggerEvent(virtualArrayDeltaEvent);
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

		enableGeneMappingListener = new EnableGeneMappingListener();
		enableGeneMappingListener.setHandler(this);
		eventPublisher.addListener(EnableGeneMappingEvent.class, enableGeneMappingListener);

		enRoutePathEventListener = new EnRoutePathEventListener();
		enRoutePathEventListener.setExclusiveEventSpace(pathwayPathEventSpace);
		enRoutePathEventListener.setHandler(this);
		eventPublisher.addListener(PathwayPathSelectionEvent.class, enRoutePathEventListener);

		selectPathModeEventListener = new SelectPathModeEventListener();
		selectPathModeEventListener.setExclusiveEventSpace(pathwayPathEventSpace);
		selectPathModeEventListener.setHandler(this);
		eventPublisher.addListener(EnablePathSelectionEvent.class, selectPathModeEventListener);

		addTablePerspectivesListener = new AddTablePerspectivesListener();
		addTablePerspectivesListener.setHandler(this);
		eventPublisher.addListener(AddTablePerspectivesEvent.class, addTablePerspectivesListener);

		sampleMappingModeListener = new SampleMappingModeListener();
		sampleMappingModeListener.setHandler(this);
		eventPublisher.addListener(SampleMappingModeEvent.class, sampleMappingModeListener);

		updateColorMappingListener = new UpdateColorMappingListener();
		updateColorMappingListener.setHandler(this);
		eventPublisher.addListener(UpdateColorMappingEvent.class, updateColorMappingListener);

		clearMappingListener = new ClearMappingListener();
		clearMappingListener.setHandler(this);
		eventPublisher.addListener(ClearMappingEvent.class, clearMappingListener);

		showPortalNodesEventListener = new ShowPortalNodesEventListener();
		showPortalNodesEventListener.setHandler(this);
		showPortalNodesEventListener.setEventSpace(pathwayPathEventSpace);
		listeners.register(ShowPortalNodesEvent.class, showPortalNodesEventListener);

	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();

		listeners.unregisterAll();

		if (enableGeneMappingListener != null) {
			eventPublisher.removeListener(EnableGeneMappingEvent.class, enableGeneMappingListener);
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

		if (clearMappingListener != null) {
			eventPublisher.removeListener(clearMappingListener);
			clearMappingListener = null;
		}

		metaboliteSelectionManager.unregisterEventListeners();

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
		serializedForm.setDataDomainID(dataDomain != null ? dataDomain.getDataDomainID() : null);

		System.out.println("Serializing Pathway: review me!");

		return serializedForm;
	}

	public PathwayDataDomain getPathwayDataDomain() {
		return pathwayDataDomain;
	}

	@Override
	public int getMinPixelHeight() {
		// if (pathway == null)
		return 120;
		// return pathway.getHeight();
	}

	@Override
	public int getMinPixelWidth() {
		// if (pathway == null)
		return 120;
		// return pathway.getWidth();
	}

	@Override
	public boolean isDataView() {
		return true;
	}

	@Override
	public void setDataDomain(ATableBasedDataDomain dataDomain) {
		if (dataDomain == null) {
			if (gLPathwayAugmentationRenderer != null) {
				gLPathwayAugmentationRenderer.enableGeneMapping(false);
			}
			return;
		}
		if (!(dataDomain instanceof GeneticDataDomain))
			throw new IllegalArgumentException("Pathway view can handle only genetic data domain, tried to set: "
					+ dataDomain);

		this.dataDomain = (GeneticDataDomain) dataDomain;

		if (gLPathwayAugmentationRenderer != null) {
			gLPathwayAugmentationRenderer.enableGeneMapping(true);
		}
		// only make a new sample selection manager if necessary due to
		// different id category or because it wasn't initalized so far
		if (sampleSelectionManager == null
				|| !sampleSelectionManager.getIDType().getIDCategory()
						.equals(this.dataDomain.getSampleIDType().getIDCategory())) {
			sampleSelectionManager = new EventBasedSelectionManager(this,
					((GeneticDataDomain) dataDomain).getSampleIDType());
		}
		setDisplayListDirty();

	}

	@Override
	public void setTablePerspective(TablePerspective tablePerspective) {
		this.tablePerspective = tablePerspective;
		if (tablePerspective instanceof PathwayTablePerspective)
			pathway = ((PathwayTablePerspective) tablePerspective).getPathway();

		setDisplayListDirty();
		if (tablePerspective != null) {
			DataDomainUpdateEvent event = new DataDomainUpdateEvent(tablePerspective.getDataDomain());
			eventPublisher.triggerEvent(event);
		} else {
			dataDomain = null;
		}

		TablePerspectivesChangedEvent tbEvent = new TablePerspectivesChangedEvent(this);
		eventPublisher.triggerEvent(tbEvent);

	}

	@Override
	public GeneticDataDomain getDataDomain() {
		return dataDomain;
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

			if (isControlKeyDown || this.isShiftKeyDown) {
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
			if (allPaths == null)
				allPaths = new ArrayList<GraphPath<PathwayVertexRep, DefaultEdge>>();
			else
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
		GraphPath<PathwayVertexRep, DefaultEdge> path = new GraphPathImpl<PathwayVertexRep, DefaultEdge>(pathway,
				vertexRep, vertexRep, new ArrayList<DefaultEdge>(), 0);
		if (allPaths == null)
			allPaths = new ArrayList<GraphPath<PathwayVertexRep, DefaultEdge>>();
		else
			allPaths.clear();
		allPaths.add(path);
		selectedPath = path;
		selectedPathID = 0;
	}

	private void selectPath(PathwayVertexRep vertexRep, SelectionType selectionType) {
		if (vertexRep == null)
			return;
		if (!isPathStartSelected) {// ////////////////////////////////
			if (isControlKeyDown) {// shrink previous selected path
				shrinkSelectedPath(vertexRep);
				if (selectionType == SelectionType.SELECTION) {// click on
					previousSelectedPath = selectedPath;
				}
			}
			// //////////////////////////////
			if (isShiftKeyDown) {// extend previous selected path
				extendSelectedPath(vertexRep);
				if (selectionType == SelectionType.SELECTION) {// click on
					previousSelectedPath = selectedPath;
				}
			}
			if (!isShiftKeyDown && !isControlKeyDown && vertexRep != null) {
				// no interaction with the previous selected path
				// select vertexRep as startPoint and switch to
				// end_point_selection_mode

				if (selectionType == SelectionType.SELECTION) {

					boolean isPortalNode = false;
					for (PathwayVertexRep portal : portalVertexReps) {
						if (vertexRep == portal) {
							isPortalNode = true;
						}
					}
					if (!isPortalNode) {
						pathSegments.clear();
					}

					generateSingleNodePath(vertexRep);
					pathSegments.add(new PathwayPath(selectedPath));
					isPathStartSelected = true;
				}
			}
		} else {// //////// select end node /////////////////////////
			if (pathStartVertexRep == null)
				return;
			if(!pathway.containsVertex(pathStartVertexRep)){	
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
						if (allPaths.size() <= selectedPathID)
							selectedPathID = 0;
						selectedPath = allPaths.get(selectedPathID);
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

		triggerPathUpdate();
		isBubbleTextureDirty = true;
	}

	public void handlePathwayElementSelection(SelectionType selectionType, int externalID) {
		setDisplayListDirty();
		if (vertexSelectionManager.getElements(SelectionType.SELECTION).size() == 1) {
			pathStartVertexRep = pathwayItemManager.getPathwayVertexRep((Integer) vertexSelectionManager.getElements(
					SelectionType.SELECTION).toArray()[0]);
		}

		vertexSelectionManager.clearSelection(selectionType);
		if (metaboliteSelectionManager.getNumberOfElements(selectionType) > 0) {
			metaboliteSelectionManager.clearSelection(selectionType);
			metaboliteSelectionManager.triggerSelectionUpdateEvent();
		}

		PathwayVertexRep vertexRep = pathwayItemManager.getPathwayVertexRep(externalID);

		if (vertexRep.getType() == EPathwayVertexType.compound) {
			metaboliteSelectionManager.addToType(selectionType, vertexRep.getName().hashCode());
			metaboliteSelectionManager.triggerSelectionUpdateEvent();
		}

		if (isPathSelectionMode) {
			selectPath(vertexRep, selectionType);

			// TODO: make sure that this is the last vertex of the last path segment
			if (selectedPath != null && vertexRep == selectedPath.getEndVertex()
					&& selectedPath.getEdgeList().size() > 0) {
				// ShowPortalNodesEvent e = new ShowPortalNodesEvent(vertexRep);
				// e.setSender(this);
				// e.setEventSpace(pathwayPathEventSpace);
				// eventPublisher.triggerEvent(e);
				// the event will not be sent back to this pathway object, so highlight must be triggered here
				// highlightPortalNodes(vertexRep);
			}
		}

		// Add new vertex to internal selection manager
		vertexSelectionManager.addToType(selectionType, vertexRep.getID());

		SelectionDelta selectionDelta = vertexSelectionManager.getDelta();

		SelectionUpdateEvent event = new SelectionUpdateEvent();
		event.setSender(this);
		event.setSelectionDelta(selectionDelta);
		eventPublisher.triggerEvent(event);
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
		if (pathSegmentsBroadcasted == null)
			return;
		pathSegments = pathSegmentsBroadcasted;
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

		isBubbleTextureDirty = true;
		setDisplayListDirty();
	}

	/**
	 * @return
	 */
	public IPickingListener getPathwayElementPickingListener() {

		return pathwayElementPickingListener;
	}

	@Override
	public void notifyOfSelectionChange(EventBasedSelectionManager selectionManager) {
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
	public TablePerspective getTablePerspective() {
		return tablePerspective;
	}

	@Override
	public List<TablePerspective> getTablePerspectives() {
		ArrayList<TablePerspective> tablePerspectives = new ArrayList<TablePerspective>(1);
		tablePerspectives.add(tablePerspective);
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
	public Set<IDataDomain> getDataDomains() {
		Set<IDataDomain> dataDomains = new HashSet<IDataDomain>(1);
		dataDomains.add(dataDomain);
		return dataDomains;
	}

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

}