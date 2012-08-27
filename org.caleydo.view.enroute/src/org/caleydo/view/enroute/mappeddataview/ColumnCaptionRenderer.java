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
package org.caleydo.view.enroute.mappeddataview;

import java.util.ArrayList;

import javax.media.opengl.GL2;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.AVariablePerspective;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.view.enroute.EPickingType;

/**
 * @author Alexander Lex
 * 
 */
public class ColumnCaptionRenderer extends SelectableRenderer {

	private Group group;
	private CaleydoTextRenderer textRenderer;
	private PixelGLConverter pixelGLConverter;
	private String label;
	AVariablePerspective<?, ?, ?, ?> samplePerspective;
	APickingListener groupPickingListener;
	

	public ColumnCaptionRenderer(AGLView parentView, MappedDataRenderer parent,
			Group group, AVariablePerspective<?, ?, ?, ?> samplePerspective,
			ATableBasedDataDomain dataDomain) {
		super(parentView, parent);
		this.textRenderer = parentView.getTextRenderer();
		this.pixelGLConverter = parentView.getPixelGLConverter();
		this.group = group;
		this.label = group.getLabel();
		this.samplePerspective = samplePerspective;

		baseColor = dataDomain.getColor().getRGB();
		topBarColor = baseColor;
		bottomBarColor = topBarColor;
		registerPickingListener();

	}

	@Override
	protected void finalize() throws Throwable {
		unregisterPickingListener();
	}

	@Override
	public void renderContent(GL2 gl) {
		// float sideSpacing = pixelGLConverter.getGLWidthForPixelWidth(8);
		float sideSpacing = 0;

		float height = pixelGLConverter.getGLHeightForPixelHeight(15);

		float backgroundZ = 0;

		ArrayList<SelectionType> selectionTypes = parent.sampleGroupSelectionManager
				.getSelectionTypes(group.getID());
		calculateColors(selectionTypes);

		gl.glPushName(parentView.getPickingManager().getPickingID(parentView.getID(),
				EPickingType.SAMPLE_GROUP.name(), group.getID()));

		gl.glBegin(GL2.GL_QUADS);
		gl.glColor3fv(bottomBarColor, 0);
		gl.glVertex3f(0, 0, backgroundZ);
		gl.glColor3f(bottomBarColor[0] * 0.9f, bottomBarColor[1] * 0.9f,
				bottomBarColor[2] * 0.9f);
		gl.glVertex3f(x, 0, backgroundZ);
		gl.glColor3f(topBarColor[0] * 0.9f, topBarColor[1] * 0.9f, topBarColor[2] * 0.9f);
		gl.glVertex3f(x, y, backgroundZ);
		gl.glColor3fv(topBarColor, 0);
		gl.glVertex3f(0, y, backgroundZ);

		gl.glEnd();
		gl.glPopName();

		float width = textRenderer.getRequiredTextWidth(label, height);

		float textXOffset = sideSpacing;

		if (width < x) {
			textXOffset = (x - width) / 2;
		}

		textRenderer.renderTextInBounds(gl, label, textXOffset, (y - height) / 2, 0.1f,
				x, height);

	}

	

	private void registerPickingListener() {
		groupPickingListener = new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				parent.sampleGroupSelectionManager
						.clearSelection(SelectionType.SELECTION);
				parent.sampleGroupSelectionManager.addToType(SelectionType.SELECTION,
						pick.getObjectID());
				parent.sampleGroupSelectionManager.triggerSelectionUpdateEvent();

				parent.sampleSelectionManager.clearSelection(SelectionType.SELECTION);
				parent.sampleSelectionManager.addToType(SelectionType.SELECTION,
						samplePerspective.getIdType(), samplePerspective
								.getVirtualArray().getIDs());
				parent.sampleSelectionManager.triggerSelectionUpdateEvent();

				parentView.setDisplayListDirty();

			}

			@Override
			public void mouseOver(Pick pick) {

				parent.sampleGroupSelectionManager.addToType(SelectionType.MOUSE_OVER,
						pick.getObjectID());
				parent.sampleGroupSelectionManager.triggerSelectionUpdateEvent();
				parentView.setDisplayListDirty();

			}

			@Override
			public void mouseOut(Pick pick) {
				parent.sampleGroupSelectionManager.removeFromType(
						SelectionType.MOUSE_OVER, pick.getObjectID());
				parent.sampleGroupSelectionManager.triggerSelectionUpdateEvent();

				parentView.setDisplayListDirty();

			}
		};

		parentView.addIDPickingListener(groupPickingListener,
				EPickingType.SAMPLE_GROUP.name(), group.getID());
	}

	private void unregisterPickingListener() {
		parentView.removePickingListener(groupPickingListener);
	}

	@Override
	protected boolean permitsDisplayLists() {
		return false;
	}
}
