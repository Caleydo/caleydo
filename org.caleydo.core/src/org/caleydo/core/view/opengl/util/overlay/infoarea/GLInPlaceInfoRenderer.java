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
package org.caleydo.core.view.opengl.util.overlay.infoarea;

import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import javax.media.opengl.GL2;
import org.caleydo.core.id.IDType;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.miniview.AGLMiniView;
import org.caleydo.core.view.opengl.renderstyle.InfoAreaRenderStyle;
import com.jogamp.opengl.util.awt.TextRenderer;

/**
 * Info Area LayoutRenderer. Renders an info area. It needs only an id, a data type and a gl context, and
 * renders the information.
 * 
 * @author Alexander Lex
 */

public class GLInPlaceInfoRenderer {

	private TextRenderer textRenderer;

	private ArrayList<String> sContent;

	// private Point pickedPoint;
	private AGLMiniView miniView;

	private InformationContentCreator contentCreator;

	private Vec2f vecSize;

	private float fHeight = 0;

	private float fWidth = 0;

	private float fTextWidth;

	private float fSpacing = 0;

	// private float fZValue = 0.005f;

	private InfoAreaRenderStyle renderStyle;

	/**
	 * Constructor
	 */
	public GLInPlaceInfoRenderer(ViewFrustum viewFrustum) {
		textRenderer = new TextRenderer(new Font("Arial", Font.BOLD, 16), false);
		contentCreator = new InformationContentCreator();
		renderStyle = new InfoAreaRenderStyle(viewFrustum);
		fSpacing = renderStyle.getSpacing();
	}

	/**
	 * Set the data to be rendered.
	 * 
	 * @param iCaleydoID
	 * @param inputDataTypes
	 * @param pickedPoint
	 */
	public void setData(int iCaleydoID, IDType inputDataTypes) {

		this.sContent = contentCreator.getStringContentForID(iCaleydoID, inputDataTypes);
		// this.pickedPoint = pickedPoint;
		// miniView = new GLParCoordsMiniView();
		fHeight = 0;
		fWidth = 0;
		vecSize = new Vec2f();
		calculateWidthAndHeight();
	}

	/**
	 * Render the data previously set
	 * 
	 * @param gl
	 * @param bFirstTime
	 *            this has to be true only the first time you render it and can never be true after that
	 */
	public void renderInfoArea(GL2 gl, Vec3f vecLowerLeft, boolean bFirstTime, float fZValue) {

		String sCurrent;
		float fXLowerLeft = vecLowerLeft.x();
		float fYLowerLeft = vecLowerLeft.y();

		int iCount = 0;
		while (iCount < 2) {
			if (iCount == 0) {
				gl.glColor4fv(InfoAreaRenderStyle.INFO_AREA_COLOR, 0);

				gl.glBegin(GL2.GL_POLYGON);
			}
			else {
				gl.glColor4fv(InfoAreaRenderStyle.INFO_AREA_BORDER_COLOR, 0);
				gl.glLineWidth(InfoAreaRenderStyle.INFO_AREA_BORDER_WIDTH);
				gl.glBegin(GL2.GL_LINE_STRIP);
			}
			gl.glVertex3f(fXLowerLeft, fYLowerLeft, fZValue);
			gl.glVertex3f(fXLowerLeft + fWidth, fYLowerLeft, fZValue);
			gl.glVertex3f(fXLowerLeft + fWidth, fYLowerLeft + fHeight, fZValue);
			gl.glVertex3f(fXLowerLeft, fYLowerLeft + fHeight, fZValue);
			if (iCount == 1) {
				gl.glVertex3f(fXLowerLeft, fYLowerLeft, fZValue);
			}
			gl.glEnd();
			iCount++;
		}

		textRenderer.setColor(1f, 1f, 1f, 1);

		float fYUpperLeft = fYLowerLeft + fHeight;

		float fNextLineHeight = fYUpperLeft;

		textRenderer.begin3DRendering();

		Iterator<String> contentIterator = sContent.iterator();
		iCount = 0;

		float fFontScaling = 0.01f;
		while (contentIterator.hasNext()) {
			if (iCount == 1) {
				fFontScaling = 0.005f;
			}
			sCurrent = contentIterator.next();
			fNextLineHeight -= (float) textRenderer.getBounds(sCurrent).getHeight() * fFontScaling + fSpacing;

			textRenderer.draw3D(sCurrent, fXLowerLeft + fSpacing, fNextLineHeight, fZValue + 0.001f,
				fFontScaling);

			iCount++;
		}
		textRenderer.end3DRendering();

		if (miniView != null) {
			miniView.render(gl, fXLowerLeft + fTextWidth + fSpacing, fYLowerLeft + fSpacing, 0);
		}

		// gl.glVertex3f(fXOrigin, fYOrigin, 0);

	}

	public float getWidth() {

		return fWidth;
	}

	public float getHeight() {

		return fHeight;
	}

	private void calculateWidthAndHeight() {

		String sCurrent;

		Rectangle2D box;
		float fTemp;

		Iterator<String> contentIterator = sContent.iterator();
		int iCount = 0;
		float fFontScalingFactor = 0.01f;
		while (contentIterator.hasNext()) {

			sCurrent = contentIterator.next();
			if (iCount == 1) {
				fFontScalingFactor = 0.005f;
			}

			box = textRenderer.getBounds(sCurrent).getBounds2D();
			fHeight += box.getHeight() * fFontScalingFactor;

			fTemp = (float) box.getWidth() * fFontScalingFactor;

			if (fTemp > fWidth) {
				fWidth = fTemp;
			}
			fHeight += fSpacing;

			iCount++;

		}
		fWidth += 2 * fSpacing;
		fHeight += 2 * fSpacing;

		fTextWidth = fWidth;
		if (miniView != null) {
			fWidth += miniView.getWidth() + fSpacing * 2;

			if (fHeight < miniView.getHeight()) {
				fHeight = miniView.getHeight();
			}

			fHeight += fSpacing * 2;
		}

		vecSize.set(fWidth, fHeight);
	}
}