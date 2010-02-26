package org.caleydo.view.compare;

import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.HashMap;

import javax.media.opengl.GL;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.view.opengl.CmdCreateView;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.selection.ContentGroupList;
import org.caleydo.core.data.selection.ContentSelectionManager;
import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.data.selection.Group;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.StorageVAType;
import org.caleydo.core.data.selection.StorageVirtualArray;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IUseCase;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.camera.EProjectionMode;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.view.heatmap.GLHeatMap;

public class HeatMapWrapper {

	// private GLHeatMap heatMap;
	private HeatMapOverview overview;
	private ISet set;
	private IGeneralManager generalManager;
	private ContentVirtualArray contentVA;
	private StorageVirtualArray storageVA;
	private HeatMapLayout layout;
	private ArrayList<ContentVirtualArray> heatMapVAs;
	private HashMap<Integer, GLHeatMap> hashHeatMaps;
	private ArrayList<Pair<Integer, Pair<Integer, Integer>>> selectedGroups;
	private int id;
	private boolean useDetailView;
	private boolean haveGroupsChanged;

	private AGLView glParentView;
	private GLInfoAreaManager infoAreaManager;
	private IUseCase useCase;
	private IGLRemoteRenderingView parentView;
	private EDataDomain dataDomain;

	public HeatMapWrapper(int id, HeatMapLayout layout, AGLView glParentView,
			GLInfoAreaManager infoAreaManager, IUseCase useCase,
			IGLRemoteRenderingView parentView, EDataDomain dataDomain) {
		generalManager = GeneralManager.get();
		heatMapVAs = new ArrayList<ContentVirtualArray>();
		overview = new HeatMapOverview(layout);
		hashHeatMaps = new HashMap<Integer, GLHeatMap>();
		selectedGroups = new ArrayList<Pair<Integer, Pair<Integer, Integer>>>();
		this.layout = layout;
		this.id = id;
		this.glParentView = glParentView;
		this.infoAreaManager = infoAreaManager;
		this.useCase = useCase;
		this.parentView = parentView;
		this.dataDomain = dataDomain;
	}

	private GLHeatMap createHeatMap(GL gl, GLMouseListener glMouseListener) {

		CmdCreateView cmdView = (CmdCreateView) generalManager
				.getCommandManager().createCommandByType(
						ECommandType.CREATE_GL_VIEW);
		cmdView.setViewID(GLHeatMap.VIEW_ID);

		cmdView.setAttributes(dataDomain, EProjectionMode.ORTHOGRAPHIC, 0, 50,
				0, 50, -20, 20, -1);

		cmdView.doCommand();

		GLHeatMap heatMap = (GLHeatMap) cmdView.getCreatedObject();
		heatMap.setUseCase(useCase);
		heatMap.setRemoteRenderingGLView(parentView);
		heatMap.setSet(set);
		heatMap.setDataDomain(dataDomain);
		heatMap.setContentVAType(ContentVAType.CONTENT_EMBEDDED_HM);
		heatMap.initData();
		heatMap.setDetailLevel(EDetailLevel.MEDIUM);
		heatMap.initRemote(gl, glParentView, glMouseListener, infoAreaManager);
		heatMap.useFishEye(false);

		return heatMap;
	}

	public void setSet(ISet set) {
		this.set = set;
		contentVA = set.getContentVA(ContentVAType.CONTENT);
		storageVA = set.getStorageVA(StorageVAType.STORAGE);
		hashHeatMaps.clear();

		// heatMap.useFishEye(false);
		// heatMap.setDisplayListDirty();

		overview.setSet(set);

		// FIXME: Just for testing
		if (contentVA.size() > 80 && contentVA.getGroupList() == null) {
			ContentGroupList groupList = new ContentGroupList();
			contentVA.setGroupList(groupList);
			Group temp = new Group(5, false, 0, SelectionType.NORMAL);
			groupList.append(temp);
			temp = new Group(10, false, 0, SelectionType.NORMAL);
			groupList.append(temp);
			temp = new Group(20, false, 0, SelectionType.NORMAL);
			groupList.append(temp);
			temp = new Group(15, false, 0, SelectionType.NORMAL);
			groupList.append(temp);
			temp = new Group(30, false, 0, SelectionType.NORMAL);
			groupList.append(temp);
			temp = new Group(contentVA.size() - 80, false, 0,
					SelectionType.NORMAL);
			groupList.append(temp);
		}
	}

