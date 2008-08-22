package org.caleydo.core.util.mapping.color;

import java.util.ArrayList;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;

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

	public static int COLOR_DEPTH = 256;

	/**
	 * Constructor. Provide a list of {@link ColorMarkerPoint} where the first
	 * has the smallest value, and each next point has a bigger value. These
	 * color points work as inflection points. between two adjacent points the
	 * colors are interpolated.
	 * 
	 * @param alMarkerPoints
	 * @throws CaleydoRuntimeException if values in marker points are not
	 *             increasing, or if fvalue > 1 || fvalue < 0
	 */
	public ColorMapping(ArrayList<ColorMarkerPoint> alMarkerPoints)
	{
		alColorList = new ArrayList<float[]>(COLOR_DEPTH);
		for (int iCount = 0; iCount < COLOR_DEPTH; iCount++)
		{
			alColorList.add(new float[3]);
		}

		setUpMapping(alMarkerPoints);
	}

	/**
	 * Initialize the color mapping
	 * 
	 * @param alMarkerPoints the marker points
	 */
	private void setUpMapping(ArrayList<ColorMarkerPoint> alMarkerPoints)
	{
		float fSrcValue, fDestValue;

		for (int iCount = 0; iCount < alMarkerPoints.size() - 1; iCount++)
		{
			fSrcValue = alMarkerPoints.get(iCount).getValue();
			fDestValue = alMarkerPoints.get(iCount + 1).getValue();

			if (fDestValue < fSrcValue)
				throw new CaleydoRuntimeException(
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
	 * @return
	 * @throws CaleydoRuntimeException if fvalue > 1 || fvalue < 0
	 */
	public float[] getColor(float fValue)
	{
		if (fValue > 1 || fValue < 0)
			throw new CaleydoRuntimeException(
					"Invalid value in fValue. Has to be between 0 and 1",
					CaleydoRuntimeExceptionType.COLOR_MAPPING);

		return alColorList.get((int) (fValue * (COLOR_DEPTH - 1)));
	}

}
