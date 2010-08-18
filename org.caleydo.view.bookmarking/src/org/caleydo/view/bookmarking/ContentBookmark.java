package org.caleydo.view.bookmarking;

import javax.media.opengl.GL;

import org.caleydo.core.data.mapping.IDType;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;

import com.sun.opengl.util.j2d.TextRenderer;

/**
 * A bookmark for a gene. The id used here is DAVID
 * 
 * @author Alexander Lex
 */
class ContentBookmark extends ABookmark {

	/**
	 * Constructor taking a textRenderer
	 * 
	 * @param textRenderer
	 * @param id
	 */
	public ContentBookmark(GLBookmarkManager manager, IDType idType, Integer id,
			TextRenderer textRenderer) {
		super(manager, idType, textRenderer);
		this.id = id;
		dimensions.setHeight(0.1f);
	}

	@Override
	public void render(GL gl) {
		// String sContent = GeneralManager.get().getIDMappingManager().getID(
		// manager.getDataDomain().getPrimaryContentMappingType(),
		// EIDType.GENE_SYMBOL, id);
		//
		float yOrigin = dimensions.getYOrigin() - 0.08f;
		String sContent = manager.getDataDomain().getStorageLabel(idType, id);
		RenderingHelpers.renderText(gl, textRenderer, sContent, dimensions.getXOrigin()
				+ BookmarkRenderStyle.SIDE_SPACING, yOrigin,
				GeneralRenderStyle.SMALL_FONT_SCALING_FACTOR);

	}
	// fAlXDistances.clear();
	// renderStyle.updateFieldSizes();
	// float fXPosition = 0;
	// float fYPosition = viewFrustum.getBottom() + viewFrustum.getHeight();
	// float fFieldWidth = 0.1f;
	// float fFieldHeight = 0.3f;
	//
	// int iCurrentMouseOverElement = 0;
	// // renderStyle.clearFieldWidths();
	// // GLHelperFunctions.drawPointAt(gl, new Vec3f(1,0.2f,0));
	// int iCount = 0;
	// SelectionType currentType;
	// for (Integer iContentIndex : contentVA) {
	// iCount++;
	// // we treat normal and deselected the same atm
	//
	// currentType = SelectionType.NORMAL;
	//
	// // }
	// // else if (contentSelectionManager.checkStatus(SelectionType.SELECTION,
	// iContentIndex)
	// // || contentSelectionManager.checkStatus(SelectionType.MOUSE_OVER,
	// iContentIndex)) {
	// // fFieldWidth = renderStyle.getSelectedFieldWidth();
	// // fFieldHeight = renderStyle.getFieldHeight();
	// // currentType = SelectionType.SELECTION;
	// // }
	// // else {
	// // continue;
	// // }
	// fYPosition -= fFieldHeight;
	// fXPosition = 0;
	//
	// // GLHelperFunctions.drawPointAt(gl, 0, fYPosition, 0);
	//
	// for (Integer storageIndex : storageVA) {
	//
	// // if (currentType == SelectionType.SELECTION) {
	// // if (iCurrentMouseOverElement == iContentIndex) {
	// // renderElement(gl, iStorageIndex, iContentIndex, fXPosition +
	// fFieldWidth / 3,
	// // fYPosition, fFieldWidth / 2, fFieldHeight);
	// // }
	// // else {
	// // renderElement(gl, iStorageIndex, iContentIndex, fXPosition +
	// fFieldWidth / 2f,
	// // fYPosition, fFieldWidth / 2.5f, fFieldHeight);
	// // }
	// // }
	// // else {
	// renderElement(gl, storageIndex, iContentIndex, fXPosition, fYPosition,
	// fFieldWidth,
	// fFieldHeight / 2);
	// // }
	//
	// fXPosition += fFieldWidth;
	//
	// }
	//
	// float fFontScaling = 0;
	//
	// boolean bRenderRefSeq = false;
	//
	// fFontScaling = renderStyle.getSmallFontScalingFactor();
	//
	// String sContent;
	// String refSeq = null;
	//
	// if (set.getSetType() == ESetType.GENE_EXPRESSION_DATA) {
	// sContent =
	// GeneticIDMappingHelper.get().getShortNameFromExpressionIndex(iContentIndex);
	// refSeq =
	// GeneticIDMappingHelper.get().getRefSeqStringFromStorageIndex(iContentIndex);
	//
	// if (bRenderRefSeq) {
	// sContent += " | ";
	// // Render heat map element name
	// sContent += refSeq;
	// }
	// }
	// else if (set.getSetType() == ESetType.UNSPECIFIED) {
	// sContent =
	// generalManager.getIDMappingManager().getID(EMappingType.EXPRESSION_INDEX_2_UNSPECIFIED,
	// iContentIndex);
	// }
	// else {
	// throw new IllegalStateException("Label extraction for " +
	// set.getSetType()
	// + " not implemented yet!");
	// }
	//
	// if (sContent == null)
	// sContent = "Unknown";

