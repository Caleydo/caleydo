package org.caleydo.core.view.opengl.canvas.storagebased;

import static org.caleydo.core.view.opengl.canvas.storagebased.HeatMapRenderStyle.FIELD_Z;

import java.util.ArrayList;

import javax.management.InvalidAttributeValueException;
import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.delta.IVirtualArrayDelta;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.specialized.genetic.GeneticIDMappingHelper;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.remote.IGLCanvasRemoteRendering;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;

/**
 * The list heat map that shows elements on the right of a view that have been selected. It is registered to
 * special listeners that are triggered in such a event. Other than that it is equivalent to the
 * {@link GLHeatMap}
 * 
 * @author Alexander Lex
 */
public class GLBookmarkContainer
	extends AStorageBasedView {

	private ColorMapping colorMapper;

	protected BookmarkRenderStyle renderStyle;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param label
	 * @param viewFrustum
	 */
	public GLBookmarkContainer(GLCaleydoCanvas glCanvas, String label, IViewFrustum viewFrustum) {
		super(glCanvas, label, viewFrustum);
		contentSelectionManager = new SelectionManager.Builder(EIDType.EXPRESSION_INDEX).build();
		colorMapper = ColorMappingManager.get().getColorMapping(EColorMappingType.GENE_EXPRESSION);

		storageSelectionManager = new SelectionManager.Builder(EIDType.EXPERIMENT_INDEX).build();

		renderStyle = new BookmarkRenderStyle(viewFrustum);

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
	public void changeOrientation(boolean bDefaultOrientation) {
		// TODO Auto-generated method stub

	}

	@Override
	protected ArrayList<SelectedElementRep> createElementRep(EIDType idType, int iStorageIndex)
		throws InvalidAttributeValueException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void initLists() {
		contentVAType = EVAType.CONTENT_BOOKMARKS;
		storageVAType = EVAType.STORAGE;

		contentVA = useCase.getVA(contentVAType);
		storageVA = useCase.getVA(storageVAType);

		contentSelectionManager.resetSelectionManager();
		contentSelectionManager.setVA(contentVA);

	}

	@Override
	public boolean isInDefaultOrientation() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void renderContext(boolean bRenderContext) {
		// TODO Auto-generated method stub

	}

	@Override
	public void display(GL gl) {

		processEvents();
		// fAlXDistances.clear();
		// renderStyle.updateFieldSizes();
		float fXPosition = 0;
		float fYPosition = viewFrustum.getBottom() + viewFrustum.getHeight();
		float fFieldWidth = 0.1f;
		float fFieldHeight = 0.3f;

		int iCurrentMouseOverElement = 0;
		// renderStyle.clearFieldWidths();
		// GLHelperFunctions.drawPointAt(gl, new Vec3f(1,0.2f,0));
		int iCount = 0;
		ESelectionType currentType;
		for (Integer iContentIndex : contentVA) {
			iCount++;
			// we treat normal and deselected the same atm

			currentType = ESelectionType.NORMAL;

			// }
			// else if (contentSelectionManager.checkStatus(ESelectionType.SELECTION, iContentIndex)
			// || contentSelectionManager.checkStatus(ESelectionType.MOUSE_OVER, iContentIndex)) {
			// fFieldWidth = renderStyle.getSelectedFieldWidth();
			// fFieldHeight = renderStyle.getFieldHeight();
			// currentType = ESelectionType.SELECTION;
			// }
			// else {
			// continue;
			// }
			fYPosition -= fFieldHeight;
			fXPosition = 0;

//			GLHelperFunctions.drawPointAt(gl, 0, fYPosition, 0);

			for (Integer storageIndex : storageVA) {

				// if (currentType == ESelectionType.SELECTION) {
				// if (iCurrentMouseOverElement == iContentIndex) {
				// renderElement(gl, iStorageIndex, iContentIndex, fXPosition + fFieldWidth / 3,
				// fYPosition, fFieldWidth / 2, fFieldHeight);
				// }
				// else {
				// renderElement(gl, iStorageIndex, iContentIndex, fXPosition + fFieldWidth / 2f,
				// fYPosition, fFieldWidth / 2.5f, fFieldHeight);
				// }
				// }
				// else {
				renderElement(gl, storageIndex, iContentIndex, fXPosition, fYPosition, fFieldWidth,
					fFieldHeight / 2);
				// }

				fXPosition += fFieldWidth;

			}

			float fFontScaling = 0;

			boolean bRenderRefSeq = false;

			fFontScaling = renderStyle.getSmallFontScalingFactor();

			String sContent;
			String refSeq = null;

			if (set.getSetType() == ESetType.GENE_EXPRESSION_DATA) {
				sContent = GeneticIDMappingHelper.get().getShortNameFromExpressionIndex(iContentIndex);
				refSeq = GeneticIDMappingHelper.get().getRefSeqStringFromStorageIndex(iContentIndex);

				if (bRenderRefSeq) {
					sContent += " | ";
					// Render heat map element name
					sContent += refSeq;
				}
			}
			else if (set.getSetType() == ESetType.UNSPECIFIED) {
				sContent =
					generalManager.getIDMappingManager().getID(EMappingType.EXPRESSION_INDEX_2_UNSPECIFIED,
						iContentIndex);
			}
			else {
				throw new IllegalStateException("Label extraction for " + set.getSetType()
					+ " not implemented yet!");
			}

			if (sContent == null)
				sContent = "Unknown";

			// if (currentType == ESelectionType.SELECTION) {
			// if (iCurrentMouseOverElement == iContentIndex) {
			// iCurrentMouseOverElement = -1;
			// float fTextScalingFactor = 0.0035f;
			//
			// float fTextSpacing = 0.1f;
			// float fYSelectionOrigin =
			// -2 * fTextSpacing - (float) textRenderer.getBounds(sContent).getWidth()
			// * fTextScalingFactor;
			// float fSlectionFieldHeight = -fYSelectionOrigin + 0.01f;// renderStyle.getRenderHeight();
			//
			// // renderSelectionHighLight(gl, fXPosition,
			// // fYSelectionOrigin,
			// // fFieldWidth, fSlectionFieldHeight);
			//
			// gl.glColor3f(0.25f, 0.25f, 0.25f);
			// gl.glBegin(GL.GL_POLYGON);
			//
			// gl.glVertex3f(fXPosition + 0.03f, fYSelectionOrigin, 0.0005f);
			// gl.glVertex3f(fXPosition + fFieldWidth, fYSelectionOrigin, 0.0005f);
			// gl
			// .glVertex3f(fXPosition + fFieldWidth, fYSelectionOrigin + fSlectionFieldHeight,
			// 0.0005f);
			// gl.glVertex3f(fXPosition + 0.03f, fYSelectionOrigin + fSlectionFieldHeight, 0.0005f);
			//
			// gl.glEnd();
			//
			// textRenderer.setColor(1, 1, 1, 1);
			// gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
			// gl.glTranslatef(fXPosition + fFieldWidth / 1.5f, fYSelectionOrigin + fTextSpacing, 0);
			// gl.glRotatef(+fLineDegrees, 0, 0, 1);
			// textRenderer.begin3DRendering();
			// textRenderer.draw3D(sContent, 0, 0, 0.016f, fTextScalingFactor);
			// textRenderer.end3DRendering();
			// gl.glRotatef(-fLineDegrees, 0, 0, 1);
			// gl.glTranslatef(-fXPosition - fFieldWidth / 1.5f, -fYSelectionOrigin - fTextSpacing, 0);
			// // textRenderer.begin3DRendering();
			// gl.glPopAttrib();
			// }
			// else {
			// float fYSelectionOrigin = 0;
			// float fSlectionFieldHeight = -fYSelectionOrigin + 0.01f;// renderStyle.getRenderHeight();
			//
			// // renderSelectionHighLight(gl, fXPosition,
			// // fYSelectionOrigin,
			// // fFieldWidth, fSlectionFieldHeight);
			//
			// gl.glColor3f(0.25f, 0.25f, 0.25f);
			// gl.glBegin(GL.GL_POLYGON);
			//
			// gl.glVertex3f(fXPosition + 0.03f, fYSelectionOrigin, 0.0005f);
			// gl.glVertex3f(fXPosition + fFieldWidth, fYSelectionOrigin, 0.0005f);
			// gl
			// .glVertex3f(fXPosition + fFieldWidth, fYSelectionOrigin + fSlectionFieldHeight,
			// 0.0005f);
			// gl.glVertex3f(fXPosition + 0.03f, fYSelectionOrigin + fSlectionFieldHeight, 0.0005f);
			//
			// gl.glEnd();
			//
			// // textRenderer.setColor(1, 1, 1, 1);
			// // gl.glPushAttrib(GL.GL_CURRENT_BIT |
			// // GL.GL_LINE_BIT);
			// // gl.glTranslatef(fXPosition + fFieldWidth /
			// // 1.5f,
			// // fYSelectionOrigin + fTextSpacing, 0);
			// // gl.glRotatef(+fLineDegrees, 0, 0, 1);
			// // textRenderer.begin3DRendering();
			// // textRenderer
			// // .draw3D(sContent, 0, 0, 0.016f,
			// // fTextScalingFactor);
			// // textRenderer.end3DRendering();
			// // gl.glRotatef(-fLineDegrees, 0, 0, 1);
			// // gl.glTranslatef(-fXPosition - fFieldWidth /
			// // 1.5f,
			// // -fYSelectionOrigin - fTextSpacing, 0);
			// // // textRenderer.begin3DRendering()
			// textRenderer.setColor(1, 1, 1, 1);
			// renderCaption(gl, sContent, fXPosition + fFieldWidth / 2.2f - 0.01f,
			// 0 + 0.01f + HeatMapRenderStyle.LIST_SPACING, 0.02f, 0, fFontScaling);
			// gl.glPopAttrib();
			// // }
			// }
			// else {
			textRenderer.setColor(0, 0, 0, 1);
			renderCaption(gl, sContent, 0, fYPosition + fFieldHeight / 2, 0, 0, fFontScaling);
			// }

		}
		// renderStyle.setXDistanceAt(contentVA.indexOf(iContentIndex),
		// fXPosition);
		// fAlXDistances.add(fXPosition);

	}

	private void renderElement(final GL gl, final int iStorageIndex, final int iContentIndex,
		final float fXPosition, final float fYPosition, final float fFieldWidth, final float fFieldHeight) {

		float fLookupValue = set.get(iStorageIndex).getFloat(EDataRepresentation.NORMALIZED, iContentIndex);

		float fOpacity = 0;
		if (contentSelectionManager.checkStatus(ESelectionType.DESELECTED, iContentIndex)) {
			fOpacity = 0.3f;
		}
		else {
			fOpacity = 1.0f;
		}

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

	private void renderCaption(GL gl, String sLabel, float fXOrigin, float fYOrigin, float fZOrigin,
		float fRotation, float fFontScaling) {

		if (sLabel.length() > GeneralRenderStyle.NUM_CHAR_LIMIT + 1) {
			sLabel = sLabel.substring(0, GeneralRenderStyle.NUM_CHAR_LIMIT - 2);
			sLabel = sLabel + "..";
		}

		// textRenderer.setColor(0, 0, 0, 1);
		gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
		// gl.glTranslatef(fXOrigin, fYOrigin, fZOrigin);
		// gl.glRotatef(fRotation, 0, 0, 1);
		textRenderer.begin3DRendering();
		textRenderer.draw3D(sLabel, fXOrigin, fYOrigin, 0, fFontScaling);
		textRenderer.end3DRendering();
		// gl.glRotatef(-fRotation, 0, 0, 1);
		// gl.glTranslatef(-fXOrigin, -fYOrigin, -fZOrigin);
		// textRenderer.begin3DRendering();
		gl.glPopAttrib();
	}

	@Override
	protected void displayLocal(GL gl) {
		// TODO Auto-generated method stub

	}

	@Override
	public void displayRemote(GL gl) {
		// TODO Auto-generated method stub
		display(gl);
	}

	@Override
	public String getDetailedInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getShortInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void handleEvents(EPickingType ePickingType, EPickingMode ePickingMode, int iExternalID,
		Pick pick) {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(GL gl) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initLocal(GL gl) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initRemote(GL gl, AGLEventListener glParentView, GLMouseListener glMouseListener,
		IGLCanvasRemoteRendering remoteRenderingGLCanvas, GLInfoAreaManager infoAreaManager) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleUpdateView() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleVirtualArrayUpdate(IVirtualArrayDelta delta, String info) {
		if (delta.getVAType() == contentVAType)
			contentVA.setDelta(delta);
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		// TODO Auto-generated method stub
		return null;
	}

}
