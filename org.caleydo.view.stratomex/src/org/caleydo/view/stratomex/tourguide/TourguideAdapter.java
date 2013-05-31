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
package org.caleydo.view.stratomex.tourguide;

import gleem.linalg.Vec3f;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.media.opengl.GL2;
import javax.media.opengl.GLContext;

import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.DataDomainOracle;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.ExtensionUtils;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.color.Colors;
import org.caleydo.core.util.color.IColor;
import org.caleydo.core.view.ViewManager;
import org.caleydo.core.view.listener.RemoveTablePerspectiveEvent;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.ElementLayouts;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.multiform.MultiFormRenderer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.datadomain.pathway.data.PathwayTablePerspective;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.view.stratomex.EEmbeddingID;
import org.caleydo.view.stratomex.EPickingType;
import org.caleydo.view.stratomex.GLStratomex;
import org.caleydo.view.stratomex.brick.GLBrick;
import org.caleydo.view.stratomex.brick.configurer.ClinicalDataConfigurer;
import org.caleydo.view.stratomex.brick.configurer.IBrickConfigurer;
import org.caleydo.view.stratomex.brick.configurer.PathwayDataConfigurer;
import org.caleydo.view.stratomex.brick.sorting.NoSortingSortingStrategy;
import org.caleydo.view.stratomex.column.BrickColumn;
import org.caleydo.view.stratomex.column.BrickColumnManager;
import org.caleydo.view.stratomex.column.FrameHighlightRenderer;
import org.caleydo.view.stratomex.listener.AddGroupsToStratomexListener;
import org.caleydo.view.stratomex.tourguide.event.HighlightBrickEvent;
import org.caleydo.view.stratomex.tourguide.event.UpdateNumericalPreviewEvent;
import org.caleydo.view.stratomex.tourguide.event.UpdatePathwayPreviewEvent;
import org.caleydo.view.stratomex.tourguide.event.UpdateStratificationPreviewEvent;
import org.caleydo.view.stratomex.tourguide.internal.AddAttachedLayoutRenderer;
import org.caleydo.view.stratomex.tourguide.internal.ConfirmCancelLayoutRenderer;
import org.caleydo.view.stratomex.tourguide.internal.ESelectionMode;
import org.caleydo.view.stratomex.tourguide.internal.EWizardMode;
import org.caleydo.view.stratomex.tourguide.internal.PrimitivePathwayRenderer;
import org.caleydo.view.stratomex.tourguide.internal.TemplateHighlightRenderer;
import org.caleydo.view.stratomex.tourguide.internal.event.AddNewColumnEvent;
import org.caleydo.view.stratomex.tourguide.internal.event.ConfirmCancelNewColumnEvent;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * @author Samuel Gratzl
 *
 */
public class TourguideAdapter implements IStratomexAdapter {
	/**
     *
     */
	private static final IColor COLOR_SELECTED = Colors.YELLOW;

	private static final IColor COLOR_POSSIBLE_SELECTION = Colors.GREEN;

	private static final String EXTENSION_POINT = "org.caleydo.view.stratomex.AddWizardElementFactory";

	private static final String ADD_PICKING_TYPE = "templateAdd";
	private static final String ADD_DEPENDENT_PICKING_TYPE = "templateDependentAdd";
	private static final String CONFIRM_PICKING_TYPE = "templateConfirm";
	private static final String CANCEL_PICKING_TYPE = "templateAbort";

	private final GLStratomex stratomex;

	/**
	 * factory of the wizard
	 */
	private final IAddWizardElementFactory factory = ExtensionUtils.findFirstImplementation(EXTENSION_POINT, "class",
			IAddWizardElementFactory.class);

	private AAddWizardElement wizard;

	private int previewIndex; // where
	// what either an element or a brick
	private EWizardMode wizardMode;
	private ElementLayout wizardElement;
	private BrickColumn wizardPreview;

	/**
	 * the current selection and related information
	 */
	private ESelectionMode selectionMode = null;
	private GLBrick selectionCurrent = null;

