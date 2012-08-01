/**
 * 
 */
package org.caleydo.view.enroute.mappeddataview;

import java.util.Set;

import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.perspective.ADataPerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.id.IDType;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.datadomain.genetic.GeneticDataDomain;

/**
 * Bean that can be used to initialize a {@link ContentRenderer}.
 * 
 * @author Christian
 * 
 */
public class ContentRendererInitializor implements IContentRendererInitializor {

	private Integer geneID;
	private Integer davidID;
	private GeneticDataDomain dataDomain;
	private DataContainer dataContainer;
	private ADataPerspective<?, ?, ?, ?> experimentPerspective;
	private AGLView view;
	private MappedDataRenderer mappedDataRenderer;
	private Group group;

	public ContentRendererInitializor(DataContainer dataContainer, int davidId,
			MappedDataRenderer mappedDataRenderer, AGLView view) {

		this.dataContainer = dataContainer;
		this.davidID = davidId;
		this.mappedDataRenderer = mappedDataRenderer;
		this.view = view;

		dataDomain = (GeneticDataDomain) dataContainer.getDataDomain();

		if (dataDomain.isGeneRecord()) {
			experimentPerspective = dataContainer.getDimensionPerspective();
		} else {
			experimentPerspective = dataContainer.getRecordPerspective();
		}

		if (dataDomain.isGeneRecord()) {
			group = dataContainer.getDimensionGroup();
			if (group == null) {
				group = dataContainer.getDimensionPerspective().getVirtualArray()
						.getGroupList().get(0);
			}
		} else {
			group = dataContainer.getRecordGroup();
			if (group == null) {
				group = dataContainer.getRecordPerspective().getVirtualArray()
						.getGroupList().get(0);
			}
		}

		IDType geneIDTYpe = dataDomain.getGeneIDType();
		Set<Integer> geneIDs = dataDomain.getGeneIDMappingManager().getIDAsSet(
				IDType.getIDType("DAVID"), geneIDTYpe, davidID);
		if (geneIDs == null) {
			// System.out.println("No mapping for david: " + davidID);
			geneID = null;

		} else {
			geneID = geneIDs.iterator().next();
			if (geneIDs.size() > 1) {

				Set<String> names = dataDomain.getGeneIDMappingManager().getIDAsSet(
						IDType.getIDType("DAVID"),
						dataDomain.getHumanReadableGeneIDType(), davidID);
				System.out.println("Here's the problem: " + names + " / " + geneIDs);
			}
		}
	}

	/**
	 * @return the davidID, see {@link #davidID}
	 */
	@Override
	public Integer getDavidID() {
		return davidID;
	}

	/**
	 * @return the geneID, see {@link #geneID}
	 */
	@Override
	public Integer getGeneID() {
		return geneID;
	}

	/**
	 * @return the dataContainer, see {@link #dataContainer}
	 */
	@Override
	public DataContainer getDataContainer() {
		return dataContainer;
	}

	/**
	 * @return the dataDomain, see {@link #dataDomain}
	 */
	@Override
	public GeneticDataDomain getDataDomain() {
		return dataDomain;
	}

	/**
	 * @return the mappedDataRenderer, see {@link #mappedDataRenderer}
	 */
	@Override
	public MappedDataRenderer getMappedDataRenderer() {
		return mappedDataRenderer;
	}

	/**
	 * @return the experimentPerspective, see {@link #experimentPerspective}
	 */
	@Override
	public ADataPerspective<?, ?, ?, ?> getExperimentPerspective() {
		return experimentPerspective;
	}

	/**
	 * @return the group, see {@link #group}
	 */
	@Override
	public Group getGroup() {
		return group;
	}

	/**
	 * @return the view, see {@link #view}
	 */
	@Override
	public AGLView getView() {
		return view;
	}

}
