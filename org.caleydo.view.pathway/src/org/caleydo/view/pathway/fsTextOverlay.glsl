uniform sampler2D pathwayTex;
varying vec2 texCoord;

void main(void)
{
	vec4 texValue=texture2D(pathwayTex, texCoord);
	float threshold=0.8;
	if((texValue.r>threshold && texValue.g>threshold && texValue.b>threshold) || texValue.a<0.1){
		//texValue = vec4(1.0,0.0,0.0,1.0);
		discard;
	}else {
		//texValue = vec4(texValue.r -0.1,texValue.g-0.1, texValue.b-0.1,1.0);
	}
		
	gl_FragColor = texValue;
} 