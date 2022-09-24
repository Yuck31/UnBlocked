#version 330 core

layout (location = 0) in vec2 attrib_Position;
layout (location = 1) in vec2 attrib_TexCoords;

out vec2 frag_TexCoords;

void main()
{
    gl_Position = vec4(attrib_Position.x, attrib_Position.y, 0.0, 1.0); 
    frag_TexCoords = attrib_TexCoords;
}
