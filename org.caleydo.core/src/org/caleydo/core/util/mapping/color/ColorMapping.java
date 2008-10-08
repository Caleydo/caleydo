package org.caleydo.core.util.mapping.color;

import java.util.ArrayList;
import java.util.Collections;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.conversion.ConversionTools;
import org.caleydo.core.util.preferences.PreferenceConstants;

/**
 * Color mapping. The class is initialized with a list of inflection points and
 * an associated color. A color mapping for values between 0 and 1 based on the
 * provided points is accessible.
 * 
 * @author Alexander Lex
 */
public class ColorMapping
{

	ArrayList<float[]> alColorList;
	ArrayList<ColorMarkerPoint> alMarkerPoints;

	float[] fArNotANumberColor = { 0, 0, 1 };

	public static int COLOR_DEPTH = 256;

	/**
	 * Constructor. Provide a list of {@link ColorMarkerPoint} where the first
	 * has the smallest value, and each next point has a bigger value. These
	 * color points work as inflection points. between two adjacent points the
	 * colors are interpolated.
	 * 
	 * @param alMarkerPoints
	 * @throws IllegalArgumentException if values in marker points are not
	 *             increasing, or if fvalue > 1 || fvalue < 0
	 */
	protected ColorMapping(ArrayList<ColorMarkerPoint> alMarkerPoints)
	{
		init(alMarkerPoints);
	}

	/**
	 * Reset the color mapping, same principles as constructor
	 * {@link ColorMapping#ColorMapping(ArrayList)}
	 * 
	 * @param alMarkerPoints
	 */
	protected void resetColorMapping(ArrayList<ColorMarkerPoint> alMarkerPoints)
	{
		init(alMarkerPoints);
	}

	private void init(ArrayList<ColorMarkerPoint> alMarkerPoints)
	{
		this.alMarkerPoints = alMarkerPoints;
		alColorList = new ArrayList<float[]>(COLOR_DEPTH);
		for (int iCount = 0; iCount < COLOR_DEPTH; iCount++)
		{
			alColorList.add(new float[3]);
		}

		setUpMapping();
		fArNotANumberColor = ConversionTools.getColorFromString(GeneralManager.get()
				.getPreferenceStore().getString(PreferenceConstants.NAN_COLOR));
	}

	/**
	 * Initialize the color mapping
	 * 
	 * @param alMarkerPoints the marker points
	 */
	private void setUpMapping()
	{
		Collections.sort(alMarkerPoints);
		float fSrcValue, fDestValue;

		for (int iCount = 0; iCount < alMarkerPoints.size() - 1; iCount++)
		{
			fSrcValue = alMarkerPoints.get(iCount).getValue();
			fDestValue = alMarkerPoints.get(iCount + 1).getValue();

			if (fDestValue < fSrcValue)
				throw new IllegalArgumentException(
						"Marker points values have to be increasing in size, "
								+ "but this was not the case");

			float[] fSrcColor = alMarkerPoints.get(iCount).getColor();
			float[] fDestColor = alMarkerPoints.get(iCount + 1).getColor();

			int iSrcIndex = (int) (fSrcValue * (COLOR_DEPTH - 1));
			int iDestIndex = (int) (fDestValue * (COLOR_DEPTH - 1));

			int iColorRange = iDestIndex - iSrcIndex;

			for (int iInnerCount = 1; iInnerCount <= iColorRange; iInnerCount++)
			{
				float[] fColor = alColorList.get(iSrcIndex + iInnerCount);
				float fDivisor = (float) iColorRange / (float) iInnerCount;
				fColor[0] = fSrcColor[0] + (fDestColor[0] - fSrcColor[0]) / fDivisor;
				fColor[1] = fSrcColor[1] + (fDestColor[1] - fSrcColor[1]) / fDivisor;
				fColor[2] = fSrcColor[2] + (fDestColor[2] - fSrcColor[2]) / fDivisor;
			}
		}
	}

	/**
	 * Return the mapped color for any value between 0 and 1
	 * 
	 * @param fValue
	 * @return float array with length 3, RGB
	 * @throws IllegalArgumentException if fvalue > 1 || fvalue < 0
	 */
	public float[] getColor(float fValue)
	{
		if (Float.isNaN(fValue))
		{
			return fArNotANumberColor;
		}

		if (fValue > 1 || fValue < 0)
			throw new IllegalArgumentException(
					"Invalid value in fValue. Has to be between 0 and 1");

		return alColorList.get((int) (fValue * (COLOR_DEPTH - 1)));
	}

	/**
	 * Get the marker points on which the color mapping is based
	 * 
	 * @return the list of marker points
	 */
	public ArrayList<ColorMarkerPoint> getMarkerPoints()
	{
		return alMarkerPoints;
	}

}
