package com.jmex.physics.impl.jbullet.geometry;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.IndexedMesh;
import com.bulletphysics.collision.shapes.TriangleIndexVertexArray;
import com.jme.math.Matrix3f;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.TriMesh;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.geometry.PhysicsMesh;
import com.jmex.physics.impl.jbullet.JBulletPhysicsNode;
import com.jmex.physics.impl.jbullet.geometry.proxies.MeshProxy;
import com.jmex.physics.impl.jbullet.util.VecmathConverter;
import com.jmex.physics.material.Material;

public class JBulletMesh extends PhysicsMesh implements JBulletGeometry {

    private CollisionShape shape;
    private boolean dirty;
    private com.jme.math.Vector3f previousScale = new com.jme.math.Vector3f();
    private Quaternion previousRotation = new Quaternion();
    private com.jme.math.Vector3f previousTranslation = new com.jme.math.Vector3f();

    private float volume;

    private FloatBuffer vertices;
    private IntBuffer indices;

    public JBulletMesh( PhysicsNode node ) {
        super( node );
    }

    @Override
    public void copyFrom( TriMesh triMesh, float volume, Vector3f centerOfMass, Matrix3f inertia ) {
        copyFrom( triMesh, volume );
    }

    @Override
    public void copyFrom( TriMesh mesh ) {

        // Create vertices buffer
        if ( vertices != null ) {
            vertices.clear();
        }

        // Create indices buffer
        if ( indices != null ) {
            indices.clear();
        }

        // Retrieve mesh scale to bake vertex data

        TriangleIndexVertexArray jBulletMeshData = new TriangleIndexVertexArray();

        IndexedMesh jBulletIndexedMesh = new IndexedMesh();
        jBulletIndexedMesh.triangleIndexBase = ByteBuffer.allocate( mesh.getTriangleCount() * 3 * 4 );
        jBulletIndexedMesh.vertexBase = ByteBuffer.allocate( mesh.getVertexCount() * 3 * 4 );

        IntBuffer indices = mesh.getIndexBuffer();
        indices.rewind();

        FloatBuffer vertices = mesh.getVertexBuffer();
        vertices.rewind();

        int verticesLength = mesh.getVertexCount() * 3;
        jBulletIndexedMesh.numVertices = verticesLength;
        jBulletIndexedMesh.vertexStride = 12; //3 verts * 4 bytes per.
        for ( int i = 0; i < verticesLength; i++ ) {
            float tempFloat = vertices.get();
            jBulletIndexedMesh.vertexBase.putFloat( tempFloat );
        }

        int indicesLength = mesh.getTriangleCount() * 3;
        jBulletIndexedMesh.numTriangles = mesh.getTriangleCount();
        jBulletIndexedMesh.triangleIndexStride = 12; //3 index entries * 4 bytes each.
        for ( int i = 0; i < indicesLength; i++ ) {
            jBulletIndexedMesh.triangleIndexBase.putInt( indices.get() );
        }

        jBulletMeshData.addIndexedMesh( jBulletIndexedMesh );

        shape = new MeshProxy( jBulletMeshData, true, this);
        shape.setLocalScaling( VecmathConverter.convert( getParent().getLocalScale() ) );

    }

    @Override
    protected void drawDebugShape( PhysicsNode physicsNode, Renderer renderer ) {
        // TODO Auto-generated method stub

    }

    public void copyFrom( TriMesh mesh, float volume ) {
        copyFrom( mesh );
        this.volume = volume;
    }

    public CollisionShape getJBulletShape() {
        if ( shape == null ) {
            throw new IllegalArgumentException( "Cannot access the underlying CollisionShape until the TriMesh has been defined." );
        }
        return shape;
    }

    public boolean isDirty() {
        if ( dirty ) {
            return true;
        }
        return !( getLocalTranslation().equals( previousTranslation ) &&
                getLocalRotation().equals( previousRotation ) &&
                getLocalScale().equals( previousScale ) );
    }

    public void setDirty( boolean value ) {
        dirty = value;
        if ( !value ) {
            previousTranslation.set( getLocalTranslation() );
            previousRotation.set( getLocalRotation() );
            previousScale.set( getLocalScale() );
        }
    }

    @Override
    public void setMaterial( Material value ) {
        super.setMaterial( value );
        dirty = true;
    }

    @Override
    protected void setNode( PhysicsNode node ) {
        ( (JBulletPhysicsNode) getPhysicsNode() ).setDirty( true );
        super.setNode( node );
        if ( node != null ) {
            ( (JBulletPhysicsNode) node ).setDirty( true );
        }
        dirty = true;
    }

    @Override
    protected void setParent( Node parent ) {
        ( (JBulletPhysicsNode) getPhysicsNode() ).setDirty( true );
        super.setParent( parent );
        if ( parent != null ) {
            ( (JBulletPhysicsNode) parent ).setDirty( true );
        }
        dirty = true;
    }

    @Override
    public void lookAt( com.jme.math.Vector3f position, com.jme.math.Vector3f upVector ) {
        super.lookAt( position, upVector );
        dirty = true;
    }

    @Override
    public void rotateUpTo( com.jme.math.Vector3f newUp ) {
        super.rotateUpTo( newUp );
        dirty = true;
    }

    @Override
    public void setLocalRotation( Matrix3f rotation ) {
        super.setLocalRotation( rotation );
        dirty = true;
    }

    @Override
    public void setLocalRotation( Quaternion quaternion ) {
        super.setLocalRotation( quaternion );
        dirty = true;
    }

    @Override
    public void setLocalScale( float localScale ) {
        super.setLocalScale( localScale );
        dirty = true;
    }

    @Override
    public void setLocalScale( com.jme.math.Vector3f localScale ) {
        super.setLocalScale( localScale );
        dirty = true;
    }

    @Override
    public void setLocalTranslation( float x, float y, float z ) {
        super.setLocalTranslation( x, y, z );
        dirty = true;
    }

    @Override
    public void setLocalTranslation( com.jme.math.Vector3f localTranslation ) {
        super.setLocalTranslation( localTranslation );
        dirty = true;
    }

    public float getVolume() {
        return volume;
    }

	public void buildShapeToScale() {
		return;
	}
}
