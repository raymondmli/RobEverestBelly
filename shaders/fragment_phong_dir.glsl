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
    

    //NOTE: in my comments, I refer to the various pixels that make up the terrain and objects lying on the terrain as fragments.

    // Computing the angle from the centre of torch beam
    vec3 vp = (view_matrix*viewPosition).xyz; //viewPosition coordinate in camera coordinates. Remember that viewPosition is the global coordinate for every fragment of our world.
    vec3 sTorch = normalize(vp - torchPos); //vector of the light to the world from the torch.
    float torchBeta = dot(-sTorch, torchDir); //Cosine of the angle between the vector from sourceVector and the centre of the torchLight which is the direction of the torch. Imagine torchlight being a cone with torchDir being the centre vector from that cone, the source vector(s) being the 'light beams' that form the cone and the torchBeta being the angle between the source vector and the centre vector. See close to end of week 5 lecture slides for a nice diagram and almost 1.5 hours into the week 5 lecture for a brief explanation.
    //SIDENOTE: I've actually forgotten to normalise the vectors in torchBeta, but I don't think it particularly matters if the code is this broken.

    float dist = length(vp - torchPos); //distance between surface to torchPos
    //ATTENUATION CODE BELOW IGNORE TILL TORCH IS FIXED
    float distAttenuation = 1.f/(constAttenuation
                                 + linearAttenuation*dist
                                 + quadAttenuation*dist*dist);
    float spotAttenuation = pow(torchBeta,torchAttenuation);
    float totalAttenuation = 1;//spotAttenuation;//*distAttenuation;
    //ATTENUATION CODE ABOVE IGNORE TILL TORCH IS FIXED


    vec3 sTorchActual; //To get a nice cone, we cannot take every single source vector/light beam from the torch; we must cull the source vectors that lie outside the cone. We cull them if the angle between them and the direction vector (the centre vector) is larger than the cutoff angle. Which is what the below bit does.
    // Compute the s, v and r vectors of torch
    if(torchBeta > torchCutoffCos) //source vector stays if within angle. Remember torchBeta and torchCutoffCos  are the cosines of their respective angles
        sTorchActual = normalize(vp - torchPos).xyz;
    else //else it goes.
        sTorchActual = vec3(0);

    vec3 vTorch = normalize(-viewPosition.xyz); //view vector which is vector from the fragments to the camera. Pretty sure this value is wrong.
    vec3 rTorch = normalize(reflect(-sTorchActual,m)); //source vector of the torch refected across the normal of the fragment.
    //various light levels for the torch
    //Just plugging in the relevant vectors into the relevant equations.
    vec3 diffTorch = max(torchIntensity*diffuseCoeff*dot(m,sTorchActual), 0.0);
    vec3 specTorch;
    if (dot(m,sTorchActual) > 0)
        specTorch = max(torchIntensity*specularCoeff*pow(dot(rTorch,vTorch),phongExp), 0.0);
    else
        specTorch = vec3(0);
    //Adding the light from the torch and the light from the 'sun' up.
    specular += specTorch;
    diffuse += diffTorch;
    vec4 ambientAndDiffuse = vec4(ambient + diffuse, 1);
    
    outputColor = ambientAndDiffuse*input_color*texture(tex, texCoordFrag) + vec4(specular, 1);
}
