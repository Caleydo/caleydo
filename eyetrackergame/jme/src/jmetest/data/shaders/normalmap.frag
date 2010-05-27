uniform sampler2D baseMap;
uniform sampler2D normalMap;
uniform sampler2D specularMap;

varying vec3 viewDirection;
varying vec3 lightDirections[$NL$];
varying vec2 texcoords;

void main(void)
{
	/* Extract colors from baseMap and specularMap */
	vec4  baseColor = texture2D( baseMap, texcoords );
    vec3  normal = normalize( ( texture2D( normalMap, texcoords ).xyz * 2.0 ) - 1.0 );
	vec4  specularColor = texture2D( specularMap, texcoords );
	
    vec3 normalizedViewDirection = normalize( viewDirection );
		
	/* Sum up lighting models with OpenGL provided light/material properties */
    vec4 totalAmbient = gl_LightModel.ambient * gl_FrontMaterial.ambient;  // init with global ambient
    vec4 totalDiffuse; 
    vec4 totalSpecular;

    // ----------------------- LIGHTS -----------------------
    for(int i = 0; i < $NL$; i++) {
        vec3 normalizedLightDirection = normalize( lightDirections[i] );
        float NDotL = dot( normal, normalizedLightDirection );
        vec3 reflection = normalize( ( ( 2.0 * normal ) * NDotL ) - normalizedLightDirection ); 
           
        /* Sum up lighting models with OpenGL provided light/material properties */
        totalAmbient  += gl_FrontLightProduct[i].ambient; 
        totalDiffuse  += clamp( gl_FrontLightProduct[i].diffuse * max( 0.0, NDotL ), 0.0, 1.0 ); 
        totalSpecular += gl_FrontLightProduct[i].specular * specularColor * ( pow( max( 0.0, dot( reflection, normalizedViewDirection ) ), gl_FrontMaterial.shininess ) );
    } // for

	/* Set final pixel color as sum of lighting models */
    gl_FragColor = totalAmbient * baseColor + totalDiffuse * baseColor + totalSpecular;
}