package plugin.loader.texture;

import com.jagex.draw.raster.GameRasterizer;
import com.jagex.io.Buffer;
import com.displee.cache.index.Index;


public class NewTexture {

	public static Index spriteIndex;
	private int[] spriteGroup;
	private TextureOperation aTextureOperation_1145;
	private TextureOperation[] operations;
	private TextureOperation aTextureOperation_1148;
	private int[] spriteFrames;
	static int anInt1150 = -1;
	static int currentBaseY;

	final int[] generateTexturePixels(int width, boolean var2, int height, boolean var9) {

		int var11;
		for(var11 = 0; var11 < this.operations.length; ++var11) {
			if(this.operations[var11] == null)
				continue;
			//this.operations[var11].method160(width, height);
		}

		int[] var10 = new int[width * height];
		int var12;
		byte var13;
		if(var9) {
			var13 = -1;
			var12 = -1;
			var11 = height - 1;
		} else {
			var13 = 1;
			var11 = 0;
			var12 = height;
		}

		int var14 = 0;

		int var15;
		/*for(var15 = 0; width > var15; ++var15) {
			if(var2) {
				var14 = var15;
			}

			int[] var17;
			int[] var16;
			int[] var18;
			if(this.aTextureOperation_1145.aBoolean2375) {
				int[] var19 = this.aTextureOperation_1145.method154(var15, (byte)109);
				var16 = var19;
				var17 = var19;
				var18 = var19;
			} else {
				int[][] var24 = this.aTextureOperation_1145.method166(-1, var15);
				var16 = var24[0];
				var18 = var24[2];
				var17 = var24[1];
			}

			for(int var25 = var11; var25 != var12; var25 += var13) {
				int var20 = var16[var25] >> 4;
				if(var20 > 255) {
					var20 = 255;
				}

				if(var20 < 0) {
					var20 = 0;
				}

				var20 =  GameRasterizer.getInstance().textureBrightness[var20];
				int var22 = var18[var25] >> 4;
				int var21 = var17[var25] >> 4;
				if(var21 > 255) {
					var21 = 255;
				}

				if(0 > var21) {
					var21 = 0;
				}

				if(var22 > 255) {
					var22 = 255;
				}

				var21 =  GameRasterizer.getInstance().textureBrightness[var21];
				if(var22 < 0) {
					var22 = 0;
				}

				var22 =  GameRasterizer.getInstance().textureBrightness[var22];
				var10[var14++] = (var20 << 16) - -(var21 << 8) + var22;
				if(var2) {
					var14 += height + -1;
				}
			}
		}*/

		for(var15 = 0; var15 < this.operations.length; ++var15) {
			if(this.operations[var15] == null)
				continue;
			//this.operations[var15].method161();
		}

		return var10;
	}


	public NewTexture() {
		this.spriteFrames = new int[0];
		this.spriteGroup = new int[0];
	}

	static TextureOperation getTextureOperation(Buffer rsByteBuffer) {
		rsByteBuffer.readUByte();
		int var2 = rsByteBuffer.readUByte();
		TextureOperation var3 = method1777(var2);
		if(var3 == null)
			return null;
		var3.anInt2381 = rsByteBuffer.readUByte();
		int var4 = rsByteBuffer.readUByte();
		for(int var5 = 0; var5 < var4; ++var5) {
			int var6 = rsByteBuffer.readUByte();
			var3.decode(var6, rsByteBuffer);
		}
		var3.postDecode();
		return var3;
	}


