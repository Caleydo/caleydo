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
import org.caleydo.view.stratomex.column.BrickColumn;
import org.caleydo.view.stratomex.column.BrickColumnManager;
import org.caleydo.view.stratomex.listener.AddGroupsToStratomexListener;
import org.caleydo.view.stratomex.tourguide.event.HighlightBrickEvent;
import org.caleydo.view.stratomex.tourguide.event.UpdateNumericalPreviewEvent;
import org.caleydo.view.stratomex.tourguide.event.UpdatePathwayPreviewEvent;
import org.caleydo.view.stratomex.tourguide.event.UpdateStratificationPreviewEvent;
import org.caleydo.view.stratomex.tourguide.internal.AddDependentLayoutRenderer;
import org.caleydo.view.stratomex.tourguide.internal.BrickHighlightRenderer;
import org.caleydo.view.stratomex.tourguide.internal.ConfirmCancelLayoutRenderer;
import org.caleydo.view.stratomex.tourguide.internal.ESelectionMode;
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

	public void renderConfirmButton(GL2 gl, float x, float y, float w, float h, int id) {
		boolean disabled = wizardPreview == null; // no preview no accept
		renderButton(gl, x, y, w, h, stratomex, CONFIRM_PICKING_TYPE, id, "resources/icons/stratomex/template/accept"
				+ (disabled ? "_disabled" : "") + ".png");
	}

	public void renderCancelButton(GL2 gl, float x, float y, float w, float h, int id) {
		renderButton(gl, x, y, w, h, stratomex, CANCEL_PICKING_TYPE, id,
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
					EventPublisher.trigger(new ConfirmCancelNewColumnEvent(true, pick.getObjectID() - 1).to(receiver)
							.from(this));
			}
		}, CONFIRM_PICKING_TYPE);

		stratomex.addTypePickingTooltipListener("Cancel temporary column", CANCEL_PICKING_TYPE);
		stratomex.addTypePickingListener(new IPickingListener() {
			@Override
			public void pick(Pick pick) {
				if (pick.getPickingMode() == PickingMode.CLICKED)
					EventPublisher.trigger(new ConfirmCancelNewColumnEvent(false, pick.getObjectID() - 1).to(receiver)
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
		switch(selectionMode) {
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
				changeHighlight(this.selectionCurrent.getLayout(), COLOR_POSSIBLE_SELECTION);
			}
			changeHighlight(brick.getLayout(), COLOR_SELECTED);
			this.selectionCurrent = brick;

			stratomex.setDisplayListDirty();
		}
	}

	private void changeHighlight(ElementLayout layout, IColor color) {
		// select brick by changing highlight
		for (BrickHighlightRenderer glow : Iterables.filter(layout.getBackgroundRenderer(),
				BrickHighlightRenderer.class)) {
			glow.setColor(color.getRGBA());
			return;
		}
		// no yet there add one
		layout.addBackgroundRenderer(new BrickHighlightRenderer(color.getRGBA()));
	}

	@Override
	public void selectStratification(Predicate<TablePerspective> filter, boolean autoSelectLeftOfMe) {
		this.selectionMode = ESelectionMode.STRATIFICATION;
		// highlight all possibles
		int index = 0;
		GLBrick toSelect = null;
		for (BrickColumn col : stratomex.getBrickColumnManager().getBrickColumns()) {
			if (filter.apply(col.getTablePerspective())) {
				changeHighlight(col.getHeaderBrick().getLayout(), COLOR_POSSIBLE_SELECTION);

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
					changeHighlight(brick.getLayout(), COLOR_POSSIBLE_SELECTION);
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

		ElementLayout layout = null;
		if (event.getGroup() == null) {
			layout = brickColumn.getLayout();
		} else {
			Group g = event.getGroup();
			for (GLBrick brick : brickColumn.getSegmentBricks()) {
				if (g.equals(brick.getTablePerspective().getRecordGroup())) {
					layout = brick.getLayout();
					break;
				}
			}
		}
		if (layout == null)
			return;

		if (!event.isHighlight()) {
			layout.clearBackgroundRenderers(BrickHighlightRenderer.class);
		} else {
			layout.addBackgroundRenderer(new BrickHighlightRenderer(event.getColor().getColorComponents(null)));
		}
		if (layout.getLayoutManager() != null)
			layout.updateSubLayout();
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
	 * @return
	 */
	private ElementLayout createTemplateElement(int index, TablePerspective source) {
		assert factory != null;
		wizard = source == null ? factory.create(this, stratomex) : factory.createDependent(this, stratomex, source);
		stratomex.registerEventListener(wizard);
		ElementLayout l = ElementLayouts.wrap(wizard, 120);
		l.addBackgroundRenderer(new TemplateHighlightRenderer());
		l.addBackgroundRenderer(new ConfirmCancelLayoutRenderer(stratomex, index, this));
		return l;
	}

	@ListenTo(sendToMe = true)
	private void onAddEmptyColumn(AddNewColumnEvent event) {
		if (wizardPreview != null || wizardElement != null) // only one at one time
			return;

		int index;
		TablePerspective source = null;
		if (!event.isDependentOne()) {
			if (event.getObjectId() <= 0) {
				// left or first
				index = -1;
			} else {
				// right of
				BrickColumnManager brickColumnManager = stratomex.getBrickColumnManager();
				BrickColumn col = brickColumnManager.getBrickColumnSpacers().get(event.getObjectId()).getLeftDimGroup();
				index = col == null ? -1 : brickColumnManager.getBrickColumns().indexOf(col);
			}
		} else {
			int brickID = event.getObjectId();
			BrickColumnManager brickColumnManager = stratomex.getBrickColumnManager();
			index = 0;
			for (BrickColumn col : brickColumnManager.getBrickColumns()) {
				if (col.getID() == brickID) {
					source = col.getTablePerspective();
					break;
				}
				index++;
			}
			if (source == null)
				return;
		}

		previewIndex = index;
		wizardElement = createTemplateElement(index + 1, source);

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
				wizardPreview.getLayout().clearForegroundRenderers();
				if (canHaveDependentColumns(wizardPreview))
					wizardPreview.getLayout().addForeGroundRenderer(
							new AddDependentLayoutRenderer(stratomex, wizardPreview.getID(), this));
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
		if (!hasTourGuide() || !canHaveDependentColumns(brickColumn))
			return;
		brickColumn.getLayout().addForeGroundRenderer(
				new AddDependentLayoutRenderer(stratomex, brickColumn.getID(), this));
	}

	/**
	 * determines whether a given BrickColumn can have dependent ones
	 * 
	 * @param brickColumn
	 * @return
	 */
	private static boolean canHaveDependentColumns(BrickColumn brickColumn) {
		return !(brickColumn.getTablePerspective() instanceof PathwayTablePerspective);
	}

	/**
	 *
	 */
	private void done(boolean confirmed) {
		selectionMode = null;
		selectionCurrent = null;
		// clear highlights
		for (BrickColumn col : stratomex.getBrickColumnManager().getBrickColumns()) {
			col.getHeaderBrick().getLayout().clearBackgroundRenderers(BrickHighlightRenderer.class);
			for (GLBrick brick : col.getSegmentBricks()) {
				brick.getLayout().clearBackgroundRenderers(BrickHighlightRenderer.class);
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
		if (wizard != null)
			wizard.onUpdate(event);
		else { // no wizard there to handle add a template column on the fly
			replaceTemplate(event.getTablePerspective(), null);
		}
	}

	@ListenTo(sendToMe = true)
	private void onUpdatePreview(UpdatePathwayPreviewEvent event) {
		if (wizard != null)
			wizard.onUpdate(event);
		else { // no wizard there check if something is selected otherwise
			BrickColumn selected = stratomex.getBrickColumnManager().getActiveBrickColumn();
			if (selected == null) {
				// FIXME create a wizard at a specific step

			} else {
				replacePathwayTemplate(selected.getTablePerspective().getRecordPerspective(), event.getPathway());
			}
		}
	}

	@ListenTo(sendToMe = true)
	private void onUpdateNumerical(UpdateNumericalPreviewEvent event) {
		if (wizard != null)
			wizard.onUpdate(event);
		else { // no wizard there check if something is selected otherwise
			BrickColumn selected = stratomex.getBrickColumnManager().getActiveBrickColumn();
			if (selected == null) {
				// FIXME
			} else {
				replaceClinicalTemplate(selected.getTablePerspective().getRecordPerspective(),
						event.getTablePerspective());
			}
		}
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
			BrickColumn left = previewIndex < 0 ? null : bcm.getBrickColumns().get(bcm.getCenterColumnStartIndex() + previewIndex);
			added = stratomex.addTablePerspectives(withL, config, left, false);
		} else if (wizardPreview != null) {
			added = stratomex.addTablePerspectives(withL, config, wizardPreview, true);
			stratomex.removeTablePerspective(wizardPreview.getTablePerspective());
		} else {
			// create a preview on the fly
			added = stratomex.addTablePerspectives(withL, config, wizardPreview, true);
		}
		wizardPreview = added.get(0).getSecond();
		wizardPreview.getLayout().clearForegroundRenderers();
		wizardPreview.getLayout().addForeGroundRenderer(new ConfirmCancelLayoutRenderer(stratomex, previewIndex, this));
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
			new_.addForeGroundRenderer(new ConfirmCancelLayoutRenderer(stratomex, previewIndex, this));
		} else {
			throw new IllegalStateException();
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
		TablePerspective t = asPerspective(underlying, pathway);
		TablePerspective underlyingTP = findTablePerspective(underlying);
		if (underlyingTP == null)
			return;
		replaceTemplate(t, new PathwayDataConfigurer());
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
	public MultiFormRenderer createPreviewRenderer(TablePerspective tablePerspective) {
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
