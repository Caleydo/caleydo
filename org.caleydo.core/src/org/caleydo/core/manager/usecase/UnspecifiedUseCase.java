package org.caleydo.core.manager.usecase;

import org.caleydo.core.data.selection.delta.IVirtualArrayDelta;
import org.caleydo.core.view.opengl.canvas.storagebased.EVAType;

/**
 * Use case for arbitrary data which is not further specified.
 * 
 * @author Marc Streit
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
