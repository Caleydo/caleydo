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

package com.jme.input.util;

import java.util.ArrayList;

import com.jme.input.InputHandler;
import com.jme.input.InputHandlerDevice;
import com.jme.input.action.InputActionInterface;

/**
 * 
 */
class UtilInputHandlerDevice extends InputHandlerDevice {

    public static final String DEVICE_UTIL = "Synthetic Input Device";

    public UtilInputHandlerDevice() {
        super(DEVICE_UTIL);
    }

    protected void createTriggers( InputActionInterface action, int axisIndex, int buttonIndex, boolean allowRepeats,
                                  InputHandler inputHandler) {
        if (axisIndex != InputHandler.AXIS_NONE) {
            if (axisIndex != InputHandler.AXIS_ALL) {
                SyntheticAxis axis = axes.get(axisIndex);
                axis.createTrigger(inputHandler, action, allowRepeats);
            } else {
                for (int i = axes.size() - 1; i >= 0; i--) {
                    SyntheticAxis axis = axes.get(i);
                    axis.createTrigger(inputHandler, action, allowRepeats);
                }
            }
        }
        if (buttonIndex != InputHandler.BUTTON_NONE) {
            if (buttonIndex != InputHandler.BUTTON_ALL) {
                SyntheticButton button = buttons.get(buttonIndex);
                button.createTrigger(inputHandler, action, allowRepeats);
            } else {
                for (int i = buttons.size() - 1; i >= 0; i--) {
                    SyntheticButton button = buttons.get(i);
                    button.createTrigger(inputHandler, action, allowRepeats);
                }
            }
        }
    }

    private static UtilInputHandlerDevice instance;

    /**
     * @return only instance of UtilInputHandlerDevice
     */
    static UtilInputHandlerDevice get() {
        if (instance == null) {
            instance = new UtilInputHandlerDevice();
            InputHandler.addDevice(instance);
        }
        return instance;
    }

    private ArrayList<SyntheticAxis> axes = new ArrayList<SyntheticAxis>();

    void addAxis( SyntheticAxis axis) {
        int index = axes.size();
        axes.add(axis);
        axis.setIndex(index);
    }

    void removeAxis( SyntheticAxis axis) {
        if (axes.get(axis.getIndex()) == axis) {
            axes.set(axis.getIndex(), null);
        }
    }

    private ArrayList<SyntheticButton> buttons = new ArrayList<SyntheticButton>();

    void addButton( SyntheticButton button) {
        int index = buttons.size();
        buttons.add(button);
        button.setIndex(index);
    }

    void removeButton( SyntheticButton button) {
        if (buttons.get(button.getIndex()) == button) {
            buttons.set(button.getIndex(), null);
        }
    }
}
