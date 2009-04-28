package org.caleydo.core.view.opengl.canvas.pathway;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import javax.media.opengl.GL;

import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.data.graph.pathway.item.edge.PathwayReactionEdgeGraphItemRep;
import org.caleydo.core.data.graph.pathway.item.edge.PathwayRelationEdgeGraphItemRep;
import org.caleydo.core.data.graph.pathway.item.vertex.EPathwayVertexShape;
import org.caleydo.core.data.graph.pathway.item.vertex.EPathwayVertexType;
import org.caleydo.core.data.graph.pathway.item.vertex.PathwayVertexGraphItemRep;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.GenericSelectionManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IIDMappingManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;
import org.caleydo.util.graph.EGraphItemHierarchy;
import org.caleydo.util.graph.EGraphItemKind;
import org.caleydo.util.graph.EGraphItemProperty;
import org.caleydo.util.graph.IGraphItem;
import org.caleydo.util.graph.algorithm.GraphVisitorSearchBFS;

/**
 * OpenGL pathway manager.
 * 
 * @author Marc Streit
 */
public class GLPathwayContentCreator {
	private IGeneralManager generalManager;

	private static final float Z_OFFSET = 0.01f;

	private GLPathway glPathwayView;

	private int iEnzymeNodeDisplayListId = -1;
	private int iCompoundNodeDisplayListId = -1;
	private int iHighlightedEnzymeNodeDisplayListId = -1;
	private int iHighlightedCompoundNodeDisplayListId = -1;

	private boolean bEnableEdgeRendering = false;
	private boolean bEnableIdenticalNodeHighlighting = true;
	private boolean bEnableNeighborhood = false;
	private boolean bEnableGeneMapping = true;

	private HashMap<PathwayGraph, Integer> hashPathway2VerticesDisplayListId;
	private HashMap<PathwayGraph, Integer> hashPathway2EdgesDisplayListId;
	// private HashMap<Integer, ArrayList<float[]>> hashElementId2MappingColorArray;

	private ColorMapping colorMapper;

	private GenericSelectionManager internalSelectionManager;

	private ArrayList<Integer> iArSelectedEdgeRepId;

	private IIDMappingManager idMappingManager;

	/**
	 * Constructor.
	 */
	public GLPathwayContentCreator(IViewFrustum viewFrustum, GLPathway glPathwayView) {

		this.generalManager = GeneralManager.get();
		this.glPathwayView = glPathwayView;

		colorMapper = ColorMappingManager.get().getColorMapping(EColorMappingType.GENE_EXPRESSION);

		hashPathway2VerticesDisplayListId = new HashMap<PathwayGraph, Integer>();
		hashPathway2EdgesDisplayListId = new HashMap<PathwayGraph, Integer>();
		// hashElementId2MappingColorArray = new HashMap<Integer, ArrayList<float[]>>();

		iArSelectedEdgeRepId = new ArrayList<Integer>();

		idMappingManager = generalManager.getIDMappingManager();
	}

	public void init(final GL gl, final GenericSelectionManager internalSelectionManager) {

		buildEnzymeNodeDisplayList(gl);
		buildCompoundNodeDisplayList(gl);
		buildHighlightedEnzymeNodeDisplayList(gl);
		buildHighlightedCompoundNodeDisplayList(gl);

		this.internalSelectionManager = internalSelectionManager;

		// hashElementId2MappingColorArray.clear();

		if (generalManager.getIDMappingManager().hasMapping(EMappingType.REFSEQ_MRNA_INT_2_EXPRESSION_INDEX)) {
			bEnableGeneMapping = true;
		}
		else {
			bEnableGeneMapping = false;
		}
	}

	public void buildPathwayDisplayList(final GL gl, final IUniqueObject containingView,
		final PathwayGraph pathway) {
		generalManager.getLogger().log(Level.FINE, "Build display list for pathway " + pathway.getID());

		if (pathway == null)
			return;

		int iVerticesDisplayListId = -1;
		int iEdgesDisplayListId = -1;

		if (hashPathway2VerticesDisplayListId.containsKey(pathway)) {
			// Replace current display list if a display list exists
			iVerticesDisplayListId = hashPathway2VerticesDisplayListId.get(pathway);
		}
		else {
			// Creating vertex display list for pathways
			iVerticesDisplayListId = gl.glGenLists(1);
			hashPathway2VerticesDisplayListId.put(pathway, iVerticesDisplayListId);
		}

		// performIdenticalNodeHighlighting();

		gl.glNewList(iVerticesDisplayListId, GL.GL_COMPILE);
		extractVertices(gl, containingView, pathway);
		gl.glEndList();

		if (hashPathway2EdgesDisplayListId.containsKey(pathway)) {
			// Replace current display list if a display list exists
			iEdgesDisplayListId = hashPathway2EdgesDisplayListId.get(pathway);
		}
		else {
			// Creating edge display list for pathways
			iEdgesDisplayListId = gl.glGenLists(1);
			hashPathway2EdgesDisplayListId.put(pathway, iEdgesDisplayListId);
		}

		gl.glNewList(iEdgesDisplayListId, GL.GL_COMPILE);
		extractEdges(gl, pathway);
		gl.glEndList();
	}

