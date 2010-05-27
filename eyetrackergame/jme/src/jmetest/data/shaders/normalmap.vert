attribute vec3 modelTangent;

varying vec3 viewDirection;
varying vec3 lightDirections[$NL$];
varying vec2 texcoords;

void main(void)
{
    gl_Position = ftransform();
    texcoords = (gl_TextureMatrix[0] * gl_MultiTexCoord0).xy;
    
    /* Get view and light directions in viewspace */
    vec3 localViewDirection = -(gl_ModelViewMatrix * gl_Vertex).xyz;
    
    /* Calculate tangent info - stored in attributes */
    vec3 normal = gl_NormalMatrix * gl_Normal;
    vec3 tangent = gl_NormalMatrix * modelTangent;
    vec3 binormal = cross( normal, tangent );
    
    /* Transform localViewDirection into texture space */
    viewDirection.x = dot( tangent, localViewDirection );
    viewDirection.y = dot( binormal, localViewDirection );
    viewDirection.z = dot( normal, localViewDirection );
    
	for(int i = 0; i < $NL$; i++) {
	    vec3 localLightDirection = gl_LightSource[i].position.xyz + localViewDirection;
        lightDirections[i].x = dot( tangent, localLightDirection );
        lightDirections[i].y = dot( binormal, localLightDirection );
        lightDirections[i].z = dot( normal, localLightDirection );
        lightDirections[i] = normalize( lightDirections[i] );
	} // for
} // main