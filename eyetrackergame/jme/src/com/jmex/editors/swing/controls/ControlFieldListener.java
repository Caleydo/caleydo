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

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.jme.input.KeyInput;
import com.jme.input.KeyInputListener;
import com.jme.input.MouseInput;
import com.jme.input.MouseInputListener;
import com.jme.input.controls.Binding;
import com.jme.input.controls.binding.JoystickAxisBinding;
import com.jme.input.controls.binding.JoystickButtonBinding;
import com.jme.input.controls.binding.KeyboardBinding;
import com.jme.input.controls.binding.MouseAxisBinding;
import com.jme.input.controls.binding.MouseButtonBinding;
import com.jme.input.joystick.Joystick;
import com.jme.input.joystick.JoystickInput;
import com.jme.input.joystick.JoystickInputListener;

/**
 * @author Matthew D. Hicks
 */
public class ControlFieldListener implements JoystickInputListener, MouseInputListener, KeyInputListener {
	private static final long DELAY = 500;
	
	private GameControlEditor editor;
	private long lastHit = 0;
	private ControlField field;
	private boolean hasBeenSet;
	private boolean disabled;
	
	public ControlFieldListener(GameControlEditor editor) {
		this.editor = editor;
	}
	
	public void prompt(ControlField field) {
		if ((!disabled) && (System.currentTimeMillis() > lastHit + DELAY)) {
			this.field = field;
			field.setText("Press a key");
			
			MouseInput.get().setCursorVisible(false);
			
			KeyInput.get().addListener(this);
			MouseInput.get().addListener(this);
			JoystickInput.get().addListener(this);
			
			hasBeenSet = false;
		}
	}
	
	public void onKey(char character, int keyCode, boolean pressed) {
		if (pressed) {
			if (keyCode == KeyInput.KEY_ESCAPE) {
				setBinding(null, false);
			} else if (keyCode == KeyInput.KEY_DELETE) {
				setBinding(null, true);
			} else {
				setBinding(new KeyboardBinding(keyCode), true);
			}
		}
	}

	public void onButton(int button, boolean pressed, int x, int y) {
		if (pressed) {
			setBinding(new MouseButtonBinding(button), true);
		}
	}

	public void onMove(int xDelta, int yDelta, int newX, int newY) {
		if ((xDelta == 0) && (yDelta == 0)) return;
		if ((Math.abs(xDelta) < GameControlEditor.MOUSE_THRESHOLD) && (Math.abs(yDelta) < GameControlEditor.MOUSE_THRESHOLD)) return;
		if (Math.abs(xDelta) > Math.abs(yDelta)) {
			// X change is greater
			if (xDelta > 0) {
				setBinding(new MouseAxisBinding(MouseAxisBinding.AXIS_X, false), true);
			} else {
				setBinding(new MouseAxisBinding(MouseAxisBinding.AXIS_X, true), true);
			}
		} else {
			// Y change is greater
			if (yDelta > 0) {
				setBinding(new MouseAxisBinding(MouseAxisBinding.AXIS_Y, false), true);
			} else {
				setBinding(new MouseAxisBinding(MouseAxisBinding.AXIS_Y, true), true);
			}
		}
	}

	public void onWheel(int wheelDelta, int x, int y) {
		if (wheelDelta > 0) {
			setBinding(new MouseAxisBinding(MouseAxisBinding.AXIS_W, false), true);
		} else if (wheelDelta < 0) {
			setBinding(new MouseAxisBinding(MouseAxisBinding.AXIS_W, true), true);
		}
	}

	public void onAxis(Joystick controller, int axis, float axisValue) {
		if (axisValue != 0.0f) {
			if (Math.abs(axisValue) < GameControlEditor.JOYSTICK_THRESHOLD) return;
			boolean reverse = (axisValue < 0.0f);
			setBinding(new JoystickAxisBinding(controller, axis, reverse), true);
		}
	}

	public void onButton(Joystick controller, int button, boolean pressed) {
		if (pressed) {
			setBinding(new JoystickButtonBinding(controller, button), true);
		}
	}
	
	private synchronized void setBinding(final Binding binding, final boolean set) {
		if (hasBeenSet) return;
		hasBeenSet = true;
		disabled = true;
		
		KeyInput.get().removeListener(this);
		MouseInput.get().removeListener(this);
		JoystickInput.get().removeListener(this);
		
		MouseInput.get().setCursorVisible(true);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (set) {
					ControlField boundField = null;
					for (ControlField[] fields : editor.controls.values()) {
						for (ControlField f : fields) {
							if ((f.getBinding() != null) && (f.getBinding().toString().equals(binding.toString()))) {
								boundField = f;
							}
						}
					}

					boolean shouldSet = true;
					if ((boundField != null) && (boundField.getControl() != field.getControl())) {
						if (JOptionPane.showInternalConfirmDialog(field, binding.toString() + " is already bound to " + boundField.getControl().getName() + ".\n\nDo you wish to replace it?", "Binding Already Exists", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
							shouldSet = false;
						}
					}
					
					if (shouldSet) {
						if (boundField != null) {
							boundField.setBinding(null);
						}
						field.setBinding(binding);
					} else {
						field.updateText();
					}
				}
				lastHit = System.currentTimeMillis();
				
				disabled = false;
			}
		});
	}
}