	public void init(GL gl, AGLView glParentView,
			GLMouseListener glMouseListener, GLInfoAreaManager infoAreaManager,
			IUseCase useCase, IGLRemoteRenderingView parentView,
			EDataDomain dataDomain) {
		// createHeatMap(useCase, parentView, dataDomain);

	}

	private void setEmbeddedHeatMapData(GLHeatMap heatMap,
			int firstSampleIndex, int lastSampleIndex) {

		// TODO: Is this really necessary?
		heatMap.resetView();
		ContentVirtualArray va = new ContentVirtualArray();

		for (int i = firstSampleIndex; i <= lastSampleIndex; i++) {
			if (i >= contentVA.size())
				break;
			va.append(contentVA.get(i));

		}
		heatMapVAs.clear();
		heatMapVAs.add(va);

		heatMap.setContentVA(va);
		heatMap.useFishEye(false);
	}

	public void drawLocalItems(GL gl, TextureManager textureManager,
			PickingManager pickingManager, int viewID) {

		overview.draw(gl, textureManager, pickingManager, viewID, id);

	}

	public void drawRemoteItems(GL gl) {

		// if (useDetailView) {
		Vec3f detailPosition = layout.getDetailPosition();

		float currentPositionY = detailPosition.y() + layout.getDetailHeight();
		int numTotalSamples = 0;
		for (Pair<Integer, Pair<Integer, Integer>> groupWithBounds : selectedGroups) {
			int numSamplesInHeatMap = (groupWithBounds.getSecond().getSecond() - groupWithBounds
					.getSecond().getFirst()) + 1;
			numTotalSamples += numSamplesInHeatMap;
		}

		for (Pair<Integer, Pair<Integer, Integer>> groupWithBounds : selectedGroups) {

			GLHeatMap heatMap = hashHeatMaps.get(groupWithBounds.getFirst());
			if (heatMap == null)
				continue;
			int numSamplesInHeatMap = (groupWithBounds.getSecond().getSecond() - groupWithBounds
					.getSecond().getFirst()) + 1;
			float heatMapHeight = layout
					.getDetailHeatMapHeight(numSamplesInHeatMap,
							numTotalSamples, selectedGroups.size());

			gl.glTranslatef(detailPosition.x(), currentPositionY
					- heatMapHeight, detailPosition.z());
			heatMap.getViewFrustum().setLeft(detailPosition.x());
			heatMap.getViewFrustum()
					.setBottom(currentPositionY - heatMapHeight);
			heatMap.getViewFrustum().setRight(
					detailPosition.x() + layout.getDetailWidth());
			heatMap.getViewFrustum().setTop(currentPositionY);
			// if(haveGroupsChanged) {
			// heatMap.setDisplayListDirty();
			// haveGroupsChanged = false;
			// }
			heatMap.displayRemote(gl);

			gl.glTranslatef(-detailPosition.x(),
					-(currentPositionY - heatMapHeight), -detailPosition.z());
			currentPositionY -= (heatMapHeight + layout
					.getDetailHeatMapGapHeight());
		}

		// }

		// ContentVirtualArray va = heatMapVAs.get(0);
		//
		// for (int i = 0; i < va.size(); i++) {
		// Vec2f position = getLeftLinkPositionFromContentID(va.get(i));
		// GLHelperFunctions.drawPointAt(gl, position.x(), position.y(), 1);
		// position = getRightLinkPositionFromContentID(va.get(i));
		// GLHelperFunctions.drawPointAt(gl, position.x(), position.y(), 1);
		// }
	}

	public boolean handleDragging(GL gl, GLMouseListener glMouseListener) {
		if (overview.handleDragging(gl, glMouseListener)) {

			selectedGroups = overview.getSelectedGroups();

			for (Pair<Integer, Pair<Integer, Integer>> groupWithBounds : selectedGroups) {
				if (!hashHeatMaps.containsKey(groupWithBounds.getFirst())) {
					GLHeatMap heatMap = createHeatMap(gl, glMouseListener);
					setEmbeddedHeatMapData(heatMap, groupWithBounds.getSecond()
							.getFirst(), groupWithBounds.getSecond()
							.getSecond());

					hashHeatMaps.put(groupWithBounds.getFirst(), heatMap);
				}
				GLHeatMap heatMap = hashHeatMaps
						.get(groupWithBounds.getFirst());
				heatMap.setDisplayListDirty();
			}
			// if (selectedGroups.size() > 0) {
			// setEmbeddedHeatMapData(selectedGroupBounds.get(0).getFirst(),
			// selectedGroupBounds.get(0).getSecond());
			// }
			return true;
		}
		return false;
	}

