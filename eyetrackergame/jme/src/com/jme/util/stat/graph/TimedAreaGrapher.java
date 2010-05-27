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
import com.jme.scene.TriMesh;
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
public class TimedAreaGrapher extends AbstractStatGrapher implements TableLinkable {

    public static final StatType Vertical = new StatType("_timedGrapher_vert");
    public static final StatType Horizontal = new StatType("_timedGrapher_horiz");
    
    public enum ConfigKeys {
        Antialias,
        ShowAreas,
        Width,
        Stipple,
        Color,
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

    private HashMap<StatType, AreaEntry> entries = new HashMap<StatType, AreaEntry>();

    private BlendState defBlendState = null;

    public TimedAreaGrapher(int width, int height) {
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
                // First figure out the max value.
                double max = 0;
                for (StatType type : sample.values.keySet()) {
                    if (config.containsKey(type)) {
                        max += sample.values.get(type).val;
                    }
                }
                double accum = 0;
                for (StatType type : sample.values.keySet()) {
                    if (config.containsKey(type)) {
                        AreaEntry entry = entries.get(type);
                        // Prepare our entry object as needed.
                        if (entry == null
                                || entry.maxSamples != StatCollector
                                        .getMaxSamples()) {
                            entry = new AreaEntry(StatCollector.getMaxSamples(), type);
                            entries.put(type, entry);
                        }

                        // average by max and bump by accumulated total.
                        double value = sample.values.get(type).val / sample.actualTime;
                        sample.values.get(type).average = value * 100;
    
                        Vector3f point1 = new Vector3f(i, (float)(value + accum), 0);
                        entry.verts.add(point1);
                        Vector3f point2 = new Vector3f(i, (float)(accum), 0);
                        entry.verts.add(point2);
                        entry.visited = true;
                        accum += value;
                    }
                }
            }
        }

        float scaleWidth = texWidth / (float)(StatCollector.getMaxSamples()-1);
        float scaleHeight = texHeight / 1.02f;
        for (Iterator<StatType> i = entries.keySet().iterator(); i.hasNext(); ) {
            AreaEntry entry = entries.get(i.next());
            // - Go through the entries list and remove any that were not
            // visited.
            if (!entry.visited) {
                entry.area.removeFromParent();
                i.remove();
                continue;
            }

            // - Update the params with the verts and count.
            FloatBuffer fb = BufferUtils.createFloatBuffer(entry.verts
                    .toArray(new Vector3f[] {}));
            fb.rewind();
            entry.area.setVertexBuffer(fb);
            entry.area.setLocalScale(new Vector3f(scaleWidth, scaleHeight, 1));
            entry.area.getIndexBuffer().limit(entry.verts.size());

            // - attach to root as needed
            if (!graphRoot.equals(entry.area.getParent())) {
                graphRoot.attachChild(entry.area);
                entry.area.updateRenderState();
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

        horizontals.setDefaultColor(getColorConfig(TimedAreaGrapher.Horizontal, ConfigKeys.Color.name(), ColorRGBA.blue.clone()));
        horizontals.setLineWidth(getIntConfig(TimedAreaGrapher.Horizontal, ConfigKeys.Width.name(), 1));
        horizontals.setStipplePattern(getShortConfig(TimedAreaGrapher.Horizontal, ConfigKeys.Stipple.name(), (short) 0xFF00));
        horizontals.setAntialiased(getBooleanConfig(TimedAreaGrapher.Horizontal, ConfigKeys.Antialias.name(), true));
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
        
        verticals.setDefaultColor(getColorConfig(TimedAreaGrapher.Vertical, ConfigKeys.Color.name(), ColorRGBA.red.clone()));
        verticals.setLineWidth(getIntConfig(TimedAreaGrapher.Vertical, ConfigKeys.Width.name(), 1));
        verticals.setStipplePattern(getShortConfig(TimedAreaGrapher.Vertical, ConfigKeys.Stipple.name(), (short) 0xFF00));
        verticals.setAntialiased(getBooleanConfig(TimedAreaGrapher.Vertical, ConfigKeys.Antialias.name(), true));
    }

    class AreaEntry {
        public ArrayList<Vector3f> verts = new ArrayList<Vector3f>();
        public int maxSamples;
        public boolean visited;
        public TriMesh area;

        public AreaEntry(int maxSamples, StatType type) {
            this.maxSamples = maxSamples;

            area = new TriMesh("a", BufferUtils.createVector3Buffer(maxSamples * 2),
                    null, null, null, BufferUtils.createIntBuffer(maxSamples * 2));
            for (int i = 0; i < maxSamples * 2; i++) {
                area.getIndexBuffer().put(i);
            }
            area.getIndexBuffer().rewind();
            area.setRenderQueueMode(Renderer.QUEUE_ORTHO);
            area.setMode(TriMesh.Mode.Strip);

            area.setDefaultColor(getColorConfig(type, ConfigKeys.Color.name(), ColorRGBA.lightGray.clone()));
            if (!getBooleanConfig(type, ConfigKeys.ShowAreas.name(), true)) {
                area.setCullHint(CullHint.Always);
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
        if (!getBooleanConfig(type, ConfigKeys.ShowAreas.name(), true)) {
            lineKey.setCullHint(CullHint.Always);
        }

        return lineKey;
    }

    public void reset() {
        synchronized (StatCollector.getHistorical()) {
            for (Iterator<StatType> i = entries.keySet().iterator(); i.hasNext(); ) {
                AreaEntry entry = entries.get(i.next());
                entry.area.removeFromParent();
                i.remove();
            }  
        }
    }
}
