package com.rspsi.plugin.loader;

import com.displee.cache.index.archive.Archive;
import com.displee.cache.index.archive.file.File;

import com.jagex.cache.anim.Graphic;
import com.jagex.cache.loader.anim.AnimationDefinitionLoader;
import com.jagex.cache.loader.anim.GraphicLoader;
import com.jagex.io.Buffer;
import lombok.val;

import java.util.Arrays;

public class GraphicLoaderOSRS extends GraphicLoader {


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

		val highestId = Arrays.stream(archive.fileIds()).max().getAsInt();
		graphics = new Graphic[highestId + 1];
		

		for(File file : archive.files()){
			try {
				graphics[file.getId()] = decode(new Buffer(file.getData()));
				graphics[file.getId()].setId(file.getId());
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
		int lastOpcode = -1;
		do {
			int opcode = buffer.readUByte();
			if (opcode == 0)
				return graphic;

			if (opcode == 1) {
				graphic.setModel(buffer.readUShort());
			} else if (opcode == 2) {
				int animationId = buffer.readUShort();
				if (animationId >= 0) {
					graphic.setAnimation(AnimationDefinitionLoader.getAnimation(animationId));
				}
				graphic.setAnimationId(animationId);
			} else if (opcode == 4) {
				graphic.setBreadthScale(buffer.readUShort());
			} else if (opcode == 5) {
				graphic.setDepthScale(buffer.readUShort());
			} else if (opcode == 6) {
				graphic.setOrientation(buffer.readUShort());
			} else if (opcode == 7) {
				graphic.setAmbience(buffer.readUByte());
			} else if (opcode == 8) {
				graphic.setModelShadow(buffer.readUByte());
			} else if (opcode >= 40 && opcode < 50) {
				if(graphic.getOriginalColours() == null) {
					int[] originalColours = new int[10];
					int[] replacementColours = new int[10];
					graphic.setOriginalColours(originalColours);
					graphic.setReplacementColours(replacementColours);
				}
				graphic.getOriginalColours()[opcode - 40] = buffer.readUShort();
			} else if (opcode >= 50 && opcode < 60) {
				if(graphic.getOriginalColours() == null) {
					int[] originalColours = new int[10];
					int[] replacementColours = new int[10];
					graphic.setOriginalColours(originalColours);
					graphic.setReplacementColours(replacementColours);
				}
				graphic.getReplacementColours()[opcode - 50] = buffer.readUShort();

			} else {
				System.out.println("Error unrecognised spotanim config code: " + opcode + " last: " + lastOpcode);
			}
			lastOpcode = opcode;
		} while (true);
	}

}
