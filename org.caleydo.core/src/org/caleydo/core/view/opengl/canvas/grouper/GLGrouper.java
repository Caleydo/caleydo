package org.caleydo.core.view.opengl.canvas.grouper;

import gleem.linalg.Vec3f;

import java.awt.Font;
import java.util.GregorianCalendar;
import java.util.HashMap;

import javax.media.opengl.GL;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.manager.event.view.ClearSelectionsEvent;
import org.caleydo.core.manager.event.view.storagebased.RedrawViewEvent;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.grouper.compositegraphic.GroupRepresentation;
import org.caleydo.core.view.opengl.canvas.grouper.compositegraphic.VAElementRepresentation;
import org.caleydo.core.view.opengl.canvas.grouper.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.canvas.grouper.drawingstrategies.DrawingStrategyManager;
import org.caleydo.core.view.opengl.canvas.grouper.drawingstrategies.group.EGroupDrawingStrategyType;
import org.caleydo.core.view.opengl.canvas.grouper.drawingstrategies.group.IGroupDrawingStrategy;
import org.caleydo.core.view.opengl.canvas.grouper.drawingstrategies.vaelement.EVAElementDrawingStrategyType;
import org.caleydo.core.view.opengl.canvas.grouper.drawingstrategies.vaelement.IVAElementDrawingStrategy;
import org.caleydo.core.view.opengl.canvas.listener.ClearSelectionsListener;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.RedrawViewListener;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;

import com.sun.opengl.util.j2d.TextRenderer;

/**
 * The group assignment interface
 * 
 * @author Christian Partl
 * @author Alexander Lex
 */
