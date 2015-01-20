#version 150

uniform sampler2D pathwayTex;
varying vec2 texCoord;

void main(void)
{
	vec4 texValue=texture2D(pathwayTex, texCoord);
	if (texValue.a < 0.1)
		discard;
	float threshold=0.8;
	
	if((texValue.r>threshold && texValue.g>threshold && texValue.b>threshold)){
		discard;
	}
		
	gl_FragColor = texValue;
} 