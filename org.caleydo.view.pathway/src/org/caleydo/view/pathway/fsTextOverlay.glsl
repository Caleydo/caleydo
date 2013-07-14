uniform sampler2D pathwayTex;
varying vec2 texCoord;

float threshold=0.8;
	
void main(void) {
	vec4 texValue=texture2D(pathwayTex, texCoord);
	
	//discard pixel if it is always white or has a too high alpha value
	if((texValue.r>threshold && texValue.g>threshold && texValue.b>threshold) || texValue.a<0.1){
		discard;
	}
			
	gl_FragColor = texValue;
} 