	public void performIdenticalNodeHighlighting(ESelectionType eSelectionType) {
		if (internalSelectionManager == null)
			return;

		iArSelectedEdgeRepId.clear();

		ArrayList<Integer> iAlTmpSelectedGraphItemIds = new ArrayList<Integer>();
		Set<Integer> tmpItemIDs;
		tmpItemIDs = internalSelectionManager.getElements(eSelectionType);

		if (tmpItemIDs != null) {
			iAlTmpSelectedGraphItemIds.addAll(tmpItemIDs);
		}

		// tmpItemIDs =
		// internalSelectionManager.getElements(ESelectionType.SELECTION);
		//
		// if (tmpItemIDs != null)
		// iAlTmpSelectedGraphItemIds.addAll(tmpItemIDs);

		if (iAlTmpSelectedGraphItemIds.size() == 0)
			return;

		// Copy selection IDs to array list object
		for (int iItemIndex = 0; iItemIndex < iAlTmpSelectedGraphItemIds.size(); iItemIndex++) {
			if (!bEnableIdenticalNodeHighlighting) {
				continue;
			}

			// // Perform identical node highlighting only on nodes with depth 0
			// if (iAlTmpSelectedGraphItemDepth.get(iItemIndex) != 0)
			// continue;
			for (IGraphItem graphItem : ((IGraphItem) generalManager.getPathwayItemManager().getItem(
				iAlTmpSelectedGraphItemIds.get(iItemIndex)))
				.getAllItemsByProp(EGraphItemProperty.ALIAS_PARENT)) {
				for (IGraphItem graphItemRep : graphItem.getAllItemsByProp(EGraphItemProperty.ALIAS_CHILD)) {
					if (tmpItemIDs.contains(graphItemRep.getId())) {
						continue;
					}
					internalSelectionManager.addToType(eSelectionType, graphItemRep.getId());
					for (int iConnectionID : internalSelectionManager
						.getConnectionForElementID(iAlTmpSelectedGraphItemIds.get(iItemIndex))) {
						internalSelectionManager.addConnectionID(iConnectionID, graphItemRep.getId());
					}
					if (bEnableNeighborhood) {
						performNeighborhoodAlgorithm(graphItemRep);
					}
				}
			}
		}
	}

	private void performNeighborhoodAlgorithm(final IGraphItem selectedVertex) {
		GraphVisitorSearchBFS graphVisitorSearchBFS;

		if (bEnableNeighborhood) {
			graphVisitorSearchBFS = new GraphVisitorSearchBFS(selectedVertex, 4);
		}
		else {
			graphVisitorSearchBFS = new GraphVisitorSearchBFS(selectedVertex, 0);
		}

		graphVisitorSearchBFS.setProp(EGraphItemProperty.OUTGOING);
		graphVisitorSearchBFS.setGraph(selectedVertex.getAllGraphByType(EGraphItemHierarchy.GRAPH_PARENT)
			.get(0));

		// List<IGraphItem> lGraphItems =
		// graphVisitorSearchBFS.getSearchResult();
		graphVisitorSearchBFS.getSearchResult();

		List<List<IGraphItem>> lDepthSearchResult = graphVisitorSearchBFS.getSearchResultDepthOrdered();
		List<IGraphItem> lGraphItems = new ArrayList<IGraphItem>();

		int iTmpDepth = 0;
		ESelectionType tmpType;

		for (int iDepthIndex = 0; iDepthIndex < lDepthSearchResult.size(); iDepthIndex++) {
			lGraphItems = lDepthSearchResult.get(iDepthIndex);

			for (int iItemIndex = 0; iItemIndex < lGraphItems.size(); iItemIndex++) {
				// Check if selected item is a vertex
				if (lGraphItems.get(iItemIndex) instanceof PathwayVertexGraphItemRep) {
					iTmpDepth = (iDepthIndex + 1) / 2;

					if (iTmpDepth == 1) {
						tmpType = ESelectionType.NEIGHBORHOOD_1;
					}
					else if (iTmpDepth == 2) {
						tmpType = ESelectionType.NEIGHBORHOOD_2;
					}
					else if (iTmpDepth == 3) {
						tmpType = ESelectionType.NEIGHBORHOOD_3;
					}
					else
						throw new IllegalStateException("Neighborhood depth greater than 3 is not supported!");

					internalSelectionManager.addToType(tmpType, lGraphItems.get(iItemIndex).getId());

				}
				else {
					iArSelectedEdgeRepId.add(lGraphItems.get(iItemIndex).getId());
				}
			}
		}
	}

	private void buildEnzymeNodeDisplayList(final GL gl) {
		// Creating display list for node cube objects
		iEnzymeNodeDisplayListId = gl.glGenLists(1);

		float fNodeWidth = PathwayRenderStyle.ENZYME_NODE_WIDTH;
		float fNodeHeight = PathwayRenderStyle.ENZYME_NODE_HEIGHT;

		gl.glNewList(iEnzymeNodeDisplayListId, GL.GL_COMPILE);
		fillNodeDisplayList(gl, fNodeWidth, fNodeHeight);
		gl.glEndList();
	}

	protected void buildHighlightedEnzymeNodeDisplayList(final GL gl) {
		// Creating display list for node cube objects
		iHighlightedEnzymeNodeDisplayListId = gl.glGenLists(1);

		float fNodeWidth = PathwayRenderStyle.ENZYME_NODE_WIDTH;
		float fNodeHeight = PathwayRenderStyle.ENZYME_NODE_HEIGHT;

		gl.glNewList(iHighlightedEnzymeNodeDisplayListId, GL.GL_COMPILE);
		fillNodeDisplayListFrame(gl, fNodeWidth, fNodeHeight);
		gl.glEndList();
	}

	protected void buildCompoundNodeDisplayList(final GL gl) {
		// Creating display list for node cube objects
		iCompoundNodeDisplayListId = gl.glGenLists(1);

		float fNodeWidth = PathwayRenderStyle.COMPOUND_NODE_WIDTH;
		float fNodeHeight = PathwayRenderStyle.COMPOUND_NODE_HEIGHT;

		gl.glNewList(iCompoundNodeDisplayListId, GL.GL_COMPILE);
		fillNodeDisplayList(gl, fNodeWidth, fNodeHeight);
		gl.glEndList();
	}

