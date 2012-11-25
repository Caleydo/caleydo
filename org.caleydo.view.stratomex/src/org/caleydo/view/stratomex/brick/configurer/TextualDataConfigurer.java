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
package org.caleydo.view.stratomex.brick.configurer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.media.opengl.GL2;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.layout.util.ViewLayoutRenderer;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.stratomex.EPickingType;
import org.caleydo.view.stratomex.brick.EContainedViewType;
import org.caleydo.view.stratomex.brick.GLBrick;
import org.caleydo.view.stratomex.brick.layout.ABrickLayoutConfiguration;
import org.caleydo.view.stratomex.brick.layout.CollapsedBrickLayoutTemplate;
import org.caleydo.view.stratomex.brick.layout.CompactHeaderBrickLayoutTemplate;
import org.caleydo.view.stratomex.brick.layout.DefaultBrickLayoutTemplate;
import org.caleydo.view.stratomex.brick.layout.DetailBrickLayoutTemplate;
import org.caleydo.view.stratomex.brick.layout.HeaderBrickLayoutTemplate;
import org.caleydo.view.stratomex.brick.layout.TitleOnlyHeaderBrickLayoutTemplate;
import org.caleydo.view.stratomex.brick.ui.BrickViewSwitchingButton;
import org.caleydo.view.stratomex.brick.viewcreation.ParCoordsCreator;
import org.caleydo.view.stratomex.brick.viewcreation.TagCloudCreator;

/**
 * Configurer for bricks to display nominal data.
 * 
 * @author Partl
 * 
 */
