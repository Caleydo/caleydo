package org.caleydo.core.view.opengl.canvas.storagebased;

import static org.caleydo.core.view.opengl.canvas.storagebased.HeatMapRenderStyle.FIELD_Z;
import static org.caleydo.core.view.opengl.canvas.storagebased.HeatMapRenderStyle.SELECTION_Z;
import static org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle.MOUSE_OVER_COLOR;
import static org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle.MOUSE_OVER_LINE_WIDTH;
import static org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle.SELECTED_COLOR;
import static org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle.SELECTED_LINE_WIDTH;
import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import gleem.linalg.Vec4f;

import java.util.ArrayList;
import java.util.Set;

import javax.management.InvalidAttributeValueException;
import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.data.selection.ESelectionCommandType;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.GenericSelectionManager;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionCommandEventContainer;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.manager.event.EMediatorType;
import org.caleydo.core.manager.event.view.remote.LoadPathwaysByGeneEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.mapping.IDMappingHelper;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.remote.IGLCanvasRemoteRendering;
import org.caleydo.core.view.opengl.mouse.PickingMouseListener;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.serialize.ASerializedView;
import org.caleydo.core.view.serialize.SerializedDummyView;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;

/**
 * Rendering the GLHeatMap
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class GLHeatMap
	extends AStorageBasedView {
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

	boolean bIsInListMode = false;

	boolean bUseDetailLevel = true;

	int iCurrentMouseOverElement = -1;

	/**
	 * Constructor.
	 * 
	 * @param iGLCanvasID
	 * @param sLabel
	 * @param viewFrustum
	 */
	public GLHeatMap(final int iGLCanvasID, final String sLabel, final IViewFrustum viewFrustum) {

		super(iGLCanvasID, sLabel, viewFrustum);
		viewType = EManagedObjectType.GL_HEAT_MAP;

		ArrayList<ESelectionType> alSelectionTypes = new ArrayList<ESelectionType>();
		alSelectionTypes.add(ESelectionType.NORMAL);
		alSelectionTypes.add(ESelectionType.MOUSE_OVER);
		alSelectionTypes.add(ESelectionType.SELECTION);

		contentSelectionManager = new GenericSelectionManager.Builder(EIDType.EXPRESSION_INDEX).build();
		storageSelectionManager = new GenericSelectionManager.Builder(EIDType.EXPERIMENT_INDEX).build();

		colorMapper = ColorMappingManager.get().getColorMapping(EColorMappingType.GENE_EXPRESSION);

		fAlXDistances = new ArrayList<Float>();
	}

	@Override
	public void init(GL gl) {
		initData();
		if (stableSetForRendering == null)
			return;
	}

	@Override
	public synchronized void resetView() {
		initData();
	}

	@Override
	public void initLocal(GL gl) {
		bRenderOnlyContext = false;

		generalManager.getEventPublisher().addSender(EMediatorType.PROPAGATION_MEDIATOR, this);

		bRenderStorageHorizontally = false;

		iGLDisplayListIndexLocal = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexLocal;
		init(gl);
	}

	@Override
	public void initRemote(final GL gl, final int iRemoteViewID,
		final PickingMouseListener pickingTriggerMouseAdapter,
		final IGLCanvasRemoteRendering remoteRenderingGLCanvas, GLInfoAreaManager infoAreaManager) {
		bRenderOnlyContext = true;

		this.remoteRenderingGLCanvas = remoteRenderingGLCanvas;

		bRenderStorageHorizontally = false;

		this.pickingTriggerMouseAdapter = pickingTriggerMouseAdapter;

		iGLDisplayListIndexRemote = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexRemote;
		init(gl);

	}

	public synchronized void setToListMode(boolean bSetToListMode) {
		this.bIsInListMode = bSetToListMode;
		super.setDetailLevel(EDetailLevel.HIGH);
		bUseDetailLevel = false;
		setDisplayListDirty();
	}

	@Override
	public synchronized void setDetailLevel(EDetailLevel detailLevel) {
		if (bUseDetailLevel) {
			super.setDetailLevel(detailLevel);
		}
		// renderStyle.setDetailLevel(detailLevel);
		renderStyle.updateFieldSizes();
	}

	@Override
	public synchronized void displayLocal(GL gl) {

		if (stableSetForRendering == null)
			return;

		if (bIsTranslationAnimationActive) {
			doTranslation();
		}

		pickingManager.handlePicking(iUniqueID, gl);

		if (bIsDisplayListDirtyLocal) {
			buildDisplayList(gl, iGLDisplayListIndexLocal);
			bIsDisplayListDirtyLocal = false;
		}
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		display(gl);
		checkForHits(gl);

		if (eBusyModeState != EBusyModeState.OFF) {
			renderBusyMode(gl);
		}
	}

	@Override
	public synchronized void displayRemote(GL gl) {

		if (stableSetForRendering == null)
			return;

		if (bIsTranslationAnimationActive) {
			bIsDisplayListDirtyRemote = true;
			doTranslation();
		}

		if (bIsDisplayListDirtyRemote) {
			buildDisplayList(gl, iGLDisplayListIndexRemote);
			bIsDisplayListDirtyRemote = false;
		}
		iGLDisplayListToCall = iGLDisplayListIndexRemote;

		display(gl);
		checkForHits(gl);

		// pickingTriggerMouseAdapter.resetEvents();
	}

	@Override
	public synchronized void display(GL gl) {

		// clipToFrustum(gl);

		gl.glCallList(iGLDisplayListToCall);

		// buildDisplayList(gl, iGLDisplayListIndexRemote);
	}

	private void buildDisplayList(final GL gl, int iGLDisplayListIndex) {

		if (bHasFrustumChanged) {
			bHasFrustumChanged = false;
		}
		gl.glNewList(iGLDisplayListIndex, GL.GL_COMPILE);

		if (contentSelectionManager.getNumberOfElements() == 0 && !bIsInListMode) {
			renderSymbol(gl);
		}
		else {

			float fSpacing = 0;
			if (!bRenderStorageHorizontally) {
				if (bIsInListMode) {
					fSpacing = HeatMapRenderStyle.LIST_SPACING;
				}
				gl.glTranslatef(vecTranslation.x(), viewFrustum.getHeight() - fSpacing, vecTranslation.z());
				gl.glRotatef(vecRotation.x(), vecRotation.y(), vecRotation.z(), vecRotation.w());
			}

			gl.glTranslatef(fAnimationTranslation, 0.0f, 0.0f);

			renderHeatMap(gl);

			renderSelection(gl, ESelectionType.MOUSE_OVER);
			renderSelection(gl, ESelectionType.SELECTION);

			gl.glTranslatef(-fAnimationTranslation, 0.0f, 0.0f);

			if (!bRenderStorageHorizontally) {
				gl.glRotatef(-vecRotation.x(), vecRotation.y(), vecRotation.z(), vecRotation.w());
				gl
					.glTranslatef(-vecTranslation.x(), -viewFrustum.getHeight() + fSpacing, -vecTranslation
						.z());
			}

			gl.glDisable(GL.GL_STENCIL_TEST);
		}
		gl.glEndList();
	}

	/**
	 * Render the symbol of the view instead of the view
	 * 
	 * @param gl
	 */
	private void renderSymbol(GL gl) {
		float fXButtonOrigin = 0.33f * renderStyle.getScaling();
		float fYButtonOrigin = 0.33f * renderStyle.getScaling();
		Texture tempTexture = iconTextureManager.getIconTexture(gl, EIconTextures.HEAT_MAP_SYMBOL);
		tempTexture.enable();
		tempTexture.bind();

		TextureCoords texCoords = tempTexture.getImageTexCoords();

		gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
		gl.glColor4f(1f, 1, 1, 1f);
		gl.glBegin(GL.GL_POLYGON);

		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(fXButtonOrigin, fYButtonOrigin, 0.01f);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(fXButtonOrigin, 2 * fYButtonOrigin, 0.01f);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(fXButtonOrigin * 2, 2 * fYButtonOrigin, 0.01f);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(fXButtonOrigin * 2, fYButtonOrigin, 0.01f);
		gl.glEnd();
		gl.glPopAttrib();
		tempTexture.disable();
	}

	public synchronized void renderHorizontally(boolean bRenderStorageHorizontally) {

		this.bRenderStorageHorizontally = bRenderStorageHorizontally;
		// renderStyle.setBRenderStorageHorizontally(bRenderStorageHorizontally);
		setDisplayListDirty();
	}

	@Override
	protected void initLists() {
		if (bRenderOnlyContext) {
			iContentVAID = mapVAIDs.get(EStorageBasedVAType.EXTERNAL_SELECTION);
		}
		else {
			if (!mapVAIDs.containsKey(EStorageBasedVAType.COMPLETE_SELECTION)) {
				initCompleteList();
			}
			iContentVAID = mapVAIDs.get(EStorageBasedVAType.COMPLETE_SELECTION);
		}
		iStorageVAID = mapVAIDs.get(EStorageBasedVAType.STORAGE_SELECTION);

		contentSelectionManager.resetSelectionManager();
		storageSelectionManager.resetSelectionManager();

		contentSelectionManager.setVA(stableSetForRendering.getVA(iContentVAID));
		storageSelectionManager.setVA(stableSetForRendering.getVA(iStorageVAID));

		int iNumberOfColumns = stableSetForRendering.getVA(iContentVAID).size();
		int iNumberOfRows = stableSetForRendering.getVA(iStorageVAID).size();

		for (int iRowCount = 0; iRowCount < iNumberOfRows; iRowCount++) {
			storageSelectionManager.initialAdd(stableSetForRendering.getVA(iStorageVAID).get(iRowCount));

		}

		// this for loop executes one per axis
		for (int iColumnCount = 0; iColumnCount < iNumberOfColumns; iColumnCount++) {
			contentSelectionManager.initialAdd(stableSetForRendering.getVA(iContentVAID).get(iColumnCount));
		}

		renderStyle = new HeatMapRenderStyle(this, viewFrustum);
		super.renderStyle = renderStyle;

		vecTranslation = new Vec3f(0, renderStyle.getYCenter() * 2, 0);

	}

	@Override
	public String getShortInfo() {
		return "Heat Map - " + stableSetForRendering.getVA(iContentVAID).size() + " genes / "
			+ stableSetForRendering.getVA(iStorageVAID).size() + " experiments";
	}

	@Override
	public String getDetailedInfo() {
		StringBuffer sInfoText = new StringBuffer();
		sInfoText.append("<b>Type:</b> Heat Map\n");

		if (bRenderStorageHorizontally) {
			sInfoText.append(stableSetForRendering.getVA(iContentVAID).size() + "Genes in columns and "
				+ stableSetForRendering.getVA(iStorageVAID).size() + " experiments in rows.\n");
		}
		else {
			sInfoText.append(stableSetForRendering.getVA(iContentVAID).size() + " Genes in rows and "
				+ stableSetForRendering.getVA(iStorageVAID).size() + " experiments in columns.\n");
		}

		if (bRenderOnlyContext) {
			sInfoText.append("Showing only genes which occur in one of the other views in focus\n");
		}
		else {
			if (bUseRandomSampling) {
				sInfoText.append("Random sampling active, sample size: " + iNumberOfRandomElements + "\n");
			}
			else {
				sInfoText.append("Random sampling inactive\n");
			}

			if (dataFilterLevel == EDataFilterLevel.COMPLETE) {
				sInfoText.append("Showing all genes in the dataset\n");
			}
			else if (dataFilterLevel == EDataFilterLevel.ONLY_MAPPING) {
				sInfoText.append("Showing all genes that have a known DAVID ID mapping\n");
			}
			else if (dataFilterLevel == EDataFilterLevel.ONLY_CONTEXT) {
				sInfoText
					.append("Showing all genes that are contained in any of the KEGG or Biocarta pathways\n");
			}
		}

		return sInfoText.toString();
	}

	@Override
	protected void handleEvents(EPickingType ePickingType, EPickingMode pickingMode, int iExternalID,
		Pick pick) {
		if (detailLevel == EDetailLevel.VERY_LOW) {
			return;
		}
		ESelectionType eSelectionType;
		switch (ePickingType) {
			case HEAT_MAP_LINE_SELECTION:
				iCurrentMouseOverElement = iExternalID;
				switch (pickingMode) {
					case DOUBLE_CLICKED:

						LoadPathwaysByGeneEvent loadPathwaysByGeneEvent = new LoadPathwaysByGeneEvent();
						loadPathwaysByGeneEvent.setGeneID(iExternalID);
						loadPathwaysByGeneEvent.setIdType(EIDType.EXPRESSION_INDEX);
						generalManager.getEventPublisher().triggerEvent(loadPathwaysByGeneEvent);
						// intentionally no break

					case CLICKED:
						eSelectionType = ESelectionType.SELECTION;
						break;
					case MOUSE_OVER:

						eSelectionType = ESelectionType.MOUSE_OVER;

						// Check if mouse over element is already selected ->
						// ignore
						if (contentSelectionManager.checkStatus(ESelectionType.SELECTION, iExternalID)) {
							contentSelectionManager.clearSelection(eSelectionType);
							triggerEvent(EMediatorType.SELECTION_MEDIATOR,
								new SelectionCommandEventContainer(EIDType.EXPRESSION_INDEX,
									new SelectionCommand(ESelectionCommandType.CLEAR, eSelectionType)));
							setDisplayListDirty();
							return;
						}

						break;
					default:
						return;

				}

				if (contentSelectionManager.checkStatus(eSelectionType, iExternalID)) {
					break;
				}

				connectedElementRepresentationManager.clear(EIDType.EXPRESSION_INDEX);

				contentSelectionManager.clearSelection(eSelectionType);

				// TODO: Integrate multi spotting support again
				// // Resolve multiple spotting on chip and add all to the
				// // selection manager.
				// Integer iRefSeqID =
				// idMappingManager.getID(EMappingType.EXPRESSION_INDEX_2_REFSEQ_MRNA_INT, iExternalID);
				//
				Integer iMappingID = generalManager.getIDManager().createID(EManagedObjectType.CONNECTION);
				// for (Object iExpressionIndex : idMappingManager.getMultiID(
				// EMappingType.REFSEQ_MRNA_INT_2_EXPRESSION_INDEX, iRefSeqID)) {
				// contentSelectionManager.addToType(eSelectionType, (Integer) iExpressionIndex);
				// contentSelectionManager.addConnectionID(iMappingID, (Integer) iExpressionIndex);
				// }
				contentSelectionManager.addToType(eSelectionType, iExternalID);
				contentSelectionManager.addConnectionID(iMappingID, iExternalID);

				if (eFieldDataType == EIDType.EXPRESSION_INDEX) {
					ISelectionDelta selectionDelta = contentSelectionManager.getDelta();

					triggerEvent(EMediatorType.SELECTION_MEDIATOR, new SelectionCommandEventContainer(
						EIDType.REFSEQ_MRNA_INT, new SelectionCommand(ESelectionCommandType.CLEAR,
							eSelectionType)));

					handleConnectedElementRep(selectionDelta);
					SelectionUpdateEvent event = new SelectionUpdateEvent();
					event.setSelectionDelta(selectionDelta);
					event.setInfo(getShortInfo());
					eventPublisher.triggerEvent(event);

					// fixme old style because of private mediator
					// triggerEvent(EMediatorType.SELECTION_MEDIATOR, new
					// DeltaEventContainer<ISelectionDelta>(selectionDelta));
				}

				setDisplayListDirty();
				break;

			case HEAT_MAP_STORAGE_SELECTION:

				switch (pickingMode) {
					case CLICKED:
						eSelectionType = ESelectionType.SELECTION;
						break;
					case MOUSE_OVER:

						eSelectionType = ESelectionType.MOUSE_OVER;

						// Check if mouse over element is already selected ->
						// ignore
						if (storageSelectionManager.checkStatus(ESelectionType.SELECTION, iExternalID)) {
							storageSelectionManager.clearSelection(eSelectionType);
							triggerEvent(EMediatorType.SELECTION_MEDIATOR,
								new SelectionCommandEventContainer(EIDType.EXPERIMENT_INDEX,
									new SelectionCommand(ESelectionCommandType.CLEAR, eSelectionType)));
							setDisplayListDirty();
							return;
						}

						break;
					default:
						return;
				}

				if (storageSelectionManager.checkStatus(eSelectionType, iExternalID)) {
					break;
				}

				storageSelectionManager.clearSelection(eSelectionType);
				storageSelectionManager.addToType(eSelectionType, iExternalID);

				if (eStorageDataType == EIDType.EXPERIMENT_INDEX) {

					triggerEvent(EMediatorType.SELECTION_MEDIATOR, new SelectionCommandEventContainer(
						EIDType.EXPERIMENT_INDEX, new SelectionCommand(ESelectionCommandType.CLEAR,
							eSelectionType)));
					ISelectionDelta selectionDelta = storageSelectionManager.getDelta();
					SelectionUpdateEvent event = new SelectionUpdateEvent();
					event.setSelectionDelta(selectionDelta);
					eventPublisher.triggerEvent(event);
				}
				setDisplayListDirty();
				break;
		}
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
		ESelectionType currentType;
		for (Integer iContentIndex : stableSetForRendering.getVA(iContentVAID)) {
			iCount++;
			// we treat normal and deselected the same atm
			if (contentSelectionManager.checkStatus(ESelectionType.NORMAL, iContentIndex)
				|| contentSelectionManager.checkStatus(ESelectionType.DESELECTED, iContentIndex)) {
				fFieldWidth = renderStyle.getNormalFieldWidth();
				fFieldHeight = renderStyle.getFieldHeight();
				currentType = ESelectionType.NORMAL;

			}
			else if (contentSelectionManager.checkStatus(ESelectionType.SELECTION, iContentIndex)
				|| contentSelectionManager.checkStatus(ESelectionType.MOUSE_OVER, iContentIndex)) {
				fFieldWidth = renderStyle.getSelectedFieldWidth();
				fFieldHeight = renderStyle.getFieldHeight();
				currentType = ESelectionType.SELECTION;
			}
			else {
				continue;
			}

			if (bIsInListMode) {
				fYPosition = HeatMapRenderStyle.LIST_SPACING;
			}
			else {
				fYPosition = 0;
			}

			for (Integer iStorageIndex : stableSetForRendering.getVA(iStorageVAID)) {
				if (bIsInListMode) {
					if (currentType == ESelectionType.SELECTION) {
						if (iCurrentMouseOverElement == iContentIndex) {
							renderElement(gl, iStorageIndex, iContentIndex, fXPosition + fFieldWidth / 3,
								fYPosition, fFieldWidth / 2, fFieldHeight);
						}
						else {
							renderElement(gl, iStorageIndex, iContentIndex, fXPosition + fFieldWidth / 2f,
								fYPosition, fFieldWidth / 2.5f, fFieldHeight);
						}
					}
					else {
						renderElement(gl, iStorageIndex, iContentIndex, fXPosition + fFieldWidth / 2,
							fYPosition, fFieldWidth / 2, fFieldHeight);
					}
				}
				else {
					renderElement(gl, iStorageIndex, iContentIndex, fXPosition, fYPosition, fFieldWidth,
						fFieldHeight);
				}
				fYPosition += fFieldHeight;

			}

			float fFontScaling = 0;

			float fColumnDegrees = 0;
			float fLineDegrees = 0;
			if (bRenderStorageHorizontally) {
				fColumnDegrees = 0;
				fLineDegrees = 25;
			}
			else {
				fColumnDegrees = 60;
				fLineDegrees = 90;
			}

			// render line captions
			if (fFieldWidth > 0.1f) {
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
					bRenderRefSeq = true;
					String sContent;

					if (stableSetForRendering.getSetType() == ESetType.GENE_EXPRESSION_DATA) {
						sContent = IDMappingHelper.get().getShortNameFromDavid(iContentIndex);

						if (bRenderRefSeq) {
							sContent += " | ";
							// Render heat map element name
							sContent += IDMappingHelper.get().getRefSeqStringFromStorageIndex(iContentIndex);
						}
					}
					else if (stableSetForRendering.getSetType() == ESetType.UNSPECIFIED) {
						sContent =
							generalManager.getIDMappingManager().getID(
								EMappingType.EXPRESSION_INDEX_2_UNSPECIFIED, iContentIndex);
					}
					else {
						throw new IllegalStateException("Label extraction for "
							+ stableSetForRendering.getSetType() + " not implemented yet!");
					}

					if (sContent == null)
						sContent = "Unknown";

					if (bIsInListMode) {

						if (currentType == ESelectionType.SELECTION) {
							if (iCurrentMouseOverElement == iContentIndex) {
								iCurrentMouseOverElement = -1;
								float fTextScalingFactor = 0.0035f;

								float fTextSpacing = 0.1f;
								float fYSelectionOrigin =
									-2 * fTextSpacing - (float) textRenderer.getBounds(sContent).getWidth()
										* fTextScalingFactor;
								float fSlectionFieldHeight =
									-fYSelectionOrigin + renderStyle.getRenderHeight();

								// renderSelectionHighLight(gl, fXPosition,
								// fYSelectionOrigin,
								// fFieldWidth, fSlectionFieldHeight);

								gl.glColor3f(0.25f, 0.25f, 0.25f);
								gl.glBegin(GL.GL_POLYGON);

								gl.glVertex3f(fXPosition + 0.03f, fYSelectionOrigin, 0.0005f);
								gl.glVertex3f(fXPosition + fFieldWidth, fYSelectionOrigin, 0.0005f);
								gl.glVertex3f(fXPosition + fFieldWidth, fYSelectionOrigin
									+ fSlectionFieldHeight, 0.0005f);
								gl.glVertex3f(fXPosition + 0.03f, fYSelectionOrigin + fSlectionFieldHeight,
									0.0005f);

								gl.glEnd();

								textRenderer.setColor(1, 1, 1, 1);
								gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
								gl.glTranslatef(fXPosition + fFieldWidth / 1.5f, fYSelectionOrigin
									+ fTextSpacing, 0);
								gl.glRotatef(+fLineDegrees, 0, 0, 1);
								textRenderer.begin3DRendering();
								textRenderer.draw3D(sContent, 0, 0, 0.016f, fTextScalingFactor);
								textRenderer.end3DRendering();
								gl.glRotatef(-fLineDegrees, 0, 0, 1);
								gl.glTranslatef(-fXPosition - fFieldWidth / 1.5f, -fYSelectionOrigin
									- fTextSpacing, 0);
								// textRenderer.begin3DRendering();
								gl.glPopAttrib();
							}
							else {
								float fYSelectionOrigin = 0;
								float fSlectionFieldHeight =
									-fYSelectionOrigin + renderStyle.getRenderHeight();

								// renderSelectionHighLight(gl, fXPosition,
								// fYSelectionOrigin,
								// fFieldWidth, fSlectionFieldHeight);

								gl.glColor3f(0.25f, 0.25f, 0.25f);
								gl.glBegin(GL.GL_POLYGON);

								gl.glVertex3f(fXPosition + 0.03f, fYSelectionOrigin, 0.0005f);
								gl.glVertex3f(fXPosition + fFieldWidth, fYSelectionOrigin, 0.0005f);
								gl.glVertex3f(fXPosition + fFieldWidth, fYSelectionOrigin
									+ fSlectionFieldHeight, 0.0005f);
								gl.glVertex3f(fXPosition + 0.03f, fYSelectionOrigin + fSlectionFieldHeight,
									0.0005f);

								gl.glEnd();

								// textRenderer.setColor(1, 1, 1, 1);
								// gl.glPushAttrib(GL.GL_CURRENT_BIT |
								// GL.GL_LINE_BIT);
								// gl.glTranslatef(fXPosition + fFieldWidth /
								// 1.5f,
								// fYSelectionOrigin + fTextSpacing, 0);
								// gl.glRotatef(+fLineDegrees, 0, 0, 1);
								// textRenderer.begin3DRendering();
								// textRenderer
								// .draw3D(sContent, 0, 0, 0.016f,
								// fTextScalingFactor);
								// textRenderer.end3DRendering();
								// gl.glRotatef(-fLineDegrees, 0, 0, 1);
								// gl.glTranslatef(-fXPosition - fFieldWidth /
								// 1.5f,
								// -fYSelectionOrigin - fTextSpacing, 0);
								// // textRenderer.begin3DRendering()
								textRenderer.setColor(1, 1, 1, 1);
								renderCaption(gl, sContent, fXPosition + fFieldWidth / 2.2f - 0.01f,
									0 + 0.01f + HeatMapRenderStyle.LIST_SPACING, 0.02f, fLineDegrees,
									fFontScaling);
								gl.glPopAttrib();
							}
						}
						else {
							textRenderer.setColor(0, 0, 0, 1);
							renderCaption(gl, sContent, fXPosition + fFieldWidth / 2 - 0.01f,
								0 + 0.01f + HeatMapRenderStyle.LIST_SPACING, 0, fLineDegrees, fFontScaling);
						}
					}
					else {
						textRenderer.setColor(0, 0, 0, 1);
						renderCaption(gl, sContent, fXPosition + fFieldWidth / 6 * 4.5f, fYPosition + 0.1f,
							0, fLineDegrees, fFontScaling);
					}
				}

			}
			// renderStyle.setXDistanceAt(set.getVA(iContentVAID).indexOf(iContentIndex),
			// fXPosition);
			fAlXDistances.add(fXPosition);
			fXPosition += fFieldWidth;

			// render column captions
			if (detailLevel == EDetailLevel.HIGH && !bIsInListMode) {
				if (iCount == stableSetForRendering.getVA(iContentVAID).size()) {
					fYPosition = 0;
					for (Integer iStorageIndex : stableSetForRendering.getVA(iStorageVAID)) {
						renderCaption(gl, stableSetForRendering.get(iStorageIndex).getLabel(),
							fXPosition + 0.1f, fYPosition + fFieldHeight / 2, 0, fColumnDegrees, renderStyle
								.getSmallFontScalingFactor());
						fYPosition += fFieldHeight;
					}
				}
			}
		}
	}

	private void renderElement(final GL gl, final int iStorageIndex, final int iContentIndex,
		final float fXPosition, final float fYPosition, final float fFieldWidth, final float fFieldHeight) {

		float fLookupValue =
			stableSetForRendering.get(iStorageIndex).getFloat(EDataRepresentation.NORMALIZED, iContentIndex);

		float fOpacity = 1;
		// if (contentSelectionManager.checkStatus(ESelectionType.MOUSE_OVER, iContentIndex)
		// || contentSelectionManager.checkStatus(ESelectionType.SELECTION, iContentIndex)
		// || detailLevel.compareTo(EDetailLevel.LOW) > 0) {
		// fOpacity = 1f;
		// }
		// else {
		// fOpacity = 0.3f;
		// }

		float[] fArMappingColor = colorMapper.getColor(fLookupValue);

		gl.glColor4f(fArMappingColor[0], fArMappingColor[1], fArMappingColor[2], fOpacity);

		gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.HEAT_MAP_STORAGE_SELECTION,
			iStorageIndex));
		gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.HEAT_MAP_LINE_SELECTION,
			iContentIndex));
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(fXPosition, fYPosition, FIELD_Z);
		gl.glVertex3f(fXPosition + fFieldWidth, fYPosition, FIELD_Z);
		gl.glVertex3f(fXPosition + fFieldWidth, fYPosition + fFieldHeight, FIELD_Z);
		gl.glVertex3f(fXPosition, fYPosition + fFieldHeight, FIELD_Z);
		gl.glEnd();

		gl.glPopName();
		gl.glPopName();
	}

	private void renderSelection(final GL gl, ESelectionType eSelectionType) {
		if (bIsInListMode)
			return;
		// content selection

		Set<Integer> selectedSet = contentSelectionManager.getElements(eSelectionType);
		float fHeight = 0;
		float fXPosition = 0;
		float fYPosition = 0;

		switch (eSelectionType) {
			case SELECTION:
				gl.glColor4fv(SELECTED_COLOR, 0);
				gl.glLineWidth(SELECTED_LINE_WIDTH);
				break;
			case MOUSE_OVER:
				gl.glColor4fv(MOUSE_OVER_COLOR, 0);
				gl.glLineWidth(MOUSE_OVER_LINE_WIDTH);
				break;
		}

		int iColumnIndex = 0;
		for (int iTempColumn : stableSetForRendering.getVA(iContentVAID)) {
			for (Integer iCurrentColumn : selectedSet) {

				if (iCurrentColumn == iTempColumn) {
					fHeight = stableSetForRendering.getVA(iStorageVAID).size() * renderStyle.getFieldHeight();
					fXPosition = fAlXDistances.get(iColumnIndex);

					fYPosition = 0;

					gl.glBegin(GL.GL_LINE_LOOP);
					gl.glVertex3f(fXPosition, fYPosition, SELECTION_Z);
					gl.glVertex3f(fXPosition + renderStyle.getSelectedFieldWidth(), fYPosition, SELECTION_Z);
					gl.glVertex3f(fXPosition + renderStyle.getSelectedFieldWidth(), fYPosition + fHeight,
						SELECTION_Z);
					gl.glVertex3f(fXPosition, fYPosition + fHeight, SELECTION_Z);
					gl.glEnd();

					fHeight = 0;
					fXPosition = 0;
				}
			}
			iColumnIndex++;
		}

		// storage selection

		gl.glEnable(GL.GL_LINE_STIPPLE);
		gl.glLineStipple(2, (short) 0xAAAA);

		selectedSet = storageSelectionManager.getElements(eSelectionType);
		int iLineIndex = 0;
		for (int iTempLine : stableSetForRendering.getVA(iStorageVAID)) {
			for (Integer iCurrentLine : selectedSet) {
				if (iTempLine == iCurrentLine) {
					// TODO we need indices of all elements

					fYPosition = iLineIndex * renderStyle.getFieldHeight();
					gl.glBegin(GL.GL_LINE_LOOP);
					gl.glVertex3f(0, fYPosition, SELECTION_Z);
					gl.glVertex3f(renderStyle.getRenderHeight(), fYPosition, SELECTION_Z);
					gl.glVertex3f(renderStyle.getRenderHeight(), fYPosition + renderStyle.getFieldHeight(),
						SELECTION_Z);
					gl.glVertex3f(0, fYPosition + renderStyle.getFieldHeight(), SELECTION_Z);
					gl.glEnd();
				}
			}
			iLineIndex++;
		}

		gl.glDisable(GL.GL_LINE_STIPPLE);
	}

	@Override
	protected void handleConnectedElementRep(ISelectionDelta selectionDelta) {
		// FIXME: should not be necessary here, incorrect init.
		if (renderStyle == null)
			return;

		renderStyle.updateFieldSizes();
		fAlXDistances.clear();
		float fDistance = 0;

		for (Integer iStorageIndex : stableSetForRendering.getVA(iContentVAID)) {
			fAlXDistances.add(fDistance);
			if (contentSelectionManager.checkStatus(ESelectionType.MOUSE_OVER, iStorageIndex)
				|| contentSelectionManager.checkStatus(ESelectionType.SELECTION, iStorageIndex)) {
				fDistance += renderStyle.getSelectedFieldWidth();
			}
			else {
				fDistance += renderStyle.getNormalFieldWidth();
			}

		}
		super.handleConnectedElementRep(selectionDelta);
	}

	@Override
	protected ArrayList<SelectedElementRep> createElementRep(EIDType idType, int iStorageIndex)
		throws InvalidAttributeValueException {

		SelectedElementRep elementRep;
		ArrayList<SelectedElementRep> alElementReps = new ArrayList<SelectedElementRep>(4);

		for (int iContentIndex : stableSetForRendering.getVA(iContentVAID).indicesOf(iStorageIndex)) {
			if (iContentIndex == -1) {
				// throw new
				// IllegalStateException("No such element in virtual array");
				// TODO this shouldn't happen here.
				continue;
			}

			float fXValue = fAlXDistances.get(iContentIndex); // + renderStyle.getSelectedFieldWidth() / 2;
			// float fYValue = 0;
			float fYValue = renderStyle.getYCenter();

			// Set<Integer> mouseOver = storageSelectionManager.getElements(ESelectionType.MOUSE_OVER);
			// for (int iLineIndex : mouseOver)
			// {
			// fYValue = set.getVA(iStorageVAID).indexOf(iLineIndex) * renderStyle.getFieldHeight() +
			// renderStyle.getFieldHeight()/2;
			// break;
			// }

			if (bRenderStorageHorizontally) {
				elementRep =
					new SelectedElementRep(EIDType.EXPRESSION_INDEX, iUniqueID, fXValue
						+ fAnimationTranslation, fYValue, 0);

			}
			else {
				Rotf myRotf = new Rotf(new Vec3f(0, 0, 1), -(float) Math.PI / 2);
				Vec3f vecPoint = myRotf.rotateVector(new Vec3f(fXValue, fYValue, 0));
				vecPoint.setY(vecPoint.y() + vecTranslation.y());
				elementRep =
					new SelectedElementRep(EIDType.EXPRESSION_INDEX, iUniqueID, vecPoint.x(), vecPoint.y()
						- fAnimationTranslation, 0);

			}
			alElementReps.add(elementRep);
		}
		return alElementReps;
	}

	/**
	 * Re-position a view centered on a element, specified by the element ID
	 * 
	 * @param iElementID
	 *            the ID of the element that should be in the center
	 */
	protected void rePosition(int iElementID) {

		int iSelection;
		if (bRenderStorageHorizontally) {
			iSelection = iContentVAID;
		}
		else {
			iSelection = iStorageVAID;
			// TODO test this
		}

		float fCurrentPosition =
			stableSetForRendering.getVA(iSelection).indexOf(iElementID) * renderStyle.getNormalFieldWidth();// +
		// renderStyle.getXSpacing(
		// );

		float fFrustumLength = viewFrustum.getRight() - viewFrustum.getLeft();
		float fLength =
			(stableSetForRendering.getVA(iSelection).size() - 1) * renderStyle.getNormalFieldWidth() + 1.5f; // MARC
		// :
		// 1.5
		// =
		// correction of
		// lens effect in
		// heatmap

		fAnimationTargetTranslation = -(fCurrentPosition - fFrustumLength / 2);

		if (-fAnimationTargetTranslation > fLength - fFrustumLength) {
			fAnimationTargetTranslation = -(fLength - fFrustumLength + 2 * 0.00f);
		}
		else if (fAnimationTargetTranslation > 0) {
			fAnimationTargetTranslation = 0;
		}
		else if (-fAnimationTargetTranslation < -fAnimationTranslation + fFrustumLength / 2 - 0.00f
			&& -fAnimationTargetTranslation > -fAnimationTranslation - fFrustumLength / 2 + 0.00f) {
			fAnimationTargetTranslation = fAnimationTranslation;
			return;
		}

		bIsTranslationAnimationActive = true;
	}

	private void doTranslation() {

		float fDelta = 0;
		if (fAnimationTargetTranslation < fAnimationTranslation - 0.5f) {

			fDelta = -0.5f;

		}
		else if (fAnimationTargetTranslation > fAnimationTranslation + 0.5f) {
			fDelta = 0.5f;
		}
		else {
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
			iContentVAID = mapVAIDs.get(EStorageBasedVAType.EXTERNAL_SELECTION);
		}
		else {
			if (!mapVAIDs.containsKey(EStorageBasedVAType.COMPLETE_SELECTION)) {
				initCompleteList();
			}

			iContentVAID = mapVAIDs.get(EStorageBasedVAType.COMPLETE_SELECTION);
		}

		contentSelectionManager.setVA(stableSetForRendering.getVA(iContentVAID));
		// renderStyle.setActiveVirtualArray(iContentVAID);

		setDisplayListDirty();

	}

	/*
	 * *
	 * @deprecated Use {@link #renderCaption(GL,String,float,float,float,float,float)} instead
	 */
	// private void renderCaption(GL gl, String sLabel, float fXOrigin, float
	// fYOrigin,
	// float fRotation, float fFontScaling)
	// {
	// renderCaption(gl, sLabel, fXOrigin, fYOrigin, 0, fRotation,
	// fFontScaling);
	// }
	private void renderCaption(GL gl, String sLabel, float fXOrigin, float fYOrigin, float fZOrigin,
		float fRotation, float fFontScaling) {

		if (sLabel.length() > GeneralRenderStyle.NUM_CHAR_LIMIT + 1) {
			sLabel = sLabel.substring(0, GeneralRenderStyle.NUM_CHAR_LIMIT - 2);
			sLabel = sLabel + "..";
		}

		// textRenderer.setColor(0, 0, 0, 1);
		gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
		gl.glTranslatef(fXOrigin, fYOrigin, fZOrigin);
		gl.glRotatef(fRotation, 0, 0, 1);
		textRenderer.begin3DRendering();
		textRenderer.draw3D(sLabel, 0, 0, 0, fFontScaling);
		textRenderer.end3DRendering();
		gl.glRotatef(-fRotation, 0, 0, 1);
		gl.glTranslatef(-fXOrigin, -fYOrigin, -fZOrigin);
		// textRenderer.begin3DRendering();
		gl.glPopAttrib();
	}

	@Override
	public synchronized void broadcastElements() {
		ISelectionDelta delta = contentSelectionManager.getCompleteDelta();

		SelectionUpdateEvent event = new SelectionUpdateEvent();
		event.setSelectionDelta(delta);
		event.setInfo(getShortInfo());
		eventPublisher.triggerEvent(event);

		// fixme old style because of private mediator
		// triggerEvent(EMediatorType.SELECTION_MEDIATOR, new DeltaEventContainer<ISelectionDelta>(delta));

		setDisplayListDirty();
	}

	// @Override
	// public synchronized void clear()
	// {
	// contentSelectionManager.clearSelections();
	// storageSelectionManager.clearSelections();
	// setDisplayListDirty();
	// }

	@Override
	public void changeOrientation(boolean defaultOrientation) {
		renderHorizontally(defaultOrientation);
	}

	@Override
	public boolean isInDefaultOrientation() {
		return bRenderStorageHorizontally;
	}

	public boolean isInListMode() {
		return bIsInListMode;
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedDummyView serializedForm = new SerializedDummyView();
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

}