	protected void buildHighlightedCompoundNodeDisplayList(final GL gl) {
		// Creating display list for node cube objects
		iHighlightedCompoundNodeDisplayListId = gl.glGenLists(1);

		float fNodeWidth = PathwayRenderStyle.COMPOUND_NODE_WIDTH;
		float fNodeHeight = PathwayRenderStyle.COMPOUND_NODE_HEIGHT;

		gl.glNewList(iHighlightedCompoundNodeDisplayListId, GL.GL_COMPILE);
		fillNodeDisplayListFrame(gl, fNodeWidth, fNodeHeight);
		gl.glEndList();
	}

	private void fillNodeDisplayList(final GL gl, final float fNodeWidth, final float fNodeHeight) {

		gl.glBegin(GL.GL_QUADS);

		// FRONT FACE
		gl.glNormal3f(0.0f, 0.0f, 1.0f);
		// Top Right Of The Quad (Front)
		gl.glVertex3f(-fNodeWidth, -fNodeHeight, Z_OFFSET);
		// Top Left Of The Quad (Front)
		gl.glVertex3f(fNodeWidth, -fNodeHeight, Z_OFFSET);
		// Bottom Left Of The Quad (Front)
		gl.glVertex3f(fNodeWidth, fNodeHeight, Z_OFFSET);
		// Bottom Right Of The Quad (Front)
		gl.glVertex3f(-fNodeWidth, fNodeHeight, Z_OFFSET);

		// // BACK FACE
		// gl.glNormal3f(0.0f, 0.0f, -1.0f);
		// // Bottom Left Of The Quad (Back)
		// gl.glVertex3f(fNodeWidth, -fNodeHeight, -Z_OFFSET);
		// // Bottom Right Of The Quad (Back)
		// gl.glVertex3f(-fNodeWidth, -fNodeHeight, -Z_OFFSET);
		// // Top Right Of The Quad (Back)
		// gl.glVertex3f(-fNodeWidth, fNodeHeight, -Z_OFFSET);
		// // Top Left Of The Quad (Back)
		// gl.glVertex3f(fNodeWidth, fNodeHeight, -Z_OFFSET);
		//
		// // TOP FACE
		// gl.glNormal3f(0.0f, 1.0f, 0.0f);
		// // Top Right Of The Quad (Top)
		// gl.glVertex3f(fNodeWidth, fNodeHeight, -Z_OFFSET);
		// // Top Left Of The Quad (Top)
		// gl.glVertex3f(-fNodeWidth, fNodeHeight, -Z_OFFSET);
		// // Bottom Left Of The Quad (Top)
		// gl.glVertex3f(-fNodeWidth, fNodeHeight, Z_OFFSET);
		// // Bottom Right Of The Quad (Top)
		// gl.glVertex3f(fNodeWidth, fNodeHeight, Z_OFFSET);
		//
		// // BOTTOM FACE
		// gl.glNormal3f(0.0f, -1.0f, 0.0f);
		// // Top Right Of The Quad (Bottom)
		// gl.glVertex3f(fNodeWidth, -fNodeHeight, Z_OFFSET);
		// // Top Left Of The Quad (Bottom)
		// gl.glVertex3f(-fNodeWidth, -fNodeHeight, Z_OFFSET);
		// // Bottom Left Of The Quad (Bottom)
		// gl.glVertex3f(-fNodeWidth, -fNodeHeight, -Z_OFFSET);
		// // Bottom Right Of The Quad (Bottom)
		// gl.glVertex3f(fNodeWidth, -fNodeHeight, -Z_OFFSET);
		//
		// // RIGHT FACE
		// gl.glNormal3f(1.0f, 0.0f, 0.0f);
		// // Top Right Of The Quad (Right)
		// gl.glVertex3f(fNodeWidth, fNodeHeight, -Z_OFFSET);
		// // Top Left Of The Quad (Right)
		// gl.glVertex3f(fNodeWidth, fNodeHeight, Z_OFFSET);
		// // Bottom Left Of The Quad (Right)
		// gl.glVertex3f(fNodeWidth, -fNodeHeight, Z_OFFSET);
		// // Bottom Right Of The Quad (Right)
		// gl.glVertex3f(fNodeWidth, -fNodeHeight, -Z_OFFSET);
		//
		// // LEFT FACE
		// gl.glNormal3f(-1.0f, 0.0f, 0.0f);
		// // Top Right Of The Quad (Left)
		// gl.glVertex3f(-fNodeWidth, fNodeHeight, Z_OFFSET);
		// // Top Left Of The Quad (Left)
		// gl.glVertex3f(-fNodeWidth, fNodeHeight, -Z_OFFSET);
		// // Bottom Left Of The Quad (Left)
		// gl.glVertex3f(-fNodeWidth, -fNodeHeight, -Z_OFFSET);
		// // Bottom Right Of The Quad (Left)
		// gl.glVertex3f(-fNodeWidth, -fNodeHeight, Z_OFFSET);

		gl.glEnd();
	}

	protected void fillNodeDisplayListFrame(final GL gl, final float fNodeWidth, final float fNodeHeight) {
		gl.glLineWidth(3);

		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(-fNodeWidth, fNodeHeight, 0.02f);
		gl.glVertex3f(fNodeWidth, fNodeHeight, 0.02f);
		gl.glVertex3f(fNodeWidth, -fNodeHeight, 0.02f);
		gl.glVertex3f(-fNodeWidth, -fNodeHeight, 0.02f);
		gl.glEnd();
	}

	private void extractVertices(final GL gl, final IUniqueObject containingView,
		PathwayGraph pathwayToExtract) {
		for (IGraphItem vertexRep : pathwayToExtract.getAllItemsByKind(EGraphItemKind.NODE)) {
			if (vertexRep == null) {
				continue;
			}

			createVertex(gl, containingView, (PathwayVertexGraphItemRep) vertexRep, pathwayToExtract);
		}
	}

