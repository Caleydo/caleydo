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

import com.jme.scene.TriMesh;

/**
 * @author Pirx
 */
public class FontMesh extends TriMesh {
    private static final long serialVersionUID = 1L;
    /*
     * private static final long serialVersionUID = 1L; private String text;
     * private Font font; private float extrusion; private double flatness;
     * private boolean drawFront; private boolean drawBack; private boolean
     * drawSide; /// Creates a new instance of FontMesh public FontMesh(String
     * name, Font font, String text, float extrusion, double flatness, boolean
     * drawFront, boolean drawSide, boolean drawBack) { super(name); this.text =
     * text; this.font = font; this.extrusion = extrusion; this.flatness =
     * flatness; this.drawFront = drawFront; this.drawBack = drawBack;
     * this.drawSide = drawSide; allocateVertices(); } public String getText() {
     * return text; } public Font getFont() { return font; } public float
     * getExtrusion() { return extrusion; } public double getFlatness() { return
     * flatness; } public boolean hasFront() { return drawFront; } public
     * boolean hasSide() { return drawSide; } public boolean hasBack() { return
     * drawBack; } private void allocateVertices() { GlyphVector gv =
     * font.createGlyphVector(new FontRenderContext(null,true, true), text);
     * gv.performDefaultLayout(); List<Vector3f> vertexList = new ArrayList<Vector3f>();
     * List<Integer> indexList = new ArrayList<Integer>(); List<Vector3f>
     * normalList = new ArrayList<Vector3f>(); for (int g = 0; g <
     * gv.getNumGlyphs(); g++) { FontPolygon fontPolygon = null; Glyph fontGlyph =
     * new Glyph(); Shape s = gv.getGlyphOutline(g); PathIterator pi = new
     * FlatteningPathIterator(s.getPathIterator(null), flatness); float[] coords =
     * new float[6]; while (!pi.isDone()) { int seg = pi.currentSegment(coords);
     * //logger.info(seg); switch (seg) { case PathIterator.SEG_MOVETO:
     * fontPolygon = new FontPolygon(); fontPolygon.addPoint(new
     * Vector3f(coords[0], -coords[1], 0)); break; case PathIterator.SEG_LINETO:
     * fontPolygon.addPoint(new Vector3f(coords[0], -coords[1], 0)); break; case
     * PathIterator.SEG_CLOSE: fontPolygon.close();
     * fontGlyph.addPolygon(fontPolygon); fontPolygon = null; break; default:
     * throw new IllegalArgumentException("unknown segment type " + seg); }
     * pi.next(); } if (drawSide) { //fontGlyph.calculateSides(vertexList,
     * indexList, normalList, extrusion); } if (drawFront || drawBack) {
     * //fontGlyph.triangulate(vertexList, indexList, normalList); } }
     * Vector3f[] vertexes = vertexList.toArray(new Vector3f[] {});
     * //vertQuantity = vertexes.length; FloatBuffer vertBuf =
     * BufferUtils.createFloatBuffer(vertexes); Vector3f[] normals =
     * normalList.toArray(new Vector3f[] {}); FloatBuffer normBuf =
     * BufferUtils.createFloatBuffer(normals); int[] indexes = new
     * int[indexList.size()]; for (int i = 0; i < indexes.length; i++) {
     * indexes[i] = indexList.get(i).intValue(); } //triangleQuantity =
     * indexes.length / 3; IntBuffer indexBuffer =
     * BufferUtils.createIntBuffer(indexes); // New way of creating the TriMesh
     * logger.info("VertBuf:" + vertBuf.capacity());
     * logger.info("normBuf:" + normBuf.capacity());
     * logger.info("indexBuffer:" + indexBuffer.capacity());
     * reconstruct(vertBuf, normBuf, null, null);
     * getBatch(0).setIndexBuffer(indexBuffer);
     * getBatch(0).setTriangleQuantity(indexBuffer.capacity() / 3);
     * logger.info( "TriMesh created.");
     * setDefaultColor(ColorRGBA.gray); } private void mergeHoles(List<FontPolygon>
     * polygons) { //detect holes; boolean[] isHole = new
     * boolean[polygons.size()]; for (int i = 0; i < polygons.size(); i++) {
     * FontPolygon fp = polygons.get(i); isHole[i] = fp.isHole(); } for (int
     * holeID = 0; holeID < polygons.size(); holeID++) { if (!isHole[holeID]) {
     * continue; } FontPolygon hole = polygons.get(holeID); FontPolygon.Distance
     * minDist = new FontPolygon.Distance(-1, -1, Float.MAX_VALUE); FontPolygon
     * minOutline = null; for (int outlineID = 0; outlineID < polygons.size();
     * outlineID++) { if (isHole[outlineID]) { logger.info("Cont:" +
     * outlineID); continue; } FontPolygon outline = polygons.get(outlineID);
     * FontPolygon.Distance dist = outline.getMinDistance(hole);
     * logger.info("dist.sqrDist:" + dist.sqrDist + " < minDist.sqrDist:" +
     * minDist.sqrDist); if (dist.sqrDist < minDist.sqrDist) { minDist = dist;
     * minOutline = outline; } } if (minOutline != null)
     * minOutline.mergeHole(hole, minDist); else logger.warning("Damn:
     * minOutline == null"); } //remove holes from list for (int i =
     * polygons.size() - 1; i >= 0; i--) { if (isHole[i]) { polygons.remove(i); } } }
     */

}
