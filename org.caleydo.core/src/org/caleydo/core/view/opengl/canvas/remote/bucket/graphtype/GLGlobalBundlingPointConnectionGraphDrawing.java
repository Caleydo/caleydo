package org.caleydo.core.view.opengl.canvas.remote.bucket.graphtype;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.media.opengl.GL;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.view.opengl.canvas.remote.bucket.BucketGraphDrawingAdapter;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevel;

/**
 * Specialized connection line renderer for bucket view.
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class GLGlobalBundlingPointConnectionGraphDrawing
	extends BucketGraphDrawingAdapter {

	/**
	 * Constructor.
	 * 
	 * @param underInteractionLevel
	 * @param stackLevel
	 * @param poolLevel
	 */
	public GLGlobalBundlingPointConnectionGraphDrawing(final RemoteLevel focusLevel, final RemoteLevel stackLevel,
		final RemoteLevel poolLevel) {
		super(focusLevel, stackLevel, poolLevel);
	}

	protected void renderLineBundling(GL gl, EIDType idType, float[] fArColor) {
		Set<Integer> keySet = hashIDTypeToViewToPointLists.get(idType).keySet();
		HashMap<Integer, Vec3f> hashViewToCenterPoint = new HashMap<Integer, Vec3f>();

		for (Integer iKey : keySet) {
			hashViewToCenterPoint.put(iKey, calculateCenter(hashIDTypeToViewToPointLists.get(idType).get(iKey)));
		}

		Vec3f vecCenter = calculateCenter(hashViewToCenterPoint.values());

		for (Integer iKey : keySet) {
			Vec3f vecViewBundlingPoint = calculateBundlingPoint(hashViewToCenterPoint.get(iKey), vecCenter);

			for (ArrayList<Vec3f> alCurrentPoints : hashIDTypeToViewToPointLists.get(idType).get(iKey)) {
				if (alCurrentPoints.size() > 1) {
					renderPlanes(gl, vecViewBundlingPoint, alCurrentPoints);
				}
				else {
					renderLine(gl, vecViewBundlingPoint, alCurrentPoints.get(0), 0, hashViewToCenterPoint
						.get(iKey), fArColor);
				}
			}

			renderLine(gl, vecViewBundlingPoint, vecCenter, 0, fArColor);
		}
	}
	
}