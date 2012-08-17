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
package org.caleydo.view.stratomex.brick.layout;

import java.util.ArrayList;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.RecordPerspective;
import org.caleydo.core.event.data.ReplaceTablePerspectiveEvent;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.clusterer.gui.ClusterDialog;
import org.caleydo.core.util.clusterer.initialization.ClusterConfiguration;
import org.caleydo.core.view.listener.RemoveTablePerspectiveEvent;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.ColorRenderer;
import org.caleydo.core.view.opengl.layout.util.LineSeparatorRenderer;
import org.caleydo.core.view.opengl.layout.util.Zoomer;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.button.Button;
import org.caleydo.core.view.opengl.util.button.ButtonRenderer;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.stratomex.EPickingType;
import org.caleydo.view.stratomex.GLStratomex;
import org.caleydo.view.stratomex.brick.GLBrick;
import org.caleydo.view.stratomex.brick.configurer.IBrickConfigurer;
import org.caleydo.view.stratomex.brick.ui.HandleRenderer;
import org.caleydo.view.stratomex.column.BrickColumn;
import org.eclipse.swt.widgets.Shell;

/**
 * Brick layout for central brick in {@link BrickColumn} containing a caption
 * bar, toolbar, footer bar and view.
 * 
 * @author Christian Partl
 * 
 */
public class HeaderBrickLayoutTemplate extends ABrickLayoutConfiguration {

	protected static final int TOOLBAR_HEIGHT_PIXELS = 16;
	protected static final int HEADER_BAR_HEIGHT_PIXELS = 12;
	protected static final int FOOTER_BAR_HEIGHT_PIXELS = 12;
	protected static final int LINE_SEPARATOR_HEIGHT_PIXELS = 3;
	protected static final int BUTTON_HEIGHT_PIXELS = 16;
	protected static final int BUTTON_WIDTH_PIXELS = 16;
	protected static final int HANDLE_SIZE_PIXELS = 8;

	protected static final int CLUSTER_BUTTON_ID = 0;
	protected static final int LOCK_RESIZING_BUTTON_ID = 1;
	protected static final int REMOVE_COLUMN_BUTTON_ID = 2;

	// protected ArrayList<BrickViewSwitchingButton> viewSwitchingButtons;
	protected ArrayList<ElementLayout> headerBarElements;
	protected ArrayList<ElementLayout> toolBarElements;
	protected ArrayList<ElementLayout> footerBarElements;

	protected GLStratomex stratomex;

	// protected Button heatMapButton;
	// protected Button parCoordsButton;
	// protected Button histogramButton;
	// protected Button overviewHeatMapButton;

	// protected Button clusterButton;
	protected Button lockResizingButton;
	protected boolean showToolBar;
	protected boolean showFooterBar;
	protected boolean showClusterButton = false;

	protected int guiElementsHeight = 0;
	protected Row headerBar;
	protected Row toolBar;
	protected Row footerBar;

	public HeaderBrickLayoutTemplate(GLBrick brick, BrickColumn dimensionGroup,
			GLStratomex stratomex, IBrickConfigurer configurer) {
		super(brick, dimensionGroup);
		// viewSwitchingButtons = new ArrayList<BrickViewSwitchingButton>();
		this.stratomex = stratomex;

		headerBarElements = new ArrayList<ElementLayout>();
		footerBarElements = new ArrayList<ElementLayout>();
		toolBarElements = new ArrayList<ElementLayout>();
		footerBar = new Row();
		configurer.configure(this);
		registerPickingListeners();
		viewTypeChanged(getDefaultViewType());

	}

	@Override
	public void setLockResizing(boolean lockResizing) {
		if (lockResizingButton != null)
			lockResizingButton.setSelected(lockResizing);
	}

