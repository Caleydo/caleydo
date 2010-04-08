package org.caleydo.view.matchmaker;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.media.opengl.GL;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.view.opengl.CmdCreateView;
import org.caleydo.core.data.collection.ISet;
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
import org.caleydo.core.data.selection.SelectionTypeEvent;
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
import org.caleydo.core.view.opengl.util.ColorUtil;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.view.heatmap.dendrogram.GLDendrogram;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.heatmap.template.ComparerDetailTemplate;
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
	private ISet set;

	private IGeneralManager generalManager;
	private ContentVirtualArray contentVA;
	private AHeatMapLayout layout;
	private HashMap<Integer, GLHeatMap> hashHeatMaps;
	private HashMap<Group, Boolean> selectedGroups;
	private GLDendrogram<ContentGroupList> dendrogram;
	private boolean isNewSelection;
	private boolean isInitialized;
	private boolean isNewSet;
	private int id;
	private int activeHeatMapID;

	private AGLView glParentView;
	private ACompareViewState state;
	private GLInfoAreaManager infoAreaManager;
	private IUseCase useCase;
	private IGLRemoteRenderingView parentView;
	private EDataDomain dataDomain;

	// private SelectionUpdateListener selectionUpdateListener;
	private IEventPublisher eventPublisher;
	private ContentSelectionManager contentSelectionManager;

	private boolean useSorting = true;
	private boolean useZoom = false;
	private boolean useFishEye = false;

	public HeatMapWrapper(int id, AHeatMapLayout layout, AGLView glParentView,
			GLInfoAreaManager infoAreaManager, IUseCase useCase,
			IGLRemoteRenderingView parentView, EDataDomain dataDomain,
			ACompareViewState state) {

		generalManager = GeneralManager.get();
		overview = new HeatMapOverview(layout);
		hashHeatMaps = new HashMap<Integer, GLHeatMap>();
		// hashHeatMapPositions = new HashMap<Integer, Vec3f>();
		selectedGroups = new HashMap<Group, Boolean>();

		this.layout = layout;
		this.id = id;
		this.glParentView = glParentView;
		this.infoAreaManager = infoAreaManager;
		this.useCase = useCase;
		this.parentView = parentView;
		this.dataDomain = dataDomain;
		this.state = state;

		isNewSelection = false;
		eventPublisher = GeneralManager.get().getEventPublisher();
		contentSelectionManager = useCase.getContentSelectionManager();
		// contentSelectionManager.addTypeToDeltaBlacklist(activeHeatMapSelectionType);
		activeHeatMapID = -1;
	}

	private GLHeatMap createHeatMap(GL gl, GLMouseListener glMouseListener) {

		CmdCreateView cmdView = (CmdCreateView) generalManager.getCommandManager()
				.createCommandByType(ECommandType.CREATE_GL_VIEW);
		cmdView.setViewID(GLHeatMap.VIEW_ID);

		cmdView.setAttributes(dataDomain, EProjectionMode.ORTHOGRAPHIC, 0, 50, 0, 50,
				-20, 20, -1);

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
		heatMap.useFishEye(false);

		return heatMap;
	}

	private void createDendrogram(GL gl, GLMouseListener glMouseListener) {

		CmdCreateView cmdView = (CmdCreateView) generalManager.getCommandManager()
				.createCommandByType(ECommandType.CREATE_GL_VIEW);
		cmdView.setViewID(GLDendrogram.VIEW_ID + ".horizontal");

		cmdView.setAttributes(dataDomain, EProjectionMode.ORTHOGRAPHIC, 0, 50, 0, 50,
				-20, 20, -1);

		cmdView.doCommand();

		dendrogram = (GLDendrogram<ContentGroupList>) cmdView.getCreatedObject();
		dendrogram.setDataDomain(dataDomain);
		dendrogram.setUseCase(useCase);
		dendrogram.setRemoteRenderingGLView(parentView);
		dendrogram.setSet(set);
		dendrogram.setContentVAType(ContentVAType.CONTENT);
		dendrogram.initData();
		dendrogram.setRenderUntilCut(false);
		dendrogram.initRemote(gl, glParentView, glMouseListener, infoAreaManager);
	}

	public ISet getSet() {
		return set;
	}

	public void setSet(ISet set) {
		this.set = set;
		contentVA = set.getContentVA(ContentVAType.CONTENT);

		// FIXME: Can we do this? Shall we do this in some other way? Do it also
		// with dendrogram.
		for (GLHeatMap heatMap : hashHeatMaps.values()) {
			heatMap.destroy();
		}
		hashHeatMaps.clear();
		selectedGroups.clear();
		contentSelectionManager = useCase.getContentSelectionManager();
		// contentSelectionManager.clearSelections();
		contentSelectionManager.setVA(contentVA);
		ContentGroupList contentGroupList = contentVA.getGroupList();

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

		overview.setSet(set);
		isNewSet = true;
		isInitialized = false;
		activeHeatMapID = -1;
	}

	public void init(GL gl, GLMouseListener glMouseListener,
			GLInfoAreaManager infoAreaManager, EDataDomain dataDomain) {

		if (set == null)
			return;

		ContentGroupList contentGroupList = contentVA.getGroupList();
		hashHeatMaps.clear();
		// selectedGroups.clear();

		if (contentGroupList == null)
			return;

		int groupSampleStartIndex = 0;
		int groupSampleEndIndex = 0;
		int groupIndex = 0;
		for (Group group : contentGroupList) {
			groupSampleEndIndex = groupSampleStartIndex + group.getNrElements() - 1;
			GLHeatMap heatMap = createHeatMap(gl, glMouseListener);
			setEmbeddedHeatMapData(heatMap, groupSampleStartIndex, groupSampleEndIndex);

			hashHeatMaps.put(groupIndex, heatMap);
			groupSampleStartIndex += group.getNrElements();
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
		layout.calculateDrawingParameters();

		for (Group group : selectedGroups.keySet()) {

			if (!selectedGroups.containsKey(group))
				continue;
			GLHeatMap heatMap = hashHeatMaps.get(group.getGroupIndex());
			if (heatMap == null)
				continue;

			float heatMapHeight = layout.getDetailHeatMapHeight(group.getGroupIndex());
			Vec3f heatMapPosition = layout
					.getDetailHeatMapPosition(group.getGroupIndex());

			heatMap.getViewFrustum().setLeft(heatMapPosition.x());
			heatMap.getViewFrustum().setBottom(heatMapPosition.y());
			heatMap.getViewFrustum().setRight(
					heatMapPosition.x() + layout.getDetailWidth());
			heatMap.getViewFrustum().setTop(heatMapPosition.y() + heatMapHeight);

		}
	}

	public void drawLocalItems(GL gl, TextureManager textureManager,
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

	public void drawRemoteItems(GL gl, GLMouseListener glMouseListener,
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

	public boolean handleDragging(GL gl, GLMouseListener glMouseListener) {
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

		dendrogram.setRedrawDendrogram();

	}

	public float[] getLeftOverviewLinkPositionFromContentIndex(int contentIndex) {

		Vec3f overviewPosition = layout.getOverviewHeatMapPosition();

		return new float[] { overviewPosition.x(),
				layout.getOverviewHeatMapSamplePositionY(contentIndex), 0 };
	}

	public float[] getLeftOverviewLinkPositionFromContentID(int contentID) {

		int contentIndex = contentVA.indexOf(contentID);

		if (contentVA.indexOf(contentID) == -1)
			return null;

		return getLeftOverviewLinkPositionFromContentIndex(contentIndex);
	}

	public float[] getRightOverviewLinkPositionFromContentIndex(int contentIndex) {

		Vec3f overviewPosition = layout.getOverviewHeatMapPosition();

		return new float[] { overviewPosition.x() + layout.getOverviewHeatMapWidth(),
				layout.getOverviewHeatMapSamplePositionY(contentIndex), 0 };
	}

	public float[] getRightOverviewLinkPositionFromContentID(int contentID) {

		int contentIndex = contentVA.indexOf(contentID);

		if (contentVA.indexOf(contentID) == -1)
			return null;

		return getRightOverviewLinkPositionFromContentIndex(contentIndex);
	}

	public float[] getRightDetailLinkPositionFromContentID(int contentID) {

		Float yCoordinate = getDetailYCoordinateByContentID(contentID);

		if (yCoordinate == null)
			return null;

		return new float[] { layout.getDetailPosition().x() + layout.getDetailWidth(),
				yCoordinate, 0 };
	}

	public float[] getLeftDetailLinkPositionFromContentID(int contentID) {

		Float yCoordinate = getDetailYCoordinateByContentID(contentID);

		if (yCoordinate == null)
			return null;

		return new float[] { layout.getDetailPosition().x(), yCoordinate, 0 };
	}

	private Float getDetailYCoordinateByContentID(int contentID) {

		// For the group check we need the index in the global content VA
		Group group = getSelectedGroupFromContentIndex(contentVA.indexOf(contentID));
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

		Vec3f heatMapPosition = layout.getDetailHeatMapPosition(group.getGroupIndex());
		// hashHeatMapPositions.get(groupIndex);

		int numTotalSamples = 0;
		float totalHeatMapOverheadSpacing = 0;
		for (Group tempGroup : selectedGroups.keySet()) {
			GLHeatMap tempHeatMap = hashHeatMaps.get(tempGroup.getGroupIndex());
			numTotalSamples += tempHeatMap.getNumberOfVisibleElements();
			totalHeatMapOverheadSpacing += tempHeatMap.getRequiredOverheadSpacing();
		}

		float heatMapHeight = layout.getDetailHeatMapHeight(groupIndex);

		Float elementInHMPosition = heatMap.getYCoordinateByContentIndex(contentIndex);

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
	public ArrayList<ContentVirtualArray> getContentVAsOfHeatMaps(
			boolean considerSelectedGroups) {
		ArrayList<ContentVirtualArray> contentVAs = new ArrayList<ContentVirtualArray>();

		ContentGroupList groupList = contentVA.getGroupList();
		for (int groupIndex = 0; groupIndex < groupList.size(); groupIndex++) {
			Group group = groupList.get(groupIndex);

			if (!considerSelectedGroups || selectedGroups.containsKey(group)) {
				GLHeatMap heatMap = hashHeatMaps.get(group.getGroupIndex());
				contentVAs.add(heatMap.getContentVA());
			}
		}
		return contentVAs;
	}

	public Collection<GLHeatMap> getHeatMaps(boolean considerSelections) {

		if (considerSelections) {
			ArrayList<GLHeatMap> heatMaps = new ArrayList<GLHeatMap>();
			for (Group group : selectedGroups.keySet()) {
				GLHeatMap heatMap = hashHeatMaps.get(group.getGroupIndex());
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

	public ArrayList<ContentSelectionManager> getContentSelectionManagersOfHeatMaps() {

		ArrayList<ContentSelectionManager> contentSelectionManagers = new ArrayList<ContentSelectionManager>();
		for (Group group : selectedGroups.keySet()) {
			GLHeatMap heatMap = hashHeatMaps.get(group.getGroupIndex());
			contentSelectionManagers.add(heatMap.getContentSelectionManager());
		}

		return contentSelectionManagers;
	}

	public Group getSelectedGroupFromContentIndex(int contentIndex) {
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

	public void handleOverviewSliderSelection(EPickingType pickingType,
			EPickingMode pickingMode) {
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
	public void choosePassiveHeatMaps(ArrayList<ContentVirtualArray> foreignContentVAs,
			boolean hideVisible, boolean considerSelectedGroups, boolean selectGroups) {

		ContentGroupList groupList = contentVA.getGroupList();
		// FIXME this isn't to nice
		for (Group group : groupList.getGroups())
			group.setSelectionType(SelectionType.NORMAL);
		// FIXME we shouldn't do that here
		groupList.updateGroupInfo();
		HashMap<Group, Boolean> tempGroups = new HashMap<Group, Boolean>();

		tempGroups.clear();
		for (ContentVirtualArray foreignVa : foreignContentVAs) {
			for (Integer contentID : foreignVa) {
				int vaIndex = contentVA.indexOf(contentID);
				Group selectedGroup = groupList.getGroupOfVAIndex(vaIndex);
				if (!tempGroups.containsKey(selectedGroup)) {
					tempGroups.put(selectedGroup, null);
					selectedGroup.resetVisualGenesCounter();
				}

				GLHeatMap heatMap = hashHeatMaps.get(selectedGroup.getGroupIndex());
				ContentVirtualArray heatMapVA = heatMap.getContentVA();
				int index = heatMapVA.indexOf(contentID);
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
		sort(foreignContentVAs, considerSelectedGroups, hideVisible);

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
		// ContentVirtualArray contentVA = heatMap.getContentVA();
		//
		// if (useSorting) {
		// // re-sort the source virtual array to group genes according
		// // to
		// // vas in the destination (here)
		// for (ContentVirtualArray foreignVA : foreignContentVAs) {
		// Integer foreignContentLastOrdererIndex = 0;
		// for (int contentIndex = 0; contentIndex < nrGenes; contentIndex++) {
		// Integer contentID = contentVA.get(contentIndex);
		// int foreignIndex = foreignVA.indexOf(contentID);
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
		// for (int contentIndex = nrGenes; contentIndex < contentVA
		// .size(); contentIndex++) {
		// SelectionDeltaItem item = new SelectionDeltaItem();
		// item.setPrimaryID(contentVA.get(contentIndex));
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
			GLHeatMap heatMap = hashHeatMaps.get(group.getGroupIndex());

			heatMap.recalculateLayout();
		}
	}

	/**
	 * Sorts the content of the virtual arrays of this heat map wrapper
	 * according to the occurences in the foreign VAs. The parameter specifies
	 * whether elements should be hidden or not. If all elements occur in the
	 * foreign VA the parameter has no effect but increased performance
	 * 
	 * @param foreignContentVAs
	 * @param hideVisible
	 */
	public void sort(ArrayList<ContentVirtualArray> foreignContentVAs,
			boolean hideVisible, boolean considerSelectedGroups) {
		ContentGroupList groupList = contentVA.getGroupList();

		// for (ContentVirtualArray foreignVa : foreignContentVAs) {
		// for (Integer contentID : foreignVa) {
		// int vaIndex = contentVA.indexOf(contentID);
		// Group selectedGroup = groupList.getGroupOfVAIndex(vaIndex);
		// gr
		// }
		// }
		// here we hide those that are not part of the other va, and re-sort the
		// source va
		for (int groupIndex = groupList.size() - 1; groupIndex >= 0; groupIndex--) {
			Group group = groupList.get(groupIndex);
			if (!considerSelectedGroups || selectedGroups.containsKey(group)) {
				GLHeatMap heatMap = hashHeatMaps.get(group.getGroupIndex());

				int nrGenes = group.getContainedNrGenes();

				ContentVirtualArray contentVA = heatMap.getContentVA();

				if (useSorting) {
					// re-sort the source virtual array to group genes according
					// to
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

					if (hideVisible) {
						// hide the elements not in the source vas
						SelectionDelta contentSelectionDelta = new SelectionDelta(
								EIDType.EXPRESSION_INDEX);

						for (int contentIndex = nrGenes; contentIndex < contentVA.size(); contentIndex++) {
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
		selectedGroups.put(selectedGroup, null);
		selectedGroup.setSelectionType(SelectionType.SELECTION);

		isNewSelection = true;

		state.setHeatMapWrapperDisplayListDirty();

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
		// FIXME: Events are too slow?
		contentSelectionManager.clearSelection(GLHeatMap.SELECTION_HIDDEN);
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
			// TODO: Maybe just set selection list dirty
			if (selectedGroups.isEmpty())
				state.setHeatMapWrapperSelectionDisplayListDirty();
			else
				state.setHeatMapWrapperDisplayListDirty();
		}
	}

	public void handleReplaceContentVA(EIDCategory idCategory, ContentVAType vaType) {

		contentVA = set.getContentVA(vaType);
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
		
//		SelectionCommandEvent selectionCommandEvent = new SelectionCommandEvent();
//		SelectionCommand clearSelectionCommand = new SelectionCommand(ESelectionCommandType.CLEAR, ACompareViewState.ACTIVE_HEATMAP_SELECTION_TYPE);
//		selectionCommandEvent.setCategory(EIDCategory.GENE);
//		selectionCommandEvent.setSelectionCommand(clearSelectionCommand);
//		eventPublisher.triggerEvent(selectionCommandEvent);
		
		int previouslyActiveHeatMapID = activeHeatMapID;
		activeHeatMapID = groupIndex;
		// FIXME FIXME!!!!! we need to set heat maps inactive as well, this is
		// just for now:
//		for (GLHeatMap heatMap : hashHeatMaps.values()) {
//			heatMap.setActive(false);
//		}

		if (previouslyActiveHeatMapID != -1) {
			GLHeatMap heatMap = hashHeatMaps.get(activeHeatMapID);
			ContentSelectionManager hmContentSelectionManager = heatMap
					.getContentSelectionManager();
			for (Integer elementID : heatMap.getContentVA()) {

				hmContentSelectionManager.removeFromType(
						ACompareViewState.ACTIVE_HEATMAP_SELECTION_TYPE, elementID);
			}
			SelectionUpdateEvent selectionUpdateEvent = new SelectionUpdateEvent();
			selectionUpdateEvent.setSelectionDelta(hmContentSelectionManager.getDelta());
			selectionUpdateEvent.setSender(heatMap);
			eventPublisher.triggerEvent(selectionUpdateEvent);
		}

		GLHeatMap heatMap = hashHeatMaps.get(groupIndex);
		// heatMap.setActive(true);
		ContentSelectionManager hmContentSelectionManager = heatMap
				.getContentSelectionManager();
		hmContentSelectionManager.addToType(ACompareViewState.ACTIVE_HEATMAP_SELECTION_TYPE, heatMap
				.getContentVA().getVirtualArray());

		SelectionUpdateEvent selectionUpdateEvent = new SelectionUpdateEvent();
		selectionUpdateEvent.setSelectionDelta(hmContentSelectionManager.getDelta());
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
			hmContentSelectionManager.addToType(selectionType, heatMap.getContentVA()
					.getVirtualArray());
			selectionUpdateEvent = new SelectionUpdateEvent();

			selectionUpdateEvent.setSender(heatMap);
			selectionUpdateEvent.setSelectionDelta(hmContentSelectionManager
					.getDelta());
			eventPublisher.triggerEvent(selectionUpdateEvent);
		}

	}

	public void setHeatMapsInactive() {
		if (activeHeatMapID == -1)
			return;

		GLHeatMap heatMap = hashHeatMaps.get(activeHeatMapID);
		ContentSelectionManager hmContentSelectionManager = heatMap
				.getContentSelectionManager();
		for (Integer elementID : heatMap.getContentVA()) {
			hmContentSelectionManager.removeFromType(
					ACompareViewState.ACTIVE_HEATMAP_SELECTION_TYPE, elementID);
		}

		SelectionUpdateEvent selectionUpdateEvent = new SelectionUpdateEvent();
		selectionUpdateEvent.setSelectionDelta(hmContentSelectionManager.getDelta());
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
		return set.getLabel();
	}


	public GLHeatMap getHeatMapByContentID(int contentID) {
		for (GLHeatMap tmpHeatMap : hashHeatMaps.values()) {
			if (tmpHeatMap.getContentVA().containsElement(contentID) > 0) {
				return tmpHeatMap;
			}
		}

		return null;
	}

	public GLDendrogram<ContentGroupList> getDendrogram() {
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

	public void handleContentGroupListUpdate(ContentGroupList contentGroupList) {
		selectedGroups.clear();
		contentVA.setGroupList(contentGroupList);
		contentGroupList.updateGroupInfo();
		for (int i = 0; i <= 10 && i < contentGroupList.size(); i++) {
			Group group = contentGroupList.get(i);
			group.setSelectionType(SelectionType.SELECTION);
			selectedGroups.put(group, null);
		}
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
