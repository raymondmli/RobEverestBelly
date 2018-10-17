//OLD SHADER
out vec4 outputColor;

uniform vec4 input_color;

uniform mat4 view_matrix;

// Light properties
uniform vec3 lightPos;
uniform vec3 lightIntensity;
uniform vec3 ambientIntensity;

//Torch properties
uniform vec3 torchPos;
uniform vec3 torchDir;
uniform vec3 torchIntensity;
uniform float torchCutoffCos;
//attenuation factors
uniform float torchAttenuation;
uniform float constAttenuation;
uniform float linearAttenuation;
uniform float quadAttenuation;

// Material properties
uniform vec3 ambientCoeff;
uniform vec3 diffuseCoeff;
uniform vec3 specularCoeff;
uniform float phongExp;

uniform sampler2D tex;

in vec4 viewPosition;
in vec3 m;

in vec2 texCoordFrag;

void main()
{
    // Compute the s, v and r vectors of "sun"
    vec3 s = normalize(view_matrix*vec4(lightPos,0)).xyz;
    vec3 v = normalize(-viewPosition.xyz);
    vec3 r = normalize(reflect(-s,m));
    
    vec3 ambient = ambientIntensity*ambientCoeff;
    vec3 diffuse = max(lightIntensity*diffuseCoeff*dot(m,s), 0.0);
    vec3 specular;
    
    // Only show specular reflections for the front face
    if (dot(m,s) > 0)
        specular = max(lightIntensity*specularCoeff*pow(dot(r,v),phongExp), 0.0);
    else
        specular = vec3(0);
    
    //TEST
  //  vec3 directionTorch = normalize(view_matrix*vec4(torchDir, 0)).xyz;
    
    // Computing the angle from the centre of torch beam
    vec3 vp = (view_matrix*viewPosition).xyz; //non-homogeneous viewPosition coordinate
    vec3 sTorch = normalize(vp - torchPos);
    float torchBeta = dot(-sTorch, torchDir); //is in cos form
//     float torchBeta = dot(-sTorch, directionTorch); //is in cos form

    float dist = length(vp - torchPos); //distance between surface to torchPos
    
    float distAttenuation = 1.f/(constAttenuation
                                 + linearAttenuation*dist
                                 + quadAttenuation*dist*dist);
    float spotAttenuation = pow(torchBeta,torchAttenuation);
    float totalAttenuation = 1;//spotAttenuation;//*distAttenuation;
    
    
    vec3 sTorchActual;
    // Compute the s, v and r vectors of torch
    if(torchBeta > torchCutoffCos)
        sTorchActual = normalize(vp - torchPos).xyz;
    else
        sTorchActual = vec3(0);
    
    vec3 vTorch = normalize(-viewPosition.xyz);
    vec3 rTorch = normalize(reflect(-sTorchActual,m));
    //various light levels for the torch
    vec3 diffTorch = max(torchIntensity*diffuseCoeff*dot(m,sTorchActual), 0.0);
    vec3 specTorch;
    if (dot(m,sTorchActual) > 0)
        specTorch = max(torchIntensity*specularCoeff*pow(dot(rTorch,vTorch),phongExp), 0.0);
    else
        specTorch = vec3(0);
    
    specular += specTorch;
    diffuse += diffTorch;
    vec4 ambientAndDiffuse = vec4(ambient + diffuse, 1);
    
    outputColor = ambientAndDiffuse*input_color*texture(tex, texCoordFrag) + vec4(specular, 1);
}
