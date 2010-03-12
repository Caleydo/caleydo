package org.caleydo.view.compare;

import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.HashMap;

import javax.media.opengl.GL;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.view.opengl.CmdCreateView;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.ContentGroupList;
import org.caleydo.core.data.selection.ContentSelectionManager;
import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.data.selection.Group;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.StorageVAType;
import org.caleydo.core.data.selection.StorageVirtualArray;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IUseCase;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.view.opengl.camera.EProjectionMode;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.SelectionUpdateListener;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.view.compare.rendercommand.IHeatMapRenderCommand;
import org.caleydo.view.heatmap.GLHeatMap;

public class HeatMapWrapper implements ISelectionUpdateHandler {

	// private GLHeatMap heatMap;
	private HeatMapOverview overview;
	private ISet set;
	private IGeneralManager generalManager;
	private ContentVirtualArray contentVA;
	private StorageVirtualArray storageVA;
	private AHeatMapLayout layout;
	private HashMap<Integer, GLHeatMap> hashHeatMaps;
	private HashMap<Integer, Vec3f> hashHeatMapPositions;
	private ArrayList<GroupInfo> selectedGroups;
	private boolean isNewSelection;
	private int id;
	private int activeHeatMapID;
	private boolean useDetailView;

	private AGLView glParentView;
	private GLInfoAreaManager infoAreaManager;
	private IUseCase useCase;
	private IGLRemoteRenderingView parentView;
	private EDataDomain dataDomain;

	private SelectionUpdateListener selectionUpdateListener;
	private IEventPublisher eventPublisher;
	private ContentSelectionManager contentSelectionManager;
	private SelectionType activeHeatMapSelectionType;

	public HeatMapWrapper(int id, AHeatMapLayout layout, AGLView glParentView,
			GLInfoAreaManager infoAreaManager, IUseCase useCase,
			IGLRemoteRenderingView parentView, EDataDomain dataDomain,
			SelectionType activeHeatMapSelectionType) {

		generalManager = GeneralManager.get();
		overview = new HeatMapOverview(layout);
		hashHeatMaps = new HashMap<Integer, GLHeatMap>();
		hashHeatMapPositions = new HashMap<Integer, Vec3f>();
		selectedGroups = new ArrayList<GroupInfo>();

		this.layout = layout;
		this.id = id;
		this.glParentView = glParentView;
		this.infoAreaManager = infoAreaManager;
		this.useCase = useCase;
		this.parentView = parentView;
		this.dataDomain = dataDomain;
		this.activeHeatMapSelectionType = activeHeatMapSelectionType;

		isNewSelection = false;
		eventPublisher = GeneralManager.get().getEventPublisher();
		contentSelectionManager = useCase.getContentSelectionManager();
		// contentSelectionManager.addTypeToDeltaBlacklist(activeHeatMapSelectionType);
		activeHeatMapID = -1;
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
		heatMap.setSendClearSelectionsEvent(true);
		// ContentSelectionManager hmContentSelectionManager =
		// heatMap.getContentSelectionManager();
		// hmContentSelectionManager.addSelectionType(activeHeatMapSelectionType);
		// hmContentSelectionManager.addTypeToDeltaBlacklist(activeHeatMapSelectionType);
		heatMap.useFishEye(false);

		return heatMap;
	}

	public void setSet(ISet set) {
		this.set = set;
		contentVA = set.getContentVA(ContentVAType.CONTENT);
		storageVA = set.getStorageVA(StorageVAType.STORAGE);
		hashHeatMaps.clear();
		selectedGroups.clear();
		contentSelectionManager.clearSelections();
		contentSelectionManager.setVA(contentVA);

		// heatMap.useFishEye(false);
		// heatMap.setDisplayListDirty();

		overview.setSet(set);

		// FIXME: Just for testing
		// if (contentVA.size() > 80 && contentVA.getGroupList() == null) {
		// ContentGroupList groupList = new ContentGroupList();
		// contentVA.setGroupList(groupList);
		// Group temp = new Group(5, false, 0, SelectionType.NORMAL);
		// groupList.append(temp);
		// temp = new Group(10, false, 0, SelectionType.NORMAL);
		// groupList.append(temp);
		// temp = new Group(20, false, 0, SelectionType.NORMAL);
		// groupList.append(temp);
		// temp = new Group(15, false, 0, SelectionType.NORMAL);
		// groupList.append(temp);
		// temp = new Group(30, false, 0, SelectionType.NORMAL);
		// groupList.append(temp);
		// temp = new Group(contentVA.size() - 80, false, 0,
		// SelectionType.NORMAL);
		// groupList.append(temp);
		// }
	}

