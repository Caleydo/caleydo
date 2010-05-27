varying vec4 viewCoords;
varying vec2 vTexCoord;

void main()
{
   vec2 Pos = sign(gl_Vertex.xy);
   gl_Position = vec4(Pos.xy, 0, 1);
   vTexCoord.x = 0.5 * (1.0 + Pos.x);
   vTexCoord.y = 0.5 * (1.0 + Pos.y);
   //viewCoords = gl_ModelViewProjectionMatrix * gl_Vertex;
}
