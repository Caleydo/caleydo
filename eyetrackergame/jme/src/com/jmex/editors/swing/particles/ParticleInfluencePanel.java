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

import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.jme.math.Line;
import com.jme.math.Vector3f;
import com.jmex.effects.particles.FloorInfluence;
import com.jmex.effects.particles.ParticleInfluence;
import com.jmex.effects.particles.ParticleSystem;
import com.jmex.effects.particles.SimpleParticleInfluenceFactory;
import com.jmex.effects.particles.SwarmInfluence;
import com.jmex.effects.particles.WanderInfluence;

public class ParticleInfluencePanel extends ParticleEditPanel {

    private static final long serialVersionUID = 1L;

    private InfluenceListModel influenceModel = new InfluenceListModel();
    private JList influenceList = new JList(influenceModel);
    private JButton deleteInfluenceButton;
    private JPanel influenceParamsPanel;
    
    public ParticleInfluencePanel() {
        super();
        setLayout(new GridBagLayout());
        initPanel();
    }
    
    private void initPanel() {
        influenceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        influenceList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                int idx = influenceList.getSelectedIndex();
                deleteInfluenceButton.setEnabled(idx != -1);
                updateInfluenceParams();
            }
        });
        
        JButton newInfluenceButton = new JButton(new AbstractAction("Add Influence") {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                ParticleInfluence infl = getNewInfluence();
                if (infl != null) {
                    getEdittedParticles().addInfluence(infl);
                    int idx = getEdittedParticles().getInfluences().size() - 1;
                    influenceModel.fireIntervalAdded(idx, idx);
                    influenceList.setSelectedIndex(idx);
                }
            }
            private ParticleInfluence getNewInfluence() {
                Object chosen = JOptionPane.showInputDialog(ParticleInfluencePanel.this,
                        "Choose the influence type to add:", "Add Influence",
                        JOptionPane.OK_CANCEL_OPTION, null, 
                        new String[] { 
                            "wind",
                            "gravity",
                            "drag",
                            "vortex",
                            "swarm",
                            "wander",
                            "floor"
                        }, 
                        null);

                ParticleInfluence infl = null;
                if ("wind".equals(chosen)) {
                    infl = SimpleParticleInfluenceFactory.createBasicWind(1f,
                            Vector3f.UNIT_X.clone(), true, true);
                } else if ("gravity".equals(chosen)) {
                    infl = SimpleParticleInfluenceFactory.createBasicGravity(
                            Vector3f.ZERO.clone(), true);
                } else if ("drag".equals(chosen)) {
                    infl = SimpleParticleInfluenceFactory.createBasicDrag(1f);
                } else if ("vortex".equals(chosen)) {
                    infl = SimpleParticleInfluenceFactory.createBasicVortex(1f,
                            0f, new Line(new Vector3f(),
                                    Vector3f.UNIT_Y.clone()), true, true);
                } else if ("swarm".equals(chosen)) {
                    infl = new SwarmInfluence(new Vector3f(), 3);
                } else if ("wander".equals(chosen)) {
                    infl = new WanderInfluence();
                }else if ("floor".equals(chosen)) {
                    infl = new FloorInfluence(new Vector3f(0, -1, 0), new Vector3f(0, 1, 0), 0.75f);
                }
                return infl;
            }
        });
        newInfluenceButton.setMargin(new Insets(2, 2, 2, 2));
        
        deleteInfluenceButton = new JButton(new AbstractAction("Delete") {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                int idx = influenceList.getSelectedIndex();
                getEdittedParticles().getInfluences().remove(idx);
                influenceModel.fireIntervalRemoved(idx, idx);
                influenceList.setSelectedIndex(
                    idx >= getEdittedParticles().getInfluences().size() ? idx - 1 : idx);
            }
        });
        deleteInfluenceButton.setMargin(new Insets(2, 2, 2, 2));
        deleteInfluenceButton.setEnabled(false);
        
        JPanel influenceListPanel = new JPanel(new GridBagLayout());
        influenceListPanel.setBorder(createTitledBorder("PARTICLE INFLUENCES"));
        influenceListPanel.add(influenceList, new GridBagConstraints(0, 0, 1, 3, 0.5,
            0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(5, 10, 10, 5), 0, 0));
        influenceListPanel.add(newInfluenceButton, new GridBagConstraints(1, 0, 1, 1,
            0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(5, 5, 5, 5), 0, 0));
        influenceListPanel.add(deleteInfluenceButton, new GridBagConstraints(1, 1, 1, 1,
                0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 5, 10, 5), 0, 0));
        
        influenceParamsPanel = new JPanel(new BorderLayout());
        
        add(influenceListPanel, new GridBagConstraints(0, 0, 1, 1, 0.5,
            0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(5, 10, 10, 5), 0, 0));
        add(influenceParamsPanel, new GridBagConstraints(0, 1, 1, 1, 0.5,
            1.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
            new Insets(5, 10, 10, 5), 0, 0));
    }
    
    @Override
    public void updateWidgets() {
        influenceList.clearSelection();
        int fcount = (getEdittedParticles().getInfluences() == null) ?
            0 : getEdittedParticles().getInfluences().size();
        influenceModel.fireContentsChanged(0, fcount - 1);
    }

    /**
     * updateInfluenceParams
     */
    private void updateInfluenceParams() {
        influenceParamsPanel.removeAll();
        int idx = influenceList.getSelectedIndex();
        if (idx == -1) {
            influenceParamsPanel.validate();
            return;
        }
        ParticleInfluence influence = getEdittedParticles().getInfluences().get(idx);
        if (influence instanceof SimpleParticleInfluenceFactory.BasicWind) {
            WindInfluencePanel windParamsPanel = new WindInfluencePanel();
            windParamsPanel.setEdittedInfluence(influence);
            windParamsPanel.updateWidgets();
            influenceParamsPanel.add(windParamsPanel);
            
        } else if (influence instanceof SimpleParticleInfluenceFactory.BasicGravity) {
            GravityInfluencePanel gravityParamsPanel = new GravityInfluencePanel();
            gravityParamsPanel.setEdittedInfluence(influence);
            gravityParamsPanel.updateWidgets();
            influenceParamsPanel.add(gravityParamsPanel);
            
        } else if (influence instanceof SimpleParticleInfluenceFactory.BasicDrag) {
            DragInfluencePanel dragParamsPanel = new DragInfluencePanel();
            dragParamsPanel.setEdittedInfluence(influence);
            dragParamsPanel.updateWidgets();
            influenceParamsPanel.add(dragParamsPanel);
            
        } else if (influence instanceof SimpleParticleInfluenceFactory.BasicVortex) {
            VortexInfluencePanel vortexParamsPanel = new VortexInfluencePanel();
            vortexParamsPanel.setEdittedInfluence(influence);
            vortexParamsPanel.updateWidgets();
            influenceParamsPanel.add(vortexParamsPanel);

        } else if (influence instanceof SwarmInfluence) {
            SwarmInfluencePanel swarmInfluencePanel = new SwarmInfluencePanel();
            swarmInfluencePanel.setEdittedInfluence(influence);
            swarmInfluencePanel.updateWidgets();
            influenceParamsPanel.add(swarmInfluencePanel);

        } else if (influence instanceof WanderInfluence) {
            WanderInfluencePanel influencePanel = new WanderInfluencePanel();
            influencePanel.setEdittedInfluence(influence);
            influencePanel.updateWidgets();
            influenceParamsPanel.add(influencePanel);

        } else if (influence instanceof FloorInfluence) {
          FloorInfluencePanel floorInfluencePanel = new FloorInfluencePanel();
          floorInfluencePanel.setEdittedInfluence(influence);
          floorInfluencePanel.updateWidgets();
          influenceParamsPanel.add(floorInfluencePanel);
        }
        influenceParamsPanel.getParent().validate();
        influenceParamsPanel.getParent().repaint();
    }

    class InfluenceListModel extends AbstractListModel {

        private static final long serialVersionUID = 1L;

        public int getSize() {
            ParticleSystem particles = getEdittedParticles();
            return (particles == null || particles.getInfluences() == null) ? 0
                    : particles.getInfluences().size();
        }

        public Object getElementAt(int index) {
            ParticleInfluence pf = getEdittedParticles().getInfluences().get(index);
            if (pf instanceof SimpleParticleInfluenceFactory.BasicWind) {
                return "Wind";
            } else if (pf instanceof SimpleParticleInfluenceFactory.BasicGravity) {
                return "Gravity";
            } else if (pf instanceof SimpleParticleInfluenceFactory.BasicDrag) {
                return "Drag";
            } else if (pf instanceof SimpleParticleInfluenceFactory.BasicVortex) {
                return "Vortex";
            } else if (pf instanceof SwarmInfluence) {
                return "Swarm";
            } else if (pf instanceof WanderInfluence) {
                return "Wander";
            } else if (pf instanceof FloorInfluence) {
                return "Floor";
            } else {
                return "???";
            }
        }

        public void fireContentsChanged(int idx0, int idx1) {
            super.fireContentsChanged(this, idx0, idx1);
        }

        public void fireIntervalAdded(int idx0, int idx1) {
            super.fireIntervalAdded(this, idx0, idx1);
        }

        public void fireIntervalRemoved(int idx0, int idx1) {
            super.fireIntervalRemoved(this, idx0, idx1);
        }
    }

}
