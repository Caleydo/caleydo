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

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingCapsule;
import com.jme.bounding.BoundingSphere;
import com.jme.bounding.BoundingVolume;
import com.jme.bounding.OrientedBoundingBox;
import com.jme.input.util.SyntheticButton;
import com.jme.math.LineSegment;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Capsule;
import com.jme.scene.shape.GeoSphere;
import com.jme.scene.shape.Sphere;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jmex.physics.geometry.PhysicsBox;
import com.jmex.physics.geometry.PhysicsCapsule;
import com.jmex.physics.geometry.PhysicsCylinder;
import com.jmex.physics.geometry.PhysicsMesh;
import com.jmex.physics.geometry.PhysicsRay;
import com.jmex.physics.geometry.PhysicsSphere;
import com.jmex.physics.material.Material;
import com.jmex.terrain.TerrainBlock;

/**
 * A PhysicsNode defines a physical entity. Its children may be graphical and physical scenegraph Spatial. The graphical
 * Spatials are used for visual representation and may be used for automatic computation of physical representation
 * (see {@link #generatePhysicsGeometry}, {@link PhysicsCollisionGeometry}). The physical Spatials define the collision
 * bounds for the physics simulation. Advanced implementation may even use them for mass distribution of dynamic nodes.
 * <p/>
 * <p/>
 * PhysicsNodes are created solely by the PhysicsSpace (methods {@link PhysicsSpace#createDynamicNode()} and
 * {@link PhysicsSpace#createStaticNode()}).
 *
 * @author Irrisor
 * @see #generatePhysicsGeometry()
 * @see DynamicPhysicsNode
 * @see StaticPhysicsNode
 * @see PhysicsCollisionGeometry
 */
public abstract class PhysicsNode extends Node implements PhysicsSpatial {
    /**
     * Constructor.
     *
     * @param name name of the node
     */
    PhysicsNode( String name ) {
        super( name );
    }

    PhysicsNode() {
        this( null );
    }

    /**
     * @return space this node belongs to, must not be null
     */
    public abstract PhysicsSpace getSpace();

    /**
     * @return true if this is a static (passive, immovable) node.
     */
    public abstract boolean isStatic();

    /**
     * This method generates physics geometry bounds for detecting collision from the graphical representation in this
     * PhysicsNode.
     *
     * @throws IllegalStateException if no graphical representation is present (no Geometries within this Node)
     * @see PhysicsCollisionGeometry
     */
    public void generatePhysicsGeometry() {
        generatePhysicsGeometry( false );
    }

    /**
     * This method generates physics geometry bounds for detecting collision from the graphical representation in this
     * PhysicsNode.
     *
     * @param useTriangleAccurateGeometries true to use triangle accuracy for collision detection with arbitrary
     * geometries - use with care! (makes it expensive to compute collisions)
     * @throws IllegalStateException if no graphical representation is present (no Geometries within this Node)
     * @see PhysicsCollisionGeometry
     */
    public void generatePhysicsGeometry( boolean useTriangleAccurateGeometries ) {
    	generatePhysicsGeometry( this, this, useTriangleAccurateGeometries );
    }

    /**
     * This method generates physics geometry bounds for detecting collision from the graphical representation in this
     * PhysicsNode.
     *
     * @param source The Node whose children are traversed to create corresponding collision geometries
     * @param target The PhysicsNode to which the collision geometries are added. Can be the same object as <b>source</b>.
     * @param useTriangleAccurateGeometries true to use triangle accuracy for collision detection with arbitrary
     * geometries - use with care! (makes it expensive to compute collisions)
     * @throws IllegalStateException if no graphical representation is present (no Geometries within this Node)
     * @see PhysicsCollisionGeometry
     */
    public static void generatePhysicsGeometry( Node source, PhysicsNode target, boolean useTriangleAccurateGeometries ) {
    	generatePhysicsGeometry( source, target, useTriangleAccurateGeometries, null );
    }

