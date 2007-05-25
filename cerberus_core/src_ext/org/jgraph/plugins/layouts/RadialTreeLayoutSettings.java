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
import java.util.Properties;

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
public class RadialTreeLayoutSettings extends JPanel implements JGraphLayoutSettings {

	protected RadialTreeLayoutAlgorithm layout;
	
    private JTextField widthTextField = new JTextField(),
    				   heightTextField = new JTextField(),
    				   radiusXTextField = new JTextField(),
					   radiusYTextField = new JTextField(),
					   centerXTextField = new JTextField(),
					   centerYTextField = new JTextField();

    /**
     * Creates new form SugiyamaLayoutConfigurationDialog
     */
    public RadialTreeLayoutSettings(RadialTreeLayoutAlgorithm layout) {
    	this.layout = layout;
        JPanel jPanel1 = new javax.swing.JPanel(new GridLayout(6,2,4,4));
        jPanel1.add(new JLabel("Width"));
        jPanel1.add(widthTextField);
        jPanel1.add(new JLabel("Height"));
        jPanel1.add(heightTextField);
        // XOr
        jPanel1.add(new JLabel("Radius X"));
        jPanel1.add(radiusXTextField);
        jPanel1.add(new JLabel("Radius Y"));
        jPanel1.add(radiusYTextField);
        jPanel1.add(new JLabel("Center X"));
        jPanel1.add(centerXTextField);
        jPanel1.add(new JLabel("Center Y"));
        jPanel1.add(centerYTextField);
        add(jPanel1, BorderLayout.CENTER);
        revert();
    }
    
	/**
	 * Implementation.
	 */
	public void revert() {
		widthTextField.setText(String.valueOf(layout.WIDTH));
		heightTextField.setText(String.valueOf(layout.HEIGHT));
		if (layout.WIDTH == 0) {
			centerXTextField.setText(String.valueOf(layout.ROOTX));
			radiusXTextField.setText(String.valueOf(layout.RADIUSX));
		} else {
			centerXTextField.setText("0");
			radiusXTextField.setText("0");
		}
		if (layout.HEIGHT == 0) {
			centerYTextField.setText(String.valueOf(layout.ROOTY));
			radiusYTextField.setText(String.valueOf(layout.RADIUSY));
		} else {
			centerYTextField.setText("0");
			radiusYTextField.setText("0");
		}
	}
    
    private void check() {
        try {
        	Double.parseDouble(widthTextField.getText());
        	Double.parseDouble(heightTextField.getText());
        	Double.parseDouble(centerXTextField.getText());
        	Double.parseDouble(centerYTextField.getText());
        	Double.parseDouble(radiusXTextField.getText());
        	Double.parseDouble(radiusYTextField.getText());
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
	
	/**
	 * Implementation.
	 */
	public void apply() {
		check();
		Properties props = new Properties();
		props.put(RadialTreeLayoutAlgorithm.KEY_WIDTH, widthTextField.getText());
		props.put(RadialTreeLayoutAlgorithm.KEY_HEIGHT, heightTextField.getText());
		props.put(RadialTreeLayoutAlgorithm.KEY_CENTRE_X, centerXTextField.getText());
		props.put(RadialTreeLayoutAlgorithm.KEY_CENTRE_Y, centerYTextField.getText());
		props.put(RadialTreeLayoutAlgorithm.KEY_RADIUS_X, radiusXTextField.getText());
		props.put(RadialTreeLayoutAlgorithm.KEY_RADIUS_Y, radiusYTextField.getText());
		layout.setConfiguration(props);
	}

}

