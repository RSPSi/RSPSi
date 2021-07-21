package com.rspsi.plugin.loader;

import com.displee.cache.index.Index;
import com.displee.cache.index.archive.Archive;
import com.displee.cache.index.archive.file.File;
import com.rspsi.jagex.cache.ArchiveUtils;
import com.rspsi.jagex.cache.ArchiveUtils;
import com.rspsi.jagex.cache.config.VariableBits;
import com.rspsi.jagex.cache.loader.config.VariableBitLoader;
import com.rspsi.jagex.io.Buffer;
import org.apache.commons.compress.utils.Lists;

import java.nio.ByteBuffer;
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
		List<VariableBits> varbits = Lists.newArrayList();
		Archive highestArchive = ArchiveUtils.getHighestArchive(index);
		File highestFile = ArchiveUtils.getHighestFile(highestArchive);
		int size = highestArchive.getId() * 127 + highestFile.getId();
		for (int id = 0; id < size; id++) {
			File file = index.archive(id >>> 10).file(id & 0x3ff);
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