    /**
     * This method generates physics geometry bounds for detecting collision from the graphical representation in this
     * PhysicsNode.
     *
     * @param source The Node whose children are traversed to create corresponding collision geometries
     * @param target The PhysicsNode to which the collision geometries are added. Can be the same object as <b>source</b>.
     * @param useTriangleAccurateGeometries true to use triangle accuracy for collision detection with arbitrary
     * geometries - use with care! (makes it expensive to compute collisions)
     * @param collisionGeometryMap If a map is passed, each spatial for which a collision geometry is generated
     * will be put into it with the collision geometry as value. Also, Spatials already inside the map won't have
     * any collision geometry generated. Pass null to avoid this.
     * @throws IllegalStateException if no graphical representation is present (no Geometries within this Node)
     * @see PhysicsCollisionGeometry
     */
    public static void generatePhysicsGeometry( Node source, PhysicsNode target, boolean useTriangleAccurateGeometries, Map<Spatial, PhysicsCollisionGeometry> collisionGeometryMap ) {
        source.updateGeometricState( 0, true );
        Vector3f translation = new Vector3f();
        Quaternion rotation = new Quaternion();
        Vector3f scale = new Vector3f( 1, 1, 1 );
        for ( int i = source.getQuantity() - 1; i >= 0; i-- ) {
            Spatial child = source.getChild( i );
            if ( ! ( child instanceof PhysicsCollisionGeometry ) ) {
                target.addPhysicsGeometries( child, translation, rotation, scale, useTriangleAccurateGeometries, collisionGeometryMap );
            }
        }
    }

    private void addPhysicsGeometries( Spatial spatial, Vector3f translation, Quaternion rotation, Vector3f scale,
                                       boolean useTriangleAccurateGeometries, Map<Spatial, PhysicsCollisionGeometry> collisionGeometryMap ) {
        {
            // incorporate spatials transforms into translation/rotation/scale
            // we have to create new objects here as the method is invoked recursively
            Vector3f nextTranslation = rotation.mult( spatial.getLocalTranslation(),
                    new Vector3f() ).multLocal( scale )
                    .addLocal( translation );
            Quaternion nextRotation = rotation.mult( spatial.getLocalRotation(), new Quaternion() );
            Vector3f localScale = spatial.getLocalScale();
            Vector3f nextScale = new Vector3f( scale ).multLocal( localScale );
            if ( nextScale.x <= 0 || nextScale.y <= 0 || nextScale.z <= 0 ) {
                Logger.getLogger( PhysicsSpace.LOGGER_NAME ).log( Level.WARNING, "Skipped node:" ,
                        new IllegalArgumentException( "Scale cannot have a component that is 0 (or smaller) to " +
                                "generate collision geometries!" ) );
                return;
            }
            translation = nextTranslation;
            rotation = nextRotation;
            scale = nextScale;
        }

        PhysicsCollisionGeometry collisionGeometry = null;
    	if ( collisionGeometryMap == null || !collisionGeometryMap.containsKey( spatial ) ) {
    		// generate collision geometry for the spatial
        	collisionGeometry = generatePhysicsGeometry( spatial, translation, rotation, scale, useTriangleAccurateGeometries );

            if ( collisionGeometry != null && collisionGeometryMap != null )
            	collisionGeometryMap.put( spatial, collisionGeometry );
    	}

    	if ( collisionGeometry != null ) {
	    	collisionGeometry.setName( spatial.getName() );

	    	// update the collision geometry with the transformation of the spatial
	    	rotation.mult( collisionGeometry.getLocalTranslation(),
	    		collisionGeometry.getLocalTranslation() ).multLocal( scale )
	    		.addLocal( translation );
	        rotation.mult( collisionGeometry.getLocalRotation(), collisionGeometry.getLocalRotation() );
	        	collisionGeometry.getLocalScale().multLocal( scale );
    	}

        if ( spatial instanceof Node ) {
            Node node = (Node) spatial;
            for ( int i = node.getQuantity() - 1; i >= 0; i-- ) {
                Spatial child = node.getChild( i );
                addPhysicsGeometries( child, translation, rotation, scale, useTriangleAccurateGeometries, collisionGeometryMap );
            }
        }
    }

