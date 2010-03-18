package org.caleydo.view.compare;

import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.media.opengl.GL;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.view.opengl.CmdCreateView;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.set.SetRelations;
import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.ContentGroupList;
import org.caleydo.core.data.selection.ContentSelectionManager;
import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.data.selection.ESelectionCommandType;
import org.caleydo.core.data.selection.Group;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.StorageVAType;
import org.caleydo.core.data.selection.StorageVirtualArray;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDeltaItem;
import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IUseCase;
import org.caleydo.core.manager.event.view.SelectionCommandEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.view.opengl.camera.EProjectionMode;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.view.compare.layout.AHeatMapLayout;
import org.caleydo.view.compare.layout.HeatMapLayoutDetailViewRight;
import org.caleydo.view.compare.rendercommand.IHeatMapRenderCommand;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.heatmap.template.ComparerDetailTemplate;

public class HeatMapWrapper {

	// private GLHeatMap heatMap;
	private HeatMapOverview overview;
	private ISet set;

	private IGeneralManager generalManager;
	private ContentVirtualArray contentVA;
	private StorageVirtualArray storageVA;
	private AHeatMapLayout layout;
	private HashMap<Integer, GLHeatMap> hashHeatMaps;
	private HashMap<Integer, Vec3f> hashHeatMapPositions;
	private HashMap<Group, GroupInfo> selectedGroups;
	private boolean isNewSelection;
	private int id;
	private int activeHeatMapID;
	private boolean useDetailView;
	private SetRelations relations;

	private AGLView glParentView;
	private GLInfoAreaManager infoAreaManager;
	private IUseCase useCase;
	private IGLRemoteRenderingView parentView;
	private EDataDomain dataDomain;

	// private SelectionUpdateListener selectionUpdateListener;
	private IEventPublisher eventPublisher;
	private ContentSelectionManager contentSelectionManager;
	private SelectionType activeHeatMapSelectionType;

