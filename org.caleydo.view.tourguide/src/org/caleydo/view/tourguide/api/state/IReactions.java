/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.api.state;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.view.stratomex.brick.configurer.IBrickConfigurer;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.spi.score.IScore;

/**
 * possible things that an {@link IState} or {@link ITransition} can do to interact with the system
 *
 * @author Samuel Gratzl
 *
 */
public interface IReactions {
	/**
	 * switches to another state
	 *
	 * @param target
	 */
	void switchTo(IState target);

	/**
	 * adds some score to tour guide
	 *
	 * @param mode
	 * @param scores
	 */
	void addScoreToTourGuide(EDataDomainQueryMode mode, IScore... scores);

	/**
	 * replaces the current template with a visualization of the given data
	 *
	 * @param with
	 * @param configurer
	 */
	void replaceTemplate(TablePerspective with, IBrickConfigurer configurer, boolean highlight);

	/**
	 * replaces the current template with the given renderer
	 *
	 * @param renderer
	 */
	void replaceTemplate(ALayoutRenderer renderer);

	/**
	 * replaces the current template with a visualization of the given clinical variable
	 *
	 * @param underlying
	 * @param numerical
	 * @param extra
	 *            whether an extra brick should be replaced/created or the main one
	 */
	void replaceClinicalTemplate(Perspective underlying, TablePerspective numerical, boolean extra, boolean highlight);

	/**
	 * replaces the current template with a visualization of the given pathway
	 *
	 * @param underlying
	 * @param pathway
	 * @param extra
	 *            whether an extra brick should be replaced/created or the main one
	 */
	void replacePathwayTemplate(Perspective underlying, PathwayGraph pathway, boolean extra, boolean highlight);

	/**
	 * factory method for creating a preview for the given {@link TablePerspective}
	 *
	 * @param tablePerspective
	 * @return
	 */
	ALayoutRenderer createPreview(TablePerspective tablePerspective);

	/**
	 * factory method for creating a preview of the given pathway
	 *
	 * @param pathway
	 * @return
	 */
	ALayoutRenderer createPreview(PathwayGraph pathway);

	/**
	 * @return
	 */
	AGLView getGLView();


}
