package org.caleydo.view.heatmap;

import static org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle.MOUSE_OVER_COLOR;
import static org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle.MOUSE_OVER_LINE_WIDTH;
import static org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle.SELECTED_COLOR;
import static org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle.SELECTED_LINE_WIDTH;
import static org.caleydo.view.heatmap.HeatMapRenderStyle.FIELD_Z;
import static org.caleydo.view.heatmap.HeatMapRenderStyle.SELECTION_Z;
import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import gleem.linalg.Vec4f;

import java.util.ArrayList;
import java.util.Set;

import javax.management.InvalidAttributeValueException;
import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.ContentSelectionManager;
import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.data.selection.IVirtualArray;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.VABasedSelectionManager;
import org.caleydo.core.data.selection.delta.ContentVADelta;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.manager.event.view.ClearSelectionsEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.manager.usecase.EDataFilterLevel;
import org.caleydo.core.manager.view.ConnectedElementRepresentationManager;
import org.caleydo.core.manager.view.StandardTransformer;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.clusterer.AffinityClusterer;
import org.caleydo.core.util.clusterer.ClusterManager;
import org.caleydo.core.util.clusterer.ClusterResult;
import org.caleydo.core.util.clusterer.ClusterState;
import org.caleydo.core.util.clusterer.EClustererAlgo;
import org.caleydo.core.util.clusterer.EClustererType;
import org.caleydo.core.util.clusterer.EDistanceMeasure;
import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.AStorageBasedView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.container.ExperimentContextMenuItemContainer;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.container.ContentContextMenuItemContainer;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.heatmap.listener.GLHeatMapKeyListener;

