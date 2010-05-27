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
package com.jmex.game.state;

import java.util.logging.Logger;

import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Quad;
import com.jme.system.DisplaySystem;
import com.jme.util.Debug;
import com.jme.util.stat.StatCollector;
import com.jme.util.stat.StatType;
import com.jme.util.stat.graph.GraphFactory;
import com.jme.util.stat.graph.LineGrapher;
import com.jme.util.stat.graph.TabledLabelGrapher;


/**
 * A GameState which displays some useful statistics like 
 * FPS, avg. Objects/Tris etc.<br>
 * Note: statistic gathering needs to be activated:
 * System.setProperty("jme.stats", "set");
 * @author Christoph Luder
 */
public class StatisticsGameState extends BasicGameState {
    private Logger logger = Logger.getLogger(StatisticsGameState.class.getName());
    /** The Graph node, rendered in Ortho queue. */
    private Node graphNode = null;
    private Quad lineGraph = null;
    private Quad labGraph = null;
    private LineGrapher lgrapher = null;
    private TabledLabelGrapher tgrapher = null;
    private DisplaySystem display = DisplaySystem.getDisplaySystem();

    /** transparency of the graph */
    private float alpha = 0.6f;
    /** width factor of the graph */
    private float widthFactor =1.0f;
    /** height factor of the graph */
    private float heightFactor = 0.75f;
    /** should the graph be displayed */
    private boolean doLineGraph = true;
    
    /**
     * a GameState which displays the statistics graph.
     */
    public StatisticsGameState() {
        this("statistics", 1.0f, 0.75f, 0.6f, true);
    }
    
    /**
     * a GameState which displays the statistics graph with the given width, height and alpha.
     * @param name name of the game state
     * @param width with of the graph
     * @param height height of the graph 
     * @param alpha transparency of the graph
     * @param doLineGraph display the lines graph also
     */
    public StatisticsGameState(String name, float widthFactor, float heightFactor, float alpha,
                                boolean doLineGraph) {
        super(name);
        this.widthFactor = widthFactor;
        this.heightFactor = heightFactor;
        this.alpha = alpha;
        this.doLineGraph = doLineGraph;
        setupGraph();
    }

    private void setupGraph() {
        // check if statistics are enabled
        if (Debug.stats == false) {
            logger.severe("Statistics cannot be displayed if Debug.stats is false, "
                    +"enable gathering of statistics first: System.setProperty(\"jme.stats\", \"set\");");
        }
        
        // graph node is being rendered in the ortho queue
        graphNode = new Node( "Graph node" );
        graphNode.setCullHint( Spatial.CullHint.Never );
        graphNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
        
        setupStatGraphs();
        setupStats();

        graphNode.updateGeometricState( 0.0f, true );
        graphNode.updateRenderState();
    }

    protected void setupStatGraphs() {
        StatCollector.setSampleRate(1000L);
        StatCollector.setMaxSamples(40);

        // the draw() method of the Quads is overridden, so that the geometry to draw the statistics,
        // is not listed in the statistics itself
        if (doLineGraph) {
            lineGraph = new Quad("lineGraph", display.getWidth()*widthFactor, display.getHeight()*heightFactor) {
                private static final long serialVersionUID = 1L;
                @Override
                public void draw(Renderer r) {
                    StatCollector.pause();
                    super.draw(r);
                    StatCollector.resume();
                }
            };
        }
        // create the label quad, (40 is a good height for 2 rows)
        labGraph = new Quad("labelGraph", display.getWidth()*widthFactor, doLineGraph==true?40:80) {
            private static final long serialVersionUID = 1L;
            @Override
            public void draw(Renderer r) {
                StatCollector.pause();
                super.draw(r);
                StatCollector.resume();
            }
        };
        
        if (doLineGraph) {
        	lgrapher = GraphFactory.makeLineGraph((int)(lineGraph.getWidth()+.5f), (int)(lineGraph.getHeight()+.5f), lineGraph);
        }
        tgrapher = GraphFactory.makeTabledLabelGraph((int)(labGraph.getWidth()+.5f), (int)(labGraph.getHeight()+.5f), labGraph);
                   
        if (doLineGraph) {
            lineGraph.setLocalTranslation((lineGraph.getWidth()*.5f), display.getHeight()-labGraph.getHeight()-(lineGraph.getHeight()*0.5f),0);
            lineGraph.getDefaultColor().a = alpha;
            graphNode.attachChild(lineGraph);
        }
        
        tgrapher.setColumns(doLineGraph==true?2:1);
        tgrapher.setMinimalBackground(false);
        tgrapher.linkTo(lgrapher);

        labGraph.setLocalTranslation((labGraph.getWidth()*.5f), (display.getHeight()-labGraph.getHeight()*0.5f),0);
        labGraph.getDefaultColor().a = alpha;
        
        graphNode.attachChild(labGraph);   
    }

