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
			new GroupRepresentation(iUniqueID, pickingManager, new ClusterNode("root", 0, 0, 0, true));

		VAElementRepresentation element1 =
			new VAElementRepresentation(iUniqueID, pickingManager, new ClusterNode("one", 1, 0, 0, true));
		VAElementRepresentation element2 =
			new VAElementRepresentation(iUniqueID, pickingManager, new ClusterNode("two", 2, 0, 0, true));
		VAElementRepresentation element3 =
			new VAElementRepresentation(iUniqueID, pickingManager, new ClusterNode("three", 3, 0, 0, true));

		root.add(element1);
		root.add(element2);
		root.add(element3);

		hashGroups.put(0, root);
		hashElements.put(1, element1);
		hashElements.put(2, element2);
		hashElements.put(3, element3);
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
	}

	private void buildDisplayList(final GL gl, int iGLDisplayListIndex) {
		gl.glNewList(iGLDisplayListIndex, GL.GL_COMPILE);
		
		GroupRepresentation root = hashGroups.get(0);
		Vec3f vecPosition = new Vec3f(viewFrustum.getWidth() / 2.0f, viewFrustum.getHeight(), 0.0f);
		root.calculateDrawingParameters(gl, textRenderer);
		root.setPosition(vecPosition);
		root.draw(gl, textRenderer, vecPosition);

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

				switch (pickingMode) {
					case CLICKED:
						break;
					case MOUSE_OVER:
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
