/*
 * Copyright (c) 2003-2009 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
/*
 * FontPolygon.java
 *
 * Created on 23. April 2006, 18:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.jmex.font3d.math;

import java.util.ArrayList;
import java.util.List;

import com.jme.math.Vector3f;

/**
 *
 * @author Pirx
 */
public class ClosedPolygon
{
	private List<Vector3f> points = new ArrayList<Vector3f>();

	public void addPoint(Vector3f point)
	{
		if(points.size() > 0)
		{
			Vector3f lastpoint = points.get(points.size() - 1);
			if(lastpoint.equals(point))
			{
				//logger.info("Skipping duplicate point.");
				return;
			}
		}
		if(points.size() > 1)
		{
			// Check they are not on a straight line
			Vector3f p_1 = points.get(points.size()-2);
			Vector3f p_2 = points.get(points.size()-1);
			Vector3f v1 = new Vector3f(p_2).subtractLocal(p_1).normalizeLocal();
			Vector3f v2 = new Vector3f(point).subtractLocal(p_2).normalizeLocal();
			if(v1.equals(v2))
			{
				// Same direction, straight line, remove the last one in the vector
				//logger.info("REMOVING THE LAST ONE, TO AVOID STRAIGHT LINES");
				points.remove(points.size()-1);
			}
		}
		points.add(point);
	}

	public void close()
	{
		if(points.size() > 3)
		{
			if(points.get(0).equals(points.get(points.size() - 1)))
			{
				//logger.info("Removing last, duplicate point.");
				points.remove(points.size() - 1);
			}
		}
	}

	public List<Vector3f> getPoints()
	{
		return points;
	}

	/*
	public void createSides(List<Vector3f> vertexList, List<Integer> indexList,
			List<Vector3f> normalList, float extrusion)
	{
		int startIndex = vertexList.size();
		int size = points.size();
		for (int i = 0; i < size; i++)
		{
			Vector3f point = points.get(i);
			vertexList.add(new Vector3f(point.x, point.y, 0));
			vertexList.add(new Vector3f(point.x, point.y, -extrusion));
			int i1 = (i + 1) % size;
			//first triangle
			indexList.add(Integer.valueOf(startIndex + 2 * i));
			indexList.add(Integer.valueOf(startIndex + 2 * i + 1));
			indexList.add(Integer.valueOf(startIndex + 2 * i1));
			//second triangle
			indexList.add(Integer.valueOf(startIndex + 2 * i + 1));
			indexList.add(Integer.valueOf(startIndex + 2 * i1 + 1));
			indexList.add(Integer.valueOf(startIndex + 2 * i1));
			int i0 = (i - 1 + size) % size;
			Vector3f point0 = points.get(i0);
			Vector3f point1 = points.get(i1);
			Vector3f dir = point1.subtract(point0);
			Vector3f normal = new Vector3f(-dir.y, dir.x, 0).normalize();
			normalList.add(normal);
			normalList.add(normal);
		}
	}

	private static float sqr(float a)
	{
		return a * a;
	}

	public Distance getMinDistance(FontPolygon fp)
	{
		int inIndex = -1;
		int outIndex = -1;
		float minDist = Float.MAX_VALUE;
		for (int i = 0; i < points.size(); i++)
		{
			Vector3f outPoint = points.get(i);
			for (int j = 0; j < fp.points.size(); j++)
			{
				Vector3f inPoint = fp.points.get(j);
				float dist = sqr(outPoint.x - inPoint.x)
						+ sqr(outPoint.y - inPoint.y);
				if (dist <= minDist)
				{
					minDist = dist;
					inIndex = j;
					outIndex = i;
				}
			}
		}
		return new Distance(inIndex, outIndex, minDist);
	}

	//Assumptions:
	//* this polygon is an outline
	//* fp is a hole enclosed by this polygon
	//* the connection between the closest points of both doesn't
	//  intersect with any other line (theoretically, there are counterexamples)
	public void mergeHole(FontPolygon fp, Distance d)
	{
		List<Vector3f> newPoints = new ArrayList<Vector3f>();
		for (int i = 0; i < points.size(); i++)
		{
			newPoints.add(points.get(i));
			if (i == d.outIndex)
			{
				int fpSize = fp.points.size();
				for (int j = 0; j <= fpSize; j++)
				{
					newPoints.add(fp.points.get((j + d.inIndex) % fpSize));
				}
				newPoints.add(points.get(i));
			}
		}

		points = newPoints;
	}
	*/
	
