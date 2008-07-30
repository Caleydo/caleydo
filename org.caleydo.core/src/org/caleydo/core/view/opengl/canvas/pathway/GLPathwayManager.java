package org.caleydo.core.view.opengl.canvas.pathway;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;

import javax.media.opengl.GL;

import org.caleydo.core.data.AManagedObject;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.data.graph.pathway.item.edge.PathwayReactionEdgeGraphItemRep;
import org.caleydo.core.data.graph.pathway.item.edge.PathwayRelationEdgeGraphItemRep;
import org.caleydo.core.data.graph.pathway.item.vertex.EPathwayVertexShape;
import org.caleydo.core.data.graph.pathway.item.vertex.EPathwayVertexType;
import org.caleydo.core.data.graph.pathway.item.vertex.PathwayVertexGraphItem;
import org.caleydo.core.data.graph.pathway.item.vertex.PathwayVertexGraphItemRep;
import org.caleydo.core.data.view.rep.renderstyle.PathwayRenderStyle;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.specialized.genome.pathway.EPathwayDatabaseType;
import org.caleydo.core.manager.view.EPickingType;
import org.caleydo.core.util.mapping.GenomeColorMapper;
import org.caleydo.core.view.opengl.util.GLTextUtils;
import org.caleydo.core.view.opengl.util.selection.EViewInternalSelectionType;
import org.caleydo.core.view.opengl.util.selection.GenericSelectionManager;
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
public class GLPathwayManager
{

	private IGeneralManager generalManager;

	public static final float SCALING_FACTOR_X = 0.0025f;

	public static final float SCALING_FACTOR_Y = 0.0025f;

	private static final float Z_OFFSET = 0.0001f;

	private int iEnzymeNodeDisplayListId = -1;

	private int iCompoundNodeDisplayListId = -1;

	private int iHighlightedEnzymeNodeDisplayListId = -1;

	private int iHighlightedCompoundNodeDisplayListId = -1;

	private PathwayRenderStyle renderStyle;

	private boolean bEnableGeneMapping = true;

	private boolean bEnableEdgeRendering = false;

	private boolean bEnableIdenticalNodeHighlighting = true;

	private boolean bEnableNeighborhood = false;

	private boolean bEnableAnnotation = true;

	private HashMap<Integer, Integer> hashPathwayId2VerticesDisplayListId;

	private HashMap<Integer, Integer> hashPathwayId2EdgesDisplayListId;

	private GenomeColorMapper genomeMapper;

	private GenericSelectionManager internalSelectionManager;

	private HashMap<Integer, Integer> hashSelectedVertexRepId2Depth;

	private ArrayList<Integer> iArSelectedEdgeRepId;

	private HashMap<Integer, ArrayList<Vec3f>> hashElementId2MappingColorArray;

	private int iMappingRowCount = 1;

	/**
	 * Constructor.
	 */
	public GLPathwayManager(final IGeneralManager generalManager)
	{

		this.generalManager = generalManager;

		renderStyle = new PathwayRenderStyle();
		hashPathwayId2VerticesDisplayListId = new HashMap<Integer, Integer>();
		hashPathwayId2EdgesDisplayListId = new HashMap<Integer, Integer>();
		hashElementId2MappingColorArray = new HashMap<Integer, ArrayList<Vec3f>>();

		hashSelectedVertexRepId2Depth = new HashMap<Integer, Integer>();
		iArSelectedEdgeRepId = new ArrayList<Integer>();
	}

	public void init(final GL gl, final ArrayList<ISet> alSetData,
			final GenericSelectionManager internalSelectionManager)
	{

		buildEnzymeNodeDisplayList(gl);
		buildCompoundNodeDisplayList(gl);
		buildHighlightedEnzymeNodeDisplayList(gl);
		buildHighlightedCompoundNodeDisplayList(gl);

		this.internalSelectionManager = internalSelectionManager;

		// Initialize genome mapper
		// TODO: move to a manager because more classes use the genome mapper
		// maybe GenomeIdManager is the right place
		genomeMapper = new GenomeColorMapper(generalManager);
		genomeMapper.setMappingData(alSetData);
	}

