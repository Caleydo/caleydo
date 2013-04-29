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
package org.caleydo.view.enroute.path;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.util.connectionline.ClosedArrowRenderer;
import org.caleydo.core.view.opengl.util.connectionline.ConnectionLineRenderer;
import org.caleydo.core.view.opengl.util.connectionline.LineCrossingRenderer;
import org.caleydo.core.view.opengl.util.connectionline.LineEndArrowRenderer;
import org.caleydo.core.view.opengl.util.connectionline.LineEndStaticLineRenderer;
import org.caleydo.core.view.opengl.util.connectionline.LineLabelRenderer;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.item.edge.EPathwayReactionEdgeType;
import org.caleydo.datadomain.pathway.graph.item.edge.EPathwayRelationEdgeSubType;
import org.caleydo.datadomain.pathway.graph.item.edge.PathwayReactionEdgeRep;
import org.caleydo.datadomain.pathway.graph.item.edge.PathwayRelationEdgeRep;
import org.caleydo.datadomain.pathway.graph.item.vertex.EPathwayVertexType;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.view.enroute.path.node.ALinearizableNode;
import org.jgrapht.graph.DefaultEdge;

/**
 * Utility class for rendering pathway edges.
 *
 * @author Christian Partl
 *
 */
public final class EdgeRenderUtil {

	private EdgeRenderUtil() {
	}

