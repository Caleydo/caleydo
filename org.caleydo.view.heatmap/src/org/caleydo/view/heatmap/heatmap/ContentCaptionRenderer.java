package org.caleydo.view.heatmap.heatmap;

import java.awt.Font;
import java.util.Set;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.manager.IIDMappingManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.view.heatmap.HeatMapRenderStyle;

public class ContentCaptionRenderer extends AContentRenderer {

	private CaleydoTextRenderer textRenderer;

	public ContentCaptionRenderer(GLHeatMap heatMap) {
		super(heatMap);
	
		textRenderer = new CaleydoTextRenderer(
				new Font("Arial", Font.PLAIN, 24), false);
	}



	public void renderContentCaptions(GL gl) {

		float yPosition = y;
		float xPosition = 0;
		float fieldHeight = 0;

		SelectionType currentType;
		float fFontScaling = 0;

		float fColumnDegrees = 0;
		float fLineDegrees = 0;

		fColumnDegrees = 90;// 60;
		fLineDegrees = 0;

		// render line captions
		// if (nfieldHeight > 0.055f) {
		boolean bRenderRefSeq = false;

		fFontScaling = GeneralRenderStyle.SMALL_FONT_SCALING_FACTOR;

		// bRenderRefSeq = true;
		String sContent = null;
		String refSeq = null;

		ContentVirtualArray contentVA = heatMap.getContentVA();

		for (Integer iContentIndex : contentVA) {

			boolean isSelected;
			if (heatMap.getContentSelectionManager().checkStatus(
					SelectionType.SELECTION, iContentIndex)
					|| heatMap.getContentSelectionManager().checkStatus(
							SelectionType.MOUSE_OVER, iContentIndex)) {
				fieldHeight = selectedFieldHeight;
				currentType = SelectionType.SELECTION;
				isSelected = true;
			} else {

				fieldHeight = normalFieldHeight;
				currentType = SelectionType.NORMAL;
				isSelected = false;
			}

		
			yPosition -= fieldHeight;

			sContent = getID(iContentIndex, false);
			if (sContent == null)
				sContent = "Unknown";

			textRenderer.setColor(0, 0, 0, 1);

			// if (heatMap.bClusterVisualizationGenesActive)
			// gl.glTranslatef(0, renderStyle.getWidthClusterVisualization(),
			// 0);

			// if (currentType == SelectionType.SELECTION
			// || currentType == SelectionType.MOUSE_OVER) {
			// renderCaption(gl, sContent, 0, yPosition + fieldHeight / 6
			// * 2.5f, 0, fLineDegrees, fFontScaling);
			// if (refSeq != null)
			// renderCaption(gl, refSeq, 0, yPosition + fieldHeight / 6
			// * 4.5f, 0, fLineDegrees, fFontScaling);
			// } else {
			renderCaption(gl, sContent, 0, yPosition + fieldHeight / 4, 0,
					fLineDegrees, fFontScaling);
			// }

			// if (heatMap.bClusterVisualizationGenesActive)
			// gl.glTranslatef(0, -renderStyle.getWidthClusterVisualization(),
			// 0);

		}
	}

	private String getID(Integer iContentIndex, boolean beVerbose) {
		String sContent = "";

		IIDMappingManager idMappingManager = GeneralManager.get()
				.getIDMappingManager();
		ESetType setType = heatMap.getSet().getSetType();
		if (setType == ESetType.GENE_EXPRESSION_DATA) {

			// FIXME: Due to new mapping system, a mapping involving
			// expression index can return a set of values,
			// depending on the IDType that has been specified when
			// loading expression data. Possibly a different
			// handling of the Set is required.
			Set<String> setGeneSymbols = idMappingManager.getIDAsSet(
					EIDType.EXPRESSION_INDEX, EIDType.GENE_SYMBOL,
					iContentIndex);

			if ((setGeneSymbols != null && !setGeneSymbols.isEmpty())) {
				sContent = (String) setGeneSymbols.toArray()[0];
			}

			if (sContent == null || sContent.equals(""))
				sContent = "Unkonwn Gene";

			// FIXME: Due to new mapping system, a mapping involving
			// expression index can return a set of values,
			// depending on the IDType that has been specified when
			// loading expression data. Possibly a different
			// handling of the Set is required.

			// GeneticIDMappingHelper.get().getRefSeqStringFromStorageIndex(iContentIndex);

			if (beVerbose) {
				Set<String> setRefSeqIDs = idMappingManager.getIDAsSet(
						EIDType.EXPRESSION_INDEX, EIDType.REFSEQ_MRNA,
						iContentIndex);

				if ((setRefSeqIDs != null && !setRefSeqIDs.isEmpty())) {
					String refSeq = (String) setRefSeqIDs.toArray()[0];
					sContent += " | ";
					// Render heat map element name
					sContent += refSeq;
				}
			}
		} else if (setType == ESetType.UNSPECIFIED) {
			sContent = idMappingManager.getID(EIDType.EXPRESSION_INDEX,
					EIDType.UNSPECIFIED, iContentIndex);
		} else {
			throw new IllegalStateException("Label extraction for " + setType
					+ " not implemented yet!");
		}

		return sContent;
	}

	private void renderCaption(GL gl, String sLabel, float fXOrigin,
			float fYOrigin, float fZOrigin, float fRotation, float fFontScaling) {
		// if (heatMap.isRenderedRemote()
		// && heatMap.getRemoteRenderingGLCanvas().getViewType().equals(
		// "org.caleydo.view.bucket"))
		// fFontScaling *= 1.5;
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

}
