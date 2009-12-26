package org.caleydo.core.view.opengl.canvas.grouper;

import gleem.linalg.Vec3f;

import java.awt.Font;
import java.util.HashMap;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.Histogram;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.manager.event.view.ClearSelectionsEvent;
import org.caleydo.core.manager.event.view.storagebased.RedrawViewEvent;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
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

	private Histogram histogram;
	private ColorMapping colorMapping;
	private GrouperRenderStyle renderStyle;

	private boolean bUpdateColorPointPosition = false;
	private boolean bUpdateLeftSpread = false;
	private boolean bUpdateRightSpread = false;
	private boolean bIsFirstTimeUpdateColor = false;
	private float fColorPointPositionOffset = 0.0f;
	private int iColorMappingPointMoved = -1;
	private HashMap<Integer, GroupRepresentation> hashGroups;
	private HashMap<Integer, VAElementRepresentation> hashElements;

	private DragAndDropController dragAndDropController = null;
	protected RedrawViewListener redrawViewListener = null;
	protected ClearSelectionsListener clearSelectionsListener = null;

	private TextRenderer textRenderer;

	float fRenderWidth;

	private ColorMappingManager colorMappingManager;

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
		dragAndDropController = new DragAndDropController();

		renderStyle = new GrouperRenderStyle(this, viewFrustum);
		textRenderer = new TextRenderer(new Font("Arial", Font.PLAIN, 32), true, true);
		// registerEventListeners();
	}

	@Override
	public void init(GL gl) {
		initTestHierarchy();
	}

	@Override
	public void initLocal(GL gl) {

		iGLDisplayListIndexLocal = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexLocal;
		init(gl);
	}

	public void initTestHierarchy() {
		GroupRepresentation root =
			new GroupRepresentation(iUniqueID, pickingManager, new ClusterNode("root", 0, 0, 0, true),
				renderStyle);
		
		GroupRepresentation group1 =
			new GroupRepresentation(iUniqueID, pickingManager, new ClusterNode("group1", 4, 0, 0, false),
				renderStyle);
		GroupRepresentation group2 =
			new GroupRepresentation(iUniqueID, pickingManager, new ClusterNode("group2", 5, 0, 0, false),
				renderStyle);
		GroupRepresentation group3 =
			new GroupRepresentation(iUniqueID, pickingManager, new ClusterNode("group3", 6, 0, 0, false),
				renderStyle);

		VAElementRepresentation element1 =
			new VAElementRepresentation(iUniqueID, pickingManager, new ClusterNode("one", 1, 0, 0, false));
		VAElementRepresentation element2 =
			new VAElementRepresentation(iUniqueID, pickingManager, new ClusterNode("two", 2, 0, 0, false));
		VAElementRepresentation element3 =
			new VAElementRepresentation(iUniqueID, pickingManager, new ClusterNode("three", 3, 0, 0, false));
		VAElementRepresentation element4 =
			new VAElementRepresentation(iUniqueID, pickingManager, new ClusterNode("four", 7, 0, 0, false));
		VAElementRepresentation element5 =
			new VAElementRepresentation(iUniqueID, pickingManager, new ClusterNode("five", 8, 0, 0, false));

		root.add(element1);
		root.add(group1);
		root.add(group3);
		group1.add(element2);
		group1.add(group2);
		group2.add(element3);
		group2.add(element4);
		group3.add(element5);
		

		hashGroups.put(0, root);
		hashGroups.put(4, group1);
		hashGroups.put(5, group2);
		hashGroups.put(6, group3);
		hashElements.put(1, element1);
		hashElements.put(2, element2);
		hashElements.put(3, element3);
		hashElements.put(7, element4);
		hashElements.put(8, element5);
		
		root.calculateHierarchyLevels(0);
	}

	@Override
	public void initRemote(final GL gl, final AGLEventListener glParentView,
		final GLMouseListener glMouseListener, GLInfoAreaManager infoAreaManager) {

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

		if (bIsDisplayListDirtyLocal || dragAndDropController.isDragging()) {
			buildDisplayList(gl, iGLDisplayListIndexLocal);
			bIsDisplayListDirtyLocal = false;
		}
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		display(gl);
		checkForHits(gl);
	}

	@Override
	public void displayRemote(GL gl) {
		if (bIsDisplayListDirtyRemote || dragAndDropController.isDragging()) {
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
	}

	private void buildDisplayList(final GL gl, int iGLDisplayListIndex) {
		gl.glNewList(iGLDisplayListIndex, GL.GL_COMPILE);

		
		GroupRepresentation root = hashGroups.get(0);
		Vec3f vecPosition = new Vec3f(viewFrustum.getWidth() / 2.0f, viewFrustum.getHeight(), -10.0f);	
		root.setPosition(vecPosition);
		root.calculateDrawingParameters(gl, textRenderer);
		root.draw(gl, textRenderer, vecPosition);
		dragAndDropController.handleDragging(gl, glMouseListener);

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
						if(groupRep != null) {
							dragAndDropController.clearDraggables();
							dragAndDropController.addDraggable(groupRep);
							dragAndDropController.startDragging();
						}
						break;
					case DRAGGED:
						if(groupRep != null) {
							if(dragAndDropController.isDragging()) {
								dragAndDropController.setDropArea(groupRep);
							}
						}
						break;
					default:
						return;
				}
				break;

			case GROUPER_VA_ELEMENT_SELECTION:
				switch (pickingMode) {
					case CLICKED:
						break;
					case MOUSE_OVER:
						break;
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

}
