/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.enroute.mappeddataview;

import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.base.ILabelProvider;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.ATimedMouseOutPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.button.Button;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.view.enroute.EPickingType;

/**
 * @author Alexander Lex
 *
 */
public class ColumnCaptionRenderer extends SelectableRenderer implements ILabelProvider {

	private Group group;
	private CaleydoTextRenderer textRenderer;
	private PixelGLConverter pixelGLConverter;
	private String label;
	Perspective samplePerspective;
	APickingListener groupPickingListener;
	protected MappedDataRenderer parent;
	private Button button;

	private int pickingID;

	public ColumnCaptionRenderer(AGLView parentView, MappedDataRenderer parent, Group group,
			Perspective samplePerspective, ATableBasedDataDomain dataDomain, Button button) {

		super(parentView, dataDomain.getColor());
		this.textRenderer = parentView.getTextRenderer();
		this.pixelGLConverter = parentView.getPixelGLConverter();
		this.group = group;
		this.label = group.getLabel();
		this.samplePerspective = samplePerspective;
		this.parent = parent;
		this.button = button;

		registerPickingListener();

		pickingID = parentView.getPickingManager().getPickingID(parentView.getID(), EPickingType.SAMPLE_GROUP.name(),
				group.getID());

	}

	@Override
	protected void finalize() throws Throwable {

	}

	@Override
	public void renderContent(GL2 gl) {

		// float sideSpacing = pixelGLConverter.getGLWidthForPixelWidth(8);
		float sideSpacing = 0;

		float textHeight = pixelGLConverter.getGLHeightForPixelHeight(15);

		float backgroundZ = 0;

		List<SelectionType> selectionTypes = parent.sampleGroupSelectionManager.getSelectionTypes(group.getID());
		colorCalculator.calculateColors(selectionTypes);
		float[] topBarColor = colorCalculator.getPrimaryColor().getRGB();
		float[] bottomBarColor = colorCalculator.getSecondaryColor().getRGB();

		gl.glPushName(pickingID);

		gl.glBegin(GL2.GL_QUADS);
		gl.glColor3fv(bottomBarColor, 0);
		gl.glVertex3f(0, 0, backgroundZ);
		gl.glColor3f(bottomBarColor[0] * 0.9f, bottomBarColor[1] * 0.9f, bottomBarColor[2] * 0.9f);
		gl.glVertex3f(x, 0, backgroundZ);
		gl.glColor3f(topBarColor[0] * 0.9f, topBarColor[1] * 0.9f, topBarColor[2] * 0.9f);
		gl.glVertex3f(x, y, backgroundZ);
		gl.glColor3fv(topBarColor, 0);
		gl.glVertex3f(0, y, backgroundZ);

		gl.glEnd();
		gl.glPopName();

		float textWidth = textRenderer.getRequiredTextWidth(label, textHeight);

		float textXOffset = sideSpacing;

		if (textWidth < x) {
			textXOffset = (x - textWidth) / 2;
		}

		textRenderer.renderTextInBounds(gl, label, textXOffset, (y - textHeight) / 2, 0.1f, x, textHeight);

	}

	private void registerPickingListener() {
		unregisterPickingListener();

		groupPickingListener = new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				parent.sampleGroupSelectionManager.clearSelection(SelectionType.SELECTION);
				parent.sampleGroupSelectionManager.addToType(SelectionType.SELECTION, pick.getObjectID());
				parent.sampleGroupSelectionManager.triggerSelectionUpdateEvent();

				parent.sampleSelectionManager.clearSelection(SelectionType.SELECTION);
				parent.sampleSelectionManager.addToType(SelectionType.SELECTION, samplePerspective.getIdType(),
						samplePerspective.getVirtualArray().getIDs());
				parent.sampleSelectionManager.triggerSelectionUpdateEvent();

				parentView.setDisplayListDirty();

			}

			@Override
			public void mouseOver(Pick pick) {
				parent.sampleGroupSelectionManager.addToType(SelectionType.MOUSE_OVER, pick.getObjectID());
				parent.sampleGroupSelectionManager.triggerSelectionUpdateEvent();
				parentView.setDisplayListDirty();

			}

			@Override
			public void mouseOut(Pick pick) {
				parent.sampleGroupSelectionManager.removeFromType(SelectionType.MOUSE_OVER, pick.getObjectID());
				parent.sampleGroupSelectionManager.triggerSelectionUpdateEvent();
				parentView.setDisplayListDirty();

			}
		};

		ATimedMouseOutPickingListener timedMouseOutListener = new ATimedMouseOutPickingListener() {
			@Override
			public void mouseOver(Pick pick) {
				super.mouseOver(pick);
				button.setVisible(true);
				parentView.setDisplayListDirty();
			}

			@Override
			protected void timedMouseOut(Pick pick) {
				button.setVisible(false);
				parentView.setDisplayListDirty();
			}
		};

		parentView.addIDPickingListener(groupPickingListener, EPickingType.SAMPLE_GROUP.name(), group.getID());
		parentView.addIDPickingListener(timedMouseOutListener, EPickingType.SAMPLE_GROUP.name(), group.getID());
		parentView.addIDPickingTooltipListener(this, EPickingType.SAMPLE_GROUP.name(), group.getID());

	}

	private void unregisterPickingListener() {
		parentView.removeAllIDPickingListeners(EPickingType.SAMPLE_GROUP.name(), group.getID());
	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return false;
	}

	@Override
	public String getLabel() {
		int numSelectedElementsInGroup = 0;
		for (Integer id : samplePerspective.getVirtualArray()) {
			List<SelectionType> experimentSelectionTypes = parent.sampleSelectionManager.getSelectionTypes(
					samplePerspective.getIdType(), id);
			if (experimentSelectionTypes.contains(SelectionType.SELECTION))
				numSelectedElementsInGroup++;
		}
		return group.getLabel() + ", Elements: " + group.getSize() + ", Selected: " + numSelectedElementsInGroup;
	}

	@Override
	public String getProviderName() {
		return "Group Column";
	}
}