	public HeatMapWrapper(int id, AHeatMapLayout layout, AGLView glParentView,
			GLInfoAreaManager infoAreaManager, IUseCase useCase,
			IGLRemoteRenderingView parentView, EDataDomain dataDomain) {

		generalManager = GeneralManager.get();
		overview = new HeatMapOverview(layout);
		hashHeatMaps = new HashMap<Integer, GLHeatMap>();
		hashHeatMapPositions = new HashMap<Integer, Vec3f>();
		selectedGroups = new HashMap<Group, GroupInfo>();

		this.layout = layout;
		this.id = id;
		this.glParentView = glParentView;
		this.infoAreaManager = infoAreaManager;
		this.useCase = useCase;
		this.parentView = parentView;
		this.dataDomain = dataDomain;

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

		if (layout instanceof HeatMapLayoutDetailViewRight)
			heatMap.setRenderTemplate(new ComparerDetailTemplate(false));
		else
			heatMap.setRenderTemplate(new ComparerDetailTemplate(true));

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

	public ISet getSet() {
		return set;
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

	public void init(GL gl, GLMouseListener glMouseListener,
			GLInfoAreaManager infoAreaManager, EDataDomain dataDomain) {

		if (set == null)
			return;

		ContentGroupList contentGroupList = contentVA.getGroupList();
		// FIXME: Can we do this? Shall we do this in some other way?
		for (GLHeatMap heatMap : hashHeatMaps.values()) {
			heatMap.destroy();
		}
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

		float totalHeatMapOverheadSize = 0;
		for (Group group : selectedGroups.keySet()) {
			GLHeatMap heatMap = hashHeatMaps.get(group.getGroupIndex());
			int numSamplesInHeatMap = heatMap.getNumberOfVisibleElements();
			numTotalSamples += numSamplesInHeatMap;
			totalHeatMapOverheadSize += heatMap.getRequiredOverheadSpacing();
		}

		// for (Group group : contentVA.getGroupList()) {
		for (Group group : selectedGroups.keySet()) {

			if (!selectedGroups.containsKey(group))
				continue;
			GLHeatMap heatMap = hashHeatMaps.get(group.getGroupIndex());
			if (heatMap == null)
				continue;
			int numSamplesInHeatMap = group.getNrElements();
			float heatMapHeight = layout.getDetailHeatMapHeight(
					numSamplesInHeatMap, numTotalSamples,
					selectedGroups.size(),
					heatMap.getRequiredOverheadSpacing(),
					totalHeatMapOverheadSize);
			Vec3f heatMapPosition = hashHeatMapPositions.get(group
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

		// Vec3f position = layout.getPosition();
		// GLHelperFunctions.drawPointAt(gl, position.x(), position.y(), 1);
		//
		// GLHelperFunctions.drawPointAt(gl, position.x() + layout.getWidth(),
		// position.y(), 1);

	}

	public void calculateHeatMapPositions() {

		hashHeatMapPositions.clear();

		int numTotalSamples = 0;
		float totalHeatMapOverheadSpacing = 0;
		for (Group group : selectedGroups.keySet()) {
			GLHeatMap heatMap = hashHeatMaps.get(group.getGroupIndex());
			int numSamplesInHeatMap = heatMap.getNumberOfVisibleElements();
			numTotalSamples += numSamplesInHeatMap;
			totalHeatMapOverheadSpacing += heatMap.getRequiredOverheadSpacing();
		}
		Vec3f detailPosition = layout.getDetailPosition();
		float currentPositionY = detailPosition.y() + layout.getDetailHeight();

		for (Group group : contentVA.getGroupList()) {

			if (!selectedGroups.containsKey(group))
				continue;
			GLHeatMap heatMap = hashHeatMaps.get(group.getGroupIndex());
			if (heatMap == null)
				continue;
			int numSamplesInHeatMap = heatMap.getNumberOfVisibleElements();
			float heatMapHeight = layout.getDetailHeatMapHeight(
					numSamplesInHeatMap, numTotalSamples,
					selectedGroups.size(),
					heatMap.getRequiredOverheadSpacing(),
					totalHeatMapOverheadSpacing);
			hashHeatMapPositions.put(group.getGroupIndex(), new Vec3f(
					detailPosition.x(), currentPositionY - heatMapHeight,
					detailPosition.z()));
			currentPositionY -= (heatMapHeight + layout
					.getDetailHeatMapGapHeight());
		}
	}

	public boolean handleDragging(GL gl, GLMouseListener glMouseListener) {
		if (overview.handleDragging(gl, glMouseListener)) {

			HashMap<Group, GroupInfo> newGroups = overview.getSelectedGroups();

			// first check the obvious = if we have changes here we don't need
			// to check in detail
			if (newGroups.size() != selectedGroups.size()) {
				isNewSelection = true;
			}
			// now check in detail
			if (!isNewSelection) {
				for (Group newGroup : newGroups.keySet()) {
					if (!selectedGroups.containsKey(newGroup)) {
						isNewSelection = true;
						break;
					}
				}
			}

			if (isNewSelection) {
				clearDeselected();
				selectedGroups.clear();
				selectedGroups.putAll(newGroups);
				setHeatMapsInactive();
			}

			return true;
		}
		return false;
	}

	public void processEvents() {

		for (Group group : selectedGroups.keySet()) {
			GLHeatMap heatMap = hashHeatMaps.get(group.getGroupIndex());
			if (heatMap != null) {
				heatMap.processEvents();
			}
		}
	}

	public void setDisplayListDirty() {

		for (Group group : selectedGroups.keySet()) {
			GLHeatMap heatMap = hashHeatMaps.get(group.getGroupIndex());

			if (heatMap != null) {
				heatMap.setDisplayListDirty();
			}
		}

	}

	public Vec2f getLeftOverviewLinkPositionFromIndex(int contentIndex) {

		Vec3f overviewPosition = layout.getOverviewHeatMapPosition();
		float sampleHeight = layout.getOverviewHeight() / contentVA.size();

		return new Vec2f(overviewPosition.x(), overviewPosition.y()
				+ layout.getOverviewHeight()
				- ((sampleHeight * contentIndex) + sampleHeight / 2.0f));
	}

	public Vec2f getLeftOverviewLinkPositionFromContentID(int contentID) {

		int contentIndex = contentVA.indexOf(contentID);

		if (contentVA.indexOf(contentID) == -1)
			return null;

		return getLeftOverviewLinkPositionFromIndex(contentIndex);
	}

	public Vec2f getRightOverviewLinkPositionFromContentIndex(int contentIndex) {

		Vec3f overviewPosition = layout.getOverviewHeatMapPosition();
		float sampleHeight = layout.getOverviewHeight() / contentVA.size();

		return new Vec2f(overviewPosition.x()
				+ layout.getOverviewHeatmapWidth(), overviewPosition.y()
				+ layout.getOverviewHeight()
				- ((sampleHeight * contentIndex) + sampleHeight / 2.0f));
	}

	public Vec2f getRightOverviewLinkPositionFromContentID(int contentID) {

		int contentIndex = contentVA.indexOf(contentID);

		if (contentVA.indexOf(contentID) == -1)
			return null;

		return getRightOverviewLinkPositionFromContentIndex(contentIndex);
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
		Group group = getGroupFromContentIndex(contentVA.indexOf(contentID));
		if (group == null)
			return null;

		int groupIndex = group.getGroupIndex();

		GLHeatMap heatMap = hashHeatMaps.get(groupIndex);
		if (heatMap == null)
			return null;

		int contentIndex = heatMap.getContentVA().indexOf(contentID);
		if (contentIndex == -1)
			return null;

		// calculateHeatMapPositions();

		Vec3f heatMapPosition = hashHeatMapPositions.get(groupIndex);

		int numTotalSamples = 0;
		float totalHeatMapOverheadSpacing = 0;
		for (Group tempGroup : selectedGroups.keySet()) {
			GLHeatMap tempHeatMap = hashHeatMaps.get(tempGroup.getGroupIndex());
			numTotalSamples += tempHeatMap.getNumberOfVisibleElements();
			totalHeatMapOverheadSpacing += tempHeatMap
					.getRequiredOverheadSpacing();
		}

		float heatMapHeight = layout.getDetailHeatMapHeight(heatMap
				.getNumberOfVisibleElements(), numTotalSamples, selectedGroups
				.size(), heatMap.getRequiredOverheadSpacing(),
				totalHeatMapOverheadSpacing);

		Float elementInHMPosition = heatMap
				.getYCoordinateByContentIndex(contentIndex);
		if (elementInHMPosition == null)
			return null;
		else
			return heatMapPosition.y() + (heatMapHeight - elementInHMPosition);
	}

	public ArrayList<ContentVirtualArray> getContentVAsOfHeatMaps() {
		ArrayList<ContentVirtualArray> contentVAs = new ArrayList<ContentVirtualArray>();

		for (Group group : selectedGroups.keySet()) {
			GLHeatMap heatMap = hashHeatMaps.get(group.getGroupIndex());
			contentVAs.add(heatMap.getContentVA());
		}

		return contentVAs;
	}

	public ArrayList<GLHeatMap> getHeatMaps() {
		
		ArrayList<GLHeatMap> heatMaps = new ArrayList<GLHeatMap>();

		for (Group group : selectedGroups.keySet()) {
			GLHeatMap heatMap = hashHeatMaps.get(group.getGroupIndex());
			heatMaps.add(heatMap);
		}
		return heatMaps;
	}
	
	public ArrayList<ContentSelectionManager> getContentSelectionManagersOfHeatMaps() {

		ArrayList<ContentSelectionManager> contentSelectionManagers = new ArrayList<ContentSelectionManager>();
		for (Group group : selectedGroups.keySet()) {
			GLHeatMap heatMap = hashHeatMaps.get(group.getGroupIndex());
			contentSelectionManagers.add(heatMap.getContentSelectionManager());
		}

		return contentSelectionManagers;
	}

	public Group getGroupFromContentIndex(int contentIndex) {
		for (Group group : selectedGroups.keySet()) {
			if (contentIndex >= group.getStartIndex()
					&& contentIndex <= group.getEndIndex()) {
				return group;
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

	/**
	 * 
	 * @param gl
	 * @param glMouseListener
	 * @param foreignContentVAs
	 *            The source virtual arrays of which we search the match
	 */
	// public void selectGroupsFromContentVAList(GL gl,
	// GLMouseListener glMouseListener,
	// ArrayList<ContentVirtualArray> contentVAs) {
	//
	// // TODO this is the place where we want to set the heat map content vas
	//
	// selectedGroups.clear();
	//
	// ContentGroupList contentGroupList = contentVA.getGroupList();
	//
	// int groupSampleStartIndex = 0;
	// int groupSampleEndIndex = 0;
	// int groupIndex = 0;
	// boolean groupAdded = false;
	// // for all groups in the current wrapper
	// for (Group group : contentGroupList) {
	// groupSampleEndIndex = groupSampleStartIndex + group.getNrElements()
	// - 1;
	// group.setSelectionType(SelectionType.NORMAL);
	// // for all external clusters
	// for (ContentVirtualArray va : contentVAs) {
	// // for every element in this cluster
	// for (Integer contentID : va) {
	// int contentIndex = contentVA.indexOf(contentID);
	// if (contentIndex == -1)
	// continue;
	//
	// // we check whether the element is in this group
	// if (groupSampleStartIndex <= contentIndex
	// && groupSampleEndIndex >= contentIndex) {
	// selectedGroups.put(group, new GroupInfo(group,
	// groupIndex, groupSampleStartIndex));
	// group.setSelectionType(SelectionType.SELECTION);
	// groupAdded = true;
	// break;
	// }
	// }
	// if (groupAdded)
	// break;
	// }
	//
	// groupSampleStartIndex += group.getNrElements();
	// groupIndex++;
	// groupAdded = false;
	// }
	//
	// // for (GroupInfo groupInfo : selectedGroups) {
	// // if (!hashHeatMaps.containsKey(groupInfo.getGroupIndex())) {
	// // GLHeatMap heatMap = createHeatMap(gl, glMouseListener);
	// // setEmbeddedHeatMapData(heatMap, groupInfo.getLowerBoundIndex(),
	// // groupInfo.getUpperBoundIndex());
	// //
	// // hashHeatMaps.put(groupInfo.getGroupIndex(), heatMap);
	// // }
	// // GLHeatMap heatMap = hashHeatMaps.get(groupInfo.getGroupIndex());
	// // heatMap.setDisplayListDirty();
	// // }
	// }

	/**
	 * <p>
	 * This method chooses which groups (and therefore heat maps) are to be
	 * rendered at the passive side. It does so based on a virtualArray
	 * containing all elements to be rendered.
	 * </p>
	 * <p>
	 * This is called in the passive heat map.
	 * </p>
	 * <p>
	 * It also re-sorts the passive group's virtual arrays to minimize crossings
	 * and sets elements that are in a passive group but not in the supplied
	 * virtual array to {@link GLHeatMap#SELECTION_HIDDEN} so that they can be
	 * hidden on demand.
	 */
	public void selectGroupsFromContentVAList(
			HashMap<Integer, Integer> relationMap,
			ArrayList<ContentVirtualArray> foreignContentVAs) {
		ContentGroupList groupList = contentVA.getGroupList();
		// FIXME we shouldn't do that here
		groupList.updateGroupInfo();

		selectedGroups.clear();
		for (ContentVirtualArray foreignVa : foreignContentVAs) {

			for (Integer contentID : foreignVa) {
				int vaIndex = contentVA.indexOf(contentID);
				Group selectedGroup = groupList.getGroupOfVAIndex(vaIndex);

				GroupInfo currentInfo;

				if (!selectedGroups.containsKey(selectedGroup)) {
					currentInfo = new GroupInfo();
					selectedGroups.put(selectedGroup, currentInfo);
				} else {
					currentInfo = selectedGroups.get(selectedGroup);

				}
				GLHeatMap heatMap = hashHeatMaps.get(selectedGroup
						.getGroupIndex());
				ContentVirtualArray heatMapVA = heatMap.getContentVA();
				int index = heatMapVA.indexOf(contentID);
				if (index >= 0)
					heatMapVA.move(index, currentInfo.getContainedNrGenes());
				else
					System.out.println("Problem");

				currentInfo.increaseContainedNumberOfGenesByOne();

			}
		}

		// here we Hide those that are not part of the other va, and re-sort the
		// source va
		for (int groupIndex = groupList.size() - 1; groupIndex >= 0; groupIndex--) {
			Group group = groupList.get(groupIndex);
			if (selectedGroups.containsKey(group)) {
				GLHeatMap heatMap = hashHeatMaps.get(group.getGroupIndex());

				int nrGenes = selectedGroups.get(group).getContainedNrGenes();

				ContentVirtualArray contentVA = heatMap.getContentVA();

				// re-sort the source virtual array to group genes according to
				// vas in the destination (here)
				for (ContentVirtualArray foreignVA : foreignContentVAs) {
					Integer foreignContentLastOrdererIndex = 0;
					for (int contentIndex = 0; contentIndex < nrGenes; contentIndex++) {
						Integer contentID = contentVA.get(contentIndex);
						int foreignIndex = foreignVA.indexOf(contentID);
						if (foreignIndex != -1) {
							foreignVA.move(foreignIndex,
									foreignContentLastOrdererIndex++);
						}
					}
				}

				// hide the elements not in the source vas
				SelectionDelta contentSelectionDelta = new SelectionDelta(
						EIDType.EXPRESSION_INDEX);

				for (int contentIndex = nrGenes; contentIndex < contentVA
						.size(); contentIndex++) {
					SelectionDeltaItem item = new SelectionDeltaItem();
					item.setPrimaryID(contentVA.get(contentIndex));
					item.setSelectionType(GLHeatMap.SELECTION_HIDDEN);
					contentSelectionDelta.add(item);
				}
				SelectionUpdateEvent event = new SelectionUpdateEvent();
				event.setSender(this);
				event.setSelectionDelta(contentSelectionDelta);

				eventPublisher.triggerEvent(event);
			}
		}
	}

	public void handleGroupSelection(SelectionType selectionType,
			int groupIndex, boolean isControlPressed) {

		if (selectionType != SelectionType.SELECTION)
			return;

		clearDeselected();

		for (Group group : selectedGroups.keySet()) {
			if (group.getGroupIndex() == groupIndex && isControlPressed) {

				group.setSelectionType(SelectionType.NORMAL);
				selectedGroups.remove(group);
				if (activeHeatMapID == groupIndex)
					setHeatMapsInactive();

				isNewSelection = true;
				return;
			}
		}

		ContentGroupList contentGroupList = contentVA.getGroupList();
		contentGroupList.updateGroupInfo();

		if (!isControlPressed) {
			for (Group group : selectedGroups.keySet())
				group.setSelectionType(SelectionType.NORMAL);

			selectedGroups.clear();
		}

		Group selectedGroup = contentGroupList.get(groupIndex);
		selectedGroups.put(selectedGroup, new GroupInfo());
		selectedGroup.setSelectionType(SelectionType.SELECTION);

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

	private void clearDeselected() {
		SelectionCommand selectionCommand = new SelectionCommand(
				ESelectionCommandType.CLEAR, GLHeatMap.SELECTION_HIDDEN);

		SelectionCommandEvent event = new SelectionCommandEvent();
		event.setSelectionCommand(selectionCommand);
		event.setCategory(EIDCategory.GENE);
		event.setSender(this);
		eventPublisher.triggerEvent(event);
	}

	public boolean isNewSelection() {
		return isNewSelection;
	}

	public void handleSelectionUpdate(ISelectionDelta selectionDelta,
			boolean scrollToSelection, String info) {

		if (selectionDelta.getIDType() == EIDType.EXPRESSION_INDEX) {
			contentSelectionManager.setDelta(selectionDelta);
			glParentView.setDisplayListDirty();
		}

	}

	//
	// @Override
	// public void queueEvent(AEventListener<? extends IListenerOwner> listener,
	// AEvent event) {
	// glParentView.queueEvent(listener, event);
	//
	// }

	// /**
	// * Register all event listeners used by the HeatMapWrapper.
	// */
	// public void registerEventListeners() {
	//
	// selectionUpdateListener = new SelectionUpdateListener();
	// selectionUpdateListener.setHandler(this);
	// eventPublisher.addListener(SelectionUpdateEvent.class,
	// selectionUpdateListener);
	// }
	//
	// /**
	// * Unregister all event listeners used by the HeatMapWrapper.
	// */
	// public void unregisterEventListeners() {
	//
	// if (selectionUpdateListener != null) {
	// eventPublisher.removeListener(selectionUpdateListener);
	// selectionUpdateListener = null;
	// }
	// }

	public void setHeatMapActive(int groupIndex) {
		if (activeHeatMapID == groupIndex)
			return;

		int previouslyActiveHeatMapID = activeHeatMapID;
		activeHeatMapID = groupIndex;
		// FIXME FIXME!!!!! we need to set heat maps inactive as well, this is
		// just for now:
		for (GLHeatMap heatMap : hashHeatMaps.values()) {
			heatMap.setActive(false);
		}

		if (previouslyActiveHeatMapID != -1) {
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
		heatMap.setActive(true);
		ContentSelectionManager hmContentSelectionManager = heatMap
				.getContentSelectionManager();
		hmContentSelectionManager.addToType(activeHeatMapSelectionType, heatMap
				.getContentVA().getVirtualArray());

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

	public HashMap<Group, GroupInfo> getSelectedGroups() {
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

	public void setRelations(SetRelations relations) {
		this.relations = relations;
	}

	public SelectionType getActiveHeatMapSelectionType() {
		return activeHeatMapSelectionType;
	}

	public void setActiveHeatMapSelectionType(
			SelectionType activeHeatMapSelectionType) {
		this.activeHeatMapSelectionType = activeHeatMapSelectionType;
	}

}
