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

package com.jme.scene;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.logging.Logger;

import com.jme.intersection.CollisionResults;
import com.jme.math.FastMath;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.geom.BufferUtils;

/**
 * <code>Line</code> subclasses geometry and defines a collection of lines.
 * For every two points, a line is created. If mode is set to CONNECTED, these
 * lines as connected as one big line. If it is set to LOOP, it is also rendered
 * connected but the last point is connected to the first point.
 * 
 * @author Mark Powell
 * @author Joshua Slack
 * @version $Id: Line.java 4751 2009-11-09 17:13:20Z blaine.dev $
 */
public class Line extends Geometry {
    private static final Logger logger = Logger.getLogger(Line.class.getName());

    private static final long serialVersionUID = 1L;

    protected transient IntBuffer indexBuffer;

    private float lineWidth = 1.0f;
    private Mode mode = Mode.Segments;
    private short stipplePattern = (short) 0xFFFF;
    private int stippleFactor = 1;
    private boolean antialiased = false;

    public enum Mode {
        /**
         * Every two vertices referenced by the indexbuffer will be considered a
         * stand-alone line segment.
         */
        Segments,
        /**
         * The first two vertices referenced by the indexbuffer create a line,
         * from there, every additional vertex is paired with the preceding
         * vertex to make a new, connected line.
         */
        Connected,
        /**
         * Identical to <i>Connected</i> except the final indexed vertex is
         * then connected back to the initial vertex to form a loop.
         */
        Loop;
    }

    public Line() {

    }

    /**
     * Constructs a new line with the given name. By default, the line has no
     * information.
     * 
     * @param name
     *            The name of the line.
     */
    public Line(String name) {
        super(name);
    }

    /**
     * Constructor instantiates a new <code>Line</code> object with a given
     * set of data. Any data can be null except for the vertex list. If vertices
     * are null an exception will be thrown.
     * 
     * @param name
     *            the name of the scene element. This is required for
     *            identification and comparision purposes.
     * @param vertex
     *            the vertices that make up the lines.
     * @param normal
     *            the normals of the lines.
     * @param color
     *            the color of each point of the lines.
     * @param coords
     *            the texture coordinates of the lines.
     */
    public Line(String name, FloatBuffer vertex, FloatBuffer normal,
            FloatBuffer color, TexCoords coords) {
        super(name, vertex, normal, color, coords);
        generateIndices();
        logger.fine("Line created.");
    }

    /**
     * Constructor instantiates a new <code>Line</code> object with a given
     * set of data. Any data can be null except for the vertex list. If vertices
     * are null an exception will be thrown.
     * 
     * @param name
     *            the name of the scene element. This is required for
     *            identification and comparision purposes.
     * @param vertex
     *            the vertices that make up the lines.
     * @param normal
     *            the normals of the lines.
     * @param color
     *            the color of each point of the lines.
     * @param texture
     *            the texture coordinates of the lines.
     */
    public Line(String name, Vector3f[] vertex, Vector3f[] normal,
            ColorRGBA[] color, Vector2f[] texture) {
        super(name, BufferUtils.createFloatBuffer(vertex), BufferUtils
                .createFloatBuffer(normal), BufferUtils
                .createFloatBuffer(color), TexCoords.makeNew(texture));
        generateIndices();
        logger.fine("Line created.");
    }

    @Override
    public void reconstruct(FloatBuffer vertices, FloatBuffer normals,
            FloatBuffer colors, TexCoords coords) {
        super.reconstruct(vertices, normals, colors, coords);
        generateIndices();
    }

    public void generateIndices() {
        if (getIndexBuffer() == null
                || getIndexBuffer().limit() != getVertexCount()) {
            setIndexBuffer(BufferUtils.createIntBuffer(getVertexCount()));
        } else
            getIndexBuffer().rewind();

        for (int x = 0; x < getVertexCount(); x++)
            getIndexBuffer().put(x);
    }

    /**
     * <code>getIndexBuffer</code> retrieves the indices array as an
     * <code>IntBuffer</code>.
     * 
     * @return the indices array as an <code>IntBuffer</code>.
     */
    public IntBuffer getIndexBuffer() {
        return indexBuffer;
    }

    /**
     * <code>setIndexBuffer</code> sets the index array for this
     * <code>Line</code>.
     * 
     * @param indices
     *            the index array as an IntBuffer.
     */
    public void setIndexBuffer(IntBuffer indices) {
        indexBuffer = indices;
    }

    /*
     * unsupported
     * 
     * @see com.jme.scene.Spatial#hasCollision(com.jme.scene.Spatial,
     *      com.jme.intersection.CollisionResults)
     */
    public void findCollisions(
            Spatial scene, CollisionResults results, int requiredOnBits) {
        // unsupported
    }

    /**
     * Always return false for lines.
     * 
     * @return always false for lines
     */
    public boolean hasCollision(
            Spatial scene, boolean checkTriangles, int requiredOnBits) {
        return false;
    }

