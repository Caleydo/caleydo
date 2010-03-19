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
		
		
		int color =(iColorNr+7) % 10;
		
		float[] fArMappingColor = new float[] { 0F, 0F, 0F, 0F };
		
		switch (color) {
		case 1:
			fArMappingColor = new float[] { 31, 120, 180, 1 };
			break;
		case 2:
			fArMappingColor = new float[] { 178, 223, 138, 1 };
			break;
		case 3:
			fArMappingColor = new float[] { 51, 160, 44, 1 };
			break;
		case 4:
			fArMappingColor = new float[] { 251, 154, 153, 1 };
			break;
		case 5:
			fArMappingColor = new float[] { 227, 26, 28, 1 };
			break;
		case 6:
			fArMappingColor = new float[] { 166, 206, 227, 1 };
			break;		
		case 7:
			fArMappingColor = new float[] { 253, 191, 111, 1 };
			break;
		case 8:
			fArMappingColor = new float[] { 255, 127, 0, 1 };
			break;
		case 9:
			fArMappingColor = new float[] { 202, 178, 214, 1 };
			break;
		case 0:
			fArMappingColor = new float[] { 106, 61, 154, 1 };
			break;
			
		default:
			fArMappingColor = new float[] { 0, 0, 0, 1 };
		}
		
		
		for (int i=0;i<3;i++)				
			fArMappingColor[i] = fArMappingColor[i]/255f;
		
		return fArMappingColor;

	}

}
