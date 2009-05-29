package org.caleydo.core.view.opengl.canvas.radial;

import gleem.linalg.Vec2f;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.xml.bind.JAXBException;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.graph.tree.TreePorter;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.manager.event.view.radial.ChangeColorModeEvent;
import org.caleydo.core.manager.event.view.radial.GoBackInHistoryEvent;
import org.caleydo.core.manager.event.view.radial.GoForthInHistoryEvent;
import org.caleydo.core.manager.event.view.radial.SetMaxDisplayedHierarchyDepthEvent;
import org.caleydo.core.manager.event.view.storagebased.RedrawViewEvent;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.RedrawViewListener;
import org.caleydo.core.view.opengl.canvas.radial.event.ClusterNodeSelectionEvent;
import org.caleydo.core.view.opengl.canvas.radial.event.ClusterNodeSelectionListener;
import org.caleydo.core.view.opengl.canvas.radial.event.IClusterNodeEventReceiver;
import org.caleydo.core.view.opengl.canvas.radial.listener.ChangeColorModeListener;
import org.caleydo.core.view.opengl.canvas.radial.listener.GoBackInHistoryListener;
import org.caleydo.core.view.opengl.canvas.radial.listener.GoForthInHistoryListener;
import org.caleydo.core.view.opengl.canvas.radial.listener.SetMaxDisplayedHierarchyDepthListener;
import org.caleydo.core.view.opengl.canvas.remote.IGLCanvasRemoteRendering;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.core.view.serialize.ASerializedView;
import org.caleydo.core.view.serialize.SerializedDummyView;

