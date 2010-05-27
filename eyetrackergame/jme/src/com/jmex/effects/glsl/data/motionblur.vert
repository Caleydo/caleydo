/*
 * Copyright (c) 2003-2006 jMonkeyEngine
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

uniform mat4 prevModelViewMatrix;
uniform mat4 prevModelViewProjectionMatrix;
uniform vec2 halfWinSize;
uniform float blurStrength;

varying vec4 viewCoords;
varying vec2 velocity;

void main(void)
{
	// transform previous and current pos to eye space
	vec4 P = gl_ModelViewMatrix * gl_Vertex;
	vec4 Pprev = prevModelViewMatrix * gl_Vertex;  //TODO: should be previous coord
	
	// transform normal to eye space
	vec3 N = gl_NormalMatrix * gl_Normal;

	// calculate eye space motion vector
	vec3 motionVector = P.xyz - Pprev.xyz;

	// calculate clip space motion vector
	vec4 addNormal = vec4(gl_Normal,0.0)*vec4(0.2);
	P = gl_ModelViewProjectionMatrix * (gl_Vertex+addNormal);
	Pprev = prevModelViewProjectionMatrix * (gl_Vertex+addNormal);  //TODO: should be previous coord

	// choose previous or current position based
	// on dot product between motion vector and normal
	bool flag = dot(motionVector, N) > 0.0;
	vec4 Pstretch = flag ? P : Pprev;

	gl_Position = Pstretch;
	viewCoords = Pstretch;

	// do divide by W -> NDC coordinates
	P.xy = P.xy / P.w;
	Pprev.xy = Pprev.xy / Pprev.w;

	// calculate window space velocity
	velocity = halfWinSize.xy * (P.xy - Pprev.xy) * vec2(blurStrength);
}