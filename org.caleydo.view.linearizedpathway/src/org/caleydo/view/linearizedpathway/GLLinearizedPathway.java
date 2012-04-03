package org.caleydo.view.linearizedpathway;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.connectionline.ClosedArrowRenderer;
import org.caleydo.core.view.opengl.util.connectionline.ConnectionLineRenderer;
import org.caleydo.core.view.opengl.util.connectionline.LineCrossingRenderer;
import org.caleydo.core.view.opengl.util.connectionline.LineEndArrowRenderer;
import org.caleydo.core.view.opengl.util.connectionline.LineEndStaticLineRenderer;
import org.caleydo.core.view.opengl.util.connectionline.LineLabelRenderer;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.item.edge.EPathwayRelationEdgeSubType;
import org.caleydo.datadomain.pathway.graph.item.edge.PathwayReactionEdgeRep;
import org.caleydo.datadomain.pathway.graph.item.edge.PathwayRelationEdgeRep;
import org.caleydo.datadomain.pathway.graph.item.vertex.EPathwayVertexType;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.manager.PathwayDatabaseType;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.view.linearizedpathway.renderstyle.TemplateRenderStyle;
import org.eclipse.swt.widgets.Composite;
import org.jgrapht.graph.DefaultEdge;

/**
 * 
 * 
 * @author Christian
 */

public class GLLinearizedPathway extends AGLView {

	public final static String VIEW_TYPE = "org.caleydo.view.linearizedpathway";

	private TemplateRenderStyle renderStyle;

	/**
	 * The pathway of the linearized path.
	 */
	private PathwayGraph pathway;

	/**
	 * The path of the pathway that is currently linearized.
	 */
	private List<PathwayVertexRep> path;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param viewLabel
	 * @param viewFrustum
	 */
	public GLLinearizedPathway(GLCanvas glCanvas, Composite parentComposite,
			ViewFrustum viewFrustum) {

		super(glCanvas, parentComposite, viewFrustum);

		viewType = GLLinearizedPathway.VIEW_TYPE;
		viewLabel = "Linearized Pathway";
	}

	@Override
	public void init(GL2 gl) {
		displayListIndex = gl.glGenLists(1);
		renderStyle = new TemplateRenderStyle(viewFrustum);
		textRenderer = new CaleydoTextRenderer(24);

		super.renderStyle = renderStyle;
		detailLevel = EDetailLevel.HIGH;

		path = new ArrayList<PathwayVertexRep>();

		for (PathwayGraph graph : PathwayManager.get().getAllItems()) {
			if (graph.getType() == PathwayDatabaseType.KEGG
					&& graph.getTitle().startsWith("Glioma")) {
				pathway = graph;
				break;
			}
		}

		PathwayVertexRep currentVertex = null;

		for (PathwayVertexRep vertex : pathway.vertexSet()) {
			currentVertex = vertex;
			break;
		}

		// float currentPosition = 7.5f;

		for (int i = 0; i < 6; i++) {

			path.add(currentVertex);

			for (DefaultEdge edge : pathway.edgesOf(currentVertex)) {
				// PathwayVertexRep v1 = currentGraph.getEdgeSource(edge);
				PathwayVertexRep v2 = pathway.getEdgeTarget(edge);

				currentVertex = v2;
			}
		}

	}

	@Override
	public void initLocal(GL2 gl) {
		init(gl);
	}

