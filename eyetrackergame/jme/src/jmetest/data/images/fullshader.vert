uniform float floatVal;
uniform vec2 vec2Val;
uniform vec3 vec3Val;
uniform vec4 vec4Val;

uniform int intVal;
uniform ivec2 ivec2Val;
uniform ivec3 ivec3Val;
uniform ivec4 ivec4Val;

uniform bool boolVal;
uniform bvec2 bvec2Val;
uniform bvec3 bvec3Val;
uniform bvec4 bvec4Val;

uniform mat2 mat2Val;
uniform mat3 mat3Val;
uniform mat4 mat4Val;


void main(void)
{
    gl_FrontColor = vec4(floatVal + vec2Val.x + vec3Val.x + vec4Val.x +
                         float(intVal) + float(ivec2Val.x) + float(ivec3Val.x) + float(ivec4Val.x) +
                         mat2Val[0][0] + mat3Val[0][0] + mat4Val[0][0]);

    if (boolVal == true || bvec2Val.x == true || bvec3Val.x == true || bvec4Val.x == true) {
        gl_FrontColor = vec4(1.0, 0.0, 0.0, 1.0);
    }

    gl_Position = ftransform();
}