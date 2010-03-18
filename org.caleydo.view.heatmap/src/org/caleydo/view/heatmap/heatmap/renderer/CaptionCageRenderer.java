package org.caleydo.view.heatmap.heatmap.renderer;

import javax.media.opengl.GL;

import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;

public class CaptionCageRenderer extends AContentRenderer {

	public CaptionCageRenderer(GLHeatMap heatMap) {
		super(heatMap);
	}

	public void render(GL gl) {

		float yPosition = y;
		float xPosition = 0;
		float fieldHeight = 0;

		gl.glColor3f(0.6f, 0.6f, 0.6f);
		gl.glLineWidth(2);

		if (!heatMap.isCaptionsImpossible()) {		

			ContentVirtualArray contentVA = heatMap.getContentVA();

			for (Integer iContentIndex : contentVA) {

				if (heatMap.getContentSelectionManager().checkStatus(
						SelectionType.SELECTION, iContentIndex)
						|| heatMap.getContentSelectionManager().checkStatus(
								SelectionType.MOUSE_OVER, iContentIndex)) {
					fieldHeight = selectedFieldHeight;

				} else if (heatMap.isHideElements()
						&& heatMap.getContentSelectionManager().checkStatus(
								GLHeatMap.SELECTION_HIDDEN, iContentIndex)) {
					continue;
				} else {

					fieldHeight = normalFieldHeight;
				}

				gl.glBegin(GL.GL_LINE_STRIP);
				gl.glVertex3f(xPosition, yPosition, 0);
				// gl.glVertex3f(xPosition , yPosition - fieldHeight, 0);
				// gl.glVertex3f(xPosition + x, yPosition - fieldHeight, 0);
				gl.glVertex3f(xPosition + x, yPosition, 0);
				gl.glEnd();

				yPosition -= fieldHeight;

			}
		}
		gl.glBegin(GL.GL_LINE_STRIP);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(x, 0, 0);
		gl.glVertex3f(x, y, 0);
		gl.glVertex3f(0, y, 0);

		gl.glEnd();

	}
}
