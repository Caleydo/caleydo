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
package com.jmex.font3d.math;

import com.jme.math.FastMath;

public class PlanarEdge
{
	private PlanarVertex orig;
	private PlanarEdge  twin;
	private PlanarEdge  next;
	private PlanarEdge  prev;
	float angle = -1;
	boolean realedge;
	
	PlanarEdge(PlanarVertex orig, boolean realedge)
	{
		this.orig = orig;
		this.realedge = realedge;
	}
	public boolean isRealEdge()
	{
		return realedge;
	}
	public PlanarVertex getOrigin()
	{
		return orig;
	}
	/**
	 * Calculated the angle from this edge to the given edge (counter clockwise), the result is in the interval [0;2*PI).
	 * 
	 * @param edge
	 * @return
	 */
	public float angleCounterClockWise(PlanarEdge edge)
	{
		if(this == edge)
			throw new RuntimeException("You are trying to find the angle after adding an edge...");
		//float myangle = FastMath.atan2(getDX(), getDY());
		float e_angle = edge.getAngle(); //FastMath.atan2(edge.getDX(), edge.getDY());
		while(e_angle < getAngle())
			e_angle += FastMath.TWO_PI;
		while(e_angle > getAngle()+FastMath.TWO_PI)
			e_angle -= FastMath.TWO_PI;
		if(e_angle == getAngle())
		{
			// We have the same angle, then the unreal edges are on the outer rim, hence the angle is 2 PI and not 0,  
			if(!isRealEdge())
				e_angle += FastMath.TWO_PI;
		}
		return e_angle - getAngle();
	}
	
	float getDX()
	{
		return twin.orig.point.x - orig.point.x;
	}
	float getDY()
	{
		return twin.orig.point.y - orig.point.y;
	}
	float getAngle()
	{
		return angle;
	}
	public PlanarVertex getDestination()
	{
		return twin.orig;
	}
	PlanarEdge getNext()
	{
		return next;
	}
	void setNext(PlanarEdge next)
	{
		this.next = next;
		next.prev = this;
		/*
		if(isRealEdge() != next.isRealEdge())
		{
			logger.info("\nReal/Unreal edges bound in next/prev relation:");
			logger.info("Edge(prev):"+this);
			logger.info("Edge(next):"+next);
		}
		else
		{
			logger.info("\nCorrect bound in next/prev relation:");
			logger.info("== Edge(prev):"+this);
			logger.info("== Edge(next):"+next);
		}
		*/
	}
	PlanarEdge getPrev()
	{
		return prev;
	}
	PlanarEdge getTwin()
	{
		return twin;
	}
	void setTwin(PlanarEdge  twin)
	{
		this.twin = twin;
		twin.twin = this;
		
		angle = FastMath.atan2(getDY(), getDX());
		twin.angle = FastMath.atan2(twin.getDY(), twin.getDX());
		
		float limit = FastMath.FLT_EPSILON*4;
		float anglediff = FastMath.abs(angleCounterClockWise(twin) - FastMath.PI);
		if(anglediff > limit)
		{
			throw new GeometricException("Two twins do not have opposite angles: "+angleCounterClockWise(twin)+" != "+FastMath.PI+" : ("+anglediff+" > "+limit+")");
		}
	}
}
