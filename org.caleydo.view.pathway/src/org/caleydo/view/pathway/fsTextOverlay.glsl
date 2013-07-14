uniform sampler2D pathwayTex;
uniform int mode;
varying vec2 texCoord;

//https://github.com/ashi009/sc/blob/master/shader/fragment.glslf
vec3 rgb2hsv(vec3 c) {
	float M = max(max(c.r, c.g), c.b);
	float m = min(min(c.r, c.g), c.b);
	float C = M - m;
	float del = 60.0 / C;

	float h = 0.0;
	float s = M > 0.0 ? 1.0 - m / M : 0.0;
	float v = M;

	if (M == c.r) {
		h = (c.g - c.b) * del;
	if (h < 0.0) 
		h += 360.0;
	} else if (M == c.g)
		h = (c.b - c.r) * del + 120.0;
	else
		h = (c.r - c.g) * del + 240.0;

	return vec3(h, s, v);
}

vec3 hsv2rgb(vec3 c) {
	float H = c.x / 60.0;
	float Hi = H - 6.0 * floor(H / 6.0);
	float f = H - floor(H);

	float p = c.z * (1.0 - c.y);
	float q = c.z * (1.0 - f * c.y);
	float t = c.z * (1.0 - (1.0 - f) * c.y);

	if (Hi < 1.0)
		return vec3(c.z, t, p);
	else if (Hi < 2.0)
		return vec3(q, c.z, p);
	else if (Hi < 3.0)
		return vec3(p, c.z, t);
	else if (Hi < 4.0)
		return vec3(p, q, c.z);
	else if (Hi < 5.0)
		return vec3(t, p, c.z);
	else
		return vec3(c.z, p, q);
}

void main(void)
{
	vec4 texValue=texture2D(pathwayTex, texCoord);
	if (texValue.a < 0.1)
		discard;
	if (mode == 1) {//wiki pathways
		float threshold=0.8;
		if((texValue.r>threshold && texValue.g>threshold && texValue.b>threshold)){
			discard;
		}
	} else if (mode == 0) {//kegg
		//rgb(191,255,191) or hsv(120,25,100) (hsv range: 360,100,100)
		vec3 hsv = rgb2hsv(texValue.rgb);
		float hDelta = abs(hsv.x - 120);
		float sDelta = abs(hsv.y - 0.25);
		float vDelta = abs(hsv.z - 1); 
		if(hDelta < 5 && sDelta < 0.1 && vDelta < 0.2) //dicard matching ones
			discard;
		else if (hDelta < 35 && sDelta < 0.15 && vDelta < 0.2) { //blend similar ones
			vec3 rgb2 = hsv2rgb(vec3(hsv.x,0,hsv.z));
			texValue.rgb = rgb2;
		}
	}
		
	gl_FragColor = texValue;
} 