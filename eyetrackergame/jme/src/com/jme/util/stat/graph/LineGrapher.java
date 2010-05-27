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

package com.jme.util.stat.graph;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Line;
import com.jme.scene.Node;
import com.jme.scene.Point;
import com.jme.scene.Spatial.CullHint;
import com.jme.scene.state.BlendState;
import com.jme.system.DisplaySystem;
import com.jme.util.Debug;
import com.jme.util.geom.BufferUtils;
import com.jme.util.stat.MultiStatSample;
import com.jme.util.stat.StatCollector;
import com.jme.util.stat.StatType;

/**
 * @author Joshua Slack
 */
public class LineGrapher extends AbstractStatGrapher implements TableLinkable {

    public static final StatType Vertical = new StatType("_lineGrapher_vert");
    public static final StatType Horizontal = new StatType("_lineGrapher_horiz");
    
    public enum ConfigKeys {
        ShowPoints,
        PointSize,
        PointColor,
        Antialias,
        ShowLines,
        Width,
        Stipple,
        Color,
        FrameAverage,
    }

    protected Node graphRoot = new Node("root");
    protected Line horizontals, verticals;
    protected int eventCount = 0;
    protected int threshold = 1;
    protected float startMarker = 0;
    private float off;
    private float vSpan;
    private int majorHBar = 20;
    private int majorVBar = 10;

    private HashMap<StatType, LineEntry> entries = new HashMap<StatType, LineEntry>();

    private BlendState defBlendState = null;

    public LineGrapher(int width, int height) {
        super(width, height);

        // Setup our static horizontal graph lines
        createHLines();

        defBlendState = DisplaySystem.getDisplaySystem().getRenderer().createBlendState();
        defBlendState.setEnabled(true);
        defBlendState.setBlendEnabled(true);
        defBlendState.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
        defBlendState
                .setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
        graphRoot.setRenderState(defBlendState);
    }

    public void statsUpdated() {
        if (!isEnabled() || !Debug.updateGraphs)
            return;

        // Turn off stat collection while we draw this graph.
        StatCollector.pause();

        // some basic stats:
        int texWidth = gWidth;
        int texHeight = gHeight;

        // On stat event:
        // - check if enough events have been triggered to cause an update.
        eventCount++;
        off += StatCollector.getStartOffset();
        if (eventCount < threshold) {
            return;
        } else {
            eventCount = 0;
        }

        // - (Re)attach horizontal bars.
        if (!graphRoot.equals(horizontals.getParent())) {
            graphRoot.attachChild(horizontals);
            horizontals.updateRenderState();
        }

        // - Check if we have valid vertical bars:
        float newVSpan = calcVSpan();
        if (verticals == null || newVSpan != vSpan) {
            vSpan = newVSpan;
            createVLines();
        }
        off %= (StatCollector.getSampleRate() * majorVBar);

        // - (Re)attach vertical bars.
        if (!graphRoot.equals(verticals.getParent())) {
            graphRoot.attachChild(verticals);
            verticals.updateRenderState();
        }

        // - shift verticals based on current time
        shiftVerticals();

        for (StatType type : entries.keySet()) {
            entries.get(type).visited = false;
            entries.get(type).verts.clear();
        }

        // - For each sample, add points and extend the lines of the
        // corresponding Line objects.
        synchronized (StatCollector.getHistorical()) {
            for (int i = 0; i < StatCollector.getHistorical().size(); i++) {
                MultiStatSample sample = StatCollector.getHistorical().get(i);
                for (StatType type : config.keySet()) {
                    if (sample.values.containsKey(type)) {
                        LineEntry entry = entries.get(type);
                        // Prepare our entry object as needed.
                        if (entry == null
                                || entry.maxSamples != StatCollector
                                        .getMaxSamples()) {
                            entry = new LineEntry(StatCollector.getMaxSamples(), type);
                            entries.put(type, entry);
                        }
    
                        double value = getBooleanConfig(type, ConfigKeys.FrameAverage.name(), false) ? sample.values.get(type).average : sample.values.get(type).val;
                        float val = (float)value;
    
                        Vector3f point = new Vector3f(i, val, 0);
                        // Now, add
                        entry.verts.add(point);
    
                        // Update min/max
                        if (entry.max < val) {
                            entry.max = val;
                        }
    
                        entry.visited = true;
                    } else {
                        LineEntry entry = entries.get(type);
                        if (entry != null) {
                        	entry.verts.add(new Vector3f(i, 0, 0));
                        }
                    }
                }
            }
        }

        for (Iterator<StatType> i = entries.keySet().iterator(); i.hasNext(); ) {
            LineEntry entry = entries.get(i.next());
            // - Go through the entries list and remove any that were not
            // visited.
            if (!entry.visited) {
                entry.line.removeFromParent();
                entry.point.removeFromParent();
                i.remove();
                continue;
            }

            // - Update the Point and Line params with the verts and count.
            FloatBuffer fb = BufferUtils.createFloatBuffer(entry.verts
                    .toArray(new Vector3f[] {}));
            fb.rewind();
            entry.point.setVertexBuffer(fb);
            float scaleWidth = texWidth / (float)(StatCollector.getMaxSamples()-1);
            float scaleHeight = texHeight / (entry.max * 1.02f);
            entry.point.setLocalScale(new Vector3f(scaleWidth, scaleHeight, 1));
            entry.line.setVertexBuffer(fb);
            entry.line.setLocalScale(new Vector3f(scaleWidth, scaleHeight, 1));
            fb.rewind();

            // - attach point/line to root as needed
            if (!graphRoot.equals(entry.line.getParent())) {
                graphRoot.attachChild(entry.line);
                entry.line.updateRenderState();
            }
            if (!graphRoot.equals(entry.point.getParent())) {
                graphRoot.attachChild(entry.point);
                entry.point.updateRenderState();
            }
        }

        // - Now, draw to texture via a TextureRenderer
        texRenderer.render(graphRoot, tex);

        // Turn stat collection back on.
        StatCollector.resume();
    }