/**
 * Rendering the GLHeatMap
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class GLRadialHierarchy
	extends AGLEventListener
	implements IClusterNodeEventReceiver, IViewCommandHandler {

	public static final int DISP_HIER_DEPTH_DEFAULT = 7;

	private int iMaxDisplayedHierarchyDepth;
	private int iUpwardNavigationSliderID;
	private int iUpwardNavigationSliderButtonID;

	private boolean bIsAnimationActive;
	private boolean bIsNewSelection;

	private Tree<PartialDisc> partialDiscTree;
	private HashMap<Integer, PartialDisc> hashPartialDiscs;

	private PartialDisc pdRealRootElement;
	private PartialDisc pdCurrentRootElement;
	private PartialDisc pdCurrentSelectedElement;
	private PartialDisc pdCurrentMouseOverElement;

	private DrawingController drawingController;
	private NavigationHistory navigationHistory;
	private OneWaySlider upwardNavigationSlider;

	private ClusterNodeSelectionListener clusterNodeMouseOverListener;
	private RedrawViewListener redrawViewListener;
	private GoBackInHistoryListener goBackInHistoryListener;
	private GoForthInHistoryListener goForthInHistoryListener;
	private ChangeColorModeListener changeColorModeListener;
	private SetMaxDisplayedHierarchyDepthListener setMaxDisplayedHierarchyDepthListener;

	boolean bIsInListMode = false;

	boolean bUseDetailLevel = true;
	ISet set;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param sLabel
	 * @param viewFrustum
	 */
	public GLRadialHierarchy(GLCaleydoCanvas glCanvas, final String sLabel, final IViewFrustum viewFrustum) {
		super(glCanvas, sLabel, viewFrustum, true);

		viewType = EManagedObjectType.GL_RADIAL_HIERARCHY;

		ArrayList<ESelectionType> alSelectionTypes = new ArrayList<ESelectionType>();
		alSelectionTypes.add(ESelectionType.NORMAL);
		alSelectionTypes.add(ESelectionType.MOUSE_OVER);
		alSelectionTypes.add(ESelectionType.SELECTION);

		DrawingStrategyManager.init(pickingManager, iUniqueID);
		hashPartialDiscs = new HashMap<Integer, PartialDisc>();
		partialDiscTree = new Tree<PartialDisc>();
		iMaxDisplayedHierarchyDepth = DISP_HIER_DEPTH_DEFAULT;
		navigationHistory = new NavigationHistory(this, null);
		drawingController = new DrawingController(this, navigationHistory);
		navigationHistory.setDrawingController(drawingController);
		iUpwardNavigationSliderButtonID = 0;
		iUpwardNavigationSliderID = 0;

		glKeyListener = new GLRadialHierarchyKeyListener(this);

		// TODO: Where to call register and unregister really?
		// registerEventListeners();

		bIsAnimationActive = false;
		bIsNewSelection = false;
	}

	@Override
	public void init(GL gl) {
		initTestHierarchy();

		Rectangle controlBox = new Rectangle(0, 0, 0.3f, 1.2f);
		upwardNavigationSlider =
			new OneWaySlider(new Vec2f(controlBox.getMinX() + 0.1f, controlBox.getMinY() + 0.1f), 0.2f, 1,
				pdRealRootElement.getHierarchyLevel(), 1, 0, pdRealRootElement.getHierarchyDepth() - 1);

		LabelManager.get().setControlBox(controlBox);

		gl.glEnable(GL.GL_LINE_SMOOTH);
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		if (set == null)
			return;
	}

	@Override
	public void initLocal(GL gl) {

		iGLDisplayListIndexLocal = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		// Register keyboard listener to GL canvas
		parentGLCanvas.getParentComposite().getDisplay().asyncExec(new Runnable() {
			public void run() {
				parentGLCanvas.getParentComposite().addKeyListener(glKeyListener);
			}
		});

		init(gl);
	}

	@Override
	public void initRemote(final GL gl, final AGLEventListener glParentView,
		final GLMouseListener glMouseListener, final IGLCanvasRemoteRendering remoteRenderingGLCanvas,
		GLInfoAreaManager infoAreaManager) {

		// Register keyboard listener to GL canvas
		glParentView.getParentGLCanvas().getParentComposite().getDisplay().asyncExec(new Runnable() {
			public void run() {
				glParentView.getParentGLCanvas().getParentComposite().addKeyListener(glKeyListener);
			}
		});

		this.remoteRenderingGLView = remoteRenderingGLCanvas;

		this.glMouseListener = glMouseListener;

		iGLDisplayListIndexRemote = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexRemote;
		init(gl);

	}

	private int buildTree(Tree<ClusterNode> tree, ClusterNode clusterNode, PartialDisc partialDisc,
		int iChildID) {

		ArrayList<ClusterNode> alChildNodes = tree.getChildren(clusterNode);
		ArrayList<PartialDisc> alChildDiscs = new ArrayList<PartialDisc>();

		if (alChildNodes != null) {
			for (ClusterNode cnChild : alChildNodes) {
				iChildID++;
				PartialDisc pdCurrentChildDisc =
					new PartialDisc(cnChild.getClusterNr(), cnChild.getNrElements(), partialDiscTree, cnChild);
				try {
					alChildDiscs.add(pdCurrentChildDisc);
					partialDiscTree.addChild(partialDisc, pdCurrentChildDisc);
					hashPartialDiscs.put(cnChild.getClusterNr(), pdCurrentChildDisc);
					iChildID += buildTree(tree, cnChild, pdCurrentChildDisc, iChildID);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return iChildID;
	}

	private void initTestHierarchy() {

		iMaxDisplayedHierarchyDepth = DISP_HIER_DEPTH_DEFAULT;
		int childID = 0;
		Tree<ClusterNode> tree = new Tree<ClusterNode>();
		TreePorter treePorter = new TreePorter();

		try {
			tree = treePorter.importTree("data/clustering/tree.xml");
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ClusterNode cnRoot = tree.getRoot();
		// setDepthsToZero(tree, cnRoot);
		//
		// ClusterHelper.determineHierarchyDepth(tree);

		PartialDisc pdRoot =
			new PartialDisc(cnRoot.getClusterNr(), cnRoot.getNrElements(), partialDiscTree, cnRoot);
		partialDiscTree.setRootNode(pdRoot);
		hashPartialDiscs.put(cnRoot.getClusterNr(), pdRoot);
		buildTree(tree, cnRoot, pdRoot, childID);
		pdRoot.calculateHierarchyLevels(0);

		// pdRoot.calculateSizes();

		pdCurrentMouseOverElement = pdRoot;
		pdCurrentRootElement = pdRoot;
		pdCurrentSelectedElement = pdRoot;
		pdRealRootElement = pdRoot;

		navigationHistory.addNewHistoryEntry(drawingController.getCurrentDrawingState(),
			pdCurrentRootElement, pdCurrentSelectedElement, iMaxDisplayedHierarchyDepth);
	}

	@Override
	public void setDetailLevel(EDetailLevel detailLevel) {
		if (bUseDetailLevel) {
			super.setDetailLevel(detailLevel);
			// renderStyle.setDetailLevel(detailLevel);
		}

	}

	@Override
	public void displayLocal(GL gl) {
		pickingManager.handlePicking(this, gl);

		if (bIsDisplayListDirtyLocal && !bIsAnimationActive) {
			buildDisplayList(gl, iGLDisplayListIndexLocal);
			bIsDisplayListDirtyLocal = false;
		}
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		display(gl);
		checkForHits(gl);

		if (eBusyModeState != EBusyModeState.OFF) {
			renderBusyMode(gl);
		}
	}

	@Override
	public void displayRemote(GL gl) {
		if (bIsDisplayListDirtyRemote && !bIsAnimationActive) {
			buildDisplayList(gl, iGLDisplayListIndexRemote);
			bIsDisplayListDirtyRemote = false;
		}
		iGLDisplayListToCall = iGLDisplayListIndexRemote;

		display(gl);
		checkForHits(gl);

		// glMouseListener.resetEvents();
	}

	@Override
	public void display(GL gl) {
		processEvents();
		render(gl);

		if (upwardNavigationSlider.handleDragging(gl, glMouseListener)) {
			PartialDisc pdNewRootElement =
				pdCurrentRootElement.getParentWithLevel(upwardNavigationSlider.getSelectedValue());
			if (pdNewRootElement != null) {
				pdCurrentRootElement = pdNewRootElement;
				pdCurrentMouseOverElement = pdNewRootElement;
				
				bIsNewSelection = true;

				PartialDisc pdSelectedElement =
					drawingController.getCurrentDrawingState().getSelectedElement();
				if (pdSelectedElement != null) {
					pdCurrentSelectedElement = pdSelectedElement;
				}
				navigationHistory.addNewHistoryEntry(drawingController.getCurrentDrawingState(),
					pdCurrentRootElement, pdCurrentSelectedElement, iMaxDisplayedHierarchyDepth);
				setDisplayListDirty();
				
				ClusterNodeSelectionEvent event = new ClusterNodeSelectionEvent();
				event.setSender(this);
				event.setClusterNumber(pdCurrentSelectedElement.getElementID());
				event.setSelectionType(ESelectionType.SELECTION);

				eventPublisher.triggerEvent(event);
			}
		}
		// clipToFrustum(gl);
		//
		if (bIsAnimationActive) {
			float fXCenter = viewFrustum.getWidth() / 2;
			float fYCenter = viewFrustum.getHeight() / 2;

			gl.glLoadIdentity();
			upwardNavigationSlider.draw(gl, pickingManager, textureManager, iUniqueID,
				iUpwardNavigationSliderID, iUpwardNavigationSliderButtonID);
			drawingController.draw(fXCenter, fYCenter, gl, new GLU());
		}
		else
			gl.glCallList(iGLDisplayListToCall);

		// buildDisplayList(gl, iGLDisplayListIndexRemote);

	}

	private void buildDisplayList(final GL gl, int iGLDisplayListIndex) {

		gl.glNewList(iGLDisplayListIndex, GL.GL_COMPILE);

		float fXCenter = viewFrustum.getWidth() / 2;
		float fYCenter = viewFrustum.getHeight() / 2;

		gl.glLoadIdentity();

		upwardNavigationSlider.draw(gl, pickingManager, textureManager, iUniqueID, iUpwardNavigationSliderID,
			iUpwardNavigationSliderButtonID);

		drawingController.draw(fXCenter, fYCenter, gl, new GLU());

		// TextRenderer renderer = new TextRenderer(new Font("Courier New", Font.PLAIN, 78), false);
		//		
		// renderer.setColor(0, 0, 0, 1);
		// renderer.begin3DRendering();
		// renderer.draw3D("Hello World!", 0, 0, 0, 0.003f);
		// renderer.end3DRendering();
		// renderer.flush();
		// Rectangle2D rect = renderer.getBounds("Hello World!");
		//		
		// gl.glColor4f(0,0,0,1);
		// gl.glBegin(GL.GL_POLYGON);
		//		
		//		
		// gl.glVertex3f((float)rect.getWidth() * 0.003f, 0, 0);
		// gl.glVertex3f((float)rect.getWidth() * 0.003f, -1, 0);
		// gl.glVertex3f(0, -1, 0);
		// gl.glVertex3f(0, 0, 0);
		// gl.glEnd();

		gl.glEndList();
	}

	private void render(GL gl) {

		// gl.glLoadIdentity();
		// gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT);
		//
		// gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.RAD_HIERARCHY_PDISC_SELECTION,
		// -2));
		// gl.glColor3f(1, 0, 0);
		// gl.glBegin(GL.GL_POLYGON);
		// gl.glVertex3f(0, 1, 0);
		// gl.glVertex3f(1, 1, 0);
		// gl.glVertex3f(1, 0, 0);
		// gl.glVertex3f(0, 0, 0);
		// gl.glEnd();
		// gl.glPopName();
		//
		// gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.RAD_HIERARCHY_PDISC_SELECTION,
		// -3));
		// gl.glColor3f(0, 1, 0);
		// gl.glBegin(GL.GL_POLYGON);
		// gl.glVertex3f(1, 1, 0);
		// gl.glVertex3f(2, 1, 0);
		// gl.glVertex3f(2, 0, 0);
		// gl.glVertex3f(1, 0, 0);
		// gl.glEnd();
		// gl.glPopName();
		//
		// gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.RAD_HIERARCHY_PDISC_SELECTION,
		// -4));
		// gl.glColor3f(0, 0, 1);
		// gl.glBegin(GL.GL_POLYGON);
		// gl.glVertex3f(0, 2, 0);
		// gl.glVertex3f(1, 2, 0);
		// gl.glVertex3f(1, 1, 0);
		// gl.glVertex3f(0, 1, 0);
		// gl.glEnd();
		// gl.glPopName();
		//
		// gl.glPopAttrib();

		// TextRenderer textRenderer = new TextRenderer(new Font("Arial", Font.PLAIN, 24), false);
		//
		// textRenderer.begin3DRendering();
		// textRenderer.draw3D(navigationHistory.getPos() + ", " + navigationHistory.getSize(), 0, 0, 0,
		// 0.004f);
		// textRenderer.end3DRendering();
		// textRenderer.flush();

		// // gl.glDisable(GL.GL_DEPTH_TEST);
		// // gl.glEnable(GL.GL_BLEND);
		// // gl.glBlendFunc(GL.GL_SRC_ALPHA_SATURATE, GL.GL_ONE);
		//
		// zTransform += 1.0;
		// if (zTransform == 360)
		// zTransform = 0;
		// // gl.glOrtho(0.0, 1.0, 0.0, 1.0, -1.0, 1.0);
		// float[] array =
		// new float[] { 1.0f, 0.0f, -5.0f, 1.0f, 1.0f, -5.0f, 0.0f, 1.0f, -5.0f, 0.0f, 0.0f, -5.0f };
		// float[] array2 =
		// new float[] { -1.0f, 0.0f, -2.0f, -1.0f, -1.0f, -2.0f, 0.0f, -1.0f, -2.0f, 0.0f, 0.0f, -2.0f };
		// byte indices[] = { 0, 2, 3, 1 };
		// ByteBuffer indexBuffer = BufferUtil.newByteBuffer(indices.length);
		// indexBuffer.put(indices);
		// FloatBuffer verticesBuffer = BufferUtil.newFloatBuffer(array.length + array2.length);
		// // for(int i = 0; i < array.length; i++)
		// verticesBuffer.put(array);
		// verticesBuffer.put(array2);
		//
		// verticesBuffer.rewind();
		// indexBuffer.rewind();
		//
		// gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
		// gl.glVertexPointer(3, GL.GL_FLOAT, 0, verticesBuffer);
		// // GLHelperFunctions.drawAxis(gl);
		//
		// int mode = 0;
		// gl.glMatrixMode(GL.GL_MODELVIEW);
		//
		// // gl.glLoadIdentity();
		// gl.glTranslatef(2.0f, 2.0f, 0.0f);
		// gl.glRotatef(zTransform, 0.0f, 0.0f, 1.0f);
		// glu.gluPartialDisk(x, 1 , 2, 3, 1, 30, 60);
		// gl.glColor4f(0, 1, 0, 0.8f);
		// glu.gluPartialDisk(x, 1 , 2, 3, 1, 110, 30);

		// glu.gluPartialDisk(x, 1 , 2, 10, 1, 180, 60);
		// gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.RADIAL_HIERARCHY_SELECTION,
		// 44));
		// if(selection == 44)
		// gl.glColor4f(1, 1, 0, 1);
		// else
		// gl.glColor4f(1, 0, 0, 0.5f);
		// GLPrimitives.renderPartialDisc(gl, 1, 2, 90, 90);
		//		
		// GLPrimitives.renderPartialDisc(gl, 1, 2, 90, 90);
		// gl.glPopName();
		//		
		// gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.RADIAL_HIERARCHY_SELECTION,
		// 11));
		// if(selection == 11)
		// gl.glColor4f(1, 1, 0, 1);
		// else
		// gl.glColor4f(1, 0, 0, 1);
		// GLPrimitives.renderPartialDisc(gl, 1, 2, 180, 90);
		// GLPrimitives.renderCircle(gl, 1);
		// gl.glPopName();
		//		
		// if(mode == 0)
		// {
		// // gl.glBegin(GL.GL_POLYGON);
		// // gl.glArrayElement(0);
		// // gl.glArrayElement(1);
		// // gl.glArrayElement(2);
		// // gl.glArrayElement(3);
		// // // gl.glVertex3f(5, 4, zTransform);
		// // // gl.glVertex3f(5, 5, zTransform);
		// // // gl.glVertex3f(4, 5, zTransform);
		// // // gl.glVertex3f(4, 4, zTransform);
		// // gl.glEnd();
		// // gl.glBegin(GL.GL_POLYGON);
		// // gl.glArrayElement(4);
		// // gl.glArrayElement(5);
		// // gl.glArrayElement(6);
		// // gl.glArrayElement(7);
		// // gl.glVertex3f(5, 4, zTransform);
		// // gl.glVertex3f(5, 5, zTransform);
		// // gl.glVertex3f(4, 5, zTransform);
		// // gl.glVertex3f(4, 4, zTransform);
		// gl.glEnd();
		// }
		// else if(mode == 1)
		// {
		// gl.glDrawElements(GL.GL_POLYGON, 4, GL.GL_UNSIGNED_BYTE, indexBuffer);
		// }
	}

	@Override
	public String getDetailedInfo() {
		return new String("");
	}

	@Override
	protected void handleEvents(EPickingType ePickingType, EPickingMode pickingMode, int iExternalID,
		Pick pick) {
		if (detailLevel == EDetailLevel.VERY_LOW) {
			return;
		}
		switch (ePickingType) {

			case RAD_HIERARCHY_PDISC_SELECTION:

				PartialDisc pdPickedElement = hashPartialDiscs.get(iExternalID);

				switch (pickingMode) {
					case CLICKED:
						if (pdPickedElement != null)
							drawingController.handleSelection(pdPickedElement);
						break;

					case MOUSE_OVER:
						if (pdPickedElement != null)
							drawingController.handleFocus(pdPickedElement);
						break;

					case RIGHT_CLICKED:
						if (pdPickedElement != null)
							drawingController.handleAlternativeSelection(pdPickedElement);
						break;

					default:
						return;
				}
				break;

			case RAD_HIERARCHY_SLIDER_SELECTION:
				switch (pickingMode) {
					case CLICKED:
						if (iExternalID == iUpwardNavigationSliderID)
							upwardNavigationSlider.setDragging(true);
						break;

					default:
						return;
				}
				break;

			case RAD_HIERARCHY_SLIDER_BUTTON_SELECTION:
				switch (pickingMode) {
					case CLICKED:
						if (iExternalID == iUpwardNavigationSliderButtonID)
							upwardNavigationSlider.handleButtonClick();
						PartialDisc pdNewRootElement =
							pdCurrentRootElement
								.getParentWithLevel(upwardNavigationSlider.getSelectedValue());
						if (pdNewRootElement != null) {
							pdCurrentRootElement = pdNewRootElement;
							pdCurrentMouseOverElement = pdNewRootElement;

							bIsNewSelection = true;
							
							PartialDisc pdSelectedElement =
								drawingController.getCurrentDrawingState().getSelectedElement();
							if (pdSelectedElement != null) {
								pdCurrentSelectedElement = pdSelectedElement;
							}
							navigationHistory.addNewHistoryEntry(drawingController.getCurrentDrawingState(),
								pdCurrentRootElement, pdCurrentSelectedElement, iMaxDisplayedHierarchyDepth);
							setDisplayListDirty();
						}
						break;

					default:
						return;
				}
				break;
		}
	}

	public void goBackInHistory() {
		navigationHistory.goBack();
	}

	public void goForthInHistory() {
		// handleMouseOver(10);
		navigationHistory.goForth();
	}

	public void changeColorMode() {
		
//		ClusterNodeSelectionEvent event = new ClusterNodeSelectionEvent();
//		event.setClusterNumber(1073741840);
//		event.setSelectionType(ESelectionType.SELECTION);
//		
//		eventPublisher.triggerEvent(event);

		DrawingStrategyManager drawingStrategyManager = DrawingStrategyManager.get();
		if (drawingStrategyManager.getDefaultStrategyType() == DrawingStrategyManager.PD_DRAWING_STRATEGY_RAINBOW) {
			drawingStrategyManager
				.setDefaultStrategy(DrawingStrategyManager.PD_DRAWING_STRATEGY_EXPRESSION_COLOR);
		}
		else {
			drawingStrategyManager.setDefaultStrategy(DrawingStrategyManager.PD_DRAWING_STRATEGY_RAINBOW);
		}
		setDisplayListDirty();
	}

	public PartialDisc getRealRootElement() {
		return pdRealRootElement;
	}

	public void setRealRootElement(PartialDisc pdRealRootElement) {
		this.pdRealRootElement = pdRealRootElement;
	}

	public PartialDisc getCurrentRootElement() {
		return pdCurrentRootElement;
	}

	public void setCurrentRootElement(PartialDisc pdCurrentRootElement) {
		this.pdCurrentRootElement = pdCurrentRootElement;
		upwardNavigationSlider.setSelectedValue(pdCurrentRootElement.getHierarchyLevel());
	}

	public PartialDisc getCurrentSelectedElement() {
		return pdCurrentSelectedElement;
	}

	public void setCurrentSelectedElement(PartialDisc pdCurrentSelectedElement) {
		this.pdCurrentSelectedElement = pdCurrentSelectedElement;
	}

	public PartialDisc getCurrentMouseOverElement() {
		return pdCurrentMouseOverElement;
	}

	public void setCurrentMouseOverElement(PartialDisc pdCurrentMouseOverElement) {
		this.pdCurrentMouseOverElement = pdCurrentMouseOverElement;
	}

	public void setNewSelection(boolean bIsNewSelection) {
		this.bIsNewSelection = bIsNewSelection;
	}

	public boolean isNewSelection() {
		return bIsNewSelection;
	}

	public int getMaxDisplayedHierarchyDepth() {
		return iMaxDisplayedHierarchyDepth;
	}

	public void setMaxDisplayedHierarchyDepth(int iMaxDisplayedHierarchyDepth) {
		if (this.iMaxDisplayedHierarchyDepth != iMaxDisplayedHierarchyDepth) {
			
			bIsNewSelection = false;
			this.iMaxDisplayedHierarchyDepth = iMaxDisplayedHierarchyDepth;
			navigationHistory.setCurrentMaxDisplayedHierarchyDepth(iMaxDisplayedHierarchyDepth);
			setDisplayListDirty();
		}
	}

	public boolean isInListMode() {
		return bIsInListMode;
	}

	public void setAnimationActive(boolean bIsAnimationActive) {
		this.bIsAnimationActive = bIsAnimationActive;
	}

	@Override
	public void broadcastElements(EVAOperation type) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getNumberOfSelections(ESelectionType selectionType) {
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
		// TODO Auto-generated method stub

	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedDummyView serializedForm = new SerializedDummyView();
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public void handleClusterNodeSelection(int iClusterNumber, ESelectionType selectionType) {
		PartialDisc pdSelected = hashPartialDiscs.get(iClusterNumber);
		if (pdSelected != null) {
			
			switch(selectionType) {
				
				case MOUSE_OVER: 
					pdCurrentMouseOverElement = pdSelected;
					setDisplayListDirty();
					break;
					
				case SELECTION:
					pdCurrentMouseOverElement = pdSelected;
					bIsNewSelection = true;
					setDisplayListDirty();
					break;
			}
			
			
		}
	}

	// public void setDisplayListDirty() {
	// int x = 0;
	// }

	@Override
	public void registerEventListeners() {
		redrawViewListener = new RedrawViewListener();
		redrawViewListener.setHandler(this);
		eventPublisher.addListener(RedrawViewEvent.class, redrawViewListener);

		clusterNodeMouseOverListener = new ClusterNodeSelectionListener();
		clusterNodeMouseOverListener.setHandler(this);
		eventPublisher.addListener(ClusterNodeSelectionEvent.class, clusterNodeMouseOverListener);

		goBackInHistoryListener = new GoBackInHistoryListener();
		goBackInHistoryListener.setHandler(this);
		eventPublisher.addListener(GoBackInHistoryEvent.class, goBackInHistoryListener);

		goForthInHistoryListener = new GoForthInHistoryListener();
		goForthInHistoryListener.setHandler(this);
		eventPublisher.addListener(GoForthInHistoryEvent.class, goForthInHistoryListener);

		changeColorModeListener = new ChangeColorModeListener();
		changeColorModeListener.setHandler(this);
		eventPublisher.addListener(ChangeColorModeEvent.class, changeColorModeListener);

		setMaxDisplayedHierarchyDepthListener = new SetMaxDisplayedHierarchyDepthListener();
		setMaxDisplayedHierarchyDepthListener.setHandler(this);
		eventPublisher.addListener(SetMaxDisplayedHierarchyDepthEvent.class,
			setMaxDisplayedHierarchyDepthListener);

		// clearSelectionsListener = new ClearSelectionsListener();
		// clearSelectionsListener.setHandler(this);
		// eventPublisher.addListener(ClearSelectionsEvent.class, clearSelectionsListener);
	}

	@Override
	public void unregisterEventListeners() {
		if (redrawViewListener != null) {
			eventPublisher.removeListener(redrawViewListener);
			redrawViewListener = null;
		}
		if (clusterNodeMouseOverListener != null) {
			eventPublisher.removeListener(clusterNodeMouseOverListener);
			clusterNodeMouseOverListener = null;
		}
		if (goBackInHistoryListener != null) {
			eventPublisher.removeListener(goBackInHistoryListener);
			goBackInHistoryListener = null;
		}
		if (goForthInHistoryListener != null) {
			eventPublisher.removeListener(goForthInHistoryListener);
			goForthInHistoryListener = null;
		}
		if (changeColorModeListener != null) {
			eventPublisher.removeListener(changeColorModeListener);
			changeColorModeListener = null;
		}
		if (setMaxDisplayedHierarchyDepthListener != null) {
			eventPublisher.removeListener(setMaxDisplayedHierarchyDepthListener);
			setMaxDisplayedHierarchyDepthListener = null;
		}
		// if (clearSelectionsListener != null) {
		// eventPublisher.removeListener(clearSelectionsListener);
		// clearSelectionsListener = null;
		// }
	}

	@Override
	public void handleClearSelections() {
		// TODO: Later on when using Selection Manager

	}

	@Override
	public void handleRedrawView() {
		setDisplayListDirty();
	}

	@Override
	public void handleUpdateView() {
		setDisplayListDirty();
	}

	public void handleKeyboardAlternativeDiscSelection() {
		if (pdCurrentMouseOverElement != null) {
			drawingController.handleAlternativeSelection(pdCurrentMouseOverElement);
		}
	}

}
