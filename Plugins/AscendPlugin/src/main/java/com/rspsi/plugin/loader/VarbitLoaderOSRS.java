package com.rspsi.plugin.loader;

import com.displee.cache.index.archive.Archive;

import com.jagex.cache.config.VariableBits;
import com.jagex.cache.loader.config.VariableBitLoader;
import com.jagex.io.Buffer;


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
		Buffer buffer = new Buffer(archive.file("varbit.dat"));
		count = buffer.readUShort();
		if (bits == null) {
			bits = new VariableBits[count];
		}

		for (int i = 0; i < count; i++) {
			if (bits[i] == null) {
				bits[i] = new VariableBits();
			}
			bits[i] = decode(buffer);
		}

		if (buffer.getPosition() != buffer.getPayload().length) {
			System.out.println("varbit load mismatch");
		}
	}
	
	private VariableBits decode(Buffer buffer) {
		VariableBits bit = new VariableBits();
	//	int opcode = buffer.readUByte();
		int setting = buffer.readUShort();
		int low = buffer.readUByte();
		int high = buffer.readUByte();
		bit.setSetting(setting);
		bit.setHigh(high);
		bit.setLow(low);
		return bit;
	}

	@Override
	public void init(Buffer arg0, Buffer arg1) {

	}

}