	public void init(GL gl, AGLView glParentView,
			GLMouseListener glMouseListener, GLInfoAreaManager infoAreaManager,
			IUseCase useCase, IGLRemoteRenderingView parentView,
			EDataDomain dataDomain) {

		ContentGroupList contentGroupList = contentVA.getGroupList();
		hashHeatMaps.clear();
		selectedGroups.clear();

		if (contentGroupList == null)
			return;

		int groupSampleStartIndex = 0;
		int groupSampleEndIndex = 0;
		int groupIndex = 0;
		for (Group group : contentGroupList) {
			groupSampleEndIndex = groupSampleStartIndex + group.getNrElements()
					- 1;
			group.setSelectionType(SelectionType.NORMAL);
			GLHeatMap heatMap = createHeatMap(gl, glMouseListener);
			setEmbeddedHeatMapData(heatMap, groupSampleStartIndex,
					groupSampleEndIndex);

			hashHeatMaps.put(groupIndex, heatMap);
			groupSampleStartIndex += group.getNrElements();
			groupIndex++;
		}

	}

	private void setEmbeddedHeatMapData(GLHeatMap heatMap,
			int firstSampleIndex, int lastSampleIndex) {

		
		// TODO: we need to do re-sorting here, this has to be called on the fly, and 
		// TODO: Is this really necessary?
		heatMap.resetView();
		ContentVirtualArray va = new ContentVirtualArray();

		for (int i = firstSampleIndex; i <= lastSampleIndex; i++) {
			if (i >= contentVA.size())
				break;
			va.append(contentVA.get(i));

		}

		heatMap.setContentVA(va);
		heatMap.useFishEye(false);
	}

	public void calculateDrawingParameters() {
		calculateHeatMapPositions();

		int numTotalSamples = 0;

		for (GroupInfo groupInfo : selectedGroups) {
			int numSamplesInHeatMap = groupInfo.getGroup().getNrElements();
			numTotalSamples += numSamplesInHeatMap;
		}

		for (GroupInfo groupInfo : selectedGroups) {

			GLHeatMap heatMap = hashHeatMaps.get(groupInfo.getGroupIndex());
			if (heatMap == null)
				continue;
			int numSamplesInHeatMap = groupInfo.getGroup().getNrElements();
			float heatMapHeight = layout
					.getDetailHeatMapHeight(numSamplesInHeatMap,
							numTotalSamples, selectedGroups.size());
			Vec3f heatMapPosition = hashHeatMapPositions.get(groupInfo
					.getGroupIndex());

			heatMap.getViewFrustum().setLeft(heatMapPosition.x());
			heatMap.getViewFrustum().setBottom(heatMapPosition.y());
			heatMap.getViewFrustum().setRight(
					heatMapPosition.x() + layout.getDetailWidth());
			heatMap.getViewFrustum()
					.setTop(heatMapPosition.y() + heatMapHeight);

		}
	}

	public void drawLocalItems(GL gl, TextureManager textureManager,
			PickingManager pickingManager, GLMouseListener glMouseListener,
			int viewID) {

		ArrayList<IHeatMapRenderCommand> renderCommands = layout
				.getRenderCommandsOfLocalItems();

		for (IHeatMapRenderCommand renderCommand : renderCommands) {
			renderCommand.render(gl, this);
		}

		// overview.draw(gl, textureManager, pickingManager,
		// contentSelectionManager, viewID, id);

		// calculateHeatMapPositions();
		// drawVisLinksBetweenOverviewAndDetail(gl);
	}

