package org.caleydo.view.pathway;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.media.opengl.GL2;

import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.data.collection.dimension.ADimension;
import org.caleydo.core.data.collection.dimension.DataRepresentation;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.mapping.IDMappingManager;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.util.mapping.color.ColorMapper;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.DetailLevel;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.item.edge.PathwayReactionEdgeGraphItemRep;
import org.caleydo.datadomain.pathway.graph.item.edge.PathwayRelationEdgeGraphItemRep;
import org.caleydo.datadomain.pathway.graph.item.vertex.EPathwayVertexShape;
import org.caleydo.datadomain.pathway.graph.item.vertex.EPathwayVertexType;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexGraphItem;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexGraphItemRep;
import org.caleydo.datadomain.pathway.manager.PathwayItemManager;
import org.caleydo.util.graph.EGraphItemHierarchy;
import org.caleydo.util.graph.EGraphItemKind;
import org.caleydo.util.graph.EGraphItemProperty;
import org.caleydo.util.graph.IGraphItem;
import org.caleydo.util.graph.algorithm.GraphVisitorSearchBFS;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * OpenGL2 pathway manager.
 * 
 * @author Marc Streit
 */
public class GLPathwayContentCreator {
	private GeneralManager generalManager;

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
	// private HashMap<Integer, ArrayList<float[]>>
	// hashElementId2MappingColorArray;

	private ColorMapper colorMapper;

	private SelectionManager internalSelectionManager;

	private ArrayList<Integer> iArSelectedEdgeRepId;

	private IDMappingManager idMappingManager;

	private PathwayItemManager pathwayItemManager;

	private ATableBasedDataDomain geneticDataDomain;
	
	private DataRepresentation dimensionDataRepresentation = DataRepresentation.NORMALIZED;

	/**
	 * Constructor.
	 */
	public GLPathwayContentCreator(ViewFrustum viewFrustum, GLPathway glPathwayView) {

		this.generalManager = GeneralManager.get();
		this.glPathwayView = glPathwayView;

		colorMapper = ColorMappingManager.get().getColorMapping(
				EColorMappingType.GENE_EXPRESSION);

		hashPathway2VerticesDisplayListId = new HashMap<PathwayGraph, Integer>();
		hashPathway2EdgesDisplayListId = new HashMap<PathwayGraph, Integer>();
		// hashElementId2MappingColorArray = new HashMap<Integer,
		// ArrayList<float[]>>();

		iArSelectedEdgeRepId = new ArrayList<Integer>();

		idMappingManager = generalManager.getIDMappingManager();
		pathwayItemManager = PathwayItemManager.get();

		geneticDataDomain = glPathwayView.getMappingDataDomain();
	}

	public void init(final GL2 gl, final SelectionManager internalSelectionManager) {

		buildEnzymeNodeDisplayList(gl);
		buildCompoundNodeDisplayList(gl);
		buildHighlightedEnzymeNodeDisplayList(gl);
		buildHighlightedCompoundNodeDisplayList(gl);

		this.internalSelectionManager = internalSelectionManager;

		// hashElementId2MappingColorArray.clear();

		// if
		// (!generalManager.getIDMappingManager().hasMapping(EIDType.REFSEQ_MRNA_INT,
		// EIDType.EXPRESSION_INDEX)) {
		// bEnableGeneMapping = false;
		// }
	}

	public void buildPathwayDisplayList(final GL2 gl, final IUniqueObject containingView,
			final PathwayGraph pathway) {

		if (pathway == null)
			return;

		int iVerticesDisplayListId = -1;
		int iEdgesDisplayListId = -1;

		if (hashPathway2VerticesDisplayListId.containsKey(pathway)) {
			// Replace current display list if a display list exists
			iVerticesDisplayListId = hashPathway2VerticesDisplayListId.get(pathway);
		} else {
			// Creating vertex display list for pathways
			iVerticesDisplayListId = gl.glGenLists(1);
			hashPathway2VerticesDisplayListId.put(pathway, iVerticesDisplayListId);
		}

		// performIdenticalNodeHighlighting();

		gl.glNewList(iVerticesDisplayListId, GL2.GL_COMPILE);
		extractVertices(gl, containingView, pathway);
		gl.glEndList();

		if (hashPathway2EdgesDisplayListId.containsKey(pathway)) {
			// Replace current display list if a display list exists
			iEdgesDisplayListId = hashPathway2EdgesDisplayListId.get(pathway);
		} else {
			// Creating edge display list for pathways
			iEdgesDisplayListId = gl.glGenLists(1);
			hashPathway2EdgesDisplayListId.put(pathway, iEdgesDisplayListId);
		}

		gl.glNewList(iEdgesDisplayListId, GL2.GL_COMPILE);
		extractEdges(gl, pathway);
		gl.glEndList();
	}

