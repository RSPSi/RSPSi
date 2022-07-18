package plugin.loader.texture;

import com.jagex.cache.loader.textures.TextureLoader;
import com.jagex.draw.textures.RGBTexture;
import com.jagex.draw.textures.SpriteTexture;
import com.jagex.draw.textures.Texture;
import com.jagex.io.Buffer;
import com.jagex.util.ColourUtils;
import com.displee.cache.index.Index;
import java.nio.ByteBuffer;
import java.util.Objects;

public class TextureDefinition {

		private boolean aBoolean3781;
		private float cachedBrightness;
		private int anInt3783;
		private int anInt3784;
		private boolean aBoolean3787;
		private int anInt3788;
		private boolean aBoolean3789;
		private NewTexture texture;
		private int anInt3791;
		private int[] cachedPixels;
		private int anInt3795 = -1;
		private int anInt3796 = 0;
		boolean aBoolean3797 = false;
		private int anInt3799;
		private boolean aBoolean3800;


		public final Texture convertToTexture(boolean lowDetail) {
			if(this.texture.spriteTextureExists()) {
				int textureWH = 128;
				int[] pixels = this.texture.generateTexturePixels(textureWH, this.aBoolean3800, textureWH, false);
				byte[] data = new byte[pixels.length * 3];

				for(int index = 0; index < pixels.length;index++){
					data[index * 3] = (byte) ColourUtils.getRed(pixels[index]);
					data[index * 3 + 1] = (byte) ColourUtils.getGreen(pixels[index]);
					data[index * 3 + 2] = (byte) ColourUtils.getBlue(pixels[index]);
				}
				Buffer buffer = new Buffer(data);
				return new RGBTexture(textureWH, textureWH, buffer);
			} else {
				return null;
			}
		}


		public TextureDefinition(Buffer buffer) throws Exception {
			this.texture = new NewTexture(buffer);
			this.aBoolean3789 = buffer.readUByte() == 1;
			this.aBoolean3800 = 1 == buffer.readUByte();
			this.aBoolean3787 = buffer.readUByte() == 1;
			this.aBoolean3781 = buffer.readUByte() == 1;
			int var2 = 3 & buffer.readUByte();
			this.anInt3783 = buffer.readByte();
			this.anInt3799 = buffer.readByte();
			int var3 = buffer.readUByte();
			buffer.readUByte();
			if(var2 == 1) {
				this.anInt3784 = 2;
			} else if(var2 == 2) {
				this.anInt3784 = 3;
			} else if(var2 == 3) {
				this.anInt3784 = 4;
			} else {
				this.anInt3784 = 0;
			}
			this.anInt3788 = (var3 & 240) >> 4;
		}
}
