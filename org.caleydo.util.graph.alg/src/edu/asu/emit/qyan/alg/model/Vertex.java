/*
 *
 * Copyright (c) 2004-2008 Arizona State University.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY ARIZONA STATE UNIVERSITY ``AS IS'' AND
 * ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL ARIZONA STATE UNIVERSITY
 * NOR ITS EMPLOYEES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package edu.asu.emit.qyan.alg.model;

import edu.asu.emit.qyan.alg.model.abstracts.BaseVertex;

/**
 * @author <a href='mailto:Yan.Qi@asu.edu'>Yan Qi</a>
 * @version $Revision: 46 $
 * @latest $Date: 2010-06-05 00:54:27 -0700 (Sat, 05 Jun 2010) $
 */
public class Vertex implements BaseVertex, Comparable<Vertex>
{
	private static int CURRENT_VERTEX_NUM = 0;
	private int _id = CURRENT_VERTEX_NUM++;
	private double _weight = 0;

	protected Object vertexData;

	public Vertex(Object vertexData) {
		this.vertexData = vertexData;
	}

	/**
	 *
	 */
	@Override
	public int get_id()
	{
		return _id;
	}

	@Override
	public String toString()
	{
		return ""+_id;
	}

	@Override
	public double get_weight()
	{
		return _weight;
	}

	@Override
	public void set_weight(double status)
	{
		_weight = status;
	}

	@Override
	public int compareTo(Vertex r_vertex)
	{
		double diff = this._weight - r_vertex._weight;
		if(diff > 0)
			return 1;
		else if(diff < 0)
			return -1;
		else
			return 0;
	}

	public static void reset()
	{
		CURRENT_VERTEX_NUM = 0;
	}

	@Override
	public Object getVertexData() {
		return vertexData;
	}
}