	private void extractEdges(final GL gl, PathwayGraph pathwayToExtract) {

		Iterator<IGraphItem> edgeIterator =
			pathwayToExtract.getAllItemsByKind(EGraphItemKind.EDGE).iterator();

		IGraphItem edgeRep;

		while (edgeIterator.hasNext()) {
			edgeRep = edgeIterator.next();

			if (edgeRep != null) {
				if (bEnableEdgeRendering) {
					createEdge(gl, edgeRep, pathwayToExtract);
				}
				// Render edge if it is contained in the minimum spanning tree
				// of the neighborhoods
				else if (iArSelectedEdgeRepId.contains(edgeRep.getId())) {
					createEdge(gl, edgeRep, pathwayToExtract);
				}
			}
		}
	}

	private void createVertex(final GL gl, final IUniqueObject containingView,
		PathwayVertexGraphItemRep vertexRep, PathwayGraph containingPathway) {

		float[] tmpNodeColor = null;

		gl.glPushName(generalManager.getViewGLCanvasManager().getPickingManager().getPickingID(
			containingView.getID(), EPickingType.PATHWAY_ELEMENT_SELECTION, vertexRep.getId()));

		EPathwayVertexShape shape = vertexRep.getShapeType();

		if (vertexRep.getAllItemsByProp(EGraphItemProperty.ALIAS_PARENT).toArray().length == 0) {
			generalManager.getLogger().log(
				Level.WARNING,
				"Cannot create pathway vertex. Pathway node representation " + vertexRep.getName()
					+ " has not parent in graph!");
			return;
		}

		EPathwayVertexType vertexType = vertexRep.getType();

		// Pathway link
		if (vertexType.equals(EPathwayVertexType.map)) {
			// Ignore KEGG title node
			if (vertexRep.getName().contains("TITLE")) {
				gl.glPopName();
				return;
			}

			float fCanvasXPos = vertexRep.getXOrigin() * PathwayRenderStyle.SCALING_FACTOR_X;
			float fCanvasYPos = vertexRep.getYOrigin() * PathwayRenderStyle.SCALING_FACTOR_Y;
			float fNodeWidth = vertexRep.getWidth() / 2.0f * PathwayRenderStyle.SCALING_FACTOR_X;
			float fNodeHeight = vertexRep.getHeight() / 2.0f * PathwayRenderStyle.SCALING_FACTOR_Y;

			gl.glTranslatef(fCanvasXPos, -fCanvasYPos, 0);

			tmpNodeColor = new float[] { 0f, 0f, 0f, 0.25f };
			gl.glColor4fv(tmpNodeColor, 0);
			fillNodeDisplayList(gl, fNodeWidth, fNodeHeight);

			// Handle selection highlighting of element
			if (internalSelectionManager.checkStatus(ESelectionType.MOUSE_OVER, vertexRep.getId())) {
				tmpNodeColor = GeneralRenderStyle.MOUSE_OVER_COLOR;
				gl.glColor4fv(tmpNodeColor, 0);
				fillNodeDisplayListFrame(gl, fNodeWidth, fNodeHeight);
			}
			else if (internalSelectionManager.checkStatus(ESelectionType.SELECTION, vertexRep.getId())) {
				tmpNodeColor = GeneralRenderStyle.SELECTED_COLOR;
				gl.glColor4fv(tmpNodeColor, 0);
				fillNodeDisplayListFrame(gl, fNodeWidth, fNodeHeight);
			}

			gl.glTranslatef(-fCanvasXPos, fCanvasYPos, 0);
		}
		// Compound
		else if (vertexType.equals(EPathwayVertexType.compound)) {
			float fCanvasXPos = vertexRep.getXOrigin() * PathwayRenderStyle.SCALING_FACTOR_X;
			float fCanvasYPos = vertexRep.getYOrigin() * PathwayRenderStyle.SCALING_FACTOR_Y;

			gl.glTranslatef(fCanvasXPos, -fCanvasYPos, 0);

			// Handle selection highlighting of element
			if (internalSelectionManager.checkStatus(ESelectionType.MOUSE_OVER, vertexRep.getId())) {
				tmpNodeColor = GeneralRenderStyle.MOUSE_OVER_COLOR;

				gl.glColor4fv(tmpNodeColor, 0);
				gl.glCallList(iHighlightedCompoundNodeDisplayListId);
			}
			else if (internalSelectionManager.checkStatus(ESelectionType.SELECTION, vertexRep.getId())) {
				tmpNodeColor = GeneralRenderStyle.SELECTED_COLOR;

				gl.glColor4fv(tmpNodeColor, 0);
				gl.glCallList(iHighlightedCompoundNodeDisplayListId);
			}

			tmpNodeColor = PathwayRenderStyle.COMPOUND_NODE_COLOR;

			gl.glColor4fv(tmpNodeColor, 0);
			gl.glCallList(iCompoundNodeDisplayListId);

			gl.glTranslatef(-fCanvasXPos, fCanvasYPos, 0);
		}
		else if (shape.equals(EPathwayVertexShape.poly)) // BIOCARTA
		{
			short[][] shArCoords = vertexRep.getCoords();

			gl.glLineWidth(3);
			if (bEnableGeneMapping && glPathwayView.iCurrentStorageIndex != -1) {

				tmpNodeColor = determineNodeColor(vertexRep);
				gl.glLineWidth(4);

				if (tmpNodeColor != null) {
					gl.glColor3fv(tmpNodeColor, 0);

					if (glPathwayView.getDetailLevel() == EDetailLevel.HIGH) {

						gl.glBegin(GL.GL_LINE_STRIP);
						for (int iPointIndex = 0; iPointIndex < shArCoords.length; iPointIndex++) {
							gl.glVertex3f(shArCoords[iPointIndex][0] * PathwayRenderStyle.SCALING_FACTOR_X,
								-shArCoords[iPointIndex][1] * PathwayRenderStyle.SCALING_FACTOR_Y, Z_OFFSET);
						}
						gl.glEnd();

						// Transparent node for picking
						gl.glColor4f(0, 0, 0, 0);
						gl.glBegin(GL.GL_POLYGON);
						for (int iPointIndex = 0; iPointIndex < shArCoords.length; iPointIndex++) {
							gl.glVertex3f(shArCoords[iPointIndex][0] * PathwayRenderStyle.SCALING_FACTOR_X,
								-shArCoords[iPointIndex][1] * PathwayRenderStyle.SCALING_FACTOR_Y, Z_OFFSET);
						}
						gl.glEnd();
					}
					else {
						gl.glBegin(GL.GL_POLYGON);
						for (int iPointIndex = 0; iPointIndex < shArCoords.length; iPointIndex++) {
							gl.glVertex3f(shArCoords[iPointIndex][0] * PathwayRenderStyle.SCALING_FACTOR_X,
								-shArCoords[iPointIndex][1] * PathwayRenderStyle.SCALING_FACTOR_Y, Z_OFFSET);
						}
						gl.glEnd();

						// Handle selection highlighting of element
						if (internalSelectionManager
							.checkStatus(ESelectionType.MOUSE_OVER, vertexRep.getId())) {
							tmpNodeColor = GeneralRenderStyle.MOUSE_OVER_COLOR;
							gl.glLineWidth(3);
							gl.glColor4fv(tmpNodeColor, 0);
							gl.glBegin(GL.GL_LINE_STRIP);
							for (int iPointIndex = 0; iPointIndex < shArCoords.length; iPointIndex++) {
								gl.glVertex3f(shArCoords[iPointIndex][0]
									* PathwayRenderStyle.SCALING_FACTOR_X, -shArCoords[iPointIndex][1]
									* PathwayRenderStyle.SCALING_FACTOR_Y, Z_OFFSET);
							}
							gl.glEnd();
						}
						else if (internalSelectionManager.checkStatus(ESelectionType.SELECTION, vertexRep
							.getId())) {
							tmpNodeColor = GeneralRenderStyle.SELECTED_COLOR;
							gl.glLineWidth(3);
							gl.glColor4fv(tmpNodeColor, 0);
							gl.glBegin(GL.GL_LINE_STRIP);
							for (int iPointIndex = 0; iPointIndex < shArCoords.length; iPointIndex++) {
								gl.glVertex3f(shArCoords[iPointIndex][0]
									* PathwayRenderStyle.SCALING_FACTOR_X, -shArCoords[iPointIndex][1]
									* PathwayRenderStyle.SCALING_FACTOR_Y, Z_OFFSET);
							}
							gl.glEnd();
						}
					}
				}
			}
			else {
				// Handle selection highlighting of element
				if (internalSelectionManager.checkStatus(ESelectionType.MOUSE_OVER, vertexRep.getId())) {
					tmpNodeColor = GeneralRenderStyle.MOUSE_OVER_COLOR;
				}
				else if (internalSelectionManager.checkStatus(ESelectionType.SELECTION, vertexRep.getId())) {
					tmpNodeColor = GeneralRenderStyle.SELECTED_COLOR;
				}
				else if (internalSelectionManager.checkStatus(ESelectionType.NORMAL, vertexRep.getId())) {
					tmpNodeColor = PathwayRenderStyle.ENZYME_NODE_COLOR;
				}

				else {
					tmpNodeColor = new float[] { 0, 0, 0, 0 };
				}

				gl.glColor4fv(tmpNodeColor, 0);
				gl.glLineWidth(3);
				gl.glBegin(GL.GL_LINE_STRIP);
				for (int iPointIndex = 0; iPointIndex < shArCoords.length; iPointIndex++) {
					gl.glVertex3f(shArCoords[iPointIndex][0] * PathwayRenderStyle.SCALING_FACTOR_X,
						-shArCoords[iPointIndex][1] * PathwayRenderStyle.SCALING_FACTOR_Y, Z_OFFSET);
				}
				gl.glEnd();

				if (!internalSelectionManager.checkStatus(ESelectionType.DESELECTED, vertexRep.getId())) {

					// Transparent node for picking
					gl.glColor4f(0, 0, 0, 0);
					gl.glBegin(GL.GL_POLYGON);
					for (int iPointIndex = 0; iPointIndex < shArCoords.length; iPointIndex++) {
						gl.glVertex3f(shArCoords[iPointIndex][0] * PathwayRenderStyle.SCALING_FACTOR_X,
							-shArCoords[iPointIndex][1] * PathwayRenderStyle.SCALING_FACTOR_Y, Z_OFFSET);
					}
					gl.glEnd();
				}
			}
		}
		// Enzyme / Gene
		else if (vertexType.equals(EPathwayVertexType.gene) || vertexType.equals(EPathwayVertexType.enzyme)
		// new kegg data assign enzymes without mapping to "undefined"
			// which we represent as other
			|| vertexType.equals(EPathwayVertexType.other)) {

			float fCanvasXPos = vertexRep.getXOrigin() * PathwayRenderStyle.SCALING_FACTOR_X;
			float fCanvasYPos = vertexRep.getYOrigin() * PathwayRenderStyle.SCALING_FACTOR_Y;

			gl.glTranslatef(fCanvasXPos, -fCanvasYPos, 0);

			gl.glLineWidth(1);
			if (bEnableGeneMapping && glPathwayView.iCurrentStorageIndex != -1) {

				tmpNodeColor = determineNodeColor(vertexRep);

				if (tmpNodeColor != null) {
					gl.glColor3fv(tmpNodeColor, 0);

					if (glPathwayView.getDetailLevel() == EDetailLevel.HIGH) {

						gl.glCallList(iHighlightedEnzymeNodeDisplayListId);

						// Transparent node for picking
						gl.glColor4f(0, 0, 0, 0);
						gl.glCallList(iEnzymeNodeDisplayListId);
					}
					else {
						gl.glCallList(iEnzymeNodeDisplayListId);

						// Handle selection highlighting of element
						if (internalSelectionManager
							.checkStatus(ESelectionType.MOUSE_OVER, vertexRep.getId())) {
							tmpNodeColor = GeneralRenderStyle.MOUSE_OVER_COLOR;
							gl.glColor4fv(tmpNodeColor, 0);
							gl.glCallList(iHighlightedEnzymeNodeDisplayListId);
						}
						else if (internalSelectionManager.checkStatus(ESelectionType.SELECTION, vertexRep
							.getId())) {
							tmpNodeColor = GeneralRenderStyle.SELECTED_COLOR;
							gl.glColor4fv(tmpNodeColor, 0);
							gl.glCallList(iHighlightedEnzymeNodeDisplayListId);
						}
					}
				}
			}
			else {
				// Handle selection highlighting of element
				if (internalSelectionManager.checkStatus(ESelectionType.MOUSE_OVER, vertexRep.getId())) {
					tmpNodeColor = GeneralRenderStyle.MOUSE_OVER_COLOR;
				}
				else if (internalSelectionManager.checkStatus(ESelectionType.SELECTION, vertexRep.getId())) {
					tmpNodeColor = GeneralRenderStyle.SELECTED_COLOR;
				}
				else if (internalSelectionManager.checkStatus(ESelectionType.NORMAL, vertexRep.getId())) {
					tmpNodeColor = PathwayRenderStyle.ENZYME_NODE_COLOR;
				}
				else {
					tmpNodeColor = new float[] { 0, 0, 0, 0 };
				}

				gl.glColor4fv(tmpNodeColor, 0);
				gl.glCallList(iHighlightedEnzymeNodeDisplayListId);

				if (!internalSelectionManager.checkStatus(ESelectionType.DESELECTED, vertexRep.getId())) {

					// Transparent node for picking
					gl.glColor4f(0, 0, 0, 0);
					gl.glCallList(iEnzymeNodeDisplayListId);
				}
			}

			// // Handle selection highlighting of element
			// if (internalSelectionManager.checkStatus(ESelectionType.MOUSE_OVER, iVertexRepID)
			// || internalSelectionManager.checkStatus(ESelectionType.NEIGHBORHOOD_1, iVertexRepID)
			// || internalSelectionManager.checkStatus(ESelectionType.NEIGHBORHOOD_2, iVertexRepID)
			// || internalSelectionManager.checkStatus(ESelectionType.NEIGHBORHOOD_3, iVertexRepID)
			// || internalSelectionManager.checkStatus(ESelectionType.SELECTION, iVertexRepID)) {
			// if (internalSelectionManager.checkStatus(ESelectionType.MOUSE_OVER, iVertexRepID)) {
			// tmpNodeColor = GeneralRenderStyle.MOUSE_OVER_COLOR;
			// }
			// else if (internalSelectionManager.checkStatus(ESelectionType.SELECTION, iVertexRepID)) {
			// tmpNodeColor = GeneralRenderStyle.SELECTED_COLOR;
			// }
			// else if (internalSelectionManager.checkStatus(ESelectionType.NEIGHBORHOOD_1, iVertexRepID)) {
			// gl.glEnable(GL.GL_LINE_STIPPLE);
			// gl.glLineStipple(4, (short) 0xAAAA);
			// tmpNodeColor = renderStyle.getNeighborhoodNodeColorByDepth(1);
			// }
			// else if (internalSelectionManager.checkStatus(ESelectionType.NEIGHBORHOOD_2, iVertexRepID)) {
			// gl.glEnable(GL.GL_LINE_STIPPLE);
			// gl.glLineStipple(2, (short) 0xAAAA);
			// tmpNodeColor = renderStyle.getNeighborhoodNodeColorByDepth(2);
			// }
			// else if (internalSelectionManager.checkStatus(ESelectionType.NEIGHBORHOOD_3, iVertexRepID)) {
			// gl.glEnable(GL.GL_LINE_STIPPLE);
			// gl.glLineStipple(1, (short) 0xAAAA);
			// tmpNodeColor = renderStyle.getNeighborhoodNodeColorByDepth(3);
			// }
			//
			// gl.glColor4fv(tmpNodeColor, 0);
			// gl.glCallList(iHighlightedEnzymeNodeDisplayListId);
			// gl.glDisable(GL.GL_LINE_STIPPLE);
			// }

			gl.glTranslatef(-fCanvasXPos, fCanvasYPos, 0);
		}

		gl.glPopName();
	}