    /**
     * This method generates physics geometry bounds for detecting collision from the graphical representation of the given Spatial.
     *
     * @param spatial The Spatial for which to generate physics geometry.
     * @param translation World translation of the Spatial.
     * @param rotation World rotation of the Spatial.
     * @param scale World scale of the Spatial.
     * @param useTriangleAccurateGeometries true to use triangle accuracy for collision detection with arbitrary
     * geometries - use with care! (makes it expensive to compute collisions)
     * @return The collision geometry or <b>null</b> if the Spatial's type is not recognized.
     */
	private PhysicsCollisionGeometry generatePhysicsGeometry( Spatial spatial, Vector3f translation, Quaternion rotation, Vector3f scale, boolean useTriangleAccurateGeometries ) {
        if ( spatial instanceof Geometry ) {
            Geometry geometry = (Geometry) spatial;

            try {
	            BoundingVolume bound = geometry.getModelBound();

	            if ( bound == null || useTriangleAccurateGeometries ) {
	                // try to create default bounding for unbounded jME primitives
	                if ( geometry instanceof Box ) {
	                    Box box = (Box) geometry;
	                    bound = new BoundingBox( box.getCenter(), box.xExtent, box.yExtent, box.zExtent );
	                }
	                else if ( geometry instanceof Sphere ) {
	                    Sphere sphere = (Sphere) geometry;
	                    bound = new BoundingSphere( sphere.radius, sphere.getCenter() );
	                } else if ( geometry instanceof GeoSphere ) {
                        GeoSphere sphere = (GeoSphere) geometry;
                        bound = new BoundingSphere( sphere.getRadius(), new Vector3f() );
                    } else if ( geometry instanceof Capsule ) {
                        Capsule capsule = (Capsule) geometry;
                        bound = new BoundingCapsule( new Vector3f(),
                                new LineSegment( new Vector3f(), new Vector3f( 0, 1, 0 ),
                                        capsule.getHeight()/2 ),
                                capsule.getRadius() );
                        //debug:
                        capsule.setModelBound( bound );
                    }
	                // TODO: more defaults?
	                else {
	                    if ( !useTriangleAccurateGeometries ) {
	                        Logger.getLogger( PhysicsSpace.LOGGER_NAME ).warning( "no model bound: " + spatial );
	                        return null;
	                    }
	                }
	                if ( !useTriangleAccurateGeometries ) {
	                    Logger.getLogger( PhysicsSpace.LOGGER_NAME ).info( "using default model bound for: " + spatial );
	                }
	            }

	            PhysicsCollisionGeometry collisionGeometry;
	            if ( useTriangleAccurateGeometries ) {
	                if ( geometry instanceof Box ) {
	                    collisionGeometry = createPhysicsGeometry( (BoundingBox) bound );
	                }
	                else if ( geometry instanceof Sphere ) {
	                    collisionGeometry = createPhysicsGeometry( (BoundingSphere) bound );
	                }
                    else if ( geometry instanceof Capsule ) {
                        collisionGeometry = createPhysicsGeometry( (BoundingCapsule) bound );
                    }
	                else if ( geometry instanceof TriMesh ) {
	                	collisionGeometry = createPhysicsGeometry( (TriMesh) geometry );
	                } else {
	                    Logger.getLogger( PhysicsSpace.LOGGER_NAME ).warning( "unknown type: " + spatial );
	                    return null;
	                }
	            } else {
	                if ( geometry instanceof TerrainBlock ) {
	                    collisionGeometry = createPhysicsGeometry( (TriMesh) geometry );
	                }
	                else if ( bound instanceof BoundingSphere ) {
	                    collisionGeometry = createPhysicsGeometry( (BoundingSphere) bound );
	                }
	                else if ( bound instanceof BoundingBox ) {
	                    collisionGeometry = createPhysicsGeometry( (BoundingBox) bound );
	                }
	                else if ( bound instanceof OrientedBoundingBox ) {
	                    collisionGeometry = createPhysicsGeometry( (OrientedBoundingBox) bound );
	                }
                    else if ( bound instanceof BoundingCapsule ) {
                        collisionGeometry = createPhysicsGeometry( (BoundingCapsule) bound );
                    }
	                else {
	                    throw new RuntimeException( "Unknown bounding volume type: " + bound.getClass() );
	                }
	            }
	            return collisionGeometry;
	        }
            catch (IllegalArgumentException e) {
                // HACK: this identifies TriMeshes which are created by the 3DS loader
                // for sub-materials (multiple materials inside the same mesh);
            		// see TDSFile.putChildMeshes(). These meshes can have bounds of zero volume.
                if (geometry.getName().contains("##"))
                		Logger.getLogger( PhysicsSpace.LOGGER_NAME ).warning(geometry.getName() + ": " + e.getMessage());
                else
                		throw e;
            }
        }
        return null;
	}

