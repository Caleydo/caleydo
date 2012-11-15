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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.PixelGrabber;
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
import org.caleydo.core.data.virtualarray.delta.RecordVADelta;
import org.caleydo.core.data.virtualarray.delta.VADeltaItem;
import org.caleydo.core.data.virtualarray.events.RecordVADeltaEvent;
import org.caleydo.core.event.data.DataDomainUpdateEvent;
import org.caleydo.core.event.data.SelectionUpdateEvent;
import org.caleydo.core.event.view.SwitchDataRepresentationEvent;
import org.caleydo.core.event.view.TablePerspectivesChangedEvent;
import org.caleydo.core.event.view.pathway.LoadPathwayEvent;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.object.ManagedObjectType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.color.ColorManager;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.util.mapping.color.IColorMappingUpdateListener;
import org.caleydo.core.util.mapping.color.UpdateColorMappingEvent;
import org.caleydo.core.util.mapping.color.UpdateColorMappingListener;
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
import org.caleydo.core.view.vislink.ConnectedElementRepresentationManager;
import org.caleydo.datadomain.genetic.EGeneIDTypes;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.caleydo.datadomain.genetic.GeneticDataSupportDefinition;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.datadomain.pathway.contextmenu.container.GeneMenuItemContainer;
import org.caleydo.datadomain.pathway.contextmenu.item.LoadPathwaysByPathwayItem;
import org.caleydo.datadomain.pathway.data.PathwayTablePerspective;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.PathwayPath;
import org.caleydo.datadomain.pathway.graph.item.vertex.EPathwayVertexType;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertex;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.manager.EPathwayDatabaseType;
import org.caleydo.datadomain.pathway.manager.PathwayItemManager;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.view.pathway.event.ClearMappingEvent;
import org.caleydo.view.pathway.event.ClearPathEvent;
import org.caleydo.view.pathway.event.EnRoutePathEvent;
import org.caleydo.view.pathway.event.EnableGeneMappingEvent;
import org.caleydo.view.pathway.event.SampleMappingModeEvent;
import org.caleydo.view.pathway.event.SampleMappingModeListener;
import org.caleydo.view.pathway.event.SelectPathModeEvent;
import org.caleydo.view.pathway.listener.ClearMappingListener;
import org.caleydo.view.pathway.listener.ClearPathEventListener;
import org.caleydo.view.pathway.listener.EnRoutePathEventListener;
import org.caleydo.view.pathway.listener.EnableGeneMappingListener;
import org.caleydo.view.pathway.listener.SelectPathModeEventListener;
import org.caleydo.view.pathway.listener.SwitchDataRepresentationListener;
import org.caleydo.view.pathway.toolbar.actions.SelectPathAction;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Composite;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.KShortestPaths;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.GraphPathImpl;

import setvis.SetOutline;
import setvis.bubbleset.BubbleSet;
import setvis.gui.CanvasComponent;
import setvis.shape.AbstractShapeGenerator;
import setvis.shape.BSplineShapeGenerator;

import com.jogamp.opengl.util.awt.TextureRenderer;
import com.jogamp.opengl.util.texture.Texture;

/**
 * Single OpenGL2 pathway view
 *
 * @author Marc Streit
 * @author Alexander Lex
 */