	@Override
	public void setStaticLayouts() {
		guiElementsHeight = 0;
		Row baseRow = new Row("baseRow");
		baseRow.setFrameColor(1, 0, 0, 0.5f);

		baseRow.setFrameColor(0, 0, 1, 0);
		baseElementLayout = baseRow;

		Column baseColumn = new Column("baseColumn");
		baseColumn.setFrameColor(0, 1, 0, 0);

		ElementLayout spacingLayoutY = new ElementLayout("spacingLayoutY");
		spacingLayoutY.setPixelSizeY(SPACING_PIXELS);
		spacingLayoutY.setPixelSizeX(0);

		headerBar = createHeaderBar();
		toolBar = createToolBar();

		if (showToolBar) {

			baseColumn.append(toolBar);
			// baseColumn.append(lineSeparatorLayout);
			// guiElementsHeight += (2 * SPACING_PIXELS)
			// + LINE_SEPARATOR_HEIGHT_PIXELS;
		}

		baseRow.setRenderer(borderedAreaRenderer);

		baseRow.addForeGroundRenderer(new HandleRenderer(brick, HANDLE_SIZE_PIXELS, brick
				.getTextureManager(), HandleRenderer.MOVE_HORIZONTALLY_HANDLE
				| HandleRenderer.ALL_RESIZE_HANDLES | HandleRenderer.ALL_EXPAND_HANDLES));

		ElementLayout spacingLayoutX = new ElementLayout("spacingLayoutX");
		spacingLayoutX.setPixelSizeX(SPACING_PIXELS);
		spacingLayoutX.setRatioSizeY(0);

		baseRow.append(spacingLayoutX);
		baseRow.append(baseColumn);
		baseRow.append(spacingLayoutX);

		ElementLayout dimensionBarLayout = new ElementLayout("dimensionBar");
		dimensionBarLayout.setFrameColor(1, 0, 1, 0);
		dimensionBarLayout.setPixelSizeY(FOOTER_BAR_HEIGHT_PIXELS);

		if (viewLayout == null) {
			viewLayout = new ElementLayout("viewLayout");
			viewLayout.setFrameColor(1, 0, 0, 1);
			viewLayout
					.addBackgroundRenderer(new ColorRenderer(new float[] { 1, 1, 1, 1 }));
			Zoomer zoomer = new Zoomer(stratomex, viewLayout);
			viewLayout.setZoomer(zoomer);
		}
		viewLayout.setRenderer(viewRenderer);

		// captionRow.append(spacingLayoutX);

		ElementLayout lineSeparatorLayout = new ElementLayout("lineSeparator");
		lineSeparatorLayout.setPixelSizeY(LINE_SEPARATOR_HEIGHT_PIXELS);
		lineSeparatorLayout.setRatioSizeX(1);
		lineSeparatorLayout.setRenderer(new LineSeparatorRenderer(false));

		// ElementLayout dimensionBarLaylout = new
		// ElementLayout("dimensionBar");
		// dimensionBarLaylout.setPixelGLConverter(pixelGLConverter);
		// dimensionBarLaylout.setPixelSizeY(FOOTER_BAR_HEIGHT_PIXELS);
		// dimensionBarLaylout.setRatioSizeX(1);
		// dimensionBarLaylout.setRenderer(new DimensionBarRenderer(brick));

		baseColumn.append(spacingLayoutY);
		guiElementsHeight += SPACING_PIXELS;
		if (showFooterBar) {
			footerBar = createFooterBar();
			baseColumn.append(footerBar);
			baseColumn.append(spacingLayoutY);
			guiElementsHeight += SPACING_PIXELS + FOOTER_BAR_HEIGHT_PIXELS;
		}
		// if (!dimensionGroup.isProportionalMode())
		baseColumn.append(viewLayout);

		baseColumn.append(spacingLayoutY);
		baseColumn.append(headerBar);
		baseColumn.append(spacingLayoutY);
		guiElementsHeight += (2 * SPACING_PIXELS) + HEADER_BAR_HEIGHT_PIXELS;

	}

	protected Row createFooterBar() {
		Row footerBar = new Row("footerBar");
		footerBar.setPixelSizeY(FOOTER_BAR_HEIGHT_PIXELS);

		for (ElementLayout element : footerBarElements) {
			footerBar.append(element);
		}

		return footerBar;
	}

	protected Row createHeaderBar() {
		Row headerBar = new Row();
		headerBar.setPixelSizeY(HEADER_BAR_HEIGHT_PIXELS);

		for (ElementLayout element : headerBarElements) {
			headerBar.append(element);
		}

		ElementLayout spacingLayoutX = new ElementLayout("spacingLayoutX");
		spacingLayoutX.setPixelSizeX(SPACING_PIXELS);
		spacingLayoutX.setRatioSizeY(0);

		return headerBar;
	}