	public void performIdenticalNodeHighlighting(SelectionType selectionType) {
		if (internalSelectionManager == null)
			return;

		iArSelectedEdgeRepId.clear();

		ArrayList<Integer> iAlTmpSelectedGraphItemIds = new ArrayList<Integer>();
		Set<Integer> tmpItemIDs;
		tmpItemIDs = internalSelectionManager.getElements(selectionType);

		if (tmpItemIDs != null) {
			iAlTmpSelectedGraphItemIds.addAll(tmpItemIDs);
		}

		// tmpItemIDs =
		// internalSelectionManager.getElements(SelectionType.SELECTION);
		//
		// if (tmpItemIDs != null)
		// iAlTmpSelectedGraphItemIds.addAll(tmpItemIDs);

		if (iAlTmpSelectedGraphItemIds.size() == 0)
			return;

		// Copy selection IDs to array list object
		for (Integer graphItemID : iAlTmpSelectedGraphItemIds) {
			if (!bEnableIdenticalNodeHighlighting) {
				continue;
			}

			// // Perform identical node highlighting only on nodes with depth 0
			// if (iAlTmpSelectedGraphItemDepth.get(iItemIndex) != 0)
			// continue;

			List<IGraphItem> items = ((IGraphItem) pathwayItemManager
					.getItem(graphItemID))
					.getAllItemsByProp(EGraphItemProperty.ALIAS_PARENT);
			for (IGraphItem graphItem : ((IGraphItem) pathwayItemManager
					.getItem(graphItemID))
					.getAllItemsByProp(EGraphItemProperty.ALIAS_PARENT)) {
				List<IGraphItem> items2 = graphItem
						.getAllItemsByProp(EGraphItemProperty.ALIAS_CHILD);
				for (IGraphItem graphItemRep : graphItem
						.getAllItemsByProp(EGraphItemProperty.ALIAS_CHILD)) {
					if (tmpItemIDs.contains(graphItemRep.getId())) {
						continue;
					}
					internalSelectionManager.addToType(selectionType,
							graphItemRep.getId());

					// for (int iConnectionID : internalSelectionManager
					// .getConnectionForElementID(graphItemID)) {
					// internalSelectionManager.addConnectionID(iConnectionID,
					// graphItemRep.getId());
					// }
					// if (bEnableNeighborhood) {
					// performNeighborhoodAlgorithm(graphItemRep);
					// }
				}
			}
		}
	}

	private void performNeighborhoodAlgorithm(final IGraphItem selectedVertex) {
		GraphVisitorSearchBFS graphVisitorSearchBFS;

		if (bEnableNeighborhood) {
			graphVisitorSearchBFS = new GraphVisitorSearchBFS(selectedVertex, 4);
		} else {
			graphVisitorSearchBFS = new GraphVisitorSearchBFS(selectedVertex, 0);
		}

		graphVisitorSearchBFS.setProp(EGraphItemProperty.OUTGOING);
		graphVisitorSearchBFS.setGraph(selectedVertex.getAllGraphByType(
				EGraphItemHierarchy.GRAPH_PARENT).get(0));

		// List<IGraphItem> lGraphItems =
		// graphVisitorSearchBFS.getSearchResult();
		graphVisitorSearchBFS.getSearchResult();

		List<List<IGraphItem>> lDepthSearchResult = graphVisitorSearchBFS
				.getSearchResultDepthOrdered();
		List<IGraphItem> lGraphItems = new ArrayList<IGraphItem>();
		//
		// int iTmpDepth = 0;
		// SelectionType tmpType;

		for (int iDepthIndex = 0; iDepthIndex < lDepthSearchResult.size(); iDepthIndex++) {
			lGraphItems = lDepthSearchResult.get(iDepthIndex);

			for (int iItemIndex = 0; iItemIndex < lGraphItems.size(); iItemIndex++) {
				// Check if selected item is a vertex
				if (lGraphItems.get(iItemIndex) instanceof PathwayVertexGraphItemRep) {
					// iTmpDepth = (iDepthIndex + 1) / 2;

					// FIXME - this needs to be adapted to the new selection
					// types when re-activating the neighborhoods
					// if (iTmpDepth == 1) {
					// tmpType = SelectionType.NEIGHBORHOOD_1;
					// } else if (iTmpDepth == 2) {
					// tmpType = SelectionType.NEIGHBORHOOD_2;
					// } else if (iTmpDepth == 3) {
					// tmpType = SelectionType.NEIGHBORHOOD_3;
					// } else
					// throw new IllegalStateException(
					// "Neighborhood depth greater than 3 is not supported!");
					//
					// internalSelectionManager.addToType(tmpType, lGraphItems
					// .get(iItemIndex).getId());

				} else {
					iArSelectedEdgeRepId.add(lGraphItems.get(iItemIndex).getId());
				}
			}
		}
	}

