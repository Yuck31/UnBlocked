#version 330 core

//Vertex Attributes.
layout(location = 0) in vec3 attrib_Position;
layout(location = 1) in vec4 attrib_Color;

//View Matrix.
uniform mat4 uView;

//Fragment Values.
//out vec3 frag_Position;
out vec4 frag_Color;

void main()
{
    gl_Position = uView * vec4(attrib_Position.x, attrib_Position.y, attrib_Position.z, 1.0);

    //Send color to fragment shader.
    //frag_Position = attrib_Position;
    frag_Color = attrib_Color;
}