    /**
     * Puts a circle into vertex and normal buffer at the current buffer
     * position. The buffers are enlarged and copied if they are too small.
     * 
     * @param radius
     *            radius of the circle
     * @param x
     *            x coordinate of circle center
     * @param y
     *            y coordinate of circle center
     * @param segments
     *            number of line segments the circle is built from
     * @param insideOut
     *            false for normal winding (ccw), true for clockwise winding
     */
    public void appendCircle(float radius, float x, float y, int segments,
            boolean insideOut) {
        int requiredFloats = segments * 2 * 3;
        FloatBuffer verts = BufferUtils.ensureLargeEnough(getVertexBuffer(),
                requiredFloats);
        setVertexBuffer(verts);
        FloatBuffer normals = BufferUtils.ensureLargeEnough(getNormalBuffer(),
                requiredFloats);
        setNormalBuffer(normals);
        float angle = 0;
        float step = FastMath.PI * 2 / segments;
        for (int i = 0; i < segments; i++) {
            float dx = FastMath.cos(insideOut ? -angle : angle) * radius;
            float dy = FastMath.sin(insideOut ? -angle : angle) * radius;
            if (i > 0) {
                verts.put(dx + x).put(dy + y).put(0);
                normals.put(dx).put(dy).put(0);
            }
            verts.put(dx + x).put(dy + y).put(0);
            normals.put(dx).put(dy).put(0);
            angle += step;
        }
        verts.put(radius + x).put(y).put(0);
        normals.put(radius).put(0).put(0);
        generateIndices();
    }

    /**
     * @return true if points are to be drawn antialiased
     */
    public boolean isAntialiased() {
        return antialiased;
    }

    /**
     * Sets whether the point should be antialiased. May decrease performance.
     * If you want to enabled antialiasing, you should also use an alphastate
     * with a source of SourceFunction.SourceAlpha and a destination of DB_ONE_MINUS_SRC_ALPHA
     * or DB_ONE.
     * 
     * @param antialiased
     *            true if the line should be antialiased.
     */
    public void setAntialiased(boolean antialiased) {
        this.antialiased = antialiased;
    }

    /**
     * @return either SEGMENTS, CONNECTED or LOOP. See class description.
     */
    public Mode getMode() {
        return mode;
    }

    /**
     * @param mode
     *            either SEGMENTS, CONNECTED or LOOP. See class description.
     */
    public void setMode(Mode mode) {
        this.mode = mode;
    }

    /**
     * @return the width of this line.
     */
    public float getLineWidth() {
        return lineWidth;
    }

    /**
     * Sets the width of the line when drawn. Non anti-aliased line widths are
     * rounded to the nearest whole number by opengl.
     * 
     * @param lineWidth
     *            The lineWidth to set.
     */
    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
    }

    /**
     * @return the set stipplePattern. 0xFFFF means no stipple.
     */
    public short getStipplePattern() {
        return stipplePattern;
    }

    /**
     * The stipple or pattern to use when drawing this line. 0xFFFF is a solid
     * line.
     * 
     * @param stipplePattern
     *            a 16bit short whose bits describe the pattern to use when
     *            drawing this line
     */
    public void setStipplePattern(short stipplePattern) {
        this.stipplePattern = stipplePattern;
    }

    /**
     * @return the set stippleFactor.
     */
    public int getStippleFactor() {
        return stippleFactor;
    }

    /**
     * @param stippleFactor
     *            magnification factor to apply to the stipple pattern.
     */
    public void setStippleFactor(int stippleFactor) {
        this.stippleFactor = stippleFactor;
    }

    /**
     * Used with Serialization. Do not call this directly.
     * 
     * @param s
     * @throws IOException
     * @see java.io.Serializable
     */
    private void writeObject(java.io.ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        if (getIndexBuffer() == null)
            s.writeInt(0);
        else {
            s.writeInt(getIndexBuffer().limit());
            getIndexBuffer().rewind();
            for (int x = 0, len = getIndexBuffer().limit(); x < len; x++)
                s.writeInt(getIndexBuffer().get());
        }
    }

    /**
     * Used with Serialization. Do not call this directly.
     * 
     * @param s
     * @throws IOException
     * @throws ClassNotFoundException
     * @see java.io.Serializable
     */
    private void readObject(java.io.ObjectInputStream s) throws IOException,
            ClassNotFoundException {
        s.defaultReadObject();
        int len = s.readInt();
        if (len == 0) {
            setIndexBuffer(null);
        } else {
            IntBuffer buf = BufferUtils.createIntBuffer(len);
            for (int x = 0; x < len; x++)
                buf.put(s.readInt());
            setIndexBuffer(buf);
        }
    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(lineWidth, "lineWidth", 1);
        capsule.write(mode, "mode", Mode.Segments);
        capsule.write(stipplePattern, "stipplePattern", (short) 0xFFFF);
        capsule.write(antialiased, "antialiased", false);
        capsule.write(indexBuffer, "indexBuffer", null);
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        lineWidth = capsule.readFloat("lineWidth", 1);
        mode = (Mode) capsule.readEnum("mode", Mode.class, Mode.Segments);
        stipplePattern = capsule.readShort("stipplePattern", (short) 0xFFFF);
        antialiased = capsule.readBoolean("antialiased", false);
        indexBuffer = capsule.readIntBuffer("indexBuffer", null);
    }

    public void draw(Renderer r) {
        if (!r.isProcessingQueue()) {
            if (r.checkAndAdd(this))
                return;
        }

        r.draw(this);
    }

}