    private float calcVSpan() {
        return texRenderer.getWidth() * majorVBar
                / StatCollector.getMaxSamples();
    }

    private void shiftVerticals() {
        int texWidth = texRenderer.getWidth();
        double xOffset = -(off * texWidth)
                / (StatCollector.getMaxSamples() * StatCollector
                        .getSampleRate());
        verticals.getWorldTranslation().x = (float)xOffset;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    // - Setup horizontal bars
    private void createHLines() {
        // some basic stats:
        int texWidth = texRenderer.getWidth();
        int texHeight = texRenderer.getHeight();

        FloatBuffer verts = BufferUtils
                .createVector3Buffer((int) (100 / majorHBar) * 2);

        float div = texHeight * majorHBar / 100f;
        
        for (int y = 0, i = 0; i < verts.capacity(); i+=6, y += div) {
            verts.put(0).put(y).put(0);
            verts.put(texWidth).put(y).put(0);
        }

        horizontals = new Line("horiz", verts, null, null, null);
        horizontals.setMode(Line.Mode.Segments);
        horizontals.setRenderQueueMode(Renderer.QUEUE_ORTHO);

        horizontals.setDefaultColor(getColorConfig(LineGrapher.Horizontal, ConfigKeys.Color.name(), ColorRGBA.blue.clone()));
        horizontals.setLineWidth(getIntConfig(LineGrapher.Horizontal, ConfigKeys.Width.name(), 1));
        horizontals.setStipplePattern(getShortConfig(LineGrapher.Horizontal, ConfigKeys.Stipple.name(), (short) 0xFF00));
        horizontals.setAntialiased(getBooleanConfig(LineGrapher.Horizontal, ConfigKeys.Antialias.name(), true));
    }

    // - Setup enough vertical bars to have one at every (10 X samplerate)
    // secs... we'll need +1 bar.
    private void createVLines() {
        // some basic stats:
        int texWidth = texRenderer.getWidth();
        int texHeight = texRenderer.getHeight();

        FloatBuffer verts = BufferUtils
                .createVector3Buffer(((int) (texWidth / vSpan) + 1) * 2);

        for (float x = vSpan; x <= texWidth + vSpan; x += vSpan) {
            verts.put(x).put(0).put(0);
            verts.put(x).put(texHeight).put(0);
        }

        verticals = new Line("vert", verts, null, null, null);
        verticals.setMode(Line.Mode.Segments);
        verticals.setRenderQueueMode(Renderer.QUEUE_ORTHO);
        
        verticals.setDefaultColor(getColorConfig(LineGrapher.Vertical, ConfigKeys.Color.name(), ColorRGBA.red.clone()));
        verticals.setLineWidth(getIntConfig(LineGrapher.Vertical, ConfigKeys.Width.name(), 1));
        verticals.setStipplePattern(getShortConfig(LineGrapher.Vertical, ConfigKeys.Stipple.name(), (short) 0xFF00));
        verticals.setAntialiased(getBooleanConfig(LineGrapher.Vertical, ConfigKeys.Antialias.name(), true));
    }

    class LineEntry {
        public ArrayList<Vector3f> verts = new ArrayList<Vector3f>();
        public int maxSamples;
        public float min = 0;
        public float max = 10;
        public boolean visited;
        public Point point;
        public Line line;

        public LineEntry(int maxSamples, StatType type) {
            this.maxSamples = maxSamples;

            point = new Point("p", BufferUtils.createVector3Buffer(maxSamples),
                    null, null, null);
            point.setRenderQueueMode(Renderer.QUEUE_ORTHO);

            point.setDefaultColor(getColorConfig(type, ConfigKeys.PointColor.name(), ColorRGBA.white.clone()));
            point.setPointSize(getIntConfig(type, ConfigKeys.PointSize.name(), 5));
            point.setAntialiased(getBooleanConfig(type, ConfigKeys.Antialias.name(), true));
            if (!getBooleanConfig(type, ConfigKeys.ShowPoints.name(), false)) {
                point.setCullHint(CullHint.Always);
            }

            line = new Line("l", BufferUtils.createVector3Buffer(maxSamples),
                    null, null, null);
            line.setRenderQueueMode(Renderer.QUEUE_ORTHO);
            line.setMode(Line.Mode.Connected);

            line.setDefaultColor(getColorConfig(type, ConfigKeys.Color.name(), ColorRGBA.lightGray.clone()));
            line.setLineWidth(getIntConfig(type, ConfigKeys.Width.name(), 3));
            line.setStipplePattern(getShortConfig(type, ConfigKeys.Stipple.name(), (short) 0xFFFF));
            line.setAntialiased(getBooleanConfig(type, ConfigKeys.Antialias.name(), true));
            if (!getBooleanConfig(type, ConfigKeys.ShowLines.name(), true)) {
                line.setCullHint(CullHint.Always);
            }
        }
    }

    public Line updateLineKey(StatType type, Line lineKey) {
        if (lineKey == null) {
            lineKey = new Line("lk", BufferUtils.createVector3Buffer(2), null,
                    null, null);
            FloatBuffer fb = BufferUtils.createFloatBuffer(new Vector3f[] {new Vector3f(0,0,0), new Vector3f(30, 0, 0)});
            fb.rewind();
            lineKey.setVertexBuffer(fb);
        }

        lineKey.setRenderQueueMode(Renderer.QUEUE_ORTHO);
        lineKey.setMode(Line.Mode.Connected);

        lineKey.setDefaultColor(getColorConfig(type, ConfigKeys.Color.name(), ColorRGBA.lightGray.clone()));
        lineKey.setLineWidth(getIntConfig(type, ConfigKeys.Width.name(), 3));
        lineKey.setStipplePattern(getShortConfig(type, ConfigKeys.Stipple.name(), (short) 0xFFFF));
        lineKey.setAntialiased(getBooleanConfig(type, ConfigKeys.Antialias.name(), true));
        if (!getBooleanConfig(type, ConfigKeys.ShowLines.name(), true)) {
            lineKey.setCullHint(CullHint.Always);
        }

        return lineKey;
    }

    public void reset() {
        synchronized (StatCollector.getHistorical()) {
            for (Iterator<StatType> i = entries.keySet().iterator(); i.hasNext(); ) {
                LineEntry entry = entries.get(i.next());
                entry.line.removeFromParent();
                entry.point.removeFromParent();
                i.remove();
            }  
        }
    }
}
