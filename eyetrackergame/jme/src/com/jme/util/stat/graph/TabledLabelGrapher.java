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

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Line;
import com.jme.scene.Node;
import com.jme.scene.Text;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.BlendState;
import com.jme.system.DisplaySystem;
import com.jme.util.Debug;
import com.jme.util.stat.MultiStatSample;
import com.jme.util.stat.StatCollector;
import com.jme.util.stat.StatType;
import com.jme.util.stat.StatValue;

/**
 * @author Joshua Slack
 */
public class TabledLabelGrapher extends AbstractStatGrapher {

    public enum ConfigKeys {
        TextColor,
        Name,
        FrameAverage,
        Decimals,
        FontScale,
        ValueScale,
        Abbreviate,
    }

    public static int DEFAULT_DECIMALS = 2;

    protected Node graphRoot = new Node("root");
    protected int eventCount = 0;
    protected int threshold = 1;
    protected int columns = 1;

    protected Quad bgQuad = new Quad("bgQuad", 1, 1);

    protected BlendState defBlendState = null;

    private HashMap<StatType, LabelEntry> entries = new HashMap<StatType, LabelEntry>();

    private boolean minimalBackground;

    private AbstractStatGrapher linkedGraph;

    public TabledLabelGrapher(int width, int height) {
        super(width, height);

        defBlendState = DisplaySystem.getDisplaySystem().getRenderer()
                .createBlendState();
        defBlendState.setEnabled(true);
        defBlendState.setBlendEnabled(true);
        defBlendState.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
        defBlendState
                .setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
        graphRoot.setRenderState(defBlendState);

        bgQuad.setRenderQueueMode(Renderer.QUEUE_ORTHO);
        bgQuad.setDefaultColor(ColorRGBA.black.clone());
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
        if (eventCount < threshold) {
            return;
        } else {
            eventCount = 0;
        }

        int col = 0;
        float lastY = texHeight-3, maxY = 0;
        float colSize = texWidth/(float)getColumns();

        // clear visitations
        for (StatType type : entries.keySet()) {
            entries.get(type).visited = false;
        }
        
        // - We only care about the most recent stats
        synchronized (StatCollector.getHistorical()) {
            MultiStatSample sample = StatCollector.getHistorical().get(
                    StatCollector.getHistorical().size() - 1);
            // - go through things we are configured for
            for (StatType type : config.keySet()) {                
                StatValue val = sample.values.get(type);
                if (val == null) { 
                    if (!StatCollector.hasHistoricalStat(type)) continue;
                    else val = new StatValue(0,1);
                }
                
                LabelEntry entry = entries.get(type);
                // Prepare our entry object as needed.
                if (entry == null) {
                    entry = new LabelEntry(type);
                    entries.put(type, entry);
                    graphRoot.attachChild(entry.text);
                }
                entry.visited = true;
                
                // Update text value
                double value = getBooleanConfig(type, ConfigKeys.FrameAverage.name(), false) ? val.average : val.val;
                entry.text.print(getStringConfig(type, ConfigKeys.Name.name(), type.getStatName()) + " " + stripVal(value, type));
                
                // Set font scale
                float scale = getFloatConfig(type, ConfigKeys.FontScale.name(), .80f);
                entry.text.setLocalScale(scale);
                
                // See if we have a defained color for this type, otherwise use
                // the corresponding color from a linked line grapher, or if
                // none, use white.
                entry.text.setTextColor(getColorConfig(type, ConfigKeys.TextColor
                        .name(), linkedGraph != null ? linkedGraph
                        .getColorConfig(type, LineGrapher.ConfigKeys.Color
                                .name(), ColorRGBA.white.clone())
                        : ColorRGBA.white.clone()));
                

                // Update text placement.
                float labelHeight = entry.text.getHeight();
                if (maxY < labelHeight) {
                    maxY = labelHeight;
                }
                entry.text.setLocalTranslation(colSize * col, lastY-labelHeight, 0);
                entry.text.updateRenderState();
                
                // Update line key as needed
                if (linkedGraph != null && linkedGraph.hasConfig(type) && linkedGraph instanceof TableLinkable) {
                    // add line keys
                    entry.lineKey = ((TableLinkable)linkedGraph).updateLineKey(type, entry.lineKey);
                    if (entry.lineKey.getParent() != graphRoot) {
                        graphRoot.attachChild(entry.lineKey);
                    }
                    entry.lineKey.updateRenderState();
                    Vector3f tLoc = entry.text.getLocalTranslation();
                    entry.lineKey.setLocalTranslation(tLoc.x+entry.text.getWidth()+15, tLoc.y+(.5f*entry.text.getHeight()), 0);
                }

                // update column / row variables
                col++;
                col %= getColumns();
                if (col == 0) {
                    lastY -= maxY;
                    maxY = 0;
                }
            }


            for (Iterator<StatType> i = entries.keySet().iterator(); i.hasNext(); ) {
                LabelEntry entry = entries.get(i.next());
                // - Go through the entries list and remove any that were not
                // visited.
                if (!entry.visited) {
                    entry.text.removeFromParent();
                    entry.lineKey.removeFromParent();
                    i.remove();
                }
            }
        }

        if (minimalBackground) {
            texRenderer.getBackgroundColor().a = 0;
            
            lastY-=3;
            if (col != 0) {
                lastY -= maxY;
            }
            bgQuad.resize(texWidth, texHeight-lastY);
            bgQuad.setRenderState(defBlendState);
            bgQuad.updateRenderState();
            bgQuad.setLocalTranslation(texWidth/2f, texHeight - (texHeight-lastY)/2f, 0);
            
            // - Draw our bg quad
            texRenderer.render(bgQuad, tex);
    
            // - Now, draw to texture via a TextureRenderer
            texRenderer.render(graphRoot, tex, false);
        } else {
            texRenderer.getBackgroundColor().a = 1;
            
            // - Now, draw to texture via a TextureRenderer
            texRenderer.render(graphRoot, tex);
        }

        // Turn stat collection back on.
        StatCollector.resume();
    }

