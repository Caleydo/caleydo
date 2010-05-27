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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.MouseInputAdapter;

import com.jme.math.FastMath;

public class ValueSpinner extends JSpinner {
    private static final long serialVersionUID = 1L;
    private NumberEditor ne;

    public ValueSpinner(float minimum, float maximum, float stepSize) {
        this(Float.valueOf(minimum), Float.valueOf(maximum),
            Float.valueOf(stepSize));
        ((NumberEditor)getEditor()).getFormat().setMinimumFractionDigits(
            (int)FastMath.log(1f/stepSize, 10f));
    }
    
    public ValueSpinner(int minimum, int maximum, int stepSize) {
        this(Integer.valueOf(minimum), Integer.valueOf(maximum),
            Integer.valueOf(stepSize));
    }
    
    public ValueSpinner(Number minimum, Number maximum, Number stepSize) {
        super(new SpinnerNumberModel(minimum, (Comparable)minimum,
            (Comparable)maximum, stepSize));
        MouseInputAdapter mia = new MouseInputAdapter() {
            public void mousePressed(MouseEvent e) {
                _last.setLocation(e.getPoint());
            } 
            public void mouseDragged(MouseEvent e) {
                int delta = (e.getX() - _last.x) + (_last.y - e.getY());
                _last.setLocation(e.getPoint());
                for (int ii = 0, nn = Math.abs(delta); ii < nn; ii++) {
                    Object next = (delta > 0) ? getModel().getNextValue() :
                        getModel().getPreviousValue();
                    if (next != null) {
                        getModel().setValue(next);
                    }
                }
            }
            protected Point _last = new Point();
        };
        ne = new NumberEditor(this) {
            private static final long serialVersionUID = 1L;

            public Dimension preferredLayoutSize(Container parent) {
                Dimension d = super.preferredLayoutSize(parent);
                d.width = Math.min(Math.max(d.width, 50), 65);
                return d;
            }
        };
        setEditor(ne);
        addMouseInputListener(this, mia);
    }
    
    @Override
    public Dimension getMinimumSize() {
        return ne.getPreferredSize();
    }
    
    protected void addMouseInputListener(Container c, MouseInputAdapter mia) {
        for (int ii = 0, nn = c.getComponentCount(); ii < nn; ii++) {
            Component comp = c.getComponent(ii);
            if (comp instanceof JButton) {
                comp.addMouseListener(mia);
                comp.addMouseMotionListener(mia);
                
            } else if (comp instanceof Container) {
                addMouseInputListener((Container)comp, mia);
            }
        }
    }
}

