/**
 * 
 */
package org.caleydo.view.enroute.mappeddataview;

import java.util.Set;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.AVariablePerspective;
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
	private TablePerspective tablePerspective;
	private AVariablePerspective<?, ?, ?, ?> experimentPerspective;
	private AGLView view;
	private MappedDataRenderer mappedDataRenderer;
	private Group group;

	public ContentRendererInitializor(TablePerspective tablePerspective, int davidId,
			MappedDataRenderer mappedDataRenderer, AGLView view) {

		this.tablePerspective = tablePerspective;
		this.davidID = davidId;
		this.mappedDataRenderer = mappedDataRenderer;
		this.view = view;

		dataDomain = (GeneticDataDomain) tablePerspective.getDataDomain();

		if (dataDomain.isGeneRecord()) {
			experimentPerspective = tablePerspective.getDimensionPerspective();
		} else {
			experimentPerspective = tablePerspective.getRecordPerspective();
		}

		if (dataDomain.isGeneRecord()) {
			group = tablePerspective.getDimensionGroup();
			if (group == null) {
				group = tablePerspective.getDimensionPerspective().getVirtualArray()
						.getGroupList().get(0);
			}
		} else {
			group = tablePerspective.getRecordGroup();
			if (group == null) {
				group = tablePerspective.getRecordPerspective().getVirtualArray()
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
	 * @return the tablePerspective, see {@link #tablePerspective}
	 */
	@Override
	public TablePerspective getTablePerspective() {
		return tablePerspective;
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
	public AVariablePerspective<?, ?, ?, ?> getExperimentPerspective() {
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