public class GLPathway
	extends AGLView
	implements ISingleTablePerspectiveBasedView, IViewCommandHandler, IEventBasedSelectionManagerUser,
	IColorMappingUpdateListener {

	public static String VIEW_TYPE = "org.caleydo.view.pathway";

	public static String VIEW_NAME = "Pathway";

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

	private ConnectedElementRepresentationManager connectedElementRepresentationManager;

	/**
	 * Own texture manager is needed for each GL2 context, because textures cannot be bound to multiple GL2 contexts.
	 */
	private HashMap<GL, GLPathwayTextureManager> hashGLcontext2TextureManager;

	private Vec3f vecScaling;
	private Vec3f vecTranslation;

	private EnableGeneMappingListener enableGeneMappingListener;
	private SwitchDataRepresentationListener switchDataRepresentationListener;
	private EnRoutePathEventListener enRoutePathEventListener;
	private SelectPathModeEventListener selectPathModeEventListener;
	private ClearPathEventListener clearPathEventListener;
	private AddTablePerspectivesListener addTablePerspectivesListener;
	private SampleMappingModeListener sampleMappingModeListener;
	private UpdateColorMappingListener updateColorMappingListener;
	private ClearMappingListener clearMappingListener;

	private IPickingListener pathwayElementPickingListener;

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

	private TextureRenderer texRenderer;
	private SetOutline setOutline;
	private AbstractShapeGenerator shaper;
	private CanvasComponent bubblesetCanvas;
	private Texture bubbleSetsTexture;
	private boolean isBubbleTextureDirty;
	private boolean isPathStartSelected = false;

	private boolean isControlKeyDown = false;
	private boolean isShiftKeyDown = false;
	private int selectedPathID;

	/**
	 * Determines whether the paths should be selectable via mouse click.
	 */
	private boolean isPathSelectionMode = false;
	private SelectPathAction selectPathAction = null;

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

		connectedElementRepresentationManager = generalManager.getViewManager()
				.getConnectedElementRepresentationManager();

		vertexSelectionManager = new EventBasedSelectionManager(this, IDType.getIDType(EGeneIDTypes.PATHWAY_VERTEX_REP
				.name()));

		vecScaling = new Vec3f(1, 1, 1);
		vecTranslation = new Vec3f(0, 0, 0);

		registerPickingListeners();
		registerMouseListeners();
		registeKeyListeners();

		// ///////////////////////////////////////////////////
		// / bubble sets
		setOutline = new BubbleSet(100, 20, 3, 10.0, 7.0, 0.5, 2.5, 15.0, 8);
		((BubbleSet) setOutline).useVirtualEdges(false);
		shaper = new BSplineShapeGenerator(setOutline);
		bubblesetCanvas = new CanvasComponent(shaper);
		bubblesetCanvas.setDefaultView();
		isBubbleTextureDirty = true;
		selectedPathID = 0;
	}

	private void selectNextPath() {
		if (allPaths == null)
			return;

		if (allPaths.size() == 1)
			selectedPathID = 0;
		else {
			selectedPathID++;
			if (selectedPathID > allPaths.size() - 1)
				selectedPathID = 0;

			selectedPath = allPaths.get(selectedPathID);

			if (selectedPath.getEdgeList().size() > 0 && !isShiftKeyDown) {
				PathwayVertexRep startPrevVertex = selectedPath.getStartVertex();
				PathwayVertexRep endPrevVertex = selectedPath.getEndVertex();
				List<DefaultEdge> edgePrevList = selectedPath.getEdgeList();
				previousSelectedPath = new GraphPathImpl<PathwayVertexRep, DefaultEdge>(pathway, startPrevVertex,
						endPrevVertex, edgePrevList, 0);
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

		// we will adapt the dimensions in each frame
		texRenderer = new TextureRenderer(1280, 768, true);

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
					setPathSelectionMode(!isPathSelectionMode);
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

				PathwayVertexRep vertexRep = pathwayItemManager.getPathwayVertexRep(pick
						.getObjectID());

				// Load embedded pathway
				if (vertexRep.getType() == EPathwayVertexType.map) {
					PathwayGraph pathway = PathwayManager.get().getPathwayByTitle(vertexRep.getName(),
							EPathwayDatabaseType.KEGG);

					if (pathway != null) {
						LoadPathwayEvent event = new LoadPathwayEvent();
						event.setSender(this);
						event.setPathwayID(pathway.getID());
						event.setDataDomainID(dataDomain.getDataDomainID());
						GeneralManager.get().getEventPublisher().triggerEvent(event);
					}
				}
				else {

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

				PathwayVertexRep vertexRep = pathwayItemManager.getPathwayVertexRep(pick
						.getObjectID());

				if (vertexRep.getType() == EPathwayVertexType.map) {

					LoadPathwaysByPathwayItem menuItem = new LoadPathwaysByPathwayItem(PathwayManager.get()
							.getPathwayByTitle(vertexRep.getName(), EPathwayDatabaseType.KEGG),
							dataDomain.getDataDomainID());
					contextMenuCreator.addContextMenuItem(menuItem);

				}
				else if (vertexRep.getType() == EPathwayVertexType.gene) {
					for (PathwayVertex pathwayVertex : vertexRep.getPathwayVertices()) {
						for (Integer davidID : pathwayItemManager.getDavidIdByPathwayVertex(pathwayVertex)) {
							GeneMenuItemContainer contexMenuItemContainer = new GeneMenuItemContainer();
							contexMenuItemContainer.setDataDomain(dataDomain);
							contexMenuItemContainer.setData(pathwayDataDomain.getDavidIDType(), davidID);
							contextMenuCreator.addContextMenuItemContainer(contexMenuItemContainer);
						}
					}
				}

				handlePathwayElementSelection(SelectionType.SELECTION, pick.getObjectID());
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
				int[] pixels = new int[1];
				Image img = texRenderer.getImage();
				PixelGrabber pxlGrabber = new PixelGrabber(img, pickX, pickY, 1, 1, pixels, 0, 1);
				try {
					pxlGrabber.grabPixels();
				}
				catch (InterruptedException e) {
					System.err.println("interrupted waiting for pixels!");
					return;
				}
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
						// System.out.println("DENIS_DEBUG:: found usedColor id="
						// + i);
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

			};
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

		display(gl);

	}

	@Override
	public void display(final GL2 gl) {
		checkForHits(gl);

//		gl.glColor3f(1, 0, 0);
//		gl.glBegin(GL2.GL_POLYGON);
//		gl.glVertex3f(0, 0, 0);
//		gl.glVertex3f(0, 1, 0);
//		gl.glVertex3f(1, 1, 0);
//		gl.glVertex3f(1, 0, 0);
//		gl.glEnd();

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
		gl.glEnable(GL2.GL_STENCIL_TEST);
		gl.glClearStencil(0);
		gl.glClear(GL2.GL_STENCIL_BUFFER_BIT);
		textureOffset += PathwayRenderStyle.Z_OFFSET;
		gl.glTranslatef(0, pathwayHeight, textureOffset);
		gLPathwayAugmentationRenderer.renderPathway(gl, pathway, false);
		gl.glTranslatef(0, -pathwayHeight, -textureOffset);

		if (enablePathwayTexture) {
			float fPathwayTransparency = 1.0f;
			textureOffset += PathwayRenderStyle.Z_OFFSET;
			gl.glTranslatef(0.0f, 0.0f, textureOffset);

			gl.glEnable(GL2.GL_STENCIL_TEST);
			gl.glDisable(GL2.GL_DEPTH_TEST);
			//gl.glStencilFunc(GL2.GL_EQUAL, 0, 1);
			gl.glStencilFunc(GL2.GL_GREATER, 2, 0xff);
			gl.glStencilOp(GL2.GL_KEEP, GL2.GL_KEEP, GL2.GL_KEEP);
			gl.glPushName(generalManager.getViewManager().getPickingManager()
					.getPickingID(uniqueID, EPickingType.PATHWAY_TEXTURE_SELECTION.name(), 0));
			hashGLcontext2TextureManager.get(gl).renderPathway(gl, this, pathway, fPathwayTransparency, false);
			gl.glPopName();


			gl.glStencilFunc(GL2.GL_GREATER, 1, 0xff);
			gl.glStencilOp(GL2.GL_KEEP, GL2.GL_KEEP, GL2.GL_KEEP);
			textureOffset -= 2f * PathwayRenderStyle.Z_OFFSET;
			gl.glTranslatef(0.0f, 0.0f, textureOffset);
			overlayBubbleSets(gl);

			gl.glEnable(GL2.GL_DEPTH_TEST);
			gl.glDisable(GL2.GL_STENCIL_TEST);
		}
		// //
		gl.glScalef(1 / vecScaling.x(), 1 / vecScaling.y(), 1 / vecScaling.z());
		gl.glTranslatef(-vecTranslation.x(), -vecTranslation.y(), -vecTranslation.z());

		gl.glDisable(GL2.GL_STENCIL_TEST);
		gl.glPopMatrix();
	}

	private void updateBubbleSetsTexture(GL2 gl) {
		int groupID = bubblesetCanvas.getGroupCount() - 1;
		while (bubblesetCanvas.getGroupCount() > 0) {
			bubblesetCanvas.setCurrentGroup(groupID);
			bubblesetCanvas.removeCurrentGroup();
			groupID--;
		}

		// updateSingleBubbleSet(gl, selectedPath);
		// updateSingleBubbleSet(gl, mouseOverPath);

		int bbGroupID = -1;
		HashSet<PathwayVertexRep> visitedNodes = new HashSet<PathwayVertexRep>();
		for (GraphPath<PathwayVertexRep, DefaultEdge> path : allPaths) {
			// updateSingleBubbleSet(gl, path, bbGroupID,visitedNodes);
			if (path == null)
				return;

			double bbItemW = 10;
			double bbItemH = 10;

			// group0

			gl.glPushName(generalManager.getViewManager().getPickingManager()
					.getPickingID(uniqueID, EPickingType.PATHWAY_PATH_SELECTION.name(), allPaths.indexOf(path)));
			float[] colorValues = new float[3];
			Integer outlineThickness;
			bbGroupID++;
			if (path == selectedPath) {
				colorValues = SelectionType.SELECTION.getColor();
				outlineThickness = 3;
				// bubble sets do not allow to delete
				bubblesetCanvas.addGroup(new Color(colorValues[0], colorValues[1], colorValues[2]), outlineThickness,
						true);
			}
			else {
				List<org.caleydo.core.util.color.Color> colorTable = (ColorManager.get())
						.getColorList("qualitativeColors");
				int colorID;
				// avoid the last two colors because they are close to orange
				// (the selection color)
				if (bbGroupID < colorTable.size() - 2)
					colorID = bbGroupID;
				else
					colorID = colorTable.size() - 1;
				org.caleydo.core.util.color.Color c = colorTable.get(colorID);
				outlineThickness = 1;
				// bubble sets do not allow to delete
				bubblesetCanvas.addGroup(new Color(c.r, c.g, c.b), outlineThickness, true);
			}

			if (path.getEndVertex() == path.getStartVertex()) {
				PathwayVertexRep sourceVertexRep = path.getEndVertex();
				bbItemW = sourceVertexRep.getWidth();
				bbItemH = sourceVertexRep.getHeight();
				double posX = sourceVertexRep.getLowerLeftCornerX();
				double posY = sourceVertexRep.getLowerLeftCornerY();
				bubblesetCanvas.addItem(bbGroupID, posX, posY, bbItemW, bbItemH);
			}
			else {
				for (DefaultEdge edge : path.getEdgeList()) {
					PathwayVertexRep sourceVertexRep = pathway.getEdgeSource(edge);
					PathwayVertexRep targetVertexRep = pathway.getEdgeTarget(edge);

					bbItemW = sourceVertexRep.getWidth();
					bbItemH = sourceVertexRep.getHeight();
					double posX = sourceVertexRep.getLowerLeftCornerX();
					double posY = sourceVertexRep.getLowerLeftCornerY();
					double tX = targetVertexRep.getLowerLeftCornerX();
					double tY = targetVertexRep.getLowerLeftCornerY();

					bubblesetCanvas.addItem(bbGroupID, posX, posY, bbItemW, bbItemH);
					//
					bubblesetCanvas.addItem(bbGroupID, tX, tY, bbItemW, bbItemH);
					//
					bubblesetCanvas.addEdge(bbGroupID, posX, posY, tX, tY);
					visitedNodes.add(sourceVertexRep);
				}
			}

			// DefaultEdge lastEdge =
			// path.getEdgeList().get(path.getEdgeList().size()-1);
			// if (lastEdge != null) {
			// PathwayVertexRep targetVertexRep =
			// pathway.getEdgeTarget(lastEdge);
			// double posX = targetVertexRep.getLowerLeftCornerX();
			// double posY = targetVertexRep.getLowerLeftCornerY();
			// bbItemW = targetVertexRep.getWidth();
			// bbItemH = targetVertexRep.getHeight();
			// bubblesetCanvas.addItem(bbGroupID, posX, posY, bbItemW, bbItemH);
			// visitedNodes.add(targetVertexRep);
			// }
			gl.glPopName();
			//

		}
		// /////////////////////
		//
		// HashSet<PathwayVertexRep> otherNodes = new HashSet();
		// HashSet<Rectangle2D> otherRects = new HashSet();
		// Set<PathwayVertexRep> vSet = pathway.vertexSet();
		// double bbItemW = 10;
		// double bbItemH = 10;
		// Iterator iter=vSet.iterator();
		// while(iter.hasNext()){
		// PathwayVertexRep pathwayVertexRep=(PathwayVertexRep)iter.next();
		// if(!visitedNodes.contains(pathwayVertexRep)){
		// otherNodes.add(pathwayVertexRep);
		// double posX = pathwayVertexRep.getLowerLeftCornerX();
		// double posY = pathwayVertexRep.getLowerLeftCornerY();
		// bbItemW=pathwayVertexRep.getWidth();
		// bbItemH=pathwayVertexRep.getHeight();
		// final double x = bubblesetCanvas.getXForScreen(posX);
		// final double y = bubblesetCanvas.getYForScreen(posY);
		// otherRects.add(new Rectangle2D.Double(x - bbItemW * 0.5, y - bbItemH
		// * 0.5,bbItemW, bbItemH));
		// }
		// }
		// bubblesetCanvas.resolveEdgeIntersections(otherRects);
		// // add all other vertices
		// bubblesetCanvas.addGroup(new Color(0f,0f,0f),1, false); // bubble
		// sets do not allow to delete
		// bbGroupID++;
		// Iterator otherNodesIter=otherNodes.iterator();
		// while(otherNodesIter.hasNext()){
		// PathwayVertexRep
		// pathwayVertexRep=(PathwayVertexRep)otherNodesIter.next();
		// double posX = pathwayVertexRep.getLowerLeftCornerX();
		// double posY = pathwayVertexRep.getLowerLeftCornerY();
		// bbItemW=pathwayVertexRep.getWidth();
		// bbItemH=pathwayVertexRep.getHeight();
		// bubblesetCanvas.addItem(bbGroupID, posX, posY, bbItemW, bbItemH);
		// }
		//
		// /////////////////////
		if (allPaths.size() <= selectedPathID)
			selectedPathID = 0;
		bubblesetCanvas.setSelection(selectedPathID); // the selected set will
														// be rendered on top of
														// all others
		texRenderer.setSize(pathway.getWidth(), pathway.getHeight());
		Graphics2D g2d = texRenderer.createGraphics();
		bubblesetCanvas.paint(g2d);

		g2d.dispose();
	}

	private void overlayBubbleSets(GL2 gl) {

		
		if (allPaths == null)
			return;

		texRenderer.setColor(1.0f, 1.0f, 1.0f, 0.75f);
		if (isBubbleTextureDirty) {
			updateBubbleSetsTexture(gl);
			isBubbleTextureDirty = false;
		}
		bubbleSetsTexture = texRenderer.getTexture();

		float textureWidth = pixelGLConverter.getGLWidthForPixelWidth(pathway.getWidth());
		float textureHeight = pixelGLConverter.getGLHeightForPixelHeight(pathway.getHeight());

		gl.glPushName(generalManager.getViewManager().getPickingManager()
				.getPickingID(uniqueID, EPickingType.PATHWAY_TEXTURE_SELECTION.name(), 0));

		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE_MINUS_SRC_ALPHA);
			
		
		bubbleSetsTexture.enable(gl);
		bubbleSetsTexture.bind(gl);
		gl.glBegin(GL2.GL_QUADS);

		gl.glTexCoord2f(0, 1);
		gl.glVertex3f(0.0f, 0.0f, 0.0f);
		gl.glTexCoord2f(1, 1);
		gl.glVertex3f(textureWidth, 0.0f, 0.0f);
		gl.glTexCoord2f(1, 0);
		gl.glVertex3f(textureWidth, textureHeight, 0.0f);
		gl.glTexCoord2f(0, 0);
		gl.glVertex3f(0.0f, textureHeight, 0.0f);

		gl.glEnd();
		bubbleSetsTexture.disable(gl);
		gl.glPopName();

	}

	// private void renderPaths(GL2 gl) {
	//
	// if (allPaths == null)
	// return;
	//
	// for (GraphPath<PathwayVertexRep, DefaultEdge> path : allPaths)
	// renderSinglePath(gl, path);
	// }
	//
	// private void renderSinglePath(GL2 gl, GraphPath<PathwayVertexRep,
	// DefaultEdge> path) {
	//
	// if (path == null)
	// return;
	//
	// gl.glLineWidth(5);
	//
	// gl.glPushName(generalManager
	// .getViewManager()
	// .getPickingManager()
	// .getPickingID(uniqueID, EPickingType.PATHWAY_PATH_SELECTION.name(),
	// allPaths.indexOf(path)));
	//
	// if (path == selectedPath)
	// gl.glColor4fv(SelectionType.SELECTION.getColor(), 0);
	// else
	// gl.glColor4fv(PathwayRenderStyle.PATH_COLOR, 0);
	//
	// for (DefaultEdge edge : path.getEdgeList()) {
	//
	// PathwayVertexRep sourceVertexRep = pathway.getEdgeSource(edge);
	// PathwayVertexRep targetVertexRep = pathway.getEdgeTarget(edge);
	//
	// // gl.glBegin(GL.GL_LINES);
	// // gl.glVertex3f(sourceVertexRep.getCenterX() *
	// // PathwayRenderStyle.SCALING_FACTOR_X,
	// // -sourceVertexRep.getCenterY() *
	// // PathwayRenderStyle.SCALING_FACTOR_Y, 0.1f);
	// // gl.glVertex3f(targetVertexRep.getCenterX() *
	// // PathwayRenderStyle.SCALING_FACTOR_X,
	// // -targetVertexRep.getCenterY() *
	// // PathwayRenderStyle.SCALING_FACTOR_Y, 0.1f);
	// // gl.glEnd();
	// }
	//
	// gl.glPopName();
	// }

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

	private void createConnectionLines(SelectionType selectionType, int iConnectionID) {
		// // check in preferences if we should draw connection lines for mouse
		// // over
		// if
		// (!connectedElementRepresentationManager.isSelectionTypeRenderedWithVisuaLinks(selectionType))
		// return;
		// // check for selections
		// if
		// (!generalManager.getPreferenceStore().getBoolean(PreferenceConstants.VISUAL_LINKS_FOR_SELECTIONS)
		// && selectionType == SelectionType.SELECTION)
		// return;
		//
		// PathwayVertexRep tmpPathwayVertexRep;
		// int pathwayHeight = pathway.getHeight();
		//
		// int viewID = uniqueID;
		// // If rendered remote (hierarchical heat map) - use the remote view
		// ID
		// // if (glRemoteRenderingView != null && glRemoteRenderingView
		// instanceof
		// // AGLViewBrowser)
		// // viewID = glRemoteRenderingView.getID();
		//
		// for (int vertexRepID :
		// vertexSelectionManager.getElements(selectionType)) {
		// tmpPathwayVertexRep =
		// pathwayItemManager.getPathwayVertexRep(vertexRepID);
		//
		// ElementConnectionInformation elementRep = new
		// ElementConnectionInformation(dataDomain.getRecordIDType(),
		// viewID, tmpPathwayVertexRep.getLowerLeftCornerX() *
		// PathwayRenderStyle.SCALING_FACTOR_X
		// * vecScaling.x() + vecTranslation.x(),
		// (pathwayHeight - tmpPathwayVertexRep.getLowerLeftCornerY()) *
		// PathwayRenderStyle.SCALING_FACTOR_Y
		// * vecScaling.y() + vecTranslation.y(), 0);
		//
		// // for (Integer iConnectionID : selectionManager
		// // .getConnectionForElementID(iVertexRepID))
		// // {
		// connectedElementRepresentationManager.addSelection(iConnectionID,
		// elementRep, selectionType);
		// // }
		// }
		// // }
	}

	@Override
	public void broadcastElements(EVAOperation type) {

		if (pathway == null)
			return;

		RecordVADelta delta = new RecordVADelta(tablePerspective.getRecordPerspective().getPerspectiveID(),
				pathwayDataDomain.getDavidIDType());

		for (PathwayVertexRep vertexRep : pathway.vertexSet()) {
			for (Integer davidID : vertexRep.getDavidIDs()) {
				delta.add(VADeltaItem.create(type, davidID));
			}
		}

		RecordVADeltaEvent virtualArrayDeltaEvent = new RecordVADeltaEvent();
		virtualArrayDeltaEvent.setSender(this);
		virtualArrayDeltaEvent.setDataDomainID(dataDomain.getDataDomainID());
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
		connectedElementRepresentationManager.clear(dataDomain.getRecordIDType());
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

		switchDataRepresentationListener = new SwitchDataRepresentationListener();
		switchDataRepresentationListener.setHandler(this);
		eventPublisher.addListener(SwitchDataRepresentationEvent.class, switchDataRepresentationListener);

		enRoutePathEventListener = new EnRoutePathEventListener();
		enRoutePathEventListener.setHandler(this);
		eventPublisher.addListener(EnRoutePathEvent.class, enRoutePathEventListener);

		selectPathModeEventListener = new SelectPathModeEventListener();
		selectPathModeEventListener.setHandler(this);
		eventPublisher.addListener(SelectPathModeEvent.class, selectPathModeEventListener);

		clearPathEventListener = new ClearPathEventListener();
		clearPathEventListener.setHandler(this);
		eventPublisher.addListener(ClearPathEvent.class, clearPathEventListener);

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

	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();

		if (enableGeneMappingListener != null) {
			eventPublisher.removeListener(EnableGeneMappingEvent.class, enableGeneMappingListener);
			enableGeneMappingListener = null;
		}

		if (switchDataRepresentationListener != null) {
			eventPublisher.removeListener(switchDataRepresentationListener);
			switchDataRepresentationListener = null;
		}

		if (enRoutePathEventListener != null) {
			eventPublisher.removeListener(enRoutePathEventListener);
			enRoutePathEventListener = null;
		}

		if (selectPathModeEventListener != null) {
			eventPublisher.removeListener(selectPathModeEventListener);
			selectPathModeEventListener = null;
		}

		if (clearPathEventListener != null) {
			eventPublisher.removeListener(clearPathEventListener);
			clearPathEventListener = null;
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

	public void switchDataRepresentation() {
		gLPathwayAugmentationRenderer.switchDataRepresentation();
		setDisplayListDirty();
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
		}
		else {
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
			int connectionID = generalManager.getIDCreator().createID(ManagedObjectType.CONNECTION);
			vertexSelectionManager.addConnectionID(connectionID, selectedPath.getEndVertex().getID());
			connectedElementRepresentationManager.clear(vertexSelectionManager.getIDType(), SelectionType.SELECTION);
			createConnectionLines(SelectionType.SELECTION, connectionID);
			// SelectionDelta selectionDelta =
			// createExternalSelectionDelta(vertexSelectionManager.getDelta());
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

	private void shrinkSelectedPath(PathwayVertexRep vertexRep) {
		if (previousSelectedPath == null)
			return;
		List<DefaultEdge> edgeListPrev = previousSelectedPath.getEdgeList();
		List<DefaultEdge> edgeListNew = new ArrayList<DefaultEdge>();
		PathwayVertexRep startVertex = previousSelectedPath.getStartVertex();
		PathwayVertexRep endVertex = previousSelectedPath.getEndVertex();
		if (vertexRep == startVertex) {
			generateSingleNodePath(vertexRep);
		}
		else {
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
		}
		else {
			allPaths = null;
		}
		if (allPaths == null) {
			allPaths = new ArrayList<GraphPath<PathwayVertexRep, DefaultEdge>>();
			allPaths.add(previousSelectedPath);
			selectedPathID = 0;
			selectedPath = previousSelectedPath;
		}
		else {
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
					generateSingleNodePath(vertexRep);
					isPathStartSelected = true;
				}
			}
		}
		else {// //////// select end node /////////////////////////
			if (pathStartVertexRep == null)
				return;
			KShortestPaths<PathwayVertexRep, DefaultEdge> pathAlgo = new KShortestPaths<PathwayVertexRep, DefaultEdge>(
					pathway, pathStartVertexRep, MAX_PATHS);
			List<GraphPath<PathwayVertexRep, DefaultEdge>> allPathsTmp = null;
			if (vertexRep != pathStartVertexRep) {
				allPathsTmp = pathAlgo.getPaths(vertexRep);
				// if at least one path exist update the selected path
				if (allPathsTmp != null && allPathsTmp.size() > 0) {
					allPaths = allPathsTmp;
					if (allPaths.size() <= selectedPathID)
						selectedPathID = 0;
					selectedPath = allPaths.get(selectedPathID);
				}
			}
			else {
				generateSingleNodePath(vertexRep);
			}

			if (selectionType == SelectionType.SELECTION) {// click on
															// end node
				isPathStartSelected = false;
				previousSelectedPath = selectedPath;
			}
		}

		triggerPathUpdate();
		isBubbleTextureDirty = true;
	}

	public void handlePathwayElementSelection(SelectionType selectionType, int externalID) {
		setDisplayListDirty();
		if (vertexSelectionManager.getElements(SelectionType.SELECTION).size() == 1) {
			pathStartVertexRep = pathwayItemManager
					.getPathwayVertexRep((Integer) vertexSelectionManager.getElements(SelectionType.SELECTION)
							.toArray()[0]);
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
		// //////////////////////////////////////
		if (isPathSelectionMode) {
			selectPath(vertexRep, selectionType);
		}
		// //////////////////////////////////////
		// Add new vertex to internal selection manager
		vertexSelectionManager.addToType(selectionType, vertexRep.getID());

		int iConnectionID = generalManager.getIDCreator().createID(ManagedObjectType.CONNECTION);
		vertexSelectionManager.addConnectionID(iConnectionID, vertexRep.getID());
		connectedElementRepresentationManager.clear(vertexSelectionManager.getIDType(), selectionType);

		createConnectionLines(selectionType, iConnectionID);

		// SelectionDelta selectionDelta =
		// createExternalSelectionDelta(vertexSelectionManager.getDelta());
		SelectionDelta selectionDelta = vertexSelectionManager.getDelta();

		SelectionUpdateEvent event = new SelectionUpdateEvent();
		event.setSender(this);
		event.setSelectionDelta(selectionDelta);
		eventPublisher.triggerEvent(event);
	}

	private void triggerPathUpdate() {
		EnRoutePathEvent pathEvent = new EnRoutePathEvent();
		pathEvent.setPath(new PathwayPath(selectedPath));
		pathEvent.setSender(this);
		eventPublisher.triggerEvent(pathEvent);
	}

	/**
	 * @param selectedPath setter, see {@link #selectedPath}
	 */
	public void setSelectedPath(PathwayPath selectedPath) {
		if (selectedPath == null || selectedPath.getPathway() != pathway)
			return;

		this.selectedPath = selectedPath.getPath();

		allPaths = new ArrayList<GraphPath<PathwayVertexRep, DefaultEdge>>();
		allPaths.add(selectedPath.getPath());

		isBubbleTextureDirty = true;
		setDisplayListDirty();
	}

	public void clearPath() {
		selectedPath = null;
		allPaths = null;

		triggerPathUpdate();
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
	public void notifyOfChange(EventBasedSelectionManager selectionManager) {
		setDisplayListDirty();
	}

	/**
	 * @return the metaboliteSelectionManager, see {@link #metaboliteSelectionManager}
	 */
	public EventBasedSelectionManager getMetaboliteSelectionManager() {
		return metaboliteSelectionManager;
	}

	/**
	 * @param isPathSelectionMode setter, see {@link #isPathSelectionMode}
	 */
	public void setPathSelectionMode(boolean isPathSelectionMode) {
		this.isPathSelectionMode = isPathSelectionMode;
		isPathStartSelected = false;
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

	@Override
	public void handleClearSelections() {
		vertexSelectionManager.clearSelections();
		sampleSelectionManager.clearSelections();
		metaboliteSelectionManager.clearSelections();

	}

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
	 * @param sampleMappingMode setter, see {@link #sampleMappingMode}
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

}