	public TourguideAdapter(GLStratomex stratomex) {
		this.stratomex = stratomex;
	}

	public boolean hasTourGuide() {
		return factory != null;
	}

	public void renderAddButton(GL2 gl, float x, float y, float w, float h, int id) {
		if (!hasTourGuide() || wizardElement != null || wizardPreview != null) // not more than one at the same time
			return;
		renderButton(gl, x, y, w, h, stratomex, ADD_PICKING_TYPE, id, "resources/icons/stratomex/template/add.png");
	}

	public void renderAddDependentButton(GL2 gl, float x, float y, float w, float h, int id) {
		if (!hasTourGuide() || wizardElement != null || wizardPreview != null) // not more than one at the same time
			return;
		renderButton(gl, x, y, w, h, stratomex, ADD_DEPENDENT_PICKING_TYPE, id,
				"resources/icons/stratomex/template/add.png");
	}

	public void renderConfirmButton(GL2 gl, float x, float y, float w, float h) {
		boolean disabled = wizardPreview == null; // no preview no accept
		renderButton(gl, x, y, w, h, stratomex, CONFIRM_PICKING_TYPE, 1, "resources/icons/stratomex/template/accept"
				+ (disabled ? "_disabled" : "") + ".png");
	}

	public void renderCancelButton(GL2 gl, float x, float y, float w, float h) {
		renderButton(gl, x, y, w, h, stratomex, CANCEL_PICKING_TYPE, 1,
				"resources/icons/stratomex/template/cancel.png");
	}

	private static void renderButton(GL2 gl, float x, float y, float w, float h, AGLView view, String pickingType,
			int id, String texture) {
		GLGraphics.checkError(gl);

		id = view.getPickingManager().getPickingID(view.getID(), pickingType, id + 1);
		// stratomex.addIDPickingTooltipListener("Add another column", pickingType, pickedObjectID)
		gl.glPushName(id);

		final float wi = view.getPixelGLConverter().getGLWidthForPixelWidth(32);
		final float hi = view.getPixelGLConverter().getGLHeightForPixelHeight(32);
		final float xi = x + w * 0.5f - wi * 0.5f;
		final float yi = y + h * 0.5f - hi * 0.5f;
		final float z = 1.5f;

		Vec3f lowerLeftCorner = new Vec3f(xi, yi, z);
		Vec3f lowerRightCorner = new Vec3f(xi + wi, yi, z);
		Vec3f upperRightCorner = new Vec3f(xi + wi, yi + hi, z);
		Vec3f upperLeftCorner = new Vec3f(xi, yi + hi, z);

		view.getTextureManager().renderTexture(gl, texture, lowerLeftCorner, lowerRightCorner, upperRightCorner,
				upperLeftCorner, 1, 1, 1, 1);

		gl.glPopName();
		GLGraphics.checkError(gl);
	}

