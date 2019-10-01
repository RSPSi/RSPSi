package org.displee.utilities;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

public class FileChannelUtil {
	

	public static MappedByteBuffer readOnlyMappedBuffer(FileChannel channel, long offset, long size) throws IOException {
		return channel.map(MapMode.READ_ONLY, offset, size);
	}
	
	public static MappedByteBuffer readWriteMappedBuffer(FileChannel channel, long offset, long size) throws IOException {
		return channel.map(MapMode.READ_WRITE, offset, size);
	}
	
	public static void readData(FileChannel channel, long offset, long size, byte[] output) throws IOException {
		readOnlyMappedBuffer(channel, offset, size).get(output, 0, (int)size);
	}
	
	public static void writeData(FileChannel channel, long offset, long size, byte[] input) throws IOException {
		readWriteMappedBuffer(channel, offset, size).put(input, 0, (int)size);
	}

}
