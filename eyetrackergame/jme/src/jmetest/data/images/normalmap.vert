//attribute vec3 modelTangent;
//attribute vec3 modelBinormal;

varying vec3 viewDirection;
varying vec3 lightDirection;

void main(void)
{
	/* Transform vertices and pass on texture coordinates */
	gl_Position = ftransform();
	gl_TexCoord[0] = gl_TextureMatrix[0] * gl_MultiTexCoord0 * vec4(4.0, 4.0, 1.0, 1.0);
	
	/* Transform vertex into viewspace */
	vec4 vertexViewSpace = gl_ModelViewMatrix * gl_Vertex;
	
	/* Get view and light directions in viewspace */
	vec3 localViewDirection = -vertexViewSpace.xyz;
	vec3 localLightDirection = gl_LightSource[0].position.xyz;
	
	/* Calculate tangent info - stored in colorbuffer */
	vec3 normal = gl_NormalMatrix * gl_Normal;
	vec3 tangent = gl_NormalMatrix * (gl_Color.xyz*2.0-1.0);
	vec3 binormal = cross( normal, tangent );

	/* Calculate tangent info - stored in attributes */
//	vec3 normal = gl_NormalMatrix * gl_Normal;
//	vec3 tangent = gl_NormalMatrix * modelTangent;
//	vec3 binormal = gl_NormalMatrix * modelBinormal;

	/* Calculate tangent info - stored in texturecoordinates */
//	vec3 normal = gl_NormalMatrix * gl_Normal;
//	vec3 tangent = gl_NormalMatrix * gl_MultiTexCoord1.xyz;
//	vec3 binormal = gl_NormalMatrix * gl_MultiTexCoord2.xyz;
	
	/* Transform localViewDirection into texture space */
	viewDirection.x = dot( tangent, localViewDirection );
	viewDirection.y = dot( binormal, localViewDirection );
	viewDirection.z = dot( normal, localViewDirection );

	/* Transform localLightDirection into texture space */
	lightDirection.x = dot( tangent, localLightDirection );
	lightDirection.y = dot( binormal, localLightDirection );
	lightDirection.z = dot( normal, localLightDirection );
}