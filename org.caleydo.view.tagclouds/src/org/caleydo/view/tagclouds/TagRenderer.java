package org.caleydo.view.tagclouds;

import java.util.Set;

import javax.media.opengl.GL2;

import org.caleydo.core.data.selection.RecordSelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;

public class TagRenderer extends LayoutRenderer {
	private CaleydoTextRenderer textRenderer;
	private String text = "";

	private GLTagCloud tagCloud;
	private RecordSelectionManager contentSelectionManager;
	private Integer dimensionID;

	private float color[];

	private boolean isEven = false;
	private boolean allowTextScaling = false;

	public TagRenderer(CaleydoTextRenderer textRenderer, String text, GLTagCloud tagCloud) {
		this.textRenderer = textRenderer;
		this.text = text;
		this.tagCloud = tagCloud;
		color = new float[] { 0, 0, 0 };
	}

	public TagRenderer(CaleydoTextRenderer textRenderer, GLTagCloud tagCloud,
			Integer dimensionID) {
		this.textRenderer = textRenderer;
		this.tagCloud = tagCloud;
		this.contentSelectionManager = tagCloud.getContentSelectionManager();
		this.dimensionID = dimensionID;
		color = SelectionType.MOUSE_OVER.getColor();
	}

	public void setEven(boolean isEven) {
		this.isEven = isEven;
	}

	public void setAllowTextScaling(boolean allowTextScaling) {
		this.allowTextScaling = allowTextScaling;
	}

	public void selectionUpdated() {
		Set<Integer> selectedElements = contentSelectionManager
				.getElements(SelectionType.MOUSE_OVER);
		if (selectedElements.isEmpty())
			return;

		int recordID = -1;
		for (Integer tempID : selectedElements) {
			recordID = tempID;
			break;
		}

		if (!tagCloud.getRecordVA().contains(recordID))
			return;
		text = tagCloud.getTable().getRawAsString(dimensionID, recordID);

	}

	public void render(GL2 gl) {

		float sideSpacing = 0.1f;
		float topSpacing = 0.03f;

		if (isEven) {
			gl.glColor3f(0.9f, 0.9f, 0.9f);
			gl.glBegin(GL2.GL_POLYGON);
			gl.glVertex3f(0, 0, -.001f);
			gl.glVertex3f(0, y, -.001f);
			gl.glVertex3f(x, y, -.001f);
			gl.glVertex3f(x, 0, -.001f);
			gl.glEnd();
		}
		textRenderer.setColor(color);

		float maxHeight = tagCloud.getPixelGLConverter().getGLHeightForPixelHeight(50);
		if (allowTextScaling && y > maxHeight) {
			float renderHeight = maxHeight;

			topSpacing = (y - renderHeight) / 2;
			textRenderer.renderTextInBounds(gl, text, sideSpacing, topSpacing, 0, x - 3
					* sideSpacing, renderHeight);
		} else {
			textRenderer.renderTextInBounds(gl, text, sideSpacing, topSpacing / 2, 0, x
					- 3 * sideSpacing, y - topSpacing);
		}
	};

}
