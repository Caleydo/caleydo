package org.caleydo.core.util.clusterer.nominal;

import org.caleydo.core.util.clusterer.AClusterer;

public abstract class ALocationClusterer
	extends AClusterer {

	
	protected int getClusterIndex(int value) {
		if (value == 0)
			return 0;
		if (value >= 10 && value <= 19)
			return 1;
		if (value >= 20 && value <= 29)
			return 2;
		if (value >= 30 && value <= 36)
			return 3;
		if (value >= 40 && value <= 44)
			return 4;
		if (value >= 50 && value <= 53)
			return 5;
		if (value >= 60 && value <= 67)
			return 6;
		if (value >= 70 && value <= 74)
			return 7;
		if (value >= 80 && value <= 85)
			return 8;
		if (value >= 90 && value <= 95)
			return 9;
		if (value >= 100 && value <= 105)
			return 10;
		if (value >= 110 && value <= 114)
			return 11;
		if (value >= 120 && value <= 126)
			return 12;
		if (value >= 130 && value <= 134)
			return 13;
		if (value >= 140 && value <= 143)
			return 14;
		if (value >= 150 && value <= 152)
			return 15;
		if (value >= 160 && value <= 166)
			return 16;
		if (value >= 181 && value <= 183)
			return 17;
		if (value >= 190 && value <= 192)
			return 18;
		if (value >= 200 && value <= 207)
			return 19;
		if (value >= 211 && value <= 212)
			return 20;
		if (value >= 220 && value <= 224)
			return 21;
		if (value == 230)
			return 22;
		if (value >= 240 && value <= 241)
			return 23;

		return 0;
	}

}
