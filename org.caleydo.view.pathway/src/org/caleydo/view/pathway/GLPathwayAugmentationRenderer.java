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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.column.DataRepresentation;
import org.caleydo.core.data.perspective.table.Average;
import org.caleydo.core.data.perspective.table.TablePerspectiveStatistics;
import org.caleydo.core.data.selection.EventBasedSelectionManager;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.item.vertex.EPathwayVertexShape;
import org.caleydo.datadomain.pathway.graph.item.vertex.EPathwayVertexType;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertex;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.manager.PathwayItemManager;

/**
 * Class responsible for rendering all augmentations on top of the pathway texture.
 *
 * @author Marc Streit
 * @author Alexander Lex
 */
public class GLPathwayAugmentationRenderer {

	private GLPathway glPathwayView;

	private int enzymeNodeDisplayListId = -1;
	private int compoundNodeDisplayListId = -1;
	private int framedEnzymeNodeDisplayListId = -1;
	private int framedMappedEnzymeNodeDisplayListId = -1;
	private int framedCompoundNodeDisplayListId = -1;
	private int upscaledFilledEnzymeNodeDisplayListId = -1;
	private int upscaledFramedEnzymeNodeDisplayListID = -1;

	private boolean enableEdgeRendering = false;
	private boolean enableGeneMapping = false;

	private HashMap<PathwayGraph, Integer> hashPathway2VerticesDisplayListId;
	private HashMap<PathwayGraph, Integer> hashPathway2EdgesDisplayListId;

	private SelectionManager vertexSelectionManager;

	private ArrayList<Integer> selectedEdgeRepId;

	private IDMappingManager idMappingManager;

	private PathwayItemManager pathwayItemManager;

	private String dimensionDataRepresentation = DataRepresentation.NORMALIZED;

	/**
	 * The virtual array containing the samples that are currently mapped onto the nodes
	 */
	private VirtualArray selectedSamplesVA;

	private float stdBarHeight;
	private float onePxlWidth;
	private float onePxlHeight;
	private float stdDevBarHeight_1_third;

	/**
	 * Constructor.
	 */
	public GLPathwayAugmentationRenderer(ViewFrustum viewFrustum, GLPathway glPathwayView) {

		this.glPathwayView = glPathwayView;
		idMappingManager = glPathwayView.getPathwayDataDomain().getGeneIDMappingManager();

		hashPathway2VerticesDisplayListId = new HashMap<PathwayGraph, Integer>();
		hashPathway2EdgesDisplayListId = new HashMap<PathwayGraph, Integer>();

		selectedEdgeRepId = new ArrayList<Integer>();

		pathwayItemManager = PathwayItemManager.get();

	}

	public void init(final GL2 gl, SelectionManager vertexSelectionManager) {

		this.vertexSelectionManager = vertexSelectionManager;
	}

	public void buildPathwayDisplayList(final GL2 gl, final PathwayGraph pathway) {

		buildEnzymeNodeDisplayList(gl);
		buildCompoundNodeDisplayList(gl);
		buildFramedEnzymeNodeDisplayList(gl);
		buildFramedCompoundNodeDisplayList(gl);
		buildUpscaledEnzymeNodeDisplayList(gl);
		buildUpscaledFramedEnzymeNodeDisplayList(gl);

		if (pathway == null)
			return;

		int iVerticesDisplayListId = -1;
		int edgesDisplayListId = -1;

		if (hashPathway2VerticesDisplayListId.containsKey(pathway)) {
			// Replace current display list if a display list exists
			iVerticesDisplayListId = hashPathway2VerticesDisplayListId.get(pathway);
		} else {
			// Creating vertex display list for pathways
			iVerticesDisplayListId = gl.glGenLists(1);
			hashPathway2VerticesDisplayListId.put(pathway, iVerticesDisplayListId);
		}

		createSelectedSamplesVA();

		gl.glNewList(iVerticesDisplayListId, GL2.GL_COMPILE);
		renderVertices(gl, pathway);
		gl.glEndList();

		if (hashPathway2EdgesDisplayListId.containsKey(pathway)) {
			// Replace current display list if a display list exists
			edgesDisplayListId = hashPathway2EdgesDisplayListId.get(pathway);
		} else {
			// Creating edge display list for pathways
			edgesDisplayListId = gl.glGenLists(1);
			hashPathway2EdgesDisplayListId.put(pathway, edgesDisplayListId);
		}

		gl.glNewList(edgesDisplayListId, GL2.GL_COMPILE);
		extractEdges(gl, pathway);
		gl.glEndList();
	}

	/**
	 * Creates a sample va based on the state of {@link GLPathway#getSampleMappingMode()}
	 */
	private void createSelectedSamplesVA() {
		if (glPathwayView.getDataDomain() == null) {
			selectedSamplesVA = null;
			return;
		}
		Set<Integer> selectedSamples = glPathwayView.getSampleSelectionManager().getElements(SelectionType.SELECTION);
		List<Integer> selectedSamplesArray = new ArrayList<Integer>();

		// Only add selected samples for single pathway
		switch (glPathwayView.getSampleMappingMode()) {
		case ALL:
			if (!glPathwayView.getDataDomain().isGeneRecord()) {
				selectedSamplesVA = glPathwayView.getTablePerspective().getRecordPerspective().getVirtualArray();
				return;
			} else {
				selectedSamplesVA = glPathwayView.getTablePerspective().getDimensionPerspective().getVirtualArray();
				return;
			}

		case SELECTED:
			selectedSamplesArray.addAll(selectedSamples);
			if (selectedSamplesArray.isEmpty()) {

				selectedSamplesVA = null;
				return;
			}

			selectedSamplesVA = new VirtualArray(glPathwayView.getSampleSelectionManager().getIDType(),
						selectedSamplesArray);

			break;
		default:
			throw new IllegalStateException("Unknown state when switching " + glPathwayView.getSampleMappingMode());
		}

	}

	public void performIdenticalNodeHighlighting(SelectionType selectionType) {
		if (vertexSelectionManager == null)
			return;

		selectedEdgeRepId.clear();

		ArrayList<Integer> selectedGraphItemIDs = new ArrayList<Integer>();
		Set<Integer> itemIDs;
		itemIDs = vertexSelectionManager.getElements(selectionType);

		if (itemIDs != null) {
			selectedGraphItemIDs.addAll(itemIDs);
		}

		if (selectedGraphItemIDs.size() == 0)
			return;

		// Copy selection IDs to array list object
		for (Integer graphItemID : selectedGraphItemIDs) {

			for (PathwayVertex vertex : pathwayItemManager.getPathwayVertexRep(graphItemID).getPathwayVertices()) {

				for (PathwayVertexRep vertexRep : vertex.getPathwayVertexReps()) {

					if (itemIDs.contains(vertexRep.getID())) {
						continue;
					}
					vertexSelectionManager.addToType(selectionType, vertexRep.getID());
				}

			}
		}
	}