	private void buildEnzymeNodeDisplayList(final GL2 gl) {
		// Creating display list for node cube objects
		iEnzymeNodeDisplayListId = gl.glGenLists(1);

		float fNodeWidth = PathwayRenderStyle.ENZYME_NODE_WIDTH;
		float fNodeHeight = PathwayRenderStyle.ENZYME_NODE_HEIGHT;

		gl.glNewList(iEnzymeNodeDisplayListId, GL2.GL_COMPILE);
		fillNodeDisplayList(gl, fNodeWidth, fNodeHeight);
		gl.glEndList();
	}

	protected void buildHighlightedEnzymeNodeDisplayList(final GL2 gl) {
		// Creating display list for node cube objects
		iHighlightedEnzymeNodeDisplayListId = gl.glGenLists(1);

		float fNodeWidth = PathwayRenderStyle.ENZYME_NODE_WIDTH;
		float fNodeHeight = PathwayRenderStyle.ENZYME_NODE_HEIGHT;

		gl.glNewList(iHighlightedEnzymeNodeDisplayListId, GL2.GL_COMPILE);
		fillNodeDisplayListFrame(gl, fNodeWidth, fNodeHeight);
		gl.glEndList();
	}

	protected void buildCompoundNodeDisplayList(final GL2 gl) {
		// Creating display list for node cube objects
		iCompoundNodeDisplayListId = gl.glGenLists(1);

		float fNodeWidth = PathwayRenderStyle.COMPOUND_NODE_WIDTH;
		float fNodeHeight = PathwayRenderStyle.COMPOUND_NODE_HEIGHT;

		gl.glNewList(iCompoundNodeDisplayListId, GL2.GL_COMPILE);
		fillNodeDisplayList(gl, fNodeWidth, fNodeHeight);
		gl.glEndList();
	}

	protected void buildHighlightedCompoundNodeDisplayList(final GL2 gl) {
		// Creating display list for node cube objects
		iHighlightedCompoundNodeDisplayListId = gl.glGenLists(1);

		float fNodeWidth = PathwayRenderStyle.COMPOUND_NODE_WIDTH;
		float fNodeHeight = PathwayRenderStyle.COMPOUND_NODE_HEIGHT;

		gl.glNewList(iHighlightedCompoundNodeDisplayListId, GL2.GL_COMPILE);
		fillNodeDisplayListFrame(gl, fNodeWidth, fNodeHeight);
		gl.glEndList();
	}

