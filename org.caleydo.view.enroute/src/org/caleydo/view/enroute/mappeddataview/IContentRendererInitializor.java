/**
 * 
 */
package org.caleydo.view.enroute.mappeddataview;

import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.perspective.ADataPerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.datadomain.genetic.GeneticDataDomain;

/**
 * Interface for beans that can be used to initialize a {@link ContentRenderer}.
 * 
 * @author Christian
 * 
 */
public interface IContentRendererInitializor {

	public Integer getDavidID();

	public Integer getGeneID();

	public DataContainer getDataContainer();

	public GeneticDataDomain getDataDomain();

	public MappedDataRenderer getMappedDataRenderer();

	public ADataPerspective<?, ?, ?, ?> getExperimentPerspective();

	public Group getGroup();

	public AGLView getView();
}
