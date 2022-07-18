package com.rspsi.plugin.loader;

import com.jagex.cache.anim.Graphic;
import com.jagex.cache.loader.anim.AnimationDefinitionLoader;
import com.jagex.cache.loader.anim.GraphicLoader;
import com.jagex.io.Buffer;
import com.displee.cache.index.archive.Archive;


public class MyGraphicLoader extends GraphicLoader {


	private Graphic[] graphics;
	private int count;
	
	@Override
	public int count() {
		return count;
	}

	@Override
	public Graphic forId(int id) {
		if(id < 0 || id > count)
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
		int modelid = buffer.readUShort();
		int sizeXY = buffer.readUByte();
		int sizeZ = buffer.readUByte();
		int j = buffer.readUShort();
		if (j != 65535) {
			int[] destColours = new int[j ];
			int[] originalColours = new int[j ];
			for (int k = 0; k < j; k++) {
				destColours[k] = buffer.readUShort();
			}
			for (int k = 0; k < j; k++) {
				originalColours[k] = buffer.readUShort();
			}
		}
		return graphic;
	}

}
