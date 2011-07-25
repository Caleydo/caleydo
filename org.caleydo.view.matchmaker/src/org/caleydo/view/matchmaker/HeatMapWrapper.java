package org.caleydo.view.matchmaker;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.id.IDCategory;
import org.caleydo.core.data.selection.RecordSelectionManager;
import org.caleydo.core.data.selection.ESelectionCommandType;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.SelectionTypeEvent;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDeltaItem;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.RecordGroupList;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.ATableBasedDataDomain;
import org.caleydo.core.manager.event.EventPublisher;
import org.caleydo.core.manager.event.view.SelectionCommandEvent;
import org.caleydo.core.manager.event.view.dimensionbased.SelectionUpdateEvent;
import org.caleydo.core.manager.picking.PickingMode;
import org.caleydo.core.manager.picking.PickingType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.view.opengl.camera.ECameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.DetailLevel;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.ColorUtil;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.view.heatmap.dendrogram.GLDendrogram;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.heatmap.template.MatchmakerDetailTemplate;
import org.caleydo.view.matchmaker.layout.AHeatMapLayout;
import org.caleydo.view.matchmaker.layout.HeatMapLayoutDetailViewRight;
import org.caleydo.view.matchmaker.rendercommand.IHeatMapRenderCommand;
import org.caleydo.view.matchmaker.state.ACompareViewState;

/**
 * 
 * @author Christian Partl
 * @author Alexander Lex
 * 
 */
public class HeatMapWrapper {

	private static final String SELECTION_TYPE_NAME = "Compare Type ";

	private static int selectionTypeNumber = 0;

	private HeatMapOverview overview;
	private DataTable dataTable;

	private String recordVAType = DataTable.RECORD;
	private RecordVirtualArray recordVA;
	private AHeatMapLayout layout;
	private HashMap<Integer, GLHeatMap> hashHeatMaps;
	private HashMap<Group, Boolean> selectedGroups;
	private GLDendrogram<RecordGroupList> dendrogram;
	private boolean isNewSelection;
	private boolean isInitialized;
	private boolean isNewSet;
	private int id;
	private int activeHeatMapID;

	private AGLView glParentView;
	private ACompareViewState state;
	// private GLInfoAreaManager infoAreaManager;
	private ATableBasedDataDomain dataDomain;
	private IGLRemoteRenderingView parentView;

	// private SelectionUpdateListener selectionUpdateListener;
	private EventPublisher eventPublisher;
	private RecordSelectionManager contentSelectionManager;

	private boolean useSorting = true;
	// private boolean useZoom = false;
	private boolean useFishEye = false;

	public HeatMapWrapper(int id, AHeatMapLayout layout, AGLView glParentView,
			GLInfoAreaManager infoAreaManager, ATableBasedDataDomain dataDomain,
			IGLRemoteRenderingView parentView, ACompareViewState state) {

		overview = new HeatMapOverview(layout);
		hashHeatMaps = new HashMap<Integer, GLHeatMap>();
		// hashHeatMapPositions = new HashMap<Integer, Vec3f>();
		selectedGroups = new HashMap<Group, Boolean>();

		this.layout = layout;
		this.id = id;
		this.glParentView = glParentView;
		this.dataDomain = dataDomain;
		this.parentView = parentView;
		this.state = state;

		isNewSelection = false;
		eventPublisher = GeneralManager.get().getEventPublisher();
		contentSelectionManager = dataDomain.getRecordSelectionManager();
		// contentSelectionManager.addTypeToDeltaBlacklist(activeHeatMapSelectionType);
		activeHeatMapID = -1;

	}