	public void registerPickingListeners() {
		final Object receiver = TourguideAdapter.this;

		stratomex.addTypePickingTooltipListener("Add another column at this position", ADD_PICKING_TYPE);
		stratomex.addTypePickingListener(new IPickingListener() {
			@Override
			public void pick(Pick pick) {
				if (pick.getPickingMode() == PickingMode.CLICKED)
					EventPublisher.trigger(new AddNewColumnEvent(pick.getObjectID() - 1).to(receiver).from(this));
			}
		}, ADD_PICKING_TYPE);

		// FIXME tooltip per stratification
		stratomex.addTypePickingTooltipListener("Add datasets using the stratification", ADD_DEPENDENT_PICKING_TYPE);
		stratomex.addTypePickingListener(new IPickingListener() {
			@Override
			public void pick(Pick pick) {
				if (pick.getPickingMode() == PickingMode.CLICKED)
					EventPublisher.trigger(new AddNewColumnEvent(pick.getObjectID() - 1, true).to(receiver).from(this));
			}
		}, ADD_DEPENDENT_PICKING_TYPE);

		stratomex.addTypePickingTooltipListener("Confirm the current previewed element", CONFIRM_PICKING_TYPE);
		stratomex.addTypePickingListener(new IPickingListener() {
			@Override
			public void pick(Pick pick) {
				if (pick.getPickingMode() == PickingMode.CLICKED)
					EventPublisher.trigger(new ConfirmCancelNewColumnEvent(true).to(receiver)
							.from(this));
			}
		}, CONFIRM_PICKING_TYPE);

		stratomex.addTypePickingTooltipListener("Cancel temporary column", CANCEL_PICKING_TYPE);
		stratomex.addTypePickingListener(new IPickingListener() {
			@Override
			public void pick(Pick pick) {
				if (pick.getPickingMode() == PickingMode.CLICKED)
					EventPublisher.trigger(new ConfirmCancelNewColumnEvent(false).to(receiver)
							.from(this));
			}
		}, CANCEL_PICKING_TYPE);


		IPickingListener brickPicker = new IPickingListener() {
			@Override
			public void pick(Pick pick) {
				onBrickPick(pick);
			}
		};
		stratomex.addTypePickingListener(brickPicker, EPickingType.BRICK.name());
		stratomex.addTypePickingListener(brickPicker, EPickingType.BRICK_TITLE.name());

		stratomex.addTypePickingListener(new IPickingListener() {
			@Override
			public void pick(Pick pick) {
				if (wizard != null)
					wizard.onPick(pick);
			}
		}, AAddWizardElement.PICKING_TYPE);
	}

	/**
	 * if we pick an brick
	 *
	 * @param pick
	 */
	protected void onBrickPick(Pick pick) {
		// don't need to select
		if (pick.getPickingMode() != PickingMode.CLICKED || selectionMode == null || wizard == null)
			return;
		GLBrick brick = findBick(pick.getObjectID());
		if (brick == null)
			return;
		boolean isHeader = brick.isHeaderBrick();
		if (isHeader != (selectionMode == ESelectionMode.STRATIFICATION))
			return;
		if (this.selectionCurrent == brick)
			return;

		selectBrick(brick);
	}

	private void selectBrick(GLBrick brick) {
		boolean handled = false;
		switch (selectionMode) {
		case STRATIFICATION:
			handled = wizard.onSelected(brick.getBrickColumn().getTablePerspective());
			break;
		case GROUP:
			handled = wizard.onSelected(brick.getBrickColumn().getTablePerspective(), brick.getTablePerspective()
					.getRecordGroup());
			break;
		}
		if (handled) {
			if (this.selectionCurrent != null) {
				changeHighlight(this.selectionCurrent, COLOR_POSSIBLE_SELECTION);
			}
			changeHighlight(brick, COLOR_SELECTED);
			this.selectionCurrent = brick;

			stratomex.setDisplayListDirty();
		}
	}

	private void changeHighlight(GLBrick brick, IColor color) {
		if (brick.isHeaderBrick()) {
			brick.getBrickColumn().setHighlightColor(color == null ? BrickColumn.REVERT_COLOR : color.getRGBA());
		} else {
			ElementLayout layout = brick.getLayout();
			if (color == null)
				layout.clearBackgroundRenderers(FrameHighlightRenderer.class);
			else {
				// select brick by changing highlight
				for (FrameHighlightRenderer glow : Iterables.filter(layout.getBackgroundRenderer(),
						FrameHighlightRenderer.class)) {
					glow.setColor(color.getRGBA());
					return;
				}
				// no yet there add one
				layout.addBackgroundRenderer(new FrameHighlightRenderer(color.getRGBA(), true));
			}
		}
	}

	@Override
	public void selectStratification(Predicate<TablePerspective> filter, boolean autoSelectLeftOfMe) {
		this.selectionMode = ESelectionMode.STRATIFICATION;
		// highlight all possibles
		int index = 0;
		GLBrick toSelect = null;
		for (BrickColumn col : stratomex.getBrickColumnManager().getBrickColumns()) {
			if (filter.apply(col.getTablePerspective())) {
				changeHighlight(col.getHeaderBrick(), COLOR_POSSIBLE_SELECTION);

				if (autoSelectLeftOfMe && previewIndex == index) {
					toSelect = col.getHeaderBrick();
				}
			}
			index++;
		}

		if (toSelect != null)
			selectBrick(toSelect);

		repaint();
	}