	private void createEdge(final GL gl, IGraphItem edgeRep, PathwayGraph containingPathway) {

		List<IGraphItem> listGraphItemsIn = edgeRep.getAllItemsByProp(EGraphItemProperty.INCOMING);
		List<IGraphItem> listGraphItemsOut = edgeRep.getAllItemsByProp(EGraphItemProperty.OUTGOING);

		if (listGraphItemsIn.isEmpty() || listGraphItemsOut.isEmpty())
			return;

		float[] tmpColor;
		float fReactionLineOffset = 0;

		// Check if edge is a reaction
		if (edgeRep instanceof PathwayReactionEdgeGraphItemRep) {
			tmpColor = PathwayRenderStyle.REACTION_EDGE_COLOR;
			fReactionLineOffset = 0.01f;
		}
		// Check if edge is a relation
		else if (edgeRep instanceof PathwayRelationEdgeGraphItemRep) {
			tmpColor = PathwayRenderStyle.RELATION_EDGE_COLOR;
		}
		else {
			tmpColor = new float[] { 0, 0, 0, 0 };
		}

		gl.glLineWidth(4);
		gl.glColor4fv(tmpColor, 0);
		gl.glBegin(GL.GL_LINES);

		Iterator<IGraphItem> iterSourceGraphItem = listGraphItemsIn.iterator();
		Iterator<IGraphItem> iterTargetGraphItem = listGraphItemsOut.iterator();

		PathwayVertexGraphItemRep tmpSourceGraphItem;
		PathwayVertexGraphItemRep tmpTargetGraphItem;
		while (iterSourceGraphItem.hasNext()) {

			tmpSourceGraphItem = (PathwayVertexGraphItemRep) iterSourceGraphItem.next();

			while (iterTargetGraphItem.hasNext()) {
				tmpTargetGraphItem = (PathwayVertexGraphItemRep) iterTargetGraphItem.next();

				gl.glVertex3f(tmpSourceGraphItem.getXOrigin() * PathwayRenderStyle.SCALING_FACTOR_X
					+ fReactionLineOffset, -tmpSourceGraphItem.getYOrigin()
					* PathwayRenderStyle.SCALING_FACTOR_Y + fReactionLineOffset, 0.02f);
				gl.glVertex3f(tmpTargetGraphItem.getXOrigin() * PathwayRenderStyle.SCALING_FACTOR_X
					+ fReactionLineOffset, -tmpTargetGraphItem.getYOrigin()
					* PathwayRenderStyle.SCALING_FACTOR_Y + fReactionLineOffset, 0.02f);
			}
		}

		gl.glEnd();
	}