	private GLHeatMap createHeatMap(GL2 gl, GLMouseListener glMouseListener) {

		ViewFrustum viewFrustum = new ViewFrustum(ECameraProjectionMode.ORTHOGRAPHIC, 0,
				50, 0, 50, -20, 20);

		GLHeatMap heatMap = new GLHeatMap(parentView.getParentGLCanvas(),
				parentView.getParentComposite(), viewFrustum);
		heatMap.setRemoteRenderingGLView(parentView);
		heatMap.setDataDomain(dataDomain);
		heatMap.setRecordVAType(GLHeatMap.CONTENT_EMBEDDED_VA);

		if (layout instanceof HeatMapLayoutDetailViewRight)
			heatMap.setRenderTemplate(new MatchmakerDetailTemplate(heatMap, false));
		else
			heatMap.setRenderTemplate(new MatchmakerDetailTemplate(heatMap, true));

		heatMap.setDataTable(dataTable);
		heatMap.initData();
		heatMap.setDetailLevel(DetailLevel.MEDIUM);
		heatMap.initRemote(gl, glParentView, glMouseListener);
		heatMap.setSendClearSelectionsEvent(true);
		heatMap.useFishEye(false);

		return heatMap;
	}

	private void createDendrogram(GL2 gl, GLMouseListener glMouseListener) {

		ViewFrustum viewFrustum = new ViewFrustum(ECameraProjectionMode.ORTHOGRAPHIC, 0,
				50, 0, 50, -20, 20);

		dendrogram = new GLDendrogram<RecordGroupList>(glParentView.getParentGLCanvas(),
				glParentView.getParentComposite(), viewFrustum, true);
		dendrogram.setDataDomain(dataDomain);
		dendrogram.setRemoteRenderingGLView(parentView);
		dendrogram.setRecordVAType(recordVAType);
		dendrogram.initData();
		dendrogram.setRenderUntilCut(false);
		dendrogram.initRemote(gl, glParentView, glMouseListener);
	}

	public DataTable getDataTable() {
		return dataTable;
	}

	public void setDataTable(DataTable dataTable) {
		this.dataTable = dataTable;
		recordVA = dataTable.getRecordData(DataTable.RECORD).getRecordVA();

		// FIXME: Can we do this? Shall we do this in some other way? Do it also
		// with dendrogram.
		for (GLHeatMap heatMap : hashHeatMaps.values()) {
			heatMap.destroy();
		}
		hashHeatMaps.clear();
		selectedGroups.clear();
		contentSelectionManager = dataDomain.getRecordSelectionManager();
		// contentSelectionManager.clearSelections();
		contentSelectionManager.setVA(recordVA);
		RecordGroupList contentGroupList = recordVA.getGroupList();

		// FIXME
		try {
			contentGroupList.updateGroupInfo();
		} catch (Exception e) {
			System.out
					.println("NPE when trying to update group info in heatmap wrapper!!");
		}

		for (Group group : contentGroupList) {
			group.setSelectionType(SelectionType.NORMAL);
		}

		// heatMap.useFishEye(false);
		// heatMap.setDisplayListDirty();

		overview.setDataTable(dataTable);
		isNewSet = true;
		isInitialized = false;
		activeHeatMapID = -1;
	}

	public void init(GL2 gl, GLMouseListener glMouseListener,
			GLInfoAreaManager infoAreaManager) {

		if (dataTable == null)
			return;

		RecordGroupList contentGroupList = recordVA.getGroupList();
		hashHeatMaps.clear();
		// selectedGroups.clear();

		if (contentGroupList == null)
			return;

		int groupSampleStartIndex = 0;
		int groupSampleEndIndex = 0;
		int groupIndex = 0;
		for (Group group : contentGroupList) {
			groupSampleEndIndex = groupSampleStartIndex + group.getSize() - 1;
			GLHeatMap heatMap = createHeatMap(gl, glMouseListener);
			setEmbeddedHeatMapData(heatMap, groupSampleStartIndex, groupSampleEndIndex);

			hashHeatMaps.put(groupIndex, heatMap);
			groupSampleStartIndex += group.getSize();
			groupIndex++;
		}

		// createDendrogram(gl, glMouseListener);

		if (isNewSet) {
			createDendrogram(gl, glMouseListener);
		}
		isInitialized = true;
		isNewSet = false;
		clearDeselected();
	}

	private void setEmbeddedHeatMapData(GLHeatMap heatMap, int firstSampleIndex,
			int lastSampleIndex) {

		// TODO: Is this really necessary?
		heatMap.resetView();
		RecordVirtualArray va = new RecordVirtualArray();

		for (int i = firstSampleIndex; i <= lastSampleIndex; i++) {
			if (i >= recordVA.size())
				break;
			va.append(recordVA.get(i));

		}

		heatMap.setRecordVA(va);
		heatMap.useFishEye(false);
	}

