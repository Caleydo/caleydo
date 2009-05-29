package org.caleydo.core.manager.usecase;


/**
 * Use case for arbitrary data which is not further specified.
 * 
 * @author Marc Streit
 *
 */
public class UnspecifiedUseCase
	extends AUseCase {

	/**
	 * Constructor.
	 */
	public UnspecifiedUseCase() {
		
		super();
		eUseCaseMode = EUseCaseMode.UNSPECIFIED_DATA;
		sContentLabelSingular = "entity";
		sContentLabelPlural = "entities";
	}
}
