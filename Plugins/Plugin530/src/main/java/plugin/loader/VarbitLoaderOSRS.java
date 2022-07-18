package plugin.loader;

import com.jagex.cache.config.VariableBits;
import com.jagex.cache.loader.config.VariableBitLoader;
import com.jagex.io.Buffer;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.compress.utils.Lists;
import com.displee.cache.index.Index;
import com.displee.cache.index.archive.Archive;
import com.displee.cache.index.archive.file.File;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
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

		val highestId = Arrays.stream(index.archiveIds()).max().getAsInt();
		val highestArchive = index.archive(highestId);
		val highestFile = Arrays.stream(highestArchive.fileIds()).max().getAsInt();

		int size = (highestId * 127) + highestFile;
		bits = new VariableBits[size];
		for (int id = 0; id < size; id++) {
			VariableBits varbit = new VariableBits();
			File file = index.archive(id >>> 10).file(id & 1023);
			if (Objects.nonNull(file) && Objects.nonNull(file.getData())) {
				ByteBuffer buff = ByteBuffer.wrap(file.getData());
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
			}
			bits[id] = varbit;

		}
		this.count = bits.length;
		log.info("Loaded {} varbits", this.count);
	}

	@Override
	public void init(Buffer arg0, Buffer arg1) {

	}

}