    private PhysicsCollisionGeometry createPhysicsGeometry( TriMesh geometry ) {
        PhysicsMesh mesh = createMesh( null );
        mesh.copyFrom( geometry );
        mesh.getLocalTranslation().set( 0, 0, 0 );
        mesh.setLocalScale( 1 );
        mesh.getLocalRotation().set( 0, 0, 0, 1 );
        return mesh;
    }

    private PhysicsCollisionGeometry createPhysicsGeometry( OrientedBoundingBox box ) {
        if ( box.getExtent().x <= 0 || box.getExtent().y <= 0 || box.getExtent().z <= 0 ) {
            Logger.getLogger( PhysicsSpace.LOGGER_NAME ).log( Level.WARNING, "Skipped geometry:" ,
                    new IllegalArgumentException( "Extent cannot have a component that is 0 to generate " +
                    "collision geometries!" ) );
            return null;
        }
        PhysicsBox geom = createBox( null );
        geom.getLocalScale().set( box.getExtent().x * 2, box.getExtent().y * 2, box.getExtent().z * 2 );
        geom.getLocalTranslation().set( box.getCenter() );
        geom.getLocalRotation().fromAxes( box.getXAxis(), box.getYAxis(), box.getZAxis() );
        return geom;
    }

    private PhysicsCollisionGeometry createPhysicsGeometry( BoundingBox box ) {
        if ( box.xExtent <= 0 || box.yExtent <= 0 || box.zExtent <= 0 ) {
            Logger.getLogger( PhysicsSpace.LOGGER_NAME ).log( Level.WARNING, "Skipped geometry:" ,
                    new IllegalArgumentException( "Extent cannot have a component that is 0 to generate " +
                    "collision geometries!" ) );
        }
        PhysicsBox geom = createBox( null );
        geom.getLocalScale().set( box.xExtent * 2, box.yExtent * 2, box.zExtent * 2 );
        geom.getLocalTranslation().set( box.getCenter() );
        return geom;
    }

    private PhysicsCollisionGeometry createPhysicsGeometry( BoundingSphere sphere ) {
        float radius = sphere.getRadius();
        if ( radius <= 0 ) {
            throw new IllegalArgumentException( "Found bounding sphere with radius 0 - this is not allowed!" );
        }
        PhysicsSphere geom = createSphere( null );
        geom.setLocalScale( radius );
        geom.getLocalTranslation().set( sphere.getCenter() );
        return geom;
    }

    private PhysicsCollisionGeometry createPhysicsGeometry( BoundingCapsule capsule ) {
        float radius = capsule.getRadius();
        if ( radius <= 0 ) {
            throw new IllegalArgumentException( "Found bounding capsule with radius 0 - this is not allowed!" );
        }
        float height = capsule.getLineSegment().getExtent() * 2;
        if ( height <= 0 ) {
            throw new IllegalArgumentException( "Found bounding capsule with height 0 - this is not allowed!" );
        }
        PhysicsCapsule geom = createCapsule( null );
        geom.getLocalScale().set( radius, radius, height );
        final Vector3f dir = capsule.getLineSegment().getDirection();
        //noinspection SuspiciousNameCombination
        final Vector3f dir2 = new Vector3f( dir.z,  dir.x, dir.y );
        //noinspection SuspiciousNameCombination
        final Vector3f dir3 = new Vector3f( dir.y,  dir.z, dir.x );
        geom.getLocalRotation().fromAxes( dir2, dir3, dir );
        geom.getLocalTranslation().set( capsule.getCenter() ).addLocal( capsule.getLineSegment().getOrigin() );
        return geom;
    }

