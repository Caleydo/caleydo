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

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jme.math.FastMath;
import com.jmex.editors.swing.widget.SphericalUnitVectorPanel;
import com.jmex.editors.swing.widget.ValuePanel;

public class ParticleEmissionPanel extends ParticleEditPanel {
    private static final long serialVersionUID = 1L;

    private JCheckBox rotateWithEmitterBox;
    private SphericalUnitVectorPanel directionPanel = new SphericalUnitVectorPanel();
    private ValuePanel minAnglePanel = new ValuePanel("Min Degrees Off Dir.: ",
            "", 0f, 360f, 1f);
    private ValuePanel maxAnglePanel = new ValuePanel("Max Degrees Off Dir.: ",
            "", 0f, 360f, 1f);
    private ValuePanel velocityPanel = new ValuePanel("Initial Velocity: ", "",
            0f, Float.MAX_VALUE, 0.001f);

    public ParticleEmissionPanel() {
        super();
        setLayout(new GridBagLayout());
        initPanel();
    }
    
    private void initPanel() {
        rotateWithEmitterBox = new JCheckBox(new AbstractAction(
                "Rotate With Emitter") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                getEdittedParticles().setRotateWithScene(rotateWithEmitterBox
                        .isSelected());
            }
        });
        rotateWithEmitterBox.setFont(new Font("Arial", Font.BOLD, 12));

        directionPanel.setBorder(createTitledBorder("DIRECTION"));
        directionPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (getEdittedParticles() != null) {
                    getEdittedParticles().getEmissionDirection().set(
                            directionPanel.getValue());
                    getEdittedParticles().updateRotationMatrix();
                }
            }
        });
        directionPanel.add(rotateWithEmitterBox, new GridBagConstraints(0, 2,
                1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

        minAnglePanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                getEdittedParticles().setMinimumAngle(minAnglePanel.getFloatValue()
                        * FastMath.DEG_TO_RAD);
            }
        });
        maxAnglePanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                getEdittedParticles().setMaximumAngle(maxAnglePanel.getFloatValue()
                        * FastMath.DEG_TO_RAD);
            }
        });
        JPanel anglePanel = new JPanel(new GridBagLayout());
        anglePanel.setBorder(createTitledBorder("ANGLE"));
        anglePanel.add(minAnglePanel, new GridBagConstraints(0, 0, 1, 1, 1.0,
                0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 10, 5, 10), 0, 0));
        anglePanel.add(maxAnglePanel, new GridBagConstraints(0, 1, 1, 1, 1.0,
                0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 10, 5, 10), 0, 0));

        velocityPanel.setBorder(createTitledBorder("VELOCITY"));
        velocityPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                getEdittedParticles().setInitialVelocity(velocityPanel.getFloatValue());
            }
        });

        add(directionPanel, new GridBagConstraints(0, 0, 1, 1, 1.0,
                0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(10, 5, 5, 5), 0, 0));
        add(anglePanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 10, 5), 0, 0));
        add(velocityPanel, new GridBagConstraints(0, 2, 1, 1, 1.0,
                1.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 10, 5), 0, 0));
    }
    
    @Override
    public void updateWidgets() {
        rotateWithEmitterBox.setSelected(getEdittedParticles().isRotateWithScene());
        directionPanel.setValue(getEdittedParticles().getEmissionDirection());
        minAnglePanel.setValue(getEdittedParticles().getMinimumAngle() * FastMath.RAD_TO_DEG);
        maxAnglePanel.setValue(getEdittedParticles().getMaximumAngle() * FastMath.RAD_TO_DEG);
        velocityPanel.setValue(getEdittedParticles().getInitialVelocity());
    }

}
