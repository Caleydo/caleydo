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
package org.caleydo.view.radial;

import gleem.linalg.Vec2f;
import java.util.ArrayList;
import javax.media.opengl.GL2;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;

/**
 * This class represents one line in a label. It consists of label items, which
 * actually hold the content that shall be displayed.
 * 
 * @author Christian Partl
 */
public class LabelLine {

	private static final float DEFAULT_LINE_HEIGHT = 0.1f;

	private ArrayList<ALabelItem> alLabelItems;
	private float fWidth;
	private float fHeight;
	private Vec2f vecPosition;

	/**
	 * Constructor.
	 */
	public LabelLine() {
		alLabelItems = new ArrayList<ALabelItem>();
		vecPosition = new Vec2f(0, 0);
		fWidth = 0;
		fHeight = DEFAULT_LINE_HEIGHT;
	}

	/**
	 * Adds a label item at the end of the line.
	 * 
	 * @param labelItem
	 *            Item that shall e added.
	 */
	public void addLabelItem(ALabelItem labelItem) {
		alLabelItems.add(labelItem);
	}

	/**
	 * Calculates the size of the label line. It also sets the height of each
	 * Label item to the height of the line. The height of the line is
	 * determined by the item with the largest height.
	 * 
	 * @param textRenderer
	 *            TextRenderer object which shall be used by text items to
	 *            render and determine their size.
	 * @param fTextScaling
	 *            Font scaling which shall be used by text items to render and
	 *            determine their size.
	 * @param iConsideredLabelTypes
	 *            Bitmask which determines the types of label items that should
	 *            be taken into consideration when determining the line's
	 *            height.
	 */
	public void calculateSize(GL2 gl, CaleydoTextRenderer textRenderer,
			float fTextScaling, int iConsideredLabelTypes) {

		fHeight = 0;
		fWidth = 0;

		for (ALabelItem currentItem : alLabelItems) {
			if (currentItem instanceof TextItem) {
				((TextItem) currentItem).setRenderingProperties(gl, textRenderer,
						fTextScaling);
			}

			if ((iConsideredLabelTypes & currentItem.getLabelItemType()) > 0) {
				float fItemHeight = currentItem.getHeight();
				if (fItemHeight > fHeight) {
					fHeight = fItemHeight;
				}
			}
		}

		if (fHeight <= 0) {
			fHeight = DEFAULT_LINE_HEIGHT;
		}

		for (ALabelItem currentItem : alLabelItems) {
			currentItem.setHeight(fHeight);
			fWidth += currentItem.getWidth();
		}
	}

	/**
	 * Draws all items of this line.
	 * 
	 * @param gl
	 *            GL2 object that shall be used for drawing.
	 */
	public void draw(GL2 gl) {

		for (ALabelItem currentItem : alLabelItems) {
			currentItem.draw(gl);
		}
	}

	/**
	 * @return Height of the line. The method calculateSize must have been
	 *         called before in order to obtain a proper value.
	 */
	public float getHeight() {
		return fHeight;
	}

	/**
	 * @return Width of the line. The method calculateSize must have been called
	 *         before in order to obtain a proper value.
	 */
	public float getWidth() {
		return fWidth;
	}

	/**
	 * Sets the line's position.
	 * 
	 * @param fXPosition
	 *            X coordinate of the line.
	 * @param fYPosition
	 *            Y coordinate of the line.
	 */
	public void setPosition(float fXPosition, float fYPosition) {
		vecPosition.set(fXPosition, fYPosition);

		for (ALabelItem currentItem : alLabelItems) {
			currentItem.setPosition(fXPosition, fYPosition);
			fXPosition += currentItem.getWidth();
		}
	}

	/**
	 * @return The current position of the line.
	 */
	public Vec2f getPosition() {
		return vecPosition;
	}
}
