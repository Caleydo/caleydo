package org.caleydo.view.dataflipper;

import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import gleem.linalg.open.Transform;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.data.CmdDataCreateDataDomain;
import org.caleydo.core.command.view.opengl.CmdCreateView;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IDataDomain;
import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.ISetBasedDataDomain;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.manager.datadomain.AssociationManager;
import org.caleydo.core.manager.datadomain.DataDomainGraph;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.datadomain.IDataDomainBasedView;
import org.caleydo.core.manager.event.data.ClusterSetEvent;
import org.caleydo.core.manager.event.view.ViewActivationEvent;
import org.caleydo.core.manager.event.view.remote.LoadPathwayEvent;
import org.caleydo.core.manager.event.view.remote.LoadPathwaysByGeneEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.path.GuidanceNode;
import org.caleydo.core.manager.path.HistoryNode;
import org.caleydo.core.manager.path.INode;
import org.caleydo.core.manager.path.Path;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.view.ConnectedElementRepresentationManager;
import org.caleydo.core.manager.view.RemoteRenderingTransformer;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.system.SystemTime;
import org.caleydo.core.util.system.Time;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.SerializedGlyphView;
import org.caleydo.core.view.opengl.canvas.remote.AGLConnectionLineRenderer;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteElementManager;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevelElement;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.core.view.opengl.util.slerp.SlerpAction;
import org.caleydo.core.view.opengl.util.slerp.SlerpMod;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.core.view.opengl.util.vislink.NURBSCurve;
import org.caleydo.rcp.view.listener.AddPathwayListener;
import org.caleydo.rcp.view.listener.IRemoteRenderingHandler;
import org.caleydo.rcp.view.listener.LoadPathwaysByGeneListener;
import org.caleydo.view.browser.HTMLBrowser;
import org.caleydo.view.browser.SerializedHTMLBrowserView;
import org.caleydo.view.heatmap.hierarchical.SerializedHierarchicalHeatMapView;
import org.caleydo.view.parcoords.GLParallelCoordinates;
import org.caleydo.view.parcoords.SerializedParallelCoordinatesView;
import org.caleydo.view.pathwaybrowser.GLPathwayViewBrowser;
import org.caleydo.view.pathwaybrowser.SerializedPathwayViewBrowserView;
import org.caleydo.view.tissue.GLTissue;
import org.caleydo.view.tissue.SerializedTissueView;
import org.caleydo.view.tissuebrowser.SerializedTissueViewBrowserView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Shell;

import com.sun.opengl.util.j2d.TextRenderer;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;

