package com.rspsi.plugin.loader;

import com.displee.cache.index.archive.Archive;
import com.displee.cache.index.archive.file.File;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.stream.IntStream;

import com.jagex.cache.def.RSArea;
import com.jagex.cache.loader.config.RSAreaLoader;
import com.jagex.io.Buffer;
import com.jagex.util.ByteBufferUtils;
import lombok.val;

public class RSAreaLoaderOSRS extends RSAreaLoader {

	private RSArea[] areas;
	@Override
	public RSArea forId(int id) {
		if(id < 0 || id >= areas.length)
			return null;
		return areas[id];
	}

	@Override
	public int count() {
		return areas.length;
	}

	@Override
	public void init(Archive archive) {
		if(archive == null){
			areas = new RSArea[1000];
			IntStream.range(0, areas.length).forEach(index -> {
				RSArea dummyArea = new RSArea(index);
				dummyArea.setSpriteId(index);
				areas[index] = dummyArea;
			});
			return;
		}
		val highestId = Arrays.stream(archive.fileIds()).max().getAsInt();
		areas = new RSArea[highestId + 1];
		for(File file : archive.files()) {
			if(file != null && file.getData() != null) {
				RSArea area = decode(file.getId(), ByteBuffer.wrap(file.getData()));
				areas[file.getId()] = area;
			}
		}
	}
	
	private RSArea decode(int id, ByteBuffer buffer) {
		RSArea area = new RSArea(id);
		while (true) {
			int opcode = buffer.get() & 0xFF;
			if (opcode == 0)
				break;

			if (opcode == 1) {
				area.setSpriteId(ByteBufferUtils.getSmartInt(buffer));
			} else if (opcode == 2) {
				area.setAnInt1967(ByteBufferUtils.getSmartInt(buffer));
			} else if (opcode == 3) {
				area.setName(ByteBufferUtils.getOSRSString(buffer));
			} else if (opcode == 4) {
				area.setAnInt1959(ByteBufferUtils.getMedium(buffer));
			} else if (opcode == 5) {
				ByteBufferUtils.getMedium(buffer);
			} else if (opcode == 6) {
				area.setAnInt1968(buffer.get() & 0xFF);
			} else if (opcode == 7) {
				int flags = buffer.get() & 0xFF;
				if ((flags & 0x1) == 0) {
				}
				if ((flags & 0x2) == 2) {
				}
			} else if (opcode == 8) {
				buffer.get();
			} else if (opcode >= 10 && opcode <= 14) {
				area.getAStringArray1969()[opcode - 10] = ByteBufferUtils.getOSRSString(buffer);
			} else if (opcode == 15) {
				int size = buffer.get() & 0xFF;
				int[] anIntArray1982 = new int[size * 2];

				for (int i = 0; i < size * 2; ++i) {
					anIntArray1982[i] = buffer.getShort();
				}

				buffer.getInt();
				int size2 = buffer.get() & 0xFF;
				int[] anIntArray1981 = new int[size2];

				for (int i = 0; i < anIntArray1981.length; ++i) {
					anIntArray1981[i] = buffer.getInt();
				}

				byte[] aByteArray1979 = new byte[size];

				for (int i = 0; i < size; ++i) {
					aByteArray1979[i] = buffer.get();
				}
				area.setAnIntArray1982(anIntArray1982);
				area.setAnIntArray1981(anIntArray1981);
				area.setAByteArray1979(aByteArray1979);
			} else if (opcode == 17) {
				area.setAString1970(ByteBufferUtils.getOSRSString(buffer));
			} else if (opcode == 18) {
				ByteBufferUtils.getSmartInt(buffer);
			} else if (opcode == 19) {
				area.setAnInt1980(buffer.getShort() & 0xFFFF);
			} else if (opcode == 21) {
				buffer.getInt();
			} else if (opcode == 22) {
				buffer.getInt();
			} else if (opcode == 23) {
				buffer.get();
				buffer.get();
				buffer.get();
			} else if (opcode == 24) {
				buffer.getShort();
				buffer.getShort();
			} else if (opcode == 25) {
				ByteBufferUtils.getSmartInt(buffer);
			} else if (opcode == 28) {
				buffer.get();
			} else if (opcode == 29) {
				buffer.get();
			} else if (opcode == 30) {
				buffer.get();
			}
		}
		return area;
	}

	@Override
	public void init(Buffer data, Buffer indexBuffer) {
		// TODO Auto-generated method stub

	}

}