	static TextureOperation method1777(int var0) {
		/*if(var0 == 0) {
			return new Class3_Sub13_Sub22();
		} else if(var0 == 1) {
			return new Class3_Sub13_Sub11();
		} else if (var0 == 2) {
			return new Class3_Sub13_Sub31();
		} else if (var0 == 3) {
			return new Class3_Sub13_Sub29();
		} else if (var0 == 4) {
			return new Class3_Sub13_Sub19();
		} else if (var0 == 5) {
			return new Class3_Sub13_Sub24();
		} else if (var0 == 6) {
			return new Class3_Sub13_Sub2();
		} else if (var0 == 7) {
			return new Class3_Sub13_Sub27();
		} else if (var0 == 8) {
			return new Class3_Sub13_Sub39();
		} else if (var0 == 9) {
			return new Class3_Sub13_Sub8();
		} else if (var0 == 10) {
			return new Class3_Sub13_Sub37();
		} else if (var0 == 11) {
			return new Class3_Sub13_Sub20();
		} else if (var0 == 12) {
			return new Class3_Sub13_Sub1();
		} else if (var0 == 13) {
			return new Class3_Sub13_Sub30();
		} else if (var0 == 14) {
			return new Class3_Sub13_Sub32();
		} else if (var0 == 15) {
			return new Class3_Sub13_Sub16();
		} else if (var0 == 16) {
			return new Class3_Sub13_Sub9();
		} else if (var0 == 17) {
			return new Class3_Sub13_Sub15();
		} else if (var0 == 18) {
			return new Class3_Sub13_Sub23_Sub1();
		} else if (var0 == 19) {
			return new Class3_Sub13_Sub18();
		} else if (var0 == 20) {
			return new Class3_Sub13_Sub13();
		} else if (var0 == 21) {
			return new Class3_Sub13_Sub5();
		} else if (var0 == 22) {
			return new Class3_Sub13_Sub35();
		} else if (var0 == 23) {
			return new Class3_Sub13_Sub17();
		} else if (var0 == 24) {
			return new Class3_Sub13_Sub12();
		} else if (var0 == 25) {
			return new Class3_Sub13_Sub34();
		} else if (var0 == 26) {
			return new Class3_Sub13_Sub6();
		} else if (var0 == 27) {
			return new Class3_Sub13_Sub7();
		} else if (var0 == 28) {
			return new Class3_Sub13_Sub25();
		} else if (var0 == 29) {
			return new Class3_Sub13_Sub33();
		} else if (var0 == 30) {
			return new Class3_Sub13_Sub10();
		} else if (var0 == 31) {
			return new Class3_Sub13_Sub14();
		} else if (var0 == 32) {
			return new Class3_Sub13_Sub28();
		} else if (var0 == 33) {
			return new Class3_Sub13_Sub3();
		} else if (var0 == 34) {
			return new Class3_Sub13_Sub4();
		} else if (var0 == 35) {
			return new Class3_Sub13_Sub26();
		} else if (var0 == 36) {
			return new Class3_Sub13_Sub36();
		} else if (var0 == 37) {
			return new Class3_Sub13_Sub21();
		} else if (var0 == 38) {
			return new Class3_Sub13_Sub38();
		} else*/ if (var0 == 39) {
			return new SpriteTextureOperation();
		} else {
			return null;
		}
	}

	public NewTexture(Buffer buffer) throws Exception {
		int paletteSize = buffer.readUByte();
		this.operations = new TextureOperation[paletteSize];
		int[][] pixels = new int[paletteSize][];
		int spriteFrameCount = 0;
		int spriteGroupCount = 0;

		int texture;
		TextureOperation textureOperation;
		int var8;
		int spriteId;
		for(texture = 0; paletteSize > texture; ++texture) {
			textureOperation = getTextureOperation(buffer);

			if(textureOperation == null)
				continue;

			if(0 <= textureOperation.getSpriteGroup()) {
				++spriteGroupCount;
			}

			if(textureOperation.getSpriteFrame() >= 0) {
				++spriteFrameCount;
			}

			var8 = textureOperation.subOperations.length;
			pixels[texture] = new int[var8];

			for(spriteId = 0; spriteId < var8; ++spriteId) {
				pixels[texture][spriteId] = buffer.readUByte();
			}

			this.operations[texture] = textureOperation;
		}

		this.spriteGroup = new int[spriteGroupCount];
		this.spriteFrames = new int[spriteFrameCount];
		spriteGroupCount = 0;
		spriteFrameCount = 0;

		for(texture = 0; texture < paletteSize; ++texture) {
			textureOperation = this.operations[texture];

			if(textureOperation == null)
				continue;

			var8 = textureOperation.subOperations.length;

			for(spriteId = 0; var8 > spriteId; ++spriteId) {
				textureOperation.subOperations[spriteId] = this.operations[pixels[texture][spriteId]];
			}

			spriteId = textureOperation.getSpriteGroup();
			int frameId = textureOperation.getSpriteFrame();
			if(spriteId > 0) {
				this.spriteGroup[spriteGroupCount++] = spriteId;
			}

			if(frameId > 0) {
				this.spriteFrames[spriteFrameCount++] = frameId;
			}

			pixels[texture] = null;
		}

		int idx = buffer.readUByte();
		this.aTextureOperation_1145 = this.operations[idx];
		idx = buffer.readUByte();
		this.aTextureOperation_1148 = this.operations[idx];
	}

	public boolean spriteTextureExists() {
		for(int var4 = 0; this.spriteGroup.length > var4; ++var4) {
			if(spriteIndex.archive(this.spriteGroup[var4]) == null) {
				return false;
			}
		}
		return true;
	}
}
