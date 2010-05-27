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
import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jmex.editors.swing.widget.SphericalUnitVectorPanel;
import com.jmex.editors.swing.widget.ValuePanel;
import com.jmex.effects.particles.SimpleParticleInfluenceFactory;

public class WindInfluencePanel extends InfluenceEditPanel {

    private static final long serialVersionUID = 1L;

    private ValuePanel windStrengthPanel = new ValuePanel("Strength: ", "", 0f,
            100f, 0.1f);
    private SphericalUnitVectorPanel windDirectionPanel = new SphericalUnitVectorPanel();
    private JCheckBox windRandomBox;

    public WindInfluencePanel() {
        super();
        setLayout(new GridBagLayout());
        initPanel();
    }

    private void initPanel() {
        windDirectionPanel.setBorder(createTitledBorder(" DIRECTION "));
        windDirectionPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ((SimpleParticleInfluenceFactory.BasicWind) getEdittedInfluence())
                        .setWindDirection(windDirectionPanel.getValue());
            }
        });
        windStrengthPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ((SimpleParticleInfluenceFactory.BasicWind) getEdittedInfluence())
                        .setStrength(windStrengthPanel.getFloatValue());
            }
        });
        windRandomBox = new JCheckBox(new AbstractAction("Vary Randomly") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                ((SimpleParticleInfluenceFactory.BasicWind) getEdittedInfluence())
                        .setRandom(windRandomBox.isSelected());
            }
        });

        setBorder(createTitledBorder(" WIND PARAMETERS "));
        add(windDirectionPanel, new GridBagConstraints(0, 0, 1, 1, 0.5, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 10, 5), 0, 0));
        add(windStrengthPanel, new GridBagConstraints(0, 1, 1, 1, 0.5, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 10, 5), 0, 0));
        add(windRandomBox, new GridBagConstraints(0, 2, 1, 1, 0.5, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(5, 5, 10, 5), 0, 0));
    }

    @Override
    public void updateWidgets() {
        SimpleParticleInfluenceFactory.BasicWind wind = (SimpleParticleInfluenceFactory.BasicWind) getEdittedInfluence();
        windDirectionPanel.setValue(wind.getWindDirection());
        windStrengthPanel.setValue(wind.getStrength());
        windRandomBox.setSelected(wind.isRandom());
    }
}
