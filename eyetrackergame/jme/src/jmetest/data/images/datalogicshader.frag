uniform sampler2D baseTexture;
uniform vec3 positionOffset;

void main(void)
{
	vec4 baseColor = texture2D( baseTexture, gl_TexCoord[0].xy );
    gl_FragColor = baseColor * vec4(positionOffset.xyz, 1.0) * vec4(0.01);
}