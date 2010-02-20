package org.caleydo.view.compare;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.media.opengl.GL;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.view.opengl.CmdCreateView;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.data.selection.EVAType;
import org.caleydo.core.data.selection.IVirtualArray;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.IVirtualArrayDelta;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.delta.VADeltaItem;
import org.caleydo.core.data.selection.delta.VirtualArrayDelta;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.EProjectionMode;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.view.heatmap.GLHeatMap;

import com.sun.opengl.util.j2d.TextRenderer;

/**
 * The group assignment interface
 * 
 * @author Christian Partl
 * @author Alexander Lex
 * @author Marc Streit
 */
public class GLCompare extends AGLView implements IViewCommandHandler,
		IGLRemoteRenderingView {

	public final static String VIEW_ID = "org.caleydo.view.compare";

	private TextRenderer textRenderer;
	private SelectionManager selectionManager;

	private GLHeatMap glHeatMapView;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param sLabel
	 * @param viewFrustum
	 */
	public GLCompare(GLCaleydoCanvas glCanvas, final String sLabel,
			final IViewFrustum viewFrustum) {
		super(glCanvas, sLabel, viewFrustum, true);

		viewType = VIEW_ID;
	}

	@Override
	public void init(GL gl) {
		contentVA = useCase.getVA(EVAType.CONTENT);
		storageVA = useCase.getVA(EVAType.STORAGE);
		createHeatMap();
		glHeatMapView.initRemote(gl, this, glMouseListener, null);
		glHeatMapView.useFishEye(false);
	}

	@Override
	public void initLocal(GL gl) {

		iGLDisplayListIndexLocal = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		init(gl);
	}

	@Override
	public void initRemote(final GL gl, final AGLView glParentView,
			final GLMouseListener glMouseListener,
			GLInfoAreaManager infoAreaManager) {

		// Register keyboard listener to GL canvas
		glParentView.getParentGLCanvas().getParentComposite().getDisplay()
				.asyncExec(new Runnable() {
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
	public void initData() {
		super.initData();
	}

	/**
	 * Create embedded heat map
	 * 
	 * @param
	 */
	private void createHeatMap() {
		CmdCreateView cmdView = (CmdCreateView) generalManager
				.getCommandManager().createCommandByType(
						ECommandType.CREATE_GL_VIEW);
		cmdView.setViewID(GLHeatMap.VIEW_ID);

		float fHeatMapHeight = viewFrustum.getHeight();
		float fHeatMapWidth = viewFrustum.getWidth();

		cmdView.setAttributes(dataDomain, EProjectionMode.ORTHOGRAPHIC, 0,
				fHeatMapHeight, 0, fHeatMapWidth, -20, 20, -1);

		cmdView.doCommand();

		glHeatMapView = (GLHeatMap) cmdView.getCreatedObject();
		glHeatMapView.setUseCase(useCase);
		glHeatMapView.setRemoteRenderingGLView(this);

		glHeatMapView.setDataDomain(dataDomain);
		glHeatMapView.setSet(set);
	    glHeatMapView.setContentVAType(EVAType.CONTENT_EMBEDDED_HM);
		glHeatMapView.initData();
		glHeatMapView.setDetailLevel(EDetailLevel.MEDIUM);
		setEmbeddedHeatMapData();
	}

	private void setEmbeddedHeatMapData() {

		// TODO: Is this really necessary?
		glHeatMapView.resetView();
		IVirtualArrayDelta delta = new VirtualArrayDelta(EVAType.CONTENT_EMBEDDED_HM,
				EIDType.EXPRESSION_INDEX);

		for (int i = 0; i < 10; i++) {
			if (i >= contentVA.size())
				break;

			int contentIndex = contentVA.get(i);
			delta.add(VADeltaItem.append(contentIndex));
		}
		for (int i = 10; i < contentVA.size(); i++) {
			int contentIndex = contentVA.get(i);
			delta.add(VADeltaItem.removeElement(contentIndex));
		}
		
		glHeatMapView.handleVirtualArrayUpdate(delta, getShortInfo());

	}

	@Override
	public void setDetailLevel(EDetailLevel detailLevel) {

	}

	@Override
	public void displayLocal(GL gl) {
		processEvents();
		if (glHeatMapView != null)
			glHeatMapView.processEvents();
		if (!isVisible())
			return;
		pickingManager.handlePicking(this, gl);

		if (bIsDisplayListDirtyLocal) {
			bIsDisplayListDirtyLocal = false;
			buildDisplayList(gl, iGLDisplayListIndexLocal);
			glHeatMapView.setDisplayListDirty();
		}
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		display(gl);
		checkForHits(gl);
	}

	@Override
	public void displayRemote(GL gl) {
		if (bIsDisplayListDirtyRemote) {
			bIsDisplayListDirtyRemote = false;
			buildDisplayList(gl, iGLDisplayListIndexRemote);
		}
		iGLDisplayListToCall = iGLDisplayListIndexRemote;

		display(gl);
		checkForHits(gl);
	}

	@Override
	public void display(GL gl) {
		// processEvents();
		gl.glCallList(iGLDisplayListToCall);

		glHeatMapView.getViewFrustum().setTop(viewFrustum.getTop() / 2.0f);
		glHeatMapView.getViewFrustum().setRight(viewFrustum.getRight() / 2.0f);
		gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.COMPARE_EMBEDDED_VIEW_SELECTION, glHeatMapView
						.getID()));
		glHeatMapView.displayRemote(gl);
		gl.glPopName();

		if (!isRenderedRemote())
			contextMenu.render(gl, this);
	}

	private void buildDisplayList(final GL gl, int iGLDisplayListIndex) {
		gl.glNewList(iGLDisplayListIndex, GL.GL_COMPILE);

		gl.glEndList();
	}

	@Override
	public String getDetailedInfo() {
		return new String("");
	}

	@Override
	protected void handlePickingEvents(EPickingType ePickingType,
			EPickingMode pickingMode, int iExternalID, Pick pick) {
		if (detailLevel == EDetailLevel.VERY_LOW) {
			return;
		}

		switch (ePickingType) {
		case COMPARE_EMBEDDED_VIEW_SELECTION:
			if (pickingMode == EPickingMode.RIGHT_CLICKED) {
				contextMenu.setLocation(pick.getPickedPoint(),
						getParentGLCanvas().getWidth(), getParentGLCanvas()
								.getHeight());
				contextMenu.setMasterGLView(this);
			}
			break;
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
		SerializedCompareView serializedForm = new SerializedCompareView(
				dataDomain);
		serializedForm.setViewID(this.getID());
		return serializedForm;
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
	public List<AGLView> getRemoteRenderedViews() {
		// TODO: Do it differently?
		return new ArrayList<AGLView>();
	}

}
