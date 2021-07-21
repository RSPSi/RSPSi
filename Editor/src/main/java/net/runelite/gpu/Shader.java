package net.runelite.gpu;

import com.google.common.annotations.VisibleForTesting;
import com.jogamp.opengl.GL3;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.gpu.exceptions.ShaderException;
import net.runelite.gpu.util.GLUtil;
import net.runelite.gpu.util.Template;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class Shader
{
	public static final String LINUX_VERSION_HEADER =
			"#version 420\n" +
					"#extension GL_ARB_compute_shader : require\n" +
					"#extension GL_ARB_shader_storage_buffer_object : require\n" +
					"#extension GL_ARB_explicit_attrib_location : require\n";
	public static final String WINDOWS_VERSION_HEADER = "#version 430\n";

	public  static final Shader BASIC_PROGRAM = new Shader()
			.add(GL3.GL_VERTEX_SHADER, "/gpu/vbasic.glsl")
			.add(GL3.GL_FRAGMENT_SHADER, "/gpu/flatColorFrag.glsl");


	public static final Shader PROGRAM = new Shader()
			.add(GL3.GL_VERTEX_SHADER, "vert.glsl")
			.add(GL3.GL_GEOMETRY_SHADER, "geom.glsl")
			.add(GL3.GL_FRAGMENT_SHADER, "frag.glsl");

	public static final Shader COMPUTE_PROGRAM = new Shader()
			.add(GL3.GL_COMPUTE_SHADER, "comp.glsl");

	public static final Shader SMALL_COMPUTE_PROGRAM = new Shader()
			.add(GL3.GL_COMPUTE_SHADER, "comp_small.glsl");

	public static final Shader UNORDERED_COMPUTE_PROGRAM = new Shader()
			.add(GL3.GL_COMPUTE_SHADER, "comp_unordered.glsl");


	public static final Shader UI_PROGRAM = new Shader()
			.add(GL3.GL_VERTEX_SHADER, "vertui.glsl")
			.add(GL3.GL_FRAGMENT_SHADER, "fragui.glsl");

	@VisibleForTesting
	final List<Unit> units = new ArrayList<>();

	@RequiredArgsConstructor
	@VisibleForTesting
	static class Unit
	{
		@Getter
		private final int type;

		@Getter
		private final String filename;
	}

	public Shader()
	{
	}

	public Shader add(int type, String name)
	{
		units.add(new Unit(type, name));
		return this;
	}

	public int compile(GL3 gl, Template template) throws ShaderException
	{
		int program = gl.glCreateProgram();
		int[] shaders = new int[units.size()];
		int i = 0;
		boolean ok = false;
		try
		{
			String[] sources = new String[shaders.length];
			while (i < shaders.length)
			{
				Unit unit = units.get(i);
				int shader = gl.glCreateShader(unit.type);
				String source = template.load(unit.filename);
				sources[i] = source;
				gl.glShaderSource(shader, 1, new String[]{source}, null);
				gl.glCompileShader(shader);

				if (GLUtil.glGetShader(gl, shader, gl.GL_COMPILE_STATUS) != gl.GL_TRUE)
				{
					String err = GLUtil.glGetShaderInfoLog(gl, shader);
					gl.glDeleteShader(shader);
					System.out.println(source);
					throw new ShaderException(err);
				}
				gl.glAttachShader(program, shader);
				shaders[i++] = shader;
			}

			gl.glLinkProgram(program);

			if (GLUtil.glGetProgram(gl, program, gl.GL_LINK_STATUS) == gl.GL_FALSE)
			{
				System.out.println(Arrays.toString(sources));
				String err = GLUtil.glGetProgramInfoLog(gl, program);
				throw new ShaderException(err);
			}

			gl.glValidateProgram(program);

			if (GLUtil.glGetProgram(gl, program, gl.GL_VALIDATE_STATUS) == gl.GL_FALSE)
			{
				System.out.println(Arrays.toString(sources));
				String err = GLUtil.glGetProgramInfoLog(gl, program);
				throw new ShaderException(err);
			}

			ok = true;
		}
		finally
		{
			while (i > 0)
			{
				int shader = shaders[--i];
				gl.glDetachShader(program, shader);
				gl.glDeleteShader(shader);
			}

			if (!ok)
			{
				gl.glDeleteProgram(program);
			}
		}

		return program;
	}
}

