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

package com.jme.util.stat;

/**
 * @author Joshua Slack
 */
public class StatType implements Comparable<StatType> {
    
    public static final StatType STAT_FRAMES = new StatType("_frames");

    public static final StatType STAT_TRIANGLE_COUNT = new StatType("_triCount");
    public static final StatType STAT_QUAD_COUNT = new StatType("_quadCount");
    public static final StatType STAT_LINE_COUNT = new StatType("_lineCount");
    public static final StatType STAT_POINT_COUNT = new StatType("_pointCount");
    public static final StatType STAT_VERTEX_COUNT = new StatType("_vertCount");
    public static final StatType STAT_GEOM_COUNT = new StatType("_geomCount");
    public static final StatType STAT_TEXTURE_BINDS = new StatType("_texBind");
    public static final StatType STAT_SHADER_BINDS = new StatType("_shaderBind");

    public static final StatType STAT_UNSPECIFIED_TIMER = new StatType("_timedOther");
    public static final StatType STAT_RENDER_TIMER = new StatType("_timedRenderer");
    public static final StatType STAT_STATES_TIMER = new StatType("_timedStates");
    public static final StatType STAT_TEXTURE_STATE_TIMER = new StatType("_timedTextureState");
    public static final StatType STAT_SHADER_STATE_TIMER = new StatType("_timedShaderState");
    public static final StatType STAT_UPDATE_TIMER = new StatType("_timedUpdates");
    public static final StatType STAT_DISPLAYSWAP_TIMER = new StatType("_timedSwap");
    
    private String statName = "-unknown-";

    public StatType(String name) {
        statName = name;
    }

    public String getStatName() {
        return statName;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof StatType)) {
            return false;
        }
        StatType other = (StatType)obj;
        if (!statName.equals(other.statName)) return false;

        return true;
    }
    
    @Override
    public int hashCode() {
        int hash = statName.hashCode();
        return hash;
    }

    public int compareTo(StatType obj) {
        StatType other = (StatType)obj;
        return statName.compareTo(other.statName);
    }
}