	private void fillNodeDisplayList(final GL2 gl, final float fNodeWidth,
			final float fNodeHeight) {

		gl.glBegin(GL2.GL_QUADS);

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

	protected void fillNodeDisplayListFrame(final GL2 gl, final float fNodeWidth,
			final float fNodeHeight) {
		gl.glLineWidth(3);

		gl.glBegin(GL2.GL_LINE_LOOP);
		gl.glVertex3f(-fNodeWidth, fNodeHeight, 0.02f);
		gl.glVertex3f(fNodeWidth, fNodeHeight, 0.02f);
		gl.glVertex3f(fNodeWidth, -fNodeHeight, 0.02f);
		gl.glVertex3f(-fNodeWidth, -fNodeHeight, 0.02f);
		gl.glEnd();
	}

	private void extractVertices(final GL2 gl, final IUniqueObject containingView,
			PathwayGraph pathwayToExtract) {
		for (IGraphItem vertexRep : pathwayToExtract
				.getAllItemsByKind(EGraphItemKind.NODE)) {
			if (vertexRep == null) {
				continue;
			}

			createVertex(gl, containingView, (PathwayVertexGraphItemRep) vertexRep,
					pathwayToExtract);
		}
	}

	private void extractEdges(final GL2 gl, PathwayGraph pathwayToExtract) {

		Iterator<IGraphItem> edgeIterator = pathwayToExtract.getAllItemsByKind(
				EGraphItemKind.EDGE).iterator();

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

	private void createVertex(final GL2 gl, final IUniqueObject containingView,
			PathwayVertexGraphItemRep vertexRep, PathwayGraph containingPathway) {

		float[] tmpNodeColor = null;

		gl.glPushName(generalManager
				.getViewManager()
				.getPickingManager()
				.getPickingID(containingView.getID(),
						PickingType.PATHWAY_ELEMENT_SELECTION, vertexRep.getId()));

		EPathwayVertexShape shape = vertexRep.getShapeType();

		if (vertexRep.getAllItemsByProp(EGraphItemProperty.ALIAS_PARENT).toArray().length == 0) {
			Logger.log(new Status(IStatus.WARNING, this.toString(),
					"Cannot create pathway vertex. Pathway node representation "
							+ vertexRep.getName() + " has not parent in graph!"));
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

			float fCanvasXPos = vertexRep.getXOrigin()
					* PathwayRenderStyle.SCALING_FACTOR_X;
			float fCanvasYPos = vertexRep.getYOrigin()
					* PathwayRenderStyle.SCALING_FACTOR_Y;
			float fNodeWidth = vertexRep.getWidth() / 2.0f
					* PathwayRenderStyle.SCALING_FACTOR_X;
			float fNodeHeight = vertexRep.getHeight() / 2.0f
					* PathwayRenderStyle.SCALING_FACTOR_Y;

			gl.glTranslatef(fCanvasXPos, -fCanvasYPos, 0);

			tmpNodeColor = new float[] { 0f, 0f, 0f, 0.25f };
			gl.glColor4fv(tmpNodeColor, 0);
			fillNodeDisplayList(gl, fNodeWidth, fNodeHeight);

			// Handle selection highlighting of element

			if (internalSelectionManager.checkStatus(SelectionType.SELECTION,
					vertexRep.getId())) {
				tmpNodeColor = SelectionType.SELECTION.getColor();
				gl.glColor4fv(tmpNodeColor, 0);
				fillNodeDisplayListFrame(gl, fNodeWidth, fNodeHeight);
			} else if (internalSelectionManager.checkStatus(SelectionType.MOUSE_OVER,
					vertexRep.getId())) {
				tmpNodeColor = SelectionType.MOUSE_OVER.getColor();
				gl.glColor4fv(tmpNodeColor, 0);
				fillNodeDisplayListFrame(gl, fNodeWidth, fNodeHeight);
			}

			gl.glTranslatef(-fCanvasXPos, fCanvasYPos, 0);
		}
		// Compound
		else if (vertexType.equals(EPathwayVertexType.compound)) {
			float fCanvasXPos = vertexRep.getXOrigin()
					* PathwayRenderStyle.SCALING_FACTOR_X;
			float fCanvasYPos = vertexRep.getYOrigin()
					* PathwayRenderStyle.SCALING_FACTOR_Y;

			gl.glTranslatef(fCanvasXPos, -fCanvasYPos, 0);

			// Handle selection highlighting of element
			if (internalSelectionManager.checkStatus(SelectionType.SELECTION,
					vertexRep.getId())) {
				tmpNodeColor = SelectionType.SELECTION.getColor();

				gl.glColor4fv(tmpNodeColor, 0);
				gl.glCallList(iHighlightedCompoundNodeDisplayListId);
			} else if (internalSelectionManager.checkStatus(SelectionType.MOUSE_OVER,
					vertexRep.getId())) {
				tmpNodeColor = SelectionType.MOUSE_OVER.getColor();

				gl.glColor4fv(tmpNodeColor, 0);
				gl.glCallList(iHighlightedCompoundNodeDisplayListId);
			}

			tmpNodeColor = PathwayRenderStyle.COMPOUND_NODE_COLOR;

			gl.glColor4fv(tmpNodeColor, 0);
			gl.glCallList(iCompoundNodeDisplayListId);

			gl.glTranslatef(-fCanvasXPos, fCanvasYPos, 0);
		} else if (shape.equals(EPathwayVertexShape.poly)) // BIOCARTA
		{
			short[][] shArCoords = vertexRep.getCoords();

			gl.glLineWidth(3);
			if (bEnableGeneMapping && glPathwayView.iCurrentDimensionIndex != -1) {

				tmpNodeColor = determineNodeColor(vertexRep);
				gl.glLineWidth(4);

				if (tmpNodeColor != null) {
					gl.glColor3fv(tmpNodeColor, 0);

					if (glPathwayView.getDetailLevel() == DetailLevel.HIGH) {

						gl.glBegin(GL2.GL_LINE_STRIP);
						for (int iPointIndex = 0; iPointIndex < shArCoords.length; iPointIndex++) {
							gl.glVertex3f(shArCoords[iPointIndex][0]
									* PathwayRenderStyle.SCALING_FACTOR_X,
									-shArCoords[iPointIndex][1]
											* PathwayRenderStyle.SCALING_FACTOR_Y,
									Z_OFFSET);
						}
						gl.glEnd();

						// Transparent node for picking
						gl.glColor4f(0, 0, 0, 0);
						gl.glBegin(GL2.GL_POLYGON);
						for (int iPointIndex = 0; iPointIndex < shArCoords.length; iPointIndex++) {
							gl.glVertex3f(shArCoords[iPointIndex][0]
									* PathwayRenderStyle.SCALING_FACTOR_X,
									-shArCoords[iPointIndex][1]
											* PathwayRenderStyle.SCALING_FACTOR_Y,
									Z_OFFSET);
						}
						gl.glEnd();
					} else {
						gl.glBegin(GL2.GL_POLYGON);
						for (int iPointIndex = 0; iPointIndex < shArCoords.length; iPointIndex++) {
							gl.glVertex3f(shArCoords[iPointIndex][0]
									* PathwayRenderStyle.SCALING_FACTOR_X,
									-shArCoords[iPointIndex][1]
											* PathwayRenderStyle.SCALING_FACTOR_Y,
									Z_OFFSET);
						}
						gl.glEnd();

						// Handle selection highlighting of element
						if (internalSelectionManager.checkStatus(SelectionType.SELECTION,
								vertexRep.getId())) {
							tmpNodeColor = SelectionType.SELECTION.getColor();
							gl.glLineWidth(3);
							gl.glColor4fv(tmpNodeColor, 0);
							gl.glBegin(GL2.GL_LINE_STRIP);
							for (int iPointIndex = 0; iPointIndex < shArCoords.length; iPointIndex++) {
								gl.glVertex3f(shArCoords[iPointIndex][0]
										* PathwayRenderStyle.SCALING_FACTOR_X,
										-shArCoords[iPointIndex][1]
												* PathwayRenderStyle.SCALING_FACTOR_Y,
										Z_OFFSET);
							}
							gl.glEnd();
						} else if (internalSelectionManager.checkStatus(
								SelectionType.MOUSE_OVER, vertexRep.getId())) {
							tmpNodeColor = SelectionType.MOUSE_OVER.getColor();
							gl.glLineWidth(3);
							gl.glColor4fv(tmpNodeColor, 0);
							gl.glBegin(GL2.GL_LINE_STRIP);
							for (int iPointIndex = 0; iPointIndex < shArCoords.length; iPointIndex++) {
								gl.glVertex3f(shArCoords[iPointIndex][0]
										* PathwayRenderStyle.SCALING_FACTOR_X,
										-shArCoords[iPointIndex][1]
												* PathwayRenderStyle.SCALING_FACTOR_Y,
										Z_OFFSET);
							}
							gl.glEnd();
						}
					}
				}
			} else {
				// Handle selection highlighting of element
				if (internalSelectionManager.checkStatus(SelectionType.SELECTION,
						vertexRep.getId())) {
					tmpNodeColor = SelectionType.SELECTION.getColor();
				} else if (internalSelectionManager.checkStatus(SelectionType.MOUSE_OVER,
						vertexRep.getId())) {
					tmpNodeColor = SelectionType.MOUSE_OVER.getColor();
				}
				// else if (internalSelectionManager.checkStatus(
				// SelectionType.NORMAL, vertexRep.getId())) {
				// tmpNodeColor = PathwayRenderStyle.ENZYME_NODE_COLOR;
				// }
				else {
					tmpNodeColor = PathwayRenderStyle.ENZYME_NODE_COLOR;
					// tmpNodeColor = new float[] { 0, 0, 0, 0 };
				}

				gl.glColor4fv(tmpNodeColor, 0);
				gl.glLineWidth(3);
				gl.glBegin(GL2.GL_LINE_STRIP);
				for (int iPointIndex = 0; iPointIndex < shArCoords.length; iPointIndex++) {
					gl.glVertex3f(shArCoords[iPointIndex][0]
							* PathwayRenderStyle.SCALING_FACTOR_X,
							-shArCoords[iPointIndex][1]
									* PathwayRenderStyle.SCALING_FACTOR_Y, Z_OFFSET);
				}
				gl.glEnd();

				if (!internalSelectionManager.checkStatus(SelectionType.DESELECTED,
						vertexRep.getId())) {

					// Transparent node for picking
					gl.glColor4f(0, 0, 0, 0);
					gl.glBegin(GL2.GL_POLYGON);
					for (int iPointIndex = 0; iPointIndex < shArCoords.length; iPointIndex++) {
						gl.glVertex3f(shArCoords[iPointIndex][0]
								* PathwayRenderStyle.SCALING_FACTOR_X,
								-shArCoords[iPointIndex][1]
										* PathwayRenderStyle.SCALING_FACTOR_Y, Z_OFFSET);
					}
					gl.glEnd();
				}
			}
		}
		// Enzyme / Gene
		else if (vertexType.equals(EPathwayVertexType.gene)
				|| vertexType.equals(EPathwayVertexType.enzyme)
				// new kegg data assign enzymes without mapping to "undefined"
				// which we represent as other
				|| vertexType.equals(EPathwayVertexType.other)) {

			float fCanvasXPos = vertexRep.getXOrigin()
					* PathwayRenderStyle.SCALING_FACTOR_X;
			float fCanvasYPos = vertexRep.getYOrigin()
					* PathwayRenderStyle.SCALING_FACTOR_Y;

			gl.glTranslatef(fCanvasXPos, -fCanvasYPos, 0);

			gl.glLineWidth(1);
			if (bEnableGeneMapping && glPathwayView.iCurrentDimensionIndex != -1) {

				tmpNodeColor = determineNodeColor(vertexRep);

				if (tmpNodeColor != null) {
					gl.glColor3fv(tmpNodeColor, 0);

					if (glPathwayView.getDetailLevel() == DetailLevel.HIGH) {

						gl.glCallList(iHighlightedEnzymeNodeDisplayListId);

						// Transparent node for picking
						gl.glColor4f(0, 0, 0, 0);
						gl.glCallList(iEnzymeNodeDisplayListId);
					} else {
						gl.glCallList(iEnzymeNodeDisplayListId);

						// Handle selection highlighting of element
						if (internalSelectionManager.checkStatus(SelectionType.SELECTION,
								vertexRep.getId())) {
							tmpNodeColor = SelectionType.SELECTION.getColor();
							gl.glColor4fv(tmpNodeColor, 0);
							gl.glCallList(iHighlightedEnzymeNodeDisplayListId);
						} else if (internalSelectionManager.checkStatus(
								SelectionType.MOUSE_OVER, vertexRep.getId())) {
							tmpNodeColor = SelectionType.MOUSE_OVER.getColor();
							gl.glColor4fv(tmpNodeColor, 0);
							gl.glCallList(iHighlightedEnzymeNodeDisplayListId);
						}
					}
				}
			} else {
				// Handle selection highlighting of element
				if (internalSelectionManager.checkStatus(SelectionType.SELECTION,
						vertexRep.getId())) {
					tmpNodeColor = SelectionType.SELECTION.getColor();
				} else if (internalSelectionManager.checkStatus(SelectionType.MOUSE_OVER,
						vertexRep.getId())) {
					tmpNodeColor = SelectionType.MOUSE_OVER.getColor();
				} else if (internalSelectionManager.checkStatus(SelectionType.NORMAL,
						vertexRep.getId())) {
					tmpNodeColor = PathwayRenderStyle.ENZYME_NODE_COLOR;
				} else {
					tmpNodeColor = new float[] { 0, 0, 0, 0 };
				}

				gl.glColor4fv(tmpNodeColor, 0);
				gl.glCallList(iHighlightedEnzymeNodeDisplayListId);

				if (!internalSelectionManager.checkStatus(SelectionType.DESELECTED,
						vertexRep.getId())) {

					// Transparent node for picking
					gl.glColor4f(0, 0, 0, 0);
					gl.glCallList(iEnzymeNodeDisplayListId);
				}
			}

			// // Handle selection highlighting of element
			// if
			// (internalSelectionManager.checkStatus(SelectionType.MOUSE_OVER,
			// iVertexRepID)
			// ||
			// internalSelectionManager.checkStatus(SelectionType.NEIGHBORHOOD_1,
			// iVertexRepID)
			// ||
			// internalSelectionManager.checkStatus(SelectionType.NEIGHBORHOOD_2,
			// iVertexRepID)
			// ||
			// internalSelectionManager.checkStatus(SelectionType.NEIGHBORHOOD_3,
			// iVertexRepID)
			// || internalSelectionManager.checkStatus(SelectionType.SELECTION,
			// iVertexRepID)) {
			// if
			// (internalSelectionManager.checkStatus(SelectionType.MOUSE_OVER,
			// iVertexRepID)) {
			// tmpNodeColor = GeneralRenderStyle.MOUSE_OVER_COLOR;
			// }
			// else if
			// (internalSelectionManager.checkStatus(SelectionType.SELECTION,
			// iVertexRepID)) {
			// tmpNodeColor = GeneralRenderStyle.SELECTED_COLOR;
			// }
			// else if
			// (internalSelectionManager.checkStatus(SelectionType.NEIGHBORHOOD_1,
			// iVertexRepID)) {
			// gl.glEnable(GL2.GL_LINE_STIPPLE);
			// gl.glLineStipple(4, (short) 0xAAAA);
			// tmpNodeColor = renderStyle.getNeighborhoodNodeColorByDepth(1);
			// }
			// else if
			// (internalSelectionManager.checkStatus(SelectionType.NEIGHBORHOOD_2,
			// iVertexRepID)) {
			// gl.glEnable(GL2.GL_LINE_STIPPLE);
			// gl.glLineStipple(2, (short) 0xAAAA);
			// tmpNodeColor = renderStyle.getNeighborhoodNodeColorByDepth(2);
			// }
			// else if
			// (internalSelectionManager.checkStatus(SelectionType.NEIGHBORHOOD_3,
			// iVertexRepID)) {
			// gl.glEnable(GL2.GL_LINE_STIPPLE);
			// gl.glLineStipple(1, (short) 0xAAAA);
			// tmpNodeColor = renderStyle.getNeighborhoodNodeColorByDepth(3);
			// }
			//
			// gl.glColor4fv(tmpNodeColor, 0);
			// gl.glCallList(iHighlightedEnzymeNodeDisplayListId);
			// gl.glDisable(GL2.GL_LINE_STIPPLE);
			// }

			gl.glTranslatef(-fCanvasXPos, fCanvasYPos, 0);
		}

		gl.glPopName();
	}

	private void createEdge(final GL2 gl, IGraphItem edgeRep,
			PathwayGraph containingPathway) {

		List<IGraphItem> listGraphItemsIn = edgeRep
				.getAllItemsByProp(EGraphItemProperty.INCOMING);
		List<IGraphItem> listGraphItemsOut = edgeRep
				.getAllItemsByProp(EGraphItemProperty.OUTGOING);

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
		} else {
			tmpColor = new float[] { 0, 0, 0, 0 };
		}

		gl.glLineWidth(4);
		gl.glColor4fv(tmpColor, 0);
		gl.glBegin(GL2.GL_LINES);

		Iterator<IGraphItem> iterSourceGraphItem = listGraphItemsIn.iterator();
		Iterator<IGraphItem> iterTargetGraphItem = listGraphItemsOut.iterator();

		PathwayVertexGraphItemRep tmpSourceGraphItem;
		PathwayVertexGraphItemRep tmpTargetGraphItem;
		while (iterSourceGraphItem.hasNext()) {

			tmpSourceGraphItem = (PathwayVertexGraphItemRep) iterSourceGraphItem.next();

			while (iterTargetGraphItem.hasNext()) {
				tmpTargetGraphItem = (PathwayVertexGraphItemRep) iterTargetGraphItem
						.next();

				gl.glVertex3f(tmpSourceGraphItem.getXOrigin()
						* PathwayRenderStyle.SCALING_FACTOR_X + fReactionLineOffset,
						-tmpSourceGraphItem.getYOrigin()
								* PathwayRenderStyle.SCALING_FACTOR_Y
								+ fReactionLineOffset, 0.02f);
				gl.glVertex3f(tmpTargetGraphItem.getXOrigin()
						* PathwayRenderStyle.SCALING_FACTOR_X + fReactionLineOffset,
						-tmpTargetGraphItem.getYOrigin()
								* PathwayRenderStyle.SCALING_FACTOR_Y
								+ fReactionLineOffset, 0.02f);
			}
		}

		gl.glEnd();
	}

	public void renderPathway(final GL2 gl, final PathwayGraph pathway,
			boolean bRenderLabels) {
		if (bEnableEdgeRendering || !iArSelectedEdgeRepId.isEmpty()) {
			int iTmpEdgesDisplayListID = hashPathway2EdgesDisplayListId.get(pathway);
			gl.glCallList(iTmpEdgesDisplayListID);
		}

		Integer iTmpVerticesDisplayListID = hashPathway2VerticesDisplayListId
				.get(pathway);

		if (iTmpVerticesDisplayListID != null) {
			gl.glCallList(iTmpVerticesDisplayListID);

			// if (bRenderLabels && bEnableAnnotation)
			// renderLabels(gl, iPathwayID);
		}
	}

	private float[] determineNodeColor(PathwayVertexGraphItemRep vertexRep) {

		int iDavidID = pathwayItemManager
				.getDavidIdByPathwayVertexGraphItem((PathwayVertexGraphItem) vertexRep
						.getAllItemsByProp(EGraphItemProperty.ALIAS_PARENT).get(0));

		if (iDavidID == -1 || iDavidID == 0)
			return null;
		else {

			Set<Integer> ids = idMappingManager.<Integer, Integer> getIDAsSet(
					glPathwayView.getDataDomain().getDavidIDType(), glPathwayView
							.getMappingDataDomain().getRecordIDType(), iDavidID);
			if (ids == null)
				return null;
			for (Integer iExpressionIndex : ids) {

				ADimension dimension = geneticDataDomain.getTable().get(
						glPathwayView.iCurrentDimensionIndex);
				if (dimension == null)
					throw new IllegalStateException("No dimension in this set with id: "
							+ glPathwayView.iCurrentDimensionIndex);
				float expressionValue = dimension.getFloat(dimensionDataRepresentation,
						iExpressionIndex);

				return colorMapper.getColor(expressionValue);

			}
		}

		return null;
	}

	public void enableEdgeRendering(final boolean bEnableEdgeRendering) {
		this.bEnableEdgeRendering = bEnableEdgeRendering;
	}

	public void enableGeneMapping(final boolean bEnableGeneMappging) {
		this.bEnableGeneMapping = bEnableGeneMappging;
	}

	public void enableIdenticalNodeHighlighting(
			final boolean bEnableIdenticalNodeHighlighting) {
		this.bEnableIdenticalNodeHighlighting = bEnableIdenticalNodeHighlighting;
	}

	public void enableNeighborhood(final boolean bEnableNeighborhood) {
		this.bEnableNeighborhood = bEnableNeighborhood;
	}

	public void enableAnnotation(final boolean bEnableAnnotation) {
	}

	public void setMappingRowCount(final int iMappingRowCount) {
	}
	
	public void switchDataRepresentation() {
		if (dimensionDataRepresentation.equals(DataRepresentation.NORMALIZED)) {
			if (!geneticDataDomain.getTable().containsFoldChangeRepresentation())
				geneticDataDomain.getTable().createFoldChangeRepresentation();
			dimensionDataRepresentation = DataRepresentation.FOLD_CHANGE_NORMALIZED;
		}
		else
			dimensionDataRepresentation = DataRepresentation.NORMALIZED;
	}
}
