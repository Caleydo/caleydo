package org.caleydo.view.datagraph.event;

import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.event.AEvent;

public class CreateViewFromDataContainerEvent extends AEvent {

	private String viewType;
	private IDataDomain dataDomain;
	private DataContainer dataContainer;

	public CreateViewFromDataContainerEvent(String viewType, IDataDomain dataDomain,
			DataContainer dataContainer) {
		this.setViewType(viewType);
		this.setDataDomain(dataDomain);
		this.setDataContainer(dataContainer);
	}

	@Override
	public boolean checkIntegrity() {
		// TODO real check
		return true;
	}

	public void setViewType(String viewType) {
		this.viewType = viewType;
	}

	public String getViewType() {
		return viewType;
	}

	public void setDataDomain(IDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	public IDataDomain getDataDomain() {
		return dataDomain;
	}

	public void setDataContainer(DataContainer dataContainer) {
		this.dataContainer = dataContainer;
	}

	public DataContainer getDataContainer() {
		return dataContainer;
	}

}