	public void processEvents() {

		for (Pair<Integer, Pair<Integer, Integer>> groupWithBounds : selectedGroups) {
			GLHeatMap heatMap = hashHeatMaps.get(groupWithBounds.getFirst());
			if (heatMap != null) {
				heatMap.processEvents();
			}
		}
	}

	public void setDisplayListDirty() {
		
		for (Pair<Integer, Pair<Integer, Integer>> groupWithBounds : selectedGroups) {
			GLHeatMap heatMap = hashHeatMaps.get(groupWithBounds.getFirst());

			if (heatMap != null) {
				heatMap.setDisplayListDirty();
			}
		}

	}

	public Vec2f getLeftLinkPositionFromContentID(int contentID) {

		int contentIndex = contentVA.indexOf(contentID);

		if (contentVA.indexOf(contentID) == -1)
			return null;

		Vec3f overviewPosition = layout.getOverviewPosition();

		return new Vec2f(overviewPosition.x(), overviewPosition.y()
				+ layout.getOverviewHeight() / contentVA.size() * contentIndex);
	}

	public Vec2f getRightLinkPositionFromContentID(int contentID,
			ContentVirtualArray contentVA) {

		int contentIndex = contentVA.indexOf(contentID);

		if (contentVA.indexOf(contentID) == -1)
			return null;

		Vec3f detailPosition = layout.getDetailPosition();

		// For the group check we need the index in the global content VA
		Integer groupID = getGroupIDFromContentIndex(set.getContentVA(
				ContentVAType.getPrimaryVAType()).indexOf(contentID));
		if (groupID == null)
			return null;

		GLHeatMap heatMap = hashHeatMaps.get(groupID);
		if(heatMap == null)
			return null;

		return new Vec2f(detailPosition.x() + layout.getDetailWidth(),
				detailPosition.y()
						+ heatMap.getYCoordinateByContentIndex(contentIndex));
	}

	public ArrayList<ContentVirtualArray> getContentVAsOfHeatMaps() {
		return heatMapVAs;
	}

	public ArrayList<ContentSelectionManager> getContentSelectionManagersOfHeatMaps() {

		ArrayList<ContentSelectionManager> contentSelectionManagers = new ArrayList<ContentSelectionManager>();
		for (Pair<Integer, Pair<Integer, Integer>> groupWithBounds : selectedGroups) {
			GLHeatMap heatMap = hashHeatMaps.get(groupWithBounds.getFirst());
			contentSelectionManagers.add(heatMap.getContentSelectionManager());
		}

		return contentSelectionManagers;
	}

	private Integer getGroupIDFromContentIndex(int contentIndex) {
		for (Pair<Integer, Pair<Integer, Integer>> groupWithBounds : selectedGroups) {
			if (contentIndex >= groupWithBounds.getSecond().getFirst()
					&& contentIndex <= groupWithBounds.getSecond().getSecond()) {
				return groupWithBounds.getFirst();
			}
		}
		return null;
	}

	public int getID() {
		return id;
	}

	public void useDetailView(boolean useDetailView) {
		this.useDetailView = useDetailView;
	}

	public void handleOverviewSliderSelection(EPickingType pickingType,
			EPickingMode pickingMode) {
		overview.handleSliderSelection(pickingType, pickingMode);
	}

	// public void handleGroupSelection(SelectionType selectionType,
	// int groupIndex) {
	//
	// ContentSelectionManager contentSelectionManager =
	// heatMap.getContentSelectionManager();
	//		
	// contentSelectionManager.clearSelection(selectionType);
	// contentVA.getGroupList().get(groupIndex).setSelectionType(selectionType);
	// ArrayList<Integer> groupElements =
	// set.getContentTree().getNodeByNumber(groupIndex).getLeaveIds();
	//		
	// contentSelectionManager.clearSelection(selectionType);
	// contentSelectionManager.addToType(selectionType, groupElements);
	// //
	// ISelectionDelta selectionDelta = contentSelectionManager.getDelta();
	// SelectionUpdateEvent event = new SelectionUpdateEvent();
	// event.setSender(this);
	// event.setSelectionDelta((SelectionDelta) selectionDelta);
	// //event.setInfo(getShortInfoLocal());
	// GeneralManager.get().getEventPublisher().triggerEvent(event);
	// }
}
