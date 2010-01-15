package org.caleydo.view.base.swt.toolbar.content.pathway;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.view.opengl.canvas.remote.SerializedRemoteRenderingView;
import org.caleydo.view.base.action.toolbar.view.pathway.GeneMappingAction;
import org.caleydo.view.base.action.toolbar.view.pathway.TextureAction;
import org.caleydo.view.base.swt.toolbar.content.IToolBarItem;
import org.caleydo.view.base.swt.toolbar.content.ToolBarContainer;

/**
 * Widget based toolbar container to display pathway related toolbar content.
 * 
 * @author Marc Streit
 */
public class PathwayToolBarContainer
	extends ToolBarContainer {

	/** Mediator to handle actions triggered by the contributed elements */
	PathwayToolBarMediator pathwayToolBarMediator;

	/** serialized remote rendering view to read the configuration from */
	SerializedRemoteRenderingView targetViewData;

	/**
	 * Creates a the pathway selection box and add the pathway toolbar items.
	 */
	@Override
	public List<IToolBarItem> getToolBarItems() {

		List<IToolBarItem> elements = new ArrayList<IToolBarItem>();

		TextureAction textureAction = new TextureAction(pathwayToolBarMediator);
		textureAction.setTexturesEnabled(targetViewData.isPathwayTexturesEnabled());
		elements.add(textureAction);

		GeneMappingAction geneMappingAction = new GeneMappingAction(pathwayToolBarMediator);
		geneMappingAction.setGeneMappingEnabled(targetViewData.isGeneMappingEnabled());
		elements.add(geneMappingAction);

		// TODO: neighborhood currently broken
		// elements.add(new NeighborhoodAction(pathwayToolBarMediator));

		PathwaySearchBox pathwaySearchBox = new PathwaySearchBox("");
		pathwaySearchBox.setPathwayToolBarMediator(pathwayToolBarMediator);
		elements.add(pathwaySearchBox);

		return elements;
	}

	public PathwayToolBarMediator getPathwayToolBarMediator() {
		return pathwayToolBarMediator;
	}

	public void setPathwayToolBarMediator(PathwayToolBarMediator pathwayToolBarMediator) {
		this.pathwayToolBarMediator = pathwayToolBarMediator;
	}

	public SerializedRemoteRenderingView getTargetViewData() {
		return targetViewData;
	}

	public void setTargetViewData(SerializedRemoteRenderingView targetViewData) {
		this.targetViewData = targetViewData;
	}
}