	public void drawRemoteItems(GL gl, GLMouseListener glMouseListener,
			PickingManager pickingManager) {

		// if (useDetailView) {

		// int numTotalSamples = 0;
		//
		// for (GroupInfo groupInfo : selectedGroups) {
		// int numSamplesInHeatMap = groupInfo.getGroup().getNrElements();
		// numTotalSamples += numSamplesInHeatMap;
		// }
		//
		// calculateHeatMapPositions();
		//
		// for (GroupInfo groupInfo : selectedGroups) {
		//
		// GLHeatMap heatMap = hashHeatMaps.get(groupInfo.getGroupIndex());
		// if (heatMap == null)
		// continue;
		// int numSamplesInHeatMap = groupInfo.getGroup().getNrElements();
		// float heatMapHeight = layout
		// .getDetailHeatMapHeight(numSamplesInHeatMap,
		// numTotalSamples, selectedGroups.size());
		// Vec3f heatMapPosition = hashHeatMapPositions.get(groupInfo
		// .getGroupIndex());
		//
		// gl.glTranslatef(heatMapPosition.x(), heatMapPosition.y(),
		// heatMapPosition.z());
		// heatMap.getViewFrustum().setLeft(heatMapPosition.x());
		// heatMap.getViewFrustum().setBottom(heatMapPosition.y());
		// heatMap.getViewFrustum().setRight(
		// heatMapPosition.x() + layout.getDetailWidth());
		// heatMap.getViewFrustum()
		// .setTop(heatMapPosition.y() + heatMapHeight);
		//
		// if (isNewSelection) {
		// heatMap.setDisplayListDirty();
		// }
		// gl.glPushName(pickingManager.getPickingID(glParentView.getID(),
		// layout.getHeatMapPickingType(), groupInfo.getGroupIndex()));
		// heatMap.displayRemote(gl);
		// gl.glPopName();
		//
		// gl.glTranslatef(-heatMapPosition.x(), -heatMapPosition.y(),
		// -heatMapPosition.z());
		//
		// // ContentVirtualArray va = heatMap.getContentVA();
		// //
		// // for (int i = 0; i < va.size(); i++) {
		// // // Vec2f position = getLeftLinkPositionFromContentID(va.get(i));
		// // // GLHelperFunctions.drawPointAt(gl, position.x(), position.y(),
		// // // 1);v
		// // Vec2f position = getLeftDetailLinkPositionFromContentID(va
		// // .get(i));
		// // GLHelperFunctions
		// // .drawPointAt(gl, position.x(), position.y(), 1);
		// // }
		// }

		ArrayList<IHeatMapRenderCommand> renderCommands = layout
				.getRenderCommandsOfRemoteItems();

		for (IHeatMapRenderCommand renderCommand : renderCommands) {
			renderCommand.render(gl, this);
		}

		isNewSelection = false;

	}

	public void calculateHeatMapPositions() {

		hashHeatMapPositions.clear();

		int numTotalSamples = 0;
		for (GroupInfo groupInfo : selectedGroups) {
			int numSamplesInHeatMap = groupInfo.getGroup().getNrElements();
			numTotalSamples += numSamplesInHeatMap;
		}
		Vec3f detailPosition = layout.getDetailPosition();
		float currentPositionY = detailPosition.y() + layout.getDetailHeight();

		for (GroupInfo groupInfo : selectedGroups) {

			GLHeatMap heatMap = hashHeatMaps.get(groupInfo.getGroupIndex());
			if (heatMap == null)
				continue;
			int numSamplesInHeatMap = groupInfo.getGroup().getNrElements();
			float heatMapHeight = layout
					.getDetailHeatMapHeight(numSamplesInHeatMap,
							numTotalSamples, selectedGroups.size());
			hashHeatMapPositions.put(groupInfo.getGroupIndex(), new Vec3f(
					detailPosition.x(), currentPositionY - heatMapHeight,
					detailPosition.z()));
			currentPositionY -= (heatMapHeight + layout
					.getDetailHeatMapGapHeight());
		}
	}

	public boolean handleDragging(GL gl, GLMouseListener glMouseListener) {
		if (overview.handleDragging(gl, glMouseListener)) {

			ArrayList<GroupInfo> newGroups = overview.getSelectedGroups();
			if (newGroups.size() != selectedGroups.size()) {
				isNewSelection = true;
			}

			if (!isNewSelection) {
				for (int i = 0; i < newGroups.size(); i++) {
					if (newGroups.get(i).getGroupIndex() != selectedGroups.get(
							i).getGroupIndex()) {
						isNewSelection = true;
					}
				}
			}

			if (isNewSelection) {
				selectedGroups.clear();
				selectedGroups.addAll(newGroups);
				setHeatMapsInactive();
			}

			return true;
		}
		return false;
	}

	public void processEvents() {

		for (GroupInfo groupInfo : selectedGroups) {
			GLHeatMap heatMap = hashHeatMaps.get(groupInfo.getGroupIndex());
			if (heatMap != null) {
				heatMap.processEvents();
			}
		}
	}

