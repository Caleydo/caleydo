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
package org.caleydo.view.pathway;

import gleem.linalg.Vec3f;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.PixelGrabber;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.management.InvalidAttributeValueException;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.awt.GLCanvas;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.selection.ElementConnectionInformation;
import org.caleydo.core.data.selection.EventBasedSelectionManager;
import org.caleydo.core.data.selection.IEventBasedSelectionManagerUser;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDeltaItem;
import org.caleydo.core.data.selection.events.ISelectionCommandHandler;
import org.caleydo.core.data.selection.events.ISelectionUpdateHandler;
import org.caleydo.core.data.virtualarray.EVAOperation;
import org.caleydo.core.data.virtualarray.delta.RecordVADelta;
import org.caleydo.core.data.virtualarray.delta.VADeltaItem;
import org.caleydo.core.data.virtualarray.events.RecordVADeltaEvent;
import org.caleydo.core.event.view.SwitchDataRepresentationEvent;
import org.caleydo.core.event.view.remote.LoadPathwayEvent;
import org.caleydo.core.event.view.tablebased.SelectionUpdateEvent;
import org.caleydo.core.gui.preferences.PreferenceConstants;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.object.ManagedObjectType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.color.ColorManager;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.ISingleTablePerspectiveBasedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.ATableBasedView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.vislink.ConnectedElementRepresentationManager;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
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
import org.caleydo.view.pathway.event.ClearPathEvent;
import org.caleydo.view.pathway.event.EnRoutePathEvent;
import org.caleydo.view.pathway.event.EnableGeneMappingEvent;
import org.caleydo.view.pathway.event.SelectPathModeEvent;
import org.caleydo.view.pathway.listener.ClearPathEventListener;
import org.caleydo.view.pathway.listener.EnRoutePathEventListener;
import org.caleydo.view.pathway.listener.EnableGeneMappingListener;
import org.caleydo.view.pathway.listener.SelectPathModeEventListener;
import org.caleydo.view.pathway.listener.SwitchDataRepresentationListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Composite;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.KShortestPaths;
import org.jgrapht.graph.DefaultEdge;

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