	/**
	 * Creates the toolbar containing buttons for view switching.
	 * 
	 * @param pixelHeight
	 * @return
	 */
	protected Row createToolBar() {
		Row toolBar = new ToolBar("ToolBarRow", brick);
		toolBar.setPixelSizeY(0);

		ElementLayout spacingLayoutX = new ElementLayout("spacingLayoutX");
		spacingLayoutX.setPixelSizeX(SPACING_PIXELS);
		spacingLayoutX.setRatioSizeY(0);
		toolBar.append(spacingLayoutX);

		for (ElementLayout element : toolBarElements) {
			toolBar.append(element);
		}

		ElementLayout greedyXLayout = new ElementLayout("greedy");
		greedyXLayout.setGrabX(true);
		greedyXLayout.setRatioSizeY(0);
		toolBar.append(greedyXLayout);

		if (showClusterButton) {
			Button clusterButton = new Button(
					EPickingType.DIMENSION_GROUP_CLUSTER_BUTTON.name(),
					CLUSTER_BUTTON_ID, EIconTextures.CLUSTER_ICON);
			ElementLayout clusterButtonLayout = new ElementLayout("clusterButton");
			clusterButtonLayout.setPixelSizeX(BUTTON_WIDTH_PIXELS);
			clusterButtonLayout.setPixelSizeY(BUTTON_HEIGHT_PIXELS);
			clusterButtonLayout.setRenderer(new ButtonRenderer(clusterButton, brick,
					brick.getTextureManager(), DefaultBrickLayoutTemplate.BUTTON_Z));

			toolBar.append(clusterButtonLayout);
			toolBar.append(spacingLayoutX);

			brick.removeAllTypePickingListeners(EPickingType.DIMENSION_GROUP_CLUSTER_BUTTON
					.name());
			brick.addTypePickingListener(new APickingListener() {

				@Override
				public void clicked(Pick pick) {

					brick.getParentComposite().getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {

							ClusterConfiguration clusterConfiguration = new ClusterConfiguration();
							TablePerspective oldTablePerspective = brick.getBrickColumn()
									.getTablePerspective();
							clusterConfiguration
									.setSourceDimensionPerspective(oldTablePerspective
											.getDimensionPerspective());
							clusterConfiguration
									.setSourceRecordPerspective(oldTablePerspective
											.getRecordPerspective());
							ATableBasedDataDomain dataDomain = oldTablePerspective
									.getDataDomain();

							// here we create the new record perspective
							// which is
							// intended to be used once the clustering is
							// complete
							RecordPerspective newRecordPerspective = new RecordPerspective(
									dataDomain);

							// we temporarily set the old va to the new
							// perspective,
							// to avoid empty bricks
							newRecordPerspective.setVirtualArray(oldTablePerspective
									.getRecordPerspective().getVirtualArray());

							dataDomain.getTable().registerRecordPerspective(
									newRecordPerspective);

							clusterConfiguration
									.setOptionalTargetRecordPerspective(newRecordPerspective);

							TablePerspective newTablePerspective = dataDomain.getTablePerspective(
									newRecordPerspective.getPerspectiveID(),
									oldTablePerspective.getDimensionPerspective()
											.getPerspectiveID());

							ReplaceTablePerspectiveEvent rEvent = new ReplaceTablePerspectiveEvent(
									brick.getBrickColumn().getStratomexView().getID(),
									newTablePerspective, oldTablePerspective);

							GeneralManager.get().getEventPublisher().triggerEvent(rEvent);
							ClusterDialog dialog = new ClusterDialog(new Shell(), brick
									.getDataDomain(), clusterConfiguration);
							dialog.open();
							// clusterConfiguration =
							// dialog.getClusterConfiguration();
							// if (clusterConfiguration == null)
							// return;
						}
					});
				}
			}, EPickingType.DIMENSION_GROUP_CLUSTER_BUTTON.name());
		}

		Button removeColumnButton = new Button(EPickingType.REMOVE_COLUMN_BUTTON.name(),
				REMOVE_COLUMN_BUTTON_ID, EIconTextures.REMOVE);
		ElementLayout removeColumnButtonLayout = new ElementLayout("removeColumnButton");
		removeColumnButtonLayout.setPixelSizeX(BUTTON_WIDTH_PIXELS);
		removeColumnButtonLayout.setPixelSizeY(BUTTON_HEIGHT_PIXELS);
		removeColumnButtonLayout.setRenderer(new ButtonRenderer(removeColumnButton,
				brick, brick.getTextureManager(), DefaultBrickLayoutTemplate.BUTTON_Z));

		toolBar.append(removeColumnButtonLayout);
		toolBar.append(spacingLayoutX);

		brick.removeAllTypePickingListeners(EPickingType.REMOVE_COLUMN_BUTTON.name());
		brick.addTypePickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {

				brick.getParentComposite().getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						RemoveTablePerspectiveEvent event = new RemoveTablePerspectiveEvent(
								brick.getBrickColumn().getTablePerspective().getID());
						event.setSender(this);
						GeneralManager.get().getEventPublisher().triggerEvent(event);
					}
				});
			}
		}, EPickingType.REMOVE_COLUMN_BUTTON.name());

		return toolBar;
	}

	@Override
	protected void registerPickingListeners() {

		// brick.removeAllIDPickingListeners(pickingType, pickedObjectID)
		brick.addIDPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				// boolean isResizingLocked = !lockResizingButton.isSelected();
				// brick.setSizeFixed(isResizingLocked);
				// lockResizingButton.setSelected(isResizingLocked);
			}

		}, EPickingType.BRICK_LOCK_RESIZING_BUTTON.name(), LOCK_RESIZING_BUTTON_ID);
		brick.addIDPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				dimensionGroup.showDetailedBrick(brick, false);
			}
		}, EPickingType.EXPAND_RIGHT_HANDLE.name(), brick.getID());

		brick.addIDPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				dimensionGroup.showDetailedBrick(brick, true);
			}
		}, EPickingType.EXPAND_LEFT_HANDLE.name(), brick.getID());
	}

	@Override
	public int getMinHeightPixels() {
		if (viewRenderer == null)
			return 20;

		return guiElementsHeight + viewRenderer.getMinHeightPixels();
	}

	@Override
	public int getMinWidthPixels() {
		int headerBarWidth = calcSumPixelWidth(headerBar.getElements());
		int toolBarWidth = showToolBar ? calcSumPixelWidth(toolBar.getElements()) : 0;
		int footerBarWidth = showFooterBar ? calcSumPixelWidth(footerBar.getElements())
				: 0;

		int minGuiElementWidth = Math.max(headerBarWidth,
				Math.max(toolBarWidth, footerBarWidth))
				+ 2 * SPACING_PIXELS;
		if (viewRenderer == null)
			return minGuiElementWidth;

		return Math.max(minGuiElementWidth,
				(2 * SPACING_PIXELS) + viewRenderer.getMinWidthPixels());
		// return pixelGLConverter.getPixelWidthForGLWidth(dimensionGroup
		// .getMinWidth());
	}

	@Override
	public ABrickLayoutConfiguration getCollapsedLayoutTemplate() {
		return new CompactHeaderBrickLayoutTemplate(brick, dimensionGroup, stratomex,
				brick.getBrickConfigurer());
	}

	@Override
	public ABrickLayoutConfiguration getExpandedLayoutTemplate() {
		return this;
	}

	/**
	 * @return The elements displayed in the header bar.
	 */
	public ArrayList<ElementLayout> getHeaderBarElements() {
		return headerBarElements;
	}

	/**
	 * Sets the elements that should appear in the header bar. The elements will
	 * placed from left to right using the order of the specified list.
	 * 
	 * @param headerBarElements
	 */
	public void setHeaderBarElements(ArrayList<ElementLayout> headerBarElements) {
		this.headerBarElements = headerBarElements;
	}

	/**
	 * @return The elements displayed in the tool bar.
	 */
	public ArrayList<ElementLayout> getToolBarElements() {
		return toolBarElements;
	}

	/**
	 * Sets the elements that should appear in the tool bar. The elements will
	 * placed from left to right using the order of the specified list.
	 * 
	 * @param toolBarElements
	 */
	public void setToolBarElements(ArrayList<ElementLayout> toolBarElements) {
		this.toolBarElements = toolBarElements;
	}

	/**
	 * @return The elements displayed in the footer bar.
	 */
	public ArrayList<ElementLayout> getFooterBarElements() {
		return footerBarElements;
	}

	/**
	 * Sets the elements that should appear in the footer bar. The elements will
	 * placed from left to right using the order of the specified list.
	 * 
	 * @param footerBarElements
	 */
	public void setFooterBarElements(ArrayList<ElementLayout> footerBarElements) {
		this.footerBarElements = footerBarElements;
	}

	/**
	 * @return True, if the toolbar is shown, false otherwise.
	 */
	public boolean isShowToolBar() {
		return showToolBar;
	}

	/**
	 * Specifies whether the toolbar shall be shown.
	 * 
	 * @param showToolBar
	 */
	public void showToolBar(boolean showToolBar) {
		this.showToolBar = showToolBar;
	}

	/**
	 * @return True, if the footer bar is shown, false otherwise.
	 */
	public boolean isShowFooterBar() {
		return showFooterBar;
	}

	/**
	 * Specifies whether the footer bar shall be shown.
	 * 
	 * @param showFooterBar
	 */
	public void showFooterBar(boolean showFooterBar) {
		this.showFooterBar = showFooterBar;
	}

	public void showClusterButton(boolean showClusterButton) {
		this.showClusterButton = showClusterButton;
	}

	@Override
	public void destroy() {
		super.destroy();
		brick.removeAllIDPickingListeners(EPickingType.BRICK_LOCK_RESIZING_BUTTON.name(),
				LOCK_RESIZING_BUTTON_ID);
	}

}
