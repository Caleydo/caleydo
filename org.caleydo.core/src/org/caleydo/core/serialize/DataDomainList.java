package org.caleydo.core.serialize;

import java.util.List;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.caleydo.core.data.datadomain.ADataDomain;

/**
 * Collection-class for a list of all data domains to store
 * 
 * @author Marc Streit
 */
@XmlType
@XmlRootElement
public class DataDomainList {

	/** list of all data domains to (re-)store */
	private List<? extends ADataDomain> dataDomains;

	@XmlElementWrapper
	public List<? extends ADataDomain> getDataDomains() {
		return dataDomains;
	}

	public void setDataDomains(List<? extends ADataDomain> dataDomains) {
		this.dataDomains = dataDomains;
	}
}
