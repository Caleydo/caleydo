package com.jme.util.geom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.SharedMesh;
import com.jme.scene.TexCoords;
import com.jme.scene.TriMesh;

/**
 * Note: Does not work with geometry using texcoords other than 2d coords.
 * 
 * @author Joshua Slack
 */

public class GeometryTool {
    private static final Logger logger = Logger.getLogger(GeometryTool.class
            .getName());

    public static final int MV_SAME_NORMALS = 1;
    public static final int MV_SAME_TEXS = 2;
    public static final int MV_SAME_COLORS = 4;

    @SuppressWarnings("unchecked")
    public static VertMap minimizeVerts(TriMesh mesh, int options) {
        if (mesh instanceof SharedMesh)
            mesh = ((SharedMesh) mesh).getTarget();

        int vertCount = -1;
        int oldCount = mesh.getVertexCount();
        int newCount = 0;

        VertMap result = new VertMap(mesh);

        while (vertCount != newCount) {
            vertCount = mesh.getVertexCount();
            // go through each vert...
            Vector3f[] verts = BufferUtils.getVector3Array(mesh
                    .getVertexBuffer());
            Vector3f[] norms = null;
            if (mesh.getNormalBuffer() != null)
                norms = BufferUtils.getVector3Array(mesh.getNormalBuffer());

            ColorRGBA[] colors = null;
            if (mesh.getColorBuffer() != null)
                colors = BufferUtils.getColorArray(mesh.getColorBuffer());

            Vector2f[][] tex = new Vector2f[mesh.getNumberOfUnits()][];
            for (int x = 0; x < tex.length; x++) {
                if (mesh.getTextureCoords(x) != null) {
                    tex[x] = BufferUtils.getVector2Array(mesh
                            .getTextureCoords(x).coords);
                }
            }

            int[] inds = BufferUtils.getIntArray(mesh.getIndexBuffer());

            HashMap<VertKey, Integer> store = new HashMap<VertKey, Integer>();
            int good = 0;
            for (int x = 0, max = verts.length; x < max; x++) {
                VertKey vkey = new VertKey(verts[x], norms != null ? norms[x]
                        : null, colors != null ? colors[x] : null, getTexs(tex,
                        x), options);
                // if we've already seen it, mark it for deletion and repoint
                // the corresponding index
                if (store.containsKey(vkey)) {
                    int newInd = store.get(vkey);
                    result.replaceIndex(x, newInd);
                    findReplace(x, newInd, inds);
                    verts[x] = null;
                    if (norms != null)
                        norms[newInd].addLocal(norms[x].normalizeLocal());
                    if (colors != null)
                        colors[x] = null;
                } else {
                    store.put(vkey, x);
                    good++;
                }
            }

            ArrayList<Vector3f> newVects = new ArrayList<Vector3f>(good);
            ArrayList<Vector3f> newNorms = new ArrayList<Vector3f>(good);
            ArrayList<ColorRGBA> newColors = new ArrayList<ColorRGBA>(good);
            ArrayList[] newTexs = new ArrayList[mesh.getNumberOfUnits()];
            for (int x = 0; x < newTexs.length; x++) {
                if (mesh.getTextureCoords(x) != null) {
                    newTexs[x] = new ArrayList<Vector2f>(good);
                }
            }

            // go through each vert
            // add non-duped verts, texs, normals to new buffers
            // and set into mesh.
            int off = 0;
            for (int x = 0, max = verts.length; x < max; x++) {
                if (verts[x] == null) {
                    // shift indices above this down a notch.
                    decrementIndices(x - off, inds);
                    result.decrementIndices(x - off);
                    off++;
                } else {
                    newVects.add(verts[x]);
                    if (norms != null)
                        newNorms.add(norms[x].normalizeLocal());
                    if (colors != null)
                        newColors.add(colors[x]);
                    for (int y = 0; y < newTexs.length; y++) {
                        if (mesh.getTextureCoords(y) != null)
                            newTexs[y].add(tex[y][x]);
                    }
                }
            }

            mesh.setVertexBuffer(BufferUtils.createFloatBuffer(newVects
                    .toArray(new Vector3f[0])));
            if (norms != null)
                mesh.setNormalBuffer(BufferUtils.createFloatBuffer(newNorms
                        .toArray(new Vector3f[0])));
            if (colors != null)
                mesh.setColorBuffer(BufferUtils.createFloatBuffer(newColors
                        .toArray(new ColorRGBA[0])));

            for (int x = 0; x < newTexs.length; x++) {
                if (mesh.getTextureCoords(x) != null) {
                    mesh.setTextureCoords(TexCoords
                            .makeNew((Vector2f[]) newTexs[x]
                                    .toArray(new Vector2f[0])), x);
                }
            }

            mesh.getIndexBuffer().clear();
            mesh.getIndexBuffer().put(inds);
            newCount = mesh.getVertexCount();
        }
        logger
                .info("mesh: " + mesh + " old: " + oldCount + " new: "
                        + newCount);

        return result;
    }

    private static Vector2f[] getTexs(Vector2f[][] tex, int i) {
        Vector2f[] res = new Vector2f[tex.length];
        for (int x = 0; x < tex.length; x++) {
            if (tex[x] != null) {
                res[x] = tex[x][i];
            }
        }
        return res;
    }

    private static void findReplace(int oldI, int newI, int[] indices) {
        for (int x = indices.length; --x >= 0;)
            if (indices[x] == oldI)
                indices[x] = newI;
    }

    private static void decrementIndices(int above, int[] inds) {
        for (int x = inds.length; --x >= 0;)
            if (inds[x] >= above)
                inds[x]--;
    }

}
