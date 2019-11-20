package com.rspsi.plugin.loader;

import org.major.cache.anim.FrameConstants;

import com.jagex.Client;
import com.jagex.cache.anim.Frame;
import com.jagex.cache.anim.FrameBase;
import com.jagex.cache.loader.anim.FrameBaseLoader;
import com.jagex.cache.loader.anim.FrameLoader;
import com.jagex.io.Buffer;
import com.rspsi.cache.CacheFileType;

public class MyFrameLoader extends FrameLoader {

	private Frame[][] frames;

	public void init(int size) {
		frames = new Frame[size][0];
	}

	@Override
	protected Frame forId(int index) {
		try {
			int fileId = index >> 16;
			index = index & 0xffff;
			if (frames[fileId].length == 0) {
				Client.getSingleton().getProvider().requestFile(CacheFileType.ANIMATION, fileId);
				return null;
			}
			return frames[fileId][index];
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public void load(int file, byte[] data) {
		Buffer buffer = new Buffer(data);
		FrameBase base = FrameBaseLoader.instance.decode(buffer);

		int frameCount = buffer.readUShort();
		frames[file] = new Frame[(int) (frameCount * 3.5)];

		int[] translationIndices = new int[500];
		int[] transformX = new int[500];
		int[] transformY = new int[500];
		int[] transformZ = new int[500];

		for (int frameIndex = 0; frameIndex < frameCount; frameIndex++) {
			int id = buffer.readUShort();
			Frame frame = new Frame();
			frames[file][id] = frame;
			frame.setBase(base);

			int transformations = buffer.readUByte();
			int lastIndex = -1;
			int transformation = 0;

			for (int index = 0; index < transformations; index++) {
				try {
					int attribute = buffer.readUByte();
					if (attribute > 0) {
						if (base.getTransformationType(index) != FrameConstants.CENTROID_TRANSFORMATION) {
							for (int next = index - 1; next > lastIndex; next--) {
								if (base.getTransformationType(next) != FrameConstants.CENTROID_TRANSFORMATION) {
									continue;
								}
	
								translationIndices[transformation] = next;
								transformX[transformation] = 0;
								transformY[transformation] = 0;
								transformZ[transformation] = 0;
								transformation++;
								break;
							}
						}
	
						translationIndices[transformation] = index;
	
	
						int standard = base.getTransformationType(index) == FrameConstants.SCALE_TRANSFORMATION ? 128 : 0;
	
						transformX[transformation] = (attribute & FrameConstants.TRANSFORM_X) != 0 ? buffer.readShort2() : standard;
						transformY[transformation] = (attribute & FrameConstants.TRANSFORM_Y) != 0 ? buffer.readShort2() : standard;
						transformZ[transformation] = (attribute & FrameConstants.TRANSFORM_Z) != 0 ? buffer.readShort2() : standard;
	
						lastIndex = index;
						transformation++;
	
						if(base.getTransformationType(index) == FrameConstants.ROTATION_TRANSFORMATION) {
							transformX[index] = ((transformX[index] & 0xff) << 3) + (transformX[index] >> 8 & 0x7);
							transformY[index] = ((transformY[index] & 0xff) << 3) + (transformY[index] >> 8 & 0x7);
							transformZ[index] = ((transformZ[index] & 0xff) << 3) + (transformZ[index] >> 8 & 0x7);
						}
	
						if (base.getTransformationType(index) == FrameConstants.ALPHA_TRANSFORMATION) {
							frame.setOpaque(false);
						}
					}
				} catch(Exception ex) {
					
				}
			}

			frame.setTransformationCount(transformation);
			frame.setTransformationIndices(translationIndices);
			frame.setTransformX(transformX);
			frame.setTransformY(transformY);
			frame.setTransformZ(transformZ);
		}
	}

}