public class GLGrouper
	extends AGLEventListener
	implements IViewCommandHandler {

	boolean bUseDetailLevel = true;

	private boolean bControlPressed = false;
	private double dCollapseButtonDragOverTime;
	private int iDraggedOverCollapseButtonID;

	private boolean bHierarchyChanged;

	private GrouperRenderStyle renderStyle;
	private HashMap<Integer, GroupRepresentation> hashGroups;
	private HashMap<Integer, VAElementRepresentation> hashElements;

	private GroupRepresentation rootGroup;

	private DrawingStrategyManager drawingStrategyManager = null;
	private DragAndDropController dragAndDropController = null;
	protected RedrawViewListener redrawViewListener = null;
	protected ClearSelectionsListener clearSelectionsListener = null;

	private TextRenderer textRenderer;
	private SelectionManager selectionManager;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param sLabel
	 * @param viewFrustum
	 */
	public GLGrouper(GLCaleydoCanvas glCanvas, final String sLabel, final IViewFrustum viewFrustum) {
		super(glCanvas, sLabel, viewFrustum, true);

		hashElements = new HashMap<Integer, VAElementRepresentation>();
		hashGroups = new HashMap<Integer, GroupRepresentation>();
		viewType = EManagedObjectType.GL_HISTOGRAM;
		dragAndDropController = new DragAndDropController(this);
		// TODO:if this should be general, use dynamic idType
		selectionManager = new SelectionManager.Builder(EIDType.EXPERIMENT_INDEX).build();

		renderStyle = new GrouperRenderStyle(this, viewFrustum);
		textRenderer = new TextRenderer(new Font("Arial", Font.PLAIN, 32), true, true);

		glKeyListener = new GLGrouperKeyListener(this);

		iDraggedOverCollapseButtonID = -1;
		bHierarchyChanged = true;
		// registerEventListeners();
	}

	@Override
	public void init(GL gl) {
		drawingStrategyManager = new DrawingStrategyManager(pickingManager, iUniqueID, renderStyle);
		initTestHierarchy();
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

	public void initTestHierarchy() {

		IGroupDrawingStrategy groupDrawingStrategy =
			drawingStrategyManager.getGroupDrawingStrategy(EGroupDrawingStrategyType.NORMAL);
		IVAElementDrawingStrategy elementDrawingStrategy =
			drawingStrategyManager.getVAElementDrawingStrategy(EVAElementDrawingStrategyType.NORMAL);

		rootGroup =
			new GroupRepresentation(new ClusterNode("root", 0, 0, 0, true), renderStyle,
				groupDrawingStrategy, drawingStrategyManager, this);

		GroupRepresentation group1 =
			new GroupRepresentation(new ClusterNode("group1", 4, 0, 0, false), renderStyle,
				groupDrawingStrategy, drawingStrategyManager, this);
		GroupRepresentation group2 =
			new GroupRepresentation(new ClusterNode("group2", 5, 0, 0, false), renderStyle,
				groupDrawingStrategy, drawingStrategyManager, this);
		GroupRepresentation group3 =
			new GroupRepresentation(new ClusterNode("group3", 6, 0, 0, false), renderStyle,
				groupDrawingStrategy, drawingStrategyManager, this);

		VAElementRepresentation element1 =
			new VAElementRepresentation(new ClusterNode("one", 1, 0, 0, false), elementDrawingStrategy,
				drawingStrategyManager, this);
		VAElementRepresentation element2 =
			new VAElementRepresentation(new ClusterNode("two", 2, 0, 0, false), elementDrawingStrategy,
				drawingStrategyManager, this);
		VAElementRepresentation element3 =
			new VAElementRepresentation(new ClusterNode("three", 3, 0, 0, false), elementDrawingStrategy,
				drawingStrategyManager, this);
		VAElementRepresentation element4 =
			new VAElementRepresentation(new ClusterNode("four", 7, 0, 0, false), elementDrawingStrategy,
				drawingStrategyManager, this);
		VAElementRepresentation element5 =
			new VAElementRepresentation(new ClusterNode("five", 8, 0, 0, false), elementDrawingStrategy,
				drawingStrategyManager, this);

		rootGroup.add(group1);
		rootGroup.add(element1);
		rootGroup.add(group3);
		group1.add(element2);
		group1.add(group2);
		group2.add(element3);
		group2.add(element4);
		group3.add(element5);

		hashGroups.put(rootGroup.getID(), rootGroup);
		hashGroups.put(group1.getID(), group1);
		hashGroups.put(group2.getID(), group2);
		hashGroups.put(group3.getID(), group3);
		hashElements.put(element1.getID(), element1);
		hashElements.put(element2.getID(), element2);
		hashElements.put(element3.getID(), element3);
		hashElements.put(element4.getID(), element4);
		hashElements.put(element5.getID(), element5);

		selectionManager.initialAdd(rootGroup.getID());
		selectionManager.initialAdd(group1.getID());
		selectionManager.initialAdd(group2.getID());
		selectionManager.initialAdd(group3.getID());
		selectionManager.initialAdd(element1.getID());
		selectionManager.initialAdd(element2.getID());
		selectionManager.initialAdd(element3.getID());
		selectionManager.initialAdd(element4.getID());
		selectionManager.initialAdd(element5.getID());

		rootGroup.calculateHierarchyLevels(0);
	}

	@Override
	public void initRemote(final GL gl, final AGLEventListener glParentView,
		final GLMouseListener glMouseListener, GLInfoAreaManager infoAreaManager) {

		// Register keyboard listener to GL canvas
		glParentView.getParentGLCanvas().getParentComposite().getDisplay().asyncExec(new Runnable() {
			public void run() {
				glParentView.getParentGLCanvas().getParentComposite().addKeyListener(glKeyListener);
			}
		});

		this.glMouseListener = glMouseListener;

		iGLDisplayListIndexRemote = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexRemote;
		init(gl);

	}

	@Override
	public void initData() {
		super.initData();
	}

	@Override
	public void setDetailLevel(EDetailLevel detailLevel) {
		if (bUseDetailLevel) {
			super.setDetailLevel(detailLevel);
		}

	}

	@Override
	public void displayLocal(GL gl) {
		pickingManager.handlePicking(this, gl);

		if (bIsDisplayListDirtyLocal) {
			buildDisplayList(gl, iGLDisplayListIndexLocal);
			bIsDisplayListDirtyLocal = false;
		}
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		display(gl);
		checkForHits(gl);
	}

	@Override
	public void displayRemote(GL gl) {
		if (bIsDisplayListDirtyRemote) {
			buildDisplayList(gl, iGLDisplayListIndexRemote);
			bIsDisplayListDirtyRemote = false;
		}
		iGLDisplayListToCall = iGLDisplayListIndexRemote;

		display(gl);
		checkForHits(gl);
	}

	@Override
	public void display(GL gl) {
		processEvents();
		gl.glCallList(iGLDisplayListToCall);
		
		dragAndDropController.handleDragging(gl, glMouseListener);

		if (bControlPressed) {
			gl.glColor3f(0, 0, 0);
			gl.glBegin(GL.GL_POLYGON);
			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(0, 1, 0);
			gl.glVertex3f(1, 1, 0);
			gl.glVertex3f(1, 0, 0);
			gl.glEnd();
		}
	}

	private void buildDisplayList(final GL gl, int iGLDisplayListIndex) {
		gl.glNewList(iGLDisplayListIndex, GL.GL_COMPILE);

		gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.GROUPER_BACKGROUND_SELECTION, 0));

		gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT);
		gl.glColor3f(1.0f, 1.0f, 1.0f);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(0.0f, 0.0f, -11.0f);
		gl.glVertex3f(viewFrustum.getWidth(), 0.0f, -11.0f);
		gl.glVertex3f(viewFrustum.getWidth(), viewFrustum.getHeight(), -11.0f);
		gl.glVertex3f(0.0f, viewFrustum.getHeight(), -11.0f);
		gl.glEnd();
		gl.glPopAttrib();

		gl.glPopName();

		Vec3f vecPosition = new Vec3f(viewFrustum.getWidth() / 2.0f, viewFrustum.getHeight(), -10.0f);
		rootGroup.setPosition(vecPosition);
		rootGroup.setHierarchyPosition(vecPosition);
		if (bHierarchyChanged) {
			rootGroup.calculateHierarchyLevels(0);
			rootGroup.calculateDrawingParameters(gl, textRenderer);
			bHierarchyChanged = false;
		}
		rootGroup.draw(gl, textRenderer);

		gl.glEndList();
	}

	@Override
	public String getDetailedInfo() {
		return new String("");
	}

	@Override
	protected void handlePickingEvents(EPickingType ePickingType, EPickingMode pickingMode, int iExternalID,
		Pick pick) {
		if (detailLevel == EDetailLevel.VERY_LOW) {
			return;
		}
		switch (ePickingType) {

			case GROUPER_GROUP_SELECTION:
				GroupRepresentation groupRep = hashGroups.get(iExternalID);
				switch (pickingMode) {
					case CLICKED:
						iDraggedOverCollapseButtonID = -1;
						if (groupRep != null) {
							if (!bControlPressed
								&& !selectionManager.checkStatus(ESelectionType.SELECTION, groupRep.getID())) {
								dragAndDropController.clearDraggables();
								selectionManager.clearSelection(ESelectionType.SELECTION);
							}
							groupRep.addAsDraggable(dragAndDropController);
							dragAndDropController.startDragging();

							groupRep.setSelectionType(ESelectionType.SELECTION, selectionManager);
							rootGroup.updateDrawingStrategies(selectionManager, drawingStrategyManager);
							setDisplayListDirty();
						}
						break;
					case DRAGGED:
						iDraggedOverCollapseButtonID = -1;
						if (groupRep != null) {
							if (dragAndDropController.isDragging()) {
								dragAndDropController.setDropArea(groupRep);
							}
						}
						break;
					case MOUSE_OVER:
						iDraggedOverCollapseButtonID = -1;
						if (groupRep != null) {
							if (selectionManager.checkStatus(ESelectionType.MOUSE_OVER, groupRep.getID())
								|| selectionManager.checkStatus(ESelectionType.SELECTION, groupRep.getID())) {
								return;
							}
							selectionManager.clearSelection(ESelectionType.MOUSE_OVER);
							selectionManager.addToType(ESelectionType.MOUSE_OVER, groupRep.getID());
							rootGroup.updateDrawingStrategies(selectionManager, drawingStrategyManager);
							setDisplayListDirty();
						}
						break;
					default:
						return;
				}
				break;

			case GROUPER_VA_ELEMENT_SELECTION:
				VAElementRepresentation elementRep = hashElements.get(iExternalID);
				switch (pickingMode) {
					case CLICKED:
						iDraggedOverCollapseButtonID = -1;
						if (elementRep != null) {

							if (!bControlPressed
								&& !selectionManager
									.checkStatus(ESelectionType.SELECTION, elementRep.getID())) {
								dragAndDropController.clearDraggables();
								selectionManager.clearSelection(ESelectionType.SELECTION);
							}
							dragAndDropController.addDraggable(elementRep);
							dragAndDropController.startDragging();

							selectionManager.addToType(ESelectionType.SELECTION, elementRep.getID());
							rootGroup.updateDrawingStrategies(selectionManager, drawingStrategyManager);
							setDisplayListDirty();
						}
						break;
					case MOUSE_OVER:
						iDraggedOverCollapseButtonID = -1;
						if (elementRep != null) {
							if (selectionManager.checkStatus(ESelectionType.MOUSE_OVER, elementRep.getID())
								|| selectionManager.checkStatus(ESelectionType.SELECTION, elementRep.getID())) {
								return;
							}
							selectionManager.clearSelection(ESelectionType.MOUSE_OVER);
							selectionManager.addToType(ESelectionType.MOUSE_OVER, elementRep.getID());
							rootGroup.updateDrawingStrategies(selectionManager, drawingStrategyManager);
							setDisplayListDirty();
						}
						break;
					default:
						return;
				}
				break;

			case GROUPER_BACKGROUND_SELECTION:
				switch (pickingMode) {
					case CLICKED:
						iDraggedOverCollapseButtonID = -1;
						dragAndDropController.clearDraggables();
						selectionManager.clearSelections();
						rootGroup.updateDrawingStrategies(selectionManager, drawingStrategyManager);
						setDisplayListDirty();
						break;
					default:
						return;
				}
				break;

			case GROUPER_COLLAPSE_BUTTON_SELECTION:
				GroupRepresentation group = hashGroups.get(iExternalID);
				switch (pickingMode) {
					case CLICKED:
						iDraggedOverCollapseButtonID = -1;
						if (group != null) {
							group.setCollapsed(!group.isCollapsed());
							setDisplayListDirty();
						}
						break;
					case DRAGGED:
						if (group != null && group.isCollapsed()) {
							double dCurrentTimeStamp = GregorianCalendar.getInstance().getTimeInMillis();

							if (dCurrentTimeStamp - dCollapseButtonDragOverTime > 500
								&& group.getID() == iDraggedOverCollapseButtonID) {
								group.setCollapsed(false);
								iDraggedOverCollapseButtonID = -1;
								setDisplayListDirty();
								return;
							}
							if (group.getID() != iDraggedOverCollapseButtonID)
								dCollapseButtonDragOverTime = dCurrentTimeStamp;
							iDraggedOverCollapseButtonID = group.getID();
						}
					default:
						return;
				}
				break;
		}
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
	public void handleRedrawView() {
		setDisplayListDirty();
	}

	@Override
	public void handleUpdateView() {
		setDisplayListDirty();
	}

	@Override
	public void handleClearSelections() {
		// nothing to do because histogram has no selections
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedGrouperView serializedForm = new SerializedGrouperView(dataDomain);
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();
		redrawViewListener = new RedrawViewListener();
		redrawViewListener.setHandler(this);
		eventPublisher.addListener(RedrawViewEvent.class, redrawViewListener);

		clearSelectionsListener = new ClearSelectionsListener();
		clearSelectionsListener.setHandler(this);
		eventPublisher.addListener(ClearSelectionsEvent.class, clearSelectionsListener);
	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();
		if (redrawViewListener != null) {
			eventPublisher.removeListener(redrawViewListener);
			redrawViewListener = null;
		}
		if (clearSelectionsListener != null) {
			eventPublisher.removeListener(clearSelectionsListener);
			clearSelectionsListener = null;
		}
	}

	public boolean isControlPressed() {
		return bControlPressed;
	}

	public void setControlPressed(boolean bControlPressed) {
		this.bControlPressed = bControlPressed;
	}

	public boolean isHierarchyChanged() {
		return bHierarchyChanged;
	}

	public void setHierarchyChanged(boolean bHierarchyChanged) {
		this.bHierarchyChanged = bHierarchyChanged;
	}
	
	public void addGroupRepresentation(int iID, GroupRepresentation groupRepresentation) {
		hashGroups.put(iID, groupRepresentation);
	}
	
	public void addVAElementRepresentation(int iID, VAElementRepresentation elementRepresentation) {
		hashElements.put(iID, elementRepresentation);
	}
	
	public void removeGroupRepresentation(int iID) {
		hashGroups.remove(iID);
	}
	
	public void removeVAElementRepresentation(int iID) {
		hashElements.remove(iID);
	}

}