	@Override
	public void selectGroup(Predicate<Pair<TablePerspective, Group>> filter) {
		this.selectionMode = ESelectionMode.GROUP;
		// highlight all possibles
		for (BrickColumn col : stratomex.getBrickColumnManager().getBrickColumns()) {
			TablePerspective tablePerspective = col.getTablePerspective();
			for (GLBrick brick : col.getSegmentBricks()) {
				if (filter.apply(Pair.make(tablePerspective, brick.getTablePerspective().getRecordGroup())))
					changeHighlight(brick, COLOR_POSSIBLE_SELECTION);
			}
		}
		repaint();
	}


	@ListenTo(sendToMe = true)
	private void onHighlight(HighlightBrickEvent event) {
		BrickColumnManager manager = stratomex.getBrickColumnManager();
		BrickColumn brickColumn = manager.getBrickColumn(event.getStratification());
		if (brickColumn == null)
			return;

		IColor c = event.isHighlight() ? Colors.of(event.getColor()) : null;
		if (event.getGroup() == null) {
			changeHighlight(brickColumn.getHeaderBrick(), c);
		} else {
			Group g = event.getGroup();
			for (GLBrick brick : brickColumn.getSegmentBricks()) {
				if (g.equals(brick.getTablePerspective().getRecordGroup())) {
					changeHighlight(brick, c);
					break;
				}
			}
		}
	}


	private void repaint() {
		stratomex.updateLayout();
		stratomex.setDisplayListDirty();
	}

	/**
	 * @param brickId
	 * @return
	 */
	private GLBrick findBick(int brickId) {
		for (BrickColumn col : stratomex.getBrickColumnManager().getBrickColumns()) {
			if (col.getHeaderBrick().getID() == brickId)
				return col.getHeaderBrick();
			for (GLBrick brick : col.getSegmentBricks()) {
				if (brick.getID() == brickId)
					return brick;
			}
		}
		return null;
	}

	/**
	 * @param index
	 * @param independentOne
	 * @return
	 */
	private ElementLayout createTemplateElement(TablePerspective source, boolean independentOne) {
		assert factory != null;
		createWizard(source, independentOne);
		ElementLayout l = ElementLayouts.wrap(wizard, 120);
		l.addBackgroundRenderer(new TemplateHighlightRenderer());
		l.addBackgroundRenderer(new ConfirmCancelLayoutRenderer(stratomex, this));
		return l;
	}

	private void createWizard(TablePerspective source, boolean independentOne) {
		if (source == null) {
			wizard = factory.create(this, stratomex);
			wizardMode = EWizardMode.GLOBAL;
		} else if (independentOne) {
			wizard = factory.createIndepenent(this, stratomex, source);
			wizardMode = EWizardMode.INDEPENDENT;
		} else {
			wizard = factory.createDependent(this, stratomex, source);
			wizardMode = EWizardMode.DEPENDENT;
		}
		stratomex.registerEventListener(wizard);
		wizard.prepare();
	}

	@ListenTo(sendToMe = true)
	private void onAddEmptyColumn(AddNewColumnEvent event) {
		if (wizardPreview != null || wizardElement != null) // only one at one time
			return;

		int index = 0;
		TablePerspective source = null;
		BrickColumnManager brickColumnManager = stratomex.getBrickColumnManager();
		if (event.isDependentOne()) {
			for (BrickColumn col : brickColumnManager.getBrickColumns()) {
				if (col.getID() == event.getObjectId()) {
					source = col.getTablePerspective();
					break;
				}
				index++;
			}
			if (source == null)
				return;
		} else if (event.isIndependentOne()) {
			for (BrickColumn col : brickColumnManager.getBrickColumns()) {
				if (col.getID() == event.getObjectId()) {
					source = col.getTablePerspective();
					index -= 1; // left of
					break;
				}
				index++;
			}
			if (source == null)
				return;
		} else {
			if (event.getObjectId() <= 0) {
				// left or first
				index = -1;
			} else {
				// right of
				BrickColumn col = brickColumnManager.getBrickColumnSpacers().get(event.getObjectId()).getLeftDimGroup();
				index = col == null ? -1 : brickColumnManager.getBrickColumns().indexOf(col);
			}
		}

		previewIndex = index;
		wizardElement = createTemplateElement(source, event.isIndependentOne());

		stratomex.relayout();
	}