	public void buildPathwayDisplayList(final GL gl, final AManagedObject containingView,
			final int iPathwayId)
	{

		generalManager.getLogger().log(Level.INFO,
				"Build display list for pathway " + iPathwayId);

		if (iPathwayId == -1)
			return;

		PathwayGraph tmpPathway = (PathwayGraph) generalManager.getPathwayManager().getItem(
				iPathwayId);

		int iVerticesDisplayListId = -1;
		int iEdgesDisplayListId = -1;

		if (hashPathwayId2VerticesDisplayListId.containsKey(iPathwayId))
		{
			// Replace current display list if a display list exists
			iVerticesDisplayListId = hashPathwayId2VerticesDisplayListId.get(iPathwayId);
		}
		else
		{
			// Creating vertex display list for pathways
			iVerticesDisplayListId = gl.glGenLists(1);
			hashPathwayId2VerticesDisplayListId.put(iPathwayId, iVerticesDisplayListId);
		}

		// performIdenticalNodeHighlighting();

		gl.glNewList(iVerticesDisplayListId, GL.GL_COMPILE);
		extractVertices(gl, containingView, tmpPathway);
		gl.glEndList();

		if (hashPathwayId2EdgesDisplayListId.containsKey(iPathwayId))
		{
			// Replace current display list if a display list exists
			iEdgesDisplayListId = hashPathwayId2EdgesDisplayListId.get(iPathwayId);
		}
		else
		{
			// Creating edge display list for pathways
			iEdgesDisplayListId = gl.glGenLists(1);
			hashPathwayId2EdgesDisplayListId.put(iPathwayId, iEdgesDisplayListId);
		}

		gl.glNewList(iEdgesDisplayListId, GL.GL_COMPILE);
		extractEdges(gl, tmpPathway);
		gl.glEndList();
	}

	public void performIdenticalNodeHighlighting()
	{

		if (internalSelectionManager == null)
			return;

		hashSelectedVertexRepId2Depth.clear();
		iArSelectedEdgeRepId.clear();

		ArrayList<Integer> iAlTmpSelectedGraphItemIds = new ArrayList<Integer>();
		iAlTmpSelectedGraphItemIds.addAll(internalSelectionManager
				.getElements(EViewInternalSelectionType.MOUSE_OVER));

		// ArrayList<Integer> iAlTmpSelectedGraphItemDepth =
		// alSetSelection.get(1).getGroupArray();

		if (iAlTmpSelectedGraphItemIds.size() == 0)
			return;

		// Copy selection IDs to array list object
		for (int iItemIndex = 0; iItemIndex < iAlTmpSelectedGraphItemIds.size(); iItemIndex++)
		{
			// // Check if ID is valid
			// if (iAlTmpSelectedGraphItemIds.get(iItemIndex) == 0)
			// continue;

			hashSelectedVertexRepId2Depth.put(iAlTmpSelectedGraphItemIds.get(iItemIndex), 0);// iAlTmpSelectedGraphItemDepth
																								// .
																								// get
																								// (
			// iItemIndex));

			if (!bEnableIdenticalNodeHighlighting)
				continue;

			// // Perform identical node highlighting only on nodes with depth 0
			// if (iAlTmpSelectedGraphItemDepth.get(iItemIndex) != 0)
			// continue;

			Iterator<IGraphItem> iterGraphItems = ((IGraphItem) generalManager
					.getPathwayItemManager().getItem(
							iAlTmpSelectedGraphItemIds.get(iItemIndex))).getAllItemsByProp(
					EGraphItemProperty.ALIAS_PARENT).iterator();
			Iterator<IGraphItem> iterIdenticalGraphItemReps;
			IGraphItem identicalNode;

			while (iterGraphItems.hasNext())
			{
				iterIdenticalGraphItemReps = iterGraphItems.next().getAllItemsByProp(
						EGraphItemProperty.ALIAS_CHILD).iterator();

				while (iterIdenticalGraphItemReps.hasNext())
				{
					identicalNode = iterIdenticalGraphItemReps.next();

					hashSelectedVertexRepId2Depth.put(identicalNode.getId(), 0);

					performNeighborhoodAlgorithm(identicalNode);
				}
			}
		}

		// Store currently selected vertices back to selection set
		Set<Entry<Integer, Integer>> setAllSelectedVertices = hashSelectedVertexRepId2Depth
				.entrySet();

		// int[] iArTmpGraphItemId = new int[setAllSelectedVertices.size()];
		// int[] iArTmpGraphItemDepth = new int[setAllSelectedVertices.size()];

		ArrayList<Integer> iAlTmpGraphItemId = new ArrayList<Integer>();
		ArrayList<Integer> iAlTmpGraphItemDepth = new ArrayList<Integer>();

		Iterator<Entry<Integer, Integer>> iterAllSelectedVertices = setAllSelectedVertices
				.iterator();

		int iItemIndex = 0;
		Entry<Integer, Integer> tmpEntry;
		while (iterAllSelectedVertices.hasNext())
		{
			tmpEntry = iterAllSelectedVertices.next();

			iAlTmpGraphItemId.add(tmpEntry.getKey());
			iAlTmpGraphItemDepth.add(tmpEntry.getValue());
			iItemIndex++;
		}
	}