public class GLPathway extends ATableBasedView implements
		ISingleTablePerspectiveBasedView, ISelectionUpdateHandler, IViewCommandHandler,
		ISelectionCommandHandler, IEventBasedSelectionManagerUser {
	public static String VIEW_TYPE = "org.caleydo.view.pathway";

	public static String VIEW_NAME = "Pathway";

	/**
	 * The maximal number of paths in the pathway that are looked up. The user
	 * specifies from which source to which destination node the search will be
	 * triggered.
	 */
	private final static int MAX_PATHS = 10;

	protected PathwayDataDomain pathwayDataDomain;
	private PathwayGraph pathway;

	private boolean enablePathwayTexture = true;

	private boolean isPathwayDataDirty = false;

	private PathwayManager pathwayManager;
	private PathwayItemManager pathwayItemManager;

	private GLPathwayContentCreator gLPathwayContentCreator;

	private SelectionManager geneSelectionManager;
	private EventBasedSelectionManager sampleSelectionManager;

	/**
	 * Selection manager for metabolites (compounds). Uses the hash value of
	 * compound names as id.
	 */
	private EventBasedSelectionManager metaboliteSelectionManager;

	private ConnectedElementRepresentationManager connectedElementRepresentationManager;

	/**
	 * Own texture manager is needed for each GL2 context, because textures
	 * cannot be bound to multiple GL2 contexts.
	 */
	private HashMap<GL, GLPathwayTextureManager> hashGLcontext2TextureManager;

	private Vec3f vecScaling;
	private Vec3f vecTranslation;

	protected EnableGeneMappingListener enableGeneMappingListener;
	protected SwitchDataRepresentationListener switchDataRepresentationListener;
	protected EnRoutePathEventListener enRoutePathEventListener;
	protected SelectPathModeEventListener selectPathModeEventListener;
	protected ClearPathEventListener clearPathEventListener;

	private IPickingListener pathwayElementPickingListener;

	/**
	 * The currently selected path as selected by the user from allPaths.
	 */
	private GraphPath<PathwayVertexRep, DefaultEdge> selectedPath;

	/**
	 * All paths which are available between two user selected nodes.
	 */
	private List<GraphPath<PathwayVertexRep, DefaultEdge>> allPaths = null;

	private TextureRenderer texRenderer;
	private SetOutline setOutline;
	private AbstractShapeGenerator shaper;
	private CanvasComponent bubblesetCanvas;
	Texture bubbleSetsTexture;
	private boolean isBubbleTextureDirty;

	private boolean isControlKeyDown = false;
	private boolean isShiftKeyDown = false;
	private int selectedPathID;

	/**
	 * Determines whether the paths should be selectable via mouse click.
	 */
	private boolean isPathSelectionMode = false;

	/**
	 * Constructor.
	 */
	public GLPathway(GLCanvas glCanvas, Composite parentComposite, ViewFrustum viewFrustum) {
		super(glCanvas, parentComposite, viewFrustum, VIEW_TYPE, VIEW_NAME);

		pathwayManager = PathwayManager.get();
		pathwayItemManager = PathwayItemManager.get();

		metaboliteSelectionManager = new EventBasedSelectionManager(this,
				IDType.getIDType("METABOLITE"));
		metaboliteSelectionManager.registerEventListeners();

		pathwayDataDomain = (PathwayDataDomain) DataDomainManager.get()
				.getDataDomainByType("org.caleydo.datadomain.pathway");

		hashGLcontext2TextureManager = new HashMap<GL, GLPathwayTextureManager>();

		connectedElementRepresentationManager = generalManager.getViewManager()
				.getConnectedElementRepresentationManager();

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

	@Override
	public void setTablePerspective(TablePerspective tablePerspective) {

		super.setTablePerspective(tablePerspective);

		if (tablePerspective instanceof PathwayTablePerspective)
			pathway = ((PathwayTablePerspective) tablePerspective).getPathway();
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
		gLPathwayContentCreator = new GLPathwayContentCreator(viewFrustum, this);
	}

	@Override
	public void initLocal(final GL2 gl) {
		init(gl);
		// we will adapt the dimensions in each frame
		texRenderer = new TextureRenderer(1280, 768, true);
	}

	@Override
	public void initRemote(final GL2 gl, final AGLView glParentView,
			final GLMouseListener glMouseListener) {
		this.glMouseListener = glMouseListener;
		init(gl);
	}

	@Override
	public void init(final GL2 gl) {

		displayListIndex = gl.glGenLists(1);
		// Check if pathway exists or if it's already loaded
		if (pathway == null || !pathwayManager.hasItem(pathway.getID()))
			return;

		initPathwayData(gl);
	}

	protected void registerMouseListeners() {

		parentGLCanvas.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				selectNextPath();
			}
		});
	}

	protected void registeKeyListeners() {

		parentGLCanvas.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				// System.out.println("getKeyCode()"+e.getKeyCode());
				// //comment_1/2:
				if (e.isControlDown() && (e.getKeyCode() == 79)) { // ctrl +o
					setPathSelectionMode(!isPathSelectionMode);
					// System.out.println("ctrl + 79 "); //comment_2/2: will
					// remove this and the println at comment_1-2 after i know
					// which ctrol+key combination are still available
					// GeneralManager.get().getEventPublisher().getListenerMap().get(SelectPathModeEvent);
					// TODO select / unselect button
				}
				isControlKeyDown = e.isControlDown();
				isShiftKeyDown = e.isShiftDown();
			}

			@Override
			public void keyReleased(KeyEvent e) {

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

				handlePathwayElementSelection(SelectionType.MOUSE_OVER,
						pick.getObjectID());
			}

			@Override
			public void clicked(Pick pick) {

				if (detailLevel == EDetailLevel.VERY_LOW) {
					return;
				}

				// We do not handle picking events in pathways for StratomeX
				if (glRemoteRenderingView != null
						&& glRemoteRenderingView.getViewType().equals(
								"org.caleydo.view.brick"))
					return;

				handlePathwayElementSelection(SelectionType.SELECTION, pick.getObjectID());
			}

			@Override
			public void doubleClicked(Pick pick) {

				if (detailLevel == EDetailLevel.VERY_LOW) {
					return;
				}

				PathwayVertexRep vertexRep = (PathwayVertexRep) pathwayItemManager
						.getPathwayVertexRep(pick.getObjectID());

				// Load embedded pathway
				if (vertexRep.getType() == EPathwayVertexType.map) {
					PathwayGraph pathway = PathwayManager.get().getPathwayByTitle(
							vertexRep.getName(), EPathwayDatabaseType.KEGG);

					if (pathway != null) {
						LoadPathwayEvent event = new LoadPathwayEvent();
						event.setSender(this);
						event.setPathwayID(pathway.getID());
						event.setDataDomainID(dataDomain.getDataDomainID());
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

				PathwayVertexRep vertexRep = (PathwayVertexRep) pathwayItemManager
						.getPathwayVertexRep(pick.getObjectID());

				if (vertexRep.getType() == EPathwayVertexType.map) {

					LoadPathwaysByPathwayItem menuItem = new LoadPathwaysByPathwayItem(
							PathwayManager.get().getPathwayByTitle(vertexRep.getName(),
									EPathwayDatabaseType.KEGG), dataDomain
									.getDataDomainID());
					contextMenuCreator.addContextMenuItem(menuItem);

				} else if (vertexRep.getType() == EPathwayVertexType.gene) {
					for (PathwayVertex pathwayVertex : vertexRep.getPathwayVertices()) {

						GeneMenuItemContainer contexMenuItemContainer = new GeneMenuItemContainer();
						contexMenuItemContainer
								.setDataDomain((ATableBasedDataDomain) dataDomain);
						contexMenuItemContainer
								.setData(
										pathwayDataDomain.getDavidIDType(),
										pathwayItemManager
												.getDavidIdByPathwayVertex((PathwayVertex) pathwayVertex));
						contextMenuCreator
								.addContextMenuItemContainer(contexMenuItemContainer);
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
				int pickX = (int) pick.getPickedPoint().getX();
				int pickY = (int) pick.getPickedPoint().getY();
				System.out.println("\nDENIS_DEBUG::UnScaled pickX:" + pickX + " pickY:"
						+ pickY);
				//
				// code adapted from documentation at
				// http://docs.oracle.com/javase/6/docs/api/java/awt/image/PixelGrabber.html
				int[] pixels = new int[1];
				Image img = texRenderer.getImage();
				PixelGrabber pxlGrabber = new PixelGrabber(img, pickX, pickY, 1, 1,
						pixels, 0, 1);
				try {
					pxlGrabber.grabPixels();
				} catch (InterruptedException e) {
					System.err.println("interrupted waiting for pixels!");
					return;
				}
				int alpha = (pixels[0] >> 24) & 0xff;
				int red = (pixels[0] >> 16) & 0xff;
				int green = (pixels[0] >> 8) & 0xff;
				int blue = (pixels[0]) & 0xff;
				System.out.println("DENIS_DEBUG:: pickedRed:" + red + " pickedGreen:"
						+ green + " pickedBlue:" + blue + " pickedAlpha:" + alpha);
				// look up color
				List<org.caleydo.core.util.color.Color> colorTable = (ColorManager.get()).getColorList("qualitativeColors");
				float[] cComponents = new float[4];
				for (int i = 0; i < colorTable.size() - 2; i++) {
					org.caleydo.core.util.color.Color c = colorTable.get(i);
					//
					int threshold = 10;
					cComponents = c.getRGB();
					if (red > (int) (cComponents[0] * 255f) - threshold
							&& red < (int) (cComponents[0] * 255f) + threshold) {
						System.out.println("DENIS_DEBUG:: found usedColor id=" + i);
						// select
						selectedPathID = i;
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
		if (pathway == null || !pathwayManager.hasItem(pathway.getID()))
			return;

		if (isPathwayDataDirty)
			initPathwayData(gl);

		pickingManager.handlePicking(this, gl);
		if (isDisplayListDirty) {
			rebuildPathwayDisplayList(gl, displayListIndex);
			isDisplayListDirty = false;
		}
		display(gl);
	}

	@Override
	public void displayRemote(final GL2 gl) {

		if (isDisplayListDirty) {
			calculatePathwayScaling(gl, pathway);
			rebuildPathwayDisplayList(gl, displayListIndex);
			isDisplayListDirty = false;
		}

		display(gl);

	}

	@Override
	public void display(final GL2 gl) {
		checkForHits(gl);

		if (pathway != null) {
			// TODO: also put this in global DL
			renderPathway(gl, pathway);

			gl.glCallList(displayListIndex);
		}
	}

	protected void initPathwayData(final GL2 gl) {

		isPathwayDataDirty = false;
		isDisplayListDirty = true;

		geneSelectionManager.clearSelections();
		sampleSelectionManager.clearSelections();
		selectedPath = null;
		allPaths = null;

		gLPathwayContentCreator.init(gl, geneSelectionManager);

		// Create new pathway manager for GL2 context
		if (!hashGLcontext2TextureManager.containsKey(gl)) {
			hashGLcontext2TextureManager.put(gl, new GLPathwayTextureManager());
		}

		calculatePathwayScaling(gl, pathway);
		pathwayManager.setPathwayVisibilityState(pathway, true);

		// gLPathwayContentCreator.buildPathwayDisplayList(gl, this,
		// iPathwayID);
	}

	private void renderPathway(final GL2 gl, final PathwayGraph pathway) {

		gl.glPushMatrix();
		gl.glTranslatef(vecTranslation.x(), vecTranslation.y(), vecTranslation.z());
		gl.glScalef(vecScaling.x(), vecScaling.y(), vecScaling.z());
		float textureOffset = 0.0f;// to avoid z fighting
		if (enablePathwayTexture) {
			float fPathwayTransparency = 1.0f;

			hashGLcontext2TextureManager.get(gl).renderPathway(gl, this, pathway,
					fPathwayTransparency, false);
			textureOffset += 0.01f;
			gl.glTranslatef(0.0f, 0.0f, textureOffset);
			overlayBubbleSets(gl);

		}

		float tmp = PathwayRenderStyle.SCALING_FACTOR_Y * pathway.getHeight();

		// Pathway texture height is subtracted from Y to align pathways to
		// front level
		textureOffset += 0.01f;
		gl.glTranslatef(0, tmp, textureOffset);
		gLPathwayContentCreator.renderPathway(gl, pathway, false);
		renderPaths(gl);
		gl.glTranslatef(0, -tmp, -textureOffset);

		gl.glScalef(1 / vecScaling.x(), 1 / vecScaling.y(), 1 / vecScaling.z());
		gl.glTranslatef(-vecTranslation.x(), -vecTranslation.y(), -vecTranslation.z());

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
		HashSet<PathwayVertexRep> visitedNodes = new HashSet();
		for (GraphPath<PathwayVertexRep, DefaultEdge> path : allPaths) {
			// updateSingleBubbleSet(gl, path, bbGroupID,visitedNodes);
			if (path == null)
				return;

			double bbItemW = 10;
			double bbItemH = 10;

			// group0

			gl.glPushName(generalManager
					.getViewManager()
					.getPickingManager()
					.getPickingID(uniqueID, EPickingType.PATHWAY_PATH_SELECTION.name(),
							allPaths.indexOf(path)));
			float[] colorValues = new float[3];
			Integer outlineThickness;
			bbGroupID++;
			if (path == selectedPath) {
				colorValues = SelectionType.SELECTION.getColor();
				outlineThickness = 3;
				// bubble sets do not allow to delete
				bubblesetCanvas.addGroup(new Color(colorValues[0], colorValues[1],
						colorValues[2]), outlineThickness, true);
			} else {
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
				bubblesetCanvas
						.addGroup(new Color(c.r, c.g, c.b), outlineThickness, true);
			}

			DefaultEdge lastEdge = null;
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
				bubblesetCanvas.addEdge(bbGroupID, posX, posY, tX, tY);
				lastEdge = edge;
				visitedNodes.add(sourceVertexRep);
			}
			if (lastEdge != null) {
				PathwayVertexRep targetVertexRep = pathway.getEdgeTarget(lastEdge);
				double posX = targetVertexRep.getLowerLeftCornerX();
				double posY = targetVertexRep.getLowerLeftCornerY();
				bbItemW = targetVertexRep.getWidth();
				bbItemH = targetVertexRep.getHeight();
				bubblesetCanvas.addItem(bbGroupID, posX, posY, bbItemW, bbItemH);
				visitedNodes.add(targetVertexRep);
			}
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

		float textureWidth = PathwayRenderStyle.SCALING_FACTOR_X * pathway.getWidth();
		float textureHeight = PathwayRenderStyle.SCALING_FACTOR_Y * pathway.getHeight();

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

	private void renderPaths(GL2 gl) {

		if (allPaths == null)
			return;

		for (GraphPath<PathwayVertexRep, DefaultEdge> path : allPaths)
			renderSinglePath(gl, path);
	}

	private void renderSinglePath(GL2 gl, GraphPath<PathwayVertexRep, DefaultEdge> path) {

		if (path == null)
			return;

		gl.glLineWidth(5);

		gl.glPushName(generalManager
				.getViewManager()
				.getPickingManager()
				.getPickingID(uniqueID, EPickingType.PATHWAY_PATH_SELECTION.name(),
						allPaths.indexOf(path)));

		if (path == selectedPath)
			gl.glColor4fv(SelectionType.SELECTION.getColor(), 0);
		else
			gl.glColor4fv(PathwayRenderStyle.PATH_COLOR, 0);

		for (DefaultEdge edge : path.getEdgeList()) {

			PathwayVertexRep sourceVertexRep = pathway.getEdgeSource(edge);
			PathwayVertexRep targetVertexRep = pathway.getEdgeTarget(edge);

			// gl.glBegin(GL.GL_LINES);
			// gl.glVertex3f(sourceVertexRep.getCenterX() *
			// PathwayRenderStyle.SCALING_FACTOR_X,
			// -sourceVertexRep.getCenterY() *
			// PathwayRenderStyle.SCALING_FACTOR_Y, 0.1f);
			// gl.glVertex3f(targetVertexRep.getCenterX() *
			// PathwayRenderStyle.SCALING_FACTOR_X,
			// -targetVertexRep.getCenterY() *
			// PathwayRenderStyle.SCALING_FACTOR_Y, 0.1f);
			// gl.glEnd();
		}

		gl.glPopName();
	}

	private void rebuildPathwayDisplayList(final GL2 gl, int iGLDisplayListIndex) {
		gLPathwayContentCreator.buildPathwayDisplayList(gl, this, pathway);

		// gl.glNewList(iGLDisplayListIndex, GL2.GL_COMPILE);
		// renderPathwayName(gl);
		// gl.glEndList();
	}

	@Override
	public void handleSelectionUpdate(SelectionDelta selectionDelta) {

		if (pathway == null)
			return;

		if (selectionDelta.getIDType().getIDCategory() == geneSelectionManager
				.getIDType().getIDCategory()) {
			SelectionDelta resolvedDelta = resolveExternalSelectionDelta(selectionDelta);
			geneSelectionManager.setDelta(resolvedDelta);

			setDisplayListDirty();

			int pathwayHeight = pathway.getHeight();
			for (SelectionDeltaItem item : resolvedDelta) {
				if (item.getSelectionType() != SelectionType.MOUSE_OVER
						&& item.getSelectionType() != SelectionType.SELECTION) {
					continue;
				}

				PathwayVertexRep vertexRep = (PathwayVertexRep) pathwayItemManager
						.getPathwayVertexRep(item.getID());

				int viewID = uniqueID;
				// If rendered remote (hierarchical heat map) - use the remote
				// view ID
				// if (glRemoteRenderingView != null && glRemoteRenderingView
				// instanceof AGLViewBrowser)
				// viewID = glRemoteRenderingView.getID();

				ElementConnectionInformation elementRep = new ElementConnectionInformation(
						dataDomain.getRecordIDType(), viewID,
						vertexRep.getLowerLeftCornerX()
								* PathwayRenderStyle.SCALING_FACTOR_X * vecScaling.x()
								+ vecTranslation.x(),
						(pathwayHeight - vertexRep.getLowerLeftCornerY())
								* PathwayRenderStyle.SCALING_FACTOR_Y * vecScaling.y()
								+ vecTranslation.y(), 0);

				for (Integer iConnectionID : item.getConnectionIDs()) {
					connectedElementRepresentationManager.addSelection(iConnectionID,
							elementRep, item.getSelectionType());
				}
			}
		}
	}

	private ArrayList<Integer> getExpressionIndicesFromPathwayVertexRep(
			int pathwayVertexRepID) {

		ArrayList<Integer> alExpressionIndex = new ArrayList<Integer>();

		for (PathwayVertex vertex : pathwayItemManager.getPathwayVertexRep(
				pathwayVertexRepID).getPathwayVertices()) {

			Integer davidID = pathwayItemManager
					.getDavidIdByPathwayVertex((PathwayVertex) vertex);

			if (davidID == null || davidID == -1) {
				continue;
			}

			IDType geneIDType = geneSelectionManager.getIDType();

			Set<Integer> dataTableExpressionIndex = pathwayDataDomain
					.getGeneIDMappingManager().getIDAsSet(
							pathwayDataDomain.getDavidIDType(), geneIDType, davidID);
			if (dataTableExpressionIndex == null)
				continue;
			alExpressionIndex.addAll(dataTableExpressionIndex);
		}

		return alExpressionIndex;
	}

	private SelectionDelta createExternalSelectionDelta(SelectionDelta selectionDelta) {
		SelectionDelta newSelectionDelta = new SelectionDelta(
				geneSelectionManager.getIDType());

		for (SelectionDeltaItem item : selectionDelta) {
			for (Integer expressionIndex : getExpressionIndicesFromPathwayVertexRep(item
					.getID())) {

				SelectionDeltaItem newItem = newSelectionDelta.addSelection(
						expressionIndex, item.getSelectionType());
				newItem.setRemove(item.isRemove());

				for (Integer connectionID : item.getConnectionIDs()) {
					newSelectionDelta.addConnectionID(expressionIndex, connectionID);
				}
			}
		}

		return newSelectionDelta;
	}

	private SelectionDelta resolveExternalSelectionDelta(SelectionDelta selectionDelta) {

		SelectionDelta newSelectionDelta = new SelectionDelta(
				geneSelectionManager.getIDType());
		// pathwayDataDomain.getPrimaryIDType());

		PathwayVertex pathwayVertex;

		IDMappingManager idMappingManager = pathwayDataDomain.getGeneIDMappingManager();

		for (SelectionDeltaItem item : selectionDelta) {

			Set<Integer> tableIDs = idMappingManager.getIDAsSet(
					selectionDelta.getIDType(), pathwayDataDomain.getDavidIDType(),
					item.getID());

			if (tableIDs == null || tableIDs.isEmpty()) {
				continue;
			}

			Integer davidID = (Integer) tableIDs.toArray()[0];

			pathwayVertex = pathwayItemManager.getPathwayVertexByDavidId(davidID);

			// Ignore David IDs that do not exist in any pathway
			if (pathwayVertex == null) {
				continue;
			}

			// Convert DAVID ID to pathway graph item representation ID
			for (PathwayVertexRep vertexRep : pathwayVertex.getPathwayVertexReps()) {
				if (!pathway.containsVertex(vertexRep)) {
					continue;
				}

				SelectionDeltaItem newItem = newSelectionDelta.addSelection(
						vertexRep.getID(), item.getSelectionType());
				newItem.setRemove(item.isRemove());
				for (int iConnectionID : item.getConnectionIDs()) {
					newItem.addConnectionID(iConnectionID);
				}
			}
		}

		return newSelectionDelta;
	}

	private void calculatePathwayScaling(final GL2 gl, final PathwayGraph pathway) {

		if (hashGLcontext2TextureManager.get(gl) == null)
			return;

		// // Missing power of two texture GL2 extension workaround
		// PathwayGraph tmpPathwayGraph =
		// (PathwayGraph)generalManager.getPathwayManager().getItem(iPathwayId);
		// ImageIcon img = new ImageIcon(generalManager.getPathwayManager()
		// .getPathwayDatabaseByType(tmpPathwayGraph.getType()).getImagePath()
		// + tmpPathwayGraph.getImageLink());
		// int iImageWidth = img.getIconWidth();
		// int iImageHeight = img.getIconHeight();
		// tmpPathwayGraph.setWidth(iImageWidth);
		// tmpPathwayGraph.setHeight(iImageHeight);
		// img = null;

		float fPathwayScalingFactor = 0;
		float fPadding = 0.98f;

		if (pathway.getType().equals(EPathwayDatabaseType.BIOCARTA)) {
			fPathwayScalingFactor = 5;
		} else {
			fPathwayScalingFactor = 3.2f;
		}

		int iImageWidth = pathway.getWidth();
		int iImageHeight = pathway.getHeight();

		if (iImageWidth == -1 || iImageHeight == -1) {
			Logger.log(new Status(IStatus.ERROR, this.toString(),
					"Problem because pathway texture width or height is invalid!"));
		}

		float fTmpPathwayWidth = iImageWidth * PathwayRenderStyle.SCALING_FACTOR_X
				* fPathwayScalingFactor;
		float fTmpPathwayHeight = iImageHeight * PathwayRenderStyle.SCALING_FACTOR_Y
				* fPathwayScalingFactor;

		float pathwayAspectRatio = fTmpPathwayWidth / fTmpPathwayHeight;
		float viewFrustumWidth = viewFrustum.getRight() - viewFrustum.getLeft();
		float viewFrustumHeight = viewFrustum.getTop() - viewFrustum.getBottom();
		float viewFrustumAspectRatio = viewFrustumWidth / viewFrustumHeight;
		boolean pathwayFitsViewFrustum = true;

		if (viewFrustumAspectRatio < pathwayAspectRatio
				&& fTmpPathwayWidth > viewFrustumWidth) {

			// if (fTmpPathwayWidth > viewFrustum.getRight() -
			// viewFrustum.getLeft()
			// && fTmpPathwayWidth > fTmpPathwayHeight) {
			vecScaling.setX((viewFrustum.getRight() - viewFrustum.getLeft())
					/ (iImageWidth * PathwayRenderStyle.SCALING_FACTOR_X) * fPadding);
			vecScaling.setY(vecScaling.x());

			vecTranslation
					.set((viewFrustum.getRight() - viewFrustum.getLeft() - iImageWidth
							* PathwayRenderStyle.SCALING_FACTOR_X * vecScaling.x()) / 2.0f,
							(viewFrustum.getTop() - viewFrustum.getBottom() - iImageHeight
									* PathwayRenderStyle.SCALING_FACTOR_Y
									* vecScaling.y()) / 2.0f, 0);
			pathwayFitsViewFrustum = false;
		}
		if (viewFrustumAspectRatio >= pathwayAspectRatio
				&& fTmpPathwayHeight > viewFrustumHeight) {
			//
			// else if (fTmpPathwayHeight > viewFrustum.getTop()
			// - viewFrustum.getBottom()) {
			vecScaling.setY((viewFrustum.getTop() - viewFrustum.getBottom())
					/ (iImageHeight * PathwayRenderStyle.SCALING_FACTOR_Y) * fPadding);
			vecScaling.setX(vecScaling.y());

			vecTranslation
					.set((viewFrustum.getRight() - viewFrustum.getLeft() - iImageWidth
							* PathwayRenderStyle.SCALING_FACTOR_X * vecScaling.x()) / 2.0f,
							(viewFrustum.getTop() - viewFrustum.getBottom() - iImageHeight
									* PathwayRenderStyle.SCALING_FACTOR_Y
									* vecScaling.y()) / 2.0f, 0);
			pathwayFitsViewFrustum = false;

		} // else {

		if (pathwayFitsViewFrustum) {
			vecScaling.set(fPathwayScalingFactor, fPathwayScalingFactor, 1f);

			vecTranslation.set((viewFrustum.getRight() - viewFrustum.getLeft()) / 2.0f
					- fTmpPathwayWidth / 2.0f,
					(viewFrustum.getTop() - viewFrustum.getBottom()) / 2.0f
							- fTmpPathwayHeight / 2.0f, 0);
		}
	}

	public void enableGeneMapping(final boolean bEnableMapping) {
		gLPathwayContentCreator.enableGeneMapping(bEnableMapping);
		setDisplayListDirty();
	}

	public void enablePathwayTextures(final boolean bEnablePathwayTexture) {
		gLPathwayContentCreator.enableEdgeRendering(!bEnablePathwayTexture);
		setDisplayListDirty();

		this.enablePathwayTexture = bEnablePathwayTexture;
	}

	public void enableNeighborhood(final boolean bEnableNeighborhood) {
		setDisplayListDirty();

		gLPathwayContentCreator.enableNeighborhood(bEnableNeighborhood);
	}

	private void createConnectionLines(SelectionType selectionType, int iConnectionID) {
		// check in preferences if we should draw connection lines for mouse
		// over
		if (!connectedElementRepresentationManager
				.isSelectionTypeRenderedWithVisuaLinks(selectionType))
			return;
		// check for selections
		if (!generalManager.getPreferenceStore().getBoolean(
				PreferenceConstants.VISUAL_LINKS_FOR_SELECTIONS)
				&& selectionType == SelectionType.SELECTION)
			return;

		PathwayVertexRep tmpPathwayVertexRep;
		int pathwayHeight = pathway.getHeight();

		int viewID = uniqueID;
		// If rendered remote (hierarchical heat map) - use the remote view ID
		// if (glRemoteRenderingView != null && glRemoteRenderingView instanceof
		// AGLViewBrowser)
		// viewID = glRemoteRenderingView.getID();

		for (int vertexRepID : geneSelectionManager.getElements(selectionType)) {
			tmpPathwayVertexRep = pathwayItemManager.getPathwayVertexRep(vertexRepID);

			ElementConnectionInformation elementRep = new ElementConnectionInformation(
					dataDomain.getRecordIDType(), viewID,
					tmpPathwayVertexRep.getLowerLeftCornerX()
							* PathwayRenderStyle.SCALING_FACTOR_X * vecScaling.x()
							+ vecTranslation.x(),
					(pathwayHeight - tmpPathwayVertexRep.getLowerLeftCornerY())
							* PathwayRenderStyle.SCALING_FACTOR_Y * vecScaling.y()
							+ vecTranslation.y(), 0);

			// for (Integer iConnectionID : selectionManager
			// .getConnectionForElementID(iVertexRepID))
			// {
			connectedElementRepresentationManager.addSelection(iConnectionID, elementRep,
					selectionType);
			// }
		}
		// }
	}

	@Override
	public void broadcastElements(EVAOperation type) {

		if (pathway == null)
			return;

		RecordVADelta delta = new RecordVADelta(tablePerspective.getRecordPerspective()
				.getPerspectiveID(), pathwayDataDomain.getDavidIDType());

		for (PathwayVertexRep vertexRep : pathway.vertexSet()) {
			for (Integer davidID : vertexRep.getDavidIDs()) {
				delta.add(VADeltaItem.create(type, (Integer) davidID));
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
		eventPublisher.addListener(EnableGeneMappingEvent.class,
				enableGeneMappingListener);

		switchDataRepresentationListener = new SwitchDataRepresentationListener();
		switchDataRepresentationListener.setHandler(this);
		eventPublisher.addListener(SwitchDataRepresentationEvent.class,
				switchDataRepresentationListener);

		enRoutePathEventListener = new EnRoutePathEventListener();
		enRoutePathEventListener.setHandler(this);
		eventPublisher.addListener(EnRoutePathEvent.class, enRoutePathEventListener);

		selectPathModeEventListener = new SelectPathModeEventListener();
		selectPathModeEventListener.setHandler(this);
		eventPublisher
				.addListener(SelectPathModeEvent.class, selectPathModeEventListener);

		clearPathEventListener = new ClearPathEventListener();
		clearPathEventListener.setHandler(this);
		eventPublisher.addListener(ClearPathEvent.class, clearPathEventListener);
	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();

		if (enableGeneMappingListener != null) {
			eventPublisher.removeListener(EnableGeneMappingEvent.class,
					enableGeneMappingListener);
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

		metaboliteSelectionManager.unregisterEventListeners();

	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedPathwayView serializedForm = new SerializedPathwayView(
				pathwayDataDomain.getDataDomainID(), this);
		// FIXME this needs to be reviewed - what is the unique, serializable ID
		// of the pathway here?
		if (pathway != null)
			serializedForm.setPathwayID(pathway.getID());

		System.out.println("Serializing Pathway: review me!");

		return serializedForm;
	}

	public PathwayDataDomain getPathwayDataDomain() {
		return pathwayDataDomain;
	}

	@Override
	public int getMinPixelHeight() {
		return 120;
	}

	@Override
	public int getMinPixelWidth() {
		if (pathway == null)
			return 70;
		float aspectRatio = (float) pathway.getWidth() / (float) pathway.getHeight();
		return (int) (120.0f * aspectRatio);
	}

	@Override
	public boolean isDataView() {
		return true;
	}

	@Override
	public void switchDataRepresentation() {
		gLPathwayContentCreator.switchDataRepresentation();
		setDisplayListDirty();
	}

	@Override
	public void setDataDomain(ATableBasedDataDomain dataDomain) {
		if (!(dataDomain instanceof GeneticDataDomain))
			throw new IllegalArgumentException(
					"Pathway view can handle only genetic data domain, tried to set: "
							+ dataDomain);

		GeneticDataDomain geneticDataDomain = (GeneticDataDomain) dataDomain;
		if (geneticDataDomain.isGeneRecord())
			geneSelectionManager = geneticDataDomain.getRecordSelectionManager();
		else
			geneSelectionManager = geneticDataDomain.getDimensionSelectionManager();
		
		sampleSelectionManager = new EventBasedSelectionManager(this,
				((GeneticDataDomain) dataDomain).getSampleIDType());

		super.setDataDomain(dataDomain);
	}

	@Override
	public ATableBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	@Override
	protected ArrayList<ElementConnectionInformation> createElementConnectionInformation(
			IDType idType, int id) throws InvalidAttributeValueException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return the geneSelectionManager, see {@link #geneSelectionManager}
	 */
	public SelectionManager getGeneSelectionManager() {
		return geneSelectionManager;
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

	public void handlePathwayElementSelection(SelectionType selectionType, int externalID) {

		setDisplayListDirty();
		if (geneSelectionManager.checkStatus(selectionType, externalID)) {
			return;
		}

		PathwayVertexRep previouslySelectedVertexRep = null;
		if (geneSelectionManager.getElements(SelectionType.SELECTION).size() == 1) {
			previouslySelectedVertexRep = (PathwayVertexRep) pathwayItemManager
					.getPathwayVertexRep((Integer) geneSelectionManager.getElements(
							SelectionType.SELECTION).toArray()[0]);
		}

		geneSelectionManager.clearSelection(selectionType);
		if (metaboliteSelectionManager.getNumberOfElements(selectionType) > 0) {
			metaboliteSelectionManager.clearSelection(selectionType);
			metaboliteSelectionManager.triggerSelectionUpdateEvent();
		}

		PathwayVertexRep vertexRep = (PathwayVertexRep) pathwayItemManager
				.getPathwayVertexRep(externalID);

		if (vertexRep.getType() == EPathwayVertexType.compound) {
			metaboliteSelectionManager.addToType(selectionType, vertexRep.getName()
					.hashCode());
			metaboliteSelectionManager.triggerSelectionUpdateEvent();
		}

		if (isPathSelectionMode) {
			if (previouslySelectedVertexRep != null) {
				if (!isControlKeyDown) { // extend current path
					// System.out.println("isShiftKeyDown && NOTisControlKeyDown");
					KShortestPaths<PathwayVertexRep, DefaultEdge> pathAlgo = new KShortestPaths<PathwayVertexRep, DefaultEdge>(
							pathway, previouslySelectedVertexRep, MAX_PATHS);

					if (vertexRep != previouslySelectedVertexRep)
						allPaths = pathAlgo.getPaths(vertexRep);

					if (allPaths != null && allPaths.size() > 0) {
						// selectedPath = allPaths.get(0);
						if (allPaths.size() <= selectedPathID)
							selectedPathID = 0;
						selectedPath = allPaths.get(selectedPathID);
						// allPaths.clear();
						selectedPathID = 0;
						allPaths.add(selectedPath);
						triggerPathUpdate();
						isBubbleTextureDirty = true;
					}
				} else if (selectedPath != null) {
					// System.out.println("isControlKeyDown && isShiftKeyDown");
					KShortestPaths<PathwayVertexRep, DefaultEdge> pathAlgo = new KShortestPaths<PathwayVertexRep, DefaultEdge>(
							pathway, selectedPath.getStartVertex(), MAX_PATHS);
					allPaths = pathAlgo.getPaths(vertexRep);
					if (allPaths != null && allPaths.size() > 0) {
						// selectedPath = allPaths.get(0);
						if (allPaths.size() <= selectedPathID)
							selectedPathID = 0;
						selectedPath = allPaths.get(selectedPathID);
						allPaths.clear();
						selectedPathID = 0;
						allPaths.add(selectedPath);
						triggerPathUpdate();
						isBubbleTextureDirty = true;
					}
				}
			} // if (previouslySelectedVertexRep != null)
				// else if (previouslySelectedVertexRep != null
				// && selectionType == SelectionType.MOUSE_OVER) {
				//
				// KShortestPaths<PathwayVertexRep, DefaultEdge> pathAlgo = new
				// KShortestPaths<PathwayVertexRep, DefaultEdge>(
				// pathway, previouslySelectedVertexRep, MAX_PATHS);
				//
				// if (vertexRep != previouslySelectedVertexRep) {
				// List<GraphPath<PathwayVertexRep, DefaultEdge>> mouseOverPaths
				// =
				// pathAlgo
				// .getPaths(vertexRep);
				//
				// if (mouseOverPaths != null && mouseOverPaths.size() > 0) {
				//
				// allPaths = mouseOverPaths;
				// if (allPaths.size() <= selectedPathID)
				// selectedPathID = 0;
				// selectedPath = allPaths.get(selectedPathID);
				// if (selectedPath != null && isControlKeyDown)
				// allPaths.add(selectedPath);
				//
				// isBubbleTextureDirty = true;
				// }
				// }
				// }
				// }// if(isPathSelectionMode)
		}// if(isPathSelectionMode)
			// else{
			// Add new vertex to internal selection manager
		geneSelectionManager.addToType(selectionType, vertexRep.getID());

		int iConnectionID = generalManager.getIDCreator().createID(
				ManagedObjectType.CONNECTION);
		geneSelectionManager.addConnectionID(iConnectionID, vertexRep.getID());
		connectedElementRepresentationManager.clear(geneSelectionManager.getIDType(),
				selectionType);

		createConnectionLines(selectionType, iConnectionID);

		SelectionDelta selectionDelta = createExternalSelectionDelta(geneSelectionManager
				.getDelta());
		SelectionUpdateEvent event = new SelectionUpdateEvent();
		event.setSender(this);
		event.setDataDomainID(dataDomain.getDataDomainID());
		event.setSelectionDelta((SelectionDelta) selectionDelta);
		eventPublisher.triggerEvent(event);
		// }
	}

	private void triggerPathUpdate() {
		EnRoutePathEvent pathEvent = new EnRoutePathEvent();
		pathEvent.setPath(new PathwayPath(selectedPath));
		pathEvent.setDataDomainID(dataDomain.getDataDomainID());
		pathEvent.setSender(this);
		eventPublisher.triggerEvent(pathEvent);
	}

	/**
	 * @param selectedPath
	 *            setter, see {@link #selectedPath}
	 */
	public void setSelectedPath(PathwayPath selectedPath) {
		if (selectedPath.getPathway() != pathway)
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
	public void notifyOfChange() {
		setDisplayListDirty();
	}

	/**
	 * @return the metaboliteSelectionManager, see
	 *         {@link #metaboliteSelectionManager}
	 */
	public EventBasedSelectionManager getMetaboliteSelectionManager() {
		return metaboliteSelectionManager;
	}

	/**
	 * @param isPathSelectionMode
	 *            setter, see {@link #isPathSelectionMode}
	 */
	public void setPathSelectionMode(boolean isPathSelectionMode) {
		this.isPathSelectionMode = isPathSelectionMode;
	}

	/**
	 * @return the isPathSelectionMode, see {@link #isPathSelectionMode}
	 */
	public boolean isPathSelectionMode() {
		return isPathSelectionMode;
	}
}