	@ListenTo(sendToMe = true)
	private void onConfirmCancelColumn(ConfirmCancelNewColumnEvent event) {
		boolean confirm = event.isConfirm();

		if (confirm && (wizardPreview == null))
			return; // invalid action

		if (confirm) {
			// remove the preview buttons
			if (wizardPreview != null) {
				final Row layout = wizardPreview.getLayout();
				layout.clearForegroundRenderers(AddAttachedLayoutRenderer.class);
				layout.clearForegroundRenderers(ConfirmCancelLayoutRenderer.class);
				if (canHaveDependentColumns(wizardPreview))
					layout.addForeGroundRenderer(new AddAttachedLayoutRenderer(stratomex, wizardPreview.getID(), this,
							false));
				if (canHaveIndependentColumns(wizardPreview))
					layout.addForeGroundRenderer(new AddAttachedLayoutRenderer(stratomex, wizardPreview.getID(), this,
							true));
			}
		} else {
			if (wizardPreview != null)
				stratomex.removeTablePerspective(wizardPreview.getTablePerspective());
		}

		// reset
		done(confirm);

		repaint();
		stratomex.relayout();
	}

	/**
	 * listens to remove events done via the remove button and check if this was our template
	 *
	 * @param event
	 */
	@ListenTo
	private void onRemoveTablePerspective(RemoveTablePerspectiveEvent event) {
		if (event.getReceiver() != stratomex)
			return;
		// removed my template
		if (this.wizardPreview != null && this.wizardPreview.getTablePerspective() == event.getTablePerspective()) {
			done(false);
		}
	}

	/**
	 * @param brickColumn
	 */
	public void addedBrickColumn(BrickColumn brickColumn) {
		if (!hasTourGuide())
			return;

		Row layout = brickColumn.getLayout();
		if (canHaveDependentColumns(brickColumn))
			layout.addForeGroundRenderer(new AddAttachedLayoutRenderer(stratomex, brickColumn.getID(), this, false));

		if (canHaveIndependentColumns(brickColumn))
			layout.addForeGroundRenderer(new AddAttachedLayoutRenderer(stratomex, brickColumn.getID(), this, true));
	}

	/**
	 * determines whether a given BrickColumn can have dependent ones
	 *
	 * @param brickColumn
	 * @return
	 */
	private static boolean canHaveDependentColumns(BrickColumn brickColumn) {
		IBrickConfigurer b = brickColumn.getBrickConfigurer();
		if (b instanceof PathwayDataConfigurer) {
			return false;
		}
		if (b instanceof ClinicalDataConfigurer) {
			return false;
		}
		return true;
	}

	private static boolean canHaveIndependentColumns(BrickColumn brickColumn) {
		IBrickConfigurer b = brickColumn.getBrickConfigurer();
		if (b instanceof PathwayDataConfigurer) {
			return false; // TODO
		}
		if (b instanceof ClinicalDataConfigurer) {
			return b.getBrickSortingStrategy() instanceof NoSortingSortingStrategy;
		}
		return false;
	}

	/**
     *
     */
	private void done(boolean confirmed) {
		selectionMode = null;
		selectionCurrent = null;
		// clear highlights
		for (BrickColumn col : stratomex.getBrickColumnManager().getBrickColumns()) {
			col.setHighlightColor(BrickColumn.REVERT_COLOR);
			for (GLBrick brick : col.getSegmentBricks()) {
				brick.getLayout().clearBackgroundRenderers(FrameHighlightRenderer.class);
			}
		}

		// cleanup template
		if (wizardElement != null)
			cleanupWizardElement();

		wizardPreview = null;

		if (wizard != null) {
			wizard.done(confirmed);
			wizard.destroy(GLContext.getCurrent().getGL().getGL2());
			wizard = null;
		}
	}

