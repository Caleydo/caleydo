/*
 * @(#)TreeLayoutSettings.java 1.0 12-JUL-2004
 * 
 * Copyright (c) 2004-2005, Gaudenz Alder
 * All rights reserved. 
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jgraph.plugins.layouts;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


/**
 * A Dialog for configuring a TreeLayoutAlgorithm.
 * You can use horizontal and vertical spacing.<br>
 *<br>
 *<br>
 * @author Gaudenz Alder<br>
 * @version 1.0 init
 */
public class MoenLayoutSettings extends JPanel implements JGraphLayoutSettings {

	protected MoenLayoutAlgorithm layout;
	
    private JComboBox orientationCombo = new JComboBox(new Object[]{"West-East", "North-South"});
    private JTextField nodeDistanceTextField = new JTextField();
    /**
     * Creates new form SugiyamaLayoutConfigurationDialog
     */
    public MoenLayoutSettings(MoenLayoutAlgorithm layout) {
    	this.layout = layout;
        JPanel jPanel1 = new javax.swing.JPanel(new GridLayout(2,2,4,4));
        jPanel1.add(new JLabel("Orientation"));
        jPanel1.add(orientationCombo);
        jPanel1.add(new JLabel("Node Distance"));
        jPanel1.add(nodeDistanceTextField);
        add(jPanel1, BorderLayout.CENTER);
        revert();
    }
    
	/**
	 * Implementation.
	 */
	public void revert() {
		setOrientation(layout.orientation);
		nodeDistanceTextField.setText(String.valueOf(layout.childParentDistance));
	}
    
    private void check() {
        try {
            Integer.parseInt(nodeDistanceTextField.getText());
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
	
	/**
	 * Implementation.
	 */
	public void apply() {
		check();
		layout.orientation = getOrientation();
		layout.childParentDistance = Integer.parseInt(nodeDistanceTextField.getText());
	}
    
    /**
     * Returns the value of the "Vertical spacing" as text.
     */
    public int getOrientation() {
    	int result = MoenLayoutAlgorithm.LEFT_TO_RIGHT;
    	switch (orientationCombo.getSelectedIndex()) {
    		case 1:
    			result = MoenLayoutAlgorithm.UP_TO_DOWN;
    	}
    	return result;
    }
    
    /**
     * Set the value of the "Vertical Spacing" text field.
     */
    public void setOrientation(int orientation) {
    	int index = 0;
    	switch (orientation) {
    		case MoenLayoutAlgorithm.UP_TO_DOWN:
    			index = 1;
    	}
    	orientationCombo.setSelectedIndex(index);
    }

}