	public static void renderEdge(GL2 gl, ALinearizableNode node1, ALinearizableNode node2, Vec3f node1ConnectionPoint,
			Vec3f node2ConnectionPoint, float zCoordinate, boolean isVerticalConnection,
			PixelGLConverter pixelGLConverter, CaleydoTextRenderer textRenderer, PathSizeConfiguration sizeConfig) {

		List<PathwayVertexRep> node1VertexReps = node1.getVertexReps();
		List<PathwayVertexRep> node2VertexReps = node2.getVertexReps();
		PathwayGraph pathway = null;
		DefaultEdge edge = null;
		PathwayVertexRep vertexRep1 = null;
		for (PathwayVertexRep node1VertexRep : node1VertexReps) {
			for (PathwayVertexRep node2VertexRep : node2VertexReps) {
				if (node1VertexRep.getPathway() == node2VertexRep.getPathway()) {
					pathway = node1VertexRep.getPathway();
					vertexRep1 = node1VertexRep;
					edge = pathway.getEdge(node1VertexRep, node2VertexRep);
					if (edge == null) {
						edge = pathway.getEdge(node2VertexRep, node1VertexRep);
					}
					if (edge != null)
						break;
				}
			}
			if (edge != null)
				break;
		}
		if (edge == null || pathway == null || vertexRep1 == null)
			return;

		// PathwayVertexRep vertexRep1 = node1.getPrimaryPathwayVertexRep();
		// PathwayVertexRep vertexRep2 = node2.getPrimaryPathwayVertexRep();

		ConnectionLineRenderer connectionRenderer = new ConnectionLineRenderer();
		List<Vec3f> linePoints = new ArrayList<Vec3f>();

		boolean isNode1Target = pathway.getEdgeTarget(edge) == vertexRep1;

		Vec3f sourceConnectionPoint = (isNode1Target) ? node2ConnectionPoint : node1ConnectionPoint;
		Vec3f targetConnectionPoint = (isNode1Target) ? node1ConnectionPoint : node2ConnectionPoint;

		sourceConnectionPoint.setZ(zCoordinate);
		targetConnectionPoint.setZ(zCoordinate);

		linePoints.add(sourceConnectionPoint);
		linePoints.add(targetConnectionPoint);

		if (edge instanceof PathwayRelationEdgeRep) {
			PathwayRelationEdgeRep relationEdgeRep = (PathwayRelationEdgeRep) edge;

			ArrayList<EPathwayRelationEdgeSubType> subtypes = relationEdgeRep.getRelationSubTypes();
			float spacing = pixelGLConverter.getGLHeightForPixelHeight(3);

			for (EPathwayRelationEdgeSubType subtype : subtypes) {
				switch (subtype) {
				case compound:
					// TODO:
					break;
				case hidden_compound:
					// TODO:
					break;
				case activation:
					connectionRenderer.addAttributeRenderer(createDefaultLineEndArrowRenderer(pixelGLConverter,
							sizeConfig));
					break;
				case inhibition:
					connectionRenderer.addAttributeRenderer(createDefaultLineEndStaticLineRenderer(
							isVerticalConnection, pixelGLConverter, sizeConfig));
					if (isVerticalConnection) {
						targetConnectionPoint.setY(targetConnectionPoint.y() + ((isNode1Target) ? -spacing : spacing));
					} else {
						targetConnectionPoint.setX(targetConnectionPoint.x() + ((isNode1Target) ? spacing : -spacing));
					}
					break;
				case expression:
					connectionRenderer.addAttributeRenderer(createDefaultLineEndArrowRenderer(pixelGLConverter,
							sizeConfig));
					if (vertexRep1.getType() == EPathwayVertexType.gene
							&& vertexRep1.getType() == EPathwayVertexType.gene) {
						connectionRenderer.addAttributeRenderer(createDefaultLabelOnLineRenderer("e", pixelGLConverter,
								textRenderer, sizeConfig));
					}
					break;
				case repression:
					connectionRenderer.addAttributeRenderer(createDefaultLineEndArrowRenderer(pixelGLConverter,
							sizeConfig));
					connectionRenderer.addAttributeRenderer(createDefaultLineEndStaticLineRenderer(
							isVerticalConnection, pixelGLConverter, sizeConfig));
					targetConnectionPoint.setY(targetConnectionPoint.y() + ((isNode1Target) ? -spacing : spacing));
					break;
				case indirect_effect:
					connectionRenderer.addAttributeRenderer(createDefaultLineEndArrowRenderer(pixelGLConverter,
							sizeConfig));
					connectionRenderer.setLineStippled(true);
					break;
				case state_change:
					connectionRenderer.setLineStippled(true);
					break;
				case binding_association:
					// Nothing to do
					break;
				case dissociation:
					connectionRenderer.addAttributeRenderer(createDefaultOrthogonalLineCrossingRenderer(
							pixelGLConverter, sizeConfig));
					break;
				case missing_interaction:
					connectionRenderer.addAttributeRenderer(createDefaultLineCrossingRenderer(pixelGLConverter,
							sizeConfig));
					break;
				case phosphorylation:
					connectionRenderer.addAttributeRenderer(createDefaultLabelAboveLineRenderer(
							EPathwayRelationEdgeSubType.phosphorylation.getSymbol(), pixelGLConverter, textRenderer,
							sizeConfig));
					break;
				case dephosphorylation:
					connectionRenderer.addAttributeRenderer(createDefaultLabelAboveLineRenderer(
							EPathwayRelationEdgeSubType.dephosphorylation.getSymbol(), pixelGLConverter, textRenderer,
							sizeConfig));
					break;
				case glycosylation:
					connectionRenderer.addAttributeRenderer(createDefaultLabelAboveLineRenderer(
							EPathwayRelationEdgeSubType.glycosylation.getSymbol(), pixelGLConverter, textRenderer,
							sizeConfig));
					break;
				case ubiquitination:
					connectionRenderer.addAttributeRenderer(createDefaultLabelAboveLineRenderer(
							EPathwayRelationEdgeSubType.ubiquitination.getSymbol(), pixelGLConverter, textRenderer,
							sizeConfig));
					break;
				case methylation:
					connectionRenderer.addAttributeRenderer(createDefaultLabelAboveLineRenderer(
							EPathwayRelationEdgeSubType.methylation.getSymbol(), pixelGLConverter, textRenderer,
							sizeConfig));
					break;
				}
			}
		} else {
			// TODO: This is just a default edge. Is this right?


			LineEndArrowRenderer lineEndArrowRenderer = createDefaultLineEndArrowRenderer(pixelGLConverter, sizeConfig);

			connectionRenderer.addAttributeRenderer(lineEndArrowRenderer);

			if (edge instanceof PathwayReactionEdgeRep) {
				PathwayReactionEdgeRep reactionEdge = (PathwayReactionEdgeRep) edge;
				if (reactionEdge.getType() == EPathwayReactionEdgeType.reversible) {
					lineEndArrowRenderer = createDefaultLineEndArrowRenderer(pixelGLConverter, sizeConfig);
					connectionRenderer.addAttributeRenderer(lineEndArrowRenderer);
				}
			}
		}

		connectionRenderer.renderLine(gl, linePoints);
	}