	private void performNeighborhoodAlgorithm(final IGraphItem selectedVertex)
	{

		GraphVisitorSearchBFS graphVisitorSearchBFS;

		if (bEnableNeighborhood)
			graphVisitorSearchBFS = new GraphVisitorSearchBFS(selectedVertex, 4);
		else
			graphVisitorSearchBFS = new GraphVisitorSearchBFS(selectedVertex, 0);

		graphVisitorSearchBFS.setProp(EGraphItemProperty.OUTGOING);
		graphVisitorSearchBFS.setGraph(selectedVertex.getAllGraphByType(
				EGraphItemHierarchy.GRAPH_PARENT).get(0));

		// List<IGraphItem> lGraphItems =
		// graphVisitorSearchBFS.getSearchResult();
		graphVisitorSearchBFS.getSearchResult();

		List<List<IGraphItem>> lDepthSearchResult = graphVisitorSearchBFS
				.getSearchResultDepthOrdered();
		List<IGraphItem> lGraphItems = new ArrayList<IGraphItem>();

		for (int iDepthIndex = 0; iDepthIndex < lDepthSearchResult.size(); iDepthIndex++)
		{
			lGraphItems = lDepthSearchResult.get(iDepthIndex);

			for (int iItemIndex = 0; iItemIndex < lGraphItems.size(); iItemIndex++)
			{
				// Check if selected item is a vertex
				if (lGraphItems.get(iItemIndex) instanceof PathwayVertexGraphItemRep)
				{
					hashSelectedVertexRepId2Depth.put(lGraphItems.get(iItemIndex).getId(),
							(iDepthIndex + 1) / 2); // consider
					// only
					// vertices
					// for
					// depth
				}
				else
				{
					iArSelectedEdgeRepId.add(lGraphItems.get(iItemIndex).getId());
				}
			}
		}
	}

	private void buildEnzymeNodeDisplayList(final GL gl)
	{

		// Creating display list for node cube objects
		iEnzymeNodeDisplayListId = gl.glGenLists(1);

		float fNodeWidth = renderStyle.getEnzymeNodeWidth(true);
		float fNodeHeight = renderStyle.getEnzymeNodeHeight(true);

		gl.glNewList(iEnzymeNodeDisplayListId, GL.GL_COMPILE);
		fillNodeDisplayList(gl, fNodeWidth, fNodeHeight);
		gl.glEndList();
	}

	protected void buildHighlightedEnzymeNodeDisplayList(final GL gl)
	{

		// Creating display list for node cube objects
		iHighlightedEnzymeNodeDisplayListId = gl.glGenLists(1);

		float fNodeWidth = renderStyle.getEnzymeNodeWidth(true);
		float fNodeHeight = renderStyle.getEnzymeNodeHeight(true);

		gl.glNewList(iHighlightedEnzymeNodeDisplayListId, GL.GL_COMPILE);
		fillNodeDisplayListFrame(gl, fNodeWidth, fNodeHeight);
		gl.glEndList();
	}

	protected void buildCompoundNodeDisplayList(final GL gl)
	{

		// Creating display list for node cube objects
		iCompoundNodeDisplayListId = gl.glGenLists(1);

		float fNodeWidth = renderStyle.getCompoundNodeWidth(true);
		float fNodeHeight = renderStyle.getCompoundNodeHeight(true);

		gl.glNewList(iCompoundNodeDisplayListId, GL.GL_COMPILE);
		fillNodeDisplayList(gl, fNodeWidth, fNodeHeight);
		gl.glEndList();
	}

	protected void buildHighlightedCompoundNodeDisplayList(final GL gl)
	{

		// Creating display list for node cube objects
		iHighlightedCompoundNodeDisplayListId = gl.glGenLists(1);

		float fNodeWidth = renderStyle.getCompoundNodeWidth(true);
		float fNodeHeight = renderStyle.getCompoundNodeHeight(true);

		gl.glNewList(iHighlightedCompoundNodeDisplayListId, GL.GL_COMPILE);
		fillNodeDisplayListFrame(gl, fNodeWidth, fNodeHeight);
		gl.glEndList();
	}

