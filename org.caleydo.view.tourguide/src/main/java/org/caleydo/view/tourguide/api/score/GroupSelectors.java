/*******************************************************************************

 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.api.score;

import java.util.Collection;

import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.view.tourguide.spi.algorithm.IComputeElement;
import org.caleydo.view.tourguide.spi.score.IGroupSelector;
import org.caleydo.view.tourguide.spi.score.IScore;

/**
 * @author Samuel Gratzl
 *
 */
public class GroupSelectors {
	public static IGroupSelector MAX = new IGroupSelector() {
		@Override
		public Group select(IScore score, IComputeElement elem, Collection<Group> groups) {
			Group m = null;
			float max = Float.NEGATIVE_INFINITY;
			for(Group g : groups) {
				float f = score.apply(elem, g);
				if (!Float.isNaN(f) && f > max) {
					max = f;
					m = g;
				}
			}
			return m;
		}
	};

	public static IGroupSelector MAX_ABS = new IGroupSelector() {
		@Override
		public Group select(IScore score, IComputeElement elem, Collection<Group> groups) {
			Group m = null;
			float max = Float.NEGATIVE_INFINITY;
			for (Group g : groups) {
				float f = Math.abs(score.apply(elem, g));
				if (!Float.isNaN(f) && f > max) {
					max = f;
					m = g;
				}
			}
			return m;
		}
	};
}