	private static LineEndArrowRenderer createDefaultLineEndArrowRenderer(PixelGLConverter pixelGLConverter,
			PathSizeConfiguration sizeConfig) {
		ClosedArrowRenderer arrowRenderer = new ClosedArrowRenderer(pixelGLConverter);
		arrowRenderer.setHeadToBasePixels(sizeConfig.edgeArrowSize);
		arrowRenderer.setBaseWidthPixels(sizeConfig.edgeArrwoBaseLineSize);
		return new LineEndArrowRenderer(false, arrowRenderer);
	}

	private static LineEndStaticLineRenderer createDefaultLineEndStaticLineRenderer(boolean isHorizontalLine,
			PixelGLConverter pixelGLConverter, PathSizeConfiguration sizeConfig) {
		LineEndStaticLineRenderer lineEndRenderer = new LineEndStaticLineRenderer(false, pixelGLConverter);
		lineEndRenderer.setHorizontalLine(isHorizontalLine);
		lineEndRenderer.setLineLengthPixels(sizeConfig.edgeArrwoBaseLineSize);
		return lineEndRenderer;
	}

	private static LineLabelRenderer createDefaultLabelOnLineRenderer(String text, PixelGLConverter pixelGLConverter,
			CaleydoTextRenderer textRenderer, PathSizeConfiguration sizeConfig) {
		LineLabelRenderer lineLabelRenderer = new LineLabelRenderer(0.5f, pixelGLConverter, text, textRenderer);
		lineLabelRenderer.setXCentered(true);
		lineLabelRenderer.setYCentered(true);
		lineLabelRenderer.setLineOffsetPixels(0);
		lineLabelRenderer.setTextHeight(sizeConfig.edgeTextHeight);
		return lineLabelRenderer;
	}

	private static LineLabelRenderer createDefaultLabelAboveLineRenderer(String text,
			PixelGLConverter pixelGLConverter, CaleydoTextRenderer textRenderer, PathSizeConfiguration sizeConfig) {
		LineLabelRenderer lineLabelRenderer = new LineLabelRenderer(0.66f, pixelGLConverter, text, textRenderer);
		lineLabelRenderer.setTextHeight(sizeConfig.edgeTextHeight);
		lineLabelRenderer.setLineOffsetPixels(5);
		lineLabelRenderer.setBackGroundColor(new float[] { 0, 0, 0, 0 });
		return lineLabelRenderer;
	}

	private static LineCrossingRenderer createDefaultOrthogonalLineCrossingRenderer(PixelGLConverter pixelGLConverter,
			PathSizeConfiguration sizeConfig) {
		LineCrossingRenderer lineCrossingRenderer = new LineCrossingRenderer(0.5f, pixelGLConverter);
		lineCrossingRenderer.setCrossingAngle(90);
		lineCrossingRenderer.setLineLengthPixels(sizeConfig.edgeArrwoBaseLineSize);
		return lineCrossingRenderer;
	}

	private static LineCrossingRenderer createDefaultLineCrossingRenderer(PixelGLConverter pixelGLConverter,
			PathSizeConfiguration sizeConfig) {
		LineCrossingRenderer lineCrossingRenderer = new LineCrossingRenderer(0.5f, pixelGLConverter);
		lineCrossingRenderer.setCrossingAngle(45);
		lineCrossingRenderer.setLineLengthPixels(sizeConfig.edgeArrwoBaseLineSize);
		return lineCrossingRenderer;
	}

}
