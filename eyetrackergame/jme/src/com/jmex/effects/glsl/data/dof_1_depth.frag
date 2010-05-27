// "Depth of Field" demo for Ogre
// Copyright (C) 2006  Christian Lindequist Larsen
//
// This code is in the public domain. You may do whatever you want with it.

// dofParams coefficients:
// x = near blur depth; y = focal plane depth; z = far blur depth
// w = blurriness cutoff constant for objects behind the focal plane
uniform vec4 dofParams;

varying float depth; // in view space

uniform sampler2D mainTexture; 

void main()
{
	float f;
	vec4 texCol = texture2D(mainTexture,gl_TexCoord[0].st);
	
	if (depth < dofParams.y)
	{
		// scale depth value between near blur distance and focal distance to
		// [-1, 0] range
		f = (depth - dofParams.y) / (dofParams.y - dofParams.x);
	}
	else
	{
		// scale depth value between focal distance and far blur distance to
		// [0, 1] range
		f = (depth - dofParams.y) / (dofParams.z - dofParams.y);
		// clamp the far blur to a maximum blurriness
		f = clamp(f, 0.0, dofParams.w);
	}

	// scale and bias into [0, 1] range
	vec4 sum = vec4(0.5*f + 0.5);
	if (texCol.a<0.1)
	{
		texCol.a = 0.0;
	}
	sum.a = texCol.a;
	gl_FragColor = sum;
}