	private void cleanupWizardElement() {
		wizardElement.setRenderer(null);
		wizardElement.destroy(GLContext.getCurrent().getGL().getGL2());
		wizardElement = null;
	}

	@ListenTo(sendToMe = true)
	private void onUpdatePreview(UpdateStratificationPreviewEvent event) {
		if (wizard == null) { // no wizard there to handle add a template column on the fly
			initIntermediateWizard();
			wizard = factory.createForStratification(this, stratomex);
			stratomex.registerEventListener(wizard);
			wizard.prepare();
		}
		wizard.onUpdate(event);
	}

	private TablePerspective initIntermediateWizard() {
		final BrickColumnManager bcm = stratomex.getBrickColumnManager();
		BrickColumn selected = bcm.getActiveBrickColumn();
		TablePerspective selectedTP = selected == null ? null : selected.getTablePerspective();
		wizardMode = EWizardMode.GLOBAL;
		previewIndex = selected != null ? bcm.indexOfBrickColumn(selected) : bcm.getRightColumnStartIndex() - 1;
		return selectedTP;
	}

	@ListenTo(sendToMe = true)
	private void onUpdatePreview(UpdatePathwayPreviewEvent event) {
		if (wizard == null) { // no wizard there to handle add a template column on the fly
			initIntermediateWizard();
			wizard = factory.createForPathway(this, stratomex);
			stratomex.registerEventListener(wizard);
			wizard.prepare();
		}
		wizard.onUpdate(event);

	}

	@ListenTo(sendToMe = true)
	private void onUpdateNumerical(UpdateNumericalPreviewEvent event) {
		if (wizard == null) { // no wizard there to handle add a template column on the fly
			initIntermediateWizard();
			wizard = factory.createForOther(this, stratomex);
			stratomex.registerEventListener(wizard);
			wizard.prepare();
		}
		wizard.onUpdate(event);
	}

	/**
	 * @param columns
	 */
	public void addTemplateColumns(List<Object> columns) {
		if (wizardElement == null)
			return;
		if (previewIndex < 0)
			columns.add(0, wizardElement);
		else {
			int index = previewIndex - stratomex.getBrickColumnManager().getCenterColumnStartIndex();
			columns.add(index + 1, wizardElement);
		}
	}

	/**
	 * whether a template column exists or not
	 *
	 * @return
	 */
	public boolean isEmpty() {
		return wizardElement == null;
	}

	@Override
	public void replaceTemplate(TablePerspective with, IBrickConfigurer config) {
		List<Pair<Integer, BrickColumn>> added;
		final List<TablePerspective> withL = Collections.singletonList(with);

		if (wizardElement != null) {
			cleanupWizardElement();
			BrickColumnManager bcm = stratomex.getBrickColumnManager();
			BrickColumn left = previewIndex < 0 ? null : bcm.getBrickColumns().get(
					bcm.getCenterColumnStartIndex() + previewIndex);
			added = stratomex.addTablePerspectives(withL, config, left, false);
			if (wizardMode == EWizardMode.INDEPENDENT) {
				updateDependentBrickColumn(with, added.get(0).getSecond());
			}
		} else if (wizardPreview != null) {
			added = stratomex.addTablePerspectives(withL, config, wizardPreview, true);
			stratomex.removeTablePerspective(wizardPreview.getTablePerspective());
			if (wizardMode == EWizardMode.INDEPENDENT) {
				updateDependentBrickColumn(with, added.get(0).getSecond());
			}
		} else {
			// create a preview on the fly
			added = stratomex.addTablePerspectives(withL, config, wizardPreview, true);
		}
		wizardPreview = added.get(0).getSecond();
		wizardPreview.getLayout().clearForegroundRenderers();
		wizardPreview.getLayout().addForeGroundRenderer(new ConfirmCancelLayoutRenderer(stratomex, this));
	}

