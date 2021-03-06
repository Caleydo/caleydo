/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.pathway;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2GL3;

import org.caleydo.core.data.collection.table.NumericalTable;
import org.caleydo.core.data.perspective.table.Average;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.table.TablePerspectiveStatistics;
import org.caleydo.core.data.selection.EventBasedSelectionManager;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.datadomain.genetic.EGeneIDTypes;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.item.vertex.EPathwayVertexType;
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

	/** The perspective that is being mapped */
	private TablePerspective mappingPerspective;

	private HashMap<PathwayGraph, Integer> hashPathway2VerticesDisplayListId;

	private SelectionManager vertexSelectionManager;

	// private EventBasedSelectionManager pathwaySelectionManager;

	private PathwayItemManager pathwayItemManager;

	/**
	 * The virtual array containing the samples that are currently mapped onto the nodes
	 */
	private VirtualArray selectedSamplesVA;

	private float stdBarHeight;
	private float onePxlWidth;
	private float onePxlHeight;
	private float thirdOfstdDevBarHeight;

	private PixelGLConverter pixelGLConverter;

	boolean isVisible = true;

	public void setVisible(boolean visible) {
		this.isVisible = visible;
	}

	/**
	 * Constructor.
	 */
	public GLPathwayAugmentationRenderer(ViewFrustum viewFrustum, GLPathway glPathwayView) {

		this.glPathwayView = glPathwayView;
		hashPathway2VerticesDisplayListId = new HashMap<PathwayGraph, Integer>();
		pathwayItemManager = PathwayItemManager.get();
		pixelGLConverter = glPathwayView.getPixelGLConverter();

	}

	public void init(final GL2 gl, SelectionManager vertexSelectionManager) {
		// this.pathwaySelectionManager = pathwaySelectionManager;
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

		int verticesDisplayListId = -1;

		if (hashPathway2VerticesDisplayListId.containsKey(pathway)) {
			// Replace current display list if a display list exists
			verticesDisplayListId = hashPathway2VerticesDisplayListId.get(pathway);
		} else {
			// Creating vertex display list for pathways
			verticesDisplayListId = gl.glGenLists(1);
			hashPathway2VerticesDisplayListId.put(pathway, verticesDisplayListId);
		}

		createSelectedSamplesVA();

		gl.glNewList(verticesDisplayListId, GL2.GL_COMPILE);
		renderVertices(gl, pathway);
		gl.glEndList();

	}

	/**
	 * Creates a sample va based on the state of {@link GLPathway#getSampleMappingMode()}
	 */
	private void createSelectedSamplesVA() {
		List<TablePerspective> tablePerspectives = glPathwayView.getTablePerspectives();
		if (tablePerspectives.isEmpty()) {
			selectedSamplesVA = null;
			return;
		}

		Set<Integer> selectedSamples = glPathwayView.getSampleSelectionManager().getElements(SelectionType.SELECTION);
		List<Integer> selectedSamplesArray = new ArrayList<Integer>();

		// Only add selected samples for single pathway
		switch (glPathwayView.getSampleMappingMode()) {
		case ALL:
			selectedSamplesVA = null;
			break;
		case SELECTED:
			selectedSamplesArray.addAll(selectedSamples);
			if (selectedSamplesArray.isEmpty()) {
				selectedSamplesVA = null;
				return;
			}

			selectedSamplesVA = new VirtualArray(glPathwayView.getSampleSelectionManager().getIDType(),
					selectedSamplesArray);
			GroupList groupList = new GroupList();
			groupList.append(new Group(selectedSamplesVA.size()));
			selectedSamplesVA.setGroupList(groupList);

			break;
		default:
			throw new IllegalStateException("Unknown state when switching " + glPathwayView.getSampleMappingMode());
		}

	}

	private void buildEnzymeNodeDisplayList(final GL2 gl) {

		if (enzymeNodeDisplayListId == -1)
			enzymeNodeDisplayListId = gl.glGenLists(1);

		float nodeWidth = pixelGLConverter.getGLWidthForPixelWidth(PathwayRenderStyle.ENZYME_NODE_PIXEL_WIDTH);
		float nodeHeight = pixelGLConverter.getGLHeightForPixelHeight(PathwayRenderStyle.ENZYME_NODE_PIXEL_HEIGHT);

		gl.glNewList(enzymeNodeDisplayListId, GL2.GL_COMPILE);
		renderQuad(gl, nodeWidth, nodeHeight);
		gl.glEndList();
	}

	private void buildUpscaledEnzymeNodeDisplayList(final GL2 gl) {

		if (upscaledFilledEnzymeNodeDisplayListId == -1)
			upscaledFilledEnzymeNodeDisplayListId = gl.glGenLists(1);

		float nodeWidth = pixelGLConverter.getGLWidthForPixelWidth(PathwayRenderStyle.ENZYME_NODE_PIXEL_WIDTH);
		float nodeHeight = pixelGLConverter.getGLHeightForPixelHeight(PathwayRenderStyle.ENZYME_NODE_PIXEL_HEIGHT);

		// float scaleFactor = 1.4f;
		float scaleFactor = 3.f;
		nodeWidth *= scaleFactor;
		nodeHeight *= scaleFactor;

		gl.glNewList(upscaledFilledEnzymeNodeDisplayListId, GL2.GL_COMPILE);
		renderQuad(gl, nodeWidth, nodeHeight);
		gl.glEndList();
	}

	protected void buildUpscaledFramedEnzymeNodeDisplayList(final GL2 gl) {

		if (upscaledFramedEnzymeNodeDisplayListID == -1)
			upscaledFramedEnzymeNodeDisplayListID = gl.glGenLists(1);

		float nodeWidth = pixelGLConverter.getGLWidthForPixelWidth(PathwayRenderStyle.ENZYME_NODE_PIXEL_WIDTH);
		float nodeHeight = pixelGLConverter.getGLHeightForPixelHeight(PathwayRenderStyle.ENZYME_NODE_PIXEL_HEIGHT);

		float scaleFactor = 3.f;
		nodeWidth *= scaleFactor;
		nodeHeight *= scaleFactor;

		gl.glNewList(upscaledFramedEnzymeNodeDisplayListID, GL2.GL_COMPILE);
		renderFrame(gl, nodeWidth, nodeHeight);
		gl.glEndList();
	}

	protected void buildFramedEnzymeNodeDisplayList(final GL2 gl) {

		if (framedEnzymeNodeDisplayListId == -1)
			framedEnzymeNodeDisplayListId = gl.glGenLists(1);
		if (framedMappedEnzymeNodeDisplayListId == -1)
			framedMappedEnzymeNodeDisplayListId = gl.glGenLists(1);

		float nodeWidth = pixelGLConverter.getGLWidthForPixelWidth(PathwayRenderStyle.ENZYME_NODE_PIXEL_WIDTH);
		float nodeHeight = pixelGLConverter.getGLHeightForPixelHeight(PathwayRenderStyle.ENZYME_NODE_PIXEL_HEIGHT);

		stdBarHeight = pixelGLConverter.getGLHeightForPixelHeight(PathwayRenderStyle.STD_DEV_BAR_PIXEL_HEIGHT);
		onePxlWidth = pixelGLConverter.getGLWidthForPixelWidth(1);
		onePxlHeight = pixelGLConverter.getGLHeightForPixelHeight(1);
		thirdOfstdDevBarHeight = (stdBarHeight / 3f);

		gl.glNewList(framedEnzymeNodeDisplayListId, GL2.GL_COMPILE);
		renderFrame(gl, nodeWidth + onePxlWidth, nodeHeight);
		gl.glEndList();

		gl.glNewList(framedMappedEnzymeNodeDisplayListId, GL2.GL_COMPILE);
		renderFrame(gl, nodeWidth + onePxlWidth, nodeHeight + (2f * thirdOfstdDevBarHeight) - onePxlHeight);
		gl.glEndList();
	}

	protected void buildCompoundNodeDisplayList(final GL2 gl) {
		// Creating display list for node cube objects
		if (compoundNodeDisplayListId == -1)
			compoundNodeDisplayListId = gl.glGenLists(1);

		float nodeWidth = pixelGLConverter.getGLWidthForPixelWidth(PathwayRenderStyle.COMPOUND_NODE_PIXEL_WIDTH);
		float nodeHeight = pixelGLConverter.getGLHeightForPixelHeight(PathwayRenderStyle.COMPOUND_NODE_PIXEL_HEIGHT);

		gl.glNewList(compoundNodeDisplayListId, GL2.GL_COMPILE);
		renderQuad(gl, nodeWidth, nodeHeight);
		gl.glEndList();
	}

	protected void buildFramedCompoundNodeDisplayList(final GL2 gl) {
		// Creating display list for node cube objects
		if (framedCompoundNodeDisplayListId == -1)
			framedCompoundNodeDisplayListId = gl.glGenLists(1);

		float nodeWidth = pixelGLConverter.getGLWidthForPixelWidth(PathwayRenderStyle.COMPOUND_NODE_PIXEL_WIDTH);
		float nodeHeight = pixelGLConverter.getGLHeightForPixelHeight(PathwayRenderStyle.COMPOUND_NODE_PIXEL_HEIGHT);

		gl.glNewList(framedCompoundNodeDisplayListId, GL2.GL_COMPILE);
		renderFrame(gl, nodeWidth, nodeHeight);
		gl.glEndList();
	}

	private void renderQuad(final GL2 gl, float nodeWidth, float nodeHeight) {

		gl.glBegin(GL2GL3.GL_QUADS);
		gl.glNormal3f(0.0f, 0.0f, 1.0f);
		gl.glVertex3f(0, 0, PathwayRenderStyle.Z_OFFSET);
		gl.glVertex3f(nodeWidth, 0, PathwayRenderStyle.Z_OFFSET);
		gl.glVertex3f(nodeWidth, nodeHeight, PathwayRenderStyle.Z_OFFSET);
		gl.glVertex3f(0, nodeHeight, PathwayRenderStyle.Z_OFFSET);
		gl.glEnd();
	}

	private void renderFrame(final GL2 gl, float nodeWidth, float nodeHeight) {
		gl.glLineWidth(3);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(0, 0, PathwayRenderStyle.Z_OFFSET + 0.03f);
		gl.glVertex3f(nodeWidth, 0, PathwayRenderStyle.Z_OFFSET + 0.03f);
		gl.glVertex3f(nodeWidth, nodeHeight, PathwayRenderStyle.Z_OFFSET + 0.03f);
		gl.glVertex3f(0, nodeHeight, PathwayRenderStyle.Z_OFFSET + 0.03f);
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

	private void renderVertex(final GL2 gl, PathwayVertexRep vertexRep) {

		Color tmpNodeColor = null;
		gl.glPushName(glPathwayView.getPickingManager().getPickingID(glPathwayView.getID(),
				EPickingType.PATHWAY_ELEMENT_SELECTION.name(), vertexRep.getID()));

		float canvasXPos = pixelGLConverter.getGLWidthForPixelWidth(vertexRep.getCenterX());
		float canvasYPos = pixelGLConverter.getGLHeightForPixelHeight(vertexRep.getCenterY());
		float vertexHeight = pixelGLConverter.getGLHeightForPixelHeight(vertexRep.getHeight());
		canvasYPos += vertexHeight;

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
			// gl.glEnable(GL.GL_STENCIL_TEST);
			// gl.glColorMask(false, false, false, false);
			// gl.glDisable(GL.GL_DEPTH_TEST);
			// // gl.glStencilFunc(GL.GL_ALWAYS,2, 1);
			// gl.glStencilFunc(GL.GL_ALWAYS, 2, 0xff);
			// gl.glStencilOp(GL.GL_REPLACE, GL.GL_REPLACE, GL.GL_REPLACE);
			// renderQuad(gl, nodeWidth, nodeHeight);
			//
			// gl.glStencilFunc(GL.GL_ALWAYS, 2, 0xff);
			// renderFrame(gl, nodeWidth, nodeHeight);
			// //
			// gl.glDisable(GL.GL_STENCIL_TEST);
			// gl.glColorMask(true, true, true, true);
			// gl.glEnable(GL.GL_DEPTH_TEST);

			tmpNodeColor = Color.TRANSPARENT;
			gl.glColor4fv(tmpNodeColor.getRGBA(), 0);
			renderQuad(gl, nodeWidth, nodeHeight);

			gl.glEnable(GL.GL_STENCIL_TEST);
			gl.glColorMask(false, false, false, false);
			gl.glDisable(GL.GL_DEPTH_TEST);
			// // gl.glStencilFunc(GL.GL_ALWAYS,2, 1);
			gl.glStencilFunc(GL.GL_ALWAYS, 2, 0xff);
			gl.glStencilOp(GL.GL_REPLACE, GL.GL_REPLACE, GL.GL_REPLACE);

			// Handle selection highlighting of element
			// PathwayGraph pathway = PathwayManager.get().getPathwayByTitle(vertexRep.getName(),
			// EPathwayDatabaseType.KEGG);
			if (vertexSelectionManager.checkStatus(SelectionType.SELECTION, vertexRep.getID())) {
				// || (pathway != null && pathwaySelectionManager
				// .checkStatus(SelectionType.SELECTION, pathway.getID()))) {
				tmpNodeColor = SelectionType.SELECTION.getColor();
				gl.glColor4fv(tmpNodeColor.getRGBA(), 0);
				renderFrame(gl, nodeWidth, nodeHeight);
			} else if (vertexSelectionManager.checkStatus(SelectionType.MOUSE_OVER, vertexRep.getID())) {
				// || (pathway != null && pathwaySelectionManager.checkStatus(SelectionType.MOUSE_OVER,
				// pathway.getID()))) {
				tmpNodeColor = SelectionType.MOUSE_OVER.getColor();
				gl.glColor4fv(tmpNodeColor.getRGBA(), 0);
				renderFrame(gl, nodeWidth, nodeHeight);
			}
			gl.glDisable(GL.GL_STENCIL_TEST);
			gl.glColorMask(true, true, true, true);
			gl.glEnable(GL.GL_DEPTH_TEST);

			if (vertexSelectionManager.checkStatus(SelectionType.SELECTION, vertexRep.getID())) {
				// || (pathway != null && pathwaySelectionManager
				// .checkStatus(SelectionType.SELECTION, pathway.getID()))) {
				tmpNodeColor = SelectionType.SELECTION.getColor();
				gl.glColor4fv(tmpNodeColor.getRGBA(), 0);
				renderFrame(gl, nodeWidth, nodeHeight);
			} else if (vertexSelectionManager.checkStatus(SelectionType.MOUSE_OVER, vertexRep.getID())) {
				// || (pathway != null && pathwaySelectionManager.checkStatus(SelectionType.MOUSE_OVER,
				// pathway.getID()))) {
				tmpNodeColor = SelectionType.MOUSE_OVER.getColor();
				gl.glColor4fv(tmpNodeColor.getRGBA(), 0);
				renderFrame(gl, nodeWidth, nodeHeight);
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

				gl.glColor4fv(tmpNodeColor.getRGBA(), 0);
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

				gl.glColor4fv(tmpNodeColor.getRGBA(), 0);
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

			gl.glColor4fv(tmpNodeColor.getRGBA(), 0);
			gl.glCallList(compoundNodeDisplayListId);

			break;
		case group:

			// gl.glColor4f(1, 1, 0, 1);
			// fillNodeDisplayList(gl, nodeWidth, nodeHeight);
			break;
		case gene:
		case enzyme:
		case other:
			if (isVisible)
				renderGeneNode(gl, vertexRep);
			else {// mask only
				float width = pixelGLConverter.getGLWidthForPixelWidth(vertexRep.getWidth());
				float height = pixelGLConverter.getGLHeightForPixelHeight(vertexRep.getHeight());

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

				// Handle selection highlighting of element
				Color nodeColor;

				if (vertexSelectionManager.checkStatus(SelectionType.SELECTION, vertexRep.getID())) {
					nodeColor = SelectionType.SELECTION.getColor();
					maskFramedEnzymeNode(gl, width, height);
				} else if (vertexSelectionManager.checkStatus(SelectionType.MOUSE_OVER, vertexRep.getID())) {
					nodeColor = SelectionType.MOUSE_OVER.getColor();
					maskFramedEnzymeNode(gl, width, height);
				} else if (vertexSelectionManager.checkStatus(SelectionType.NORMAL, vertexRep.getID())) {
					nodeColor = PathwayRenderStyle.ENZYME_NODE_COLOR;
				} else {
					nodeColor = Color.TRANSPARENT;
				}

				gl.glColor4fv(nodeColor.getRGBA(), 0);
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

	private void renderGeneNode(GL2 gl, PathwayVertexRep vertexRep) {

		float[] nodeColor;

		float width = pixelGLConverter.getGLWidthForPixelWidth(vertexRep.getWidth());
		float height = pixelGLConverter.getGLHeightForPixelHeight(vertexRep.getHeight());

		gl.glLineWidth(1);
		// // create mask
		gl.glEnable(GL.GL_STENCIL_TEST);
		gl.glColorMask(false, false, false, false);
		gl.glDisable(GL.GL_DEPTH_TEST);
		gl.glDisable(GL.GL_BLEND);
		gl.glStencilFunc(GL.GL_GREATER, 1, 0xff);
		gl.glStencilOp(GL.GL_KEEP, GL.GL_REPLACE, GL.GL_REPLACE);
		//
		// gl.glCallList(enzymeNodeDisplayListId);
		renderQuad(gl, width, height);

		gl.glDisable(GL.GL_STENCIL_TEST);
		gl.glColorMask(true, true, true, true);
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glEnable(GL.GL_BLEND);

		if (mappingPerspective != null) {
			Average average = getExpressionAverage(mappingPerspective, vertexRep);
			if (average != null) {
				nodeColor = mappingPerspective.getDataDomain().getTable().getColorMapper()
						.getColor((float) average.getArithmeticMean());
			} else {
				nodeColor = null;
			}
			if (average != null && nodeColor != null) {
				// System.out.println(pixelGLConverter.getPixelWidthForGLWidth(glPathwayView.getViewFrustum().getWidth()));
				// if (pixelGLConverter.getPixelWidthForGLWidth(glPathwayView.getViewFrustum().getWidth()) < 300
				// || pixelGLConverter.getPixelHeightForGLHeight(glPathwayView.getViewFrustum().getHeight()) < 300) {
				// return;
				// }

				gl.glColor4f(nodeColor[0], nodeColor[1], nodeColor[2], 0.8f);
				if (glPathwayView.getDetailLevel() == EDetailLevel.HIGH) {

					gl.glEnable(GL.GL_STENCIL_TEST);
					gl.glDisable(GL.GL_DEPTH_TEST);
					gl.glDisable(GL.GL_BLEND);
					gl.glStencilFunc(GL.GL_GREATER, 2, 0xff);
					gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_KEEP);
					// gl.glCallList(enzymeNodeDisplayListId);

					// gl.glTranslatef(0, 0, 0.5f);
					renderQuad(gl, width, height);
					// gl.glTranslatef(0, 0, -0.5f);
					gl.glEnable(GL.GL_DEPTH_TEST);
					gl.glDisable(GL.GL_STENCIL_TEST);

					// max std dev is 0.5 -> thus we multiply it with 2
					Float stdDev = pixelGLConverter.getGLWidthForPixelWidth(PathwayRenderStyle.ENZYME_NODE_PIXEL_WIDTH)
							* (float) average.getStandardDeviation() * 2.0f;

					// rendering the std-dev box
					if (!stdDev.isNaN() && this.glPathwayView.isShowStdDevBars()) {
						renderStdDevBar(gl, width, height, stdDev);
					} else {
						int i = 0;
					}

					// Handle selection highlighting of element
					gl.glPushMatrix();
					gl.glTranslatef(0, -(2f * thirdOfstdDevBarHeight - onePxlHeight), 0);
					if (vertexSelectionManager.checkStatus(SelectionType.SELECTION, vertexRep.getID())) {
						nodeColor = SelectionType.SELECTION.getColor().getRGBA();
						gl.glColor4fv(nodeColor, 0);
						renderFrame(gl, width + onePxlWidth, height + (2f * thirdOfstdDevBarHeight) - onePxlHeight);
						// gl.glCallList(framedMappedEnzymeNodeDisplayListId);
						// maskFramedEnzymeNode(gl);
						// // // create mask
						gl.glEnable(GL.GL_STENCIL_TEST);
						gl.glColorMask(false, false, false, false);
						gl.glDisable(GL.GL_DEPTH_TEST);
						gl.glDisable(GL.GL_BLEND);
						gl.glStencilFunc(GL.GL_GREATER, 2, 0xff);
						gl.glStencilOp(GL.GL_KEEP, GL.GL_REPLACE, GL.GL_REPLACE);
						renderFrame(gl, width + onePxlWidth, height + (2f * thirdOfstdDevBarHeight) - onePxlHeight);
						// gl.glCallList(framedMappedEnzymeNodeDisplayListId);
						gl.glDisable(GL.GL_STENCIL_TEST);
						gl.glColorMask(true, true, true, true);
						gl.glEnable(GL.GL_DEPTH_TEST);
						gl.glEnable(GL.GL_BLEND);
					} else if (vertexSelectionManager.checkStatus(SelectionType.MOUSE_OVER, vertexRep.getID())) {
						nodeColor = SelectionType.MOUSE_OVER.getColor().getRGBA();
						gl.glColor4fv(nodeColor, 0);
						renderFrame(gl, width + onePxlWidth, height + (2f * thirdOfstdDevBarHeight) - onePxlHeight);
						// gl.glCallList(framedMappedEnzymeNodeDisplayListId);
						// maskFramedEnzymeNode(gl);
						// // // create mask
						gl.glEnable(GL.GL_STENCIL_TEST);
						gl.glColorMask(false, false, false, false);
						gl.glDisable(GL.GL_DEPTH_TEST);
						gl.glDisable(GL.GL_BLEND);
						gl.glStencilFunc(GL.GL_GREATER, 2, 0xff);
						gl.glStencilOp(GL.GL_KEEP, GL.GL_REPLACE, GL.GL_REPLACE);
						renderFrame(gl, width + onePxlWidth, height + (2f * thirdOfstdDevBarHeight) - onePxlHeight);
						// gl.glCallList(framedMappedEnzymeNodeDisplayListId);
						gl.glDisable(GL.GL_STENCIL_TEST);
						gl.glColorMask(true, true, true, true);
						gl.glEnable(GL.GL_DEPTH_TEST);
						gl.glEnable(GL.GL_BLEND);
					}
					gl.glPopMatrix();

				} else { // detail level is not high

					// Upscaled version of pathway node needed for e.g. StratomeX. We make nodes bigger that are far
					// from the mean.
					// FIXME We guess the mean to be 0.5 in normalized space if we don't know better.
					float dsMean = 0.5f;
					// we scale the deviation
					float scaleConstant = 20;
					if (mappingPerspective.getDataDomain().getTable() instanceof NumericalTable) {
						NumericalTable table = (NumericalTable) mappingPerspective.getDataDomain().getTable();
						dsMean = (float) table.getDatasetStatistics().getMean();
						dsMean = (float) table.getNormalizedForRaw(table.getDefaultDataTransformation(), dsMean);
					}

					float deviation = 1 + Math.abs(dsMean - (float) average.getArithmeticMean()) * scaleConstant;
					// limit to 4 times the size
					if (deviation > 4)
						deviation = 4;
					renderQuad(gl, width * deviation, height * deviation);
					// gl.glCallList(upscaledFilledEnzymeNodeDisplayListId);

					gl.glEnable(GL.GL_STENCIL_TEST);
					gl.glColorMask(false, false, false, false);
					gl.glDisable(GL.GL_DEPTH_TEST);
					gl.glDisable(GL.GL_BLEND);
					gl.glStencilFunc(GL.GL_ALWAYS, 2, 0xff);
					gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_REPLACE);
					//
					renderQuad(gl, width * deviation, height * deviation);

					// gl.glCallList(upscaledFilledEnzymeNodeDisplayListId);

					gl.glDisable(GL.GL_STENCIL_TEST);
					gl.glColorMask(true, true, true, true);
					gl.glEnable(GL.GL_DEPTH_TEST);
					gl.glEnable(GL.GL_BLEND);

					// Handle selection highlighting of element
					if (vertexSelectionManager.checkStatus(SelectionType.SELECTION, vertexRep.getID())) {
						nodeColor = SelectionType.SELECTION.getColor().getRGBA();
						gl.glColor4fv(nodeColor, 0);
						renderQuad(gl, width * deviation, height * deviation);

						// gl.glCallList(upscaledFilledEnzymeNodeDisplayListId);
						// maskFramedEnzymeNode(gl);
						gl.glEnable(GL.GL_STENCIL_TEST);
						gl.glColorMask(false, false, false, false);
						gl.glDisable(GL.GL_DEPTH_TEST);
						gl.glDisable(GL.GL_BLEND);
						// gl.glStencilFunc(GL.GL_ALWAYS, 1, 0xff);
						gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_REPLACE);
						gl.glStencilFunc(GL.GL_ALWAYS, 2, 0xff);
						renderQuad(gl, width * deviation, height * deviation);

						// gl.glCallList(upscaledFilledEnzymeNodeDisplayListId);

						gl.glDisable(GL.GL_STENCIL_TEST);
						gl.glColorMask(true, true, true, true);
						gl.glEnable(GL.GL_DEPTH_TEST);
						gl.glEnable(GL.GL_BLEND);
					} else if (vertexSelectionManager.checkStatus(SelectionType.MOUSE_OVER, vertexRep.getID())) {
						nodeColor = SelectionType.MOUSE_OVER.getColor().getRGBA();
						gl.glColor4fv(nodeColor, 0);
						renderQuad(gl, width * deviation, height * deviation);

						// gl.glCallList(upscaledFilledEnzymeNodeDisplayListId);
						// maskFramedEnzymeNode(gl);
						gl.glEnable(GL.GL_STENCIL_TEST);
						gl.glColorMask(false, false, false, false);
						gl.glDisable(GL.GL_DEPTH_TEST);
						gl.glDisable(GL.GL_BLEND);
						// gl.glStencilFunc(GL.GL_ALWAYS, 1, 0xff);
						gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_REPLACE);
						gl.glStencilFunc(GL.GL_ALWAYS, 2, 0xff);
						renderQuad(gl, width * deviation, height * deviation);

						// gl.glCallList(upscaledFilledEnzymeNodeDisplayListId);

						gl.glDisable(GL.GL_STENCIL_TEST);
						gl.glColorMask(true, true, true, true);
						gl.glEnable(GL.GL_DEPTH_TEST);
						gl.glEnable(GL.GL_BLEND);
					}
				}
			} else { // there is no mapping
				renderNonMappingGlyph(gl, width, height);
				//
				// Handle selection highlighting of element
				if (vertexSelectionManager.checkStatus(SelectionType.SELECTION, vertexRep.getID())) {
					nodeColor = SelectionType.SELECTION.getColor().getRGBA();
					gl.glColor4fv(nodeColor, 0);
					renderFrame(gl, width + onePxlWidth, height + thirdOfstdDevBarHeight);
					// gl.glCallList(framedEnzymeNodeDisplayListId);
					maskFramedEnzymeNode(gl, width, height);
				} else if (vertexSelectionManager.checkStatus(SelectionType.MOUSE_OVER, vertexRep.getID())) {
					nodeColor = SelectionType.MOUSE_OVER.getColor().getRGBA();
					gl.glColor4fv(nodeColor, 0);
					renderFrame(gl, width + onePxlWidth, height + thirdOfstdDevBarHeight);
					// gl.glCallList(framedEnzymeNodeDisplayListId);
					maskFramedEnzymeNode(gl, width, height);
				}
			}

		} else {
			// Handle selection highlighting of element
			nodeColor = null;
			if (vertexSelectionManager.checkStatus(SelectionType.SELECTION, vertexRep.getID())) {
				nodeColor = SelectionType.SELECTION.getColor().getRGBA();
				maskFramedEnzymeNode(gl, width, height);
			} else if (vertexSelectionManager.checkStatus(SelectionType.MOUSE_OVER, vertexRep.getID())) {
				nodeColor = SelectionType.MOUSE_OVER.getColor().getRGBA();
				maskFramedEnzymeNode(gl, width, height);
			} else if (vertexSelectionManager.checkStatus(SelectionType.NORMAL, vertexRep.getID())) {
				nodeColor = PathwayRenderStyle.ENZYME_NODE_COLOR.getRGBA();
			}

			if (nodeColor != null) {
				gl.glColor4fv(nodeColor, 0);
				renderFrame(gl, width + onePxlWidth, height + thirdOfstdDevBarHeight);
				gl.glCallList(framedEnzymeNodeDisplayListId);
			}

			if (!vertexSelectionManager.checkStatus(SelectionType.DESELECTED, vertexRep.getID())) {

				// Transparent node for picking
				gl.glColor4f(0, 0, 0, 0);
				renderQuad(gl, width, height);
				// gl.glCallList(enzymeNodeDisplayListId);

			}
		}

		if (glPathwayView.getDetailLevel() == EDetailLevel.HIGH) {

			renderExclamationMark(gl, vertexRep, width, height);
			renderMultiMappingGlyph(gl, width, height, vertexRep);
		}

	}

	private void renderMultiMappingGlyph(final GL2 gl, final float nodeWidth, final float nodeHeight,
			PathwayVertexRep vertexRep) {
		if (vertexRep.getPathwayVertices().size() > 1) {

			float width = pixelGLConverter.getGLWidthForPixelWidth(PathwayRenderStyle.COMPOUND_NODE_PIXEL_WIDTH);

			float[] nodeColor = PathwayRenderStyle.ENZYME_NODE_COLOR.getRGBA();
			gl.glColor4fv(nodeColor, 0);

			gl.glBegin(GL.GL_TRIANGLES);
			gl.glVertex3f(0, nodeHeight, PathwayRenderStyle.Z_OFFSET);
			gl.glVertex3f(width, nodeHeight, PathwayRenderStyle.Z_OFFSET);
			gl.glVertex3f(0, nodeHeight - width, PathwayRenderStyle.Z_OFFSET);
			gl.glEnd();
		}
	}

	private void renderNonMappingGlyph(final GL2 gl, final float nodeWidth, final float nodeHeight) {
		// render a black glyph in the corder of the
		// rectangle in order to indicate that we either do
		// not have mapping or data

		// make unmapped nodes white
		gl.glColor4f(1, 1, 1, 1);
		renderQuad(gl, nodeWidth, nodeHeight);
		// gl.glCallList(enzymeNodeDisplayListId);

		float[] nodeColor = PathwayRenderStyle.ENZYME_NODE_COLOR.getRGBA();
		gl.glColor4f(nodeColor[0], nodeColor[1], nodeColor[2], 0.7f);
		// gl.glCallList(compoundNodeDisplayListId);
		float boxWidth = pixelGLConverter.getGLWidthForPixelWidth(PathwayRenderStyle.COMPOUND_NODE_PIXEL_WIDTH);
		float boxHeight = pixelGLConverter.getGLHeightForPixelHeight(PathwayRenderStyle.COMPOUND_NODE_PIXEL_HEIGHT);

		gl.glDisable(GL.GL_DEPTH_TEST);
		gl.glBegin(GL2GL3.GL_QUADS);
		gl.glNormal3f(0.0f, 0.0f, 1.0f);
		gl.glVertex3f(nodeWidth - boxWidth, boxHeight, PathwayRenderStyle.Z_OFFSET);
		gl.glVertex3f(nodeWidth, boxHeight, PathwayRenderStyle.Z_OFFSET);
		gl.glVertex3f(nodeWidth, 0, PathwayRenderStyle.Z_OFFSET);
		gl.glVertex3f(nodeWidth - boxWidth, 0, PathwayRenderStyle.Z_OFFSET);
		gl.glEnd();
		gl.glEnable(GL.GL_DEPTH_TEST);
	}

	private void renderExclamationMark(final GL2 gl, PathwayVertexRep vertexRep, final float nodeWidth,
			final float nodeHeight) {
		// rendering the exclamation mark
		float threshold = 0.2f;
		Pair<TablePerspective, Average> highestAverage = null;
		Average average;
		for (TablePerspective tablePerspective : glPathwayView.getTablePerspectives()) {
			// if (tablePerspective.getDataDomain().getLabel().contains("RNA"))
			// continue;
			average = getExpressionAverage(tablePerspective, vertexRep);
			if (average == null)
				continue;
			if (average.getStandardDeviation() > threshold) {
				if (highestAverage == null
						|| average.getStandardDeviation() > highestAverage.getSecond().getStandardDeviation()) {
					highestAverage = new Pair<>(tablePerspective, average);
				}
			}
		}

		if (highestAverage != null) {

			gl.glColor3fv(highestAverage.getFirst().getDataDomain().getColor().darker().getRGB(), 0);
			// gl.glColor3f(1, 0, 0);
			gl.glBegin(GL2.GL_POLYGON);
			gl.glVertex3f(nodeWidth, nodeHeight, PathwayRenderStyle.Z_OFFSET);
			gl.glVertex3f(nodeWidth - 5 * onePxlWidth, nodeHeight, PathwayRenderStyle.Z_OFFSET);
			gl.glVertex3f(nodeWidth - 4 * onePxlWidth, nodeHeight - 10 * onePxlWidth, PathwayRenderStyle.Z_OFFSET);
			gl.glVertex3f(nodeWidth - 1 * onePxlWidth, nodeHeight - 10 * onePxlWidth, PathwayRenderStyle.Z_OFFSET);
			gl.glEnd();

			// gl.glColor3fv(tablePerspective.getDataDomain().getColor().getRGB(), 0);
			gl.glBegin(GL2.GL_POLYGON);
			gl.glVertex3f(nodeWidth, 5 * onePxlWidth, PathwayRenderStyle.Z_OFFSET);
			gl.glVertex3f(nodeWidth - 5 * onePxlWidth, 5 * onePxlWidth, PathwayRenderStyle.Z_OFFSET);
			gl.glVertex3f(nodeWidth - 5 * onePxlWidth, 0, PathwayRenderStyle.Z_OFFSET);
			gl.glVertex3f(nodeWidth, 0, PathwayRenderStyle.Z_OFFSET);
			gl.glEnd();

		}
	}

	private void renderStdDevBar(final GL2 gl, float nodeWidth, float nodeHeight, float stdDev) {

		// ////////////////////////////// h bars
		gl.glDisable(GL.GL_BLEND);

		float bottom = -(2f * thirdOfstdDevBarHeight);
		float top = thirdOfstdDevBarHeight - onePxlHeight;
		// background white
		gl.glColor4f(1, 1, 1, 1f);
		gl.glBegin(GL2GL3.GL_QUADS);
		gl.glVertex3f(0, bottom, PathwayRenderStyle.Z_OFFSET);
		gl.glVertex3f(0, top, PathwayRenderStyle.Z_OFFSET);
		gl.glVertex3f(nodeWidth + onePxlWidth, top, PathwayRenderStyle.Z_OFFSET);
		gl.glVertex3f(nodeWidth + onePxlWidth, bottom, PathwayRenderStyle.Z_OFFSET);
		gl.glEnd();

		// the actual bar
		gl.glColor3fv(mappingPerspective.getDataDomain().getColor().darker().getRGB(), 0);
		// gl.glColor3f(49f / 255f, 163f / 255, 84f / 255);
		gl.glBegin(GL2GL3.GL_QUADS);
		gl.glVertex3f(0, bottom, PathwayRenderStyle.Z_OFFSET);
		gl.glVertex3f(0, top, PathwayRenderStyle.Z_OFFSET);
		gl.glVertex3f(stdDev, top, PathwayRenderStyle.Z_OFFSET);
		gl.glVertex3f(stdDev, bottom, PathwayRenderStyle.Z_OFFSET);
		gl.glEnd();

		// frame
		gl.glDisable(GL.GL_DEPTH_TEST);
		gl.glColor4f(0, 0, 0, 1f);
		gl.glLineWidth(1.f);
		// gl.glEnable(GL.GL_LINE_SMOOTH);

		gl.glDisable(GL.GL_LINE_SMOOTH);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(0, bottom, PathwayRenderStyle.Z_OFFSET);
		gl.glVertex3f(0, top, PathwayRenderStyle.Z_OFFSET);
		gl.glVertex3f(nodeWidth + onePxlWidth, top, PathwayRenderStyle.Z_OFFSET);
		gl.glVertex3f(nodeWidth + onePxlWidth, bottom, PathwayRenderStyle.Z_OFFSET);
		gl.glEnd();

		// // // create mask
		gl.glEnable(GL.GL_STENCIL_TEST);
		gl.glColorMask(false, false, false, false);
		gl.glDisable(GL.GL_DEPTH_TEST);
		gl.glDisable(GL.GL_BLEND);
		gl.glStencilFunc(GL.GL_GREATER, 2, 0xff);
		gl.glStencilOp(GL.GL_KEEP, GL.GL_REPLACE, GL.GL_REPLACE);
		// //
		gl.glBegin(GL2GL3.GL_QUADS);
		gl.glVertex3f(0, bottom, PathwayRenderStyle.Z_OFFSET);
		gl.glVertex3f(0, top, PathwayRenderStyle.Z_OFFSET);
		gl.glVertex3f(nodeWidth + onePxlWidth, top, PathwayRenderStyle.Z_OFFSET);
		gl.glVertex3f(nodeWidth + onePxlWidth, bottom, PathwayRenderStyle.Z_OFFSET);
		gl.glEnd();
		//
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(0, bottom, PathwayRenderStyle.Z_OFFSET);
		gl.glVertex3f(0, top, PathwayRenderStyle.Z_OFFSET);
		gl.glVertex3f(nodeWidth + onePxlWidth, top, PathwayRenderStyle.Z_OFFSET);
		gl.glVertex3f(nodeWidth + onePxlWidth, bottom, PathwayRenderStyle.Z_OFFSET);
		gl.glEnd();
		//
		gl.glDisable(GL.GL_STENCIL_TEST);
		gl.glColorMask(true, true, true, true);
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glEnable(GL.GL_BLEND);
	}

	private void maskFramedEnzymeNode(final GL2 gl, float width, float height) {
		gl.glEnable(GL.GL_STENCIL_TEST);
		gl.glColorMask(false, false, false, false);
		gl.glDisable(GL.GL_DEPTH_TEST);
		gl.glDisable(GL.GL_BLEND);
		// gl.glStencilFunc(GL.GL_ALWAYS, 1, 0xff);
		gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_REPLACE);
		gl.glStencilFunc(GL.GL_ALWAYS, 2, 0xff);

		renderFrame(gl, width + onePxlWidth, height);
		// gl.glCallList(framedEnzymeNodeDisplayListId);

		gl.glDisable(GL.GL_STENCIL_TEST);
		gl.glColorMask(true, true, true, true);
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glEnable(GL.GL_BLEND);
	}

	public void renderPathway(final GL2 gl, final PathwayGraph pathway, boolean bRenderLabels) {

		Integer tmpVerticesDisplayListID = hashPathway2VerticesDisplayListId.get(pathway);

		if (tmpVerticesDisplayListID != null) {
			gl.glCallList(tmpVerticesDisplayListID);

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
	private Average getExpressionAverage(TablePerspective tablePerspective, PathwayVertexRep vertexRep) {

		if (selectedSamplesVA != null && selectedSamplesVA.size() == 0)
			return null;

		List<Integer> mappedDavidIds = pathwayItemManager.getDavidIDsByPathwayVertexRep(vertexRep);

		final IDType david = IDType.getIDType(EGeneIDTypes.DAVID.name());

		Average average = null;
		if (selectedSamplesVA == null) {
			average = tablePerspective.getContainerStatistics().getAverage(david, mappedDavidIds);
		} else {
			average = TablePerspectiveStatistics.calculateAverage(selectedSamplesVA, tablePerspective.getDataDomain(),
					david, mappedDavidIds);
			if (Double.isNaN(average.getArithmeticMean()))
				return null;
		}
		return average;
	}

	/**
	 * @param mappingPerspective
	 *            setter, see {@link mappingPerspective}
	 */
	public void setMappingPerspective(TablePerspective mappingPerspective) {
		this.mappingPerspective = mappingPerspective;
	}

	/**
	 * @return the mappingPerspective, see {@link #mappingPerspective}
	 */
	public TablePerspective getMappingPerspective() {
		return mappingPerspective;
	}

}
