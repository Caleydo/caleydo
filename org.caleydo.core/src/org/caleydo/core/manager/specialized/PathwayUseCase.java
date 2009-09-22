package org.caleydo.core.manager.specialized;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.usecase.AUseCase;
import org.caleydo.core.manager.usecase.EDataDomain;

/**
 * TODO The use case for pathway input data.
 * 
 * @author Marc Streit
 */
@XmlType
@XmlRootElement
public class PathwayUseCase
	extends AUseCase {

	/**
	 * Constructor.
	 */
	public PathwayUseCase() {

		useCaseMode = EDataDomain.CLINICAL_DATA;
	}
	
	@Override
	public void setSet(ISet set) {

		super.setSet(set);
	}
}
