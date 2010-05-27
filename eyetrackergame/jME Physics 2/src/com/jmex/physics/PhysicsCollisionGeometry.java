/*
 * Copyright (c) 2005-2007 jME Physics 2
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of 'jME Physics 2' nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
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

package com.jmex.physics;

import com.jme.bounding.BoundingVolume;
import com.jme.input.util.SyntheticButton;
import com.jme.intersection.CollisionResults;
import com.jme.intersection.PickResults;
import com.jme.math.Ray;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jmex.physics.material.Material;

import java.io.IOException;

/**
 * Subclasses of this class are used to specify the physical geometry of a {@link PhysicsNode}. The physical geometry
 * defines the collision bounds for the physics simulation. Advanced implementation may even use them for mass
 * distribution of dynamic nodes.
 * <p/>
 * PhysicsCollisionGeometries are created via the create* methods in {@link PhysicsNode}.
 * <p/>
 * TODO: Cylinder, Cone, Capsule, TriMesh, Plane?, Ray?
 *
 * @author Irrisor
 */
public abstract class PhysicsCollisionGeometry extends Spatial implements PhysicsSpatial  {
    public static final String PHYSICS_NODE_PROPERTY = "physicsNode";

	@Override
    public void draw( Renderer r ) {
        // not drawn by default
    }

    public int getTriangleCount() {
        return 0;
    }

    public int getVertexCount() {
        return 0;
    }

    private PhysicsNode node;

    /**
     * Allow to change the associated node by the implementation
     * @param node new phyiscs node of this geometry
     */
    protected void setNode( PhysicsNode node ) {
        this.node = node;
    }

    protected PhysicsCollisionGeometry( PhysicsNode node, String name ) {
        super( name );
//        if ( node == null ) {
//            throw new NullPointerException();
//        }
        this.node = node;
    }

    protected PhysicsCollisionGeometry( PhysicsNode node ) {
        this( node, null );
    }

    public static final String MATERIAL_PROPERTY = "material";

    @Override
	public void read(JMEImporter im) throws IOException {
		super.read(im);
		
        InputCapsule capsule = im.getCapsule( this );

        Material material = (Material)capsule.readSavable(MATERIAL_PROPERTY, null);
        setMaterial(Material.checkForCommonMaterial(material));

		// physicsNode property is not read because it is set during collision geometry creation
	}

    
    
	@Override
	public void write(JMEExporter ex) throws IOException {
		super.write(ex);
        OutputCapsule capsule = ex.getCapsule(this);

        capsule.write(getParent(), PHYSICS_NODE_PROPERTY, null);
        capsule.write(getMaterial(), MATERIAL_PROPERTY, null);
	}

	public static PhysicsNode readPhysicsNodeFromInputCapsule(InputCapsule inputCapsule) throws IOException {
		return (PhysicsNode) inputCapsule.readSavable( PHYSICS_NODE_PROPERTY, null );
	}

	/**
     * Allow only PhysicsNodes as parent.
     *
     * @param parent new parent
     * @see Spatial#setParent(com.jme.scene.Node)
     */
    @Override
    protected void setParent( Node parent ) {
        if ( parent instanceof PhysicsNode || parent == null ) {
        	if ( parent != node && parent != null )
        		setNode( (PhysicsNode) parent );
       		super.setParent( parent );
        }
        else {
            throw new IllegalArgumentException( "parent of a PhysicsCollisionGeometry must be a PhysicsNode" );
        }
    }

    /**
     * Draw a visual representation of this physical collision bound.
     *
     * @param physicsNode node this geometry belongs to
     * @param renderer    where to draw to
     */
    protected abstract void drawDebugShape( PhysicsNode physicsNode, Renderer renderer );

    @Override
    public void findCollisions( Spatial scene, CollisionResults results, int requiredOnBits ) {
        // TODO: should this collide with other scenegraph objects?
    }

    @Override
    public void findPick( Ray toTest, PickResults results, int requiredOnBits ) {
        // TODO: should this be pickable
    }
    
    @Override
    public boolean hasCollision( Spatial scene, boolean checkTriangles, int requiredOnBits ) {
        // TODO: should this collide with other scenegraph objects?
        return false;
    }

    @Override
    public void updateWorldBound() {
        // TODO: need bounds?
//        if (bound != null) {
//            worldBound = bound.transform(worldRotation, worldTranslation,
//                    worldScale, worldBound);
//        }
    }

    /**
     * @return the PhysicsNode this collision geometry belongs to
     */
    public final PhysicsNode getPhysicsNode() {
        return node;
    }


    /**
     * Query material of this geometry. If no material was set for this geometry the material of the
     * physics node is returned.
     *
     * @return material used for this geometry, not null
     * @see PhysicsNode#getMaterial
     */
    public Material getMaterial() {
        Material material = this.material;
        if ( material != null ) {
            return material;
        }
        else {
            PhysicsNode node = getPhysicsNode();
            if ( node != null ) {
                return node.getMaterial();
            } else {
                return Material.DEFAULT;
            }
        }
    }

    /**
     * store the value for field material
     */
    private Material material;

    /**
     * Change material of this geometry. If the same material is specified for a geometry as the physics node currently
     * has the material of this geometry is inherited. The same applies if no material is set for a geometry.
     *
     * @param value new material
     * @see PhysicsNode#setMaterial
     */
    public void setMaterial( final Material value ) {
        if ( value == null || value == getPhysicsNode().getMaterial() ) {
            this.material = null;
        }
        else {
            this.material = value;
        }
    }

    /**
     * Query the volume of this geometry. Commonly used for mass calculation.
     *
     * @return the current value of this geometry
     */
    public abstract float getVolume();

    SyntheticButton collisionEventHandler;

    /**
     * Creates a synthetic button that is triggered when this node collides with another node.
     * <p>
     * Note: if this event handler is obtained it <i>must</i> be used with an InputHandler which is updated regularly
     *
     * @return a synthetic button that is triggered on a collision event that involves this node
     * @see PhysicsSpace#getCollisionEventHandler()
     */
    public SyntheticButton getCollisionEventHandler() {
        if ( collisionEventHandler == null ) {
            collisionEventHandler = new SyntheticButton( "collision" );
        }
        return collisionEventHandler;
    }

    public void updateModelBound() {
        // TODO: need bounds?
    }

    public void setModelBound( BoundingVolume modelBound ) {
        // TODO: need bounds?
    }
}

/*
* $log$
*/
