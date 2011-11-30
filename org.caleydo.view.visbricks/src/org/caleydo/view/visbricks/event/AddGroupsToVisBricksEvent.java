package org.caleydo.view.visbricks.event;

import java.util.ArrayList;
import java.util.List;
import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.event.AEvent;
import org.caleydo.view.visbricks.GLVisBricks;
import org.caleydo.view.visbricks.brick.layout.IBrickConfigurer;
import org.caleydo.view.visbricks.brick.layout.NumericalDataConfigurer;

/**
 * <p>
 * The {@link AddGroupsToVisBricksEvent} is an event that signals to add one or
 * several {@link DataContainer}s as DimensionGroups to {@link GLVisBricks}.
 * </p>
 * <p>
 * There are two ways to specify a group to be added:
 * <ol>
 * <li>by adding a list of pre-existing {@link DataContainer}s</li>
 * <li>by specifying exactly one {@link DataContainer}</li>
 * </ol>
 * </p>
 * 
 * @author Alexander Lex
 * 
 */
public class AddGroupsToVisBricksEvent
	extends AEvent {

	private List<DataContainer> dataContainers;

	private GLVisBricks receiver;

	/**
	 * Optional member for determining a specialized data configurer that will
	 * be used in visbricks. If not specified, visbricks will use the
	 * {@link NumericalDataConfigurer}.
	 */
	private IBrickConfigurer dataConfigurer;

	/**
	 * Specify a list of pre-existing {@link ADimensionGroupData}s to be added
	 * to VisBricks
	 * 
	 * @param dataContainers
	 */
	public AddGroupsToVisBricksEvent(List<DataContainer> dataContainers) {
		this.dataContainers = dataContainers;
	}

	/**
	 * Specify a record and dimension perspective, from which a new
	 * {@link ADimensionGroupData} is created in this event.
	 * 
	 * @param dataDomainID
	 * @param dimensionPerspectiveID
	 * @param recordPerspectiveID
	 */
	public AddGroupsToVisBricksEvent(DataContainer dataContainer) {
		dataContainers = new ArrayList<DataContainer>();
		this.dataContainers.add(dataContainer);
	}

	@Override
	public boolean checkIntegrity() {
		if (dataContainers == null)
			return false;

		return true;
	}

	public List<DataContainer> getDataContainers() {

		if (dataContainers != null)
			return dataContainers;
		else {
			throw new IllegalStateException("Event illegaly initialized");
		}
	}

	public GLVisBricks getReceiver() {
		return receiver;
	}

	public void setReceiver(GLVisBricks receiver) {
		this.receiver = receiver;
	}

	/**
	 * @param dataConfigurer setter, see {@link #dataConfigurer}
	 */
	public void setDataConfigurer(IBrickConfigurer dataConfigurer) {
		this.dataConfigurer = dataConfigurer;
	}

	/**
	 * @return the dataConfigurer, see {@link #dataConfigurer}
	 */
	public IBrickConfigurer getDataConfigurer() {
		return dataConfigurer;
	}
}
