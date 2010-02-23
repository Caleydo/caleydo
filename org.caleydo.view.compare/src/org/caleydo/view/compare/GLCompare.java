package org.caleydo.view.compare;

import gleem.linalg.Vec2f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.set.SetComparer;
import org.caleydo.core.data.collection.set.SetRelations;
import org.caleydo.core.data.selection.ContentSelectionManager;
import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.StorageVAType;
import org.caleydo.core.manager.event.view.grouper.CompareGroupsEvent;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.clusterer.ClusterState;
import org.caleydo.core.util.clusterer.EClustererAlgo;
import org.caleydo.core.util.clusterer.EClustererType;
import org.caleydo.core.util.clusterer.EDistanceMeasure;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.view.compare.listener.CompareGroupsEventListener;

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

	private ArrayList<ISet> setsToCompare;

	private TextRenderer textRenderer;
	private HeatMapLayoutLeft heatMapLayoutLeft;
	private HeatMapLayoutRight heatMapLayoutRight;
	private HeatMapWrapper leftHeatMapWrapper;
	private HeatMapWrapper rightHeatMapWrapper;

	private CompareGroupsEventListener compareGroupsEventListener;
	
	private SetRelations relations;
	
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
		setsToCompare = new ArrayList<ISet>();
	}

	@Override
	public void init(GL gl) {
//		contentVA = useCase.getContentVA(ContentVAType.CONTENT);
//		storageVA = useCase.getStorageVA(StorageVAType.STORAGE);
		heatMapLayoutLeft = new HeatMapLayoutLeft();
		heatMapLayoutRight = new HeatMapLayoutRight();
		leftHeatMapWrapper = new HeatMapWrapper(heatMapLayoutLeft);
		rightHeatMapWrapper = new HeatMapWrapper(heatMapLayoutRight);
		leftHeatMapWrapper.init(gl, this, glMouseListener, null, useCase, this,
				dataDomain);
		rightHeatMapWrapper.init(gl, this, glMouseListener, null, useCase,
				this, dataDomain);

		leftHeatMapWrapper.setSet(set);
		rightHeatMapWrapper.setSet(set);
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

	// /**
	// * Create embedded heat map
	// *
	// * @param
	// */
	//
	// <<<<<<< .mine
	// =======
	// float fHeatMapHeight = viewFrustum.getHeight();
	// float fHeatMapWidth = viewFrustum.getWidth();
	//
	// cmdView.setAttributes(dataDomain, EProjectionMode.ORTHOGRAPHIC, 0,
	// fHeatMapHeight, 0, fHeatMapWidth, -20, 20, -1);
	//
	// cmdView.doCommand();
	//
	// glHeatMapView = (GLHeatMap) cmdView.getCreatedObject();
	// glHeatMapView.setUseCase(useCase);
	// glHeatMapView.setRemoteRenderingGLView(this);
	//
	// glHeatMapView.setDataDomain(dataDomain);
	// glHeatMapView.setSet(set);
	// glHeatMapView.setContentVAType(ContentVAType.CONTENT_EMBEDDED_HM);
	// glHeatMapView.initData();
	// glHeatMapView.setDetailLevel(EDetailLevel.MEDIUM);
	// setEmbeddedHeatMapData();
	// }
	//
	// private void setEmbeddedHeatMapData() {
	//
	// // TODO: Is this really necessary?
	// glHeatMapView.resetView();
	//		
	//
	// for (int i = 0; i < 10; i++) {
	// if (i >= contentVA.size())
	// break;
	//
	// int contentIndex = contentVA.get(i);
	// delta.add(VADeltaItem.append(contentIndex));
	// }
	// for (int i = 10; i < contentVA.size(); i++) {
	// int contentIndex = contentVA.get(i);
	// delta.add(VADeltaItem.removeElement(contentIndex));
	// }
	//
	// glHeatMapView.handleContentVAUpdate(delta, getShortInfo());
	// }

	@Override
	public void setDetailLevel(EDetailLevel detailLevel) {

	}

	@Override
	public void displayLocal(GL gl) {
		processEvents();
		leftHeatMapWrapper.processEvents();
		rightHeatMapWrapper.processEvents();
		if (!isVisible())
			return;
		pickingManager.handlePicking(this, gl);
		heatMapLayoutLeft.setLayoutParameters(0.0f, 0.0f, viewFrustum.getTop(),
				viewFrustum.getRight() / 3.0f);
		heatMapLayoutRight.setLayoutParameters(
				2.0f * viewFrustum.getRight() / 3.0f, 0.0f, viewFrustum
						.getTop(), viewFrustum.getRight() / 3.0f);

		if (bIsDisplayListDirtyLocal) {
			bIsDisplayListDirtyLocal = false;
			buildDisplayList(gl, iGLDisplayListIndexLocal);
			leftHeatMapWrapper.setDisplayListDirty();
			rightHeatMapWrapper.setDisplayListDirty();
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
		leftHeatMapWrapper.drawRemoteItems(gl);
		rightHeatMapWrapper.drawRemoteItems(gl);
		renderRelations(gl);
		
		if (!isRenderedRemote())
			contextMenu.render(gl, this);
	}

	private void buildDisplayList(final GL gl, int iGLDisplayListIndex) {
		gl.glNewList(iGLDisplayListIndex, GL.GL_COMPILE);
		leftHeatMapWrapper.drawLocalItems(gl, textureManager, pickingManager,
				iUniqueID);
		rightHeatMapWrapper.drawLocalItems(gl, textureManager, pickingManager,
				iUniqueID);
		gl.glEndList();
	}

	@Override
	public String getDetailedInfo() {
		return new String("");
	}

	private void renderRelations(GL gl) {
		
		if (relations == null)
			return;
	
		//ContentVirtualArray contentVALeftAll = relations.getSetLeft().getContentVA(ContentVAType.CONTENT);
		ContentSelectionManager contentSelectionManager = leftHeatMapWrapper.getContentSelectionManagersOfHeatMaps().get(0);
		ContentVirtualArray contentVALeft = leftHeatMapWrapper.getContentVAsOfHeatMaps().get(0);
		//ContentVirtualArray contentVARight = rightHeatMapWrapper.getContentVAsOfHeatMaps().get(0);
		
		gl.glColor3f(0,0,0);
		gl.glLineWidth(4);
		//HashMap<Integer, Integer> relationLeftToRight = relations.getHashLeftToRight();
		for (Integer contentID : contentVALeft) {		
			
			//Integer contentID = contentVALeft.get(contentIndex);
			
			for (SelectionType type : contentSelectionManager.getSelectionTypes(contentID)) {
				gl.glColor4fv(type.getColor(), 0);	
			}	
			
			//int relationIndex = contentVALeftAll.indexOf(contentID);
			Vec2f leftPos = leftHeatMapWrapper.getRightLinkPositionFromContentID(contentID);
			
			if (leftPos == null)
				continue;
			
			Vec2f rightPos = rightHeatMapWrapper.getRightLinkPositionFromContentID(contentID);
			
			if (rightPos == null)
				continue;
			
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(leftPos.x(), viewFrustum.getHeight()-leftPos.y(), 0);
			gl.glVertex3f(rightPos.x(), viewFrustum.getHeight()-rightPos.y(), 0);			
			gl.glEnd();
		}
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
		compareGroupsEventListener = new CompareGroupsEventListener();
		compareGroupsEventListener.setHandler(this);
		eventPublisher.addListener(CompareGroupsEvent.class,
				compareGroupsEventListener);

	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();
		if (compareGroupsEventListener != null) {
			eventPublisher.removeListener(compareGroupsEventListener);
			compareGroupsEventListener = null;
		}
	}

	@Override
	public List<AGLView> getRemoteRenderedViews() {
		// TODO: Do it differently?
		return new ArrayList<AGLView>();
	}

	public void setGroupsToCompare(ArrayList<ISet> sets) {
		setsToCompare.clear();
		setsToCompare.addAll(sets);

		ClusterState clusterState = new ClusterState();
		clusterState.setClustererAlgo(EClustererAlgo.AFFINITY_PROPAGATION);
		clusterState.setClustererType(EClustererType.GENE_CLUSTERING);
		clusterState.setAffinityPropClusterFactorGenes(5);
		clusterState.setDistanceMeasure(EDistanceMeasure.EUCLIDEAN_DISTANCE);

		for (ISet set : sets) {
			set.cluster(clusterState);
		}

		if (sets.size() >= 2) {
			ISet setLeft = setsToCompare.get(0);
			ISet setRight = setsToCompare.get(1);
			relations = SetComparer.compareSets(setLeft, setRight);

			leftHeatMapWrapper.setSet(setLeft);
			rightHeatMapWrapper.setSet(setRight);
			setDisplayListDirty();
		}

	}

}
