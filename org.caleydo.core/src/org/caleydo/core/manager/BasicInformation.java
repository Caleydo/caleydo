package org.caleydo.core.manager;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.caleydo.core.specialized.Organism;

@XmlRootElement
@XmlType
public class BasicInformation {

	private Organism organism = Organism.HOMO_SAPIENS;

	public void setOrganism(Organism organism) {
		this.organism = organism;
	}

	public Organism getOrganism() {
		return organism;
	}
}