	public void calculateDrawingParameters() {
		layout.calculateDrawingParameters();

		for (Group group : selectedGroups.keySet()) {

			if (!selectedGroups.containsKey(group))
				continue;
			GLHeatMap heatMap = hashHeatMaps.get(group.getGroupID());
			if (heatMap == null)
				continue;

			float heatMapHeight = layout.getDetailHeatMapHeight(group.getGroupID());
			Vec3f heatMapPosition = layout.getDetailHeatMapPosition(group.getGroupID());

			heatMap.getViewFrustum().setLeft(heatMapPosition.x());
			heatMap.getViewFrustum().setBottom(heatMapPosition.y());
			heatMap.getViewFrustum().setRight(
					heatMapPosition.x() + layout.getDetailWidth());
			heatMap.getViewFrustum().setTop(heatMapPosition.y() + heatMapHeight);

		}
	}

	public void drawLocalItems(GL2 gl, TextureManager textureManager,
			PickingManager pickingManager, GLMouseListener glMouseListener, int viewID) {

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

	public void drawRemoteItems(GL2 gl, GLMouseListener glMouseListener,
			PickingManager pickingManager) {

		ArrayList<IHeatMapRenderCommand> renderCommands = layout
				.getRenderCommandsOfRemoteItems();

		for (IHeatMapRenderCommand renderCommand : renderCommands) {
			renderCommand.render(gl, this);
		}

		isNewSelection = false;

		// Vec3f position = layout.getDendrogramPosition();
		// GLHelperFunctions.drawPointAt(gl, position.x(), position.y(), 1);
		//
		// GLHelperFunctions.drawPointAt(gl, position.x()
		// + layout.getDendrogramWidth(), position.y(), 1);

	}

	public boolean handleDragging(GL2 gl, GLMouseListener glMouseListener) {
		if (overview.handleDragging(gl, glMouseListener)) {

			HashMap<Group, Boolean> newGroups = overview.getSelectedGroups();

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

		for (GLHeatMap heatMap : hashHeatMaps.values()) {
			heatMap.processEvents();
		}
	}

	public void setDisplayListDirty() {

		for (Group group : selectedGroups.keySet()) {
			GLHeatMap heatMap = hashHeatMaps.get(group.getGroupID());

			if (heatMap != null) {
				heatMap.setDisplayListDirty();
			}
		}

		dendrogram.setRedrawDendrogram();

	}

	public float[] getLeftOverviewLinkPositionFromContentIndex(int recordIndex) {

		Vec3f overviewPosition = layout.getOverviewHeatMapPosition();

		return new float[] { overviewPosition.x(),
				layout.getOverviewHeatMapSamplePositionY(recordIndex), 0 };
	}

	public float[] getLeftOverviewLinkPositionFromContentID(int recordID) {

		int recordIndex = recordVA.indexOf(recordID);

		if (recordVA.indexOf(recordID) == -1)
			return null;

		return getLeftOverviewLinkPositionFromContentIndex(recordIndex);
	}

	public float[] getRightOverviewLinkPositionFromContentIndex(int recordIndex) {

		Vec3f overviewPosition = layout.getOverviewHeatMapPosition();

		return new float[] { overviewPosition.x() + layout.getOverviewHeatMapWidth(),
				layout.getOverviewHeatMapSamplePositionY(recordIndex), 0 };
	}

	public float[] getRightOverviewLinkPositionFromContentID(int recordID) {

		int recordIndex = recordVA.indexOf(recordID);

		if (recordVA.indexOf(recordID) == -1)
			return null;

		return getRightOverviewLinkPositionFromContentIndex(recordIndex);
	}

	public float[] getRightDetailLinkPositionFromContentID(int recordID) {

		Float yCoordinate = getDetailYCoordinateByContentID(recordID);

		if (yCoordinate == null)
			return null;

		return new float[] { layout.getDetailPosition().x() + layout.getDetailWidth(),
				yCoordinate, 0 };
	}

	public float[] getLeftDetailLinkPositionFromContentID(int recordID) {

		Float yCoordinate = getDetailYCoordinateByContentID(recordID);

		if (yCoordinate == null)
			return null;

		return new float[] { layout.getDetailPosition().x(), yCoordinate, 0 };
	}

	private Float getDetailYCoordinateByContentID(int recordID) {

		// For the group check we need the index in the global content VA
		Group group = getSelectedGroupFromContentIndex(recordVA.indexOf(recordID));
		if (group == null)
			return null;

		int groupIndex = group.getGroupID();

		GLHeatMap heatMap = hashHeatMaps.get(groupIndex);
		if (heatMap == null)
			return null;

		int recordIndex = heatMap.getRecordVA().indexOf(recordID);
		if (recordIndex == -1)
			return null;

		// calculateHeatMapPositions();

		Vec3f heatMapPosition = layout.getDetailHeatMapPosition(group.getGroupID());
		// hashHeatMapPositions.get(groupIndex);

		int numTotalSamples = 0;
		float totalHeatMapOverheadSpacing = 0;
		for (Group tempGroup : selectedGroups.keySet()) {
			GLHeatMap tempHeatMap = hashHeatMaps.get(tempGroup.getGroupID());
			numTotalSamples += tempHeatMap.getNumberOfVisibleElements();
			totalHeatMapOverheadSpacing += tempHeatMap.getRequiredOverheadSpacing();
		}

		float heatMapHeight = layout.getDetailHeatMapHeight(groupIndex);

		Float elementInHMPosition = heatMap.getYCoordinateByContentIndex(recordIndex);

		if (elementInHMPosition == null)
			return null;
		else {
			return heatMapPosition.y() + (heatMapHeight - elementInHMPosition);
		}
	}

	/**
	 * Creates and returns the content VAs of all groups.
	 * 
	 * @param considerSelectedGroups
	 *            If true only VAs are added where the corresponding group is
	 *            selected.
	 * @return
	 */
	public ArrayList<RecordVirtualArray> getRecordVAsOfHeatMaps(
			boolean considerSelectedGroups) {
		ArrayList<RecordVirtualArray> recordVAs = new ArrayList<RecordVirtualArray>();

		RecordGroupList groupList = recordVA.getGroupList();
		for (int groupIndex = 0; groupIndex < groupList.size(); groupIndex++) {
			Group group = groupList.get(groupIndex);

			if (!considerSelectedGroups || selectedGroups.containsKey(group)) {
				GLHeatMap heatMap = hashHeatMaps.get(group.getGroupID());
				recordVAs.add(heatMap.getRecordVA());
			}
		}
		return recordVAs;
	}

	public Collection<GLHeatMap> getHeatMaps(boolean considerSelections) {

		if (considerSelections) {
			ArrayList<GLHeatMap> heatMaps = new ArrayList<GLHeatMap>();
			for (Group group : selectedGroups.keySet()) {
				GLHeatMap heatMap = hashHeatMaps.get(group.getGroupID());
				heatMaps.add(heatMap);
			}
			return heatMaps;

		}

		return hashHeatMaps.values();
	}

	// public Collection<GLHeatMap> getHeatMaps() {
	//
	// ArrayList<GLHeatMap> heatMaps = new ArrayList<GLHeatMap>();
	// for (Group group : selectedGroups.keySet()) {
	// GLHeatMap heatMap = hashHeatMaps.get(group.getGroupIndex());
	// heatMaps.add(heatMap);
	// }
	// return hashHeatMaps.values();
	// }

	public ArrayList<RecordSelectionManager> getContentSelectionManagersOfHeatMaps() {

		ArrayList<RecordSelectionManager> contentSelectionManagers = new ArrayList<RecordSelectionManager>();
		for (Group group : selectedGroups.keySet()) {
			GLHeatMap heatMap = hashHeatMaps.get(group.getGroupID());
			contentSelectionManagers.add(heatMap.getContentSelectionManager());
		}

		return contentSelectionManagers;
	}

	public Group getSelectedGroupFromContentIndex(int recordIndex) {
		for (Group group : selectedGroups.keySet()) {
			if (recordIndex >= group.getStartIndex()
					&& recordIndex <= group.getEndIndex()) {
				return group;
			}
		}
		return null;
	}

	public int getID() {
		return id;
	}

	public void handleOverviewSliderSelection(PickingType pickingType,
			PickingMode pickingMode) {
		overview.handleSliderSelection(pickingType, pickingMode);
	}

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
	public void choosePassiveHeatMaps(ArrayList<RecordVirtualArray> foreignRecordVAs,
			boolean hideVisible, boolean considerSelectedGroups, boolean selectGroups) {

		RecordGroupList groupList = recordVA.getGroupList();
		// FIXME this isn't to nice
		for (Group group : groupList.getGroups())
			group.setSelectionType(SelectionType.NORMAL);
		// FIXME we shouldn't do that here
		groupList.updateGroupInfo();
		HashMap<Group, Boolean> tempGroups = new HashMap<Group, Boolean>();

		tempGroups.clear();
		for (RecordVirtualArray foreignVa : foreignRecordVAs) {
			for (Integer recordID : foreignVa) {
				int vaIndex = recordVA.indexOf(recordID);
				Group selectedGroup = groupList.getGroupOfVAIndex(vaIndex);
				if (!tempGroups.containsKey(selectedGroup)) {
					tempGroups.put(selectedGroup, null);
					selectedGroup.resetVisualGenesCounter();
				}

				GLHeatMap heatMap = hashHeatMaps.get(selectedGroup.getGroupID());
				RecordVirtualArray heatMapVA = heatMap.getRecordVA();
				int index = heatMapVA.indexOf(recordID);
				if (useSorting && index >= 0)
					heatMapVA.move(index, selectedGroup.getContainedNrGenes());
				else
					System.out.println("Problem");

				selectedGroup.increaseContainedNumberOfGenesByOne();

			}
		}

		if (selectGroups) {
			selectedGroups.clear();
			selectedGroups.putAll(tempGroups);
		}
		sort(foreignRecordVAs, considerSelectedGroups, hideVisible);

		// here we Hide those that are not part of the other va, and re-sort the
		// source va
		// for (int groupIndex = groupList.size() - 1; groupIndex >= 0;
		// groupIndex--) {
		// Group group = groupList.get(groupIndex);
		// if (selectedGroups.containsKey(group)) {
		// GLHeatMap heatMap = hashHeatMaps.get(group.getGroupIndex());
		//
		// int nrGenes = group.getContainedNrGenes();
		//
		// ContentVirtualArray recordVA = heatMap.getRecordVA();
		//
		// if (useSorting) {
		// // re-sort the source virtual array to group genes according
		// // to
		// // vas in the destination (here)
		// for (ContentVirtualArray foreignVA : foreignRecordVAs) {
		// Integer foreignContentLastOrdererIndex = 0;
		// for (int recordIndex = 0; recordIndex < nrGenes; recordIndex++) {
		// Integer recordID = recordVA.get(recordIndex);
		// int foreignIndex = foreignVA.indexOf(recordID);
		// if (foreignIndex != -1) {
		// foreignVA.move(foreignIndex,
		// foreignContentLastOrdererIndex++);
		// }
		// }
		// }
		//
		// // hide the elements not in the source vas
		// SelectionDelta contentSelectionDelta = new SelectionDelta(
		// EIDType.EXPRESSION_INDEX);
		//
		// for (int recordIndex = nrGenes; recordIndex < recordVA
		// .size(); recordIndex++) {
		// SelectionDeltaItem item = new SelectionDeltaItem();
		// item.setPrimaryID(recordVA.get(recordIndex));
		// item.setSelectionType(GLHeatMap.SELECTION_HIDDEN);
		// contentSelectionDelta.add(item);
		// }
		// SelectionUpdateEvent event = new SelectionUpdateEvent();
		// event.setSender(this);
		// event.setSelectionDelta(contentSelectionDelta);
		//
		// eventPublisher.triggerEvent(event);
		// }
		// }
		// }
		for (Group group : selectedGroups.keySet()) {
			GLHeatMap heatMap = hashHeatMaps.get(group.getGroupID());

			heatMap.recalculateLayout();
		}
	}

	/**
	 * Sorts the content of the virtual arrays of this heat map wrapper
	 * according to the occurences in the foreign VAs. The parameter specifies
	 * whether elements should be hidden or not. If all elements occur in the
	 * foreign VA the parameter has no effect but increased performance
	 * 
	 * @param foreignRecordVAs
	 * @param hideVisible
	 */
	public void sort(ArrayList<RecordVirtualArray> foreignRecordVAs,
			boolean hideVisible, boolean considerSelectedGroups) {
		RecordGroupList groupList = recordVA.getGroupList();

		// for (ContentVirtualArray foreignVa : foreignRecordVAs) {
		// for (Integer recordID : foreignVa) {
		// int vaIndex = recordVA.indexOf(recordID);
		// Group selectedGroup = groupList.getGroupOfVAIndex(vaIndex);
		// gr
		// }
		// }
		// here we hide those that are not part of the other va, and re-sort the
		// source va
		for (int groupIndex = groupList.size() - 1; groupIndex >= 0; groupIndex--) {
			Group group = groupList.get(groupIndex);
			if (!considerSelectedGroups || selectedGroups.containsKey(group)) {
				GLHeatMap heatMap = hashHeatMaps.get(group.getGroupID());

				int nrGenes = group.getContainedNrGenes();

				RecordVirtualArray recordVA = heatMap.getRecordVA();

				if (useSorting) {
					// re-sort the source virtual array to group genes according
					// to
					// vas in the destination (here)
					for (RecordVirtualArray foreignVA : foreignRecordVAs) {
						Integer foreignContentLastOrdererIndex = 0;
						for (int recordIndex = 0; recordIndex < nrGenes; recordIndex++) {
							Integer recordID = recordVA.get(recordIndex);
							int foreignIndex = foreignVA.indexOf(recordID);
							if (foreignIndex != -1) {
								foreignVA.move(foreignIndex,
										foreignContentLastOrdererIndex++);
							}
						}
					}

					if (hideVisible) {
						// hide the elements not in the source vas
						SelectionDelta contentSelectionDelta = new SelectionDelta(
								dataDomain.getRecordIDType());

						for (int recordIndex = nrGenes; recordIndex < recordVA.size(); recordIndex++) {
							SelectionDeltaItem item = new SelectionDeltaItem();
							item.setPrimaryID(recordVA.get(recordIndex));
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
		}
		// for (Group group : selectedGroups.keySet()) {
		// GLHeatMap heatMap = hashHeatMaps.get(group.getGroupIndex());
		//
		// heatMap.recalculateLayout();
		// }

	}

	public void handleGroupSelection(SelectionType selectionType, int groupIndex,
			boolean isControlPressed, boolean addNewSelectionType) {

		if (selectionType != SelectionType.SELECTION)
			return;

		clearDeselected();

		for (Group group : selectedGroups.keySet()) {
			if (group.getGroupID() == groupIndex && isControlPressed) {

				group.setSelectionType(SelectionType.NORMAL);
				selectedGroups.remove(group);
				if (activeHeatMapID == groupIndex)
					setHeatMapsInactive();

				isNewSelection = true;
				return;
			}
		}

		RecordGroupList contentGroupList = recordVA.getGroupList();
		contentGroupList.updateGroupInfo();

		if (!isControlPressed) {
			for (Group group : selectedGroups.keySet())
				group.setSelectionType(SelectionType.NORMAL);

			selectedGroups.clear();
		}

		Group selectedGroup = contentGroupList.get(groupIndex);
		selectedGroups.put(selectedGroup, null);
		selectedGroup.setSelectionType(SelectionType.SELECTION);

		isNewSelection = true;

		state.setHeatMapWrapperDisplayListDirty();

		// ContentSelectionManager contentSelectionManager = heatMap
		// .getContentSelectionManager();
		//
		// contentSelectionManager.clearSelection(selectionType);
		//
		// ArrayList<Integer> groupElements = dataTable.getContentTree()
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
		// FIXME: Events are too slow?
		contentSelectionManager.clearSelection(GLHeatMap.SELECTION_HIDDEN);
		SelectionCommand selectionCommand = new SelectionCommand(
				ESelectionCommandType.CLEAR, GLHeatMap.SELECTION_HIDDEN);

		SelectionCommandEvent event = new SelectionCommandEvent();
		event.setSelectionCommand(selectionCommand);
		event.dataTableIDCategory(dataDomain.getRecordIDCategory());
		event.setSender(this);
		eventPublisher.triggerEvent(event);
	}

	public boolean isNewSelection() {
		return isNewSelection;
	}

	public void handleSelectionUpdate(ISelectionDelta selectionDelta,
			boolean scrollToSelection, String info) {

		if (selectionDelta.getIDType() == dataDomain.getRecordIDType()) {
			contentSelectionManager.setDelta(selectionDelta);
			// TODO: Maybe just set selection list dirty
			if (selectedGroups.isEmpty())
				state.setHeatMapWrapperSelectionDisplayListDirty();
			else
				state.setHeatMapWrapperDisplayListDirty();
		}
	}

	public void handleReplaceRecordVA(IDCategory idCategory, String vaType) {

		recordVA = dataTable.getRecordData(vaType).getRecordVA();
	}

	public void handleClearSelections() {
		contentSelectionManager.clearSelections();
		for (Group group : selectedGroups.keySet())
			group.setSelectionType(SelectionType.NORMAL);
		selectedGroups.clear();
	}

	public void setHeatMapActive(int groupIndex, boolean addToNewSelectionType) {
		if (activeHeatMapID == groupIndex)
			return;

		// SelectionCommandEvent selectionCommandEvent = new
		// SelectionCommandEvent();
		// SelectionCommand clearSelectionCommand = new
		// SelectionCommand(ESelectionCommandType.CLEAR,
		// ACompareViewState.ACTIVE_HEATMAP_SELECTION_TYPE);
		// selectionCommandEvent.setCategory(EIDCategory.GENE);
		// selectionCommandEvent.setSelectionCommand(clearSelectionCommand);
		// eventPublisher.triggerEvent(selectionCommandEvent);

		int previouslyActiveHeatMapID = activeHeatMapID;
		activeHeatMapID = groupIndex;
		// FIXME FIXME!!!!! we need to set heat maps inactive as well, this is
		// just for now:
		// for (GLHeatMap heatMap : hashHeatMaps.values()) {
		// heatMap.setActive(false);
		// }

		if (previouslyActiveHeatMapID != -1) {
			GLHeatMap heatMap = hashHeatMaps.get(activeHeatMapID);
			for (Integer elementID : heatMap.getRecordVA()) {

				contentSelectionManager.removeFromType(
						ACompareViewState.ACTIVE_HEATMAP_SELECTION_TYPE, elementID);
			}
			SelectionUpdateEvent selectionUpdateEvent = new SelectionUpdateEvent();
			selectionUpdateEvent.setSelectionDelta(contentSelectionManager.getDelta());
			selectionUpdateEvent.setSender(heatMap);
			eventPublisher.triggerEvent(selectionUpdateEvent);
		}

		GLHeatMap heatMap = hashHeatMaps.get(groupIndex);
		// heatMap.setActive(true);
		contentSelectionManager.addToType(
				ACompareViewState.ACTIVE_HEATMAP_SELECTION_TYPE, heatMap.getRecordVA()
						.getVirtualArray());

		SelectionUpdateEvent selectionUpdateEvent = new SelectionUpdateEvent();
		selectionUpdateEvent.setSelectionDelta(contentSelectionManager.getDelta());
		selectionUpdateEvent.setSender(heatMap);
		eventPublisher.triggerEvent(selectionUpdateEvent);

		if (addToNewSelectionType) {

			SelectionType selectionType = new SelectionType();
			selectionType.setType(SELECTION_TYPE_NAME + selectionTypeNumber);
			selectionType.setPriority(0.8f + 0.0001f * selectionTypeNumber);
			selectionType.setColor(ColorUtil.getColor(selectionTypeNumber++));
			selectionType.setManaged(true);

			SelectionTypeEvent event = new SelectionTypeEvent();
			event.addSelectionType(selectionType);
			event.setSender(heatMap);
			eventPublisher.triggerEvent(event);
			contentSelectionManager.addToType(selectionType, heatMap.getRecordVA()
					.getVirtualArray());
			selectionUpdateEvent = new SelectionUpdateEvent();

			selectionUpdateEvent.setSender(heatMap);
			selectionUpdateEvent.setSelectionDelta(contentSelectionManager.getDelta());
			eventPublisher.triggerEvent(selectionUpdateEvent);
		}

	}

	public void setHeatMapsInactive() {

		for (Integer elementID : recordVA) {
			contentSelectionManager.removeFromType(
					ACompareViewState.ACTIVE_HEATMAP_SELECTION_TYPE, elementID);
		}

		SelectionUpdateEvent selectionUpdateEvent = new SelectionUpdateEvent();
		selectionUpdateEvent.setSelectionDelta(contentSelectionManager.getDelta());
		eventPublisher.triggerEvent(selectionUpdateEvent);

		activeHeatMapID = -1;

		// if (activeHeatMapID == -1)
		// return;
		//
		// GLHeatMap heatMap = hashHeatMaps.get(activeHeatMapID);
		// ContentSelectionManager hmContentSelectionManager = heatMap
		// .getContentSelectionManager();
		// for (Integer elementID : heatMap.getRecordVA()) {
		// hmContentSelectionManager.removeFromType(
		// ACompareViewState.ACTIVE_HEATMAP_SELECTION_TYPE, elementID);
		// }
		//
		// SelectionUpdateEvent selectionUpdateEvent = new
		// SelectionUpdateEvent();
		// selectionUpdateEvent.setSelectionDelta(hmContentSelectionManager.getDelta());
		// eventPublisher.triggerEvent(selectionUpdateEvent);
		//
		// activeHeatMapID = -1;
	}

	public AHeatMapLayout getLayout() {
		return layout;
	}

	public HeatMapOverview getOverview() {
		return overview;
	}

	public RecordSelectionManager getContentSelectionManager() {
		return contentSelectionManager;
	}

	public RecordVirtualArray getRecordVA() {
		return recordVA;
	}

	public HashMap<Group, Boolean> getSelectedGroups() {
		return selectedGroups;
	}

	public GLHeatMap getHeatMap(int id) {
		return hashHeatMaps.get(id);
	}

	public Vec3f getHeatMapPosition(int id) {
		return layout.getDetailHeatMapPosition(id);
		// hashHeatMapPositions.get(id);
	}

	public String getCaption() {
		return dataTable.getLabel();
	}

	public GLHeatMap getHeatMapByContentID(int recordID) {
		for (GLHeatMap tmpHeatMap : hashHeatMaps.values()) {
			if (tmpHeatMap.getRecordVA().contains(recordID)) {
				return tmpHeatMap;
			}
		}

		return null;
	}

	public GLDendrogram<RecordGroupList> getDendrogram() {
		return dendrogram;
	}

	public void setUseSorting(boolean useSorting) {
		this.useSorting = useSorting;
		isInitialized = false;
	}

	public void setUseZoom(boolean useZoom) {
		layout.setUseZoom(useZoom);
	}

	public void setUseFishEye(boolean useFishEye) {
		this.useFishEye = useFishEye;
	}

	public void handleContentGroupListUpdate(RecordGroupList contentGroupList) {
		selectedGroups.clear();
		recordVA.setGroupList(contentGroupList);
		contentGroupList.updateGroupInfo();
		// for (int i = 0; i <= 10 && i < contentGroupList.size(); i++) {
		// Group group = contentGroupList.get(i);
		// group.setSelectionType(SelectionType.SELECTION);
		// selectedGroups.put(group, null);
		// }
		setHeatMapsInactive();
		clearDeselected();

		overview.updateHeatMapTextures(contentSelectionManager);

		isInitialized = false;
		isNewSelection = true;
	}

	public boolean isInitialized() {
		return isInitialized;
	}

	public void setLayout(AHeatMapLayout layout) {
		this.layout = layout;
	}

	public AGLView getView() {
		return glParentView;
	}

}
