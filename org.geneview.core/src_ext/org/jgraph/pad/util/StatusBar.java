/*
 * Copyright (C) 2001-2004 Gaudenz Alder
 *
 * 6/01/2006: I, Raphpael Valyi, changed back the header of this file to LGPL
 * because nobody changed the file significantly since the last
 * 3.0 version of GPGraphpad that was LGPL. By significantly, I mean: 
 *  - less than 3 instructions changes could honnestly have been done from an old fork,
 *  - license or copyright changes in the header don't count
 *  - automaticaly updating imports don't count,
 *  - updating systematically 2 instructions to a library specification update don't count.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package org.jgraph.pad.util;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class StatusBar extends JPanel {
	/** contains the message at the current
	 *  Status bar
	 */
	protected JLabel message;
	
	/** contains the scale for the current
	 *  graph
	 */
	protected JLabel scale;
	
	/**
	 * Constructor for StatusBar.
	 *
	 */
	public StatusBar() {
		super();
		setLayout(new BorderLayout());
		message = new JLabel("Ready.");
		scale = new JLabel("100%");
		message.setBorder(BorderFactory.createLoweredBevelBorder());
		scale.setBorder(BorderFactory.createLoweredBevelBorder());
		add(message, BorderLayout.CENTER);
		add(scale, BorderLayout.EAST);
	}
	/**
	 * Returns the message.
	 *
	 * @return The message from the status bar
	 */
	public String getMessage() {
		return message.getText() ;
	}
	
	/**
	 * Returns the scale.
	 * @return JLabel
	 */
	public String getScale() {
		return scale.getText() ;
	}
	
	/**
	 * Sets the message.
	 * @param message The message to set
	 */
	public void setMessage(String message) {
		this.message.setText(message);
	}
	
	/**
	 * Sets the scale.
	 * @param scale The scale to set
	 */
	public void setScale(String scale) {
		this.scale.setText(scale);
	}
	
}
