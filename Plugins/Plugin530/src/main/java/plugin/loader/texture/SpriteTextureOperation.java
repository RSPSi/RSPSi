package plugin.loader.texture;

import com.jagex.Client;
import com.jagex.cache.graphics.Sprite;
import com.jagex.io.Buffer;
import com.displee.cache.index.Index;

public class SpriteTextureOperation extends TextureOperation {

	public static Index spriteIndex;

	private int anInt3278 = -1;
	int anInt3280;
	int anInt3283;
	int[] anIntArray3284;

	final boolean loadSprite() {
		if(this.anIntArray3284 == null) {
			if(this.anInt3278 < 0) {
				return false;
			} else {
				Sprite var3 = Client.getSingleton().getCache().getSprite(anInt3278);
				if(var3 == null)
					return false;
				var3.resize();
				this.anInt3283 = var3.getHeight();
				this.anInt3280 = var3.getWidth();
				this.anIntArray3284 = var3.getRaster();
				return true;
			}
		} else {
			return true;
		}
	}

	final int getSpriteGroup() {
		return this.anInt3278;
	}

	public SpriteTextureOperation() {
		super(0, false);
	}


	final void decode(int var1, Buffer var2) {
		if(var1 == 0) {
			this.anInt3278 = var2.getULEShort();
		}
	}


}
