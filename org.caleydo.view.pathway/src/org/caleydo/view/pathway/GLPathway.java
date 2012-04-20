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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.management.InvalidAttributeValueException;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.awt.GLCanvas;

import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.id.ManagedObjectType;
import org.caleydo.core.data.mapping.IDMappingManager;
import org.caleydo.core.data.selection.ElementConnectionInformation;
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
import org.caleydo.core.event.view.pathway.DisableGeneMappingEvent;
import org.caleydo.core.event.view.pathway.EnableGeneMappingEvent;
import org.caleydo.core.event.view.remote.LoadPathwayEvent;
import org.caleydo.core.event.view.tablebased.SelectionUpdateEvent;
import org.caleydo.core.gui.preferences.PreferenceConstants;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.logging.Logger;
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
import org.caleydo.datadomain.pathway.data.PathwayDataContainer;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.PathwayPath;
import org.caleydo.datadomain.pathway.graph.item.vertex.EPathwayVertexType;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertex;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.manager.PathwayDatabaseType;
import org.caleydo.datadomain.pathway.manager.PathwayItemManager;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.view.pathway.event.LinearizedPathwayPathEvent;
import org.caleydo.view.pathway.event.ShowBubbleSetForPathwayVertexRepsEvent;
import org.caleydo.view.pathway.listener.DisableGeneMappingListener;
import org.caleydo.view.pathway.listener.EnableGeneMappingListener;
import org.caleydo.view.pathway.listener.ShowBubbleSetForPathwayVertexRepsEventListener;
import org.caleydo.view.pathway.listener.SwitchDataRepresentationListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Composite;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;