	private void buildEnzymeNodeDisplayList(final GL2 gl) {

		enzymeNodeDisplayListId = gl.glGenLists(1);

		float nodeWidth = glPathwayView.getPixelGLConverter().getGLWidthForPixelWidth(
				PathwayRenderStyle.ENZYME_NODE_PIXEL_WIDTH);
		float nodeHeight = glPathwayView.getPixelGLConverter().getGLHeightForPixelHeight(
				PathwayRenderStyle.ENZYME_NODE_PIXEL_HEIGHT);

		gl.glNewList(enzymeNodeDisplayListId, GL2.GL_COMPILE);
		fillNodeDisplayList(gl, nodeWidth, nodeHeight);
		gl.glEndList();
	}

	private void buildUpscaledEnzymeNodeDisplayList(final GL2 gl) {

		upscaledFilledEnzymeNodeDisplayListId = gl.glGenLists(1);

		float nodeWidth = glPathwayView.getPixelGLConverter().getGLWidthForPixelWidth(
				PathwayRenderStyle.ENZYME_NODE_PIXEL_WIDTH);
		float nodeHeight = glPathwayView.getPixelGLConverter().getGLHeightForPixelHeight(
				PathwayRenderStyle.ENZYME_NODE_PIXEL_HEIGHT);

		// float scaleFactor = 1.4f;
		float scaleFactor = 3.f;
		nodeWidth *= scaleFactor;
		nodeHeight *= scaleFactor;

		gl.glNewList(upscaledFilledEnzymeNodeDisplayListId, GL2.GL_COMPILE);
		fillNodeDisplayList(gl, nodeWidth, nodeHeight);
		gl.glEndList();
	}

	protected void buildUpscaledFramedEnzymeNodeDisplayList(final GL2 gl) {

		upscaledFramedEnzymeNodeDisplayListID = gl.glGenLists(1);

		float nodeWidth = glPathwayView.getPixelGLConverter().getGLWidthForPixelWidth(
				PathwayRenderStyle.ENZYME_NODE_PIXEL_WIDTH);
		float nodeHeight = glPathwayView.getPixelGLConverter().getGLHeightForPixelHeight(
				PathwayRenderStyle.ENZYME_NODE_PIXEL_HEIGHT);

		float scaleFactor = 3.f;
		nodeWidth *= scaleFactor;
		nodeHeight *= scaleFactor;

		gl.glNewList(upscaledFramedEnzymeNodeDisplayListID, GL2.GL_COMPILE);
		fillNodeDisplayListFrame(gl, nodeWidth, nodeHeight);
		gl.glEndList();
	}

	protected void buildFramedEnzymeNodeDisplayList(final GL2 gl) {

		framedEnzymeNodeDisplayListId = gl.glGenLists(1);
		framedMappedEnzymeNodeDisplayListId = gl.glGenLists(1);

		float nodeWidth = glPathwayView.getPixelGLConverter().getGLWidthForPixelWidth(
				PathwayRenderStyle.ENZYME_NODE_PIXEL_WIDTH);
		float nodeHeight = glPathwayView.getPixelGLConverter().getGLHeightForPixelHeight(
				PathwayRenderStyle.ENZYME_NODE_PIXEL_HEIGHT);

		PixelGLConverter pixelGLConverter = glPathwayView.getPixelGLConverter();
		stdBarHeight = pixelGLConverter.getGLHeightForPixelHeight(PathwayRenderStyle.STD_DEV_BAR_PIXEL_HEIGHT);
		onePxlWidth = pixelGLConverter.getGLWidthForPixelWidth(1);
		onePxlHeight = pixelGLConverter.getGLHeightForPixelHeight(1);
		stdDevBarHeight_1_third = (stdBarHeight / 3f);

		gl.glNewList(framedEnzymeNodeDisplayListId, GL2.GL_COMPILE);
		fillNodeDisplayListFrame(gl, nodeWidth + onePxlWidth, nodeHeight);
		gl.glEndList();

		gl.glNewList(framedMappedEnzymeNodeDisplayListId, GL2.GL_COMPILE);
		fillNodeDisplayListFrame(gl, nodeWidth + onePxlWidth, nodeHeight + (2f * stdDevBarHeight_1_third)
				- onePxlHeight);
		gl.glEndList();
	}

	protected void buildCompoundNodeDisplayList(final GL2 gl) {
		// Creating display list for node cube objects
		compoundNodeDisplayListId = gl.glGenLists(1);

		float nodeWidth = glPathwayView.getPixelGLConverter().getGLWidthForPixelWidth(
				PathwayRenderStyle.COMPOUND_NODE_PIXEL_WIDTH);
		float nodeHeight = glPathwayView.getPixelGLConverter().getGLHeightForPixelHeight(
				PathwayRenderStyle.COMPOUND_NODE_PIXEL_HEIGHT);

		gl.glNewList(compoundNodeDisplayListId, GL2.GL_COMPILE);
		fillNodeDisplayList(gl, nodeWidth, nodeHeight);
		gl.glEndList();
	}

	protected void buildFramedCompoundNodeDisplayList(final GL2 gl) {
		// Creating display list for node cube objects
		framedCompoundNodeDisplayListId = gl.glGenLists(1);

		float nodeWidth = glPathwayView.getPixelGLConverter().getGLWidthForPixelWidth(
				PathwayRenderStyle.COMPOUND_NODE_PIXEL_WIDTH);
		float nodeHeight = glPathwayView.getPixelGLConverter().getGLHeightForPixelHeight(
				PathwayRenderStyle.COMPOUND_NODE_PIXEL_HEIGHT);

		gl.glNewList(framedCompoundNodeDisplayListId, GL2.GL_COMPILE);
		fillNodeDisplayListFrame(gl, nodeWidth, nodeHeight);
		gl.glEndList();
	}

	private void fillNodeDisplayList(final GL2 gl, float nodeWidth, float nodeHeight) {

		gl.glBegin(GL2.GL_QUADS);
		gl.glNormal3f(0.0f, 0.0f, 1.0f);
		gl.glVertex3f(0, 0, PathwayRenderStyle.Z_OFFSET);
		gl.glVertex3f(nodeWidth, 0, PathwayRenderStyle.Z_OFFSET);
		gl.glVertex3f(nodeWidth, -nodeHeight, PathwayRenderStyle.Z_OFFSET);
		gl.glVertex3f(0, -nodeHeight, PathwayRenderStyle.Z_OFFSET);
		gl.glEnd();
	}