    /**
     * overridden to check we don't get another PhysicsNode as parent.
     *
     * @param parent new Parent
     * @see com.jme.scene.Spatial#setParent(com.jme.scene.Node)
     */
    @Override
    protected void setParent( Node parent ) {
        if ( parent != null ) {
            Node ancestor = parent;
            while ( ancestor != null ) {
                if ( ancestor instanceof DynamicPhysicsNode ) {
                    throw new IllegalArgumentException( "DynamicPhysicsNodes cannot contain other PhysicsNodes!" );
                }
                ancestor = ancestor.getParent();
            }
        }
        super.setParent( parent );
    }

//    /**
//     * Activate physical behaviour. PhysicsNodes are in deactivated state after creation. PhysicsNode does not interfer
//     * with other PhysicsNodes nor react in any other physical way while deactivated.
//     * Thus all PhysicsNodes have to be activated.
//     * <p/>
//     * If physics Spatials are present within this Node they are used for the physical collision bounds of this Node.
//     * If no such Spatials exist they are generated from the graphical representation by calling
//     * {@link #generatePhysicsGeometry()}.
//     *
//     * @see #deactivate
//     */
//    public abstract void activate();
//
//    /**
//     * Deactivate physical behaviour. PhysicsNode does not interfer
//     * with other PhysicsNodes nor react in any other physical way while deactivated.
//     *
//     * @see #activate
//     */
//    public abstract void deactivate();

    /**
     * Draw information about the physical properties of the Node.
     *
     * @param renderer where to draw to
     */
    protected void drawDebugInfo( Renderer renderer ) {
        PhysicsDebugger.drawCollisionGeometry( this, renderer );
    }

//    /**
//     * @return true if node was activated
//     * @see #activate()
//     */
//    public abstract boolean isActivated();

    /**
     * Create a physics sphere.
     *
     * @param name name of the Spatial
     * @return a new physics sphere
     * @see PhysicsCollisionGeometry
     * @see PhysicsSphere
     */
    public PhysicsSphere createSphere( String name ) {
        return getSpace().createSphere( name, this );
    }

    /**
     * Create a physics box.
     *
     * @param name name of the Spatial
     * @return a new physics box
     * @see PhysicsCollisionGeometry
     * @see PhysicsBox
     */
    public PhysicsBox createBox( String name ) {
        return getSpace().createBox( name, this );
    }

    /**
     * Create a physics cylinder.
     *
     * @param name name of the Spatial
     * @return a new physics cylinder
     * @see PhysicsCollisionGeometry
     * @see PhysicsCylinder
     */
    public PhysicsCylinder createCylinder( String name ) {
        return getSpace().createCylinder( name, this );
    }

    /**
     * Create a physics capsule.
     *
     * @param name name of the Spatial
     * @return a new physics capsule
     * @see PhysicsCollisionGeometry
     * @see PhysicsCapsule
     */
    public PhysicsCapsule createCapsule( String name ) {
        return getSpace().createCapsule( name, this );
    }

    /**
     * Create a physics mesh.
     *
     * @param name name of the Spatial
     * @return a new physics mesh
     * @see PhysicsCollisionGeometry
     * @see PhysicsMesh
     */
    public PhysicsMesh createMesh( String name ) {
        return getSpace().createMesh( name, this );
    }

    /**
     * Create a physics ray.
     *
     * @param name name of the Spatial
     * @return a new physics ray
     * @see PhysicsCollisionGeometry
     * @see PhysicsRay
     */
    public PhysicsRay createRay( String name ) {
        return getSpace().createRay( name, this );
    }

