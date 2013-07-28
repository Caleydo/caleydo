/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.enroute.mappeddataview;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.base.ILabelProvider;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.contextmenu.GenericContextMenuItem;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.ATimedMouseOutPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.button.Button;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.view.enroute.EPickingType;
import org.caleydo.view.enroute.SelectionColorCalculator;
import org.caleydo.view.enroute.event.ShowGroupSelectionDialogEvent;

/**
 * @author Alexander Lex
 *
 */
public class ColumnCaptionRenderer extends ALayoutRenderer implements ILabelProvider {

	private Group group;
	private CaleydoTextRenderer textRenderer;
	private PixelGLConverter pixelGLConverter;
	private String label;
	Perspective samplePerspective;
	APickingListener groupPickingListener;
	protected MappedDataRenderer parent;
	private Button button;

	private int pickingID;
	private AGLView parentView;
	private SelectionColorCalculator colorCalculator;

	public ColumnCaptionRenderer(AGLView parentView, MappedDataRenderer parent, Group group,
			Perspective samplePerspective, ATableBasedDataDomain dataDomain, Button button) {
		this.parentView = parentView;
		colorCalculator = new SelectionColorCalculator(dataDomain.getColor());
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
		gl.glColor3fv(bottomBarColor, 0);
		gl.glVertex3f(x, 0, backgroundZ);
		gl.glColor3fv(topBarColor, 0);
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

			/*
			 * (non-Javadoc)
			 *
			 * @see
			 * org.caleydo.core.view.opengl.picking.APickingListener#rightClicked(org.caleydo.core.view.opengl.picking
			 * .Pick)
			 */
			@Override
			protected void rightClicked(Pick pick) {
				// final ATableBasedDataDomain dataDomain = parent.contextualTablePerspectives.get(0).getDataDomain();
				// final IDType contextRowIDType = dataDomain.getOppositeIDType(parent.sampleIDType);
				//
				// Perspective rowPerspective =
				// parent.contextualTablePerspectives.get(0).getPerspective(contextRowIDType);
				//
				// ShowContextElementSelectionDialogEvent contextEvent = new ShowContextElementSelectionDialogEvent(
				// rowPerspective);
				//
				// AContextMenuItem selectCompoundItem = new GenericContextMenuItem("Select compounds to show ",
				// contextEvent);
				// parentView.getContextMenuCreator().addContextMenuItem(selectCompoundItem);

				List<Perspective> perspectives = new ArrayList<>();

				for (TablePerspective perspective : parent.parentView.getTablePerspectives()) {
					perspectives.add(perspective.getPerspective(parent.sampleIDType));
				}
				// Perspective columnPerspective = parent.parentView.getTablePerspectives()
				// .get(parent.parentView.getTablePerspectives().size() - 1)
				// .getPerspective(parent.sampleIDType);

				ShowGroupSelectionDialogEvent selectGroupEvent = new ShowGroupSelectionDialogEvent(perspectives);

				AContextMenuItem selectGroupItem = new GenericContextMenuItem("Select sample groups to show",
						selectGroupEvent);
				parentView.getContextMenuCreator().addContextMenuItem(selectGroupItem);
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
		// parentView.addIDPickingListener(timedMouseOutListener, EPickingType.SAMPLE_GROUP.name(), group.getID());
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