public class GLDataFlipper extends AGLView implements IGLRemoteRenderingView,
		IRemoteRenderingHandler {

	public final static String VIEW_ID = "org.caleydo.view.dataflipper";

	private static final int SLERP_RANGE = 1000;
	private static final int SLERP_SPEED = 1500;

	private static final int MAX_SIDE_VIEWS = 10;

	private static float DATA_DOMAIN_Z = 4;

	private static float DATA_DOMAIN_SCALING_FACTOR = 1f;
	private static float MAX_HISTORY_DATA_DOMAINS = 5;
	private static float INTERFACE_WIDTH = 0.12f * DATA_DOMAIN_SCALING_FACTOR;
	private static float ICON_PADDING = 0.015f * DATA_DOMAIN_SCALING_FACTOR;
	private static float DATA_DOMAIN_SPACING = 1.2f;
	private static float DATA_DOMAIN_HEIGHT = 0.95f;
	private static float[] INTERFACE_ICON_BACKGROUND_COLOR = new float[] { 1, 1, 1 };
	private static float[] CONNECTION_LINE_COLOR = new float[] { 1, 1, 1, 1 };

	private ArrayList<ASerializedView> newViews;

	private ArrayList<AGLView> containedGLViews;

	private RemoteLevelElement focusElement;
	private ArrayList<RemoteLevelElement> stackElementsLeft;
	private ArrayList<RemoteLevelElement> stackElementsRight;
	private ArrayList<RemoteLevelElement> allElements;

	private Transform focusTransform;
	private Transform focusTransformFullScreen;

	protected AGLConnectionLineRenderer glConnectionLineRenderer;

	private AssociationManager dataDomainViewAssociationManager;

	/**
	 * Transformation utility object to transform and project view related
	 * coordinates
	 */
	protected RemoteRenderingTransformer selectionTransformer;

	private GLInfoAreaManager infoAreaManager;

	private TextRenderer textRenderer;

	private TextureManager textureManager;

	private ArrayList<SlerpAction> arSlerpActions;

	private Time time;

	private RemoteLevelElement lastPickedRemoteLevelElement;
	private AGLView lastPickedView;
	private HistoryNode lastSelectedDataDomainNode;
	private HistoryNode mouseOverDataDomainNode;
	private String mouseOverInterface;
	private String mouseOverNextDataDomain;

	private DataDomainGraph dataDomainGraph;
	private Path historyPath;
	private Path guidancePath;
	private GuidanceNode currentGuidanceNode;

	private float metaViewAnimation = 0;

	/**
	 * Slerp factor: 0 = source; 1 = destination
	 */
	private int iSlerpFactor = 0;

	private AddPathwayListener addPathwayListener = null;
	private LoadPathwaysByGeneListener loadPathwaysByGeneListener = null;

	private ZoomMouseWheelListener zoomMouseWheelListener;
	private boolean showFocusViewFullScreen = false;

	private String startDataDomainType = "org.caleydo.datadomain.clinical";

	// TODO: change to empty dummy view instead of tissue
	private GLTissue glBrowserImageView;
	private Shell browserOverlayShell;
	private HTMLBrowser browserView;

	// FIXME: it is not nice to store the view as a member var. instead the
	// early initialized views should be handled differently
	private AGLView pathwayBrowserView;

	/**
	 * Constructor.
	 */
	public GLDataFlipper(GLCaleydoCanvas glCanvas, final String sLabel,
			final IViewFrustum viewFrustum) {

		super(glCanvas, sLabel, viewFrustum, true);

		viewType = GLDataFlipper.VIEW_ID;

		zoomMouseWheelListener = new ZoomMouseWheelListener(this);
		parentGLCanvas.removeMouseWheelListener(glMouseListener);
		parentGLCanvas.addMouseWheelListener(zoomMouseWheelListener);

		textRenderer = new TextRenderer(new Font("Arial", Font.PLAIN, 24), false);
		textureManager = new TextureManager();
		arSlerpActions = new ArrayList<SlerpAction>();

		glMouseListener.addGLCanvas(this);

		newViews = new ArrayList<ASerializedView>();
		containedGLViews = new ArrayList<AGLView>();
		stackElementsRight = new ArrayList<RemoteLevelElement>();
		stackElementsLeft = new ArrayList<RemoteLevelElement>();
		allElements = new ArrayList<RemoteLevelElement>();

		dataDomainGraph = new DataDomainGraph();

		// TODO: Move to render style
		focusTransform = new Transform();
		focusTransform.setTranslation(new Vec3f(-0.1f, 0.28f, 4));
		focusTransform.setScale(new Vec3f(1 / 2.5f, 1 / 2.5f, 1 / 2.5f));

		focusTransformFullScreen = new Transform();
		focusTransformFullScreen.setTranslation(new Vec3f(-2.5f, -2.5f, 2));
		focusTransformFullScreen.setScale(new Vec3f(1, 1, 1));

		focusElement = new RemoteLevelElement(null);
		focusElement.setTransform(focusTransform);
		RemoteElementManager.get().registerItem(focusElement);
		allElements.add(focusElement);

		for (int iSideViewsIndex = 1; iSideViewsIndex <= MAX_SIDE_VIEWS; iSideViewsIndex++) {
			RemoteLevelElement newElement = new RemoteLevelElement(null);
			Transform transform = new Transform();
			transform.setTranslation(new Vec3f(-2.1f - iSideViewsIndex / 1.8f + 1.5f,
					-1.25f + 1.5f, 4f));
			transform.setScale(new Vec3f(1 / 2.4f, 1 / 2.4f, 1 / 2.4f));
			transform.setRotation(new Rotf(new Vec3f(0, 1, 0), Vec3f
					.convertGrad2Radiant(96)));
			newElement.setTransform(transform);
			stackElementsLeft.add(newElement);
			allElements.add(newElement);
			RemoteElementManager.get().registerItem(newElement);

			newElement = new RemoteLevelElement(null);
			transform = new Transform();
			transform.setTranslation(new Vec3f(3.15f + iSideViewsIndex / 1.8f + 1.5f,
					-1.55f + 1.5f, -1f));
			transform.setScale(new Vec3f(1 / 1.95f, 1 / 1.95f, 1 / 2f));
			transform.setRotation(new Rotf(new Vec3f(0, -1, 0), Vec3f
					.convertGrad2Radiant(96)));
			newElement.setTransform(transform);
			stackElementsRight.add(newElement);
			allElements.add(newElement);
			RemoteElementManager.get().registerItem(newElement);
		}

		glConnectionLineRenderer = new GLConnectionLineRendererDataFlipper(focusElement,
				stackElementsLeft, stackElementsRight);

		if (DataDomainManager.getInstance().getDataDomain(
				"org.caleydo.datadomain.pathway") == null) {
			CmdDataCreateDataDomain cmd = new CmdDataCreateDataDomain(
					ECommandType.CREATE_DATA_DOMAIN);
			cmd.setAttributes("org.caleydo.datadomain.pathway");
			cmd.doCommand();
		}

		if (DataDomainManager.getInstance()
				.getDataDomain("org.caleydo.datadomain.tissue") == null) {
			CmdDataCreateDataDomain cmd = new CmdDataCreateDataDomain(
					ECommandType.CREATE_DATA_DOMAIN);
			cmd.setAttributes("org.caleydo.datadomain.tissue");
			cmd.doCommand();
		}
	}

	@Override
	public void initLocal(final GL gl) {
		// iGLDisplayList = gl.glGenLists(1);

		ArrayList<RemoteLevelElement> remoteLevelElementWhiteList = new ArrayList<RemoteLevelElement>();
		remoteLevelElementWhiteList.add(focusElement);
		remoteLevelElementWhiteList.add(stackElementsLeft.get(0));
		remoteLevelElementWhiteList.add(stackElementsRight.get(0));
		selectionTransformer = new RemoteRenderingTransformer(iUniqueID,
				remoteLevelElementWhiteList);

		init(gl);
	}

	@Override
	public void initRemote(final GL gl, final AGLView glParentView,
			final GLMouseListener glMouseListener, GLInfoAreaManager infoAreaManager) {

		throw new IllegalStateException("Not implemented to be rendered remote");
	}

	@Override
	public void init(final GL gl) {
		gl.glClearColor(0.5f, 0.5f, 0.5f, 1f);

		time = new SystemTime();
		((SystemTime) time).rebase();

		infoAreaManager = new GLInfoAreaManager();
		infoAreaManager.initInfoInPlace(viewFrustum);

		if (glConnectionLineRenderer != null) {
			glConnectionLineRenderer.init(gl);
		}

		initDataDomainViewAssociation();
		initGuidancePath();

		ASerializedView serView = addView("org.caleydo.datadomain.pathway",
				"org.caleydo.view.pathwaybrowser");
		pathwayBrowserView = createView(gl, serView);
		pathwayBrowserView.initRemote(gl, this, glMouseListener, infoAreaManager);
		containedGLViews.add(pathwayBrowserView);
	}

	private void initGuidancePath() {
		guidancePath = new Path();

		guidancePath.addNode(new GuidanceNode("org.caleydo.datadomain.clinical", ""));

		currentGuidanceNode = (GuidanceNode) guidancePath.getLastNode();

		guidancePath.addNode(currentGuidanceNode, new GuidanceNode(
				"org.caleydo.datadomain.organ", ""));
		guidancePath.addNode(currentGuidanceNode, new GuidanceNode(
				"org.caleydo.datadomain.genetic", ""));

		guidancePath.addNode(new GuidanceNode("org.caleydo.datadomain.pathway", ""));
		guidancePath.addNode(new GuidanceNode("org.caleydo.datadomain.genetic", ""));
		guidancePath.addNode(new GuidanceNode("org.caleydo.datadomain.clinical", ""));

		guidancePath.addNode(new GuidanceNode("org.caleydo.datadomain.tissue", ""));

		guidancePath.addNode(new GuidanceNode("org.caleydo.datadomain.clinical", ""));
	}

	private void initDataDomainViewAssociation() {

		dataDomainViewAssociationManager = new AssociationManager();

		String dataDomainType = "org.caleydo.datadomain.genetic";
		dataDomainViewAssociationManager.registerDatadomainTypeViewTypeAssociation(
				dataDomainType, "org.caleydo.view.heatmap.hierarchical");
		dataDomainViewAssociationManager.registerDatadomainTypeViewTypeAssociation(
				dataDomainType, "org.caleydo.view.parcoords");
		dataDomainViewAssociationManager.registerDatadomainTypeViewTypeAssociation(
				dataDomainType, "org.caleydo.analytical.clustering");
		dataDomainViewAssociationManager.registerDatadomainTypeViewTypeAssociation(
				dataDomainType, "org.caleydo.view.tissue");

		dataDomainType = "org.caleydo.datadomain.clinical";
		dataDomainViewAssociationManager.registerDatadomainTypeViewTypeAssociation(
				dataDomainType, "org.caleydo.view.glyph");
		dataDomainViewAssociationManager.registerDatadomainTypeViewTypeAssociation(
				dataDomainType, "org.caleydo.view.parcoords");

		dataDomainType = "org.caleydo.datadomain.tissue";
		dataDomainViewAssociationManager.registerDatadomainTypeViewTypeAssociation(
				dataDomainType, "org.caleydo.view.tissuebrowser");

		dataDomainType = "org.caleydo.datadomain.pathway";
		dataDomainViewAssociationManager.registerDatadomainTypeViewTypeAssociation(
				dataDomainType, "org.caleydo.view.pathwaybrowser");

		historyPath = new Path();
		historyPath.addNode(new HistoryNode(startDataDomainType,
				dataDomainViewAssociationManager));
		lastSelectedDataDomainNode = (HistoryNode) historyPath.getLastNode();
	}

	@Override
	public void displayLocal(final GL gl) {

		for (AGLView view : containedGLViews)
			view.processEvents();

		pickingManager.handlePicking(this, gl);

		display(gl);

		if (eBusyModeState != EBusyModeState.OFF) {
			renderBusyMode(gl);
		}

		checkForHits(gl);

		ConnectedElementRepresentationManager cerm = GeneralManager.get()
				.getViewGLCanvasManager().getConnectedElementRepresentationManager();
		cerm.doViewRelatedTransformation(gl, selectionTransformer);

		// gl.glCallList(iGLDisplayListIndexLocal);
	}

	@Override
	public void displayRemote(final GL gl) {
		display(gl);
	}

	@Override
	public void display(final GL gl) {

		time.update();

		// gl.glCallList(iGLDisplayList);

		doSlerpActions(gl);
		initNewView(gl);

		renderRemoteLevelElement(gl, focusElement);

		if (!showFocusViewFullScreen) {
			for (RemoteLevelElement element : stackElementsLeft) {
				renderRemoteLevelElement(gl, element);
			}

			for (RemoteLevelElement element : stackElementsRight) {
				renderRemoteLevelElement(gl, element);
			}

			renderHandles(gl);

			renderDataDomains(gl, historyPath.getLastNode().getDataDomainType(), 1f,
					-2.53f);

			if (glConnectionLineRenderer != null && arSlerpActions.isEmpty()) {
				glConnectionLineRenderer.render(gl);
			}
		}

		// renderGuidanceConnections(gl);

		float fZTranslation = 0;
		fZTranslation = 4f;

		gl.glTranslatef(0, 0, fZTranslation);
		contextMenu.render(gl, this);
		gl.glTranslatef(0, 0, -fZTranslation);
	}

	private void renderDataDomains(GL gl, String dataDomainType, float x, float y) {

		if (metaViewAnimation < DATA_DOMAIN_SPACING)
			metaViewAnimation += 0.02f;

		// gl.glScalef(9f/10, 9f/10, 9f/10);
		renderDataDomain(gl, (HistoryNode) historyPath.getLastNode(), x
				- metaViewAnimation, y + DATA_DOMAIN_HEIGHT / 2f);
		// gl.glScalef(10f/9, 10f/9, 10f/9);

		Set<String> neighbors = dataDomainGraph.getNeighboursOf(historyPath.getLastNode()
				.getDataDomainType());
		int numberOfVerticalDataDomains = neighbors.size() + 1;

		// Render past data domains
		HistoryNode historyNode = (HistoryNode) historyPath.getLastNode();
		if (historyNode != null) {
			for (int i = 0; i < MAX_HISTORY_DATA_DOMAINS; i++) {

				if (historyPath.getPrecedingNode(historyNode).size() < 1)
					break;

				historyNode = (HistoryNode) historyPath.getPrecedingNode(historyNode)
						.get(0);

				if (historyNode == null)
					break;

				renderDataDomain(gl, historyNode, x - metaViewAnimation - (i + 1)
						* DATA_DOMAIN_SPACING, y + DATA_DOMAIN_HEIGHT / 2f);

				gl.glLineWidth(5);
				gl.glColor3f(0.3f, 0, 0);
				gl.glBegin(GL.GL_LINES);
				gl.glVertex3f(x - metaViewAnimation + 0.5f * DATA_DOMAIN_SCALING_FACTOR,
						y + DATA_DOMAIN_HEIGHT / 2f + 0.15f, DATA_DOMAIN_Z);
				gl.glVertex3f(x - metaViewAnimation + 0.5f - DATA_DOMAIN_SPACING
						* (i + 1), y + DATA_DOMAIN_HEIGHT / 2f + 0.15f, DATA_DOMAIN_Z);
				gl.glEnd();
			}
		}

		float ySteps = DATA_DOMAIN_HEIGHT / (numberOfVerticalDataDomains);
		float yNeighbor = y + 0.2f;

		// Do not show possible next domain if the data domain is currently not
		// in mouse over state
		if (mouseOverDataDomainNode != null
				&& dataDomainType != mouseOverDataDomainNode.getDataDomainType())
			return;

		// Render possible next data domains
		for (String nextDataDomainType : neighbors) {

			yNeighbor += ySteps;

			boolean highlight = false;

			for (INode nextGuidanceNode : guidancePath
					.getFollowingNodes(currentGuidanceNode)) {
				if (nextGuidanceNode.getDataDomainType().equals(nextDataDomainType)) {
					highlight = true;
					break;
				}
			}

			boolean mouseOver = false;
			if (mouseOverNextDataDomain == nextDataDomainType)
				mouseOver = true;

			renderNextDataDomain(gl, nextDataDomainType, x - metaViewAnimation
					+ DATA_DOMAIN_SPACING, yNeighbor, highlight, mouseOver);

			gl.glLineWidth(5);

			float z = DATA_DOMAIN_Z;

			if (highlight) {
				gl.glColor3f(0.3f, 0f, 0f);
				z += 0.01f;
			} else if (mouseOver)
				gl.glColor3f(0.15f, 0.15f, 0.15f);
			else
				gl.glColor3f(0.3f, 0.3f, 0.3f);

			float x1 = x - metaViewAnimation + 0.5f * DATA_DOMAIN_SCALING_FACTOR;
			float x2 = x - metaViewAnimation + DATA_DOMAIN_SPACING;
			float y1 = y + DATA_DOMAIN_HEIGHT / 2f + 0.15f;
			float y2 = yNeighbor + 0.1f;

			ArrayList<Vec3f> points = new ArrayList<Vec3f>();
			points.add(new Vec3f(x1, y1, z));
			points.add(new Vec3f(x1 + Math.abs((x1 - x2) / 3), y1, z));
			points.add(new Vec3f(x2 - Math.abs((x1 - x2) / 3), y2, z));
			points.add(new Vec3f(x2, y2, z));

			renderSingleCurve(gl, points, 20);
		}
	}

	private void renderRemoteLevelElement(final GL gl, RemoteLevelElement element) {

		AGLView glView = element.getGLView();
		if (glView == null) {
			return;
		}

		gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.REMOTE_LEVEL_ELEMENT, element.getID()));
		gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.REMOTE_VIEW_SELECTION, glView.getID()));

		gl.glPushMatrix();

		Transform transform = element.getTransform();
		Vec3f translation = transform.getTranslation();
		Rotf rot = transform.getRotation();
		Vec3f scale = transform.getScale();
		Vec3f axis = new Vec3f();
		float fAngle = rot.get(axis);

		gl.glTranslatef(translation.x() - 1.5f, translation.y() - 1.5f, translation.z());
		gl.glRotatef(Vec3f.convertRadiant2Grad(fAngle), axis.x(), axis.y(), axis.z());
		gl.glScalef(scale.x(), scale.y(), scale.z());

		boolean renderBorder = false;
		if (lastPickedRemoteLevelElement != null)
			renderBorder = (element.getGLView() == lastPickedRemoteLevelElement
					.getGLView()) ? true : false;
		else
			renderBorder = false;

		// if (!showFocusViewFullScreen)
		renderBucketWall(gl, renderBorder);

		glView.displayRemote(gl);

		gl.glPopMatrix();

		gl.glPopName();
		gl.glPopName();
	}

	/**
	 * Adds new remote-rendered-views that have been queued for displaying to
	 * this view. Only one view is taken from the list and added for remote
	 * rendering per call to this method.
	 * 
	 * @param GL
	 */
	private void initNewView(GL gl) {

		// if (!arSlerpActions.isEmpty())
		// return;

		if (!newViews.isEmpty()) {
			ASerializedView serView = newViews.remove(0);
			AGLView view = createView(gl, serView);

			if (view instanceof GLTissue) {
				glBrowserImageView = (GLTissue) view;
				glBrowserImageView.setTexturePath(GeneralManager.CALEYDO_HOME_PATH
						+ "browser.png");
			}

			// TODO: remove when activating slerp
			view.initRemote(gl, this, glMouseListener, infoAreaManager);
			// view.getViewFrustum().considerAspectRatio(true);

			containedGLViews.add(view);
			lastSelectedDataDomainNode.addGLView(view);

			openView(view, lastSelectedDataDomainNode);

			if (newViews.isEmpty()) {
				triggerToolBarUpdate();
				enableUserInteraction();
			}
		}
	}

	/**
	 * Triggers a toolbar update by sending an event similar to the view
	 * activation
	 * 
	 * @TODO: Move to remote rendering base class
	 */
	private void triggerToolBarUpdate() {

		ViewActivationEvent viewActivationEvent = new ViewActivationEvent();
		viewActivationEvent.setSender(this);
		List<AGLView> glViews = getRemoteRenderedViews();

		List<IView> views = new ArrayList<IView>();
		views.add(this);
		for (AGLView view : glViews) {
			views.add(view);

			// // Add the heatmap as a toolbar view because the hierarchical
			// itself
			// // has none toolbar
			// if (view instanceof GLHierarchicalHeatMap)
			// ((GLHierarchicalHeatMap) view).getRemoteRenderedViews();
		}

		viewActivationEvent.setViews(views);

		IEventPublisher eventPublisher = GeneralManager.get().getEventPublisher();
		eventPublisher.triggerEvent(viewActivationEvent);
	}

	@Override
	public List<AGLView> getRemoteRenderedViews() {
		return containedGLViews;
	}

	@Override
	public void initFromSerializableRepresentation(ASerializedView ser) {
		// resetView(false);

		SerializedDataFlipperView serializedView = (SerializedDataFlipperView) ser;
		newViews.addAll(serializedView.getInitialContainedViews());

		setDisplayListDirty();
	}

	/**
	 * Creates and initializes a new view based on its serialized form. The view
	 * is already added to the list of event receivers and senders.
	 * 
	 * @param gl
	 * @param serView
	 *            serialized form of the view to create
	 * @return the created view ready to be used within the application
	 */
	private AGLView createView(GL gl, ASerializedView serView) {

		ICommandManager commandManager = generalManager.getCommandManager();
		CmdCreateView cmdView = (CmdCreateView) commandManager
				.createCommandByType(ECommandType.CREATE_GL_VIEW);
		cmdView.setViewID(serView.getViewType());
		cmdView.setAttributesFromSerializedForm(serView);
		cmdView.doCommand();

		AGLView glView = cmdView.getCreatedObject();
		glView.setRemoteRenderingGLView(this);

		if (glView instanceof IDataDomainBasedView<?>) {
			((IDataDomainBasedView<IDataDomain>) glView).setDataDomain(DataDomainManager
					.getInstance().getDataDomain(serView.getDataDomainType()));
		}

		return glView;
	}

	/**
	 * Disables picking and enables busy mode
	 */
	public void disableUserInteraction() {
		IViewManager canvasManager = generalManager.getViewGLCanvasManager();
		canvasManager.getPickingManager().enablePicking(false);
		canvasManager.requestBusyMode(this);
	}

	/**
	 * Enables picking and disables busy mode
	 */
	public void enableUserInteraction() {
		IViewManager canvasManager = generalManager.getViewGLCanvasManager();
		canvasManager.getPickingManager().enablePicking(true);
		canvasManager.releaseBusyMode(this);
	}

	private void doSlerpActions(final GL gl) {
		if (arSlerpActions.isEmpty())
			return;

		if (iSlerpFactor == 0) {

			for (SlerpAction tmpSlerpAction : arSlerpActions) {
				tmpSlerpAction.start();
			}
		}

		if (iSlerpFactor < SLERP_RANGE) {
			// Makes animation rendering CPU speed independent
			iSlerpFactor += SLERP_SPEED * time.deltaT();

			if (iSlerpFactor > SLERP_RANGE) {
				iSlerpFactor = SLERP_RANGE;
			}
		}

		for (SlerpAction tmpSlerpAction : arSlerpActions) {
			slerpView(gl, tmpSlerpAction);
		}

		// Check if slerp action is finished
		if (iSlerpFactor >= SLERP_RANGE) {

			// // Finish in reverse order - otherwise the target ID would
			// overwrite the next
			// for (int iSlerpIndex = arSlerpActions.size() - 1; iSlerpIndex >=
			// 0; iSlerpIndex--) {
			// arSlerpActions.get(iSlerpIndex).finished();
			// }

			for (SlerpAction tmpSlerpAction : arSlerpActions) {
				tmpSlerpAction.finished();

				updateViewDetailLevels(tmpSlerpAction.getDestinationRemoteLevelElement());

				AGLView glView = generalManager.getViewGLCanvasManager().getGLView(
						tmpSlerpAction.getElementId());

				// if (glView instanceof GLTissueViewBrowser)
				// ((GLTissueViewBrowser) glView).setSlerpActive(false);
				//
				if (glView instanceof GLParallelCoordinates) {

					boolean renderConnectionsLeft = true;
					if (glView == focusElement.getGLView())
						renderConnectionsLeft = false;

					((GLParallelCoordinates) glView)
							.setRenderConnectionState(renderConnectionsLeft);

				}
			}

			arSlerpActions.clear();
			iSlerpFactor = 0;

			if (focusElement.getGLView() instanceof GLTissue) {
				openBrowserOverlay();
			} else {
				closeBrowserOverlay();
			}

			// Trigger chain move when selected view has not reached the focus
			// position
			if (lastPickedView != focusElement.getGLView())
				chainMove(lastPickedRemoteLevelElement);
		}
	}

	private void slerpView(final GL gl, SlerpAction slerpAction) {
		int iViewID = slerpAction.getElementId();

		if (iViewID == -1)
			return;

		SlerpMod slerpMod = new SlerpMod();

		if (iSlerpFactor == 0) {
			slerpMod.playSlerpSound();
		}

		Transform transform = slerpMod.interpolate(slerpAction
				.getOriginRemoteLevelElement().getTransform(), slerpAction
				.getDestinationRemoteLevelElement().getTransform(), (float) iSlerpFactor
				/ SLERP_RANGE);

		gl.glPushMatrix();

		slerpMod.applySlerp(gl, transform, true, true);

		renderBucketWall(gl, true);
		generalManager.getViewGLCanvasManager().getGLView(iViewID).displayRemote(gl);

		gl.glPopMatrix();

		// // Check if slerp action is finished
		// if (iSlerpFactor >= SLERP_RANGE) {
		// // arSlerpActions.remove(slerpAction);
		// arSlerpActions.removeAll();
		//
		// iSlerpFactor = 0;
		//			
		// slerpAction.finished();
		//
		// // RemoteLevelElement destinationElement =
		// slerpAction.getDestinationRemoteLevelElement();
		//
		// // updateViewDetailLevels(destinationElement);
		// // bUpdateOffScreenTextures = true;
		// }

		// // After last slerp action is done the line connections are turned on
		// // again
		// if (arSlerpActions.isEmpty()) {
		// if (glConnectionLineRenderer != null) {
		// glConnectionLineRenderer.enableRendering(true);
		// }
		//
		// generalManager.getViewGLCanvasManager().getInfoAreaManager().enable(!bEnableNavigationOverlay);
		// generalManager.getViewGLCanvasManager().getConnectedElementRepresentationManager().clearTransformedConnections();
		// }
	}

	@Override
	public void broadcastElements(EVAOperation type) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearAllSelections() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getDetailedInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumberOfSelections(SelectionType SelectionType) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getShortInfo() {
		// TODO return something usefull
		return "DataFilpper";
	}

	@Override
	protected void handlePickingEvents(EPickingType pickingType,
			EPickingMode pickingMode, int externalPickingID, Pick pick) {

		// if (pickingType != EPickingType.REMOTE_LEVEL_ELEMENT) {
		mouseOverNextDataDomain = null;
		// lastPickedRemoteLevelElement = null;
		// // mouseOverInterface = null;
		// }

		switch (pickingType) {

		case REMOTE_VIEW_SELECTION:

			// TODO: find out corresponding view and data domain type and set
			// it!
			mouseOverDataDomainNode = null;
			mouseOverInterface = null;

			switch (pickingMode) {
			case MOUSE_OVER:

				setDisplayListDirty();
				break;

			case CLICKED:
				break;
			case RIGHT_CLICKED:
				contextMenu.setLocation(pick.getPickedPoint(), getParentGLCanvas()
						.getWidth(), getParentGLCanvas().getHeight());
				contextMenu.setMasterGLView(this);
				break;

			}
			break;

		case REMOTE_LEVEL_ELEMENT:

			switch (pickingMode) {
			case CLICKED:

				// Check if other slerp action is currently running
				if (iSlerpFactor > 0 && iSlerpFactor < SLERP_RANGE) {
					break;
				}

				// glConnectionLineRenderer.enableRendering(true);

				arSlerpActions.clear();
				lastPickedRemoteLevelElement = RemoteElementManager.get().getItem(
						externalPickingID);
				lastPickedView = lastPickedRemoteLevelElement.getGLView();

				chainMove(lastPickedRemoteLevelElement);

				break;
			case MOUSE_OVER:

				lastPickedRemoteLevelElement = RemoteElementManager.get().getItem(
						externalPickingID);
				// mouseOverInterface =
				// lastPickedRemoteLevelElement.getGLView().getViewType();

				break;
			}
			break;

		case INTERFACE_SELECTION:

			mouseOverInterface = null;

			switch (pickingMode) {
			case CLICKED:

				handleDataDomainSelection(externalPickingID);

				break;
			case MOUSE_OVER:

				HistoryNode historyNode = (HistoryNode) historyPath.getLastNode();
				if (historyNode != null) {
					for (int i = 0; i < MAX_HISTORY_DATA_DOMAINS; i++) {

						// String dataDomainType =
						// historyNode.getDataDomainType();
						String interfaceType = historyNode
								.getInterfaceType(externalPickingID);

						if (interfaceType == null) {
							if (historyPath.getPrecedingNode(historyNode) != null
									&& historyPath.getPrecedingNode(historyNode).size() < 1)
								break;

							historyNode = (HistoryNode) historyPath.getPrecedingNode(
									historyNode).get(0);
							mouseOverDataDomainNode = historyNode;

							if (historyNode == null)
								break;
							continue;
						}

						mouseOverInterface = interfaceType;
						break;
					}
				}

				break;
			}

			break;

		case DATA_DOMAIN_SELECTION:

			mouseOverDataDomainNode = null;
			HistoryNode historyNode;
			switch (pickingMode) {
			case CLICKED:
				// handleDataDomainSelection(externalPickingID);

				// historyNode = (HistoryNode) historyPath.getLastNode();
				// if (historyNode != null) {
				// for (int i = 0; i < MAX_HISTORY_DATA_DOMAINS; i++) {
				//
				// String interfaceType = historyNode
				// .getInterfaceType(externalPickingID);
				//
				// if (interfaceType == null) {
				// if (historyPath.getPrecedingNode(historyNode) != null
				// && historyPath.getPrecedingNode(historyNode).size() < 1)
				// break;
				//
				// historyNode = (HistoryNode) historyPath.getPrecedingNode(
				// historyNode).get(0);
				//
				// if (historyNode == null)
				// break;
				// continue;
				// }
				//
				// if
				// (historyNode.getDataDomainType().equals("org.caleydo.datadomain.genetic"))
				// addView(historyNode, "org.caleydo.view.pathwaybrowser");
				// break;
				// }
				// }

				break;

			case MOUSE_OVER:

				historyNode = (HistoryNode) historyPath.getLastNode();
				if (historyNode != null) {
					for (int i = 0; i < MAX_HISTORY_DATA_DOMAINS; i++) {

						String interfaceType = historyNode
								.getInterfaceType(externalPickingID);

						if (interfaceType == null) {
							if (historyPath.getPrecedingNode(historyNode) != null
									&& historyPath.getPrecedingNode(historyNode).size() < 1)
								break;

							historyNode = (HistoryNode) historyPath.getPrecedingNode(
									historyNode).get(0);

							if (historyNode == null)
								break;
							continue;
						}

						mouseOverDataDomainNode = historyNode;
						break;
					}
				}

				break;
			}
			break;

		case NEXT_DATA_DOMAIN_SELECTION:

			mouseOverInterface = null;

			switch (pickingMode) {

			case MOUSE_OVER:

				mouseOverNextDataDomain = determineDataDomainByHash(externalPickingID);

				break;

			case CLICKED:

				for (IDataDomain tmpDataDomain : DataDomainManager.getInstance()
						.getDataDomains()) {
					String tmpDataDomainType = tmpDataDomain.getDataDomainType();
					if (tmpDataDomainType.hashCode() == externalPickingID) {

						HistoryNode dataDomainNode = null;
						if ((!historyPath.getLastNode().getDataDomainType().equals(
								tmpDataDomainType))) {

							// Remove history path in case the user branched the
							// path
							if (mouseOverDataDomainNode != null) {
								for (INode node : historyPath
										.getFollowingNodes(mouseOverDataDomainNode)) {
									historyPath.getGraph().removeVertex(node);
								}
								historyPath.setLastNode(mouseOverDataDomainNode);
							}

							metaViewAnimation = 0;
							dataDomainNode = new HistoryNode(tmpDataDomainType,
									dataDomainViewAssociationManager);
							historyPath.addNode(dataDomainNode);
						}

						if (dataDomainNode != null) {
							lastSelectedDataDomainNode = dataDomainNode;

							for (INode nextDataDomainNode : guidancePath
									.getFollowingNodes(currentGuidanceNode)) {
								if (nextDataDomainNode.getDataDomainType() == dataDomainNode
										.getDataDomainType())
									currentGuidanceNode = (GuidanceNode) nextDataDomainNode;
							}
						}

						addView(dataDomainNode, dataDomainNode
								.getInterfaceType(dataDomainNode.getFirstInterfaceID()));
						break;
					}
				}
				break;
			}
			break;

		case REMOTE_VIEW_REMOVE:

			switch (pickingMode) {
			case CLICKED:

				closeView(externalPickingID);
				break;
			}
			break;
		}
	}

	private String determineDataDomainByHash(int externalPickingID) {
		for (IDataDomain tmpDataDomain : DataDomainManager.getInstance().getDataDomains()) {
			String tmpDataDomainType = tmpDataDomain.getDataDomainType();
			if (tmpDataDomainType.hashCode() == externalPickingID) {
				return tmpDataDomainType;
			}
		}

		// Handle data domain type "organ" - because the data domain
		// class does not exist
		if ("org.caleydo.datadomain.organ".hashCode() == externalPickingID)
			return "org.caleydo.datadomain.organ";

		throw new IllegalStateException("Unable to find data domain for hash");
	}

	private void handleDataDomainSelection(int externalPickingID) {

		HistoryNode historyNode = (HistoryNode) historyPath.getLastNode();
		if (historyNode != null) {
			for (int i = 0; i < MAX_HISTORY_DATA_DOMAINS; i++) {

				String dataDomainType = historyNode.getDataDomainType();
				String interfaceType = historyNode.getInterfaceType(externalPickingID);

				if (interfaceType == null) {
					if (historyPath.getPrecedingNode(historyNode) != null
							&& historyPath.getPrecedingNode(historyNode).size() < 1)
						break;

					historyNode = (HistoryNode) historyPath.getPrecedingNode(historyNode)
							.get(0);

					if (historyNode == null)
						break;
					continue;
				}

				lastSelectedDataDomainNode = historyNode;

				if (interfaceType.contains("view")) {
					addView(historyNode, interfaceType);
				} else
					triggerAnalyticalInterface(dataDomainType, interfaceType);

				if (!interfaceType.equals("org.caleydo.view.tissue"))
					closeBrowserOverlay();

				break;
			}
		}
	}

	private void triggerAnalyticalInterface(String dataDomainType, String interfaceType) {

		if (interfaceType.equals("org.caleydo.analytical.clustering")) {
			ArrayList<ISet> sets = new ArrayList<ISet>();
			sets.add(((ISetBasedDataDomain) DataDomainManager.getInstance()
					.getDataDomain(dataDomainType)).getSet());

			ClusterSetEvent event = new ClusterSetEvent(sets);
			event.setSender(this);
			GeneralManager.get().getEventPublisher().triggerEvent(event);
		}
	}

	private void chainMove(RemoteLevelElement selectedElement) {

		// Clear connection lines
		generalManager.getViewGLCanvasManager()
				.getConnectedElementRepresentationManager().clearAll();

		// Chain slerping to the right
		if (stackElementsLeft.contains(selectedElement)) {

			for (int iElementIndex = stackElementsLeft.size(); iElementIndex >= 0; iElementIndex--) {

				if (iElementIndex < (MAX_SIDE_VIEWS - 1)) {
					arSlerpActions
							.add(new SlerpAction(
									stackElementsLeft.get(iElementIndex + 1),
									stackElementsLeft.get(iElementIndex)));
				}

				if (iElementIndex == 0) {
					arSlerpActions.add(new SlerpAction(stackElementsLeft
							.get(iElementIndex), focusElement));
				}
			}

			arSlerpActions.add(new SlerpAction(focusElement, stackElementsRight.get(0)));

			for (int iElementIndex = 0; iElementIndex < stackElementsRight.size(); iElementIndex++) {

				if (iElementIndex < (MAX_SIDE_VIEWS - 1)) {
					arSlerpActions.add(new SlerpAction(stackElementsRight
							.get(iElementIndex), stackElementsRight
							.get(iElementIndex + 1)));
				}
			}
		}
		// Chain slerping to the left
		else if (stackElementsRight.contains(selectedElement)) {

			for (int iElementIndex = 0; iElementIndex < stackElementsRight.size(); iElementIndex++) {

				if (iElementIndex < (MAX_SIDE_VIEWS - 1)) {
					arSlerpActions.add(new SlerpAction(stackElementsRight
							.get(iElementIndex + 1), stackElementsRight
							.get(iElementIndex)));
				}

				if (iElementIndex == 0) {
					arSlerpActions.add(new SlerpAction(stackElementsRight
							.get(iElementIndex), focusElement));
				}
			}

			arSlerpActions.add(new SlerpAction(focusElement, stackElementsLeft.get(0)));

			for (int iElementIndex = 0; iElementIndex < stackElementsLeft.size(); iElementIndex++) {

				if (iElementIndex < (MAX_SIDE_VIEWS - 1)) {
					arSlerpActions
							.add(new SlerpAction(stackElementsLeft.get(iElementIndex),
									stackElementsLeft.get(iElementIndex + 1)));
				}
			}
		}
	}

	private void freeFocusElementByChainMove(boolean left) {

		if (!left) {

			for (int iElementIndex = 0; iElementIndex < stackElementsRight.size(); iElementIndex++) {

				if (iElementIndex < (MAX_SIDE_VIEWS - 1)) {
					arSlerpActions.add(new SlerpAction(stackElementsRight
							.get(iElementIndex), stackElementsRight
							.get(iElementIndex + 1)));
				}
			}

			arSlerpActions.add(new SlerpAction(focusElement, stackElementsRight.get(0)));
		} else {

			for (int iElementIndex = 0; iElementIndex < stackElementsLeft.size(); iElementIndex++) {

				if (iElementIndex < (MAX_SIDE_VIEWS - 1)) {
					arSlerpActions
							.add(new SlerpAction(stackElementsLeft.get(iElementIndex),
									stackElementsLeft.get(iElementIndex + 1)));
				}
			}

			arSlerpActions.add(new SlerpAction(focusElement, stackElementsLeft.get(0)));

			// for (int iElementIndex = 0; iElementIndex <
			// stackElementsLeft.size(); iElementIndex++) {
			//
			// if (iElementIndex < (MAX_SIDE_VIEWS - 1)) {
			// arSlerpActions
			// .add(new SlerpAction(stackElementsLeft.get(iElementIndex),
			// stackElementsLeft.get(iElementIndex + 1)));
			// }
			// }

			// arSlerpActions.add(new SlerpAction(focusElement,
			// stackElementsLeft.get(0)));
		}
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedDataFlipperView serializedForm = new SerializedDataFlipperView();
		serializedForm.setViewID(this.getID());

		// IViewManager viewManager = generalManager.getViewGLCanvasManager();

		// ArrayList<ASerializedView> remoteViews =
		// new ArrayList<ASerializedView>(focusLevel.getAllElements().size());
		// for (RemoteLevelElement rle : focusLevel.getAllElements()) {
		// if (rle.getContainedElementID() != -1) {
		// AGLEventListener remoteView =
		// viewManager.getGLEventListener(rle.getContainedElementID());
		// remoteViews.add(remoteView.getSerializableRepresentation());
		// }
		// }
		// serializedForm.setFocusViews(remoteViews);
		//
		// remoteViews = new
		// ArrayList<ASerializedView>(stackLevel.getAllElements().size());
		// for (RemoteLevelElement rle : stackLevel.getAllElements()) {
		// if (rle.getContainedElementID() != -1) {
		// AGLEventListener remoteView =
		// viewManager.getGLEventListener(rle.getContainedElementID());
		// remoteViews.add(remoteView.getSerializableRepresentation());
		// }
		// }
		// serializedForm.setStackViews(remoteViews);

		return serializedForm;
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		super.reshape(drawable, x, y, width, height);

		// AGLView glView = focusElement.getGLView();
		// if (glView == null)
		// return;

		// IViewFrustum frustum = glView.getViewFrustum();
		// frustum.setTop(8*fAspectRatio);
		// glView.reshape(drawable, x, y, width, height);
	}

	private void renderDataDomain(final GL gl, HistoryNode node, float x, float y) {

		String dataDomainType = node.getDataDomainType();
		IDataDomain dataDomain = DataDomainManager.getInstance().getDataDomain(
				dataDomainType);
		EIconTextures dataDomainIcon = dataDomain.getIcon();

		float maxViewIcons = 4;

		// Render possible next data domains if mouse over
		if (mouseOverDataDomainNode == node && node != historyPath.getLastNode()
				&& metaViewAnimation >= DATA_DOMAIN_SPACING) {

			Set<String> neighbors = dataDomainGraph.getNeighboursOf(dataDomainType);
			int numberOfVerticalDataDomains = neighbors.size();
			float ySteps = DATA_DOMAIN_HEIGHT / (numberOfVerticalDataDomains) / 1.3f;
			float yNeighbor = y + 0.4f;
			for (String nextDataDomainType : neighbors) {

				// Prevent showing data domain which is next one anyways
				if (nextDataDomainType == historyPath.getFollowingNodes(node).get(0)
						.getDataDomainType())
					continue;

				boolean highlight = false;
				// for (INode nextGuidanceNode : guidancePath
				// .getFollowingNodes(currentGuidanceNode)) {
				// if
				// (nextGuidanceNode.getDataDomainType().equals(nextDataDomainType))
				// {
				// highlight = true;
				// break;
				// }
				// }

				boolean mouseOver = (nextDataDomainType == mouseOverNextDataDomain) ? true
						: false;

				yNeighbor += ySteps;
				renderNextDataDomain(gl, nextDataDomainType, x + 0.9f - metaViewAnimation
						+ DATA_DOMAIN_SPACING, yNeighbor - 0.47f, highlight, mouseOver);

				gl.glLineWidth(5);

				if (!mouseOver)
					gl.glColor3f(0.3f, 0.3f, 0.3f);
				else
					gl.glColor3f(0.15f, 0.15f, 0.15f);

				float x1 = x - metaViewAnimation + 0.5f * DATA_DOMAIN_SCALING_FACTOR
						+ 1.2f;
				float x2 = x - metaViewAnimation + DATA_DOMAIN_SPACING + 0.9f;
				float y1 = y + DATA_DOMAIN_HEIGHT / 2f + 0.15f - 0.47f;
				float y2 = yNeighbor + 0.1f - 0.47f;

				ArrayList<Vec3f> points = new ArrayList<Vec3f>();
				points.add(new Vec3f(x1, y1, DATA_DOMAIN_Z));
				points.add(new Vec3f(x1 + Math.abs((x1 - x2) / 3), y1, DATA_DOMAIN_Z));
				points.add(new Vec3f(x2 - Math.abs((x1 - x2) / 3), y2, DATA_DOMAIN_Z));
				points.add(new Vec3f(x2, y2, DATA_DOMAIN_Z));

				renderSingleCurve(gl, points, 15);
			}
		}

		gl.glTranslatef(x, y, DATA_DOMAIN_Z);

		gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.DATA_DOMAIN_SELECTION, node.getFirstInterfaceID()));

		float r = 1;
		float g = 1;
		float b = 1;
		if (mouseOverDataDomainNode != null && node == mouseOverDataDomainNode) {
			r = 0.5f;
			g = 0.5f;
			b = 0.5f;
		}

		// Data background
		textureManager.renderTexture(gl, EIconTextures.DATA_FLIPPER_DATA_ICON_BACKGROUND,
				new Vec3f(0, 0, 0), new Vec3f(0.51f * DATA_DOMAIN_SCALING_FACTOR, 0, 0),
				new Vec3f(0.51f * DATA_DOMAIN_SCALING_FACTOR,
						0.3f * DATA_DOMAIN_SCALING_FACTOR, 0), new Vec3f(0,
						0.3f * DATA_DOMAIN_SCALING_FACTOR, 0), r, g, b, 1);

		// Data icon
		textureManager.renderTexture(gl, dataDomainIcon, new Vec3f(0f, 0.02f, 0.01f),
				new Vec3f(0.5f * DATA_DOMAIN_SCALING_FACTOR,
						0.02f * DATA_DOMAIN_SCALING_FACTOR, 0.01f), new Vec3f(
						0.5f * DATA_DOMAIN_SCALING_FACTOR,
						0.28f * DATA_DOMAIN_SCALING_FACTOR, 0.01f), new Vec3f(0.0f,
						0.28f * DATA_DOMAIN_SCALING_FACTOR, 0.01f), 1, 1, 1, 1);

		gl.glPopName();

		gl.glTranslatef(0, 0.31f * DATA_DOMAIN_SCALING_FACTOR, 0);

		for (int viewIndex = 0; viewIndex < maxViewIcons; viewIndex++) {

			EIconTextures icon = EIconTextures.DATA_FLIPPER_VIEW_ICON_BACKGROUND_SQUARE;
			if (viewIndex == 0 || viewIndex == maxViewIcons - 1)
				icon = EIconTextures.DATA_FLIPPER_VIEW_ICON_BACKGROUND_ROUNDED;

			if (viewIndex == maxViewIcons - 1) {
				gl.glTranslatef(0, INTERFACE_WIDTH, 0);
				gl.glRotatef(270, 0, 0, 1);
			}

			if (viewIndex < node.getAllInterfaces().length && mouseOverInterface != null
					&& node.getAllInterfaces()[viewIndex] == mouseOverInterface
					&& mouseOverDataDomainNode != null && node == mouseOverDataDomainNode) {
				r = 0.5f;
				g = 0.5f;
				b = 0.5f;
			} else {
				r = 1;
				g = 1;
				b = 1;
			}

			// Interface background
			textureManager.renderTexture(gl, icon, new Vec3f(INTERFACE_WIDTH, 0.0f, 0),
					new Vec3f(0.0f, 0.0f, 0), new Vec3f(0.0f, INTERFACE_WIDTH, 0),
					new Vec3f(INTERFACE_WIDTH, INTERFACE_WIDTH, 0), r, g, b, 1);

			if (viewIndex == maxViewIcons - 1) {
				gl.glRotatef(-270, 0, 0, 1);
				gl.glTranslatef(0, -INTERFACE_WIDTH, 0);
			}

			if (viewIndex < node.getAllInterfaces().length) {

				String viewType = node.getAllInterfaces()[viewIndex];

				RemoteLevelElement element = null;

				if (focusElement.getGLView() != null
						&& node.getGLView(viewType) == focusElement.getGLView()) {
					element = focusElement;
				}

				if (element == null) {
					for (RemoteLevelElement tmpElement : stackElementsLeft) {
						if (tmpElement.getGLView() != null
								&& node.getGLView(viewType) == tmpElement.getGLView()) {
							element = tmpElement;
						}
					}
				}

				if (element == null) {
					for (RemoteLevelElement tmpElement : stackElementsRight) {
						if (tmpElement.getGLView() != null
								&& node.getGLView(viewType) == tmpElement.getGLView()) {
							element = tmpElement;
						}
					}
				}

				if (mouseOverInterface != null && mouseOverInterface.equals(viewType)
						&& mouseOverDataDomainNode != null
						&& mouseOverDataDomainNode == node) {
					INTERFACE_ICON_BACKGROUND_COLOR = new float[] { 1, 1, 1 };
					CONNECTION_LINE_COLOR = new float[] { 0.15f, 0.15f, 0.15f, 1 };

				} else if (element == null) {
					INTERFACE_ICON_BACKGROUND_COLOR = new float[] { 0.5f, 0.5f, 0.5f };
					// } else if (element == lastPickedRemoteLevelElement) {
					// INTERFACE_ICON_BACKGROUND_COLOR = new float[] { 1, 1, 1
					// };
					// CONNECTION_LINE_COLOR = new float[] { 0.15f, 0.15f, 0.15f
					// };
				} else {
					INTERFACE_ICON_BACKGROUND_COLOR = new float[] { 1f, 1f, 1f };
					CONNECTION_LINE_COLOR = new float[] { 0.3f, 0.3f, 0.3f, 1 };
				}

				if (element != null)
					gl.glPushName(pickingManager.getPickingID(iUniqueID,
							EPickingType.REMOTE_LEVEL_ELEMENT, element.getID()));
				gl.glPushName(pickingManager.getPickingID(iUniqueID,
						EPickingType.INTERFACE_SELECTION, node.getInterfaceID(viewType)));
				gl.glPushName(pickingManager.getPickingID(iUniqueID,
						EPickingType.DATA_DOMAIN_SELECTION, node.getFirstInterfaceID()));

				// Render view icon
				textureManager.renderTexture(gl, determineViewIconPath(viewType),
						new Vec3f(INTERFACE_WIDTH - ICON_PADDING, ICON_PADDING, 0),
						new Vec3f(ICON_PADDING, ICON_PADDING, 0), new Vec3f(ICON_PADDING,
								INTERFACE_WIDTH - ICON_PADDING, 0), new Vec3f(
								INTERFACE_WIDTH - ICON_PADDING, INTERFACE_WIDTH
										- ICON_PADDING, 0),
						INTERFACE_ICON_BACKGROUND_COLOR[0],
						INTERFACE_ICON_BACKGROUND_COLOR[1],
						INTERFACE_ICON_BACKGROUND_COLOR[2], 1);

				RemoteLevelElement viewSpawnLevelElement = node.getSpawnPos(viewType);

				Transform transform = viewSpawnLevelElement.getTransform();
				transform.setTranslation(new Vec3f(x + 1.6f + viewIndex
						* (INTERFACE_WIDTH + 0.01f), y + 1.9f, DATA_DOMAIN_Z));
				transform.setScale(new Vec3f(0.01f, 0.01f, 0.01f));
				viewSpawnLevelElement.setTransform(transform);

				if (element != null && node.containsView(element.getGLView())) {

					float xCorrectionRight = 0;
					// FIXME: this correction is not nice - a actual calculation
					// with the angle of the plane would be better
					if (stackElementsRight.contains(element)) {

						if (stackElementsRight.get(0) == element)
							xCorrectionRight = -1.03f;
						else if (stackElementsRight.get(1) == element)
							xCorrectionRight = -1.13f;
						else if (stackElementsRight.get(2) == element)
							xCorrectionRight = -1.23f;
					} else if (focusElement == element) {
						xCorrectionRight += 1.5f;
					}

					transform = element.getTransform();

					float[] viewIconPos = new float[] { INTERFACE_WIDTH / 2f,
							INTERFACE_WIDTH, 0 };
					float[] viewPos = new float[] {
							transform.getTranslation().x() - x - viewIndex
									* (INTERFACE_WIDTH + 0.01f) - 1.5f + xCorrectionRight,
							-y - 1.5f - 0.05f, 0 };

					gl.glColor3fv(CONNECTION_LINE_COLOR, 0);
					renderViewIconToViewRelation(gl, element, viewIconPos, viewPos);
				}

				// for (SlerpAction slerp : arSlerpActions) {
				//
				// AGLView glView =
				// GeneralManager.get().getViewGLCanvasManager()
				// .getGLView(slerp.getElementId());
				//
				// if (glView == null || glView.viewType.equals(viewType)
				// ||
				// ((IDataDomainBasedView<IDataDomain>)glView).getDataDomain()
				// != dataDomain)
				// continue;
				//
				// float xCorrectionRight = 0;
				// // if (stackElementsRight.contains(element))
				// // xCorrectionRight = -1.1f;
				//
				// SlerpMod slerpMod = new SlerpMod();
				// transform = slerpMod.interpolate(slerp
				// .getOriginRemoteLevelElement().getTransform(), slerp
				// .getDestinationRemoteLevelElement().getTransform(),
				// (float) iSlerpFactor / SLERP_RANGE);
				//
				// INTERFACE_ICON_BACKGROUND_COLOR = new float[] { 1f, 1f, 1f };
				// gl.glColor3fv(INTERFACE_ICON_BACKGROUND_COLOR, 0);
				// gl.glLineWidth(2);
				// gl.glBegin(GL.GL_LINES);
				// gl.glVertex3f(INTERFACE_WIDTH / 2f, INTERFACE_WIDTH, 0);
				// gl.glVertex3f(transform.getTranslation().x() - x - viewIndex
				// * (INTERFACE_WIDTH + 0.01f) - 1.5f + xCorrectionRight,
				// -y - 1.5f - 0.05f, transform.getTranslation().z() -
				// DATA_DOMAIN_Z);
				// gl.glEnd();
				// }

				gl.glPopName();
				gl.glPopName();

				if (element != null)
					gl.glPopName();

			}

			gl.glTranslatef(INTERFACE_WIDTH + 0.01f, 0, 0);
		}

		// gl.glPopName();

		gl.glTranslatef(-x - maxViewIcons * (INTERFACE_WIDTH + 0.01f), -y - 0.31f
				* DATA_DOMAIN_SCALING_FACTOR, -DATA_DOMAIN_Z);

	}

	private boolean checkPreCondition(String dataDomainType, String mouseOverInterface) {

		// TODO move conditions to own class

		if (dataDomainType.equals("org.caleydo.datadomain.genetic")) {
			int numberOfPatients = ((ASetBasedDataDomain) DataDomainManager.getInstance()
					.getDataDomain("org.caleydo.datadomain.clinical")).getSet().depth();
			if (numberOfPatients > 40)
				return false;
		} else if (dataDomainType.equals("org.caleydo.datadomain.tissue")) {
			int numberOfPatients = ((ASetBasedDataDomain) DataDomainManager.getInstance()
					.getDataDomain("org.caleydo.datadomain.genetic")).getSet().size();
			if (numberOfPatients > 20)
				return false;
		} else if (dataDomainType.equals("org.caleydo.datadomain.pathway")) {

			for (AGLView view : containedGLViews) {
				if (view instanceof GLPathwayViewBrowser) {

					if (((GLPathwayViewBrowser) view).getRemoteRenderedViews().size() == 0)
						return false;

					return true;
				}
			}
			return false;
		}

		return true;
	}

	private void renderNextDataDomain(final GL gl, String dataDomainType, float x,
			float y, boolean highlight, boolean mouseOver) {

		EIconTextures dataDomainIcon = null;

		// Check if data domain is organ because this datadomain does not exist
		// as plugin.
		if (!dataDomainType.equals("org.caleydo.datadomain.organ")) {
			IDataDomain dataDomain = DataDomainManager.getInstance().getDataDomain(
					dataDomainType);
			dataDomainIcon = dataDomain.getIcon();
		} else
			dataDomainIcon = EIconTextures.DATA_DOMAIN_ORGAN;

		gl.glTranslatef(x, y, DATA_DOMAIN_Z);

		gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.NEXT_DATA_DOMAIN_SELECTION, dataDomainType.hashCode()));

		EIconTextures iconTextureBackgroundRound = EIconTextures.DATA_FLIPPER_DATA_ICON_BACKGROUND_ROUND;
		float r = 1;
		float g = 1;
		float b = 1;
		float a = 1;
		float yIconOffset = 0.02f;

		if (!checkPreCondition(dataDomainType, mouseOverInterface)) {
			r = 1f;
			g = 1f;
			b = 1f;
			// a = 0.4f;
			// r = 0.3f;
			// g = 0.3f;
			// b = 0.3f;
			a = 1;
			// TODO: render exclamation mark

			// TODO: move conditions to own class
			if (mouseOver) {

				String conditionText = "";
				if (dataDomainType.equals("org.caleydo.datadomain.tissue"))
					conditionText = "Filter below 20 patients in order to inspect tissue slices";
				else if (dataDomainType.equals("org.caleydo.datadomain.pathway"))
					conditionText = "Trigger pathway loading in order to inspect pathways ";
				else if (dataDomainType.equals("org.caleydo.datadomain.genetic"))
					conditionText = "Filter below 40 patients in order to inspect their gene expression";

				textRenderer.setColor(0f, 0f, 0f, 1);
				textRenderer.begin3DRendering();
				textRenderer.draw3D(conditionText, 0.35f, 0.05f, 0, 0.0035f);
				textRenderer.end3DRendering();

				r = 0.3f;
				g = 0.3f;
				b = 0.3f;
				a = 1;
			}

			iconTextureBackgroundRound = EIconTextures.DATA_FLIPPER_DATA_ICON_BACKGROUND_ROUND_HIGHLIGHTED;
			yIconOffset = 0.05f;
			textureManager.renderTexture(gl, EIconTextures.DATA_FLIPPER_EXCLAMATION_MARK,
					new Vec3f(0.25f, 0.03f, 0), new Vec3f(0.3f, 0.03f, 0), new Vec3f(
							0.3f, 0.17f, 0), new Vec3f(0.25f, 0.17f, 0), r, g, b, a);

		} else if (mouseOver) {
			r = 0.3f;
			g = 0.3f;
			b = 0.3f;
			a = 1;
		} else if (highlight) {
			// r = 1f;
			// g = 0f;
			// b = 0f;
			// a = 1;
			r = 1f;
			g = 0.3f;
			b = 0.3f;
			a = 1;

			iconTextureBackgroundRound = EIconTextures.DATA_FLIPPER_DATA_ICON_BACKGROUND_ROUND_HIGHLIGHTED;
			yIconOffset = 0.05f;
		}

		// Data background
		textureManager.renderTexture(gl, iconTextureBackgroundRound, new Vec3f(0, 0, 0),
				new Vec3f(0.21f * DATA_DOMAIN_SCALING_FACTOR, 0, 0), new Vec3f(
						0.21f * DATA_DOMAIN_SCALING_FACTOR,
						0.2f * DATA_DOMAIN_SCALING_FACTOR, 0), new Vec3f(0,
						0.2f * DATA_DOMAIN_SCALING_FACTOR, 0), r, g, b, a);

		// Data icon
		textureManager.renderTexture(gl, dataDomainIcon,
				new Vec3f(0f, yIconOffset, 0.01f), new Vec3f(
						0.2f * DATA_DOMAIN_SCALING_FACTOR, yIconOffset
								* DATA_DOMAIN_SCALING_FACTOR, 0.01f), new Vec3f(
						0.2f * DATA_DOMAIN_SCALING_FACTOR,
						0.18f * DATA_DOMAIN_SCALING_FACTOR, 0.01f), new Vec3f(0.0f,
						0.18f * DATA_DOMAIN_SCALING_FACTOR, 0.01f), 1, 1, 1, 1);

		gl.glPopName();

		gl.glTranslatef(-x, -y, -DATA_DOMAIN_Z);

	}

	private void renderViewIconToViewRelation(GL gl, RemoteLevelElement element,
			float[] viewIconPos, float[] viewPos) {

		gl.glLineWidth(5);
		// gl.glBegin(GL.GL_LINES);
		// gl.glVertex3fv(viewIconPos, 0);
		// gl.glVertex3fv(viewPos, 0);
		// gl.glEnd();

		float yLineOffset = (viewPos[1] - viewIconPos[1]);
		float yLineOffsetFactor = 0.8f;

		if (stackElementsLeft.contains(element)) {
			int stackPos = stackElementsLeft.indexOf(element);
			yLineOffsetFactor -= (stackPos + 1) / 10f;
		} else if (stackElementsRight.contains(element)) {
			int stackPos = stackElementsRight.indexOf(element);
			yLineOffsetFactor -= (stackPos + 1) / 10f;
		}

		ArrayList<Vec3f> points = new ArrayList<Vec3f>();
		points.add(new Vec3f(viewIconPos[0], viewIconPos[1], viewIconPos[2]));
		points.add(new Vec3f(viewIconPos[0], viewIconPos[1] + yLineOffset
				* yLineOffsetFactor, viewIconPos[2]));
		points.add(new Vec3f(viewPos[0], viewPos[1] - yLineOffset
				* (1 - yLineOffsetFactor), viewPos[2]));
		points.add(new Vec3f(viewPos[0], viewPos[1], viewPos[2]));
		renderSingleCurve(gl, points, 20);
	}

	public void renderSingleCurve(GL gl, ArrayList<Vec3f> points, int curvePoints) {

		NURBSCurve curve = new NURBSCurve(points, curvePoints);
		points = curve.getCurvePoints();

		gl.glBegin(GL.GL_LINE_STRIP);
		for (int i = 0; i < points.size(); i++) {
			Vec3f point = points.get(i);
			gl.glVertex3f(point.x(), point.y(), point.z());
		}
		gl.glEnd();

		gl.glPopName();
	}

	private String determineViewIconPath(String viewName) {

		if (viewName.equals("org.caleydo.analytical.clustering"))
			return "resources/icons/view/storagebased/clustering.png";

		viewName = viewName.replace("org.caleydo.view.", "");

		if (viewName.contains("hierarchical"))
			viewName = viewName.replace(".hierarchical", "");

		if (viewName.contains("pathwaybrowser") || viewName.contains("tissuebrowser"))
			viewName = viewName.replace("browser", "");

		// FIXME: remove if browser gl view has its own gl class
		if (viewName.equals("tissue"))
			viewName = "browser";

		String subfolder = "";
		if (viewName.contains("parcoords") || viewName.contains("heatmap"))
			subfolder = "storagebased/";

		return "resources/icons/view/" + subfolder + viewName + "/" + viewName + ".png";
	}

	private void renderHandles(final GL gl) {

		// Bucket center (focus)
		RemoteLevelElement element = focusElement;
		AGLView glView = element.getGLView();
		if (glView != null) {
			Transform transform;
			Vec3f translation;
			float fYCorrection = 0f;
			transform = element.getTransform();
			translation = transform.getTranslation();
			gl.glTranslatef(translation.x() - 1.5f, translation.y() - 0.225f - 0.075f
					+ fYCorrection, translation.z() + 0.001f);
			renderNavigationHandleBar(gl, element, 3.2f, 0.075f, false, 2);
			gl.glTranslatef(-translation.x() + 1.5f, -translation.y() + 0.225f + 0.075f
					- fYCorrection, -translation.z() - 0.001f);
		}

		// Left first
		element = stackElementsLeft.get(0);
		glView = element.getGLView();
		if (glView != null) {

			gl.glTranslatef(-0.64f, -1.25f, 4.02f);
			gl.glRotatef(90, 0, 0, 1);
			renderNavigationHandleBar(gl, element, 3.33f, 0.075f, false, 2);
			gl.glRotatef(-90, 0, 0, 1);
			gl.glTranslatef(0.64f, 1.25f, -4.02f);
		}

		// Left second
		element = stackElementsLeft.get(1);
		glView = element.getGLView();
		if (glView != null) {

			gl.glTranslatef(-1.17f, -1.25f, 4.02f);
			gl.glRotatef(90, 0, 0, 1);
			renderNavigationHandleBar(gl, element, 3.32f, 0.075f, false, 2);
			gl.glRotatef(-90, 0, 0, 1);
			gl.glTranslatef(1.17f, 1.25f, -4.02f);
		}

		// Left third
		element = stackElementsLeft.get(2);
		if (element.getGLView() != null) {

			gl.glTranslatef(-1.73f, -1.25f, 4.02f);
			gl.glRotatef(90, 0, 0, 1);
			renderNavigationHandleBar(gl, element, 3.32f, 0.075f, false, 2);
			gl.glRotatef(-90, 0, 0, 1);
			gl.glTranslatef(1.73f, 1.25f, -4.02f);
		}

		// Right first
		element = stackElementsRight.get(0);
		glView = element.getGLView();
		if (element.getGLView() != null) {

			gl.glTranslatef(0.65f, 2.08f, 4.02f);
			gl.glRotatef(-90, 0, 0, 1);
			renderNavigationHandleBar(gl, element, 3.34f, 0.075f, false, 2);
			gl.glRotatef(90, 0, 0, 1);
			gl.glTranslatef(-0.65f, -2.08f, -4.02f);
		}

		// Right second
		element = stackElementsRight.get(1);
		if (element.getGLView() != null) {

			gl.glTranslatef(1.1f, 2.08f, 4.02f);
			gl.glRotatef(-90, 0, 0, 1);
			renderNavigationHandleBar(gl, element, 3.34f, 0.075f, false, 2);
			gl.glRotatef(90, 0, 0, 1);
			gl.glTranslatef(-1.1f, -2.08f, -4.02f);
		}

		// Right thrid
		element = stackElementsRight.get(2);
		if (element.getGLView() != null) {

			gl.glTranslatef(1.55f, 2.08f, 4.02f);
			gl.glRotatef(-90, 0, 0, 1);
			renderNavigationHandleBar(gl, element, 3.34f, 0.075f, false, 2);
			gl.glRotatef(90, 0, 0, 1);
			gl.glTranslatef(-1.55f, -2.08f, -4.02f);
		}
	}

	// private void renderViewConnectionPipes(final GL gl, RemoteLevelElement
	// element) {
	//
	// textureManager.renderTexture(gl,
	// EIconTextures.DATA_FLIPPER_CONNECTION_STRAIGHT, new Vec3f(0, 0, 0),
	// new Vec3f(0.63f, 0, 0), new Vec3f(0.63f, 0.46f, 0), new Vec3f(0, 0.46f,
	// 0), 1, 1, 1, 1);
	//
	// textureManager.renderTexture(gl,
	// EIconTextures.DATA_FLIPPER_CONNECTION_CORNER, new Vec3f(0, 0, 0),
	// new Vec3f(0.63f, 0, 0), new Vec3f(0.63f, 0.46f, 0), new Vec3f(0, 0.46f,
	// 0), 1, 1, 1, 1);
	//
	// }

	private void renderNavigationHandleBar(final GL gl, RemoteLevelElement element,
			float fHandleWidth, float fHandleHeight, boolean bUpsideDown,
			float fScalingFactor) {

		// Render icons
		gl.glTranslatef(0, 2 + fHandleHeight, 0);
		renderSingleHandle(gl, element.getID(), EPickingType.REMOTE_VIEW_DRAG,
				EIconTextures.NAVIGATION_DRAG_VIEW, fHandleHeight, fHandleHeight);
		gl.glTranslatef(fHandleWidth - 2 * fHandleHeight, 0, 0);
		if (bUpsideDown) {
			gl.glRotatef(180, 1, 0, 0);
			gl.glTranslatef(0, fHandleHeight, 0);
		}
		renderSingleHandle(gl, element.getID(), EPickingType.REMOTE_VIEW_LOCK,
				EIconTextures.NAVIGATION_LOCK_VIEW, fHandleHeight, fHandleHeight);
		if (bUpsideDown) {
			gl.glTranslatef(0, -fHandleHeight, 0);
			gl.glRotatef(-180, 1, 0, 0);
		}
		gl.glTranslatef(fHandleHeight, 0, 0);
		renderSingleHandle(gl, element.getID(), EPickingType.REMOTE_VIEW_REMOVE,
				EIconTextures.NAVIGATION_REMOVE_VIEW, fHandleHeight, fHandleHeight);
		gl.glTranslatef(-fHandleWidth + fHandleHeight, -2 - fHandleHeight, 0);

		// Render background (also draggable)
		gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.REMOTE_VIEW_DRAG, element.getID()));
		gl.glColor3f(0.25f, 0.25f, 0.25f);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(0 + fHandleHeight, 2 + fHandleHeight, 0);
		gl.glVertex3f(fHandleWidth - 2 * fHandleHeight, 2 + fHandleHeight, 0);
		gl.glVertex3f(fHandleWidth - 2 * fHandleHeight, 2, 0);
		gl.glVertex3f(0 + fHandleHeight, 2, 0);
		gl.glEnd();

		gl.glPopName();

		// Render view information
		String sText = element.getGLView().getShortInfo();

		int iMaxChars = 50;
		if (sText.length() > iMaxChars) {
			sText = sText.subSequence(0, iMaxChars - 3) + "...";
		}

		float fTextScalingFactor = 0.0027f;

		if (bUpsideDown) {
			gl.glRotatef(180, 1, 0, 0);
			gl.glTranslatef(0, -4 - fHandleHeight, 0);
		}

		textRenderer.setColor(0.7f, 0.7f, 0.7f, 1);
		textRenderer.begin3DRendering();
		textRenderer.draw3D(sText, fHandleWidth / fScalingFactor
				- (float) textRenderer.getBounds(sText).getWidth() / 2f
				* fTextScalingFactor, 2.02f, 0f, fTextScalingFactor);
		textRenderer.end3DRendering();

		if (bUpsideDown) {
			gl.glTranslatef(0, 4 + fHandleHeight, 0);
			gl.glRotatef(-180, 1, 0, 0);
		}
	}

	// FIXME: method copied from bucket
	private void renderSingleHandle(final GL gl, int iRemoteLevelElementID,
			EPickingType ePickingType, EIconTextures eIconTexture, float fWidth,
			float fHeight) {

		gl.glPushName(pickingManager.getPickingID(iUniqueID, ePickingType,
				iRemoteLevelElementID));

		Texture tempTexture = textureManager.getIconTexture(gl, eIconTexture);
		tempTexture.enable();
		tempTexture.bind();

		TextureCoords texCoords = tempTexture.getImageTexCoords();
		gl.glColor3f(1, 1, 1);
		gl.glBegin(GL.GL_POLYGON);

		// if (eOrientation == EOrientation.TOP) {
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(0, -fHeight, 0f);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(0, 0, 0f);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(fWidth, 0, 0f);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(fWidth, -fHeight, 0f);
		gl.glEnd();
		// }
		// else if (eOrientation == EOrientation.LEFT) {
		// gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		// gl.glVertex3f(0, -fHeight, 0f);
		// gl.glTexCoord2f(texCoords.left(), texCoords.top());
		// gl.glVertex3f(0, 0, 0f);
		// gl.glTexCoord2f(texCoords.right(), texCoords.top());
		// gl.glVertex3f(fWidth, 0, 0f);
		// gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		// gl.glVertex3f(fWidth, -fHeight, 0f);
		// gl.glEnd();
		// }

		tempTexture.disable();

		gl.glPopName();
	}

	public void renderBucketWall(final GL gl, boolean bRenderBorder) {

		gl.glLineWidth(2);

		// if (arSlerpActions.isEmpty()) {
		gl.glColor4f(1f, 1f, 1f, 1.0f); // normal mode
		// }
		// else {
		// gl.glColor4f(1f, 1f, 1f, 0.3f);
		// }

		if (!newViews.isEmpty()) {
			gl.glColor4f(1f, 1f, 1f, 0.3f);
		}

		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(0, 0, -0.01f);
		gl.glVertex3f(0, 8, -0.01f);
		gl.glVertex3f(8, 8, -0.01f);
		gl.glVertex3f(8, 0, -0.01f);
		gl.glEnd();

		if (!bRenderBorder)
			return;

		gl.glColor4f(0.15f, 0.15f, 0.15f, 1f);
		gl.glLineWidth(4f);

		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(0, 0, -0.01f);
		gl.glVertex3f(0, 8, -0.01f);
		gl.glVertex3f(8, 8, -0.01f);
		gl.glVertex3f(8, 0, -0.01f);
		gl.glEnd();
	}

	private void updateViewDetailLevels(RemoteLevelElement element) {

		AGLView glActiveSubView = element.getGLView();

		if (glActiveSubView == null)
			return;

		glActiveSubView.setRemoteLevelElement(element);

		// Update detail level of moved view when slerp action is finished;
		if (element == focusElement) {
			glActiveSubView.setDetailLevel(EDetailLevel.HIGH);
		} else {
			glActiveSubView.setDetailLevel(EDetailLevel.LOW);
		}
	}

	@Override
	public void registerEventListeners() {

		super.registerEventListeners();

		addPathwayListener = new AddPathwayListener();
		addPathwayListener.setHandler(this);
		eventPublisher.addListener(LoadPathwayEvent.class, addPathwayListener);

		loadPathwaysByGeneListener = new LoadPathwaysByGeneListener();
		loadPathwaysByGeneListener.setHandler(this);
		eventPublisher.addListener(LoadPathwaysByGeneEvent.class,
				loadPathwaysByGeneListener);
	}

	@Override
	public void unregisterEventListeners() {

		super.unregisterEventListeners();

		if (addPathwayListener != null) {
			eventPublisher.removeListener(addPathwayListener);
			addPathwayListener = null;
		}

		if (loadPathwaysByGeneListener != null) {
			eventPublisher.removeListener(loadPathwaysByGeneListener);
			loadPathwaysByGeneListener = null;
		}
	}

	@Override
	public void addPathwayView(int iPathwayID) {
		// isPathwayContentAvailable = true;

	}

	@Override
	public void loadDependentPathways(Set<PathwayGraph> newPathwayGraphs) {
		metaViewAnimation = 0;
		HistoryNode dataDomainNode = new HistoryNode("org.caleydo.datadomain.pathway",
				dataDomainViewAssociationManager);
		historyPath.addNode(dataDomainNode);
		dataDomainNode.addGLView(pathwayBrowserView);
		openView(pathwayBrowserView, dataDomainNode);
	}

	@Override
	public void setConnectionLinesEnabled(boolean enabled) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setGeneMappingEnabled(boolean geneMappingEnabled) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setNeighborhoodEnabled(boolean neighborhoodEnabled) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPathwayTexturesEnabled(boolean pathwayTexturesEnabled) {
		// TODO Auto-generated method stub

	}

	@Override
	public void toggleNavigationMode() {
		// TODO Auto-generated method stub

	}

	@Override
	public void toggleZoom() {
		// TODO Auto-generated method stub
	}

	private void addView(HistoryNode dataDomainNode, String interfaceType) {

		// Check if view already exists
		if (dataDomainNode.containsViewForType(interfaceType)) {
			AGLView glView = dataDomainNode.getGLView(interfaceType);

			if (!isViewOpen(glView))
				openView(glView, dataDomainNode);

			return;
		}

		// Do not create a pathway browser because it is created in the init
		if (interfaceType.equals("org.caleydo.view.pathwaybrowser")) {
			dataDomainNode.addGLView(pathwayBrowserView);
			openView(pathwayBrowserView, dataDomainNode);
			return;
		}

		ASerializedView serView = addView(dataDomainNode.getDataDomainType(),
				interfaceType);
		if (serView != null)
			newViews.add(serView);
	}

	private ASerializedView addView(String dataDomainType, String interfaceType) {

		ASerializedView serView = null;
		if (interfaceType.equals("org.caleydo.view.parcoords")) {
			serView = new SerializedParallelCoordinatesView(dataDomainType);
		} else if (interfaceType.equals("org.caleydo.view.parcoords")) {
			serView = new SerializedParallelCoordinatesView(dataDomainType);
		} else if (interfaceType.equals("org.caleydo.view.heatmap.hierarchical")) {
			serView = new SerializedHierarchicalHeatMapView(dataDomainType);
		} else if (interfaceType.equals("org.caleydo.view.tissuebrowser")) {
			serView = new SerializedTissueViewBrowserView(dataDomainType);
		} else if (interfaceType.equals("org.caleydo.view.pathwaybrowser")) {
			serView = new SerializedPathwayViewBrowserView(dataDomainType);
		} else if (interfaceType.equals("org.caleydo.view.glyph")) {
			serView = new SerializedGlyphView(dataDomainType);
		} else if (interfaceType.equals("org.caleydo.view.browser")) {
			serView = new SerializedHTMLBrowserView(dataDomainType);
		} else if (interfaceType.equals("org.caleydo.view.tissue")) {
			serView = new SerializedTissueView(dataDomainType);
		}

		return serView;
	}

	private void openView(AGLView view, HistoryNode dataDomainNode) {

		RemoteLevelElement viewSpawnElement = dataDomainNode.getSpawnPos(view
				.getViewType());
		RemoteLevelElement destinationElement = null;

		if (focusElement.isFree()) {
			viewSpawnElement.setGLView(view);
			view.setRemoteLevelElement(focusElement);
			view.setDetailLevel(EDetailLevel.HIGH);
			destinationElement = focusElement;
		} else {

			boolean freeFocusToLeft = false;

			if (dataDomainNode != historyPath.getLastNode()
					&& !dataDomainNode.containsView(focusElement.getGLView()))
				freeFocusToLeft = false;
			else {
				freeFocusToLeft = true;
			}

			freeFocusElementByChainMove(freeFocusToLeft);
			destinationElement = focusElement;
		}

		if (destinationElement != null) {
			lastPickedView = view;
			lastPickedRemoteLevelElement = focusElement;
			viewSpawnElement.setGLView(view);
			arSlerpActions.add(new SlerpAction(viewSpawnElement, destinationElement));
		}
	}

	private boolean isViewOpen(AGLView glView) {

		for (RemoteLevelElement element : allElements) {
			if (element.getGLView() == glView) {
				// chainMove(element);
				lastPickedRemoteLevelElement = element;
				lastPickedView = lastPickedRemoteLevelElement.getGLView();
				chainMove(lastPickedRemoteLevelElement);
				// // arSlerpActions.add(new SlerpAction(focusElement,
				// element));
				return true;
			}
		}
		return false;
	}

	private void closeView(int remoteElementID) {

		RemoteLevelElement sourceElement = RemoteElementManager.get().getItem(
				remoteElementID);
		AGLView view = sourceElement.getGLView();

		RemoteLevelElement destinationElement = null;

		// Node historyNode = historyPath.getLastNode();
		// if (historyNode != null) {
		// for (int i = 0; i < MAX_HISTORY_DATA_DOMAINS; i++) {
		//
		// if (historyNode.containsView(view)) {
		// destinationElement = historyNode.getSpawnPos(view.getViewType());
		//
		// break;
		// }
		// }
		// }

		HistoryNode historyNode = (HistoryNode) historyPath.getLastNode();
		if (historyNode != null) {
			for (int i = 0; i < MAX_HISTORY_DATA_DOMAINS; i++) {

				if (historyNode.containsView(view)) {
					destinationElement = historyNode.getSpawnPos(view.getViewType());

					break;
				}

				historyNode = (HistoryNode) historyPath.getPrecedingNode(historyNode)
						.get(0);

				if (historyNode == null)
					break;
			}
		}

		if (destinationElement != null) {
			arSlerpActions.add(new SlerpAction(sourceElement, destinationElement));
		}
	}

	public void showFocusViewFullScreen(boolean showFocusViewFullScreen) {

		this.showFocusViewFullScreen = showFocusViewFullScreen;
		AGLView focusView = focusElement.getGLView();

		if (focusView == null)
			return;

		if (showFocusViewFullScreen) {
			focusTransformFullScreen.setTranslation(new Vec3f(-4 / fAspectRatio * 0.4f,
					-2.5f, 0));
			focusTransformFullScreen.setScale(new Vec3f(1, 1, 1));
			focusElement.setTransform(focusTransformFullScreen);
		} else {
			focusElement.setTransform(focusTransform);
		}

		focusView.setDisplayListDirty();
	}

	private void openBrowserOverlay() {

		GeneralManager.get().getGUIBridge().getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				if (browserOverlayShell == null) {
					browserOverlayShell = new Shell(SWT.NO_TRIM | SWT.RESIZE);

					int x = 729;
					int y = 143;

					browserOverlayShell.setBounds(x, y, 764, 760);

					if (browserView == null) {
						for (IView view : GeneralManager.get().getViewGLCanvasManager()
								.getAllItems()) {
							if (view instanceof HTMLBrowser) {
								browserView = (HTMLBrowser) view;
								break;
							}
						}
					}

					browserView.getComposite().setParent(browserOverlayShell);

					FillLayout fillLayout = new FillLayout(SWT.VERTICAL);
					fillLayout.marginHeight = 5;
					fillLayout.marginWidth = 5;
					fillLayout.spacing = 1;
					browserOverlayShell.setLayout(fillLayout);
					browserOverlayShell.open();
				}

				browserOverlayShell.setVisible(true);
				browserOverlayShell.open();
				browserView.makeRegularScreenshots(true);
			}
		});
	}

	private void closeBrowserOverlay() {

		GeneralManager.get().getGUIBridge().getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {

				if (browserOverlayShell != null && !browserOverlayShell.isDisposed())
					browserOverlayShell.setVisible(false);

				if (glBrowserImageView != null) {
					glBrowserImageView.updateTexture();
				}

				if (browserView != null)
					browserView.makeRegularScreenshots(false);
			}
		});
	}
}