	private void fillNodeDisplayList(final GL gl, final float fNodeWidth,
			final float fNodeHeight)
	{

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

		// BACK FACE
		gl.glNormal3f(0.0f, 0.0f, -1.0f);
		// Bottom Left Of The Quad (Back)
		gl.glVertex3f(fNodeWidth, -fNodeHeight, -Z_OFFSET);
		// Bottom Right Of The Quad (Back)
		gl.glVertex3f(-fNodeWidth, -fNodeHeight, -Z_OFFSET);
		// Top Right Of The Quad (Back)
		gl.glVertex3f(-fNodeWidth, fNodeHeight, -Z_OFFSET);
		// Top Left Of The Quad (Back)
		gl.glVertex3f(fNodeWidth, fNodeHeight, -Z_OFFSET);

		// TOP FACE
		gl.glNormal3f(0.0f, 1.0f, 0.0f);
		// Top Right Of The Quad (Top)
		gl.glVertex3f(fNodeWidth, fNodeHeight, -Z_OFFSET);
		// Top Left Of The Quad (Top)
		gl.glVertex3f(-fNodeWidth, fNodeHeight, -Z_OFFSET);
		// Bottom Left Of The Quad (Top)
		gl.glVertex3f(-fNodeWidth, fNodeHeight, Z_OFFSET);
		// Bottom Right Of The Quad (Top)
		gl.glVertex3f(fNodeWidth, fNodeHeight, Z_OFFSET);

		// BOTTOM FACE
		gl.glNormal3f(0.0f, -1.0f, 0.0f);
		// Top Right Of The Quad (Bottom)
		gl.glVertex3f(fNodeWidth, -fNodeHeight, Z_OFFSET);
		// Top Left Of The Quad (Bottom)
		gl.glVertex3f(-fNodeWidth, -fNodeHeight, Z_OFFSET);
		// Bottom Left Of The Quad (Bottom)
		gl.glVertex3f(-fNodeWidth, -fNodeHeight, -Z_OFFSET);
		// Bottom Right Of The Quad (Bottom)
		gl.glVertex3f(fNodeWidth, -fNodeHeight, -Z_OFFSET);

		// RIGHT FACE
		gl.glNormal3f(1.0f, 0.0f, 0.0f);
		// Top Right Of The Quad (Right)
		gl.glVertex3f(fNodeWidth, fNodeHeight, -Z_OFFSET);
		// Top Left Of The Quad (Right)
		gl.glVertex3f(fNodeWidth, fNodeHeight, Z_OFFSET);
		// Bottom Left Of The Quad (Right)
		gl.glVertex3f(fNodeWidth, -fNodeHeight, Z_OFFSET);
		// Bottom Right Of The Quad (Right)
		gl.glVertex3f(fNodeWidth, -fNodeHeight, -Z_OFFSET);

		// LEFT FACE
		gl.glNormal3f(-1.0f, 0.0f, 0.0f);
		// Top Right Of The Quad (Left)
		gl.glVertex3f(-fNodeWidth, fNodeHeight, Z_OFFSET);
		// Top Left Of The Quad (Left)
		gl.glVertex3f(-fNodeWidth, fNodeHeight, -Z_OFFSET);
		// Bottom Left Of The Quad (Left)
		gl.glVertex3f(-fNodeWidth, -fNodeHeight, -Z_OFFSET);
		// Bottom Right Of The Quad (Left)
		gl.glVertex3f(-fNodeWidth, -fNodeHeight, Z_OFFSET);

		gl.glEnd();
	}

	protected void fillNodeDisplayListFrame(final GL gl, final float fNodeWidth,
			final float fNodeHeight)
	{

		gl.glLineWidth(5);

		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(-fNodeWidth, fNodeHeight, 0.02f);
		gl.glVertex3f(fNodeWidth, fNodeHeight, 0.02f);
		gl.glVertex3f(fNodeWidth, -fNodeHeight, 0.02f);
		gl.glVertex3f(-fNodeWidth, -fNodeHeight, 0.02f);
		gl.glEnd();
	}

	private void extractVertices(final GL gl, final AManagedObject containingView,
			PathwayGraph pathwayToExtract)
	{

		Iterator<IGraphItem> vertexIterator = pathwayToExtract.getAllItemsByKind(
				EGraphItemKind.NODE).iterator();;
		IGraphItem vertexRep;

		while (vertexIterator.hasNext())
		{
			vertexRep = vertexIterator.next();

			if (vertexRep != null)
			{
				createVertex(gl, containingView, (PathwayVertexGraphItemRep) vertexRep,
						pathwayToExtract);
			}
		}
	}

	private void extractEdges(final GL gl, PathwayGraph pathwayToExtract)
	{

		Iterator<IGraphItem> edgeIterator = pathwayToExtract.getAllItemsByKind(
				EGraphItemKind.EDGE).iterator();

		IGraphItem edgeRep;

		while (edgeIterator.hasNext())
		{
			edgeRep = edgeIterator.next();

			if (edgeRep != null)
			{
				if (bEnableEdgeRendering)
				{
					createEdge(gl, edgeRep, pathwayToExtract);
				}
				// Render edge if it is contained in the minimum spanning tree
				// of the neighborhoods
				else if (iArSelectedEdgeRepId.contains(edgeRep.getId()))
				{
					createEdge(gl, edgeRep, pathwayToExtract);
				}
			}
		}
	}

