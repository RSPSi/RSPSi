package com.rspsi.renderer;

public class GLMesh {/*
	// Mesh Data
	private int vertexCount;    // Vertex Count
	private FloatBuffer vertices;    // Vertex Data
	private FloatBuffer texCoords;    // Texture Coordinates
	private int[] textureId = new int[1];  // Texture ID

	// Vertex Buffer Object Names
	private int[] VBOVertices = new int[1];  // Vertex VBO Name
	private int[] VBOTexCoords = new int[1];// Texture Coordinate VBO Name
	private boolean fUseVBO;

	public int getVertexCount() {
		return vertexCount;
	}

	public boolean loadHeightmap(GL gl, Texture texture, float flHeightScale, float flResolution, boolean useVBO) throws IOException {

		IntBuffer texturePixelBuffer = GpuIntBuffer.allocateDirect(texture.getPixels().length);
		texturePixelBuffer.put(texture.getPixels());

		// Generate Vertex Field
		vertexCount = (int) (texture.getWidth() * texture.getHeight() * 6 /
				(flResolution * flResolution));
		// Allocate Vertex Data
		vertices = GpuFloatBuffer.allocateDirect(vertexCount * 3);
		// Allocate Tex Coord Data
		texCoords = GpuFloatBuffer.allocateDirect(vertexCount * 2);
		for (int nZ = 0; nZ < texture.getHeight(); nZ += (int) flResolution) {
			for (int nX = 0; nX < texture.getWidth(); nX += (int) flResolution) {
				for (int nTri = 0; nTri < 6; nTri++) {
					// Using This Quick Hack, Figure The X,Z Position Of The Point
					float flX = (float) nX +
							((nTri == 1 || nTri == 2 || nTri == 5) ?
									flResolution : 0.0f);
					float flZ = (float) nZ +
							((nTri == 2 || nTri == 4 || nTri == 5) ?
									flResolution : 0.0f);

					// Set The Data, Using PtHeight To Obtain The Y Value
					vertices.put(flX - (texture.getWidth() / 2f));
					vertices.put(pointHeight(texture, (int) flX, (int) flZ) *
							flHeightScale);
					vertices.put(flZ - (texture.getHeight() / 2f));

					// Stretch The Texture Across The Entire Mesh
					texCoords.put(flX / texture.getWidth());
					texCoords.put(flZ / texture.getHeight());
				}
			}
		}
		vertices.flip();
		texCoords.flip();


		// Load The Texture Into OpenGL
		gl.glGenTextures(1, textureId, 0);      // Get An Open ID
		gl.glBindTexture(GL.GL_TEXTURE_2D, textureId[0]);  // Bind The Texture
		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, 3, texture.getWidth(),
				texture.getHeight(), 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE,
				texturePixelBuffer);

		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
				GL.GL_LINEAR);

		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
				GL.GL_LINEAR);

		if (useVBO) {
			// Load Vertex Data Into The Graphics Card Memory
			buildVBOs(gl);  // Build The VBOs
		}

		return true;
	}

	public void render(GL gl) {
		// Enable Pointers
		gl.glEnableClientState(GL.GL_VERTEX_ARRAY);  // Enable Vertex Arrays
		// Enable Texture Coord Arrays
		gl.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);

		// Set Pointers To Our Data
		if (fUseVBO) {
			gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, mesh.VBOTexCoords[0]);
			// Set The TexCoord Pointer To The TexCoord Buffer
			gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, 0);
			gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, mesh.VBOVertices[0]);
			// Set The Vertex Pointer To The Vertex Buffer
			gl.glVertexPointer(3, GL.GL_FLOAT, 0, 0);
		} else {
			// Set The Vertex Pointer To Our Vertex Data
			gl.glVertexPointer(3, GL.GL_FLOAT, 0, mesh.vertices);
			// Set The Vertex Pointer To Our TexCoord Data
			gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, mesh.texCoords);
		}

		// Render
		// Draw All Of The Triangles At Once
		gl.glDrawArrays(GL.GL_TRIANGLES, 0, mesh.vertexCount);

		// Disable Pointers
		// Disable Vertex Arrays
		gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
		// Disable Texture Coord Arrays
		gl.glDisableClientState(GL.GL_TEXTURE_COORD_ARRAY);
	}

	private float pointHeight(Texture texture, int nX, int nY) {
		// Calculate The Position In The Texture, Careful Not To Overflow
		int nPos = ((nX % texture.getWidth()) + ((nY % texture.getHeight()) *
				texture.getWidth())) * 3;
		// Get The Red Component
		float flR = unsignedByteToInt(texture.getPixels().get(nPos));
		// Get The Green Component
		float flG = unsignedByteToInt(texture.getPixels().get(nPos + 1));
		// Get The Blue Component
		float flB = unsignedByteToInt(texture.getPixels().get(nPos + 2));
		// Calculate The Height Using The Luminance Algorithm
		return (0.299f * flR + 0.587f * flG + 0.114f * flB);
	}

	private int unsignedByteToInt(byte b) {
		return (int) b & 0xFF;
	}

	private void buildVBOs(GL gl) {
		// Generate And Bind The Vertex Buffer
		gl.glGenBuffersARB(1, VBOVertices, 0);  // Get A Valid Name
		gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, VBOVertices[0]);  // Bind The Buffer

		// Load The Data
		gl.glBufferDataARB(GL.GL_ARRAY_BUFFER_ARB, vertexCount * 3 *
				BufferUtil.SIZEOF_FLOAT, vertices, GL.GL_STATIC_DRAW_ARB);

		// Generate And Bind The Texture Coordinate Buffer
		gl.glGenBuffersARB(1, VBOTexCoords, 0);  // Get A Valid Name
		gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, VBOTexCoords[0]); // Bind The Buffer
		// Load The Data
		gl.glBufferDataARB(GL.GL_ARRAY_BUFFER_ARB, vertexCount * 2 *
				BufferUtil.SIZEOF_FLOAT, texCoords, GL.GL_STATIC_DRAW_ARB);

		// Our Copy Of The Data Is No Longer Necessary, It Is Safe In The Graphics Card
		vertices = null;
		texCoords = null;
		fUseVBO = true;
	}
*/}