	public void setDisplayListDirty() {

		for (GroupInfo groupInfo : selectedGroups) {
			GLHeatMap heatMap = hashHeatMaps.get(groupInfo.getGroupIndex());

			if (heatMap != null) {
				heatMap.setDisplayListDirty();
			}
		}

	}

	public Vec2f getLeftOverviewLinkPositionFromContentID(int contentID) {

		int contentIndex = contentVA.indexOf(contentID);

		if (contentVA.indexOf(contentID) == -1)
			return null;

		Vec3f overviewPosition = layout.getOverviewPosition();
		float sampleHeight = layout.getOverviewHeight() / contentVA.size();

		return new Vec2f(overviewPosition.x(), overviewPosition.y()
				+ layout.getOverviewHeight()
				- ((sampleHeight * contentIndex) + sampleHeight / 2.0f));
	}

	public Vec2f getRightOverviewLinkPositionFromContentID(int contentID) {

		int contentIndex = contentVA.indexOf(contentID);

		if (contentVA.indexOf(contentID) == -1)
			return null;

		Vec3f overviewPosition = layout.getOverviewPosition();
		float sampleHeight = layout.getOverviewHeight() / contentVA.size();

		return new Vec2f(overviewPosition.x() + layout.getTotalOverviewWidth(),
				overviewPosition.y() + layout.getOverviewHeight()
						- ((sampleHeight * contentIndex) + sampleHeight / 2.0f));
	}

	public Vec2f getRightDetailLinkPositionFromContentID(int contentID) {

		Float yCoordinate = getDetailYCoordinateByContentID(contentID);

		if (yCoordinate == null)
			return null;

		return new Vec2f(layout.getDetailPosition().x()
				+ layout.getDetailWidth(), yCoordinate);
	}

	public Vec2f getLeftDetailLinkPositionFromContentID(int contentID) {

		Float yCoordinate = getDetailYCoordinateByContentID(contentID);

		if (yCoordinate == null)
			return null;

		return new Vec2f(layout.getDetailPosition().x(), yCoordinate);
	}

	private Float getDetailYCoordinateByContentID(int contentID) {

		// For the group check we need the index in the global content VA
		GroupInfo groupInfo = getGroupInfoFromContentIndex(contentVA
				.indexOf(contentID));
		if (groupInfo == null)
			return null;

		int groupIndex = groupInfo.getGroupIndex();

		GLHeatMap heatMap = hashHeatMaps.get(groupIndex);
		if (heatMap == null)
			return null;

		int contentIndex = heatMap.getContentVA().indexOf(contentID);
		if (contentIndex == -1)
			return null;

		// calculateHeatMapPositions();

		Vec3f heatMapPosition = hashHeatMapPositions.get(groupIndex);

		int numTotalSamples = 0;
		for (GroupInfo info : selectedGroups) {
			numTotalSamples += info.getGroup().getNrElements();
		}

		int numSamplesInHeatMap = groupInfo.getGroup().getNrElements();
		float heatMapHeight = layout.getDetailHeatMapHeight(
				numSamplesInHeatMap, numTotalSamples, selectedGroups.size());

		return heatMapPosition.y()
				+ (heatMapHeight - heatMap
						.getYCoordinateByContentIndex(contentIndex));
	}

	public ArrayList<ContentVirtualArray> getContentVAsOfHeatMaps() {
		ArrayList<ContentVirtualArray> contentVAs = new ArrayList<ContentVirtualArray>();

		for (GroupInfo groupInfo : selectedGroups) {
			GLHeatMap heatMap = hashHeatMaps.get(groupInfo.getGroupIndex());
			contentVAs.add(heatMap.getContentVA());
		}

		return contentVAs;
	}

	public ArrayList<ContentSelectionManager> getContentSelectionManagersOfHeatMaps() {

		ArrayList<ContentSelectionManager> contentSelectionManagers = new ArrayList<ContentSelectionManager>();
		for (GroupInfo groupInfo : selectedGroups) {
			GLHeatMap heatMap = hashHeatMaps.get(groupInfo.getGroupIndex());
			contentSelectionManagers.add(heatMap.getContentSelectionManager());
		}

		return contentSelectionManagers;
	}