	protected void fillNodeDisplayListFrame(final GL2 gl, float nodeWidth, float nodeHeight) {
		gl.glLineWidth(3);

		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(0, 0, PathwayRenderStyle.Z_OFFSET + 0.03f);
		gl.glVertex3f(nodeWidth, 0, PathwayRenderStyle.Z_OFFSET + 0.03f);
		gl.glVertex3f(nodeWidth, -nodeHeight, PathwayRenderStyle.Z_OFFSET + 0.03f);
		gl.glVertex3f(0, -nodeHeight, PathwayRenderStyle.Z_OFFSET + 0.03f);
		gl.glEnd();
	}

	/**
	 * Iterates over all vertices in the pathway and renders the augmentations of the vertex reps
	 */
	private void renderVertices(final GL2 gl, PathwayGraph pathway) {

		for (PathwayVertexRep vertexRep : pathway.vertexSet()) {
			if (vertexRep == null) {
				continue;
			}

			renderVertex(gl, vertexRep);
		}
	}

	private void extractEdges(final GL2 gl, PathwayGraph pathwayToExtract) {

		// while (pathwayToExtract.edgeSet()) {
		// edgeRep = edgeIterator.next();
		//
		// if (edgeRep != null) {
		// if (enableEdgeRendering) {
		// createEdge(gl, edgeRep, pathwayToExtract);
		// }
		// // Render edge if it is contained in the minimum spanning tree
		// // of the neighborhoods
		// else if (selectedEdgeRepId.contains(edgeRep.getID())) {
		// createEdge(gl, edgeRep, pathwayToExtract);
		// }
		// }
		// }
	}

	private void renderVertex(final GL2 gl, PathwayVertexRep vertexRep) {

		float[] tmpNodeColor = null;

		gl.glPushName(glPathwayView.getPickingManager().getPickingID(glPathwayView.getID(),
				EPickingType.PATHWAY_ELEMENT_SELECTION.name(), vertexRep.getID()));

		EPathwayVertexShape shape = vertexRep.getShapeType();

		if (shape.equals(EPathwayVertexShape.poly))
			renderPolyVertex(gl, vertexRep);

		PixelGLConverter pixelGLConverter = glPathwayView.getPixelGLConverter();

		float canvasXPos = pixelGLConverter.getGLWidthForPixelWidth(vertexRep.getCenterX());
		float canvasYPos = pixelGLConverter.getGLHeightForPixelHeight(vertexRep.getCenterY());

		gl.glTranslatef(canvasXPos, -canvasYPos, 0);

		EPathwayVertexType vertexType = vertexRep.getType();

		switch (vertexType) {
		// Pathway link
		case map:
			// Ignore KEGG title node
			if (vertexRep.getName().contains("TITLE")) {
				gl.glTranslatef(-canvasXPos, canvasYPos, 0);
				gl.glPopName();
				return;
			}

			float nodeWidth = pixelGLConverter.getGLWidthForPixelWidth(vertexRep.getWidth());
			float nodeHeight = pixelGLConverter.getGLHeightForPixelHeight(vertexRep.getHeight());

			// create mask to prevent redrawing
			gl.glEnable(GL.GL_STENCIL_TEST);
			gl.glColorMask(false, false, false, false);
			gl.glDisable(GL.GL_DEPTH_TEST);
			// gl.glStencilFunc(GL.GL_ALWAYS,2, 1);
			gl.glStencilFunc(GL.GL_ALWAYS, 2, 0xff);
			gl.glStencilOp(GL.GL_REPLACE, GL.GL_REPLACE, GL.GL_REPLACE);

			fillNodeDisplayList(gl, nodeWidth, nodeHeight);

			gl.glStencilFunc(GL.GL_ALWAYS, 2, 0xff);
			fillNodeDisplayListFrame(gl, nodeWidth, nodeHeight);

			gl.glDisable(GL.GL_STENCIL_TEST);
			gl.glColorMask(true, true, true, true);
			gl.glEnable(GL.GL_DEPTH_TEST);

			tmpNodeColor = new float[] { 0.f, 0.f, 0.f, 0.25f };
			gl.glColor4fv(tmpNodeColor, 0);
			fillNodeDisplayList(gl, nodeWidth, nodeHeight);

			// Handle selection highlighting of element

			if (vertexSelectionManager.checkStatus(SelectionType.SELECTION, vertexRep.getID())) {
				tmpNodeColor = SelectionType.SELECTION.getColor();
				gl.glColor4fv(tmpNodeColor, 0);
				fillNodeDisplayListFrame(gl, nodeWidth, nodeHeight);
			} else if (vertexSelectionManager.checkStatus(SelectionType.MOUSE_OVER, vertexRep.getID())) {
				tmpNodeColor = SelectionType.MOUSE_OVER.getColor();
				gl.glColor4fv(tmpNodeColor, 0);
				fillNodeDisplayListFrame(gl, nodeWidth, nodeHeight);
			}

			break;
		case compound:

			// // create mask
			gl.glEnable(GL.GL_STENCIL_TEST);
			gl.glColorMask(false, false, false, false);
			gl.glDisable(GL.GL_DEPTH_TEST);
			gl.glDisable(GL.GL_BLEND);
			gl.glStencilFunc(GL.GL_ALWAYS, 1, 0xff);
			gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_REPLACE);
			//
			gl.glCallList(compoundNodeDisplayListId);

			gl.glDisable(GL.GL_STENCIL_TEST);
			gl.glColorMask(true, true, true, true);
			gl.glEnable(GL.GL_DEPTH_TEST);
			gl.glEnable(GL.GL_BLEND);

			EventBasedSelectionManager metabolicSelectionManager = glPathwayView.getMetaboliteSelectionManager();
			// Handle selection highlighting of element
			if (vertexSelectionManager.checkStatus(SelectionType.SELECTION, vertexRep.getID())
					|| metabolicSelectionManager.checkStatus(SelectionType.SELECTION, vertexRep.getName().hashCode())) {
				tmpNodeColor = SelectionType.SELECTION.getColor();

				gl.glColor4fv(tmpNodeColor, 0);
				gl.glCallList(framedCompoundNodeDisplayListId);

				gl.glEnable(GL.GL_STENCIL_TEST);
				gl.glColorMask(false, false, false, false);
				gl.glDisable(GL.GL_DEPTH_TEST);
				gl.glDisable(GL.GL_BLEND);
				gl.glStencilFunc(GL.GL_ALWAYS, 2, 0xff);
				gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_REPLACE);

				gl.glCallList(framedCompoundNodeDisplayListId);

				gl.glDisable(GL.GL_STENCIL_TEST);
				gl.glColorMask(true, true, true, true);
				gl.glEnable(GL.GL_DEPTH_TEST);
				gl.glEnable(GL.GL_BLEND);

			} else if (vertexSelectionManager.checkStatus(SelectionType.MOUSE_OVER, vertexRep.getID())
					|| metabolicSelectionManager.checkStatus(SelectionType.MOUSE_OVER, vertexRep.getName().hashCode())) {
				tmpNodeColor = SelectionType.MOUSE_OVER.getColor();

				gl.glColor4fv(tmpNodeColor, 0);
				gl.glCallList(framedCompoundNodeDisplayListId);

				gl.glEnable(GL.GL_STENCIL_TEST);
				gl.glColorMask(false, false, false, false);
				gl.glDisable(GL.GL_DEPTH_TEST);
				gl.glDisable(GL.GL_BLEND);
				gl.glStencilFunc(GL.GL_ALWAYS, 2, 0xff);
				gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_REPLACE);