	private void createVertex(final GL gl, final AManagedObject containingView,
			PathwayVertexGraphItemRep vertexRep, PathwayGraph containingPathway)
	{

		Vec3f tmpNodeColor = null;

		gl.glPushName(generalManager.getViewGLCanvasManager().getPickingManager()
				.getPickingID(containingView.getId(), EPickingType.PATHWAY_ELEMENT_SELECTION,
						vertexRep.getId()));

		EPathwayVertexShape shape = vertexRep.getShapeType();

		if (vertexRep.getAllItemsByProp(EGraphItemProperty.ALIAS_PARENT).toArray().length == 0)
		{
			generalManager.getLogger().log(
					Level.WARNING,
					"Cannot create pathway vertex. Pathway node representation "
							+ vertexRep.getName() + " has not parent in graph!");
			return;
		}

		EPathwayVertexType vertexType = ((PathwayVertexGraphItem) vertexRep
				.getPathwayVertexGraphItem()).getType();

		// Pathway link
		// if (shape.equals(EPathwayVertexShape.roundrectangle))
		if (vertexType.equals(EPathwayVertexType.map))
		{
			float fCanvasXPos = (vertexRep.getXOrigin() * SCALING_FACTOR_X);
			float fCanvasYPos = (vertexRep.getYOrigin() * SCALING_FACTOR_Y);
			float fNodeWidth = vertexRep.getWidth() / 2.0f * SCALING_FACTOR_X;
			float fNodeHeight = vertexRep.getHeight() / 2.0f * SCALING_FACTOR_Y;

			gl.glTranslatef(fCanvasXPos, -fCanvasYPos, 0);

			// Handle selection highlighting of element
			if (hashSelectedVertexRepId2Depth.containsKey(vertexRep.getId()))
			{
				tmpNodeColor = renderStyle.getHighlightedNodeColor();

				gl.glColor4f(tmpNodeColor.x(), tmpNodeColor.y(), tmpNodeColor.z(), 1);

				fillNodeDisplayListFrame(gl, fNodeWidth, fNodeHeight);
			}

			if (bEnableGeneMapping)
				tmpNodeColor = renderStyle.getPathwayNodeColor(true);
			else
				tmpNodeColor = renderStyle.getPathwayNodeColor(false);

			gl.glColor4f(tmpNodeColor.x(), tmpNodeColor.y(), tmpNodeColor.z(), 1);

			fillNodeDisplayList(gl, fNodeWidth, fNodeHeight);

			gl.glTranslatef(-fCanvasXPos, fCanvasYPos, 0);
		}
		// Compound
		// else if (shape.equals(EPathwayVertexShape.circle))
		else if (vertexType.equals(EPathwayVertexType.compound))
		{
			float fCanvasXPos = (vertexRep.getXOrigin() * SCALING_FACTOR_X);
			float fCanvasYPos = (vertexRep.getYOrigin() * SCALING_FACTOR_Y);

			gl.glTranslatef(fCanvasXPos, -fCanvasYPos, 0);

			// Handle selection highlighting of element
			if (hashSelectedVertexRepId2Depth.containsKey(vertexRep.getId()))
			{
				tmpNodeColor = renderStyle.getHighlightedNodeColor();

				gl.glColor4f(tmpNodeColor.x(), tmpNodeColor.y(), tmpNodeColor.z(), 1);
				gl.glCallList(iHighlightedCompoundNodeDisplayListId);
			}

			if (bEnableGeneMapping)
				tmpNodeColor = renderStyle.getCompoundNodeColor(true);
			else
				tmpNodeColor = renderStyle.getCompoundNodeColor(false);

			gl.glColor4f(tmpNodeColor.x(), tmpNodeColor.y(), tmpNodeColor.z(), 1);
			gl.glCallList(iCompoundNodeDisplayListId);

			gl.glTranslatef(-fCanvasXPos, fCanvasYPos, 0);
		}
		else if (shape.equals(EPathwayVertexShape.poly)) // BIOCARTA
		{
			gl.glColor4f(0, 0, 0, 0);

			short[][] shArCoords = vertexRep.getCoords();

			gl.glBegin(GL.GL_POLYGON);
			for (int iPointIndex = 0; iPointIndex < shArCoords.length; iPointIndex++)
			{
				gl.glVertex3f(shArCoords[iPointIndex][0] * SCALING_FACTOR_X,
						-shArCoords[iPointIndex][1] * SCALING_FACTOR_Y, Z_OFFSET);
			}
			gl.glEnd();

			// Handle selection highlighting of element
			if (hashSelectedVertexRepId2Depth.containsKey(vertexRep.getId()))
			{
				tmpNodeColor = renderStyle.getHighlightedNodeColor();
				gl.glColor4f(tmpNodeColor.x(), tmpNodeColor.y(), tmpNodeColor.z(), 1);
				gl.glLineWidth(5);
			}
			else
			{
				gl.glColor4f(0.8f, 0.8f, 0.8f, 1); // TODO color constant
				gl.glLineWidth(3);
			}

			gl.glBegin(GL.GL_LINE_STRIP);
			for (int iPointIndex = 0; iPointIndex < shArCoords.length; iPointIndex++)
			{
				gl.glVertex3f(shArCoords[iPointIndex][0] * SCALING_FACTOR_X,
						-shArCoords[iPointIndex][1] * SCALING_FACTOR_Y, Z_OFFSET);
			}
			gl.glEnd();
		}
		// Enzyme
		// else if (shape.equals(EPathwayVertexShape.rectangle)
		// || shape.equals(EPathwayVertexShape.rect))
		else if (vertexType.equals(EPathwayVertexType.gene)
				|| vertexType.equals(EPathwayVertexType.enzyme))
		{
			float fCanvasXPos = (vertexRep.getXOrigin() * SCALING_FACTOR_X);
			float fCanvasYPos = (vertexRep.getYOrigin() * SCALING_FACTOR_Y);
			float fNodeWidth = vertexRep.getWidth() / 2.0f * SCALING_FACTOR_X;
			float fNodeHeight = vertexRep.getHeight() / 2.0f * SCALING_FACTOR_Y;

			gl.glTranslatef(fCanvasXPos, -fCanvasYPos, 0);

			// Handle selection highlighting of element
			if (hashSelectedVertexRepId2Depth.containsKey(vertexRep.getId()))
			{
				int iDepth = hashSelectedVertexRepId2Depth.get(vertexRep.getId());
				tmpNodeColor = renderStyle.getHighlightedNodeColor();

				if (iDepth != 0)
				{
					gl.glEnable(GL.GL_LINE_STIPPLE);
				}

				if (iDepth == 1)
				{
					gl.glLineStipple(4, (short) 0xAAAA);
				}
				else if (iDepth == 2)
				{
					gl.glLineStipple(2, (short) 0xAAAA);
				}
				else if (iDepth == 3)
				{
					gl.glLineStipple(1, (short) 0xAAAA);
				}

				gl.glColor4f(tmpNodeColor.x(), tmpNodeColor.y(), tmpNodeColor.z(), 1);
				gl.glCallList(iHighlightedEnzymeNodeDisplayListId);
				gl.glDisable(GL.GL_LINE_STIPPLE);
			}

			if (bEnableGeneMapping && vertexType.equals(EPathwayVertexType.gene))
			{
				mapExpression(gl, vertexRep, fNodeWidth, fNodeHeight);
			}
			else
			{
				tmpNodeColor = renderStyle.getEnzymeNodeColor(bEnableGeneMapping);
				gl.glColor4f(tmpNodeColor.x(), tmpNodeColor.y(), tmpNodeColor.z(), 1);

				gl.glCallList(iEnzymeNodeDisplayListId);
			}

			gl.glTranslatef(-fCanvasXPos, fCanvasYPos, 0);
		}

		gl.glPopName();
	}

