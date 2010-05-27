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

package com.jmex.editors.swing.widget;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jme.math.FastMath;
import com.jme.math.Vector3f;

public class SphericalUnitVectorPanel extends JPanel implements ChangeListener {

    private static final long serialVersionUID = 1L;

    private ValuePanel azimuthPanel = new ValuePanel("Azimuth: ", "", -180f,
            +180f, 1f);
    private ValuePanel elevationPanel = new ValuePanel("Elevation: ", "", -90f,
            +90f, 1f);
    private ArrayList<ChangeListener> changeListeners = new ArrayList<ChangeListener>();
    private boolean setting;
    private Vector3f vector = new Vector3f();

    public SphericalUnitVectorPanel() {
        super(new GridBagLayout());
        azimuthPanel.addChangeListener(this);
        elevationPanel.addChangeListener(this);

        add(azimuthPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        add(elevationPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
    }

    public void setValue(Vector3f value) {
        FastMath.cartesianToSpherical(value, vector);
        setting = true;
        azimuthPanel.setValue(vector.y * FastMath.RAD_TO_DEG);
        elevationPanel.setValue(vector.z * FastMath.RAD_TO_DEG);
        setting = false;
    }

    public Vector3f getValue() {
        vector.set(1f, azimuthPanel.getFloatValue() * FastMath.DEG_TO_RAD,
                elevationPanel.getFloatValue() * FastMath.DEG_TO_RAD);
        Vector3f result = new Vector3f();
        FastMath.sphericalToCartesian(vector, result);
        return result;
    }

    public void addChangeListener(ChangeListener l) {
        changeListeners.add(l);
    }

    public void stateChanged(ChangeEvent e) {
        if (!setting) {
            for (ChangeListener l : changeListeners) {
                l.stateChanged(e);
            }
        }
    }
}