				gl.glCallList(framedCompoundNodeDisplayListId);

				gl.glDisable(GL.GL_STENCIL_TEST);
				gl.glColorMask(true, true, true, true);
				gl.glEnable(GL.GL_DEPTH_TEST);
				gl.glEnable(GL.GL_BLEND);

			}

			tmpNodeColor = PathwayRenderStyle.COMPOUND_NODE_COLOR;

			gl.glColor4fv(tmpNodeColor, 0);
			gl.glCallList(compoundNodeDisplayListId);

			break;
		case group:

			// gl.glColor4f(1, 1, 0, 1);
			// fillNodeDisplayList(gl, nodeWidth, nodeHeight);
			break;
		case gene:
		case enzyme:
			// new kegg data assign enzymes without mapping to "undefined"
			// which we represent as other
		case other:

			gl.glLineWidth(1);
			// // create mask
			gl.glEnable(GL.GL_STENCIL_TEST);
			gl.glColorMask(false, false, false, false);
			gl.glDisable(GL.GL_DEPTH_TEST);
			gl.glDisable(GL.GL_BLEND);
			gl.glStencilFunc(GL.GL_GREATER, 1, 0xff);
			gl.glStencilOp(GL.GL_KEEP, GL.GL_REPLACE, GL.GL_REPLACE);
			//
			gl.glCallList(enzymeNodeDisplayListId);

			gl.glDisable(GL.GL_STENCIL_TEST);
			gl.glColorMask(true, true, true, true);
			gl.glEnable(GL.GL_DEPTH_TEST);
			gl.glEnable(GL.GL_BLEND);
			if (enableGeneMapping) {
				Average average = getExpressionAverage(vertexRep);
				if (average != null)
					tmpNodeColor = glPathwayView.getDataDomain().getColorMapper()
							.getColor((float) average.getArithmeticMean());

				if (tmpNodeColor != null) {

					gl.glColor4f(tmpNodeColor[0], tmpNodeColor[1], tmpNodeColor[2], 0.8f);

					if (glPathwayView.getDetailLevel() == EDetailLevel.HIGH) {

						gl.glEnable(GL.GL_STENCIL_TEST);
						gl.glDisable(GL.GL_DEPTH_TEST);
						gl.glDisable(GL.GL_BLEND);
						// gl.glStencilFunc(GL2.GL_EQUAL, 0, 1);
						gl.glStencilFunc(GL.GL_GREATER, 2, 0xff);
						gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_KEEP);
						// gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
						gl.glCallList(enzymeNodeDisplayListId);

						gl.glEnable(GL.GL_DEPTH_TEST);
						gl.glDisable(GL.GL_STENCIL_TEST);
						// gl.glBegin(GL.gl)

						// gl.glEnable(GL.GL_DEPTH_TEST);

						// max std dev is 0.5 -> thus we multiply it with 2
						Float stdDev = pixelGLConverter
								.getGLWidthForPixelWidth(PathwayRenderStyle.ENZYME_NODE_PIXEL_WIDTH)
								* (float) average.getStandardDeviation() * 2.0f;

						float x = pixelGLConverter
								.getGLWidthForPixelWidth(PathwayRenderStyle.ENZYME_NODE_PIXEL_WIDTH + 1);
						float y = -pixelGLConverter
								.getGLHeightForPixelHeight(PathwayRenderStyle.ENZYME_NODE_PIXEL_HEIGHT - 1);

						// rendering the std-dev box
						if (!stdDev.isNaN() && selectedSamplesVA.size() > 1) {
							// ////////////////////////////// v bars
							// // // opaque background
							// gl.glColor4f(1, 1, 1, 1f);
							// gl.glBegin(GL2.GL_QUADS);
							// gl.glVertex3f(x, y - .001f, PathwayRenderStyle.Z_OFFSET);
							// gl.glVertex3f(x + stdBarWidth, y, PathwayRenderStyle.Z_OFFSET);
							// gl.glVertex3f(x + stdBarWidth, 0, PathwayRenderStyle.Z_OFFSET);
							// gl.glVertex3f(x, 0, PathwayRenderStyle.Z_OFFSET);
							// gl.glEnd();
							//
							// gl.glColor4fv(PathwayRenderStyle.STD_DEV_COLOR, 0);
							// gl.glBegin(GL2.GL_QUADS);
							// gl.glVertex3f(x, y, PathwayRenderStyle.Z_OFFSET + 0.01f);
							// gl.glVertex3f(x + stdBarWidth, y, PathwayRenderStyle.Z_OFFSET + 0.01f);
							// gl.glVertex3f(x + stdBarWidth, y + stdDev, PathwayRenderStyle.Z_OFFSET + 0.01f);
							// gl.glVertex3f(x, y + stdDev, PathwayRenderStyle.Z_OFFSET + 0.01f);
							// gl.glEnd();
							//
							// // frame
							// gl.glColor4f(0, 0, 0, 1f);
							// gl.glBegin(GL.GL_LINE_LOOP);
							// gl.glVertex3f(x, y, PathwayRenderStyle.Z_OFFSET + 0.02f);
							// gl.glVertex3f(x + stdBarWidth, y, PathwayRenderStyle.Z_OFFSET + 0.02f);
							// gl.glVertex3f(x + stdBarWidth, 0, PathwayRenderStyle.Z_OFFSET + 0.02f);
							// gl.glVertex3f(x, 0, PathwayRenderStyle.Z_OFFSET + 0.02f);
							// gl.glEnd();
							//
							// // // // create mask
							// gl.glEnable(GL.GL_STENCIL_TEST);
							// gl.glColorMask(false, false, false, false);
							// gl.glDisable(GL.GL_DEPTH_TEST);
							// gl.glDisable(GL.GL_BLEND);
							// gl.glStencilFunc(GL.GL_ALWAYS, 2, 0xff);
							// gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_REPLACE);
							// // //
							// gl.glBegin(GL2.GL_QUADS);
							// gl.glVertex3f(x, y, PathwayRenderStyle.Z_OFFSET);
							// gl.glVertex3f(x + stdBarWidth, y, PathwayRenderStyle.Z_OFFSET);
							// gl.glVertex3f(x + stdBarWidth, 0, PathwayRenderStyle.Z_OFFSET);
							// gl.glVertex3f(x, 0, PathwayRenderStyle.Z_OFFSET);
							// gl.glEnd();
							// gl.glBegin(GL.GL_LINE_LOOP);
							// gl.glVertex3f(x, y, PathwayRenderStyle.Z_OFFSET + 0.02f);
							// gl.glVertex3f(x + stdBarWidth, y, PathwayRenderStyle.Z_OFFSET + 0.02f);
							// gl.glVertex3f(x + stdBarWidth, 0, PathwayRenderStyle.Z_OFFSET + 0.02f);
							// gl.glVertex3f(x, 0, PathwayRenderStyle.Z_OFFSET + 0.02f);
							// gl.glEnd();
							//
							// gl.glDisable(GL.GL_STENCIL_TEST);
							// gl.glColorMask(true, true, true, true);
							// gl.glEnable(GL.GL_DEPTH_TEST);
							// gl.glEnable(GL.GL_BLEND);

							// ////////////////////////////// h bars
							gl.glDisable(GL.GL_BLEND);
							gl.glColor4f(1, 1, 1, 1f);
							gl.glBegin(GL2.GL_QUADS);
							gl.glVertex3f(0, y - (2f * stdDevBarHeight_1_third), PathwayRenderStyle.Z_OFFSET);
							gl.glVertex3f(0, y + stdDevBarHeight_1_third - onePxlHeight, PathwayRenderStyle.Z_OFFSET);
							gl.glVertex3f(x, y + stdDevBarHeight_1_third - onePxlHeight, PathwayRenderStyle.Z_OFFSET);
							gl.glVertex3f(x, y - (2f * stdDevBarHeight_1_third), PathwayRenderStyle.Z_OFFSET);
							gl.glEnd();

							gl.glColor4fv(PathwayRenderStyle.STD_DEV_COLOR, 0);
							gl.glBegin(GL2.GL_QUADS);
							gl.glVertex3f(0, y - (2f * stdDevBarHeight_1_third), PathwayRenderStyle.Z_OFFSET);
							gl.glVertex3f(0, y + stdDevBarHeight_1_third - onePxlHeight, PathwayRenderStyle.Z_OFFSET);
							gl.glVertex3f(stdDev, y + stdDevBarHeight_1_third - onePxlHeight,
									PathwayRenderStyle.Z_OFFSET);
							gl.glVertex3f(stdDev, y - (2f * stdDevBarHeight_1_third), PathwayRenderStyle.Z_OFFSET);
							gl.glEnd();

							// frame
							gl.glDisable(GL.GL_DEPTH_TEST);
							gl.glColor4f(0, 0, 0, 1f);
							gl.glLineWidth(1.f);
							// gl.glEnable(GL.GL_LINE_SMOOTH);

							gl.glDisable(GL.GL_LINE_SMOOTH);
							gl.glBegin(GL.GL_LINE_LOOP);
							gl.glVertex3f(0 + onePxlWidth, y - (2f * stdDevBarHeight_1_third),
									PathwayRenderStyle.Z_OFFSET);
							gl.glVertex3f(0 + onePxlWidth, y + stdDevBarHeight_1_third - onePxlHeight,
									PathwayRenderStyle.Z_OFFSET);
							gl.glVertex3f(x + onePxlWidth, y + stdDevBarHeight_1_third - onePxlHeight,
									PathwayRenderStyle.Z_OFFSET);
							gl.glVertex3f(x + onePxlWidth, y - (2f * stdDevBarHeight_1_third),
									PathwayRenderStyle.Z_OFFSET);
							gl.glEnd();

							// // // create mask
							gl.glEnable(GL.GL_STENCIL_TEST);
							gl.glColorMask(false, false, false, false);
							gl.glDisable(GL.GL_DEPTH_TEST);
							gl.glDisable(GL.GL_BLEND);
							gl.glStencilFunc(GL.GL_GREATER, 2, 0xff);
							gl.glStencilOp(GL.GL_KEEP, GL.GL_REPLACE, GL.GL_REPLACE);
							// //
							gl.glBegin(GL2.GL_QUADS);
							gl.glVertex3f(0, y - (2f * stdDevBarHeight_1_third), PathwayRenderStyle.Z_OFFSET);
							gl.glVertex3f(0, y + stdDevBarHeight_1_third - onePxlHeight, PathwayRenderStyle.Z_OFFSET);
							gl.glVertex3f(x, y + stdDevBarHeight_1_third - onePxlHeight, PathwayRenderStyle.Z_OFFSET);
							gl.glVertex3f(x, y - (2f * stdDevBarHeight_1_third), PathwayRenderStyle.Z_OFFSET);
							gl.glEnd();
							//
							gl.glBegin(GL.GL_LINE_LOOP);
							gl.glVertex3f(0 + onePxlWidth, y - (2f * stdDevBarHeight_1_third),
									PathwayRenderStyle.Z_OFFSET);
							gl.glVertex3f(0 + onePxlWidth, y + stdDevBarHeight_1_third - onePxlHeight,
									PathwayRenderStyle.Z_OFFSET);
							gl.glVertex3f(x + onePxlWidth, y + stdDevBarHeight_1_third - onePxlHeight,
									PathwayRenderStyle.Z_OFFSET);
							gl.glVertex3f(x + onePxlWidth, y - (2f * stdDevBarHeight_1_third),
									PathwayRenderStyle.Z_OFFSET);
							gl.glEnd();
							//
							gl.glDisable(GL.GL_STENCIL_TEST);
							gl.glColorMask(true, true, true, true);
							gl.glEnable(GL.GL_DEPTH_TEST);
							gl.glEnable(GL.GL_BLEND);

						}

						// Handle selection highlighting of element
						if (vertexSelectionManager.checkStatus(SelectionType.SELECTION, vertexRep.getID())) {
							tmpNodeColor = SelectionType.SELECTION.getColor();
							gl.glColor4fv(tmpNodeColor, 0);
							gl.glCallList(framedMappedEnzymeNodeDisplayListId);
							// maskFramedEnzymeNode(gl);
							// // // create mask
							gl.glEnable(GL.GL_STENCIL_TEST);
							gl.glColorMask(false, false, false, false);
							gl.glDisable(GL.GL_DEPTH_TEST);
							gl.glDisable(GL.GL_BLEND);
							gl.glStencilFunc(GL.GL_GREATER, 2, 0xff);
							gl.glStencilOp(GL.GL_KEEP, GL.GL_REPLACE, GL.GL_REPLACE);
							gl.glCallList(framedMappedEnzymeNodeDisplayListId);
							gl.glDisable(GL.GL_STENCIL_TEST);
							gl.glColorMask(true, true, true, true);
							gl.glEnable(GL.GL_DEPTH_TEST);
							gl.glEnable(GL.GL_BLEND);
						} else if (vertexSelectionManager.checkStatus(SelectionType.MOUSE_OVER, vertexRep.getID())) {
							tmpNodeColor = SelectionType.MOUSE_OVER.getColor();
							gl.glColor4fv(tmpNodeColor, 0);
							gl.glCallList(framedMappedEnzymeNodeDisplayListId);
							// maskFramedEnzymeNode(gl);
							// // // create mask
							gl.glEnable(GL.GL_STENCIL_TEST);
							gl.glColorMask(false, false, false, false);
							gl.glDisable(GL.GL_DEPTH_TEST);
							gl.glDisable(GL.GL_BLEND);
							gl.glStencilFunc(GL.GL_GREATER, 2, 0xff);
							gl.glStencilOp(GL.GL_KEEP, GL.GL_REPLACE, GL.GL_REPLACE);
							gl.glCallList(framedMappedEnzymeNodeDisplayListId);
							gl.glDisable(GL.GL_STENCIL_TEST);
							gl.glColorMask(true, true, true, true);
							gl.glEnable(GL.GL_DEPTH_TEST);
							gl.glEnable(GL.GL_BLEND);
						}

					} else {
						// Upscaled version of pathway node needed for e.g.
						// StratomeX
						gl.glCallList(upscaledFilledEnzymeNodeDisplayListId);

						gl.glEnable(GL.GL_STENCIL_TEST);
						gl.glColorMask(false, false, false, false);
						gl.glDisable(GL.GL_DEPTH_TEST);
						gl.glDisable(GL.GL_BLEND);
						gl.glStencilFunc(GL.GL_ALWAYS, 2, 0xff);
						gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_REPLACE);
						//
						gl.glCallList(upscaledFilledEnzymeNodeDisplayListId);

						gl.glDisable(GL.GL_STENCIL_TEST);
						gl.glColorMask(true, true, true, true);
						gl.glEnable(GL.GL_DEPTH_TEST);
						gl.glEnable(GL.GL_BLEND);

						// Handle selection highlighting of element
						if (vertexSelectionManager.checkStatus(SelectionType.SELECTION, vertexRep.getID())) {
							tmpNodeColor = SelectionType.SELECTION.getColor();
							gl.glColor4fv(tmpNodeColor, 0);
							gl.glCallList(upscaledFilledEnzymeNodeDisplayListId);
							// maskFramedEnzymeNode(gl);
							gl.glEnable(GL.GL_STENCIL_TEST);
							gl.glColorMask(false, false, false, false);
							gl.glDisable(GL.GL_DEPTH_TEST);
							gl.glDisable(GL.GL_BLEND);
							// gl.glStencilFunc(GL.GL_ALWAYS, 1, 0xff);
							gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_REPLACE);
							gl.glStencilFunc(GL.GL_ALWAYS, 2, 0xff);

							gl.glCallList(upscaledFilledEnzymeNodeDisplayListId);

							gl.glDisable(GL.GL_STENCIL_TEST);
							gl.glColorMask(true, true, true, true);
							gl.glEnable(GL.GL_DEPTH_TEST);
							gl.glEnable(GL.GL_BLEND);
						} else if (vertexSelectionManager.checkStatus(SelectionType.MOUSE_OVER, vertexRep.getID())) {
							tmpNodeColor = SelectionType.MOUSE_OVER.getColor();
							gl.glColor4fv(tmpNodeColor, 0);
							gl.glCallList(upscaledFilledEnzymeNodeDisplayListId);
							// maskFramedEnzymeNode(gl);
							gl.glEnable(GL.GL_STENCIL_TEST);
							gl.glColorMask(false, false, false, false);
							gl.glDisable(GL.GL_DEPTH_TEST);
							gl.glDisable(GL.GL_BLEND);
							// gl.glStencilFunc(GL.GL_ALWAYS, 1, 0xff);
							gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_REPLACE);
							gl.glStencilFunc(GL.GL_ALWAYS, 2, 0xff);

							gl.glCallList(upscaledFilledEnzymeNodeDisplayListId);

							gl.glDisable(GL.GL_STENCIL_TEST);
							gl.glColorMask(true, true, true, true);
							gl.glEnable(GL.GL_DEPTH_TEST);
							gl.glEnable(GL.GL_BLEND);
						}
					}
				} else {
					// render a black glyph in the corder of the
					// rectangle in order to indicate that we either do
					// not have mapping or data

					// transparent node for picking
					gl.glColor4f(0, 0, 0, 0);
					gl.glCallList(enzymeNodeDisplayListId);

					tmpNodeColor = PathwayRenderStyle.ENZYME_NODE_COLOR;
					gl.glColor4f(tmpNodeColor[0], tmpNodeColor[1], tmpNodeColor[2], 0.7f);
					// gl.glCallList(compoundNodeDisplayListId);
					float boxWidth = glPathwayView.getPixelGLConverter().getGLWidthForPixelWidth(
							PathwayRenderStyle.COMPOUND_NODE_PIXEL_WIDTH);
					float boxHeight = glPathwayView.getPixelGLConverter().getGLHeightForPixelHeight(
							PathwayRenderStyle.COMPOUND_NODE_PIXEL_HEIGHT);
					float y = -pixelGLConverter.getGLHeightForPixelHeight(PathwayRenderStyle.ENZYME_NODE_PIXEL_HEIGHT);

					gl.glDisable(GL.GL_DEPTH_TEST);
					gl.glBegin(GL2.GL_QUADS);
					gl.glNormal3f(0.0f, 0.0f, 1.0f);
					gl.glVertex3f(0, y + boxHeight, PathwayRenderStyle.Z_OFFSET);
					gl.glVertex3f(boxWidth, y + boxHeight, PathwayRenderStyle.Z_OFFSET);
					gl.glVertex3f(boxWidth, y, PathwayRenderStyle.Z_OFFSET);
					gl.glVertex3f(0, y, PathwayRenderStyle.Z_OFFSET);
					gl.glEnd();
					gl.glEnable(GL.GL_DEPTH_TEST);
					//
					// Handle selection highlighting of element
					if (vertexSelectionManager.checkStatus(SelectionType.SELECTION, vertexRep.getID())) {
						tmpNodeColor = SelectionType.SELECTION.getColor();
						gl.glColor4fv(tmpNodeColor, 0);
						gl.glCallList(framedEnzymeNodeDisplayListId);
						maskFramedEnzymeNode(gl);
					} else if (vertexSelectionManager.checkStatus(SelectionType.MOUSE_OVER, vertexRep.getID())) {
						tmpNodeColor = SelectionType.MOUSE_OVER.getColor();
						gl.glColor4fv(tmpNodeColor, 0);
						gl.glCallList(framedEnzymeNodeDisplayListId);
						maskFramedEnzymeNode(gl);
					}
				}
			} else {
				// Handle selection highlighting of element
				if (vertexSelectionManager.checkStatus(SelectionType.SELECTION, vertexRep.getID())) {
					tmpNodeColor = SelectionType.SELECTION.getColor();
					maskFramedEnzymeNode(gl);
				} else if (vertexSelectionManager.checkStatus(SelectionType.MOUSE_OVER, vertexRep.getID())) {
					tmpNodeColor = SelectionType.MOUSE_OVER.getColor();
					maskFramedEnzymeNode(gl);
				} else if (vertexSelectionManager.checkStatus(SelectionType.NORMAL, vertexRep.getID())) {
					tmpNodeColor = PathwayRenderStyle.ENZYME_NODE_COLOR;
				} else {
					tmpNodeColor = new float[] { 0, 0, 0, 0 };
				}

				gl.glColor4fv(tmpNodeColor, 0);
				gl.glCallList(framedEnzymeNodeDisplayListId);

				if (!vertexSelectionManager.checkStatus(SelectionType.DESELECTED, vertexRep.getID())) {

					// Transparent node for picking
					gl.glColor4f(0, 0, 0, 0);
					gl.glCallList(enzymeNodeDisplayListId);

				}
			}

			break;
		default:
			break;
		}

		gl.glTranslatef(-canvasXPos, canvasYPos, 0);

		gl.glPopName();
	}

	private void maskFramedEnzymeNode(final GL2 gl) {
		gl.glEnable(GL.GL_STENCIL_TEST);
		gl.glColorMask(false, false, false, false);
		gl.glDisable(GL.GL_DEPTH_TEST);
		gl.glDisable(GL.GL_BLEND);
		// gl.glStencilFunc(GL.GL_ALWAYS, 1, 0xff);
		gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_REPLACE);
		gl.glStencilFunc(GL.GL_ALWAYS, 2, 0xff);

		gl.glCallList(framedEnzymeNodeDisplayListId);

		gl.glDisable(GL.GL_STENCIL_TEST);
		gl.glColorMask(true, true, true, true);
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glEnable(GL.GL_BLEND);
	}

	private void renderPolyVertex(GL2 gl, PathwayVertexRep vertexRep) {

		// float[] tmpNodeColor = null;
		//
		// ArrayList<Pair<Short, Short>> coords = vertexRep.getCoords();
		//
		// gl.glLineWidth(3);
		// if (enableGeneMapping) {
		//
		// Average average = getExpressionAverage(vertexRep);
		// tmpNodeColor = colorMapper.getColor((float)
		// average.getArithmeticMean());
		// gl.glLineWidth(4);
		//
		// if (tmpNodeColor != null) {
		// gl.glColor3fv(tmpNodeColor, 0);
		//
		// if (glPathwayView.getDetailLevel() == EDetailLevel.HIGH) {
		//
		// gl.glBegin(GL.GL_LINE_STRIP);
		// for (int pointIndex = 0; pointIndex < coords.size(); pointIndex++) {
		// gl.glVertex3f(coords.get(pointIndex).getFirst() *
		// PathwayRenderStyle.SCALING_FACTOR_X, -coords
		// .get(pointIndex).getSecond() * PathwayRenderStyle.SCALING_FACTOR_Y,
		// Z_OFFSET);
		// }
		// gl.glEnd();
		//
		// // Transparent node for picking
		// gl.glColor4f(0, 0, 0, 0);
		// gl.glBegin(GL2.GL_POLYGON);
		// for (int pointIndex = 0; pointIndex < coords.size(); pointIndex++) {
		// gl.glVertex3f(coords.get(pointIndex).getFirst() *
		// PathwayRenderStyle.SCALING_FACTOR_X, -coords
		// .get(pointIndex).getSecond() * PathwayRenderStyle.SCALING_FACTOR_Y,
		// Z_OFFSET);
		// }
		// gl.glEnd();
		// }
		// else {
		// gl.glBegin(GL2.GL_POLYGON);
		// for (int pointIndex = 0; pointIndex < coords.size(); pointIndex++) {
		// gl.glVertex3f(coords.get(pointIndex).getFirst() *
		// PathwayRenderStyle.SCALING_FACTOR_X, -coords
		// .get(pointIndex).getSecond() * PathwayRenderStyle.SCALING_FACTOR_Y,
		// Z_OFFSET);
		// }
		// gl.glEnd();
		//
		// // Handle selection highlighting of element
		// if (vertexSelectionManager.checkStatus(SelectionType.SELECTION,
		// vertexRep.getID())) {
		// tmpNodeColor = SelectionType.SELECTION.getColor();
		// gl.glLineWidth(3);
		// gl.glColor4fv(tmpNodeColor, 0);
		// gl.glBegin(GL.GL_LINE_STRIP);
		// for (int pointIndex = 0; pointIndex < coords.size(); pointIndex++) {
		// gl.glVertex3f(coords.get(pointIndex).getFirst() *
		// PathwayRenderStyle.SCALING_FACTOR_X,
		// -coords.get(pointIndex).getSecond() *
		// PathwayRenderStyle.SCALING_FACTOR_Y, Z_OFFSET);
		// }
		// gl.glEnd();
		// }
		// else if
		// (vertexSelectionManager.checkStatus(SelectionType.MOUSE_OVER,
		// vertexRep.getID())) {
		// tmpNodeColor = SelectionType.MOUSE_OVER.getColor();
		// gl.glLineWidth(3);
		// gl.glColor4fv(tmpNodeColor, 0);
		// gl.glBegin(GL.GL_LINE_STRIP);
		// for (int pointIndex = 0; pointIndex < coords.size(); pointIndex++) {
		// gl.glVertex3f(coords.get(pointIndex).getFirst() *
		// PathwayRenderStyle.SCALING_FACTOR_X,
		// -coords.get(pointIndex).getSecond() *
		// PathwayRenderStyle.SCALING_FACTOR_Y, Z_OFFSET);
		// }
		// gl.glEnd();
		// }
		// }
		// }
		// }
		// else {
		// // Handle selection highlighting of element
		// if (vertexSelectionManager.checkStatus(SelectionType.SELECTION,
		// vertexRep.getID())) {
		// tmpNodeColor = SelectionType.SELECTION.getColor();
		// }
		// else if
		// (vertexSelectionManager.checkStatus(SelectionType.MOUSE_OVER,
		// vertexRep.getID())) {
		// tmpNodeColor = SelectionType.MOUSE_OVER.getColor();
		// }
		// // else if (vertexSelectionManager.checkStatus(
		// // SelectionType.NORMAL, vertexRep.getID())) {
		// // tmpNodeColor = PathwayRenderStyle.ENZYME_NODE_COLOR;
		// // }
		// else {
		// tmpNodeColor = PathwayRenderStyle.ENZYME_NODE_COLOR;
		// // tmpNodeColor = new float[] { 0, 0, 0, 0 };
		// }
		//
		// gl.glColor4fv(tmpNodeColor, 0);
		// gl.glLineWidth(3);
		// gl.glBegin(GL.GL_LINE_STRIP);
		// for (int pointIndex = 0; pointIndex < coords.size(); pointIndex++) {
		// gl.glVertex3f(coords.get(pointIndex).getFirst() *
		// PathwayRenderStyle.SCALING_FACTOR_X,
		// -coords.get(pointIndex).getSecond() *
		// PathwayRenderStyle.SCALING_FACTOR_Y, Z_OFFSET);
		// }
		// gl.glEnd();
		//
		// if (!vertexSelectionManager.checkStatus(SelectionType.DESELECTED,
		// vertexRep.getID())) {
		//
		// // Transparent node for picking
		// gl.glColor4f(0, 0, 0, 0);
		// gl.glBegin(GL2.GL_POLYGON);
		// for (int pointIndex = 0; pointIndex < coords.size(); pointIndex++) {
		// gl.glVertex3f(coords.get(pointIndex).getFirst() *
		// PathwayRenderStyle.SCALING_FACTOR_X,
		// -coords.get(pointIndex).getSecond() *
		// PathwayRenderStyle.SCALING_FACTOR_Y, Z_OFFSET);
		// }
		// gl.glEnd();
		// }
		// }

	}

	// private void createEdge(final GL2 gl, PathwayRelationEdgeRep edgeRep,
	// PathwayGraph containingPathway) {

	// List<IGraphItem> listGraphItemsIn = edgeRep
	// .getAllItemsByProp(EGraphItemProperty.INCOMING);
	// List<IGraphItem> listGraphItemsOut = edgeRep
	// .getAllItemsByProp(EGraphItemProperty.OUTGOING);
	//
	// if (listGraphItemsIn.isEmpty() || listGraphItemsOut.isEmpty())
	// return;
	//
	// float[] tmpColor;
	// float fReactionLineOffset = 0;
	//
	// // Check if edge is a reaction
	// if (edgeRep instanceof PathwayReactionEdgeGraphItemRep) {
	// tmpColor = PathwayRenderStyle.REACTION_EDGE_COLOR;
	// fReactionLineOffset = 0.01f;
	// }
	// // Check if edge is a relation
	// else if (edgeRep instanceof PathwayRelationEdgeGraphItemRep) {
	// tmpColor = PathwayRenderStyle.RELATION_EDGE_COLOR;
	// } else {
	// tmpColor = new float[] { 0, 0, 0, 0 };
	// }
	//
	// gl.glLineWidth(4);
	// gl.glColor4fv(tmpColor, 0);
	// gl.glBegin(GL.GL_LINES);
	//
	// Iterator<IGraphItem> iterSourceGraphItem =
	// listGraphItemsIn.iterator();
	// Iterator<IGraphItem> iterTargetGraphItem =
	// listGraphItemsOut.iterator();
	//
	// PathwayVertexGraphItemRep tmpSourceGraphItem;
	// PathwayVertexGraphItemRep tmpTargetGraphItem;
	// while (iterSourceGraphItem.hasNext()) {
	//
	// tmpSourceGraphItem = (PathwayVertexGraphItemRep)
	// iterSourceGraphItem.next();
	//
	// while (iterTargetGraphItem.hasNext()) {
	// tmpTargetGraphItem = (PathwayVertexGraphItemRep) iterTargetGraphItem
	// .next();
	//
	// gl.glVertex3f(tmpSourceGraphItem.getXOrigin()
	// * PathwayRenderStyle.SCALING_FACTOR_X + fReactionLineOffset,
	// -tmpSourceGraphItem.getYOrigin()
	// * PathwayRenderStyle.SCALING_FACTOR_Y
	// + fReactionLineOffset, 0.02f);
	// gl.glVertex3f(tmpTargetGraphItem.getXOrigin()
	// * PathwayRenderStyle.SCALING_FACTOR_X + fReactionLineOffset,
	// -tmpTargetGraphItem.getYOrigin()
	// * PathwayRenderStyle.SCALING_FACTOR_Y
	// + fReactionLineOffset, 0.02f);
	// }
	// }
	//
	// gl.glEnd();
	// }

	public void renderPathway(final GL2 gl, final PathwayGraph pathway, boolean bRenderLabels) {
		if (enableEdgeRendering || !selectedEdgeRepId.isEmpty()) {
			int tmpEdgesDisplayListID = hashPathway2EdgesDisplayListId.get(pathway);
			gl.glCallList(tmpEdgesDisplayListID);
		}

		Integer tmpVerticesDisplayListID = hashPathway2VerticesDisplayListId.get(pathway);

		if (tmpVerticesDisplayListID != null) {
			gl.glCallList(tmpVerticesDisplayListID);

			// if (bRenderLabels && bEnableAnnotation)
			// renderLabels(gl, iPathwayID);
		}
	}

	/**
	 * Calculates the average value of the selected samples (taken from {@link #selectedSamplesVA}) selectedSamplesVA.
	 *
	 * FIXME: doesn't consider multi-mappings atm.
	 *
	 * @param vertexRep
	 * @return
	 */
	private Average getExpressionAverage(PathwayVertexRep vertexRep) {

		if (selectedSamplesVA == null || selectedSamplesVA.size() == 0)
			return null;

		List<Integer> mappedDavidIds = pathwayItemManager.getDavidIDsByPathwayVertexRep(vertexRep);

		Average average = null;
		for (Integer davidID : mappedDavidIds) {

			Set<Integer> expressionIndices = idMappingManager.<Integer, Integer> getIDAsSet(glPathwayView
					.getPathwayDataDomain().getDavidIDType(), glPathwayView.getDataDomain().getGeneIDType(), davidID);
			if (expressionIndices == null)
				continue;

			for (Integer expressionIndex : expressionIndices) {
				average = TablePerspectiveStatistics.calculateAverage(selectedSamplesVA, glPathwayView.getDataDomain()
						.getTable(), expressionIndex);

			}
			return average;
		}

		return null;
	}

	public void enableEdgeRendering(final boolean bEnableEdgeRendering) {
		this.enableEdgeRendering = bEnableEdgeRendering;
	}

	public void enableGeneMapping(final boolean enableGeneMappging) {
		this.enableGeneMapping = enableGeneMappging;
	}

	public void enableNeighborhood(final boolean bEnableNeighborhood) {
	}
}
