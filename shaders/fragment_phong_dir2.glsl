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
//Torch attenuation properties
uniform float spotAttenuation;
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
    // Compute the s, v and r vectors
    vec3 s = normalize(view_matrix*vec4(lightPos,0)).xyz;
    vec3 v = normalize(-viewPosition.xyz);
    vec3 r = normalize(reflect(-s,m));
    
    vec3 ambient = ambientIntensity*ambientCoeff;

    //remember all normals you use should be unnormalised.
    vec3 vp = viewPosition.xyz; //unnormalised
    //torch cutoff
    vec3 sTorch = torchPos-vp; //torchPos is 0,0,0
    float theta = dot(normalize(torchDir), normalize(-sTorch)); //cosine of the angle between direction of torch and the s vector. torchDir is 0,0,-1
    vec3 diffuseTorch;
    //calculating specular for torch
    vec3 rTorch = reflect(-sTorch,m);
    vec3 specTorch;

    //spot attenuation
    float spotAtten = pow(theta, spotAttenuation);
    //distance attenuation
    float dist = length(vp);
    float distAtten = 1.f/(constAttenuation + linearAttenuation * dist + quadAttenuation * dist * dist);
    //culling the light outside the cutoff
    if(theta > torchCutoffCos) {
        diffuseTorch = /*max(torchIntensity * dot(normalize(sTorch), normalize(m)),0) */ torchIntensity * spotAtten * distAtten / 1.5;
        specTorch = max(torchIntensity * pow(dot(rTorch,v),phongExp),0.0) * spotAtten * distAtten;
    }
    else {
        diffuseTorch = vec3(0);
        specTorch = vec3(0);
    }
    //diffuse for sun
    vec3 diffuse = max(lightIntensity*diffuseCoeff*dot(m,s), 0.0) + diffuseTorch;
    //specular for 'sun'
    vec3 specular;
    // Only show specular reflections for the front face
    if (dot(m,s) > 0)
        specular = max(lightIntensity*specularCoeff*pow(dot(r,v),phongExp), 0.0);
    else
        specular = vec3(0);
    
    vec4 ambientAndDiffuse = vec4(ambient + diffuse, 1);
    
    outputColor = ambientAndDiffuse*input_color*texture(tex, texCoordFrag) + vec4(specular, 1);
}
