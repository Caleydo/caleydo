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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import org.caleydo.core.view.opengl.renderstyle.infoarea.AInfoOverlayRenderStyle;
import com.jogamp.opengl.util.awt.Overlay;

/**
 * Class implements the overlay info area.
 * 
 * @author Marc Streit
 * @deprecated Use status line + tooltip approach
 */
@Deprecated
public class GLOverlayInfoRenderer {

	private Overlay glOverlay;

	private Font font;

	private ArrayList<String> sAlContent;

	/**
	 * Constructor.
	 */
	public GLOverlayInfoRenderer() {
		font = new Font("Courier", Font.BOLD, 16);
		sAlContent = new ArrayList<String>();
	}

	public void init(final GLAutoDrawable drawable) {

		glOverlay = new Overlay(drawable);
	}

	/**
	 * Set the data to be rendered.
	 * 
	 * @param uniqueID
	 * @param eInputDataTypes
	 * @param pickedPoint
	 */
	public void setData(final ArrayList<String> sAlContent) {

		this.sAlContent = sAlContent;
	}

	public void render(final GLAutoDrawable drawable) {

		GL2 gl = drawable.getGL().getGL2();

		int iLineCount = 0;

		int viewport[] = new int[4];
		gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);

		int panelHeight = 0;
		if (sAlContent.isEmpty()) {
			panelHeight = 0;
		}
		else {
			panelHeight = (sAlContent.size() + 1) * AInfoOverlayRenderStyle.LINE_HEIGHT;
		}

		if (panelHeight > AInfoOverlayRenderStyle.MAX_OVERLAY_HEIGHT) {
			panelHeight = AInfoOverlayRenderStyle.MAX_OVERLAY_HEIGHT;
		}

		if (drawable.getWidth() == 0 || drawable.getHeight() == 0)
			return;

		Graphics2D g2d = glOverlay.createGraphics();
		g2d.setComposite(AlphaComposite.Src);

		int iXPos = (viewport[2] - AInfoOverlayRenderStyle.OVERLAY_WIDTH) / 2;
		int iYPos = 0;

		// Flush info area
		g2d.setColor(new Color(1, 1, 1, 0));
		g2d.fillRect(iXPos, iYPos, AInfoOverlayRenderStyle.OVERLAY_WIDTH,
			AInfoOverlayRenderStyle.MAX_OVERLAY_HEIGHT);

		g2d.setColor(AInfoOverlayRenderStyle.backgroundColor);
		g2d.fillRect(iXPos, iYPos, AInfoOverlayRenderStyle.OVERLAY_WIDTH, panelHeight);

		g2d.setColor(AInfoOverlayRenderStyle.borderColor);
		g2d.drawRect(iXPos, iYPos, AInfoOverlayRenderStyle.OVERLAY_WIDTH - 1, panelHeight);

		g2d.setColor(AInfoOverlayRenderStyle.fontColor);
		g2d.setFont(font);

		Iterator<String> iterContentCreator = sAlContent.iterator();

		while (iterContentCreator.hasNext()) {
			iLineCount++;
			g2d.drawString(iterContentCreator.next(),
				(viewport[2] - AInfoOverlayRenderStyle.OVERLAY_WIDTH) / 2 + 10, iLineCount
					* AInfoOverlayRenderStyle.LINE_HEIGHT);
		}

		// render all the overlay to the screen
		glOverlay.markDirty((viewport[2] - AInfoOverlayRenderStyle.OVERLAY_WIDTH) / 2, 0,
			AInfoOverlayRenderStyle.OVERLAY_WIDTH, AInfoOverlayRenderStyle.MAX_OVERLAY_HEIGHT);
		glOverlay.drawAll();
		g2d.dispose();
	}
}
