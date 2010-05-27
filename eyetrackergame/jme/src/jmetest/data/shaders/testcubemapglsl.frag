// Our base texture
uniform sampler2D baseTex;
// Our glossmap texture
uniform sampler2D glossMap;
// Cube map
uniform samplerCube cubeMapTex;
// Reflection factor
uniform float reflectionFactor;

// Reflected vector
varying vec3 R;

void main()
{
   gl_FragColor = mix(texture2D(baseTex, gl_TexCoord[0].st), textureCube(cubeMapTex, R), texture2D(glossMap, gl_TexCoord[0].st) * reflectionFactor);
}