package org.caleydo.view.tagclouds;

import java.util.Set;

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.storage.NominalStorage;
import org.caleydo.core.data.selection.ContentSelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.util.text.MinSizeTextRenderer;

public class StorageCaptionRenderer extends LayoutRenderer {
	private MinSizeTextRenderer textRenderer;
	private String text = "";

	
	private float color[] = {44f/256,162f/256,95f/256};


	public StorageCaptionRenderer(MinSizeTextRenderer textRenderer, String text) {
		this.textRenderer = textRenderer;
		this.text = text;
	}


	public void render(GL2 gl) {

		float sideSpacing = 0.1f;
		float topSpacing = 0.03f;

		
			gl.glColor3f(0.9f, 0.9f, 0.9f);
			gl.glBegin(GL2.GL_POLYGON);
			gl.glVertex3f(0, 0, -.001f);
			gl.glVertex3f(0, y, -.001f);
			gl.glColor3f(0.7f, 0.7f, 0.7f);
			gl.glVertex3f(x, y, -.001f);
			gl.glVertex3f(x, 0, -.001f);
			gl.glEnd();
		
		textRenderer.setWindowSize(x, y);
		textRenderer.setColor(color);

		textRenderer.renderTextInBounds(gl, text, sideSpacing, topSpacing / 2, 0, x
				- sideSpacing, y - topSpacing);

	};

}
