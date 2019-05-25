package com.jagex;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

import com.jagex.net.ResourceProvider;

import io.nshusa.rsam.FileStore;
import io.nshusa.rsam.IndexedFileSystem;
import io.nshusa.rsam.binary.Archive;

public class Cache {
	
	

	private IndexedFileSystem indexedFileSystem;

		
	public Cache(Path path) {

			indexedFileSystem = IndexedFileSystem.init(path);
			indexedFileSystem.load();
		Archive version;
		
			try {
				version = createArchive(5, "update list");
				resourceProvider = new ResourceProvider(this);
				resourceProvider.init(version);
				Thread t = new Thread(resourceProvider);
				t.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		
	}
	
	public void init(Path path) {
		
	}
	
	public ResourceProvider resourceProvider;
	

	public final Archive createArchive(int file, String displayedName) throws Exception {
		System.out.println(displayedName);
        return Archive.decode(indexedFileSystem.getStore(FileStore.ARCHIVE_FILE_STORE).readFile(file));
	}
	
	public byte[] getFile(int index, int file) {
		return indexedFileSystem.getStore(index + 1).readFile(file).array();
	}
	
	public boolean putFile(int index, int file, byte[] data) {
		return indexedFileSystem.getStore(index + 1).writeFile(file, data);
	}
	
	public void close() throws IOException {
		indexedFileSystem.close();
	}

	public ResourceProvider getProvider() {
		return resourceProvider;
	}
	

}
