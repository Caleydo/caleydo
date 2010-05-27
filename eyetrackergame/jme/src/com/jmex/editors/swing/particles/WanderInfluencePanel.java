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

package com.jmex.editors.swing.particles;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jmex.editors.swing.widget.ValuePanel;
import com.jmex.effects.particles.WanderInfluence;

public class WanderInfluencePanel extends InfluenceEditPanel {

    private static final long serialVersionUID = 1L;

    private ValuePanel wanderRadius = new ValuePanel("Wander Circle Radius: ", "", 0,
            Float.MAX_VALUE, 0.01f);
    private ValuePanel wanderDistance = new ValuePanel("Wander Circle Distance: ", "", -Float.MIN_VALUE,
            Float.MAX_VALUE, 0.1f);
    private ValuePanel wanderJitter = new ValuePanel("Jitter Amount: ", "", -Float.MIN_VALUE,
            Float.MAX_VALUE, 0.001f);

    public WanderInfluencePanel() {
        super();
        setLayout(new GridBagLayout());
        initPanel();
    }

    private void initPanel() {
        wanderRadius.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ((WanderInfluence) getEdittedInfluence()).setWanderRadius(wanderRadius.getFloatValue());
            }
        });
        wanderDistance.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ((WanderInfluence) getEdittedInfluence()).setWanderDistance(wanderDistance.getFloatValue());
            }
        });
        wanderJitter.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ((WanderInfluence) getEdittedInfluence()).setWanderJitter(wanderJitter.getFloatValue());
            }
        });
        
        setBorder(createTitledBorder(" WANDER PARAMETERS "));
        add(wanderRadius, new GridBagConstraints(0, 0, 1, 1, 0.5, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 10, 5), 0, 0));
        add(wanderDistance, new GridBagConstraints(0, 1, 1, 1, 0.5, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 10, 5), 0, 0));
        add(wanderJitter, new GridBagConstraints(0, 2, 1, 1, 0.5, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 10, 5), 0, 0));
    }

    @Override
    public void updateWidgets() {
        WanderInfluence wander = (WanderInfluence) getEdittedInfluence();
        wanderRadius.setValue(wander.getWanderRadius());
        wanderDistance.setValue(wander.getWanderDistance());
        wanderJitter.setValue(wander.getWanderJitter());
    }
}