	/**
	 * updates the dependent brick column if an independent brick column is for it selected
	 *
	 * @param with
	 */
	private void updateDependentBrickColumn(TablePerspective with, BrickColumn new_) {
		BrickColumnManager bcm = stratomex.getBrickColumnManager();
		int index = bcm.indexOfBrickColumn(new_) + 1;
		if (index <= 0 || index >= bcm.getBrickColumns().size())
			return;
		BrickColumn toUpdate = bcm.getBrickColumns().get(index);
		TablePerspective from = toUpdate.getTablePerspective();

		IBrickConfigurer brickConfigurer = toUpdate.getBrickConfigurer();
		if (brickConfigurer instanceof ClinicalDataConfigurer) {
			TablePerspective to = asPerspective(with.getRecordPerspective(), from);
			ClinicalDataConfigurer configurer = AddGroupsToStratomexListener
					.createKaplanConfigurer(stratomex, with, to);

			stratomex.addTablePerspectives(Lists.newArrayList(to), configurer, new_, true);
			stratomex.removeTablePerspective(from);
		} else if (brickConfigurer instanceof PathwayDataConfigurer) {
			// TODO
			// TablePerspective t = asPerspective(with, pathway);
		}

	}

	@Override
	public void replaceTemplate(ALayoutRenderer renderer) {
		if (wizardElement != null) {
			wizardElement.setRenderer(renderer);
			renderer.setLimits(wizardElement.getSizeScaledX(), wizardElement.getSizeScaledY()); // don't know why the
																								// element layout does
																								// it not by it own
		} else if (wizardPreview != null) {
			ElementLayout new_ = ElementLayouts.wrap(renderer, 120);
			previewIndex = stratomex.getBrickColumnManager().indexOfBrickColumn(wizardPreview) - 1;
			stratomex.removeTablePerspective(wizardPreview.getTablePerspective());
			wizardElement = new_;
			new_.addBackgroundRenderer(new TemplateHighlightRenderer());
			new_.addForeGroundRenderer(new ConfirmCancelLayoutRenderer(stratomex, this));
		} else {
			ElementLayout new_ = ElementLayouts.wrap(renderer, 120);
			wizardElement = new_;
			new_.addBackgroundRenderer(new TemplateHighlightRenderer());
			new_.addForeGroundRenderer(new ConfirmCancelLayoutRenderer(stratomex, this));
			stratomex.relayout();
		}
	}

	@Override
	public void replaceClinicalTemplate(Perspective underlying, TablePerspective numerical) {
		TablePerspective t = asPerspective(underlying, numerical);
		TablePerspective underlyingTP = findTablePerspective(underlying);
		if (underlyingTP == null)
			return;
		ClinicalDataConfigurer configurer = AddGroupsToStratomexListener.createKaplanConfigurer(stratomex,
				underlyingTP, t);
		replaceTemplate(t, configurer);
	}

	@Override
	public void replacePathwayTemplate(Perspective underlying, PathwayGraph pathway) {
		if (underlying == null) {
			replaceTemplate(new PrimitivePathwayRenderer(pathway, stratomex));
		} else {
			TablePerspective t = asPerspective(underlying, pathway);
			replaceTemplate(t, new PathwayDataConfigurer());
		}
	}


	private TablePerspective findTablePerspective(Perspective record) {
		for (TablePerspective p : stratomex.getTablePerspectives())
			if (p.getRecordPerspective() == record)
				return p;
		return null;
	}

	private static TablePerspective asPerspective(Perspective underlying, TablePerspective clinicalVariable) {
		Perspective dim = clinicalVariable.getDimensionPerspective();
		ATableBasedDataDomain dataDomain = (ATableBasedDataDomain) dim.getDataDomain();

		Perspective rec = null;

		for (String id : dataDomain.getRecordPerspectiveIDs()) {
			Perspective r = dataDomain.getTable().getRecordPerspective(id);
			if (r.getDataDomain().equals(underlying.getDataDomain())
					&& r.isLabelDefault() == underlying.isLabelDefault() && r.getLabel().equals(underlying.getLabel())) {
				rec = r;
				break;
			}
		}
		if (rec == null) { // not found create a new one
			rec = dataDomain.convertForeignPerspective(underlying);
			dataDomain.getTable().registerRecordPerspective(rec);
		}
		return dataDomain.getTablePerspective(rec.getPerspectiveID(), dim.getPerspectiveID(), false);
	}