	// if (currentType == SelectionType.SELECTION) {
	// if (iCurrentMouseOverElement == iContentIndex) {
	// iCurrentMouseOverElement = -1;
	// float fTextScalingFactor = 0.0035f;
	//
	// float fTextSpacing = 0.1f;
	// float fYSelectionOrigin =
	// -2 * fTextSpacing - (float) textRenderer.getBounds(sContent).getWidth()
	// * fTextScalingFactor;
	// float fSlectionFieldHeight = -fYSelectionOrigin + 0.01f;//
	// renderStyle.getRenderHeight();
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
	// .glVertex3f(fXPosition + fFieldWidth, fYSelectionOrigin +
	// fSlectionFieldHeight,
	// 0.0005f);
	// gl.glVertex3f(fXPosition + 0.03f, fYSelectionOrigin +
	// fSlectionFieldHeight, 0.0005f);
	//
	// gl.glEnd();
	//
	// textRenderer.setColor(1, 1, 1, 1);
	// gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
	// gl.glTranslatef(fXPosition + fFieldWidth / 1.5f, fYSelectionOrigin +
	// fTextSpacing, 0);
	// gl.glRotatef(+fLineDegrees, 0, 0, 1);
	// textRenderer.begin3DRendering();
	// textRenderer.draw3D(sContent, 0, 0, 0.016f, fTextScalingFactor);
	// textRenderer.end3DRendering();
	// gl.glRotatef(-fLineDegrees, 0, 0, 1);
	// gl.glTranslatef(-fXPosition - fFieldWidth / 1.5f, -fYSelectionOrigin -
	// fTextSpacing, 0);
	// // textRenderer.begin3DRendering();
	// gl.glPopAttrib();
	// }
	// else {
	// float fYSelectionOrigin = 0;
	// float fSlectionFieldHeight = -fYSelectionOrigin + 0.01f;//
	// renderStyle.getRenderHeight();
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
	// .glVertex3f(fXPosition + fFieldWidth, fYSelectionOrigin +
	// fSlectionFieldHeight,
	// 0.0005f);
	// gl.glVertex3f(fXPosition + 0.03f, fYSelectionOrigin +
	// fSlectionFieldHeight, 0.0005f);
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
	// textRenderer.setColor(0, 0, 0, 1);
	// renderCaption(gl, sContent, 0, fYPosition + fFieldHeight / 2, 0, 0,
	// fFontScaling);
	// }

	// }
	// renderStyle.setXDistanceAt(contentVA.indexOf(iContentIndex),
	// fXPosition);
	// fAlXDistances.add(fXPosition);

	// private void renderElement(final GL gl, final int iStorageIndex, final
	// int iContentIndex,
	// final float fXPosition, final float fYPosition, final float fFieldWidth,
	// final float fFieldHeight) {
	//
	// float fLookupValue =
	// set.get(iStorageIndex).getFloat(EDataRepresentation.NORMALIZED,
	// iContentIndex);
	//
	// float fOpacity = 0;
	// if (contentSelectionManager.checkStatus(SelectionType.DESELECTED,
	// iContentIndex)) {
	// fOpacity = 0.3f;
	// }
	// else {
	// fOpacity = 1.0f;
	// }
	//
	// float[] fArMappingColor = colorMapper.getColor(fLookupValue);
	//
	// gl.glColor4f(fArMappingColor[0], fArMappingColor[1], fArMappingColor[2],
	// fOpacity);
	//
	// gl.glPushName(pickingManager.getPickingID(iUniqueID,
	// EPickingType.HEAT_MAP_STORAGE_SELECTION,
	// iStorageIndex));
	// gl.glPushName(pickingManager.getPickingID(iUniqueID,
	// EPickingType.HEAT_MAP_LINE_SELECTION,
	// iContentIndex));
	// gl.glBegin(GL.GL_POLYGON);
	// gl.glVertex3f(fXPosition, fYPosition, FIELD_Z);
	// gl.glVertex3f(fXPosition + fFieldWidth, fYPosition, FIELD_Z);
	// gl.glVertex3f(fXPosition + fFieldWidth, fYPosition + fFieldHeight,
	// FIELD_Z);
	// gl.glVertex3f(fXPosition, fYPosition + fFieldHeight, FIELD_Z);
	// gl.glEnd();
	//
	// gl.glPopName();
	// gl.glPopName();
	// }
	//
	// private void renderCaption(GL gl, String sLabel, float fXOrigin, float
	// fYOrigin, float fZOrigin,
	// float fRotation, float fFontScaling) {
	//
	// if (sLabel.length() > GeneralRenderStyle.NUM_CHAR_LIMIT + 1) {
	// sLabel = sLabel.substring(0, GeneralRenderStyle.NUM_CHAR_LIMIT - 2);
	// sLabel = sLabel + "..";
	// }
	//
	// gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
	// textRenderer.begin3DRendering();
	// textRenderer.draw3D(sLabel, fXOrigin, fYOrigin, 0, fFontScaling);
	// textRenderer.end3DRendering();
	// gl.glPopAttrib();
	// }

}