	public void renderPathway(final GL gl, final PathwayGraph pathway, boolean bRenderLabels) {
		if (bEnableEdgeRendering || !iArSelectedEdgeRepId.isEmpty()) {
			int iTmpEdgesDisplayListID = hashPathway2EdgesDisplayListId.get(pathway);
			gl.glCallList(iTmpEdgesDisplayListID);
		}

		Integer iTmpVerticesDisplayListID = hashPathway2VerticesDisplayListId.get(pathway);

		if (iTmpVerticesDisplayListID != null) {
			gl.glCallList(iTmpVerticesDisplayListID);

			// if (bRenderLabels && bEnableAnnotation)
			// renderLabels(gl, iPathwayID);
		}
	}

	private float[] determineNodeColor(PathwayVertexGraphItemRep vertexRep) {

		int iDavidID =
			generalManager.getPathwayItemManager().getDavidIdByPathwayVertexGraphItemId(
				vertexRep.getAllItemsByProp(EGraphItemProperty.ALIAS_PARENT).get(0).getId());

		if (iDavidID == -1 || iDavidID == 0) {
			generalManager.getLogger().log(Level.WARNING, "Invalid David Gene ID.");
		}
		else {
			Set<Integer> iSetRefSeq =
				idMappingManager.getMultiID(EMappingType.DAVID_2_REFSEQ_MRNA_INT, iDavidID);

			if (iSetRefSeq == null) {
				generalManager.getLogger().log(Level.SEVERE, "No RefSeq IDs found for David: " + iDavidID);
			}
			else {
				// Check for multiple mapping
				if (iSetRefSeq.size() > 1)
					return new float[] { 0, 1, 1 };

				for (Object iRefSeqID : iSetRefSeq) {

					if (idMappingManager.getMultiID(EMappingType.REFSEQ_MRNA_INT_2_EXPRESSION_INDEX,
						((Integer) iRefSeqID)) == null) {
						break;
					}

					for (Object iExpressionIndex : idMappingManager.getMultiID(
						EMappingType.REFSEQ_MRNA_INT_2_EXPRESSION_INDEX, ((Integer) iRefSeqID))) {

						return colorMapper
							.getColor(glPathwayView.getSet().get(glPathwayView.iCurrentStorageIndex).getFloat(
								EDataRepresentation.NORMALIZED, ((Integer) iExpressionIndex).intValue()));
					}
				}
			}
		}

		// No mapping found
		return null;
	}

