/*
 * Copyright (c) 2004, Alain Michaud
 * All rights reserved. 
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * - Neither the name of JGraph nor the names of its contributors may be used
 *   to endorse or promote products derived from this software without specific
 *   prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.jgraph.plugins.layouts;
 
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jgraph.graph.CellView;
import org.jgraph.graph.GraphModel;

/**
 * This class is similar to the TreeLayoutAlgorithm but it will sort
 * the different levels of the graph using the default comparator
 * or a custom comparator that you supply.
 *
 * @author Alain Michaud
 */
public class OrderedTreeLayoutAlgorithm extends TreeLayoutAlgorithm {

	private Comparator comparator = null;

	/**
	 * Constructor
	 * @param comparator use to sort the cells
	 */
	public OrderedTreeLayoutAlgorithm(Comparator comparator){
	    super();
	    setComparator(comparator);
	}

	/**
	 * Constructor (will use the default comparator)
	 */
	public OrderedTreeLayoutAlgorithm(){
	    super();
	    setComparator(new DefaultComparator());
	}

	/**
	 * Returns the name of this algorithm in human
	 * readable form.
	 */
	public String toString() {
		return "Ordered Tree Layout";
	}

    /**
     * @param comparator The cell's comparator
     */
    public void setComparator(Comparator comparator) {
        this.comparator = comparator;
    }

    /**
     * @return the cell comparator
     */
    public Comparator getComparator(){
        return this.comparator;
    }

    /**
     * Gets the children of the given cell view and sorts
     * it using a supplied comparator or the default comparator if
     * none is specified
     */
	public List getChildren(CellView view) {
	    //call in the getChildren methode of the TreeLayoutAlgorithm
	    List children = super.getChildren(view);

		//Sort the list using the provided comparator
		if(null == comparator){
		    Collections.sort(children);
		}
		else{
		    Collections.sort(children, comparator);
		}
		return children;
	}
	
	public class DefaultComparator implements Comparator {

		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Object o1, Object o2) {
			if (o1 instanceof CellView && o2 instanceof CellView) {
				GraphModel m = graph.getModel();
				Object c1 = ((CellView) o1).getCell();
				Object c2 = ((CellView) o2).getCell();
				Object p1 = m.getParent(c1);
				Object p2 = m.getParent(c2);
				int index1 = (p1 == null) ? m.getIndexOfRoot(c1) : m.getIndexOfChild(p1, c1);
				int index2 = (p2 == null) ? m.getIndexOfRoot(c2) : m.getIndexOfChild(p2, c2);
				return new Integer(index1).compareTo(new Integer(index2));
			}
			return -1;
		}
		
	}
 
}