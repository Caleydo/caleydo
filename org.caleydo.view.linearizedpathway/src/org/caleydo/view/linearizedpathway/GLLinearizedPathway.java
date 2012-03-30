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
import org.caleydo.core.view.opengl.canvas.DetailLevel;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.connectionline.ClosedArrowRenderer;
import org.caleydo.core.view.opengl.util.connectionline.ConnectionLineRenderer;
import org.caleydo.core.view.opengl.util.connectionline.LineEndArrowRenderer;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
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
		detailLevel = DetailLevel.HIGH;
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

		PathwayGraph currentGraph = null;

		List<PathwayVertexRep> path = new ArrayList<PathwayVertexRep>();

		GLU glu = new GLU();

		for (PathwayGraph graph : PathwayManager.get().getAllItems()) {
			if (graph.getType() == PathwayDatabaseType.KEGG
					&& graph.getTitle().startsWith("Glioma")) {
				currentGraph = graph;
				break;
			}
		}
		textRenderer.setColor(0, 0, 0, 1);
		textRenderer.renderTextInBounds(gl, currentGraph.getTitle(), 1, 8, 0, 3, 0.1f);

		PathwayVertexRep currentVertex = null;

		for (PathwayVertexRep vertex : currentGraph.vertexSet()) {
			currentVertex = vertex;
			break;
		}

		// float currentPosition = 7.5f;

		for (int i = 0; i < 6; i++) {

			path.add(currentVertex);

			// textRenderer.renderTextInBounds(gl, "VertexType: "
			// + currentVertex.getType().name(), 1, currentPosition, 0, 5,
			// 0.1f);
			// currentPosition -= 0.1f;
			//
			// textRenderer.renderTextInBounds(gl, "VertexName: " +
			// currentVertex.getName(),
			// 1, currentPosition, 0, 5, 0.1f);
			// currentPosition -= 0.13f;

			// DefaultEdge currentEdge = null;
			// float space = 0;
			for (DefaultEdge edge : currentGraph.edgesOf(currentVertex)) {
				// PathwayVertexRep v1 = currentGraph.getEdgeSource(edge);
				PathwayVertexRep v2 = currentGraph.getEdgeTarget(edge);
				// textRenderer.renderTextInBounds(gl, v1.getName(), 1 + space,
				// currentPosition, 0, 5, 0.1f);
				// currentPosition -= 0.1f;
				// textRenderer.renderTextInBounds(gl, v2.getName(), 1 + space,
				// currentPosition, 0, 5, 0.1f);
				// currentPosition -= 0.13f;
				currentVertex = v2;
			}
		}

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
				ConnectionLineRenderer connectionRenderer = new ConnectionLineRenderer();
				List<Vec3f> linePoints = new ArrayList<Vec3f>();
				linePoints.add(prevRenderer.getBottomConnectionPoint());
				linePoints.add(renderer.getTopConnectionPoint());
				
				DefaultEdge edge = currentGraph.getEdge(prevVertex, vertex);
				
				ClosedArrowRenderer arrowRenderer = new ClosedArrowRenderer(pixelGLConverter);
				LineEndArrowRenderer lineEndArrowRenderer = new LineEndArrowRenderer(currentGraph.getEdgeTarget(edge) != vertex, arrowRenderer);
				
				connectionRenderer.addAttributeRenderer(lineEndArrowRenderer);
				connectionRenderer.renderLine(gl, linePoints);
			}
				
				prevRenderer = renderer;
				prevVertex = vertex;

			currentPosition = new Vec3f(currentPosition.x(), currentPosition.y() - yStep,
					currentPosition.z());
		}

		checkForHits(gl);
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