	private GroupInfo getGroupInfoFromContentIndex(int contentIndex) {
		for (GroupInfo groupInfo : selectedGroups) {
			if (contentIndex >= groupInfo.getLowerBoundIndex()
					&& contentIndex <= groupInfo.getUpperBoundIndex()) {
				return groupInfo;
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

	public void selectGroupsFromContentVAList(GL gl,
			GLMouseListener glMouseListener,
			ArrayList<ContentVirtualArray> contentVAs) {

		// TODO this is the place where we want to set the heat map content vas
		
		selectedGroups.clear();

		ContentGroupList contentGroupList = contentVA.getGroupList();

		int groupSampleStartIndex = 0;
		int groupSampleEndIndex = 0;
		int groupIndex = 0;
		boolean groupAdded = false;
		for (Group group : contentGroupList) {
			groupSampleEndIndex = groupSampleStartIndex + group.getNrElements()
					- 1;
			group.setSelectionType(SelectionType.NORMAL);
			for (ContentVirtualArray va : contentVAs) {
				for (Integer contentID : va) {
					int contentIndex = contentVA.indexOf(contentID);
					if (contentIndex == -1)
						continue;

					if (groupSampleStartIndex <= contentIndex
							&& groupSampleEndIndex >= contentIndex) {
						selectedGroups.add(new GroupInfo(group, groupIndex,
								groupSampleStartIndex));
						group.setSelectionType(SelectionType.SELECTION);
						groupAdded = true;
						break;
					}
				}
				if (groupAdded)
					break;
			}

			groupSampleStartIndex += group.getNrElements();
			groupIndex++;
			groupAdded = false;
		}

		// for (GroupInfo groupInfo : selectedGroups) {
		// if (!hashHeatMaps.containsKey(groupInfo.getGroupIndex())) {
		// GLHeatMap heatMap = createHeatMap(gl, glMouseListener);
		// setEmbeddedHeatMapData(heatMap, groupInfo.getLowerBoundIndex(),
		// groupInfo.getUpperBoundIndex());
		//
		// hashHeatMaps.put(groupInfo.getGroupIndex(), heatMap);
		// }
		// GLHeatMap heatMap = hashHeatMaps.get(groupInfo.getGroupIndex());
		// heatMap.setDisplayListDirty();
		// }
	}

	public void handleGroupSelection(SelectionType selectionType,
			int groupIndex, boolean isControlPressed) {

		if (selectionType != SelectionType.SELECTION)
			return;

		for (GroupInfo groupInfo : selectedGroups) {
			if (groupInfo.getGroupIndex() == groupIndex && isControlPressed) {

				groupInfo.getGroup().setSelectionType(SelectionType.NORMAL);
				selectedGroups.remove(groupInfo);
				if (activeHeatMapID == groupIndex)
					setHeatMapsInactive();

				isNewSelection = true;
				return;
			}
		}

		ContentGroupList contentGroupList = contentVA.getGroupList();
		ArrayList<GroupInfo> tempGroupList = new ArrayList<GroupInfo>();

		if (isControlPressed) {
			tempGroupList.addAll(selectedGroups);
		} else if (activeHeatMapID != groupIndex) {
			setHeatMapsInactive();
		}
		selectedGroups.clear();

		int groupSampleStartIndex = 0;
		// int groupSampleEndIndex = 0;
		int currentGroupIndex = 0;
		for (Group group : contentGroupList) {
			// groupSampleEndIndex = groupSampleStartIndex +
			// group.getNrElements()
			// - 1;
			if (!tempGroupList.isEmpty()
					&& tempGroupList.get(0).getGroupIndex() == currentGroupIndex) {
				selectedGroups.add(tempGroupList.get(0));
				tempGroupList.remove(0);
			} else if (currentGroupIndex == groupIndex) {
				group.setSelectionType(selectionType);
				selectedGroups.add(new GroupInfo(group, groupIndex,
						groupSampleStartIndex));

			} else {
				group.setSelectionType(SelectionType.NORMAL);
			}
			groupSampleStartIndex += group.getNrElements();
			currentGroupIndex++;
		}

		isNewSelection = true;

		glParentView.setDisplayListDirty();

		// ContentSelectionManager contentSelectionManager = heatMap
		// .getContentSelectionManager();
		//
		// contentSelectionManager.clearSelection(selectionType);
		//
		// ArrayList<Integer> groupElements = set.getContentTree()
		// .getNodeByNumber(selectedGroup.getClusterNode().getID())
		// .getLeaveIds();
		//
		// contentSelectionManager.clearSelection(selectionType);
		// contentSelectionManager.addToType(selectionType, groupElements);
		// //
		// ISelectionDelta selectionDelta = contentSelectionManager.getDelta();
		// SelectionUpdateEvent event = new SelectionUpdateEvent();
		// event.setSender(this);
		// event.setSelectionDelta((SelectionDelta) selectionDelta);
		// // event.setInfo(getShortInfoLocal());
		// GeneralManager.get().getEventPublisher().triggerEvent(event);
	}

	public boolean isNewSelection() {
		return isNewSelection;
	}

	@Override
	public void handleSelectionUpdate(ISelectionDelta selectionDelta,
			boolean scrollToSelection, String info) {

		if (selectionDelta.getIDType() == EIDType.EXPRESSION_INDEX) {
			contentSelectionManager.setDelta(selectionDelta);
			glParentView.setDisplayListDirty();
		}

	}

	@Override
	public void queueEvent(AEventListener<? extends IListenerOwner> listener,
			AEvent event) {
		glParentView.queueEvent(listener, event);

	}

	/**
	 * Register all event listeners used by the HeatMapWrapper.
	 */
	public void registerEventListeners() {

		selectionUpdateListener = new SelectionUpdateListener();
		selectionUpdateListener.setHandler(this);
		eventPublisher.addListener(SelectionUpdateEvent.class,
				selectionUpdateListener);
	}

	/**
	 * Unregister all event listeners used by the HeatMapWrapper.
	 */
	public void unregisterEventListeners() {

		if (selectionUpdateListener != null) {
			eventPublisher.removeListener(selectionUpdateListener);
			selectionUpdateListener = null;
		}
	}

	public void setHeatMapActive(int groupIndex) {
		if (activeHeatMapID == groupIndex)
			return;

		if (activeHeatMapID != -1) {
			GLHeatMap heatMap = hashHeatMaps.get(activeHeatMapID);
			ContentSelectionManager hmContentSelectionManager = heatMap
					.getContentSelectionManager();
			for (Integer elementID : heatMap.getContentVA()) {
				hmContentSelectionManager.removeFromType(
						activeHeatMapSelectionType, elementID);
			}
			SelectionUpdateEvent selectionUpdateEvent = new SelectionUpdateEvent();
			selectionUpdateEvent.setSelectionDelta(hmContentSelectionManager
					.getDelta());
			eventPublisher.triggerEvent(selectionUpdateEvent);
		}

		GLHeatMap heatMap = hashHeatMaps.get(groupIndex);
		ContentSelectionManager hmContentSelectionManager = heatMap
				.getContentSelectionManager();
		hmContentSelectionManager.addToType(activeHeatMapSelectionType, heatMap
				.getContentVA().getVirtualArray());
		activeHeatMapID = groupIndex;
		SelectionUpdateEvent selectionUpdateEvent = new SelectionUpdateEvent();
		selectionUpdateEvent.setSelectionDelta(hmContentSelectionManager
				.getDelta());
		eventPublisher.triggerEvent(selectionUpdateEvent);
	}

	public void setHeatMapsInactive() {
		if (activeHeatMapID == -1)
			return;

		GLHeatMap heatMap = hashHeatMaps.get(activeHeatMapID);
		ContentSelectionManager hmContentSelectionManager = heatMap
				.getContentSelectionManager();
		for (Integer elementID : heatMap.getContentVA()) {
			hmContentSelectionManager.removeFromType(
					activeHeatMapSelectionType, elementID);
		}

		SelectionUpdateEvent selectionUpdateEvent = new SelectionUpdateEvent();
		selectionUpdateEvent.setSelectionDelta(hmContentSelectionManager
				.getDelta());
		eventPublisher.triggerEvent(selectionUpdateEvent);

		activeHeatMapID = -1;
	}

	public AHeatMapLayout getLayout() {
		return layout;
	}

	public HeatMapOverview getOverview() {
		return overview;
	}

	public ContentSelectionManager getContentSelectionManager() {
		return contentSelectionManager;
	}

	public ContentVirtualArray getContentVA() {
		return contentVA;
	}

	public ArrayList<GroupInfo> getSelectedGroups() {
		return selectedGroups;
	}

	public GLHeatMap getHeatMap(int id) {
		return hashHeatMaps.get(id);
	}

	public Vec3f getHeatMapPosition(int id) {
		return hashHeatMapPositions.get(id);
	}
	
	public String getCaption() {
		return set.getLabel();
	}
}
