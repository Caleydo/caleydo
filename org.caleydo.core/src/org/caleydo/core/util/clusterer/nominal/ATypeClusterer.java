package org.caleydo.core.util.clusterer.nominal;

import org.caleydo.core.util.clusterer.AClusterer;

public abstract class ATypeClusterer
	extends AClusterer {
	
	protected int getClusterIndex(int value) {
		if (value == 0)
			return 0;
		if (value >= 101 && value <= 138)
			return 1;
		if (value >= 201 && value <= 228)
			return 2;
		if (value >= 301 && value <= 318)
			return 3;
		if (value >= 401 && value <= 419)
			return 4;
		if (value >= 501 && value <= 530)
			return 5;
		if (value >= 601 && value <= 631)
			return 6;
		if (value >= 701 && value <= 715)
			return 7;
		if (value >= 801 && value <= 816)
			return 8;
		if (value >= 901 && value <= 902)
			return 9;

		return 0;
	}

}
