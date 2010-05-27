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

import com.jme.math.FastMath;
import com.jmex.editors.swing.widget.ValuePanel;
import com.jmex.editors.swing.widget.VectorPanel;
import com.jmex.effects.particles.SwarmInfluence;

public class SwarmInfluencePanel extends InfluenceEditPanel {

    private static final long serialVersionUID = 1L;

    private ValuePanel swarmRange = new ValuePanel("Range: ", "", -Float.MIN_VALUE,
            Float.MAX_VALUE, 0.1f);
    private ValuePanel swarmTurnSpeed = new ValuePanel("Turn Speed: ", "", -Float.MIN_VALUE,
            Float.MAX_VALUE, 0.1f);
    private ValuePanel swarmMaxSpeed = new ValuePanel("Max Speed: ", "", -Float.MIN_VALUE,
            Float.MAX_VALUE, 0.1f);
    private ValuePanel swarmAcceleration = new ValuePanel("Acceleration: ", "", -Float.MIN_VALUE,
            Float.MAX_VALUE, 0.1f);
    private ValuePanel swarmDeviance = new ValuePanel("Deviance: ", "", 0,
            180, 1f);
    private VectorPanel swarmLocationPanel = new VectorPanel(-Float.MIN_VALUE,
            Float.MAX_VALUE, 0.1f);

    public SwarmInfluencePanel() {
        super();
        setLayout(new GridBagLayout());
        initPanel();
    }

    private void initPanel() {
        swarmRange.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ((SwarmInfluence) getEdittedInfluence()).setSwarmRange(swarmRange.getFloatValue());
            }
        });
        swarmDeviance.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ((SwarmInfluence) getEdittedInfluence()).setDeviance(FastMath.DEG_TO_RAD * swarmDeviance.getFloatValue());
            }
        });
        swarmMaxSpeed.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ((SwarmInfluence) getEdittedInfluence()).setMaxSpeed(swarmMaxSpeed.getFloatValue());
            }
        });
        swarmAcceleration.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ((SwarmInfluence) getEdittedInfluence()).setSpeedBump(swarmAcceleration.getFloatValue());
            }
        });
        swarmTurnSpeed.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ((SwarmInfluence) getEdittedInfluence()).setTurnSpeed(swarmTurnSpeed.getFloatValue());
            }
        });

        swarmLocationPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ((SwarmInfluence) getEdittedInfluence()).setSwarmOffset(swarmLocationPanel.getValue());
            }
        });
        
        swarmLocationPanel.setBorder(createTitledBorder(" SWARM OFFSET "));
        
        setBorder(createTitledBorder(" SWARM PARAMETERS "));
        add(swarmLocationPanel, new GridBagConstraints(0, 0, 1, 1, 0.5, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 10, 5), 0, 0));
        add(swarmRange, new GridBagConstraints(0, 1, 1, 1, 0.5, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 10, 5), 0, 0));
        add(swarmMaxSpeed, new GridBagConstraints(0, 2, 1, 1, 0.5, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 10, 5), 0, 0));
        add(swarmAcceleration, new GridBagConstraints(0, 3, 1, 1, 0.5, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 10, 5), 0, 0));
        add(swarmTurnSpeed, new GridBagConstraints(0, 4, 1, 1, 0.5, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 10, 5), 0, 0));
        add(swarmDeviance, new GridBagConstraints(0, 5, 1, 1, 0.5, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 10, 5), 0, 0));
    }

    @Override
    public void updateWidgets() {
        SwarmInfluence swarm = (SwarmInfluence) getEdittedInfluence();
        swarmLocationPanel.setValue(swarm.getSwarmOffset());
        swarmRange.setValue(swarm.getSwarmRange());
        swarmMaxSpeed.setValue(swarm.getMaxSpeed());
        swarmAcceleration.setValue(swarm.getSpeedBump());
        swarmTurnSpeed.setValue(swarm.getTurnSpeed());
        swarmDeviance.setValue(swarm.getDeviance() * FastMath.RAD_TO_DEG);
    }
}