	private void createEdge(final GL gl, IGraphItem edgeRep, PathwayGraph containingPathway)
	{

		List<IGraphItem> listGraphItemsIn = edgeRep
				.getAllItemsByProp(EGraphItemProperty.INCOMING);
		List<IGraphItem> listGraphItemsOut = edgeRep
				.getAllItemsByProp(EGraphItemProperty.OUTGOING);

		if (listGraphItemsIn.isEmpty() || listGraphItemsOut.isEmpty())
		{
			// generalManager.getSingelton().logMsg(
			// this.getClass().getSimpleName()
			// +
			// ": createEdge(): Edge has either no incoming or outcoming vertex."
			// ,
			// LoggerType.VERBOSE);

			return;
		}

		Vec3f tmpColor;
		float fReactionLineOffset = 0;

		// Check if edge is a reaction
		if (edgeRep instanceof PathwayReactionEdgeGraphItemRep)
		{
			tmpColor = renderStyle.getReactionEdgeColor();
			fReactionLineOffset = 0.01f;
		}
		// Check if edge is a relation
		else if (edgeRep instanceof PathwayRelationEdgeGraphItemRep)
		{
			tmpColor = renderStyle.getRelationEdgeColor();
		}
		else
		{
			tmpColor = new Vec3f(0, 0, 0);
		}

		gl.glLineWidth(4);
		gl.glColor4f(tmpColor.x(), tmpColor.y(), tmpColor.z(), 1);
		gl.glBegin(GL.GL_LINES);

		Iterator<IGraphItem> iterSourceGraphItem = listGraphItemsIn.iterator();
		Iterator<IGraphItem> iterTargetGraphItem = listGraphItemsOut.iterator();

		PathwayVertexGraphItemRep tmpSourceGraphItem;
		PathwayVertexGraphItemRep tmpTargetGraphItem;
		while (iterSourceGraphItem.hasNext())
		{

			tmpSourceGraphItem = (PathwayVertexGraphItemRep) iterSourceGraphItem.next();

			while (iterTargetGraphItem.hasNext())
			{
				tmpTargetGraphItem = (PathwayVertexGraphItemRep) iterTargetGraphItem.next();

				gl.glVertex3f(tmpSourceGraphItem.getXOrigin() * SCALING_FACTOR_X
						+ fReactionLineOffset, -tmpSourceGraphItem.getYOrigin()
						* SCALING_FACTOR_Y + fReactionLineOffset, 0.02f);
				gl.glVertex3f(tmpTargetGraphItem.getXOrigin() * SCALING_FACTOR_X
						+ fReactionLineOffset, -tmpTargetGraphItem.getYOrigin()
						* SCALING_FACTOR_Y + fReactionLineOffset, 0.02f);
			}
		}

		gl.glEnd();
	}