	protected static TablePerspective asPerspective(Perspective record, PathwayGraph pathway) {
		PathwayDataDomain pathwayDataDomain = (PathwayDataDomain) DataDomainManager.get().getDataDomainByType(
				PathwayDataDomain.DATA_DOMAIN_TYPE);

		ATableBasedDataDomain dataDomain = (ATableBasedDataDomain) record.getDataDomain();
		Perspective dimension = dataDomain.getTable().getDefaultDimensionPerspective();
		for (PathwayTablePerspective p : pathwayDataDomain.getTablePerspectives()) {
			if (p.getPathway().equals(pathway) && p.getRecordPerspective().equals(record)
					&& p.getDimensionPerspective().equals(dimension))
				return p;
		}
		// not found create new one
		PathwayTablePerspective pathwayDimensionGroup = new PathwayTablePerspective(dataDomain, pathwayDataDomain,
				record, dimension, pathway);

		pathwayDimensionGroup.setPrivate(true);
		pathwayDataDomain.addTablePerspective(pathwayDimensionGroup);

		return pathwayDimensionGroup;
	}

	@Override
	public List<TablePerspective> getVisibleTablePerspectives() {
		return stratomex.getTablePerspectives();
	}

	@Override
	public ALayoutRenderer createPreviewRenderer(PathwayGraph pathway) {
		return new PrimitivePathwayRenderer(pathway, stratomex);
	}

	@Override
	public ALayoutRenderer createPreviewRenderer(TablePerspective tablePerspective) {
		// create a preview similar to the header
		EEmbeddingID embeddingID = selectEmbeddingID(tablePerspective);

		Set<String> remoteRenderedViewIDs = ViewManager.get().getRemotePlugInViewIDs(GLStratomex.VIEW_TYPE,
				embeddingID.id());

		MultiFormRenderer multiFormRenderer = new MultiFormRenderer(stratomex, true);
		List<TablePerspective> tablePerspectives = Lists.newArrayList(tablePerspective);

		String brickEventSpace = GeneralManager.get().getEventPublisher().createUniqueEventSpace();
		for (String viewID : remoteRenderedViewIDs) {
			multiFormRenderer.addPluginVisualization(viewID, GLStratomex.VIEW_TYPE, embeddingID.id(),
					tablePerspectives, brickEventSpace);
		}
		return multiFormRenderer;
	}

	private static EEmbeddingID selectEmbeddingID(TablePerspective tablePerspective) {
		EEmbeddingID embeddingID;
		if (tablePerspective instanceof PathwayTablePerspective)
			embeddingID = EEmbeddingID.PATHWAY_HEADER_BRICK;
		else if (DataDomainOracle.isClinical(tablePerspective.getDataDomain()) && hasIntegers(tablePerspective))
			embeddingID = EEmbeddingID.CLINICAL_HEADER_BRICK;
		else if (DataDomainOracle.isCategoricalDataDomain(tablePerspective.getDataDomain()))
			embeddingID = EEmbeddingID.CATEGORICAL_HEADER_BRICK;
		else
			embeddingID = EEmbeddingID.NUMERICAL_HEADER_BRICK;
		return embeddingID;
	}

	/**
	 * @param tablePerspective
	 * @return
	 */
	private static boolean hasIntegers(TablePerspective tablePerspective) {
		Table table = tablePerspective.getDataDomain().getTable();
		VirtualArray dva = tablePerspective.getDimensionPerspective().getVirtualArray();
		VirtualArray rva = tablePerspective.getRecordPerspective().getVirtualArray();
		if (dva.size() == 0 || rva.size() == 0)
			return false;
		return table.getRawDataType(dva.get(0), rva.get(0)) != EDataType.STRING;
	}
}