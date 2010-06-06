package org.caleydo.view.dataflipper;

import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import gleem.linalg.open.Transform;

import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.caleydo.core.manager.datadomain.AssociationManager;
import org.caleydo.core.manager.datadomain.DataDomainGraph;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.datadomain.IDataDomainBasedView;
import org.caleydo.core.manager.event.data.ClusterSetEvent;
import org.caleydo.core.manager.event.view.ViewActivationEvent;
import org.caleydo.core.manager.event.view.remote.LoadPathwayEvent;
import org.caleydo.core.manager.event.view.remote.LoadPathwaysByGeneEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.path.Node;
import org.caleydo.core.manager.path.Path;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.view.ConnectedElementRepresentationManager;
import org.caleydo.core.manager.view.RemoteRenderingTransformer;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.collection.Pair;
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
import org.caleydo.core.view.swt.ASWTView;
import org.caleydo.rcp.view.listener.AddPathwayListener;
import org.caleydo.rcp.view.listener.IRemoteRenderingHandler;
import org.caleydo.rcp.view.listener.LoadPathwaysByGeneListener;
import org.caleydo.view.browser.SerializedHTMLBrowserView;
import org.caleydo.view.heatmap.hierarchical.SerializedHierarchicalHeatMapView;
import org.caleydo.view.parcoords.SerializedParallelCoordinatesView;
import org.caleydo.view.pathwaybrowser.SerializedPathwayViewBrowserView;
import org.caleydo.view.tissuebrowser.SerializedTissueViewBrowserView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.sun.opengl.util.j2d.TextRenderer;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;

