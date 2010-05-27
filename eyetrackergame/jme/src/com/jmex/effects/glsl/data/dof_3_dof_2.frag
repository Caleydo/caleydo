// "Depth of Field" demo for Ogre
// Copyright (C) 2006  Christian Lindequist Larsen
//
// This code is in the public domain. You may do whatever you want with it.

uniform float sampleDist0;
uniform sampler2D scene;				// full resolution image
uniform sampler2D depth;				// full resolution image with depth values

varying vec2 vTexCoord;


void main()
{

   vec2 samples00 = vec2(-0.326212, -0.405805);
   vec2 samples01 = vec2(-0.840144, -0.073580);
   vec2 samples02 = vec2(-0.695914,  0.457137);
   vec2 samples03 = vec2(-0.203345,  0.620716);
   vec2 samples04 = vec2( 0.962340, -0.194983);
   vec2 samples05 = vec2( 0.473434, -0.480026);
   vec2 samples06 = vec2( 0.519456,  0.767022);
   vec2 samples07 = vec2( 0.185461, -0.893124);
   vec2 samples08 = vec2( 0.507431,  0.064425);
   vec2 samples09 = vec2( 0.896420,  0.412458);
   vec2 samples10 = vec2(-0.321940, -0.932615);
   vec2 samples11 = vec2(-0.791559, -0.597705);

   vec2 newCoord;
   vec4 sum = texture2D(scene, vTexCoord);

	 
   vec4 d = texture2D(depth, vTexCoord);
  
   float _sampleDist0 = (d.r)*sampleDist0;
  
   float additionCount = 1.0;
	
   vec4 dCheck = vec4(0.0, 0.0, 0.0, 0.0);
   newCoord = vTexCoord + _sampleDist0 * samples00;
   dCheck = texture2D(depth, newCoord);
   if (abs(dCheck.r-d.r)<0.1)
   {
    	sum += texture2D(scene, newCoord);
    	additionCount = additionCount+1.0;
   }
   
   newCoord = vTexCoord + _sampleDist0 * samples01;
   dCheck = texture2D(depth, newCoord);
   if (abs(dCheck.r-d.r)<0.1)
   {
    	sum += texture2D(scene, newCoord);
    	additionCount = additionCount+1.0;
   }

   newCoord = vTexCoord + _sampleDist0 * samples02;
   dCheck = texture2D(depth, newCoord);
   if (abs(dCheck.r-d.r)<0.1)
   {
    	sum += texture2D(scene, newCoord);
    	additionCount = additionCount+1.0;
   }

   newCoord = vTexCoord + _sampleDist0 * samples03;
   dCheck = texture2D(depth, newCoord);
   if (abs(dCheck.r-d.r)<0.1)
   {
    	sum += texture2D(scene, newCoord);
    	additionCount = additionCount+1.0;
   }

   newCoord = vTexCoord + _sampleDist0 * samples04;
   dCheck = texture2D(depth, newCoord);
   if (abs(dCheck.r-d.r)<0.1)
   {
    	sum += texture2D(scene, newCoord);
    	additionCount = additionCount+1.0;
   }

   newCoord = vTexCoord + _sampleDist0 * samples05;
   dCheck = texture2D(depth, newCoord);
   if (abs(dCheck.r-d.r)<0.1)
   {
    	sum += texture2D(scene, newCoord);
    	additionCount = additionCount+1.0;
   }

   newCoord = vTexCoord + _sampleDist0 * samples06;
   dCheck = texture2D(depth, newCoord);
   if (abs(dCheck.r-d.r)<0.1)
   {
    	sum += texture2D(scene, newCoord);
    	additionCount = additionCount+1.0;
   }

   newCoord = vTexCoord + _sampleDist0 * samples07;
   dCheck = texture2D(depth, newCoord);
   if (abs(dCheck.r-d.r)<0.1)
   {
    	sum += texture2D(scene, newCoord);
    	additionCount = additionCount+1.0;
   }

   newCoord = vTexCoord + _sampleDist0 * samples08;
   dCheck = texture2D(depth, newCoord);
   if (abs(dCheck.r-d.r)<0.1)
   {
    	sum += texture2D(scene, newCoord);
    	additionCount = additionCount+1.0;
   }

   newCoord = vTexCoord + _sampleDist0 * samples09;
   dCheck = texture2D(depth, newCoord);
   if (abs(dCheck.r-d.r)<0.1)
   {
    	sum += texture2D(scene, newCoord);
    	additionCount = additionCount+1.0;
   }

   newCoord = vTexCoord + _sampleDist0 * samples10;
   dCheck = texture2D(depth, newCoord);
   if (abs(dCheck.r-d.r)<0.1)
   {
    	sum += texture2D(scene, newCoord);
    	additionCount = additionCount+1.0;
   }

   newCoord = vTexCoord + _sampleDist0 * samples11;
   dCheck = texture2D(depth, newCoord);
   if (abs(dCheck.r-d.r)<0.1)
   {
    	sum += texture2D(scene, newCoord);
    	additionCount = additionCount+1.0;
   }

   sum /= additionCount;
   sum.a = d.r*2.0;
   gl_FragColor =  sum;
		
}
