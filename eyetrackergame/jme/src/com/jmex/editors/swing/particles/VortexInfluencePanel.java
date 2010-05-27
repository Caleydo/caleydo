package com.jmex.editors.swing.particles;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jme.math.FastMath;
import com.jmex.editors.swing.widget.SphericalUnitVectorPanel;
import com.jmex.editors.swing.widget.ValuePanel;
import com.jmex.effects.particles.SimpleParticleInfluenceFactory;

public class VortexInfluencePanel extends InfluenceEditPanel {

    private static final long serialVersionUID = 1L;

    private JComboBox vortexTypeBox;
    private ValuePanel vortexRadiusPanel = new ValuePanel("Radius: ", "", 0f,
            Float.MAX_VALUE, 1f);
    private ValuePanel vortexHeightPanel = new ValuePanel("Height: ", "",
            -Float.MAX_VALUE, Float.MAX_VALUE, 1f);
    private ValuePanel vortexStrengthPanel = new ValuePanel("Strength: ", "",
            0f, Float.MAX_VALUE, 0.1f);
    private ValuePanel vortexDivergencePanel = new ValuePanel("Divergence: ",
            "", -90f, 90f, 1f);
    private SphericalUnitVectorPanel vortexDirectionPanel = new SphericalUnitVectorPanel();
    private JCheckBox vortexRandomBox;

    public VortexInfluencePanel() {
        super();
        setLayout(new GridBagLayout());
        initPanel();
    }

    private void initPanel() {
        vortexTypeBox = new JComboBox(new String[] { "Cylinder", "Torus" });
        vortexTypeBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int type = vortexTypeBox.getSelectedIndex();
                ((SimpleParticleInfluenceFactory.BasicVortex) getEdittedInfluence())
                        .setType(type);
                vortexRadiusPanel
                        .setEnabled(type == SimpleParticleInfluenceFactory.BasicVortex.VT_TORUS);
                vortexHeightPanel
                        .setEnabled(type == SimpleParticleInfluenceFactory.BasicVortex.VT_TORUS);
            }
        });

        vortexRadiusPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ((SimpleParticleInfluenceFactory.BasicVortex) getEdittedInfluence())
                        .setRadius(vortexRadiusPanel.getFloatValue());
            }
        });

        vortexHeightPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ((SimpleParticleInfluenceFactory.BasicVortex) getEdittedInfluence())
                        .setHeight(vortexHeightPanel.getFloatValue());
            }
        });

        vortexDirectionPanel.setBorder(createTitledBorder(" DIRECTION "));
        vortexDirectionPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ((SimpleParticleInfluenceFactory.BasicVortex) getEdittedInfluence())
                        .getAxis()
                        .setDirection(vortexDirectionPanel.getValue());
            }
        });
        vortexStrengthPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ((SimpleParticleInfluenceFactory.BasicVortex) getEdittedInfluence())
                        .setStrength(vortexStrengthPanel.getFloatValue());
            }
        });
        vortexDivergencePanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ((SimpleParticleInfluenceFactory.BasicVortex) getEdittedInfluence())
                        .setDivergence(vortexDivergencePanel.getFloatValue()
                                * FastMath.DEG_TO_RAD);
            }
        });
        vortexRandomBox = new JCheckBox(new AbstractAction("Vary Randomly") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                ((SimpleParticleInfluenceFactory.BasicVortex) getEdittedInfluence())
                        .setRandom(vortexRandomBox.isSelected());
            }
        });

        setBorder(createTitledBorder(" VORTEX PARAMETERS "));
        add(createBoldLabel("Type:"), new GridBagConstraints(0, 0, 1, 1, 0,
                0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        add(vortexRadiusPanel, new GridBagConstraints(0, 1, 2, 1, 0.5, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 10, 5), 0, 0));
        add(vortexHeightPanel, new GridBagConstraints(0, 2, 2, 1, 0.5, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 10, 5), 0, 0));
        add(vortexTypeBox, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        add(vortexStrengthPanel, new GridBagConstraints(0, 3, 2, 1, 0.5, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 10, 5), 0, 0));
        add(vortexDivergencePanel, new GridBagConstraints(0, 4, 2, 1, 0.5, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 10, 5), 0, 0));
        add(vortexDirectionPanel, new GridBagConstraints(0, 5, 2, 1, 0.5, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 10, 5), 0, 0));
        add(vortexRandomBox, new GridBagConstraints(0, 6, 2, 1, 0.5, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
                        5, 5, 10, 5), 0, 0));
    }

    @Override
    public void updateWidgets() {
        SimpleParticleInfluenceFactory.BasicVortex vortex = (SimpleParticleInfluenceFactory.BasicVortex) getEdittedInfluence();
        vortexTypeBox.setSelectedIndex(vortex.getType());
        vortexHeightPanel.setValue(vortex.getHeight());
        vortexHeightPanel
                .setEnabled(vortex.getType() == SimpleParticleInfluenceFactory.BasicVortex.VT_TORUS);
        vortexRadiusPanel.setValue(vortex.getRadius());
        vortexRadiusPanel
                .setEnabled(vortex.getType() == SimpleParticleInfluenceFactory.BasicVortex.VT_TORUS);
        vortexDirectionPanel.setValue(vortex.getAxis().getDirection());
        vortexStrengthPanel.setValue(vortex.getStrength());
        vortexDivergencePanel.setValue(vortex.getDivergence()
                * FastMath.RAD_TO_DEG);
        vortexRandomBox.setSelected(vortex.isRandom());
    }
}
