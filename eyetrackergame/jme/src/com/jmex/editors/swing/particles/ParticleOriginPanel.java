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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jme.math.FastMath;
import com.jme.math.Line;
import com.jme.math.Rectangle;
import com.jme.math.Ring;
import com.jme.math.Vector3f;
import com.jmex.editors.swing.widget.ValuePanel;
import com.jmex.editors.swing.widget.VectorPanel;
import com.jmex.effects.particles.ParticleSystem;

public class ParticleOriginPanel extends ParticleEditPanel {
    private static final long serialVersionUID = 1L;
    
    // origin panel components
    private VectorPanel translationPanel = new VectorPanel(-Float.MAX_VALUE,
            Float.MAX_VALUE, 1f);
    private VectorPanel rotationPanel = new VectorPanel(-180f, 180f, 1f);
    private ValuePanel scalePanel = new ValuePanel("System Scale: ", " ", 0f,
            Float.MAX_VALUE, 0.01f);
    private JComboBox originTypeBox;
    private JPanel originParamsPanel;
    private JPanel pointParamsPanel;
    private JPanel lineParamsPanel;
    private ValuePanel lineLengthPanel = new ValuePanel("Length: ", "", 0f,
            Float.MAX_VALUE, 1f);
    private JPanel rectParamsPanel;
    private ValuePanel rectWidthPanel = new ValuePanel("Width: ", "", 0f,
            Float.MAX_VALUE, 1f);
    private ValuePanel rectHeightPanel = new ValuePanel("Height: ", "", 0f,
            Float.MAX_VALUE, 1f);
    private JPanel ringParamsPanel;
    private ValuePanel ringInnerPanel = new ValuePanel("Inner Radius: ", "",
            0f, Float.MAX_VALUE, 1f);
    private ValuePanel ringOuterPanel = new ValuePanel("Outer Radius: ", "", 0f, Float.MAX_VALUE, 1f);

    public ParticleOriginPanel() {
        super();
        setLayout(new GridBagLayout());
        initPanel();
    }
    