	public void renderPathway(final GL gl, final int iPathwayID, boolean bRenderLabels)
	{

		if (bEnableEdgeRendering || !iArSelectedEdgeRepId.isEmpty())
		{
			int iTmpEdgesDisplayListID = hashPathwayId2EdgesDisplayListId.get(iPathwayID);
			gl.glCallList(iTmpEdgesDisplayListID);
		}

		Integer iTmpVerticesDisplayListID = hashPathwayId2VerticesDisplayListId
				.get(iPathwayID);

		if (iTmpVerticesDisplayListID != null)
		{
			gl.glCallList(iTmpVerticesDisplayListID);

			if (bRenderLabels && bEnableAnnotation)
				renderLabels(gl, iPathwayID);
		}
	}

	private void renderLabels(final GL gl, final int iPathwayID)
	{

		PathwayVertexGraphItemRep vertexRep;
		PathwayGraph tmpPathway = (PathwayGraph) generalManager.getPathwayManager().getItem(
				iPathwayID);

		// Don't annotate BioCarta pathways - because of good texture annotation
		if (tmpPathway.getType().equals(EPathwayDatabaseType.BIOCARTA))
			return;

		Iterator<IGraphItem> vertexRepIterator = tmpPathway.getAllItemsByKind(
				EGraphItemKind.NODE).iterator();

		while (vertexRepIterator.hasNext())
		{
			vertexRep = (PathwayVertexGraphItemRep) vertexRepIterator.next();

			if (vertexRep != null)
			{
				float fNodeWidth = vertexRep.getWidth() / 2.0f * SCALING_FACTOR_X;
				float fNodeHeight = vertexRep.getHeight() / 2.0f * SCALING_FACTOR_Y;
				float fCanvasXPos = (vertexRep.getXOrigin() * SCALING_FACTOR_X);
				float fCanvasYPos = (vertexRep.getYOrigin() * SCALING_FACTOR_Y);

				gl.glTranslated(fCanvasXPos - fNodeWidth + 0.01f, -fCanvasYPos - 0.01f, 0);
				gl.glColor3f(0, 0, 0);
				GLTextUtils.renderTextInRegion(gl, vertexRep.getName(), 10, 0, 0, 0.03f,
						fNodeWidth, fNodeHeight);
				// GLTextUtils.renderText(gl, vertexRep.getName(), 0, 0,
				// -0.03f);
				gl.glTranslated(-fCanvasXPos + fNodeWidth - 0.01f, fCanvasYPos + 0.01f, 0);
			}
		}
	}

	public void mapExpression(final GL gl, final PathwayVertexGraphItemRep pathwayVertexRep,
			final float fNodeWidth, final float fNodeHeight)
	{

		ArrayList<Vec3f> alMappingColor;

		// Check if vertex is already mapped
		if (hashElementId2MappingColorArray.containsKey(pathwayVertexRep.getId()))
		{
			// Load existing mapping
			alMappingColor = hashElementId2MappingColorArray.get(pathwayVertexRep.getId());
		}
		else
		{
			// Request mapping
			alMappingColor = genomeMapper.getMappingColorArrayByVertexRep(pathwayVertexRep);
			hashElementId2MappingColorArray.put((Integer) pathwayVertexRep.getId(),
					alMappingColor);
		}

		drawMapping(gl, alMappingColor, fNodeWidth, fNodeHeight, false);
	}