	public boolean isHole()
	{
		int size = points.size();
		int rightMostPoint = 0;
		for (int i = 0; i < size; i++)
		{
			if(points.get(i).x > points.get(rightMostPoint).x)
				rightMostPoint = i;
		}
		// Now we just need to see if the turn is right/left
		{
			Vector3f v1 = points.get((rightMostPoint - 1 + size) % size);
			Vector3f v2 = points.get(rightMostPoint);
			Vector3f v = points.get((rightMostPoint + 1) % size);
			float turnang = (v2.x - v1.x) * (v.y - v1.y) - (v.x - v1.x) * (v2.y - v1.y);
			
			//logger.info("turnang:"+turnang);
			return turnang > 0;
		}
	}

	/*
	public boolean isHoleOLD()
	{
		int size = points.size();
		float[] dir = new float[size];
		Vector3f lastPoint = points.get(0);
		float turnang_sum = 0;
		for (int i = 0; i < size; i++)
		{
			Vector3f newPoint = points.get((i + 1) % size);
			//logger.info("newPoint:"+newPoint);
			float dx = newPoint.x - lastPoint.x;
			float dy = newPoint.y - lastPoint.y;
			dir[i] = (float) Math.atan2(dy, dx);
			lastPoint = newPoint;
			
			{
				Vector3f v1 = points.get((i - 1 + size) % size);
				Vector3f v2 = points.get(i);
				Vector3f v = points.get((i + 1) % size);
				float turnang = (v2.x - v1.x) * (v.y - v1.y) - (v.x - v1.x) * (v2.y - v1.y);
				turnang_sum += turnang;
				//logger.info("turnang:"+turnang);
			}
		}
		logger.info("turnang_sum:"+turnang_sum);
		if(true)
			return turnang_sum > 0;

		float sum = 0;
		for (int i = 0; i < size; i++)
		{
			float angle = dir[i] - dir[(i + 1) % size];
			while (angle < -Math.PI)
			{
				logger.info("------------- "+angle+" < -"+Math.PI);
				angle += FastMath.TWO_PI;
			}
			
			while (angle > Math.PI)
			{
				logger.info("------------- "+angle+" > "+Math.PI);
				angle -= FastMath.TWO_PI;
			}
			sum += angle;
		}
		logger.info("sum:"+sum);
		return sum < 0;
	}
	*/

	/*
	public void triangulate(List<Vector3f> vertexList, List<Integer> indexList,
			List<Vector3f> normalList, boolean drawFront, boolean drawBack,
			float extrusion)
	{
		List triangles = Triangulator.triangulate(points);
		if (drawFront)
		{
			int startIndex = vertexList.size();
			Vector3f frontNormal = new Vector3f(0, 0, 1);
			for (int i = 0; i < points.size(); i++)
			{
				vertexList.add(points.get(i));
				normalList.add(frontNormal);
			}
			for (int i = 0; i < triangles.size(); i++)
			{
				int[] triangle = (int[]) triangles.get(i);
				indexList.add(Integer.valueOf(triangle[0] + startIndex));
				indexList.add(Integer.valueOf(triangle[1] + startIndex));
				indexList.add(Integer.valueOf(triangle[2] + startIndex));
			}
		}
		if (drawBack)
		{
			int startIndex = vertexList.size();
			Vector3f backNormal = new Vector3f(0, 0, -1);
			for (int i = 0; i < points.size(); i++)
			{
				Vector3f point = points.get(i);
				vertexList.add(new Vector3f(point.x, point.y, -extrusion));
				normalList.add(backNormal);
			}
			for (int i = 0; i < triangles.size(); i++)
			{
				int[] triangle = (int[]) triangles.get(i);
				indexList.add(Integer.valueOf(triangle[0] + startIndex));
				indexList.add(Integer.valueOf(triangle[2] + startIndex));
				indexList.add(Integer.valueOf(triangle[1] + startIndex));
			}
		}
	}
	*/

	public static class Distance
	{
		public int inIndex;
		public int outIndex;
		public float sqrDist;

		public Distance(int inIndex, int outIndex, float sqrDist)
		{
			this.inIndex = inIndex;
			this.outIndex = outIndex;
			this.sqrDist = sqrDist;
		}
	}

}
