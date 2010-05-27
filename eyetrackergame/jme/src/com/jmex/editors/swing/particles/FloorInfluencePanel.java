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

import java.awt.BorderLayout;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jmex.editors.swing.widget.ValuePanel;
import com.jmex.editors.swing.widget.VectorPanel;
import com.jmex.effects.particles.FloorInfluence;

public class FloorInfluencePanel extends InfluenceEditPanel {

    private static final long serialVersionUID = 1L;

    private VectorPanel posVector = new VectorPanel(-Float.MAX_VALUE,
            Float.MAX_VALUE, 0.1f);

    private VectorPanel normalVector = new VectorPanel(-Float.MAX_VALUE,
            Float.MAX_VALUE, 0.1f);

    private ValuePanel bouncynessValue = new ValuePanel("Bouncyness: ", "", 0,
            Float.MAX_VALUE, 0.01f);

    public FloorInfluencePanel() {
        super();
        setLayout(new BorderLayout());
        initPanel();
    }

    private void initPanel() {
        posVector.setBorder(createTitledBorder(" PLANE POSITION "));
        posVector.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                ((FloorInfluence) getEdittedInfluence()).setPos(posVector
                        .getValue());
            }
        });
        add(posVector, BorderLayout.NORTH);

        normalVector.setBorder(createTitledBorder(" PLANE NORMAL "));
        normalVector.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                ((FloorInfluence) getEdittedInfluence()).setNormal(normalVector
                        .getValue());
            }
        });
        add(normalVector, BorderLayout.CENTER);

        bouncynessValue.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                ((FloorInfluence) getEdittedInfluence())
                        .setBouncyness(bouncynessValue.getFloatValue());
            }
        });
        add(bouncynessValue, BorderLayout.SOUTH);
    }

    @Override
    public void updateWidgets() {
        posVector.setValue(((FloorInfluence) getEdittedInfluence()).getPos());
        normalVector.setValue(((FloorInfluence) getEdittedInfluence())
                .getNormal());
        bouncynessValue.setValue(((FloorInfluence) getEdittedInfluence())
                .getBouncyness());
    }
}
