/*
 * Open Dynamics Engine for Java (odejava) Copyright (c) 2004, Jani Laakso, All
 * rights reserved. Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the following conditions
 * are met: Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer. Redistributions
 * in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. Neither the name of the odejava nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission. THIS
 * SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.odejava;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jme.math.Matrix4f;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.TriMesh;
import org.odejava.ode.Ode;
import org.odejava.ode.OdeConstants;
import org.odejava.ode.SWIGTYPE_p_dxTriMeshData;
import org.odejava.ode.SWIGTYPE_p_float;
import org.odejava.ode.SWIGTYPE_p_int;

/**
 * A triangle mesh (TriMesh) represents an arbitrary collection of triangles.
 * The triangle mesh collision system has the following features:
 * <p/>
 * Any triangle soup can be represented i.e. the triangles are not required to
 * have any particular strip, fan or grid structure. Triangle meshes can
 * interact with spheres, boxes and rays. It works well for relatively large
 * triangles. It uses temporal coherence to speed up collision tests. When a
 * geom has its collision checked with a trimesh once, data is stored inside the
 * trimesh. This data can be cleared with the dGeomTriMeshClearTCCache function.
 * In the future it will be possible to disable this functionality.
 * <p/>
 * Note: give index in such way that triangles are build clockwise (z is up).
 * Created 16.12.2003 (dd.mm.yyyy)
 * <p/>
 * <br> see http://odejava.dev.java.net
 *
 * @author Jani Laakso E-mail: jani.laakso@itmill.com
 */
public class GeomTriMesh extends PlaceableGeom {
    private final SWIGTYPE_p_dxTriMeshData data;
    private SWIGTYPE_p_float odeVertices;
    private SWIGTYPE_p_int odeIndices;

    private final SWIGTYPE_p_float lastTransform_float16Array = Odejava.createSwigFloatArray( 16 );
    private final Matrix4f lastTransform_matrix = new Matrix4f();

    /**
     * Create and ode representation of the geometry from jme's own trimesh
     * class.
     * Regards current world scale.
     *
     * @param name An optional name to associate with this geometry
     * @param mesh graphical representation
     */
    public GeomTriMesh( String name, TriMesh mesh ) {
        super( name );

        // Create ODE TriMeshData
        data = Ode.dGeomTriMeshDataCreate();
        if ( mesh != null ) {
            updateData( mesh );
        }


        spaceId = Ode.getPARENTSPACEID_ZERO();

        // Create ODE TriMesh
        geomId = Ode.dCreateTriMesh( spaceId, data, null, null, null );

        retrieveNativeAddr();
    }

    /**
     * Recreates ODE vertices and indices lists for a given jME TriMesh object.
     *
     * @param mesh The TriMesh to be built in ODE.
     */
    public void updateData( TriMesh mesh ) {

        int totalVerticesLength = mesh.getVertexCount()*3;
        int totalIndicesLength = mesh.getTriangleCount()*3;

        // Create vertices buffer
        if ( odeVertices != null ) {
            Ode.delete_floatArray( odeVertices );
            odeVertices = null;
        }
        odeVertices = Odejava.createSwigFloatArray( totalVerticesLength );

        // Create indices buffer
        if ( odeIndices != null ) {
            Ode.delete_intArray( odeIndices );
            odeIndices = null;
        }
        SWIGTYPE_p_int result = Ode.new_intArray( totalIndicesLength );

        // Retrieve mesh scale to bake vertex data
        float[] scale = {mesh.getWorldScale().x, mesh.getWorldScale().y, mesh.getWorldScale().z};

        // Walk batches and fill in vertices and index data

        IntBuffer indices = mesh.getIndexBuffer();
        indices.rewind();

        FloatBuffer vertices = mesh.getVertexBuffer();
        vertices.rewind();

        int vertexOffset = 0;
        int partialVertexOffset = vertexOffset / 3;
        int verticesLength = mesh.getVertexCount() * 3;
        for ( int i = 0; i < verticesLength / 3; i++ ) {
            Ode.floatArray_setitem( odeVertices, vertexOffset, vertices.get() * scale[0] );
            Ode.floatArray_setitem( odeVertices, vertexOffset + 1, vertices.get() * scale[1] );
            Ode.floatArray_setitem( odeVertices, vertexOffset + 2, vertices.get() * scale[2] );
            vertexOffset += 3;
        }

        int indicesLength = mesh.getTriangleCount() * 3;
        int indexOffset = 0;
        for ( int i = 0; i < indicesLength / 3; i++ ) {
            Ode.intArray_setitem( result, indexOffset, indices.get() + partialVertexOffset );
            Ode.intArray_setitem( result, indexOffset + 1, indices.get() + partialVertexOffset );
            Ode.intArray_setitem( result, indexOffset + 2, indices.get() + partialVertexOffset );
            indexOffset += 3;
        }

        odeIndices = result;

        // Send to ODE
        Ode.dGeomTriMeshDataBuildSingle1( data, odeVertices.getVoidPointer(), 12,
                totalVerticesLength / 3, odeIndices.getVoidPointer(), totalIndicesLength, 12, null );
        if ( geomId != null ) {
            Ode.dGeomTriMeshSetData( geomId, data );
        }
    }

