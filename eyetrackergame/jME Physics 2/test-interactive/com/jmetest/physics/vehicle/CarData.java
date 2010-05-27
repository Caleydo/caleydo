/*
 * Copyright (c) 2005-2007 jME Physics 2
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of 'jME Physics 2' nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
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

package com.jmetest.physics.vehicle;

import com.jme.math.Vector3f;

public interface CarData {
    // You can use a model of yourself here, but remember to calibrate tha CHASSIS_SCALE properly.
    public static final String CHASSIS_MODEL = "radical.jme";
    public static final float CHASSIS_MASS = 1;
    public static final Vector3f CHASSIS_SCALE = new Vector3f( 7, 6.5f, 7 );

    public static final float SUSPENSION_MASS = 1f;
    public static final float SUSPENSION_COURSE = 1f;
    public static final float SUSPENSION_STIFFNESS = 10;
    public static final float SUSPENSION_RESISTANCE = 15;
    public static final Vector3f FRONT_SUSPENSION_OFFSET = new Vector3f( 10, -1.75f, 3.25f );
    public static final Vector3f REAR_SUSPENSION_OFFSET = new Vector3f( -9.5f, -1.75f, 3.25f );

    // You can also use your own wheel model here, but remember to calibrate the scale as well.
    public static final String WHEEL_MODEL = "wheel2.jme";
    public static final float WHEEL_MASS = 0.25f;
    public static final float WHEEL_SPEED = 100;
    public static final float WHEEL_ACCEL = 25;
    public static final float WHEEL_Z_OFFSET = 3.5f;
    public static final float WHEEL_SCALE = 3;

    // The fancy smoke effect. Made with Ren's particle editor.
    public static final String SMOKE_MODEL = "smoke.jme";
    // If you change the chassis model, you'll want to calibrate this offset also.
    public static final Vector3f SMOKE_OFFSET = new Vector3f( -15, -1.75f, -4.75f );
}