    private String stripVal(double val, StatType type) {
        // scale as needed
        val = val * getDoubleConfig(type, ConfigKeys.ValueScale.name(), 1.0);
        
        String post = "";
        // Break it down if needed.
        if (getBooleanConfig(type, ConfigKeys.Abbreviate.name(), true)) {
            if ( val >= 1000000 ) {
                val /= 1000000;
                post = "m";
            } else if (val >= 1000) {
                val /= 1000;
                post = "k";
            }
        }
        
        int decimals = getIntConfig(type, ConfigKeys.Decimals.name(), DEFAULT_DECIMALS);
        if (!"".equals(post) && decimals == 0) {
            decimals = 1; // use 1 spot anyway.
        }

        StringBuilder format = new StringBuilder(decimals > 0 ? "0.0" : "0");
        for (int x = 1; x < decimals; x++) {
            format.append("0");
        }
        
        return new DecimalFormat(format.toString()).format(val) + post;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        if (columns < 1) {
            throw new IllegalArgumentException("columns must be >= 1 ("+columns+")");
        }
        this.columns = columns;
    }

    public boolean isMinimalBackground() {
        return minimalBackground;
    }

    public void setMinimalBackground(boolean minimalBackground) {
        this.minimalBackground = minimalBackground;
    }

    public void linkTo(AbstractStatGrapher grapher) {
        this.linkedGraph = grapher;
    }

    class LabelEntry {
        Text text;
        Line lineKey;
        boolean visited;
        StatType type;
        
        public LabelEntry(StatType type) {
            text = Text.createDefaultTextLabel("label", getStringConfig(type, ConfigKeys.Name.name(), type.getStatName()));
        }
    }

    public void reset() {
        synchronized (StatCollector.getHistorical()) {
            for (Iterator<StatType> i = entries.keySet().iterator(); i.hasNext(); ) {
                LabelEntry entry = entries.get(i.next());
                entry.text.removeFromParent();
                entry.lineKey.removeFromParent();
                i.remove();
            }
        }
    }
}
