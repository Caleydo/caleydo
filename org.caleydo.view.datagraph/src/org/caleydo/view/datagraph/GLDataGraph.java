package org.caleydo.view.datagraph;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL2;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.EVAOperation;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.DetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.view.datagraph.listener.GLDataGraphKeyListener;

/**
 * This class is responsible for rendering the radial hierarchy and receiving
 * user events and events from other views.
 * 
 * @author Christian Partl
 */
public class GLDataGraph extends AGLView implements IViewCommandHandler {

	public final static String VIEW_ID = "org.caleydo.view.datagraph";

	public final static int BOUNDS_SPACING_PIXELS = 30;

	private GLDataGraphKeyListener glKeyListener;
	private boolean useDetailLevel = false;

	private Graph<IDataGraphNode> dataGraph;
	private ForceDirectedGraphLayout graphLayout;
	private int maxNodeWidthPixels;
	private int maxNodeHeightPixels;
	private DragAndDropController dragAndDropController;
	private boolean applyAutomaticLayout;
	private Map<IDataGraphNode, Pair<Float, Float>> relativeNodePositions;

	/**
	 * Constructor.
	 */
	public GLDataGraph(GLCaleydoCanvas glCanvas, final ViewFrustum viewFrustum) {
		super(glCanvas, viewFrustum, true);
		viewType = GLDataGraph.VIEW_ID;
		glKeyListener = new GLDataGraphKeyListener();
		dataGraph = new Graph<IDataGraphNode>();
		graphLayout = new ForceDirectedGraphLayout();
		relativeNodePositions = new HashMap<IDataGraphNode, Pair<Float, Float>>();
		dragAndDropController = new DragAndDropController(this);

		DataNode o1 = new DataNode(graphLayout, this, dragAndDropController, 0);
		DataNode o2 = new DataNode(graphLayout, this, dragAndDropController, 1);
		DataNode o3 = new DataNode(graphLayout, this, dragAndDropController, 2);
		DataNode o4 = new DataNode(graphLayout, this, dragAndDropController, 3);
		DataNode o5 = new DataNode(graphLayout, this, dragAndDropController, 4);

		dataGraph.addNode(o1);
		dataGraph.addNode(o2);
		dataGraph.addNode(o3);
		dataGraph.addNode(o4);
		dataGraph.addNode(o5);

		dataGraph.addEdge(o1, o2);
		dataGraph.addEdge(o1, o3);
		dataGraph.addEdge(o3, o4);
		dataGraph.addEdge(o5, o4);
		dataGraph.addEdge(o3, o2);

		maxNodeWidthPixels = Integer.MIN_VALUE;
		maxNodeHeightPixels = Integer.MIN_VALUE;

		for (IDataGraphNode node : dataGraph.getNodes()) {
			if (node.getHeightPixels() > maxNodeHeightPixels)
				maxNodeHeightPixels = node.getHeightPixels();

			if (node.getWidthPixels() > maxNodeWidthPixels)
				maxNodeWidthPixels = node.getWidthPixels();
		}

		applyAutomaticLayout = true;
	}

	@Override
	public void init(GL2 gl) {
		textRenderer = new CaleydoTextRenderer(24);
	}

	@Override
	public void initLocal(GL2 gl) {

		iGLDisplayListIndexLocal = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		// Register keyboard listener to GL2 canvas
		parentGLCanvas.getParentComposite().getDisplay()
				.asyncExec(new Runnable() {
					@Override
					public void run() {
						parentGLCanvas.getParentComposite().addKeyListener(
								glKeyListener);
					}
				});

		init(gl);
	}

	@Override
	public void initRemote(final GL2 gl, final AGLView glParentView,
			final GLMouseListener glMouseListener) {

		// Register keyboard listener to GL2 canvas
		glParentView.getParentGLCanvas().getParentComposite().getDisplay()
				.asyncExec(new Runnable() {
					@Override
					public void run() {
						glParentView.getParentGLCanvas().getParentComposite()
								.addKeyListener(glKeyListener);
					}
				});

		this.glMouseListener = glMouseListener;

		iGLDisplayListIndexRemote = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexRemote;
		init(gl);

	}

	@Override
	public void setDetailLevel(DetailLevel detailLevel) {
		if (useDetailLevel) {
			super.setDetailLevel(detailLevel);
			// renderStyle.setDetailLevel(detailLevel);
		}

	}

	@Override
	public void displayLocal(GL2 gl) {

		if (!lazyMode)
			pickingManager.handlePicking(this, gl);

		if (bIsDisplayListDirtyLocal) {
			buildDisplayList(gl, iGLDisplayListIndexLocal);
			bIsDisplayListDirtyLocal = false;
		}
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		display(gl);

		if (!lazyMode)
			checkForHits(gl);

		if (eBusyModeState != EBusyModeState.OFF) {
			renderBusyMode(gl);
		}
	}

