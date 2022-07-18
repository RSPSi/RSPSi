package com.rspsi.plugin.loader;

import com.jagex.cache.config.VariableBits;
import com.jagex.cache.loader.config.VariableBitLoader;
import com.jagex.io.Buffer;
import lombok.val;
import org.apache.commons.compress.utils.Lists;
import com.displee.cache.index.Index;
import com.displee.cache.index.archive.Archive;
import com.displee.cache.index.archive.file.File;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

//Checked
public class VarbitLoaderOSRS extends VariableBitLoader{

	private int count;
	private VariableBits[] bits;
	
	@Override
	public int count() {
		return count;
	}

	@Override
	public VariableBits forId(int id) {
		if(id < 0 || id > bits.length)
			return null;
		
		return bits[id];
	}

	@Override
	public void init(Archive archive) {

	}

	public void decodeVarbits(Index index) {
		val highestId = Arrays.stream(index.archiveIds()).max().getAsInt();
		val highestArchive = index.archive(highestId);
		val highestFile = Arrays.stream(highestArchive.fileIds()).max().getAsInt();

		List<VariableBits> varbits = Lists.newArrayList();
		int size = (highestId * 127 + highestFile);
		for (int id = 0; id < size; id++) {
			File file = index.archive(id >>> 1416501898).file(id & 0x3ff);
			if (Objects.nonNull(file) && Objects.nonNull(file.getData())) {
				ByteBuffer buff = ByteBuffer.wrap(file.getData());
				VariableBits varbit = new VariableBits();
				try {
					while (true) {
						int opcode = buff.get() & 0xff;
						if (opcode == 0)
							break;
						if (opcode == 1) {
							int setting = buff.getShort() & 0xffff;
							int low = buff.get() & 0xff;
							int high = buff.get() & 0xff;
							varbit.setSetting(setting);
							varbit.setHigh(high);
							varbit.setLow(low);
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				varbits.add(varbit);
			}
		}
		bits = varbits.toArray(new VariableBits[0]);
		this.count = bits.length;
	}

	@Override
	public void init(Buffer arg0, Buffer arg1) {

	}

}