	// private void renderLabels(final GL gl, final int iPathwayID)
	// {
	// PathwayVertexGraphItemRep vertexRep;
	// PathwayGraph tmpPathway =
	// generalManager.getPathwayManager().getItem(iPathwayID);
	//
	// // Don't annotate BioCarta pathways - because of good texture annotation
	// if (tmpPathway.getType().equals(EPathwayDatabaseType.BIOCARTA))
	// return;
	//
	// Iterator<IGraphItem> vertexRepIterator = tmpPathway.getAllItemsByKind(
	// EGraphItemKind.NODE).iterator();
	//
	// while (vertexRepIterator.hasNext())
	// {
	// vertexRep = (PathwayVertexGraphItemRep) vertexRepIterator.next();
	//
	// if (vertexRep != null)
	// {
	// float fNodeWidth = vertexRep.getWidth() / 2.0f
	// * PathwayRenderStyle.SCALING_FACTOR_X;
	// float fNodeHeight = vertexRep.getHeight() / 2.0f
	// * PathwayRenderStyle.SCALING_FACTOR_Y;
	// float fCanvasXPos = (vertexRep.getXOrigin() *
	// PathwayRenderStyle.SCALING_FACTOR_X);
	// float fCanvasYPos = (vertexRep.getYOrigin() *
	// PathwayRenderStyle.SCALING_FACTOR_Y);
	//
	// gl.glTranslated(fCanvasXPos - fNodeWidth + 0.01f, -fCanvasYPos - 0.01f,
	// 0);
	// gl.glColor3f(0, 0, 0);
	// GLTextUtils.renderTextInRegion(gl, vertexRep.getName(), 10, 0, 0, 0.03f,
	// fNodeWidth, fNodeHeight);
	// // GLTextUtils.renderText(gl, vertexRep.getName(), 0, 0,
	// // -0.03f);
	// gl.glTranslated(-fCanvasXPos + fNodeWidth - 0.01f, fCanvasYPos + 0.01f,
	// 0);
	// }
	// }
	// }

