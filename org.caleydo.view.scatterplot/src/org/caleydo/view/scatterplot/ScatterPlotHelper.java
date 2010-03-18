package org.caleydo.view.scatterplot;

public  class ScatterPlotHelper {
	
	public static float getSpace(float x, float y) {
		return x * y;
	}
	
	public static boolean getCorrelation(float x, float y) {
		float fCorrelation = 1.3f;
		if ((x / y) > fCorrelation)
			return false;
		if ((y / x) > fCorrelation)
			return false;

		return true;
	}
	
	public static float[] getSelectionColor(int iColorNr) {
		
		
		int color =iColorNr % 6;
		switch (color) {
		case 0:
			return new float[] { 1, 0, 0, 1 };
		case 1:
			return new float[] { 0.3f, 0.3f, 1, 1 };
		case 2:
			return new float[] { 0, 1, 0, 1 };
		case 3:
			return new float[] { 1, 1, 0, 1 };
		case 4:
			return new float[] { 0, 1, 1, 1 };
		case 5:
			return new float[] { 1, 0, 1, 1 };
		default:
			return new float[] { 0, 0, 0, 1 };
		}

	}

}