	private void drawMapping(final GL gl, final ArrayList<Vec3f> alMappingColor,
			final float fNodeWidth, final float fNodeHeight, final boolean bEnableGrid)
	{

		int iColumnCount = (int) Math.ceil((float) alMappingColor.size()
				/ (float) iMappingRowCount);

		Vec3f tmpNodeColor = null;

		gl.glPushMatrix();

		// If no mapping is available - render whole node in one color
		if (alMappingColor.size() == 1)
		{
			tmpNodeColor = alMappingColor.get(0);

			// Check if the mapping gave back a valid color
			if (tmpNodeColor.x() == -1)
				tmpNodeColor = renderStyle.getEnzymeNodeColor(true);

			gl.glColor3f(tmpNodeColor.x(), tmpNodeColor.y(), tmpNodeColor.z());
			gl.glCallList(iEnzymeNodeDisplayListId);
		}
		else
		{
			gl.glTranslatef(-fNodeWidth + fNodeWidth / iColumnCount, -fNodeHeight
					+ fNodeHeight / iMappingRowCount, 0.0f);

			for (int iRowIndex = 0; iRowIndex < iMappingRowCount; iRowIndex++)
			{
				for (int iColumnIndex = 0; iColumnIndex < iColumnCount; iColumnIndex++)
				{
					int iCurrentElement = iRowIndex * iMappingRowCount + iColumnIndex;

					if (iCurrentElement < alMappingColor.size())
						tmpNodeColor = alMappingColor.get(iCurrentElement);// (
					// iRowIndex
					// +
					// 1
					// )
					// *
					// iColumnIndex
					// )
					// ;
					else
						continue;

					// Check if the mapping gave back a valid color
					if (tmpNodeColor.x() != -1)
					{
						gl.glColor3f(tmpNodeColor.x(), tmpNodeColor.y(), tmpNodeColor.z());
						gl.glScalef(1.0f / iColumnCount, 1.0f / iMappingRowCount, 1.0f);
						gl.glCallList(iEnzymeNodeDisplayListId);
						gl.glScalef(iColumnCount, iMappingRowCount, 1.0f);
					}

					gl.glTranslatef(fNodeWidth * 2.0f / iColumnCount, 0.0f, 0.0f);
				}

				gl.glTranslatef(-2.0f * fNodeWidth, 2.0f * fNodeHeight / iMappingRowCount,
						0.0f);
			}
		}

		gl.glPopMatrix();

		// Render grid
		if (bEnableGrid)
		{
			gl.glColor3f(1, 1, 1);
			gl.glBegin(GL.GL_LINE_LOOP);
			gl.glVertex3f(-fNodeWidth, -fNodeHeight, Z_OFFSET);
			gl.glVertex3f(fNodeWidth, -fNodeHeight, Z_OFFSET);
			gl.glVertex3f(fNodeWidth, fNodeHeight, Z_OFFSET);
			gl.glVertex3f(-fNodeWidth, fNodeHeight, Z_OFFSET);
			gl.glEnd();

			gl.glBegin(GL.GL_LINES);
			for (int iRowIndex = 1; iRowIndex <= iMappingRowCount; iRowIndex++)
			{
				gl.glVertex3f(-fNodeWidth, -fNodeHeight + (2 * fNodeHeight / iMappingRowCount)
						* iRowIndex, Z_OFFSET);
				gl.glVertex3f(fNodeWidth, -fNodeHeight + (2 * fNodeHeight / iMappingRowCount)
						* iRowIndex, Z_OFFSET);
			}
			for (int iColumnIndex = 1; iColumnIndex <= iColumnCount; iColumnIndex++)
			{
				gl.glVertex3f(-fNodeWidth + (2 * fNodeWidth / iColumnCount) * iColumnIndex,
						fNodeHeight, Z_OFFSET);
				gl.glVertex3f(-fNodeWidth + (2 * fNodeWidth / iColumnCount) * iColumnIndex,
						-fNodeHeight, Z_OFFSET);
			}
			gl.glEnd();
		}
	}

	public void enableEdgeRendering(final boolean bEnableEdgeRendering)
	{

		this.bEnableEdgeRendering = bEnableEdgeRendering;
	}

	public void enableGeneMapping(final boolean bEnableGeneMappging)
	{

		this.bEnableGeneMapping = bEnableGeneMappging;
	}

	public void enableIdenticalNodeHighlighting(final boolean bEnableIdenticalNodeHighlighting)
	{

		this.bEnableIdenticalNodeHighlighting = bEnableIdenticalNodeHighlighting;
	}

	public void enableNeighborhood(final boolean bEnableNeighborhood)
	{

		this.bEnableNeighborhood = bEnableNeighborhood;
	}

	public void enableAnnotation(final boolean bEnableAnnotation)
	{

		this.bEnableAnnotation = bEnableAnnotation;
	}

	public void setMappingRowCount(final int iMappingRowCount)
	{

		this.iMappingRowCount = iMappingRowCount;
	}
}