	@Override
	public void displayRemote(GL2 gl) {

		if (bIsDisplayListDirtyRemote) {
			buildDisplayList(gl, iGLDisplayListIndexRemote);
			bIsDisplayListDirtyRemote = false;
		}
		iGLDisplayListToCall = iGLDisplayListIndexRemote;

		display(gl);
		checkForHits(gl);

	}

	@Override
	public void display(GL2 gl) {
		gl.glCallList(iGLDisplayListToCall);

		dragAndDropController.handleDragging(gl, glMouseListener);

		if (!isRenderedRemote())
			contextMenu.render(gl, this);

	}

	/**
	 * Builds the display list for a given display list index.
	 * 
	 * @param gl
	 *            Instance of GL2.
	 * @param iGLDisplayListIndex
	 *            Index of the display list.
	 */
	private void buildDisplayList(final GL2 gl, int iGLDisplayListIndex) {
		gl.glNewList(iGLDisplayListIndex, GL2.GL_COMPILE);

		PixelGLConverter pixelGLConverter = parentGLCanvas
				.getPixelGLConverter();

		int drawingAreaWidth = pixelGLConverter
				.getPixelWidthForGLWidth(viewFrustum.getWidth())
				- 2
				* BOUNDS_SPACING_PIXELS - maxNodeWidthPixels;
		int drawingAreaHeight = pixelGLConverter
				.getPixelHeightForGLHeight(viewFrustum.getHeight())
				- 2
				* BOUNDS_SPACING_PIXELS - maxNodeHeightPixels;
		if (applyAutomaticLayout) {
			graphLayout.setGraph(dataGraph);
			Rectangle2D rect = new Rectangle();

			rect.setFrame(BOUNDS_SPACING_PIXELS + (maxNodeWidthPixels / 2.0f),
					BOUNDS_SPACING_PIXELS + (maxNodeHeightPixels / 2.0f),
					drawingAreaWidth, drawingAreaHeight);
			graphLayout.layout(rect);
		} else {
			if (!dragAndDropController.isDragging()) {
				for (IDataGraphNode node : dataGraph.getNodes()) {
					Pair<Float, Float> relativePosition = relativeNodePositions
							.get(node);
					graphLayout.setNodePosition(node, new Point2D.Double(
							relativePosition.getFirst() * drawingAreaWidth,
							relativePosition.getSecond() * drawingAreaHeight));
				}
			}
		}

		for (IDataGraphNode node : dataGraph.getNodes()) {
			Point2D position = graphLayout.getNodePosition(node, true);
			float relativePosX = (float) position.getX() / drawingAreaWidth;
			float relativePosY = (float) position.getY() / drawingAreaHeight;
			relativeNodePositions.put(node, new Pair<Float, Float>(
					relativePosX, relativePosY));

			((DataNode) node).render(gl);

		}
		renderEdges(gl);
		gl.glEndList();

	}

	private void renderEdges(GL2 gl) {

		PixelGLConverter pixelGLConverter = parentGLCanvas
				.getPixelGLConverter();

		gl.glPushAttrib(GL2.GL_LINE_BIT);
		gl.glLineWidth(2);
		gl.glBegin(GL2.GL_LINES);
		for (Pair<IDataGraphNode, IDataGraphNode> edge : dataGraph
				.getAllEdges()) {
			Point2D position1 = graphLayout.getNodePosition(edge.getFirst());
			Point2D position2 = graphLayout.getNodePosition(edge.getSecond());

			float x1 = pixelGLConverter.getGLWidthForPixelWidth((int) position1
					.getX());
			float x2 = pixelGLConverter.getGLWidthForPixelWidth((int) position2
					.getX());
			float y1 = pixelGLConverter
					.getGLHeightForPixelHeight((int) position1.getY());
			float y2 = pixelGLConverter
					.getGLHeightForPixelHeight((int) position2.getY());

			gl.glVertex3f(x1, y1, -0.5f);
			gl.glVertex3f(x2, y2, -0.5f);
		}
		gl.glEnd();
		gl.glPopAttrib();
	}

	@Override
	public String getDetailedInfo() {
		return new String("");
	}

	@Override
	protected void handlePickingEvents(EPickingType pickingType,
			EPickingMode pickingMode, int iExternalID, Pick pick) {
		if (detailLevel == DetailLevel.VERY_LOW) {
			return;
		}

	}

	@Override
	public void broadcastElements(EVAOperation type) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getNumberOfSelections(SelectionType selectionType) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getShortInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearAllSelections() {

	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedDataGraphView serializedForm = new SerializedDataGraphView();
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public void initFromSerializableRepresentation(ASerializedView ser) {

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
	public void handleClearSelections() {
	}

	@Override
	public void handleRedrawView() {
		setDisplayListDirty();
	}

	@Override
	public void handleUpdateView() {

		setDisplayListDirty();
	}

	public void setApplyAutomaticLayout(boolean applyAutomaticLayout) {
		this.applyAutomaticLayout = applyAutomaticLayout;
	}

}
