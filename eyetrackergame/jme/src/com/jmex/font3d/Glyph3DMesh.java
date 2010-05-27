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
package com.jmex.font3d;

import java.nio.IntBuffer;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.TexCoords;
import com.jme.scene.TriMesh;
import com.jme.util.geom.BufferUtils;
import com.jmex.font3d.math.PlanarEdge;
import com.jmex.font3d.math.PlanarVertex;
import com.jmex.font3d.math.TriangulationVertex;

/**
 * This class holds one glyph as a trimesh. The data is stored in this order:
 * sides,front and back. Hence we get the following:
 * 
 * <h4>Vertices/Normals/Texcoords/VertexColors buffers</h4>
 * <ul>
 * 	<li><i>n</i>*2 vertices used for the sides
 * 	<li><i>n</i> vertices used for the front
 * 	<li><i>n</i> vertices used for the back
 * </ul>
 * <h4>Indice buffer</h4>
 * <ul>
 * 	<li><i>n</i>*6 indices for the sides  (<i>n</i>*2 triangles)
 * 	<li>(<i>n</i>-2)*3 indices for the front (<i>n</i>-2 triangles)
 * 	<li>(<i>n</i>-2)*3 indices for the back (<i>n</i>-2 triangles)
 * </ul> 
 * 
 * TODO: We should have a threshold on the angle of the outline, that way we 
 * could make smooth only where it should actually be smooth.
 * 
 * @author emanuel
 */
public class Glyph3DMesh extends TriMesh {
    private static final long serialVersionUID = -2744055578491222293L;

    public Glyph3DMesh(Glyph3D glyph3D, boolean drawSides, boolean drawFront,
            boolean drawBack) {
        // Calculate how many vertices we need
        int vertex_count = 0;
        int triangle_count = 0;
        if (drawSides) {
            vertex_count += glyph3D.getOutline().size() * 2;
            triangle_count += glyph3D.getOutline().size() * 2;
        }
        if (drawFront) {
            vertex_count += glyph3D.getVertices().size();
            triangle_count += glyph3D.getSurface().capacity() / 3;
        }
        if (drawBack) {
            vertex_count += glyph3D.getVertices().size();
            triangle_count += glyph3D.getSurface().capacity() / 3;
        }

        // Allocate what we need
        Vector3f verts[] = new Vector3f[vertex_count];
        Vector3f norms[] = new Vector3f[vertex_count];
        Vector2f texcoords[] = new Vector2f[vertex_count];
        IntBuffer triangles = BufferUtils.createIntBuffer(triangle_count * 3);
        // triangles.rewind();
        int vcount = 0; // Used to pad indexes.
        // Add all the vertices (either one or two layers)
        int numverts = glyph3D.getVertices().size();
        if (drawSides) {
            for (TriangulationVertex v : glyph3D.getVertices()) {
                norms[v.getIndex()] = glyph3D.getOutlineNormals()[v.getIndex()];
                verts[v.getIndex()] = new Vector3f(v.getPoint());
                verts[v.getIndex()].z += 0.5f;
                norms[v.getIndex() + numverts] = glyph3D.getOutlineNormals()[v
                        .getIndex()];
                verts[v.getIndex() + numverts] = new Vector3f(v.getPoint());
                verts[v.getIndex() + numverts].z -= 0.5f;
            }
            vcount += numverts * 2;

            // Add indices
            for (PlanarEdge e : glyph3D.getOutline()) {
                if (!e.isRealEdge())
                    continue;
                PlanarVertex src = e.getOrigin();
                PlanarVertex dst = e.getDestination();
                int v1 = src.getIndex();
                int v2 = dst.getIndex();
                int v3 = dst.getIndex() + numverts;
                int v4 = src.getIndex() + numverts;
                triangles.put(new int[] { v1, v3, v2, v3, v1, v4 });
            }
        }
        if (drawFront) {
            Vector3f backnormal = new Vector3f(0, 0, 1);
            for (TriangulationVertex v : glyph3D.getVertices()) {
                norms[vcount + v.getIndex()] = backnormal;
                verts[vcount + v.getIndex()] = new Vector3f(v.getPoint());
                verts[vcount + v.getIndex()].z += 0.5f;
            }
            glyph3D.getSurface().rewind();
            while (glyph3D.getSurface().remaining() > 0) {
                int tri[] = { glyph3D.getSurface().get() + vcount,
                        glyph3D.getSurface().get() + vcount,
                        glyph3D.getSurface().get() + vcount };
                triangles.put(tri[2]);
                triangles.put(tri[1]);
                triangles.put(tri[0]);
            }
            vcount += numverts;
        }
        if (drawBack) {
            Vector3f frontnormal = new Vector3f(0, 0, -1);
            for (TriangulationVertex v : glyph3D.getVertices()) {
                norms[vcount + v.getIndex()] = frontnormal;
                verts[vcount + v.getIndex()] = new Vector3f(v.getPoint());
                verts[vcount + v.getIndex()].z -= 0.5f;
            }
            // We need to add the offset (vertcount)
            glyph3D.getSurface().rewind();
            while (glyph3D.getSurface().remaining() > 0) {
                triangles.put(glyph3D.getSurface().get() + vcount);
            }
            vcount += numverts;
        }
        
        // Set the texture coords to the vertex coords (in X/Y plane)
        for(int i = 0;i < verts.length; i++)
        {
            texcoords[i] = new Vector2f(verts[i].x, verts[i].y);
        }

        // Now lets give it a spin....
        setVertexBuffer(BufferUtils.createFloatBuffer(verts));
        setNormalBuffer(BufferUtils.createFloatBuffer(norms));
        setTextureCoords(new TexCoords(BufferUtils.createFloatBuffer(texcoords)), 0);
        setIndexBuffer(triangles);
        // logger.info("triangles:"+triangles);
    }
}
