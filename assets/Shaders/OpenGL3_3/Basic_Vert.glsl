#version 330 core//<- Says this shader is compatable with OpenGL 3.3 (core profile)

//Vertex Attributes.
layout(location = 0) in vec3 attrib_Position;
layout(location = 1) in vec4 attrib_Color;
layout(location = 2) in vec2 attrib_TexCoords;
layout(location = 3) in float attrib_TexID;

//Projection and View Matricies.
uniform mat4 uView;

//Fragment Values.
out vec3 frag_Position;
out vec4 frag_Color;
out vec2 frag_TexCoords;
out float frag_TexID;

void main()
{
    //vec3 pos = attrib_Position - cameraOffset;
    //gl_Position = vec4(pos, 1.0);

    //gl_Position = vec4(attrib_Position, 1.0);
    //gl_Position = vec4(attrib_Position.xy, 0.0, 1.0);
    //gl_Position = uView * vec4(attrib_Position.xyz, 1.0);
    gl_Position = uView * vec4(attrib_Position.x, attrib_Position.y, attrib_Position.z, 1.0);

    //Send all the atrribute data directly to the fragment shader (including position for lighting reasons).
    frag_Position = attrib_Position;
    frag_Color = attrib_Color;
    frag_TexCoords = attrib_TexCoords;
    frag_TexID = attrib_TexID;
}
