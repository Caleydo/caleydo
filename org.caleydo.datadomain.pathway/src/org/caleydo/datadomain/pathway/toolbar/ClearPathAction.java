/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.pathway.toolbar;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.gui.SimpleAction;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.datadomain.pathway.Activator;
import org.caleydo.datadomain.pathway.graph.PathwayPath;
import org.caleydo.datadomain.pathway.listener.PathwayPathSelectionEvent;

/**
 * Button to clear a selected path in the pathway view.
 *
 * @author Christian Partl
 *
 */
public class ClearPathAction extends SimpleAction {

	public static final String LABEL = "Clear path";
	public static final String ICON = "resources/icons/clear_path_2.png";

	private String eventSpace;

	/**
	 * Constructor.
	 */
	public ClearPathAction(String eventSpace) {
		super(LABEL, ICON, new ResourceLoader(Activator.getResourceLocator()));
		setChecked(false);
		this.eventSpace = eventSpace;
	}

	@Override
	public void run() {
		super.run();
		setChecked(false);
		PathwayPathSelectionEvent pathEvent = new PathwayPathSelectionEvent();

		// for (PathwayPath pathSegment : pathSegmentList) {
		// pathSegments.add(pathSegment);
		// }
		// if (selectedPath != null && pathSegments!=null && pathSegments.size()>0) {
		// //pathSegments.get(pathSegments.size()-1).setPathway(selectedPath);
		// //pathSegments.set(pathSeg, element)
		// }

		pathEvent.setPath(new PathwayPath());
		pathEvent.setSender(this);
		pathEvent.setEventSpace(eventSpace);
		
		EventPublisher.trigger(pathEvent);
	}
}
