/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 * 
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.heatmap.heatmap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import javax.management.InvalidAttributeValueException;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;
import org.caleydo.core.data.selection.ElementConnectionInformation;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.event.data.SelectionUpdateEvent;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.object.ManagedObjectType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.mapping.color.IColorMappingUpdateListener;
import org.caleydo.core.util.mapping.color.UpdateColorMappingEvent;
import org.caleydo.core.util.mapping.color.UpdateColorMappingListener;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.contextmenu.item.BookmarkMenuItem;
import org.caleydo.core.view.contextmenu.item.SeparatorMenuItem;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.ATableBasedView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.vislink.ConnectedElementRepresentationManager;
import org.caleydo.core.view.vislink.StandardTransformer;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.caleydo.datadomain.pathway.contextmenu.container.GeneMenuItemContainer;
import org.caleydo.view.heatmap.HeatMapRenderStyle;
import org.caleydo.view.heatmap.heatmap.template.AHeatMapLayoutConfiguration;
import org.caleydo.view.heatmap.heatmap.template.DefaultTemplate;
import org.caleydo.view.heatmap.heatmap.template.TextureHeatLayoutConfiguration;
import org.caleydo.view.heatmap.listener.GLHeatMapKeyListener;
import org.caleydo.view.heatmap.listener.HideHeatMapElementsEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * Rendering the GLHeatMap
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class GLHeatMap
	extends ATableBasedView
	implements IColorMappingUpdateListener {

	public static String VIEW_TYPE = "org.caleydo.view.heatmap";

	public static String VIEW_NAME = "Heatmap";

	public static final SelectionType SELECTION_HIDDEN = new SelectionType("Hidden", new float[] { 0f, 0f, 0f, 1f }, 1,
			false, false, 0.2f);

	private HeatMapRenderStyle renderStyle;

	private LayoutManager layoutManager;
	private AHeatMapLayoutConfiguration detailedRenderingTemplate;
	private TextureHeatLayoutConfiguration textureTemplate;
	/** hide elements with the state {@link #SELECTION_HIDDEN} if this is true */
	private boolean hideElements = true;
	/** try to show captions, if spacing allows it */
	private boolean showCaptions = false;

	/**
	 * Determines whether a bigger space between heat map and caption is needed
	 * or not. If false no cluster info is available and therefore no additional
	 * space is needed. Set by remote rendering view (HHM).
	 */
	boolean bClusterVisualizationGenesActive = false;

	boolean bClusterVisualizationExperimentsActive = false;

	/** Utility object for coordinate transformation and projection */
	protected StandardTransformer selectionTransformer;

	/** Signals that the heat map is currently active */
	private boolean isActive = false;

	private UpdateColorMappingListener updateColorMappingListener;

	private boolean updateColorMapping = false;

	/**
	 * Constructor.
	 * 
	 */
	public GLHeatMap(GLCanvas glCanvas, Composite parentComposite, ViewFrustum viewFrustum) {

		super(glCanvas, parentComposite, viewFrustum, VIEW_TYPE, VIEW_NAME);

		glKeyListener = new GLHeatMapKeyListener(this);
		renderStyle = new HeatMapRenderStyle(this, viewFrustum);
	}

	@Override
	public void init(GL2 gl) {
		displayListIndex = gl.glGenLists(1);

		selectionTransformer = new StandardTransformer(uniqueID);

		super.renderStyle = renderStyle;

		textRenderer = new CaleydoTextRenderer(24);

		textureTemplate = new TextureHeatLayoutConfiguration(gl, this);

		layoutManager = new LayoutManager(this.viewFrustum, pixelGLConverter);
		if (detailedRenderingTemplate == null)
			detailedRenderingTemplate = new DefaultTemplate(this);

		layoutManager.setStaticLayoutConfiguration(detailedRenderingTemplate);
		layoutManager.updateLayout();

	}

	@Override
	public void initLocal(GL2 gl) {
		// Register keyboard listener to GL2 canvas
		Display.getCurrent().asyncExec(new Runnable() {
			@Override
			public void run() {
				parentComposite.addKeyListener(glKeyListener);
			}
		});

		init(gl);
	}

	@Override
	public void initRemote(final GL2 gl, final AGLView glParentView, final GLMouseListener glMouseListener) {

		// if (glRemoteRenderingView != null
		// &&
		// glRemoteRenderingView.getViewType().equals("org.caleydo.view.bucket"))
		// renderStyle.setUseFishEye(false);

		// Register keyboard listener to GL2 canvas
		parentComposite.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				glParentView.getParentComposite().addKeyListener(glKeyListener);
			}
		});

		this.glMouseListener = glMouseListener;

		init(gl);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		super.reshape(drawable, x, y, width, height);
		layoutManager.updateLayout();
	}

	@Override
	public void setDetailLevel(EDetailLevel detailLevel) {
		if (detailLevel.equals(this.detailLevel))
			return;
		super.setDetailLevel(detailLevel);
		if (tablePerspective.getNrDimensions() > 1
				&& (detailLevel == EDetailLevel.HIGH || detailLevel == EDetailLevel.MEDIUM)) {
			layoutManager.setStaticLayoutConfiguration(detailedRenderingTemplate);
			detailedRenderingTemplate.setStaticLayouts();
			showCaptions = true;
		}
		else {
			layoutManager.setStaticLayoutConfiguration(textureTemplate);
			showCaptions = false;
		}

	}

	@Override
	public void displayLocal(GL2 gl) {

		if (!lazyMode)
			pickingManager.handlePicking(this, gl);

		display(gl);

		if (busyState != EBusyState.OFF) {
			renderBusyMode(gl);
		}
	}

	@Override
	public void displayRemote(GL2 gl) {

		display(gl);

	}

	@Override
	public void display(GL2 gl) {

		if (tablePerspective == null)
			return;
		if (updateColorMapping) {
			textureTemplate.updateColorMapping(gl);
			updateColorMapping = false;
		}

		if (isDisplayListDirty) {
			buildDisplayList(gl, displayListIndex);
			isDisplayListDirty = false;
		}

		gl.glCallList(displayListIndex);
		// numSentClearSelectionEvents = 0;

		if (!lazyMode)
			checkForHits(gl);

		ConnectedElementRepresentationManager cerm = GeneralManager.get().getViewManager()
				.getConnectedElementRepresentationManager();
		cerm.doViewRelatedTransformation(gl, selectionTransformer);

	}

	private void buildDisplayList(final GL2 gl, int displayListIndex) {

		if (hasFrustumChanged) {
			hasFrustumChanged = false;
		}
		gl.glNewList(displayListIndex, GL2.GL_COMPILE);

		if (tablePerspective.getNrRecords() == 0 || tablePerspective.getNrDimensions() == 0) {
			renderSymbol(gl, EIconTextures.HEAT_MAP_SYMBOL, 2);
		}
		else {
			layoutManager.render(gl);
		}
		gl.glEndList();
	}

	@Override
	protected void handlePickingEvents(PickingType pickingType, PickingMode pickingMode, int pickingID, Pick pick) {
		if (detailLevel == EDetailLevel.VERY_LOW) {
			return;
		}

		SelectionType selectionType;

		switch (pickingType) {
			case HEAT_MAP_RECORD_SELECTION:
				// iCurrentMouseOverElement = pickingID;
				switch (pickingMode) {

					case CLICKED:
						selectionType = SelectionType.SELECTION;
						break;

					case MOUSE_OVER:
						selectionType = SelectionType.MOUSE_OVER;
						break;

					case RIGHT_CLICKED:
						selectionType = SelectionType.SELECTION;

						if (dataDomain instanceof GeneticDataDomain && dataDomain.isColumnDimension()) {

							GeneMenuItemContainer contexMenuItemContainer = new GeneMenuItemContainer();
							contexMenuItemContainer.setDataDomain(dataDomain);
							contexMenuItemContainer.setData(recordIDType, pickingID);
							contextMenuCreator.addContextMenuItemContainer(contexMenuItemContainer);
							contextMenuCreator.addContextMenuItem(new SeparatorMenuItem());
						}
						else {
							AContextMenuItem menuItem = new BookmarkMenuItem("Bookmark "
									+ recordIDType.getIDCategory().getHumanReadableIDType() + ": "
									+ dataDomain.getRecordLabel(recordIDType, pickingID), recordIDType, pickingID,
									dataDomain.getDataDomainID());
							contextMenuCreator.addContextMenuItem(menuItem);
						}

						break;

					default:
						return;

				}

				createRecordSelection(selectionType, pickingID);

				break;

			case HEAT_MAP_DIMENSION_SELECTION:

				switch (pickingMode) {
					case CLICKED:
						selectionType = SelectionType.SELECTION;
						break;
					case MOUSE_OVER:
						selectionType = SelectionType.MOUSE_OVER;
						break;
					case RIGHT_CLICKED:

						if (dataDomain instanceof GeneticDataDomain && !dataDomain.isColumnDimension()) {

							GeneMenuItemContainer contexMenuItemContainer = new GeneMenuItemContainer();
							contexMenuItemContainer.setDataDomain(dataDomain);
							contexMenuItemContainer.setData(dimensionIDType, pickingID);
							contextMenuCreator.addContextMenuItemContainer(contexMenuItemContainer);
							contextMenuCreator.addContextMenuItem(new SeparatorMenuItem());
						}
						else {

							AContextMenuItem menuItem = new BookmarkMenuItem("Bookmark "
									+ recordIDType.getIDCategory().getHumanReadableIDType() + ": "
									+ dataDomain.getDimensionLabel(dimensionIDType, pickingID), dimensionIDType,
									pickingID, dataDomain.getDataDomainID());
							contextMenuCreator.addContextMenuItem(menuItem);
						}

					default:
						return;
				}

				createDimensionSelection(selectionType, pickingID);

				break;

			case HEAT_MAP_HIDE_HIDDEN_ELEMENTS:
				if (pickingMode == PickingMode.CLICKED)
					if (hideElements)
						hideElements = false;
					else
						hideElements = true;

				HideHeatMapElementsEvent event = new HideHeatMapElementsEvent(hideElements);
				event.setSender(this);
				event.setDataDomainID(dataDomain.getDataDomainID());
				eventPublisher.triggerEvent(event);

				setDisplayListDirty();

				break;
			case HEAT_MAP_SHOW_CAPTIONS:

				if (pickingMode == PickingMode.CLICKED)
					if (showCaptions)
						showCaptions = false;
					else {
						showCaptions = true;
					}

				detailedRenderingTemplate.setStaticLayouts();
				setDisplayListDirty();
				break;
		}
	}

	private void createRecordSelection(SelectionType selectionType, int recordID) {
		if (recordSelectionManager.checkStatus(selectionType, recordID))
			return;

		// check if the mouse-overed element is already selected, and if it is,
		// whether mouse over is clear.
		// If that all is true we don't need to do anything
		if (selectionType == SelectionType.MOUSE_OVER
				&& recordSelectionManager.checkStatus(SelectionType.SELECTION, recordID)
				&& recordSelectionManager.getElements(SelectionType.MOUSE_OVER).size() == 0)
			return;

		connectedElementRepresentationManager.clear(recordIDType, selectionType);

		recordSelectionManager.clearSelection(selectionType);

		// TODO: Integrate multi spotting support again

		Integer iMappingID = generalManager.getIDCreator().createID(ManagedObjectType.CONNECTION);
		recordSelectionManager.addToType(selectionType, recordID);
		recordSelectionManager.addConnectionID(iMappingID, recordID);

		SelectionDelta selectionDelta = recordSelectionManager.getDelta();

		prepareVisualLinkingInformation(selectionDelta);
		SelectionUpdateEvent event = new SelectionUpdateEvent();
		event.setSender(this);
		// event.setDataDomainID(dataDomain.getDataDomainID());
		event.setSelectionDelta(selectionDelta);
		eventPublisher.triggerEvent(event);
		detailedRenderingTemplate.updateSpacing();
		setDisplayListDirty();
	}

	private void createDimensionSelection(SelectionType selectionType, int dimensionID) {
		if (dimensionSelectionManager.checkStatus(selectionType, dimensionID))
			return;

		// check if the mouse-overed element is already selected, and if it is,
		// whether mouse over is clear.
		// If that all is true we don't need to do anything
		if (selectionType == SelectionType.MOUSE_OVER
				&& dimensionSelectionManager.checkStatus(SelectionType.SELECTION, dimensionID)
				&& dimensionSelectionManager.getElements(SelectionType.MOUSE_OVER).size() == 0)
			return;

		dimensionSelectionManager.clearSelection(selectionType);
		dimensionSelectionManager.addToType(selectionType, dimensionID);

		SelectionDelta selectionDelta = dimensionSelectionManager.getDelta();
		SelectionUpdateEvent event = new SelectionUpdateEvent();
		event.setSender(this);
		event.setDataDomainID(dataDomain.getDataDomainID());
		event.setSelectionDelta(selectionDelta);
		eventPublisher.triggerEvent(event);

	}

	public void upDownSelect(boolean isUp) {
		RecordVirtualArray virtualArray = tablePerspective.getRecordPerspective().getVirtualArray();
		if (virtualArray == null)
			throw new IllegalStateException("Virtual Array is required for selectNext Operation");
		int selectedElement = cursorSelect(virtualArray, recordSelectionManager, isUp);
		if (selectedElement < 0)
			return;
		createRecordSelection(SelectionType.MOUSE_OVER, selectedElement);
	}

	public void leftRightSelect(boolean isLeft) {
		DimensionVirtualArray virtualArray = tablePerspective.getDimensionPerspective().getVirtualArray();
		if (virtualArray == null)
			throw new IllegalStateException("Virtual Array is required for selectNext Operation");
		int selectedElement = cursorSelect(virtualArray, dimensionSelectionManager, isLeft);
		if (selectedElement < 0)
			return;
		createDimensionSelection(SelectionType.MOUSE_OVER, selectedElement);
	}

	public void enterPressedSelect() {
		DimensionVirtualArray virtualArray = tablePerspective.getDimensionPerspective().getVirtualArray();
		if (virtualArray == null)
			throw new IllegalStateException("Virtual Array is required for enterPressed Operation");

		java.util.Set<Integer> elements = dimensionSelectionManager.getElements(SelectionType.MOUSE_OVER);
		Integer selectedElement = -1;
		if (elements.size() == 1) {
			selectedElement = (Integer) elements.toArray()[0];
			createDimensionSelection(SelectionType.SELECTION, selectedElement);
		}

		RecordVirtualArray recordVirtualArray = tablePerspective.getRecordPerspective().getVirtualArray();
		if (recordVirtualArray == null)
			throw new IllegalStateException("Virtual Array is required for enterPressed Operation");
		elements = recordSelectionManager.getElements(SelectionType.MOUSE_OVER);
		selectedElement = -1;
		if (elements.size() == 1) {
			selectedElement = (Integer) elements.toArray()[0];
			createRecordSelection(SelectionType.SELECTION, selectedElement);
		}
	}

	private <VAType extends VirtualArray<?, ?, ?>, SelectionManagerType extends SelectionManager> int cursorSelect(
			VAType virtualArray, SelectionManagerType selectionManager, boolean isUp) {

		java.util.Set<Integer> elements = selectionManager.getElements(SelectionType.MOUSE_OVER);
		if (elements.size() == 0) {
			elements = selectionManager.getElements(SelectionType.SELECTION);
			if (elements.size() == 0)
				return -1;
		}

		if (elements.size() == 1) {
			Integer element = elements.iterator().next();
			int index = virtualArray.indexOf(element);
			int newIndex;
			if (isUp) {
				newIndex = index - 1;
				if (newIndex < 0)
					return -1;
			}
			else {
				newIndex = index + 1;
				if (newIndex == virtualArray.size())
					return -1;

			}
			return virtualArray.get(newIndex);

		}
		return -1;
	}

	@Override
	protected ArrayList<ElementConnectionInformation> createElementConnectionInformation(IDType idType, int id)
			throws InvalidAttributeValueException {
		ElementConnectionInformation elementRep;
		ArrayList<ElementConnectionInformation> alElementReps = new ArrayList<ElementConnectionInformation>(4);

		for (int recordIndex : tablePerspective.getRecordPerspective().getVirtualArray().indicesOf(id)) {
			if (recordIndex == -1) {
				continue;
			}

			float xValue = renderStyle.getXCenter();

			float yValue = 0;

			yValue = getYCoordinateOfRecord(recordIndex);
			yValue = viewFrustum.getHeight() - yValue;
			elementRep = new ElementConnectionInformation(recordIDType, uniqueID, xValue, yValue, 0);

			alElementReps.add(elementRep);
		}

		return alElementReps;
	}

	/**
	 * Returns the y coordinate of the element rendered at recordIndex, or null
	 * if the current element is hidden
	 * 
	 * @param recordIndex
	 * @return
	 */
	public Float getYCoordinateOfRecord(int recordIndex) {
		if (isHideElements()) {
			Integer recordID = tablePerspective.getRecordPerspective().getVirtualArray().get(recordIndex);
			if (recordSelectionManager.checkStatus(SELECTION_HIDDEN, recordID))
				return null;
		}
		return detailedRenderingTemplate.getYCoordinateByContentIndex(recordIndex);

	}

	/**
	 * Returns the x coordinate of the element rendered at dimensionIndex
	 * 
	 * @param dimensionIndex
	 * @return
	 */
	public Float getXCoordinateByDimensionIndex(int dimensionIndex) {
		return detailedRenderingTemplate.getXCoordinateByDimensionIndex(dimensionIndex);
	}

	@Override
	public void handleSelectionUpdate(SelectionDelta selectionDelta) {
		super.handleSelectionUpdate(selectionDelta);
		if (detailLevel == EDetailLevel.HIGH || detailLevel == EDetailLevel.MEDIUM) {
			detailedRenderingTemplate.updateSpacing();
		}

	}

	@Override
	public void handleRecordVAUpdate(String recordPerspectiveID) {
		super.handleRecordVAUpdate(recordPerspectiveID);
		// if (table.getID() != dataTableID)
		// return;

		// FIXME clustering for context heat map
		// if (delta.getVAType().equals(DataTable.RECORD_CONTEXT)
		// && recordVAType.equals(DataTable.RECORD_CONTEXT)) {
		// ClusterState state = new
		// ClusterState(EClustererAlgo.AFFINITY_PROPAGATION,
		// ClustererType.RECORD_CLUSTERING,
		// EDistanceMeasure.EUCLIDEAN_DISTANCE);
		// int recordVAID = recordVA.getID();
		//
		// if (recordVA.size() == 0 || dimensionVA.size() == 0)
		// return;
		//
		// state.setRecordVA(recordVA);
		// state.setDimensionVA(dimensionVA);
		// state.setAffinityPropClusterFactorGenes(4.0f);
		//
		// ClusterManager clusterManger = new ClusterManager(table);
		// ClusterResult result = clusterManger.cluster(state);
		//
		// recordVA = result.getRecordResult().getRecordVA();
		// recordSelectionManager.setVA(recordVA);
		// recordVA.tableID(recordVAID);
		// }
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedHeatMapView serializedForm = new SerializedHeatMapView(this);
		return serializedForm;
	}

	public void setClusterVisualizationGenesActiveFlag(boolean bClusterVisualizationActive) {
		this.bClusterVisualizationGenesActive = bClusterVisualizationActive;
	}

	public void setClusterVisualizationExperimentsActiveFlag(boolean bClusterVisualizationExperimentsActive) {
		this.bClusterVisualizationExperimentsActive = bClusterVisualizationExperimentsActive;
	}

	@Override
	public String toString() {
		return "Heat map for " + tablePerspective;
	}

	@Override
	public void destroyViewSpecificContent(GL2 gl) {
		selectionTransformer.destroy();
	}

	public void setRenderTemplate(AHeatMapLayoutConfiguration template) {
		this.detailedRenderingTemplate = template;
	}

	/**
	 * Check whether we should hide elements
	 * 
	 * @return
	 */
	public boolean isHideElements() {
		return hideElements;
	}

	/**
	 * Check whether we should try to show captions
	 * 
	 * @return
	 */
	public boolean isShowCaptions() {
		return showCaptions;
	}

	/**
	 * returns the number of elements currently visible in the heat map
	 * 
	 * @return
	 */
	public int getNumberOfVisibleRecords() {
		int size = tablePerspective.getRecordPerspective().getVirtualArray().size();
		if (isHideElements())
			return size - recordSelectionManager.getNumberOfElements(SELECTION_HIDDEN);
		else
			return size;
	}

	/**
	 * returns the overhead which the heat map needs additionally to the
	 * elements
	 * 
	 * @return
	 */
	public float getRequiredOverheadSpacing() {
		// return template.getYOverhead();
		if (isActive)
			return 0.1f;
		else
			return 0;
	}

	public boolean isActive() {
		return isActive;
	}

	/**
	 * Returns true if a minimum spacing per element is required. This is
	 * typically the case when captions are rendered.
	 * 
	 * @return
	 */
	public boolean isForceMinSpacing() {
		if (isShowCaptions() || isActive)
			return true;
		return false;
	}

	/**
	 * Returns the height of a particular element
	 * 
	 * @param recordID the id of the element - since they can be of different
	 *            height due to the fish eye
	 * @return the height of the element
	 */
	public float getFieldHeight(int recordID) {
		return detailedRenderingTemplate.getElementHeight(recordID);
	}

	public float getFieldWidth(int dimensionID) {
		return detailedRenderingTemplate.getElementWidth(dimensionID);
	}

	/**
	 * Returns the minimal spacing required when {@link #isForceMinSpacing()} is
	 * true. This is typically the case when captions are rendered.
	 * 
	 * @return
	 */
	// public float getMinSpacing() {
	// return HeatMapRenderStyle.MIN_SELECTED_FIELD_HEIGHT;
	// }

	public void recalculateLayout() {
		processEvents();
		detailedRenderingTemplate.setStaticLayouts();
	}

	public java.util.Set<Integer> getZoomedElements() {
		java.util.Set<Integer> zoomedElements = new HashSet<Integer>(
				recordSelectionManager.getElements(SelectionType.SELECTION));

		if (zoomedElements.size() > 5)
			return new HashSet<Integer>(1);
		Iterator<Integer> elementIterator = zoomedElements.iterator();
		while (elementIterator.hasNext()) {
			int recordID = elementIterator.next();
			if (!tablePerspective.getRecordPerspective().getVirtualArray().contains(recordID))
				elementIterator.remove();
			else if (recordSelectionManager.checkStatus(SELECTION_HIDDEN, recordID))
				elementIterator.remove();
		}
		return zoomedElements;
	}

	public AHeatMapLayoutConfiguration getTemplate() {
		return detailedRenderingTemplate;
	}

	@Override
	public int getMinPixelHeight(EDetailLevel detailLevel) {
		return getPixelPerElement(true, detailLevel, 3, 5);
	}

	@Override
	public int getMinPixelWidth(EDetailLevel detailLevel) {
		return getPixelPerElement(false, detailLevel, 3, 5);
	}

	@Override
	public void setFrustum(ViewFrustum viewFrustum) {
		super.setFrustum(viewFrustum);
		renderStyle = new HeatMapRenderStyle(this, viewFrustum);
		layoutManager.setViewFrustum(viewFrustum);
	}

	public void updateLayout() {
		layoutManager.updateLayout();
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();
		updateColorMappingListener = new UpdateColorMappingListener();
		updateColorMappingListener.setHandler(this);
		eventPublisher.addListener(UpdateColorMappingEvent.class, updateColorMappingListener);
	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();
		if (updateColorMappingListener != null) {
			eventPublisher.removeListener(updateColorMappingListener);
			updateColorMappingListener = null;
		}
	}

	@Override
	public void updateColorMapping() {
		updateColorMapping = true;
	}
}