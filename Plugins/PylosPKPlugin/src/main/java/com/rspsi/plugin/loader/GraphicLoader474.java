package com.rspsi.plugin.loader;


import com.displee.cache.index.archive.Archive;

import com.jagex.cache.anim.Graphic;
import com.jagex.cache.loader.anim.AnimationDefinitionLoader;
import com.jagex.cache.loader.anim.GraphicLoader;
import com.jagex.io.Buffer;


public class GraphicLoader474 extends GraphicLoader {


	private Graphic[] graphics;
	private int count;

	@Override
	public int count() {
		return count;
	}

	@Override
	public Graphic forId(int id) {
		if (id < 0 || id > count)
			return null;
		return graphics[id];
	}

	@Override
	public void init(Archive archive) {
		Buffer buffer = new Buffer(archive.file("spotanim.dat"));
		count = buffer.readUShort();
		if (graphics == null) {
			graphics = new Graphic[count];
		}

		for (int id = 0; id < count; id++) {
			try {
				graphics[id] = decode(buffer);
				graphics[id].setId(id);
			} catch (Exception ex) {

			}
		}
	}

	@Override
	public void init(byte[] data) {
		Buffer buffer = new Buffer(data);
		count = buffer.readUShort();
		if (graphics == null) {
			graphics = new Graphic[count];
		}

		for (int id = 0; id < count; id++) {
			try {
				graphics[id] = decode(buffer);
				graphics[id].setId(id);
			} catch (Exception ex) {

			}
		}
	}

	public Graphic decode(Buffer buffer) {
		Graphic graphic = new Graphic();

		int animationId = buffer.readUShort();
		if (animationId >= 0) {
			graphic.setAnimation(AnimationDefinitionLoader.getAnimation(animationId));
		}
		graphic.setAnimationId(animationId);
		graphic.setModel(buffer.readUShort());
		graphic.setAmbience(buffer.readUByte());
		graphic.setModelShadow(buffer.readUByte());

		int len = buffer.readUShort();
		int[] originalColours = new int[6];
		int[] replacementColours = new int[6];
		if (len != 65535) {
			for (int i = 0; i < len; i++) {
				originalColours[i] = buffer.readUShort();
			}

			for (int i = 0; i < len; i++) {
				replacementColours[i] = buffer.readUShort();
			}

		}

		graphic.setOriginalColours(originalColours);
		graphic.setReplacementColours(replacementColours);
		return graphic;
	}

}
