package com.rspsi.plugin.loader;

import com.displee.cache.index.archive.Archive;
import com.displee.cache.index.archive.file.File;

import java.util.Arrays;
import java.util.List;

import lombok.val;
import org.apache.commons.compress.utils.Lists;

import com.jagex.cache.anim.Animation;
import com.jagex.cache.loader.anim.AnimationDefinitionLoader;
import com.jagex.io.Buffer;

public class AnimationDefinitionLoaderOSRS extends AnimationDefinitionLoader {


	private int count;
	private Animation[] animations;
	
	@Override
	public void init(Archive archive) {
		val highestId = Arrays.stream(archive.fileIds()).max().getAsInt();
		animations = new Animation[highestId + 1];
		for(File file : archive.files()) {
			if(file != null && file.getData() != null) {
				animations[file.getId()] = decode(new Buffer(file.getData()));
			}
		}
		
	}

	@Override
	public void init(byte[] data) {
		Buffer buffer = new Buffer(data);
		count = buffer.readUShort();

		if (animations == null) {
			animations = new Animation[count];
		}

		for (int id = 0; id < count; id++) {

			animations[id] = decode(buffer);
		}
	}
	
	protected Animation decode(Buffer buffer) {
		Animation animation = new Animation();
		do {
			int opcode = buffer.readUByte();
			if (opcode == 0) {
				break;
			}
			if (opcode == 1) {
				int frameCount = buffer.readUShort();
				int[] primaryFrames = new int[frameCount];
				int[] secondaryFrames = new int[frameCount];
				int[] durations = new int[frameCount];

				for (int frame = 0; frame < frameCount; frame++) {
					durations[frame] = buffer.readUShort();
				}

				for (int frame = 0; frame < frameCount; frame++) {
					primaryFrames[frame] = buffer.readUShort();
					secondaryFrames[frame] = -1;
				}
				

				for (int frame = 0; frame < frameCount; frame++) {
					primaryFrames[frame] += buffer.readUShort() << 16;
				}

				animation.setFrameCount(frameCount);
				animation.setPrimaryFrames(primaryFrames);
				animation.setSecondaryFrames(secondaryFrames);
				animation.setDurations(durations);
			} else if (opcode == 2) {
				animation.setLoopOffset(buffer.readUShort());
			} else if (opcode == 3) {
				int count = buffer.readUByte();
				int[] interleaveOrder = new int[count + 1];
				for (int index = 0; index < count; index++) {
					interleaveOrder[index] = buffer.readUByte();
				}

				interleaveOrder[count] = 9999999;
				animation.setInterleaveOrder(interleaveOrder);
			} else if (opcode == 4) {
				animation.setStretches(true);
			} else if (opcode == 5) {
				animation.setPriority(buffer.readUByte());
			} else if (opcode == 6) {
				animation.setPlayerOffhand(buffer.readUShort());
			} else if (opcode == 7) {
				animation.setPlayerMainhand(buffer.readUShort());
			} else if (opcode == 8) {
				animation.setMaximumLoops(buffer.readUByte());
			} else if (opcode == 9) {
				animation.setAnimatingPrecedence(buffer.readUByte());
			} else if (opcode == 10) {
				animation.setWalkingPrecedence(buffer.readUByte());
			} else if (opcode == 11) {
				animation.setReplayMode(buffer.readUByte());
			} else if (opcode == 12) {
				int len = buffer.readUByte();

				for (int i = 0; i < len; i++) {
					buffer.readUShort();
				}

				for (int i = 0; i < len; i++) {
					buffer.readUShort();
				}
			} else if (opcode == 13) {
				int len = buffer.readUByte();

				for (int i = 0; i < len; i++) {
					buffer.skip(3);
				}
			
			} else {
				System.out.println("Error unrecognised seq config code: " + opcode);
			}
		} while (true);

		if (animation.getFrameCount() == 0) {
			animation.setFrameCount(1);
			int[] primaryFrames = new int[1];
			primaryFrames[0] = -1;
			int[] secondaryFrames = new int[1];
			secondaryFrames[0] = -1;
			int[] durations = new int[1];
			durations[0] = -1;
			animation.setPrimaryFrames(primaryFrames);
			animation.setSecondaryFrames(secondaryFrames);
			animation.setDurations(durations);
		}

		if (animation.getAnimatingPrecedence() == -1) {
			animation.setAnimatingPrecedence(animation.getInterleaveOrder() == null ? 0 : 2);
		}

		if (animation.getWalkingPrecedence() == -1) {
			animation.setWalkingPrecedence(animation.getInterleaveOrder() == null ? 0 : 2);
		}
		return animation;
	}

	@Override
	public int count() {
		return count;
	}

	@Override
	public Animation forId(int id) {
		if(id < 0 || id > animations.length)
			id = 0;
		return animations[id];
	}



}