/**
 * Rendering the GLHeatMap
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class GLHeatMap extends AStorageBasedView {

	public final static String VIEW_ID = "org.caleydo.view.heatmap";

	private HeatMapRenderStyle renderStyle;

	private ColorMapping colorMapper;

	private EIDType eFieldDataType = EIDType.EXPRESSION_INDEX;
	private EIDType eStorageDataType = EIDType.EXPERIMENT_INDEX;

	// private boolean bRenderHorizontally = false;

	private Vec4f vecRotation = new Vec4f(-90, 0, 0, 1);

	private Vec3f vecTranslation;

	private float fAnimationTranslation = 0;

	private boolean bIsTranslationAnimationActive = false;

	private float fAnimationTargetTranslation = 0;

	private SelectedElementRep elementRep;

	private ArrayList<Float> fAlXDistances;

	boolean bUseDetailLevel = true;
	
	private boolean sendClearSelectionsEvent = false;

	int iCurrentMouseOverElement = -1;
	
	int numSentClearSelectionEvents = 0;

	/**
	 * Determines whether a bigger space between heat map and caption is needed
	 * or not. If false no cluster info is available and therefore no additional
	 * space is needed. Set by remote rendering view (HHM).
	 */
	private boolean bClusterVisualizationGenesActive = false;

	private boolean bClusterVisualizationExperimentsActive = false;

	/** Utility object for coordinate transformation and projection */
	protected StandardTransformer selectionTransformer;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param sLabel
	 * @param viewFrustum
	 */
	public GLHeatMap(GLCaleydoCanvas glCanvas, final String sLabel,
			final IViewFrustum viewFrustum) {

		super(glCanvas, sLabel, viewFrustum);
		viewType = GLHeatMap.VIEW_ID;

		// ArrayList<SelectionType> alSelectionTypes = new
		// ArrayList<SelectionType>();
		// alSelectionTypes.add(SelectionType.NORMAL);
		// alSelectionTypes.add(SelectionType.MOUSE_OVER);
		// alSelectionTypes.add(SelectionType.SELECTION);

		colorMapper = ColorMappingManager.get().getColorMapping(
				EColorMappingType.GENE_EXPRESSION);

		fAlXDistances = new ArrayList<Float>();

		glKeyListener = new GLHeatMapKeyListener(this);
	}

	@Override
	public void init(GL gl) {
		renderStyle = new HeatMapRenderStyle(this, viewFrustum);
		super.renderStyle = renderStyle;
	}

	@Override
	public void initLocal(GL gl) {
		// Register keyboard listener to GL canvas
		GeneralManager.get().getGUIBridge().getDisplay().asyncExec(
				new Runnable() {
					public void run() {
						parentGLCanvas.getParentComposite().addKeyListener(
								glKeyListener);
					}
				});

		iGLDisplayListIndexLocal = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		selectionTransformer = new StandardTransformer(iUniqueID);
		init(gl);
	}

	@Override
	public void initRemote(final GL gl, final AGLView glParentView,
			final GLMouseListener glMouseListener,
			GLInfoAreaManager infoAreaManager) {

		if (glRemoteRenderingView.getViewType().equals(
				"org.caleydo.view.bucket"))
			renderStyle.setUseFishEye(false);

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

		selectionTransformer = new StandardTransformer(iUniqueID);

		init(gl);
	}

	@Override
	public void setDetailLevel(EDetailLevel detailLevel) {
		if (bUseDetailLevel) {
			super.setDetailLevel(detailLevel);
		}
		// renderStyle.setDetailLevel(detailLevel);
		renderStyle.updateFieldSizes();
	}

	@Override
	public void displayLocal(GL gl) {
		processEvents();
		if (!isVisible())
			return;
		if (set == null)
			return;

		if (bIsTranslationAnimationActive) {
			doTranslation();
		}

		pickingManager.handlePicking(this, gl);

		if (bIsDisplayListDirtyLocal) {
			buildDisplayList(gl, iGLDisplayListIndexLocal);
			bIsDisplayListDirtyLocal = false;
		}
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		display(gl);
		numSentClearSelectionEvents = 0;
		checkForHits(gl);

		ConnectedElementRepresentationManager cerm = GeneralManager.get()
				.getViewGLCanvasManager()
				.getConnectedElementRepresentationManager();
		cerm.doViewRelatedTransformation(gl, selectionTransformer);

		if (eBusyModeState != EBusyModeState.OFF) {
			renderBusyMode(gl);
		}
	}

	@Override
	public void displayRemote(GL gl) {
		if (set == null)
			return;

		if (bIsTranslationAnimationActive) {
			bIsDisplayListDirtyRemote = true;
			doTranslation();
		}

		if (bIsDisplayListDirtyRemote) {
			buildDisplayList(gl, iGLDisplayListIndexRemote);
			bIsDisplayListDirtyRemote = false;
			// generalManager.getViewGLCanvasManager().getConnectedElementRepresentationManager().clearTransformedConnections();
		}
		iGLDisplayListToCall = iGLDisplayListIndexRemote;

		display(gl);
		numSentClearSelectionEvents = 0;
		checkForHits(gl);

		// glMouseListener.resetEvents();
	}

	@Override
	public void display(GL gl) {

		gl.glCallList(iGLDisplayListToCall);

		// buildDisplayList(gl, iGLDisplayListIndexRemote);

		if (!isRenderedRemote())
			contextMenu.render(gl, this);
	}

	private void buildDisplayList(final GL gl, int iGLDisplayListIndex) {

		if (bHasFrustumChanged) {
			bHasFrustumChanged = false;
		}
		gl.glNewList(iGLDisplayListIndex, GL.GL_COMPILE);

		if (contentVA.size() == 0) {
			renderSymbol(gl, EIconTextures.HEAT_MAP_SYMBOL, 2);
		} else {

			float fSpacing = 0;
			// FIXME the whole heat map is turned the wrong way
			gl.glTranslatef(vecTranslation.x(), viewFrustum.getHeight()
					- fSpacing, vecTranslation.z());
			gl.glRotatef(vecRotation.x(), vecRotation.y(), vecRotation.z(),
					vecRotation.w());

			gl.glTranslatef(fAnimationTranslation, 0.0f, 0.0f);

			renderHeatMap(gl);

			renderSelection(gl, SelectionType.MOUSE_OVER);
			renderSelection(gl, SelectionType.SELECTION);

			gl.glTranslatef(-fAnimationTranslation, 0.0f, 0.0f);

			gl.glRotatef(-vecRotation.x(), vecRotation.y(), vecRotation.z(),
					vecRotation.w());
			gl.glTranslatef(-vecTranslation.x(), -viewFrustum.getHeight()
					+ fSpacing, -vecTranslation.z());

		}
		gl.glEndList();
	}

	public ContentSelectionManager getContentSelectionManager() {
		return contentSelectionManager;
	}

	@Override
	protected void initLists() {
		// todo this is not nice here, we may need a more intelligent way to
		// determine which to use
		
		if (contentVAType == ContentVAType.CONTENT_EMBEDDED_HM) 
		{
		set.setContentVA(contentVAType, new ContentVirtualArray(contentVAType));	
		}
			else
		{
			if (bRenderOnlyContext)
				contentVAType = ContentVAType.CONTENT_CONTEXT;
			else
				contentVAType = ContentVAType.CONTENT;
		}

		contentVA = set.getContentVA(contentVAType);
		storageVA = set.getStorageVA(storageVAType);

		contentSelectionManager.setVA(contentVA);
		storageSelectionManager.setVA(storageVA);

		// FIXME: do we need to do this here?
		renderStyle = new HeatMapRenderStyle(this, viewFrustum);
		if (getRemoteRenderingGLCanvas() instanceof GLHierarchicalHeatMap)
			renderStyle.setUseFishEye(false);

		vecTranslation = new Vec3f(0, renderStyle.getYCenter() * 2, 0);

	}

	@Override
	public String getShortInfo() {

		if (contentVA == null)
			return "Heat Map - 0 " + useCase.getContentLabel(false, true)
					+ " / 0 experiments";

		return "Heat Map - " + contentVA.size() + " "
				+ useCase.getContentLabel(false, true) + " / "
				+ storageVA.size() + " experiments";
	}

	@Override
	public String getDetailedInfo() {

		StringBuffer sInfoText = new StringBuffer();
		sInfoText.append("<b>Type:</b> Heat Map\n");

		sInfoText.append(contentVA.size() + " "
				+ useCase.getContentLabel(true, true) + " in rows and "
				+ storageVA.size() + " experiments in columns.\n");

		if (bRenderOnlyContext) {
			sInfoText.append("Showing only " + " "
					+ useCase.getContentLabel(false, true)
					+ " which occur in one of the other views in focus\n");
		} else {
			if (bUseRandomSampling) {
				sInfoText.append("Random sampling active, sample size: "
						+ iNumberOfRandomElements + "\n");
			} else {
				sInfoText.append("Random sampling inactive\n");
			}

			if (dataFilterLevel == EDataFilterLevel.COMPLETE) {
				sInfoText.append("Showing all genes in the dataset\n");
			} else if (dataFilterLevel == EDataFilterLevel.ONLY_MAPPING) {
				sInfoText
						.append("Showing all genes that have a known DAVID ID mapping\n");
			} else if (dataFilterLevel == EDataFilterLevel.ONLY_CONTEXT) {
				sInfoText
						.append("Showing all genes that are contained in any of the KEGG or Biocarta pathways\n");
			}
		}

		return sInfoText.toString();
	}

	@Override
	protected void handlePickingEvents(EPickingType ePickingType,
			EPickingMode pickingMode, int iExternalID, Pick pick) {
		if (detailLevel == EDetailLevel.VERY_LOW) {
			return;
		}

		SelectionType selectionType;
		switch (ePickingType) {
		case HEAT_MAP_LINE_SELECTION:
			iCurrentMouseOverElement = iExternalID;
			switch (pickingMode) {

			case CLICKED:
				selectionType = SelectionType.SELECTION;
				break;
			case MOUSE_OVER:

				selectionType = SelectionType.MOUSE_OVER;

				break;
			case RIGHT_CLICKED:
				selectionType = SelectionType.SELECTION;

				// Prevent handling of non genetic data in context menu
				if (generalManager.getUseCase(dataDomain).getDataDomain() != EDataDomain.GENETIC_DATA)
					break;

				if (!isRenderedRemote()) {
					contextMenu.setLocation(pick.getPickedPoint(),
							getParentGLCanvas().getWidth(), getParentGLCanvas()
									.getHeight());
					contextMenu.setMasterGLView(this);
				}

				ContentContextMenuItemContainer geneContextMenuItemContainer = new ContentContextMenuItemContainer();
				geneContextMenuItemContainer.setID(EIDType.EXPRESSION_INDEX,
						iExternalID);
				contextMenu.addItemContanier(geneContextMenuItemContainer);
			default:
				return;

			}

			createContentSelection(selectionType, iExternalID);

			break;

		case HEAT_MAP_STORAGE_SELECTION:

			switch (pickingMode) {
			case CLICKED:
				selectionType = SelectionType.SELECTION;
				break;
			case MOUSE_OVER:
				selectionType = SelectionType.MOUSE_OVER;
				break;
			case RIGHT_CLICKED:
				if (!isRenderedRemote()) {
					contextMenu.setLocation(pick.getPickedPoint(),
							getParentGLCanvas().getWidth(), getParentGLCanvas()
									.getHeight());
					contextMenu.setMasterGLView(this);
				}
				ExperimentContextMenuItemContainer experimentContextMenuItemContainer = new ExperimentContextMenuItemContainer();
				experimentContextMenuItemContainer.setID(iExternalID);
				contextMenu
						.addItemContanier(experimentContextMenuItemContainer);
			default:
				return;
			}

			createStorageSelection(selectionType, iExternalID);

			break;
		}
	}

	private void createContentSelection(SelectionType selectionType,
			int contentID) {
		if (contentSelectionManager.checkStatus(selectionType, contentID))
			return;

		// check if the mouse-overed element is already selected, and if it is,
		// whether mouse over is clear.
		// If that all is true we don't need to do anything
		if (selectionType == SelectionType.MOUSE_OVER
				&& contentSelectionManager.checkStatus(SelectionType.SELECTION,
						contentID)
				&& contentSelectionManager
						.getElements(SelectionType.MOUSE_OVER).size() == 0)
			return;

		connectedElementRepresentationManager.clear(EIDType.EXPRESSION_INDEX);

		contentSelectionManager.clearSelection(selectionType);

		// SelectionCommand command = new SelectionCommand(
		// ESelectionCommandType.CLEAR, selectionType);
		// sendSelectionCommandEvent(EIDType.EXPRESSION_INDEX, command);

		// TODO: Integrate multi spotting support again
		// // Resolve multiple spotting on chip and add all to the
		// // selection manager.
		// Integer iRefSeqID =
		// idMappingManager.getID(EMappingType.EXPRESSION_INDEX_2_REFSEQ_MRNA_INT,
		// iExternalID);
		//
		Integer iMappingID = generalManager.getIDManager().createID(
				EManagedObjectType.CONNECTION);
		// for (Object iExpressionIndex : idMappingManager.getMultiID(
		// EMappingType.REFSEQ_MRNA_INT_2_EXPRESSION_INDEX, iRefSeqID)) {
		// contentSelectionManager.addToType(SelectionType, (Integer)
		// iExpressionIndex);
		// contentSelectionManager.addConnectionID(iMappingID, (Integer)
		// iExpressionIndex);
		// }
		contentSelectionManager.addToType(selectionType, contentID);
		contentSelectionManager.addConnectionID(iMappingID, contentID);

		if (eFieldDataType == EIDType.EXPRESSION_INDEX) {
			SelectionDelta selectionDelta = contentSelectionManager.getDelta();

			// SelectionCommand command = new
			// SelectionCommand(ESelectionCommandType.CLEAR,
			// SelectionType);
			// sendSelectionCommandEvent(EIDType.REFSEQ_MRNA_INT, command);
			
			if(sendClearSelectionsEvent && numSentClearSelectionEvents == 0) {
				ClearSelectionsEvent clearSelectionsEvent = new ClearSelectionsEvent();
				clearSelectionsEvent.setSender(this);
				eventPublisher.triggerEvent(clearSelectionsEvent);
				numSentClearSelectionEvents++;
			} 

			handleConnectedElementRep(selectionDelta);
			SelectionUpdateEvent event = new SelectionUpdateEvent();
			event.setSender(this);
			event.setSelectionDelta(selectionDelta);
			event.setInfo(getShortInfoLocal());
			eventPublisher.triggerEvent(event);
			
			
		}

		setDisplayListDirty();
	}

	private void createStorageSelection(SelectionType selectionType,
			int storageID) {
		if (storageSelectionManager.checkStatus(selectionType, storageID))
			return;

		// check if the mouse-overed element is already selected, and if it is,
		// whether mouse over is clear.
		// If that all is true we don't need to do anything
		if (selectionType == SelectionType.MOUSE_OVER
				&& storageSelectionManager.checkStatus(SelectionType.SELECTION,
						storageID)
				&& storageSelectionManager
						.getElements(SelectionType.MOUSE_OVER).size() == 0)
			return;

		storageSelectionManager.clearSelection(selectionType);
		storageSelectionManager.addToType(selectionType, storageID);

		if (eStorageDataType == EIDType.EXPERIMENT_INDEX) {

			// SelectionCommand command = new
			// SelectionCommand(ESelectionCommandType.CLEAR,
			// SelectionType);
			// sendSelectionCommandEvent(EIDType.EXPERIMENT_INDEX, command);
			
			if(sendClearSelectionsEvent && numSentClearSelectionEvents == 0) {
				ClearSelectionsEvent clearSelectionsEvent = new ClearSelectionsEvent();
				clearSelectionsEvent.setSender(this);
				eventPublisher.triggerEvent(clearSelectionsEvent);
				numSentClearSelectionEvents++;
			}

			SelectionDelta selectionDelta = storageSelectionManager.getDelta();
			SelectionUpdateEvent event = new SelectionUpdateEvent();
			event.setSender(this);
			event.setSelectionDelta(selectionDelta);
			eventPublisher.triggerEvent(event);
		}
		setDisplayListDirty();
	}

	public void upDownSelect(boolean isUp) {
		IVirtualArray virtualArray = contentVA;
		if (virtualArray == null)
			throw new IllegalStateException(
					"Virtual Array is required for selectNext Operation");
		int selectedElement = cursorSelect(virtualArray,
				contentSelectionManager, isUp);
		if (selectedElement < 0)
			return;
		createContentSelection(SelectionType.MOUSE_OVER, selectedElement);
	}

	public void leftRightSelect(boolean isLeft) {
		IVirtualArray virtualArray = storageVA;
		if (virtualArray == null)
			throw new IllegalStateException(
					"Virtual Array is required for selectNext Operation");
		int selectedElement = cursorSelect(virtualArray,
				storageSelectionManager, isLeft);
		if (selectedElement < 0)
			return;
		createStorageSelection(SelectionType.MOUSE_OVER, selectedElement);
	}

	private int cursorSelect(IVirtualArray virtualArray,
			VABasedSelectionManager selectionManager, boolean isUp) {

		Set<Integer> elements = selectionManager
				.getElements(SelectionType.MOUSE_OVER);
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
			} else {
				newIndex = index + 1;
				if (newIndex == virtualArray.size())
					return -1;

			}
			return virtualArray.get(newIndex);

		}
		return -1;
	}

	private void renderHeatMap(final GL gl) {
		fAlXDistances.clear();
		renderStyle.updateFieldSizes();
		float fXPosition = 0;
		float fYPosition = 0;
		float fFieldWidth = 0;
		float fFieldHeight = 0;
		// renderStyle.clearFieldWidths();
		// GLHelperFunctions.drawPointAt(gl, new Vec3f(1,0.2f,0));
		int iCount = 0;
		SelectionType currentType;
		for (Integer iContentIndex : contentVA) {
			iCount++;
			// we treat normal and deselected the same atm

			if (contentSelectionManager.checkStatus(SelectionType.SELECTION,
					iContentIndex)
					|| contentSelectionManager.checkStatus(
							SelectionType.MOUSE_OVER, iContentIndex)) {
				fFieldWidth = renderStyle.getSelectedFieldWidth();
				fFieldHeight = renderStyle.getFieldHeight();
				currentType = SelectionType.SELECTION;
			} else {

				fFieldWidth = renderStyle.getNormalFieldWidth();
				fFieldHeight = renderStyle.getFieldHeight();
				currentType = SelectionType.NORMAL;

			}
			fYPosition = 0;

			for (Integer iStorageIndex : storageVA) {

				renderElement(gl, iStorageIndex, iContentIndex, fXPosition,
						fYPosition, fFieldWidth, fFieldHeight);

				fYPosition += fFieldHeight;

			}

			float fFontScaling = 0;

			float fColumnDegrees = 0;
			float fLineDegrees = 0;

			fColumnDegrees = 60;
			fLineDegrees = 90;

			// render line captions
			if (fFieldWidth > 0.055f) {
				boolean bRenderRefSeq = false;
				// if (fFieldWidth < 0.2f)
				// {
				fFontScaling = renderStyle.getSmallFontScalingFactor();
				// }
				// else
				// {
				// bRenderRefSeq = true;
				// fFontScaling = renderStyle.getHeadingFontScalingFactor();
				// }

				if (detailLevel == EDetailLevel.HIGH) {
					// bRenderRefSeq = true;
					String sContent = null;
					String refSeq = null;

					if (set.getSetType() == ESetType.GENE_EXPRESSION_DATA) {

						// FIXME: Due to new mapping system, a mapping involving
						// expression index can return a
						// Set of
						// values, depending on the IDType that has been
						// specified when loading expression
						// data.
						// Possibly a different handling of the Set is required.
						Set<String> setGeneSymbols = idMappingManager
								.getIDAsSet(EIDType.EXPRESSION_INDEX,
										EIDType.GENE_SYMBOL, iContentIndex);

						if ((setGeneSymbols != null && !setGeneSymbols
								.isEmpty())) {
							sContent = (String) setGeneSymbols.toArray()[0];
						}

						if (sContent == null || sContent.equals(""))
							sContent = "Unkonwn Gene";

						// FIXME: Due to new mapping system, a mapping involving
						// expression index can return a
						// Set of
						// values, depending on the IDType that has been
						// specified when loading expression
						// data.
						// Possibly a different handling of the Set is required.
						Set<String> setRefSeqIDs = idMappingManager.getIDAsSet(
								EIDType.EXPRESSION_INDEX, EIDType.REFSEQ_MRNA,
								iContentIndex);

						if ((setRefSeqIDs != null && !setRefSeqIDs.isEmpty())) {
							refSeq = (String) setRefSeqIDs.toArray()[0];
						}
						// GeneticIDMappingHelper.get().getRefSeqStringFromStorageIndex(iContentIndex);

						if (bRenderRefSeq) {
							sContent += " | ";
							// Render heat map element name
							sContent += refSeq;
						}
					} else if (set.getSetType() == ESetType.UNSPECIFIED) {
						sContent = generalManager.getIDMappingManager().getID(
								EIDType.EXPRESSION_INDEX, EIDType.UNSPECIFIED,
								iContentIndex);
					} else {
						throw new IllegalStateException("Label extraction for "
								+ set.getSetType() + " not implemented yet!");
					}

					if (sContent == null)
						sContent = "Unknown";

					textRenderer.setColor(0, 0, 0, 1);

					if (bClusterVisualizationGenesActive)
						gl.glTranslatef(0, renderStyle
								.getWidthClusterVisualization(), 0);

					if (currentType == SelectionType.SELECTION
							|| currentType == SelectionType.MOUSE_OVER) {
						renderCaption(gl, sContent, fXPosition + fFieldWidth
								/ 6 * 2.5f, fYPosition + 0.05f, 0,
								fLineDegrees, fFontScaling);
						if (refSeq != null)
							renderCaption(gl, refSeq, fXPosition + fFieldWidth
									/ 6 * 4.5f, fYPosition + 0.05f, 0,
									fLineDegrees, fFontScaling);
					} else {
						renderCaption(gl, sContent, fXPosition + fFieldWidth
								/ 6 * 4.5f, fYPosition + 0.05f, 0,
								fLineDegrees, fFontScaling);
					}

					if (bClusterVisualizationGenesActive)
						gl.glTranslatef(0, -renderStyle
								.getWidthClusterVisualization(), 0);
				}

			}
			// renderStyle.setXDistanceAt(contentVA.indexOf(iContentIndex),
			// fXPosition);
			fAlXDistances.add(fXPosition);
			fXPosition += fFieldWidth;

			// render column captions
			if (detailLevel == EDetailLevel.HIGH && storageVA.size() < 60) {
				if (iCount == contentVA.size()) {
					fYPosition = 0;

					if (bClusterVisualizationExperimentsActive)
						gl.glTranslatef(+renderStyle
								.getWidthClusterVisualization(), 0, 0);

					for (Integer iStorageIndex : storageVA) {
						textRenderer.setColor(0, 0, 0, 1);
						renderCaption(gl, set.get(iStorageIndex).getLabel(),
								fXPosition + 0.05f, fYPosition + fFieldHeight
										/ 2, 0, fColumnDegrees, renderStyle
										.getSmallFontScalingFactor());
						fYPosition += fFieldHeight;
					}

					if (bClusterVisualizationExperimentsActive)
						gl.glTranslatef(-renderStyle
								.getWidthClusterVisualization(), 0, 0);
				}
			}
		}
	}

	// public void selectElements() {
	// ISelectionDelta delta =
	// contentSelectionManager.selectNext(SelectionType.MOUSE_OVER);
	// if (delta == null)
	// return;
	// SelectionUpdateEvent event = new SelectionUpdateEvent();
	// event.setSelectionDelta(delta);
	// event.setSender(this);
	// eventPublisher.triggerEvent(event);
	// setDisplayListDirty();
	// }

	// @Override
	// public void clear()
	// {
	// contentSelectionManager.clearSelections();
	// storageSelectionManager.clearSelections();
	// setDisplayListDirty();
	// }

	private void renderElement(final GL gl, final int iStorageIndex,
			final int iContentIndex, final float fXPosition,
			final float fYPosition, final float fFieldWidth,
			final float fFieldHeight) {

		IStorage storage = set.get(iStorageIndex);
		float fLookupValue = storage.getFloat(
				EDataRepresentation.NORMALIZED, iContentIndex);

		float fOpacity = 0;
		if (contentSelectionManager.checkStatus(SelectionType.DESELECTED,
				iContentIndex)) {
			fOpacity = 0.3f;
		} else {
			fOpacity = 1.0f;
		}

		float[] fArMappingColor = colorMapper.getColor(fLookupValue);

		gl.glColor4f(fArMappingColor[0], fArMappingColor[1],
				fArMappingColor[2], fOpacity);

		gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.HEAT_MAP_STORAGE_SELECTION, iStorageIndex));
		gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.HEAT_MAP_LINE_SELECTION, iContentIndex));
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(fXPosition, fYPosition, FIELD_Z);
		gl.glVertex3f(fXPosition + fFieldWidth, fYPosition, FIELD_Z);
		gl.glVertex3f(fXPosition + fFieldWidth, fYPosition + fFieldHeight,
				FIELD_Z);
		gl.glVertex3f(fXPosition, fYPosition + fFieldHeight, FIELD_Z);
		gl.glEnd();

		gl.glPopName();
		gl.glPopName();
	}

	private void renderSelection(final GL gl, SelectionType selectionType) {
		// content selection

		Set<Integer> selectedSet = contentSelectionManager
				.getElements(selectionType);
		float fHeight = 0;
		float fXPosition = 0;
		float fYPosition = 0;

		if (selectionType == SelectionType.SELECTION) {
			gl.glColor4fv(SELECTED_COLOR, 0);
			gl.glLineWidth(SELECTED_LINE_WIDTH);
		} else if (selectionType == SelectionType.MOUSE_OVER) {
			gl.glColor4fv(MOUSE_OVER_COLOR, 0);
			gl.glLineWidth(MOUSE_OVER_LINE_WIDTH);
		}

		int iColumnIndex = 0;
		for (int iTempColumn : contentVA) {
			for (Integer iCurrentColumn : selectedSet) {

				if (iCurrentColumn == iTempColumn) {
					fHeight = storageVA.size() * renderStyle.getFieldHeight();
					fXPosition = fAlXDistances.get(iColumnIndex);

					fYPosition = 0;
					gl.glPushName(pickingManager.getPickingID(iUniqueID,
							EPickingType.HEAT_MAP_LINE_SELECTION,
							iCurrentColumn));

					gl.glBegin(GL.GL_LINE_LOOP);
					gl.glVertex3f(fXPosition, fYPosition, SELECTION_Z);
					gl.glVertex3f(fXPosition
							+ renderStyle.getSelectedFieldWidth(), fYPosition,
							SELECTION_Z);
					gl.glVertex3f(fXPosition
							+ renderStyle.getSelectedFieldWidth(), fYPosition
							+ fHeight, SELECTION_Z);
					gl
							.glVertex3f(fXPosition, fYPosition + fHeight,
									SELECTION_Z);
					gl.glEnd();
					gl.glPopName();
					fHeight = 0;
					fXPosition = 0;
				}
			}
			iColumnIndex++;
		}

		// storage selection

		gl.glEnable(GL.GL_LINE_STIPPLE);
		gl.glLineStipple(2, (short) 0xAAAA);

		selectedSet = storageSelectionManager.getElements(selectionType);
		int iLineIndex = 0;
		for (int iTempLine : storageVA) {
			for (Integer iCurrentLine : selectedSet) {
				if (iTempLine == iCurrentLine) {
					// TODO we need indices of all elements

					fYPosition = iLineIndex * renderStyle.getFieldHeight();

					gl.glPushName(pickingManager.getPickingID(iUniqueID,
							EPickingType.HEAT_MAP_STORAGE_SELECTION,
							iCurrentLine));

					gl.glBegin(GL.GL_LINE_LOOP);
					gl.glVertex3f(0, fYPosition, SELECTION_Z);
					gl.glVertex3f(renderStyle.getRenderHeight(), fYPosition,
							SELECTION_Z);
					gl.glVertex3f(renderStyle.getRenderHeight(), fYPosition
							+ renderStyle.getFieldHeight(), SELECTION_Z);
					gl.glVertex3f(0, fYPosition + renderStyle.getFieldHeight(),
							SELECTION_Z);
					gl.glEnd();
					gl.glPopName();
				}
			}
			iLineIndex++;
		}

		gl.glDisable(GL.GL_LINE_STIPPLE);
	}

	@Override
	protected void handleConnectedElementRep(ISelectionDelta selectionDelta) {
		// FIXME: should not be necessary here, incor init.
		if (renderStyle == null)
			return;

		renderStyle.updateFieldSizes();
		fAlXDistances.clear();
		float fDistance = 0;

		for (Integer iStorageIndex : contentVA) {
			fAlXDistances.add(fDistance);
			if (contentSelectionManager.checkStatus(SelectionType.MOUSE_OVER,
					iStorageIndex)
					|| contentSelectionManager.checkStatus(
							SelectionType.SELECTION, iStorageIndex)) {
				fDistance += renderStyle.getSelectedFieldWidth();
			} else {
				fDistance += renderStyle.getNormalFieldWidth();
			}

		}
		super.handleConnectedElementRep(selectionDelta);
	}

	@Override
	protected ArrayList<SelectedElementRep> createElementRep(EIDType idType,
			int iStorageIndex) throws InvalidAttributeValueException {

		SelectedElementRep elementRep;
		ArrayList<SelectedElementRep> alElementReps = new ArrayList<SelectedElementRep>(
				4);

		for (int iContentIndex : contentVA.indicesOf(iStorageIndex)) {
			if (iContentIndex == -1) {
				// throw new
				// IllegalStateException("No such element in virtual array");
				// TODO this shouldn't happen here.
				continue;
			}

			float fXValue = fAlXDistances.get(iContentIndex); // +
			// renderStyle.getSelectedFieldWidth()
			// / 2;
			// float fYValue = 0;
			float fYValue = renderStyle.getYCenter();

			// Set<Integer> mouseOver =
			// storageSelectionManager.getElements(SelectionType.MOUSE_OVER);
			// for (int iLineIndex : mouseOver)
			// {
			// fYValue = storageVA.indexOf(iLineIndex) *
			// renderStyle.getFieldHeight() +
			// renderStyle.getFieldHeight()/2;
			// break;
			// }

			Rotf myRotf = new Rotf(new Vec3f(0, 0, 1), -(float) Math.PI / 2);
			Vec3f vecPoint = myRotf
					.rotateVector(new Vec3f(fXValue, fYValue, 0));
			vecPoint.setY(vecPoint.y() + vecTranslation.y());
			elementRep = new SelectedElementRep(EIDType.EXPRESSION_INDEX,
					iUniqueID, vecPoint.x(), vecPoint.y()
							- fAnimationTranslation, 0);

			alElementReps.add(elementRep);
		}
		return alElementReps;
	}
	
	public float getYCoordinateByContentIndex(int contentIndex) {
		
		renderStyle.updateFieldSizes();
		float fieldWidth = renderStyle.getNormalFieldWidth();
		
		return fieldWidth * contentIndex + (fieldWidth / 2.0f);
	}

	private void doTranslation() {

		float fDelta = 0;
		if (fAnimationTargetTranslation < fAnimationTranslation - 0.5f) {

			fDelta = -0.5f;

		} else if (fAnimationTargetTranslation > fAnimationTranslation + 0.5f) {
			fDelta = 0.5f;
		} else {
			fDelta = fAnimationTargetTranslation - fAnimationTranslation;
			bIsTranslationAnimationActive = false;
		}

		if (elementRep != null) {
			ArrayList<Vec3f> alPoints = elementRep.getPoints();
			for (Vec3f currentPoint : alPoints) {
				currentPoint.setY(currentPoint.y() - fDelta);
			}
		}

		fAnimationTranslation += fDelta;
	}

	@Override
	public void renderContext(boolean bRenderOnlyContext) {

		this.bRenderOnlyContext = bRenderOnlyContext;

		if (this.bRenderOnlyContext) {
			contentVA = useCase.getContentVA(ContentVAType.CONTENT_CONTEXT);
		} else {
			contentVA = useCase.getContentVA(ContentVAType.CONTENT);
		}

		contentSelectionManager.setVA(contentVA);
		// renderStyle.setActiveVirtualArray(iContentVAID);

		setDisplayListDirty();

	}

	private void renderCaption(GL gl, String sLabel, float fXOrigin,
			float fYOrigin, float fZOrigin, float fRotation, float fFontScaling) {
		if (isRenderedRemote()
				&& glRemoteRenderingView.getViewType().equals(
						"org.caleydo.view.bucket"))
			fFontScaling *= 1.5;
		if (sLabel.length() > GeneralRenderStyle.NUM_CHAR_LIMIT + 1) {
			sLabel = sLabel.substring(0, GeneralRenderStyle.NUM_CHAR_LIMIT - 2);
			sLabel = sLabel + "..";
		}

		// textRenderer.setColor(0, 0, 0, 1);
		gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
		gl.glTranslatef(fXOrigin, fYOrigin, fZOrigin);
		gl.glRotatef(fRotation, 0, 0, 1);
		textRenderer.begin3DRendering();
		textRenderer.draw3D(gl, sLabel, 0, 0, 0, fFontScaling,
				HeatMapRenderStyle.LABEL_TEXT_MIN_SIZE);
		textRenderer.end3DRendering();
		gl.glRotatef(-fRotation, 0, 0, 1);
		gl.glTranslatef(-fXOrigin, -fYOrigin, -fZOrigin);
		// textRenderer.begin3DRendering();
		gl.glPopAttrib();
	}

	@Override
	public void handleContentVAUpdate(ContentVADelta delta, String info) {

		super.handleContentVAUpdate(delta, info);

		if (delta.getVAType() == ContentVAType.CONTENT_CONTEXT
				&& contentVAType == ContentVAType.CONTENT_CONTEXT) {
			if (contentVA.size() == 0)
				return;
			// FIXME: this is only proof of concept - use the cluster manager
			// instead of affinity directly
			// long original = System.currentTimeMillis();
			// System.out.println("beginning clustering");
			AffinityClusterer clusterer = new AffinityClusterer();
			ClusterState state = new ClusterState(
					EClustererAlgo.AFFINITY_PROPAGATION,
					EClustererType.GENE_CLUSTERING,
					EDistanceMeasure.EUCLIDEAN_DISTANCE);
			int contentVAID = contentVA.getID();
			state.setContentVA(contentVA);
			state.setStorageVA(storageVA);
			state.setAffinityPropClusterFactorGenes(4.0f);

			ClusterManager clusterManger = new ClusterManager(set);
			ClusterResult result = clusterManger.cluster(state);

			contentVA = result.getContentResult().getContentVA();
			contentSelectionManager.setVA(contentVA);
			contentVA.setID(contentVAID);
			// long result = System.currentTimeMillis() - original;
			// System.out.println("Clustering took in ms: " + result);

		}
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedHeatMapView serializedForm = new SerializedHeatMapView(
				dataDomain);
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public void handleUpdateView() {
		setDisplayListDirty();
	}

	public void setClusterVisualizationGenesActiveFlag(
			boolean bClusterVisualizationActive) {
		this.bClusterVisualizationGenesActive = bClusterVisualizationActive;
	}

	public void setClusterVisualizationExperimentsActiveFlag(
			boolean bClusterVisualizationExperimentsActive) {
		this.bClusterVisualizationExperimentsActive = bClusterVisualizationExperimentsActive;
	}

	@Override
	public String toString() {
		return "Standalone heat map, rendered remote: " + isRenderedRemote()
				+ ", contentSize: " + contentVA.size() + ", storageSize: "
				+ storageVA.size() + ", contentVAType: " + contentVAType
				+ ", remoteRenderer:" + getRemoteRenderingGLCanvas();
	}

	@Override
	public void destroy() {
		selectionTransformer.destroy();
		super.destroy();
	}

	public void useFishEye(boolean useFishEye) {
		renderStyle.setUseFishEye(useFishEye);
	}
	
	public void setContentVA(ContentVirtualArray contentVA) {
//		contentSelectionManager.setVA(contentVA);
		this.contentVA = contentVA;
		setDisplayListDirty();
	}
	
	public boolean isSendClearSelectionsEvent() {
		return sendClearSelectionsEvent;
	}

	public void setSendClearSelectionsEvent(boolean sendClearSelectionsEvent) {
		this.sendClearSelectionsEvent = sendClearSelectionsEvent;
	}
}