	@Override
	public void initRemote(final GL2 gl, final AGLView glParentView,
			final GLMouseListener glMouseListener) {

		// Register keyboard listener to GL2 canvas
		glParentView.getParentComposite().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				glParentView.getParentComposite().addKeyListener(glKeyListener);
			}
		});

		this.glMouseListener = glMouseListener;

		init(gl);
	}

	@Override
	public void displayLocal(GL2 gl) {
		pickingManager.handlePicking(this, gl);
		display(gl);
		if (busyState != EBusyState.OFF) {
			renderBusyMode(gl);
		}

	}

	@Override
	public void displayRemote(GL2 gl) {
		display(gl);
	}

	@Override
	public void display(GL2 gl) {

		// Just for testing different types of connections
		// ConnectionLineRenderer renderer = new ConnectionLineRenderer();
		//
		// ClosedArrowRenderer arrowRenderer = new
		// ClosedArrowRenderer(pixelGLConverter);
		// // arrowRenderer.setFillColor(new float[] { 1, 1, 1, 1 });
		//
		// AArrowRenderer oarrowRenderer = new
		// OpenArrowRenderer(pixelGLConverter);
		//
		// LineEndArrowRenderer a = new LineEndArrowRenderer(false,
		// arrowRenderer);
		// LineEndArrowRenderer b = new LineEndArrowRenderer(true,
		// oarrowRenderer);
		// LineCrossingRenderer c = new LineCrossingRenderer(0.8f,
		// pixelGLConverter);
		// c.setCrossingAngle(45);
		// LineLabelRenderer d = new LineLabelRenderer(0.9f, pixelGLConverter,
		// "e",
		// textRenderer);
		// // d.setLineOffsetPixels(5);
		// LineEndStaticLineRenderer e = new LineEndStaticLineRenderer(false,
		// pixelGLConverter);
		// e.setHorizontalLine(false);
		//
		// LineLabelRenderer f = new LineLabelRenderer(0.4f, pixelGLConverter,
		// "i dont care", textRenderer);
		// f.setLineOffsetPixels(5);
		//
		// LineCrossingRenderer g = new LineCrossingRenderer(0.15f,
		// pixelGLConverter);
		// // g.setCrossingAngle(45);
		//
		// renderer.addAttributeRenderer(a);
		// renderer.addAttributeRenderer(b);
		// renderer.addAttributeRenderer(c);
		// renderer.addAttributeRenderer(d);
		// renderer.addAttributeRenderer(e);
		// renderer.addAttributeRenderer(f);
		// renderer.addAttributeRenderer(g);
		//
		// ArrayList<Vec3f> linePoints = new ArrayList<Vec3f>();
		// linePoints.add(new Vec3f(1, 1, 0));
		// linePoints.add(new Vec3f(1, 2, 0));
		// linePoints.add(new Vec3f(2, 1, 0));
		// linePoints.add(new Vec3f(3, 1, 0));
		// // renderer.setLineStippled(true);
		// renderer.renderLine(gl, linePoints);
		//
		// renderer = new ConnectionLineRenderer();
		//
		// arrowRenderer = new ClosedArrowRenderer(pixelGLConverter);
		// arrowRenderer.setFillColor(new float[] { 1, 1, 1, 1 });
		// a = new LineEndArrowRenderer(false, arrowRenderer);
		//
		// d = new LineLabelRenderer(0.66f, pixelGLConverter, "+g",
		// textRenderer);
		// d.setLineOffsetPixels(5);
		//
		// renderer.addAttributeRenderer(a);
		// renderer.addAttributeRenderer(d);
		//
		// linePoints = new ArrayList<Vec3f>();
		// linePoints.add(new Vec3f(1, 3, 0));
		// linePoints.add(new Vec3f(2, 3, 0));
		// renderer.setLineStippled(true);
		// renderer.renderLine(gl, linePoints);

		GLU glu = new GLU();

		textRenderer.setColor(0, 0, 0, 1);
		textRenderer.renderTextInBounds(gl, pathway.getTitle(), 1, 8, 0, 3, 0.1f);

		Vec3f currentPosition = new Vec3f(viewFrustum.getWidth() / 2.0f,
				viewFrustum.getHeight() - 0.1f * viewFrustum.getHeight(), 0);
		float yStep = (viewFrustum.getHeight() - 0.2f * viewFrustum.getHeight())
				/ (float) path.size();

		ANodeRenderer prevRenderer = null;
		PathwayVertexRep prevVertex = null;

		for (PathwayVertexRep vertex : path) {

			ANodeRenderer renderer = null;
			if (vertex.getType() == EPathwayVertexType.gene) {
				GeneNodeRenderer geneNodeRenderer = new GeneNodeRenderer(
						pixelGLConverter, textRenderer);
				int commaIndex = vertex.getName().indexOf(',');
				if (commaIndex > 0) {
					geneNodeRenderer
							.setCaption(vertex.getName().substring(0, commaIndex));
				} else {
					geneNodeRenderer.setCaption(vertex.getName());
				}
				geneNodeRenderer.setVertex(vertex);
				geneNodeRenderer.setHeightPixels(20);
				geneNodeRenderer.setWidthPixels(70);

				renderer = geneNodeRenderer;

			} else {
				CompoundNodeRenderer compoundNodeRenderer = new CompoundNodeRenderer(
						pixelGLConverter);

				compoundNodeRenderer.setVertex(vertex);
				compoundNodeRenderer.setHeightPixels(20);
				compoundNodeRenderer.setWidthPixels(20);
				renderer = compoundNodeRenderer;
			}

			renderer.setPosition(new Vec3f(currentPosition));

			renderer.render(gl, glu);

			if (prevRenderer != null) {
				renderEdge(gl, prevVertex, vertex, prevRenderer, renderer);
			}

			prevRenderer = renderer;
			prevVertex = vertex;

			currentPosition = new Vec3f(currentPosition.x(), currentPosition.y() - yStep,
					currentPosition.z());
		}

		checkForHits(gl);
	}

	private void renderEdge(GL2 gl, PathwayVertexRep vertexRep1,
			PathwayVertexRep vertexRep2, ANodeRenderer nodeRenderer1,
			ANodeRenderer nodeRenderer2) {

		DefaultEdge edge = pathway.getEdge(vertexRep1, vertexRep2);
		if (edge == null) {
			edge = pathway.getEdge(vertexRep2, vertexRep1);
			if (edge == null)
				return;
		}

		ConnectionLineRenderer connectionRenderer = new ConnectionLineRenderer();
		List<Vec3f> linePoints = new ArrayList<Vec3f>();

		boolean isNode1Target = pathway.getEdgeTarget(edge) == vertexRep1;

		Vec3f sourceConnectionPoint = (isNode1Target) ? nodeRenderer2
				.getTopConnectionPoint() : nodeRenderer1.getBottomConnectionPoint();
		Vec3f targetConnectionPoint = (isNode1Target) ? nodeRenderer1
				.getBottomConnectionPoint() : nodeRenderer2.getTopConnectionPoint();

		linePoints.add(sourceConnectionPoint);
		linePoints.add(targetConnectionPoint);

		if (edge instanceof PathwayReactionEdgeRep) {
			// TODO: This is just a default edge. Is this right?
			ClosedArrowRenderer arrowRenderer = new ClosedArrowRenderer(pixelGLConverter);
			LineEndArrowRenderer lineEndArrowRenderer = new LineEndArrowRenderer(false,
					arrowRenderer);

			connectionRenderer.addAttributeRenderer(lineEndArrowRenderer);

			// linePoints.add(nodeRenderer1.getBottomConnectionPoint());
			// linePoints.add(nodeRenderer2.getTopConnectionPoint());

		} else {
			if (edge instanceof PathwayRelationEdgeRep) {
				PathwayRelationEdgeRep relationEdgeRep = (PathwayRelationEdgeRep) edge;

				ArrayList<EPathwayRelationEdgeSubType> subtypes = relationEdgeRep
						.getRelationSubTypes();
				float spacing = pixelGLConverter.getGLHeightForPixelHeight(2);

				for (EPathwayRelationEdgeSubType subtype : subtypes) {
					switch (subtype) {
					case compound:
						// TODO:
						break;
					case hidden_compound:
						// TODO:
						break;
					case activation:
						connectionRenderer
								.addAttributeRenderer(createDefaultLineEndArrowRenderer());
						break;
					case inhibition:
						connectionRenderer
								.addAttributeRenderer(createDefaultLineEndStaticLineRenderer());
						targetConnectionPoint.setY(targetConnectionPoint.y()
								+ ((isNode1Target) ? -spacing : spacing));
						break;
					case expression:
						connectionRenderer
								.addAttributeRenderer(createDefaultLineEndArrowRenderer());
						if (vertexRep1.getType() == EPathwayVertexType.gene
								&& vertexRep1.getType() == EPathwayVertexType.gene) {
							connectionRenderer
									.addAttributeRenderer(createDefaultLabelOnLineRenderer("e"));
						}
						break;
					case repression:
						connectionRenderer
								.addAttributeRenderer(createDefaultLineEndArrowRenderer());
						connectionRenderer
								.addAttributeRenderer(createDefaultLineEndStaticLineRenderer());
						targetConnectionPoint.setY(targetConnectionPoint.y()
								+ ((isNode1Target) ? -spacing : spacing));
						break;
					case indirect_effect:
						connectionRenderer
								.addAttributeRenderer(createDefaultLineEndArrowRenderer());
						connectionRenderer.setLineStippled(true);
						break;
					case state_change:
						connectionRenderer.setLineStippled(true);
						break;
					case binding_association:
						// Nothing to do
						break;
					case dissociation:
						connectionRenderer
								.addAttributeRenderer(createDefaultOrthogonalLineCrossingRenderer());
						break;
					case missing_interaction:
						connectionRenderer
								.addAttributeRenderer(createDefaultLineCrossingRenderer());
						break;
					case phosphorylation:
						connectionRenderer
								.addAttributeRenderer(createDefaultLabelAboveLineRenderer(EPathwayRelationEdgeSubType.phosphorylation
										.getSymbol()));
						break;
					case dephosphorylation:
						connectionRenderer
								.addAttributeRenderer(createDefaultLabelAboveLineRenderer(EPathwayRelationEdgeSubType.dephosphorylation
										.getSymbol()));
						break;
					case glycosylation:
						connectionRenderer
								.addAttributeRenderer(createDefaultLabelAboveLineRenderer(EPathwayRelationEdgeSubType.glycosylation
										.getSymbol()));
						break;
					case ubiquitination:
						connectionRenderer
								.addAttributeRenderer(createDefaultLabelAboveLineRenderer(EPathwayRelationEdgeSubType.ubiquitination
										.getSymbol()));
						break;
					case methylation:
						connectionRenderer
								.addAttributeRenderer(createDefaultLabelAboveLineRenderer(EPathwayRelationEdgeSubType.methylation
										.getSymbol()));
						break;
					}
				}
			}
		}

		connectionRenderer.renderLine(gl, linePoints);
	}

	private LineEndArrowRenderer createDefaultLineEndArrowRenderer() {
		ClosedArrowRenderer arrowRenderer = new ClosedArrowRenderer(pixelGLConverter);
		return new LineEndArrowRenderer(false, arrowRenderer);
	}

	private LineEndStaticLineRenderer createDefaultLineEndStaticLineRenderer() {
		LineEndStaticLineRenderer lineEndRenderer = new LineEndStaticLineRenderer(false,
				pixelGLConverter);
		lineEndRenderer.setHorizontalLine(true);
		return lineEndRenderer;
	}

	private LineLabelRenderer createDefaultLabelOnLineRenderer(String text) {
		LineLabelRenderer lineLabelRenderer = new LineLabelRenderer(0.66f,
				pixelGLConverter, text, textRenderer);
		lineLabelRenderer.setLineOffsetPixels(0);
		return lineLabelRenderer;
	}

	private LineLabelRenderer createDefaultLabelAboveLineRenderer(String text) {
		LineLabelRenderer lineLabelRenderer = new LineLabelRenderer(0.66f,
				pixelGLConverter, text, textRenderer);
		lineLabelRenderer.setLineOffsetPixels(5);
		return lineLabelRenderer;
	}

	private LineCrossingRenderer createDefaultOrthogonalLineCrossingRenderer() {
		LineCrossingRenderer lineCrossingRenderer = new LineCrossingRenderer(0.5f,
				pixelGLConverter);
		lineCrossingRenderer.setCrossingAngle(90);
		return lineCrossingRenderer;
	}

	private LineCrossingRenderer createDefaultLineCrossingRenderer() {
		LineCrossingRenderer lineCrossingRenderer = new LineCrossingRenderer(0.5f,
				pixelGLConverter);
		lineCrossingRenderer.setCrossingAngle(45);
		return lineCrossingRenderer;
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedLinearizedPathwayView serializedForm = new SerializedLinearizedPathwayView();
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public String toString() {
		return "TODO: ADD INFO THAT APPEARS IN THE LOG";
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();

	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();

	}

	@Override
	public int getNumberOfSelections(SelectionType SelectionType) {
		// TODO Auto-generated method stub
		return 0;
	}

}
