package plugin.loader;

import com.google.common.collect.Maps;
import com.jagex.cache.loader.textures.TextureLoader;
import com.jagex.draw.textures.Texture;
import com.jagex.io.Buffer;
import com.rspsi.core.misc.FixedHashMap;
import lombok.extern.slf4j.Slf4j;
import com.displee.cache.index.Index;
import com.displee.cache.index.archive.Archive;
import plugin.loader.texture.NewTexture;
import plugin.loader.texture.SpriteTextureOperation;
import plugin.loader.texture.TextureDefinition;

import java.util.Map;
import java.util.stream.IntStream;

@Slf4j
public class TextureLoaderOSRS extends TextureLoader {

	private Index textureIndex, spriteIndex;

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

	public void init(Index textureIndex, Index spriteIndex){
		this.textureIndex = textureIndex;
		this.spriteIndex = spriteIndex;
		SpriteTextureOperation.spriteIndex = spriteIndex;
		NewTexture.spriteIndex = spriteIndex;

		Map<Integer, Texture> textureList = Maps.newConcurrentMap();
		for(int archiveId = 0;archiveId< IntStream.of(textureIndex.archiveIds()).max().orElse(0);archiveId++) {
			Archive archive = textureIndex.archive(archiveId);
			if(archive == null){
				log.info("Archive {} null", archiveId);
			}
			byte[] data = archive.file(0).getData();
			try {
				TextureDefinition textureDefinition = new TextureDefinition(new Buffer(data));
				textureList.put(archive.getId(), textureDefinition.convertToTexture(false));
			} catch (Exception ex){
				ex.printStackTrace();
			}
		}

		int maxId = textureList.keySet().stream().max(Integer::compareTo).orElse(0);
		textures = new Texture[maxId];
		transparent = new boolean[maxId];
		int nonNullAmt = 0;

		for(Map.Entry<Integer, Texture> entry : textureList.entrySet()){
			if(entry.getValue() != null){
				textures[entry.getKey()] = entry.getValue();
				nonNullAmt++;
			}
		}
		log.info("Loaded {}/{} textures | {} | {}", nonNullAmt, maxId, textureIndex.archives().length, textureList.size());
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
