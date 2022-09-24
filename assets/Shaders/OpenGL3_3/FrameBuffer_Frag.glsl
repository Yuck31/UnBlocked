#version 330 core

//Input texCoords.
in vec2 frag_TexCoords;

//FrameBuffer Texture.
uniform sampler2D screenTexture;

//Output color.
out vec4 outputColor;

void main()
{ 
    outputColor = texture(screenTexture, frag_TexCoords);
    //outputColor = vec4(0.0, 1.0, 0.0, 1.0);
    //outputColor = vec4(frag_TexCoords, 0.0, 1.0);
}
