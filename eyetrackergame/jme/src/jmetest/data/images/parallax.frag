uniform sampler2D baseMap;
uniform sampler2D normalMap;
uniform sampler2D specularMap;

varying vec3 viewDirection;
varying vec3 lightDirection;

uniform float heightValue;

void main(void)
{
	/* Normalize view and light directions(need per-pixel normalized length) */
	vec3 normalizedViewDirection = normalize( viewDirection );
	vec3 normalizedLightDirection = normalize( lightDirection );

	/* Extract colors from baseMap and specularMap */
//	vec4  baseColor      = texture2D( baseMap, gl_TexCoord[0].xy );
	vec4  specularColor  = texture2D( specularMap, gl_TexCoord[0].xy );

	float height = length(specularColor.xyz) * heightValue - heightValue * 0.5;
	vec2 newTexcoord = gl_TexCoord[0].xy - normalizedViewDirection.xy * height;
	vec4 baseColor = texture2D( baseMap, newTexcoord );

	
	/* Calculate diffuse - Extract and expand normal and calculate dot angle to lightdirection */
	vec3  normal = normalize( ( texture2D( normalMap, gl_TexCoord[0].xy ).xyz * 2.0 ) - 1.0 );
	float NDotL = dot( normal, normalizedLightDirection ); 
	   
	/* Calculate specular - Calculate reflection vector and dot angle to viewdirection  */
	vec3  reflection = normalize( ( ( 2.0 * normal ) * NDotL ) - normalizedLightDirection ); 
	float RDotV = max( 0.0, dot( reflection, normalizedViewDirection ) );
	   	   
	/* Sum up lighting models with OpenGL provided light/material properties */
	vec4  totalAmbient   = ( gl_FrontLightModelProduct.sceneColor + gl_FrontLightProduct[0].ambient ) * baseColor; 
	vec4  totalDiffuse   = gl_FrontLightProduct[0].diffuse * max( 0.0, NDotL ) * baseColor; 
	vec4  totalSpecular  = gl_FrontLightProduct[0].specular * specularColor * ( pow( RDotV, gl_FrontMaterial.shininess ) );	

	/* Sum up lighting models with hardcoded lighting properties(for debugging) */
//	vec4  totalAmbient   = vec4(0.2, 0.2, 0.2, 1.0) * baseColor; 
//	vec4  totalDiffuse   = vec4(1.0, 1.0, 1.0, 1.0) * max( 0.0, NDotL ) * baseColor; 
//	vec4  totalSpecular  = vec4(1.0, 1.0, 1.0, 1.0) * specularColor * ( pow( RDotV, 25.0 ) );	

	/* Set final pixel color as sum of lighting models */
    gl_FragColor = totalAmbient + totalDiffuse + totalSpecular;
}