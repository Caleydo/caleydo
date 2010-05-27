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

uniform sampler2D depth;

uniform float normalMult;
uniform float depthMult;
uniform float off;

varying vec2 vTexCoord;

void main(void)
{
	vec4 s00 = texture2D(depth, vTexCoord + vec2(-off, -off));
	vec4 s01 = texture2D(depth, vTexCoord + vec2( 0,   -off));
	vec4 s02 = texture2D(depth, vTexCoord + vec2( off, -off));

	vec4 s10 = texture2D(depth, vTexCoord + vec2(-off,  0));
	vec4 s12 = texture2D(depth, vTexCoord + vec2( off,  0));

	vec4 s20 = texture2D(depth, vTexCoord + vec2(-off,  off));
	vec4 s21 = texture2D(depth, vTexCoord + vec2( 0,    off));
	vec4 s22 = texture2D(depth, vTexCoord + vec2( off,  off));

	vec4 sobelX = s00 + 2.0 * s10 + s20 - s02 - 2.0 * s12 - s22;
	vec4 sobelY = s00 + 2.0 * s01 + s02 - s20 - 2.0 * s21 - s22;

	vec4 edgeSqr = sobelX * sobelX + sobelY * sobelY;
	float col = 1.0 - dot(edgeSqr, vec4(normalMult,normalMult,normalMult,depthMult));

    gl_FragColor = vec4(col);
}