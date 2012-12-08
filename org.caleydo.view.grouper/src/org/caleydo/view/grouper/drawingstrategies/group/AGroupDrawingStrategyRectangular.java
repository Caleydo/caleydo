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
package org.caleydo.view.grouper.drawingstrategies.group;

import gleem.linalg.Vec3f;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Set;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.util.AGLGUIElement;
import org.caleydo.core.view.opengl.util.draganddrop.IDraggable;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.view.grouper.GrouperRenderStyle;
import org.caleydo.view.grouper.compositegraphic.GroupRepresentation;
import org.caleydo.view.grouper.compositegraphic.ICompositeGraphic;

import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;

public abstract class AGroupDrawingStrategyRectangular extends AGLGUIElement implements
		IGroupDrawingStrategy {

	private static final String sTextForHeightCalculation = "Text without characters below the bottom textline";

	private TextureManager textureManager;

	public AGroupDrawingStrategyRectangular() {
		setMinSize(GrouperRenderStyle.GUI_ELEMENT_MIN_SIZE);
		textureManager = new TextureManager();
	}

	@Override
	public abstract void draw(GL2 gl, GroupRepresentation groupRepresentation,
			TextRenderer textRenderer);

	@Override
	public void calculateDrawingParameters(GL2 gl, TextRenderer textRenderer,
			GroupRepresentation groupRepresentation) {

		ArrayList<ICompositeGraphic> alChildren = groupRepresentation.getChildren();

		calculateDimensions(gl, textRenderer, groupRepresentation);

		if (!groupRepresentation.isCollapsed()) {
			for (ICompositeGraphic child : alChildren) {
				child.setToMaxWidth(groupRepresentation.getWidth()
						- GrouperRenderStyle.ELEMENT_LEFT_SPACING,
						GrouperRenderStyle.ELEMENT_LEFT_SPACING);
			}
		}

	}

	@Override
	public void calculateDimensions(GL2 gl, TextRenderer textRenderer,
			GroupRepresentation groupRepresentation) {

		float fMaxChildWidth = 0.0f;
		float fHeight = getTextHeight(textRenderer);
		float fWidth = 0.0f;

		ArrayList<ICompositeGraphic> alChildren = groupRepresentation.getChildren();
		ArrayList<Float> alDropPositions = groupRepresentation.getDropPositions();

		alDropPositions.clear();

		if (!groupRepresentation.isCollapsed()) {
			alDropPositions
					.add(fHeight + (GrouperRenderStyle.ELEMENT_TOP_SPACING / 2.0f));

			for (int i = 0; i < alChildren.size(); i++) {

				ICompositeGraphic child = alChildren.get(i);

				fHeight += GrouperRenderStyle.ELEMENT_TOP_SPACING;

				child.calculateDimensions(gl, textRenderer);

				float fCurrentChildWidth = child.getWidth();

				if (fCurrentChildWidth > fMaxChildWidth)
					fMaxChildWidth = fCurrentChildWidth;

				fHeight += child.getHeight() + GrouperRenderStyle.ELEMENT_BOTTOM_SPACING;

				if (i == alChildren.size() - 1) {
					alDropPositions.add(fHeight
							- GrouperRenderStyle.ELEMENT_BOTTOM_SPACING / 2.0f);
				} else {
					alDropPositions.add(fHeight);
				}
			}
		} else {
			fHeight += GrouperRenderStyle.ELEMENT_BOTTOM_SPACING;
			alDropPositions.add(fHeight
					- (GrouperRenderStyle.ELEMENT_BOTTOM_SPACING / 2.0f));
		}

		Rectangle2D bounds = textRenderer.getBounds(groupRepresentation.getName());
		float fTextWidth = (float) bounds.getWidth() * GrouperRenderStyle.TEXT_SCALING
				+ 2.0f * GrouperRenderStyle.TEXT_SPACING;

		fWidth = Math.max(fTextWidth, fMaxChildWidth)
				+ GrouperRenderStyle.ELEMENT_LEFT_SPACING;

		groupRepresentation.setHeight(fHeight);
		groupRepresentation.setWidth(fWidth);
	}

	@Override
	public void calculateDimensionsOfLeaf(GL2 gl, TextRenderer textRenderer,
			GroupRepresentation groupRepresentation) {
		float fHeight = 0.0f;
		float fWidth = 0.0f;

		Rectangle2D bounds = textRenderer.getBounds(sTextForHeightCalculation);
		fHeight = (float) bounds.getHeight() * GrouperRenderStyle.TEXT_SCALING + 2.0f
				* GrouperRenderStyle.TEXT_SPACING;
		bounds = textRenderer.getBounds(groupRepresentation.getName());
		fWidth = (float) bounds.getWidth() * GrouperRenderStyle.TEXT_SCALING + 2.0f
				* GrouperRenderStyle.TEXT_SPACING;

		groupRepresentation.setHeight(fHeight);
		groupRepresentation.setWidth(fWidth);
	}

	protected void drawGroupRectangular(GL2 gl, GroupRepresentation groupRepresentation,
			TextRenderer textRenderer) {

		Vec3f vecPosition = groupRepresentation.getPosition();

		drawBackgroudRectangle(gl, groupRepresentation, vecPosition);

		beginGUIElement(gl, groupRepresentation.getHierarchyPosition());

		float[] text_color = GrouperRenderStyle.TEXT_COLOR;
		float fTextHeight = getTextHeight(textRenderer);

		textRenderer.setColor(text_color[0], text_color[1], text_color[2], text_color[3]);

		textRenderer.begin3DRendering();

		textRenderer.draw3D(groupRepresentation.getName(), vecPosition.x()
				+ GrouperRenderStyle.ELEMENT_LEFT_SPACING
				+ GrouperRenderStyle.TEXT_SPACING, vecPosition.y() - fTextHeight
				+ GrouperRenderStyle.TEXT_SPACING, vecPosition.z(),
				GrouperRenderStyle.TEXT_SCALING);
		textRenderer.flush();

		textRenderer.end3DRendering();

		gl.glPopAttrib();

		endGUIElement(gl);
	}

	protected void drawCollapseButton(GL2 gl, GroupRepresentation groupRepresentation,
			TextRenderer textRenderer) {

		float fTextHeight = getTextHeight(textRenderer);
		Vec3f vecPosition = groupRepresentation.getPosition();

		gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT);

		beginGUIElement(gl, groupRepresentation.getHierarchyPosition());

		float fCollapseButtonSize = 0.8f * fTextHeight;
		float fCollapseButtonPositionX = vecPosition.x()
				+ (GrouperRenderStyle.ELEMENT_LEFT_SPACING / 2.0f) - fCollapseButtonSize
				/ 2.0f;
		float fCollapseButtonPositionY = vecPosition.y() - 0.1f * fTextHeight - 0.01f;

		EIconTextures icon = null;
		if (groupRepresentation.isCollapsed())
			icon = EIconTextures.GROUPER_COLLAPSE_PLUS;
		else
			icon = EIconTextures.GROUPER_COLLAPSE_MINUS;

		Texture tempTexture = textureManager.getIconTexture(icon);

		tempTexture.enable(gl);
		tempTexture.bind(gl);
		TextureCoords texCoords = tempTexture.getImageTexCoords();

		gl.glColor4f(1, 1, 1, 1);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(fCollapseButtonPositionX, fCollapseButtonPositionY,
				vecPosition.z() + 0.01f);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(fCollapseButtonPositionX + fCollapseButtonSize,
				fCollapseButtonPositionY, vecPosition.z() + 0.01f);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(fCollapseButtonPositionX + fCollapseButtonSize,
				fCollapseButtonPositionY - fCollapseButtonSize, vecPosition.z() + 0.01f);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(fCollapseButtonPositionX, fCollapseButtonPositionY
				- fCollapseButtonSize, vecPosition.z() + 0.01f);
		gl.glEnd();

		tempTexture.disable(gl);

		endGUIElement(gl);

	}

	protected void drawRectangularBorder(GL2 gl, GroupRepresentation groupRepresentation,
			TextRenderer textRenderer) {

		Vec3f vecPosition = groupRepresentation.getPosition();
		float fHeight = groupRepresentation.getHeight();
		float fWidth = groupRepresentation.getWidth();

		beginGUIElement(gl, groupRepresentation.getHierarchyPosition());

		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(vecPosition.x(), vecPosition.y(), vecPosition.z());
		gl.glVertex3f(vecPosition.x() + fWidth, vecPosition.y(), vecPosition.z());
		gl.glVertex3f(vecPosition.x() + fWidth, vecPosition.y() - fHeight,
				vecPosition.z());
		gl.glVertex3f(vecPosition.x(), vecPosition.y() - fHeight, vecPosition.z());
		gl.glEnd();

		endGUIElement(gl);
	}

	protected void drawBackgroudRectangle(GL2 gl,
			GroupRepresentation groupRepresentation, Vec3f vecPosition) {

		float fHeight = groupRepresentation.getHeight();
		float fWidth = groupRepresentation.getWidth();

		beginGUIElement(gl, groupRepresentation.getHierarchyPosition());

		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex3f(vecPosition.x(), vecPosition.y(), vecPosition.z());
		gl.glVertex3f(vecPosition.x() + fWidth, vecPosition.y(), vecPosition.z());
		gl.glVertex3f(vecPosition.x() + fWidth, vecPosition.y() - fHeight,
				vecPosition.z());
		gl.glVertex3f(vecPosition.x(), vecPosition.y() - fHeight, vecPosition.z());
		gl.glEnd();

		endGUIElement(gl);

	}

	protected void drawChildren(GL2 gl, GroupRepresentation groupRepresentation,
			TextRenderer textRenderer) {

		if (groupRepresentation.isCollapsed())
			return;

		Vec3f vecPosition = groupRepresentation.getPosition();
		ArrayList<ICompositeGraphic> alChildren = groupRepresentation.getChildren();
		float fTextHeight = getTextHeight(textRenderer);

		float fCurrentChildYPosition = vecPosition.y() - fTextHeight;

		for (int i = 0; i < alChildren.size(); i++) {

			ICompositeGraphic child = alChildren.get(i);

			fCurrentChildYPosition -= GrouperRenderStyle.ELEMENT_TOP_SPACING;
			child.setPosition(new Vec3f(vecPosition.x()
					+ GrouperRenderStyle.ELEMENT_LEFT_SPACING, fCurrentChildYPosition,
					vecPosition.z() + 0.001f));
			child.draw(gl, textRenderer);

			fCurrentChildYPosition -= child.getHeight()
					+ GrouperRenderStyle.ELEMENT_BOTTOM_SPACING;

		}
	}

	public int getClosestDropPositionIndex(GL2 gl,
			GroupRepresentation groupRepresentation, Set<IDraggable> setDraggables,
			float fMouseCoordinateY) {

		Vec3f vecPosition = groupRepresentation.getPosition();
		ArrayList<Float> alDropPositions = groupRepresentation.getDropPositions();
		ArrayList<ICompositeGraphic> alChildren = groupRepresentation.getChildren();
		float fMinDistanceFromDropPosition = Float.MAX_VALUE;
		int iDropPositionIndex = -1;

		if (groupRepresentation.isLeaf())
			return -1;

		for (int i = 0; i < alDropPositions.size(); i++) {
			Float fDropPosition = getScaledCoordinate(gl, vecPosition.y()
					- alDropPositions.get(i), groupRepresentation.getHierarchyPosition()
					.y());
			float fCurrentDistanceFromDropPosition = Math.abs(fMouseCoordinateY
					- fDropPosition);

			if (fCurrentDistanceFromDropPosition < fMinDistanceFromDropPosition) {
				fMinDistanceFromDropPosition = fCurrentDistanceFromDropPosition;
				iDropPositionIndex = i;
			}
		}

		ICompositeGraphic childNearDropPositionLower = null;
		ICompositeGraphic childNearDropPositionUpper = null;

		if (groupRepresentation.isCollapsed()) {
			childNearDropPositionUpper = null;
			childNearDropPositionLower = alChildren.get(0);
		} else {
			if (iDropPositionIndex == alDropPositions.size() - 1) {
				childNearDropPositionLower = null;
			} else {
				childNearDropPositionLower = alChildren.get(iDropPositionIndex);
			}
			if (iDropPositionIndex == 0) {
				childNearDropPositionUpper = null;
			} else {
				childNearDropPositionUpper = alChildren.get(iDropPositionIndex - 1);
			}
		}

		for (IDraggable draggable : setDraggables) {
			if (draggable == childNearDropPositionLower
					|| draggable == childNearDropPositionUpper)
				return -1;
		}

		return iDropPositionIndex;
	}

	public void drawDropPositionMarker(GL2 gl, GroupRepresentation groupRepresentation,
			int iDropPositionIndex) {

		Vec3f vecPosition = groupRepresentation.getPosition();
		ArrayList<Float> alDropPositions = groupRepresentation.getDropPositions();

		beginGUIElement(gl, groupRepresentation.getHierarchyPosition());

		gl.glPushAttrib(GL2.GL_LINE_BIT);

		gl.glLineWidth(2.0f);

		gl.glColor3f(0.0f, 0.0f, 0.0f);
		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(vecPosition.x(),
				vecPosition.y() - alDropPositions.get(iDropPositionIndex),
				groupRepresentation.getPosition().z());
		gl.glVertex3f(vecPosition.x() + groupRepresentation.getWidth(), vecPosition.y()
				- alDropPositions.get(iDropPositionIndex), groupRepresentation
				.getPosition().z());
		gl.glEnd();

		gl.glPopAttrib();

		endGUIElement(gl);
	}

	protected float getTextHeight(TextRenderer textRenderer) {
		Rectangle2D bounds = textRenderer.getBounds(sTextForHeightCalculation);
		return (float) bounds.getHeight() * GrouperRenderStyle.TEXT_SCALING + 2.0f
				* GrouperRenderStyle.TEXT_SPACING;
	}

	protected void drawLeafRectangular(GL2 gl, GroupRepresentation groupRepresentation,
			TextRenderer textRenderer) {

		Vec3f vecPosition = groupRepresentation.getPosition();
		float fHeight = groupRepresentation.getHeight();
		float fWidth = groupRepresentation.getWidth();

		beginGUIElement(gl, groupRepresentation.getHierarchyPosition());

		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex3f(vecPosition.x(), vecPosition.y(), vecPosition.z());
		gl.glVertex3f(vecPosition.x() + fWidth, vecPosition.y(), vecPosition.z());
		gl.glVertex3f(vecPosition.x() + fWidth, vecPosition.y() - fHeight,
				vecPosition.z());
		gl.glVertex3f(vecPosition.x(), vecPosition.y() - fHeight, vecPosition.z());
		gl.glEnd();

		float[] text_color = GrouperRenderStyle.TEXT_COLOR;
		textRenderer.setColor(text_color[0], text_color[1], text_color[2], text_color[3]);

		textRenderer.begin3DRendering();

		textRenderer.draw3D(groupRepresentation.getName(), vecPosition.x()
				+ GrouperRenderStyle.TEXT_SPACING, vecPosition.y() - fHeight
				+ GrouperRenderStyle.TEXT_SPACING, vecPosition.z(),
				GrouperRenderStyle.TEXT_SCALING);
		textRenderer.flush();

		textRenderer.end3DRendering();

		endGUIElement(gl);

	}
}