/**
 * Single OpenGL2 pathway view
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public class GLPathway extends ATableBasedView implements ISelectionUpdateHandler,
		IViewCommandHandler, ISelectionCommandHandler {

	public final static String VIEW_TYPE = "org.caleydo.view.pathway";

	protected PathwayDataDomain pathwayDataDomain;
	private PathwayGraph pathway;

	private boolean enablePathwayTexture = true;

	private boolean isPathwayDataDirty = false;
	
	private PathwayManager pathwayManager;
	private PathwayItemManager pathwayItemManager;

	private GLPathwayContentCreator gLPathwayContentCreator;

	private SelectionManager geneSelectionManager;

	private ConnectedElementRepresentationManager connectedElementRepresentationManager;

	/**
	 * Own texture manager is needed for each GL2 context, because textures
	 * cannot be bound to multiple GL2 contexts.
	 */
	private HashMap<GL, GLPathwayTextureManager> hashGLcontext2TextureManager;

	private Vec3f vecScaling;
	private Vec3f vecTranslation;

	protected EnableGeneMappingListener enableGeneMappingListener;
	protected DisableGeneMappingListener disableGeneMappingListener;
	protected SwitchDataRepresentationListener switchDataRepresentationListener;
	protected ShowBubbleSetForPathwayVertexRepsEventListener showBubbleSetForPathwayVertexRepsEventListener;

	private IPickingListener pathwayElementPickingListener;

	private PathwayPath selectedPath;

	/**
	 * Constructor.
	 */
	public GLPathway(GLCanvas glCanvas, Composite parentComposite, ViewFrustum viewFrustum) {
		super(glCanvas, parentComposite, viewFrustum);
		viewLabel = "Pathway";
		viewType = VIEW_TYPE;

		pathwayManager = PathwayManager.get();
		pathwayItemManager = PathwayItemManager.get();

		pathwayDataDomain = (PathwayDataDomain) DataDomainManager.get()
				.getDataDomainByType("org.caleydo.datadomain.pathway");

		hashGLcontext2TextureManager = new HashMap<GL, GLPathwayTextureManager>();

		connectedElementRepresentationManager = generalManager.getViewManager()
				.getConnectedElementRepresentationManager();

		vecScaling = new Vec3f(1, 1, 1);
		vecTranslation = new Vec3f(0, 0, 0);

		registerPickingListeners();
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
	public void setDataContainer(DataContainer dataContainer) {

		super.setDataContainer(dataContainer);

		if (dataContainer instanceof PathwayDataContainer)
			pathway = ((PathwayDataContainer) dataContainer).getPathway();
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

	protected void registerPickingListeners() {

		pathwayElementPickingListener = new APickingListener() {

			@Override
			public void mouseOver(Pick pick) {

				if (detailLevel == EDetailLevel.VERY_LOW) {
					return;
				}

				handlePathwayElementSelection(SelectionType.MOUSE_OVER, pick.getID());
			}

			@Override
			public void clicked(Pick pick) {

				if (detailLevel == EDetailLevel.VERY_LOW) {
					return;
				}

				// We do not handle picking events in pathways for visbricks
				if (glRemoteRenderingView != null
						&& glRemoteRenderingView.getViewType().equals(
								"org.caleydo.view.brick"))
					return;

				handlePathwayElementSelection(SelectionType.SELECTION, pick.getID());
			}

			@Override
			public void doubleClicked(Pick pick) {

				if (detailLevel == EDetailLevel.VERY_LOW) {
					return;
				}

				PathwayVertexRep vertexRep = (PathwayVertexRep) pathwayItemManager
						.getPathwayVertexRep(pick.getID());

				// Load embedded pathway
				if (vertexRep.getType() == EPathwayVertexType.map) {
					PathwayGraph pathway = PathwayManager.get().searchPathwayByName(
							vertexRep.getName(), PathwayDatabaseType.KEGG);

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
				handlePathwayElementSelection(SelectionType.SELECTION, pick.getID());
			}

			@Override
			public void rightClicked(Pick pick) {

				if (detailLevel == EDetailLevel.VERY_LOW) {
					return;
				}

				PathwayVertexRep vertexRep = (PathwayVertexRep) pathwayItemManager
						.getPathwayVertexRep(pick.getID());

				if (vertexRep.getType() == EPathwayVertexType.map) {

					LoadPathwaysByPathwayItem menuItem = new LoadPathwaysByPathwayItem(
							PathwayManager.get().searchPathwayByName(vertexRep.getName(),
									PathwayDatabaseType.KEGG),
							dataDomain.getDataDomainID());
					contextMenuCreator.addContextMenuItem(menuItem);

				} else if (vertexRep.getType() == EPathwayVertexType.gene) {
					for (PathwayVertex pathwayVertexGraphItem : vertexRep
							.getPathwayVertices()) {

						GeneMenuItemContainer contexMenuItemContainer = new GeneMenuItemContainer();
						contexMenuItemContainer
								.setDataDomain((ATableBasedDataDomain) dataDomain);
						contexMenuItemContainer
								.setData(
										pathwayDataDomain.getDavidIDType(),
										pathwayItemManager
												.getDavidIdByPathwayVertex((PathwayVertex) pathwayVertexGraphItem));
						contextMenuCreator
								.addContextMenuItemContainer(contexMenuItemContainer);
					}
				}

				handlePathwayElementSelection(SelectionType.SELECTION, pick.getID());
			}
		};
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
		
		geneSelectionManager.clearSelections();
		selectedPath = null;
		
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
		// GLHelperFunctions.drawPointAt(gl, new Vec3f(0,0,0));
		gl.glTranslatef(vecTranslation.x(), vecTranslation.y(), vecTranslation.z());
		gl.glScalef(vecScaling.x(), vecScaling.y(), vecScaling.z());

		if (enablePathwayTexture) {
			float fPathwayTransparency = 1.0f;

			hashGLcontext2TextureManager.get(gl).renderPathway(gl, this, pathway,
					fPathwayTransparency, false);
		}

		float tmp = PathwayRenderStyle.SCALING_FACTOR_Y * pathway.getHeight();

		// Pathway texture height is subtracted from Y to align pathways to
		// front level
		gl.glTranslatef(0, tmp, 0);

		gLPathwayContentCreator.renderPathway(gl, pathway, false);
		renderSelectedPath(gl);

		gl.glTranslatef(0, -tmp, 0);

		gl.glScalef(1 / vecScaling.x(), 1 / vecScaling.y(), 1 / vecScaling.z());
		gl.glTranslatef(-vecTranslation.x(), -vecTranslation.y(), -vecTranslation.z());

		gl.glPopMatrix();
	}

	private void renderSelectedPath(GL2 gl) {

		if (selectedPath == null)
			return;

		gl.glColor3f(1, 0, 0);
		gl.glLineWidth(5);
		for (DefaultEdge edge : selectedPath.getPath().getEdgeList()) {
			PathwayVertexRep sourceVertexRep = pathway.getEdgeSource(edge);
			PathwayVertexRep targetVertexRep = pathway.getEdgeTarget(edge);

			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(sourceVertexRep.getXOrigin()
					* PathwayRenderStyle.SCALING_FACTOR_X, -sourceVertexRep.getYOrigin()
					* PathwayRenderStyle.SCALING_FACTOR_Y, 0.1f);
			gl.glVertex3f(targetVertexRep.getXOrigin()
					* PathwayRenderStyle.SCALING_FACTOR_X, -targetVertexRep.getYOrigin()
					* PathwayRenderStyle.SCALING_FACTOR_Y, 0.1f);
			gl.glEnd();
		}
	}

	private void rebuildPathwayDisplayList(final GL2 gl, int iGLDisplayListIndex) {
		gLPathwayContentCreator.buildPathwayDisplayList(gl, this, pathway);

		// gl.glNewList(iGLDisplayListIndex, GL2.GL_COMPILE);
		// renderPathwayName(gl);
		// gl.glEndList();
	}

	@Override
	public void handleSelectionUpdate(SelectionDelta selectionDelta,
			boolean scrollToSelection, String info) {

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
						dataDomain.getRecordIDType(), viewID, vertexRep.getXOrigin()
								* PathwayRenderStyle.SCALING_FACTOR_X * vecScaling.x()
								+ vecTranslation.x(),
						(pathwayHeight - vertexRep.getYOrigin())
								* PathwayRenderStyle.SCALING_FACTOR_Y * vecScaling.y()
								+ vecTranslation.y(), 0);

				for (Integer iConnectionID : item.getConnectionIDs()) {
					connectedElementRepresentationManager.addSelection(iConnectionID,
							elementRep, item.getSelectionType());
				}
			}
		}
	}

	private ArrayList<Integer> getExpressionIndicesFromPathwayVertexGraphItemRep(
			int iPathwayVertexGraphItemRepID) {

		ArrayList<Integer> alExpressionIndex = new ArrayList<Integer>();

		for (PathwayVertex vertex : pathwayItemManager.getPathwayVertexRep(
				iPathwayVertexGraphItemRepID).getPathwayVertices()) {

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
			for (Integer expressionIndex : getExpressionIndicesFromPathwayVertexGraphItemRep(item
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
				pathwayDataDomain.getPrimaryIDType());

		PathwayVertex pathwayVertexGraphItem;

		IDMappingManager idMappingManager = pathwayDataDomain.getGeneIDMappingManager();

		for (SelectionDeltaItem item : selectionDelta) {

			Set<Integer> tableIDs = idMappingManager.getIDAsSet(
					selectionDelta.getIDType(), pathwayDataDomain.getDavidIDType(),
					item.getID());

			if (tableIDs == null || tableIDs.isEmpty()) {
				continue;
			}

			Integer davidID = (Integer) tableIDs.toArray()[0];

			pathwayVertexGraphItem = pathwayItemManager
					.getPathwayVertexGraphItemByDavidId(davidID);

			// Ignore David IDs that do not exist in any pathway
			if (pathwayVertexGraphItem == null) {
				continue;
			}

			// Convert DAVID ID to pathway graph item representation ID
			for (PathwayVertexRep vertexRep : pathwayVertexGraphItem
					.getPathwayVertexReps()) {
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

		if (pathway.getType().equals(PathwayDatabaseType.BIOCARTA)) {
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

		PathwayVertexRep tmpPathwayVertexGraphItemRep;
		int pathwayHeight = pathway.getHeight();

		int viewID = uniqueID;
		// If rendered remote (hierarchical heat map) - use the remote view ID
		// if (glRemoteRenderingView != null && glRemoteRenderingView instanceof
		// AGLViewBrowser)
		// viewID = glRemoteRenderingView.getID();

		for (int vertexRepID : geneSelectionManager.getElements(selectionType)) {
			tmpPathwayVertexGraphItemRep = pathwayItemManager
					.getPathwayVertexRep(vertexRepID);

			ElementConnectionInformation elementRep = new ElementConnectionInformation(
					dataDomain.getRecordIDType(), viewID,
					tmpPathwayVertexGraphItemRep.getXOrigin()
							* PathwayRenderStyle.SCALING_FACTOR_X * vecScaling.x()
							+ vecTranslation.x(),
					(pathwayHeight - tmpPathwayVertexGraphItemRep.getYOrigin())
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

		RecordVADelta delta = new RecordVADelta(dataContainer.getRecordPerspective()
				.getID(), pathwayDataDomain.getDavidIDType());

		for (PathwayVertexRep vertexRep : pathway.vertexSet()) {
			for (Integer davidID : vertexRep.getDavidIDs()) {
				delta.add(VADeltaItem.create(type, (Integer) davidID));
			}
		}

		RecordVADeltaEvent virtualArrayDeltaEvent = new RecordVADeltaEvent();
		virtualArrayDeltaEvent.setSender(this);
		virtualArrayDeltaEvent.setDataDomainID(dataDomain.getDataDomainID());
		virtualArrayDeltaEvent.setVirtualArrayDelta(delta);
		virtualArrayDeltaEvent.setInfo(getViewLabel());
		eventPublisher.triggerEvent(virtualArrayDeltaEvent);
	}

	@Override
	public String getViewLabel() {
		if (pathway == null)
			return viewLabel;
		return viewLabel + ": " + pathway.getName();
	}

	@Override
	public void initData() {
		connectedElementRepresentationManager.clear(dataDomain.getRecordIDType());
		super.initData();
	}

	@Override
	public void destroy() {
		pathwayManager.setPathwayVisibilityState(pathway, false);

		super.destroy();
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();

		enableGeneMappingListener = new EnableGeneMappingListener();
		enableGeneMappingListener.setHandler(this);
		eventPublisher.addListener(EnableGeneMappingEvent.class,
				enableGeneMappingListener);

		disableGeneMappingListener = new DisableGeneMappingListener();
		disableGeneMappingListener.setHandler(this);
		eventPublisher.addListener(DisableGeneMappingEvent.class,
				disableGeneMappingListener);

		switchDataRepresentationListener = new SwitchDataRepresentationListener();
		switchDataRepresentationListener.setHandler(this);
		eventPublisher.addListener(SwitchDataRepresentationEvent.class,
				switchDataRepresentationListener);

		showBubbleSetForPathwayVertexRepsEventListener = new ShowBubbleSetForPathwayVertexRepsEventListener();
		showBubbleSetForPathwayVertexRepsEventListener.setHandler(this);
		eventPublisher.addListener(ShowBubbleSetForPathwayVertexRepsEvent.class,
				showBubbleSetForPathwayVertexRepsEventListener);
	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();

		if (enableGeneMappingListener != null) {
			eventPublisher.removeListener(EnableGeneMappingEvent.class,
					enableGeneMappingListener);
			enableGeneMappingListener = null;
		}
		if (disableGeneMappingListener != null) {
			eventPublisher.removeListener(DisableGeneMappingEvent.class,
					disableGeneMappingListener);
			disableGeneMappingListener = null;
		}

		if (switchDataRepresentationListener != null) {
			eventPublisher.removeListener(switchDataRepresentationListener);
			switchDataRepresentationListener = null;
		}

		if (showBubbleSetForPathwayVertexRepsEventListener != null) {
			eventPublisher.removeListener(showBubbleSetForPathwayVertexRepsEventListener);
			showBubbleSetForPathwayVertexRepsEventListener = null;
		}
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedPathwayView serializedForm = new SerializedPathwayView(
				pathwayDataDomain.getDataDomainID());
		serializedForm.setViewID(this.getID());

		if (pathway != null)
			serializedForm.setPathwayID(pathway.getID());

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

		if (pathwayDataDomain.getGeneIDMappingManager().hasMapping(
				pathwayDataDomain.getDavidIDType(), dataDomain.getRecordIDType())) {
			geneSelectionManager = dataDomain.getRecordSelectionManager();
		} else {
			geneSelectionManager = dataDomain.getDimensionSelectionManager();
		}

		// selectedSampleIndex =
		// dataContainer.getDimensionPerspective().getVirtualArray().get(0);

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

		PathwayVertexRep vertexRep = (PathwayVertexRep) pathwayItemManager
				.getPathwayVertexRep(externalID);

		if (previouslySelectedVertexRep != null
				&& selectionType == SelectionType.SELECTION) {
			DijkstraShortestPath<PathwayVertexRep, DefaultEdge> pathAlgo = new DijkstraShortestPath<PathwayVertexRep, DefaultEdge>(
					pathway, previouslySelectedVertexRep, vertexRep);
			// new DijkstraShortestPath<PathwayVertexRep, DefaultEdge>()

			GraphPath<PathwayVertexRep, DefaultEdge> path = pathAlgo.getPath();

			if (path != null) {

				selectedPath = new PathwayPath(path);

				LinearizedPathwayPathEvent pathEvent = new LinearizedPathwayPathEvent();

				pathEvent.setPath(selectedPath);
				pathEvent.setDataDomainID(dataDomain.getDataDomainID());
				pathEvent.setSender(this);
				eventPublisher.triggerEvent(pathEvent);

				System.out.println(selectedPath.getPath().getEdgeList());
			}
		}

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
		event.setInfo(getViewLabel());
		eventPublisher.triggerEvent(event);
	}

	/**
	 * Shows the bubble set for the specified list of {@link PathwayVertexRep}s.
	 * 
	 * @param vertexReps
	 */
	public void showBubbleSet(List<PathwayVertexRep> vertexReps) {
		// TODO: Denis.
	}

	/**
	 * @return
	 */
	public IPickingListener getPathwayElementPickingListener() {

		return pathwayElementPickingListener;
	}
}