    private boolean active;

    /**
     * @return true if node is currently active
     */
    public final boolean isActive() {
        return active;
    }

    /**
     * Activate the node when added to a space. Deactivate when removed.
     *
     * @param value true when activated
     * @return true if node was (de)activated, false if state was already set to value
     */
    public boolean setActive( boolean value ) {
        if ( active != value ) {
            active = value;
            if ( value ) {
                getSpace().addNode( this );
            }
            else {
                getSpace().removeNode( this );
            }
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Query default material of this node. If no material was set for a geometry the material of this
     * physics node is returned.
     *
     * @return material used for inheriting geometries, not null
     * @see PhysicsCollisionGeometry#getMaterial
     */
    public Material getMaterial() {
        Material material = this.material;
        if ( material != null ) {
            return material;
        }
        else {
            return getSpace().getDefaultMaterial();
        }
    }

    /**
     * store the value for field material
     */
    private Material material;

    /**
     * Change material of this node. All geometries that did not have a matrial set as well as those with the same
     * material like the node inherit this nodes material.
     *
     * @param value new material
     * @see PhysicsCollisionGeometry#setMaterial
     */
    public void setMaterial( final Material value ) {
        this.material = value;
    }

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

    public void delete()
    {
        removeFromParent();
        setActive( false );
    }

    /**
     * @return true if the physics engine believes that this nodes is resting like described in the
     * {@link DynamicPhysicsNode#rest} method. For static nodes this return true, always.
     * @see DynamicPhysicsNode#rest
     */
    public abstract boolean isResting();

    public final PhysicsNode getPhysicsNode() {
        return this;
    }

	@Override
    public Class getClassTag() {
    		return PhysicsNode.class;
    }

    public static final String MATERIAL_PROPERTY = "material";

	@Override
	public void read(JMEImporter im) throws IOException {
		super.read(im);

        InputCapsule capsule = im.getCapsule( this );

        Material material = ( Material ) capsule.readSavable( MATERIAL_PROPERTY, null );
        setMaterial( Material.checkForCommonMaterial( material ) );

        reassignMaterials( this );
	}

	/**
	 * Reassigns materials so collision geometry materials are nulled if they are
	 * identical to this node's material.
	 */
	private void reassignMaterials( Node root ) {
        if ( getChildren() != null ) {
        	for ( Spatial child : root.getChildren() ) {
        		if ( child instanceof PhysicsCollisionGeometry ) {
					PhysicsCollisionGeometry physicsCollisionGeometry = ( PhysicsCollisionGeometry ) child;
					physicsCollisionGeometry.setMaterial( physicsCollisionGeometry.getMaterial() );
				}
        		if ( child instanceof Node ) {
        			reassignMaterials( ( Node ) child );
				}
        	}
        }
	}

	@Override
	public void write(JMEExporter ex) throws IOException {
		super.write(ex);

        OutputCapsule capsule = ex.getCapsule( this );

        capsule.write(getMaterial(), MATERIAL_PROPERTY, null);
	}

    /**
     * store value for field collisionGroup
     */
    private CollisionGroup collisionGroup;

    /**
     * @return current value of the field collisionGroup
     */
    public CollisionGroup getCollisionGroup() {
        return this.collisionGroup;
    }

    /**
     * @see CollisionGroup
     * @param value new value for field collisionGroup
     * @return true if collisionGroup was changed
     */
    public void setCollisionGroup( CollisionGroup value ) {
        if ( value == null )
        {
            throw new IllegalArgumentException( "A physics node must be in a collision group." );
        }
        final CollisionGroup oldValue = this.collisionGroup;
        if ( oldValue != value ) {
            if ( oldValue != null ) {
                this.collisionGroup = null;
                oldValue.removeFromNodes( this );
            }
            this.collisionGroup = value;
//            firePropertyChange( "collisionGroup", oldValue, value );
            value.addToNodes( this );
        }
    }
}

/*
* $log$
*/
