package org.caleydo.view.visbricks.brick.layout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.view.visbricks.brick.EContainedViewType;
import org.caleydo.view.visbricks.brick.GLBrick;
import org.caleydo.view.visbricks.brick.ui.AContainedViewRenderer;
import org.caleydo.view.visbricks.brick.ui.BrickRemoteViewRenderer;
import org.caleydo.view.visbricks.brick.ui.DummyRenderer;
import org.caleydo.view.visbricks.brick.viewcreation.PathwayCreator;
import org.caleydo.view.visbricks.dimensiongroup.DimensionGroupCaptionRenderer;

public class PathwayDataConfigurer implements IBrickConfigurer {

	protected static final int CAPTION_HEIGHT_PIXELS = 16;

	@Override
	public void configure(CentralBrickLayoutTemplate layoutTemplate) {

		HashSet<EContainedViewType> validViewTypes = new HashSet<EContainedViewType>();
		validViewTypes.add(EContainedViewType.DUMMY_VIEW);

		layoutTemplate.setValidViewTypes(validViewTypes);
		layoutTemplate.setDefaultViewType(EContainedViewType.DUMMY_VIEW);

		PixelGLConverter pixelGLConverter = layoutTemplate
				.getPixelGLConverter();

		ArrayList<ElementLayout> headerBarElements = new ArrayList<ElementLayout>();

		ElementLayout captionLayout = new ElementLayout("caption1");

		captionLayout.setPixelGLConverter(pixelGLConverter);
		captionLayout.setPixelSizeY(CAPTION_HEIGHT_PIXELS);
		captionLayout.setFrameColor(0, 0, 1, 1);

		DimensionGroupCaptionRenderer captionRenderer = new DimensionGroupCaptionRenderer(
				layoutTemplate.getDimensionGroup(), "dfkgjd");
		captionLayout.setRenderer(captionRenderer);

		headerBarElements.add(captionLayout);

		layoutTemplate.setHeaderBarElements(headerBarElements);

		layoutTemplate.showFooterBar(false);
		layoutTemplate.showToolBar(false);

	}

	@Override
	public void configure(CompactBrickLayoutTemplate layoutTemplate) {
		HashSet<EContainedViewType> validViewTypes = new HashSet<EContainedViewType>();
		validViewTypes.add(EContainedViewType.DUMMY_VIEW);

		layoutTemplate.setValidViewTypes(validViewTypes);
		layoutTemplate.setDefaultViewType(EContainedViewType.DUMMY_VIEW);

		layoutTemplate.showFooterBar(false);
	}

	@Override
	public void configure(CompactCentralBrickLayoutTemplate layoutTemplate) {
		HashSet<EContainedViewType> validViewTypes = new HashSet<EContainedViewType>();
		validViewTypes.add(EContainedViewType.DUMMY_VIEW);

		layoutTemplate.setValidViewTypes(validViewTypes);
		layoutTemplate.setDefaultViewType(EContainedViewType.DUMMY_VIEW);

		layoutTemplate.showFooterBar(false);
	}

	@Override
	public void configure(DefaultBrickLayoutTemplate layoutTemplate) {
		HashSet<EContainedViewType> validViewTypes = new HashSet<EContainedViewType>();
		validViewTypes.add(EContainedViewType.PATHWAY_VIEW);

		layoutTemplate.setValidViewTypes(validViewTypes);
		layoutTemplate.setDefaultViewType(EContainedViewType.PATHWAY_VIEW);

		layoutTemplate.showFooterBar(false);
	}

	@Override
	public void configure(DetailBrickLayoutTemplate layoutTemplate) {
		HashSet<EContainedViewType> validViewTypes = new HashSet<EContainedViewType>();
		validViewTypes.add(EContainedViewType.PATHWAY_VIEW);

		layoutTemplate.setValidViewTypes(validViewTypes);
		layoutTemplate.setDefaultViewType(EContainedViewType.PATHWAY_VIEW);

		layoutTemplate.showFooterBar(false);
	}

	@Override
	public void setBrickViews(GLBrick brick, GL2 gl,
			GLMouseListener glMouseListener, ABrickLayoutTemplate brickLayout) {

		HashMap<EContainedViewType, AGLView> views = new HashMap<EContainedViewType, AGLView>();
		HashMap<EContainedViewType, AContainedViewRenderer> containedViewRenderers = new HashMap<EContainedViewType, AContainedViewRenderer>();

		PathwayCreator pathwayCreator = new PathwayCreator();
		AGLView pathway = pathwayCreator.createRemoteView(brick, gl,
				glMouseListener);
		AContainedViewRenderer pathwayLayoutRenderer = new BrickRemoteViewRenderer(
				pathway, brick);
		views.put(EContainedViewType.PATHWAY_VIEW, pathway);
		containedViewRenderers.put(EContainedViewType.PATHWAY_VIEW,
				pathwayLayoutRenderer);
		DummyRenderer dummyView = new DummyRenderer();
		containedViewRenderers.put(EContainedViewType.DUMMY_VIEW, dummyView);

		brick.setViews(views);
		brick.setContainedViewRenderers(containedViewRenderers);

	}

}
