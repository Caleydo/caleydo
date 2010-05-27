const float MULTIPLIER = (1.0 / 9.0) * 0.4;
// note: OFFSET will be added by the client code before upload of the
// shader since it's a constant dependent on a client value (the size of the shadow map)
// OFFSET = 0.5f / SHADOW_MAP_SIZE;

uniform sampler2DShadow shadowMap;

void main()
{   
	float shade = shadow2DProj(shadowMap, gl_TexCoord[0] + vec4(OFFSET * -1.0, OFFSET * -1.0, 0.0, 0.0)).r;
    shade      += shadow2DProj(shadowMap, gl_TexCoord[0] + vec4(OFFSET *  0.0, OFFSET * -1.0, 0.0, 0.0)).r;
    shade      += shadow2DProj(shadowMap, gl_TexCoord[0] + vec4(OFFSET *  1.0, OFFSET * -1.0, 0.0, 0.0)).r;
    shade      += shadow2DProj(shadowMap, gl_TexCoord[0] + vec4(OFFSET * -1.0, OFFSET *  0.0, 0.0, 0.0)).r;
    shade      += shadow2DProj(shadowMap, gl_TexCoord[0] + vec4(OFFSET *  0.0, OFFSET *  0.0, 0.0, 0.0)).r;
    shade      += shadow2DProj(shadowMap, gl_TexCoord[0] + vec4(OFFSET *  1.0, OFFSET *  0.0, 0.0, 0.0)).r;
    shade      += shadow2DProj(shadowMap, gl_TexCoord[0] + vec4(OFFSET * -1.0, OFFSET *  1.0, 0.0, 0.0)).r;
    shade      += shadow2DProj(shadowMap, gl_TexCoord[0] + vec4(OFFSET *  0.0, OFFSET *  1.0, 0.0, 0.0)).r;
    shade      += shadow2DProj(shadowMap, gl_TexCoord[0] + vec4(OFFSET *  1.0, OFFSET *  1.0, 0.0, 0.0)).r;
    shade *= MULTIPLIER;
    
    gl_FragColor = vec4(0,0,0,shade);
}