public class TextualDataConfigurer
	extends ATableBasedDataConfigurer {

	protected static final int PARCOORDS_BUTTON_ID = 2;
	protected static final int TAGCLOUD_BUTTON_ID = 3;

	public TextualDataConfigurer(TablePerspective tablePerspective) {
		super(tablePerspective);
	}

	@Override
	public void configure(HeaderBrickLayoutTemplate layoutTemplate) {
		BrickViewSwitchingButton parCoordsButton = new BrickViewSwitchingButton(
				EPickingType.BRICK_TOOLBAR_VIEW_SWITCHING_BUTTONS.name(), PARCOORDS_BUTTON_ID,
				EIconTextures.PAR_COORDS_ICON, EContainedViewType.PARCOORDS_VIEW);
		BrickViewSwitchingButton tagCloudButton = new BrickViewSwitchingButton(
				EPickingType.BRICK_TOOLBAR_VIEW_SWITCHING_BUTTONS.name(), TAGCLOUD_BUTTON_ID,
				EIconTextures.TAGCLOUD_ICON, EContainedViewType.TAGCLOUD_VIEW);

		ArrayList<BrickViewSwitchingButton> viewSwitchingButtons = new ArrayList<BrickViewSwitchingButton>();
		viewSwitchingButtons.add(parCoordsButton);
		viewSwitchingButtons.add(tagCloudButton);

		ArrayList<ElementLayout> headerBarElements = createHeaderBarElements(layoutTemplate);
		ArrayList<ElementLayout> toolBarElements = createToolBarElements(layoutTemplate,
				viewSwitchingButtons);
		ArrayList<ElementLayout> footerBarElements = createFooterBarElements(layoutTemplate);

		layoutTemplate.setHeaderBarElements(headerBarElements);
		layoutTemplate.setToolBarElements(toolBarElements);
		layoutTemplate.setFooterBarElements(footerBarElements);

		// layoutTemplate.setViewSwitchingButtons(viewSwitchingButtons);

		HashSet<EContainedViewType> validViewTypes = new HashSet<EContainedViewType>();
		validViewTypes.add(EContainedViewType.PARCOORDS_VIEW);
		validViewTypes.add(EContainedViewType.TAGCLOUD_VIEW);

		layoutTemplate.setValidViewTypes(validViewTypes);
		layoutTemplate.setDefaultViewType(EContainedViewType.PARCOORDS_VIEW);

		layoutTemplate.showToolBar(true);
		layoutTemplate.showFooterBar(true);
	}

	@Override
	public void configure(CollapsedBrickLayoutTemplate layoutTemplate) {
		HashSet<EContainedViewType> validViewTypes = new HashSet<EContainedViewType>();
		validViewTypes.add(EContainedViewType.TAGCLOUD_VIEW);

		layoutTemplate.setValidViewTypes(validViewTypes);
		layoutTemplate.setDefaultViewType(EContainedViewType.TAGCLOUD_VIEW);

		ArrayList<ElementLayout> footerBarElements = createFooterBarElements(layoutTemplate);
		layoutTemplate.setFooterBarElements(footerBarElements);
	}

	@Override
	public void configure(DefaultBrickLayoutTemplate layoutTemplate) {

		BrickViewSwitchingButton parCoordsButton = new BrickViewSwitchingButton(
				EPickingType.BRICK_TOOLBAR_VIEW_SWITCHING_BUTTONS.name(), PARCOORDS_BUTTON_ID,
				EIconTextures.PAR_COORDS_ICON, EContainedViewType.PARCOORDS_VIEW);
		BrickViewSwitchingButton tagCloudButton = new BrickViewSwitchingButton(
				EPickingType.BRICK_TOOLBAR_VIEW_SWITCHING_BUTTONS.name(), TAGCLOUD_BUTTON_ID,
				EIconTextures.TAGCLOUD_ICON, EContainedViewType.TAGCLOUD_VIEW);

		ArrayList<BrickViewSwitchingButton> viewSwitchingButtons = new ArrayList<BrickViewSwitchingButton>();
		viewSwitchingButtons.add(parCoordsButton);
		viewSwitchingButtons.add(tagCloudButton);

		ArrayList<ElementLayout> toolBarElements = createToolBarElements(layoutTemplate,
				viewSwitchingButtons);
		layoutTemplate.setToolBarElements(toolBarElements);

		ArrayList<ElementLayout> footerBarElements = createFooterBarElements(layoutTemplate);
		layoutTemplate.setFooterBarElements(footerBarElements);

		// layoutTemplate.setViewSwitchingButtons(viewSwitchingButtons);

		HashSet<EContainedViewType> validViewTypes = new HashSet<EContainedViewType>();
		validViewTypes.add(EContainedViewType.PARCOORDS_VIEW);
		validViewTypes.add(EContainedViewType.TAGCLOUD_VIEW);

		layoutTemplate.setValidViewTypes(validViewTypes);
		layoutTemplate.setDefaultViewType(EContainedViewType.PARCOORDS_VIEW);

		layoutTemplate.setShowFooterBar(true);

	}

	@Override
	public void configure(DetailBrickLayoutTemplate layoutTemplate) {

		BrickViewSwitchingButton parCoordsButton = new BrickViewSwitchingButton(
				EPickingType.BRICK_TOOLBAR_VIEW_SWITCHING_BUTTONS.name(), PARCOORDS_BUTTON_ID,
				EIconTextures.PAR_COORDS_ICON, EContainedViewType.PARCOORDS_VIEW);
		BrickViewSwitchingButton tagCloudButton = new BrickViewSwitchingButton(
				EPickingType.BRICK_TOOLBAR_VIEW_SWITCHING_BUTTONS.name(), TAGCLOUD_BUTTON_ID,
				EIconTextures.TAGCLOUD_ICON, EContainedViewType.TAGCLOUD_VIEW);

		ArrayList<BrickViewSwitchingButton> viewSwitchingButtons = new ArrayList<BrickViewSwitchingButton>();
		viewSwitchingButtons.add(parCoordsButton);
		viewSwitchingButtons.add(tagCloudButton);

		ArrayList<ElementLayout> toolBarElements = createToolBarElements(layoutTemplate,
				viewSwitchingButtons);
		layoutTemplate.setToolBarElements(toolBarElements);

		ArrayList<ElementLayout> footerBarElements = createFooterBarElements(layoutTemplate);
		layoutTemplate.setFooterBarElements(footerBarElements);

		// layoutTemplate.setViewSwitchingButtons(viewSwitchingButtons);

		HashSet<EContainedViewType> validViewTypes = new HashSet<EContainedViewType>();
		validViewTypes.add(EContainedViewType.PARCOORDS_VIEW);
		validViewTypes.add(EContainedViewType.TAGCLOUD_VIEW);

		layoutTemplate.setValidViewTypes(validViewTypes);
		layoutTemplate.setDefaultViewType(EContainedViewType.PARCOORDS_VIEW);

		layoutTemplate.showFooterBar(true);

	}

	@Override
	public void setBrickViews(GLBrick brick, GL2 gl, GLMouseListener glMouseListener,
			ABrickLayoutConfiguration brickLayout) {

		HashMap<EContainedViewType, AGLView> views = new HashMap<EContainedViewType, AGLView>();
		HashMap<EContainedViewType, LayoutRenderer> containedViewRenderers = new HashMap<EContainedViewType, LayoutRenderer>();

		ParCoordsCreator parCoordsCreator = new ParCoordsCreator();
		AGLView parCoords = parCoordsCreator.createRemoteView(brick, gl, glMouseListener);
		LayoutRenderer parCoordsLayoutRenderer = new ViewLayoutRenderer(parCoords);
		views.put(EContainedViewType.PARCOORDS_VIEW, parCoords);
		containedViewRenderers.put(EContainedViewType.PARCOORDS_VIEW, parCoordsLayoutRenderer);

		TagCloudCreator tagCloudCreator = new TagCloudCreator();
		AGLView tagCloud = tagCloudCreator.createRemoteView(brick, gl, glMouseListener);
		LayoutRenderer tagCloudLayoutRenderer = new ViewLayoutRenderer(tagCloud);
		views.put(EContainedViewType.TAGCLOUD_VIEW, tagCloud);
		containedViewRenderers.put(EContainedViewType.TAGCLOUD_VIEW, tagCloudLayoutRenderer);

		brick.setViews(views);
		brick.setContainedViewRenderers(containedViewRenderers);
	}

	@Override
	public void configure(CompactHeaderBrickLayoutTemplate layoutTemplate) {
		HashSet<EContainedViewType> validViewTypes = new HashSet<EContainedViewType>();
		validViewTypes.add(EContainedViewType.TAGCLOUD_VIEW);

		layoutTemplate.setValidViewTypes(validViewTypes);
		layoutTemplate.setDefaultViewType(EContainedViewType.TAGCLOUD_VIEW);

		ArrayList<ElementLayout> headerBarElements = createHeaderBarElements(layoutTemplate);
		layoutTemplate.setHeaderBarElements(headerBarElements);
		ArrayList<ElementLayout> footerBarElements = createFooterBarElements(layoutTemplate);
		layoutTemplate.setFooterBarElements(footerBarElements);

		layoutTemplate.showFooterBar(true);
	}

	@Override
	public void configure(TitleOnlyHeaderBrickLayoutTemplate layoutTemplate) {
		HashSet<EContainedViewType> validViewTypes = new HashSet<EContainedViewType>();
		validViewTypes.add(EContainedViewType.TAGCLOUD_VIEW);

		layoutTemplate.setValidViewTypes(validViewTypes);
		layoutTemplate.setDefaultViewType(EContainedViewType.TAGCLOUD_VIEW);
		
		//TODO im
	}

	@Override
	public boolean useDefaultWidth() {
		return true;
	}

	@Override
	public int getDefaultWidth() {
		return 100;
	}

}
