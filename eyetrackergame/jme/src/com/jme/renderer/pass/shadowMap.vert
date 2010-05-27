uniform mat4 inverseView;

void main(void){
    gl_TexCoord[0] = gl_TextureMatrix[0] * inverseView * (gl_ModelViewMatrix * gl_Vertex);
 
    gl_Position = ftransform();
}
