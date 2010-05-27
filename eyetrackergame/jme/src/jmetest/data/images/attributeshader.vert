attribute vec4 vertexColors;
attribute float vertexOffset;

uniform float size;

void main(void)
{
    gl_FrontColor = vertexColors + gl_Color.bgra * vec4(0.2);

    gl_Position = gl_ModelViewProjectionMatrix * (gl_Vertex * vec4(size, size, size, 1.0) + vec4(gl_Normal, 0.0) * vec4(vec3(vertexOffset), 0.0) );
}