    public void setLastTransformation( Vector3f position, Quaternion rotation ) {
        SWIGTYPE_p_float floatArray = lastTransform_float16Array;
        rotation.toRotationMatrix( lastTransform_matrix );
        lastTransform_matrix.setTranslation( position );
        Ode.floatArray_setitem( floatArray, 0, lastTransform_matrix.m00 );
        Ode.floatArray_setitem( floatArray, 1, lastTransform_matrix.m01 );
        Ode.floatArray_setitem( floatArray, 2, lastTransform_matrix.m02 );
        Ode.floatArray_setitem( floatArray, 3, lastTransform_matrix.m03 );
        Ode.floatArray_setitem( floatArray, 4, lastTransform_matrix.m10 );
        Ode.floatArray_setitem( floatArray, 5, lastTransform_matrix.m11 );
        Ode.floatArray_setitem( floatArray, 6, lastTransform_matrix.m12 );
        Ode.floatArray_setitem( floatArray, 7, lastTransform_matrix.m13 );
        Ode.floatArray_setitem( floatArray, 8, lastTransform_matrix.m20 );
        Ode.floatArray_setitem( floatArray, 9, lastTransform_matrix.m21 );
        Ode.floatArray_setitem( floatArray, 10, lastTransform_matrix.m22 );
        Ode.floatArray_setitem( floatArray, 11, lastTransform_matrix.m23 );
        Ode.floatArray_setitem( floatArray, 12, lastTransform_matrix.m30 );
        Ode.floatArray_setitem( floatArray, 13, lastTransform_matrix.m31 );
        Ode.floatArray_setitem( floatArray, 14, lastTransform_matrix.m32 );
        Ode.floatArray_setitem( floatArray, 15, lastTransform_matrix.m33 );
        Ode.dGeomTriMeshSetLastTransform( geomId, floatArray );
    }

    @Override
    protected void finalize() throws Throwable {
        Ode.delete_floatArray( lastTransform_float16Array );
        super.finalize();
    }

    public void setTCEnabled( boolean enabled ) {
        Ode.dGeomTriMeshEnableTC( geomId, OdeConstants.dBoxClass, enabled ? 1 : 0 );
        Ode.dGeomTriMeshEnableTC( geomId, OdeConstants.dCylinderClass, enabled ? 1 : 0 );
        Ode.dGeomTriMeshEnableTC( geomId, OdeConstants.dCapsuleClass, enabled ? 1 : 0 );
        Ode.dGeomTriMeshEnableTC( geomId, OdeConstants.dTriMeshClass, enabled ? 1 : 0 );
        Ode.dGeomTriMeshEnableTC( geomId, OdeConstants.dSphereClass, enabled ? 1 : 0 );
        Ode.dGeomTriMeshEnableTC( geomId, OdeConstants.dPlaneClass, enabled ? 1 : 0 );
        Ode.dGeomTriMeshEnableTC( geomId, OdeConstants.dRayClass, enabled ? 1 : 0 );
    }
}
