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
import javax.swing.SwingConstants;


/**
 * A Dialog for configuring a TreeLayoutAlgorithm.
 * You can use horizontal and vertical spacing.<br>
 *<br>
 *<br>
 * @author Gaudenz Alder<br>
 * @version 1.0 init
 */
public class TreeLayoutSettings extends JPanel implements JGraphLayoutSettings {

	protected TreeLayoutAlgorithm layout;
	
    private JComboBox alignmentCombo = new JComboBox(new Object[]{"Top", "Center", "Bottom"});
    private JComboBox orientationCombo = new JComboBox(new Object[]{"North", "East", "South", "West"});
    private JTextField nodeDistanceTextField = new JTextField(),
					   levelDistanceTextField = new JTextField();

    /**
     * Creates new form SugiyamaLayoutConfigurationDialog
     */
    public TreeLayoutSettings(TreeLayoutAlgorithm layout) {
    	this.layout = layout;
        JPanel jPanel1 = new javax.swing.JPanel(new GridLayout(4,2,4,4));
        jPanel1.add(new JLabel("Alignment"));
        jPanel1.add(alignmentCombo);
        jPanel1.add(new JLabel("Orientation"));
        jPanel1.add(orientationCombo);
        jPanel1.add(new JLabel("Node Distance"));
        jPanel1.add(nodeDistanceTextField);
        jPanel1.add(new JLabel("Level Distance"));
        jPanel1.add(levelDistanceTextField);
        add(jPanel1, BorderLayout.CENTER);
        revert();
    }
    
	/**
	 * Implementation.
	 */
	public void revert() {
		setAlignment(layout.getAlignment());
		setOrientation(layout.getOrientation());
		nodeDistanceTextField.setText(String.valueOf(layout.getNodeDistance()));
		levelDistanceTextField.setText(String.valueOf(layout.getLevelDistance()));
	}
    
    private void check() {
        try {
            Integer.parseInt(nodeDistanceTextField.getText());
            Integer.parseInt(levelDistanceTextField.getText());
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
	
	/**
	 * Implementation.
	 */
	public void apply() {
		check();
		layout.setAlignment(getAlignment());
		layout.setOrientation(getOrientation());
		layout.setNodeDistance(Integer.parseInt(nodeDistanceTextField.getText()));
		layout.setLevelDistance(Integer.parseInt(levelDistanceTextField.getText()));
	}
	
    /**
     * Returns the value of the "Horizontal spacing" as text.
     */
    public int getAlignment() {
    	int result = SwingConstants.TOP;
    	switch (alignmentCombo.getSelectedIndex()) {
    		case 1:
    			result = SwingConstants.CENTER;
    			break;
    		case 2:
    			result = SwingConstants.BOTTOM;
    	}
    	return result;
    }

    /**
     * Set the value of the "Horizontal spacing" text field.
     */
    public void setAlignment(int alignment) {
    	int index = 0;
    	switch (alignment) {
    		case SwingConstants.CENTER:
    			index = 1;
    			break;
    		case SwingConstants.BOTTOM:
    			index = 2;
    	}
    	alignmentCombo.setSelectedIndex(index);
    }
    
    /**
     * Returns the value of the "Vertical spacing" as text.
     */
    public int getOrientation() {
    	int result = SwingConstants.NORTH;
    	switch (orientationCombo.getSelectedIndex()) {
    		case 1:
    			result = SwingConstants.EAST;
    			break;
    		case 2:
    			result = SwingConstants.SOUTH;
    			break;
    		case 3:
    			result = SwingConstants.WEST;
    	}
    	return result;
    }
    
    /**
     * Set the value of the "Vertical Spacing" text field.
     */
    public void setOrientation(int orientation) {
    	int index = 0;
    	switch (orientation) {
    		case SwingConstants.EAST:
    			index = 1;
    			break;
    		case SwingConstants.SOUTH:
    			index = 2;
    			break;
    		case SwingConstants.WEST:
    			index = 3;
    	}
    	orientationCombo.setSelectedIndex(index);
    }

}

