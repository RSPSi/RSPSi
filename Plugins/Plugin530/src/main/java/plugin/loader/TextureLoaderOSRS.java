package plugin.loader;

import com.jagex.cache.graphics.IndexedImage;
import com.jagex.cache.graphics.Sprite;
import com.jagex.cache.loader.textures.TextureLoader;
import com.jagex.draw.textures.PalettedTexture;
import com.jagex.draw.textures.RGBTexture;
import com.jagex.draw.textures.SpriteTexture;
import com.jagex.draw.textures.Texture;
import com.jagex.io.Buffer;
import com.rspsi.misc.FixedHashMap;
import org.displee.cache.index.Index;
import org.displee.cache.index.archive.Archive;
import org.displee.cache.index.archive.file.File;

import java.nio.ByteBuffer;
import java.util.Arrays;


public class TextureLoaderOSRS extends TextureLoader {

	private Index textureIndex;

	private Texture[] textures = new Texture[0];
	private boolean[] transparent = new boolean[0];
	private double brightness = 0.8;
	private FixedHashMap<Integer, int[]> textureCache = new FixedHashMap<Integer, int[]>(20);
	
	
	@Override
	public Texture forId(int arg0) {
		if(arg0 < 0 || arg0 >= textures.length)
			return null;
		return textures[arg0];
	}

	@Override
	public int[] getPixels(int textureId) {

		Texture texture = forId(textureId);
		if(texture == null)
			return null;

		if(textureCache.contains(textureId))
			return textureCache.get(textureId);

		int[] texels = new int[0x10000];
		texture.setBrightness(brightness);
		textureCache.put(textureId, texels);

		return texels;
	}

	@Override
	public void init(Archive archive) {

	}

	public void init(Index index){
		this.textureIndex = index;
	}


	@Override
	public boolean isTransparent(int arg0) {
		if(arg0 < 0 || arg0 > transparent.length)
			return false;
		return transparent[arg0];
	}

	@Override
	public void setBrightness(double arg0) {
		textureCache.clear();
		this.brightness = arg0;
	}

	@Override
	public int count() {
		return textures.length;
	}

	@Override
	public void init(byte[] arg0) {
		
	}

}