    /**
     * Set up which stats to graph
     */
    protected void setupStats() {
        tgrapher.addConfig(StatType.STAT_FRAMES, TabledLabelGrapher.ConfigKeys.Decimals.name(), 0);
        tgrapher.addConfig(StatType.STAT_FRAMES, TabledLabelGrapher.ConfigKeys.Name.name(), "Frames/s:");
        tgrapher.addConfig(StatType.STAT_TRIANGLE_COUNT, TabledLabelGrapher.ConfigKeys.Decimals.name(), 0);
        tgrapher.addConfig(StatType.STAT_TRIANGLE_COUNT, TabledLabelGrapher.ConfigKeys.Name.name(), "Avg.Tris:");
        tgrapher.addConfig(StatType.STAT_TRIANGLE_COUNT, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
        tgrapher.addConfig(StatType.STAT_QUAD_COUNT, TabledLabelGrapher.ConfigKeys.Decimals.name(), 0);
        tgrapher.addConfig(StatType.STAT_QUAD_COUNT, TabledLabelGrapher.ConfigKeys.Name.name(), "Avg.Quads:");
        tgrapher.addConfig(StatType.STAT_QUAD_COUNT, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
        tgrapher.addConfig(StatType.STAT_LINE_COUNT, TabledLabelGrapher.ConfigKeys.Decimals.name(), 0);
        tgrapher.addConfig(StatType.STAT_LINE_COUNT, TabledLabelGrapher.ConfigKeys.Name.name(), "Avg.Lines:");
        tgrapher.addConfig(StatType.STAT_LINE_COUNT, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
        tgrapher.addConfig(StatType.STAT_GEOM_COUNT, TabledLabelGrapher.ConfigKeys.Decimals.name(), 0);
        tgrapher.addConfig(StatType.STAT_GEOM_COUNT, TabledLabelGrapher.ConfigKeys.Name.name(), "Avg.Objs:");
        tgrapher.addConfig(StatType.STAT_GEOM_COUNT, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
        tgrapher.addConfig(StatType.STAT_TEXTURE_BINDS, TabledLabelGrapher.ConfigKeys.Decimals.name(), 0);
        tgrapher.addConfig(StatType.STAT_TEXTURE_BINDS, TabledLabelGrapher.ConfigKeys.Name.name(), "Avg.Tex binds:");
        tgrapher.addConfig(StatType.STAT_TEXTURE_BINDS, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);

        if (!doLineGraph) {
            return;
        }
        lgrapher.addConfig(StatType.STAT_FRAMES, LineGrapher.ConfigKeys.Color.name(), ColorRGBA.green);
        lgrapher.addConfig(StatType.STAT_FRAMES, LineGrapher.ConfigKeys.Stipple.name(), 0XFF0F);
        lgrapher.addConfig(StatType.STAT_TRIANGLE_COUNT, LineGrapher.ConfigKeys.Color.name(), ColorRGBA.cyan);
        lgrapher.addConfig(StatType.STAT_TRIANGLE_COUNT, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
        lgrapher.addConfig(StatType.STAT_QUAD_COUNT, LineGrapher.ConfigKeys.Color.name(), ColorRGBA.lightGray);
        lgrapher.addConfig(StatType.STAT_QUAD_COUNT, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
        lgrapher.addConfig(StatType.STAT_LINE_COUNT, LineGrapher.ConfigKeys.Color.name(), ColorRGBA.red);
        lgrapher.addConfig(StatType.STAT_LINE_COUNT, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
        lgrapher.addConfig(StatType.STAT_GEOM_COUNT, LineGrapher.ConfigKeys.Color.name(), ColorRGBA.gray);
        lgrapher.addConfig(StatType.STAT_GEOM_COUNT, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
        lgrapher.addConfig(StatType.STAT_TEXTURE_BINDS, LineGrapher.ConfigKeys.Color.name(), ColorRGBA.orange);
        lgrapher.addConfig(StatType.STAT_TEXTURE_BINDS, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
    }

    /**
     * updates the statistics graph.
     */
    public void update(float tpf) {
        super.update(tpf);
        StatCollector.update();
    }

    /**
     * draws the graph.
     */
    @Override
    public void render(float tpf) {
        super.render(tpf);
        graphNode.draw(DisplaySystem.getDisplaySystem().getRenderer());
    }
    
    /**
     * Enable/Disable this GameState and statistics gathering.  
     */
    @Override
    public void setActive(boolean active) {
        super.setActive(active);
        Debug.updateGraphs = active;
    }

    /**
     * @return the line graph quad
     */
    public Quad getLineGraph() {
        return lineGraph;
    }

    /**
     * @return the label quad graph
     */
    public Quad getLabGraph() {
        return labGraph;
    }
}