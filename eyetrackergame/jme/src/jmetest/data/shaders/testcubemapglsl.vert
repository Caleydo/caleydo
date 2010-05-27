// modelToWorld matrix
uniform mat4 modelToWorld;

// Position of the eye in world space
uniform vec3 eyePosW;

// Reflected vector
varying vec3 R;

// normalmap vars
varying vec3 lightDirection;
varying vec3 viewDirection;

void main()
{
	// --- BEGIN BASE OPERATIONS --- //
    // Transform vertex
    gl_Position = ftransform();
    // Assign tex coord
    gl_TexCoord[0] = gl_TextureMatrix[0] * gl_MultiTexCoord0;
	// --- END BASE OPERATIONS --- //

	// --- BEGIN CODE FOR REFLECTION MAPPING --- //
    // Compute position and normal in world space
	vec3 positionW = (modelToWorld * gl_Vertex).xyz;
	vec3 N = (modelToWorld * vec4(gl_Normal,0)).xyz;
	N = normalize(N);
	
	// Compute the incident and reflected vectors
	vec3 I = positionW - eyePosW;
	R = reflect(I, N);
	// --- END CODE FOR REFLECTION MAPPING --- //

}