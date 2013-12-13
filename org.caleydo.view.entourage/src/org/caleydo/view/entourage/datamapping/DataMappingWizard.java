/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.entourage.datamapping;

import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.view.TablePerspectivesChangedEvent;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.ViewManager;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.EButtonMode;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.GLSizeRestrictiveFlowLayout;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.datadomain.pathway.listener.EnablePathSelectionEvent;
import org.caleydo.datadomain.pathway.listener.PathwayPathSelectionEvent;
import org.caleydo.datadomain.pathway.toolbar.SelectPathAction;
import org.caleydo.view.entourage.EEmbeddingID;
import org.caleydo.view.entourage.GLEntourage;
import org.eclipse.swt.widgets.Display;

/**
 * Wizard to help selecting a path and showing experimental data in enroute.
 *
 * @author Christian
 *
 */
public class DataMappingWizard extends GLElementContainer {

	private static final String PATH_SELECTION_ICON = "resources/icons/icon_32.png";
	private static final String DATA_MAPPING_ICON = "resources/icons/data_mapping.png";

	private class TextLineRenderer extends GLElement {
		private String text;

		public TextLineRenderer(String text) {
			this.text = text;
			setSize(Float.NaN, 20);
		}

		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			// g.incZ(0.5f);
			g.drawText(text, 0, 0, w, 17, VAlign.CENTER);
			// g.incZ(-0.5f);
		}
	}

	private GLEntourage entourage;
	private GLElementContainer pathAndDataLayer;
	private GLElementContainer dataLayer;

	public DataMappingWizard(GLEntourage entourage) {
		this.entourage = entourage;
		entourage.getEventListenerManager().register(this, entourage.getPathEventSpace());
		setLayout(GLLayouts.LAYERS);
		setupPathAndDataLayer();
		setupDataLayer();
	}

	private void setupPathAndDataLayer() {
		pathAndDataLayer = new GLElementContainer(new GLSizeRestrictiveFlowLayout(false, 1, GLPadding.ZERO));
		GLElementContainer selectPathButtonContainer = createButtonLayout(PATH_SELECTION_ICON,
				new IPickingListener() {
					@Override
					public void pick(Pick pick) {
						if (pick.getPickingMode() == PickingMode.CLICKED) {
							EnablePathSelectionEvent event = new EnablePathSelectionEvent(true);
							event.setEventSpace(DataMappingWizard.this.entourage.getPathEventSpace());
							GeneralManager.get().getEventPublisher().triggerEvent(event);
							Display.getDefault().asyncExec(new Runnable() {
								@Override
								public void run() {
									SelectPathAction selectPathAction = DataMappingWizard.this.entourage
											.getSelectPathAction();
									if (selectPathAction != null) {
										selectPathAction.setChecked(true);
									}
								}
							});
						}
					}
				});

		GLElementContainer openDataMapperButtonContainer = createButtonLayout(DATA_MAPPING_ICON,
				new IPickingListener() {
					@Override
					public void pick(Pick pick) {
						if (pick.getPickingMode() == PickingMode.CLICKED) {
							DataMappers.getDataMapper().show();
						}
					}
				});
		String enrouteIcon = ViewManager.get().getRemotePlugInViewIcon("org.caleydo.view.enroute",
				GLEntourage.VIEW_TYPE, EEmbeddingID.PATH_LEVEL1.id());
		GLElementContainer switchToEnrouteButtonContainer = createButtonLayout(enrouteIcon, new IPickingListener() {
			@Override
			public void pick(Pick pick) {
				if (pick.getPickingMode() == PickingMode.CLICKED
						&& entourage.getCurrentlyDisplayedPathLevel() != EEmbeddingID.PATH_LEVEL1) {
					entourage.setPathLevel(EEmbeddingID.PATH_LEVEL1);
				}
			}
		});

		pathAndDataLayer.add(createSpacing(0.5f));
		pathAndDataLayer.add(new TextLineRenderer("To visualize"));
		pathAndDataLayer.add(new TextLineRenderer("experimental data"));
		pathAndDataLayer.add(new TextLineRenderer("select a path"));
		pathAndDataLayer.add(new TextLineRenderer("in a pathway"));
		pathAndDataLayer.add(selectPathButtonContainer);
		pathAndDataLayer.add(new TextLineRenderer("and switch to"));
		pathAndDataLayer.add(new TextLineRenderer("the expanded"));
		pathAndDataLayer.add(new TextLineRenderer("enRoute"));
		pathAndDataLayer.add(new TextLineRenderer("Visualization."));
		pathAndDataLayer.add(switchToEnrouteButtonContainer);
		pathAndDataLayer.add(new TextLineRenderer(""));
		pathAndDataLayer.add(new TextLineRenderer("Use LineUp"));
		pathAndDataLayer.add(openDataMapperButtonContainer);
		pathAndDataLayer.add(new TextLineRenderer("to specify"));
		pathAndDataLayer.add(new TextLineRenderer("which data to show."));
		pathAndDataLayer.add(createSpacing(0.5f));

		add(pathAndDataLayer);
	}

	private void setupDataLayer() {
		dataLayer = new GLElementContainer(new GLSizeRestrictiveFlowLayout(true, 1, GLPadding.ZERO));

		GLElementContainer column = new GLElementContainer(new GLSizeRestrictiveFlowLayout(false, 1, GLPadding.ZERO));

		GLElementContainer openDataMapperButtonContainer = createButtonLayout(DATA_MAPPING_ICON,
				new IPickingListener() {
					@Override
					public void pick(Pick pick) {
						if (pick.getPickingMode() == PickingMode.CLICKED) {
							DataMappers.getDataMapper().show();
						}
					}
				});

		column.add(createSpacing(0.5f));
		column.add(new TextLineRenderer("Use LineUp"));
		column.add(openDataMapperButtonContainer);
		column.add(new TextLineRenderer("to specify"));
		column.add(new TextLineRenderer("the experimental data"));
		column.add(new TextLineRenderer("to show."));
		column.add(createSpacing(0.5f));

		dataLayer.setVisibility(EVisibility.NONE);
		// dataLayer.add(createSpacing(0.4f));
		dataLayer.add(column);

		add(dataLayer);
	}

	private GLElementContainer createButtonLayout(String iconPath, IPickingListener pickingListener) {

		GLButton button = new GLButton(EButtonMode.BUTTON);
		button.setSize(32, 32);
		button.setRenderer(GLRenderers.fillImage(iconPath));
		button.onPick(pickingListener);

		GLElementContainer row = new GLElementContainer(new GLSizeRestrictiveFlowLayout(true, 1, new GLPadding(2)));
		row.setSize(Float.NaN, 32);
		row.add(createSpacing(0.5f));
		row.add(button);
		row.add(createSpacing(0.5f));

		return row;
	}

	private GLElement createSpacing(float relativeSpacing) {
		GLElement spacing = new GLElement();
		spacing.setLayoutData(relativeSpacing);
		return spacing;
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		g.incZ(0.5f);
		super.renderImpl(g, w, h);
		g.incZ(-0.5f);
		// g.incZ(0.5f);
		// g.drawText("To visualize\nexperimental data\nselect a path\nin a pathway\n"
		// + "([PATH])\n and use LineUp\n[LineUp]\nto specify\nwhich data to show", 0, 0, w, 120, VAlign.CENTER);
		// List<String> lines = new ArrayList<>();
		// // lines.add("To visualize")
		// //
		// // g.drawText(lines, x, y, w, h, lineSpace, valign, style)
		// g.incZ(-0.5f);
	}

	@ListenTo(restrictExclusiveToEventSpace = true)
	public void onPathwayPathSelected(PathwayPathSelectionEvent e) {

		if (!e.getPath().isEmpty()) {
			pathAndDataLayer.setVisibility(EVisibility.NONE);
			updateDataLayerVisibility();
		} else {
			pathAndDataLayer.setVisibility(EVisibility.VISIBLE);
			dataLayer.setVisibility(EVisibility.NONE);
		}
	}

	@ListenTo
	public void onTablePerspectivesChanged(TablePerspectivesChangedEvent e) {
		if (e.getView() == entourage) {
			updateDataLayerVisibility();
		}
	}

	public void onPathLevelChanged() {
		updateDataLayerVisibility();
	}

	private void updateDataLayerVisibility() {
		if (entourage.getTablePerspectives().isEmpty()) {
			if (pathAndDataLayer.getVisibility() != EVisibility.NONE) {
				dataLayer.setVisibility(EVisibility.NONE);
			} else {
				if (entourage.getCurrentlyDisplayedPathLevel() == EEmbeddingID.PATH_LEVEL1) {
					dataLayer.setVisibility(EVisibility.VISIBLE);
				} else {
					dataLayer.setVisibility(EVisibility.NONE);
				}
			}
		} else {
			dataLayer.setVisibility(EVisibility.NONE);
		}
		relayout();
	}
}
