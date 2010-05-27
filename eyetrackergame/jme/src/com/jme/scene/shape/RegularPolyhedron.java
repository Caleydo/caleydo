// Copyright © 2008 JMonkeyEngine, all rights reserved.
// See the accompanying LICENSE file for terms and conditions of use.
// $Id: RegularPolyhedron.java 4131 2009-03-19 20:15:28Z blaine.dev $
package com.jme.scene.shape;

import com.jme.scene.TriMesh;

/**
 * A polyhedron whose faces and edges are all identical.
 * <p>
 * The main purpose of this class is to expose the side length property so
 * that tools may operate on any of the polyhedron classes without needing
 * specific code for each.
 * <p>
 * Note that the use of the term 'regular polyhedron' here is slightly different
 * from the pure mathematical definition. For more details see the page at
 * <a href="http://mathworld.wolfram.com/RegularPolyhedron.html">Math World</a>.
 *
 * @author <a href="mailto:ianp@ianp.org">Ian Phillips</a>
 * @version $Revision: 4131 $, $Date: 2009-03-19 21:15:28 +0100 (Do, 19 Mär 2009) $
 */
public abstract class RegularPolyhedron extends TriMesh {

    private static final long serialVersionUID = 1L;

    protected float sideLength;
    
    public RegularPolyhedron() {
    }

    public RegularPolyhedron(String name) {
        super(name);
    }

    /**
     * Update all of the computed information about this polyhedron.
     * <p>
     * This will be automatically called each time the side length is altered.
     */
    protected abstract void doUpdateGeometry();

    /** Get the length of an edge. */
    public float getSideLength() {
        return sideLength;
    }

    /**
     * Set the length of an edge.
     * <p>
     * <strong>Note:</strong> this method causes the tri-mesh geometry data
     * to be recalculated, see <a href="package-summary.html#mutator-methods">
     * the package description</a> for more information about this.
     * 
     * @param sideLength
     */
    public final void updateGeometry(float sideLength) {
        this.sideLength = sideLength;
        doUpdateGeometry();
    }

}
