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

package com.jmex.awt.swingui.dnd;

import java.awt.Component;
import java.awt.Point;

/**
 * @author Galun
 */
public class JMEDragSourceEvent {

    private Point point;
    private int action;
    private boolean dropSuccess;
    private boolean freeDrop;
    private Component target;

    /**
     * a class to capsule drag events delivered to the drag source
     * @param point the point the event occured
     * @param action the action (see DndConstants in swing)
     */
    public JMEDragSourceEvent(Point point, int action) {
        this(point, action, false, false);
    }

    /**
     * a class to capsule drag events delivered to the drag source
     * @param point the point the event occured
     * @param action the action (see DndConstants in swing)
     * @param success flag a successful dnd event
     */
    public JMEDragSourceEvent(Point point, int action, boolean success) {
        this(point, action, success, false);
    }

    /**
     * a class to capsule drag events delivered to the drag source
     * @param point the point the event occured
     * @param action the action (see DndConstants in swing)
     * @param target the target component this event occured
     */
    public JMEDragSourceEvent(Point point, int action, Component target) {
        this(point, action, false, false);
        this.target = target;
    }

    /**
     * a class to capsule drag events delivered to the drag source
     * @param point the point the event occured
     * @param action the action (see DndConstants in swing)
     * @param success flag a successful dnd event
     * @param freeDrop flag a drop outside of drop targets
     */
    public JMEDragSourceEvent(Point point, int action, boolean success, boolean freeDrop) {
        this.point = point;
        this.action = action;
        this.dropSuccess = success;
        this.freeDrop = freeDrop;
        this.target = null;
    }

    public Point getPoint() {
        return point;
    }

    public int getAction() {
        return action;
    }

    protected void setDropSuccess(boolean success) {
        dropSuccess = success;
    }

    public boolean getDropSuccess() {
        return dropSuccess;
    }

    /**
     * check if this is a drop outside of a drop target
     * @return true if the a drop was outside of a drop target
     */
    public boolean isFreeDrop() {
        return freeDrop;
    }

    /**
     * set the free drop flag
     * @param freeDrop TODO
     */
    public void setFreeDrop(boolean freeDrop) {
        this.freeDrop = freeDrop;
    }

    public String toString() {
        return "JMEDragSourceEvent[point=" + point.x + "/" + point.y + ", action=" + action +
        ", dropSuccess=" + dropSuccess + ", freeDrop=" + freeDrop + ", target=" + target + "]";
    }

    /**
     * @return the target component the drop occured
     */
    public Component getTarget() {
        return target;
    }
}
