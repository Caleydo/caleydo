package org.caleydo.view.visbricks.brick.layout;

import java.util.HashMap;
import java.util.HashSet;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.view.visbricks.brick.EContainedViewType;
import org.caleydo.view.visbricks.brick.GLBrick;
import org.caleydo.view.visbricks.brick.ui.AContainedViewRenderer;
import org.caleydo.view.visbricks.brick.ui.BrickRemoteViewRenderer;
import org.caleydo.view.visbricks.brick.viewcreation.PathwayCreator;

public class PathwayDataConfigurer implements IBrickConfigurer {

	@Override
	public void configure(CentralBrickLayoutTemplate layoutTemplate) {
		
		HashSet<EContainedViewType> validViewTypes = new HashSet<EContainedViewType>();
		validViewTypes.add(EContainedViewType.PATHWAY_VIEW);
		
		layoutTemplate.setValidViewTypes(validViewTypes);
		layoutTemplate.setDefaultViewType(EContainedViewType.PATHWAY_VIEW);

	}

	@Override
	public void configure(CompactBrickLayoutTemplate layoutTemplate) {
		HashSet<EContainedViewType> validViewTypes = new HashSet<EContainedViewType>();
		validViewTypes.add(EContainedViewType.PATHWAY_VIEW);
		
		layoutTemplate.setValidViewTypes(validViewTypes);
		layoutTemplate.setDefaultViewType(EContainedViewType.PATHWAY_VIEW);

	}

	@Override
	public void configure(CompactCentralBrickLayoutTemplate layoutTemplate) {
		HashSet<EContainedViewType> validViewTypes = new HashSet<EContainedViewType>();
		validViewTypes.add(EContainedViewType.PATHWAY_VIEW);
		
		layoutTemplate.setValidViewTypes(validViewTypes);
		layoutTemplate.setDefaultViewType(EContainedViewType.PATHWAY_VIEW);

	}

	@Override
	public void configure(DefaultBrickLayoutTemplate layoutTemplate) {
		HashSet<EContainedViewType> validViewTypes = new HashSet<EContainedViewType>();
		validViewTypes.add(EContainedViewType.PATHWAY_VIEW);
		
		layoutTemplate.setValidViewTypes(validViewTypes);
		layoutTemplate.setDefaultViewType(EContainedViewType.PATHWAY_VIEW);

	}

	@Override
	public void configure(DetailBrickLayoutTemplate layoutTemplate) {
		HashSet<EContainedViewType> validViewTypes = new HashSet<EContainedViewType>();
		validViewTypes.add(EContainedViewType.PATHWAY_VIEW);
		
		layoutTemplate.setValidViewTypes(validViewTypes);
		layoutTemplate.setDefaultViewType(EContainedViewType.PATHWAY_VIEW);

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


		brick.setViews(views);
		brick.setContainedViewRenderers(containedViewRenderers);

	}

}