    private void initPanel() {
        JPanel transformPanel = new JPanel(new GridBagLayout());
        transformPanel.setBorder(createTitledBorder(" EMITTER TRANSFORM "));
        
        translationPanel.setBorder(createTitledBorder(" TRANSLATION "));
        translationPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                getEdittedParticles().getLocalTranslation().set(
                    translationPanel.getValue());
            }
        });
        
        rotationPanel.setBorder(createTitledBorder(" ROTATION "));
        rotationPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                Vector3f val = rotationPanel.getValue().multLocal(
                    FastMath.DEG_TO_RAD);
                getEdittedParticles().getLocalRotation().fromAngles(val.x, val.y,
                    val.z);
            }
        });
        
        scalePanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                getEdittedParticles().setLocalScale(scalePanel.getFloatValue());
            }
        });
        
        transformPanel.add(translationPanel, new GridBagConstraints(0, 0, 1, 1,
            0.5, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(0, 0, 0, 0), 0, 0));
        transformPanel.add(rotationPanel, new GridBagConstraints(0, 1, 1, 1,
            0.5, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(0, 0, 0, 0), 0, 0));
        transformPanel.add(scalePanel, new GridBagConstraints(0, 2, 2, 1,
            1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(0, 0, 0, 0), 0, 0));
        
        originTypeBox = new JComboBox(new String[] {
            "Point", "Line", "Rectangle", "Ring" });
        originTypeBox.setBorder(createTitledBorder(" EMITTER TYPE "));
        originTypeBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateOriginParams();
            }
        });
        
        originParamsPanel = new JPanel(new BorderLayout());
        
        pointParamsPanel = createPointParamsPanel();
        lineParamsPanel = createLineParamsPanel();
        rectParamsPanel = createRectParamsPanel();
        ringParamsPanel = createRingParamsPanel();
        
        add(transformPanel, new GridBagConstraints(0, 0, 1, 1, 1.0,
            0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(10, 10, 10, 10), 0, 0));
        add(originTypeBox, new GridBagConstraints(0, 1, 1, 1, 1.0,
            0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(10, 10, 10, 10), 0, 0));
        add(originParamsPanel, new GridBagConstraints(0, 2, 1, 1,
            1.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
            new Insets(5, 10, 5, 5), 0, 0));
    }

    private JPanel createPointParamsPanel() {
        return new JPanel();
    }
    
    private JPanel createLineParamsPanel() {
        lineLengthPanel.setBorder(createTitledBorder(" LINE PARAMETERS "));
        lineLengthPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                Line line = getEdittedParticles().getLine();
                float val = lineLengthPanel.getFloatValue();
                line.getOrigin().set(-val/2, 0f, 0f);
                line.getDirection().set(val/2, 0f, 0f);
            }
        });
        return lineLengthPanel;
    }
    
    private JPanel createRectParamsPanel() {
        rectWidthPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                Rectangle rect = getEdittedParticles().getRectangle();
                float width = rectWidthPanel.getFloatValue();
                rect.getA().x = -width/2;
                rect.getB().x = width/2;
                rect.getC().x = -width/2;
            }
        });
        rectHeightPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                Rectangle rect = getEdittedParticles().getRectangle();
                float height = rectHeightPanel.getFloatValue();
                rect.getA().z = -height/2;
                rect.getB().z = -height/2;
                rect.getC().z = height/2;
            }
        });
       
        JPanel rectParamsPanel = new JPanel(new GridBagLayout());
        rectParamsPanel.setBorder(createTitledBorder(" RECTANGLE PARAMETERS "));
        rectParamsPanel.add(rectWidthPanel, new GridBagConstraints(0, 0, 1, 1, 1.0,
            0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(5, 10, 5, 10), 0, 0));
        rectParamsPanel.add(rectHeightPanel, new GridBagConstraints(0, 1, 1, 1, 1.0,
            0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(5, 10, 5, 10), 0, 0));
        return rectParamsPanel;
    }
    
    private JPanel createRingParamsPanel() {
        ringInnerPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                Ring ring = getEdittedParticles().getRing();
                ring.setInnerRadius(ringInnerPanel.getFloatValue());
            }
        });
        ringOuterPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                Ring ring = getEdittedParticles().getRing();
                ring.setOuterRadius(ringOuterPanel.getFloatValue());
            }
        }); 
        
        JPanel ringParamsPanel = new JPanel(new GridBagLayout());
        ringParamsPanel.setBorder(createTitledBorder(" RING PARAMETERS "));
        ringParamsPanel.add(ringInnerPanel, new GridBagConstraints(0, 0, 1, 1, 1.0,
            0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(5, 10, 5, 10), 0, 0));
        ringParamsPanel.add(ringOuterPanel, new GridBagConstraints(0, 1, 1, 1, 1.0,
            0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(5, 10, 5, 10), 0, 0));
        return ringParamsPanel;
    }

    @Override
    public void updateWidgets() {
        translationPanel.setValue(getEdittedParticles().getLocalTranslation());
        float[] angles = getEdittedParticles().getLocalRotation().toAngles(null);
        rotationPanel.setValue(new Vector3f(angles[0], angles[1],
            angles[2]).multLocal(FastMath.RAD_TO_DEG));
        scalePanel.setValue(getEdittedParticles().getLocalScale().x);
        
        switch (getEdittedParticles().getEmitType()) {
            case Point:
                originTypeBox.setSelectedItem("Point");
                break;
            case Line:
                originTypeBox.setSelectedItem("Line");
                break;
            case Rectangle:
                originTypeBox.setSelectedItem("Rectangle");
                break;
            case Ring:
                originTypeBox.setSelectedItem("Ring"); 
                break;
        } 
        updateOriginParams();
    }

    /**
     * updateOriginParams
     */
    private void updateOriginParams() {
        originParamsPanel.removeAll();
        String type = (String)originTypeBox.getSelectedItem();
        if (type.equals("Point")) {
            getEdittedParticles().setEmitType(ParticleSystem.EmitType.Point);
            originParamsPanel.add(pointParamsPanel);
            
        } else if (type.equals("Line")) {
            getEdittedParticles().setEmitType(ParticleSystem.EmitType.Line);
            Line line = getEdittedParticles().getLine();
            if (line == null) {
                getEdittedParticles().setGeometry(line = new Line());
            }
            lineLengthPanel.setValue(line.getOrigin().distance(
                line.getDirection()));
            originParamsPanel.add(lineParamsPanel);
            
        } else if (type.equals("Rectangle")) {
            getEdittedParticles().setEmitType(ParticleSystem.EmitType.Rectangle);
            Rectangle rect = getEdittedParticles().getRectangle();
            if (rect == null) {
                getEdittedParticles().setGeometry(rect = new Rectangle());
            }
            rectWidthPanel.setValue(rect.getA().distance(rect.getB()));
            rectHeightPanel.setValue(rect.getA().distance(rect.getC()));
            originParamsPanel.add(rectParamsPanel);
            
        } else if (type.equals("Ring")) {
            getEdittedParticles().setEmitType(ParticleSystem.EmitType.Ring);
            Ring ring = getEdittedParticles().getRing();
            if (ring == null) {
                getEdittedParticles().setGeometry(ring = new Ring());
            }
            ringInnerPanel.setValue(ring.getInnerRadius());
            ringOuterPanel.setValue(ring.getOuterRadius());
            originParamsPanel.add(ringParamsPanel);
        }
        originParamsPanel.getParent().validate();
        originParamsPanel.getParent().repaint();
    }
    
}
