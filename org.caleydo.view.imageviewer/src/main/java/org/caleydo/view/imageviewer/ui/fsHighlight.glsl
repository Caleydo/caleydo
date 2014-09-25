uniform sampler2D tex;
varying vec2 texCoord;

void main(void)
{
  vec4 texValue=texture2D(tex, texCoord);
  if (texValue.a < 0.05)
    discard;

  gl_FragDepth = 0.1;
  gl_FragColor = texValue;
}