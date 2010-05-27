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
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jme.scene.Controller;
import com.jmex.editors.swing.widget.ValuePanel;

public class ParticleFlowPanel extends ParticleEditPanel {
    private static final long serialVersionUID = 1L;

    // flow panel components
    private JCheckBox rateBox;
    private ValuePanel releaseRatePanel = new ValuePanel(
            "Particles per second: ", "", 0, Integer.MAX_VALUE, 1);
    private ValuePanel rateVarPanel = new ValuePanel("Variance: ", "%", 0f, 1f,
            0.001f);
    private ValuePanel minAgePanel = new ValuePanel("Minimum Age: ", "ms", 0f,
            Float.MAX_VALUE, 10f);
    private ValuePanel maxAgePanel = new ValuePanel("Maximum Age: ", "ms", 0f,
            Float.MAX_VALUE, 10f);
    private JCheckBox spawnBox;

    public ParticleFlowPanel() {
        setLayout(new GridBagLayout());
        initPanel();
    }

    private void initPanel() {
        rateBox = new JCheckBox(new AbstractAction("Regulate Flow") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                getEdittedParticles().getParticleController().setControlFlow(
                        rateBox.isSelected());
                updateRateLabels();
            }
        });

        releaseRatePanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                getEdittedParticles().setReleaseRate(
                        releaseRatePanel.getIntValue());
            }
        });

        rateVarPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                getEdittedParticles().setReleaseVariance(
                        rateVarPanel.getFloatValue());
            }
        });

        minAgePanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                getEdittedParticles().setMinimumLifeTime(minAgePanel.getFloatValue());
            }
        });
        maxAgePanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                getEdittedParticles().setMaximumLifeTime(maxAgePanel.getFloatValue());
            }
        });
        JPanel agePanel = new JPanel(new GridBagLayout());
        agePanel.setBorder(createTitledBorder("PARTICLE AGE"));
        agePanel.add(minAgePanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 5, 5, 5), 0, 0));
        agePanel.add(maxAgePanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 5, 5, 5), 0, 0));
        
        JPanel ratePanel = new JPanel(new GridBagLayout());
        ratePanel.setBorder(createTitledBorder("RATE"));
        ratePanel.add(rateBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(
                        10, 5, 5, 5), 0, 0));
        ratePanel.add(releaseRatePanel, new GridBagConstraints(0, 1, 1, 1, 1.0,
                0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 10, 5), 0, 0));
        ratePanel.add(rateVarPanel, new GridBagConstraints(0, 2, 1, 1, 1.0,
                0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 10, 5), 0, 0));

        spawnBox = new JCheckBox(
                new AbstractAction("Respawn 'dead' particles.") {
                    private static final long serialVersionUID = 1L;

                    public void actionPerformed(ActionEvent e) {
                        if (spawnBox.isSelected())
                            getEdittedParticles().getParticleController()
                                    .setRepeatType(Controller.RT_WRAP);
                        else
                            getEdittedParticles().getParticleController()
                                    .setRepeatType(Controller.RT_CLAMP);
                    }
                });
        spawnBox.setSelected(true);

        JButton spawnButton = new JButton(new AbstractAction(
                "Force Respawn") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                getEdittedParticles().forceRespawn();
            }
        });

        JPanel spawnPanel = new JPanel(new GridBagLayout());
        spawnPanel.setBorder(createTitledBorder("SPAWN"));
        spawnPanel.add(spawnBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(
                        10, 10, 5, 10), 0, 0));
        spawnPanel.add(spawnButton, new GridBagConstraints(0, 1, 1, 1, 0.0,
                0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(5, 10, 10, 10), 0, 0));

        add(ratePanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(10, 10, 5, 10), 0, 0));
        add(spawnPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(10, 10, 10, 10), 0, 0));
        add(agePanel, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
                new Insets(10, 10, 5, 5), 0, 0));
    }

    @Override
    public void updateWidgets() {
        rateBox.setSelected(getEdittedParticles().getParticleController()
                .isControlFlow());
        releaseRatePanel.setValue(getEdittedParticles().getReleaseRate());
        rateVarPanel.setValue(getEdittedParticles().getReleaseVariance());
        updateRateLabels();
        spawnBox.setSelected(getEdittedParticles().getParticleController()
                .getRepeatType() == Controller.RT_WRAP);
        minAgePanel.setValue(getEdittedParticles().getMinimumLifeTime());
        maxAgePanel.setValue(getEdittedParticles().getMaximumLifeTime());
    }

    /**
     * updateRateLabels
     */
    private void updateRateLabels() {
        releaseRatePanel.setEnabled(rateBox.isSelected());
        rateVarPanel.setEnabled(rateBox.isSelected());
    }
}
