package org.caleydo.core.manager.usecase;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Use case for arbitrary data which is not further specified.
 * 
 * @author Marc Streit
 */
@XmlType
@XmlRootElement
public class UnspecifiedUseCase
	extends AUseCase {

	/**
	 * Constructor.
	 */
	public UnspecifiedUseCase() {

		super();
		useCaseMode = EDataDomain.UNSPECIFIED;
		contentLabelSingular = "entity";
		contentLabelPlural = "entities";
	}
}