public class GLDataFlipper extends AGLView implements IGLRemoteRenderingView,
		IRemoteRenderingHandler {

	public final static String VIEW_ID = "org.caleydo.view.dataflipper";

	private static final int SLERP_RANGE = 1000;
	private static final int SLERP_SPEED = 1400;

	private static final int MAX_SIDE_VIEWS = 10;

	private static float DATA_DOMAIN_Z = 4;

	private static float DATA_DOMAIN_SCALING_FACTOR = 1f;
	private static float MAX_HISTORY_DATA_DOMAINS = 5;
	private static float INTERFACE_WIDTH = 0.12f * DATA_DOMAIN_SCALING_FACTOR;
	private static float ICON_PADDING = 0.015f * DATA_DOMAIN_SCALING_FACTOR;
	private static float DATA_DOMAIN_SPACING = 1.2f;
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

	private HashMap<String, HashMap<String, RemoteLevelElement>> viewSpawnPos;

	protected AGLConnectionLineRenderer glConnectionLineRenderer;

	private ArrayList<Pair<String, String>> possibleInterfacesWithDataDomain;

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

	private DataDomainGraph dataDomainGraph;
	private Path historyPath;

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
	private String focusDataDomainType = startDataDomainType;

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
		possibleInterfacesWithDataDomain = new ArrayList<Pair<String, String>>();
		viewSpawnPos = new HashMap<String, HashMap<String, RemoteLevelElement>>();

		dataDomainGraph = new DataDomainGraph();
		historyPath = new Path();
		historyPath.addNode(new Node(startDataDomainType, ""));

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

		for (String dataDomainType : dataDomainGraph.getGraph().vertexSet()) {
			init(dataDomainType);
		}
	}

	private void initDataDomainViewAssociation() {

		dataDomainViewAssociationManager = new AssociationManager();

		String dataDomainType = "org.caleydo.datadomain.genetic";
		dataDomainViewAssociationManager.registerDatadomainTypeViewTypeAssociation(
				dataDomainType, "org.caleydo.view.heatmap.hierarchical");
		dataDomainViewAssociationManager.registerDatadomainTypeViewTypeAssociation(
				dataDomainType, "org.caleydo.view.parcoords");
		// dataDomainViewAssociationManager.registerDatadomainTypeViewTypeAssociation(
		// dataDomainType, "org.caleydo.analytical.clustering");
		dataDomainViewAssociationManager.registerDatadomainTypeViewTypeAssociation(
				dataDomainType, "org.caleydo.view.browser");

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
	}

	private void init(String dataDomainType) {

		HashMap<String, RemoteLevelElement> viewSpawn = new HashMap<String, RemoteLevelElement>();
		Set<String> possibleViews = dataDomainViewAssociationManager
				.getViewTypesForDataDomain(dataDomainType);
		for (String viewType : possibleViews) {
			RemoteLevelElement remoteLevelElement = new RemoteLevelElement(null);
			remoteLevelElement.setTransform(new Transform());
			viewSpawn.put(viewType, remoteLevelElement);

		}

		viewSpawnPos.put(dataDomainType, viewSpawn);
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

			renderDataDomains(gl, focusDataDomainType, 1f, -2.55f);

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

		float height = 0.95f;

		if (metaViewAnimation < 1)
			metaViewAnimation += 0.01f;

		// gl.glScalef(9f/10, 9f/10, 9f/10);
		renderDataDomain(gl, historyPath.getLastNode(), x - metaViewAnimation, y + height
				/ 2f);
		// gl.glScalef(10f/9, 10f/9, 10f/9);

		Set<String> neighbors = dataDomainGraph.getNeighboursOf(focusDataDomainType);
		int numberOfVerticalDataDomains = neighbors.size() + 1;

		// String lastHistoryDataDomainType = "";
		// if (historyPath.getLastNode() != null) {
		// lastHistoryDataDomainType =
		// historyPath.getLastNode().getDataDomainType();
		// }

		// for (String nextDataDomainType : neighbors) {
		// if (lastHistoryDataDomainType.equals(nextDataDomainType)) {
		// numberOfVerticalDataDomains--;
		// break;
		// }
		// }

		// Render past data domains
		Node historyNode = historyPath.getLastNode();
		if (historyNode != null) {
			for (int i = 0; i < MAX_HISTORY_DATA_DOMAINS; i++) {

				if (historyPath.getPrecedingNode(historyNode).size() < 1)
					break;

				historyNode = historyPath.getPrecedingNode(historyNode).get(0);

				if (historyNode == null)
					break;

				renderDataDomain(gl, historyNode, x - metaViewAnimation - (i + 1)
						* DATA_DOMAIN_SPACING, y + height / 2f);

				gl.glLineWidth(5);
				gl.glColor3f(0.3f, 0.3f, 0.3f);
				gl.glBegin(GL.GL_LINES);
				gl.glVertex3f(x - metaViewAnimation + 0.5f * DATA_DOMAIN_SCALING_FACTOR,
						y + height / 2f + 0.15f, DATA_DOMAIN_Z);
				gl.glVertex3f(x - metaViewAnimation + 0.5f - DATA_DOMAIN_SPACING
						* (i + 1), y + height / 2f + 0.15f, DATA_DOMAIN_Z);
				gl.glEnd();
			}
		}

		float ySteps = height / (numberOfVerticalDataDomains);
		float yNeighbor = y + 0.2f;
		// Render possible next data domains
		for (String nextDataDomainType : neighbors) {

			yNeighbor += ySteps;
			renderNextDataDomain(gl, nextDataDomainType, x - metaViewAnimation
					+ DATA_DOMAIN_SPACING, yNeighbor);

			gl.glLineWidth(5);
			gl.glColor3f(0.3f, 0.3f, 0.3f);

			float x1 = x - metaViewAnimation + 0.5f * DATA_DOMAIN_SCALING_FACTOR;
			float x2 = x - metaViewAnimation + DATA_DOMAIN_SPACING;
			float y1 = y + height / 2f + 0.15f;
			float y2 = yNeighbor + 0.1f;

			ArrayList<Vec3f> points = new ArrayList<Vec3f>();
			points.add(new Vec3f(x1, y1, DATA_DOMAIN_Z));
			points.add(new Vec3f(x1 + Math.abs((x1 - x2) / 3), y1, DATA_DOMAIN_Z));
			points.add(new Vec3f(x2 - Math.abs((x1 - x2) / 3), y2, DATA_DOMAIN_Z));
			points.add(new Vec3f(x2, y2, DATA_DOMAIN_Z));

			renderSingleCurve(gl, points, 30);
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
			renderBorder = true;

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

			if (serView instanceof SerializedHTMLBrowserView) {

				// openBrowser();

				for (IView view : GeneralManager.get().getViewGLCanvasManager()
						.getAllItems()) {
					if (view instanceof ASWTView) {
						final ASWTView browserView = (ASWTView) view;

						GeneralManager.get().getGUIBridge().getDisplay().asyncExec(
								new Runnable() {

									@Override
									public void run() {
										Shell shell = new Shell(SWT.NO_TRIM | SWT.RESIZE);
										shell.setBounds(730, 150, 760, 760);

										browserView.getComposite().setParent(shell);

										FillLayout fillLayout = new FillLayout(
												SWT.VERTICAL);
										fillLayout.marginHeight = 5;
										fillLayout.marginWidth = 5;
										fillLayout.spacing = 1;
										shell.setLayout(fillLayout);
										shell.open();
									}
								});
						break;
					}
				}

			} else {
				AGLView view = createView(gl, serView);

				// TODO: remove when activating slerp
				view.initRemote(gl, this, glMouseListener, infoAreaManager);
				// view.getViewFrustum().considerAspectRatio(true);

				containedGLViews.add(view);
				historyPath.getLastNode().addView(view);

				openView(view);

				if (newViews.isEmpty()) {
					triggerToolBarUpdate();
					enableUserInteraction();
				}
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

				// AGLView glView =
				// generalManager.getViewGLCanvasManager().getGLView(
				// tmpSlerpAction.getElementId());

				// if (glView instanceof GLTissueViewBrowser)
				// ((GLTissueViewBrowser) glView).setSlerpActive(false);
				//
				// if (glView instanceof GLParallelCoordinates
				// && glView.getSet().getSetType() ==
				// ESetType.GENE_EXPRESSION_DATA) {
				//
				// boolean renderConnectionsLeft = true;
				// if (glView == focusElement.getGLView())
				// renderConnectionsLeft = false;
				//
				// ((GLParallelCoordinates) glView)
				// .setRenderConnectionState(renderConnectionsLeft);
				//
				// }
			}

			arSlerpActions.clear();
			iSlerpFactor = 0;

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void handlePickingEvents(EPickingType pickingType,
			EPickingMode pickingMode, int externalPickingID, Pick pick) {

		// isPatientAlternativeGuideActive = false;
		switch (pickingType) {

		case REMOTE_VIEW_SELECTION:
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

				break;
			}
			break;

		case VIEW_TYPE_SELECTION:
			switch (pickingMode) {
			case CLICKED:

				// AGLView view =
				// GeneralManager.get().getViewGLCanvasManager().getGLView(
				// externalPickingID);
				// String viewType =
				// possibleViewsWithDataDomain.get(externalPickingID).getFirst();
				//				
				// RemoteLevelElement element = null;
				// if (focusElement.getGLView() != null &&
				// focusElement.getGLView().getViewType().equals(viewType)) {
				// element = focusElement;
				// }
				//
				// for (RemoteLevelElement tmpElement : stackElementsLeft) {
				// if (tmpElement.getGLView() != null &&
				// tmpElement.getGLView().getViewType().equals(viewType)) {
				// element = tmpElement;
				// break;
				// }
				// }
				//
				// for (RemoteLevelElement tmpElement : stackElementsRight) {
				// if (tmpElement.getGLView() != null &&
				// tmpElement.getGLView().getViewType().equals(viewType)) {
				// element = tmpElement;
				// break;
				// }
				// }

				// // Check if view already exists - if it exists, the view will
				// be
				// // closed
				// if (element != null)
				// closeView(element.getID());
				// else

				String dataDomainType = possibleInterfacesWithDataDomain.get(
						externalPickingID).getSecond();
				String interfaceType = possibleInterfacesWithDataDomain.get(
						externalPickingID).getFirst();

				if (interfaceType.contains("view"))
					addView(dataDomainType, interfaceType);
				else
					triggerAnalyticalInterface(dataDomainType, interfaceType);

				break;

			}
			break;

		case DATA_DOMAIN_SELECTION:

			switch (pickingMode) {
			case CLICKED:

				String dataDomainType = possibleInterfacesWithDataDomain.get(
						externalPickingID).getSecond();
				String interfaceType = possibleInterfacesWithDataDomain.get(
						externalPickingID).getFirst();

				if (interfaceType.contains("view"))
					addView(dataDomainType, interfaceType);
				else
					triggerAnalyticalInterface(dataDomainType, viewType);

				break;
			}
			break;

		case NEXT_DATA_DOMAIN_SELECTION:

			switch (pickingMode) {
			case CLICKED:

				for (IDataDomain tmpDataDomain : DataDomainManager.getInstance()
						.getDataDomains()) {
					String tmpDataDomainType = tmpDataDomain.getDataDomainType();
					if (tmpDataDomainType.hashCode() == externalPickingID) {
						String[] possibleInterfaces = dataDomainViewAssociationManager
								.getViewTypesForDataDomain(tmpDataDomainType).toArray(
										new String[dataDomainViewAssociationManager
												.getViewTypesForDataDomain(
														tmpDataDomainType).size()]);

						addView(tmpDataDomain.getDataDomainType(), possibleInterfaces[0]);
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

		if (left) {

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

	private void renderDataDomain(final GL gl, Node node, float x, float y) {

		String dataDomainType = node.getDataDomainType();
		IDataDomain dataDomain = DataDomainManager.getInstance().getDataDomain(
				dataDomainType);
		EIconTextures dataDomainIcon = dataDomain.getIcon();

		float maxViewIcons = 4;

		gl.glTranslatef(x, y, DATA_DOMAIN_Z);

		gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.DATA_DOMAIN_SELECTION, possibleInterfacesWithDataDomain
						.size()));

		// Data background
		textureManager.renderTexture(gl, EIconTextures.DATA_FLIPPER_DATA_ICON_BACKGROUND,
				new Vec3f(0, 0, 0), new Vec3f(0.51f * DATA_DOMAIN_SCALING_FACTOR, 0, 0),
				new Vec3f(0.51f * DATA_DOMAIN_SCALING_FACTOR,
						0.3f * DATA_DOMAIN_SCALING_FACTOR, 0), new Vec3f(0,
						0.3f * DATA_DOMAIN_SCALING_FACTOR, 0), 1, 1, 1, 1);

		// Data icon
		textureManager.renderTexture(gl, dataDomainIcon, new Vec3f(0f, 0.02f, 0.01f),
				new Vec3f(0.5f * DATA_DOMAIN_SCALING_FACTOR,
						0.02f * DATA_DOMAIN_SCALING_FACTOR, 0.01f), new Vec3f(
						0.5f * DATA_DOMAIN_SCALING_FACTOR,
						0.28f * DATA_DOMAIN_SCALING_FACTOR, 0.01f), new Vec3f(0.0f,
						0.28f * DATA_DOMAIN_SCALING_FACTOR, 0.01f), 1, 1, 1, 1);

		gl.glPopName();

		gl.glTranslatef(0, 0.31f * DATA_DOMAIN_SCALING_FACTOR, 0);

		String[] possibleInterfaces = dataDomainViewAssociationManager
				.getViewTypesForDataDomain(dataDomainType).toArray(
						new String[dataDomainViewAssociationManager
								.getViewTypesForDataDomain(dataDomainType).size()]);

		for (int viewIndex = 0; viewIndex < maxViewIcons; viewIndex++) {

			EIconTextures icon = EIconTextures.DATA_FLIPPER_VIEW_ICON_BACKGROUND_SQUARE;
			if (viewIndex == 0 || viewIndex == maxViewIcons - 1)
				icon = EIconTextures.DATA_FLIPPER_VIEW_ICON_BACKGROUND_ROUNDED;

			if (viewIndex == maxViewIcons - 1) {
				gl.glTranslatef(0, INTERFACE_WIDTH, 0);
				gl.glRotatef(270, 0, 0, 1);
			}

			textureManager.renderTexture(gl, icon, new Vec3f(INTERFACE_WIDTH, 0.0f, 0),
					new Vec3f(0.0f, 0.0f, 0), new Vec3f(0.0f, INTERFACE_WIDTH, 0),
					new Vec3f(INTERFACE_WIDTH, INTERFACE_WIDTH, 0), 1, 1, 1, 1);

			if (viewIndex == maxViewIcons - 1) {
				gl.glRotatef(-270, 0, 0, 1);
				gl.glTranslatef(0, -INTERFACE_WIDTH, 0);
			}

			if (viewIndex < possibleInterfaces.length) {

				String viewType = possibleInterfaces[viewIndex];

				Pair<String, String> interfaceDatadomainPair = new Pair<String, String>(
						viewType, dataDomainType);
				possibleInterfacesWithDataDomain.add(interfaceDatadomainPair);

				RemoteLevelElement element = null;

				if (focusElement.getGLView() != null
						&& focusElement.getGLView().getViewType().equals(viewType)
						&& focusElement.getDataDomainBasedView().getDataDomain() == dataDomain) {
					element = focusElement;
				}

				if (element == null) {
					for (RemoteLevelElement tmpElement : stackElementsLeft) {
						if (tmpElement.getGLView() != null
								&& tmpElement.getGLView().getViewType().equals(viewType)
								&& tmpElement.getDataDomainBasedView().getDataDomain() == dataDomain) {
							element = tmpElement;
						}
					}
				}

				if (element == null) {
					for (RemoteLevelElement tmpElement : stackElementsRight) {
						if (tmpElement.getGLView() != null
								&& tmpElement.getGLView().getViewType().equals(viewType)
								&& tmpElement.getDataDomainBasedView().getDataDomain() == dataDomain) {
							element = tmpElement;
						}
					}
				}

				if (element == null) {
					INTERFACE_ICON_BACKGROUND_COLOR = new float[] { 0.5f, 0.5f, 0.5f };
				} else if (element == lastPickedRemoteLevelElement) {
					INTERFACE_ICON_BACKGROUND_COLOR = new float[] { 1, 1, 1 };
					CONNECTION_LINE_COLOR = INTERFACE_ICON_BACKGROUND_COLOR;
				} else {
					INTERFACE_ICON_BACKGROUND_COLOR = new float[] { 1f, 1f, 1f };
					CONNECTION_LINE_COLOR = new float[] { 0.3f, 0.3f, 0.3f, 1 };
				}

				if (element != null)
					gl.glPushName(pickingManager.getPickingID(iUniqueID,
							EPickingType.REMOTE_LEVEL_ELEMENT, element.getID()));
				gl.glPushName(pickingManager.getPickingID(iUniqueID,
						EPickingType.VIEW_TYPE_SELECTION,
						possibleInterfacesWithDataDomain.size() - 1));

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

				RemoteLevelElement viewSpawnLevelElement = viewSpawnPos.get(
						dataDomainType).get(viewType);

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
				// || glView.getDataDomain() != dataDomain)
				// continue;
				//
				// float xCorrectionRight = 0;
				// // if (stackElementsRight.contains(element))
				// // xCorrectionRight = -1.1f;
				//
				// SlerpMod slerpMod = new SlerpMod();
				// Transform transform = slerpMod.interpolate(slerp
				// .getOriginRemoteLevelElement().getTransform(), slerp
				// .getDestinationRemoteLevelElement().getTransform(),
				// (float) iSlerpFactor / SLERP_RANGE);
				//
				// viewIconBackgroundColor = new float[] { 1f, 1f, 1f };
				// gl.glColor3fv(viewIconBackgroundColor, 0);
				// gl.glLineWidth(2);
				// gl.glBegin(GL.GL_LINES);
				// gl.glVertex3f(viewIconWidth / 2f, viewIconWidth, 0);
				// gl.glVertex3f(transform.getTranslation().x() - x - viewIndex
				// * (viewIconWidth + 0.01f) - 1.5f + xCorrectionRight,
				// -y - 1.5f - 0.05f, transform.getTranslation().z()-z);
				// gl.glEnd();
				// }

				gl.glPopName();

				if (element != null)
					gl.glPopName();

			}

			gl.glTranslatef(INTERFACE_WIDTH + 0.01f, 0, 0);
		}

		gl.glTranslatef(-x - maxViewIcons * (INTERFACE_WIDTH + 0.01f), -y - 0.31f
				* DATA_DOMAIN_SCALING_FACTOR, -DATA_DOMAIN_Z);

	}

	private void renderNextDataDomain(final GL gl, String dataDomainType, float x, float y) {

		IDataDomain dataDomain = DataDomainManager.getInstance().getDataDomain(
				dataDomainType);
		EIconTextures dataDomainIcon = dataDomain.getIcon();

		gl.glTranslatef(x, y, DATA_DOMAIN_Z);

		gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.NEXT_DATA_DOMAIN_SELECTION, dataDomainType.hashCode()));

		// Data background
		textureManager.renderTexture(gl,
				EIconTextures.DATA_FLIPPER_DATA_ICON_BACKGROUND_ROUND,
				new Vec3f(0, 0, 0), new Vec3f(0.21f * DATA_DOMAIN_SCALING_FACTOR, 0, 0),
				new Vec3f(0.21f * DATA_DOMAIN_SCALING_FACTOR,
						0.2f * DATA_DOMAIN_SCALING_FACTOR, 0), new Vec3f(0,
						0.2f * DATA_DOMAIN_SCALING_FACTOR, 0), 1, 1, 1, 1);

		// Data icon
		textureManager.renderTexture(gl, dataDomainIcon, new Vec3f(0f, 0.02f, 0.01f),
				new Vec3f(0.2f * DATA_DOMAIN_SCALING_FACTOR,
						0.02f * DATA_DOMAIN_SCALING_FACTOR, 0.01f), new Vec3f(
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
		renderSingleCurve(gl, points, 30);
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

		// if (viewName.contains("browser"))
		// viewName = viewName.replace("browser", "");

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

		gl.glColor4f(1f, 1f, 1f, 1f);
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
		// isPathwayContentAvailable = true;
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

	private void addView(String dataDomainType, String viewType) {

		if ((!dataDomainType.equals(focusDataDomainType))) {
			// || (historyPath
			// }
			// .getLastNode() != null &&
			// !historyPath.getLastNode().toString().equals(
			// focusDataDomainType)))) {
			metaViewAnimation = 0;
			historyPath.addNode(new Node(dataDomainType, viewType));
		}

		focusDataDomainType = dataDomainType;

		// Check if view already exists
		for (AGLView glView : containedGLViews) {
			if (glView.getViewType().equals(viewType)
					&& ((IDataDomainBasedView<IDataDomain>) glView).getDataDomain()
							.getDataDomainType().equals(dataDomainType)) {

				if (!isViewOpen(glView))
					openView(glView);

				return;
			}
		}

		ASerializedView serView = null;
		if (viewType.equals("org.caleydo.view.parcoords")) {
			serView = new SerializedParallelCoordinatesView(dataDomainType);
		} else if (viewType.equals("org.caleydo.view.parcoords")) {
			serView = new SerializedParallelCoordinatesView(dataDomainType);
		} else if (viewType.equals("org.caleydo.view.heatmap.hierarchical")) {
			serView = new SerializedHierarchicalHeatMapView(dataDomainType);
		} else if (viewType.equals("org.caleydo.view.tissuebrowser")) {
			serView = new SerializedTissueViewBrowserView(dataDomainType);
		} else if (viewType.equals("org.caleydo.view.pathwaybrowser")) {
			serView = new SerializedPathwayViewBrowserView(dataDomainType);
		} else if (viewType.equals("org.caleydo.view.glyph")) {
			serView = new SerializedGlyphView(dataDomainType);
		} else if (viewType.equals("org.caleydo.view.browser")) {
			serView = new SerializedHTMLBrowserView(dataDomainType);
		}

		if (serView != null)
			newViews.add(serView);
	}

	private boolean isViewOpen(AGLView glView) {

		for (RemoteLevelElement element : allElements) {
			if (element.getGLView() == glView) {
				chainMove(element);
				return true;
			}
		}
		return false;
	}

	private void openView(AGLView view) {

		String dataDomainType = ((IDataDomainBasedView<IDataDomain>) view)
				.getDataDomain().getDataDomainType();

		RemoteLevelElement viewSpawnElement = viewSpawnPos.get(dataDomainType).get(
				view.getViewType());

		RemoteLevelElement destinationElement = null;

		if (focusElement.isFree()) {
			viewSpawnElement.setGLView(view);
			view.setRemoteLevelElement(focusElement);
			view.setDetailLevel(EDetailLevel.HIGH);
			destinationElement = focusElement;
		} else {

			// // Check if the focus element should be freed to the left or to
			// the
			// // right stack side
			// Set<String> interfaces =
			// DataDomainManager.getInstance().getAssociationManager()
			// .getViewTypesForDataDomain(
			// ((IDataDomainBasedView<IDataDomain>) view).getDataDomain()
			// .getDataDomainType());
			// for (String tmp : interfaces) {
			// if (dataDomainType.equals(tmp))
			// }
			//			
			// if (((IDataDomainBasedView<IDataDomain>) view).getDataDomain())

			freeFocusElementByChainMove(false);
			destinationElement = focusElement;
		}

		if (destinationElement != null) {
			viewSpawnElement.setGLView(view);
			arSlerpActions.add(new SlerpAction(viewSpawnElement, destinationElement));
		}
	}

	private void closeView(int remoteElementID) {

		RemoteLevelElement sourceElement = RemoteElementManager.get().getItem(
				remoteElementID);
		AGLView view = sourceElement.getGLView();

		RemoteLevelElement destinationElement = viewSpawnPos.get(
				((IDataDomainBasedView<IDataDomain>) view).getDataDomain()
						.getDataDomainType()).get(view.getViewType());

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

			IViewFrustum frustum = focusElement.getGLView().getViewFrustum();
			frustum.setLeft(0);
			frustum.setRight(8);// / fAspectRatio);
			frustum.setTop(8);
			frustum.setBottom(0);
		} else {
			focusElement.setTransform(focusTransform);

			IViewFrustum frustum = focusElement.getGLView().getViewFrustum();
			frustum.setLeft(-4);
			frustum.setRight(4);
			frustum.setTop(4);
			frustum.setBottom(-4);
		}

		focusView.setDisplayListDirty();
	}
}