	// public void mapExpression(final GL gl, final PathwayVertexGraphItemRep
	// pathwayVertexRep,
	// final float fNodeWidth, final float fNodeHeight)
	// {
	// ArrayList<float[]> alMappingColor;
	//
	// // Check if vertex is already mapped
	// if
	// (hashElementId2MappingColorArray.containsKey(pathwayVertexRep.getId()))
	// {
	// // Load existing mapping
	// alMappingColor =
	// hashElementId2MappingColorArray.get(pathwayVertexRep.getId());
	// }
	// else
	// {
	// // Request mapping
	// alMappingColor =
	// genomeMapper.getMappingColorArrayByVertexRep(pathwayVertexRep);
	// hashElementId2MappingColorArray.put(pathwayVertexRep.getId(),
	// alMappingColor);
	// }
	//
	// drawMapping(gl, alMappingColor, fNodeWidth, fNodeHeight, false);
	// }
	//
	// private void drawMapping(final GL gl, final ArrayList<float[]>
	// alMappingColor,
	// final float fNodeWidth, final float fNodeHeight, final boolean
	// bEnableGrid)
	// {
	// int iColumnCount = (int) Math.ceil((float) alMappingColor.size()
	// / (float) iMappingRowCount);
	//
	// float[] tmpNodeColor = null;
	//
	// gl.glPushMatrix();
	//
	// // If no mapping is available - render whole node in one color
	// if (alMappingColor.size() == 1)
	// {
	// tmpNodeColor = alMappingColor.get(0);
	//
	// // Check if the mapping gave back a valid color
	// // if (tmpNodeColor[0] == -1)
	// // tmpNodeColor = renderStyle.getEnzymeNodeColor(true);
	//
	// gl.glColor3fv(tmpNodeColor, 0);
	// gl.glCallList(iEnzymeNodeDisplayListId);
	// }
	// else
	// {
	// gl.glTranslatef(-fNodeWidth + fNodeWidth / iColumnCount, -fNodeHeight
	// + fNodeHeight / iMappingRowCount, 0.0f);
	//
	// for (int iRowIndex = 0; iRowIndex < iMappingRowCount; iRowIndex++)
	// {
	// for (int iColumnIndex = 0; iColumnIndex < iColumnCount; iColumnIndex++)
	// {
	// int iCurrentElement = iRowIndex * iMappingRowCount + iColumnIndex;
	//
	// if (iCurrentElement < alMappingColor.size())
	// tmpNodeColor = alMappingColor.get(iCurrentElement);// (
	// // iRowIndex
	// // +
	// // 1
	// // )
	// // *
	// // iColumnIndex
	// // )
	// // ;
	// else
	// continue;
	//
	// // TODO
	// // Check if the mapping gave back a valid color
	// // if (tmpNodeColor.x() != -1)
	// // {
	// gl.glColor3fv(tmpNodeColor, 0);
	// gl.glScalef(1.0f / iColumnCount, 1.0f / iMappingRowCount, 1.0f);
	// gl.glCallList(iEnzymeNodeDisplayListId);
	// gl.glScalef(iColumnCount, iMappingRowCount, 1.0f);
	// // }
	//
	// gl.glTranslatef(fNodeWidth * 2.0f / iColumnCount, 0.0f, 0.0f);
	// }
	//
	// gl.glTranslatef(-2.0f * fNodeWidth, 2.0f * fNodeHeight /
	// iMappingRowCount,
	// 0.0f);
	// }
	// }
	//
	// gl.glPopMatrix();
	//
	// // Render grid
	// if (bEnableGrid)
	// {
	// gl.glColor3f(1, 1, 1);
	// gl.glBegin(GL.GL_LINE_LOOP);
	// gl.glVertex3f(-fNodeWidth, -fNodeHeight, Z_OFFSET);
	// gl.glVertex3f(fNodeWidth, -fNodeHeight, Z_OFFSET);
	// gl.glVertex3f(fNodeWidth, fNodeHeight, Z_OFFSET);
	// gl.glVertex3f(-fNodeWidth, fNodeHeight, Z_OFFSET);
	// gl.glEnd();
	//
	// gl.glBegin(GL.GL_LINES);
	// for (int iRowIndex = 1; iRowIndex <= iMappingRowCount; iRowIndex++)
	// {
	// gl.glVertex3f(-fNodeWidth, -fNodeHeight + (2 * fNodeHeight /
	// iMappingRowCount)
	// * iRowIndex, Z_OFFSET);
	// gl.glVertex3f(fNodeWidth, -fNodeHeight + (2 * fNodeHeight /
	// iMappingRowCount)
	// * iRowIndex, Z_OFFSET);
	// }
	// for (int iColumnIndex = 1; iColumnIndex <= iColumnCount; iColumnIndex++)
	// {
	// gl.glVertex3f(-fNodeWidth + (2 * fNodeWidth / iColumnCount) *
	// iColumnIndex,
	// fNodeHeight, Z_OFFSET);
	// gl.glVertex3f(-fNodeWidth + (2 * fNodeWidth / iColumnCount) *
	// iColumnIndex,
	// -fNodeHeight, Z_OFFSET);
	// }
	// gl.glEnd();
	// }
	// }

	public void enableEdgeRendering(final boolean bEnableEdgeRendering) {
		this.bEnableEdgeRendering = bEnableEdgeRendering;
	}

	public void enableGeneMapping(final boolean bEnableGeneMappging) {
		this.bEnableGeneMapping = bEnableGeneMappging;
	}

	public void enableIdenticalNodeHighlighting(final boolean bEnableIdenticalNodeHighlighting) {
		this.bEnableIdenticalNodeHighlighting = bEnableIdenticalNodeHighlighting;
	}

	public void enableNeighborhood(final boolean bEnableNeighborhood) {
		this.bEnableNeighborhood = bEnableNeighborhood;
	}

	public void enableAnnotation(final boolean bEnableAnnotation) {
	}

	public void setMappingRowCount(final int iMappingRowCount) {
	}
}
