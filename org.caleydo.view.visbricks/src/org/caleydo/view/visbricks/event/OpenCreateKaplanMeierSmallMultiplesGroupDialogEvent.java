package org.caleydo.view.visbricks.event;

import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.event.AEvent;

/**
 * Event for opening the dialog for creating small multiple kaplan meier groups
 * 
 * @author Marc Streit
 * 
 */
public class OpenCreateKaplanMeierSmallMultiplesGroupDialogEvent extends AEvent {

	/**
	 * The base data container which contains the subgroups for which the small
	 * multiples will be created.
	 */
	private DataContainer dimensionGroupDataContainer;

	/**
	 * The base dimension perspective containing all genes that will be mapped
	 * to the pathways.
	 */
	private DimensionPerspective dimensionPerspective;

	/**
	 * @param dataContainer
	 *            the base data container which contains the subgroups for which
	 *            the small multiples will be created.
	 */
	public OpenCreateKaplanMeierSmallMultiplesGroupDialogEvent(
			DataContainer dimensionGroupDataContainer,
			DimensionPerspective dimensionPerspective) {

		this.setDimensionGroupDataContainer(dimensionGroupDataContainer);
		this.setDimensionPerspective(dimensionPerspective);
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}

	/**
	 * @param dataContainer
	 *            setter, see {@link #dimensionGroupDataContainer}
	 */
	public void setDimensionGroupDataContainer(DataContainer dataContainer) {
		this.dimensionGroupDataContainer = dataContainer;
	}

	/**
	 * @return the dataContainer, see {@link #dimensionGroupDataContainer}
	 */
	public DataContainer getDimensionGroupDataContainer() {
		return dimensionGroupDataContainer;
	}

	/**
	 * @param dimensionPerspective
	 *            setter, see {@link #dimensionPerspective}
	 */
	public void setDimensionPerspective(DimensionPerspective dimensionPerspective) {
		this.dimensionPerspective = dimensionPerspective;
	}

	/**
	 * @return the dimensionPerspective, see {@link #dimensionPerspective}
	 */
	public DimensionPerspective getDimensionPerspective() {
		return dimensionPerspective;
	}
}
