/*
 *
 * Copyright (c) 2004-2009 Arizona State University.  All rights
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

import java.util.LinkedList;
import java.util.List;

import edu.asu.emit.qyan.alg.model.abstracts.BaseElementWithWeight;

/**
 * @author <a href='mailto:Yan.Qi@asu.edu'>Yan Qi</a>
 * @version $Revision: 673 $
 * @latest $Id: QYPriorityQueue.java 673 2009-02-05 08:19:18Z qyan $
 */
public class QYPriorityQueue<E extends BaseElementWithWeight>
{
	List<E> _element_weight_pair_list = new LinkedList<E>();
	int _limit_size = -1;
	boolean _is_incremental = false; 
	
	/**
	 * Default constructor. 
	 */
	public QYPriorityQueue(){};
	
	/**
	 * Constructor. 
	 * @param limit_size
	 */
	public QYPriorityQueue(int limit_size, boolean is_incremental)
	{
		_limit_size = limit_size;
		_is_incremental = is_incremental;
	}
		
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return _element_weight_pair_list.toString();
	}
	
	/**
	 * Binary search is exploited to find the right position 
	 * of the new element. 
	 * @param weight
	 * @return the position of the new element
	 */
	private int _bin_locate_pos(double weight, boolean is_incremental)
	{
		int mid = 0;
		int low = 0;
		int high = _element_weight_pair_list.size() - 1;
		//
		while(low <= high)
		{
			mid = (low+high)/2;
			if(_element_weight_pair_list.get(mid).get_weight() == weight)
				return mid+1;
			
			if(is_incremental)
			{
				if(_element_weight_pair_list.get(mid).get_weight() < weight)
				{
					high = mid - 1;
				}else
				{
					low = mid + 1;
				}	
			}else
			{
				if(_element_weight_pair_list.get(mid).get_weight() > weight)
				{
					high = mid - 1;
				}else
				{
					low = mid + 1;
				}
			}	
		}
		return low;
	}
	
	/**
	 * Add a new element in the queue. 
	 * @param element
	 */
	public void add(E element)
	{
		_element_weight_pair_list.add(_bin_locate_pos(element.get_weight(), _is_incremental), element);
		
		if(_limit_size > 0 && _element_weight_pair_list.size() > _limit_size)
		{
			int size_of_results = _element_weight_pair_list.size();
			_element_weight_pair_list.remove(size_of_results-1);			
		}
	}
	
	/**
	 * It only reflects the size of the current results.
	 * @return
	 */
	public int size()
	{
		return _element_weight_pair_list.size();
	}
	
	/**
	 * Get the i th element. 
	 * @param i
	 * @return
	 */
	public E get(int i)
	{
		if(i >= _element_weight_pair_list.size())
		{
			System.err.println("The result :" + i +" doesn't exist!!!");
		}
		return _element_weight_pair_list.get(i);
	}
	
	/**
	 * Get the first element, and then remove it from the queue. 
	 * @return
	 */
	public E poll()
	{
		E ret = _element_weight_pair_list.get(0);
		_element_weight_pair_list.remove(0);
		return ret;
	}
	
	/**
	 * Check if it's empty.
	 * @return
	 */
	public boolean isEmpty()
	{
		return _element_weight_pair_list.isEmpty();
	}
	
}
