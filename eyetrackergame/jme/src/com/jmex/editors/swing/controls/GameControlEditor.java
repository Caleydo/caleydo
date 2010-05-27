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
package com.jmex.editors.swing.controls;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jme.input.controls.Binding;
import com.jme.input.controls.GameControl;
import com.jme.input.controls.GameControlManager;

/**
 * GameControlEditor is a panel to display and modify game control configuration.
 * 
 * @author Matthew D. Hicks
 */
public class GameControlEditor extends JPanel implements MouseListener {
	private static final long serialVersionUID = 1L;
	
	public static int MOUSE_THRESHOLD = 5;
	public static float JOYSTICK_THRESHOLD = 0.2f;

	private GameControlManager manager;
	private int bindings;
	protected HashMap<GameControl, ControlField[]> controls;
	private ControlFieldListener listener;
	
	public GameControlEditor(GameControlManager manager, int bindings) {
		this.manager = manager;
		this.bindings = bindings;
		controls = new HashMap<GameControl, ControlField[]>();
		init();
	}
	
	private void init() {
		listener = new ControlFieldListener(this);
		
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		GridBagConstraints constraints = new GridBagConstraints();
		for (String name : manager.getControlNames()) {
			GameControl control = manager.getControl(name);
			
			// Add the label
			JLabel label = new JLabel(name + ":");
			label.setHorizontalAlignment(JLabel.RIGHT);
			constraints.gridwidth = 1;
			constraints.anchor = GridBagConstraints.EAST;
			constraints.insets = new Insets(5, 5, 5, 5);
			layout.setConstraints(label, constraints);
			add(label);
			
			// Add bindings
			ControlField[] fields = new ControlField[bindings];
			for (int i = 0; i < fields.length; i++) {
				Binding binding = null;
				if (control.getBindings().size() > i) {
					binding = control.getBindings().get(i);
				}
				fields[i] = new ControlField(control, binding);
				fields[i].addMouseListener(this);
				
				if (i == fields.length - 1) {
					constraints.gridwidth = GridBagConstraints.REMAINDER;
				}
				layout.setConstraints(fields[i], constraints);
				add(fields[i]);
			}
			controls.put(control, fields);
		}
	}
	
	public void mouseClicked(MouseEvent evt) {
		if ((evt.getButton() == MouseEvent.BUTTON1) && (evt.getComponent() instanceof ControlField)) {
            ControlField field = (ControlField)evt.getComponent();
            listener.prompt(field);
		}
	}

	public void mouseEntered(MouseEvent evt) {
	}

	public void mouseExited(MouseEvent evt) {
	}

	public void mousePressed(MouseEvent evt) {
	}

	public void mouseReleased(MouseEvent evt) {
	}

	public void apply() {
		for (GameControl control : controls.keySet()) {
			control.clearBindings();	// Remove previous bindings
			for (ControlField field : controls.get(control)) {
				if (field.getBinding() != null) {
					// Set new binding back if not null
					control.addBinding(field.getBinding());
				}
			}
		}
	}
	
	public void reset() {
		for (GameControl control : controls.keySet()) {
			ControlField[] fields = controls.get(control);
			for (int i = 0; i < fields.length; i++) {
				if (control.getBindings().size() > i) {
					fields[i].setBinding(control.getBindings().get(i));
				} else {
					fields[i].setBinding(null);
				}
			}
		}
	}
	
	public void clear() {
		for (GameControl control : controls.keySet()) {
			ControlField[] fields = controls.get(control);
			for (int i = 0; i < fields.length; i++) {
				fields[i].setBinding(null);
			}
		}
	}
}