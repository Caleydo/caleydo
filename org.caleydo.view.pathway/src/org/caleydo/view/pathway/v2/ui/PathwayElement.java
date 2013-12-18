/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.pathway.v2.ui;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.EventListenerManager.DeepScan;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.view.pathway.Activator;

/**
 * element of this view holding a {@link TablePerspective}
 *
 * @author Christian
 *
 */
public class PathwayElement extends GLElementContainer {

	protected APathwayElementRepresentation pathwayRepresentation;
	protected List<GLElement> backgroundAugmentations = new ArrayList<>();
	protected List<GLElement> foregroundAugmentations = new ArrayList<>();

	@DeepScan
	protected PathwayDataMappingHandler mappingHandler;

	protected String eventSpace;

	public PathwayElement(String eventSpace) {
		super(GLLayouts.LAYERS);
		this.eventSpace = eventSpace;
		mappingHandler = new PathwayDataMappingHandler();
		mappingHandler.setEventSpace(eventSpace);
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		g.pushResourceLocator(Activator.getResourceLocator());
		g.decZ();
		for (GLElement element : backgroundAugmentations) {
			element.render(g);
		}
		g.incZ();
		pathwayRepresentation.render(g);
		g.incZ();
		for (GLElement element : foregroundAugmentations) {
			element.render(g);
		}
		g.decZ();

		g.popResourceLocator();
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		g.pushResourceLocator(Activator.getResourceLocator());
		g.decZ();
		for (GLElement element : backgroundAugmentations) {
			element.renderPick(g);
		}
		g.incZ();
		pathwayRepresentation.renderPick(g);
		g.incZ();
		for (GLElement element : foregroundAugmentations) {
			element.renderPick(g);
		}
		g.decZ();
		g.popResourceLocator();
	}

	@Override
	public void layout(int deltaTimeMs) {
		super.layout(deltaTimeMs);
	}

	/**
	 * @param pathwayRepresentation
	 *            setter, see {@link pathwayRepresentation}
	 */
	public void setPathwayRepresentation(APathwayElementRepresentation pathwayRepresentation) {
		// This should ensure that layout() is called first on the pathway representation so that augmentations always
		// make use of the correctly laid out pathway representation
		add(0, pathwayRepresentation);
		this.pathwayRepresentation = pathwayRepresentation;
		pathwayRepresentation.setWrappingElement(this);
		mappingHandler.setPathwayRepersentation(pathwayRepresentation);
	}

	/**
	 * @return the pathwayRepresentation, see {@link #pathwayRepresentation}
	 */
	public APathwayElementRepresentation getPathwayRepresentation() {
		return pathwayRepresentation;
	}

	public void addBackgroundAugmentation(GLElement element) {
		add(element);
		backgroundAugmentations.add(element);
	}

	public void addForegroundAugmentation(GLElement element) {
		add(element);
		foregroundAugmentations.add(element);
	}

	/**
	 * @return the mappingHandler, see {@link #mappingHandler}
	 */
	public PathwayDataMappingHandler getMappingHandler() {
		return mappingHandler;
	}

	@Override
	protected void takeDown() {
		mappingHandler.takeDown();
		super.takeDown();
	}

}
