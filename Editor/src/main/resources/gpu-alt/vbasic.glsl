#version 150

uniform mat4 viewMatrix, projMatrix;

in vec4 position;
in vec3 color;

out vec3 Color;

void main()
{
    Color = color;
    gl_Position = projMatrix * viewMatrix * position ;
}